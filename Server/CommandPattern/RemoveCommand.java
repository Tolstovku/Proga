package Server.CommandPattern;

import Common.FallingInRiver;

import java.util.concurrent.ConcurrentHashMap;

public class RemoveCommand extends Command implements Undoable {

    @Override
    public Feedback init(ConcurrentHashMap<Integer, FallingInRiver> map, String keyS) {
        try {
            writeLock.lock();
            if (keyS == null) {
                System.out.println("Файл не найден");
                return new Feedback(false,"Файл не найден");
            }
            if ((isInt(keyS)) || (keyS.equals("null"))) {
                Integer key = 0;
                if (!(keyS.equals("null"))) key = Integer.parseInt(keyS);
                if (map.containsKey(key)) {
                    map.remove(key);
                    System.out.println("Элемент успешно удален");
                    return new Feedback(true,"Элемент успешно удален");
                } else {
                    System.out.println("В коллекции нет элемента с таким ключем.");
                    return new Feedback(false, "В коллекции нет элемента с таким ключем.");
                }
            } else {
                System.out.println("Неверный формат ключа.");
                return new Feedback(false,"Неверный формат ключа.");
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
