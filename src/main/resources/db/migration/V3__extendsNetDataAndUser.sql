-- url для вебсокетов
ALTER TABLE net_data ADD COLUMN  ws_adr varchar(100);
-- колонки даты создания и обновления для Данных
ALTER TABLE net_data ADD COLUMN  updated timestamp;
ALTER TABLE net_data ADD COLUMN  created timestamp;
-- колонки даты создания и обновления для Юзеров
ALTER TABLE usr ADD COLUMN  updated timestamp;
ALTER TABLE usr ADD COLUMN  created timestamp;

-- колонки даты создания и обновления для организации
ALTER TABLE organization ADD COLUMN  updated timestamp;
ALTER TABLE organization ADD COLUMN  created timestamp;

-- колонки даты создания и обновления для рабочей группы
ALTER TABLE work_group ADD COLUMN  updated timestamp;
ALTER TABLE work_group ADD COLUMN  created timestamp;
