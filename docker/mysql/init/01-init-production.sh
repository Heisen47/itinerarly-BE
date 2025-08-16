#!/bin/bash
# Database initialization script for production

# Create application-specific user with limited privileges
mysql -u root -p"$MYSQL_ROOT_PASSWORD" <<-EOSQL
    CREATE USER IF NOT EXISTS '${MYSQL_USER}'@'%' IDENTIFIED BY '${MYSQL_PASSWORD}';
    GRANT SELECT, INSERT, UPDATE, DELETE ON ${MYSQL_DATABASE}.* TO '${MYSQL_USER}'@'%';
    FLUSH PRIVILEGES;

    -- Set timezone
    SET GLOBAL time_zone = '+00:00';

    -- Optimize for production
    SET GLOBAL innodb_buffer_pool_size = 268435456; -- 256MB
    SET GLOBAL query_cache_size = 33554432; -- 32MB
EOSQL

echo "Database initialization completed for production environment"
