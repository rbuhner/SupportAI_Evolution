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
    
    
    public SAIE_Target(String name,Rectangle xyhp,Color chp,int chpIDev,boolean optimize) throws SAIE_Util.InvalidValueException{
        this.name=name;
        isDead=false;
        
        //These will be optimized if allowed to, which allows some invalid answers to be corrected to a limited degree.
        this.xyhp=xyhp;
        this.chp=chp;
        this.chpDev=chpIDev;
        
        openEyes(optimize);
    }
    
    /** Initialization of the target: health bars, targeting, etc */
    private void openEyes(boolean optimize) throws SAIE_Util.InvalidValueException{
        BufferedImage img=SAIE_Util.r.createScreenCapture(xyhp);
        
        if(optimize){   //Color and deviation have been optimized, rectangle not yet. (later)
            //Color given will be an approximation up to the exact avg color.
            //Collecting all pixels and avg over number of pixels, seperating to get better definitions.
            System.out.println("Attempting optimization of target "+name+"...");
            if(chpDev<=0){chpDev=32;}
            
            if(!openEyesSub1(img)){
                System.out.println("First level optimization failed, attempting broader optimization...");
                chp=SAIE_Util.ClosestColor(chp).color;
                chpDev=64;
                
                if(!openEyesSub1(img)){
                    System.out.println("Error: "+name+"'s Hp has too many invalid colors, please adjust and rerun.");
                    throw new SAIE_Util.InvalidValueException();
                }
            }
        }
        
        int ty=-1;
        for(int y=0;y<xyhp.height;y++){
            ty=y;
            for(int x=0;x<xyhp.width;x++){
                //If the pixel is within deviation of the avg color, great, otherwise nextline.
                if(Math.abs(SAIE_Util.colorDif(chp,SAIE_Util.PixeltoColor(img.getRGB(x,y))))>chpDev){
                    ty=-1;
                    break;
                }
            }
            if(ty==y){break;}
        }
        if(ty!=-1){
            hpBar=new int[]{ty};
            System.out.println(name+"'s HpBar scanline set to "+ty+".");
        }else{
            System.out.println("Unable to find a singular line to read.");
            int aty[]=new int[xyhp.width];
            //Given no single continuous line is valid, look for any y per continuous valid x within the box of xyhp
            for(int x=0;x<xyhp.width;x++){
                for(int y=0;y<xyhp.height;y++){
                    if(Math.abs(SAIE_Util.colorDif(chp,SAIE_Util.PixeltoColor(img.getRGB(x,y))))>chpDev){
                        aty[x]=y;
                        break;
                    }else{aty[x]=-1;}
                }
                if(aty[x]==-1){
                    System.err.println("Error: There are no valid pixels in "+name+"'s x line "+x+".");
                    throw new SAIE_Util.InvalidValueException();
                }
            }
            hpBar=aty;
        }
        System.out.println(name+"'s HpBar now set. HpBar length of "+hpBar.length+".");
    }
    private boolean openEyesSub1(BufferedImage img){
        long cc[]=new long[]{0,0,0};    //Color collection
        int pc=0,ipc=0;                 //Pixel / Invalid Pixel count
        Color max=chp,min=chp,tc;       //Brighted/Darkest color in spectrum
        
        for(int y=0;y<img.getHeight();y++){
            for(int x=0;x<img.getWidth();x++){
                tc=SAIE_Util.PixeltoColor(img.getRGB(x,y));
                if(Math.abs(SAIE_Util.colorDif(chp,tc))<=chpDev){
                    cc[0]+=tc.getRed();
                    cc[1]+=tc.getGreen();
                    cc[2]+=tc.getBlue();
                    pc++;

                    if(SAIE_Util.colorDif(max,tc)>0){max=tc;}
                    else if(SAIE_Util.colorDif(min,tc)<0){min=tc;}
                }else{ipc++;}
            }
        }
        if(pc>ipc){ //If the invalid pixels outnumber the ones we can use, try a wider net, and if still not working, soft exit.
            Color ctemp=new Color((int)(cc[0]/pc),(int)(cc[1]/pc),(int)(cc[2]/pc));
            System.out.println(name+"'s Hp Color is: "+SAIE_Util.ClosestColor(ctemp).color.toString()+".");
            chp=ctemp;

            int tmax=Math.abs(SAIE_Util.colorDif(chp,max));
            int tmin=Math.abs(SAIE_Util.colorDif(chp,min));
            chpDev=(tmax>tmin?tmax:tmin);
            System.out.println(name+"'s Hp Color Avg Deviation is: "+chpDev+".");
            return true;
        }else{return false;}
    }
    
    /** Updating the values of the target, mainly hp/stats/etc */
    public void update(){
        BufferedImage img=SAIE_Util.r.createScreenCapture(xyhp);
        
        //If one contiguous line was found, use it, otherwise use the array of contiguous points found.
        if(hpBar.length==1){
            //To save on time and cycles, start from the known hp.
            if(Math.abs(SAIE_Util.colorDif(chp,SAIE_Util.PixeltoColor(img.getRGB(hp,hpBar[0]))))<=chpDev){
                for(hp++;hp<xyhp.width;hp++){
                    //If color found is not within deviation of avgColor, then we've hit the height of hp level.
                    if(Math.abs(SAIE_Util.colorDif(chp,SAIE_Util.PixeltoColor(img.getRGB(hp,hpBar[0]))))>chpDev){hp--;break;}
                }
            }else{
                //As above, reversed.
                for(hp--;hp>-1;hp--){
                    //If color found is within deviation of avgColor, then we've hit hp level
                    if(Math.abs(SAIE_Util.colorDif(chp,SAIE_Util.PixeltoColor(img.getRGB(hp,hpBar[0]))))<=chpDev){break;}
                }
            }
        }else{
            if(Math.abs(SAIE_Util.colorDif(chp,SAIE_Util.PixeltoColor(img.getRGB(hp,hpBar[0]))))<=chpDev){
                for(hp++;hp<xyhp.width;hp++){
                    if(Math.abs(SAIE_Util.colorDif(chp,SAIE_Util.PixeltoColor(img.getRGB(hp,hpBar[hp]))))>chpDev){hp--;break;}
                }
            }else{
                for(hp--;hp>-1;hp--){
                    if(Math.abs(SAIE_Util.colorDif(chp,SAIE_Util.PixeltoColor(img.getRGB(hp,hpBar[hp]))))<=chpDev){break;}
                }
            }
        }
        if(hp==-1){isDead=true;}
    }
    
    public String getName(){return name;}
    public Rectangle getHpBox(){return xyhp;}
    public Color getHpColor(){return chp;}
    public float getHp(){return (float)hp/xyhp.width;}
    public boolean isDead(){return isDead;}
}
