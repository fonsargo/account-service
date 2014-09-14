package ru.spbsu.apmath.accountservice.client;

import ru.spbsu.apmath.accountservice.client.servicereaderimpl.AddAmountReader;
import ru.spbsu.apmath.accountservice.client.servicereaderimpl.GetAmountReader;

import java.util.ArrayList;
import java.util.List;


public class App {

  private static final String USAGE =
          "Usage: java account-service-client [client-port] [host] [port] [rCount] [wCount] [idList(separated by ',')]";


  public static void main( String[] args ) {
    try {
      int clientPort = Integer.parseInt(args[0]);
      String host = args[1];
      int port = Integer.parseInt(args[2]);
      int rCount = Integer.parseInt(args[3]);
      int wCount = Integer.parseInt(args[4]);
      List<Integer> idList = new ArrayList<Integer>();
      for (String s : args[5].split(","))
        idList.add(Integer.parseInt(s));
      System.out.println("Starting clients...");
      for (int i = 0; i < wCount; i++) {
        new Client(clientPort, host, port, new AddAmountReader(idList));
      }
      for (int i = 0; i < rCount; i++) {
        new Client(clientPort, host, port, new GetAmountReader(idList));
      }
      System.out.println("All clients were started!");
    } catch (IndexOutOfBoundsException ibe) {
      System.out.println(USAGE);
    } catch (NumberFormatException nfe) {
      System.out.println(USAGE);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

  }
}
