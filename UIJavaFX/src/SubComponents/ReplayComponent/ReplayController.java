package SubComponents.ReplayComponent;

import GameEngine.GameEngine;
import MainComponents.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class ReplayController {
    @FXML private Button btnNext;
    @FXML private Button btnPrev;
    @FXML private AnchorPane ReplayComponent;
    @FXML private AppController mainController;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
    @FXML
    public void btnNextClicked(){
        GameEngine.gameManager.nextReplay();
        loadRoundHistory();
        mainController.getInformationComponentController().incCurrentRoundProperty();
    }
    @FXML
    public void btnPrevClicked(){
        GameEngine.gameManager.prevReplay();
        loadRoundHistory();
        mainController.getInformationComponentController().decCurrentRoundProperty();

    }
    private void generateReplayState(){
        GameEngine.gameManager.generateReplayState();
        mainController.getMapComponentController().disableMap(true);
    }
    private void exitReplayState(){
        GameEngine.gameManager.exitReplayState();
        mainController.loadCurrentInformation();
        mainController.getMapComponentController().disableMap(false);
    }
    public void setReplayState(){
        if(ReplayComponent.isVisible()){
            generateReplayState();
        }
        else{
            exitReplayState();
        }
        ReplayComponent.setManaged(!ReplayComponent.isManaged());
        ReplayComponent.setVisible(!ReplayComponent.isVisible());

    }
    private void loadRoundHistory(){
        mainController.loadRoundHistory();
    }

}
