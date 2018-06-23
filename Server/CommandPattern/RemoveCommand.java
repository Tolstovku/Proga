package Server.CommandPattern;

import Common.FallingInRiver;
import Server.ORM.DBConnectionConfig;
import Server.ORM.ORMManager;

import java.util.concurrent.ConcurrentHashMap;

public class RemoveCommand extends Command implements Undoable {

    @Override
    public Feedback init(ConcurrentHashMap<Integer, FallingInRiver> map, String keyS) {
        ORMManager<FallingInRiver> ORMManager = new ORMManager<>(FallingInRiver.class, DBConnectionConfig.url, DBConnectionConfig.login, DBConnectionConfig.password);
        try {
            writeLock.lock();
            if (keyS == null) {
                System.out.println("Файл не найден");
                return new Feedback(false,"fileNotFound");
            }
            if ((isInt(keyS)) || (keyS.equals("null"))) {
                Integer key = 0;
                if (!(keyS.equals("null"))) key = Integer.parseInt(keyS);
                DBConnectionConfig.getData();
                System.out.println(key);
                if (map.containsKey(key)) {
                    map.remove(key);
                    boolean wasExecuted = ORMManager.delete(new FallingInRiver(key, "ds", 1, 1, "ds", 1, 1));
                    System.out.println("Элемент успешно удален");
                    return new Feedback(true,"removedOne");
                } else {
                    System.out.println("В коллекции нет элемента с таким ключем.");
                    return new Feedback(false, "noSuchKey");
                }
            } else {
                System.out.println("Неверный формат ключа.");
                return new Feedback(false,"wrongFormat");
            }
        }
        finally {
            ORMManager.close();
            writeLock.unlock();
        }
    }

    public ConcurrentHashMap<Integer, FallingInRiver> undo() {
        return collectionBackup;
    }
}
