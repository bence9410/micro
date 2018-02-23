insert into oauth_client_details (client_secret, resource_ids, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, client_id) 
values ('benisecret', null, 'read,write', 'authorization_code', null, null, null, null , '{}', 'read,write', 'beni');
insert into users (username, password, enabled) values ('admin', 'pass', 1);
insert into users (username, password, enabled) values ('user', 'pass', 1);
insert into authorities (username, authority) values ('admin' , 'ROLE_ADMIN');
insert into authorities (username, authority) values ('user', 'ROLE_USER');
