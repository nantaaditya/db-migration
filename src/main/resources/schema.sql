CREATE TABLE IF NOT EXISTS database_credentials (
    id varchar(64),
    created_by varchar(10),
    created_date date,
    created_time time,
    modified_by varchar(10),
    modified_date date,
    modified_time time,
    name varchar(255),
    db_host varchar(255),
    db_name varchar(255),
    db_user text,
    db_password text,
    passphrase text,
    CONSTRAINT pkey_database_credentials PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS migration_version(
    id bigint,
    database_id varchar(64),
    created_date timestamp,
    executed_date timestamp,
    migration text,
    migration_status integer,
    rollback text,
    rollback_date timestamp,
    CONSTRAINT pkey_migration_version PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS migration_history(
    id bigint,
    database_id varchar(64),
    created_date timestamp,
    script text,
    status integer,
    CONSTRAINT pkey_migration_history PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS migration_history_seq
    INCREMENT 50
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE INDEX IF NOT EXISTS idx_migrationversion_databaseid
    ON migration_version (database_id);