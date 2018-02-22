package controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import model.Definition;
import model.Memory;
import model.ModelFactory;
import model.PDAMachine;
import view.ViewFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class LibraryController implements Initializable {

    @FXML
    private BorderPane library;
    @FXML
    private ListView lvLibrary;
    @FXML
    private HBox hbItemAction;
    @FXML
    private Button bDelete;
    @FXML
    private Button bLoad;
    private ObservableList<Object> items;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvLibrary.getSelectionModel().selectedItemProperty().addListener(observable -> {
            hbItemAction.setVisible(true);
        });
    }

    public void retrieveDefinitionStore() {

        ModelFactory.definitions = Memory.load();
        items = FXCollections.observableArrayList();
        for (Definition definition : ModelFactory.definitions) {
            items.add(definition.getIdentifier());
        }
        lvLibrary.setItems(items);

    }

    public void load(ActionEvent actionEvent) {
        Definition definition = retrieveDefinitionInstance((String) items.get(lvLibrary.getSelectionModel().getSelectedIndex()));
        PDAMachine model = new PDAMachine(definition);
        model.markAsSavedInMemory();
        ControllerFactory.pdaRunnerController.setModel(model);
        ViewFactory.globalPane.setCenter(ViewFactory.pdaRunner);
        ControllerFactory.pdaRunnerController.stop();
        BorderPane.setAlignment(ViewFactory.pdaRunner, Pos.CENTER);
    }

    private Definition retrieveDefinitionInstance(String id) {
        for (Definition definition : ModelFactory.definitions) {
            if (definition.getIdentifier().equals(id)) {
                return definition;
            }
        }
        return null;
    }

    public void delete(ActionEvent actionEvent) {
        int selectedIndex = lvLibrary.getSelectionModel().getSelectedIndex();
        ModelFactory.definitions.remove(selectedIndex);
        items.remove(selectedIndex);
        Memory.saveState();
    }
}
