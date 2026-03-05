package dataaccess;

import model.GameData;
import java.util.HashMap;

public class MemoryGameDAO {
    
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public void clearGames() {
        games.clear();
    }
    
}
