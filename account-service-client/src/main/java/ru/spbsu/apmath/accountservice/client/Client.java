package ru.spbsu.apmath.accountservice.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 14.09.2014
 * Time: 16:15
 */
public class Client implements Runnable {

  private String host;
  private int port;
  private BufferHandler bufferHandler;

  public Client(String host, int port, BufferHandler bufferHandler) {
    this.host = host;
    this.port = port;
    this.bufferHandler = bufferHandler;
  }

  @Override
  public void run() {
    try {
      System.out.println(String.format("[%s] Opening socket...", Thread.currentThread().getName()));
      SocketChannel socketChannel = SocketChannel.open();
      Selector selector = Selector.open();
      try {
        socketChannel.configureBlocking(false);
        socketChannel.socket().bind(new InetSocketAddress(0));
        System.out.println(String.format("[%s] Opened!", Thread.currentThread().getName()));
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(new InetSocketAddress(host, port));
        boolean written = false;
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        while (!Thread.currentThread().isInterrupted()) {
          int num = selector.select(1000);
          if (num == 0){
            continue;
          }
          Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
          while (iterator.hasNext()) {
            SelectionKey selectionKey = iterator.next();
            iterator.remove();
            socketChannel = (SocketChannel) selectionKey.channel();
            if (selectionKey.isConnectable()) {
              socketChannel.finishConnect();
              if (socketChannel.isConnected()) {
                socketChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
              }
            }
            if (selectionKey.isWritable() && !written) {
              byteBuffer = bufferHandler.prepareToWrite(byteBuffer);
              socketChannel.write(byteBuffer);
              byteBuffer.clear();
              written = true;
            }
            if (selectionKey.isReadable() && written) {
              if (socketChannel.read(byteBuffer) > 0) {
                written = false;
                byteBuffer = bufferHandler.readBuffer(byteBuffer);
              }
            }
          }
        }
      } finally {
        socketChannel.close();
        selector.close();
      }
    } catch (ClosedByInterruptException ce) {
      //ignore
    } catch (IOException e) {
      System.out.println(e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
