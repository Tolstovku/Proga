package Server;

import Client.FallingInRiver;

import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMain {
    public static void main(String[] args) {
        //Old part
        String path = args[0];
        String command = "";
        String param = "";
        Scanner in = new Scanner(System.in);
        Integer port = null;
        ConcurrentHashMap<Integer, FallingInRiver> map = new ConcurrentHashMap<>();
        Commands.importLHM(map, path);
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                Commands.save(map, path)
        ));
        UDPServer server = null;
        for (;;) {
            System.out.println("Введите порт:");
            try {
                port = Integer.parseInt(in.nextLine());
                server = new UDPServer(port, map);
                break;
            } catch (Exception e) {
                System.out.println("Ошибка доступа к порту.");
            }
        }
        server.start();
        while ((!command.equals("q")) && (!command.equals("Q"))) {
            String str = "";
            System.out.println("Введите команду. \nДля помощи введите pomogiti или help. \nДля выхода введите q / Q");
            str = in.nextLine();
            if ((str.contains("{")) && (str.contains("}"))) {
                command = str.substring(0, str.indexOf("{"));
                param = str.substring(str.indexOf("{") + 1, str.lastIndexOf("}"));
            } else
                command = str;
            try {
                switch (command) {
                    case "help":
                    case "pomogiti":
                        Commands.help();
                        break;
                    case "import":
                        Commands.importLHM(map, param);
                        break;
                    case "info":
                        Commands.info(map);
                        break;
                    case "save":
                        Commands.save(map, path);
                        break;
                    case "remove":
                        Commands.remove(map, (param), path);
                        break;
                    case "remove_lower":
                        Commands.remove_lower(map, (param), path);
                        break;
                    case "check":
                        Commands.check(map);
                        break;
                    case "add":
                        Commands.addFall(map, param, path);
                        break;
                    case "check_order":
                        Commands.checkOrder(map);
                        break;
                    case "Q":
                    case "q":
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Неверная команда");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}