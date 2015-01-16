package multimodal.ivyControl;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;


public class IvyControl implements IvyMessageListener {

    private Ivy bus;
    public IvyControl() throws IvyException {
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

}
