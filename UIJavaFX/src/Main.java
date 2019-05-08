import Resources.ResourceConstants;
import WelcomeScreen.WelcomeScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.annotation.Resource;
import java.net.URL;

public class Main extends Application {


    public static void main(String[] args) {
        Thread.currentThread().setName("main");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();

        //load Welcome Screen FXML
        URL WelcomeScreenURL = getClass().getResource(ResourceConstants.WELCOMESCREEN_FXML_INCLUDE_RESOURCE);
        fxmlLoader.setLocation(WelcomeScreenURL);
        Parent root = fxmlLoader.load(WelcomeScreenURL.openStream());

        // wire up primary stage
        WelcomeScreenController welcomeScreenController = fxmlLoader.getController();
        welcomeScreenController.setPrimaryStage(primaryStage);

        //set stage
        primaryStage.setTitle("Conquers Motha Fucka! (Ran is Gay)");
        Scene scene = new Scene(root, 450, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
