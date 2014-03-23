package supersecretpackage;

import javax.swing.SwingUtilities;

/**
 * Point d'entrée du framework
 */
public class Main {
    public static void main(String[] args) {
        // Utilisation de l'évènement d'envoi du thread pour construire l'UI en thread-safety.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Window.get(); // Singleton
            }
        });
    }
}
