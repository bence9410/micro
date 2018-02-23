--drop table users;
--drop table authorities;
--drop table oauth_client_details;
--drop table oauth_access_token;

create table users (
  username VARCHAR(255),
  password VARCHAR(255),
  enabled NUMBER(1,0),
  PRIMARY KEY (username)
);

create table authorities (
  username VARCHAR2(255),
  authority VARCHAR2(255),
  PRIMARY KEY (username)
);

create table oauth_client_details (
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

create table oauth_access_token (
  token_id VARCHAR2(255),
  token BLOB,
  authentication_id VARCHAR2(255),
  user_name VARCHAR2(255),
  client_id VARCHAR2(255),
  authentication BLOB,
  refresh_token VARCHAR2(255),
  PRIMARY KEY (authentication_id)
);
