package jeuDeLaVie.modele;

import java.lang.Math;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import jeuDeLaVie.etat.CelluleEtatVivant;
import jeuDeLaVie.etat.CelluleEtatMort;
import jeuDeLaVie.observateur.JeuObservateur;
import jeuDeLaVie.commande.Commande;
import jeuDeLaVie.visiteur.VisiteurCellule;

public class JeuDeLaVie {
    
    private Cellule[][] grille;
    private int xMax;
    private int yMax;
    private ArrayList<JeuObservateur> observateurs;
    private Queue<Commande> commandes;
    private int numeroGeneration;

    public JeuDeLaVie(int xMax, int yMax) {
        this.xMax = xMax;
        this.yMax = yMax;
        this.grille = new Cellule[xMax][yMax];
        this.observateurs = new ArrayList<>();
        this.commandes = new LinkedList<>();
        this.numeroGeneration = 0;
        this.initialiseGrille();
    }

    public void initialiseGrille(){
        for(int i = 0; i < xMax; i++){
            for(int j = 0; j < yMax; j++){
                if(Math.random() > 0.5){
                    grille[i][j] = new Cellule(CelluleEtatVivant.getInstance(), i, j);
                } else {
                    grille[i][j] = new Cellule(CelluleEtatMort.getInstance(), i, j);
                }
            }
        }
    }

    public String exporterPatternTexte() {
        StringBuilder builder = new StringBuilder();
        builder.append(xMax).append(",").append(yMax).append("\n");

        for (int y = 0; y < yMax; y++) {
            for (int x = 0; x < xMax; x++) {
                builder.append(grille[x][y].estVivante() ? '1' : '0');
            }
            if (y < yMax - 1) {
                builder.append("\n");
            }
        }

        return builder.toString();
    }

    public void importerPatternTexte(List<String> lignes) {
        if (lignes == null || lignes.isEmpty()) {
            throw new IllegalArgumentException("Pattern vide");
        }

        String[] dimensions = lignes.get(0).trim().split(",");
        if (dimensions.length != 2) {
            throw new IllegalArgumentException("Entête invalide: largeur,hauteur attendu");
        }

        int largeur = Integer.parseInt(dimensions[0].trim());
        int hauteur = Integer.parseInt(dimensions[1].trim());

        if (largeur <= 0 || hauteur <= 0) {
            throw new IllegalArgumentException("Dimensions invalides");
        }

        if (lignes.size() - 1 < hauteur) {
            throw new IllegalArgumentException("Nombre de lignes insuffisant pour le pattern");
        }

        Cellule[][] nouvelleGrille = new Cellule[largeur][hauteur];

        for (int y = 0; y < hauteur; y++) {
            String ligne = lignes.get(y + 1).trim();
            if (ligne.length() < largeur) {
                throw new IllegalArgumentException("Ligne " + (y + 2) + " trop courte");
            }

            for (int x = 0; x < largeur; x++) {
                char etat = ligne.charAt(x);
                if (etat == '1') {
                    nouvelleGrille[x][y] = new Cellule(CelluleEtatVivant.getInstance(), x, y);
                } else if (etat == '0') {
                    nouvelleGrille[x][y] = new Cellule(CelluleEtatMort.getInstance(), x, y);
                } else {
                    throw new IllegalArgumentException("Caractère invalide '" + etat + "' en ligne " + (y + 2));
                }
            }
        }

        this.xMax = largeur;
        this.yMax = hauteur;
        this.grille = nouvelleGrille;
        this.numeroGeneration = 0;
        actualiseObservateurs();
    }

    public Cellule getGrille(int x, int y){
        return this.grille[x][y];
    }

    public int getXMax(){
        return this.xMax;
    }

    public int getYMax(){
        return this.yMax;
    }
    
    public int getNumeroGeneration() {
        return this.numeroGeneration;
    }
    
    public int getNombreCellulesVivantes() {
        int count = 0;
        for (int i = 0; i < xMax; i++) {
            for (int j = 0; j < yMax; j++) {
                if (grille[i][j].estVivante()) {
                    count++;
                }
            }
        }
        return count;
    }
    
    public void attacheObservateur(JeuObservateur observateur) {
        observateurs.add(observateur);
    }
    
    public void detacheObservateur(JeuObservateur observateur) {
        observateurs.remove(observateur);
    }
    
    public void actualiseObservateurs() {
        for (JeuObservateur observateur : observateurs) {
            observateur.actualise();
        }
    }
    
    public void ajouteCommande(Commande commande) {
        commandes.add(commande);
    }
    
    public void executeCommandes() {
        while (!commandes.isEmpty()) {
            Commande commande = commandes.poll();
            commande.executer();
        }
    }
    
    public void distribueVisiteur(VisiteurCellule visiteur) {
        for (int i = 0; i < xMax; i++) {
            for (int j = 0; j < yMax; j++) {
                grille[i][j].accepte(visiteur, this);
            }
        }
    }
    
    public void calculerGenerationSuivante(VisiteurCellule visiteur) {

        distribueVisiteur(visiteur);

        executeCommandes();
        
        numeroGeneration++;

        actualiseObservateurs();
    }
    
    public void redimensionnerGrille(int nouveauXMax, int nouveauYMax) {
        this.xMax = nouveauXMax;
        this.yMax = nouveauYMax;
        this.grille = new Cellule[nouveauXMax][nouveauYMax];
        this.numeroGeneration = 0;
        this.initialiseGrille();
        this.actualiseObservateurs();
    }

    public void viderGrille() {
        for (int x = 0; x < xMax; x++) {
            for (int y = 0; y < yMax; y++) {
                grille[x][y].meurt();
            }
        }
        this.numeroGeneration = 0;
        this.actualiseObservateurs();
    }

    public void randomiserGrille() {
        for (int x = 0; x < xMax; x++) {
            for (int y = 0; y < yMax; y++) {
                if (Math.random() > 0.5) {
                    grille[x][y].vit();
                } else {
                    grille[x][y].meurt();
                }
            }
        }
        this.numeroGeneration = 0;
        this.actualiseObservateurs();
    }
}
