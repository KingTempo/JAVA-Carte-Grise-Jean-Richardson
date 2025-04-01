package views;

import controllers.PossederController;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import javax.swing.*;
import models.Posseder;

public class PossederView extends JFrame {
    private PossederController controller;

    public PossederView() {
        controller = new PossederController();
        setTitle("Gestion des Propriétés");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Charger les propriétés
        List<Posseder> proprietes = controller.getAllProprietes();
        for (Posseder propriete : proprietes) {
            JPanel proprietesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel proprietesLabel = new JLabel(
                    "Propriétaire: " + propriete.getPrenom() + " " + propriete.getNom() +
                            ", Véhicule: " + propriete.getMatricule() +
                            " , Date de début : " + propriete.getDateDebutPropriete() +
                            " , Date de fin : "
                            + (propriete.getDateFinPropriete() != null ? propriete.getDateFinPropriete() : "Actuel"));

            // Bouton "Modifier"
            JButton modifyButton = new JButton("Modifier la relation");
            modifyButton.addActionListener(e -> {
                // Création des champs pour les nouvelles valeurs
                JTextField nomProprietaireField = new JTextField(propriete.getNom());
                JTextField prenomProprietaireField = new JTextField(propriete.getPrenom());
                JTextField matriculeVehiculeField = new JTextField(propriete.getMatricule());
                JTextField dateDebutField = new JTextField(propriete.getDateDebutPropriete().toString());
                JTextField dateFinField = new JTextField(
                        propriete.getDateFinPropriete() != null ? propriete.getDateFinPropriete().toString() : "");

                Object[] message = {
                        "Nom du propriétaire :", nomProprietaireField,
                        "Prénom du propriétaire :", prenomProprietaireField,
                        "Matricule du véhicule :", matriculeVehiculeField,
                        "Date de début (YYYY-MM-DD) :", dateDebutField,
                        "Date de fin (YYYY-MM-DD) :", dateFinField
                };

                int option = JOptionPane.showConfirmDialog(this, message, "Modifier la relation",
                        JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        // On récupère les nouvelles valeurs
                        String newNomProprietaire = nomProprietaireField.getText();
                        String newPrenomProprietaire = prenomProprietaireField.getText();
                        String newMatriculeVehicule = matriculeVehiculeField.getText();
                        Date newDateDebut = Date.valueOf(dateDebutField.getText());
                        Date newDateFin = dateFinField.getText().isEmpty() ? null
                                : Date.valueOf(dateFinField.getText());

                        // Appel à la méthode de mise à jour
                        controller.updatePropriete(propriete.getIdProprietaire(), propriete.getIdVehicule(),
                                newNomProprietaire, newPrenomProprietaire, newMatriculeVehicule, newDateDebut,
                                newDateFin);

                        // Actualisation de la vue
                        refreshView();
                    } catch (Exception ex) {
                        showErrorMessage("Format de date invalide ou erreur lors de la mise à jour.");
                    }
                }
            });

            // Bouton "Supprimer"
            JButton deleteButton = new JButton("Supprimer");
            deleteButton.addActionListener(e -> {
                int option = JOptionPane.showConfirmDialog(
                        this,
                        "Êtes-vous sûr de vouloir supprimer cette propriété ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    controller.deletePropriete(propriete.getIdProprietaire(), propriete.getIdVehicule());
                    refreshView();
                }
            });

            proprietesPanel.add(proprietesLabel);
            proprietesPanel.add(modifyButton);
            proprietesPanel.add(deleteButton);
            panel.add(proprietesPanel);
        }

        // Créer un JScrollPane pour la liste des propriétés
        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane, BorderLayout.CENTER);

        // Panel d'action pour les boutons
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Centrer les boutons

        // Bouton d'ajout
        JButton addButton = new JButton("Ajouter une propriété");
        addButton.addActionListener(e -> {
            JTextField nomField = new JTextField();
            JTextField prenomField = new JTextField();
            JTextField matriculeField = new JTextField();
            JTextField dateDebutField = new JTextField();
            JTextField dateFinField = new JTextField();

            Object[] message = {
                    "Nom du propriétaire :", nomField,
                    "Prénom du propriétaire :", prenomField,
                    "Matricule du véhicule :", matriculeField,
                    "Date début :", dateDebutField,
                    "Date fin :", dateFinField,
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Ajouter une Propriété",
                    JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    // Convertir les champs textuels en données de type approprié
                    String nom = nomField.getText();
                    String prenom = prenomField.getText();
                    String matricule = matriculeField.getText();
                    Date dateDebut = Date.valueOf(dateDebutField.getText());
                    // Appel au contrôleur pour ajouter la propriété
                    controller.addPropriete(nom, prenom, matricule, dateDebut);
                    refreshView();
                } catch (Exception ex) {
                    showErrorMessage("Erreur dans les données fournies.");
                }
            }
        });

        // Bouton Retour
        JButton backButton = new JButton("Retour");
        backButton.addActionListener(e -> dispose());

        // Ajouter les boutons au panel d'action
        actionPanel.add(addButton);
        actionPanel.add(backButton);

        // Ajouter le panel d'action en bas de la fenêtre
        add(actionPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Méthode pour rafraîchir l'affichage
    private void refreshView() {
        dispose();
        new PossederView();
    }

    // Afficher un message d'erreur
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
