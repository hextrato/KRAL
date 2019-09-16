-- ==================================================
-- GENERAL
-- ==================================================

create extension "uuid-ossp";

-- ==================================================
-- MODEL :: KRAL Admin
-- ==================================================

-- VARCHAR2 => VARCHAR
-- DATE => TIMESTAMP
-- CHAR(1) => boolean ?
-- NUMBER(x,y) => NUMERIC(x,y)

-- -- -- -- -- -- -- --  -- --  -- --  -- --  -- --
-- TABLE :: kdm_object
-- -- -- -- -- -- -- --  -- --  -- --  -- --  -- --

-- DROP TABLE kdm_object;
CREATE TABLE kdm_object (
    uid_object       CHAR(32) NOT NULL,
    nme_object_type  VARCHAR(30) NOT NULL,
    uid_object_ref   CHAR(32) NOT NULL,
    dtm_created   TIMESTAMP NOT NULL default now(),
    dtm_expires   TIMESTAMP default null
);

ALTER TABLE kdm_object ADD CONSTRAINT kdm_object_pk PRIMARY KEY ( uid_object );

ALTER TABLE kdm_object ADD CONSTRAINT kdm_object_uid_ref_uk UNIQUE ( uid_object_ref );

-- DROP FUNCTION create_kdm_object(VARCHAR);
CREATE OR REPLACE FUNCTION create_kdm_object(pnme_object_type VARCHAR) RETURNS char(32) AS $$
DECLARE
	vuid_object CHAR(32);
	vref_object CHAR(32);
BEGIN
	vuid_object := replace(uuid_generate_v4()::text,'-','');
	vref_object := replace(uuid_generate_v4()::text,'-','');
	INSERT into kdm_object (uid_object,nme_object_type,uid_object_ref) values (vuid_object,pnme_object_type,vref_object);
	return vuid_object;
END; $$
LANGUAGE PLPGSQL;

-- example
select create_kdm_object('KDMUSER');

-- cleaning
DELETE FROM kdm_object WHERE nme_object_type = 'KDMUSER' AND uid_object NOT IN (SELECT uid_user FROM KDM_USER);
DELETE FROM kdm_object WHERE nme_object_type = 'KDSCONNECTOR' AND uid_object NOT IN (SELECT uid_connector FROM KDS_CONNECTOR);
DELETE FROM kdm_object WHERE nme_object_type = 'KDSVIEW' AND uid_object NOT IN (SELECT uid_view FROM KDS_VIEW);

-- -- -- -- -- -- -- --  -- --  -- --  -- --  -- --
-- TABLE :: kdm_user
-- -- -- -- -- -- -- --  -- --  -- --  -- --  -- --

-- DROP TABLE kdm_user;
-- DROP SEQUENCE kdm_user_sq;

CREATE SEQUENCE kdm_user_sq;

CREATE TABLE kdm_user (
    seq_user       INTEGER NOT NULL DEFAULT nextval('kdm_user_sq'),
    uid_user       CHAR(32) NOT NULL,
    nck_user       VARCHAR(30) NOT NULL,
    nme_user       VARCHAR(100) NOT NULL,
    cod_login      VARCHAR(100),
    cod_password   VARCHAR(100),
    flg_active     BOOLEAN NOT NULL default true
);

ALTER SEQUENCE kdm_user_sq OWNED BY kdm_user.seq_user;

ALTER TABLE kdm_user ADD CONSTRAINT kdm_user_pk PRIMARY KEY ( seq_user );

ALTER TABLE kdm_user ADD CONSTRAINT kdm_user_uid_uk UNIQUE ( uid_user );

ALTER TABLE kdm_user
    ADD CONSTRAINT kdm_object_user_fk FOREIGN KEY ( uid_user )
        REFERENCES kdm_object ( uid_object );

CREATE UNIQUE INDEX kdm_user_login_uk ON kdm_user (cod_login);
		
-- DROP FUNCTION create_kdm_user(VARCHAR,VARCHAR);
CREATE OR REPLACE FUNCTION create_kdm_user(pnck_user VARCHAR,pnme_user VARCHAR) RETURNS char(32) AS $$
DECLARE
	vuid CHAR(32);
	vseq INTEGER;
BEGIN
	vuid := create_kdm_object('KDMUSER');
	INSERT into kdm_user (uid_user,nck_user,nme_user) values (vuid,pnck_user,pnme_user) RETURNING seq_user INTO vseq;
	return vseq;
END; $$
LANGUAGE PLPGSQL;

-- DROP FUNCTION create_kdm_user(VARCHAR);
CREATE OR REPLACE FUNCTION create_kdm_user(pnck_user VARCHAR) RETURNS char(32) AS $$
BEGIN
	return create_kdm_user(pnck_user,pnck_user);
