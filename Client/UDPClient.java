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

    public UDPClient() {
    }

    public static void main(String[] args) {
        ClientGUI gui = new ClientGUI("Клиент");
        gui.init();

    }

    static public ConcurrentHashMap<Integer, FallingInRiver> sendCommand(String command) {
        ClientGUI gui = new ClientGUI("Клиент");
        gui.init();
        Scanner r = new Scanner(System.in);
        String s = r.nextLine();

        ConcurrentHashMap<Integer, FallingInRiver> falls = null;
        Integer port = 1337;
        InetAddress address = null;
        DatagramSocket socket = null;
        //String stringBuffer = null;
        //Scanner in = new Scanner(stringBuffer);

        /*while (true) {
            try {
                //Проверка, указан ли порт в аргументах запуска
                if (args.length == 0) {
                    port = Integer.parseInt(in.nextLine());
                } else {
                    port = Integer.parseInt(args[0]);             //Выбор порта пользователем
                }

                address = InetAddress.getLocalHost();
                socket = new DatagramSocket();
                socket.setSoTimeout(5000);
                break;
            } catch (Exception var14) {
                System.out.println("Ошибка соединения.");
            }
        }*/

        try{
            address = InetAddress.getLocalHost();
            socket = new DatagramSocket();
            socket.setSoTimeout(50000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

            byte[] receiveBuf = new byte[10000000];
            /*System.out.println("Введите команду. Help - список команд");
            String command = in.nextLine();
            if (command.equals("help")) {
                System.out.println("import {path} : импортировать ConcurrentHashMap из json файла\nremove {key} : удалить элемент из коллекции по его ключу\ninfo : вывести информацию о коллекции\nsave : сохранить коллекцию в файл\nremove_lower {key} : удалить из коллекции все элементы, ключ которых меньше, чем заданный\ncheck : показать содержимое коллекции.\nadd{\"id\":_,\"charName\":\"_\",\"splashLvl\":_,\"depth\":_} : добавить падение");
            }

            if (command.equals("q") || command.equals("Q")) {
               System.out.println("Клиент закрывается...");
               System.exit(0);
            }*/

            byte[] sendBuf = command.getBytes();
            //Отправка команды и получение пакета
            //Костыль а мб и нет: если команда == старт, то мы получим гарантированно коллекцию, поэтому ее нужно сериализировать по-человечески;
            //если же команда не старт, то получим String, так что просто toString(). Я потом переделаю (надеюсь)
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address, port.intValue());
                socket.send(sendPacket);
                DatagramPacket receivePacket = new DatagramPacket(receiveBuf, receiveBuf.length);
                socket.receive(receivePacket);
            } catch (SocketTimeoutException var12) {
                System.out.println("Socket timed out");
            } catch (Exception var13) {
                var13.printStackTrace();
            }

            /*if (command.equals("start")) {
                try {
                    falls = deserializeMap(receiveBuf);
                } catch (Exception var11) {
                    var11.printStackTrace();
                }

                Tale story = new Tale(falls);
                StoryTeller granddad = new StoryTeller(story.getTaleName());
                granddad.tellTale();
                story.tellTale();
                System.exit(0);
            } else {
                System.out.println((new String(receiveBuf)).trim());*/
            try {
                falls = deserializeMap(receiveBuf);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return falls;
        }


    static public ConcurrentHashMap<Integer, FallingInRiver> deserializeMap(byte[] buffer) throws Exception {
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
}
