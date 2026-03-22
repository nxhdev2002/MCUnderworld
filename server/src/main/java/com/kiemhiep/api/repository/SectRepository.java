package com.kiemhiep.api.repository;

import com.kiemhiep.api.model.Sect;
import com.kiemhiep.api.model.SectMember;
import com.kiemhiep.api.model.SectRelation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SectRepository {

    // --- Sect operations ---
    Optional<Sect> getById(long id);

    Optional<Sect> getByName(String name);

    Sect save(Sect sect);

    void deleteById(long id);

    /**
     * Returns all sects. For admin or small datasets only; do not use in hot path.
     * Large tables will load entirely into memory.
     */
    List<Sect> findAll();

    // --- Member operations ---
    List<SectMember> getMembers(long sectId);

    Optional<SectMember> getMember(long sectId, long playerId);

    List<SectMember> getMembersByPlayer(long playerId);

    /**
     * Add a member to a sect. Returns the saved member record.
     */
    SectMember joinMember(long sectId, long playerId, SectMember.Rank rank);

    /**
     * Update a member's rank and contribution.
     */
    void updateMember(long sectId, long playerId, SectMember.Rank rank, int contribution);

    void deleteMember(long sectId, long playerId);

    /**
     * Returns all members of a sect with specific rank.
     */
    List<SectMember> getMembersByRank(long sectId, SectMember.Rank rank);

    // --- Relation operations ---
    List<SectRelation> getRelations(long sectId);

    Optional<SectRelation> getRelation(long sectId, long relatedSectId);

    void deleteRelation(long sectId, long relatedSectId);

    /**
     * Get all relations for a sect with specific type.
     */
    List<SectRelation> getRelationsByType(long sectId, SectRelation.Type type);
}
