/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import network.XMLManager;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import network.ClientSocket;
/**
 *
 * @author Na
 */
public class NetworkChecker {
    public static boolean isNetworkAvailable(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000); // Timeout de 2 secondes
            return true; // Connexion réussie
        } catch (IOException e) {
            return false; // Pas de connexion
        } 
    }
    // Vérifier la disponibilité du réseau (pour PostgreSQL)
    public static boolean isDatabaseAvailable(String host, int port, String dbName, String user, String password) {
        // URL de la base de données PostgreSQL
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            return true;  // Connexion réussie à PostgreSQL
        } catch (SQLException e) {
            return false;  // Connexion échouée
        }
    }
    
    
        // Méthode pour vérifier régulièrement la connexion au serveur et mettre à jour le statut
    public static void startConnectionChecker(String host, int port, JLabel lblConnexion, ClientSocket clientSocket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean wasOffline = false; // Permet de savoir si on passe d'une connexion perdue à une connexion rétablie
                while (true) {
                    try {
                        if (isNetworkAvailable(host, port)) {
                            lblConnexion.setText("Statut : En ligne");
                            lblConnexion.setForeground(java.awt.Color.GREEN);
                            if (wasOffline) {
                                wasOffline = false;
                                File file = new File("pending_student.xml");
                                // Vérifier si le fichier XML contient des étudiants avant d'envoyer
                                if (XMLManager.fichierContientEtudiants("pending_student.xml")) {
                                    JOptionPane.showMessageDialog(null,
                                            "Connexion rétablie : envoi des données en attente...",
                                            "Information",
                                            JOptionPane.INFORMATION_MESSAGE);
                                    clientSocket.envoyerFichierXML("envoi_pending");
                                    clientSocket.supprimerElementEnvoye(file);
                                }
                            }
                        } else {
                            lblConnexion.setText("Statut : Hors ligne");
                            lblConnexion.setForeground(java.awt.Color.RED);
                            wasOffline = true; // Indiquer que la connexion a été perdue
                        }
                    } catch (HeadlessException e) {
                        lblConnexion.setText("Statut : Hors ligne");
                        lblConnexion.setForeground(java.awt.Color.RED);
                        wasOffline = true;
                    }
                    try {
                        Thread.sleep(2000); // Vérifier toutes les 2 secondes
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }).start();
    }
}
