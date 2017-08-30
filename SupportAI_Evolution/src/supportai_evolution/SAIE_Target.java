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
    private Color[] chp;        //Average color(s) of hpbar, to know if we're looking for a red, blue, purple, etc. hpbar,
                                //  and if we need to look for more than one color (like blue shielding on a red hpbar.)
    private int[] chpDev;       //Maximum deviation of hpbar checked pixels from avg color(s), to see past shifting, shimmering, and speckling.
                                //  these deviations will naturally correlate with the above chp(s) 1:1.
    
    private boolean isDead;     //To change mode from active to waiting and/or terminate program in early stages.
    private int hp;             //Current pixel count, which is used as the AI's sight on the hp value.
    
    
    public SAIE_Target(String name,Rectangle xyhp,Color[] chp,int[] chpIDev,boolean optimize) throws SAIE_Util.InvalidValueException{
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
            System.out.println("Attempting optimization of "+name+"...");
            for(int i:chpDev){if(i<=0){i=32;}}
            
            //If the invalid pixels outnumber the ones we can use, try a wider net, and if still not working, soft exit.
            if(!openEyesSub1(img)){
                System.out.println("First level optimization failed, attempting broader optimization...");
                
                for(Color c:chp){c=SAIE_Util.ClosestColor(c).color;}
                for(int i:chpDev){i=64;}
                
                if(!openEyesSub1(img)){
                    System.out.println("Error: "+name+"'s Hp has too many invalid colors, please adjust and rerun.");
                    throw new SAIE_Util.InvalidValueException();
                }
            }else{System.out.println("Optimization successful.");}
        }
        
        int ty=-1;
        for(int y=0;y<xyhp.height;y++){
            ty=y;
            for(int x=0;x<xyhp.width;x++){
                //If the pixel is within deviation of the avg color, great, otherwise nextline.
                if(!SAIE_Util.cWithinDev(chp,x,chpDev)){
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
                    if(!SAIE_Util.cWithinDev(chp,x,chpDev)){
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
        hp=hpBar.length-1;
    }
    private boolean openEyesSub1(BufferedImage img){
        long cc[][]=new long[chp.length][];                     //Color collection
        for(int i=0;i<cc.length;i++){cc[i]=new long[]{0,0,0};}
        int pc=0,ipc=0;                                         //Pixel / Invalid Pixel count
        Color max[]=chp.clone(),min[]=chp.clone(),tc;           //Brightest/Darkest color in spectrum
        boolean found;
        
        for(int y=0;y<img.getHeight();y++){
            for(int x=0;x<img.getWidth();x++){
                tc=SAIE_Util.PixeltoColor(img.getRGB(x,y));
                found=false;
                for(int i=0;i<chp.length;i++){
                    if(SAIE_Util.cWithinDev(chp[i],img.getRGB(x,y),chpDev[i])){
                        cc[i][0]+=tc.getRed();
                        cc[i][1]+=tc.getGreen();
                        cc[i][2]+=tc.getBlue();
                        pc++;
                        
                        if(SAIE_Util.colorDif(max[i],tc)>0){max[i]=tc;}
                        else if(SAIE_Util.colorDif(min[i],tc)<0){min[i]=tc;}
                        found=true; break;
                    }
                }
                if(!found){ipc++;}
            }
        }
        
        if(pc>ipc){
            for(int i=0;i<chp.length;i++){
                Color ctemp=new Color((int)(cc[i][0]/pc),(int)(cc[i][1]/pc),(int)(cc[i][2]/pc));
                System.out.println(name+"'s Hp Color "+i+" is: "+ctemp.toString()+".");
                chp[i]=ctemp;

                int tmax=Math.abs(SAIE_Util.colorDif(chp[i],max[i]));
                int tmin=Math.abs(SAIE_Util.colorDif(chp[i],min[i]));
                chpDev[i]=(tmax>tmin?tmax:tmin);
                System.out.println(name+"'s Hp Color Avg Deviation "+i+" is: "+chpDev[i]+".");
            }
            return true;
        }else{
            System.out.println("PC:"+pc+" IPC:"+ipc+" HpBar Length:"+img.getWidth());
            return false;
        }
    }
    
    /** Updating the values of the target, mainly hp/stats/etc */
    public void update(){
        BufferedImage img=SAIE_Util.r.createScreenCapture(xyhp);
        
        //If one contiguous line was found, use it, otherwise use the array of contiguous points found.
        if(hpBar.length==1){
            //To save on time and cycles, start from the known hp.
            if(SAIE_Util.cWithinDev(chp,img.getRGB(hp,hpBar[0]),chpDev)){
                for(hp++;hp<xyhp.width;hp++){
                    //If color found is not within deviation of avgColor, then we've hit the height of hp level.
                    if(hp>=hpBar.length-1){
                        hp=hpBar.length-1;
                        System.out.println("Linear found hp full at "+hp+".");
                        break;
                    }else if(!SAIE_Util.cWithinDev(chp,img.getRGB(hp,hpBar[0]),chpDev)){
                        hp--;
                        System.out.println("Linear found hp level of "+hp+".");
                        break;
                    }
                }
            }else{
                //As above, reversed.
                for(hp--;hp>-1;hp--){
                    //If color found is within deviation of avgColor, then we've hit hp level
                    if(SAIE_Util.cWithinDev(chp,img.getRGB(hp,hpBar[0]),chpDev)){break;}
                }
            }
        }else{
            if(SAIE_Util.cWithinDev(chp,img.getRGB(hp,hpBar[hp]),chpDev)){
                for(hp++;hp<xyhp.width;hp++){
                    if(hp>=hpBar.length-1){
                        hp=hpBar.length-1;
                        System.out.println("Non-linear found hp full at "+hp+".");
                        break;
                    }else if(!SAIE_Util.cWithinDev(chp,img.getRGB(hp,hpBar[hp]),chpDev)){
                        hp--;
                        System.out.println("Non-linear found hp level of "+hp+".");
                        break;
                    }
                }
            }else{
                for(hp--;hp>-1;hp--){
                    if(SAIE_Util.cWithinDev(chp,img.getRGB(hp,hpBar[hp]),chpDev)){break;}
                }
            }
        }
        if(hp==-1){isDead=true;}
    }
    
    public String getName(){return name;}
    public Rectangle getHpBox(){return xyhp;}
    public Color[] getHpColor(){return chp;}
    public float getHp(){System.out.println("Hp:"+hp+"/"+hpBar.length);
        return (float)hp/hpBar.length;}
    public boolean isDead(){return isDead;}
}
