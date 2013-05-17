/*
 * Transparentes Panel auf welchem die Komponenten der Landkarte liegen.
 * Es wird eine Liste von ZoomButtons erwartet, welche jedoch nicht bei der 
 * instanziirung sonders erst durch die Methode addComponents zum
 * Panel hinzugefuegt werden
 * 
 */
package playmyworld.gui;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicBorders.ButtonBorder;

/**
 *
 * @author Marci
 */
public class MapPanel extends JPanel {

    private ArrayList<ZoomButton> buttonList;
    int defaultPanelWidth;
    int defaultPanelHeight;
    protected int actZoom;
    protected int minZoom;
    protected int maxZoom;

    public MapPanel(ArrayList<ZoomButton> buttonList) {
        this.buttonList = buttonList;
        actZoom = 100;
        minZoom = 10;
        maxZoom = 200;
        setLayout(null);
        defaultPanelWidth = buttonList.get(0).getWidth();
        defaultPanelHeight = buttonList.get(0).getHeight();
        //Default Panelgroesse entspricht der Groesse der ersten Komponente 
        setSize(defaultPanelWidth, defaultPanelHeight);
        setOpaque(false);

    }
    /**
     * Sofern eine Instanz dieses Panel einder anderen Komponente
     * hinzugefuegt wurde, wird hier die Groesse der Instanz an
     * die Breite der Komponente angepasst
     */
    public void matchToParentSize() {
        if (getParent() != null) {
            int iconHeight = buttonList.get(0).getIcon().getIconHeight();
            actZoom = (int) (((double) getParent().getHeight() / iconHeight) * 100.0);
            for (ZoomButton actButton : buttonList) {
                actButton.setButtonSize(actZoom);
            }
            setSize((int) (defaultPanelWidth * actZoom / 100.0), (int) (defaultPanelHeight * actZoom / 100.0));
        }

    }
    /**
     * Fuegt die Komponenten der buttonList zum Panel hinzu
     */
    public void addComponents() {
        matchToParentSize();
        for (ZoomButton actButton : buttonList) {
            add(actButton);
        }
    }

    /**
     * @return the buttonList
     */
    public ArrayList<ZoomButton> getButtonList() {
        return buttonList;
    }

    /**
     * @param buttonList the buttonList to set
     */
    public void setButtonList(ArrayList<ZoomButton> buttonList) {
        this.buttonList = buttonList;
    }

    /**
     * @return the minZoom
     */
    public int getMinZoom() {
        return minZoom;
    }

    /**
     * @param minZoom the minZoom to set
     */
    public void setMinZoom(int minZoom) {
        this.minZoom = minZoom;
    }

    /**
     * @return the maxZoom
     */
    public int getMaxZoom() {
        return maxZoom;
    }

    /**
     * @param maxZoom the maxZoom to set
     */
    public void setMaxZoom(int maxZoom) {
        this.maxZoom = maxZoom;
    }
}
