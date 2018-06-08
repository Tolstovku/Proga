package Client;

import Common.FallingInRiver;
import Server.Commands;

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

public class CommandSender {
    private InetAddress address;
    private int port;
    private DatagramSocket socket;

    public CommandSender(InetAddress address, int port, DatagramSocket socket) {
        this.address = address;
        this.port = port;
        this.socket = socket;
    }


    public ConcurrentHashMap<Integer, FallingInRiver> getMap() throws IOException {
        ConcurrentHashMap<Integer, FallingInRiver> falls = null;
        String command = "start";
        byte[] sendBuf = command.getBytes();
        byte[] receiveBuf = new byte[10000000];
        DatagramPacket receivePacket = null;

        //Отправляем команду на сервер
            receivePacket = sendCommand(sendBuf, receiveBuf);

        //Восстанавливаем мапу
    try {
        falls = deserializeMap(receivePacket.getData());
    }
    catch (ClassNotFoundException e){e.printStackTrace();}
    catch (NullPointerException e){}
        return falls;
    }

    public void serverShutdown() {
        String command = "Server shutdown";
        byte[] sendBuf = command.getBytes();
        byte[] receiveBuf = new byte[1];
        try {
            sendCommand(sendBuf, receiveBuf);
        } catch (SocketTimeoutException e) {}
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Сервер остановлен");
    }

    private ConcurrentHashMap<Integer, FallingInRiver> deserializeMap(byte[] buffer) throws  IOException, ClassNotFoundException {
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


    private DatagramPacket sendCommand(byte[] sendBuf, byte[] receiveBuf) throws IOException{

            DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address, port);
            socket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receiveBuf, receiveBuf.length);
            socket.receive(receivePacket);
            return receivePacket;

    }
}
