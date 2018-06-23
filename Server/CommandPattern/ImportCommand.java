package Server.CommandPattern;

import Common.FallingInRiver;
import Server.ORM.DBConnectionConfig;
import Server.ORM.ORMManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class ImportCommand extends Command implements Undoable {
    /*@Override
    public Feedback init(ConcurrentHashMap<Integer, FallingInRiver> map, String path) {
        if (fileExists(path)) {
            ConcurrentHashMap<Integer,FallingInRiver> backup = new ConcurrentHashMap<Integer,FallingInRiver>(map);
            map.clear();
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
                map =  new ConcurrentHashMap<Integer,FallingInRiver>(backup);
                e.printStackTrace();
            } finally {
                writeLock.unlock();
            }
            System.out.println("Коллекция успешно импортирована");
            return new Feedback(true,"imported");
        } else return new Feedback(false,"fileNotFound");
    }*/


    public Feedback init(ConcurrentHashMap<Integer, FallingInRiver> map, String path) {
        map.clear();

        DBConnectionConfig.getData();


        ORMManager<FallingInRiver> ORMManager = new ORMManager<>(FallingInRiver.class, DBConnectionConfig.url, DBConnectionConfig.login, DBConnectionConfig.password);

        ResultSet res = ORMManager.getAllElements();
        try {
            while (res.next()) {
                String name = res.getString(1);
                int splash = Integer.parseInt(res.getString(2));
                double depth = Double.parseDouble(res.getString(3));
                String color = res.getString(4);
                int x = Integer.parseInt(res.getString(5));
                int y = Integer.parseInt(res.getString(6));
                int id = Integer.parseInt(res.getString(7));
                FallingInRiver f = new FallingInRiver(id, name, splash, depth, color, x, y);
                map.put(f.getId(), f);
            }
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
        finally {
            ORMManager.close();
        }
        return null;
    }
    public ConcurrentHashMap<Integer, FallingInRiver> undo() {
        return collectionBackup;
    }
}

