package Client;


import java.net.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class UDPClient {
    public static void main(String[] args) {
        byte[] receiveBuf = new byte[10000];
        byte[] sendBuf = new byte[1];
        ConcurrentHashMap<Integer, FallingInRiver> falls = null;

        Integer port = null;
        InetAddress address = null;
        DatagramSocket socket = null;

        Scanner in = new Scanner(System.in);
        for (; ; ) {
            System.out.println("Введите порт:");
            try {
                port = Integer.parseInt(in.nextLine());
                address = InetAddress.getByName("127.0.0.1");
                socket = new DatagramSocket();
                socket.setSoTimeout(5000);
                break;
            } catch (Exception e) {
                System.out.println("Ошибка соединения.");
            }
        }
        try {
            DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address, port);
            socket.send(sendPacket);
            System.out.println("Package sent");
            DatagramPacket receivePacket = new DatagramPacket(receiveBuf, receiveBuf.length);
            socket.receive(receivePacket);
            System.out.println("Package received");
        } catch (SocketTimeoutException e) {
            System.out.println("Socket timed out");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            falls = deserializeMap(receiveBuf);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Tale story = new Tale(falls);
        StoryTeller granddad = new StoryTeller(story.getTaleName());
        granddad.tellTale();
        story.tellTale();
    }

    static public ConcurrentHashMap<Integer, FallingInRiver> deserializeMap(byte[] buffer) throws Exception {
        int i = buffer.length - 1;
        while (i >= 0 && buffer[i] == 0)
        {
            --i;
        }
        byte[] byteArray = Arrays.copyOf(buffer, i + 1);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(byteArray));
        ConcurrentHashMap<Integer, FallingInRiver> map = (ConcurrentHashMap<Integer, FallingInRiver>) ois.readObject();
        return map;
    }
}
