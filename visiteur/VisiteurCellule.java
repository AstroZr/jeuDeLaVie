package jeuDeLaVie.visiteur;

import jeuDeLaVie.modele.Cellule;
import jeuDeLaVie.modele.JeuDeLaVie;

public interface VisiteurCellule {
    void visiteCelluleVivante(Cellule cellule, JeuDeLaVie jeu);
    void visiteCelluleMorte(Cellule cellule, JeuDeLaVie jeu);
}
