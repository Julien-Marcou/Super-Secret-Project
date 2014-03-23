package supersecretpackage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Le joueur
 */
public class Player {
    
    private float x;
    private float y;
    private float dX;
    private float dY;
    
    private int ammo;
    private ArrayList<Fire> fires;
    private int fireDuration;
    private int maxAmmo;
    
    private int collisionWidth;
    private int collisionHeight;
            
    private BufferedImage sprite;
    
    private float currentSprite;
    private int currentOrientation;
    private int currentAnimation;
    
    private final class SpriteOrientation {
        public final static int RIGHT = 0, LEFT = 164;
    }
    private final class SpriteAnimation {
        public static final int RELOADING = 0, FIRING =47, RUNNING = 86, BREATHING = 125;
    }
    private static final Map<Integer, Dimension> SpriteDimension = new HashMap<Integer, Dimension>() {{
        put(SpriteAnimation.RELOADING, new Dimension(47, 47));
        put(SpriteAnimation.FIRING, new Dimension(55, 39));
        put(SpriteAnimation.RUNNING, new Dimension(35, 39));
        put(SpriteAnimation.BREATHING, new Dimension(35, 39));
    }};
    private static final Map<Integer, Map<Integer, Dimension>> SpriteGap = new HashMap<Integer, Map<Integer, Dimension>>() {{
        put(SpriteOrientation.RIGHT, new HashMap<Integer, Dimension>() {{
            put(SpriteAnimation.RELOADING, new Dimension(16, 9));
            put(SpriteAnimation.FIRING, new Dimension(3, 1));
            put(SpriteAnimation.RUNNING, new Dimension(3, 1));
            put(SpriteAnimation.BREATHING, new Dimension(3, 1));
        }});
        put(SpriteOrientation.LEFT, new HashMap<Integer, Dimension>() {{
            put(SpriteAnimation.RELOADING, new Dimension(8, 9));
            put(SpriteAnimation.FIRING, new Dimension(29, 1));
            put(SpriteAnimation.RUNNING, new Dimension(9, 1));
            put(SpriteAnimation.BREATHING, new Dimension(9, 1));
        }});
    }};
    private static final Map<Integer, Float> SpriteDuration = new HashMap<Integer, Float>() {{
        put(SpriteAnimation.RELOADING, 1.8f);
        put(SpriteAnimation.FIRING, 0.4f);
        put(SpriteAnimation.RUNNING, 1.4f);
        put(SpriteAnimation.BREATHING, 2.2f);
    }};
    private static final Map<Integer, Integer> SpriteFrames = new HashMap<Integer, Integer>() {{
        put(SpriteAnimation.RELOADING, 19);
        put(SpriteAnimation.FIRING, 9);
        put(SpriteAnimation.RUNNING, 12);
        put(SpriteAnimation.BREATHING, 6);
    }};
    
    
    /**
     * Constructeur du joueur
     */
    public Player() {
        initialize();
        loadContent();
    }
    
    
    /**
     * Initialisation des données du Joueur
     */
    private void initialize() {
        collisionWidth = 23;
        collisionHeight = 38;
        
        currentSprite = 0;
        currentOrientation = SpriteOrientation.RIGHT;
        currentAnimation = SpriteAnimation.BREATHING;
                
        x = 150;
        y = Framework.frameHeight - 60 - collisionHeight;
        dX = 0;
        dY = 0;
        
        ammo = maxAmmo = 16;
        fires = new ArrayList<Fire>();
        fireDuration = 4;
    }
    
    
    /**
     * Chargement des ressources du Joueur
     */
    private void loadContent() {
        try {
            sprite = ImageIO.read(Resources.get("image/jack.png"));
        }
        catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Fonction de tir
     */
    public void fire() {
        if(ammo > 0) {
            int fireStart = (int)x + (collisionWidth / 2);
            int fireEnd = 0;
            int fireY = (int)y + 11; // 11 = Ajustement vertical du tir au niveau du pistolet du sprite
            
            // Tir vers la droite
            if(currentOrientation == SpriteOrientation.RIGHT) {
                fireEnd = Framework.frameWidth;
            }
            // Tir vers la gauche
            else {
                fireEnd = 0;
            }
            
            fires.add(new Fire(fireStart, fireY, fireEnd, fireY));
            ammo--;
        }
    }
    
    
    /**
     * Fonction de rechargement
     */
    public void reloadGun() {
        ammo = maxAmmo;
    }
    
    
    /**
     * Mise à jour logique du joueur
     */
    public void update() {
        
        int groundLevel = (Framework.frameHeight - 60 - collisionHeight);
        int wallPosition = (Framework.frameWidth - collisionWidth);
        Boolean groundCollision = (y >= groundLevel);
        float dYAcceleration = 1.2f;
        
        // On ne peut tirer/recharger que si on est à l'arret un déplacement annule l'animation
        if(dX == 0) {
            // Si on tire (et qu'on a des munitions et qu'ont ne recharge pas et qu'on ne tirait pas, ou que le précédent tir est à plus de 55%)
            if(ammo > 0 && currentAnimation != SpriteAnimation.RELOADING && Canvas.mouseState(MouseEvent.BUTTON1) && (currentAnimation != SpriteAnimation.FIRING || (currentAnimation == SpriteAnimation.FIRING && currentSprite > SpriteFrames.get(SpriteAnimation.FIRING) / 1.8))) {
                currentSprite = 0;
                currentAnimation = SpriteAnimation.FIRING;
                // Un tir est effectué dès la première animation
                fire();
            }
            // Si on recharge (et qu'on ne rechargait pas)
            else if((Canvas.keyState(KeyEvent.VK_R) || Canvas.mouseState(MouseEvent.BUTTON3)) && currentAnimation != SpriteAnimation.RELOADING) {
                currentSprite = 0;
                currentAnimation = SpriteAnimation.RELOADING;
                // Un rechargement est effectué à la fin de l'animation
            }
        }
            
        // On se déplace à gauche
        if(Canvas.keyState(KeyEvent.VK_Q) || Canvas.keyState(KeyEvent.VK_LEFT)) {
            dX = -2;
            currentOrientation = SpriteOrientation.LEFT;
            currentAnimation = SpriteAnimation.RUNNING;
        }
        // On se déplace à droite
        else if(Canvas.keyState(KeyEvent.VK_D) || Canvas.keyState(KeyEvent.VK_RIGHT)) {
            dX = 2;
            currentOrientation = SpriteOrientation.RIGHT;
            currentAnimation = SpriteAnimation.RUNNING;
        }
        // Sinon on ne se déplace pas
        else {
            dX = 0;
            
            // Si on ne respire/tire/recharge pas on retourne à l'animation de base (respiration)
            if(currentAnimation != SpriteAnimation.BREATHING && currentAnimation != SpriteAnimation.FIRING && currentAnimation != SpriteAnimation.RELOADING) {
                currentSprite = 0;
                currentAnimation = SpriteAnimation.BREATHING;
            }
        }

        // On saute
        if(groundCollision && dY == 0 && (Canvas.keyState(KeyEvent.VK_Z) || Canvas.keyState(KeyEvent.VK_UP))) { 
            dY = -4;
        }
            
        // On tombe
        if(!groundCollision) {
            dY += (9.81 / Framework.MAX_FPS) * dYAcceleration;
        }
        // On arrête de tomber
        else if(groundCollision && dY > 0) {
            dY = 0;
        }

        // Mise à jour déplacement
        x += dX;
        y += dY;

        // Limitation déplacement en fonction des collisions
        if(y > groundLevel) {
            y = groundLevel;
        }
        if(x < 0) {
            x = 0;
        }
        else if(x > wallPosition) {
            x = wallPosition;
        }
            
        // Mise à jour de l'animation du sprite
        currentSprite += SpriteFrames.get(currentAnimation) / (Framework.MAX_FPS * SpriteDuration.get(currentAnimation));

        // Si l'animation est terminé on reboucle au début
        if(currentSprite >= SpriteFrames.get(currentAnimation)) {
            currentSprite = 0;
                
            // Si c'était une animation de rechargement
            if(currentAnimation == SpriteAnimation.RELOADING) {
                // Quand elle est fini le rechargement est effectué
                reloadGun();
            }
            
            // Si c'était une animation de tir ou de rechargement on retourne à l'animation de base (réspiration)
            if(currentAnimation == SpriteAnimation.FIRING || currentAnimation == SpriteAnimation.RELOADING) {
                currentAnimation = SpriteAnimation.BREATHING;
            }
        }
    }
    
    
    /**
     * Mise à jour du rendu du joueur
     */
    public void draw(Graphics2D g2d) {
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
        
        // Munitions
        g2d.setColor(new Color(150, 150,150));
        g2d.drawString("Ammo : "+ammo, x - 18, y - 10);
    }
    
    
    /**
     * fires Getter
     */
    public ArrayList<Fire> getFires() {
        return fires;
    }
    
}
