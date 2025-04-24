create table Users(
	userId varchar,
	userPw varchar,
	userName varchar,
	state char(1),
	constraint PK_NAME primary key (
      	userId
  )
);

CREATE TABLE oauth_access_token
(
    token_id          VARCHAR,
    token             bytea,
    authentication_id VARCHAR,
    user_name         VARCHAR,
    client_id         VARCHAR,
    authentication    bytea,
    refresh_token     VARCHAR,
    constraint PK_TOKEN_ID primary key (
      	token_id
  )
);

-- refresh token 저장 > jwt 아닐때 유효 토큰 검증시 사용
CREATE TABLE IF NOT EXISTS oauth_refresh_token
(
    token_id       VARCHAR NULL,
    token          bytea         NULL,
    authentication bytea         NULL
);


-- 클라이언트 정보 테이블
CREATE TABLE IF NOT EXISTS oauth_client_details
(
    client_id               VARCHAR  NOT NULL,
    resource_ids            VARCHAR  NULL,
    client_secret           VARCHAR  NULL,
    scope                   VARCHAR  NULL,
    authorized_grant_types  VARCHAR  NULL,
    web_server_redirect_uri VARCHAR  NULL,
    authorities             VARCHAR  NULL,
    access_token_validity   INT           NULL,
    refresh_token_validity  INT           NULL,
    additional_information  VARCHAR NULL,
    autoapprove             VARCHAR  NULL,
     constraint CLIENT_PK_NAME primary key (
	    client_id
	  )
);

-- 권한 관리 테이블
create table oauth_approvals
(
    userId         VARCHAR,
    clientId       VARCHAR,
    scope          VARCHAR,
    status         VARCHAR,
    expiresAt      TIMESTAMP,
    lastModifiedAt TIMESTAMP
);

