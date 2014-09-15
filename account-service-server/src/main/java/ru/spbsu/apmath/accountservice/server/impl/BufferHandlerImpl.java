package ru.spbsu.apmath.accountservice.server.impl;

import ru.spbsu.apmath.accountservice.server.BufferHandler;
import ru.spbsu.apmath.accountservice.service.AccountService;

import java.nio.ByteBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 15.09.2014
 * Time: 20:56
 */
public class BufferHandlerImpl implements BufferHandler {
  AccountService accountService;

  public BufferHandlerImpl(AccountService accountService) {
    this.accountService = accountService;
  }

  @Override
  public ByteBuffer prepareToWrite(ByteBuffer buffer) {
    return buffer;
  }

  @Override
  public ByteBuffer readBuffer(ByteBuffer buffer) {
    buffer.flip();
    if (buffer.limit() == 4) {
      int id = buffer.getInt();
      System.out.println(String.format("[%s] Calling getAmount(%s)", Thread.currentThread().getName(), id));
      long value = accountService.getAmount(id);
      System.out.println(String.format("[%s] Getting value=%s", Thread.currentThread().getName(), value));
      buffer.clear();
      buffer.putLong(value);
    } else if (buffer.limit() == 12) {
      int id = buffer.getInt();
      long value = buffer.getLong();
      System.out.println(String.format("[%s] Calling addAmount(%s, %s)", Thread.currentThread().getName(), id, value));
      accountService.addAmount(id, value);
      buffer.clear();
      buffer.putChar('t'); //true
    } else {
      buffer.clear();
      buffer.putChar('f'); //false
    }
    buffer.flip();
    return buffer;
  }
}
