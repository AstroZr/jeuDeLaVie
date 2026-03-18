package jeuDeLaVie.etat;

import jeuDeLaVie.visiteur.VisiteurCellule;
import jeuDeLaVie.modele.Cellule;
import jeuDeLaVie.modele.JeuDeLaVie;

public class CelluleEtatVivant implements CelluleEtat {

    public static final CelluleEtatVivant instance = new CelluleEtatVivant();

    private CelluleEtatVivant() {
    }

    public static CelluleEtatVivant getInstance() {
        return CelluleEtatVivant.instance;
    }

    @Override
    public CelluleEtat vit() {
        return this;
    }

    @Override
    public CelluleEtat meurt() {
        return CelluleEtatMort.getInstance();
    }

    @Override
    public boolean estVivante() {
        return true;
    }
    
    @Override
    public void accepte(VisiteurCellule visiteur, Cellule cellule, JeuDeLaVie jeu) {
        visiteur.visiteCelluleVivante(cellule, jeu);
    }
    
}
