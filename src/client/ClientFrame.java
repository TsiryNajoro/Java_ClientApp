package client;

import models.Etudiant;
import network.ClientSocket;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.XMLManager;
import java.util.List;
import javax.swing.table.TableRowSorter;
import javax.swing.text.PlainDocument;


public class ClientFrame extends javax.swing.JFrame {

    private boolean enEdition = false;

    private final JTextField txtNum;
    private final JTextField txtNom;
    private final JTextField txtAdresse;
    private final JTextField txtBourse;
    private final JButton btnEnvoyer;
    private final JButton btnModifier;
    private final JButton btnSupprimer;
    private final JTable table;
    private final JLabel lblConnexion, lblImage;
    private final DefaultTableModel model;
    private final ClientSocket clientSocket;
    
    private final String host = "localhost";
    private final Integer port = 12345;
    
    
    // Creates new form ClientFrame
    public ClientFrame() throws IOException {
        setTitle("Client Étudiant");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        

        // Panel Formulaire
        JPanel panelForm = new JPanel();
        panelForm.setBorder(BorderFactory.createTitledBorder("Saisie Étudiant"));
        panelForm.setBackground(new Color(240, 240, 240));
        Font formFont = new Font("Segoe UI", Font.PLAIN, 18);
        
        // Image de l'étudiant
        lblImage = new JLabel();
        lblImage.setPreferredSize(new Dimension(200, 190));
        lblImage.setHorizontalAlignment(JLabel.CENTER);
        lblImage.setIcon(new ImageIcon(new ImageIcon("no student.png").getImage().getScaledInstance(200, 190, Image.SCALE_SMOOTH)));

        
        txtNum = new JTextField();
        txtNom = new JTextField();
        txtAdresse = new JTextField();
        txtBourse = new JTextField();

        for (JTextField field : new JTextField[]{txtNum, txtNom, txtAdresse, txtBourse}) {
            field.setFont(formFont);
            field.setMargin(new Insets(5, 10, 5, 10));
        }
        
        JLabel lblNum = new JLabel("Numéro Étudiant:");
        JLabel lblNom = new JLabel("Nom:");
        JLabel lblAdresse = new JLabel("Adresse:");
        JLabel lblBourse = new JLabel("Bourse:");
        
        lblNum.setFont(formFont);
        lblNom.setFont(formFont);
        lblAdresse.setFont(formFont);
        lblBourse.setFont(formFont);

        btnEnvoyer = new JButton("Envoyer");
        btnModifier = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer"); /*jButton*/

        int buttonWidth = 126;
        int buttonHeight = 40;

        for (JButton button : new JButton[]{btnEnvoyer, btnModifier, btnSupprimer}) {
            button.setFont(new Font("Segoe UI", Font.BOLD, 16));
            button.setBackground(new Color(70, 130, 180));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
            button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        }
        
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);

