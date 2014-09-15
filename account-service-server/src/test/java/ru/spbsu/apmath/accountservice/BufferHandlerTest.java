package ru.spbsu.apmath.accountservice;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.spbsu.apmath.accountservice.server.BufferHandler;
import ru.spbsu.apmath.accountservice.server.impl.BufferHandlerImpl;

import java.nio.ByteBuffer;
import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 15.09.2014
 * Time: 23:14
 */
public class BufferHandlerTest {

  private static BufferHandler bufferHandler;
  private static Random random;

  @BeforeClass
  public static void init() {
    bufferHandler = new BufferHandlerImpl(new SimpleAccountService());
    random = new Random();
  }

  @Test
  public void addAmountBufferTest() {
    ByteBuffer byteBuffer = ByteBuffer.allocate(16).putInt(random.nextInt()).putLong(random.nextLong());
    byteBuffer = bufferHandler.readBuffer(byteBuffer);
    assertThat("BufferHandler не вернул ожидаемого значения", byteBuffer.getChar(), equalTo('t'));
  }

  @Test
  public void getAmountBufferTest() {
    int id = random.nextInt();
    ByteBuffer byteBuffer = ByteBuffer.allocate(16).putInt(id);
    byteBuffer = bufferHandler.readBuffer(byteBuffer);
    assertThat("BufferHandler не вернул ожидаемого значения", byteBuffer.getLong(), equalTo((long)id));
  }

  @Test
  public void wrongBufferTest() {
    ByteBuffer byteBuffer = ByteBuffer.allocate(16).putInt(random.nextInt()).putInt(random.nextInt());
    byteBuffer = bufferHandler.readBuffer(byteBuffer);
    assertThat("BufferHandler не вернул ожидаемого значения", byteBuffer.getChar(), equalTo('f'));
  }
}
