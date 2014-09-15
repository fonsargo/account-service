package ru.spbsu.apmath.accountservice;

import ru.spbsu.apmath.accountservice.service.AccountService;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 15.09.2014
 * Time: 22:44
 */
public class SimpleAccountService implements AccountService {
  @Override
  public Long getAmount(Integer id) {
    return (long)id;
  }

  @Override
  public void addAmount(Integer id, Long value) {
    //ignore
  }
}
