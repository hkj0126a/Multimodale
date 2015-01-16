/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multimodal.OneRecognizer;

/**
 *
 * @author hakje
 */
public interface GesteListener {
    public void gesteFinished(Geste geste, boolean isLearning);
}
