package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import view.WebDocumentListener;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * A Controller for controlling the help's view
 */
public class HelpController implements Initializable {

    @FXML
    private WebView pdfViewer;
    private final String CREATION_HELP_PATH = "/storage/creation_help/acccb3ec-20d1-11e8-b174-0cc47a792c0a_id_acccb3ec-20d1-11e8-b174-0cc47a792c0a.html";
    private String RUN_MODE_HELP_PATH = "/storage/run_mode_help/4b1086d6-20d3-11e8-b174-0cc47a792c0a_id_4b1086d6-20d3-11e8-b174-0cc47a792c0a.html";
    private String MISC_HELP_PATH = "/storage/misc_help/7a672abe-20d0-11e8-b174-0cc47a792c0a_id_7a672abe-20d0-11e8-b174-0cc47a792c0a.html";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WebEngine webEngine = pdfViewer.getEngine();
        webEngine.documentProperty().addListener(new WebDocumentListener(webEngine));
        pdfViewer.setVisible(false);
    }


    /**
     * A method which opens up the manual covering PDA creation
     */
    public void openCreationHelp() {
        loadFileToWebView(CREATION_HELP_PATH);
    }


    /**
     * A method which opens up the manual covering PDA running
     */
    public void openRunHelp() {
        loadFileToWebView(RUN_MODE_HELP_PATH);
    }


    /**
     * A method which opens up the manual covering Misc features
     */
    public void openMiscHelp() {
        loadFileToWebView(MISC_HELP_PATH);
    }

    //loads document associated with url provided to WebView
    private void loadFileToWebView(String url) {
        try {
            pdfViewer.setZoom(2);
            pdfViewer.setVisible(true);
            pdfViewer.getEngine().load(getClass().getResource(url).toURI().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
