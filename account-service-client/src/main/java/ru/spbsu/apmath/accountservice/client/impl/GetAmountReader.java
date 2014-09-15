package ru.spbsu.apmath.accountservice.client.impl;

import ru.spbsu.apmath.accountservice.client.BufferHandler;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 14.09.2014
 * Time: 17:02
 */
public class GetAmountReader implements BufferHandler {

  private List<Integer> idList;
  private Random random;

  public GetAmountReader(List<Integer> idList) {
    this.idList = idList;
    this.random = new Random();
  }

  @Override
  public ByteBuffer prepareToWrite(ByteBuffer buffer) {
    buffer.clear();
    int id = idList.get(random.nextInt(idList.size()));
    System.out.println(String.format("Write id = %s by %s", id, Thread.currentThread().getName()));
    buffer.putInt(id);
    buffer.flip();
    return buffer;
  }

  @Override
  public ByteBuffer readBuffer(ByteBuffer buffer) {
    buffer.flip();
    long value = buffer.getLong();
    System.out.println(String.format("Got a value = %s by %s", value, Thread.currentThread().getName()));
    buffer.clear();
    return buffer;
  }
}
