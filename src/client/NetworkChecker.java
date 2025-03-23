/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import network.XMLManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
/**
 *
 * @author Na
 */
public class NetworkChecker {
    private static final String NMAP_PATH = "C:\\Program Files (x86)\\Nmap\\nmap.exe"; // Chemin absolu de Nmap
    private static final int CHECK_INTERVAL = 3000; // Vérification toutes les 3 secondes

    public static void monitorServerStatus(final String ip, final int port, final JLabel lblConnexion) {
        new Thread(() -> {
            while (true) {
                boolean isServerActive = checkServerStatus(ip, port);
                if (isServerActive) {
                   String file = "pending_student.xml";
                   File fileXML = new File("pending_student.xml");
                    
                    lblConnexion.setText("Statut : En ligne");
                    lblConnexion.setForeground(new java.awt.Color(0, 128, 0)); // Vert
                    System.out.println("✅ Le serveur est actif sur " + ip + ":" + port);
                    
                    //verification via fichierContientEtudiants de XMLManager
                    if (XMLManager.fichierContientEtudiants(file)) {
                         JOptionPane.showMessageDialog(null, "Connexion rétablie : Execution des requêtes en attentes...", 
                        "Information", JOptionPane.INFORMATION_MESSAGE);
                         XMLManager.supprimerElementEnvoye(fileXML);
                    }
                    
                } else {
                    lblConnexion.setText("Statut : Hors ligne");
                    lblConnexion.setForeground(new java.awt.Color(255, 0, 0)); // Rouge
                    System.out.println("❌ Le serveur n'est pas disponible.");
                }
                
                try {
                    Thread.sleep(CHECK_INTERVAL); // Attendre avant la prochaine vérification
                } catch (InterruptedException e) {
                    System.err.println("Erreur de pause du thread : " + e.getMessage());
                }
            }
        }).start(); // Démarrer le thread pour surveiller l'état du serveur en arrière-plan
    }

    public static boolean checkServerStatus(String ip, int port) {
        try {
            // Spécifie le chemin absolu de Nmap ici
            Process process = Runtime.getRuntime().exec(NMAP_PATH + " -p " + port + " " + ip);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Vérifier si la ligne contient l'état du port (ouvert ou fermé)
                if (line.contains("open") || line.contains("closed")) {
                    if (line.contains("open")) {
                        return true;  // Si le port est ouvert, le serveur est actif
                    }
                    return false; // Si le port est fermé, le serveur est inactif
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'exécution de nmap : " + e.getMessage());
        }
        return false; // Si aucune information valide n'est trouvée, le serveur est probablement inexistant
    }
}