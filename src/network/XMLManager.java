/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package network;

import models.Etudiant;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
/**
 *
 * @author Na
 */
public class XMLManager {
    private static final String FILE_PATH = "pending_student.xml";
    
        public static void sauvegarderEtudiantHorsLigne(Etudiant etudiant) {
        try {
            File file = new File(FILE_PATH);
            Document document;
            Element rootElement;

            // Vérifier si le fichier existe
            if (file.exists()) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                document = dBuilder.parse(file);
                rootElement = document.getDocumentElement();
            } else {
                // Création d’un nouveau document XML
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                document = dBuilder.newDocument();
                rootElement = document.createElement("Etudiants");
                document.appendChild(rootElement);
            }

            // Créer un élément "Etudiant"
            Element etudiantElement = document.createElement("Etudiant");

            // Ajouter l'action (Ajout, Modification, Suppression)
            Element action = document.createElement("Action");
            action.appendChild(document.createTextNode(etudiant.getAction()));
            etudiantElement.appendChild(action);

            Element num = document.createElement("Numero");
            num.appendChild(document.createTextNode(etudiant.getNumero()));
            etudiantElement.appendChild(num);

            Element nom = document.createElement("Nom");
            nom.appendChild(document.createTextNode(etudiant.getNom()));
            etudiantElement.appendChild(nom);

            Element adresse = document.createElement("Adresse");
            adresse.appendChild(document.createTextNode(etudiant.getAdresse()));
            etudiantElement.appendChild(adresse);

            Element bourse = document.createElement("Bourse");
            bourse.appendChild(document.createTextNode(String.valueOf(etudiant.getBourse())));
            etudiantElement.appendChild(bourse);

            // Ajouter à la racine
            rootElement.appendChild(etudiantElement);

            // Écrire le fichier
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            System.out.println("Etudiant sauvegardé localement avec action : " + etudiant.getAction());

        } catch (IOException | IllegalArgumentException | ParserConfigurationException | TransformerException | DOMException | SAXException e) {
            e.printStackTrace();
        }
    }

    public static void sauvegarderEtudiant(Etudiant etudiant) {
        try {
            File file = new File(FILE_PATH);
            Document document;
            Element rootElement;

            // Vérifier si le fichier existe
            if (file.exists()) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                document = dBuilder.parse(file);
                rootElement = document.getDocumentElement();
            } else {
                // Création d’un nouveau fichier XML
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                document = dBuilder.newDocument();
                rootElement = document.createElement("Etudiants");
                document.appendChild(rootElement);
            }

            // Ajouter un nouvel étudiant
            Element etudiantElement = document.createElement("Etudiant");

            Element action = document.createElement("Action");
            action.appendChild(document.createTextNode("Ajout"));
            etudiantElement.appendChild(action);
            
            Element num = document.createElement("Numero");
            num.appendChild(document.createTextNode(etudiant.getNumero()));
            etudiantElement.appendChild(num);

            Element nom = document.createElement("Nom");
            nom.appendChild(document.createTextNode(etudiant.getNom()));
            etudiantElement.appendChild(nom);

            Element adresse = document.createElement("Adresse");
            adresse.appendChild(document.createTextNode(etudiant.getAdresse()));
            etudiantElement.appendChild(adresse);

            Element bourse = document.createElement("Bourse");
            bourse.appendChild(document.createTextNode(String.valueOf(etudiant.getBourse())));
            etudiantElement.appendChild(bourse);

            rootElement.appendChild(etudiantElement);

            // Écriture dans le fichier
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(FILE_PATH));

            transformer.transform(source, result);

            System.out.println("Données sauvegardées en XML.");
            System.out.println("Sauvegarde de l'étudiant : " + etudiant.getNumero() + ", " + etudiant.getNom() + ", " + etudiant.getAdresse() + ", " + etudiant.getBourse());

        } catch (IOException | IllegalArgumentException | ParserConfigurationException | TransformerException | DOMException | SAXException e) {
            // Affiche l'exception si elle se produit

        }
    }

    public static void supprimerFichierXML() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
            System.out.println("Fichier XML supprimé après envoi réussi.");
        }
    }
    
    /**
     *
     * @param filePath
     * @return
     */
    public static boolean fichierContientEtudiants(String filePath) {
    try {
        File file = new File(filePath);
        if (!file.exists()) return false;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);

        NodeList etudiants = doc.getElementsByTagName("Etudiant");
        return etudiants.getLength() > 0;
    } catch (IOException | ParserConfigurationException | SAXException e) {
        return false;
    }
}
    
    public static void supprimerElementEnvoye(File file) {
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

 // Méthode pour extraire les étudiants et leur action depuis le fichier XML
    public static List<Etudiant> extractEtudiantsAvecAction(File xmlFile) {
        List<Etudiant> etudiantsList = new ArrayList<>();
        
        try {
            // Créer un DocumentBuilder pour analyser le XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            NodeList etudiantsNodes = document.getElementsByTagName("Etudiant");
            
            // Parcours des étudiants dans le XML
            for (int i = 0; i < etudiantsNodes.getLength(); i++) {
                Node etudiantNode = etudiantsNodes.item(i);
                if (etudiantNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element etudiantElement = (Element) etudiantNode;
                    
                    // Extraire les informations de chaque étudiant
                    String numero = etudiantElement.getElementsByTagName("Numero").item(0).getTextContent();
                    String nom = etudiantElement.getElementsByTagName("Nom").item(0).getTextContent();
                    String adresse = etudiantElement.getElementsByTagName("Adresse").item(0).getTextContent();
                    double bourse = Double.parseDouble(etudiantElement.getElementsByTagName("Bourse").item(0).getTextContent());
                    
                    // Extraire l'action (Ajout, Modification, Suppression)
                    String action = etudiantElement.getElementsByTagName("Action").item(0).getTextContent();
                    
                    // Créer un objet Etudiant
                    Etudiant etudiant = new Etudiant(numero, nom, adresse, bourse, action);
                    
                    // Ici, tu peux garder l'action séparée ou l'envoyer avec l'objet Etudiant
                    // Pour l'instant, on ajoute l'étudiant à la liste
                    etudiantsList.add(etudiant);
                    
                    // Si tu veux aussi garder l'action séparée, tu peux créer un objet avec Action et Etudiant
                    // ou simplement inclure l'action dans l'objet Etudiant, selon le besoin
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return etudiantsList;
    }
}
