package xlink.agent.shell;


import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ShellClient {


  public static void main(String[] args) throws Exception {
    Socket socket = new Socket("127.0.0.1", 3099);
    // Console console = System.console();
    Scanner scanner = new Scanner(System.in);
    try (BufferedInputStream bin = new BufferedInputStream(socket.getInputStream());
        OutputStream os = socket.getOutputStream();) {
    System.out.println("Connected to xagent....");
    while (true) {
        String line = scanner.nextLine();
        if ("".equals(line)) {
          System.out.print("> ");
        } else {
          os.write(line.getBytes());
          os.flush();
          byte[] receviceDate = new byte[1000];
          int size = bin.read(receviceDate);
          String output = new String(receviceDate, 0, size);
          System.out.println(output);
          System.out.print("> ");
        }
      }
    }
  }
}
