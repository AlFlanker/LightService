
-- create extension if not exists pgcrypto;
create sequence hibernate_sequence start 30 increment 1;
create table control_points
(
  id                   int8 not null,
  created              timestamp,
  is_deleted           boolean,
  date_of_last_changed timestamp,
  address              varchar(255),
  latitude             float8,
  longitude            float8,
  object_name          varchar(255),
  organization_id      int8,
  work_group_id        int8,
  primary key (id)
);
create table groups
(
  id            int8    not null,
  is_deleted    boolean not null,
  name_of_group varchar(255),
  obj_id        int8,
  user_id       int8,
  work_group_id int8,
  primary key (id)
);
create table handbook (id int8 not null, tag varchar(255), primary key (id));
create table lamp
(
  id                   int8 not null,
  created              timestamp,
  is_deleted           boolean,
  date_of_last_changed timestamp,
  address              varchar(255),
  latitude             float8,
  longitude            float8,
  alias                varchar(60),
  object_name          varchar(255),
  organization_id      int8,
  work_group_id        int8,
  cp_id                int8,
  group_id             int8,
  primary key (id)
);
create table map_of_translator
(
  handbook_id    int8         not null,
  translator     varchar(255),
  translator_key varchar(255) not null,
  primary key (handbook_id, translator_key)
);
create table net_data
(
  id              int8    not null,
  address         varchar(255),
  address2        varchar(255),
  is_active       boolean not null,
  login           varchar(255),
  password        varchar(255),
  token           varchar(255),
  type_of_service varchar(255),
  organization_id int8,
  primary key (id)
);
create table organization
(
  id      int8 not null,
  name    varchar(255),
  user_id int8,
  primary key (id)
);
create table states
(
  id              int8   not null,
  date_of_changed timestamp,
  brightness      int2   not null,
  i_ac            int4   not null,
  latitude        float4 not null,
  longitude       float4 not null,
  state           int2   not null,
  temperature     int4   not null,
  v_ac            int4   not null,
  vdcboard        int4   not null,
  flags           int2   not null,
  acknowledged    boolean,
  is_deleted      boolean,
  is_passed       boolean,
  type            varchar(255),
  lamp_id         int8,
  primary key (id)
);
create table user_role
(
  user_id int8 not null,
  roles   varchar(255)
);
create table usr
(
  id              int8    not null,
  activation_code varchar(255),
  active          boolean not null,
  email           varchar(255),
  password        varchar(255),
  username        varchar(255),
  organization_id int8,
  work_group_id   int8,
  primary key (id)
);
create table work_group
(
  id              int8 not null,
  name            varchar(255),
  organization_id int8,
  user_id         int8,
  primary key (id)
);
alter table if exists organization
  add constraint UK_8j5y8ipk73yx2joy9yr653c9t unique (name);
alter table if exists control_points
  add constraint FKbcj1vglky26oyuljugfqegysa foreign key (organization_id) references organization;
alter table if exists control_points
  add constraint FKqn9qly06tneod3dvljeu3122g foreign key (work_group_id) references work_group;
  alter table if exists groups
  add constraint groups_ref_work_group foreign key (work_group_id) references work_group;
alter table if exists groups
  add constraint FK7v9mhpnxvn70qwii3ondnv59c foreign key (obj_id) references lamp;
alter table if exists groups
  add constraint FKe9qtv18p2hupfe98wm9xx6jsv foreign key (user_id) references usr;
alter table if exists lamp
  add constraint FKqpqq2s6aanlguceeodrjxg6e4 foreign key (organization_id) references organization;
alter table if exists lamp
  add constraint FKaeklqn5y4kwh6fxw7o06ilqii foreign key (work_group_id) references work_group;
alter table if exists lamp
  add constraint FK3ogwaeglety9581koq1e942nk foreign key (cp_id) references control_points;
alter table if exists lamp
  add constraint FKh6r1oscd0dh3n0dludt0l4xds foreign key (group_id) references groups;
alter table if exists map_of_translator
  add constraint FKky38vjt2loayr7cp8v6kq8y65 foreign key (handbook_id) references handbook;
alter table if exists net_data
  add constraint FKmf9t6elf7jk1xycloxbfh309m foreign key (organization_id) references organization;
alter table if exists organization
  add constraint FK320p6saknnk5smgkcrs2d7qe8 foreign key (user_id) references usr;
alter table if exists states
  add constraint FKrk9dyn3uyhjvjsyrcc3ej3vn2 foreign key (lamp_id) references lamp;
alter table if exists user_role
  add constraint FKfpm8swft53ulq2hl11yplpr5 foreign key (user_id) references usr;
alter table if exists usr
  add constraint FKp96ghcoiahdqm697daggv2hdq foreign key (organization_id) references organization;
alter table if exists usr
  add constraint FKbkab383mejij0buyvpmf5hbxn foreign key (work_group_id) references work_group;
alter table if exists work_group
  add constraint FK2bvxn00echc59i0r5fqfbg1re foreign key (organization_id) references organization;
alter table if exists work_group
  add constraint FKiul3b18tgkgy73q0ru8ci5bkt foreign key (user_id) references usr;
INSERT INTO handbook (id, tag)
VALUES (1, 'vAc'),
       (2, 'iAc'),
       (3, 'temp'),
       (4, 'vDCB'),
       (5, 'lat'),
       (6, 'lon'),
       (7, 'brss'),
       (8, 'stat');
insert into map_of_translator(handbook_id, translator, translator_key)
values (1, 'входное напряжение питания', 'RU'),
       (2, 'входной ток', 'RU'),
       (3, 'температура', 'RU'),
       (4, 'напряжение питания', 'RU'),
       (5, 'долгота', 'RU'),
       (6, 'широта', 'RU'),
       (7, 'яркость', 'RU'),
       (8, 'состояние', 'RU');
INSERT INTO usr (id, username, password, active)
VALUES (1, 'admin', crypt('root', gen_salt('bf', 8)), true);

INSERT INTO user_role (user_id, roles)
VALUES (1, 'ADMIN');