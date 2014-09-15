package ru.spbsu.apmath.accountservice.client.impl;

import ru.spbsu.apmath.accountservice.client.BufferHandler;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 14.09.2014
 * Time: 17:14
 */
public class AddAmountReader implements BufferHandler {
  private List<Integer> idList;
  private Random random;

  public AddAmountReader(List<Integer> idList) {
    this.idList = idList;
    this.random = new Random();
  }

  @Override
  public ByteBuffer prepareToWrite(ByteBuffer buffer) {
    buffer.clear();
    int id = idList.get(random.nextInt(idList.size()));
    long value = random.nextLong();
    System.out.println(String.format("[%s] Write id = %s and value = %s", Thread.currentThread().getName(), id, value));
    buffer.putInt(id);
    buffer.putLong(value);
    buffer.flip();
    return buffer;
  }

  @Override
  public ByteBuffer readBuffer(ByteBuffer buffer) {
    buffer.flip();
    char c = buffer.getChar();
    System.out.println(String.format("[%s] Got a char = %s", Thread.currentThread().getName(), c));
    buffer.clear();
    return buffer;
  }
}
