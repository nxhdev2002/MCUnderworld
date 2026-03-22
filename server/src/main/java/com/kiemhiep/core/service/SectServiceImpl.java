package com.kiemhiep.core.service;

import com.kiemhiep.api.event.EventDispatcher;
import com.kiemhiep.api.event.SectJoinEvent;
import com.kiemhiep.api.event.SectLeaveEvent;
import com.kiemhiep.api.event.SectRelationChangeEvent;
import com.kiemhiep.api.model.Sect;
import com.kiemhiep.api.model.SectMember;
import com.kiemhiep.api.model.SectRelation;
import com.kiemhiep.api.repository.SectRepository;
import com.kiemhiep.api.service.SectService;
import com.kiemhiep.api.service.PlayerService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for sect (tông môn) management.
 */
public class SectServiceImpl implements SectService {

    private final SectRepository sectRepository;
    private final PlayerService playerService;
    private final EventDispatcher eventDispatcher;

    public SectServiceImpl(SectRepository sectRepository, PlayerService playerService, EventDispatcher eventDispatcher) {
        this.sectRepository = sectRepository;
        this.playerService = playerService;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public Optional<Sect> createSect(long playerId, String name) {
        // Check if player already leads a sect
        List<SectMember> existingMembers = sectRepository.getMembersByPlayer(playerId);
        for (SectMember member : existingMembers) {
            Optional<Sect> sectOpt = sectRepository.getById(member.sectId());
            if (sectOpt.isPresent() && sectOpt.get().leaderId() == playerId) {
                return Optional.empty(); // Already a leader
            }
        }

        // Create the sect
        Sect newSect = new Sect(
            0,
            name,
            playerId,
            1,      // Start at level 1
            0L,     // Start with 0 exp
            Instant.now(),
            Instant.now()
        );

        Sect saved = sectRepository.save(newSect);

        // Add player as LEADER member
        sectRepository.joinMember(saved.id(), playerId, SectMember.Rank.LEADER);

        // Fire event
        eventDispatcher.fire(new SectJoinEvent(playerId, saved.id(), Instant.now()));

        return Optional.of(saved);
    }

    @Override
    public Optional<SectMember> joinSect(long playerId, long sectId) {
        Optional<Sect> sectOpt = sectRepository.getById(sectId);
        if (sectOpt.isEmpty()) {
            return Optional.empty(); // Sect doesn't exist
        }

        // Check if player is already a member
        Optional<SectMember> existingMember = sectRepository.getMember(sectId, playerId);
        if (existingMember.isPresent()) {
            return Optional.empty(); // Already a member
        }

        // Check if player is already leader of another sect
        List<SectMember> playerMembers = sectRepository.getMembersByPlayer(playerId);
        for (SectMember m : playerMembers) {
            Optional<Sect> playerSect = sectRepository.getById(m.sectId());
            if (playerSect.isPresent() && playerSect.get().leaderId() == playerId) {
                return Optional.empty(); // Already leading another sect
            }
        }

        // Add member as NOVICE
        SectMember newMember = sectRepository.joinMember(sectId, playerId, SectMember.Rank.NOVICE);
        return Optional.of(newMember);
    }

    @Override
    public boolean leaveSect(long playerId, long sectId) {
        Optional<SectMember> memberOpt = sectRepository.getMember(sectId, playerId);
        if (memberOpt.isEmpty()) {
            return false; // Not a member
        }

        SectMember member = memberOpt.get();

        // Check if player is the leader
        if (member.rank() == SectMember.Rank.LEADER) {
            // Check if there are other members to transfer leadership
            List<SectMember> otherMembers = sectRepository.getMembers(sectId);
            Optional<SectMember> nextLeaderOpt = otherMembers.stream()
                .filter(m -> m.playerId() != playerId && m.rank() != SectMember.Rank.LEADER)
                .findFirst();

            if (nextLeaderOpt.isPresent()) {
                // Transfer leadership to next member
                Sect sect = sectRepository.getById(sectId).orElseThrow();
                SectMember nextLeader = nextLeaderOpt.get();

                // Update sect leader
                Sect updatedSect = new Sect(sect.id(), sect.name(), nextLeader.playerId(),
                    sect.level(), sect.exp(), sect.createdAt(), Instant.now());
                sectRepository.save(updatedSect);

                // Update member ranks - delete old leader, promote new leader
                sectRepository.deleteMember(sectId, playerId);
                sectRepository.deleteMember(sectId, nextLeader.playerId());

                // Re-add with new ranks
                sectRepository.joinMember(sectId, playerId, SectMember.Rank.MEMBER);
                sectRepository.joinMember(sectId, nextLeader.playerId(), SectMember.Rank.LEADER);
            } else {
                // No other members, just delete the sect
                sectRepository.deleteById(sectId);
            }
        } else {
            // Just remove member
            sectRepository.deleteMember(sectId, playerId);
        }

        // Fire event
        eventDispatcher.fire(new SectLeaveEvent(playerId, sectId, Instant.now()));

        return true;
    }

    @Override
    public Optional<Sect> getSectById(long sectId) {
        return sectRepository.getById(sectId);
    }

    @Override
    public List<Sect> getSectsForPlayer(long playerId) {
        // Get all members for this player, then get their sects
        List<SectMember> members = sectRepository.getMembersByPlayer(playerId);
        return members.stream()
            .map(m -> sectRepository.getById(m.sectId()))
            .flatMap(Optional::stream)
            .toList();
    }

    @Override
    public List<SectMember> getMembers(long sectId) {
        return sectRepository.getMembers(sectId);
    }

    @Override
    public Optional<SectMember> getMember(long sectId, long playerId) {
        return sectRepository.getMember(sectId, playerId);
    }

    @Override
    public SectMember updateMember(long sectId, long playerId, Optional<SectMember.Rank> rank, Optional<Integer> contribution) {
        Optional<SectMember> memberOpt = sectRepository.getMember(sectId, playerId);
        if (memberOpt.isEmpty()) {
            throw new IllegalArgumentException("Player is not a member of this sect");
        }

        SectMember member = memberOpt.get();

        SectMember updated = member;
        if (rank.isPresent()) {
            updated = new SectMember(updated.id(), updated.sectId(), updated.playerId(), rank.get(),
                contribution.orElse(updated.contribution()), updated.joinedAt(), Instant.now());
        } else if (contribution.isPresent()) {
            updated = new SectMember(updated.id(), updated.sectId(), updated.playerId(), updated.rank(),
                contribution.get(), updated.joinedAt(), Instant.now());
        }

        // Update in repository
        // Note: The repository doesn't have an update_member method
        // In a real implementation, we would add this method to the repository
        // For now, we delete and re-add
        sectRepository.deleteMember(sectId, playerId);
        sectRepository.joinMember(sectId, playerId, updated.rank());

        return updated;
    }

    @Override
    public SectRelation setRelation(long sectId, long relatedSectId, SectRelation.Type type) {
        // Check if relation already exists
        Optional<SectRelation> existing = sectRepository.getRelation(sectId, relatedSectId);

        if (existing.isPresent()) {
            // Update existing - delete and re-add with new type
            SectRelation oldRelation = existing.get();

            if (oldRelation.type() == type) {
                return oldRelation; // No change needed
            }

            com.kiemhiep.api.event.SectRelationChangeEvent.Type oldType =
                com.kiemhiep.api.event.SectRelationChangeEvent.Type.valueOf(oldRelation.type().name());
            com.kiemhiep.api.event.SectRelationChangeEvent.Type newType =
                com.kiemhiep.api.event.SectRelationChangeEvent.Type.valueOf(type.name());

            sectRepository.deleteRelation(sectId, relatedSectId);

            // Fire relation change event
            eventDispatcher.fire(new SectRelationChangeEvent(sectId, relatedSectId, oldType, newType));
        }

        // Create new relation
        SectRelation relation = new SectRelation(
            0,
            sectId,
            relatedSectId,
            type,
            Instant.now()
        );

        // In real implementation, this would insert and return with ID
        // For now, just return the relation
        return relation;
    }

    @Override
    public List<SectRelation> getRelations(long sectId) {
        return sectRepository.getRelations(sectId);
    }

    @Override
    public List<Sect> getAlliedSects(long sectId) {
        List<SectRelation> relations = sectRepository.getRelationsByType(sectId, SectRelation.Type.ALLIED);
        return relations.stream()
            .map(r -> sectRepository.getById(r.relatedSectId()))
            .flatMap(Optional::stream)
            .toList();
    }

    @Override
    public List<Sect> getHostileSects(long sectId) {
        List<SectRelation> relations = sectRepository.getRelationsByType(sectId, SectRelation.Type.HOSTILE);
        return relations.stream()
            .map(r -> sectRepository.getById(r.relatedSectId()))
            .flatMap(Optional::stream)
            .toList();
    }

    @Override
    public boolean transferLeadership(long sectId, long fromPlayerId, long toPlayerId) {
        Optional<Sect> sectOpt = sectRepository.getById(sectId);
        if (sectOpt.isEmpty()) {
            return false; // Sect doesn't exist
        }

        if (sectOpt.get().leaderId() != fromPlayerId) {
            return false; // Player is not the current leader
        }

        // Check if new leader is a member
        Optional<SectMember> newLeaderMember = sectRepository.getMember(sectId, toPlayerId);
        if (newLeaderMember.isEmpty()) {
            return false; // New leader is not a member
        }

        // Transfer leadership
        Sect sect = sectOpt.get();

        // Delete both members and re-add with new ranks
        sectRepository.deleteMember(sectId, fromPlayerId);
        sectRepository.deleteMember(sectId, toPlayerId);

        sectRepository.joinMember(sectId, toPlayerId, SectMember.Rank.LEADER);
        sectRepository.joinMember(sectId, fromPlayerId, SectMember.Rank.MEMBER);

        // Update sect leader reference
        Sect updatedSect = new Sect(sect.id(), sect.name(), toPlayerId, sect.level(), sect.exp(), sect.createdAt(), Instant.now());
        sectRepository.save(updatedSect);

        return true;
    }
}
