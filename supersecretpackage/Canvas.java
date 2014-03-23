package supersecretpackage;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * Canvas principale pour le framework
 */
public abstract class Canvas extends JPanel implements KeyListener, MouseListener {

    // Pour préciser si le curseur sera visible ou non dans le Canvas
    boolean removeCursor = false;

    // L'état des 525 touches du clavier : enfoncée ou non
    private static boolean[] keyboardState = new boolean[525];

    // L'état des 3 touches de la souris (+1) : enfoncée ou non
    private static boolean[] mouseState = new boolean[4];

    public Canvas() {
        setDoubleBuffered(true); // (VSync ?)
        setFocusable(true);
        setBackground(Color.black);

        // Si on doit supprimer le curseur dans le Canvas
        if(removeCursor) {
            BufferedImage blankCursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(blankCursorImg, new Point(0, 0), null);
            setCursor(blankCursor);
        }

        addKeyListener(this);
        addMouseListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        super.paintComponent(g2d);
        draw(g2d);
    }


    // Cette méthode remplace la méthode "paint(Graphics g)" par défaut d'un Canvas pour avoir directement le Graphics2D
    public abstract void draw(Graphics2D g2d);


    public static boolean keyState(int key) {
        return keyboardState[key];
    }

    public static boolean mouseState(int button) {
        return mouseState[button];
    }


    @Override
    public void keyPressed(KeyEvent e) {
        keyboardState[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyboardState[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Void
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseState[e.getButton()] = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseState[e.getButton()] = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Void
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Void
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Void
    }

}
