package multimodal.ivyControl;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import multimodal.Forme;
import multimodal.FormeEnum;

public class IvyControl {

    private Ivy bus;
    private Forme forme;

    public IvyControl() throws IvyException {
        // initialization, name and ready message
        bus = new Ivy("IvyControler", "IvyControler Ready", null);

        bus.bindMsg("^ICAR (.*)", (IvyClient client, String[] args) -> {
            gestureListener(client, args);
        });

        bus.bindMsg("^(.*)", (IvyClient client, String[] args) -> {
            voiceListener(client, args);
        });

        bus.bindMsg("^Palette (.*)", (IvyClient client, String[] args) -> {
            paletteListener(client, args);
        });

        bus.bindMsg("^Multimodal cam (.*)", (IvyClient client, String[] args) -> {
            camListener(client, args);
        });

        bus.bindMsg("^Multimodal control (.*)", (IvyClient client, String[] args) -> {
            controlListener(client, args);
        });

        // starts the bus on the default domain
        bus.start("127.255.255.255:2010");

        forme = new Forme();
    }

    public void send(String msg) {
        try {
            bus.sendMsg(msg);
        } catch (IvyException ex) {
            System.out.println("can't send my message !");
        }
    }

    private void gestureListener(IvyClient client, String[] args) {
        if (args != null && args.length > 0) {
            System.out.println("MESSAGE GESTURE : " + args[0]);
            switch (args[0]) {
                case "rectangle":
                    forme.setMyForme(FormeEnum.RECTANGLE);
                    send("Palette:CreerRectangle");
                    break;
                case "ellipse":
                    forme.setMyForme(FormeEnum.ELLIPSE);
                    break;
                default:
                    //dire qu'il y a un pb
                    break;
            }

        }

    }

    private void voiceListener(IvyClient client, String[] args) {
        System.out.println("TEST : "+ args[0]);
    }

    private void camListener(IvyClient client, String[] args) {

    }

    private void paletteListener(IvyClient client, String[] args) {
        //System.out.println("PALETTE");
    }

    private void controlListener(IvyClient client, String[] args) {

    }

}
