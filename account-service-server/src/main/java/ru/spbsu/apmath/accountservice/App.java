package ru.spbsu.apmath.accountservice;

import ru.spbsu.apmath.accountservice.server.Server;
import ru.spbsu.apmath.accountservice.server.impl.BufferHandlerImpl;
import ru.spbsu.apmath.accountservice.service.DataBasePool;
import ru.spbsu.apmath.accountservice.service.impl.AccountServiceImpl;
import ru.spbsu.apmath.accountservice.service.impl.CachedDBPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public class App {

  private static final String USAGE =
          "Usage: java account-service-server [port] [db host] [database] [user] [password] [(optional)manage port]";
  private static final int DEFAULT_MANAGE_PORT = 12888;

  private static int getRequests = 0;
  private static int addRequests = 0;

  public static void main(String[] args) {
    try {
      int port = Integer.parseInt(args[0]);
      System.out.println("Starting server...");
      DataBasePool dataBasePool = new CachedDBPool(args[1], args[2], args[3], args[4]);
      int managePort = DEFAULT_MANAGE_PORT;
      if (args.length > 5) {
        managePort = Integer.parseInt(args[5]);
      }
      Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
      AccountServiceImpl accountService = new AccountServiceImpl(dataBasePool);
      Thread server = new Thread(new Server(port, new BufferHandlerImpl(accountService)));
      server.start();
      startManageServer(managePort, accountService, server);
    } catch (IndexOutOfBoundsException ibe) {
      System.out.println(USAGE);
    } catch (NumberFormatException nfe) {
      System.out.println(USAGE);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

  private static void startManageServer(int managePort, AccountServiceImpl accountService, Thread server) {
    try (ServerSocketChannel ssc = ServerSocketChannel.open();
         Selector selector = Selector.open()) {
      ssc.configureBlocking( false );
      ServerSocket ss = ssc.socket();
      InetSocketAddress isa = new InetSocketAddress(managePort);
      ss.bind(isa);
      ssc.register( selector, SelectionKey.OP_ACCEPT );
      System.out.println("Listening on manage port: " + managePort);
      long now = System.currentTimeMillis();
      String message = "Not init yet";
      while (server.isAlive()) {
        long tmp = System.currentTimeMillis();
        if (tmp - now > 10000) {
          message = getStats(accountService);
          System.out.println(message);
          now = tmp;
        }
        int num = selector.select(1000);
        if (num == 0) {
          continue;
        }
        Set<SelectionKey> keys = selector.selectedKeys();
        Iterator<SelectionKey> it = keys.iterator();
        while (it.hasNext()) {
          SelectionKey key = it.next();
          if (key.isAcceptable()) {
            SocketChannel sc = ssc.accept();
            System.out.println("Accept manage connection from:" + sc);
            sc.configureBlocking( false );
            sc.register(selector, SelectionKey.OP_READ);
          } else if (key.isReadable()) {
            SocketChannel sc = (SocketChannel)key.channel();
            processInput(sc, message, server, accountService);
          }
        }
        keys.clear();
      }
    } catch(IOException ie) {
      throw new RuntimeException(ie);
    } finally {
      System.out.println("Stopping server...");
      server.interrupt();
    }
  }

  private static String getStats(AccountServiceImpl accountService) {
    String message;
    int tmpGet = accountService.getGetRequests();
    int tmpAdd = accountService.getAddRequests();
    int speedGet = tmpGet - getRequests;
    int speedAdd = tmpAdd - addRequests;
    getRequests = tmpGet;
    addRequests = tmpAdd;
    message = String.format(
            "Requests per 10 sec: addAmount:%s (total:%s), getAmount:%s (total:%s)\n",
            speedAdd, tmpAdd, speedGet, tmpGet);
    return message;
  }

  private static void processInput(SocketChannel sc, String message, Thread server, AccountServiceImpl service)
          throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(256);
    sc.read(buffer);
    buffer.flip();
    if (buffer.limit()==0) {
      return;
    } else {
      byte[] bytes = new byte[buffer.limit()];
      for (int i = 0; i < buffer.limit(); i++)
        bytes[i] = buffer.get(i);
      String command = new String(bytes);
      buffer.clear();
      System.out.println("Manage command:" + command + "<");
      if (command.toLowerCase().startsWith("stop")) {
        buffer.put("Stopped\n".getBytes());
        System.out.println("Stopping server...");
        server.interrupt();
      } else if (command.toLowerCase().startsWith("stats")) {
        buffer.put(message.getBytes());
      } else if (command.toLowerCase().startsWith("reset")) {
        buffer.put("Reseted\n".getBytes());
        service.reset();
        addRequests = 0;
        getRequests = 0;
      } else {
        buffer.put("Unknown command. Use: stop | stats | reset\n".getBytes());
      }
    }
    buffer.flip();
    sc.write( buffer );
  }
}
