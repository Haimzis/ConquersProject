package GameEngine;

import Exceptions.invalidInputException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.Files.exists;



public class GameEngine {
    private static Map<Integer, GameManager> gameManagers = new HashMap<>();
    private GameDescriptor lastGameDescriptor;
    public enum ERROR {XML_ERROR , PASS}
    public static int flag = 0; //Final check flag if everything loaded.

    public static Map<Integer, GameManager> getGameManagers() {
        return gameManagers;
    }

    public GameDescriptor loadXML(InputStream XMLPath, String fileName) throws invalidInputException {
        GameDescriptor gameDescriptor = null;
        ERROR validate = validateXML(fileName);
        switch (validate) {
            case PASS:
                gameDescriptor = createDescriptor(XMLPath);
                break;
            case XML_ERROR:
                flag = 0;
                throw  new Exceptions.invalidInputException("File is not .XML!");
        }
        return gameDescriptor;
    }

    public GameDescriptor loadXML(String XMLPath) throws invalidInputException {
        GameDescriptor gameDescriptor = null;
        ERROR validate = validateXML(XMLPath);
        switch (validate) {
            case PASS:
                gameDescriptor = createDescriptor(getPath(XMLPath));
                break;
            case XML_ERROR:
                flag = 0;
                throw  new Exceptions.invalidInputException("File is not .XML!");
        }
        return gameDescriptor;
    }
    private Path getPath(String xmlPath) {
        return Paths.get(xmlPath);
    }
    public GameDescriptor getLastGameDescriptor() {
        return lastGameDescriptor;
    }
    public GameManager newGame(GameDescriptor gameDescriptor) {
        GameManager newGame = new GameManager(gameDescriptor);
        gameManagers.put(newGame.getGameManagerID(), newGame);
        return newGame;
    }
    public GameManager getConsoleGameManager(){
        return gameManagers.get(0);
    }
    public void deleteConsoleGameManager(){
        gameManagers.remove(0);
    }
    //********************//
    /*  XML Validations  */
    //*******************//

    //creates gameDescriptor object from the given XML
    private GameDescriptor createDescriptor(Path xmlPath) throws invalidInputException {
            lastGameDescriptor = new GameDescriptor(xmlPath);
            return lastGameDescriptor;
    }

    private GameDescriptor createDescriptor(InputStream xmlPath) throws invalidInputException {
        lastGameDescriptor = new GameDescriptor(xmlPath);
        return lastGameDescriptor;
    }

    private ERROR validateXML(String xmlPath) {
        if(xmlPath.toLowerCase().endsWith(".xml"))
            return ERROR.PASS;
        else
            return ERROR.XML_ERROR;
    }

    //********************//
    /*  Files Save/Load  */
    //********************//

    public Path getLoadFilePath(String path) {
        Path loadFilePath = Paths.get(path);
        boolean fileExist = exists(loadFilePath);
        if(!fileExist) return null;
        else return loadFilePath;
    }
    public static boolean saveGame(Path path, GameManager manager) {
        File file = new File(path.toString());
        try(ObjectOutputStream write= new ObjectOutputStream (new FileOutputStream(file))) {
            write.writeObject(manager);
            return true;
        } catch(IOException nse) {
            return false;
        }
    }
    public boolean loadGame(Path path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(path.toString()))) {
            GameManager loadedGame = (GameManager) in.readObject();
            gameManagers.replace(loadedGame.getGameManagerID(),loadedGame);
            flag = 1;
            return true;
        }
    }
}