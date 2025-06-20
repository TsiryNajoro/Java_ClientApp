package models;

import java.io.Serializable;

public class Etudiant implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String numero;
    private String nom;
    private String adresse;
    private double bourse;
    private String action;

    // Constructeur, getters et setters

    public Etudiant(String numero, String nom, String adresse, double bourse, String action) {
        this.numero = numero;
        this.nom = nom;
        this.adresse = adresse;
        this.bourse = bourse;
        this.action = action;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public double getBourse() {
        return bourse;
    }

    public void setBourse(double bourse) {
        this.bourse = bourse;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
