package logic;

import gui.MainMenu; 
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainMenu(); // ¡Ahora iniciamos por el menú!
        });
    }
}