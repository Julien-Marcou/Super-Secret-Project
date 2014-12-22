package supersecretpackage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

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
    public static enum GameState {VOID, MAIN_MENU, PLAYING, GAME_OVER};
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
                // (crée un effet de lag à l'écran, mais permet de continuer à jouer)
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
     * On quitte le jeu
     */
    private void exitGame() {
        Window.close();
    }


    /**
     * On fait la mise à jour du jeu
     */
    private void updateGame() {
        if(gameState == GameState.PLAYING) {
            game.updateGame();
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
        int frameXcenter = frameWidth / 2;
        int frameYcenter = frameHeight / 2;
        switch(gameState) {

            case PLAYING :
                game.draw(g2d);
                break;

            case MAIN_MENU :
                g2d.setColor(new Color(38, 38, 38));
                g2d.fillRect(0, 0, frameWidth, frameHeight);
                g2d.setColor(new Color(150, 150, 150));
                g2d.drawString("Appuyez sur \"Espace\" ou \"Entrée\" pour commencer", frameXcenter - 138, frameYcenter - 20);
                g2d.drawString("Ou sur \"Échap\" pour quitter le jeu", frameXcenter - 90, frameYcenter + 0);
                g2d.drawString("Se déplacer : ZQSD ou ↑←↓→", frameXcenter - 84, frameYcenter + 60);
                g2d.drawString("Recharger : R ou Clic Droit", frameXcenter - 76, frameYcenter + 80);
                g2d.drawString("Tirer : Clic Gauche", frameXcenter - 50, frameYcenter + 100);
                break;

            case GAME_OVER :
                g2d.setColor(new Color(38, 38, 38));
                g2d.fillRect(0, 0, frameWidth, frameHeight);
                g2d.setColor(new Color(150, 150, 150));
                g2d.drawString("Game Over !", frameXcenter - 29, frameYcenter - 30);
                g2d.drawString("Appuyez sur \"Espace\" ou \"Entrée\" pour recommencer", frameXcenter - 142, frameYcenter + 30);
                g2d.drawString("Ou sur \"Échap\" pour quitter le jeu", frameXcenter - 90, frameYcenter + 50);
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
            return;
        }
        
        if(
            (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER)
            &&
            (gameState == GameState.MAIN_MENU || gameState == GameState.GAME_OVER)
        ) {
            newGame();
        }
    }

}
