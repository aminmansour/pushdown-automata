package controller;


import javafx.fxml.Initializable;
import model.Definition;
import model.ModelFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ExamplesController implements Initializable {


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


//    public void load(ActionEvent actionEvent) {
//        Definition definition = retrieveDefinitionInstance((String) items.get(lvLibrary.getSelectionModel().getSelectedIndex()));
//        PDAMachine model = new PDAMachine(definition);
//        model.markAsSavedInMemory();
//        ControllerFactory.pdaRunnerController.setModel(model);
//        ViewFactory.globalPane.setCenter(ViewFactory.pdaRunner);
//        ControllerFactory.pdaRunnerController.stop();
//        BorderPane.setAlignment(ViewFactory.pdaRunner, Pos.CENTER);
//    }

    private Definition retrieveDefinitionInstance(String id) {
        for (Definition definition : ModelFactory.definitions) {
            if (definition.getIdentifier().equals(id)) {
                return definition;
            }
        }
        return null;
    }


}
