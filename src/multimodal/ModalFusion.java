/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multimodal;

import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.Timer;
import multimodal.ivyControl.IvyControl;
import multimodal.camera.MultipleJava3D;

/**
 *
 * @author nathan
 */
public class ModalFusion extends javax.swing.JFrame implements ModalFusionListener {

    private IvyControl ivyControl;
    private State state = State.NOTHING;
    private Forme forme;
    private Action lastActionMade;
    private Timer timerCommandeTotale;
    private MultipleJava3D cameraHandler;

    public ModalFusion() throws IvyException {
        initComponents();
        forme = new Forme();
        ivyControl = new IvyControl(this);
        lastActionMade = new Action();
        initCameraHandler();
        initTimer();
        updatePanel();
    }

    /* ******************************************************
     ****************** INIT METHODS
     *  ******************************************************/
    private void initTimer() {
        ActionListener timerOutListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

//                System.out.println("Fin timer, forme complete ? " + forme.isComplete());
//                System.out.println("Forme = " + forme.getMyForme() + " Pos = " + forme.getX() + " " + forme.getY());
                forme.updateIsComplete();
                if (forme.isComplete()) {
                    switch (state) {
                        case NOTHING:
                            break;
                        case CREER:
                            state = State.NOTHING;
                            System.out.println(forme.commandToCreateFormePatern());
                            ivyControl.send(forme.commandToCreateFormePatern());
                            break;
                        case DEPLACER:
                            state = State.NOTHING;
                            System.out.println(forme.commandToMoveFormePatern());
                            if (lastActionMade.isDeplacementValide()) {
                                ivyControl.send(forme.commandToMoveFormePatern());
                            }
                            break;
                        default:
                            throw new AssertionError(state.name());

                    }
                } else {
//                    forme.clearForme();
                }
                forme.clearForme();
                timerCommandeTotale.stop();
                lastActionMade.init();
                updatePanel();
            }
        };
        timerCommandeTotale = new Timer(2000, timerOutListener);
    }
    /* ******************************************************
     ******************OVERRIDE LISTENER METHODS
     *  ******************************************************/

    @Override
    public void icarListener(IvyClient client, String formeName) {
        switch (state) {
            case NOTHING:
                state = State.CREER;
                forme.setTypeCommande(Commande.CREATION);
                computeGestureCommand(formeName);
                timerCommandeTotale.restart();
                break;
            case CREER:
                state = State.CREER;
                computeGestureCommand(formeName);
                timerCommandeTotale.restart();
                updateState();
                break;
            case DEPLACER:
                break;
            default:
                throw new AssertionError(state.name());
        }
        updatePanel();
    }

    @Override
    public void sraListener(IvyClient client, String confidence, String command) {
        if (computeVoiceConfidence(confidence)) {
            computeVoiceCommand(command);
        }
        updatePanel();
    }

    @Override
    public void paletteMousePressedListener(String x, String y) {
        String[] param;
        Point pInit;
        Point pFinal;
        switch (state) {
            case NOTHING:
                break;
            case CREER:
                state = State.CREER;
                timerCommandeTotale.restart();
                switch (lastActionMade.getActionEnCours()) {
                    case NULL:
                        lastActionMade.setActionEnCours(ActionEnum.CLIC);
                        param = new String[2];
                        param[0] = x;
                        param[1] = y;
                        lastActionMade.setParameters(param);
                        break;
                    case GESTE:
                        break;
                    case CAMMOVE:
                        //Récupérer la forme cliquée
                        //forme.deplacer(lastAction.param[0]);
                        break;
                    case CAMCOLOR:
                        //Récupérer la forme cliquée
                        //forme.setColor(lastAction.param[0])
                        break;
                    case CLIC:
                        break;
                    case VOIX_ICI:
//                        timerCommandeComplementaire.stop();
//                        System.out.println("CREER FORME POSITION");
                        forme.setPosition(x, y);
                        lastActionMade.init();
                        break;
                    case VOIX_UNECOULEUR:
                        break;
                    case VOIX_CETTECOULEUR:
                        System.out.println("CREER FORME CETTE COULEUR");
                        ivyControl.send("Palette:TesterPoint x=" + x + " y=" + y);
                        lastActionMade.init();
                        break;
                    case TIMER:
                        break;
                    case VOIX_DEPLACER:
                        break;
                    case COMMECELA:
                        break;
                    default:
                        throw new AssertionError(lastActionMade.getActionEnCours().name());

                }
                break;
            case DEPLACER:
                state = State.DEPLACER;
                timerCommandeTotale.restart();
                switch (lastActionMade.getActionEnCours()) {
                    case NULL:
                        lastActionMade.setActionEnCours(ActionEnum.CLIC);
                        param = new String[2];
                        param[0] = x;
                        param[1] = y;
                        lastActionMade.setParameters(param);
                        ivyControl.send("Palette:TesterPoint x=" + x + " y=" + y);
                        break;
                    case CLIC:
                        lastActionMade.setActionEnCours(ActionEnum.CLIC);
                        pFinal = new Point(Integer.parseInt(x), Integer.parseInt(y));
                        lastActionMade.setDestination(pFinal);
                        break;
                    case CHOIX_FORME:
                        lastActionMade.setActionEnCours(ActionEnum.CLIC);
                        pFinal = new Point(Integer.parseInt(x), Integer.parseInt(y));
                        System.out.println("stock dest");
                        lastActionMade.setReference(pFinal);
                        if (forme.getName() == "") {
                            ivyControl.send("Palette:TesterPoint x=" + pFinal.x + " y=" + pFinal.y);
                        }
                        break;
                    case VOIX_ICI:
                        lastActionMade.setActionEnCours(ActionEnum.CLIC);
//                            System.out.println("CHOIX DEPLACEMENT POSITION");
                        pFinal = new Point(Integer.parseInt(x), Integer.parseInt(y));
                        System.out.println("stock dest");
                        lastActionMade.setDestination(pFinal);
                        forme.setPosition(lastActionMade.getDistanceX() + "", lastActionMade.getDistanceY() + "");
                        if (forme.getName() == "") {
                            ivyControl.send("Palette:TesterPoint x=" + pFinal.x + " y=" + pFinal.y);
                        }
                        break;
                    case VOIX_DEPLACER:
                        lastActionMade.setActionEnCours(ActionEnum.CLIC);
                        pInit = new Point(Integer.parseInt(x), Integer.parseInt(y));
                        lastActionMade.setReference(pInit);
                        ivyControl.send("Palette:TesterPoint x=" + x + " y=" + y);
                        break;
                    case FORME_SELECTIONNEE:
//                            System.out.println("CHOIX DEPLACEMENT POSITION");
                        lastActionMade.setActionEnCours(ActionEnum.CLIC);
                        pFinal = new Point(Integer.parseInt(x), Integer.parseInt(y));
                        lastActionMade.setDestination(pFinal);
                        break;
                    case VOIX_CETTECOULEUR:
//                            System.out.println("CHOIX DEPLACEMENT FORME");
                        ivyControl.send("Palette:TesterPoint x=" + x + " y=" + y);
                        break;
                    default:
                        throw new AssertionError(lastActionMade.getActionEnCours().name());
                }
                break;
            default:
                throw new AssertionError(state.name());
        }
        updatePanel();
    }

    @Override
    public void paletteFormeInformationListener(String name, String backgroundColor, String strokeColor) {
//        System.out.println("Etat = " + state + " Dernier action = " + lastActionMade.getActionEnCours());
//        System.out.println("Name = " + name);
        switch (state) {
            case NOTHING:
                break;
            case CREER:
                state = State.CREER;
                timerCommandeTotale.restart();
                forme.setBackgroundColor(backgroundColor);
                forme.setStrokeColor(strokeColor);
                break;
            case DEPLACER:
                state = State.DEPLACER;
                timerCommandeTotale.restart();
                switch (lastActionMade.getActionEnCours()) {
                    case CLIC:
//                        System.out.println("Nom clic " + name);
                        forme.setName(name);
                        lastActionMade.setActionEnCours(ActionEnum.FORME_SELECTIONNEE);
                        updateState();
                        break;
                    case VOIX_DEPLACER:
                        forme.setName(name);
                        lastActionMade.setActionEnCours(ActionEnum.FORME_SELECTIONNEE);
                        updateState();
                        break;
                    case CHOIX_FORME:
//                        System.out.println("nom de la forme " + name + " char at 0 " + name.charAt(0));
//                        System.out.println("Déplacement de la forme " + forme.getMyForme());
                        forme.setName(name);
                        lastActionMade.setActionEnCours(ActionEnum.FORME_SELECTIONNEE);
                        updatePanel();
                        break;
                    case FORME_SELECTIONNEE:
                        break;
                    default:
                        throw new AssertionError(lastActionMade.getActionEnCours().name());
                }

                //verifier qu'il y'a pas 2 rectangles au même endroit
                break;
            default:
                break;
        }
        updatePanel();
    }

    @Override
    public void cameraListener(String x, String y, String couleur) {
//        System.out.println("réception caméra X= " + x + " Y= " + y + " couleur = "+ couleur);
        switch (state) {
            case NOTHING:
                break;
            case CREER:
                forme.setBackgroundColor(couleur);
                break;
            case DEPLACER:
                switch (lastActionMade.getActionEnCours()) {
                    case NULL:
                        break;
                    case GESTE:
                        break;
                    case CAMMOVE:
                        timerCommandeTotale.restart();
                        ivyControl.send("Palette:DeplacerObjet nom=" + forme.getName() + " x=" + x + " y=" + y);
                        break;
                    case CAMCOLOR:
                        break;
                    case CLIC:
                        break;
                    case VOIX_ICI:
                        break;
                    case VOIX_UNECOULEUR:
                        break;
                    case VOIX_CETTECOULEUR:
                        break;
                    case TIMER:
                        break;
                    case VOIX_DEPLACER:
                        break;
                    case COMMECELA:
                        break;
                    case CHOIX_FORME:
                        break;
                    case FORME_SELECTIONNEE:
                        timerCommandeTotale.restart();
                        lastActionMade.setActionEnCours(ActionEnum.CAMMOVE);
                        ivyControl.send("Palette:DeplacerObjet nom=" + forme.getName() + " x=" + x + " y=" + y);
                        break;
                    default:
                        throw new AssertionError(lastActionMade.getActionEnCours().name());

                }
                break;
            default:
                throw new AssertionError(state.name());
        }
    }

    /* ******************************************************
     ******************COMPUTE METHODS
     *  ******************************************************/
