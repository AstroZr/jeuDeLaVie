package jeuDeLaVie.modele;

import jeuDeLaVie.etat.CelluleEtat;
import jeuDeLaVie.visiteur.VisiteurCellule;

public class Cellule{
    private CelluleEtat etat;
    private int x;
    private int y;

    public Cellule(CelluleEtat etat, int x, int y) {
        this.etat = etat;
        this.x = x;
        this.y = y;
    }

    public void vit(){
        etat = etat.vit();
    }

    public void meurt(){
        etat = etat.meurt();
    }

    public boolean estVivante(){
        return etat.estVivante();
    }

    public int nombreVoisinesVivantes(JeuDeLaVie jeu){
        int count = 0;
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                if(i == 0 && j == 0) continue;
                int xVoisin = x + i;
                int yVoisin = y + j;
                if(xVoisin >= 0 && xVoisin < jeu.getXMax() && yVoisin >= 0 && yVoisin < jeu.getYMax()){
                    if(jeu.getGrille(xVoisin, yVoisin).estVivante()){
                        count++;
                    }
                }
            }
        }
        return count;
    }
    
    public void accepte(VisiteurCellule visiteur, JeuDeLaVie jeu) {
        etat.accepte(visiteur, this, jeu);
    }
}
