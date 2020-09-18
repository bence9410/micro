create table users (
  username VARCHAR(255),
  password VARCHAR(255),
  enabled int8,
  PRIMARY KEY (username)
);

create table authorities (
  username VARCHAR(255),
  authority VARCHAR(255),
  PRIMARY KEY (username)
);

create table oauth_client_details (
  client_id VARCHAR(255),
  resource_ids VARCHAR(255),
  client_secret VARCHAR(255),
  scope VARCHAR(255),
  authorized_grant_types VARCHAR(255),
  web_server_redirect_uri VARCHAR(255),
  authorities VARCHAR(255),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additional_information VARCHAR(255),
  autoapprove VARCHAR(255),
  PRIMARY KEY (client_id)
);

create table oauth_access_token (
  token_id VARCHAR(255),
  token bytea,
  authentication_id VARCHAR(255),
  user_name VARCHAR(255),
  client_id VARCHAR(255),
  authentication bytea,
  refresh_token VARCHAR(255),
  PRIMARY KEY (authentication_id)
);

insert into oauth_client_details (client_secret, resource_ids, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, client_id) 
values ('benisecret', null, 'read,write', 'authorization_code', null, null, null, null , '{}', 'read,write', 'beni');

insert into users (username, password, enabled) values ('admin', 'pass', 1);

insert into users (username, password, enabled) values ('user', 'pass', 1);

insert into authorities (username, authority) values ('admin' , 'ROLE_ADMIN');

insert into authorities (username, authority) values ('user', 'ROLE_USER');

create table amusementpark_message (id bigserial not null, content varchar(255), primary key (id));

insert into amusementpark_message (id, content) values (0, '1. Amusement Park message');

insert into amusementpark_message (id, content) values (1, '2. Amusement Park message');

insert into amusementpark_message (id, content) values (2, '3. Amusement Park message');

create table zoo_message (id bigserial not null, content varchar(255), primary key (id));

insert into zoo_message (id, content) values (0, '1. Zoo message');
insert into zoo_message (id, content) values (1, '2. Zoo message');

create table visitor_message (id bigserial not null, content varchar(255), primary key (id));

insert into visitor_message (id, content) values (0, '1. Visitor %d message');
insert into visitor_message (id, content) values (1, '2. Visitor %d message');
