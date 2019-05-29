package GameEngine;

import Exceptions.invalidInputException;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.Files.exists;



public class GameEngine {
    private GameDescriptor descriptor;
    private Map<Integer ,GameManager> gameManagers = new HashMap<>();


    public enum ERROR {XML_ERROR , PASS}
    public static int flag = 0; //Final check flag if everything loaded.

    public GameDescriptor getDescriptor() {
        return descriptor;
    }
    public void setDescriptor(GameDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    public void loadXML(String XMLPath) throws invalidInputException {
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
        this.descriptor = gameDescriptor;
    }
    private Path getPath(String xmlPath) {
        return Paths.get(xmlPath);
    }
    public void newGame() {
        GameManager newGame =new GameManager(descriptor);
       gameManagers.put(newGame.getGameManagerID(),newGame);
    }

    //********************//
    /*  XML Validations  */
    //*******************//

    //creates gameDescriptor object from the given XML
    private GameDescriptor createDescriptor(Path xmlPath) throws invalidInputException {
            return new GameDescriptor(xmlPath);
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