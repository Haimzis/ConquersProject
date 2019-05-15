package GameEngine;

import javafx.collections.ObservableList;
import javafx.scene.Node;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.exists;



public class GameEngine {
    private GameDescriptor descriptor;
    public static GameManager gameManager;


    public enum ERROR {XML_ERROR , PASS}
    public static int flag = 0; //Final check flag if everything loaded.

    public GameDescriptor getDescriptor() {
        return descriptor;
    }
    public void setDescriptor(GameDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    public Boolean loadXML(String XMLPath) {
        GameDescriptor gameDescriptor = null;
        ERROR validate = validateXML(XMLPath);
        switch (validate) {
            case PASS:
                gameDescriptor = createDescriptor(getPath(XMLPath));
                break;
            case XML_ERROR:
                System.out.println("XML file not found");
                flag = 0;
                break;
        }

        if(gameDescriptor != null) // Means everything is loaded and in place to begin the game
            flag = 1; //GD has been loaded successfully
        this.descriptor = gameDescriptor;
        return flag == 1;
    }
    private Path getPath(String xmlPath) {
        return Paths.get(xmlPath);
    }
    public void newGame() {
        gameManager = new GameManager(descriptor);
    }

    //********************//
    /*  XML Validations  */
    //*******************//

    //creates gameDescriptor object from the given XML
    private GameDescriptor createDescriptor(Path xmlPath) {
        try {
            return new GameDescriptor(xmlPath);
        }
        catch (IllegalArgumentException e) {
            System.out.println("Descriptor failed to create , try again with different XML.");
            return null;
        }
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
    public boolean loadGame(Path path) {
        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(path.toString()))) {
            gameManager = (GameManager) in.readObject();
            flag = 1;
            return true;
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }
}