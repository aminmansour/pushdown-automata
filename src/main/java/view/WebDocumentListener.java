package view;

import com.sun.webkit.WebPage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.web.WebEngine;
import org.w3c.dom.Document;

import java.lang.reflect.Field;

/**
 * A web document listener which allows for pdf reader to have a transparent background
 */
public class WebDocumentListener implements ChangeListener<Document> {
    private final WebEngine webEngine;

    /**
     * @param webEngine the associated web engine of the webview this listener instance pertains to
     */
    public WebDocumentListener(WebEngine webEngine) {
        this.webEngine = webEngine;
    }

    @Override
    public void changed(ObservableValue<? extends Document> arg0,
                        Document arg1, Document arg2) {
        try {
            Field f = webEngine.getClass().getDeclaredField("page");
            f.setAccessible(true);
            WebPage page = (WebPage) f.get(webEngine);
            page.setBackgroundColor((new java.awt.Color(0, 0, 0, 0)).getRGB());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
