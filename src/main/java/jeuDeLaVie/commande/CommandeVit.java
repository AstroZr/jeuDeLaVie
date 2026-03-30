package jeuDeLaVie.commande;

import jeuDeLaVie.modele.Cellule;

public class CommandeVit implements Commande {
    
    private Cellule cellule;
    
    public CommandeVit(Cellule cellule) {
        this.cellule = cellule;
    }
    
    @Override
    public void executer() {
        cellule.vit();
    }
}
