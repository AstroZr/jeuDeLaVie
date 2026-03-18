# 🎮 Jeu de la Vie - Implémentation avec Design Patterns

## 📋 Description

Implémentation du **Jeu de la Vie** de John Conway utilisant **5 Design Patterns** :
- ✅ **État** - Gestion des états des cellules (vivante/morte)
- ✅ **Singleton** - Instance unique pour chaque type d'état
- ✅ **Commande** - File d'actions différées pour calcul de génération
- ✅ **Visiteur** - Séparation des règles du jeu de la structure de données
- ✅ **Observateur** - Découplage entre logique métier et interface graphique

## 📁 Structure du Projet

```
jeuDeLaVie/
├── Cellule.java                    # Contexte du pattern État
├── CelluleEtat.java                # Interface État
├── CelluleEtatVivant.java          # État Vivant (Singleton)
├── CelluleEtatMort.java            # État Mort (Singleton)
├── Commande.java                   # Interface Commande
├── CommandeVit.java                # Commande pour faire vivre
├── CommandeMeurt.java              # Commande pour faire mourir
├── Visiteur.java                   # Interface Visiteur
├── VisiteurClassique.java          # Règles de Conway
├── Observable.java                 # Interface Observable
├── Observateur.java                # Interface Observateur
├── JeuDeLaVie.java                 # Classe centrale (grille + logique)
├── JeuDeLaVieUI.java               # Interface graphique Swing
├── Main.java                       # Point d'entrée
├── pom.xml                         # Configuration Maven
├── ARCHITECTURE.md                 # Documentation d'architecture
└── architecture.puml               # Diagramme PlantUML
```

## 🚀 Compilation et Exécution

### Prérequis
- Java JDK 11 ou supérieur
- Maven 3.6 ou supérieur

### Option 1 : Avec Maven (recommandé)

```bash
# Se placer dans le dossier du projet
cd jeuDeLaVie

# Compiler le projet
mvn clean compile

# Exécuter le projet
mvn exec:java

# Ou créer un JAR exécutable puis le lancer
mvn clean package
java -jar target/jeu-de-la-vie-1.0.0.jar
```

### Option 2 : Compilation Java directe

```bash
# Se placer dans le dossier parent de jeuDeLaVie
cd ..

# Compiler tous les fichiers Java
javac jeuDeLaVie/*.java

# Exécuter le programme
java jeuDeLaVie.Main
```

### Option 3 : Avec VS Code ou IntelliJ
1. Ouvrir le dossier dans votre IDE
2. Laisser Maven télécharger les dépendances
3. Exécuter la classe `Main.java`

## 🎯 Utilisation - Démarrage Rapide

Une fois lancé, le programme :
1. Crée une grille de **100×100 cellules**
2. Initialise aléatoirement des cellules vivantes (50% de probabilité)
3. Affiche l'interface graphique avec panneau de contrôle à droite
4. Permet la simulation automatique ou manuelle des générations
5. Affiche l'état actuel (numéro de génération, cellules vivantes, etc.)

## ✨ Fonctionnalités Principales

### 🎮 Simulation et Animation
- **Démarrer/Arrêter** : Lance ou pause la simulation automatique
- **Suivant** : Calcule une génération supplémentaire manuellement
- **Contrôle de vitesse** : Ajustez la vitesse de la simulation (lent ↔ rapide)
- **Numéro de génération** : Affichage du nombre de générations écoulées
- **Compteur de cellules vivantes** : Suivi des statistiques en temps réel

### 🎨 Mode Dessin
- **Passer en mode dessin** : Activez le mode édition pour modifier la grille
  - **Clic gauche** : Créer une cellule vivante
  - **Clic droit** : Supprimer une cellule (la rendre morte)
  - **Glister-déposer** : Dessiner continuellement
- **Code couleur par voisinage** : Les cellules vivantes sont colorées en nuances de rouge selon leur nombre de voisins vivants (plus sombre = plus de voisins)

### 📏 Grille et Navigation
- **Taille configurable** : Modifiez les dimensions de la grille (dialogue *Taille de la grille*)
- **Grille 100×100 par défaut** : Pre-configurée au lancement
- **Navigation fluide** : Utilisez la molette souris et les barres de défilement

### 📁 Gestion des Patterns
- **Sauvegarder** : Exporte l'état actuel de la grille au format `.jdlv` (texte lisible)
- **Charger** : Importe un pattern sauvegardé précédemment
- **Format** : Stockage simple (dimensions + grille en 0/1)

### 🔄 Règles du Jeu (5 Variantes)
Sélectionnez une règle dans le menu *Règles* :

1. **Classique** (Conway)
   - Cellule vivante avec 2-3 voisins → survit
   - Cellule morte avec exactement 3 voisins → naît
   - *Comportement original du Jeu de la Vie de John Conway*

2. **HighLife**
   - Cellule vivante avec 2-3 voisins → survit
   - Cellule morte avec 3 ou 6 voisins → naît
   - *Variation créée par John von Neumann qui ajoute des structures supplémentaires*

3. **Day and Night**
   - Cellule vivante avec 3, 4, 6, 7 ou 8 voisins → survit
   - Cellule morte avec 3, 6, 7 ou 8 voisins → naît
   - *Règles symétriques produisant des patterns très complexes et stables*

