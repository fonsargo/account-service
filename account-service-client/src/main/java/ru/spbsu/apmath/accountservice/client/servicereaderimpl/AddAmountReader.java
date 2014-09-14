package ru.spbsu.apmath.accountservice.client.servicereaderimpl;

import ru.spbsu.apmath.accountservice.client.ServiceReader;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 14.09.2014
 * Time: 17:14
 */
public class AddAmountReader implements ServiceReader {
  private List<Integer> idList;
  private Random random;

  public AddAmountReader(List<Integer> idList) {
    this.idList = idList;
  }

  @Override
  public ByteBuffer prepareBufferToWrite(ByteBuffer buffer) {
    buffer.clear();
    int id = idList.get(random.nextInt(idList.size()));
    long value = random.nextLong();
    System.out.println(String.format("Write id = %s and value = %s by %s", id, value, Thread.currentThread().getName()));
    buffer.putInt(id);
    buffer.putLong(value);
    return buffer;
  }

  @Override
  public void readBuffer(ByteBuffer buffer) {
    buffer.flip();
    char c = buffer.getChar();
    System.out.println(String.format("Got a char = %s by %s", c, Thread.currentThread().getName()));
  }
}
