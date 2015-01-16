package ivytp.translater;

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

public class ivyTranslater implements IvyMessageListener {

    private Ivy bus;
    //private JFrame myFrame;
    public ivyTranslater(/*JFrame frame*/) throws IvyException {
        // initialization, name and ready message
        bus = new Ivy("IvyTranslater", "IvyTranslater Ready", null);
        bus.bindMsg("^Hello(.*)", this);
        bus.bindMsg("^(.*)AFR", new IvyMessageListener() {
            // callback for "Bye" message
            public void receive(IvyClient client, String[] args) {
                //incrémenter jlabel
                //myFrame.incrementNbAFR();
            }
        }
        );
        bus.bindMsg("^Bye$", new IvyMessageListener() {
      // callback for "Bye" message

            public void receive(IvyClient client, String[] args) {
                bus.stop();
            }
        }
        );
        // starts the bus on the default domain
        bus.start("10.3.8.255:1234");
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
        new ivyTranslater();
    }
}