END; $$
LANGUAGE PLPGSQL;

-- example
select create_kdm_user('admin','admin');
select create_kdm_user('kral');
select * from kdm_object;
select * from kdm_user;

-- ==================================================
-- MODEL :: KRAL Admin
-- ==================================================

-- -- -- -- -- -- -- --  -- --  -- --  -- --  -- --
-- TABLE :: kds_connector
-- -- -- -- -- -- -- --  -- --  -- --  -- --  -- --

CREATE SEQUENCE kds_connector_sq;

CREATE TABLE kds_connector (
    seq_connector   INTEGER NOT NULL DEFAULT nextval('kds_connector_sq'),
    uid_connector   CHAR(32) NOT NULL,
    nck_connector   VARCHAR(30) NOT NULL,
    nme_connector   VARCHAR(100) NOT NULL,
    url_type        VARCHAR(10),
    url_host        VARCHAR(100),
    url_port        NUMERIC(5,0),
    url_source      VARCHAR(100),
    url_username    VARCHAR(100),
    url_password    VARCHAR(100)
);

ALTER SEQUENCE kds_connector_sq OWNED BY kds_connector.seq_connector;

ALTER TABLE kds_connector ADD CONSTRAINT kds_connector_pk PRIMARY KEY ( seq_connector );

ALTER TABLE kds_connector ADD CONSTRAINT kds_connector_uid_uk UNIQUE ( uid_connector );

ALTER TABLE kds_connector
    ADD CONSTRAINT kds_object_connector_fk FOREIGN KEY ( uid_connector )
        REFERENCES kdm_object ( uid_object );

-- DROP FUNCTION create_kds_connector(VARCHAR,VARCHAR);
CREATE OR REPLACE FUNCTION create_kds_connector(pnck_connector VARCHAR,pnme_connector VARCHAR) RETURNS char(32) AS $$
DECLARE
	vuid CHAR(32);
	vseq INTEGER;
BEGIN
	vuid := create_kdm_object('KDSCONNECTOR');
	INSERT into kds_connector (uid_connector,nck_connector,nme_connector) values (vuid,pnck_connector,pnme_connector) RETURNING seq_connector INTO vseq;
	return vseq;
END; $$
LANGUAGE PLPGSQL;

-- DROP FUNCTION create_kds_connector(VARCHAR);
CREATE OR REPLACE FUNCTION create_kds_connector(pnck_connector VARCHAR) RETURNS char(32) AS $$
BEGIN
	return create_kds_connector(pnck_connector,pnck_connector);
END; $$
LANGUAGE PLPGSQL;

-- example
select create_kds_connector('hextrato');
select * from kdm_object where nme_object_type = 'KDSCONNECTOR';
select * from kds_connector;

-- -- -- -- -- -- -- --  -- --  -- --  -- --  -- --
-- TABLE :: kds_view
-- -- -- -- -- -- -- --  -- --  -- --  -- --  -- --

CREATE SEQUENCE kds_view_sq;

CREATE TABLE kds_view (
    seq_view          INTEGER NOT NULL DEFAULT nextval('kds_view_sq'),
    uid_view          CHAR(32) NOT NULL,
    nck_view          VARCHAR(30) NOT NULL,
    nme_view          VARCHAR(100) NOT NULL,
    nme_view_object   VARCHAR(250) NOT NULL,
    seq_connector     INTEGER NOT NULL
);

ALTER SEQUENCE kds_view_sq OWNED BY kds_view.seq_view;

ALTER TABLE kds_view ADD CONSTRAINT kds_view_pk PRIMARY KEY ( seq_view );

ALTER TABLE kds_view ADD CONSTRAINT kds_view_uid_uk UNIQUE ( uid_view );

ALTER TABLE kds_view
    ADD CONSTRAINT kds_connector_view_fk FOREIGN KEY ( seq_connector )
        REFERENCES kds_connector ( seq_connector );

ALTER TABLE kds_view
    ADD CONSTRAINT kds_object_view__fk FOREIGN KEY ( uid_view )
        REFERENCES kdm_object ( uid_object );

-- DROP FUNCTION create_kds_view(VARCHAR,VARCHAR,VARCHAR,INTEGER);
CREATE OR REPLACE FUNCTION create_kds_view(pnck_view VARCHAR,pnme_view VARCHAR,pnme_view_object VARCHAR,pseq_connector INTEGER) RETURNS char(32) AS $$
DECLARE
	vuid CHAR(32);
	vseq INTEGER;
	vseq_connector INTEGER;
