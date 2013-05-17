package playmyworld.gui;

/**
 * Spezialisierter Button, der ein Dropdown-Menue (JPopupMenue)
 * beim Klick oeffnet, welches dann mit Einträgen (JMenuItem)
 * versehen werden kann
 */
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class PopupButton extends JButton {

    private JPopupMenu menu;
    private boolean popupVisible;

    public PopupButton(Icon icon) {
        super(icon);
        init();
    }

    private void init() {
        super.setModel(new DefaultButtonModel() {

            @Override
            public boolean isPressed() {
                return isPopupVisible() || super.isPressed();
            }

            @Override
            public boolean isArmed() {
                return isPopupVisible() || super.isArmed();
            }

            @Override
            public boolean isRollover() {
                return isPopupVisible() || super.isRollover();
            }
        });

        popupVisible = false;

        menu = new JPopupMenu();
        menu.addPopupMenuListener(new PopupListener(this));

        addActionListener(new PopupHandler(this, menu));
    }

    public JPopupMenu getMenu() {
        return menu;
    }

    public void setPopupVisible(boolean isVisible) {
        popupVisible = isVisible;
    }

    public boolean isPopupVisible() {
        return popupVisible;
    }

    private class PopupHandler implements ActionListener {

        private PopupButton button;
        private JPopupMenu menu;

        public PopupHandler(PopupButton b, JPopupMenu m) {
            button = b;
            menu = m;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            menu.show(button, 0, button.getHeight());
        }
    }

    private class PopupListener implements PopupMenuListener {

        PopupButton button;

        public PopupListener(PopupButton b) {
            button = b;
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            button.setPopupVisible(false);
        }

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            button.setPopupVisible(true);
        }
    }
}