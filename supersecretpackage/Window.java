package supersecretpackage;

import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 * Creates frame and set its properties
 */
public class Window extends JFrame {
    
    private static Window self = null;
    private boolean fullScreenMode = false;
        
    private Window() {
        
        setTitle("Super Secret Project");
        
        // Full screen mode
        if(fullScreenMode) {
            setUndecorated(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        // Window mode
        else {
            setSize(800, 600);
            setLocationRelativeTo(null);
            setResizable(false);
        }
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new Framework());
        setVisible(true);
    }
    
    
    public static Window get() {
        if(self == null) {
            self = new Window();
        }
        return self;
    }
    
    
    public static void close() {
        if(self != null) {
            WindowEvent wev = new WindowEvent(self, WindowEvent.WINDOW_CLOSING);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
            self.setVisible(false);
            self.dispose();
            System.exit(0);
        }
    }

}
