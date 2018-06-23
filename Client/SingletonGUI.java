package Client;

import Common.FallingInRiver;

import java.util.concurrent.ConcurrentHashMap;

public class SingletonGUI {
    private static ClientGUI gui;

    public static ClientGUI getGUI(){
        if (gui==null)
            gui= new ClientGUI();
        return gui;
    }

    public static void updateGUICollection(ConcurrentHashMap<Integer, FallingInRiver> newCollection){
        gui.updateCollection(newCollection);
    }

    public static void initGUI(){
        gui.init();
    }

    public static void showMessage(String message){
        gui.showMessage(message);
    }
}
