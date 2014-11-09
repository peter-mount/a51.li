----------------------------------------------------------------------------
--
-- Copyright 2014 Peter T Mount.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--      http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--
----------------------------------------------------------------------------

DROP TABLE visit;
DROP TABLE memo;
DROP TABLE memotype;
DROP TABLE remotehost;
DROP TABLE useragent;
DROP TABLE url;
DROP TABLE linktype;

DROP TABLE user_twitter;
DROP TABLE twitter;
DROP TABLE twitapp;
DROP TABLE users;

DROP SEQUENCE memoid;

-----------------------------------------------------------------
-- Maps users who can/have created url's
CREATE TABLE users (
    id          SERIAL,
    username    NAME,
    userkey     TEXT,
    homepage    TEXT,
    enabled     BOOLEAN,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX users_n ON users(username);

-----------------------------------------------------------------
-- Twitter applications
--
-- This holds the keys to the application(s) so we don't keep it
-- in config within the application
CREATE TABLE twitapp (
    id      SERIAL,
    appname NAME,
    key     TEXT,
    secret  TEXT,
    enabled BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX twitapp_a ON twitapp(appname);

-----------------------------------------------------------------
-- Twitter accounts
CREATE TABLE twitter (
    id          SERIAL,
    account     NAME,
    token       TEXT,
    secret      TEXT,
    application BIGINT NOT NULL REFERENCES twitapp(id),
    PRIMARY KEY(id)
);
-- Twitter accounts a user can tweet to
CREATE TABLE user_twitter (
    userid      BIGINT NOT NULL REFERENCES users(id),
    twitterid   BIGINT NOT NULL REFERENCES twitter(id),
    PRIMARY KEY (userid,twitterid)
);

-----------------------------------------------------------------
-- Maps to the LinkType enum

CREATE TABLE linktype (
    id      INTEGER NOT NULL,
    servlet NAME NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX urltype_i ON urltype(id);
INSERT INTO linktype VALUES (1,'/redirector/');
INSERT INTO linktype VALUES (2,'/viewmemo/');
INSERT INTO linktype VALUES (3,'/viewimage/');

-----------------------------------------------------------------
-- A URL
CREATE TABLE url (
    id          SERIAL,
    url         TEXT,
    tm          TIMESTAMP,
    linktype    INTEGER NOT NULL references linktype(id),
    userid      BIGINT NOT NULL REFERENCES users(id),
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX url_t ON url(tm);
CREATE UNIQUE INDEX url_u ON url(url);
CREATE UNIQUE INDEX url_ut ON url(url,tm);
CREATE UNIQUE INDEX url_ui ON url(url,userid);
CREATE UNIQUE INDEX url_uit ON url(url,userid,tm);
CREATE INDEX url_i ON url(userid);

CREATE OR REPLACE FUNCTION createlink(purl TEXT, puser NAME, pkey NAME, ptype integer)
RETURNS BIGINT AS $$
DECLARE
    usr RECORD;
    tmp RECORD;
BEGIN
    -- Url lready exists?
    SELECT * INTO tmp FROM url WHERE url= purl;
    IF FOUND THEN
        RETURN tmp.id;
    END IF;

    -- find and validate the user
    SELECT * INTO usr FROM users WHERE username=puser AND userkey=pkey;
    IF NOT FOUND THEN
        RETURN 0;
    END IF;

    INSERT INTO url (url,tm,linktype,userid) VALUES (purl,now(),ptype,usr.id);
    RETURN currval('url_id_seq');
END;
$$ LANGUAGE plpgsql;

-----------------------------------------------------------------
-- Memo display type
CREATE TABLE memotype (
    id          INTEGER NOT NULL,
    styleName   NAME NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX memotype_i ON memotype(id);
INSERT INTO memotype VALUES (1,'html');
INSERT INTO memotype VALUES (2,'text');
INSERT INTO memotype VALUES (3,'log');

-----------------------------------------------------------------
-- A memo. It's linked to URL
CREATE TABLE memo (
    id          BIGINT NOT NULL REFERENCES url(id),
    title       TEXT,
    memo        TEXT,
    memotype    INTEGER NOT NULL references memotype(id),
    expires     TIMESTAMP,
    PRIMARY KEY(id)
);

CREATE SEQUENCE memoid;

CREATE OR REPLACE FUNCTION creatememo(puser NAME, pkey NAME, ptitle TEXT, ptext TEXT, ptype integer, pexp TIMESTAMP)
RETURNS BIGINT AS $$
DECLARE
    uid BIGINT;
BEGIN
    SELECT createlink( concat('memo:',nextval('memoid')::text), puser, pkey, 2) INTO uid;
    IF uid = 0 THEN
        RETURN 0;
    END IF;

    INSERT INTO memo (id,title,memo,memotype,expires) VALUES (uid,ptitle,pTEXT,ptype,pexp);
    
    RETURN uid;
END;
$$ LANGUAGE plpgsql;


-----------------------------------------------------------------
-- Normalises the useragent

CREATE TABLE useragent (
    id      SERIAL,
    agent   TEXT NOT NULL,
    PRIMARY KEY(id)
);
CREATE UNIQUE INDEX useragent_a ON useragent (agent );

-----------------------------------------------------------------
-- Normalises the remote host address

CREATE TABLE remotehost (
    id      SERIAL,
    remote  VARCHAR(128) NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX remotehost_r ON remotehost(remote);

-----------------------------------------------------------------
-- Logs a user visit

CREATE TABLE visit (
    id          SERIAL,
    url         BIGINT NOT NULL REFERENCES url(id),
    remote      BIGINT NOT NULL REFERENCES remotehost(id),
    useragent   BIGINT NOT NULL REFERENCES useragent(id),
    tm          TIMESTAMP
);

CREATE INDEX visit_u ON visit(url);
CREATE INDEX visit_t ON visit(tm);
CREATE INDEX visit_ut ON visit(url,tm);


CREATE OR REPLACE FUNCTION useragent(val TEXT)
RETURNS BIGINT AS $$
DECLARE
    tmp RECORD;
BEGIN
    SELECT * INTO tmp FROM useragent WHERE agent = val;
    IF FOUND THEN
        RETURN tmp.id;
    END IF;

    INSERT INTO useragent (agent) VALUES (val);
    RETURN currval('useragent_id_seq');
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION remotehost(val TEXT)
RETURNS BIGINT AS $$
DECLARE
    tmp RECORD;
BEGIN
    SELECT * INTO tmp FROM remotehost WHERE remote = val;
    IF FOUND THEN
        RETURN tmp.id;
    END IF;

    INSERT INTO remotehost (remote) VALUES (val);
    RETURN currval('remotehost_id_seq');
END;
$$ LANGUAGE plpgsql;
