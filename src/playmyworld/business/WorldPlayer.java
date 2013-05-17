package playmyworld.business;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.*;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import playmyworld.exceptions.NoPlaylistSelectedException;
import playmyworld.model.Playlist;
import playmyworld.model.Repository;
import playmyworld.model.Track;

/**
 *
 * @author tinaschedlbauer
 */
public class WorldPlayer extends Observable {

    private Repository rep;
    private Playlist aktList;
    private Player myPlayer;
    private PlayerThread pth;
    private Timer myTimer;
    private int trackNo;
    private boolean playing;
    private boolean skippressed;

    private class PlayerThread extends Thread {

        @Override
        public void run() {
            try {
                myPlayer = new Player(new FileInputStream(selectTrack(trackNo).getSoundfile()));
                setTimer();

                playing = true;
                setChanged();
                notifyObservers(playing);

                myPlayer.play();

                if (!skippressed && playing) { //damit nach Stop gedrueckt wieder gleiches Lied
                    if (aktList.getTracks().size() == (trackNo + 1)) {
                        stopp();
                    } else {
                        try {
                            skip();
                        } catch (NoPlaylistSelectedException ex) {
                            Logger.getLogger(WorldPlayer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } catch (JavaLayerException ex) {
                Logger.getLogger(WorldPlayer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(WorldPlayer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException ex){
                
            }
        }
    }

    public WorldPlayer() {
        rep = Repository.getInstance();
        trackNo = 0;
        playing = false;
        skippressed = false;
    }

    public WorldPlayer(Repository rep) {
        this.rep = rep;
        trackNo = 0;
        playing = false;
        skippressed = false;
    }

    public void play() throws NoPlaylistSelectedException {
        if (aktList != null) {

            //wenn gerade ein lied spielt, soll trotzdem ein anderes ausgewaehlt werden koennen
            if (playing) {
                stopp();
                skippressed = true;
            }
            play(trackNo);
        }
    }

    public void play(Track tr) throws NoPlaylistSelectedException {
        if (aktList != null) {

            //wenn gerade ein lied spielt, soll trotzdem ein anderes ausgewaehlt werden koennen
            if (playing) {
                stopp();
                skippressed = true;
            }
            play(aktList.getPosition(tr));
        }
    }

    public void play(int nummer) throws NoPlaylistSelectedException {
        if (aktList != null) {

            //wenn gerade ein Lied spielt, soll trotzdem ein anderes ausgewaehlt werden koennen
            if (playing) {
                stopp();
                skippressed = true;
            }

            trackNo = nummer;

            setChanged();
            notifyObservers(selectTrack(trackNo));

            pth = new PlayerThread();
            pth.start();

            skippressed = false;
        } else {
            throw new NoPlaylistSelectedException();
        }
    }

    public synchronized void skip() throws NoPlaylistSelectedException {
        if (aktList != null) {
            //schliesst javaplayer 
            if (playing) {
                myPlayer.close();
            }
            //zeigt dem Thread an, dass skip gedrueckt wurde
            skippressed = true;


            if (trackNo < aktList.getTracks().size() - 1) {
                trackNo++;
            } else {
                trackNo = 0;
            }

            if (playing) { //nur wenn der Player spielt soll der neue Track abgespielt werden
                play(trackNo);
            } else { //ansonsten nur der GUI melden, dass ein neuer Track aktuell ist
                setChanged();
                notifyObservers(selectTrack(trackNo));
            }
        }
    }

    public synchronized void skipback() throws NoPlaylistSelectedException {
        if (aktList != null) {

            if (playing) {
                myPlayer.close();
            }
            skippressed = true;

            if (trackNo > 0) {
                trackNo--;
            } else {
                trackNo = aktList.getTracks().size() - 1;
            }

            if (playing) {
                play(trackNo);
            } else {
                setChanged();
                notifyObservers(selectTrack(trackNo));
            }
        }
    }

    public void stopp() {
        playing = false;
        myPlayer.close();
        myTimer.cancel();
        setChanged();
        notifyObservers(playing);
        setChanged();
        notifyObservers(0);
    }

    private void setTimer() {
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                setChanged();
                notifyObservers(myPlayer.getPosition() / 1000);
            }
        }, 0, 250);
    }

    public void setTrack(int no){
        setChanged();
        notifyObservers(selectTrack(no));
    }

    private Track selectTrack(int no) {
        trackNo = no;
        return aktList.getTrack(no);
    }

    
    public void selectPlaylist(Playlist pl) {
        aktList = pl;

        setChanged();
        notifyObservers(aktList);

        trackNo = 0;
    } 

    public Repository getRep() {
        return rep;
    }
}
