package playmyworld.model;

/**
 *
 * @author tinaschedlbauer
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class Playlist implements Serializable {

    private List<Track> tracks;
    private long id;
    private String title;
    private Date creationDate;

    public Playlist() {
        tracks = new ArrayList<Track>();
        id = 0;
        title = ResourceBundle.getBundle("playmyworld.bundle/playerbundle").getString("playlist.title"); //DefaultTitel
        creationDate = new Date();
    }

    public Playlist(String title) {
        tracks = new ArrayList<Track>();
        id = 0;
        creationDate = new Date();
        this.title = title;
    }

    /**
     * Gibt den Track einer bestimmten Nummer zurueck
     * @param no 
     *      gesuchte TrackNummer
     * @return 
     *      Track mit dieser Nummer
     */
    public Track getTrack(int no) {
        if (size() <= no) {
            return null;
        } else {        
            return tracks.get(no);
        }
    }

    /**
     * Fuegt Track zur Liste hinzu, wenn nicht schon vorhanden
     * @param tr
     *      zuzufuegender Track
     */
    public boolean addTrack(Track tr) {
        if (!contains(tr)) {
            return tracks.add(tr);
        }
        return false;
    }
    
    /**
     * Ermittelt den Index eines Tracks in der Liste
     * 
     * @param tr
     *      gesuchter Track
     *      
     */
    public int getPosition(Track tr) {
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).equals(tr)) {
                return i;
            }
        }
        return -1;
    }

    /**
     *  @return 
     *      Anzahl der Tracks in der Liste
     */
    public int size() {
        return tracks.size();
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    public boolean contains(Track tr) {
        for (Track akt : tracks) {
            if (akt.equals(tr)) {
                return true;
            }
        }
        return false;
    }
}
