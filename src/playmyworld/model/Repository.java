package playmyworld.model;

import playmyworld.model.Playlist;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import playmyworld.model.Chartlist;

/**
 * Playlist-Verwaltung als Singleton-Klasse
 * 
 * @author tinaschedlbauer
 */
public class Repository {

    private static Repository instance = null;
    private List<Chartlist> charts; //alle Laendercharts
    private List<Playlist> ownLists; //selbst erstellte Playlisten
    String ser;

    private Repository() {
        System.out.println("load Repository...");
        charts = new ArrayList<Chartlist>();
        ownLists = new ArrayList<Playlist>();
        init();
    }

    private void init() {
        File f = new File("ser");
        if (!f.exists()) {
            f.mkdir();
        }
        ser = f.getAbsolutePath();

        try {
            FileInputStream ips = new FileInputStream("ser/ch.ser.xml");
            XMLDecoder is = new XMLDecoder(ips);
            int size = (Integer) is.readObject();
            System.out.println("load chartlists...");

            System.out.println(size);
            for (int i = 0; i < size-1; i++) {
                charts.add((Chartlist) is.readObject());
            }

        } catch (FileNotFoundException ex) {
            System.out.println("Lesefehler: ser/ch.ser.xml");
        }

        //init OwnLists
        try {
            FileInputStream ips = new FileInputStream("ser/ownlists.ser.xml");
            XMLDecoder is = new XMLDecoder(ips);
            int size = (Integer) is.readObject();
            System.out.println("load ownlists...");

            for (int i = 0; i < size; i++) {
                ownLists.add((Playlist) is.readObject());
                ownLists.get(i).setId(i + 1000);
            }
        } catch (FileNotFoundException ex) {
            ownLists.add(new Playlist());
        }
    }

    /**
     * Sucht Chartliste mit bestimmtem Titel
     * (nur weil man weiss, dass es keine Chartlisten gibt, die denselben Titel haben)
     * 
     * @param title
     *      gesuchter Titel
     * @return
     *      Chartliste mit diesem Titel
     */
    public Chartlist findChartsByTitle(String title) {
        for (Chartlist actList : charts) {
            if (actList.getTitle().equals(title)) {
                return actList;
            }
        }
        return null;
    }

    public List<Chartlist> getCharts() {
        return charts;
    }

    public List<Playlist> getOwnLists() {
        return ownLists;
    }

    
    /**
     * Serialisiert die Liste von eigenen Playlists in eine XML Datei
     */
    public void saveOwnLists() {
        try {
            XMLEncoder os = new XMLEncoder(new FileOutputStream(ser + "/ownLists.ser.xml"));
            os.writeObject(ownLists.size());
            for (Playlist actpl : ownLists) {
                os.writeObject(actpl);
            }
            os.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Lesefehler: ser/ownLists.ser.xml");
        }
    }

    /**
     * Erstellt, wenn noch nicht vorhanden, eine Instanz von Repository
     * 
     * @return 
     *      Repository-Instanz  
     */
    public static synchronized Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }
}
