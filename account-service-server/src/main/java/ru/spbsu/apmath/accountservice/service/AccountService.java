package ru.spbsu.apmath.accountservice.service;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 12.09.2014
 * Time: 15:45
 */
public interface AccountService {
  /**
   * Retrieves current balance or zero if addAmount() method was not called before for specified id
   *
   * @param id balance identifier
   */
  Long getAmount(Integer id);

  /**
   * Increases balance or set if addAmount() method was called first time
   *
   * @param id    balance identifier
   * @param value positive or negative value, which must be added to current balance
   */
  void addAmount(Integer id, Long value);
}
