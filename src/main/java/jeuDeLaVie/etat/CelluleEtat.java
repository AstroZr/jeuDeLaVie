package jeuDeLaVie.etat;

import jeuDeLaVie.visiteur.VisiteurCellule;
import jeuDeLaVie.modele.Cellule;
import jeuDeLaVie.modele.JeuDeLaVie;

public interface CelluleEtat {

        CelluleEtat vit();
        CelluleEtat meurt();
        boolean estVivante();
        void accepte(VisiteurCellule visiteur, Cellule cellule, JeuDeLaVie jeu);
        
}
