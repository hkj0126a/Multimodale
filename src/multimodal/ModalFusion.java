/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multimodal;

import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public ModalFusion() throws IvyException {
        initComponents();
        forme = new Forme();
        ivyControl = new IvyControl(this);
        lastActionMade = new Action();
    }

    /* ******************************************************
     ******************OVERRIDE LISTENER METHODS
     *  ******************************************************/
    @Override
    public void icarListener(IvyClient client, String formeName) {

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
            case CREER_COLOR:
                System.out.println("CREER FORME CETTE COULEUR");
                ivyControl.send("Palette:TesterPoint x=" + x + " y=" + y);
                break;
            case CREER_POSITION:
                System.out.println("CREER FORME POSITION");
                forme.setPosition(x, y);
                state = State.NOTHING;
                break;
            case DEPLACER_CHOISIR_FORME:
                System.out.println("CHOIX DEPLACEMENT FORME");
                ivyControl.send("Palette:TesterPoint x=" + x + " y=" + y);
                //state = State.DEPLACER_CHOISIR_POSITION;
                break;
            case DEPLACER_CHOISIR_POSITION:
                System.out.println("CHOIX DEPLACEMENT POSITION");
                forme.setX(x);
                forme.setY(y);
                break;
        }
    }

    @Override
    public void paletteFormeInformationListener(String name, String backgroundColor, String strokeColor) {
        switch (state) {
            case CREER_COLOR:
                forme.setBackgroundColor(computeRGBToString(backgroundColor));
                forme.setStrokeColor(computeRGBToString(strokeColor));
                state = State.NOTHING;
                break;
            case DEPLACER_CHOISIR_FORME:
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

    private boolean computeVoiceConfidence(String confidence) {
        System.out.println("FORME : " + forme);

        String convertedConfidence = confidence.replace(",", ".");
        double confidenceRate = Double.parseDouble(convertedConfidence);
        return (confidenceRate >= 0.90) ? true : false;
    }

    private void computeVoiceCommand(String command) {
        switch (command) {
            case "validation":
                if (forme.toString() != null) {
                    switch (state) {
                        case CREER_COLOR:
                            ivyControl.send(forme.commandToCreateFormePatern());
                            break;
                        case CREER_POSITION:
                            ivyControl.send(forme.commandToCreateFormePatern());
                            break;
                        case DEPLACER_CHOISIR_POSITION:
                            ivyControl.send(forme.commandToMoveFormePatern());
                            break;
                    }
                    System.out.println(forme);
                }
                forme.clearForme();
                break;
            case "designationcolor":
                state = State.CREER_COLOR;
                break;
            case "deplacementChoixForme":
                forme.clearForme();
                state = State.DEPLACER_CHOISIR_FORME;
                break;
            case "deplacementChoixPosition":
                //forme.clearForme();
                state = State.DEPLACER_CHOISIR_POSITION;
                break;
            case "ici":
                state = State.CREER_POSITION;
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
