/*
 * Eine abgeleitete Klasse von MapPanel, welche neben den Eigenschaften 
 * des MapPanels als Funktionen das Scrollen und Zoomen des Panels zur
 * Verfuegung stellt
 */
package playmyworld.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Panel das mit seinen ZoomButtons gezoomt werden kann
 * @author Marci
 */
public class ZoomPanel extends MapPanel {

    private final int zoomStep = 10;
    protected JButton zoom;
    protected JButton zoomOut;
    protected Timer timer;
    private boolean editable; //ob Panel zoom- und scrollbar sein soll

    public ZoomPanel(ArrayList<ZoomButton> componentList) {
        super(componentList);
        setEditable(true);
        //this.setActZoom(landZoom);
        MovingAdapter ma = new MovingAdapter();
        addMouseMotionListener(ma);
        addMouseListener(ma);
        addMouseWheelListener(ma);
        matchToParentSize();
    }

    /**
     * @return the defaultPanelWidth
     */
    public int getPanelWidth() {
        return getWidth();
    }

    /**
     * @return the defaultPanelHeight
     */
    public int getPanelHeight() {
        return getHeight();
    }

    /**
     * Die Anpassung an die Elternkomponente soll hier zentriert erfolgen und
     * muss daher über schrieben werden
     */
    @Override
    public void matchToParentSize() {
        if (getParent() != null) {
            setSize((int) (defaultPanelWidth * actZoom / 100.0) + getParent().getWidth() * 2, (int) (defaultPanelHeight * actZoom / 100.0) + getParent().getHeight() * 2);
            //gesamtes Panel an Frame zentrieren
            setLocation(getParent().getWidth() / 2 - (getPanelWidth() / 2), getParent().getHeight() / 2 - (getPanelHeight() / 2));
            //Buttongroesse an Frame anpassen
            int iconHeight = getButtonList().get(0).getIcon().getIconHeight();
            actZoom = (int) (((double) getParent().getHeight() / iconHeight) * 100.0);
            minZoom = actZoom;
            maxZoom = minZoom * 2;
            for (ZoomButton actButton : getButtonList()) {
                actButton.setButtonSize(actZoom);
                actButton.setLocation(getPanelWidth() / 2 - (actButton.getWidth() / 2), getPanelHeight() / 2 - (actButton.getHeight() / 2));
                actButton.repaint();
            }
        }
    }

    /**
     * @return the editable
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * @param editable the editable to set
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * Innere Klasse, die als Mausadapter das Scrollen und Zoomen uebernimmt
     */
    private class MovingAdapter extends MouseAdapter {

        private int pressX;
        private int pressY;

        /**
         * Zusammen mit mouseDragged fuer das Scrollen zustaendig
         * @param e 
         */
        @Override
        public void mousePressed(MouseEvent e) {
            if (editable) {
                pressX = e.getX();
                pressY = e.getY();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (editable) {
                int actX = e.getX();
                int actY = e.getY();

                int panelX = getLocation().x;
                int panelY = getLocation().y;
                int dx = actX - pressX;
                int dy = actY - pressY;
                //Falls ueberall Scrollen erlaubt - scrolle ohne Einschraenkung
                if (scrollInAnyDirection()) {
                    setLocation(panelX + dx, panelY + dy);
                    //Sonst beachte Einschränkungen
                } else {
                    int midX = getX() + getWidth() / 2;
                    int midY = getY() + getHeight() / 2;
                    //linksoben angedockt
                    if (midX <= 0 && midY <= 0) {
                        if (dx < 0 && dy < 0) {
                            setLocation(getX(), getY());
                        } else if (dx < 0) {
                            setLocation(getX(), panelY + dy);
                        } else if (dy < 0) {
                            setLocation(panelX + dx, getY());
                        } else {
                            setLocation(panelX + dx, panelY + dy);
                        }
                    } //rechtsoben angedockt
                    else if (midX >= getParent().getWidth() && midY <= 0) {
                        if (dx > 0 && dy < 0) {
                            setLocation(getX(), getY());
                        } else if (dx > 0) {
                            setLocation(getX(), panelY + dy);
                        } else if (dy < 0) {
                            setLocation(panelX + dx, getY());
                        } else {
                            setLocation(panelX + dx, panelY + dy);
                        }
                    } //linksunten angedockt
                    else if (midX <= 0 && midY >= getParent().getHeight()) {
                        if (dx < 0 && dy > 0) {
                            setLocation(getX(), getY());
                        } else if (dx < 0) {
                            setLocation(getX(), panelY + dy);
                        } else if (dy > 0) {
                            setLocation(panelX + dx, getY());
                        } else {
                            setLocation(panelX + dx, panelY + dy);
                        }
                    } //rechtsunten angedockt
                    else if (midX >= getParent().getWidth() && midY >= getParent().getHeight()) {
                        if (dx > 0 && dy > 0) {
                            setLocation(getX(), getY());
                        } else if (dx > 0) {
                            setLocation(getX(), panelY + dy);
                        } else if (dy > 0) {
                            setLocation(panelX + dx, getY());
                        } else {
                            setLocation(panelX + dx, panelY + dy);
                        }
                    } //links angedockt
                    else if (midX <= 0) {
                        if (dx < 0) {
                            setLocation(getX(), panelY + dy);
                        } else {
                            setLocation(panelX + dx, panelY + dy);
                        }
                    } //rechts angedockt
                    else if (midX >= getParent().getWidth()) {
                        if (dx > 0) {
                            setLocation(getX(), panelY + dy);
                        } else {
                            setLocation(panelX + dx, panelY + dy);
                        }
                    } //oben angedockt
                    else if (midY <= 0) {
                        if (dy < 0) {
                            setLocation(panelX + dx, getY());
                        } else {
                            setLocation(panelX + dx, panelY + dy);
                        }
                    } //unten angedockt
                    else if (midY >= getParent().getHeight()) {
                        if (dy > 0) {
                            setLocation(panelX + dx, getY());
                        } else {
                            setLocation(panelX + dx, panelY + dy);
                        }
                    }
                }
                repaint();
            }
        }

        /**
         * Uebernimmt das Zoomen per Mausrad
         * @param e 
         */
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (editable) {
                int notches = e.getWheelRotation();

                if (notches < 0) {
                    zoomIn();
                } else {
                    zoomOut();
                }
                //setLocation(getParent().getWidth() / 2 - (getPanelWidth() / 2), getParent().getHeight() / 2 - (getPanelHeight() / 2));
            }
        }
    }

