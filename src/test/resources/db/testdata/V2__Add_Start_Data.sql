
INSERT INTO  usr (id,username,password,active)
  VALUES (2 , 'YugSystemAdmin',crypt('p', gen_salt('bf', 8)),true);

INSERT INTO  organization (id,name,user_id)
  VALUES (1,'JugSystem',2);



UPDATE usr set organization_id = 1 where id =2;

INSERT INTO  work_group (id,name,organization_id)
  VALUES (1,'JugSystem_default',1);
INSERT INTO  usr (id,username,password,active,organization_id,work_group_id)
  VALUES (3 , 'MainDisp',crypt('p', gen_salt('bf', 8)),true,1,1);

UPDATE work_group set user_id = 3 where id =1;


INSERT INTO  usr (id,username,password,active,organization_id,work_group_id)
  VALUES (4 , 'Disp1',crypt('p', gen_salt('bf', 8)),true,1,1);

INSERT INTO  usr (id,username,password,active,organization_id,work_group_id)
  VALUES (5 , 'Disp2',crypt('p', gen_salt('bf', 8)),true,1,1);

INSERT INTO  user_role (user_id,roles)
  VALUES (2 , 'SuperUserOwner');

INSERT INTO  user_role (user_id,roles)
  VALUES (3 , 'SuperUser');

INSERT INTO  user_role (user_id,roles)
  VALUES (4 , 'USER');

INSERT INTO  user_role (user_id,roles)
  VALUES (5 , 'USER');


INSERT INTO net_data( id,address,address2,is_active,token,type_of_service,organization_id )
  VALUES (1,'https://bs.net868.ru:20010/externalapi/appdata',
  'https://bs.net868.ru:20010/externalapi/sendCommand',
  false,'1c68a488ec0d4dde80439e9627d23154','Net868',1);

