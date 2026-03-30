package jeuDeLaVie.visiteur;

import jeuDeLaVie.modele.Cellule;
import jeuDeLaVie.modele.JeuDeLaVie;
import jeuDeLaVie.commande.CommandeVit;
import jeuDeLaVie.commande.CommandeMeurt;

/**
 * Visiteur implémentant les règles du jeu HighLife
 * - Une cellule morte naît si elle a exactement 3 ou 6 voisins vivants
 * - Une cellule vivante survit si elle a 2 ou 3 voisins vivants
 */
public class VisiteurHighLife implements VisiteurCellule {
    
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
        
        if (voisines == 3 || voisines == 6) {
            jeu.ajouteCommande(new CommandeVit(cellule));
        }
    }
}
