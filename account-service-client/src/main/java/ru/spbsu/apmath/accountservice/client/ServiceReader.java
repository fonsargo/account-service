package ru.spbsu.apmath.accountservice.client;

import java.nio.ByteBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 14.09.2014
 * Time: 16:49
 */
public interface ServiceReader {
  ByteBuffer prepareBufferToWrite(ByteBuffer buffer);
  void readBuffer(ByteBuffer buffer);
}
