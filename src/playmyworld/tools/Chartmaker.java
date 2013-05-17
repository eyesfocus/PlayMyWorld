package playmyworld.tools;

import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import playmyworld.model.Chartlist;
import playmyworld.model.Track;

/**
 * Klasse zum individuellen Erstellen einer XML Datei mit serialisierten Chartlisten
 * und deren Tracks.
 * 
 * Voraussetzung:
 * Jede Top10 Liste besitzt eigenen Ueberordner (Landordner mit Landkennung)
 * Alle Landordner muessen im Projektordner "chartfiles" liegen.
 * 
 * @author tinaschedlbauer
 */
public class Chartmaker {

    private List<Chartlist> charts; //selbst erstellte Playlisten

    public Chartmaker() {
        init();
    }

    /**
     * Initialisiert die Liste und fuellt sie mit Daten
     */
    private void init() {
        String path = "chartfiles";
        charts = new ArrayList<Chartlist>();
        File f = new File(path);
        File[] inhalt = f.listFiles();


        for (int i = 0; i < inhalt.length; i++) {
            File akt = inhalt[i];
            if (akt.isDirectory()) {
                Chartlist aktChart = new Chartlist(akt.getName());
                File[] landordner = akt.listFiles();
                for (int j = 0; j < landordner.length; j++) {
                    File aktmp3 = landordner[j];
                    if (aktmp3.getPath().endsWith(".mp3")) {
                        aktChart.addTrack(new Track(aktmp3.getPath()));
                    }
                }
                charts.add(aktChart);
            }
        }
        save();
    }

    /**
     * Speichert die erstellte Liste
     */
    private void save() {
        File f = new File("ser");
        if (!f.exists()) {
            f.mkdir();
        }

        String ser = f.getAbsolutePath();

        System.out.println("saving...");
        try {
            XMLEncoder os = new XMLEncoder(new FileOutputStream(ser + "/ch.ser.xml"));
            os.writeObject(charts.size());
            for (Chartlist actpl : charts) {
                os.writeObject(actpl);
            }
            os.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Lesefehler: ser/ch.ser.xml");
        }
    }

    public static void main(String[] args) {
        Chartmaker c = new Chartmaker();
    }
}
