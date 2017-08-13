/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supportai_evolution;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *  An encapsulation of any targets SAI-E needs to keep track of, like other players, creatures, etc.
 * @author Robert
 */
public class SAIE_Target {
    private final String name;
    
    private Rectangle xyhp;     //Description of where the health point bar (hpbar) is on screen.
    private int[] hpBar;        //Line or array of pixels to check hp with, to increase speed of check.
    private Color chp;          //Average color of hpbar, to know if we're looking for a red, blue, purple, etc. hpbar
    private int chpDev;       //Maximum deviation of hpbar checked pixels from avg color, to see past shifting, shimmering, and speckling.
    
    private boolean isDead;     //To change mode from active to waiting and/or terminate program in early stages.
    private int hp;             //Current pixel count, which is used as the AI's sight on the hp value.
    
    
    public SAIE_Target(String name,Rectangle xyhp,Color chp,int chpIDev){
        this.name=name;
        
        this.xyhp=xyhp;
        isDead=false;
        this.chp=chp;
        this.chpDev=chpIDev;
        
        openEyes();
    }
    
    /** Initialization of the target: health bars, targeting, etc */
    private void openEyes(){
        BufferedImage img=SAIE_Util.r.createScreenCapture(xyhp);
        
    }
    
    /** Updating the values of the target, mainly hp/stats/etc */
    public void update(){}
    
    public String getName(){return name;}
    public Rectangle getHpBox(){return xyhp;}
    public Color getHpColor(){return chp;}
    public float getHp(){return (float)hp/xyhp.width;}
    public boolean isDead(){return isDead;}
}
