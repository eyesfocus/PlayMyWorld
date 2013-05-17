package playmyworld.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import playmyworld.model.*;
import playmyworld.model.Repository;
import playmyworld.exceptions.NotExistingPlaylistException;
import playmyworld.model.Track;

/**
 *
 * @author tinaschedlbauer
 */
public class Mp3Manager {

    private Repository rep;

    public Mp3Manager(Repository rep) {
        this.rep = rep;
    }

    /*
    
    LOESCHEN UND ZUFUEGEN
    
     */
    /**
     * Loescht einen Track aus einer Playlist
     * @param pl, tr
     *      Playlist, aus der geloescht wird, Track der geloescht werden soll
     */
    public void deleteTrack(Playlist pl, Track tr) {
        pl.getTracks().remove(tr);
        rep.saveOwnLists();
    }

    /**
     * Fuegt neue Playlist zur Playlistsammlung zu
     * @return 
     *      neue Playlist
     */
    public Playlist addNewOwnList() {
        Playlist newOwn = new Playlist();
        rep.getOwnLists().add(newOwn);
        rep.saveOwnLists();
        return newOwn;
    }

    /**
     * Fuegt einen Track zu einer Playlist hinzu
     * @param tr, pl
     *      zuzufuegender Track, Playlist zu der er zugefuegt wird
     */
    public boolean addToOwnList(Track tr, Playlist pl) throws NotExistingPlaylistException {
        boolean added;
        if (pl == null) {
            throw new NotExistingPlaylistException();
        }
        added = pl.addTrack(tr);
        rep.saveOwnLists();
        return added;
    }

    /**
     *  Benennt eine Playlist um
     * 
     * @param pl - die umzubennenende Playlist
     * @param name - die neue Playlist-Bezeichnung
     */
    public void renameList(Playlist pl, String name) {
        pl.setTitle(name);
        rep.saveOwnLists();
    }

    /**
     * Loescht Playlist aus Sammlung
     * @param pl
     *      zu loeschende Playlist
     */
    public void deleteOwnList(Playlist pl) {
        rep.getOwnLists().remove(pl);
        rep.saveOwnLists();
    }

    /*    
    FINDER UND GETTER 
     */
    public Chartlist findChartsByTitle(String name) {
        return rep.findChartsByTitle(name);
    }

    public List<Playlist> getOwnLists() {
        return rep.getOwnLists();
    }

    public List<Chartlist> getCharts() {
        return rep.getCharts();
    }

    /**
     * Durchsucht alle Playlisten nach dem uebergebenen Track
     * 
     * @param tr
     *      Track der gesucht wird
     * @return 
     *      Liste mit allen Chartlisten die tr enthalten
     */
    public List<Chartlist> getChartsOfCurrent(Track tr) {
        List<Chartlist> all = rep.getCharts();
        List<Chartlist> ret = new ArrayList<Chartlist>();


        for (Chartlist aktCh : all) {
            for (Track akttr : aktCh.getTracks()) {
                if (akttr.equals(tr)) {
                    ret.add(aktCh);
                }
            }
        }
        return ret;
    }

    /**
     * Methode zum Kopieren von Mp3 Dateien aus Charttracks in das 
     * eigene File-System
     * 
     * @param chtr
     *      zu kopierender Charttrack
     * @returns
     *      Track mit Referenz auf kopierte Mp3
     */
    public Track addCharttrackToMyFiles(Track chtr) throws IOException {
        String directory = "./copiedFromCharts";
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdir(); //..dann erstelle Ordner
        }
        //Datei, auf der der Charttrack basiert
        File mp3 = new File(chtr.getSoundfile());

        //Datei auf der der Track in der OwnList basieren soll
        File copied_mp3 = new File(dir, mp3.getName());

        if (!copied_mp3.exists()) {
            copyMp3(mp3, copied_mp3); //.. dann kopiere
        }

        //neues Track-Objekt, das auf kopierter Mp3 basiert
        Track copied_track = new Track(copied_mp3.getPath());
        return copied_track;
    }

    /**
     * Kopiert eine bereits bestehende Datei in eine neue
     * @param in, out
     *      in: zu kopierende Datei
     *      out: kopierte Datei
     * 
     */
    private void copyMp3(File in, File out) throws IOException {
        FileChannel inChannel = new FileInputStream(in).getChannel();
        FileChannel outChannel = new FileOutputStream(out).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            throw e;
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }
}
