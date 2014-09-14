package ru.spbsu.apmath.accountservice.client;

import ru.spbsu.apmath.accountservice.client.servicereaderimpl.AddAmountReader;
import ru.spbsu.apmath.accountservice.client.servicereaderimpl.GetAmountReader;

import java.util.ArrayList;
import java.util.List;


public class App {

  private static final String USAGE =
          "Usage: java account-service-client [host] [port] [rCount] [wCount] [idList(separated by ',')]";


  public static void main( String[] args ) {
    try {
      String host = args[0];
      int port = Integer.parseInt(args[1]);
      int rCount = Integer.parseInt(args[2]);
      int wCount = Integer.parseInt(args[3]);
      List<Integer> idList = new ArrayList<Integer>();
      for (String s : args[4].split(","))
        idList.add(Integer.parseInt(s));
      System.out.println("Starting clients...");
      for (int i = 0; i < wCount; i++) {
        new Client(host, port, new AddAmountReader(idList));
      }
      for (int i = 0; i < rCount; i++) {
        new Client(host, port, new GetAmountReader(idList));
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
