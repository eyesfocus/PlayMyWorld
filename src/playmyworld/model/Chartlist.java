/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package playmyworld.model;

import java.io.Serializable;
import java.util.ResourceBundle;

/**
 *
 * @author tinaschedlbauer
 */
public class Chartlist extends Playlist implements Serializable{

    private String land;

    public Chartlist() {
    }

    public Chartlist(String land) {
        super(ResourceBundle.getBundle("playmyworld.bundle/charts").getString(land));
        this.land = land;
    }
    
    public void setLand(String land){
        this.land = land;
    }
    
    public String getLand(){
        return land;
    }
}
