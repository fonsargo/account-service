package ru.spbsu.apmath.accountservice.server;

import ru.spbsu.apmath.accountservice.service.AccountService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 12.09.2014
 * Time: 17:30
 */
public class Server implements Runnable {
  private int port;
  private ExecutorService executorService;
  private AccountService accountService;

  public Server(int port, AccountService accountService) {
    this.port = port;
    this.executorService = Executors.newCachedThreadPool();
    this.accountService = accountService;
  }

  public void run() {
    try {
      ServerSocketChannel ssc = ServerSocketChannel.open();
      ssc.configureBlocking(false);
      ServerSocket ss = ssc.socket();
      InetSocketAddress isa = new InetSocketAddress(port);
      ss.bind(isa);
      Selector selector = Selector.open();
      ssc.register(selector, SelectionKey.OP_ACCEPT);
      System.out.println("Listening on port: " + port);
      while (true) {
        selector.select();
        Set keys = selector.selectedKeys();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
          SelectionKey key = (SelectionKey)it.next();
          if (key.isAcceptable()) {
            SocketChannel socketChannel = ssc.accept();
            System.out.println("Accept connection from: " + socketChannel.socket());
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
          } else if (key.isReadable()) {
            SocketChannel sc = (SocketChannel)key.channel();
            System.out.println(String.format("Get readable channel %s and add it to the executor service.", key.channel()));
            executorService.submit(new ChannelHandler(sc, accountService));
            key.cancel();
          }
        }
        keys.clear();
      }
    } catch(IOException ie ) {
      System.out.println(ie);
    }
  }
}
