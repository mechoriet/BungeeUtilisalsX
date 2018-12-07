/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.library;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import lombok.Getter;

public enum StandardLibrary {

    SQLITE(
            "org.sqlite.JDBC",
            "http://central.maven.org/maven2/org/xerial/sqlite-jdbc/{version}/sqlite-jdbc-{version}.jar",
            "3.25.2",
            checkType("SQLITE")
    ),
    MARIADB(
            "org.mariadb.jdbc.MariaDbDataSource",
            "http://central.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/{version}/mariadb-java-client-{version}.jar",
            "2.3.0",
            checkType("MARIADB")
    ),
    POSTGRESQL(
            "org.postgresql.ds.PGSimpleDataSource",
            "http://central.maven.org/maven2/org/postgresql/postgresql/{version}/postgresql-{version}.jar",
            "42.2.5",
            checkType("POSTGRESQL")
    ),
    MONGODB(
            "com.mongodb.MongoClient",
            "http://central.maven.org/maven2/org/mongodb/mongo-java-driver/{version}/mongo-java-driver-{version}.jar",
            "3.8.2",
            checkType("MONGODB")
    ),
    HIKARIDB(
            "com.zaxxer.hikari.HikariDataSource",
            "http://central.maven.org/maven2/com/zaxxer/HikariCP/{version}/HikariCP-{version}.jar",
            "3.2.0",
            checkType("MYSQL", "MARIADB", "POSTGRESQL")
    ),
    GOOGLE_HTTP_CLIENT(
            "com.google.api.client.http.HttpRequest",
            "http://central.maven.org/maven2/com/google/http-client/google-http-client/1.26.0/google-http-client-{version}.jar",
            "1.26.0",
            true
    );

    @Getter
    private final Library library;

    StandardLibrary(String className, String downloadURL, String version, boolean load) {
        this.library = new Library(toString(), className, downloadURL, version, load);
    }

    private static boolean checkType(String... types) {
        for (String type : types) {
            if (BungeeUtilisals.getInstance().getConfig().getString("storage.type").equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
}