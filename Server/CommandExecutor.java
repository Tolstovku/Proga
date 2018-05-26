package Server;
import Common.FallingInRiver;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class CommandExecutor extends Thread {
    private InetAddress clientIPAdress;
    private int clientPort;
    private ConcurrentHashMap<Integer, FallingInRiver> map;
    private byte[] receiveBuf;
    private DatagramPacket sendPacket;
    private DatagramSocket serverSocket;
    //Прочее
    private byte[] sendBuf;
    private String str;
    private String command;
    private String param;
    private String respond;
    private String path;

    public CommandExecutor(DatagramPacket receivedPacket, DatagramSocket serverSocket, ConcurrentHashMap<Integer, FallingInRiver> map, String path) {
        this.serverSocket = serverSocket;
        this.clientIPAdress = receivedPacket.getAddress();
        this.clientPort = receivedPacket.getPort();
        this.map = map;
        this.receiveBuf = receivedPacket.getData();
        this.path = path;
    }
    //Соединение



    public void run() {
        str = "";
        str = new String(receiveBuf).trim();
        System.out.println("Команда - " + str);
        if ((str.contains("{")) && (str.contains("}"))) {
            command = str.substring(0, str.indexOf("{"));
            param = str.substring(str.indexOf("{") + 1, str.lastIndexOf("}"));
        } else
            command = str;
        try {
            switch (command) {
                case "import":
                    respond = Commands.importCHM(map, param);
                    sendBuf = serializeMap(map);
                    break;
                case "info":
                    respond = Commands.info(map);
                    sendBuf = serializeMap(map);
                    break;
                case "save":
                    respond = Commands.save(map, path);
                    sendBuf = serializeMap(map);
                    break;
                case "remove":
                    respond = Commands.remove(map, param);
                    sendBuf = serializeMap(map);
                    break;
                case "remove_lower":
                    respond = Commands.remove_lower(map, param);
                    sendBuf = serializeMap(map);
                    break;
                case "check":
                    respond = Commands.check(map);
                    sendBuf = serializeMap(map);
                    break;
                case "add":
                    respond = Commands.addFall(map, param);
                    sendBuf = serializeMap(map);
                    break;
                case "start":
                    sendBuf = serializeMap(map);
                    break;
                case "Shutdown server":
                case "Server shutdown":
                    System.out.println("Server shutdown");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Неверная команда");
                    respond = "Неверная команда";
                    sendBuf = serializeMap(map);
            }
        }
            catch(Exception e){
                e.printStackTrace();
            }
            try {
                sendPacket = new DatagramPacket(sendBuf, sendBuf.length, clientIPAdress, clientPort);
                serverSocket.send(sendPacket);
                System.out.println("Package sent to " + clientIPAdress + ":" + clientPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    private byte[] serializeMap(ConcurrentHashMap<Integer, FallingInRiver> map) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(map);
        out.flush();
        out.close();
        return bos.toByteArray();
    }
}
