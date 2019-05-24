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
            mainController.getHeaderComponentController().writeAndShowError("Cant replay over last round!");
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
            mainController.getHeaderComponentController().writeAndShowError("Cant replay  before first round!");
        }

    }
    private void generateReplayState(){
        GameEngine.gameManager.generateReplayState();
    }
    private void exitReplayState(){
        GameEngine.gameManager.exitReplayState();
        mainController.loadCurrentInformation();
    }
    public void setReplayState(){
        if(ReplayComponent.isVisible()){
            exitReplayState();
            mainController.getHeaderComponentController().getBtnManageRound().setDisable(false);
            mainController.getMapComponentController().disableMap(false);
            mainController.getHeaderComponentController().getBtnRetire().setDisable(false);
        }
        else{
            mainController.getHeaderComponentController().getBtnManageRound().setDisable(true);
            mainController.getMapComponentController().disableMap(true);
            mainController.getHeaderComponentController().getBtnRetire().setDisable(true);
            generateReplayState();
        }
        ReplayComponent.setManaged(!ReplayComponent.isManaged());
        ReplayComponent.setVisible(!ReplayComponent.isVisible());

    }
    private void loadRoundHistory(){
        mainController.loadRoundHistory();
    }

}
