import com.sun.management.OperatingSystemMXBean;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Bogdan on 21-May-17.
 */
public class ServerMain {
    private static final int CHUNK_SIZE = 5;

    private static Logger logger = Logger.getLogger(ServerMain.class);

    private static OperatingSystemMXBean operatingSystemMXBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    public static void main(String[] args) throws IOException {
        double avgCpuUsage = 0;
        long avgRamUsage = 0;
        long ramUsage = 0;
        double cpuUsage;
        int port = 9874;
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            int fileSize = in.readInt();
            int noOfPackages = fileSize % CHUNK_SIZE == 0 ? fileSize/CHUNK_SIZE : fileSize/CHUNK_SIZE + 1;
            byte[] bytesReceived = new byte[CHUNK_SIZE];
            for(int i = 0; i < noOfPackages; i++) {
                cpuUsage = operatingSystemMXBean.getProcessCpuLoad();
                avgCpuUsage += cpuUsage;
                ramUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                avgRamUsage += ramUsage;
                int bytesToUseThisChunk = i == noOfPackages - 1 ? fileSize % CHUNK_SIZE : CHUNK_SIZE;
                in.read(bytesReceived, 0, bytesToUseThisChunk);
                out.write(bytesReceived, 0, bytesToUseThisChunk);
            }
            out.close();
            in.close();
            clientSocket.close();

            logger.info("-----------------------------------------");
            logger.info("UPLOAD FILE: This iteration used as avg of " + avgCpuUsage/noOfPackages * 100 +  "% of CPU");
            logger.info("UPLOAD FILE: This iteration used an avg of RAM usage of " + avgRamUsage/noOfPackages);
            logger.info("-----------------------------------------");
        }
    }
}
