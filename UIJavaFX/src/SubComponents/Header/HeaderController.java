package SubComponents.Header;

import MainComponents.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


public class HeaderController {
    public AnchorPane HeaderComponent;
    private AppController appController;
    @FXML private  TextFlow headerInfoArea;
    @FXML private Label currentPlayerInTurnLabel;

    public void setMainController(AppController mainController) { this.appController = mainController; }

    public  void writeIntoTextArea(String text) {
        Text textToAdd = new Text(text);
        headerInfoArea.getChildren().add(textToAdd);
    }

    public void setCurrentPlayerInTurnLbl(String currentPlayerName) {
        currentPlayerInTurnLabel.setText(currentPlayerName);
    }

}
