/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multimodal;

import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import multimodal.ivyControl.IvyControl;

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
    private Timer timerCommandeComplementaire;

    public ModalFusion() throws IvyException {
        initComponents();
        forme = new Forme();
        ivyControl = new IvyControl(this);
        lastActionMade = new Action();
        initTimer();
        initTimerActionComplementaire();
    }

    /* ******************************************************
     ****************** INIT METHODS
     *  ******************************************************/
    private void initTimer() {
        ActionListener timerOutListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Fin timer, forme complete ? " + forme.isComplete());
                System.out.println("Forme = " + forme.getMyForme() + " Pos = " + forme.getX() + " " + forme.getY());
                forme.updateIsComplete();
                if (forme.isComplete()) {
                    switch (state) {
                        case NOTHING:
                            timerCommandeTotale.stop();
                            break;
                        case CREER:
                            state = State.NOTHING;
                            timerCommandeTotale.stop();
                            ivyControl.send(forme.commandToCreateFormePatern());
                            break;
                        case DEPLACER:
                            state = State.NOTHING;
                            timerCommandeTotale.stop();
                            ivyControl.send(forme.commandToMoveFormePatern());
                            System.out.println(forme.commandToMoveFormePatern());
                            break;
                        default:
                            throw new AssertionError(state.name());

                    }
                } else {
                    forme.clearForme();
                    timerCommandeTotale.stop();
                }
            }
        };
        timerCommandeTotale = new Timer(4000, timerOutListener);
    }

    private void initTimerActionComplementaire() {
        ActionListener timerOutListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Timer complementaire finis ");
                if (lastActionMade.isComplete()) {
                    switch (state) {
                        case NOTHING:
                            timerCommandeComplementaire.stop();
                            break;
                        case CREER:
                            state = State.CREER;
                            timerCommandeComplementaire.stop();
                            break;
                        case DEPLACER:
                            state = State.DEPLACER;
                            timerCommandeComplementaire.stop();
                            break;
                        default:
                            throw new AssertionError(state.name());
                    }
                } else {
                    lastActionMade.init();
                    timerCommandeComplementaire.stop();
                }
            }
        };
        timerCommandeComplementaire = new Timer(2000, timerOutListener);
    }

    /* ******************************************************
     ******************OVERRIDE LISTENER METHODS
     *  ******************************************************/
    @Override
    public void icarListener(IvyClient client, String formeName) {
        switch (state) {
            case NOTHING:
                state = State.CREER;
                computeGestureCommand(formeName);
//                forme.updateIsComplete();
                timerCommandeTotale.restart();
                break;
            case CREER:
                state = State.NOTHING;
                updateState();
//                state = State.CREER;
//                computeGestureCommand(formeName);
//                forme.updateIsComplete();
//                timerCommandeTotale.restart();
                break;
            case DEPLACER:
                break;
            default:
                throw new AssertionError(state.name());

        }

    }

    @Override
    public void sraListener(IvyClient client, String confidence, String command) {
        if (computeVoiceConfidence(confidence)) {
            computeVoiceCommand(command);
        }
    }

    @Override
    public void paletteMousePressedListener(String x, String y) {
        switch (state) {
            case NOTHING:
                break;
            case CREER:
                state = State.CREER;
                timerCommandeTotale.restart();
                switch (lastActionMade.getActionEnCours()) {
                    case NULL:
                        timerCommandeComplementaire.restart();
                        lastActionMade.setActionEnCours(ActionEnum.CLIC);
                        String[] param = new String[2];
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
                        timerCommandeComplementaire.stop();
                        System.out.println("CREER FORME POSITION");
                        forme.setPosition(x, y);
//                        forme.updateIsComplete();
                        lastActionMade.init();
                        break;
                    case VOIX_UNECOULEUR:
                        break;
                    case VOIX_CETTECOULEUR:
                        timerCommandeComplementaire.stop();
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
                if (timerCommandeComplementaire.isRunning()) {
                    switch (lastActionMade.getActionEnCours()) {
                        case CLIC:
                            break;
                        case CHOIX_FORME:
                            timerCommandeComplementaire.stop();
                            System.out.println("SELECTION FORME");
                            ivyControl.send("Palette:TesterPoint x=" + x + " y=" + y);

                            break;
                        case VOIX_ICI:
//                            if (!forme.getName().equals("")) {
                            timerCommandeComplementaire.stop();
                            System.out.println("CHOIX DEPLACEMENT POSITION");
                            forme.setX(x);
                            forme.setY(y);
//                            forme.updateIsComplete();
                            lastActionMade.init();
//                            } else {
//
//                            }

                            break;
                        case VOIX_CETTECOULEUR:
                            timerCommandeComplementaire.stop();
                            System.out.println("CHOIX DEPLACEMENT FORME");
                            ivyControl.send("Palette:TesterPoint x=" + x + " y=" + y);
                            lastActionMade.init();
                            break;
                        default:
                            throw new AssertionError(lastActionMade.getActionEnCours().name());

                    }
                } else {
                    timerCommandeComplementaire.restart();
                    lastActionMade.setActionEnCours(ActionEnum.CLIC);
                    String[] param = new String[2];
                    param[0] = x;
                    param[1] = y;
                    lastActionMade.setParameters(param);
                }
                break;
            default:
                throw new AssertionError(state.name());
        }
    }

    @Override
    public void paletteFormeInformationListener(String name, String backgroundColor, String strokeColor) {
        System.out.println("Etat = " + state + " Dernier action = " + lastActionMade.getActionEnCours());
        System.out.println("Name = " + name);
        switch (state) {
            case NOTHING:
                break;
            case CREER:
                state = State.CREER;
                timerCommandeTotale.restart();
//                forme.updateIsComplete();
                forme.setBackgroundColor(computeRGBToString(backgroundColor));
                forme.setStrokeColor(computeRGBToString(strokeColor));
                break;
            case DEPLACER:
                state = State.DEPLACER;
                timerCommandeTotale.restart();
                switch (lastActionMade.getActionEnCours()) {

                    case CHOIX_FORME:
                        System.out.println("nom de la forme " + name + " char at 0 " + name.charAt(0));
                        System.out.println("Déplacement de la forme " + forme.getMyForme());
                        if (name.charAt(0) == 'R') {//&& forme.getMyForme() == FormeEnum.RECTANGLE) {
                            System.out.println("J'ai récupéré le rectangle");
                            forme.setName(name);
//                            forme.updateIsComplete();
                            lastActionMade.setActionEnCours(ActionEnum.FORME_SELECTIONNEE);
                        } else {
//                            jLabel1.setText("La forme cliquée ne corresponds pas à ce que t'as dis");
//                            lastActionMade.init();
//                            state = State.NOTHING;
//                            updateState();
                        }
                        if (name.charAt(0) == 'E') {// && forme.getMyForme() == FormeEnum.ELLIPSE) {
                            System.out.println("J'ai récupéré le cercle");
                            forme.setName(name);
//                            forme.updateIsComplete();
                            lastActionMade.setActionEnCours(ActionEnum.FORME_SELECTIONNEE);
                        } else {
//                            jLabel1.setText("La forme cliquée ne corresponds pas à ce que t'as dis");
//                            lastActionMade.init();
//                            state = State.NOTHING;
//                            updateState();
                        }
                        break;
                    default:
                        throw new AssertionError(lastActionMade.getActionEnCours().name());

                }
                if (forme.getMyForme() != null) {
                    if (forme.getMyForme().equals(FormeEnum.ELLIPSE) && name.startsWith("r")) {

                    }
                    if (forme.getMyForme().equals(FormeEnum.RECTANGLE) && name.startsWith("r")) {

                    }
                }
                forme.setName(name);

//                forme.updateIsComplete();
                //verifier qu'il y'a pas 2 rectangles au même endroit

//                state = State.DEPLACER_CHOISIR_POSITION;
                break;
            default:
                break;
        }
    }

    /* ******************************************************
     ******************COMPUTE METHODS
     *  ******************************************************/
    private String computeRGBToString(String rgb) {
        String rgbTab[] = rgb.split("=");
        String rgbParsed = rgbTab[1].split(",")[0] + ":" + rgbTab[2].split(",")[0] + ":" + rgbTab[3].split("]")[0];
        System.out.println("Couleur FORME PARSED: " + rgbParsed);
        return rgbParsed;
    }

    private void computeGestureCommand(String formeName) {
        System.out.println("Création d'un " + formeName);
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
        System.out.println("FORME : " + forme.getMyForme());
        System.out.println("State = " + state);
        String convertedConfidence = confidence.replace(",", ".");
        double confidenceRate = Double.parseDouble(convertedConfidence);
        return (confidenceRate >= 0.60) ? true : false;
    }

    private void computeVoiceCommand(String command) {
        switch (command) {

            case "designationcolor":
                switch (state) {
                    case NOTHING:
                        break;
                    case CREER:
                        state = State.CREER;
//                        forme.updateIsComplete();
                        switch (lastActionMade.getActionEnCours()) {
                            case NULL:
                                timerCommandeComplementaire.restart();
                                lastActionMade.setActionEnCours(ActionEnum.VOIX_CETTECOULEUR);
                                String[] param = new String[2];
                                param[0] = "designationcolor";
                                lastActionMade.setParameters(param);
                                break;
                            case GESTE:
                                break;
                            case CAMMOVE:
                                break;
                            case CAMCOLOR:
                                break;
                            case CLIC:
                                timerCommandeComplementaire.stop();
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
//                        forme.updateIsComplete();
                        switch (lastActionMade.getActionEnCours()) {
                            case NULL:
                                timerCommandeComplementaire.restart();
                                lastActionMade.setActionEnCours(ActionEnum.VOIX_CETTECOULEUR);
                                String[] param = new String[2];
                                param[0] = "designationcolor";
                                lastActionMade.setParameters(param);
                                break;
                            case GESTE:
                                break;
                            case CAMMOVE:
                                break;
                            case CAMCOLOR:
                                break;
                            case CLIC:
                                timerCommandeComplementaire.stop();
                                lastActionMade.getParameters();
                                int x = Integer.parseInt(lastActionMade.getParameters()[0]);
                                int y = Integer.parseInt(lastActionMade.getParameters()[1]);
                                System.out.println("DEPLACER FORME CETTE COULEUR");
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
                        timerCommandeTotale.start();
                        break;
                    case CREER:
//                        state = State.CREER;
//                        forme.updateIsComplete();
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
            case "rond":
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
                        System.out.println("deplacer rond");
                        if (lastActionMade.getActionEnCours() == ActionEnum.CLIC) {
                            forme.setMyForme(FormeEnum.ELLIPSE);
//                            forme.updateIsComplete();
                            timerCommandeComplementaire.stop();
                            //lastActionMade.setActionEnCours(ActionEnum.CHOIX_FORME);
                            lastActionMade.init();
                        } else {
                            timerCommandeComplementaire.restart();
                            forme.setMyForme(FormeEnum.ELLIPSE);
//                            forme.updateIsComplete();
                            lastActionMade.setActionEnCours(ActionEnum.CHOIX_FORME);
                        }
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
                        System.out.println("deplacer rectangle");
                        if (lastActionMade.getActionEnCours() == ActionEnum.CLIC) {
                            forme.setIsComplete(true);
                            forme.setMyForme(FormeEnum.RECTANGLE);
                            timerCommandeComplementaire.stop();
                            //lastActionMade.setActionEnCours(ActionEnum.CHOIX_FORME);
                            lastActionMade.init();
                        } else {
                            forme.setIsComplete(false);
                            timerCommandeComplementaire.restart();
                            forme.setMyForme(FormeEnum.RECTANGLE);
                            lastActionMade.setActionEnCours(ActionEnum.CHOIX_FORME);
                        }
                        break;
                    default:
                        throw new AssertionError(state.name());
                }
            case "deplacementChoixPosition":
                switch (state) {
                    case NOTHING:
                        break;
                    case CREER:
                        state = State.CREER;
//                        forme.updateIsComplete();
                        break;
                    case DEPLACER:
                        state = State.DEPLACER;
//                        forme.updateIsComplete();
                        break;
                    default:
                        throw new AssertionError(state.name());

                }
                break;
            case "ici":
                switch (state) {
                    case NOTHING:
                        break;
                    case CREER:
                        state = State.CREER;
//                        forme.updateIsComplete();
                        switch (lastActionMade.getActionEnCours()) {
                            case NULL:
                                timerCommandeComplementaire.restart();
                                lastActionMade.setActionEnCours(ActionEnum.VOIX_ICI);
                                System.out.println("créer + ici " + lastActionMade.getActionEnCours());
                                break;
                            case GESTE:
                                break;
                            case CAMMOVE:
                                break;
                            case CAMCOLOR:
                                break;
                            case CLIC:
                                timerCommandeComplementaire.stop();
                                lastActionMade.getParameters();
                                String x = lastActionMade.getParameters()[0];
                                String y = lastActionMade.getParameters()[1];
                                System.out.println("CREER FORME ICI");
                                forme.setPosition(x, y);
//                                forme.updateIsComplete();
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
//                        forme.updateIsComplete();
                        switch (lastActionMade.getActionEnCours()) {
                            case NULL:
                                timerCommandeComplementaire.restart();
                                lastActionMade.setActionEnCours(ActionEnum.VOIX_ICI);
                                break;
                            case GESTE:
                                break;
                            case CAMMOVE:
                                break;
                            case CAMCOLOR:
                                break;
                            case CLIC:
                                timerCommandeComplementaire.stop();
                                lastActionMade.getParameters();
                                String x = lastActionMade.getParameters()[0];
                                String y = lastActionMade.getParameters()[1];
                                System.out.println("DEPLACER FORME ICI");
                                forme.setX(x);
                                forme.setY(y);
//                                forme.updateIsComplete();
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
            case "commeCela":
                switch (state) {
                    case NOTHING:
                        break;
                    case CREER:
                        state = State.CREER;
//                        forme.updateIsComplete();
                        break;
                    case DEPLACER:
                        state = State.DEPLACER;
//                        forme.updateIsComplete();
                        break;
                    default:
                        throw new AssertionError(state.name());

                }
                break;
            default:
                //color
                forme.setBackgroundColor(command);
                break;
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

        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addComponent(jLabel1)
                .addContainerGap(296, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addComponent(jLabel1)
                .addContainerGap(207, Short.MAX_VALUE))
        );

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
                if ("Nimbus".equals(info.getName())) {
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
    // End of variables declaration//GEN-END:variables
    private void updateState() {
        switch (state) {
            case NOTHING:
                forme.clearForme();
                timerCommandeTotale.stop();
                forme.updateIsComplete();
                timerCommandeTotale.stop();
                timerCommandeComplementaire.stop();
                break;
            case CREER:
                break;
            case DEPLACER:
                break;
            case CREER_COLOR:
                break;
            case CREER_POSITION:
                break;
            case DEPLACER_CHOISIR_FORME:
                break;
            case DEPLACER_CHOISIR_POSITION:
                break;
            default:
                throw new AssertionError(state.name());
        }
    }
}
