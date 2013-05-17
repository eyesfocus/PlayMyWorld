package playmyworld.model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.*;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.*;

public class Track implements Serializable {

    private long id;
    private int length;
    private String title;
    private String albumTitle;
    private String band;
    private String soundfile;
    private String imgfile;

    public Track() {
    }

    public Track(String soundfile) {
        this.soundfile = soundfile;
        init();
    }

    /**
     * Fuer "festes Reinkodieren" von Trackdaten
     */
    public Track(long id, String title, int length, String albumTitle, String band, String soundfile) {
        this.id = id;
        this.title = title;
        this.length = length;
        this.albumTitle = albumTitle;
        this.band = band;
        this.soundfile = soundfile;
    }

    /*Getter*/
    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

    public int getLength() {
        return length;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public String getBand() {
        return band;
    }

    public String getSoundfile() {
        return soundfile;
    }

    public String getImgfile() {
        return imgfile;
    }


    /*Setter*/
    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public void setSoundfile(String soundfile) {
        this.soundfile = soundfile;
    }

    public void setImgfile(String imgfile) {
        this.imgfile = imgfile;
    }

    @Override
    public String toString() {
        return band + " - " + title;
    }

    public boolean equals(Track tr) {
        if (band.equals(tr.getBand()) && title.equals(tr.getTitle()) && albumTitle.equals(tr.getAlbumTitle()) && length == tr.getLength()) {
            return true;
        } else {
            return false;
        }
    }

    
    /**
     * Liest die ID-Tags aus der Mp3-Datei und speichert sie in den Attributen.
     */
    private void init() {
        try {
            MP3File f = (MP3File) AudioFileIO.read(new File(soundfile));
            MP3AudioHeader ah = (MP3AudioHeader) f.getAudioHeader();
            ID3v1Tag idv = f.getID3v1Tag();
            ID3v24Tag idv2 = (ID3v24Tag) f.getID3v2TagAsv24();
            ImageIcon icon;


            if (f.hasID3v2Tag()) {
                title = idv2.getFirst(ID3v24Frames.FRAME_ID_TITLE);
                band = idv2.getFirst(ID3v24Frames.FRAME_ID_ARTIST);
                albumTitle = idv2.getFirst(ID3v24Frames.FRAME_ID_ALBUM);
                try {
                    icon = new ImageIcon((Image) idv2.getFirstArtwork().getImage());
                    Image img = icon.getImage();
                    BufferedImage img2 = (BufferedImage) img;
                    imgfile = "coverimgs/" + band.trim() + albumTitle.trim() + "Cov.jpg";
                    if (!new File(imgfile).exists()) {
                        ImageIO.write(img2, "jpg", new File(imgfile));
                    }
                } catch (NullPointerException ex) {
                    imgfile = "coverimgs/defaultimg.jpg";
                }

            } else if (f.hasID3v1Tag()) {
                title = idv.getFirstTitle();
                band = idv.getFirstArtist();
                albumTitle = idv.getFirstAlbum();
                imgfile = "coverimgs/defaultimg.jpg";

            } else {
                ResourceBundle rb = ResourceBundle.getBundle("playmyworld.bundle/playerbundle");
                title = rb.getString("song.title");
                band = rb.getString("song.artist");
                albumTitle = rb.getString("song.album");
                imgfile = "coverimgs/defaultimg.jpg";
            }

            length = ah.getTrackLength();


        } catch (CannotReadException ex) {
            Logger.getLogger(Track.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Track.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TagException ex) {
            Logger.getLogger(Track.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ReadOnlyFileException ex) {
            Logger.getLogger(Track.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAudioFrameException ex) {
            Logger.getLogger(Track.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