    /**
     * Hilfsmethode für das Scrollen des Mausadapters.
     * Ermittelt, ob man beim Scrollen am Randbereich des Panels angedockt ist
     * bzw. wenn dies nicht der Fall ist, dass man in alle Richtungen Scrollen darf
     * 
     * @return true - wenn in alle Richtungen gescrollt werden darf
     *         false - sonst
     */
    private boolean scrollInAnyDirection() {
        int midX = getX() + getWidth() / 2;
        int midY = getY() + getHeight() / 2;

        return midX > 0 && midX < getParent().getWidth() && midY > 0 && midY < getParent().getHeight();
    }

    /**
     * Skaliert alle Buttons um den Wert scale (in Prozent)
     * und zentriert diese relativ zur instanz des ZoomPanels
     * @param scale - Skalierung in Prozent
     */
    public void scale(int scale) {
        actZoom = scale;
        for (ZoomButton bt : getButtonList()) {
            //Skalieren
            bt.setButtonSize(actZoom);
            //Zentrieren
            bt.setLocation(getPanelWidth() / 2 - (bt.getWidth() / 2), getPanelHeight() / 2 - (bt.getHeight() / 2));
        }
    }

    /**
     * Vergrössert Buttons um vorgegebenen Zoomschritt (zoomStep)
     */
    public void zoomIn() {
        if (actZoom <= getMaxZoom() - zoomStep) {
            //actZoom += zoomStep;
            scale(actZoom + zoomStep);
        }
        setLocation(getParent().getWidth() / 2 - (getPanelWidth() / 2), getParent().getHeight() / 2 - (getPanelHeight() / 2));

    }

    /**
     * Verkleinert Buttons um vorgegebenen Zoomschritt (zoomStep)
     */
    public void zoomOut() {
        if (actZoom > minZoom) {
            //actZoom -= zoomStep;
            scale(actZoom - zoomStep);
        }
        setLocation(getParent().getWidth() / 2 - (getPanelWidth() / 2), getParent().getHeight() / 2 - (getPanelHeight() / 2));

    }

    /**
     * 
     */
    /**
     *Fügt sofort alle Komponenten ein.
     */
    public void addComponentsInstant() {
        matchToParentSize();
        setLocation(getParent().getWidth() / 2
                - (getPanelWidth() / 2), getParent().getHeight()
                / 2 - (getPanelHeight() / 2));
        setSize((int) (getPanelWidth() * actZoom / 100.0), (int) (getPanelHeight() * actZoom / 100.0));
        for (ZoomButton c : getButtonList()) {
            this.add(c);
        }
    }

    /**
     * Entfernt alle Buttons von der ZoomPanel Instanz
     */
    public void removeComponents() {
        for (ZoomButton c : getButtonList()) {
            this.remove(c);
        }
    }

    /**
     * Fügt per Timer alle Komponenten nacheinander mit einer Verzögerung (delay) ein.
     * 
     * @param delay - Verzögerung in Milisekunden
     */
    public void addComponentsDelayed(int delay) {
        matchToParentSize();
        // relativ zum layouPanel mittig ausrichten
        setLocation(getParent().getWidth() / 2
                - (getPanelWidth() / 2), getParent().getHeight()
                / 2 - (getPanelHeight() / 2));
        // Timer für schrittweise Laenderanzeige
        timer = new Timer(delay, new ActionListener() {

            int count = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (count > getButtonList().size() - 1) {
                    for (JComponent c : getButtonList()) {
                        c.setEnabled(true);
                    }
                    timer.stop();
                } else {
                    ZoomButton actButton = getButtonList().get(count);
                    actButton.setLocation(getPanelWidth() / 2 - (actButton.getWidth() / 2), getPanelHeight() / 2 - (actButton.getHeight() / 2));
                    actButton.setToolTipText(actButton.getButtonName());
                    actButton.setEnabled(false);
                    add(actButton);
                    repaint();
                    count++;
                }
            }
        });
        timer.start();

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

    }

    /**
     * 
     * @return alle Buttons des Panels in einer Liste
     */
    public ArrayList<ZoomButton> getKontinenetButtonList() {
        return getButtonList();
    }

    /**
     * 
     * @param kontinenetButtonList - die Buttons welche das Panel verwalten soll 
     */
    public void setKontinenetButtonList(ArrayList<ZoomButton> kontinenetButtonList) {
        this.setButtonList(kontinenetButtonList);
    }
}
