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
            //  start miniTimer (à la fin, si ParamAction est defini + etat defini --> action.mergeInfo + forme.setIsComplete(true);
            //                  else clear
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
//                            lastActionMade.getCommand();
                            //switch sur le type de l'action (si c'est pour la forme ou pour interroger la palette sur ivy)
//                                forme.setIsComplete(true);
                            break;
                        case DEPLACER:
                            state = State.DEPLACER;
                            timerCommandeComplementaire.stop();
//                            lastActionMade.getCommand();
                            //switch sur le type de l'action (si c'est pour la forme ou pour interroger la palette sur ivy)
//                                forme.setIsComplete(true);
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
//        System.out.println("Etat = " + state + " Dernier action = " + lastActionMade.getActionEnCours());
        switch (state) {
            case NOTHING:
                state = State.CREER;
                //forme.setIsComplete(false);
                computeGestureCommand(formeName);
                forme.updateIsComplete();
                timerCommandeTotale.restart();
                break;
            case CREER:
                state = State.CREER;
                //forme.setIsComplete(false);
                computeGestureCommand(formeName);
                forme.updateIsComplete();
                timerCommandeTotale.restart();
                break;
            case DEPLACER:
                //computeGestureCommand(direction);
                break;
            default:
                throw new AssertionError(state.name());

        }

    }

    @Override
    public void sraListener(IvyClient client, String confidence, String command) {
//        System.out.println("Etat = " + state + " Dernier action = " + lastActionMade.getActionEnCours());
        if (computeVoiceConfidence(confidence)) {
            computeVoiceCommand(command);
        }
    }

    @Override
    public void paletteMousePressedListener(String x, String y) {
//        System.out.println("Etat = " + state + " Dernier action = " + lastActionMade.getActionEnCours());
        switch (state) {
            case NOTHING:
                break;
            case CREER:
                state = State.CREER;
                //forme.setIsComplete(false);
//                forme.updateIsComplete();
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
                        forme.updateIsComplete();
                        lastActionMade.init();
                        break;
                    case VOIX_UNECOULEUR:
                        break;
                    case VOIX_CETTECOULEUR:
                        timerCommandeComplementaire.stop();
                        System.out.println("CREER FORME CETTE COULEUR");
                        ivyControl.send("Palette:TesterPoint x=" + x + " y=" + y);
                        //A mettre dans le listener de la palette
                        //Récupérer la couleur de la forme cliquée et faire un forme.setCouleur();
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
                        break;
                    case CAMCOLOR:
                        break;
                    case CLIC:
                        break;
                    case VOIX_ICI:
                        timerCommandeComplementaire.stop();
                        System.out.println("CHOIX DEPLACEMENT POSITION");
                        //forme.setIsComplete(true);
                        forme.setX(x);
                        forme.setY(y);
                        forme.updateIsComplete();
                        lastActionMade.init();
                        break;
                    case VOIX_UNECOULEUR:
                        break;
                    case VOIX_CETTECOULEUR:
                        timerCommandeComplementaire.stop();
                        //Selection de la forme en fonction de la couleur
                        //forme.setIsComplete(false);
                        System.out.println("CHOIX DEPLACEMENT FORME");
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
            default:
                throw new AssertionError(state.name());
        }
    }

    @Override
    public void paletteFormeInformationListener(String name, String backgroundColor, String strokeColor) {
        System.out.println("Etat = " + state + " Dernier action = " + lastActionMade.getActionEnCours());
        switch (state) {
            case NOTHING:
                break;
            case CREER:
                state = State.CREER;
                timerCommandeTotale.restart();
                //forme.setIsComplete(true);
                forme.updateIsComplete();
                forme.setBackgroundColor(computeRGBToString(backgroundColor));
                forme.setStrokeColor(computeRGBToString(strokeColor));
                break;
            case DEPLACER:
                state = State.DEPLACER;
                timerCommandeTotale.restart();
                //forme.setIsComplete(true);
                forme.updateIsComplete();
                //verifier qu'il y'a pas 2 rectangles au même endroit
                forme.setName(name);
                state = State.DEPLACER_CHOISIR_POSITION;
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

        String convertedConfidence = confidence.replace(",", ".");
        double confidenceRate = Double.parseDouble(convertedConfidence);
        return (confidenceRate >= 0.60) ? true : false;
    }

    private void computeVoiceCommand(String command) {
        switch (command) {
            case "validation":
                if (forme.toString() != null) {
                    switch (state) {
                        case NOTHING:
                            break;
                        case DEPLACER_CHOISIR_FORME:
                            break;
                        case CREER_COLOR:
                            ivyControl.send(forme.commandToCreateFormePatern());
                            break;
                        case CREER_POSITION:
                            ivyControl.send(forme.commandToCreateFormePatern());
                            break;
                        case DEPLACER_CHOISIR_POSITION:
                            ivyControl.send(forme.commandToMoveFormePatern());
                            break;

                        default:
                            throw new AssertionError(state.name());
                    }
                    System.out.println(forme);
                }
                forme.clearForme();
                break;
            case "designationcolor":
//                state = State.CREER_COLOR;
                switch (state) {
                    case NOTHING:
                        break;
                    case CREER:
                        state = State.CREER;
                        //forme.setIsComplete(false);
                        forme.updateIsComplete();
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
                        //if lastAction c'est clic
                        //  lastAction.getCommand
                        //  execute
                        //  miniTimer.stop
                        //else
                        //  Créer action avec "designationColor"
                        //  miniTimer.restart
                        break;
                    case DEPLACER:
                        state = State.DEPLACER;
                        //forme.setIsComplete(false);
                        forme.updateIsComplete();
                        //Créer action avec "designationColor"                        
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
                        break;
                    case CREER:
                        state = State.CREER;
                        //forme.setIsComplete(false);
                        forme.updateIsComplete();
                        //Créer action avec "deplacementChoixForme"
                        break;
                    case DEPLACER:
                        state = State.DEPLACER;
                        //forme.setIsComplete(false);
                        forme.updateIsComplete();
                        //Créer action avec "deplacementChoixForme"                        
                        break;
                    default:
                        throw new AssertionError(state.name());

                }
//                forme.clearForme();
//                state = State.DEPLACER_CHOISIR_FORME;
                break;
            case "deplacementChoixPosition":
                switch (state) {
                    case NOTHING:
                        break;
                    case CREER:
                        state = State.CREER;
                        //forme.setIsComplete(false);
                        forme.updateIsComplete();
                        //Créer action avec "deplacementChoixPos"
                        break;
                    case DEPLACER:
                        state = State.DEPLACER;
                        //forme.setIsComplete(false);
                        forme.updateIsComplete();
                        //Créer action avec "deplacementChoixPosition"                        
                        break;
                    default:
                        throw new AssertionError(state.name());

                }
                //forme.clearForme();
//                state = State.DEPLACER_CHOISIR_POSITION;
                break;
            case "ici":
                //state = State.CREER_POSITION;
                switch (state) {
                    case NOTHING:
                        break;
                    case CREER:
                        state = State.CREER;
                        //forme.setIsComplete(false);
                        forme.updateIsComplete();
                        //Créer action avec "ICI"
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
                                forme.updateIsComplete();
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
                        forme.updateIsComplete();
                        //forme.setIsComplete(false);
                        //Créer action avec "ICI"
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
                                forme.updateIsComplete();
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
                        forme.updateIsComplete();
                        //forme.setIsComplete(false);
                        //Créer action avec "commeCela"
                        break;
                    case DEPLACER:
                        state = State.DEPLACER;
                        //forme.setIsComplete(false);
                        forme.updateIsComplete();
                        //Créer action avec "commeCela"                        
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
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
            java.util.logging.Logger.getLogger(ModalFusion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ModalFusion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ModalFusion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ModalFusion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ModalFusion().setVisible(true);
                } catch (IvyException ex) {
                    Logger.getLogger(ModalFusion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
