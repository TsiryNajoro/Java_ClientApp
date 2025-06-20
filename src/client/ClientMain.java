package client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class ClientMain {
    public static void main(String[] args) {
        // Lancer le ClientFrame ici
        SwingUtilities.invokeLater(() -> {
            try {
                new ClientFrame().setVisible(true); 
            } catch (IOException ex) {
                Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