BEGIN
	SELECT min(seq_connector) INTO vseq_connector FROM kds_connector WHERE seq_connector = pseq_connector;
	IF vseq_connector IS NULL THEN
		raise 'KDSCONNECTOR: invalid';
	END IF;
	vuid := create_kdm_object('KDSVIEW');
	INSERT into kds_view (uid_view,nck_view,nme_view,nme_view_object,seq_connector) values (vuid,pnck_view,pnme_view,pnme_view_object,pseq_connector) RETURNING seq_view INTO vseq;
	return vseq;
END; $$
LANGUAGE PLPGSQL;

-- DROP FUNCTION create_kds_view(VARCHAR,INTEGER);
CREATE OR REPLACE FUNCTION create_kds_view(pnck_view VARCHAR,pseq_connector INTEGER) RETURNS char(32) AS $$
BEGIN
	return create_kds_view(pnck_view,pnck_view,pnck_view,pseq_connector);
END; $$
LANGUAGE PLPGSQL;

-- example
select create_kds_view('patient','Basic patient demographic data','VIP_USUARIO',0);
select create_kds_view('patient','Basic patient demographic data','VIP_USUARIO',5);
select create_kds_view('test',3);
select * from kdm_object where nme_object_type = 'KDSVIEW';
select * from kds_view;

-- -- -- -- -- -- -- --  -- --  -- --  -- --  -- --
-- TABLE :: kds_attribute
-- -- -- -- -- -- -- --  -- --  -- --  -- --  -- --

CREATE SEQUENCE kds_attribute_sq;

CREATE TABLE kds_attribute (
    seq_view                    INTEGER NOT NULL,
    seq_attribute               INTEGER NOT NULL DEFAULT nextval('kds_attribute_sq'),
    uid_attribute               CHAR(32) NOT NULL,
    nck_attribute               VARCHAR(30) NOT NULL,
    nme_attribute               VARCHAR(100) NOT NULL,
    nme_attribute_object        VARCHAR(250) NOT NULL,
    seq_attribute_property_of   INTEGER
);

ALTER SEQUENCE kds_attribute_sq OWNED BY kds_attribute.seq_attribute;

ALTER TABLE kds_attribute ADD CONSTRAINT kds_attribute_pk PRIMARY KEY ( seq_view, seq_attribute );

ALTER TABLE kds_attribute ADD CONSTRAINT kds_attribute_seq_uk UNIQUE ( seq_attribute );

ALTER TABLE kds_attribute ADD CONSTRAINT kds_attribute_uid_uk UNIQUE ( uid_attribute );

ALTER TABLE kds_attribute
    ADD CONSTRAINT kds_attribute_property_of_fk FOREIGN KEY ( seq_view, seq_attribute_property_of )
        REFERENCES kds_attribute ( seq_view, seq_attribute );

ALTER TABLE kds_attribute
    ADD CONSTRAINT kds_object_attribute_fk FOREIGN KEY ( uid_attribute )
        REFERENCES kdm_object ( uid_object );

ALTER TABLE kds_attribute
    ADD CONSTRAINT kds_view_attribute_fk FOREIGN KEY ( seq_view )
        REFERENCES kds_view ( seq_view );
		
-- DROP FUNCTION create_kds_attribute(VARCHAR,VARCHAR,VARCHAR,INTEGER);
CREATE OR REPLACE FUNCTION create_kds_attribute(pnck_attribute VARCHAR,pnme_attribute VARCHAR,pnme_attribute_object VARCHAR,pseq_view INTEGER) RETURNS char(32) AS $$
DECLARE
	vuid CHAR(32);
	vseq INTEGER;
	vseq_view INTEGER;
BEGIN
	SELECT min(seq_view) INTO vseq_view FROM kds_view WHERE seq_view = pseq_view;
	IF vseq_view IS NULL THEN
		raise 'KDSVIEW: invalid';
	END IF;
	vuid := create_kdm_object('KDSATTRIBUTE');
	INSERT into kds_attribute (uid_attribute,nck_attribute,nme_attribute,nme_attribute_object,seq_view) values (vuid,pnck_attribute,pnme_attribute,pnme_attribute_object,pseq_view) RETURNING seq_attribute INTO vseq;
	return vseq;
END; $$
LANGUAGE PLPGSQL;

-- DROP FUNCTION create_kds_attribute(VARCHAR,INTEGER);
CREATE OR REPLACE FUNCTION create_kds_attribute(pnck_attribute VARCHAR,pseq_view INTEGER) RETURNS char(32) AS $$
BEGIN
	return create_kds_attribute(pnck_attribute,pnck_attribute,pnck_attribute,pseq_view);
END; $$
LANGUAGE PLPGSQL;

-- example
select create_kds_attribute('CD_USUARIO',2);
select create_kds_attribute('NM_USUARIO',2);
update kds_attribute set seq_attribute_property_of = 1 where seq_attribute=2;
select * from kdm_object where nme_object_type = 'KDSATTRIBUTE';
select * from kds_attribute;

