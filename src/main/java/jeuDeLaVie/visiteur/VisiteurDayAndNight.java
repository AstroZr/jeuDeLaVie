package jeuDeLaVie.visiteur;

import jeuDeLaVie.modele.Cellule;
import jeuDeLaVie.modele.JeuDeLaVie;
import jeuDeLaVie.commande.CommandeVit;
import jeuDeLaVie.commande.CommandeMeurt;

/**
 * Visiteur implémentant les règles du jeu Day & Night
 * - Une cellule morte naît si elle a 3, 6, 7 ou 8 voisins vivants
 * - Une cellule vivante survit si elle a 3, 4, 6, 7 ou 8 voisins vivants
 */
public class VisiteurDayAndNight implements VisiteurCellule {
    
    @Override
    public void visiteCelluleVivante(Cellule cellule, JeuDeLaVie jeu) {
        int voisines = cellule.nombreVoisinesVivantes(jeu);

        if (voisines != 3 && voisines != 4 && voisines != 6 && voisines != 7 && voisines != 8) {
            jeu.ajouteCommande(new CommandeMeurt(cellule));
        }
    }
    
    @Override
    public void visiteCelluleMorte(Cellule cellule, JeuDeLaVie jeu) {
        int voisines = cellule.nombreVoisinesVivantes(jeu);
        
        if (voisines == 3 || voisines == 6 || voisines == 7 || voisines == 8) {
            jeu.ajouteCommande(new CommandeVit(cellule));
        }
    }
}