4. **Seeds**
   - Cellule vivante : meurt toujours (ne survit jamais)
   - Cellule morte avec exactement 3 voisins → naît
   - *Produit des patterns éphémères et fragmentés qui disparaissent rapidement*

5. **Vie sans la mort**
   - Cellule vivante : survit toujours (immortelle)
   - Cellule morte avec exactement 3 voisins → naît
   - *Les cellules une fois nées ne meurent jamais - la population ne cesse de croître*

### 🎁 Formes Pré-construites
Insérez rapidement des patterns classiques avec les boutons *Formes* :
- **Blinker** : Oscillateur simple de période 2
- **Glider** : Motif mobile qui se déplace diagonalement
- **LWSS** (Lightweight SpaceShip) : Vaisseau spatial léger qui se déplace horizontalement
- **Pulsar** : Oscillateur de période 3 créant un motif en croix

### 🔧 Actions Globales
- **Reset** : Effacez la grille et la réinitialisez aléatoirement
- **Taille de la grille** : Modifiez les dimensions (relance la grille)
- **Quitter** : Ferme l'application

## 🏗️ Architecture des Design Patterns

### 1. Pattern État
- **Cellule** : Contexte qui délègue son comportement à son état
- **CelluleEtat** : Interface définissant les états possibles
- **CelluleEtatVivant / CelluleEtatMort** : Implémentations concrètes (Singleton)

### 2. Pattern Singleton
- Les états `CelluleEtatVivant` et `CelluleEtatMort` sont des Singletons
- Optimise la mémoire : 1 seule instance de chaque état au lieu de milliers

### 3. Pattern Commande
- **Commande** : Interface des actions
- **CommandeVit / CommandeMeurt** : Actions concrètes sur les cellules
- **JeuDeLaVie** : Invoqueur qui stocke et exécute les commandes

### 4. Pattern Visiteur
- **Visiteur** : Interface des règles du jeu
- **VisiteurClassique** : Implémente les règles de Conway
- Permet d'ajouter facilement de nouvelles règles (HighLife, Day&Night, etc.)

### 5. Pattern Observateur
- **JeuDeLaVie** : Observable qui notifie ses observateurs
- **JeuDeLaVieUI** : Observateur qui met à jour l'affichage
- **ObservateurConsole** : Observateur qui affiche les statistiques

## 📊 Flux d'Exécution

```
1. Initialisation
   Main → crée JeuDeLaVie → crée JeuDeLaVieUI → enregistre observateur

2. Pour chaque génération
   JeuDeLaVie.calculerGenerationSuivante()
   ├─► distribueVisiteur(VisiteurClassique)
   │   └─► Pour chaque cellule : accepte(visiteur)
   │       └─► visiteur crée des commandes selon les règles
   │
   ├─► executeCommandes()
   │   └─► exécute toutes les commandes (vit/meurt)
   │
   └─► notifieObservateurs()
       └─► actualise() sur tous les observateurs
           └─► JeuDeLaVieUI redessine la grille
```

## 🔬 Détail Technique des Règles de Conway

Le **pattern Visiteur** `VisiteurClassique` implémente les règles comme suit :

| État | Condition | Résultat |
|------|-----------|----------|
| 🟢 Vivante | < 2 voisins | Meurt (solitude) |
| 🟢 Vivante | 2-3 voisins | Survit |
| 🟢 Vivante | > 3 voisins | Meurt (surpopulation) |
| ⚪ Morte | = 3 voisins | Naît (reproduction) |

Cette implémentation crée des patterns stables (still lifes), oscillateurs, et engins spatiaux (spaceships).

## 🔧 Extensions Possibles

### 1. Autres Règles (Visiteurs)
Créer de nouveaux visiteurs :
- **VisiteurHighLife** : ajout de la règle "naît avec 6 voisines"
- **VisiteurDayAndNight** : règles symétriques
- **VisiteurSeeds** : cellule ne survit jamais

### 2. Interface Graphique Avancée
- Bouton Play/Pause
- Slider de vitesse
- Menu de sélection des règles
- Grille éditable au clic
- Zoom et déplacement
- Sauvegarde/Chargement de configurations

### 3. Observateurs Supplémentaires
- **ObservateurConsole** : affiche stats en temps réel
- **ObservateurFichier** : sauvegarde l'historique
- **ObservateurStatistiques** : graphiques d'évolution

## 📝 Tests

Pour vérifier que votre code fonctionne :

```java
// Test Singleton
CelluleEtatVivant v1 = CelluleEtatVivant.getInstance();
CelluleEtatVivant v2 = CelluleEtatVivant.getInstance();
System.out.println(v1 == v2); // doit afficher true

// Test État
Cellule c = new Cellule();
System.out.println(c.estVivante()); // false
c.vit();
System.out.println(c.estVivante()); // true
```

## 📖 Documentation

- **ARCHITECTURE.md** : Documentation complète de l'architecture
- **architecture.puml** : Diagramme UML (ouvrir avec PlantUML)

## 🎓 Auteur

Projet de TP - Modélisation et Implémentation d'un Automate Cellulaire
Université du Mans

## 📜 Licence

Projet éducatif - Usage libre pour l'apprentissage
