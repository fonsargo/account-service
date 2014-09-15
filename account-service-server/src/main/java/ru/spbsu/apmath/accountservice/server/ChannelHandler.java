package ru.spbsu.apmath.accountservice.server;

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
  private BufferHandler bufferHandler;

  private static final long TIMEOUT = 10000;

  public ChannelHandler(SocketChannel socketChannel, BufferHandler bufferHandler) {
    this.socketChannel = socketChannel;
    this.bufferHandler = bufferHandler;
  }

  @Override
  public void run() {
    ByteBuffer buffer = ByteBuffer.allocate(16);
    boolean read = false;
    try {
      Selector selector = Selector.open();
      socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
      System.out.println(String.format("[%s] Opened!", Thread.currentThread().getName()));
      long now = System.currentTimeMillis();
      while (true) {
        selector.select();
        Set<SelectionKey> keys = selector.selectedKeys();
        Iterator<SelectionKey> it = keys.iterator();
        while (it.hasNext()) {
          SelectionKey key = it.next();
          if (key.isReadable() && !read) {
            if (socketChannel.read(buffer) > 0) {
              read = true;
              now = System.currentTimeMillis();
              buffer = bufferHandler.readBuffer(buffer);
            } else {
              if (System.currentTimeMillis() - now > TIMEOUT) {
                throw new RuntimeException("Connection time out");
              }
            }
          }
          if (key.isWritable() && read) {
            read = false;
            socketChannel.write(bufferHandler.prepareToWrite(buffer));
            System.out.println(String.format("[%s] Write buffer: %s", Thread.currentThread().getName(), buffer));
            buffer.clear();
          }
        }
        keys.clear();
      }
    } catch (Exception e) {
      System.out.println(String.format("[%s] Error: %s", Thread.currentThread().getName(), e.getMessage()));
      throw new RuntimeException(e);
    } finally {
      System.out.println(String.format("[%s] Closing channel...", Thread.currentThread().getName()));
      try {
        socketChannel.close();
      } catch (IOException e) {
        System.out.println(String.format("[%s] Channel not closed!", Thread.currentThread().getName()));
        throw new RuntimeException(e);
      }
    }
  }
}
