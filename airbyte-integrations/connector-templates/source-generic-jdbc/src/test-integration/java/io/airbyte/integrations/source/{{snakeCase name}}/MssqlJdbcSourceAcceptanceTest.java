/*
 * MIT License
 *
 * Copyright (c) 2020 Airbyte
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.airbyte.integrations.source.mssql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import io.airbyte.commons.json.Jsons;
import io.airbyte.db.Databases;
import io.airbyte.db.jdbc.JdbcDatabase;
import io.airbyte.integrations.source.jdbc.AbstractJdbcSource;
import io.airbyte.integrations.source.jdbc.test.JdbcSourceAcceptanceTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.JdbcDatabaseContainer;


public class {{pascalCase name}}JdbcSourceAcceptanceTest extends JdbcSourceAcceptanceTest {



  private static JdbcDatabaseContainer dbContainer; //TODO Add your container here instead of this one. Ex.org.testcontainers.containers.MSSQLServerContainer<?>
  private static JdbcDatabase database;
  private JsonNode config;

  @BeforeAll
  static void init() {

    // TODO init your container.
    //  Ex:dbContainer = new org.testcontainers.containers.MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2019-latest").acceptLicense();
    dbContainer = new JdbcDatabaseContainer();
    dbContainer.start();
  }

  @BeforeEach
  public void setup() throws Exception {
    final JsonNode configWithoutDbName = Jsons.jsonNode(ImmutableMap.builder()
        .put("host", dbContainer.getHost())
        .put("port", dbContainer.getFirstMappedPort())
        .put("username", dbContainer.getUsername())
        .put("password", dbContainer.getPassword())
        .build());

    database = Databases.createJdbcDatabase(
        configWithoutDbName.get("username").asText(),
        configWithoutDbName.get("password").asText(),
        String.format("jdbc:sqlserver://%s:%s",
            configWithoutDbName.get("host").asText(),
            configWithoutDbName.get("port").asInt()),
        "add_your_driver_here"); // TODO add the driver name. Ex."com.microsoft.sqlserver.jdbc.SQLServerDriver"

    final String dbName = "db_" + RandomStringUtils.randomAlphabetic(10).toLowerCase();

    database.execute(
        ctx -> ctx.createStatement().execute(String.format("CREATE DATABASE %s;", dbName)));

    config = Jsons.clone(configWithoutDbName);
    ((ObjectNode) config).put("database", dbName);

    super.setup();
  }

  @AfterAll
  public static void cleanUp() throws Exception {
    database.close();
    dbContainer.close();
  }

  @Override
  public boolean supportsSchemas() {
    return true;
  }

  @Override
  public JsonNode getConfig() {
    return Jsons.clone(config);
  }

  @Override
  public AbstractJdbcSource getSource() {
    return new {{pascalCase name}}Source();
  }

  @Override
  public String getDriverClass() {
    return {{pascalCase name}}Source.DRIVER_CLASS;
  }

}
