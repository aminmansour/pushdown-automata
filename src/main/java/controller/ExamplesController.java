package controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import model.Definition;
import model.Memory;
import model.ModelFactory;
import model.PDAMachine;
import view.ViewFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }




    private Definition retrieveDefinitionInstance(String id) {
        for (Definition definition : ModelFactory.libraryStore) {
            if (definition.getIdentifier().equals(id)) {
                return definition;
            }
        }
        return null;
    }

    public void load(Definition defToLoad) {
        PDAMachine model = new PDAMachine(defToLoad);
        ControllerFactory.pdaRunnerController.setModel(model);

        ViewFactory.globalPane.setCenter(ViewFactory.pdaRunner);
        ControllerFactory.pdaRunnerController.stop();
        BorderPane.setAlignment(ViewFactory.pdaRunner, Pos.CENTER);
    }


    public void alert() {
        ArrayList<Definition> definitions = Memory.loadExamples();
        bExample1.setOnAction(event -> load(definitions.get(4)));
        bExample2.setOnAction(event -> load(definitions.get(0)));
        bExample3.setOnAction(event -> load(definitions.get(1)));
        bExample4.setOnAction(event -> load(definitions.get(3)));
        bExample5.setOnAction(event -> load(definitions.get(2)));
    }
}