//    private String computeRGBToString(String rgb) {
//        System.out.println("************ Compute RGB to string " + rgb);
////        String rgbTab[] = rgb.split("=");
////        String rgbParsed = rgbTab[1].split(",")[0] + ":" + rgbTab[2].split(",")[0] + ":" + rgbTab[3].split("]")[0];
////        System.out.println("Couleur FORME PARSED: " + rgbParsed);
//        return rgb;
//    }
    private void computeGestureCommand(String formeName) {
//        System.out.println("Création d'un " + formeName);
        switch (formeName) {
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
        lastActionMade.init();
    }

    private boolean computeVoiceConfidence(String confidence) {
//        System.out.println("FORME : " + forme.getMyForme());
//        System.out.println("State = " + state);
        String convertedConfidence = confidence.replace(",", ".");
        double confidenceRate = Double.parseDouble(convertedConfidence);
        return (confidenceRate >= 0.60) ? true : false;
    }

    private void computeVoiceCommand(String command) {
        timerCommandeTotale.restart();
        switch (command) {
            case "designationcolor":
                switch (state) {
                    case NOTHING:
                        break;
                    case CREER:
                        state = State.CREER;
                        switch (lastActionMade.getActionEnCours()) {
                            case NULL:
                                lastActionMade.setActionEnCours(ActionEnum.VOIX_CETTECOULEUR);
                                break;
                            case GESTE:
                                break;
                            case CAMMOVE:
                                break;
                            case CAMCOLOR:
                                break;
                            case CLIC:
                                lastActionMade.getParameters();
                                int x = Integer.parseInt(lastActionMade.getParameters()[0]);
                                int y = Integer.parseInt(lastActionMade.getParameters()[1]);
                                System.out.println("CREER FORME CETTE COULEUR");
                                ivyControl.send("Palette:TesterPoint x=" + x + " y=" + y);
                                lastActionMade.init();
                                break;
                            case VOIX_ICI:
                                break;
                            case VOIX_UNECOULEUR:
                                break;
                            case VOIX_CETTECOULEUR:
                                break;
                            case TIMER:
                                break;
                            case VOIX_DEPLACER:
                                break;
                            case COMMECELA:
                                break;
                            default:
                                throw new AssertionError(lastActionMade.getActionEnCours().name());

                        }
                        break;
                    case DEPLACER:
                        state = State.DEPLACER;
                        switch (lastActionMade.getActionEnCours()) {
                            case NULL:
                                lastActionMade.setActionEnCours(ActionEnum.VOIX_CETTECOULEUR);
                                break;
                            case GESTE:
                                break;
                            case CAMMOVE:
                                break;
                            case CAMCOLOR:
                                break;
                            case CLIC:
                                int x = lastActionMade.getReference().x;
                                int y = lastActionMade.getReference().y;
//                                System.out.println("DEPLACER FORME CETTE COULEUR");
                                ivyControl.send("Palette:TesterPoint x=" + x + " y=" + y);
                                lastActionMade.init();
                                break;
                            case VOIX_ICI:
                                break;
                            case VOIX_UNECOULEUR:
                                break;
                            case VOIX_CETTECOULEUR:
                                break;
                            case TIMER:
                                break;
                            case VOIX_DEPLACER:
                                break;
                            case COMMECELA:
                                break;
                            default:
                                throw new AssertionError(lastActionMade.getActionEnCours().name());

                        }
                        break;
                    default:
                        throw new AssertionError(state.name());

                }
                break;
            case "deplacementChoixForme":
                switch (state) {
                    case NOTHING:
                        state = State.DEPLACER;
                        forme.setTypeCommande(Commande.DEPLACEMENT);
                        lastActionMade.setActionEnCours(ActionEnum.VOIX_DEPLACER);
                        timerCommandeTotale.start();
                        break;
                    case CREER:
                        state = State.NOTHING;
                        updateState();
                        break;
                    case DEPLACER:
                        state = State.NOTHING;
                        updateState();
                        break;
                    default:
                        throw new AssertionError(state.name());

                }
                break;
            case "ellipse":
                switch (state) {
                    case NOTHING:
//                        Interdit
                        break;
                    case CREER:
                        state = State.NOTHING;
                        updateState();
                        break;
                    case DEPLACER:
                        state = State.DEPLACER;
                        forme.setMyForme(FormeEnum.ELLIPSE);
                        lastActionMade.setActionEnCours(ActionEnum.CHOIX_FORME);
                        updatePanel();
                        break;
                    default:
                        throw new AssertionError(state.name());
                }
                break;
            case "rectangle":
                switch (state) {
                    case NOTHING:
//                        Interdit
                        break;
                    case CREER:
                        state = State.NOTHING;
                        updateState();
                        break;
                    case DEPLACER:
                        state = State.DEPLACER;
                        forme.setMyForme(FormeEnum.RECTANGLE);
                        lastActionMade.setActionEnCours(ActionEnum.CHOIX_FORME);
                        updatePanel();
                        break;
                    default:
                        throw new AssertionError(state.name());
                }
            case "ici":
                switch (state) {
                    case NOTHING:
                        break;
                    case CREER:
                        state = State.CREER;
                        switch (lastActionMade.getActionEnCours()) {
                            case NULL:
                                lastActionMade.setActionEnCours(ActionEnum.VOIX_ICI);
//                                System.out.println("créer + ici " + lastActionMade.getActionEnCours());
                                break;
                            case GESTE:
                                break;
                            case CAMMOVE:
                                break;
                            case CAMCOLOR:
                                break;
                            case CLIC:
                                String x = lastActionMade.getParameters()[0];
                                String y = lastActionMade.getParameters()[1];
//                                System.out.println("CREER FORME ICI");
                                forme.setPosition(x, y);
                                lastActionMade.init();
                                break;
                            case VOIX_ICI:
                                break;
                            case VOIX_UNECOULEUR:
                                break;
                            case VOIX_CETTECOULEUR:
                                break;
                            case TIMER:
                                break;
                            case VOIX_DEPLACER:
                                break;
                            case COMMECELA:
                                break;
                            default:
                                throw new AssertionError(lastActionMade.getActionEnCours().name());

                        }
                        break;
                    case DEPLACER:
                        state = State.DEPLACER;
                        String x,
                         y;
                        switch (lastActionMade.getActionEnCours()) {
                            case NULL:
                                lastActionMade.setActionEnCours(ActionEnum.VOIX_ICI);
                                break;
                            case GESTE:
                                break;
                            case CAMMOVE:
                                break;
                            case CAMCOLOR:
                                break;
                            case CLIC:
                                forme.setPosition(lastActionMade.getDistanceX() + "", lastActionMade.getDistanceY() + "");
                                break;
                            case FORME_SELECTIONNEE:
                                lastActionMade.setActionEnCours(ActionEnum.VOIX_ICI);
                                break;
                            case VOIX_ICI:
                                break;
                            case VOIX_UNECOULEUR:
                                break;
                            case VOIX_CETTECOULEUR:
                                break;
                            case TIMER:
                                break;
                            case VOIX_DEPLACER:
                                break;
                            case COMMECELA:
                                break;
                            case CHOIX_FORME:
                                lastActionMade.setActionEnCours(ActionEnum.VOIX_ICI);
                                break;
                            default:
                                throw new AssertionError(lastActionMade.getActionEnCours().name());

                        }
                        break;
                    default:
//                        throw new AssertionError(state.name());

                }
                break;
            default:
                //color
                forme.setBackgroundColor(command);
                break;
        }
    }

    private void updateState() {
        switch (state) {
            case NOTHING:
                lastActionMade.init();
                forme.clearForme();
                forme.updateIsComplete();
                timerCommandeTotale.stop();
                break;
            case CREER:
                break;
            case DEPLACER:
                break;
            default:
                throw new AssertionError(state.name());
        }
    }

    private void updatePanel() {
        //label Ivyfusion
        labelState.setText(state.toString());
        labelEtatTimer.setText(timerCommandeTotale.isRunning() + "");
        //Label forme
        labelForme.setText(forme.getMyForme().toString());
        labelNom.setText(forme.getName());
        labelX.setText(forme.getX());
        labelY.setText(forme.getY());
        labelCommande.setText(forme.getTypeCommande().toString());
        //Label dernière action
        labelFormeDerniereAction.setText(lastActionMade.getActionEnCours().toString());
        labelXParam.setText(lastActionMade.getParameters()[0]);
        labelYParam.setText(lastActionMade.getParameters()[1]);
    }

    /**
     * *******************************************************************
     * Ajout caméra
     * *******************************************************************
     */
    private void initCameraHandler() {
        try {
            cameraHandler = new MultipleJava3D();
            cameraHandler.setVisible(true);
            Insets ins = cameraHandler.getInsets();
            cameraHandler.setSize(320 + ins.left + ins.right, 240 + ins.top + ins.bottom);
            cameraHandler.startCapture();
            cameraHandler.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (Exception ex) {
            Logger.getLogger(ModalFusion.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        labelState = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        labelEtatTimer = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        labelSourceEvenement = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        labelForme = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        labelX = new javax.swing.JLabel();
        labelY = new javax.swing.JLabel();
        labelCommande = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labelNom = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        labelFormeDerniereAction = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        labelXParam = new javax.swing.JLabel();
        labelYParam = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridLayout(3, 0));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Etat"));

        jLabel2.setText("Etat courant:");

        labelState.setText("labelState");

        jLabel1.setText("jLabel1");

        jLabel5.setText("Timer commande actif:");

        labelEtatTimer.setText("jLabel13");

        jLabel14.setText("Evènement:");

        labelSourceEvenement.setText("jLabel15");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelState))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelEtatTimer)))
                        .addContainerGap(252, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelSourceEvenement)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(30, 30, 30))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(labelState))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(labelEtatTimer))
                .addGap(38, 38, 38)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel14)
                    .addComponent(labelSourceEvenement))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Forme en mémoire"));

        jLabel6.setText("Forme:");

        labelForme.setText("labelForme");

        jLabel4.setText("X =");

        jLabel7.setText("Y =");

        jLabel8.setText("Commande actuelle =");

        labelX.setText("jLabel9");

        labelY.setText("jLabel10");

        labelCommande.setText("jLabel11");

        jLabel3.setText("Nom =");

        labelNom.setText("jLabel9");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelCommande))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelForme))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelY)
                            .addComponent(labelX)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelNom)))
                .addContainerGap(256, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(labelForme))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(labelNom))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(labelX))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(labelY))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(labelCommande))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Dernière action stockée"));

        jLabel9.setText("Paramètres:");

        jLabel10.setText("Etat:");

        labelFormeDerniereAction.setText("jLabel11");

        jLabel11.setText("X =");

        jLabel12.setText("Y =");

        labelXParam.setText("jLabel5");

        labelYParam.setText("jLabel13");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelYParam))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelXParam))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelFormeDerniereAction)))))
                .addContainerGap(303, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(labelFormeDerniereAction))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(labelXParam))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(labelYParam))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ModalFusion.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ModalFusion.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ModalFusion.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ModalFusion.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ModalFusion().setVisible(true);

                } catch (IvyException ex) {
                    Logger.getLogger(ModalFusion.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel labelCommande;
    private javax.swing.JLabel labelEtatTimer;
    private javax.swing.JLabel labelForme;
    private javax.swing.JLabel labelFormeDerniereAction;
    private javax.swing.JLabel labelNom;
    private javax.swing.JLabel labelSourceEvenement;
    private javax.swing.JLabel labelState;
    private javax.swing.JLabel labelX;
    private javax.swing.JLabel labelXParam;
    private javax.swing.JLabel labelY;
    private javax.swing.JLabel labelYParam;
    // End of variables declaration//GEN-END:variables

}
