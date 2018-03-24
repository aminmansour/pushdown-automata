import controller.ControllerFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import view.ViewFactory;

/**
 * The main class of application. Responsible for pre-loading all views and setting up stage for suitable
 * viewing by user.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane root = FXMLLoader.load(getClass().getResource("/layouts/main.fxml"));
        primaryStage.setTitle("PDA");
        primaryStage.setScene(new Scene(root, 1400, 900));
        primaryStage.show();
        primaryStage.setMaximized(true);
        maximizeStage(primaryStage);
        preloadViews(primaryStage);
        ViewFactory.switchToView(ViewFactory.home);
    }

    //maximize window
    private void maximizeStage(Stage primaryStage) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
    }

    //preload all pages on application loadLibrary
    private void preloadViews(Stage primaryStage) throws java.io.IOException {
        ViewFactory.stage = primaryStage;
        ViewFactory.globalPane = (BorderPane) primaryStage.getScene().getRoot();

        FXMLLoader toolbarLoader = new FXMLLoader();
        HBox toolbar = toolbarLoader.load(getClass().getResource("/layouts/tool_bar_partial.fxml").openStream());
        ControllerFactory.toolBarPartialController = toolbarLoader.getController();
        ((BorderPane) primaryStage.getScene().getRoot()).setTop(toolbar);

        FXMLLoader homeLoader = new FXMLLoader(getClass().getResource("/layouts/home_page.fxml"));
        ViewFactory.home = homeLoader.load();
        ControllerFactory.homeController = homeLoader.getController();

        FXMLLoader pdaRunnerLoader = new FXMLLoader(getClass().getResource("/layouts/pda_runner_page.fxml"));
        ViewFactory.pdaRunner = pdaRunnerLoader.load();
        ControllerFactory.pdaRunnerController = pdaRunnerLoader.getController();


        FXMLLoader quickDefinitionLoader = new FXMLLoader(getClass().getResource("/layouts/quick_definition_page.fxml"));
        ViewFactory.quickDefinition = quickDefinitionLoader.load();
        ControllerFactory.quickDefinitionController = quickDefinitionLoader.getController();

        FXMLLoader helpLoader = new FXMLLoader(getClass().getResource("/layouts/help_page.fxml"));
        ViewFactory.help = helpLoader.load();
        ControllerFactory.helpController = helpLoader.getController();
    }


    public static void main(String[] args) {
        launch(args);
    }


}
