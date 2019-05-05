package SubComponents.InformationTable;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.ArrayList;
import java.util.List;


public class InformationController {
    private static int tabCount = 0;

    @FXML
    private TabPane tabPane;


   /* public void addTab(String playerName) {
        Tab tab = createTab(playerName);
        tabPane.getTabs().add(tab);
    }*/

   /* public static Tab createTab(String tabTitle) {
        tabCount++;

        Tab tab = new Tab(tabTitle);
        List<Node> nodeList = new ArrayList<>();

        for(int i = 0 ; i < 7 ; i++) {
            Node node = createContentNode();
        }
        return tab;
    }*/

   /* public static Node createContentNode() {
        Node type  = new Label("Type :" );
    }*/


}
