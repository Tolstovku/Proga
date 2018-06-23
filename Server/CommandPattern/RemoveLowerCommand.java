package Server.CommandPattern;

import Common.FallingInRiver;
import Server.ORM.DBConnectionConfig;
import Server.ORM.ORMManager;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RemoveLowerCommand extends Command implements Undoable {
    @Override
    public Feedback init(ConcurrentHashMap<Integer, FallingInRiver> map, String keyS) {
        ORMManager<FallingInRiver> ORMManager = new ORMManager<>(FallingInRiver.class, DBConnectionConfig.url, DBConnectionConfig.login, DBConnectionConfig.password);
        ORMManager.setAutoCommit(false);
        boolean wasExecuted;
        boolean good;
        try {
            writeLock.lock();
            if (keyS == null) {
                System.out.println("Файл не найден");
                return new Feedback(false,"fileNotFound");
            }
            if (isInt(keyS) || (keyS == "null")) {
                Integer key = 0;
                if (keyS != "null") key = Integer.parseInt(keyS);
                boolean flag = false;
                Set entryset = map.entrySet();
                Iterator<Map.Entry> iter = entryset.iterator();
                while (iter.hasNext()) {
                    Integer mapEntryKey = (Integer) iter.next().getKey();
                    if (mapEntryKey < key) {
                        flag = true;
                        wasExecuted = ORMManager.delete(new FallingInRiver(mapEntryKey, "ds", 1, 1, "ds", 1, 1));
                    }
                }
                if (flag == true) {
                    System.out.println("Элементы успешно удалены");
                    return new Feedback(true,"removedMany");
                } else {
                    ORMManager.executeQuery("COMMIT;");
                    System.out.println("В коллекции нет элементов, с ключом, меньше " + key);
                    return new Feedback(false,"noKeyLess");
                }
            } else {
                System.out.println("Неверный формат ключа.");
                return new Feedback(false, "wrongFormat");
            }
        }
        finally {
            ORMManager.commit();
            ORMManager.close();
            writeLock.unlock();
        }
    }

    public ConcurrentHashMap<Integer, FallingInRiver> undo() {
        return collectionBackup;
    }
}
