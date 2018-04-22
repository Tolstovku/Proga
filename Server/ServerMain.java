package Server;

import Common.FallingInRiver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMain {
    public static void main(String[] args) {
        DatagramPacket receivePacket = null;
        DatagramSocket serverSocket;
        boolean running;
        ConcurrentHashMap<Integer, FallingInRiver> map = new ConcurrentHashMap<>();
        Integer port;
        String path = args[0];
        String command = "";
        String param = "";
        Scanner in = new Scanner(System.in);
        Commands.importCHM(map, path);
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                Commands.save(map, path)
        ));
        ////Проверка, указан ли порт в аргументах запуска
        for (; ; ) {
            try {
                if (args.length < 2) {
                    System.out.println("Введите порт:");
                    port = Integer.parseInt(in.nextLine());
                } else port = Integer.parseInt(args[1]);
                serverSocket = new DatagramSocket(port);
                break;
            } catch (Exception e) {
                System.out.println("Ошибка доступа к порту.");
            }
        }
        //Ждем пакета
        for (; ; ) {
            try {
                byte[] recieveBuf = new byte[1000];
                receivePacket = new DatagramPacket(recieveBuf, recieveBuf.length);
                serverSocket.receive(receivePacket);
                System.out.println("Пакет получен");
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Пакет получен - выполняем необходимые действия. Тот факт, что потоку нужно передавать path, кажется мне тупым, мб надо пофиксить.
            new CommandExecutor(receivePacket, serverSocket, map, path).start();

        }
    }
}