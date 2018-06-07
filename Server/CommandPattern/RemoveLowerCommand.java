package Server.CommandPattern;

import Common.FallingInRiver;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RemoveLowerCommand extends Command implements Undoable {
    @Override
    public Feedback init(ConcurrentHashMap<Integer, FallingInRiver> map, String keyS) {
        try {
            writeLock.lock();
            if (keyS == null) {
                System.out.println("Файл не найден");
                return new Feedback(false,"Файл не найден");
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
                        map.remove(mapEntryKey);
                    }
                }
                if (flag == true) {
                    System.out.println("Элементы успешно удалены");
                    return new Feedback(true,"Элементы успешно удалены");
                } else {
                    System.out.println("В коллекции нет элементов, с ключом, меньше " + key);
                    return new Feedback(false,"В коллекции нет элементов, с ключом, меньше " + key);
                }
            } else {
                System.out.println("Неверный формат ключа.");
                return new Feedback(false, "Неверный формат ключа.");
            }
        }
        finally {
            writeLock.unlock();
        }
    }

    public ConcurrentHashMap<Integer, FallingInRiver> undo() {
        return collectionBackup;
    }
}
