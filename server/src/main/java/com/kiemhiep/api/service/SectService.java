package com.kiemhiep.api.service;

import com.kiemhiep.api.model.Sect;
import com.kiemhiep.api.model.SectMember;
import com.kiemhiep.api.model.SectRelation;

import java.util.List;
import java.util.Optional;

/**
 * Service for sect (tông môn) management.
 * <p>
 * Features: create, join, leave, member management, relations (ALLIED/HOSTILE/NEUTRAL).
 */
public interface SectService {

    /**
     * Create a new sect. Player becomes the LEADER.
     *
     * @param playerId the player creating the sect
     * @param name     the sect name
     * @return the created sect, or empty if player already leads a sect
     */
    Optional<Sect> createSect(long playerId, String name);

    /**
     * Join an existing sect.
     *
     * @param playerId the player joining
     * @param sectId   the sect to join
     * @return the sect member record, or empty if join failed
     */
    Optional<SectMember> joinSect(long playerId, long sectId);

    /**
     * Leave a sect.
     *
     * @param playerId the player leaving
     * @param sectId   the sect to leave
     * @return true if successfully left
     */
    boolean leaveSect(long playerId, long sectId);

    /**
     * Get sect by ID.
     *
     * @param sectId the sect ID
     * @return the sect if found
     */
    Optional<Sect> getSectById(long sectId);

    /**
     * Get all sects a player is a member of.
     *
     * @param playerId the player ID
     * @return list of sects (possibly empty)
     */
    List<Sect> getSectsForPlayer(long playerId);

    /**
     * Get members of a sect.
     *
     * @param sectId the sect ID
     * @return list of members (possibly empty)
     */
    List<SectMember> getMembers(long sectId);

    /**
     * Get a specific member's details in a sect.
     *
     * @param sectId  the sect ID
     * @param playerId the player ID
     * @return the member record if found
     */
    Optional<SectMember> getMember(long sectId, long playerId);

    /**
     * Update a member's rank or contribution.
     *
     * @param sectId   the sect ID
     * @param playerId the player ID
     * @param rank     new rank (optional)
     * @param contribution new contribution (optional)
     * @return the updated member
     */
    SectMember updateMember(long sectId, long playerId, Optional<SectMember.Rank> rank, Optional<Integer> contribution);

    /**
     * Set relation between two sects.
     *
     * @param sectId       the source sect ID
     * @param relatedSectId the target sect ID
     * @param type         the relation type (ALLIED, HOSTILE, NEUTRAL)
     * @return the relation record
     */
    SectRelation setRelation(long sectId, long relatedSectId, SectRelation.Type type);

    /**
     * Get all relations for a sect.
     *
     * @param sectId the sect ID
     * @return map of relatedSectId -> relation
     */
    List<SectRelation> getRelations(long sectId);

    /**
     * Get all ALLIED sects for a sect.
     *
     * @param sectId the sect ID
     * @return list of allied sects
     */
    List<Sect> getAlliedSects(long sectId);

    /**
     * Get all HOSTILE sects for a sect.
     *
     * @param sectId the sect ID
     * @return list of hostile sects
     */
    List<Sect> getHostileSects(long sectId);

    /**
     * Transfer leadership of a sect.
     *
     * @param sectId  the sect ID
     * @param fromPlayerId current leader
     * @param toPlayerId new leader
     * @return true if transfer succeeded
     */
    boolean transferLeadership(long sectId, long fromPlayerId, long toPlayerId);
}
