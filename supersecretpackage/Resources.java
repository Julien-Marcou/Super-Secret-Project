package supersecretpackage;

import java.net.URL;

/**
 * Classe d'aide à l'utilisation de ressources externes
 */
public class Resources {
    
    public static final String root = "/";
    public static final String prefix = "supersecret";
    public static final Class src = Resources.class.getClass();
    
    public static URL get(String url) {
        
        // Pour récupère une image "test.jpg" du dossier "images",
        // Faire : Ressources.get("image/test.jpg");
        // Tout les dossiers des ressources peuvent être préfixé
        
        url = url
            .replaceFirst("image", prefix+"images")
            .replaceFirst("video", prefix+"videos")
            .replaceFirst("sound", prefix+"sounds")
            .replaceFirst("music", prefix+"musics")
            .replaceFirst("texture", prefix+"textures");
        return src.getResource(root+url);
    }
    
}
