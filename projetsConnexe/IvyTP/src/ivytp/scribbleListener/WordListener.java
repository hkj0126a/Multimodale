/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivytp.scribbleListener;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hakje
 */
public class WordListener implements IvyMessageListener {

    private Ivy myBus;

    public WordListener(Ivy bus) {
        myBus = bus;
    }

    @Override
    public void receive(IvyClient ic, String[] strings) {
        try {
            System.out.println("Résultat = " + strings[1]);
            switch (strings[1]) {
                case "+":
                    myBus.sendMsg("ZoomEvent acc=reims wp=WP1 role=TC direction=in");
                    break;
                case "_":
                case "-":
                    myBus.sendMsg("ZoomEvent acc=reims wp=WP1 role=TC direction=out");
                    break;
                default:

                    break;
            }
        } catch (IvyException ex) {
            Logger.getLogger(WordListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
