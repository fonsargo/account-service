package ru.spbsu.apmath.accountservice;

import ru.spbsu.apmath.accountservice.server.Server;
import ru.spbsu.apmath.accountservice.server.impl.BufferHandlerImpl;
import ru.spbsu.apmath.accountservice.service.DataBasePool;
import ru.spbsu.apmath.accountservice.service.impl.AccountServiceImpl;
import ru.spbsu.apmath.accountservice.service.impl.DBPool;


public class App {

  private static final String USAGE = "Usage: java account-service-server [port]";

  public static void main(String[] args) {
    try {
      int port = Integer.parseInt(args[0]);
      System.out.println("Starting server...");
      DataBasePool dataBasePool = new DBPool("jdbc:postgresql://185.4.74.215/accounts", "test_user", "qwerty");
      AccountServiceImpl accountService = new AccountServiceImpl(dataBasePool);
      new Thread(new Server(port, new BufferHandlerImpl(accountService))).start();
      int get = 0;
      int add = 0;
      while(true) {
        Thread.sleep(10000);
        int tmpGet = accountService.getAddRequests();
        int tmpAdd = accountService.getGetRequests();
        System.out.println(String.format("COUNT OF REQUESTS PER 10 SECOND: add=%s (%s), get=%s (%s)",
                tmpAdd - add, tmpAdd, tmpGet - get, tmpGet));
        get = tmpGet;
        add = tmpAdd;
      }
    } catch (IndexOutOfBoundsException ibe) {
      System.out.println(USAGE);
    } catch (NumberFormatException nfe) {
      System.out.println(USAGE);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}
