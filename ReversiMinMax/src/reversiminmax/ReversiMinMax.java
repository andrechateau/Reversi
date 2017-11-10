/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reversiminmax;

import GUI.Game;
import javax.swing.JOptionPane;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;

/**
 *
 * @author Andre Chateaubriand
 */
public class ReversiMinMax {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int k = JOptionPane.showConfirmDialog(null, "Play versus IA?", "IA Mode", JOptionPane.YES_NO_OPTION);
        Game.IA = k == 0;
        try {
            AppGameContainer app = new AppGameContainer(new ScalableGame(new Game("Reversi"), 800, 600));
            app.setDisplayMode(800, 600, false);
            //app.setTitle("Mundos Profundis");
            app.setIcon("res/white.png");
            app.setAlwaysRender(true);
            app.setTargetFrameRate(60);
            app.setVSync(true);
            app.setVerbose(false);
            app.start();

        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

}
