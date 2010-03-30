//  1: le mode plein écran marche mal
//  2: l'image selectionnée n'est pas visible dans la liste au démarrage dans la table
//  3: implementer le prefetch des images
//  5: faire une revue de code du multi-threading
//     verifier qu'il est correct
//     verifier que les lectures sont elles aussi synchronized
//     verifier que le callback de computation arrive dans le thread principal
// 13: corriger le bordel sur les PhotoTraits
//     (supprimer au moins le bordel de INTEGER_MIN)
//     et surtout FilterOnPhotoTrait.filter qui est innomable
// 14: faire test avec 20 threads tournant en simultané
//     apparemment cela fait repparaitre le probleme d'out of memory
// 18: creer une fenetre de debug qui peut afficher toutes les valeurs des tags d'une photo
// 19: ne pas indiquer le nom du folder lorsque des images de differents folders sont affiches
// 21: ajout/retrait de colonne
// 24: il doit y avoir une race condition entre le resize de l'editor et l'affichage de la photo:
//     l'image reste parfois en loading indéfiniment
//   31: ajouter un bouton dans l'Editor pour remettre le focus à (0,0) et le zoom à 1
//       (appeler le bouton "fit")
//   32: les titres des filtres subject & location sont mal disposés quand ceux-ci sont énablés
//   35: quand on tape Ctrl-L dans la table, cela ne devrait pas commencer une édition de cellule
//   39: ajouter select all / unselect all dans les filtres
//   40: le paging avec la molette ne marche plus après un Ctrl-E suivi d'un second Ctrl-E
//   41: l'édition de la location ne marche pas dans la liste
//       et marche mal dans l'éditor (ce n'est pris en compte que lorsque qu'on pagine à une autre image)
//       il y a sans doute le même problème avec l'édition du sujet)
//   42: il manque la bordure lorsqu'on sélectionne la location ou le subject dans la table
//   43: ajouter compteur d'images en bas de la table (total/filtered/selected)
//   0045: l'affichage de la liste est pourri en cas de filtre qui ne matche avec rien
//   0048: créer le SubjectCellEditor
//   0049: rendre le SubjectCellEditor et LocationCellEditor accessible depuis le PhotoEditor
//   0054 - les modifications ne sont pas mises à jour sur l'éditeur
//   0055 - le filtre sur les copies est mal affiché
//   0056 - le paging avec la molette ne marche plus après une rotation avec Alt+molette
//   0057 - blocage parfois si deux vidéos d'affilée
//   0058 - les panoramas ne s'affichent pas
//   0059 - le focus est perdu après une rotation
//   0062 - implémenter un mode défillement automatique (aléatoire ou non) ds images (faire gaffe aux vidéos)



package lmzr.photomngr.ui;

import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.WindowConstants;

import lmzr.photomngr.data.ConcretePhotoList;
import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.data.filter.FilteredPhotoList;
import lmzr.photomngr.imagecomputation.SubsampledImageCachedManager;
import lmzr.photomngr.scheduler.Scheduler;

/**
 * @author Laurent Mazuré
 */
public class Main implements WindowListener {

	private final PhotoDisplayer a_displayer;
	
    /**
     * @param args
     */
    public static void main(String[] args) {
        new Main(args[0],args[1]);
    }
    
    /**
     * @param root directory where are the photo folders and the index file
     * @param cache directory where the cached images are stored
     */
    public Main(final String root,
    		    final String cache) {

        final String s_root = root;
        
        final Scheduler scheduler = new Scheduler();

        final ConcretePhotoList a_list = new ConcretePhotoList();
        final FilteredPhotoList a_filteredList = new FilteredPhotoList(a_list);

        final GPSDatabase a_GPSDatabase = new GPSDatabase(s_root+File.separator+"gps.txt", a_list.getLocationFactory());
        
        final PhotoListDisplay a_listDisplay = new PhotoListDisplay(a_list,a_filteredList);
        final ListSelectionManager selection = new ListSelectionManager(a_filteredList,a_listDisplay.getLineSelectionListModel());
        
        a_displayer = new PhotoDisplayer(scheduler, a_filteredList, a_GPSDatabase, new SubsampledImageCachedManager(cache), selection);
        
        final int i = a_list.getRowCount()-1;
        a_listDisplay.getLineSelectionListModel().setSelectionInterval(i,i);

        
        a_listDisplay.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        a_listDisplay.addWindowListener(this);
        a_listDisplay.setBounds(new Rectangle(0,0,1280,300));
        a_listDisplay.setVisible(true);
        
        a_displayer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        a_displayer.addWindowListener(this);
        a_displayer.setBounds(new Rectangle(100,300,1000,720));
        a_displayer.setVisible(true);
        
        a_list.initialize(s_root+File.separator+"photo_ref.txt", s_root, scheduler);
    }

    /**
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    public void windowOpened(@SuppressWarnings("unused") final WindowEvent e) {        
        //
    }

    /**
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    public void windowClosing(@SuppressWarnings("unused") final WindowEvent e) {
        a_displayer.controlledExit(); //TODO dirty hack
    }

    /**
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    public void windowClosed(@SuppressWarnings("unused") final WindowEvent e) {
        //
    }

    /**
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    public void windowIconified(@SuppressWarnings("unused") final WindowEvent e) {
        //
    }

    /**
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    public void windowDeiconified(@SuppressWarnings("unused") final WindowEvent e) {
        //
    }

    /**
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    public void windowActivated(@SuppressWarnings("unused") final WindowEvent e) {
        //
    }

    /**
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    public void windowDeactivated(@SuppressWarnings("unused") final WindowEvent e) {
        //
    }
}
