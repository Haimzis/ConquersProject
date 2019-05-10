package SubComponents.Popups.ResultPopup;

import MainComponents.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ResultPopupController {
    private AppController mainController;
    private AppController.Result result;
    @FXML private ImageView victoryIcon;
    @FXML private ImageView defeatIcon;
    @FXML private Label resultsLabel;
    @FXML private TextFlow resultsTextArea;
    @FXML private AnchorPane mainAnchor;

    public void setMainController(AppController mainController) { this.mainController = mainController; }
    public void setResult(AppController.Result result) { this.result = result; }


    public void animatePopUp() {

    }

    private void populateInfoBasedOnResult(String info) {
        switch(result) {
            case WIN:
                populateWinInfo(info);
                break;
            case LOSE:
                populateLoseInfo(info);
                break;
            case DRAW:
                populateDrawInfo(info);
                break;
        }
    }

    private void populateDrawInfo(String info) {


    }

    private void populateLoseInfo(String info) {

    }

    private void populateWinInfo(String info) {
        victoryIcon.setVisible(true);
        Text text = new Text(info);


    }
}
