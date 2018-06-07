package Client;


import Common.FallingInRiver;
import Server.Commands;
import sun.reflect.annotation.EnumConstantNotPresentExceptionProxy;
import javax.swing.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class UDPClient {
    Integer port = null;
    InetAddress address = null;
    private DatagramSocket socket = null;

    public static void main(String[] args) {
        InetAddress address = null;
        int port = 1337;
        DatagramSocket socket = null;
        //Задаем инетадресс(локалхост), порт, создаем сокет
        try {
            if (args.length != 0)  //Проверка, указан ли порт в аргументах запуска
                port = Integer.parseInt(args[0]);
            address = InetAddress.getLocalHost();
            socket = new DatagramSocket();
            socket.setSoTimeout(2000);
        } catch (Exception var14) {
            System.out.println("Ошибка соединения.");
        }

        CommandSender commandSender = new CommandSender(address, port, socket);
        ClientGUI gui = new ClientGUI("Клиент", commandSender);
        gui.init();

    }
}
