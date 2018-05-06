package Server;

import Client.FallingInRiver;

import java.io.*;
import java.net.*;
import java.security.spec.ECField;
import java.util.concurrent.ConcurrentHashMap;

 public class UDPServer extends Thread {
    private DatagramSocket serverSocket;
    private byte[] buf = new byte[64*1024];
    private byte[] recieveBuf = new byte[1];
    private boolean running;
    private ConcurrentHashMap<Integer, FallingInRiver> map;
    private Integer port;

    public UDPServer(Integer port, ConcurrentHashMap<Integer, FallingInRiver> map) throws SocketException {
        this.map = map;
        this.port = port;
    }

    public void run() {
        running = true;

        try {
            serverSocket = new DatagramSocket(port);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        while (running) {
            DatagramPacket receivePacket = new DatagramPacket(recieveBuf, recieveBuf.length);
            try {
                serverSocket.receive(receivePacket);
                System.out.println("Package received");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            new Thread(() -> {
                try {
                    InetAddress IPAddress = receivePacket.getAddress();
                    Integer clientPort = receivePacket.getPort();
                    buf = serializeMap();
                    DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, IPAddress, clientPort);
                    serverSocket.send(sendPacket);
                    System.out.println("Package sent");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        serverSocket.close();
    }

    private byte[] serializeMap() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(map);
        out.flush();
        out.close();
        return bos.toByteArray();
    }
}
