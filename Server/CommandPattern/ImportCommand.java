package Server.CommandPattern;

import Common.FallingInRiver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.concurrent.ConcurrentHashMap;

public class ImportCommand extends Command implements Undoable {
    @Override
    public Feedback init(ConcurrentHashMap<Integer, FallingInRiver> map, String path) {
        if (fileExists(path)) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            FileInputStream fis;
            BufferedInputStream bis;
            String importedJson = "";
            String decodedJson;
            try {
                writeLock.lock();
                fis = new FileInputStream(path);
                bis = new BufferedInputStream(fis);
                while (bis.available() > 0) {
                    char c = (char) bis.read();
                    importedJson += c;
                    if (c == '}') {
                        decodedJson = new String(importedJson.getBytes("ISO-8859-1"), "utf-8");
                        FallingInRiver f = gson.fromJson(decodedJson, FallingInRiver.class);
                        map.put(f.getId(), new FallingInRiver(f.getId(), f.getCharName(), f.getSplashLvl(), f.getDepth(), f.getColor().toString(), f.getX(), f.getY()));
                        importedJson = "";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                writeLock.unlock();
            }
            System.out.println("Коллекция успешно импортирована");
            return new Feedback(true,"Коллекция успешно импортирована");
        } else return new Feedback(false,"Файл не найден");
    }

    public ConcurrentHashMap<Integer, FallingInRiver> undo() {
        return collectionBackup;
    }
}

