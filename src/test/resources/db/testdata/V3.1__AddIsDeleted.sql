-- поля удаленно для классов:
BEGIN;
ALTER TABLE net_data ADD COLUMN  is_deleted boolean;
ALTER TABLE net_data ADD COLUMN  ws_active boolean;
ALTER TABLE usr ADD COLUMN  is_deleted boolean;
ALTER TABLE organization ADD COLUMN  is_deleted boolean;
ALTER TABLE work_group ADD COLUMN  is_deleted boolean;
UPDATE net_data SET is_deleted = false ;
UPDATE net_data SET ws_active = false ;
UPDATE usr SET is_deleted = false ;
UPDATE organization SET is_deleted = false ;
UPDATE work_group SET is_deleted = false ;
COMMIT ;

