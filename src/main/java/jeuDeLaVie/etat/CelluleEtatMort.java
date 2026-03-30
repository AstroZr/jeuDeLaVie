package jeuDeLaVie.etat;

import jeuDeLaVie.visiteur.VisiteurCellule;
import jeuDeLaVie.modele.Cellule;
import jeuDeLaVie.modele.JeuDeLaVie;

public class CelluleEtatMort implements CelluleEtat {

    public static final CelluleEtatMort instance = new CelluleEtatMort();

    private CelluleEtatMort() {
    }

    public static CelluleEtatMort getInstance() {
        return CelluleEtatMort.instance;
    }
    
    @Override
    public CelluleEtat vit() {
        return CelluleEtatVivant.getInstance();
    }

    @Override
    public CelluleEtat meurt() {
        return this;
    }

    @Override
    public boolean estVivante() {
        return false;
    }
    
    @Override
    public void accepte(VisiteurCellule visiteur, Cellule cellule, JeuDeLaVie jeu) {
        visiteur.visiteCelluleMorte(cellule, jeu);
    }
    
}
