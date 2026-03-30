package jeuDeLaVie;

import jeuDeLaVie.modele.JeuDeLaVie;
import jeuDeLaVie.observateur.JeuDeLaVieUI;

public class Main {
    public static void main(String[] args) {

        JeuDeLaVie jeu = new JeuDeLaVie(100, 100);
        
        JeuDeLaVieUI ui = new JeuDeLaVieUI(jeu);
        
        jeu.attacheObservateur(ui);
        
        jeu.actualiseObservateurs();
    }
}
