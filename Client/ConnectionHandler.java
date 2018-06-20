package Client;

import Common.FallingInRiver;
import Server.Commands;
import Server.SingletonCollection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionHandler {
    private static InetAddress address;
    private static int port;
    private static DatagramSocket socket;
    private static byte[] sendBuf;
    private static byte[] receiveBuf = new byte[10000000];

    public static void configure(InetAddress addrAss, int por, DatagramSocket socke) {
        address = addrAss;
        port = por;
        socket = socke;
    }


    public static void getMap() {
        try {
            sendCommand("start");
        }
        catch (Exception e) {e.printStackTrace();}
    }

    public static void serverShutdown() {
        try {
            sendCommand("Server shutdown");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Сервер остановлен");
    }

    private  ConcurrentHashMap<Integer, FallingInRiver> deserializeMap(byte[] buffer) throws  IOException, ClassNotFoundException {
        //Удаление null-значений из массива байтов
        int i = buffer.length - 1;
        while (i >= 0 && buffer[i] == 0) {
            --i;
        }
        byte[] trimmedBuf = Arrays.copyOf(buffer, i + 1);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(trimmedBuf));
        ConcurrentHashMap<Integer, FallingInRiver> map = (ConcurrentHashMap<Integer, FallingInRiver>) ois.readObject();
        return map;
    }


    private static void sendCommand(String command) throws IOException{
            sendBuf=command.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address, port);
            if (socket==null) System.out.println("Excuse me what the fuck");
            socket.send(sendPacket);
    }


    public static void handlePacket(DatagramPacket receivedPacket){
        byte[] buffer = receivedPacket.getData();
        Object object;
        int i = buffer.length - 1;
        while (i >= 0 && buffer[i] == 0) {
            --i;
        }
        byte[] trimmedBuf = Arrays.copyOf(buffer, i + 1);
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(trimmedBuf));
            object = ois.readObject();
            if (object instanceof ConcurrentHashMap){
                SingletonGUI.updateGUICollection((ConcurrentHashMap<Integer, FallingInRiver>) object);
            }
            else
            SingletonGUI.showMessage((String) object);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
