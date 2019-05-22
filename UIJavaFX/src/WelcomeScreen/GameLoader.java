package WelcomeScreen;

import GameEngine.GameEngine;
import Tasks.SavedGameLoaderTask;
import Tasks.XMLLoaderTask;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;

public class GameLoader {
    private Task<Boolean> currentRunningTask;
    private WelcomeScreenController controller;
    public GameLoader(WelcomeScreenController controller) {
        this.controller = controller;
    }
    public void loadXML(GameEngine gameEngine,
                        SimpleStringProperty messageProperty,
                        SimpleStringProperty selectedFilePathProperty,
                        SimpleBooleanProperty isLoadSucceedProperty) {

        currentRunningTask = new XMLLoaderTask(
                gameEngine,
                messageProperty::set,
                selectedFilePathProperty::getValue,
                isLoadSucceedProperty::set);
        new Thread(currentRunningTask).start();
    }

    public void loadSavedGame(GameEngine gameEngine,
                              SimpleStringProperty messageProperty,
                              SimpleStringProperty selectedFilePathProperty,
                              SimpleBooleanProperty isLoadSucceedProperty) {

        currentRunningTask = new SavedGameLoaderTask(
                gameEngine,
                messageProperty::set,
                selectedFilePathProperty::getValue,
                isLoadSucceedProperty::set);
        new Thread(currentRunningTask).start();
    }
}
