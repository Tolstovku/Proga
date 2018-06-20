package Server;

import Common.FallingInRiver;
import Server.CommandPattern.Command;
import Server.CommandPattern.ImportCommand;

import java.util.concurrent.ConcurrentHashMap;

public class SingletonCollection {
    private static ConcurrentHashMap<Integer, FallingInRiver> collection;

    public static ConcurrentHashMap<Integer,FallingInRiver> getCollection(){
        if (collection==null)
            collection= new ConcurrentHashMap<Integer,FallingInRiver>();
        return collection;
    }

    public static void setCollection(ConcurrentHashMap<Integer, FallingInRiver> newCollection) {
        collection = new ConcurrentHashMap<>(newCollection);
    }

    public static void importFromJson (String path){
        if (collection==null)
            collection= new ConcurrentHashMap<Integer,FallingInRiver>();
        ImportCommand imp = new ImportCommand();
        imp.init(collection, path);
    }

}
