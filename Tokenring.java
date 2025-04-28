//Site.java 2 terminals
import java.io.*;
import java.net.*;
import java.util.Scanner;
public class Site {
    private static boolean hasToken;
    private static String nextSiteIP;
    private static int listenPort;
    private static int sendPort;
    public Site(boolean hasToken, String nextSiteIP, int listenPort, int sendPort) {
        Site.hasToken = hasToken;
        Site.nextSiteIP = nextSiteIP;
        Site.listenPort = listenPort;
        Site.sendPort = sendPort;
    }
    public void start() {
        // Start a thread to listen for incoming token
        new Thread(() -> listenForToken()).start();
        // If this site has token initially, send it manually
        if (hasToken) {
            try {
                Thread.sleep(2000); // small delay to allow other site to start
                enterCriticalSection();
                passToken();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void listenForToken() {
        try (ServerSocket serverSocket = new ServerSocket(listenPort)) {
            while (true) {
                Socket socket = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message = reader.readLine();
                if ("TOKEN".equals(message)) {
                    hasToken = true;
                    enterCriticalSection();
                    passToken();
                }
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void passToken() {
        try (Socket socket = new Socket(nextSiteIP, sendPort)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("TOKEN\n");
            writer.flush();
            hasToken = false;
            System.out.println("Token passed to " + nextSiteIP + ":" + sendPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void enterCriticalSection() {
        System.out.println("\n==== Entering Critical Section ====");
        try {
            Thread.sleep(3000); // Simulate doing some work in CS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("==== Exiting Critical Section ====\n");
    }
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: java Site <hasToken:true/false> <nextSiteIP> <listenPort> <sendPort>");
            return;
        }
        boolean hasToken = Boolean.parseBoolean(args[0]);
        String nextSiteIP = args[1];
        int listenPort = Integer.parseInt(args[2]);
        int sendPort = Integer.parseInt(args[3]);

        Site site = new Site(hasToken, nextSiteIP, listenPort, sendPort);
        site.start();
    }
}

////Site.java 5 terminals
import java.io.*;
import java.net.*;
import java.util.Scanner;
public class Site {
    private static boolean hasToken;
    private static String nextSiteIP;
    private static int listenPort;
    private static int sendPort;
    public Site(boolean hasToken, String nextSiteIP, int listenPort, int sendPort) {
        Site.hasToken = hasToken;
        Site.nextSiteIP = nextSiteIP;
        Site.listenPort = listenPort;
        Site.sendPort = sendPort;
    }
    public void start() {
        // Start a thread to listen for incoming token
        new Thread(() -> listenForToken()).start();
        // If this site has token initially, send it manually
        if (hasToken) {
            try {
                Thread.sleep(2000); // small delay to allow other site to start
                enterCriticalSection();
                passToken();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void listenForToken() {
        try (ServerSocket serverSocket = new ServerSocket(listenPort)) {
            while (true) {
                Socket socket = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message = reader.readLine();
                if ("TOKEN".equals(message)) {
                    hasToken = true;
                    enterCriticalSection();
                    passToken();
                }
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void passToken() {
        try (Socket socket = new Socket(nextSiteIP, sendPort)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("TOKEN\n");
            writer.flush();
            hasToken = false;
            System.out.println("Token passed to " + nextSiteIP + ":" + sendPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void enterCriticalSection() {
        System.out.println("\n==== Entering Critical Section ====");
        try {
            Thread.sleep(3000); // Simulate doing some work in CS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("==== Exiting Critical Section ====\n");
    }
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: java Site <hasToken:true/false> <nextSiteIP> <listenPort> <sendPort>");
            return;
        }
        boolean hasToken = Boolean.parseBoolean(args[0]);
        String nextSiteIP = args[1];
        int listenPort = Integer.parseInt(args[2]);
        int sendPort = Integer.parseInt(args[3]);

        Site site = new Site(hasToken, nextSiteIP, listenPort, sendPort);
        site.start();
    }
}

//Commands
//1st terminal
//        javac Site.java
//        java Site true localhost 5000 5001
//
//        2nd terminal
//        java Site false localhost 5001 5000

//TestClass
package com.pranav.Mail_Sender;
import java.util.Scanner;

class TokenRing {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the number of processes in the ring: ");
        int n = sc.nextInt();
        int token = 0;
        System.out.println("Initializing ring...");
        for (int i = 0; i < n; i++) {
            System.out.print(" " + i);
        }
        System.out.println();
        while (true) {
            System.out.print("\nDo you want to send a message? (yes/no): ");
            String choice = sc.next().toLowerCase();
            if (!choice.equals("yes")) {
                System.out.println("Exiting token ring simulation.");
                break;
            }
            System.out.print("Enter sender process ID (0 to " + (n - 1) + "): ");
            int sender = sc.nextInt();
            System.out.print("Enter receiver process ID (0 to " + (n - 1) + "): ");
            int receiver = sc.nextInt();
            sc.nextLine(); // clear buffer
            System.out.print("Enter the message to be sent: ");
            String message = sc.nextLine()
            System.out.println("\nToken circulation started...");
            // Circulate token to sender
            for (int i = token; i != sender; i = (i + 1) % n) {
                System.out.print(i + " -> ");
            }
            System.out.println(sender);
            // Sender sends the message
            System.out.println(sender + " sending message: '" + message + "'");
            // Forward message to receiver
            for (int i = (sender + 1) % n; i != receiver; i = (i + 1) % n) {
                System.out.println("Message '" + message + "' forwarded by process " + i);
            }
            // Receiver receives the message
            System.out.println("Receiver " + receiver + " received the message: '" + message + "'");
            // Update token holder
            token = sender;
        }
        sc.close();
    }
}

