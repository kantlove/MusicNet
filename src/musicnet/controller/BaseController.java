package musicnet.controller;

import musicnet.Main;

public class BaseController {
    private Main main;

    public void setMain(Main main) {
        this.main = main;
    }

    public Main getMain() {
        return main;
    }
}
