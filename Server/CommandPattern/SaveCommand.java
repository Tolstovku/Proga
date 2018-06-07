package Server.CommandPattern;

import Common.FallingInRiver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SaveCommand extends Command {

    @Override
    public Feedback init(ConcurrentHashMap<Integer, FallingInRiver> map, String path) {
        if (fileExists(path)) {
            try {
                readLock.lock();
                FileOutputStream fos = new FileOutputStream(path);
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                Set entryset = map.entrySet();
                Iterator<Map.Entry> iter = entryset.iterator();
                String decodedTmp;
                while (iter.hasNext()) {
                    String tmp = gson.toJson(iter.next().getValue());
                    decodedTmp = new String(tmp.getBytes());
                    byte[] buffer = decodedTmp.getBytes();
                    fos.write(buffer, 0, buffer.length);
                }

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                readLock.unlock();
            }
            System.out.println("Коллекция успешно сохранена");
            return new Feedback(true,"Коллекция успешно сохранена");
        } else return new Feedback(false, "Файл не найден");
    }
}
