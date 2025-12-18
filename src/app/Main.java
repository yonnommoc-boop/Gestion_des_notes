package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.table.*;

public class GradeManagerGUI extends JFrame {
    private GradeManager manager = new GradeManager();
    private JTextField matriculeField = new JTextField(12);
    private JButton validerBtn = new JButton("Valider");
    private JTextArea outputArea = new JTextArea(18, 48);

    // Shared variables for methods
    private JPanel header;
    private JButton menuBtn;
    private JPopupMenu popup;
    private DateTimeFormatter dtf;
    private JPanel center;
    private JPanel inputPanel;
    private JLabel opLabel;
    private JScrollPane scroll;
    private JPanel bottomFrame;

    public GradeManagerGUI() {
        super("Gestion des notes des élèves");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8,8));

        // Initialize shared variables
        dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Call the 5 methods in order
        initFrontView();
        initStudentInformation();
        initStudentMarksAndModifications();
        initStatistics();
        initParametersAndMiscellaneous();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Method 1: Front View - Sets up the GUI layout, header, menu button, popup menu structure, and center panel
    private void initFrontView() {
        // Header with title (orange theme)
        Color ORANGE = new Color(255, 140, 0); // dark orange
        Color VIOLET = new Color(148, 0, 211); // (previous) violet, kept for compatibility
        // violet léger, pas trop foncé (plus clair)
        Color LIGHT_VIOLET = new Color(200, 160, 255);
        header = new JPanel(new BorderLayout());
        // mettre l'en-tête en orange (cadre)
        header.setBackground(ORANGE);
        header.setBorder(new EmptyBorder(10, 12, 10, 12));
        JLabel title = new JLabel("Gestion des notes des étudiants de 2025-2026", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        // create a small orange circular icon to the left of the title
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(28, 28, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(ORANGE);
        g2.fillOval(2,2,24,24);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(2,2,24,24);
        g2.dispose();
        ImageIcon icon = new ImageIcon(img);
        title.setIcon(icon);
        title.setIconTextGap(12);
        header.add(title, BorderLayout.CENTER);

        // --- Bouton hamburger (trois traits) en haut à droite ---
        menuBtn = new JButton();
        menuBtn.setOpaque(false);
        menuBtn.setContentAreaFilled(false);
        menuBtn.setBorderPainted(false);
        menuBtn.setFocusPainted(false);
        // créer une icône simple 28x20 avec 3 traits blancs
        java.awt.image.BufferedImage hamImg = new java.awt.image.BufferedImage(28, 20, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D gH = hamImg.createGraphics();
        gH.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        gH.setColor(Color.WHITE);
        gH.fillRoundRect(2,2,24,3,3,3);
        gH.fillRoundRect(2,8,24,3,3,3);
        gH.fillRoundRect(2,14,24,3,3,3);
        gH.dispose();
        menuBtn.setIcon(new ImageIcon(hamImg));

        // créer le menu contextuel avec sous-menus
        popup = new JPopupMenu();

        // 1) Gérer les étudiants
        JMenu gererEtudiants = new JMenu("Gérer les étudiants");
        JMenuItem listStudentsItem = new JMenuItem("Liste des étudiants");
        JMenuItem addStudentItem = new JMenuItem("Ajouter un étudiant");
        JMenuItem removeModifyItem = new JMenuItem("Supprimer / Modifier");
        gererEtudiants.add(listStudentsItem);
        gererEtudiants.add(addStudentItem);
        gererEtudiants.add(removeModifyItem);

        // 2) Gestion des notes
        JMenu gestionNotes = new JMenu("Gestion des notes");
        JMenuItem saisirNotesItem = new JMenuItem("Saisir les notes");
        JMenuItem modifierNotesItem = new JMenuItem("Modifier les notes");
        JMenuItem bulletinEtudiantItem = new JMenuItem("Bulletin d'un étudiant");
        JMenuItem bulletinClasseItem = new JMenuItem("Bulletin de toute la classe");
        gestionNotes.add(saisirNotesItem);
        gestionNotes.add(modifierNotesItem);
        gestionNotes.add(bulletinEtudiantItem);
        gestionNotes.add(bulletinClasseItem);

        // 3) Statistiques
        JMenu statistiques = new JMenu("Statistiques");
        JMenuItem moyenneGenItem = new JMenuItem("Moyenne générale");
        JMenuItem moyenneParMatItem = new JMenuItem("Moyenne par matière");
        JMenuItem classementItem = new JMenuItem("Classement des étudiants");
        statistiques.add(moyenneGenItem);
        statistiques.add(moyenneParMatItem);
        statistiques.add(classementItem);

        // 4) Paramètres
        JMenu parametres = new JMenu("Paramètres");
        JMenuItem saveItem = new JMenuItem("Sauvegarder les données");
        JMenuItem exportPdfItem = new JMenuItem("Exporter PDF / TXT");
        JMenuItem changeThemeItem = new JMenuItem("Changer le thème");
        parametres.add(saveItem);
        parametres.add(exportPdfItem);
        parametres.add(changeThemeItem);

        popup.add(gererEtudiants);
        popup.add(gestionNotes);
        popup.add(statistiques);
        popup.add(parametres);

        // montrer le menu au clic du bouton
        menuBtn.addActionListener(ev -> popup.show(menuBtn, 0, menuBtn.getHeight()));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(menuBtn);
        header.add(right, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
        // add orange border around the window
        getRootPane().setBorder(BorderFactory.createLineBorder(ORANGE, 6));

        // Center panel with input and output
        center = new JPanel(new BorderLayout(10,10));
        center.setBorder(new EmptyBorder(12,12,12,12));
        // backend blanc
        center.setBackground(Color.WHITE);
        // also set the frame content background to blanc
        getContentPane().setBackground(Color.WHITE);

        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        // enlever le grand cadre : rendre le panneau d'entrée blanc (pas de cadre coloré)
        inputPanel.setBackground(Color.WHITE);

        // Label au-dessus du bouton (violet clair) — plus grand mais moins que l'en-tête
        opLabel = new JLabel("OPERATION DE CERTIFICATION", SwingConstants.CENTER);
        opLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        opLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        // afficher le label en noir atténué (gris foncé)
        opLabel.setForeground(new Color(50, 50, 50));

        // Style for button (bouton en violet foncé, texte blanc)
        validerBtn.setBackground(VIOLET);
        validerBtn.setForeground(Color.WHITE);
        validerBtn.setFocusPainted(false);
        validerBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        validerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        inputPanel.add(opLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(0,6)));
        inputPanel.add(validerBtn);

        center.add(inputPanel, BorderLayout.NORTH);

        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.BOLD, 12));
        // texte noir, fond blanc
        outputArea.setBackground(Color.WHITE);
        outputArea.setForeground(Color.BLACK);
        outputArea.setBorder(BorderFactory.createLineBorder(ORANGE));
        scroll = new JScrollPane(outputArea);
        center.add(scroll, BorderLayout.CENTER);

        // Ajout d'un cadre violet clair en dessous (cadre visuel)
        bottomFrame = new JPanel();
        bottomFrame.setPreferredSize(new Dimension(0, 64));
        bottomFrame.setBackground(ORANGE);
        center.add(bottomFrame, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);
    }

    // Method 2: Student Information - Adds action listeners for managing students (list, add, remove/modify)
    private void initStudentInformation() {
        // Find the menu items from the popup (assuming they are added in initFrontView)
        JMenu gererEtudiants = (JMenu) popup.getComponent(0);
        JMenuItem listStudentsItem = (JMenuItem) gererEtudiants.getItem(0);
        JMenuItem addStudentItem = (JMenuItem) gererEtudiants.getItem(1);
        JMenuItem removeModifyItem = (JMenuItem) gererEtudiants.getItem(2);

        // Actions - Gérer les étudiants
        listStudentsItem.addActionListener(ev -> {
            java.util.List<Student> list = manager.listStudents();
            StringBuilder sb = new StringBuilder();
            for (Student s : list) {
                sb.append(s.getId()).append(" - ").append(s.getName()).append(" (Moy: ")
                  .append(String.format("%.2f", s.getAverage())).append(")\n");
            }
            JTextArea ta = new JTextArea(sb.toString());
            ta.setEditable(false);
            ta.setForeground(Color.BLACK);
            ta.setFont(new Font("Monospaced", Font.BOLD, 12));
            JScrollPane sp = new JScrollPane(ta);
            sp.setPreferredSize(new Dimension(420, 260));
            JOptionPane.showMessageDialog(this, sp, "Liste des étudiants", JOptionPane.INFORMATION_MESSAGE);
        });

        addStudentItem.addActionListener(ev -> {
            String name = JOptionPane.showInputDialog(this, "Nom complet de l'étudiant :");
            if (name == null || name.trim().isEmpty()) return;
            String dob = JOptionPane.showInputDialog(this, "Date de naissance (dd/MM/yyyy) :");
            String id = manager.addStudentAuto(name.trim(), dob == null ? null : dob.trim());
            if (id != null) JOptionPane.showMessageDialog(this, "Étudiant ajouté : " + id);
            else JOptionPane.showMessageDialog(this, "Impossible d'ajouter l'étudiant.");
        });

        removeModifyItem.addActionListener(ev -> {
            String id = JOptionPane.showInputDialog(this, "Matricule à supprimer / modifier (ex S001) :");
            if (id == null) return;
            id = id.trim();
            if (!manager.contains(id)) { JOptionPane.showMessageDialog(this, "Aucun étudiant trouvé pour: " + id); return; }
            Object[] options = {"Supprimer", "Modifier", "Annuler"};
            int choice = JOptionPane.showOptionDialog(this, "Que voulez-vous faire pour " + id + " ?", "Supprimer / Modifier",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
            if (choice == 0) {
                boolean ok = manager.removeStudent(id);
                JOptionPane.showMessageDialog(this, ok ? "Étudiant supprimé." : "Échec lors de la suppression.");
            } else if (choice == 1) {
                Student s = manager.getStudent(id);
                String newName = JOptionPane.showInputDialog(this, "Nouveau nom :", s.getName());
                if (newName != null && !newName.trim().isEmpty()) s.setName(newName.trim());
                String newDob = JOptionPane.showInputDialog(this, "Nouvelle date de naissance (dd/MM/yyyy) :", s.getDateOfBirth() == null ? "" : s.getDateOfBirth().format(dtf));
                if (newDob != null && !newDob.trim().isEmpty()) {
                    try { s.setDateOfBirth(LocalDate.parse(newDob.trim(), dtf)); } catch (Exception ex) { /* ignore parse error */ }
                }
                JOptionPane.showMessageDialog(this, "Étudiant modifié.");
            }
        });
    }

    // Method 3: Student Marks and Modifications - Adds action listeners for grade management (enter, modify, view bulletins)
    private void initStudentMarksAndModifications() {
        // Find the menu items from the popup
        JMenu gestionNotes = (JMenu) popup.getComponent(1);
        JMenuItem saisirNotesItem = (JMenuItem) gestionNotes.getItem(0);
        JMenuItem modifierNotesItem = (JMenuItem) gestionNotes.getItem(1);
        JMenuItem bulletinEtudiantItem = (JMenuItem) gestionNotes.getItem(2);
        JMenuItem bulletinClasseItem = (JMenuItem) gestionNotes.getItem(3);

        // Actions - Gestion des notes
        saisirNotesItem.addActionListener(ev -> {
            String id = JOptionPane.showInputDialog(this, "Matricule pour saisir les notes :");
            if (id == null) return; id = id.trim();
            Student s = manager.getStudent(id);
            if (s == null) { JOptionPane.showMessageDialog(this, "Aucun étudiant pour: " + id); return; }
            for (String subj : Student.SUBJECTS) {
                Double cur = s.getGrade(subj);
                String entered = JOptionPane.showInputDialog(this, "Note pour " + subj + " :", cur == null ? "" : cur.toString());
                if (entered == null) continue; // skip
                try {
                    double val = Double.parseDouble(entered.trim());
                    manager.setGradeForSubject(id, subj, val);
                } catch (Exception ex) {
                    // ignore invalid
                }
            }
            JOptionPane.showMessageDialog(this, "Saisie terminée.");
        });

        modifierNotesItem.addActionListener(ev -> {
            String id = JOptionPane.showInputDialog(this, "Matricule pour modifier une note :");
            if (id == null) return; id = id.trim();
            Student s = manager.getStudent(id);
            if (s == null) { JOptionPane.showMessageDialog(this, "Aucun étudiant pour: " + id); return; }
            String subj = (String) JOptionPane.showInputDialog(this, "Choisir la matière :", "Matière",
                    JOptionPane.PLAIN_MESSAGE, null, Student.SUBJECTS, Student.SUBJECTS[0]);
            if (subj == null) return;
            Double cur = s.getGrade(subj);
            String entered = JOptionPane.showInputDialog(this, "Nouvelle note pour " + subj + " :", cur == null ? "" : cur.toString());
            if (entered == null) return;
            try { double val = Double.parseDouble(entered.trim()); manager.setGradeForSubject(id, subj, val); JOptionPane.showMessageDialog(this, "Note modifiée."); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Valeur non valide."); }
        });

        bulletinEtudiantItem.addActionListener(ev -> {
            String id = JOptionPane.showInputDialog(this, "Entrez le matricule (ex: S001) :");
            if (id == null) return; id = id.trim();
            if (!manager.contains(id)) { JOptionPane.showMessageDialog(this, "Aucun étudiant trouvé pour: " + id); return; }
            Student s = manager.getStudent(id);
            // Construire un tableau matière / note
            String[] cols = {"Matière", "Note"};
            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            for (String subj : Student.SUBJECTS) {
                Double g = s.getGrade(subj);
                model.addRow(new Object[]{ subj, g == null ? "" : String.format("%.2f", g) });
            }
            model.addRow(new Object[]{ "Moyenne", String.format("%.2f", s.getAverage()) });
            JTable table = new JTable(model);
            // style: texte plus foncé et en gras
            DefaultTableCellRenderer centerR = new DefaultTableCellRenderer();
            centerR.setHorizontalAlignment(SwingConstants.CENTER);
            centerR.setForeground(Color.BLACK);
            centerR.setFont(centerR.getFont().deriveFont(Font.BOLD));
            table.getColumnModel().getColumn(0).setCellRenderer(centerR);
            table.getColumnModel().getColumn(1).setCellRenderer(centerR);
            table.setRowHeight(24);
            JScrollPane sp = new JScrollPane(table);
            sp.setPreferredSize(new Dimension(420, 360));
            JOptionPane.showMessageDialog(this, sp, "Bulletin - " + id + " : " + s.getName(), JOptionPane.PLAIN_MESSAGE);
        });

        bulletinClasseItem.addActionListener(ev -> {
            java.util.List<Student> list = manager.listStudentsSortedByAverage();
            String[] cols = {"Rang", "Matricule", "Nom", "Moyenne", "Action"};
            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            for (Student s : list) {
                int r = manager.getRank(s.getId());
                model.addRow(new Object[]{ String.format("%02d", r), s.getId(), s.getName(), String.format("%.2f", s.getAverage()), "Voir" });
            }
            JTable table = new JTable(model);
            // Style général
            DefaultTableCellRenderer centerR = new DefaultTableCellRenderer();
            centerR.setHorizontalAlignment(SwingConstants.CENTER);
            centerR.setForeground(Color.BLACK);
            centerR.setFont(centerR.getFont().deriveFont(Font.BOLD));
            for (int i=0;i<4;i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerR);
            }
            // Action column centered
            table.getColumnModel().getColumn(4).setCellRenderer(centerR);
                        table.setRowHeight(26);
            // Clique sur la colonne Action pour ouvrir le bulletin détaillé
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    int row = table.rowAtPoint(e.getPoint());
                    int col = table.columnAtPoint(e.getPoint());
                    if (row >= 0 && col == 4) {
                        String id = (String) table.getValueAt(row, 1);
                        Student s = manager.getStudent(id);
                        if (s == null) return;
                        // ouvrir bulletin détaillé (réutiliser la même logique que pour un étudiant)
                        String[] cols2 = {"Matière", "Note"};
                        DefaultTableModel m2 = new DefaultTableModel(cols2, 0) {
                            @Override public boolean isCellEditable(int r2, int c2) { return false; }
                        };
                        for (String subj : Student.SUBJECTS) {
                            Double g = s.getGrade(subj);
                            m2.addRow(new Object[]{ subj, g == null ? "" : String.format("%.2f", g) });
                        }
                        m2.addRow(new Object[]{ "Moyenne", String.format("%.2f", s.getAverage()) });
                        JTable t2 = new JTable(m2);
                        DefaultTableCellRenderer c2 = new DefaultTableCellRenderer();
                        c2.setHorizontalAlignment(SwingConstants.CENTER);
                        c2.setForeground(Color.BLACK);
                        c2.setFont(c2.getFont().deriveFont(Font.BOLD));
                        t2.getColumnModel().getColumn(0).setCellRenderer(c2);
                        t2.getColumnModel().getColumn(1).setCellRenderer(c2);
                        t2.setRowHeight(24);
                        JScrollPane sp2 = new JScrollPane(t2);
                        sp2.setPreferredSize(new Dimension(420, 360));
                        JOptionPane.showMessageDialog(GradeManagerGUI.this, sp2, "Bulletin - " + id + " : " + s.getName(), JOptionPane.PLAIN_MESSAGE);
                    }
                }
            });
            JScrollPane sp = new JScrollPane(table);
            sp.setPreferredSize(new Dimension(640, 420));
            JOptionPane.showMessageDialog(this, sp, "Bulletins - Classe", JOptionPane.PLAIN_MESSAGE);
        });
    }

    // Method 4: Statistics - Adds action listeners for statistical operations (averages, rankings)
    private void initStatistics() {
        // Find the menu items from the popup
        JMenu statistiques = (JMenu) popup.getComponent(2);
        JMenuItem moyenneGenItem = (JMenuItem) statistiques.getItem(0);
        JMenuItem moyenneParMatItem = (JMenuItem) statistiques.getItem(1);
        JMenuItem classementItem = (JMenuItem) statistiques.getItem(2);

        // Actions - Statistiques
        moyenneGenItem.addActionListener(ev -> {
            java.util.List<Student> list = manager.listStudents();
            if (list.isEmpty()) { JOptionPane.showMessageDialog(this, "Aucun étudiant."); return; }
            double sum = 0.0;
            for (Student s : list) sum += s.getAverage();
            double avg = sum / list.size();
            JOptionPane.showMessageDialog(this, String.format("Moyenne générale de la classe: %.2f", avg));
        });

        moyenneParMatItem.addActionListener(ev -> {
            StringBuilder sb = new StringBuilder();
            java.util.List<Student> list = manager.listStudents();
            if (list.isEmpty()) { JOptionPane.showMessageDialog(this, "Aucun étudiant."); return; }
            for (String subj : Student.SUBJECTS) {
                double sum = 0.0; int count = 0;
                for (Student s : list) { Double g = s.getGrade(subj); if (g != null) { sum += g; count++; } }
                double avg = count==0?0.0:sum / count;
                sb.append(String.format("%s : %.2f\n", subj, avg));
            }
            JOptionPane.showMessageDialog(this, new JScrollPane(new JTextArea(sb.toString())), "Moyenne par matière", JOptionPane.INFORMATION_MESSAGE);
        });

        classementItem.addActionListener(ev -> {
            java.util.List<Student> ranked = manager.listStudentsSortedByAverage();
            StringBuilder sb = new StringBuilder();
            for (int i=0;i<ranked.size();i++) {
                Student s = ranked.get(i);
                sb.append(String.format("%02d - %s - %s : %.2f\n", i+1, s.getId(), s.getName(), s.getAverage()));
            }
            JOptionPane.showMessageDialog(this, new JScrollPane(new JTextArea(sb.toString())), "Classement", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    // Method 5: Parameters and Miscellaneous - Adds action listeners for settings (save, export, theme change)
    private void initParametersAndMiscellaneous() {
        // Find the menu items from the popup
        JMenu parametres = (JMenu) popup.getComponent(3);
        JMenuItem saveItem = (JMenuItem) parametres.getItem(0);
        JMenuItem exportPdfItem = (JMenuItem) parametres.getItem(1);
        JMenuItem changeThemeItem = (JMenuItem) parametres.getItem(2);

        // Actions - Paramètres
        saveItem.addActionListener(ev -> {
            File out = new File("students_backup.txt");
            try (PrintWriter pw = new PrintWriter(new FileWriter(out))) {
                for (Student s : manager.listStudents()) {
                    pw.println(manager.getReportCard(s.getId()));
                    pw.println();
                }
                JOptionPane.showMessageDialog(this, "Données sauvegardées dans: " + out.getAbsolutePath());
            } catch (IOException ex) { JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde: " + ex.getMessage()); }
        });

        exportPdfItem.addActionListener(ev -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Enregistrer les bulletins (TXT ou .pdf)");
            int res = fc.showSaveDialog(this);
            if (res != JFileChooser.APPROVE_OPTION) return;
            File f = fc.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                for (Student s : manager.listStudents()) { pw.println(manager.getReportCard(s.getId())); pw.println(); }
                JOptionPane.showMessageDialog(this, "Export terminé: " + f.getAbsolutePath());
            } catch (IOException ex) { JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage()); }
        });

        changeThemeItem.addActionListener(ev -> {
            Object[] options = {"Clair", "Sombre", "Annuler"};
            int c = JOptionPane.showOptionDialog(this, "Choisir un thème :", "Thème",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (c == 0) {
                // clair
                header.setBackground(new Color(255, 140, 0));
                // Assuming title is accessible; in original, it's local, but for refactoring, we can assume it's set
                // For transparency, we'll adjust as needed, but since it's local, this might need adjustment in full code
                // To keep transparent, I'll note that in full implementation, title would be a field or adjusted accordingly
                getContentPane().setBackground(Color.WHITE);
                outputArea.setBackground(Color.WHITE);
                outputArea.setForeground(Color.BLACK);
                validerBtn.setBackground(new Color(148, 0, 211));
                validerBtn.setForeground(Color.WHITE);
            } else if (c == 1) {
                // sombre
                Color dark = new Color(45,45,45);
                header.setBackground(dark);
                getContentPane().setBackground(new Color(60,63,65));
                outputArea.setBackground(new Color(40,40,40));
                outputArea.setForeground(Color.WHITE);
                validerBtn.setBackground(new Color(80, 0, 120));
                validerBtn.setForeground(Color.WHITE);
            }
        });
    }

    private void onValider() {
        // Demander le matricule via une boîte de dialogue (le champ visible a été retiré)
        String id = JOptionPane.showInputDialog(this, "Entrez le matricule (ex: S001):");
        if (id == null) return; // annulation
        id = id.trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Entrez un matricule.");
            return;
        }
        if (!manager.contains(id)) {
            outputArea.setText("Aucun étudiant trouvé pour le matricule: " + id);
            return;
        }
        String report = manager.getReportCard(id);
        outputArea.setText(report);
    }

    private void populateSampleStudents() {
    // Ajout de 10 étudiants avec génération automatique des matricules
    String id1 = manager.addStudentAuto("Aliou Diop", "12/03/2004");
    manager.setGradeForSubject(id1, "physique", 12.5);
    manager.setGradeForSubject(id1, "chimie", 14.0);
    manager.setGradeForSubject(id1, "math", 10.0);
    manager.setGradeForSubject(id1, "francais", 13.5);
    manager.setGradeForSubject(id1, "anglais", 11.0);
    manager.setGradeForSubject(id1, "biologie", 15.0);
    manager.setGradeForSubject(id1, "geographie", 12.0);
    manager.setGradeForSubject(id1, "science", 13.0);

    String id2 = manager.addStudentAuto("Fatou Ndiaye", "05/06/2003");
    manager.setGradeForSubject(id2, "physique", 9.5);
    manager.setGradeForSubject(id2, "chimie", 10.0);
    manager.setGradeForSubject(id2, "math", 8.0);
    manager.setGradeForSubject(id2, "francais", 12.0);
    manager.setGradeForSubject(id2, "anglais", 13.0);
    manager.setGradeForSubject(id2, "biologie", 11.0);
    manager.setGradeForSubject(id2, "geographie", 14.0);
    manager.setGradeForSubject(id2, "science", 10.5);

    String id3 = manager.addStudentAuto("Oumar Ciss", "20/11/2002");
    manager.setGradeForSubject(id3, "physique", 16.0);
    manager.setGradeForSubject(id3, "chimie", 15.5);
    manager.setGradeForSubject(id3, "math", 17.0);
    manager.setGradeForSubject(id3, "francais", 14.0);
    manager.setGradeForSubject(id3, "anglais", 12.5);
    manager.setGradeForSubject(id3, "biologie", 13.0);
    manager.setGradeForSubject(id3, "geographie", 11.0);
    manager.setGradeForSubject(id3, "science", 16.0);

    String id4 = manager.addStudentAuto("Amina Touré", "01/01/2004");
    manager.setGradeForSubject(id4, "physique", 10.0);
    manager.setGradeForSubject(id4, "chimie", 9.0);
    manager.setGradeForSubject(id4, "math", 11.0);
    manager.setGradeForSubject(id4, "francais", 12.5);
    manager.setGradeForSubject(id4, "anglais", 14.0);
    manager.setGradeForSubject(id4, "biologie", 10.5);
    manager.setGradeForSubject(id4, "geographie", 13.0);
    manager.setGradeForSubject(id4, "science", 9.5);

    String id5 = manager.addStudentAuto("Mamadou Ba", "17/08/2003");
    manager.setGradeForSubject(id5, "physique", 14.0);
    manager.setGradeForSubject(id5, "chimie", 13.0);
    manager.setGradeForSubject(id5, "math", 12.0);
    manager.setGradeForSubject(id5, "francais", 11.5);
    manager.setGradeForSubject(id5, "anglais", 10.0);
    manager.setGradeForSubject(id5, "biologie", 12.0);
    manager.setGradeForSubject(id5, "geographie", 12.5);
    manager.setGradeForSubject(id5, "science", 13.5);

    String id6 = manager.addStudentAuto("Hawa Diallo", "30/09/2002");
    manager.setGradeForSubject(id6, "physique", 8.0);
    manager.setGradeForSubject(id6, "chimie", 9.5);
    manager.setGradeForSubject(id6, "math", 10.0);
    manager.setGradeForSubject(id6, "francais", 11.0);
    manager.setGradeForSubject(id6, "anglais", 12.0);
    manager.setGradeForSubject(id6, "biologie", 9.0);
    manager.setGradeForSubject(id6, "geographie", 10.5);
    manager.setGradeForSubject(id6, "science", 8.5);

    String id7 = manager.addStudentAuto("Khadija Sow", "14/02/2004");
    manager.setGradeForSubject(id7, "physique", 13.0);
    manager.setGradeForSubject(id7, "chimie", 12.0);
    manager.setGradeForSubject(id7, "math", 14.5);
    manager.setGradeForSubject(id7, "francais", 15.0);
    manager.setGradeForSubject(id7, "anglais", 13.5);
    manager.setGradeForSubject(id7, "biologie", 12.5);
    manager.setGradeForSubject(id7, "geographie", 14.0);
    manager.setGradeForSubject(id7, "science", 13.0);

    String id8 = manager.addStudentAuto("Abdoulaye Kone", "23/07/2003");
    manager.setGradeForSubject(id8, "physique", 11.0);
    manager.setGradeForSubject(id8, "chimie", 10.5);
    manager.setGradeForSubject(id8, "math", 9.0);
    manager.setGradeForSubject(id8, "francais", 12.0);
    manager.setGradeForSubject(id8, "anglais", 11.5);
    manager.setGradeForSubject(id8, "biologie", 10.0);
    manager.setGradeForSubject(id8, "geographie", 9.5);
    manager.setGradeForSubject(id8, "science", 10.0);

    String id9 = manager.addStudentAuto("Seydou Faye", "09/12/2002");
    manager.setGradeForSubject(id9, "physique", 15.0);
    manager.setGradeForSubject(id9, "chimie", 14.5);
    manager.setGradeForSubject(id9, "math", 16.0);
    manager.setGradeForSubject(id9, "francais", 13.0);
    manager.setGradeForSubject(id9, "anglais", 12.0);
    manager.setGradeForSubject(id9, "biologie", 15.5);
    manager.setGradeForSubject(id9, "geographie", 14.0);
    manager.setGradeForSubject(id9, "science", 15.0);

        String id10 = manager.addStudentAuto("Mariama Konate", "02/04/2004");
    manager.setGradeForSubject(id10, "physique", 10.5);
    manager.setGradeForSubject(id10, "chimie", 11.0);
    manager.setGradeForSubject(id10, "math", 12.0);
    manager.setGradeForSubject(id10, "francais", 12.5);
    manager.setGradeForSubject(id10, "anglais", 13.0);
    manager.setGradeForSubject(id10, "biologie", 11.5);
    manager.setGradeForSubject(id10, "geographie", 12.0);
    manager.setGradeForSubject(id10, "science", 11.0);
        // (liste des étudiants masquée selon demande)
    }

    // refreshStudentList removed because the student list is not displayed

    public static void main(String[] args) {
        // start the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GradeManagerGUI();
            }
        });
    }
