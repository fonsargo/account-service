package ru.spbsu.apmath.accountservice.service.impl;

import ru.spbsu.apmath.accountservice.service.AccountService;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 12.09.2014
 * Time: 15:59
 */
public class AccountServiceImpl implements AccountService {

  AtomicInteger getRequests = new AtomicInteger(0);
  AtomicInteger addRequests = new AtomicInteger(0);

  @Override
  public Long getAmount(Integer id) {
    getRequests.incrementAndGet();
    return (long) id;
  }

  @Override
  public void addAmount(Integer id, Long value) {
    addRequests.incrementAndGet();
    //ignore
  }

  public int getGetRequests() {
    return getRequests.get();
  }

  public int getAddRequests() {
    return addRequests.get();
  }
}
