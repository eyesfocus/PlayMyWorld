
/*
 * PlayMyWorld.java
 *
 * Benutzeroberfläche zur Anzeige und zum Abspielen der 
 * aktuellen Top 10 Hits der Welt
 */
package playmyworld.gui;

import playmyworld.model.Repository;
import java.awt.Color;

import java.awt.Cursor;
import playmyworld.exceptions.NotExistingPlaylistException;
import playmyworld.model.Playlist;
import playmyworld.model.Track;
import playmyworld.exceptions.NoPlaylistSelectedException;
import playmyworld.business.Mp3Manager;
import playmyworld.business.WorldPlayer;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;
import playmyworld.model.Chartlist;

/**
 *
 * @author tinaschedlbauer
 */
public class PlayMyWorld extends javax.swing.JFrame implements Observer {

    private DefaultComboBoxModel selboxModel;
    private ResourceBundle bundle;
    private WorldPlayer player;
    private Mp3Manager manager;
    private Playlist aktList, selList, aktCharts;
    private Track aktTrack;
    private boolean playing;
    private int actTime;
    private DefaultTableModel trackListTableModel, chartTableModel;
    private ImageIcon coverImage;
    private JPopupMenu menu;
    private Timer infoViewer, trackInfoTimer;
    //mapatts
    private ButtonBuilder rep = new ButtonBuilder();
    private ArrayList<ZoomButton> euButtonList;
    private ArrayList<ZoomButton> kontinentButtonList;
    private ZoomPanel zoomPanelTr; //transparentes Panel
    private MapPanel weltPanelTr; //transparentes Panel
    private JButton backButton;
    private Thread mapThread;
    private ArrayList<ZoomButton> tempInCharts;
    private JButton zoom;
    private JButton zoomOut;
    private JButton centerButton;

    /**
     * Memberklasse zur Ermoeglichung von Drag'n'Drop.
     * Ermoeglicht das transferieren von File-Listen.
     */
    private class TransferableFileList implements Transferable {

        List<File> fileList;

        public TransferableFileList(List<File> files) {
            fileList = files;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.javaFileListFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(DataFlavor.javaFileListFlavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor.equals(DataFlavor.javaFileListFlavor)) {
                return fileList;
            }
            throw new UnsupportedFlavorException(flavor);
        }
    }

    /**
     * Memberklasse, die die Kontinentalansicht
     * nebenlaeufig  bzw. laedt
     */
    private class zoomPanelViewer implements Runnable {

        private String name;

        public zoomPanelViewer(String name) {
            this.name = name;
        }

        public zoomPanelViewer() {
        }

