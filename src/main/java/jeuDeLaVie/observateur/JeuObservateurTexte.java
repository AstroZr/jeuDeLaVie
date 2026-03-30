package jeuDeLaVie.observateur;

import jeuDeLaVie.modele.JeuDeLaVie;

public class JeuObservateurTexte implements JeuObservateur {
    
    private JeuDeLaVie jeu;
    
    public JeuObservateurTexte(JeuDeLaVie jeu) {
        this.jeu = jeu;
    }
    
    @Override
    public void actualise() {
        System.out.println("Génération " + jeu.getNumeroGeneration() + " - Cellules vivantes : " + jeu.getNombreCellulesVivantes());
    }
}
