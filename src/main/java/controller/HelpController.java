package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class HelpController implements Initializable {

    @FXML
    private WebView pdfViewer;
    private String creationHelpPath;
    private String runModeHelpPath;
    private String miscHelpPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        creationHelpPath = "src/main/resources/storage/creation_help/acccb3ec-20d1-11e8-b174-0cc47a792c0a_id_acccb3ec-20d1-11e8-b174-0cc47a792c0a.html";
        runModeHelpPath = "src/main/resources/storage/run_mode_help/4b1086d6-20d3-11e8-b174-0cc47a792c0a_id_4b1086d6-20d3-11e8-b174-0cc47a792c0a.html";
        miscHelpPath = "src/main/resources/storage/misc_help/7a672abe-20d0-11e8-b174-0cc47a792c0a_id_7a672abe-20d0-11e8-b174-0cc47a792c0a.html";
    }


    public void openCreationHelp(MouseEvent mouseEvent) {
        loadFileToWebView(creationHelpPath);
    }


    public void openRunHelp(MouseEvent mouseEvent) {
        loadFileToWebView(runModeHelpPath);
    }


    public void openMiscHelp(MouseEvent mouseEvent) {
        loadFileToWebView(miscHelpPath);
    }

    private void loadFileToWebView(String url) {
        File file = new File(url);
        pdfViewer.setZoom(2);
        WebEngine webEngine = pdfViewer.getEngine();
        webEngine.load(file.toURI().toString());
    }
}
