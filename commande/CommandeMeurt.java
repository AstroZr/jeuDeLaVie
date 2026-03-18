package jeuDeLaVie.commande;

import jeuDeLaVie.modele.Cellule;

public class CommandeMeurt implements Commande {
    
    private Cellule cellule;
    
    public CommandeMeurt(Cellule cellule) {
        this.cellule = cellule;
    }
    
    @Override
    public void executer() {
        cellule.meurt();
    }
}
