package ru.spbsu.apmath.accountservice.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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

  private int clientPort;
  private String host;
  private int port;
  private ServiceReader serviceReader;

  public Client(int clientPort, String host, int port, ServiceReader serviceReader) {
    this.clientPort = clientPort;
    this.host = host;
    this.port = port;
    this.serviceReader = serviceReader;
    new Thread(this).start();
  }

  @Override
  public void run() {
    try {
      System.out.println(String.format("[%s] Opening socket...", Thread.currentThread().getName()));
      SocketChannel socketChannel = SocketChannel.open();
      Selector selector = Selector.open();
      try {
        socketChannel.configureBlocking(false);
        socketChannel.socket().bind(new InetSocketAddress(clientPort));
        System.out.println(String.format("[%s] Opened!", Thread.currentThread().getName()));
        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT);
        boolean written = false;
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        while(true) {
          selector.select();
          Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
          while (iterator.hasNext()) {
            SelectionKey selectionKey = iterator.next();
            iterator.remove();
            socketChannel = (SocketChannel) selectionKey.channel();
            if (selectionKey.isConnectable() && !socketChannel.isConnected()) {
              System.out.println(String.format("[%s] Trying to connect...", Thread.currentThread().getName()));
              boolean success = socketChannel.connect(new InetSocketAddress(host, port));
              if (!success) {
                socketChannel.finishConnect();
                System.out.println(String.format("[%s]Failed!", Thread.currentThread().getName()));
              } else {
                System.out.println(String.format("[%s] Success", Thread.currentThread().getName()));
              }
            }
            if (selectionKey.isWritable() && !written) {
              System.out.println(String.format("[%s] Writing data...", Thread.currentThread().getName()));
              byteBuffer = serviceReader.prepareBufferToWrite(byteBuffer);
              socketChannel.write(byteBuffer);
              byteBuffer.clear();
              written = true;
            }
            if (selectionKey.isReadable() && written) {
              System.out.println(String.format("[%s] Reading data...", Thread.currentThread().getName()));
              if (socketChannel.read(byteBuffer) > 0) {
                written = false;
                serviceReader.readBuffer(byteBuffer);
              }
            }
          }
        }
      } finally {
        socketChannel.close();
        selector.close();
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
