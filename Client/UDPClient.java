package Client;


import java.net.*;

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
            socket.setSoTimeout(1000);
        } catch (Exception var14) {
            System.out.println("Ошибка соединения.");
        }

        ConnectionHandler.configure(address, port, socket);
        SingletonGUI.getGUI();
        SingletonGUI.initGUI();

        final DatagramSocket finalSocket = socket; // чтобы работало в лямбде
        new Thread(() -> {
            for(;;) {
                byte[] sendBuf;
                byte[] receiveBuf = new byte[10000000];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuf, receiveBuf.length);
                try {
                    finalSocket.receive(receivePacket);
                    ConnectionHandler.handlePacket(receivePacket);
                } catch (SocketTimeoutException e) {}
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Something very weird happened");
                }
            }

        }).start();

    }
}
