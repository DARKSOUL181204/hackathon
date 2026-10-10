ALTER TABLE assets ADD COLUMN assigned_personnel_id BIGINT NULL;
ALTER TABLE assets ADD CONSTRAINT fk_assets_assigned_personnel
    FOREIGN KEY (assigned_personnel_id) REFERENCES personnel(id);
