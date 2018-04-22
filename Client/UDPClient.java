package Client;

import Common.FallingInRiver;
import java.net.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class UDPClient {
    public static void main(String[] args) {
        String command;
        ConcurrentHashMap<Integer, FallingInRiver> falls = null;
        Integer port = null;
        InetAddress address = null;
        DatagramSocket socket = null;

        Scanner in = new Scanner(System.in);
            for (; ; ) {

                try {
                    //Проверка, указан ли порт в аргументах запуска
                    if (args.length==0) {
                        System.out.println("Введите порт:");
                        port = Integer.parseInt(in.nextLine());
                    }
                    else port = Integer.parseInt(args[0]);
                    address = InetAddress.getLocalHost();
                    socket = new DatagramSocket();
                    socket.setSoTimeout(5000);
                    break;
                } catch (Exception e) {
                    System.out.println("Ошибка соединения.");
                }
            }
            for (;;) {
                byte[] receiveBuf = new byte[10000];
                byte[] sendBuf;
                System.out.println("Введите команду");
                command = in.nextLine();
                sendBuf = command.getBytes();
                //Отправка команды и получение пакета
                try {
                    DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address, port);
                    socket.send(sendPacket);
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuf, receiveBuf.length);
                    socket.receive(receivePacket);
                } catch (SocketTimeoutException e) {
                    System.out.println("Socket timed out");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Костыль: если команда == старт, то мы получим гарантированно коллекцию, поэтому ее нужно сериализировать по-человечески;
                //если же команда не старт, то получим String, так что просто toString(). Я потом переделаю (надеюсь)
                if (command.equals("start")) {
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
                    System.exit(0);
                }
                else System.out.println(new String(receiveBuf).trim());
            }

    }


    static public ConcurrentHashMap<Integer, FallingInRiver> deserializeMap(byte[] buffer) throws Exception {
        //Удаление null-значений из массива байтов
        int i = buffer.length - 1;
        while (i >= 0 && buffer[i] == 0)
        {
            --i;
        }
        byte[] trimmedBuf = Arrays.copyOf(buffer, i + 1);

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(trimmedBuf));
        ConcurrentHashMap<Integer, FallingInRiver> map = (ConcurrentHashMap<Integer, FallingInRiver>) ois.readObject();
        return map;
    }
}
