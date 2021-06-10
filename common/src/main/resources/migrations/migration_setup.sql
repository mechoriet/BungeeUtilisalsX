CREATE TABLE IF NOT EXISTS bu_migrations
(
    id DATA_TYPE_SERIAL,
    migration_id DATA_TYPE_INT NOT NULL,
    type DATA_TYPE_VARCHAR NOT NULL,
    script DATA_TYPE_VARCHAR,
    created_at DATA_TYPE_DATETIME,
    success DATA_TYPE_BOOLEAN,
    PRIMARY KEY (id)
);