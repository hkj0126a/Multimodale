/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multimodal;

import fr.dgac.ivy.IvyClient;

/**
 *
 * @author nathan
 */
public interface ModalFusionListener {
    public void icarListener(IvyClient client, String forme);
    public void sraListener(IvyClient client, String confidence, String voiceCommand);
    public void paletteMousePressedListener(String x, String y);
    public void paletteFormeInformationListener(String name, String backgroundColor, String strokeColor);
}
