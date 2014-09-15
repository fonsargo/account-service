package ru.spbsu.apmath.accountservice.client;

import java.nio.ByteBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 15.09.2014
 * Time: 20:51
 */
public interface BufferHandler {
  ByteBuffer prepareToWrite(ByteBuffer buffer);

  ByteBuffer readBuffer(ByteBuffer buffer);
}
