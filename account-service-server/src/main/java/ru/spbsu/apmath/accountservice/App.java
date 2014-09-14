package ru.spbsu.apmath.accountservice;

import ru.spbsu.apmath.accountservice.server.Server;
import ru.spbsu.apmath.accountservice.service.impl.AccountServiceImpl;

/**
 * Hello world!
 *
 */
public class App  {

  private static final String USAGE = "Usage: java account-service-server [port]";

  public static void main( String[] args ) {
    try {
      int port = Integer.parseInt(args[0]);
      System.out.println("Starting server...");
      new Server(port, new AccountServiceImpl());
    } catch (IndexOutOfBoundsException ibe) {
      System.out.println(USAGE);
    } catch (NumberFormatException nfe) {
      System.out.println(USAGE);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
