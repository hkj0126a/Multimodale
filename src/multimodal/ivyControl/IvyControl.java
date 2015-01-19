package multimodal.ivyControl;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import java.util.logging.Level;
import java.util.logging.Logger;
import multimodal.Forme;
import multimodal.FormeEnum;
import multimodal.State;

public class IvyControl {

    private Ivy bus;
    private Forme forme;
    private State state = State.NOTHING;

    public IvyControl() throws IvyException {
        bus = new Ivy("IvyControler", "IvyControler Ready", null);
        forme = new Forme();

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
            sraListener(client, args);
        });
    }

    private void icarBinder() throws IvyException {
        bus.bindMsg("^ICAR (.*)", (IvyClient client, String[] args) -> {
            icarListener(client, args);
        });
    }

    private void paletteBinder() throws IvyException {
        bus.bindMsg("^Palette:ResultatTesterPoint x=.* y=.* nom=(.*)", (IvyClient client, String[] args) -> {
            System.out.println("NOM FORME : " + args[0]);
            send("Palette:DemanderInfo nom=" + args[0]);
        });

        bus.bindMsg("^Palette:Info nom=.* couleurFond=(.*) couleurContour=(.*)", (IvyClient client, String[] args) -> {
            System.out.println("Couleur FORME : " + args[0] + "   " + args[1]);
            String cFond[] = args[0].split("=");
            String cContour[] = args[1].split("=");

            String fond = cFond[1].split(",")[0] + ":" + cFond[2].split(",")[0] + ":" + cFond[3].split("]")[0];
            String contour = cContour[1].split(",")[0] + ":" + cContour[2].split(",")[0] + ":" + cContour[3].split("]")[0];

            System.out.println("Couleur FORME PARSED: " + fond + "   " + contour);

            forme.setCouleurFond(fond);
            forme.setCouleurContour(contour);
            state = State.NOTHING;
        });

        bus.bindMsg("^Palette:MousePressed x=(.*) y=(.*)", (IvyClient client, String[] args) -> {
            System.out.println("RESULTAT CLIC : " + args[0] + "  " + args[1]);
            paletteMousePressedListener(args);
        });
    }

    /* ******************************************************
     ******************LISTENER METHODS
     *  ******************************************************/
    private void icarListener(IvyClient client, String[] args) {
        if (args != null && args.length > 0) {
            System.out.println("Création d'un " + args[0]);
            switch (args[0]) {
                case "rectangle":
                    forme.setMyForme(FormeEnum.RECTANGLE);
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

    private void sraListener(IvyClient client, String[] args) {
        if (computeVoiceConfidence(args[1])) {
            computeVoiceCommand(args[0]);
        }
    }

    private void paletteMousePressedListener(String[] args) {
        switch (state) {
            case CREER_COLOR:
                System.out.println("JE SUIS DANS DE CETTE COULEUR");
                send("Palette:TesterPoint x=" + args[0] + " y=" + args[1]);
                break;
            case CREER_POSITION:
                System.out.println("JE SUIS DANS POSITION");
                forme.setPosition(args[0], args[1]);
                state = State.NOTHING;
                break;
            case DEPLACER_CHOISIR_FORME:
                System.out.println("JE SUIS DANS POSITION");
                forme.setPosition(args[0], args[1]);
                state = State.NOTHING;
                break;
        }
    }

    /* ******************************************************
     ******************COMPUTE METHODS
     *  ******************************************************/
    private boolean computeVoiceConfidence(String confidence) {
        System.out.println("FORME : " + forme);

        String convertedConfidence = confidence.replace(",", ".");
        double confidenceRate = Double.parseDouble(convertedConfidence);
        return (confidenceRate >= 0.90) ? true : false;
    }

    private void computeVoiceCommand(String command) {
        switch (command) {
            case "validation": {
                try {
                    if (forme.toString() != null) {
                        System.out.println(forme);
                        bus.sendMsg(forme.toString());
                        forme.clearForme();
                    } else {
                        forme.clearForme();

                    }
                } catch (IvyException ex) {
                    Logger.getLogger(IvyControl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;
            case "designationcolor":
                state = State.CREER_COLOR;
                break;
            case "deplacementChoixForme":
                forme.clearForme();
                state = State.DEPLACER_CHOISIR_FORME;
                break;
            case "deplacementChoixPosition":
                forme.clearForme();
                state = State.DEPLACER_CHOISIR_POSITION;
                break;
            case "ici":
                state = State.CREER_POSITION;
                break;
            default:
                //color
                forme.setCouleurFond(command);
                break;
        }
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
