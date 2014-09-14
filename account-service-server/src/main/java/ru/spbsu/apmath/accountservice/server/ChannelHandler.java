package ru.spbsu.apmath.accountservice.server;

import ru.spbsu.apmath.accountservice.service.AccountService;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 12.09.2014
 * Time: 20:23
 */
public class ChannelHandler implements Runnable {
  private SocketChannel socketChannel;
  private AccountService accountService;

  public ChannelHandler(SocketChannel socketChannel, AccountService accountService) {
    this.socketChannel = socketChannel;
    this.accountService = accountService;
  }

  @Override
  public void run() {
    ByteBuffer buffer = ByteBuffer.allocate(16);
    boolean read = false;
    try {
      Selector selector = Selector.open();
      socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
      while (true) {
        selector.select();
        Set<SelectionKey> keys = selector.selectedKeys();
        Iterator<SelectionKey> it = keys.iterator();
        while (it.hasNext()) {
          SelectionKey key = it.next();
          if (key.isReadable() && !read) {
            if (socketChannel.read(buffer) > 0) {
              read = true;
              readBuffer(buffer);
            }
          }
          if (key.isWritable() && read) {
            read = false;
            socketChannel.write(buffer);
            System.out.println(String.format("Write buffer %s from %s", buffer.toString(), Thread.currentThread().getName()));
            buffer.clear();
          }
        }
        keys.clear();
      }
    } catch (Exception e) {
      System.out.println(String.format("Error: %s\nFrom: %s", e.getMessage(), Thread.currentThread().getName()));
      e.printStackTrace();
      throw new RuntimeException(e);
    } finally {
      try {
        socketChannel.close();
      } catch (IOException e) {
        System.out.println("Channel not closed");
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }
  }

  private void readBuffer(ByteBuffer buffer) {
    buffer.flip();
    if (buffer.limit() == 4) {
      int id = buffer.getInt();
      System.out.println(String.format("Calling getAmount(%s) by %s", id, Thread.currentThread().getName()));
      long value = accountService.getAmount(id);
      System.out.println(String.format("Getting value=%s from %s", value, Thread.currentThread().getName()));
      buffer.clear();
      buffer.putLong(value);
    } else if (buffer.limit() == 12) {
      int id = buffer.getInt();
      long value = buffer.getLong();
      System.out.println(String.format("Calling addAmount(%s, %s) by %s", id, value, Thread.currentThread().getName()));
      accountService.addAmount(id, value);
      buffer.clear();
      buffer.putChar('t'); //true
    } else {
      buffer.clear();
      buffer.putChar('f'); //false
    }
  }
}
