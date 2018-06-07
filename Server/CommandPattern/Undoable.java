package Server.CommandPattern;

import Common.FallingInRiver;

import java.util.concurrent.ConcurrentHashMap;

public interface Undoable {
   ConcurrentHashMap<Integer, FallingInRiver> undo();
}
