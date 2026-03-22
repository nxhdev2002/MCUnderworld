-- Fix typo: skill_rAGING_thunder -> skill_raging_thunder (resource IDs must be [a-z0-9/._-])
UPDATE kiemhiep_skill_definitions
SET skill_id = 'skill_raging_thunder'
WHERE skill_id = 'skill_rAGING_thunder';
