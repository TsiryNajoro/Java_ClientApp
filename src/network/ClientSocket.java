package network;

import java.awt.Color;
import client.NetworkChecker;
import java.io.File;
import javax.swing.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ClientSocket {
    private final String host;
    private final int port;
    private final JLabel lblConnexion;
    private Socket socket;
    private ObjectOutputStream outputStream;

    // Paramètres de la base de données PostgreSQL pour la vérification de la connexion


    public ClientSocket(String host, int port, JLabel lblConnexion) {
        this.host = host;
        this.port = port;
        this.lblConnexion = lblConnexion;
        verifierConnexion();
    }

    private void verifierConnexion() {
    // Vérification de la connexion au serveur via le socket
    if (NetworkChecker.isNetworkAvailable(host, port)) {
        try {
            socket = new Socket(host, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            lblConnexion.setText("Statut : Connecté au serveur");
            lblConnexion.setForeground(Color.GREEN);
        } catch (IOException e) {
            lblConnexion.setText("Statut : Hors ligne");
            lblConnexion.setForeground(Color.RED);
            socket = null;
        }
    } else {
        lblConnexion.setText("Statut : Hors ligne (Serveur)");
        lblConnexion.setForeground(Color.RED);
    }
}

    public void supprimerElementEnvoye(File file) {
    try {
        // Charger le fichier XML existant
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(file);
        Element rootElement = document.getDocumentElement();

        // Supprimer les éléments envoyés
        NodeList etudiants = rootElement.getElementsByTagName("Etudiant");
        for (int i = 0; i < etudiants.getLength(); i++) {
            Element etudiantElement = (Element) etudiants.item(i);
            // Ajouter ici une condition pour vérifier si cet étudiant a bien été envoyé au serveur
            // Par exemple, si tu as une manière de savoir quel étudiant a été envoyé
            // (par exemple, via un attribut "envoye" ou en utilisant une base de données côté serveur)
            boolean envoyé = true;  // Remplace cette condition par un test réel
            if (envoyé) {
                rootElement.removeChild(etudiantElement);
            }
        }

        // Écrire à nouveau le fichier XML sans les éléments envoyés
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);

        System.out.println("Éléments envoyés supprimés du fichier XML.");

    } catch (IOException | IllegalArgumentException | ParserConfigurationException | TransformerException | DOMException | SAXException e) {
    }
}

    public void envoyerFichierXML(String action) {
        try {
            // Vérifier si le fichier existe
            File file = new File("pending_student.xml");
            if (!file.exists()) {
                System.out.println("Le fichier XML n'existe pas.");
                return;
            }
            // Lire le contenu du fichier XML
            Path path = file.toPath();
            byte[] fileBytes = Files.readAllBytes(path);

            // Établir la connexion avec le serveur
            socket = new Socket(host, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());

            // Envoyer une indication sur l'action
            outputStream.writeUTF(action);
            outputStream.flush();

            // Envoyer la taille du fichier pour gestion côté serveur
            outputStream.writeInt(fileBytes.length);
            outputStream.flush();

            // Envoyer le fichier XML
            outputStream.write(fileBytes);
            outputStream.flush();

            System.out.println("Fichier XML envoyé au serveur avec action : " + action);
            System.out.println("Envoi du fichier XML : " + new String(fileBytes));
        } catch (IOException e) {
            System.out.println("Erreur lors de l'envoi du fichier XML : " + e.getMessage());
        } finally {
            try {
                if (outputStream != null) outputStream.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Erreur lors de la fermeture des flux : " + e.getMessage());
            }
        }
    }

}
