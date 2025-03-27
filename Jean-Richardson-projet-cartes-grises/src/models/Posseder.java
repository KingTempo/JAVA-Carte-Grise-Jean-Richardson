package models;

import java.sql.Date;

public class Posseder {
    private int idProprietaire; // Identifiant du propriétaire
    private int idVehicule; // Identifiant du véhicule
    private String nom; // Nom du propriétaire
    private String prenom; // Prénom du propriétaire
    private String matricule; // Matricule du véhicule
    private Date dateDebutPropriete; // Date de début de propriété
    private Date dateFinPropriete; // Date de fin de propriété

    // Constructeur
    public Posseder(int idProprietaire, int idVehicule, String nom, String prenom, String matricule, Date dateDebutPropriete, Date dateFinPropriete) {
        this.idProprietaire = idProprietaire;
        this.idVehicule = idVehicule;
        this.nom = nom;
        this.prenom = prenom;
        this.matricule = matricule;
        this.dateDebutPropriete = dateDebutPropriete;
        this.dateFinPropriete = dateFinPropriete;
    }

    // Getters et Setters
    public int getIdProprietaire() {
        return idProprietaire;
    }

    public void setIdProprietaire(int idProprietaire) {
        this.idProprietaire = idProprietaire;
    }

    public int getIdVehicule() {
        return idVehicule;
    }

    public void setIdVehicule(int idVehicule) {
        this.idVehicule = idVehicule;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getMatricule() {
        return matricule;
    }

    public Date getDateDebutPropriete() {
        return dateDebutPropriete;
    }

    public void setDateDebutPropriete(Date dateDebutPropriete) {
        this.dateDebutPropriete = dateDebutPropriete;
    }

    public Date getDateFinPropriete() {
        return dateFinPropriete;
    }

    public void setDateFinPropriete(Date dateFinPropriete) {
        this.dateFinPropriete = dateFinPropriete;
    }
}