/**
 * ZoomButton ist ein spezialisierter JButton, welcher bevorzugt
 * PNG-Files (o.ä. Dateiformate) als Icon haben solle, da der Hintergrund immer Transparent ist.
 * Somit wird ein ZoomButton immer ausschließlich durch sein Icon (RolloverIcon,
 * SelectedIcon, usw.) dargestellt.
 * 
 * Das Zoomen des Buttons erfolg durch das Setzen des Zoomfaktors (aktZoom)
 * Als Defalutwert besitzt jeder instanziierte ZoomButton die Originalegroesse
 * des benutzen Icons (100%).
 * 
 * Durch das Veraendern des actZoom mit Hilfe der setButtonSize - Methode
 * kann nun jederzeit die Buttongroesse prozentual geaedert werden.
 * z.B. wuerde setButtonSize(50) die ZoomButtonGroesse halbieren (falls diese zuvor
 * auf 100 gesetzt war.
 * 
 * 
 */
package playmyworld.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

public class ZoomButton extends JToggleButton {

    private int transparentRGB;
    private BufferedImage image;
    protected ImageIcon icon;
    protected String name;
    protected String id;
    protected int actZoom;
    protected int maxZoom;
    protected int minZoom;
    private boolean isRollOver = false;
    final private int buttonWidth; //Defaultbreite des Buttons
    final private int buttonHeight;//Defaulthoehe des Buttons

    public ZoomButton(ImageIcon icon, String id, String name) {
        super(icon);
        this.name = name;
        this.icon = icon;
        this.id = id;
        transparentRGB = new Color(255, 255, 255, 0).getAlpha();
        actZoom = 100;//Default 100% Bildgroesse
        maxZoom = 500;
        minZoom = 10;
        setIcon(icon);
        image = this.getBufferedImage();
        setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
        setMargin(new Insets(0, 0, 0, 0));
        /*Rahmen nicht zeichnen*/
        setBorderPainted(false);
        /*Transparenz nicht fuellen*/
        setContentAreaFilled(false);
        buttonWidth = getIcon().getIconWidth(); //...soll als default die Iconbreite haben
        buttonHeight = getIcon().getIconHeight();//...soll als default die Iconhoehe haben
        /*
         * Anonymer MouseAdapter, welcher den Rolloverstatus erkennt und aktiviert
         * bzw. deaktiviert
         */
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                setIsRollOver(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setIsRollOver(false);
            }
        });
    }

    @Override
    public boolean contains(int posX, int posY) {
        if (super.contains(posX, posY)) {
            posX = (int) ((posX / (double) getWidth()) * buttonWidth);
            posY = (int) ((posY / (double) (getHeight()) * buttonHeight));
            return !(transparentRGB == image.getRGB(posX, posY));
        }
        return false;
    }

    /**
     * Erstellt das BufferedImage, welches in der ContainsMethode
     * als Referenz fuer das Enthaltensein der Maus zur Hilfe genommen wird
     * @return BufferedImage
     */
    private BufferedImage getBufferedImage() {
        BufferedImage image = new BufferedImage(icon.getIconWidth(),
                icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(icon.getImage(), 0, 0,
                null);
        return image;
    }

    public void setButtonSize(int imgSize) {
        if (imgSize >= getMinZoom() && imgSize <= getMaxZoom()) {
            this.actZoom = imgSize;
            setSize((int) (buttonWidth * this.actZoom / 100.0), (int) (buttonHeight * this.actZoom / 100.0));
        }
    }

    public int getImgSize() {
        return this.actZoom;
    }

    private int getActX() {
        return (int) (getWidth() / 2.0) - (int) (getIcon().getIconWidth() * actZoom / 100.0) / 2;
    }

    private int getActY() {
        return (int) (getHeight() / 2.0) - (int) (getIcon().getIconHeight() * actZoom / 100.0) / 2;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (isSelected() && getIsRollOver() && getSelectedIcon() != null && isEnabled()) {
            g2d.drawImage(((ImageIcon) getSelectedIcon()).getImage(), getActX(), getActY(), (int) (getRolloverIcon().getIconWidth() * actZoom / 100.0), (int) (getRolloverIcon().getIconHeight() * actZoom / 100.0), null);
        } else if (isSelected() && getSelectedIcon() != null) {
            g2d.drawImage(((ImageIcon) getSelectedIcon()).getImage(), getActX(), getActY(), (int) (getSelectedIcon().getIconWidth() * actZoom / 100.0), (int) (getSelectedIcon().getIconHeight() * actZoom / 100.0), null);
        } else if (getIsRollOver() && getRolloverIcon() != null && isEnabled()) {
            g2d.drawImage(((ImageIcon) getRolloverIcon()).getImage(), getActX(), getActY(), (int) (getRolloverIcon().getIconWidth() * actZoom / 100.0), (int) (getRolloverIcon().getIconHeight() * actZoom / 100.0), null);
        } else {
            g2d.drawImage(((ImageIcon) getIcon()).getImage(), getActX(), getActY(), (int) (((ImageIcon) getIcon()).getIconWidth() * actZoom / 100.0), (int) (((ImageIcon) getIcon()).getIconHeight() * actZoom / 100.0), null);
        }
    }

    public void setMaxZoom(int maxZoom) {
        this.maxZoom = maxZoom;
    }

    public int getMaxZoom() {
        return maxZoom;
    }

    public void setMinZoom(int minZoom) {
        this.minZoom = minZoom;
    }

    public int getMinZoom() {
        return minZoom;
    }

    public void setButtonName(String landName) {
        this.name = landName;
    }

    public String getButtonName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getIsRollOver() {
        return isRollOver;
    }

    public void setIsRollOver(boolean isRollOver) {
        this.isRollOver = isRollOver;
    }
}
