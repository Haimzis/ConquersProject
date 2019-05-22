package WelcomeScreen;

import GameEngine.GameEngine;
import Tasks.SavedGameLoaderTask;
import Tasks.XMLLoaderTask;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;

import java.util.Optional;
import java.util.function.Consumer;

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
        //controller.bindTaskToUIComponents(currentRunningTask, onFinish);
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
        //controller.bindTaskToUIComponents(currentRunningTask, onFinish);
        new Thread(currentRunningTask).start();
    }

    public void cancelCurrentTask() {
        currentRunningTask.cancel();
    }


}
