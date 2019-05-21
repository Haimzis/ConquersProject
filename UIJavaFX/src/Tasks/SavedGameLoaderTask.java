package Tasks;

import GameEngine.GameEngine;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class SavedGameLoaderTask extends Task<Boolean> {
    private GameEngine gameEngine;
    private Consumer<String> messageDelegate;
    private Supplier<String> filePathDelegate;
    private Consumer<Boolean> isLoadSucceedDelegate;

    public SavedGameLoaderTask(GameEngine gameEngine, Consumer<String> messageDelegate,
                               Supplier<String> filePathDelegate,
                               Consumer<Boolean> isLoadSucceedDelegate) {
        this.messageDelegate = messageDelegate;
        this.filePathDelegate = filePathDelegate;
        this.isLoadSucceedDelegate = isLoadSucceedDelegate;
        this.gameEngine = gameEngine;
    }

    @Override
    protected Boolean call() {
        try {
            gameEngine.loadGame(gameEngine.getLoadFilePath(filePathDelegate.get()));
            gameEngine.setDescriptor(GameEngine.gameManager.getGameDescriptor());
            Platform.runLater(()-> {
                messageDelegate.accept("Game loaded");
                isLoadSucceedDelegate.accept(true);
            });

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Platform.runLater(()-> {
                messageDelegate.accept("Could not load saved game file!");
                isLoadSucceedDelegate.accept(true);
            });
        }
        return Boolean.TRUE;
    }
}
