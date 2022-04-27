package lmzr.photomngr.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.Future;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import lmzr.photomngr.data.DataFormat;
import lmzr.photomngr.data.Photo;
import lmzr.photomngr.data.PhotoProvider;
import lmzr.photomngr.imagecomputation.ImageComputationConsumer;
import lmzr.photomngr.imagecomputation.ImageComputationManager;
import lmzr.photomngr.imagecomputation.ImageComputationParameters;
import lmzr.photomngr.imagecomputation.SubsampledImageCachedManager;
import lmzr.photomngr.scheduler.Scheduler;
import lmzr.photomngr.ui.action.StartPlayerAction;
import lmzr.photomngr.ui.player.Player;
import lmzr.photomngr.ui.player.PlayerFactory;
import lmzr.photomngr.ui.player.Player_myself;

/**
 * @author Laurent Mazuré
 */
public class PhotoDisplayerComponentSlot extends JComponent
                                         implements ImageComputationConsumer, ComponentListener, PhotoProvider {

    final static private DateFormat s_dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL);
    final static private PhotoDisplayerComponentFontManager a_fontManager = new PhotoDisplayerComponentFontManager();
    final private ImageComputationManager a_computationManager;

    private Photo a_photo;
    private ImageComputationParameters a_params;
    private BufferedImage a_image;
    private boolean a_imageIsComputed;
    private Future<?> a_computation;
    private JPanel a_display;
    final private JButton a_play[];

    /**
     * @param scheduler
     * @param subsampler
     * @param computationManager
     */
    public PhotoDisplayerComponentSlot(final Scheduler scheduler,
                                       final SubsampledImageCachedManager subsampler,
                                       final ImageComputationManager computationManager) {
        super();

        this.a_computationManager = computationManager;

        final GridBagLayout gridbag = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.CENTER;
        gridbag.setConstraints(this, constraints);
        setLayout(gridbag);

        addComponentListener(this);
        this.a_photo = null;
        this.a_params = null;
        this.a_image = null;
        this.a_imageIsComputed = false;
        this.a_computation = null;

        this.a_display = new JPanel();
        this.a_display.setLayout(new BoxLayout(this.a_display, BoxLayout.Y_AXIS));
        this.a_display.setOpaque(false);
        final PlayerFactory playerFactory = new PlayerFactory();
        final Player[] players = playerFactory.getPlayers();
        this.a_play = new JButton[players.length];
        for (int i=0; i<players.length; i++) {
            this.a_play[i] = new JButton(new StartPlayerAction( players[i].getName(),
                                                           KeyEvent.CHAR_UNDEFINED,
                                                           null,
                                                           "start "+players[i].getName(),
                                                           this,
                                                           players[i]));
            this.a_play[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            this.a_play[i].setAlignmentY(Component.CENTER_ALIGNMENT);
            this.a_display.add(this.a_play[i]);
        }
        this.a_display.setMaximumSize(this.a_display.getPreferredSize());
        add(this.a_display);
    }

    /**
     * @param photo data to display (set to null is the slot does not display anything)
     */
    public void setPhoto(final Photo photo) {

        // cancel the current computation if required
        if ( this.a_computation != null ) {
            this.a_computation.cancel(false);
            this.a_computation = null;
        }

        this.a_photo = photo;
        this.a_image = null;
        this.a_imageIsComputed = false;

        if (this.a_photo==null ) {
            // the slot does not display anything -> repaint the slot as empty
            this.a_display.setVisible(false);
            repaint();
            return;
        }

        final DataFormat format = this.a_photo.getFormat();

        if (!(new Player_myself()).isFormatSupported(format)) {
            for (int i=0; i<this.a_play.length; i++) {
                final StartPlayerAction action = (StartPlayerAction)this.a_play[i].getAction();
                action.setEnabled( (this.a_photo!=null) && action.getPlayer().isFormatSupported(format) );
            }
            this.a_display.setVisible(true);
        } else {
            this.a_display.setVisible(false);
        }

        if (this.a_photo==null) {
            // unsupported format
            repaint();
        } else {
            // display a photo
            if (getWidth()>0 && getHeight()>0) callImageComputation();
        }
    }

    /**
     *
     */
    public void update() {

        if ( this.a_photo!=null && this.a_photo.getFormat()==DataFormat.JPEG ) callImageComputation();
    }

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    public void paintComponent(final Graphics g) {

        final Graphics2D g2 = (Graphics2D)g;
        g2.setBackground(Color.BLACK);

        if ( this.a_photo == null ) {
            // no data is attached to this slot
            g2.clearRect(0,0,getWidth(),getHeight());
            return;
        }

        if ( this.a_imageIsComputed ) {
            repaintImage(g2);
        } else  {
            final Font font = a_fontManager.getMessageFont(getSize());
            if (font ==null) return;
            g2.setFont(font);
            final FontMetrics metrics = g2.getFontMetrics();
            g2.setColor(Color.BLACK);
            String str;
            if ((new Player_myself()).isFormatSupported(this.a_photo.getFormat())) {
                str = "Loading...";
            } else {
                str = this.a_photo.getFormat().toString()+" is not supported";
            }
            final int width = metrics.stringWidth(str);
            g2.drawString(str, getSize().width/2-width/2, getSize().height/2+metrics.getAscent() );
        }
    }

    /**
     * @see javax.swing.JComponent#paintBorder(java.awt.Graphics)
     */
    @Override
    public void paintBorder(final Graphics g) {
        super.paintBorder(g);
    }

    /**
     * @see javax.swing.JComponent#paintChildren(java.awt.Graphics)
     */
    @Override
    public void paintChildren(final Graphics g) {
        super.paintChildren(g);
    }

    /**
     * @param g
     */
    private void repaintImage(final Graphics2D g) {

        g.setBackground(Color.BLACK);

        if ( this.a_image != null ) {

            //TODO see how to handle deleted files (JPEG or other)

            g.drawImage(this.a_image, 0, 0, this);

            final Font font = a_fontManager.getAnnotationFont(getSize());
            if ( font == null ) return;

            g.setFont(font);
            g.setColor(Color.RED);

            final FontMetrics metrics = g.getFontMetrics();
            final int height = metrics.getHeight();

            final String location = this.a_photo.getIndexData().getLocation().toLongString();
            if (location!=null) {
                final int widthLocation = metrics.stringWidth(location);
                g.drawString( this.a_photo.getIndexData().getLocation().toLongString(), getSize().width/2-widthLocation/2, 1+metrics.getAscent() );
            }

            final Date date = this.a_photo.getHeaderData().getDate();
            if ( date != null ) {
                final String str = s_dateFormat.format(date);
                final int widthDate = metrics.stringWidth(str);
                g.drawString(str, getSize().width/2-widthDate/2, 1+metrics.getAscent()+height );
            }
        }
    }

    /**
     * @see lmzr.photomngr.imagecomputation.ImageComputationConsumer#consumeImageComputation(lmzr.photomngr.data.Photo, lmzr.photomngr.imagecomputation.ImageComputationParameters, java.awt.image.BufferedImage)
     */
    @Override
    public void consumeImageComputation(final Photo photo,
                                        final ImageComputationParameters params,
                                        final BufferedImage image) {

        // check that this is the up-to-date answer
        if ( photo!=this.a_photo || params!=this.a_params ) return;

        this.a_image = image;
        this.a_imageIsComputed = true;
        this.a_computation = null;
        repaint();
    }

    /**
     * launch the computation of the image
     */
    private void callImageComputation() {

        if ( this.a_photo!=null ) {
            final ImageComputationParameters params = new ImageComputationParameters(getSize().width,
                                                                                        getSize().height,
                                                                                     this.a_photo.getIndexData().getZoom(),
                                                                                     this.a_photo.getIndexData().getRotation(),
                                                                                     this.a_photo.getIndexData().getFocusX(),
                                                                                     this.a_photo.getIndexData().getFocusY());
            if ( this.a_params!=params ) {
                if ( this.a_computation!=null ) {
                    this.a_computation.cancel(false);
                }
                this.a_params = params;
                this.a_computation = this.a_computationManager.compute(this.a_photo,this.a_params,this,true);
            }
        }

        repaint();
    }

    /**
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentResized(final ComponentEvent e) {
        callImageComputation();
    }

    /**
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentMoved(final ComponentEvent e) {
    	// do nothing
    }

    /**
     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
    	// do nothing
     */
    @Override
    public void componentShown(final ComponentEvent e) {
    	// do nothing
    }

    /**
     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentHidden(final ComponentEvent e) {
    	// do nothing
    }

    /**
     * @see lmzr.photomngr.data.PhotoProvider#getPhoto()
     * @return photo currently displayed
     */
    @Override
    public Photo getPhoto()
    {
        return this.a_photo;
    }
}
