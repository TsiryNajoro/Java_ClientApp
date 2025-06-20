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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import models.Etudiant;
import network.ClientSocket;
import static network.ClientSocket.parseJsonEtudiants;
/**
 *
 * @author Na
 */
public class NetworkChecker {
    private static final String NMAP_PATH = "C:\\Program Files (x86)\\Nmap\\nmap.exe"; // Chemin absolu de Nmap
    private static final int CHECK_INTERVAL = 3000; // Vérification toutes les 3 secondes

    public static void monitorServerStatus(final String ip, final int port, final JLabel lblConnexion, final ClientFrame clientFrame, final JTable table) {
    new Thread(() -> {
        while (true) {
            boolean isServerActive = checkServerStatus(ip, port);
            if (isServerActive) {
                String file = "pending_student.xml";
                File fileXML = new File(file);
                
                lblConnexion.setText("Statut : En ligne");
                lblConnexion.setForeground(new java.awt.Color(0, 128, 0)); // Vert
                System.out.println("✅ Le serveur est actif sur " + ip + ":" + port);
                
                if (!clientFrame.isRafraichissementActif()) {
                    try {
                        Thread.sleep(CHECK_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue; // skip l'itération
                }
                
                //Reccueil des données
                // 1. Sauvegarde de la sélection actuelle
                int selectedRow = table.getSelectedRow();
                String selectedId = null;
                if (selectedRow != -1) {
                    selectedId = (String) table.getValueAt(selectedRow, 0); // Assure-toi que l'ID est dans la colonne 0
                }
             
                // 2. Reccueil des données
                demanderListeEtudiantsAuServeur(clientFrame, table);

                // 3. Réappliquer la sélection après la mise à jour
                if (selectedId != null) {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (selectedId.equals(table.getValueAt(i, 0))) {
                            table.setRowSelectionInterval(i, i);
                            break;
                        }
                    }
                }

                // Vérification du contenu du fichier XML
                if (XMLManager.fichierContientEtudiants(file)) {             
                    
                    // Récupération de la liste des étudiants
                    List<Etudiant> etudiantsList = XMLManager.extractEtudiantsAvecAction(fileXML);
                    
                    // Envoi au serveur
                     ClientSocket.envoyerListeEtudiantsAuServeur(etudiantsList, clientFrame, clientFrame.getStudentTable());
                    
                    // Vider le fichier XML
                    XMLManager.supprimerElementEnvoye(fileXML);
                    
                    // Notification de requêtes traitées
                    if (!XMLManager.fichierContientEtudiants(file)){
                        JOptionPane.showMessageDialog(null, "Connexion rétablie : Execution des requêtes en attente...",
                            "Information", JOptionPane.INFORMATION_MESSAGE);
                    }
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
    
    public static void demanderListeEtudiantsAuServeur(ClientFrame clientFrame, JTable table) {
    try (Socket socket = new Socket("localhost", 12345);
         ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
         ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {

        // Créer un étudiant "fake" pour demander la liste
        Etudiant fakeEtudiant = new Etudiant("fake", "test", "test", 0.0, "DemandeListe");

        // Convertir l'étudiant "fake" en JSON
        String etudiantData = "{"
                + "\"numero\":\"" + fakeEtudiant.getNumero() + "\","
                + "\"nom\":\"" + fakeEtudiant.getNom() + "\","
                + "\"adresse\":\"" + fakeEtudiant.getAdresse() + "\","
                + "\"bourse\":" + fakeEtudiant.getBourse() + ","
                + "\"action\":\"" + fakeEtudiant.getAction() + "\""
                + "}";

        // Envoyer l'étudiant "fake"
        outputStream.writeObject(etudiantData);

        // Attendre la réponse du serveur
        String response = (String) inputStream.readObject();
        System.out.println("Réponse du serveur : " + response);

        if ("OK".equals(response)) {
            // Réception de la liste des étudiants
            String jsonArray = (String) inputStream.readObject();
            List<Etudiant> etudiants = parseJsonEtudiants(jsonArray);

            // Mettre à jour la JTable
            ClientFrame.updateTableData(etudiants, table);
        }

    } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
    }
}

}