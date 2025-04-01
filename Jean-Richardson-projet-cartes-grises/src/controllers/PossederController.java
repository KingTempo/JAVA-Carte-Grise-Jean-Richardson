package controllers;

import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.Posseder;

public class PossederController {

    // Récupérer toutes les propriétés
    public List<Posseder> getAllProprietes() {
        List<Posseder> proprietes = new ArrayList<>();
        String query = "SELECT p.id_proprietaire, p.prenom, p.nom, v.id_vehicule, v.matricule, pos.date_debut_propriete, pos.date_fin_propriete " +
                       "FROM proprietaire p " +
                       "JOIN Posseder pos ON p.id_proprietaire = pos.id_proprietaire " +
                       "JOIN vehicule v ON pos.id_vehicule = v.id_vehicule";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Parcours des résultats de la requête
            while (rs.next()) {
                proprietes.add(new Posseder(
                        rs.getInt("id_proprietaire"),
                        rs.getInt("id_vehicule"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("matricule"),
                        rs.getDate("date_debut_propriete"),
                        rs.getDate("date_fin_propriete")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proprietes;
    }

    // Ajouter une propriété
    public void addPropriete(String nom, String prenom, String matricule, Date dateDebut) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (dateDebut == null) {
                showAlert("Erreur", "La date de début est obligatoire.");
                return;
            }
            // Récupérer l'ID du propriétaire
            int idProprietaire = getProprietaireIdByName(conn, nom, prenom);
            if (idProprietaire == -1) {
                showAlert("Erreur", "Le propriétaire n'existe pas.");
                return;
            }
            // Récupérer l'ID du véhicule
            int idVehicule = getVehiculeIdByMatricule(conn, matricule);
            if (idVehicule == -1) {
                showAlert("Erreur", "Le véhicule n'existe pas.");
                return;
            }

            // Vérifier les doublons
            if (checkDuplicatePropriete(idProprietaire, idVehicule)) {
                showAlert("Erreur", "Cette relation existe déjà.");
                return;
            }

            // Insérer une nouvelle propriété
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO POSSEDER (id_proprietaire, id_vehicule, date_debut_propriete) VALUES (?, ?, ?)")) {
                ps.setInt(1, idProprietaire);
                ps.setInt(2, idVehicule);
                ps.setDate(3, dateDebut);

                int rowsInserted = ps.executeUpdate();
                if (rowsInserted > 0) {
                    showAlert("Succès", "Possession ajoutée avec succès !");
                } else {
                    showAlert("Erreur", "Erreur lors de l'ajout.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de l'ajout de la propriété.");
        }
    }

    // Vérifier les doublons
    private boolean checkDuplicatePropriete(int idProprietaire, int idVehicule) throws SQLException {
        String query = "SELECT COUNT(*) FROM POSSEDER WHERE id_proprietaire = ? AND id_vehicule = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idProprietaire);
            ps.setInt(2, idVehicule);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Modifier une propriété
    public void updatePropriete(int idProprietaire, int idVehicule, String newNomProprietaire, String newPrenomProprietaire, String newMatriculeVehicule, Date newDateDebut, Date newDateFin) {
        // Requête pour mettre à jour les informations du propriétaire
        String updateProprietaireQuery = "UPDATE PROPRIETAIRE SET nom = ?, prenom = ? WHERE id_proprietaire = ?";
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(updateProprietaireQuery)) {
                ps.setString(1, newNomProprietaire);
                ps.setString(2, newPrenomProprietaire);
                ps.setInt(3, idProprietaire);
                ps.executeUpdate();
            }
    
            // Requête pour mettre à jour la relation dans la table POSSEDER
            String updatePossessionQuery = "UPDATE POSSEDER SET date_debut_propriete = ?, date_fin_propriete = ?, id_vehicule = (SELECT id_vehicule FROM VEHICULE WHERE matricule = ?) " +
                    "WHERE id_proprietaire = ? AND id_vehicule = ?";
    
            try (PreparedStatement ps = conn.prepareStatement(updatePossessionQuery)) {
                ps.setDate(1, newDateDebut);
                ps.setDate(2, newDateFin); // La date de fin est optionnelle
                ps.setString(3, newMatriculeVehicule);
                ps.setInt(4, idProprietaire);
                ps.setInt(5, idVehicule);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de la mise à jour de la relation.");
        }
    }

    // Supprimer une propriété
    public void deletePropriete(int idProprietaire, int idVehicule) {
        String query = "DELETE FROM POSSEDER WHERE id_proprietaire = ? AND id_vehicule = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idProprietaire);
            ps.setInt(2, idVehicule);

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                showAlert("Succès", "Possession supprimée avec succès !");
            } else {
                showAlert("Erreur", "Erreur lors de la suppression.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Obtenir l'ID du propriétaire par son nom
    private int getProprietaireIdByName(Connection conn, String nom, String prenom) throws SQLException {
        String query = "SELECT id_proprietaire FROM PROPRIETAIRE WHERE nom = ? AND prenom = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nom);
            ps.setString(2, prenom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_proprietaire");
                }
            }
        }
        return -1;
    }

    // Obtenir l'ID du véhicule par son matricule
    private int getVehiculeIdByMatricule(Connection conn, String matricule) throws SQLException {
        String query = "SELECT id_vehicule FROM VEHICULE WHERE matricule = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, matricule);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_vehicule");
                }
            }
        }
        return -1; // Retourne -1 si aucun véhicule trouvé
    }

    // Afficher une alerte
    private void showAlert(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
