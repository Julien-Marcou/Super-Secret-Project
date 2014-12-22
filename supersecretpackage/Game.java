package supersecretpackage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Classe du jeu
 */
public class Game {

    // Référence au joueur
    private Player player;
    
    // Référence aux zombies
    private ArrayList<Zombie> zombies;
    
    
    /**
     * Constructeur du jeu
     */
    public Game() {
        Thread threadForInitGame = new Thread() {
            @Override
            public void run(){
                initialize();
                loadContent();
                Framework.gameState = Framework.GameState.PLAYING;
            }
        };
        threadForInitGame.start();
    }
    
    
    /**
     * Initialisation des données du Jeu
     */
    private void initialize() {
        player = new Player(this);
        zombies = new ArrayList<Zombie>();
    }
    
    
    /**
     * Chargement des ressources du Jeu
     */
    private void loadContent() {
        // Void
    }
    
    
    /**
     * On recommence le jeu
     */
    public void restartGame() {
        // Void
        // Todo : implementer une reprise de pause
    }
    
    
    /**
     * On arrête le jeu
     */
    public void stopGame() {
        // Void
        // Todo : implementer une mise pause
    }
    
    
    /**
     * Mise à jour logique du jeu
     */
    public void updateGame() {
        
        if(player.isDead()) {
            Framework.gameState = Framework.GameState.GAME_OVER;
            return;
        }
        
        // Le joueur
        player.update();
        
        // Les zombies
        for(Iterator i = zombies.iterator(); i.hasNext();) {
            Zombie zombie = (Zombie) i.next();
            if(zombie.isDead()) {
                i.remove();
            }
            else {
                zombie.update();
            }
        }
        
        // On ajout un zombie en moyenne toutes les 2 secondes (25 au max)
        if(zombies.size() < 20 && Math.random() < (1.0 / Framework.MAX_FPS * 2)) {
            Zombie zombie = new Zombie(this);
            if(Math.random() < 0.5) {
                zombie.setX(Framework.frameWidth);
            }
            else {
                zombie.setX(-14);
            }
            zombies.add(zombie);
        }
    }
    
    
    /**
     * Mise à jour du rendu
     */
    public void draw(Graphics2D g2d) {
        
        // L'ordre de dessinage est important, le premier élément déssiné sera recouvert par les autres 
        
        // Le décors
        g2d.setColor(new Color(38, 38, 38));
        g2d.fillRect(0, 0, Framework.frameWidth, Framework.frameHeight);
        g2d.setColor(new Color(25, 20, 20));
        g2d.fillRect(0, Framework.frameHeight - 60, Framework.frameWidth, Framework.frameHeight - 60);
        
        // Le joueur
        player.draw(g2d);
        
        // Les zombies
        for(Iterator i = zombies.iterator(); i.hasNext();) {
            Zombie zombie = (Zombie) i.next();
            zombie.draw(g2d);
        }
    }
    
    
    /**
     * player Getter
     */
    public Player getPlayer() {
        return player;
    }
    
    
    /**
     * zombies Getter
     */
    public ArrayList<Zombie> getZombies() {
        return zombies;
    }
    
}
