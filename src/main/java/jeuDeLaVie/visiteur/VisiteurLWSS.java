package jeuDeLaVie.visiteur;

import jeuDeLaVie.modele.Cellule;
import jeuDeLaVie.modele.JeuDeLaVie;
import jeuDeLaVie.commande.CommandeVit;
import jeuDeLaVie.commande.CommandeMeurt;

public class VisiteurLWSS implements VisiteurCellule {
    @Override
    public void visiteCelluleVivante(Cellule cellule, JeuDeLaVie jeu) {
        int voisines = cellule.nombreVoisinesVivantes(jeu);

        if (voisines < 2 || voisines > 3) {
            jeu.ajouteCommande(new CommandeMeurt(cellule));
        }
    }

    @Override
    public void visiteCelluleMorte(Cellule cellule, JeuDeLaVie jeu) {
        int voisines = cellule.nombreVoisinesVivantes(jeu);

        if (voisines == 3 || voisines == 5) {
            jeu.ajouteCommande(new CommandeVit(cellule));
        }
    }
}