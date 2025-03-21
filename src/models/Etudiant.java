/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author Na
 */
public class Etudiant {
   private String numero;
    private String nom;
    private String adresse;
    private double bourse;

    public Etudiant(String numero, String nom, String adresse, double bourse) {
        this.numero = numero;
        this.nom = nom;
        this.adresse = adresse;
        this.bourse = bourse;
    }

    // Getters et Setters
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public double getBourse() { return bourse; }
    public void setBourse(double bourse) { this.bourse = bourse; }

    @Override
    public String toString() {
        return numero + ", " + nom + ", " + adresse + ", " + bourse;
    } 
}
