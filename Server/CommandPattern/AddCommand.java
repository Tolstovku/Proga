package Server.CommandPattern;

import Common.FallingInRiver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.ConcurrentHashMap;

public class AddCommand extends Command implements Undoable {
    @Override
    public Feedback init(ConcurrentHashMap<Integer, FallingInRiver> map, String params) {
        try {
            writeLock.lock();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            String json = ("{" + params + "}");
            try {
                FallingInRiver f = gson.fromJson(json, FallingInRiver.class);
                if (map.containsKey(f.getId())) {
                    System.out.println("Элемент с таким ключем уже существует");
                    return new Feedback(false,"Элемент с таким ключем уже существует");
                } else {
                    if (f.getX()>800 || f.getY()>400 || f.getSplashLvl()>10 || f.getDepth()>10 || f.getX()<0 || f.getY()<0 || f.getSplashLvl()<0 || f.getDepth()<0) throw new Exception();
                    map.put(f.getId(), new FallingInRiver(f.getId(), f.getCharName(), f.getSplashLvl(), f.getDepth(), f.getColor().toString(), f.getX(), f.getY()));
                    System.out.println("Элемент успешно добавлен");
                    return new Feedback(true, "Элемент успешно добавлен");
                }
            } catch (Exception e) {
                System.out.println("Ошибка добавления обьекта. Проверьте вводимые данные.");
                return new Feedback(false, "Ошибка добавления обьекта. Проверьте вводимые данные.");
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
