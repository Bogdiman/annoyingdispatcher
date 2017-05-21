import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Bogdan on 21-May-17.
 */
public class ServerMain {


    public static void main(String[] args) throws IOException {
        int port = 9874;
        try (
            ServerSocket serverSocket = new ServerSocket(port);
        ) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                int fileSize = in.readInt();
                int noOfPackages = fileSize % 5 == 0 ? fileSize/5 : fileSize/5 + 1;
                byte[] bytesReceived = new byte[5];
                for(int i = 0; i < noOfPackages; i++) {
                    int bytesToUseThisChunk = i == noOfPackages - 1 ? fileSize % 5 : 5;
                    System.out.print(bytesToUseThisChunk);
                    in.read(bytesReceived, 0, bytesToUseThisChunk);
                    System.out.print(new String(bytesReceived));
                    out.write(bytesReceived, 0, bytesToUseThisChunk);
                }
                out.close();
                in.close();
                clientSocket.close();
            }
        }
    }
}
