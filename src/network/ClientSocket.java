package network;

import client.ClientFrame;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import models.Etudiant;

public class ClientSocket {
    private final String host;
    private final int port;
    private Socket socket;
    private ObjectOutputStream outputStream;

    // Paramètres de la base de données PostgreSQL pour la vérification de la connexion


    public ClientSocket(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
public static void envoyerListeEtudiantsAuServeur(List<Etudiant> etudiants, ClientFrame clientFrame, JTable table) {
    try (Socket socket = new Socket("localhost", 12345);
         ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
         ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
        
        // Convertir la liste en chaîne de caractères (format similaire à un seul étudiant)
        for (Etudiant e : etudiants) {
            String etudiantData = "{"
                    + "\"numero\":\"" + e.getNumero() + "\","
                    + "\"nom\":\"" + e.getNom() + "\","
                    + "\"adresse\":\"" + e.getAdresse() + "\","
                    + "\"bourse\":" + e.getBourse() + ","
                    + "\"action\":\"" + e.getAction() + "\""
                    + "}";
            
            // Envoi des données
            outputStream.writeObject(etudiantData);

            // Réception de l'ACK du serveur
            String response = (String) inputStream.readObject();
            System.out.println("Réponse du serveur : " + response);

            if (!"OK".equals(response)) {
                System.err.println("Erreur lors de l'envoi de l'étudiant : " + e.getNumero());
            }
        }

        // Réception de la liste complète des étudiants (comme dans envoyerUnEtudiantAuServeur)
        String jsonArray = (String) inputStream.readObject();
        System.out.println("Liste des étudiants reçue : " + jsonArray);

        // Mise à jour de la table
        List<Etudiant> fullList = parseJsonEtudiants(jsonArray);
        ClientFrame.updateTableData(fullList, table);

    } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
    }
}

    
    public static void envoyerUnEtudiantAuServeur(Etudiant etudiant, JTable table) {
    try (Socket socket = new Socket("localhost", 12345);
         ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
         ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {

        // Convertir l'Etudiant en JSON string
        String etudiantData = "{"
                + "\"numero\":\"" + etudiant.getNumero() + "\","
                + "\"nom\":\"" + etudiant.getNom() + "\","
                + "\"adresse\":\"" + etudiant.getAdresse() + "\","
                + "\"bourse\":" + etudiant.getBourse() + ","
                + "\"action\":\"" + etudiant.getAction() + "\""
                + "}";

        // Envoi des données
        outputStream.writeObject(etudiantData);

        // Réponse du serveur (étape 1)
        String response = (String) inputStream.readObject();
        System.out.println("Réponse du serveur : " + response);

        if ("OK".equals(response)) {
            JOptionPane.showMessageDialog(null, "Enregistrement éfféctué avec succès !",
                    "Information", JOptionPane.INFORMATION_MESSAGE);

            // Étape 2 : réception de la liste complète des étudiants (au format JSON array)
            String jsonArray = (String) inputStream.readObject();
            System.out.println("Liste des étudiants reçue : " + jsonArray);

            // Parser le JSON reçu → Liste d'objets Etudiant
            List<Etudiant> etudiants = parseJsonEtudiants(jsonArray);

            // Mettre à jour la JTable
            ClientFrame.updateTableData(etudiants, table);
        }

    } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
    }
}


    public static List<Etudiant> parseJsonEtudiants(String jsonArray) {
    List<Etudiant> list = new ArrayList<>();
    jsonArray = jsonArray.trim();
    if (jsonArray.startsWith("[") && jsonArray.endsWith("]")) {
        jsonArray = jsonArray.substring(1, jsonArray.length() - 1); // Enlever les crochets

        String[] objets = jsonArray.split("\\},\\{");
        for (String obj : objets) {
            obj = obj.replace("{", "").replace("}", "");
            String[] fields = obj.split(",");
            String numero = "", nom = "", adresse = "";
            double bourse = 0;

            for (String f : fields) {
                String[] kv = f.split(":");
                String key = kv[0].replace("\"", "").trim();
                String value = kv[1].replace("\"", "").trim();

                switch (key) {
                    case "numero":
                        numero = value;
                        break;
                    case "nom":
                        nom = value;
                        break;
                    case "adresse":
                        adresse = value;
                        break;
                    case "bourse":
                        bourse = Double.parseDouble(value);
                        break;
                }
            }

            list.add(new Etudiant(numero, nom, adresse, bourse, "lecture"));
        }
    }
    return list;
}

    
}