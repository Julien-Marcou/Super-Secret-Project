package supersecretpackage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Framework de controle du jeu
 */
public class Framework extends Canvas {

    // Données du Canvas
    public static int frameWidth;
    public static int frameHeight;
    public static final long secInNanosec = 1000000000L;  // Une seconde en nanosecondes
    public static final long milisecInNanosec = 1000000L; // une milliseconde en nanosecondes
    public static final int MAX_FPS = 60; // FPS désiré
    public static final int MAX_FRAME_SKIPS = 5; // Au bout de combien de frame sautée on force la mise à jour visuelle
    public static final long FRAME_PERIOD = secInNanosec / MAX_FPS; // Durée d'une période pour faire les mises à jour

    // Données du Jeu
    private Game game;
    public static enum GameState {VOID, MAIN_MENU, PLAYING};
    public static GameState gameState;
    private boolean gameIsRunning;
    private long beginTime; // Temps au début du cyle courant
    private long timeTaken; // Durée du dernier cycle


    /**
     * Constructeur du framework, point d'entrée du jeu
     */
    public Framework() {
        super();

        gameState = GameState.VOID;

        Thread gameThread = new Thread() {
            @Override
            public void run(){
                initialize();
                loadContent();
                gameLoop();
            }
        };
        gameThread.start();
    }


    /**
     * Initialisation des données du Framework
     */
    private void initialize() {

        // Tant que la fenêtre n'est pas affiché
       do {

           // On endort le Thread 100ms (10 FPS)
            try {
                 Thread.sleep(100);
            }
            catch (InterruptedException ex) {
                // Void
            }

            // On update la dimmension de la fenêtre
            frameWidth = getWidth();
            frameHeight = getHeight();

        } while(!(frameWidth > 1 && frameHeight > 1));

       // Un fois la fenêtre affiché on passe au jeu
        gameState = GameState.MAIN_MENU;
        gameIsRunning = true;
    }


    /**
     * Chargement des ressources du Framework
     */
    private void loadContent() {
        // Void
    }


    /**
     * Un cycle pour le jeu : Mise à jour physique du jeu puis mise à jour graphique
     */
    private void gameLoop() {

        // Variables pour calculer le temps qu'aura à dormir le Thread pour rencontrer le GAME_FPS
        long timeLeft, framesSkipped;

        while(gameIsRunning) {

            beginTime = System.nanoTime();
            framesSkipped = 0;

            updateGame(); // Mise à jour du jeu (logique et données)
            renderGame(); // Rendu du jeu (affichage graphique)

            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (FRAME_PERIOD - timeTaken) / milisecInNanosec;

            // Si il reste du temps, on endort le thread
            if (timeLeft > 0) {
                try {
                     Thread.sleep(timeLeft);
                }
                catch(InterruptedException ex) {
                    // Void
                }
            }
            // Sinon
            else {
                // Tant qu'on a du retard on mets à jour le jeu sans faire le rendu
                // On quitte aussi la boucle au bout de MAX_FRAME_SKIPS frames passées,
                // Pour forcer à mettre à jour au moins une fois le rendu
                while(timeLeft < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                    updateGame();
                    timeLeft += FRAME_PERIOD / milisecInNanosec; // On rattrape notre retard
                    framesSkipped++;
                }
            }
        }
    }


    /**
     * On démarre le jeu
     */
    private void newGame() {
        game = new Game();
    }


    /**
     * On recommence le jeu
     */
    private void restartGame() {
        game.restartGame();
    }


    /**
     * On quitte le jeu
     */
    private void exitGame() {
        Window.close();
    }


    /**
     * On fait la mise à jour du jeu
     */
    private void updateGame() {
        switch(gameState) {

            case PLAYING :
                game.updateGame();
                break;

            case MAIN_MENU :
                // Void
                break;

            default :
                // Void
                break;
        }
    }


    /**
     * On fait le rendu du jeu
     */
    private void renderGame() {
        repaint();
    }


    /**
     * Mise à jour du rendu, à chaque "repaint()" / "renderGame()"
     */
    @Override
    public void draw(Graphics2D g2d) {
        switch(gameState) {

            case PLAYING :
                game.draw(g2d);
                break;

            case MAIN_MENU :
                int frameXcenter = frameWidth / 2;
                int frameYcenter = frameHeight / 2;
                g2d.setColor(new Color(38, 38, 38));
                g2d.fillRect(0, 0, frameWidth, frameHeight);
                g2d.setColor(new Color(150, 150, 150));
                g2d.drawString("Appuyez sur une touche pour commencer", frameXcenter - 114, frameYcenter - 20);
                g2d.drawString("Ou sur \"Échap\" pour quitter le jeu", frameXcenter - 90, frameYcenter + 0);

                g2d.drawString("Se déplacer : ZQSD ou ↑←↓→", frameXcenter - 84, frameYcenter + 60);
                g2d.drawString("Recharger : R ou Clic Droit", frameXcenter - 76, frameYcenter + 80);
                g2d.drawString("Tirer : Clic Gauche", frameXcenter - 50, frameYcenter + 100);
                break;

            default :
                // Void
                break;
        }
    }


    /**
     * Détection d'un relachement de touche
     */
    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            exitGame();
        }
        else if(game == null) {
            newGame();
        }
    }


    /**
     * Détection d'un relachement de souris
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if(game == null) {
            newGame();
        }
    }

}
