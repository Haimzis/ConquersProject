package SubComponents.Popups.ResultPopup;

import MainComponents.AppController;
import SubComponents.Popups.AttackPopup.AttackPopupController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class ResultPopupController {
    private AppController mainController;
    @FXML private ImageView victoryIcon;
    @FXML private ImageView defeatIcon;
    @FXML private ImageView drawIcon;
    @FXML private Label resultsLabel;
    @FXML private TextArea resultsTextArea;
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
        resultsLabel.setText("DRAW");
        drawIcon.setVisible(true);
        resultsTextArea.appendText(info);

    }

    private void populateLoseInfo(String info) {
        resultsLabel.setText("DEFEAT");
        defeatIcon.setVisible(true);
        resultsTextArea.appendText(info);
    }

    private void populateWinInfo(String info) {
        resultsLabel.setText("VICTORY!");
        victoryIcon.setVisible(true);
        resultsTextArea.appendText(info);
    }
}
