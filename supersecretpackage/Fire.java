/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package supersecretpackage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Julien
 */
public class Fire {
    
    private Game game;
    
    // Durée de vie d'un tir (en FPS)
    private static int maxLife = 4;
    
    // Coordonnées du tir
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    
    // Vie courante du tir
    private int life;
    
    // Si le tir a déjà touché une cible
    private boolean hasTouched;
    
    
    /**
     * Constructeur du tir
     */
    public Fire(Game g, int x1, int y1, int x2, int y2) {
        initialize();
        loadContent();
        game = g;
        startX = x1;
        startY = y1;
        endX = x2;
        endY = y2;
        
    }
    
    
    /**
     * Initialisation des données du Tir
     */
    private void initialize() {
        life = maxLife;
        hasTouched = false;
    }
    
    
    /**
     * Chargement des ressources du Tir
     */
    private void loadContent() {
        // Void
    }
    
    
    /**
     * Mise à jour logique du tir, on passe en paramètre le tableaux des objets avec lequels il peut entrer en collision
     */
    public void update() {
        
        ArrayList<Zombie> zombies = game.getZombies();
        
        if(isAlive()) {
        
            decreaseLife();

            // Un tir ne peut toucher qu'un zombie
            if(!hasTouched) {
                Zombie nearestZombie = null;

                // On parcours les zombies
                for(Iterator i = zombies.iterator(); i.hasNext();) {
                    Zombie zombie = (Zombie) i.next();

                    // Si le tir est en collision avec le zombie
                    if(
                        // Sur Y
                        startY >= zombie.getY() && startY <= (zombie.getY() + zombie.getCollisionHeight())
                        &&
                        // Et sur X dans un sens ou dans l'autre
                        (
                            (startX < (zombie.getX() + zombie.getCollisionWidth()) && endX >= zombie.getX()) // Tir sur la droite
                            ||
                            (startX > zombie.getX() && endX <= (zombie.getX() + zombie.getCollisionWidth())) // Tir sur la gauche
                        )
                    ) {
                        // On cherche le zombie le plus proche de l'origine du tir
                        if(
                            nearestZombie == null
                            ||
                            Math.abs(nearestZombie.getX() - startX) > Math.abs(zombie.getX() - startX)
                        ) {
                            nearestZombie = zombie;
                        }
                    }
                }

                // Le zombie le plus proche est tué
                if(nearestZombie != null) {
                    hasTouched = true;
                    nearestZombie.isDead(true);
                    
                    // Le tir s'arrête au zombie (au niveau annimation)
                    endX = (int) nearestZombie.getX() + (nearestZombie.getCollisionWidth() / 2);
                }
            }
        }
    }
    
    
    /**
     * Mise à jour du rendu du tir
     */
    public void draw(Graphics2D g2d) {
        if(isAlive()) {
            g2d.setColor(new Color(80, 80, 80));
            g2d.drawLine(startX, startY, endX, endY);
        }
    }
    
    
    /**
     * Réduit le temps de vie du tir (en FPS)
     */
    public void decreaseLife() {
        if(life > 0)
            life--;
    }
    
    
    /**
     * isAlive Getter
     */
    public boolean isAlive() {
        if(life > 0)
            return true;
        else
            return false;
    }
    
    
    /**
     * isAlive Getter
     */
    public boolean hasTouched() {
        return hasTouched;
    }
    
    
    /**
     * startX Getter
     */
    public int getStartX() {
        return startX;
    }
    
    
    /**
     * startY Getter
     */
    public int getStartY() {
        return startY;
    }
    
    
    /**
     * endX Getter
     */
    public int getEndX() {
        return endX;
    }
    
    
    /**
     * endY Getter
     */
    public int getEndY() {
        return endY;
    }
    
}
