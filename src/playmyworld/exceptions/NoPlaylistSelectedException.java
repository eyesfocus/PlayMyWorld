/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package playmyworld.exceptions;

/**
 *
 * @author tinaschedlbauer
 */
public class NoPlaylistSelectedException extends Exception{
    
    public NoPlaylistSelectedException(){
        super();
    }
    
    public NoPlaylistSelectedException(String msg){
        super(msg);
    }
    
}
