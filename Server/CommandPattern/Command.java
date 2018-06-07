package Server.CommandPattern;

import Common.FallingInRiver;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*Семейство этих классов необходимо для реализации кнопки Undo. Паттерн команда
* У каждой команды есть переменная, куда сохраняется состояние коллекции до выполнения команды.
* Каждая команда сама определяет реализацию метода init. Execute - общий для всех
* Результат выполнения команды - объект Feedback, где хранится результат выполнения команды (boolean) и сообщение, позже выводимое на экран.
* Если команд Undoable и завершилась удачно, то она помещается в стек истории выполненных команд
* В ServerGUI есть метод, который достает объект команды из стека и заменяет результатом ее undo() текущую коллекцию.
* Как выполняются команды см Executor в ServerGUI
* */

public abstract class Command {
    protected final ReadWriteLock rwl = new ReentrantReadWriteLock();
    protected final Lock readLock = rwl.readLock();
    protected final Lock writeLock = rwl.writeLock();
    protected ConcurrentHashMap<Integer, FallingInRiver> collectionBackup;
    protected String params;



    public void setParams(String params) {
        this.params = params;
    }

    public Feedback execute(ConcurrentHashMap<Integer, FallingInRiver> map, String params){
        saveBackup(map);
        return init(map, params);
    }
    public abstract Feedback init(ConcurrentHashMap<Integer, FallingInRiver> map, String params);

    public class Feedback {
        public final boolean wasExecuted;
        public final String message;

        public Feedback(boolean wasExecuted, String message) {
            this.wasExecuted = wasExecuted;
            this.message = message;
        }
    }

    protected static boolean isInt(String obj) {
        try {
            int num = Integer.parseInt(obj);
            return true;
        } catch (
                NumberFormatException e) {
            return false;
        }
    }

    protected static boolean fileExists(String path) {
        if (path == null) {
            System.out.println("Файл не найден");
            return false;
        }
        File f = new File(path);
        if (f.exists() && !f.isDirectory()) return true;
        else {
            System.out.println("Файл не найден");
            return false;
        }
    }

    public String getParams() {
        return params;
    }

    protected void saveBackup(ConcurrentHashMap<Integer, FallingInRiver> collectionBackup){
        this.collectionBackup= new ConcurrentHashMap<Integer, FallingInRiver>(collectionBackup);
    }

}
