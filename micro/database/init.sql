grant all privileges to oauth2 identified by pass;

create table oauth2.users (
  username VARCHAR(255),
  password VARCHAR(255),
  enabled NUMBER(1,0),
  PRIMARY KEY (username)
);

create table oauth2.authorities (
  username VARCHAR2(255),
  authority VARCHAR2(255),
  PRIMARY KEY (username)
);

create table oauth2.oauth_client_details (
  client_id VARCHAR2(255),
  resource_ids VARCHAR2(255),
  client_secret VARCHAR2(255),
  scope VARCHAR2(255),
  authorized_grant_types VARCHAR2(255),
  web_server_redirect_uri VARCHAR2(255),
  authorities VARCHAR2(255),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additional_information VARCHAR2(255),
  autoapprove VARCHAR2(255),
  PRIMARY KEY (client_id)
);

create table oauth2.oauth_access_token (
  token_id VARCHAR2(255),
  token BLOB,
  authentication_id VARCHAR2(255),
  user_name VARCHAR2(255),
  client_id VARCHAR2(255),
  authentication BLOB,
  refresh_token VARCHAR2(255),
  PRIMARY KEY (authentication_id)
);

insert into oauth2.oauth_client_details (client_secret, resource_ids, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, client_id) 
values ('benisecret', null, 'read,write', 'authorization_code', null, null, null, null , '{}', 'read,write', 'beni');

insert into oauth2.users (username, password, enabled) values ('admin', 'pass', 1);

insert into oauth2.users (username, password, enabled) values ('user', 'pass', 1);

insert into oauth2.authorities (username, authority) values ('admin' , 'ROLE_ADMIN');

insert into oauth2.authorities (username, authority) values ('user', 'ROLE_USER');

grant all privileges to amusementpark identified by pass;

create table amusementpark.message (id number(19,0) not null, content varchar2(255 char), primary key (id));

insert into amusementpark.message (id, content) values (0, '1. Amusement Park message');

insert into amusementpark.message (id, content) values (1, '2. Amusement Park message');

insert into amusementpark.message (id, content) values (2, '3. Amusement Park message');

grant all privileges to zoo identified by pass;

create table zoo.message (id number(19,0) not null, content varchar2(255 char), primary key (id));

insert into zoo.message (id, content) values (0, '1. Zoo message');
insert into zoo.message (id, content) values (1, '2. Zoo message');
