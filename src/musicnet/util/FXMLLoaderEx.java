package musicnet.util;

import javafx.fxml.FXMLLoader;
import musicnet.controller.BaseController;

public class FXMLLoaderEx extends FXMLLoader {
    public BaseController getController() {
        return (BaseController) super.getController();
    }
}
