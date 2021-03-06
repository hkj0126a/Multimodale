/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivytp.scribble;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;
import ivytp.scribbleListener.GestureListener;
import ivytp.scribbleListener.WordListener;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import static java.awt.event.MouseEvent.BUTTON1;
import static java.awt.event.MouseEvent.BUTTON2;
import static java.awt.event.MouseEvent.BUTTON3;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hakje
 */
public class Scribble extends javax.swing.JPanel {

    private Ivy bus;
    private List<Trait> mesTraits;
    private Trait traitCourant;
    private Point pointPrecedent;

    /**
     * Creates new form Scribble
     */
    public Scribble() {
        initComponents();
        mesTraits = new ArrayList();
        traitCourant = new Trait();
        pointPrecedent = null;
        bus = new Ivy("IvyScribble", "IvyScribble Ready", null);
        initBus();
        initListener();
    }

    private void initBus() {
        try {
            bus.start("10.3.8.255:1234");
        } catch (IvyException ex) {
            Logger.getLogger(Scribble.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initListener() {
        try {
            //Gestures
            bus.bindMsg("RecoWin (.*) RecoResult=(.*) RecoConfidence=", new GestureListener(bus));
            //Lettres
            bus.bindMsg("RecoWin (.*) RecoResult=(.*) Seg=", new WordListener(bus));
        } catch (IvyException ex) {
            Logger.getLogger(Scribble.class.getName()).log(Level.SEVERE, null, ex);
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

        zoneDessin = new javax.swing.JPanel();
        jButtonClear = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        zoneDessin.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                zoneDessinMouseDragged(evt);
            }
        });
        zoneDessin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                zoneDessinMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                zoneDessinMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout zoneDessinLayout = new javax.swing.GroupLayout(zoneDessin);
        zoneDessin.setLayout(zoneDessinLayout);
        zoneDessinLayout.setHorizontalGroup(
            zoneDessinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        zoneDessinLayout.setVerticalGroup(
            zoneDessinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 271, Short.MAX_VALUE)
        );

        jButtonClear.setText("Clear");
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });

        jButton1.setText("Send");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(zoneDessin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jButtonClear)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(zoneDessin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonClear)
                    .addComponent(jButton1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        mesTraits.clear();
        repaint();
        validate();
    }//GEN-LAST:event_jButtonClearActionPerformed

    private void zoneDessinMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_zoneDessinMouseDragged

        traitCourant.addCoordonnee(evt.getX(), evt.getY());

        if (pointPrecedent != null) {
            Graphics g = zoneDessin.getGraphics();
            g.setColor(Color.black);

            g.drawLine(pointPrecedent.x, pointPrecedent.y,
                    evt.getX(), evt.getY());
        }
        pointPrecedent = new Point(evt.getX(), evt.getY());
    }//GEN-LAST:event_zoneDessinMouseDragged

    private void zoneDessinMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_zoneDessinMouseClicked
        if (evt.getButton() == BUTTON1) {
            pointPrecedent = new Point(evt.getX(), evt.getY());
        }
        if (evt.getButton() == BUTTON2) {
            try {
                String coord = "";
                for (Trait t : mesTraits) {
                    coord += t.toString();
                }

                String message = "RecoWin MsgName=5 ";
                message += "RecognizeGesture=" + coord;
                bus.sendMsg(message);

            } catch (IvyException ex) {
                Logger.getLogger(Scribble.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        if (evt.getButton() == BUTTON3) {
            mesTraits.clear();
            repaint();
            validate();
        }
    }//GEN-LAST:event_zoneDessinMouseClicked

    private void zoneDessinMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_zoneDessinMouseReleased
        mesTraits.add(traitCourant);

        traitCourant = new Trait();
        pointPrecedent = null;
    }//GEN-LAST:event_zoneDessinMouseReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            String coord = "";
            for (Trait t : mesTraits) {
                coord += t.toString();
            }
            //Reconnaissance textuelle
            String message = "RecoWin MsgName=5 Factoid= ";
            message += "Recognize=" + coord;

            bus.sendMsg(message);
        } catch (IvyException ex) {
            Logger.getLogger(Scribble.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JPanel zoneDessin;
    // End of variables declaration//GEN-END:variables
}
