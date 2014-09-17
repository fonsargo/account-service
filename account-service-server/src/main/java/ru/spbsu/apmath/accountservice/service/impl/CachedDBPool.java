package ru.spbsu.apmath.accountservice.service.impl;

import org.postgresql.ds.PGPoolingDataSource;
import ru.spbsu.apmath.accountservice.service.DataBasePool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 17.09.2014
 * Time: 13:01
 */
public class CachedDBPool implements DataBasePool {

  private PGPoolingDataSource source;

  public CachedDBPool(String host, String database, String user, String password) {
    source = new PGPoolingDataSource();
    source.setDataSourceName("A Data Source");
    source.setServerName(host);
    source.setDatabaseName(database);
    source.setUser(user);
    source.setPassword(password);
    source.setMaxConnections(20);
    source.setInitialConnections(20);
  }

  @Override
  public Connection getConnection() throws SQLException {
    return source.getConnection();
  }

  @Override
  public void putConnection(Connection connection) throws SQLException {
    connection.close();
  }
}
