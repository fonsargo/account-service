package ru.spbsu.apmath.accountservice.service.impl;

import ru.spbsu.apmath.accountservice.service.AccountService;
import ru.spbsu.apmath.accountservice.service.DataBasePool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 12.09.2014
 * Time: 15:59
 */
public class AccountServiceImpl implements AccountService {

  private static final String GET_AMOUNT_REQUEST = "SELECT balance FROM users WHERE accountid = ?";
  private static final String ADD_AMOUNT_REQUEST = "SELECT add_amount(?, ?)";

  private DataBasePool dataBasePool;
  private AtomicInteger getRequests;
  private AtomicInteger addRequests;

  public AccountServiceImpl(DataBasePool dataBasePool) {
    getRequests = new AtomicInteger(0);
    addRequests = new AtomicInteger(0);
    this.dataBasePool = dataBasePool;
  }

  public int getGetRequests() {
    return getRequests.get();
  }

  public int getAddRequests() {
    return addRequests.get();
  }

  public void reset() {
    getRequests.set(0);
    addRequests.set(0);
  }

  @Override
  public Long getAmount(Integer id) {
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    long result;
    try {
      connection = dataBasePool.getConnection();
      preparedStatement = connection.prepareStatement(GET_AMOUNT_REQUEST);
      preparedStatement.setInt(1, id);
      resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        result = resultSet.getLong(1);
      } else {
        result = 0;
      }
    } catch (SQLException e) {
      System.out.println(String.format("[%s] SQL ERROR: %s", Thread.currentThread().getName(), e));
      throw new RuntimeException(e);
    } finally {
      closeObjects(connection, preparedStatement, resultSet);
    }
    getRequests.incrementAndGet();
    return result;
  }

  @Override
  public void addAmount(Integer id, Long value) {
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      connection = dataBasePool.getConnection();
      preparedStatement = connection.prepareStatement(ADD_AMOUNT_REQUEST);
      preparedStatement.setInt(1, id);
      preparedStatement.setLong(2, value);
      resultSet = preparedStatement.executeQuery();
    } catch (SQLException e) {
      System.out.println(String.format("[%s] SQL ERROR: %s", Thread.currentThread().getName(), e));
      throw new RuntimeException(e);
    } finally {
      closeObjects(connection, preparedStatement, resultSet);
    }
    addRequests.incrementAndGet();
  }

  private void closeObjects(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
    if (resultSet != null) {
      try {
        resultSet.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    if (preparedStatement != null) {
      try {
        preparedStatement.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    if (connection != null) {
      try {
        dataBasePool.putConnection(connection);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
