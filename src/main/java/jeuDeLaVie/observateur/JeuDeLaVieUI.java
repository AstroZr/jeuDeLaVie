package jeuDeLaVie.observateur;

import jeuDeLaVie.commande.CommandeMeurt;
import jeuDeLaVie.commande.CommandeVit;
import jeuDeLaVie.modele.Cellule;
import jeuDeLaVie.modele.JeuDeLaVie;
import jeuDeLaVie.visiteur.VisiteurCellule;
import jeuDeLaVie.visiteur.VisiteurClassique;
import jeuDeLaVie.visiteur.VisiteurDayAndNight;
import jeuDeLaVie.visiteur.VisiteurHighLife;
import jeuDeLaVie.visiteur.VisiteurSeeds;
import jeuDeLaVie.visiteur.VisiteurVieSansLaMort;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JeuDeLaVieUI extends JFrame implements JeuObservateur {
    private static final int LARGEUR_ZONE_GRILLE = 800;
    private static final int HAUTEUR_ZONE_GRILLE = 600;
    private static final int LARGEUR_PANNEAU_CONTROLE = 250;
    private static final int TAILLE_MIN_QUADRILLAGE = 9;
    private static final Color[] PALETTE_ROUGE = {
        new Color(255, 245, 245), new Color(255, 220, 220), new Color(255, 195, 195),
        new Color(255, 170, 170), new Color(250, 145, 145), new Color(245, 120, 120),
        new Color(235, 95, 95), new Color(220, 70, 70), new Color(200, 45, 45)
    };
    private final JeuDeLaVie jeu;
    private final JPanel grillePanneau;
    private final Timer timer;
    private final JScrollPane scrollGrille;
    private final JToggleButton boutonMode;
    private final JButton boutonPlayPause;
    private final JButton boutonSuivant;
    private final JLabel infoTaille;
    private final JLabel infoGeneration;
    private int tailleCellule;
    private int delai = 500;
    private boolean enExecution;
    private Boolean etatDessinActif;
    private Point pointDepartDeplacement;
    private Point vueDepartDeplacement;
    private VisiteurCellule visiteurActuel = new VisiteurClassique();

    public JeuDeLaVieUI(JeuDeLaVie jeu) {
        this.jeu = jeu;
        setTitle("Jeu de la Vie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        timer = new Timer(delai, e -> calculerGenerationSuivante());
        calculerTailleCellule();
        grillePanneau = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dessinerGrille(g);
            }
        };
        grillePanneau.setBackground(Color.WHITE);
        grillePanneau.setDoubleBuffered(true);
        mettreAJourTaillePanneau();
        installerInteractionsGrille();
        scrollGrille = new JScrollPane(grillePanneau);
        scrollGrille.setPreferredSize(new Dimension(LARGEUR_ZONE_GRILLE, HAUTEUR_ZONE_GRILLE));
        scrollGrille.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollGrille.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        add(scrollGrille, BorderLayout.CENTER);
        PanneauControleJeu controle = new PanneauControleJeu(
            this::toggleAnimation,
            this::calculerGenerationSuivante,
            this::changerRegle,
            this::setDelai,
            this::activerModeDessin,
            this::desactiverModeDessin,
            this::ajouterBlinker,
            this::ajouterGlider,
            this::ajouterLWSS,
            this::ajouterPulsar,
            this::sauvegarderPattern,
            this::chargerPattern,
            this::afficherDialogueParametres,
            this::resetGrille,
            this::quitter
        );
        boutonMode = controle.getBoutonMode();
        boutonPlayPause = controle.getBoutonPlayPause();
        boutonSuivant = controle.getBoutonSuivant();
        infoTaille = controle.getInfoTaille();
        infoGeneration = controle.getInfoGeneration();
        mettreAJourInformations();
        JScrollPane scrollControle = new JScrollPane(controle);
        scrollControle.setPreferredSize(new Dimension(LARGEUR_PANNEAU_CONTROLE, HAUTEUR_ZONE_GRILLE));
        scrollControle.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollControle.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollControle.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollControle, BorderLayout.EAST);
        setSize(1120, 680);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void installerInteractionsGrille() {
        grillePanneau.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!boutonMode.isSelected()) {
                    if (e.getButton() == MouseEvent.BUTTON1 && peutDeplacerVue()) {
                        pointDepartDeplacement = e.getPoint();
                        vueDepartDeplacement = scrollGrille.getViewport().getViewPosition();
                        grillePanneau.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    }
                    return;
                }
                if (e.getButton() == MouseEvent.BUTTON1) etatDessinActif = true;
                else if (e.getButton() == MouseEvent.BUTTON3) etatDessinActif = false;
                if (etatDessinActif != null) dessinerDepuisPoint(e.getPoint(), etatDessinActif);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                etatDessinActif = null;
                pointDepartDeplacement = null;
                vueDepartDeplacement = null;
                grillePanneau.setCursor(Cursor.getDefaultCursor());
            }
        });
        grillePanneau.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!boutonMode.isSelected() && pointDepartDeplacement != null && vueDepartDeplacement != null) {
                    JViewport viewport = scrollGrille.getViewport();
                    int dx = e.getX() - pointDepartDeplacement.x;
                    int dy = e.getY() - pointDepartDeplacement.y;
                    int nx = vueDepartDeplacement.x - dx;
                    int ny = vueDepartDeplacement.y - dy;
                    int maxX = Math.max(0, grillePanneau.getWidth() - viewport.getWidth());
                    int maxY = Math.max(0, grillePanneau.getHeight() - viewport.getHeight());
                    nx = Math.max(0, Math.min(nx, maxX));
                    ny = Math.max(0, Math.min(ny, maxY));
                    viewport.setViewPosition(new Point(nx, ny));
                    return;
                }
                if (boutonMode.isSelected() && etatDessinActif != null) dessinerDepuisPoint(e.getPoint(), etatDessinActif);
            }
        });
        grillePanneau.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double facteur = 0.85;
                if (e.getWheelRotation() < 0) {
                    facteur = 1.2;
                }
                zoom(e.getPoint(), facteur);
            }
        });
    }

    private boolean peutDeplacerVue() {
        JViewport viewport = scrollGrille.getViewport();
        return grillePanneau.getWidth() > viewport.getWidth() || grillePanneau.getHeight() > viewport.getHeight();
    }

    private void activerModeDessin() {
        arreterAnimationSiActive();
        jeu.viderGrille();
    }
    private void desactiverModeDessin() { jeu.randomiserGrille(); }
    private void setDelai(int valeur) { delai = valeur; timer.setDelay(delai); }
    private void arreterAnimationSiActive() { if (enExecution) toggleAnimation(); }
    private int[] celluleCentreVue() {
        JViewport viewport = scrollGrille.getViewport(); Point vue = viewport.getViewPosition();
        Point centre = new Point(vue.x + viewport.getWidth() / 2, vue.y + viewport.getHeight() / 2);
        int[] cellule = convertirPointEnCellule(centre);
        if (cellule != null) return cellule;
        return new int[] { jeu.getXMax() / 2, jeu.getYMax() / 2 };
    }
    private void ajouterForme(int[][] offsets) {
        int[] centre = celluleCentreVue();
        int xCentre = centre[0], yCentre = centre[1];
        for (int[] offset : offsets) {
            int x = xCentre + offset[0];
            int y = yCentre + offset[1];
            if (x >= 0 && x < jeu.getXMax() && y >= 0 && y < jeu.getYMax()) {
                jeu.ajouteCommande(new CommandeVit(jeu.getGrille(x, y)));
            }
        }
        jeu.executeCommandes();
        jeu.actualiseObservateurs();
    }
    // Formes pré-définies
    private void ajouterBlinker() { ajouterForme(new int[][]{{0, -1}, {0, 0}, {0, 1}}); }
    
    private void ajouterGlider() { ajouterForme(new int[][]{{0, 0}, {1, 0}, {2, 0}, {2, -1}, {1, -2}}); }
    
    private void ajouterLWSS() { ajouterForme(new int[][]{{0, 0}, {3, 0}, {4, 1}, {0, 2}, {4, 2}, {1, 3}, {2, 3}, {3, 3}, {4, 3}}); }
    
    private void ajouterPulsar() { ajouterForme(new int[][]{
        {-4, -6}, {-3, -6}, {-2, -6}, {2, -6}, {3, -6}, {4, -6},
        {-6, -4}, {-1, -4}, {1, -4}, {6, -4},
        {-6, -3}, {-1, -3}, {1, -3}, {6, -3},
        {-6, -2}, {-1, -2}, {1, -2}, {6, -2},
        {-4, -1}, {-3, -1}, {-2, -1}, {2, -1}, {3, -1}, {4, -1},
        {-4, 1}, {-3, 1}, {-2, 1}, {2, 1}, {3, 1}, {4, 1},
        {-6, 2}, {-1, 2}, {1, 2}, {6, 2},
        {-6, 3}, {-1, 3}, {1, 3}, {6, 3},
        {-6, 4}, {-1, 4}, {1, 4}, {6, 4},
        {-4, 6}, {-3, 6}, {-2, 6}, {2, 6}, {3, 6}, {4, 6}
    }); }

    private void calculerTailleCellule() { tailleCellule = Math.max(1, Math.min(LARGEUR_ZONE_GRILLE / jeu.getXMax(), HAUTEUR_ZONE_GRILLE / jeu.getYMax())); }
    private void mettreAJourTaillePanneau() {
        int largeur = Math.max(jeu.getXMax() * tailleCellule, LARGEUR_ZONE_GRILLE);
        int hauteur = Math.max(jeu.getYMax() * tailleCellule, HAUTEUR_ZONE_GRILLE);
        grillePanneau.setPreferredSize(new Dimension(largeur, hauteur));
        grillePanneau.revalidate();
    }
    private Point calculerOffsetGrille() {
        int largeurGrille = jeu.getXMax() * tailleCellule;
        int hauteurGrille = jeu.getYMax() * tailleCellule;
        int offsetX = 0;
        int offsetY = 0;
        if (largeurGrille < LARGEUR_ZONE_GRILLE) offsetX = (LARGEUR_ZONE_GRILLE - largeurGrille) / 2;
        if (hauteurGrille < HAUTEUR_ZONE_GRILLE) offsetY = (HAUTEUR_ZONE_GRILLE - hauteurGrille) / 2;
        return new Point(offsetX, offsetY);
    }
    private int[] convertirPointEnCellule(Point point) {
        Point offset = calculerOffsetGrille();
        int pixelX = point.x - offset.x;
        int pixelY = point.y - offset.y;
        int largeurGrille = jeu.getXMax() * tailleCellule;
        int hauteurGrille = jeu.getYMax() * tailleCellule;
        if (pixelX < 0 || pixelY < 0 || pixelX >= largeurGrille || pixelY >= hauteurGrille) return null;
        return new int[] { pixelX / tailleCellule, pixelY / tailleCellule };
    }
    private void dessinerDepuisPoint(Point point, boolean vivante) {
        int[] coord = convertirPointEnCellule(point);
        if (coord == null) return;
        int x = coord[0], y = coord[1];
        if (jeu.getGrille(x, y).estVivante() == vivante) return;
        if (vivante) jeu.ajouteCommande(new CommandeVit(jeu.getGrille(x, y)));
        else jeu.ajouteCommande(new CommandeMeurt(jeu.getGrille(x, y)));
        jeu.executeCommandes();
        jeu.actualiseObservateurs();
    }
    private Color couleurParVoisins(int voisines) { return PALETTE_ROUGE[Math.max(0, Math.min(8, voisines))]; }

    private void dessinerGrille(Graphics g) {
        Point offset = calculerOffsetGrille();
        Rectangle zoneVisible = g.getClipBounds();
        if (zoneVisible == null) zoneVisible = new Rectangle(0, 0, grillePanneau.getWidth(), grillePanneau.getHeight());
        boolean afficherQuadrillage = tailleCellule >= TAILLE_MIN_QUADRILLAGE;
        int debutX = Math.max(0, (zoneVisible.x - offset.x) / tailleCellule);
        int finX = Math.min(jeu.getXMax() - 1, (zoneVisible.x + zoneVisible.width - offset.x) / tailleCellule);
        int debutY = Math.max(0, (zoneVisible.y - offset.y) / tailleCellule);
        int finY = Math.min(jeu.getYMax() - 1, (zoneVisible.y + zoneVisible.height - offset.y) / tailleCellule);
        for (int x = debutX; x <= finX; x++) {
            for (int y = debutY; y <= finY; y++) {
                int px = offset.x + x * tailleCellule;
                int py = offset.y + y * tailleCellule;
                Cellule cellule = jeu.getGrille(x, y);
                if (cellule.estVivante()) {
                    int voisines = cellule.nombreVoisinesVivantes(jeu);
                    g.setColor(couleurParVoisins(voisines));
                    g.fillRect(px, py, tailleCellule, tailleCellule);
                }
                if (afficherQuadrillage) {
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawRect(px, py, tailleCellule, tailleCellule);
                }
            }
        }
    }
    private void mettreAJourInformations() { infoTaille.setText("Taille: " + jeu.getXMax() + " x " + jeu.getYMax()); infoGeneration.setText("Génération: " + jeu.getNumeroGeneration()); }
    private void rafraichirVue() { grillePanneau.repaint(); mettreAJourInformations(); }
    @Override
    public void actualise() { rafraichirVue(); }
    private void toggleAnimation() {
        if (enExecution) {
            timer.stop();
            enExecution = false;
            boutonPlayPause.setText("> Démarrer");
            boutonSuivant.setEnabled(true);
            return;
        }
        timer.start();
        enExecution = true;
        boutonPlayPause.setText("|| Pause");
        boutonSuivant.setEnabled(false);
    }
    private void calculerGenerationSuivante() { jeu.calculerGenerationSuivante(visiteurActuel); }
    private void changerRegle(String regle) {
        arreterAnimationSiActive();
        switch (regle) {
            case "HighLife": visiteurActuel = new VisiteurHighLife(); break;
            case "Day and Night": visiteurActuel = new VisiteurDayAndNight(); break;
            case "Seeds": visiteurActuel = new VisiteurSeeds(); break;
            case "Vie sans la mort": visiteurActuel = new VisiteurVieSansLaMort(); break;
            default: visiteurActuel = new VisiteurClassique();
        }
    }
    private void zoom(Point pointClic, double facteur) {
        int ancienneTaille = tailleCellule;
        int nouvelleTaille = (int) Math.round(tailleCellule * facteur);
        if (nouvelleTaille == tailleCellule) {
            if (facteur > 1.0) nouvelleTaille = tailleCellule + 1;
            if (facteur < 1.0) nouvelleTaille = tailleCellule - 1;
        }
        nouvelleTaille = Math.max(1, Math.min(50, nouvelleTaille));
        if (nouvelleTaille == ancienneTaille) return;
        JViewport viewport = scrollGrille.getViewport(); Point vue = viewport.getViewPosition();
        int xDansVue = pointClic.x, yDansVue = pointClic.y;
        int xAbsolu = vue.x + xDansVue, yAbsolu = vue.y + yDansVue;
        double ratio = (double) nouvelleTaille / ancienneTaille;
        tailleCellule = nouvelleTaille;
        mettreAJourTaillePanneau();
        int nx = (int) (xAbsolu * ratio) - xDansVue;
        int ny = (int) (yAbsolu * ratio) - yDansVue;
        nx = Math.max(0, Math.min(nx, grillePanneau.getWidth() - viewport.getWidth()));
        ny = Math.max(0, Math.min(ny, grillePanneau.getHeight() - viewport.getHeight()));
        viewport.setViewPosition(new Point(nx, ny));
        grillePanneau.repaint();
    }
    private void afficherDialogueParametres() {
        JPanel panneau = new JPanel(new GridLayout(2, 2, 8, 8));
        JTextField champLargeur = new JTextField(String.valueOf(jeu.getXMax())); JTextField champHauteur = new JTextField(String.valueOf(jeu.getYMax()));
        panneau.add(new JLabel("Largeur :"));
        panneau.add(champLargeur);
        panneau.add(new JLabel("Hauteur :"));
        panneau.add(champHauteur);
        int res = JOptionPane.showConfirmDialog(this, panneau, "Paramètres", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        try {
            int largeur = Integer.parseInt(champLargeur.getText()), hauteur = Integer.parseInt(champHauteur.getText());
            if (largeur <= 0 || hauteur <= 0 || largeur > 500 || hauteur > 500)
                throw new IllegalArgumentException("Valeurs entre 1 et 500.");
            jeu.redimensionnerGrille(largeur, hauteur);
            calculerTailleCellule();
            mettreAJourTaillePanneau();
            rafraichirVue();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nombres invalides.", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    private Path choisirFichierPattern(boolean sauvegarde) {
        JFileChooser chooser = new JFileChooser();
        if (sauvegarde) chooser.setDialogTitle("Sauvegarder un pattern");
        else chooser.setDialogTitle("Charger un pattern");
        chooser.setFileFilter(new FileNameExtensionFilter("Pattern Jeu de la Vie (*.jdlv)", "jdlv"));
        File dossierPatterns = new File("patterns");
        if (!dossierPatterns.exists() && sauvegarde) dossierPatterns.mkdirs();
        if (dossierPatterns.exists()) chooser.setCurrentDirectory(dossierPatterns);
        int resultat;
        if (sauvegarde) resultat = chooser.showSaveDialog(this);
        else resultat = chooser.showOpenDialog(this);
        if (resultat != JFileChooser.APPROVE_OPTION) return null;
        File fichier = chooser.getSelectedFile();
        if (sauvegarde && !fichier.getName().toLowerCase().endsWith(".jdlv"))
            fichier = new File(fichier.getParentFile(), fichier.getName() + ".jdlv");
        return fichier.toPath();
    }
    private void sauvegarderPattern() {
        arreterAnimationSiActive();
        Path chemin = choisirFichierPattern(true);
        if (chemin == null) return;
        try {
            Files.writeString(chemin, jeu.exporterPatternTexte(), StandardCharsets.UTF_8);
            JOptionPane.showMessageDialog(this, "Pattern sauvegardé: " + chemin.getFileName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur de sauvegarde: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void chargerPattern() {
        arreterAnimationSiActive();
        Path chemin = choisirFichierPattern(false);
        if (chemin == null) return;
        try {
            List<String> lignes = Files.readAllLines(chemin, StandardCharsets.UTF_8);
            jeu.importerPatternTexte(lignes);
            calculerTailleCellule();
            mettreAJourTaillePanneau();
            rafraichirVue();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void resetGrille() {
        arreterAnimationSiActive();
        if (boutonMode.isSelected()) { jeu.viderGrille(); return; }
        desactiverModeDessin();
    }
    private void quitter() {
        if (timer.isRunning()) timer.stop();
        dispose();
        System.exit(0);
    }
}
