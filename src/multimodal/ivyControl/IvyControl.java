package multimodal.ivyControl;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import multimodal.ModalFusionListener;

public class IvyControl {

    private Ivy bus;
    private ModalFusionListener observer;

    public IvyControl(ModalFusionListener m) throws IvyException {
        bus = new Ivy("IvyControler", "IvyControler Ready", null);
        observer = m;
        sraBinder();
        icarBinder();
        paletteBinder();

        bus.start("127.255.255.255:2010");
    }

    /* ******************************************************
     ******************BINDER METHODS
     *  ******************************************************/
    private void sraBinder() throws IvyException {
        bus.bindMsg("^sra5 Parsed=(.*) Confidence=(.*) NP=(.*)$", (IvyClient client, String[] args) -> {
            if (args != null && args.length > 0) {
                observer.sraListener(client, args[1], args[0]);
            }
        });
    }

    private void icarBinder() throws IvyException {
        bus.bindMsg("^ICAR (.*)", (IvyClient client, String[] args) -> {
            if (args != null && args.length > 0) {
                observer.icarListener(client, args[0]);
            }
        });
    }

    private void paletteBinder() throws IvyException {
        bus.bindMsg("^Palette:ResultatTesterPoint x=.* y=.* nom=(.*)", (IvyClient client, String[] args) -> {
            System.out.println("NOM FORME : " + args[0]);
            send("Palette:DemanderInfo nom=" + args[0]);
        });

        bus.bindMsg("^Palette:Info nom=(.*) couleurFond=(.*) couleurContour=(.*)", (IvyClient client, String[] args) -> {
            System.out.println("Couleur FORME : " + args[1] + "   " + args[2]);
            if (args != null && args.length > 0) {
                observer.paletteFormeInformationListener(args[0], args[1], args[2]);
            }

        });

        bus.bindMsg("^Palette:MousePressed x=(.*) y=(.*)", (IvyClient client, String[] args) -> {
            System.out.println("RESULTAT CLIC : " + args[0] + "  " + args[1]);
            if (args != null && args.length > 0) {
                observer.paletteMousePressedListener(args[0], args[1]);
            }
        });
    }

    /* ******************************************************
     ******************SENDER METHODS
     *  ******************************************************/
    public void send(String msg) {
        try {
            bus.sendMsg(msg);
        } catch (IvyException ex) {
            System.out.println("can't send my message !");
        }
    }
}
