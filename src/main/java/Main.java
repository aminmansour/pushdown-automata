
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import controller.ViewFactory;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane root = FXMLLoader.load(getClass().getResource("layouts/main.fxml"));
        primaryStage.setTitle("PushDownAutomata");
        primaryStage.setScene(new Scene(root, 1200, 900));
        primaryStage.show();
        setGlobalVariables(primaryStage);
        root.setCenter(ViewFactory.homePage);
    }

    private void setGlobalVariables(Stage primaryStage) throws java.io.IOException {
        ViewFactory.globalPane = (BorderPane) primaryStage.getScene().getRoot();
        ViewFactory.homePage = FXMLLoader.load(getClass().getResource("layouts/home_page.fxml"));
        ViewFactory.codeDefinition = FXMLLoader.load(getClass().getResource("layouts/code_definition_page.fxml"));
        ViewFactory.pdaRunner = FXMLLoader.load(getClass().getResource("layouts/pda_runner_page.fxml"));
        ViewFactory.stage = primaryStage;
    }


    public static void main(String[] args) {
        launch(args);
    }


}
