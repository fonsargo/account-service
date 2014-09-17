package ru.spbsu.apmath.accountservice;

import ru.spbsu.apmath.accountservice.client.Client;
import ru.spbsu.apmath.accountservice.client.impl.AddAmountReader;
import ru.spbsu.apmath.accountservice.client.impl.GetAmountReader;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ClientApp {

  private static final String USAGE =
          "Usage: java account-service-client [host] [port] [rCount] [wCount] " +
                  "[idList(separated by ',') or range (separated by ':')] [(optional)manage port]";
  private static final int DEFAULT_MANAGE_PORT = 12888;


  public static void main(String[] args) {
    try {
      String host = args[0];
      int port = Integer.parseInt(args[1]);
      int rCount = Integer.parseInt(args[2]);
      int wCount = Integer.parseInt(args[3]);
      List<Integer> idList = getIntegers(args[4]);
      int managePort = DEFAULT_MANAGE_PORT;
      if (args.length > 5) {
        managePort = Integer.parseInt(args[5]);
      }
      System.out.println("Starting clients...");
      List<Thread> threads = new ArrayList<>();
      startThreads(new Client(host, port, new AddAmountReader(idList)), wCount, threads);
      startThreads(new Client(host, port, new GetAmountReader(idList)), rCount, threads);
      System.out.println("All clients were started!");
      System.out.println("Trying to connect to the manage port...");
      startManageClient(host, managePort, threads);
    } catch (IndexOutOfBoundsException ibe) {
      System.out.println(USAGE);
    } catch (NumberFormatException nfe) {
      System.out.println(USAGE);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

  }

  private static void startManageClient(String host, int managePort, List<Thread> threads) throws IOException, InterruptedException {
    try (Socket s = new Socket(host, managePort);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
      Thread.sleep(2000);
      Scanner scanner = new Scanner(System.in);
      System.out.println("Use: stop | stats | reset");
      while (true) {
        String command = scanner.next();
        bufferedWriter.write(command);
        bufferedWriter.flush();
        String response = bufferedReader.readLine();
        System.out.println(response);
        if (response.startsWith("Stopped")) {
          break;
        }
      }
    } finally {
      System.out.println("Closing threads...");
      for (Thread thread : threads)
        thread.interrupt();
      System.out.println("Closed!");
    }
  }

  private static List<Integer> getIntegers(String arg) {
    List<Integer> idList = new ArrayList<Integer>();
    if (arg.split(":").length == 2) {
      int from = Integer.parseInt(arg.split(":")[0]);
      int to = Integer.parseInt(arg.split(":")[1]);
      if (from > to) {
        throw new IllegalArgumentException("Illegal range: " + arg);
      }
      for (int i = from; i <= to; i++)
        idList.add(i);
    } else {
      for (String s : arg.split(","))
        idList.add(Integer.parseInt(s));
    }
    return idList;
  }

  private static void startThreads(Client client, int count, List<Thread> threads) {
    for (int i = 0; i < count; i++) {
      Thread thread = new Thread(client);
      thread.start();
      threads.add(thread);
    }
  }
}
