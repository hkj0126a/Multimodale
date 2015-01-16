package tpvocal;

/**
 * Yet another Ivy java program example
 *
 * This is the example from the documentation
 *
 * @author Yannick Jestin <jestin@cena.fr>
 *
 * (c) CENA
 *
 * This program is distributed as is
 *
 */
import fr.dgac.ivy.*;
import java.awt.Dimension;
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class IvyDeplaceur implements IvyMessageListener {

    private Ivy bus;
    private MyPanel p;
    //private JFrame myFrame;

    public IvyDeplaceur(/*JFrame frame*/) throws IvyException {
        // initialization, name and ready message
        bus = new Ivy("IvyDeplaceur", "IvyTranslater Ready", null);
        bus.bindMsg("^sra5 Parsed=Action:(.*) Position:(.*) Confidence=(.*) NP=(.*)$", new IvyMessageListener() {
            // callback for "Bye" message
            public void receive(IvyClient client, String[] args) {
                System.out.println("IvyDeplaceur: action = " + args[0] + "vers = " + args[1]);
                compute(args[0], args[1], args[2]);

            }
        }
        );
        bus.bindMsg("^(.*)Action:centrer Confidence=(.*) NP=(.*)$", new IvyMessageListener() {
            // callback for "Bye" message
            public void receive(IvyClient client, String[] args) {
                System.out.println("IvyDeplaceur: Length = " + args.length);
                System.out.println("Confidence centrer = " + args[1]);
                computeCentrer(args[1]);
            }
        }
        );
        // starts the bus on the default domain
        bus.start("127.255.255.255:2010");

        p = new MyPanel();
        JFrame f = new JFrame();
        f.setSize(800, 800);
        f.add(p);
        f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void computeCentrer(String confidence) {
        String convertedConfidence = confidence.replace(",", ".");
        double confidenceRate = Double.parseDouble(convertedConfidence);
        if (confidenceRate >= 0.60) {
            Dimension d = p.getParent().getSize();
            p.getCarre().setLocation((d.width / 2) - 15, (d.height / 2) - 15);
            try {
                bus.sendMsg("Anna Say=It went to the center");
            } catch (IvyException ex) {
                Logger.getLogger(IvyDeplaceur.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void compute(String action, String direction, String confidence) {
        if (action.equals("deplacement")) {
            String convertedConfidence = confidence.replace(",", ".");
            double confidenceRate = Double.parseDouble(convertedConfidence);
            if (confidenceRate >= 0.60) {
                Point point = p.getCarre().getLocation();
                int xOffset = 0;
                int yOffset = 0;
                String directionEnglish = "";
                switch (direction) {
                    case "en haut":
                        yOffset -= 20;
                        directionEnglish = "up";
                        break;
                    case "en bas":
                        yOffset += 20;
                        directionEnglish = "down";
                        break;
                    case "a gauche":
                        xOffset -= 20;
                        directionEnglish = "to the left";
                        break;
                    case "a droite":
                        xOffset += 20;
                        directionEnglish = "to the right";
                        break;
                    case "en diagonale":
                        yOffset += 20;
                        xOffset += 20;
                        directionEnglish = "diagonal";
                        break;
                }
                p.getCarre().setLocation(point.x + xOffset, point.y + yOffset);
                try {
                    bus.sendMsg("Anna Say=It went " + directionEnglish);
                } catch (IvyException ex) {
                    Logger.getLogger(IvyDeplaceur.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    // callback associated to the "Hello" messages"
    public void receive(IvyClient client, String[] args) {
        try {
            bus.sendMsg("Bonjour" + ((args.length > 0) ? args[0] : ""));
        } catch (IvyException ie) {
            System.out.println("can't send my message !");
        }
    }

    public void send(String msg) {
        try {
            bus.sendMsg(msg);
        } catch (IvyException ex) {
            System.out.println("can't send my message !");
        }
    }

    public static void main(String args[]) throws IvyException {
        new IvyDeplaceur();
    }

}
