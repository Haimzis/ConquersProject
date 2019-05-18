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
        if(GameEngine.gameManager.nextReplay()) {
            loadRoundHistory();
            mainController.getInformationComponentController().incCurrentRoundProperty();
        }
        else{
            //exception
        }
    }
    @FXML
    public void btnPrevClicked(){
        if(GameEngine.gameManager.prevReplay()) {
            loadRoundHistory();
            mainController.getInformationComponentController().decCurrentRoundProperty();
        }
        else {
            //exception
        }

    }
    private void generateReplayState(){
        GameEngine.gameManager.generateReplayState();
        mainController.getMapComponentController().disableMap(false);
    }
    private void exitReplayState(){
        GameEngine.gameManager.exitReplayState();
        mainController.loadCurrentInformation();
        mainController.getMapComponentController().disableMap(true);
    }
    public void setReplayState(){
        if(ReplayComponent.isVisible()){
            generateReplayState();
            mainController.getHeaderComponentController().getBtnManageRound().setDisable(false);
        }
        else{
            mainController.getHeaderComponentController().getBtnManageRound().setDisable(true);
            exitReplayState();
        }
        ReplayComponent.setManaged(!ReplayComponent.isManaged());
        ReplayComponent.setVisible(!ReplayComponent.isVisible());

    }
    private void loadRoundHistory(){
        mainController.loadRoundHistory();
    }

}
