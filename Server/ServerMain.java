package Server;

import Common.FallingInRiver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMain {
    public static void main(String[] args) {


        User user = null;

        DatagramPacket receivePacket = null;
        DatagramSocket serverSocket;
        boolean running;
        Integer port;
        String path = "lol";
        try {
            path = args[0]; // Менять когда на другой платформе
        }
        catch (ArrayIndexOutOfBoundsException e){}
        String command = "";
        String param = "";
        Scanner in = new Scanner(System.in);
        SingletonCollection.importFromJson(path);

        String finalPath1 = path;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Commands.save(SingletonCollection.getCollection(), finalPath1);
        }
        ));
        ////Проверка, указан ли порт в аргументах запуска
        for (; ; ) {
            try {
                if (args.length < 2) {
                    //System.out.println("Введите порт:");
                    port = 1337;
                    //port = Integer.parseInt(in.nextLine());
                } else port = Integer.parseInt(args[1]);
                serverSocket = new DatagramSocket(port);
                break;
            } catch (Exception e) {
                System.out.println("Ошибка доступа к порту.");
            }
        }

        ServerGUI gui = new ServerGUI("Server", serverSocket);
        gui.init();

        //Ждем пакета
        for (; ; ) {
            try {
                byte[] receiveBuf = new byte[1000];
                receivePacket = new DatagramPacket(receiveBuf, receiveBuf.length);
                serverSocket.receive(receivePacket);
                System.out.println("Пакет получен");
                user = new User(receivePacket.getAddress(), receivePacket.getPort(), false);
                gui.addUser(user);
                gui.updateUsersTable();

            } catch (Exception e) {
                e.printStackTrace();
            }
            //Пакет получен - выполняем необходимые действия. Тот факт, что потоку нужно передавать path, кажется мне тупым, мб надо пофиксить.
            boolean isBanned = gui.getUsersList().get(gui.getUsersList().indexOf(user)).isBanned();
            new CommandExecutor(receivePacket, serverSocket, SingletonCollection.getCollection(), isBanned).start();
        }
    }


}


//Всем отсылать измененную коллекцию