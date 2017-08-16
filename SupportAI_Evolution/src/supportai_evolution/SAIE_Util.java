/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supportai_evolution;

import java.awt.Color;
import java.awt.Robot;
import java.awt.event.KeyEvent;

/**
 *  SAI-E's interface with the system.
 * @author Robert
 */
public class SAIE_Util {
    static Robot r;
    
    /** Presses and releases a given integer keyboard key. */
    static void typeKey(int key){
        System.out.println(key+" is being typed.");
        r.keyPress(key);
        r.keyRelease(key);
    }
    /** Presses and releases a given character keyboard key. */
    static void typeKey(char key){typeKey(charToKey(key));}
    /** Returns the integer version of a given character keyboard key. */
    static int charToKey(char key){
        //Will expand to a greater list if needed (may want to load a file from elsewhere?)
        switch(key){    //Need to see if converting to enum would make retrieval quicker...
            case 'a':return KeyEvent.VK_A;
            case 'b':return KeyEvent.VK_B;
            case 'c':return KeyEvent.VK_C;
            case 'd':return KeyEvent.VK_D;
            case 'e':return KeyEvent.VK_E;
            case 'f':return KeyEvent.VK_F;
            case 'g':return KeyEvent.VK_G;
            case 'h':return KeyEvent.VK_H;
            case 'i':return KeyEvent.VK_I;
            case 'j':return KeyEvent.VK_J;
            case 'k':return KeyEvent.VK_K;
            case 'l':return KeyEvent.VK_L;
            case 'm':return KeyEvent.VK_M;
            case 'n':return KeyEvent.VK_N;
            case 'o':return KeyEvent.VK_O;
            case 'p':return KeyEvent.VK_P;
            case 'q':return KeyEvent.VK_Q;
            case 'r':return KeyEvent.VK_R;
            case 's':return KeyEvent.VK_S;
            case 't':return KeyEvent.VK_T;
            case 'u':return KeyEvent.VK_U;
            case 'v':return KeyEvent.VK_V;
            case 'w':return KeyEvent.VK_W;
            case 'x':return KeyEvent.VK_X;
            case 'y':return KeyEvent.VK_Y;
            case 'z':return KeyEvent.VK_Z;
            
            case '0':return KeyEvent.VK_0;
            case '1':return KeyEvent.VK_1;
            case '2':return KeyEvent.VK_2;
            case '3':return KeyEvent.VK_3;
            case '4':return KeyEvent.VK_4;
            case '5':return KeyEvent.VK_5;
            case '6':return KeyEvent.VK_6;
            case '7':return KeyEvent.VK_7;
            case '8':return KeyEvent.VK_8;
            case '9':return KeyEvent.VK_9;
                
            case '/':return KeyEvent.VK_SLASH;
            case ' ':return KeyEvent.VK_SPACE;
        }
        return KeyEvent.CHAR_UNDEFINED; //If the char in doesn't correspond.
    }
    
    /**
     * What would essentially be a multi-typed array to return from some Util color functions.
     * Currently contains a Color and an int (int usually being the difference of some Color from another.)
     */
    static class ColorUtilReturn{
        public final Color color;
        public final int number;
        
        public ColorUtilReturn(Color c,int n){
            color=c;
            number=n;
        }
    }
    
    /** To interchange pixel int-colors to a more usable Color format. */
    static Color PixeltoColor(int pixel){
        int alpha=(pixel>>24)&0xff;
        int red=(pixel>>16)&0xff;
        int green=(pixel>>8)&0xff;
        int blue=(pixel)&0xff;
        
        return new Color(red,green,blue,alpha);
    }
    /** Used to determine what 'family' of colors the color is in, may upgrade so one can choose the pallete. */
    static ColorUtilReturn ClosestColor(Color c){
        int t=765;  //Max difference that can occur between two colors.
        Color cout=null;
        
        //Temp version, need to think of a better way...
        if(Math.abs(colorDif(Color.WHITE,c))<t){cout=Color.WHITE; t=Math.abs(colorDif(Color.WHITE,c));}
        if(Math.abs(colorDif(Color.RED,c))<t){cout=Color.RED; t=Math.abs(colorDif(Color.RED,c));}
        if(Math.abs(colorDif(Color.GREEN,c))<t){cout=Color.GREEN; t=Math.abs(colorDif(Color.GREEN,c));}
        if(Math.abs(colorDif(Color.BLUE,c))<t){cout=Color.BLUE; t=Math.abs(colorDif(Color.BLUE,c));}
        if(Math.abs(colorDif(Color.BLACK,c))<t){cout=Color.BLACK; t=Math.abs(colorDif(Color.BLACK,c));}
        
        return new ColorUtilReturn(cout,t);
    }
    /**
     * Returns the difference between two colors, using the positive/negative to denote brighter or darker difference.
     * @param a Origin Color to compare to
     * @param b Comparing Color
     * @return The amount that Color b is different from Color a, positive if brighter, negative if darker.
     */
    static int colorDif(Color a,Color b){return (a.getRed()-b.getRed())+(a.getGreen()-b.getGreen())+(a.getBlue()-b.getBlue());}
}
