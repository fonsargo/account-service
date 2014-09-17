package ru.spbsu.apmath.accountservice.service.impl;

import ru.spbsu.apmath.accountservice.service.DataBasePool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 16.09.2014
 * Time: 10:46
 */
public class DBPool implements DataBasePool {
  private String url;
  private String user;
  private String password;

  public DBPool(String url, String user, String password) throws ClassNotFoundException, SQLException {
    this.url = url;
    this.user = user;
    this.password = password;
    Class.forName("org.postgresql.Driver");
  }

  @Override
  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(url, user, password);
  }

  @Override
  public void putConnection(Connection connection) throws SQLException {
    connection.close();
  }
}
