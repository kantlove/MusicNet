package musicnet.util;

import javafx.fxml.FXMLLoader;
import musicnet.controller.BaseController;

/**
 * Created by Quan on 12/2/2015.
 */

public class FXMLLoaderEx extends FXMLLoader {
    public BaseController getController() {
        return super.getController();
    }
}
