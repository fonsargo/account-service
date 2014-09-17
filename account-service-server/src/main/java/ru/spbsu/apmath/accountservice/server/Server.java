package ru.spbsu.apmath.accountservice.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 12.09.2014
 * Time: 17:30
 */
public class Server implements Runnable {
  private int port;
  private ExecutorService executorService;
  private BufferHandler bufferHandler;

  public Server(int port, BufferHandler bufferHandler) {
    this.port = port;
    this.executorService = Executors.newCachedThreadPool();
    this.bufferHandler = bufferHandler;
  }

  public void run() {
    try {
      System.out.println("Opening socket...");
      ServerSocketChannel ssc = ServerSocketChannel.open();
      Selector selector = Selector.open();
      try {
        ssc.configureBlocking(false);
        ServerSocket ss = ssc.socket();
        InetSocketAddress isa = new InetSocketAddress(port);
        ss.bind(isa);
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Listening on port: " + port);
        while (!Thread.currentThread().isInterrupted()) {
          int num = selector.select(1000);
          if (num == 0)
            continue;
          Set<SelectionKey> keys = selector.selectedKeys();
          Iterator<SelectionKey> it = keys.iterator();
          while (it.hasNext()) {
            SelectionKey key = it.next();
            if (key.isAcceptable()) {
              SocketChannel socketChannel = ssc.accept();
              System.out.println("Accept connection from: " + socketChannel.socket());
              socketChannel.configureBlocking(false);
              socketChannel.register(selector, SelectionKey.OP_READ);
            } else if (key.isReadable()) {
              SocketChannel sc = (SocketChannel) key.channel();
              System.out.println(String.format("Get readable channel %s and add it to the executor service.", key.channel()));
              executorService.submit(new ChannelHandler(sc, bufferHandler));
              key.cancel();
            }
          }
          keys.clear();
        }
        System.out.println("Stopping executor service...");
        executorService.shutdownNow();
      } finally {
        ssc.close();
        selector.close();
      }
    } catch (IOException ie) {
      System.out.println(ie.getMessage());
      throw new RuntimeException(ie);
    }
  }
}
