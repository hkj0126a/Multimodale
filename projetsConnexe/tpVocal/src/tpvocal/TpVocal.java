/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpvocal;

import javax.swing.JFrame;

/**
 *
 * @author hakje
 */
public class TpVocal {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MyPanel p = new MyPanel();
        JFrame f = new JFrame();
        f.add(p);
        f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
