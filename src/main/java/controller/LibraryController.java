package controller;


import com.sun.org.apache.xpath.internal.operations.Mod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import model.Definition;
import model.MemoryFactory;
import model.ModelFactory;
import model.PDAMachine;
import view.ViewFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A Controller in charge of the Library's view
 */
public class LibraryController implements Initializable {

    @FXML
    private ListView lvLibrary;
    @FXML
    private HBox hbItemAction;
    private ObservableList<Object> items;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvLibrary.getSelectionModel().selectedItemProperty().addListener(observable -> {
            hbItemAction.setVisible(true);
        });
        retrieveDefinitionStore();
    }

    /**
     * A method which looks up the definitions stored in memory and loads
     * them up into the list of definitions in the ListView in which the user can click on.
     */
    public void retrieveDefinitionStore() {
        ModelFactory.libraryStore = MemoryFactory.loadLibrary();
        items = FXCollections.observableArrayList();
        for (Definition definition : ModelFactory.libraryStore) {
            items.add(definition.getIdentifier());
        }
        lvLibrary.setItems(items);
    }

    /**
     * A method which loads a saved PDA definition instance into a new PDAMachine instanc
     */
    public void load() {
        Definition definition = retrieveDefinitionInstance((String) items.get(lvLibrary.getSelectionModel().getSelectedIndex()));
        PDAMachine model = new PDAMachine(definition);
        model.markAsSavedInMemory();
        ControllerFactory.pdaRunnerController.loadPDA(model);
        ViewFactory.globalPane.setCenter(ViewFactory.pdaRunner);
        ControllerFactory.pdaRunnerController.stop();
        BorderPane.setAlignment(ViewFactory.pdaRunner, Pos.CENTER);
    }

    //A method which returns a definition in store if it has the same id
    private Definition retrieveDefinitionInstance(String id) {
        for (Definition definition : ModelFactory.libraryStore) {
            if (definition.getIdentifier().equals(id)) {
                return definition;
            }
        }
        return null;
    }

    /**
     * A method which deletes a definition instance from memory and saves the result
     */
    public void delete() {
        int selectedIndex = lvLibrary.getSelectionModel().getSelectedIndex();
        ModelFactory.libraryStore.remove(selectedIndex);
        items.remove(selectedIndex);
        MemoryFactory.saveState();
        if (ModelFactory.libraryStore.size() == 0) {
            hbItemAction.setVisible(false);
        }
    }
}
