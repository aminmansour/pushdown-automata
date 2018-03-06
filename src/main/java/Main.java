import controller.ControllerFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.ViewFactory;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane root = FXMLLoader.load(getClass().getResource("layouts/main.fxml"));
        primaryStage.setTitle("PushDownAutomata");
        primaryStage.setScene(new Scene(root, 1400, 900));
        primaryStage.show();
        setGlobalVariables(primaryStage);
        root.setCenter(ViewFactory.homePage);

    }

    private void setGlobalVariables(Stage primaryStage) throws java.io.IOException {
        ViewFactory.stage = primaryStage;
        ViewFactory.globalPane = (BorderPane) primaryStage.getScene().getRoot();

        FXMLLoader homeLoader = new FXMLLoader(getClass().getResource("layouts/home_page.fxml"));
        ViewFactory.homePage = homeLoader.load();
        ControllerFactory.homeController = homeLoader.getController();

        FXMLLoader codeDefinitionLoader = new FXMLLoader(getClass().getResource("layouts/code_definition_page.fxml"));
        ViewFactory.codeDefinition = codeDefinitionLoader.load();
        ControllerFactory.codeDefinitionController = codeDefinitionLoader.getController();

        FXMLLoader pdaRunnerLoader = new FXMLLoader(getClass().getResource("layouts/pda_runner_page.fxml"));
        ViewFactory.pdaRunner = pdaRunnerLoader.load();
        ControllerFactory.pdaRunnerController = pdaRunnerLoader.getController();

        FXMLLoader libraryLoader = new FXMLLoader(getClass().getResource("layouts/library_page.fxml"));
        ViewFactory.library = libraryLoader.load();
        ControllerFactory.libraryLoaderController = libraryLoader.getController();


        FXMLLoader examplesLoader = new FXMLLoader(getClass().getResource("layouts/examples_page.fxml"));
        ViewFactory.examples = examplesLoader.load();
        ControllerFactory.examplesLoaderController = examplesLoader.getController();

        FXMLLoader quickDefinitionLoader = new FXMLLoader(getClass().getResource("layouts/quick_definition_page.fxml"));
        ViewFactory.quickDefinition = quickDefinitionLoader.load();
        ControllerFactory.quickDefinitionController = quickDefinitionLoader.getController();

        FXMLLoader helpLoader = new FXMLLoader(getClass().getResource("layouts/help_page.fxml"));
        ViewFactory.help = helpLoader.load();
        ControllerFactory.helpController = helpLoader.getController();

        FXMLLoader toolbarLoader = new FXMLLoader();
        VBox toolbar = toolbarLoader.load(getClass().getResource("layouts/tool_bar_partial.fxml").openStream());
        ControllerFactory.toolBarPartialController = toolbarLoader.getController();
        ((BorderPane) primaryStage.getScene().getRoot()).setTop(toolbar);
    }


    public static void main(String[] args) {
        launch(args);
    }


}
