package ru.spbsu.apmath.accountservice.service.impl;

import ru.spbsu.apmath.accountservice.service.AccountService;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 12.09.2014
 * Time: 15:59
 */
public class AccountServiceImpl implements AccountService {

  @Override
  public Long getAmount(Integer id) {
    return (long) id;
  }

  @Override
  public void addAmount(Integer id, Long value) {
    //ignore
  }
}
