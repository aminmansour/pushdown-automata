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
 * A Controller in charge of the Info's view
 */
public class InfoController implements Initializable {

    @FXML
    private WebView pdfViewer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //load info document into webview and display
        try {
            pdfViewer.setZoom(2);
            WebEngine webEngine = pdfViewer.getEngine();
            webEngine.load(getClass().getResource("/storage/info/a28545dc-1ff7-11e8-b174-0cc47a792c0a_id_a28545dc-1ff7-11e8-b174-0cc47a792c0a.html").toURI().toString());
            webEngine.documentProperty().addListener(new WebDocumentListener(webEngine));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
}
