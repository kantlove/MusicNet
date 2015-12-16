package musicnet.controller;

import musicnet.Client;
import musicnet.Main;

public class BaseController {
    private Main main;

    public void setMain(Main main) {
        this.main = main;
    }

    public Main getMain() {
        return main;
    }

    public Client getClient() {
        return getMain().client;
    }
}