        // Utilisation de GroupLayout pour un alignement plus précis
        GroupLayout layout = new GroupLayout(panelForm);
        panelForm.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Modifier la largeur des TextField pour les rendre plus petites
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblNum)
                            .addComponent(lblNom)
                            .addComponent(lblAdresse)
                            .addComponent(lblBourse))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(txtNum, 175, 250, 400)
                            .addComponent(txtNom, 175, 250, 400)
                            .addComponent(txtAdresse, 175, 250, 400)
                            .addComponent(txtBourse, 175, 250, 400)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnEnvoyer , buttonWidth, buttonWidth, buttonWidth)
                                .addGap(10)
                                .addComponent(btnModifier, buttonWidth, buttonWidth, buttonWidth)
                                .addGap(10)
                                .addComponent(btnSupprimer , buttonWidth, buttonWidth, buttonWidth)))))
                .addGap(30) // espace entre formulaire et image
                .addComponent(lblImage)
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lblNum)
                        .addComponent(txtNum))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lblNom)
                        .addComponent(txtNom))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lblAdresse)
                        .addComponent(txtAdresse))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lblBourse)
                        .addComponent(txtBourse))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(btnEnvoyer)
                        .addComponent(btnModifier)
                        .addComponent(btnSupprimer)))
                .addComponent(lblImage) // aligne en haut
        );
        
        // Tableau
        model = new DefaultTableModel(new String[]{"Numéro", "Nom", "Adresse", "Bourse"}, 0);
        table = new JTable(model);
        
        // Création du TableRowSorter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        
        sorter.setComparator(0, (id1, id2) -> {
            // Extraction des numéros (en supposant que tes IDs sont comme "E1", "E2", etc.)
            int num1 = Integer.parseInt(id1.toString().substring(1));
            int num2 = Integer.parseInt(id2.toString().substring(1));
            return Integer.compare(num1, num2);
        });
        
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        sorter.sort();
        
        JScrollPane scrollPane = new JScrollPane(table);

        // Barre d'état (indicateur réseau)
         JPanel panelStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblConnexion = new JLabel("Statut : Hors ligne");
        lblConnexion.setForeground(Color.RED);
        lblConnexion.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panelStatus.add(lblConnexion);
        panelStatus.setBackground(new Color(240, 240, 240));
        
        clientSocket = new ClientSocket(host, port); // Assurez-vous que lblConnexion est correctement défini

        add(panelForm, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelStatus, BorderLayout.SOUTH);
        
     // Quand l'utilisateur sélectionne une ligne du tableau
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRowView = table.getSelectedRow();
                if (selectedRowView != -1) {
                    int selectedRowModel = table.convertRowIndexToModel(selectedRowView);

                    System.out.println("✅ Ligne sélectionnée (vue) : " + selectedRowView);
                    System.out.println("✅ Ligne sélectionnée (modèle) : " + selectedRowModel);

                    Object num = model.getValueAt(selectedRowModel, 0);
                    Object nom = model.getValueAt(selectedRowModel, 1);
                    Object adresse = model.getValueAt(selectedRowModel, 2);
                    Object bourse = model.getValueAt(selectedRowModel, 3);

                    System.out.println("Valeurs récupérées :");
                    System.out.println("Num : " + num + ", Nom : " + nom + ", Adresse : " + adresse + ", Bourse : " + bourse);

                    if (!enEdition) {
                        txtNum.setText(num.toString());
                        txtNom.setText(nom.toString());
                        txtAdresse.setText(adresse.toString());
                        txtBourse.setText(bourse.toString());

                        System.out.println("✔ Champs mis à jour");
                    }

                    lblImage.setIcon(new ImageIcon(new ImageIcon("student.png").getImage().getScaledInstance(200, 190, Image.SCALE_SMOOTH)));
                    btnModifier.setEnabled(true);
                    btnSupprimer.setEnabled(true);
                    btnEnvoyer.setEnabled(false);
                } else {
                    System.out.println("❌ Aucune ligne sélectionnée");
                    lblImage.setIcon(new ImageIcon(new ImageIcon("no student.png").getImage().getScaledInstance(200, 190, Image.SCALE_SMOOTH)));
                }
            }
        });




        btnEnvoyer.addActionListener((ActionEvent e) -> {
            envoyerDonnees();
        });
        btnModifier.addActionListener((ActionEvent e) -> {
            modifierEtudiant();
        });

        btnSupprimer.addActionListener((ActionEvent e) -> {
            supprimerEtudiant();
        });
              
        // Détecter l'édition dans les champs texte
        txtNum.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                enEdition = true;
            }
        });

        txtNom.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                enEdition = true;
            }
        });

        txtAdresse.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                enEdition = true;
            }
        });

        txtBourse.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                enEdition = true;
            }
        });      
        
    // Déselection et vidage des champs quand on clique ailleurs
    this.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            // Vérifie si le clic est en dehors du tableau et du formulaire
            if (!table.getBounds().contains(evt.getPoint()) && !panelForm.getBounds().contains(evt.getPoint())) {
                deselectionnerEtVider();
            }
        }
    });

    panelForm.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            deselectionnerEtVider();
        }
    });

        
        // Lancer le monitoring du serveur
        NetworkChecker.monitorServerStatus("127.0.0.1", 12345, lblConnexion, this, table);
        setVisible(true);

    }

        private boolean rafraichissementActif = true;

        public void setRafraichissementActif(boolean actif) {
            this.rafraichissementActif = actif;
        }

        public boolean isRafraichissementActif() {
            return this.rafraichissementActif;
        }

    
    
    private void modifierEtudiant() {
        enEdition = false;
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String num = txtNum.getText();
            String nom = txtNom.getText();
            String adresse = txtAdresse.getText();
            double bourse;

            try {
                bourse = Double.parseDouble(txtBourse.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Montant de bourse invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Etudiant etudiant = new Etudiant(num, nom, adresse, bourse, "Modification");

            try {
                boolean serveurActif = NetworkChecker.checkServerStatus("localhost", 12345); // ou IP réelle

                if (serveurActif) {
                    ClientSocket.envoyerUnEtudiantAuServeur(etudiant, table);
                } else {
                    XMLManager.sauvegarderEtudiantHorsLigne(etudiant);
                    JOptionPane.showMessageDialog(null, "Serveur hors ligne. Étudiant enregistré en local.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }

                deselectionnerEtVider();

            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(null, "Erreur lors de la modification de l'étudiant.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerEtudiant() {
        int selectedRow = table.getSelectedRow();

        // Vérifie que la sélection est valide ET dans les limites du modèle
        if (selectedRow != -1 && selectedRow < model.getRowCount()) {
            // Bloque le rafraîchissement pendant la suppression
            setRafraichissementActif(false);

            try {
                String num = (String) model.getValueAt(selectedRow, 0);
                String nom = (String) model.getValueAt(selectedRow, 1);
                String adresse = (String) model.getValueAt(selectedRow, 2);
                double bourse = (double) model.getValueAt(selectedRow, 3);

                Etudiant etudiant = new Etudiant(num, nom, adresse, bourse, "Suppression");

                boolean serveurActif = NetworkChecker.checkServerStatus("localhost", 12345);

                if (serveurActif) {
                    ClientSocket.envoyerUnEtudiantAuServeur(etudiant, table);
                } else {
                    XMLManager.sauvegarderEtudiantHorsLigne(etudiant);
                    JOptionPane.showMessageDialog(null, "Serveur hors ligne. Suppression enregistrée en local.",
                            "Information", JOptionPane.INFORMATION_MESSAGE);
                }

                deselectionnerEtVider();

                // Supprime la ligne du modèle (table)
                //model.removeRow(selectedRow);

            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(null, "Erreur lors de la suppression de l'étudiant.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Relance le rafraîchissement après 1s
                new javax.swing.Timer(1000, e -> setRafraichissementActif(true)).start();
            }

        } else {
            JOptionPane.showMessageDialog(null, "Veuillez sélectionner un étudiant à supprimer.",
                    "Avertissement", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void envoyerDonnees() {
        // Récupérer les données du formulaire
        File file = new File("pending_student.xml");
        String num = txtNum.getText();
        String nom = txtNom.getText();
        String adresse = txtAdresse.getText();
        double bourse = 0;

        try {
            bourse = Double.parseDouble(txtBourse.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Le montant de la bourse n'est pas valide", 
                "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
            return;  // Arrêter la méthode si la saisie est invalide
        }

        // Créer un nouvel objet Etudiant avec les données
        Etudiant etudiant = new Etudiant(num, nom, adresse, bourse, "Ajout");

        // Vérifier l'état du serveur
        if (NetworkChecker.checkServerStatus(host, port)) {
            // Envoi de l'étudiant au serveur
            ClientSocket.envoyerUnEtudiantAuServeur(etudiant, table);
            XMLManager.supprimerElementEnvoye(file);
        } else {
            // Afficher un message si la connexion au serveur est indisponible
            JOptionPane.showMessageDialog(null, "La connexion au serveur est indisponible : la requête sera exécutée quand elle sera rétablie.", 
                "Information", JOptionPane.WARNING_MESSAGE);
            // Sauvegarder l'étudiant dans le fichier XML
            XMLManager.sauvegarderEtudiant(etudiant);
        }
        
       deselectionnerEtVider();
    }


    public static void updateTableData(List<Etudiant> etudiants, JTable table) {
        // Effacer les données existantes dans la table
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        // Ajouter les nouveaux étudiants à la table
        for (Etudiant etudiant : etudiants) {
            model.addRow(new Object[]{
                etudiant.getNumero(),
                etudiant.getNom(),
                etudiant.getAdresse(),
                etudiant.getBourse()
            });
        }
    }

    public JTable getStudentTable() {
        return this.table;
    }

    // Méthode pour désélectionner et vider
    private void deselectionnerEtVider() {
    	 enEdition = false;
        // Déselectionne la ligne
        table.clearSelection();

        // Vide les champs
        txtNom.setDocument(new PlainDocument());
        txtNum.setDocument(new PlainDocument());
        txtAdresse.setDocument(new PlainDocument());
        txtBourse.setDocument(new PlainDocument());

        // Désactive les boutons
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
        btnEnvoyer.setEnabled(true);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new ClientFrame().setVisible(true);
                
            } catch (IOException ex) {
                Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
