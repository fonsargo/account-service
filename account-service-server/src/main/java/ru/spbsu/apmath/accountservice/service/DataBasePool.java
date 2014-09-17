package ru.spbsu.apmath.accountservice.service;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 16.09.2014
 * Time: 10:21
 */
public interface DataBasePool {
  Connection getConnection() throws SQLException;
  void putConnection(Connection connection) throws SQLException;
}
