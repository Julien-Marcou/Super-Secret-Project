package supersecretpackage;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Un Zombie
 */
public class Zombie {
    
    private Game game;
    
    private float x;
    private float y;
    private float dX;
    private float dY;
    
    int crunchCountdown;
    int crunchCount;
    private boolean walking;
    private boolean dead;
    private ZombieAI ai;
    
    private int collisionWidth;
    private int collisionHeight;
    private BufferedImage sprite;
    private float currentSprite;
    private int currentOrientation;
    private int currentAnimation;
    
    private final class SpriteOrientation {
        public final static int RIGHT = 0, LEFT = 39;
    }
    private final class SpriteAnimation {
        public static final int WALKINK = 0;
    }
    private static final Map<Integer, Dimension> SpriteDimension = new HashMap<Integer, Dimension>() {{
        put(SpriteAnimation.WALKINK, new Dimension(41, 39));
    }};
    private static final Map<Integer, Map<Integer, Dimension>> SpriteGap = new HashMap<Integer, Map<Integer, Dimension>>() {{
        put(SpriteOrientation.RIGHT, new HashMap<Integer, Dimension>() {{
            put(SpriteAnimation.WALKINK, new Dimension(14, 1));
        }});
        put(SpriteOrientation.LEFT, new HashMap<Integer, Dimension>() {{
            put(SpriteAnimation.WALKINK, new Dimension(13, 1));
        }});
    }};
    private static final Map<Integer, Float> SpriteDuration = new HashMap<Integer, Float>() {{
        put(SpriteAnimation.WALKINK, 2.2f);
    }};
    private static final Map<Integer, Integer> SpriteFrames = new HashMap<Integer, Integer>() {{
        put(SpriteAnimation.WALKINK, 15);
    }};
    
    
    /**
     * Constructeur du zombie
     */
    public Zombie(Game g) {
        initialize();
        loadContent();
        game = g;
    }
    
    
    /**
     * Initialisation des données du Zombie
     */
    private void initialize() {
        collisionWidth = 14;
        collisionHeight = 38;
        currentSprite = 0;
        currentOrientation = SpriteOrientation.RIGHT;
        currentAnimation = SpriteAnimation.WALKINK;
                
        x = 50;
        y = Framework.frameHeight - 60 - collisionHeight;
        dX = 0;
        dY = 0;
        
        ai = new ZombieAI();
        walking = false;
        crunchCount = 0;
        crunchCountdown = Framework.MAX_FPS*2; // Peut mordre toutes les 2sec
    }
    
    
    /**
     * Chargement des ressources du Zombie
     */
    private void loadContent() {
        try {
            sprite = ImageIO.read(Resources.get("image/zombie.png"));
        }
        catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Tourner à gauche
     */
    public void left() {
        currentOrientation = SpriteOrientation.LEFT;
    }
    
    
    /**
     * Tourner à droite
     */
    public void right() {
        currentOrientation = SpriteOrientation.RIGHT;
    }
    
    
    /**
     * Tourner à droite
     */
    public void walk() {
        walking = true;
    }
    
    
    /**
     * Lance une attaque contre le joueur
     */
    public void crunch() {
        if(crunchCount == 0)
               crunchCount = crunchCountdown;
    }
    
    
    /**
     * Mise à jour logique du zombie
     */
    public void update() {
        if(!isDead()) {
            int wallPosition = (Framework.frameWidth - collisionWidth);
            /*int groundLevel = (Framework.frameHeight - 60 - collisionHeight);
            Boolean groundCollision = (y >= groundLevel);
            float dYAcceleration = 1.2f;*/
            // PAS DE GESTION DE LA GRAVITÉ POUR LE ZOMBIE
            
            ai.updateToNextMove();
            
            // S'il mort le joueur
            if(crunchCount > 0) {
                crunchCount--;
            }

            // S'il marche
            if(walking) {
                if(currentOrientation == SpriteOrientation.LEFT) {
                    dX = -0.5f;
                }
                else {
                    dX = 0.5f;
                }
                walking = false;
            }
            else {
                dX = 0;
            }

            // S'il tombe
            /*if(!groundCollision) {
                dY += (9.81 / Framework.MAX_FPS) * dYAcceleration;
            }
            // S'il arrête de tomber
            else if(groundCollision && dY > 0) {
                dY = 0;
            }*/

            // Mise à jour déplacement
            x += dX;
            //y += dY;

            // Limitation déplacement en fonction des collisions
            /*if(y > groundLevel) {
                y = groundLevel;
            }*/
            
            // Le zombie peut apparaitre en dehors du décor mais ne peu plus en sortir
            if(x < 0 && currentOrientation == SpriteOrientation.LEFT) {
                x = 0;
            }
            else if(x > wallPosition && currentOrientation == SpriteOrientation.RIGHT) {
                x = wallPosition;
            }

            // Mise à jour de l'animation du sprite
            currentSprite += SpriteFrames.get(currentAnimation) / (Framework.MAX_FPS * SpriteDuration.get(currentAnimation));

            // Si l'animation est terminé on reboucle au début
            if(currentSprite >= SpriteFrames.get(currentAnimation)) {
                currentSprite = 0;
            }
        }
    }
    
    
    /**
     * Mise à jour du rendu du zombie
     */
    public void draw(Graphics2D g2d) {
        if(!isDead()) {
            // Boite de collision
            /*g2d.setColor(new Color(20, 20, 20));
            g2d.fillRect((int)x, (int)y, collisionWidth, collisionHeight);*/

            // Sprite
            Dimension spriteDimension = SpriteDimension.get(currentAnimation);
            Dimension spriteGap = SpriteGap.get(currentOrientation).get(currentAnimation);
            int spriteX = ((int)currentSprite * spriteDimension.width);
            int spriteY = currentAnimation + currentOrientation;
            int spriteDX = (int)x - spriteGap.width;
            int spriteDY = (int)y - spriteGap.height;

            g2d.drawImage(
                sprite.getSubimage(spriteX, spriteY, spriteDimension.width, spriteDimension.height),
                spriteDX, spriteDY, spriteDimension.width, spriteDimension.height, null
            );
        }
    }
    
    
    /**
     * position x Setter
     */
    public void setX(int x) {
        this.x = x;
    }
    
    
    /**
     * position y Setter
     */
    public void setY(int y) {
        this.y = y;
    }
    
    
    /**
     * position x Getter
     */
    public float getX() {
        return x;
    }
    
    
    /**
     * x Getter (centre du zombie et non le coin supérieur gauche)
     */
    public float getGX() {
        return x + (collisionWidth/2);
    }
    
    
    /**
     * position y Getter
     */
    public float getY() {
        return y;
    }
    
    
    /**
     * y Getter (pied du zombie et non le coin supérieur gauche)
     */
    public float getGY() {
        return y + collisionHeight;
    }
    
    
    /**
     * isDead Getter
     */
    public boolean isDead() {
        return dead;
    }
    
    
    /**
     * isCrunching Getter
     */
    public boolean isCrunching() {
        return crunchCount == crunchCountdown - 1;
    }
    
    
    /**
     * isDead Setter
     */
    public void isDead(boolean dead) {
        this.dead = dead;
    }
    
    
    /**
     * collisionWidth Getter
     */
    public int getCollisionWidth() {
        return collisionWidth;
    }
    
    
    /**
     * collisionHeight Getter
     */
    public int getCollisionHeight() {
        return collisionHeight;
    }
    
    
    /**
     * IA d'un zombie
     */
    private class ZombieAI {
        
        public ZombieAI() {}
        
        public void updateToNextMove() {
            Player target = game.getPlayer();
            if(target.getGX() > getGX()) {
                right();
                walk();
            }
            else if(target.getGX() < getGX()) {
                left();
                walk();
            }
            if(Math.abs(target.getGX() - getGX()) < 5) {
                crunch();
            }
        }
        
    }
    
}
