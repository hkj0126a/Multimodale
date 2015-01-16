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
public class GestureListener implements IvyMessageListener {

    private Ivy myBus;

    public GestureListener(Ivy bus) {
        myBus = bus;
    }

    @Override
    public void receive(IvyClient ic, String[] strings) {
        try {
            System.out.println("Résultat = " + strings[1]);
            switch (strings[1]) {
                case "UpRight":
                    myBus.sendMsg("ZoomEvent acc=reims wp=WP1 role=TC direction=in");
                    break;
                case "Down":
                    myBus.sendMsg("PanEvent acc=reims wp=WP1 role=TC direction=S");
                    break;
                case "Right":
                    myBus.sendMsg("PanEvent acc=reims wp=WP1 role=TC direction=E");
                    break;
                case "Left":
                    myBus.sendMsg("PanEvent acc=reims wp=WP1 role=TC direction=W");
                    break;
                case "Up":
                    myBus.sendMsg("PanEvent acc=reims wp=WP1 role=TC direction=N");
                    break;
                case "DownLeft":
                    myBus.sendMsg("ZoomEvent acc=reims wp=WP1 role=TC direction=out");
                    break;
                case "ArrowRight":
                    myBus.sendMsg("");
                    break;
                case "ArrowLeft":
                    myBus.sendMsg("");
                    break;
                case "Square":
                    myBus.sendMsg("ClockStop");
                    break;
                case "ChevronRight":
                    myBus.sendMsg("ClockStart");
                    break;
                default:

                    break;
            }
        } catch (IvyException ex) {
            Logger.getLogger(GestureListener.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
