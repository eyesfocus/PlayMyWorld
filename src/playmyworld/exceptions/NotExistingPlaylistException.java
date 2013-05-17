/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package playmyworld.exceptions;

/**
 *
 * @author tinaschedlbauer
 */
public class NotExistingPlaylistException extends Exception{
    
    public NotExistingPlaylistException(){
        super();
    }
    
    public NotExistingPlaylistException(String msg){
        super(msg);
    }
    
}
