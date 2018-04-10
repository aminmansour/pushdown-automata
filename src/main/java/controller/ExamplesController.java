package controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.Definition;
import model.MemoryFactory;
import model.ModelFactory;
import model.PDAMachine;
import view.ViewFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * A Controller for controlling the examples view
 */
public class ExamplesController implements Initializable {

    @FXML
    private Button bExample1;
    @FXML
    private Button bExample2;
    @FXML
    private Button bExample3;
    @FXML
    private Button bExample4;
    @FXML
    private Button bExample5;

    @FXML
    private Label lExample1;
    @FXML
    private Label lExample2;
    @FXML
    private Label lExample3;
    @FXML
    private Label lExample4;
    @FXML
    private Label lExample5;


    /**
     * A method which loads the example definition into the PDA instance
     *
     * @param defToLoad definition to load
     */
    public void load(Definition defToLoad) {
        PDAMachine model = new PDAMachine(defToLoad);
        ControllerFactory.pdaRunnerController.loadPDA(model);
        ViewFactory.switchToView(ViewFactory.pdaRunner);
        ControllerFactory.pdaRunnerController.stop();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<Definition> definitions = MemoryFactory.loadExamples();
        bExample1.setOnAction(event -> load(ModelFactory.duplicate(definitions.get(4))));
        bExample2.setOnAction(event -> load(ModelFactory.duplicate(definitions.get(0))));
        bExample3.setOnAction(event -> load(ModelFactory.duplicate(definitions.get(1))));
        bExample4.setOnAction(event -> load(ModelFactory.duplicate(definitions.get(3))));
        bExample5.setOnAction(event -> load(ModelFactory.duplicate(definitions.get(2))));

        ViewFactory.setLabelGraphic(lExample1, "fa-examples-alt", 100);
        ViewFactory.setLabelGraphic(lExample2, "fa-examples-alt", 100);
        ViewFactory.setLabelGraphic(lExample3, "fa-examples-alt", 100);
        ViewFactory.setLabelGraphic(lExample4, "fa-examples-alt", 100);
        ViewFactory.setLabelGraphic(lExample5, "fa-examples-alt", 100);
    }


}