        @Override
        public void run() {
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            zoomPanelTr.setEnabled(false);
            zoomPanelTr.setEditable(false);
            mapPanel.removeAll();
            mapPanel.add(backButton);
            mapPanel.add(zoom);
            mapPanel.add(zoomOut);
            mapPanel.add(centerButton);

            // Transparentens Panel leeren für aktuellen Kontinent
            zoomPanelTr.removeComponents();
            mapPanel.add(zoomPanelTr);

            if (name.equals("EU")) {
                zoomPanelTr.addComponentsDelayed(50);
            } else if (name.equals("A")) {
                // AFRIKA LADEN FEHLT
            } else if (name.equals("NA")) {
                // NORDAMERIKA LADEN FEHLT
            } else if (name.equals("SA")) {
                // SUEDAMERIKA LADEN FEHLT
            } else if (name.equals("AS")) {
                // ASIEN LADEN FEHLT
            } else if (name.equals("OZ")) {
                // AUSTRALIEN LADEN FEHLT
            }
            zoomPanelTr.setEditable(true);
            zoomPanelTr.setEnabled(true);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * Initialisierung der Lankartenkomponenten
     */
    private void initMapComponents() {
        euButtonList = rep.getEuropaButtonGroup();
        kontinentButtonList = rep.getKontinentButtonGroup();
        tempInCharts = new ArrayList<ZoomButton>();

        zoomPanelTr = new ZoomPanel(euButtonList);
        for (ZoomButton actButton : zoomPanelTr.getKontinenetButtonList()) {
            actButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    ZoomButton zb = ((ZoomButton) e.getSource());

                    if (zb.isSelected()) {
                        chartLabel.setText(bundle.getString("chartlistlabel.title") + " " + zb.getButtonName());
                        for (ZoomButton b : euButtonList) {
                            if (!zb.equals(b)) {
                                b.setSelected(false);
                            }
                        }
                        aktCharts = manager.findChartsByTitle(zb.getButtonName());
                        showChartTable();
                    } else {
                        chartLabel.setText(bundle.getString("chartlistlabel.title"));
                        aktCharts = null;
                        showChartTable();
                    }


                }
            });
        }
        weltPanelTr = new MapPanel(kontinentButtonList);
        addKontinente();
        weltPanelTr.addComponents();
        initZoomButtons();
        initCenterButton();
        addKontinentListener();
        initBackButton();
    }

    /** Erstellt neuen PlayMyWorld Frame */
    public PlayMyWorld() {

        this.getContentPane().setBackground(new Color(33, 34, 37));
        bundle = ResourceBundle.getBundle("playmyworld.bundle/playerbundle");

        manager = new Mp3Manager(Repository.getInstance());
        player = new WorldPlayer(Repository.getInstance());
        playing = false;
        aktCharts = null;
        aktList = null;
        selList = null;
        aktTrack = null;

        player.addObserver(this);
        initComponents();
        initPlaylistButton();
        initMapComponents();
        setBoxModel();
    }

    /**
     * Initialisiert und fügt dem PlaylistPanel
     * ein Popupbutton hinzu, der für das Editieren der
     * Playlisten zuständig ist
     */
    private void initPlaylistButton() {
        PopupButton popup = new PopupButton(new ImageIcon("pic/playerButtons/renameBt.png"));
        popup.setBounds(270, 25, 35, 25);
        popup.setToolTipText(bundle.getString("popupbutton.tooltip"));
        menu = popup.getMenu();
        JMenuItem m1 = new JMenuItem("Umbennenen");
        JMenuItem m2 = new JMenuItem("Löschen");
        JMenuItem m3 = new JMenuItem("Neue Liste");
        m1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showRenameWindow("player.edit");
            }
        });

        m2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (playing && selList.equals(aktList)) {
                    player.stopp();
                }

                manager.deleteOwnList(selList);
                selboxModel.removeElement(selList);
                selList = (Playlist) selectListBox.getSelectedItem();

                if (selList == null || selList.size() == 0) {
                    setDefault();
                }
                showTrackListTable();
            }
        });
        m3.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selList = manager.addNewOwnList();
                selboxModel.addElement(selList);
                selectListBox.setSelectedItem(selList);
                showRenameWindow("playlist.title");
                showTrackListTable();
            }
        });

        menu.add(m1);
        menu.add(m2);
        menu.add(m3);
        playListPanel.add(popup);

    }

    /** Wird vom Konstruktor aufgerufen um die Komponenten zu initialisieren.
     * 
     * ACHTUNG: Teilweise generiert vom Netbeans - GUI-Builer.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playerPanel = new javax.swing.JPanel();
        controllerPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        coverImagePanel = new javax.swing.JLabel();
        playBt = new javax.swing.JToggleButton();
        timeLine = new javax.swing.JProgressBar();
        actTimeLabel = new javax.swing.JLabel();
        timeLabel = new javax.swing.JLabel();
        infoLabel = new javax.swing.JLabel();
        nextBt = new javax.swing.JButton();
        backBt = new javax.swing.JButton();
        playListPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        trackListTable = new javax.swing.JTable();
        selectListBox = new javax.swing.JComboBox();
        delBt = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        borderPanel = new javax.swing.JPanel();
        mapPanel = new javax.swing.JPanel();
        chartListPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        chartTable = new javax.swing.JTable();
        addChartTrackBt = new javax.swing.JButton();
        chartInfoLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        chartLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        //setAutoRequestFocus(false);
        setBackground(new java.awt.Color(33, 34, 37));
        setMinimumSize(new java.awt.Dimension(956, 604));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                componentResizedHandler(evt);
            }
        });

        playerPanel.setBackground(new java.awt.Color(33, 34, 37));

        controllerPanel.setBackground(new java.awt.Color(33, 34, 37));

        jPanel5.setBackground(new java.awt.Color(33, 34, 37));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        coverImagePanel.setBackground(new java.awt.Color(33, 34, 37));

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(coverImagePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, coverImagePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
        );

        playBt.setIcon(new ImageIcon("pic/playerButtons/playBt.png"));
        playBt.setRolloverIcon(new ImageIcon("pic/playerButtons/playBt.png"));
        playBt.setRolloverSelectedIcon(new ImageIcon("pic/playerButtons/stopBt.png"));
        playBt.setSelectedIcon(new ImageIcon("pic/playerButtons/stopBt.png"));
        playBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playBtActionPerformed(evt);
            }
        });

        timeLine.setToolTipText("");

        actTimeLabel.setFont(new java.awt.Font("Lucida Grande", 0, 8)); // NOI18N
        actTimeLabel.setForeground(new java.awt.Color(255, 255, 255));
        actTimeLabel.setText("0:00");

        timeLabel.setFont(new java.awt.Font("Lucida Grande", 0, 8)); // NOI18N
        timeLabel.setForeground(new java.awt.Color(255, 255, 255));
        timeLabel.setText("0:00");

        infoLabel.setFont(new java.awt.Font("Courier New", 0, 13)); // NOI18N
        infoLabel.setForeground(new java.awt.Color(255, 255, 255));
        infoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        infoLabel.setText(bundle.getString("table.artist") + " - " + bundle.getString("table.title"));
        infoLabel.setMaximumSize(new java.awt.Dimension(238, 15));
        infoLabel.setMinimumSize(new java.awt.Dimension(238, 15));

        nextBt.setIcon(new ImageIcon("pic/playerButtons/skipBt.png"));
        nextBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextBtActionPerformed(evt);
            }
        });

        backBt.setIcon(new ImageIcon("pic/playerButtons/prevBt.png"));
        backBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout controllerPanelLayout = new org.jdesktop.layout.GroupLayout(controllerPanel);
        controllerPanel.setLayout(controllerPanelLayout);
        controllerPanelLayout.setHorizontalGroup(
            controllerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(controllerPanelLayout.createSequentialGroup()
                .addContainerGap(37, Short.MAX_VALUE)
                .add(controllerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, controllerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(controllerPanelLayout.createSequentialGroup()
                            .add(30, 30, 30)
                            .add(infoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 206, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(timeLabel))
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, controllerPanelLayout.createSequentialGroup()
                            .add(actTimeLabel)
                            .add(18, 18, 18)
                            .add(timeLine, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 204, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(24, 24, 24))
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, controllerPanelLayout.createSequentialGroup()
                            .add(backBt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(playBt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(nextBt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(64, 64, 64)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, controllerPanelLayout.createSequentialGroup()
                        .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(56, 56, 56))))
        );
        controllerPanelLayout.setVerticalGroup(
            controllerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(controllerPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(infoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(controllerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(timeLabel)
                    .add(timeLine, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(actTimeLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(controllerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(nextBt)
                    .add(playBt)
                    .add(backBt)))
        );

        infoLabel.getAccessibleContext().setAccessibleName(bundle.getString("table.title") + " - " + bundle.getString("table.artist"));

        playListPanel.setBackground(new java.awt.Color(33, 34, 37));

        jScrollPane2.setAutoscrolls(true);

        trackListTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "", "", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        trackListTable.setFillsViewportHeight(true);
        trackListTable.setSelectionBackground(new java.awt.Color(252, 188, 63));
        trackListTable.setSelectionForeground(new java.awt.Color(0, 0, 0));
        trackListTable.setShowVerticalLines(false);
        trackListTable.getTableHeader().setReorderingAllowed(false);
        trackListTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                trackListTableMouseClicked(evt);
            }
        });
        trackListTable.setTransferHandler(new TransferHandler() {

            boolean copysupported;

            public boolean canImport(TransferSupport info) {
                if (!info.isDrop()) {
                    return false;
                }
                if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    return false;
                }

                copysupported = (COPY & info.getSourceDropActions()) == COPY;
                if (copysupported) {
                    info.setDropAction(COPY);
                    return true;
                }

                return false;
            }

            public boolean importData(TransferSupport info) {
                if (!canImport(info)) {
                    return false;
                }
                Transferable transfer = info.getTransferable();

                try {
                    List<File> list = (List<File>) transfer.getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : list) {
                        if (file.getPath().endsWith(".mp3")) {
                            /*try {
                                manager.addToOwnList(new Track(file.getPath()), selList);
                            } catch (NotExistingPlaylistException ex) {
                            } */
                            add(new Track(file.getPath()));
                        }
                    }
                    showTrackListTable();
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return true;
            }
        });
        jScrollPane2.setViewportView(trackListTable);
        trackListTable.getColumnModel().getColumn(0).setMaxWidth(40);
        trackListTable.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("table.nr"));
        trackListTable.getColumnModel().getColumn(1).setHeaderValue( bundle.getString("table.title"));
        trackListTable.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("table.artist"));
        trackListTable.getColumnModel().getColumn(3).setMaxWidth(75);
        trackListTable.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("table.length"));

        selectListBox.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        selectListBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectListBoxActionPerformed(evt);
            }
        });

        delBt.setFont(new java.awt.Font("Lucida Grande", 0, 7)); // NOI18N
        delBt.setIcon(new ImageIcon("pic/playerButtons/bin.jpg"));
        delBt.setToolTipText(bundle.getString("binbutton.tooltip"));
        delBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delBtActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText(bundle.getString("playlistlabel.title"));

        org.jdesktop.layout.GroupLayout playListPanelLayout = new org.jdesktop.layout.GroupLayout(playListPanel);
        playListPanel.setLayout(playListPanelLayout);
        playListPanelLayout.setHorizontalGroup(
            playListPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(playListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(playListPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(playListPanelLayout.createSequentialGroup()
                        .add(jLabel1)
                        .add(18, 18, 18)
                        .add(selectListBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 318, Short.MAX_VALUE)
                        .add(delBt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, playListPanelLayout.createSequentialGroup()
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)
                        .add(10, 10, 10))))
        );
        playListPanelLayout.setVerticalGroup(
            playListPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(playListPanelLayout.createSequentialGroup()
                .add(26, 26, 26)
                .add(playListPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(playListPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel1)
                        .add(selectListBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(delBt))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout playerPanelLayout = new org.jdesktop.layout.GroupLayout(playerPanel);
        playerPanel.setLayout(playerPanelLayout);
        playerPanelLayout.setHorizontalGroup(
            playerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, playerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(playListPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(controllerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        playerPanelLayout.setVerticalGroup(
            playerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(playerPanelLayout.createSequentialGroup()
                .add(playerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(playerPanelLayout.createSequentialGroup()
                        .add(11, 11, 11)
                        .add(controllerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(playListPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        borderPanel.setBackground(new java.awt.Color(222, 229, 239));

        mapPanel.setBackground(new java.awt.Color(69, 90, 107));

        org.jdesktop.layout.GroupLayout mapPanelLayout = new org.jdesktop.layout.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 587, Short.MAX_VALUE)
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 304, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout borderPanelLayout = new org.jdesktop.layout.GroupLayout(borderPanel);
        borderPanel.setLayout(borderPanelLayout);
        borderPanelLayout.setHorizontalGroup(
            borderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(borderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mapPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        borderPanelLayout.setVerticalGroup(
            borderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(borderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mapPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        chartListPanel.setBackground(new java.awt.Color(33, 34, 37));

        chartTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        chartTable.setDragEnabled(true);
        chartTable.setFillsViewportHeight(true);
        chartTable.setSelectionBackground(new java.awt.Color(252, 188, 63));
        chartTable.getTableHeader().setReorderingAllowed(false);
        chartTable.setTransferHandler(new TransferHandler() {

            public int getSourceActions(JComponent c) {
                return COPY_OR_MOVE;
            }

            public Transferable createTransferable(JComponent c) {
                List<File> fileList;
                Transferable transferObject;

                Track actTrack;
                fileList = new ArrayList();

                int[] rows = chartTable.getSelectedRows();
                for (int i = 0; i < rows.length; i++) {
                    actTrack = aktCharts.getTrack(rows[i]);
                    fileList.add(new File(actTrack.getSoundfile()));
                }

                transferObject = new TransferableFileList(fileList);
                return transferObject;
            }
        });
        chartTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chartTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(chartTable);
        chartTable.getColumnModel().getColumn(0).setMaxWidth(40);
        chartTable.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("table.nr"));
        chartTable.getColumnModel().getColumn(1).setHeaderValue( bundle.getString("table.title"));

        addChartTrackBt.setText("+");
        addChartTrackBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addChartTrackBtActionPerformed(evt);
            }
        });

        chartInfoLabel.setForeground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(33, 34, 37));

        chartLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        chartLabel.setForeground(new java.awt.Color(255, 255, 255));
        chartLabel.setText(

            bundle.getString("chartlistlabel.title") + " " );

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, chartLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, chartLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout chartListPanelLayout = new org.jdesktop.layout.GroupLayout(chartListPanel);
        chartListPanel.setLayout(chartListPanelLayout);
        chartListPanelLayout.setHorizontalGroup(
            chartListPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(chartListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(chartListPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, chartListPanelLayout.createSequentialGroup()
                        .add(chartInfoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 202, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(addChartTrackBt))
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        chartListPanelLayout.setVerticalGroup(
            chartListPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(chartListPanelLayout.createSequentialGroup()
                .add(34, 34, 34)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 195, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(chartListPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(chartInfoLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(addChartTrackBt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(playerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(borderPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(chartListPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(chartListPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, borderPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(playerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void playBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playBtActionPerformed
        if (playing) { // Verhalten eines Stop-Buttons
            try {
                player.stopp();
                if (!selList.equals(aktList)) {
                    player.selectPlaylist(selList);
                    if (selList.size() > 0) {
                        player.setTrack(trackListTable.getSelectedRow());
                    } else {
                        setDefault();
                    }
                }
            } catch (NullPointerException ex) {
                infoLabel.setText(bundle.getString("player.sellist"));
            }
        } else { // Verhalten eines Play-Buttons
            try {
                player.selectPlaylist((Playlist) selectListBox.getSelectedItem());
                player.play(trackListTable.getSelectedRow());
            } catch (NoPlaylistSelectedException ex) {
                infoLabel.setText(bundle.getString("player.sellist"));
            } catch (ArrayIndexOutOfBoundsException ex) {
                infoLabel.setText(bundle.getString("player.seltrack"));
            }
        }
    }//GEN-LAST:event_playBtActionPerformed

    /**
     * MouseListener, der Doppelklicks und Einzelklicks verarbeitet
     * Beim Doppelklick wir der ausgewälte Track abgespielt
     * Beim einfach Klick werden dessen Informationen angezeigt ohne abzuspielen
     * @param evt 
     */
    private void trackListTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_trackListTableMouseClicked
        if (evt.getClickCount() == 2) {
            aktList = (Playlist) selectListBox.getSelectedItem();
            player.selectPlaylist(aktList); // bis hier geaendert

            try {
                player.play(trackListTable.getSelectedRow());
            } catch (NoPlaylistSelectedException ex) {
                infoLabel.setText(bundle.getString("player.sellist"));
            }
        } else {
            if (!playing) {
                player.setTrack(trackListTable.getSelectedRow());
            }
        }
    }//GEN-LAST:event_trackListTableMouseClicked

    /**
     * Fuegt Tracks aus der Chartsliste der eigeben aktuellen Liste hinzu
     * @param evt 
     */
    private void addChartTrackBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addChartTrackBtActionPerformed
        Track trackToAdd;
        boolean added = false;
        int[] rows = chartTable.getSelectedRows();
        int noOfAdded = 0;
        for (int i = 0; i < rows.length; i++) {
            trackToAdd = aktCharts.getTrack(rows[i]);
            added = add(trackToAdd);
            if (added) {
                noOfAdded += 1;
            }
        }
        showInfoLabel(noOfAdded);
        showTrackListTable();
    }//GEN-LAST:event_addChartTrackBtActionPerformed

    /**
     * Löscht den aktuell ausgewählten Track in er eigenen Playliste
     * @param evt 
     */
    private void delBtActionPerformed(java.awt.event.ActionEvent evt) {
        Track trackToDelete;
        int[] rows = trackListTable.getSelectedRows();

        for (int i = 0; i < rows.length; i++) {
            trackToDelete = selList.getTrack(rows[0]); //da sich ja die internen Indizes aendern
            if (playing && aktTrack.equals(trackToDelete) && selList.equals(aktList)) {
                player.stopp();
            }
            manager.deleteTrack(selList, trackToDelete);
        }


        if (selList.equals(aktList)) {
            if (selList.size() == 0) { //kein Track mehr in der Liste
                setDefault();
            } else if (playing) {
                player.setTrack(aktList.getPosition(aktTrack)); //setze neuen aktTrack (ueber Observer)
            } else {
                player.setTrack(0);
            }
        }

        showTrackListTable();
    }

    /**
     * Aktualisiert die aktuell ausgewählte eigene Playlist
     * @param evt 
     */
    private void selectListBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectListBoxActionPerformed
        if (evt.getActionCommand().equals("comboBoxChanged")) {
            selList = (Playlist) selectListBox.getSelectedItem();
            showTrackListTable();

            if (!playing) {
                aktList = selList; //ausgewaehlte ist gleichzeitig aktuell abzuspielende Liste..
                player.selectPlaylist(aktList); // der Player muss das auch wissen!
                if (selList != null && selList.size() > 0) { // Liste ist nicht leer
                    player.setTrack(0);
                    trackListTable.setRowSelectionInterval(selList.getPosition(aktTrack), selList.getPosition(aktTrack));
                } else {
                    setDefault();
                }
            } else if (aktList.equals(selList)) {
                trackListTable.setRowSelectionInterval(aktList.getPosition(aktTrack), aktList.getPosition(aktTrack));
                trackListTable.scrollRectToVisible(new Rectangle(trackListTable.getCellRect(aktList.getPosition(aktTrack), 0, true)));
            } else if (trackListTable.getRowCount() > 0) {
                //selList != aktList -> pruefen ob leer, wenn nicht, dann:
                trackListTable.setRowSelectionInterval(0, 0);
            }  //else: leere Liste
        }
    }//GEN-LAST:event_selectListBoxActionPerformed

    /**
     * MouseListenerEvent, in welchem Doppelklicks, der Charliste verarbeitet werden
     * Ein Doppelklickt fuegt den Track der eigenen Playliste hinzu,
     * falls dieser nicht schon enthalten ist.
     * @param evt 
     */
    private void chartTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chartTableMouseClicked
        if (evt.getClickCount() == 2) {
            boolean added = false;
            Track aktChartTrack = aktCharts.getTrack(chartTable.getSelectedRow());
            try {
                //kopiere, ChartTrack in mein File-System..
                Track copied_chtr = manager.addCharttrackToMyFiles(aktChartTrack);
                //..und fuege den kopierten zu
                added = add(copied_chtr);
                player.selectPlaylist(selList); // aktlist wird ueber Observer gesetzt

                if (added) {
                    showInfoLabel(1);
                } else {
                    showInfoLabel(0);
                }

                if (playing) {
                    player.stopp();
                }

                showTrackListTable();

                try {
                    player.play(copied_chtr);
                } catch (NoPlaylistSelectedException ex) {
                    infoLabel.setText(bundle.getString("player.sellist"));
                }
            } catch (IOException ex) {
            }
        }
    }//GEN-LAST:event_chartTableMouseClicked
    /**
     * Sorgt dafür, dass sich die Landkarte immer der
     * aktuellen Fenstergroesse anpasst
     * @param evt 
     */
    private void componentResizedHandler(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_componentResizedHandler

        zoom.setLocation(mapPanel.getWidth() - zoom.getIcon().getIconWidth() - 20, 20);
        zoomOut.setLocation(mapPanel.getWidth() - zoom.getIcon().getIconWidth() - 20, zoom.getIcon().getIconHeight() + 25);
        centerButton.setLocation(mapPanel.getWidth() - zoom.getIcon().getIconWidth() - 20, 110);
        weltPanelTr.matchToParentSize();
        zoomPanelTr.matchToParentSize();

    }//GEN-LAST:event_componentResizedHandler

    /**
     * Wechselt zum nächsten Track in der eigenen Playliste
     * @param evt 
     */
    private void nextBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextBtActionPerformed
        try {
            player.skip();
        } catch (NoPlaylistSelectedException ex) {
            infoLabel.setText(bundle.getString("player.sellist"));
        }
    }//GEN-LAST:event_nextBtActionPerformed

    /**
     * Wechselt zum vorherigen Track in der eigenen Playliste
     * @param evt 
     */
    private void backBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backBtActionPerformed
        try {
            player.skipback();
        } catch (NoPlaylistSelectedException ex) {
            infoLabel.setText(bundle.getString("player.sellist"));
        }
    }//GEN-LAST:event_backBtActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PlayMyWorld.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PlayMyWorld.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PlayMyWorld.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PlayMyWorld.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new PlayMyWorld().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel actTimeLabel;
    private javax.swing.JButton addChartTrackBt;
    private javax.swing.JButton backBt;
    private javax.swing.JPanel borderPanel;
    private javax.swing.JLabel chartInfoLabel;
    private javax.swing.JLabel chartLabel;
    private javax.swing.JPanel chartListPanel;
    private javax.swing.JTable chartTable;
    private javax.swing.JPanel controllerPanel;
    private javax.swing.JLabel coverImagePanel;
    private javax.swing.JButton delBt;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel mapPanel;
    private javax.swing.JButton nextBt;
    private javax.swing.JToggleButton playBt;
    private javax.swing.JPanel playListPanel;
    private javax.swing.JPanel playerPanel;
    private javax.swing.JComboBox selectListBox;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JProgressBar timeLine;
    private javax.swing.JTable trackListTable;
    // End of variables declaration//GEN-END:variables

    /**
     * Erstellt DefaultComboBoxModel fuer die Combobox, die die 
     * vorhandenen eigenen Playlisten enthaelt und repraesentiert.
     * Wird nur bei der Initialisierung der selectListBox verwendet.
     */
    private void setBoxModel() {
        selboxModel = new DefaultComboBoxModel();
        for (Playlist act : manager.getOwnLists()) {
            selboxModel.addElement(act);
        }
        selectListBox.setModel(selboxModel);
        if (manager.getOwnLists().size() > 0) {
            selectListBox.setSelectedIndex(0);
            aktList = selList = (Playlist) selectListBox.getSelectedItem();
        }
        showTrackListTable();
    }

    /**
     * Stellt den Inhalt der aktuell Ausgewaehlten Liste (selList) dar.
     * 
     * Wird immer dann aufgerufen, wenn man eine andere Playlist auswaehlt oder
     * sich in der aktuell ausgewaehlten etwas veraendert (Track loeschen/hinzufuegen)
     */
    private void showTrackListTable() {
        trackListTableModel = (DefaultTableModel) trackListTable.getModel();

        // zuerst muss der Inhalt der Tabelle entfernt werden
        trackListTable.repaint();
        trackListTableModel.getDataVector().removeAllElements();

        if (selList != null) { // besser exception?!
            for (Track act : selList.getTracks()) {
                trackListTableModel.addRow(new String[]{Integer.valueOf(selList.getPosition(act) + 1).toString(), act.getTitle(), act.getBand(), getTimeFormatted(act.getLength())});
            }

            if (selList.size() > 0) {
                trackListTable.setRowSelectionInterval(0, 0);
            }
            if (aktList != null && aktList.equals(selList) && playing) { // Falls gerade ein Track aus selList abgespielt wird, 
                trackListTable.setRowSelectionInterval(aktList.getPosition(aktTrack), aktList.getPosition(aktTrack));
            } else if (trackListTable.getRowCount() > 0) {
                trackListTable.setRowSelectionInterval(0, 0);
            }  //else: leere Liste

            trackListTable.setModel(trackListTableModel);
        }
    }

    /**
     * SwingTimer, der beim Abspielten eines Tracks dafür sorgt,
     * dass die Anzeige, des aktuell abgespielten Tracks durchläuft
     * @param stdrd 
     */
    private void startInfoTimer(final String stdrd) {
        trackInfoTimer = new Timer(250, new ActionListener() {

            char[] c_a = stdrd.toCharArray(); //erstelle char-array

            @Override
            public void actionPerformed(ActionEvent e) {
                String neu = "";
                c_a = swap(c_a);
                for (char ch : c_a) {
                    neu += ch;
                }

                infoLabel.setText(neu);
                //wenn nicht mehr spielt, soll der Text wieder auf Standard gesetzt werden
            }
        });
        trackInfoTimer.start();
    }

    private char[] swap(char[] c_a) {
        char[] neu = new char[c_a.length];
        for (int i = 0; i + 1 < c_a.length; i++) {
            neu[i] = c_a[i + 1];
        }
        neu[c_a.length - 1] = c_a[0];
        return neu;
    }

    /**
     * Wird aufgerufen, wenn man aus der Chartliste einen Track zur eigenen Liste zufuegen moechte.
     * Entweder via addBt (Button) oder Doppelklick auf gewuenschtes Lied.
     * @param tr
     *      zuzufuegender ChartTrack
     */
    private boolean add(Track tr) {
        boolean flag = false;
        boolean added = false;
        while (!flag) {
            try {
                added = manager.addToOwnList(tr, selList);
                flag = true;
            } catch (NotExistingPlaylistException ex) {
                // wenn noch keine PL existiert, erstelle neue
                aktList = selList = manager.addNewOwnList();
                selboxModel.addElement(selList);
                selectListBox.setSelectedItem(selList);
            }
        }
        return added;

    }

    /**
     * Stellt die Charts des aktuell ausgewaehlten Landes in der ChartTable dar
     * 
     */
    private void showChartTable() {
        chartTableModel = (DefaultTableModel) chartTable.getModel();

        //vorher muss der Inhalt der Tabelle geloescht werden
        chartTableModel.getDataVector().removeAllElements();
        chartTable.repaint();

        chartTable.setModel(chartTableModel);
        if (aktCharts != null) {
            for (Track act : aktCharts.getTracks()) {
                chartTableModel.addRow(new String[]{Integer.valueOf(aktCharts.getPosition(act) + 1).toString(), act.toString()});
            }
            if (aktCharts.getTracks().size() > 0) {
                chartTable.setRowSelectionInterval(0, 0);
            }
            if (aktTrack != null && aktCharts.contains(aktTrack)) {
                chartTable.setRowSelectionInterval(aktCharts.getPosition(aktTrack), aktCharts.getPosition(aktTrack));
            }
        }
    }

    /**
     * Wandelt Sekunden in Format-String um:
     * 00:00 (min:sec)
     * 
     * @param sec
     *      Sekunden, die formatiert werden sollen
     * @return 
     *      Formatierter Time-String 
     */
    private String getTimeFormatted(int sec) {
        DecimalFormat df = new DecimalFormat("00");

        int min = sec / 60;
        int secs = sec % 60;

        return min + ":" + df.format(secs);
    }

    /**
     * Methode zum Oeffnen eines neuen Frames, der ein JTextfield beinhaltet, welches
     * via ActionListener die Option bereitstellt, die ausgewaehlte Playlist umzubennen.
     * 
     * Wird aufgerufen, wenn eine neue Playlist zugefuegt bzw. wenn der
     * "Rename"-Button betaetigt wird.
     */
    private void showRenameWindow(String bundleString) {

        try {
            String actname = selectListBox.getSelectedItem().toString();
            final JDialog changeNameFrame = new JDialog(this);
            final JTextField newname = new JTextField(actname);
            changeNameFrame.setTitle(bundle.getString(bundleString));
            changeNameFrame.setSize(220, 75);
            changeNameFrame.setLocation(this.getWidth() / 2, this.getHeight() / 2);
            changeNameFrame.add(newname);
            changeNameFrame.setVisible(true);
            PlayMyWorld.this.setEnabled(false);
            changeNameFrame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

            newname.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    String inputstr = newname.getText();
                    manager.renameList(selList, inputstr);
                    PlayMyWorld.this.setEnabled(true);
                    changeNameFrame.dispose();
                    selboxModel.setSelectedItem(selList);
                    selectListBox.repaint();
                }
            });


        } catch (NullPointerException ex) {
        } //damit keine Exception geworfen wird
    }

    /**
     * Methode setzt infoLabel und imgLabel auf den Ursprungszustand.
     * Immer dann, wenn kein Track ausgewaehlt ist.
     * (ausgewaehlte Liste leer / keine Listen mehr vorhanden)
     * 
     */
    private void setDefault() {
        infoLabel.setText(infoLabel.getAccessibleContext().getAccessibleName());
        coverImagePanel.setIcon(null);
        if (!tempInCharts.isEmpty()) { //
            for (ZoomButton b : tempInCharts) {
                b.setIcon(new ImageIcon("pic/icon/" + b.getId() + ".png"));
            }
            tempInCharts.removeAll(tempInCharts);
        }
    }

    @Override
    public void update(Observable o, Object o1) {

        //Zustand des Players aktualisieren
        if (o1 instanceof Boolean) {
            playing = (Boolean) o1;
            //Setze JToggleButton auf richtigen Status
            playBt.setSelected(playing);
            if (playing) {
                startInfoTimer(aktTrack.toString() + " * ");
            } else {
                trackInfoTimer.stop();
                infoLabel.setText(aktTrack.toString());

            }
        }

        //aktuell ausgewaehlter Track 
        if (o1 instanceof Track) {
            aktTrack = (Track) o1;

            if (aktList.equals(selList)) {
                trackListTable.setRowSelectionInterval(aktList.getPosition(aktTrack), aktList.getPosition(aktTrack));
                trackListTable.scrollRectToVisible(new Rectangle(trackListTable.getCellRect(aktList.getPosition(aktTrack), 0, true)));
            }

            if (aktCharts != null) {
                if (aktCharts.contains(aktTrack)) {
                    chartTable.setRowSelectionInterval(aktCharts.getPosition(aktTrack), aktCharts.getPosition(aktTrack));
                } else {
                    chartTable.setRowSelectionInterval(0, 0);
                }
            }

            //Anzeige aktualisieren
            infoLabel.setText(aktTrack.toString());
            timeLine.setMaximum(aktTrack.getLength());
            timeLabel.setText(getTimeFormatted(aktTrack.getLength()));

            //Cover aktualisieren und anpassen
            String imgfile = aktTrack.getImgfile();
            coverImage = new ImageIcon(imgfile);
            coverImage.setImage(coverImage.getImage().getScaledInstance(coverImagePanel.getWidth(), coverImagePanel.getHeight(), Image.SCALE_DEFAULT));
            coverImagePanel.setIcon(coverImage);
            List<Chartlist> allChartsWithTr = manager.getChartsOfCurrent(aktTrack);
            System.out.println("\tIn folgenden Ländern in den Top 10:");

            if (!tempInCharts.isEmpty()) {
                for (ZoomButton b : tempInCharts) {
                    b.setIcon(new ImageIcon("pic/icon/" + b.getId() + ".png"));
                }
                tempInCharts.removeAll(tempInCharts);
            }

            for (Chartlist akt : allChartsWithTr) {
                System.out.println("\t" + akt.getLand());
                for (ZoomButton bt : euButtonList) {
                    if (bt.getId().equals(akt.getLand())) {
                        tempInCharts.add(bt);
                    }
                }
            }

            for (ZoomButton b : tempInCharts) {
                b.setIcon(new ImageIcon("pic/bl/" + b.getId() + ".png"));
            }
        }
        if (o1 instanceof Integer) {
            actTime = (Integer) o1;
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    //Aktualisieren der Zeitanzeige
                    timeLine.setValue(actTime);
                    actTimeLabel.setText(getTimeFormatted(actTime));
                    timeLabel.setText(getTimeFormatted(aktTrack.getLength() - actTime));
                }
            });
        }

        if (o1 instanceof Playlist) {
            //Aktualisieren der aktuellen eigenen Playlist
            aktList = (Playlist) o1;
        }
    }

    /**
     * Fügt den Kontinentbuttons der Weltansicht
     * ihre Listener hinzu
     */
    private void addKontinentListener() {
        for (ZoomButton actButton : kontinentButtonList) {
            actButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent event) {
                    ((ZoomButton) event.getSource()).setIsRollOver(false);
                    mapThread = new Thread(new zoomPanelViewer(((ZoomButton) event.getSource()).getId()));
                    mapThread.start();
                }
            });
        }
    }

    /**
     * Initialisert den Zurueckbutton der von der
     * Kontinentalansicht zurueck zur Weltansicht leitet(Pfeil links oben)
     */
    private void initBackButton() {
        backButton = new JButton(new ImageIcon(
                "pic/mapButtons/returnBt.png"));
        backButton.setBounds(5, 5, 60, 45);
        backButton.setContentAreaFilled(false);
        backButton.setRolloverIcon(new ImageIcon("pic/mapButtons/returnBtRoll.png"));
        backButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                weltPanelTr.matchToParentSize();
                weltPanelTr.validate();
                weltPanelTr.repaint();
                mapPanel.removeAll();
                mapPanel.add(weltPanelTr);

                weltPanelTr.matchToParentSize();
                weltPanelTr.validate();
                weltPanelTr.repaint();
                mapPanel.validate();
                mapPanel.repaint();
            }
        });
    }

    /**
     * Initialisiert die Zoombuttons fuer die Kontinentalansicht
     */
    private void initZoomButtons() {
        zoom = new JButton(new ImageIcon("pic/mapButtons/zoom_in.png"));
        zoom.setRolloverIcon(new ImageIcon("pic/mapButtons/zoom_inRoll.png"));
        zoom.setBounds(mapPanel.getWidth() - zoom.getIcon().getIconWidth() - 20, 20, zoom.getIcon().getIconWidth(), zoom.getIcon().getIconHeight());
        zoom.setContentAreaFilled(false);
        zoom.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                zoomPanelTr.zoomIn();
                repaint();
            }
        });

        zoomOut = new JButton(new ImageIcon("pic/mapButtons/zoom_out.png"));
        zoomOut.setRolloverIcon(new ImageIcon("pic/mapButtons/zoom_outRoll.png"));
        zoomOut.setBounds(mapPanel.getWidth() - zoom.getIcon().getIconWidth() - 20, zoom.getIcon().getIconHeight() + 25, zoom.getIcon().getIconWidth(), zoom.getIcon().getIconHeight());
        zoomOut.setContentAreaFilled(false);
        zoomOut.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                zoomPanelTr.zoomOut();
                repaint();
            }
        });
    }

    /**
     * Initialisiert den Button der die Kontinentalansicht zentriert
     */
    private void initCenterButton() {
        centerButton = new JButton(new ImageIcon("pic/mapButtons/center.png"));
        centerButton.setRolloverIcon(new ImageIcon("pic/mapButtons/centerRoll.png"));
        centerButton.setBounds(mapPanel.getWidth() - zoom.getIcon().getIconWidth() - 20, 110, zoom.getIcon().getIconWidth(), zoom.getIcon().getIconHeight());
        centerButton.setContentAreaFilled(false);

        centerButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                zoomPanelTr.matchToParentSize();
            }
        });

    }

    /**
     * Zeigt dem Benutzer eine Rueckmeldung ueber die
     * aktuelle Interaktion mit der Chartliste an:
     * ***Ob Track hinzugefügt zur eigenen Liste
     * ***Oder Track bereits in eigener Liste vorhanden
     */
    private synchronized void showInfoLabel(final int noOfAdded) {
        if (infoViewer != null) {
            infoViewer.stop();
            chartInfoLabel.setText(null);
        }

        infoViewer = new Timer(1000, new ActionListener() {

            int dauer = 2; //Anzeigedauer des Infolabels

            @Override
            public void actionPerformed(ActionEvent e) {
                if (dauer >= 0) {
                    if (noOfAdded > 0) {
                        chartInfoLabel.setText(bundle.getString("infolabel.added") + noOfAdded);
                    } else {
                        chartInfoLabel.setText(bundle.getString("infolabel.alradded"));
                    }
                    dauer -= 1;
                } else {
                    chartInfoLabel.setText("");
                    infoViewer.stop();
                }
            }
        });
        infoViewer.start();
    }

    /**
     * Fuegt KontinentButtons zum entsprechenden MapPanel hinzu
     */
    private void addKontinente() {
        mapPanel.add(weltPanelTr);
    }
}
