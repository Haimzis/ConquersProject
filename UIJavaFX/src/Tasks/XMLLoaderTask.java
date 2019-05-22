package Tasks;

import Exceptions.invalidInputException;
import GameEngine.GameEngine;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class XMLLoaderTask extends Task<Boolean> {
    private GameEngine gameEngine;
    private Consumer<String> messageDelegate;
    private Supplier<String> filePathDelegate;
    private Consumer<Boolean> isLoadSucceedDelegate;
    private final long SLEEP_TIME = 5;

    public XMLLoaderTask(GameEngine gameEngine,
                         Consumer<String> messageDelegate,
                         Supplier<String> filePathDelegate,
                         Consumer<Boolean> isLoadSucceedDelegate) {
        this.gameEngine = gameEngine;
        this.messageDelegate = messageDelegate;
        this.filePathDelegate = filePathDelegate;
        this.isLoadSucceedDelegate = isLoadSucceedDelegate;
    }

    @Override
    protected Boolean call() {
            try {
                gameEngine.loadXML(filePathDelegate.get());
                Platform.runLater(()-> {
                    isLoadSucceedDelegate.accept(true);
                    messageDelegate.accept("XML Loaded");
                });

            } catch (invalidInputException e) {
                e.printStackTrace();
                Platform.runLater(()-> {
                    isLoadSucceedDelegate.accept(false);
                    messageDelegate.accept(e.getMessage());
                });
            }
        return Boolean.TRUE;
    }
}
