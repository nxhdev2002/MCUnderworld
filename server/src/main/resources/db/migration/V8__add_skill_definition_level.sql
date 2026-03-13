-- Add level column to skill definitions (used by SkillDefinition model and JdbcSkillDefinitionRepository).
-- Default 1 for existing rows and for seeds that do not specify level.
ALTER TABLE kiemhiep_skill_definitions
    ADD COLUMN IF NOT EXISTS level INT NOT NULL DEFAULT 1;
