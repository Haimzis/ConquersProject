package SubComponents.Popups.ResultPopup;

import MainComponents.AppController;
import SubComponents.Popups.AttackPopup.AttackPopupController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class ResultPopupController {
    private AppController mainController;
    @FXML private ImageView victoryIcon;
    @FXML private ImageView defeatIcon;
    @FXML private ImageView drawIcon;
    @FXML private Label lblResult;
    @FXML private TextArea txtaResults;
    @FXML private AnchorPane mainAnchor;

    public void setMainController(AppController mainController) { this.mainController = mainController; }


    public void populateInfoBasedOnResult(String info , AttackPopupController.Result result) {
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
        lblResult.setText("DRAW");
        drawIcon.setVisible(true);
        txtaResults.appendText(info);

    }

    private void populateLoseInfo(String info) {
        lblResult.setText("DEFEAT");
        defeatIcon.setVisible(true);
        txtaResults.appendText(info);
    }

    private void populateWinInfo(String info) {
        lblResult.setText("VICTORY!");
        victoryIcon.setVisible(true);
        txtaResults.appendText(info);
    }
}
