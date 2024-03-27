package Main;

import View.AnalizadorView;

public class Main {
    public static void main(String[] args) {
        launchAnalizadorView();
    }

    public static void launchAnalizadorView() {
        AnalizadorView analizadorView = new AnalizadorView();
        analizadorView.setVisible(true);
    }
}
