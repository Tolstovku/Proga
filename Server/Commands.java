package Server;

import Common.FallingInRiver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jdk.nashorn.internal.runtime.ECMAException;

import java.util.concurrent.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Commands {

    private static ReadWriteLock rwl = new ReentrantReadWriteLock();
    private static Lock readLock = rwl.readLock();
    private static Lock writeLock = rwl.writeLock();

    /**
     * Импортировать обьекты коллекции из json-файла.
     *
     * @param map  Коллекция типа ConcurrentHashMap, в которую производится импорт объектов
     * @param path Путь к json-файлу, из которого производится импорт
     */

     public static String importCHM(ConcurrentHashMap<Integer, FallingInRiver> map, String path) {
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
            }
            finally {
                writeLock.unlock();
            }
            System.out.println("Коллекция успешно импортирована");
            return "Коллекция успешно импортирована";
        }
        else return "Файл не найден";
    }


    /**
     * Сохранить коллекцию в файл
     *
     * @param map  Коллекция типа ConcurrentHashMap содержимое которой необходимо сохранить
     * @param path Путь к json-файлу, в который происходит сохранение
     */

     public static String save(ConcurrentHashMap<Integer, FallingInRiver> map, String path) {

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

            }
            finally {
                readLock.unlock();
            }
            System.out.println("Коллекция успешно сохранена");
            return "Коллекция успешно сохранена";
        }
        else return "Файл не найден";
    }

    /**
     * Вывести в стандартный поток вывода информацию о коллекции
     *
     * @param map Коллекция, информацию о которой необходимо вывести
     */

     public static String info(Map map) {
         try {
             readLock.lock();
             return "Тип коллекции: " + map.getClass().toString().substring(6) + "\nКоличество элементов: " + map.size() + "\nСодержимое коллекции:" + check(map);
         }
         finally {
             readLock.unlock();
         }
    }

    /**
     * Удалить элемент из коллекции по его ключу
     *
     * @param map  Коллекция типа ConcurrentHashMap из которой производится удаление элемента.
     * @param keyS Ключ, элементы с ключами меньше которого нужно удалить.
     */
     public static String remove(ConcurrentHashMap<Integer, FallingInRiver> map, String keyS) {
         try {
             writeLock.lock();
             if (keyS == null) {
                 System.out.println("Файл не найден");
                 return "Файл не найден";
             }
             if ((isInt(keyS)) || (keyS.equals("null"))) {
                 Integer key = 0;
                 if (!(keyS.equals("null"))) key = Integer.parseInt(keyS);
                 if (map.containsKey(key)) {
                     map.remove(key);
                     System.out.println("Элемент успешно удален");
                     return "Элемент успешно удален";
                 } else {
                     System.out.println("В коллекции нет элемента с таким ключем.");
                     return "В коллекции нет элемента с таким ключем.";
                 }
             } else {
                 System.out.println("Неверный формат ключа.");
                 return "Неверный формат ключа.";
             }
         }
         finally {
             writeLock.unlock();
         }
    }

    /**
     * @param map  Коллекция типа ConcurrentHashMap из которой производится удаление элементов.
     * @param keyS
     */
     public static String remove_lower(ConcurrentHashMap<Integer, FallingInRiver> map, String keyS) {
         try {
             writeLock.lock();
             if (keyS == null) {
                 System.out.println("Файл не найден");
                 return "Файл не найден";
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
                     return "Элементы успешно удалены";
                 } else {
                     System.out.println("В коллекции нет элементов, с ключом, меньше " + key);
                     return "В коллекции нет элементов, с ключом, меньше " + key;
                 }
             } else {
                 System.out.println("Неверный формат ключа.");
                 return "Неверный формат ключа.";
             }
         }
         finally {
             writeLock.unlock();
         }
    }

    /**
     * Вывести содержимое коллекции
     *
     * @param map Коллекция, содержимое которой необходимо вывести.
     */
     public static String check(Map map) {
         try {
             readLock.lock();
             String content = "";
             GsonBuilder builder = new GsonBuilder();
             Gson gson = builder.create();
             Set entrySet = map.entrySet();
             Iterator<Map.Entry> iter = entrySet.iterator();
             if (iter.hasNext()) content = gson.toJson(iter.next().getValue());
             while (iter.hasNext()) {
                 FallingInRiver f = (FallingInRiver) iter.next().getValue();
                 content = (content + "\n" + gson.toJson(f));
             }
             return content;
         }
         finally {
             readLock.unlock();
         }
    }

    /**
     * Добавить элемент в коллекцию.
     *
     * @param map    Коллекция типа ConcurrentHashMap в которую необходимо добавить элемент-событие "Падение в реку"
     * @param params json описание элемента.
     */
     public static String addFall(ConcurrentHashMap<Integer, FallingInRiver> map, String params) {
         try {
             writeLock.lock();
             GsonBuilder builder = new GsonBuilder();
             Gson gson = builder.create();
             String json = ("{" + params + "}");
             try {
                 FallingInRiver f = gson.fromJson(json, FallingInRiver.class);
                 if (map.containsKey(f.getId())) {
                     System.out.println("Элемент с таким ключем уже существует");
                     return "Элемент с таким ключем уже существует";
                 } else {
                     if (f.getX()>800 || f.getY()>400 || f.getSplashLvl()>10 || f.getDepth()>10 || f.getX()<0 || f.getY()<0 || f.getSplashLvl()<0 || f.getDepth()<0) throw new Exception();
                     map.put(f.getId(), new FallingInRiver(f.getId(), f.getCharName(), f.getSplashLvl(), f.getDepth(), f.getColor().toString(), f.getX(), f.getY()));
                     System.out.println("Элемент успешно добавлен");
                     return "Элемент успешно добавлен";
                 }
             } catch (Exception e) {
                 System.out.println("Ошибка добавления обьекта. Проверьте вводимые данные.");
                 return "Ошибка добавления обьекта. Проверьте вводимые данные.";
             }
         }
         finally {
             writeLock.unlock();
         }
    }

    /**
     * Вывести информацию о командах
     */

     /* Не используется
     public static void help() {
        System.out.println("import {path} : импортировать ConcurrentHashMap из json файла" + "\nremove {key} : удалить элемент из коллекции по его ключу"
                + "\ninfo : вывести информацию о коллекции" + "\nsave : сохранить коллекцию в файл"
                + "\nremove_lower {key} : удалить из коллекции все элементы, ключ которых меньше, чем заданный"
                + "\ncheck : показать содержимое коллекции." + "\nadd{\"id\":_,\"charName\":\"_\",\"splashLvl\":_,\"depth\":_} : добавить падение");
    }*/

    private static boolean isInt(String obj) {
        try {
            int num = Integer.parseInt(obj);
            return true;
        } catch (
                NumberFormatException e) {
            return false;
        }
    }

    private static boolean fileExists(String path) {
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

    /**
     * Проверить сортировочность коллекции
     *
     * @param map Коллекция типа ConcurrentHashMap в которую необходимо добавить элемент-событие "Падение в реку"
     */
    //Не используется
     public static void checkOrder(ConcurrentHashMap<Integer, FallingInRiver> map) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        TreeMap<Integer, FallingInRiver> testMap = new TreeMap<>(map);
        Set entryset = testMap.entrySet();
        Iterator<Map.Entry> iter = entryset.iterator();
        while (iter.hasNext()) {
            FallingInRiver f = (FallingInRiver) iter.next().getValue();
            System.out.println(gson.toJson(f));
        }

    }

}