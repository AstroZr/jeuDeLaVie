package jeuDeLaVie.observateur;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class PanneauControleJeu extends JPanel {

    private final JToggleButton boutonMode;
    private final JButton boutonPlayPause;
    private final JButton boutonSuivant;
    private final JLabel infoTaille;
    private final JLabel infoGeneration;

    public PanneauControleJeu(
        Runnable onPlayPause,
        Runnable onSuivant,
        Consumer<String> onRegleChange,
        IntConsumer onDelaiChange,
        Runnable onModeDessinActive,
        Runnable onModeDessinDesactive,
        Runnable onAjouterBlinker,
        Runnable onAjouterGlider,
        Runnable onAjouterLWSS,
        Runnable onAjouterPulsar,
        Runnable onSauvegarder,
        Runnable onCharger,
        Runnable onParametres,
        Runnable onReset,
        Runnable onQuitter
    ) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(creerTitre("Contrôles"));
        add(Box.createRigidArea(new Dimension(0, 10)));

        boutonPlayPause = creerBouton("> Démarrer", onPlayPause);
        boutonSuivant = creerBouton("-> Suivant", onSuivant);

        JPanel sectionAnimation = creerSection("Animation");
        sectionAnimation.add(boutonPlayPause);
        sectionAnimation.add(Box.createRigidArea(new Dimension(0, 5)));
        sectionAnimation.add(boutonSuivant);
        add(sectionAnimation);
        add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel sectionRegles = creerSection("Règles");
        String[] regles = {"Classique", "HighLife", "Day and Night", "Seeds", "Vie sans la mort"};
        JComboBox<String> comboRegles = new JComboBox<>(regles);
        comboRegles.setMaximumSize(new Dimension(200, 28));
        comboRegles.setAlignmentX(Component.CENTER_ALIGNMENT);
        comboRegles.addActionListener(e -> onRegleChange.accept((String) comboRegles.getSelectedItem()));
        sectionRegles.add(comboRegles);
        add(sectionRegles);
        add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel sectionVitesse = creerSection("Vitesse");
        JSlider slider = new JSlider(50, 2000, 500);
        slider.setInverted(true);
        slider.setMajorTickSpacing(500);
        slider.setPaintTicks(true);
        slider.addChangeListener(e -> onDelaiChange.accept(slider.getValue()));

        JLabel label = new JLabel("lent            rapide");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.PLAIN, 10));

        sectionVitesse.add(slider);
        sectionVitesse.add(label);
        add(sectionVitesse);
        add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel sectionEdition = creerSection("Édition");
        boutonMode = new JToggleButton("Passer en mode dessin");
        boutonMode.setAlignmentX(Component.CENTER_ALIGNMENT);
        boutonMode.setMaximumSize(new Dimension(200, 32));
        boutonMode.addActionListener(e -> {
            if (boutonMode.isSelected()) {
                boutonMode.setText("Passer en mode normal");
                onModeDessinActive.run();
            } else {
                boutonMode.setText("Passer en mode dessin");
                onModeDessinDesactive.run();
            }
        });

        sectionEdition.add(boutonMode);
        sectionEdition.add(Box.createRigidArea(new Dimension(0, 4)));
        sectionEdition.add(creerBouton("Sauvegarder", onSauvegarder));
        sectionEdition.add(Box.createRigidArea(new Dimension(0, 4)));
        sectionEdition.add(creerBouton("Charger", onCharger));
        add(sectionEdition);
        add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel sectionFormes = creerSection("Formes");
        sectionFormes.add(creerBouton("Ajouter Blinker", onAjouterBlinker));
        sectionFormes.add(Box.createRigidArea(new Dimension(0, 4)));
        sectionFormes.add(creerBouton("Ajouter Glider", onAjouterGlider));
        sectionFormes.add(Box.createRigidArea(new Dimension(0, 4)));
        sectionFormes.add(creerBouton("Ajouter LWSS", onAjouterLWSS));
        sectionFormes.add(Box.createRigidArea(new Dimension(0, 4)));
        sectionFormes.add(creerBouton("Ajouter Pulsar", onAjouterPulsar));
        add(sectionFormes);
        add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel sectionInfos = creerSection("Infos");
        infoTaille = new JLabel();
        infoTaille.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoGeneration = new JLabel();
        infoGeneration.setAlignmentX(Component.CENTER_ALIGNMENT);
        sectionInfos.add(infoTaille);
        sectionInfos.add(Box.createRigidArea(new Dimension(0, 4)));
        sectionInfos.add(infoGeneration);
        add(sectionInfos);
        add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel sectionActions = creerSection("Actions");
        sectionActions.add(creerBouton("Taille de la grille", onParametres));
        sectionActions.add(Box.createRigidArea(new Dimension(0, 4)));
        sectionActions.add(creerBouton("Reset", onReset));
        sectionActions.add(Box.createRigidArea(new Dimension(0, 4)));

        JButton boutonQuitter = creerBouton("Quitter", onQuitter);
        boutonQuitter.setBackground(new Color(220, 70, 70));
        boutonQuitter.setForeground(Color.WHITE);
        boutonQuitter.setOpaque(true);
        sectionActions.add(boutonQuitter);
        add(sectionActions);
    }

    private JLabel creerTitre(String texte) {
        JLabel label = new JLabel(texte);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        return label;
    }

    private JPanel creerSection(String titre) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            titre,
            TitledBorder.LEFT,
            TitledBorder.TOP
        ));
        section.setMaximumSize(new Dimension(220, Integer.MAX_VALUE));
        return section;
    }

    private JButton creerBouton(String texte, Runnable action) {
        JButton bouton = new JButton(texte);
        bouton.setAlignmentX(Component.CENTER_ALIGNMENT);
        bouton.setMaximumSize(new Dimension(200, 32));
        bouton.addActionListener(e -> action.run());
        return bouton;
    }

    public JToggleButton getBoutonMode() {
        return boutonMode;
    }

    public JButton getBoutonPlayPause() {
        return boutonPlayPause;
    }

    public JButton getBoutonSuivant() {
        return boutonSuivant;
    }

    public JLabel getInfoTaille() {
        return infoTaille;
    }

    public JLabel getInfoGeneration() {
        return infoGeneration;
    }
}
