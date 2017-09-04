/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supportai_evolution;

import java.awt.Color;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *  SAI-E's interface with the system.
 * @author Robert
 */
public class SAIE_Util {
    static class InvalidValueException extends Exception{
        //Will update with more data as neccissary.
    }
    
    static Robot r;
    
    //Will expand to a greater list at need (may want to load a file from elsewhere?)
    static public enum KEYSTRINGS{  //Now that I have the switch and enum,
        A(KeyEvent.VK_A),           //  need to run a test to see which is faster...
        B(KeyEvent.VK_B),
        C(KeyEvent.VK_C),
        D(KeyEvent.VK_D),
        E(KeyEvent.VK_E),
        F(KeyEvent.VK_F),
        G(KeyEvent.VK_G),
        H(KeyEvent.VK_H),
        I(KeyEvent.VK_I),
        J(KeyEvent.VK_J),
        K(KeyEvent.VK_K),
        L(KeyEvent.VK_L),
        M(KeyEvent.VK_M),
        N(KeyEvent.VK_N),
        O(KeyEvent.VK_O),
        P(KeyEvent.VK_P),
        Q(KeyEvent.VK_Q),
        R(KeyEvent.VK_R),
        S(KeyEvent.VK_S),
        T(KeyEvent.VK_T),
        U(KeyEvent.VK_U),
        V(KeyEvent.VK_V),
        W(KeyEvent.VK_W),
        X(KeyEvent.VK_X),
        Y(KeyEvent.VK_Y),
        Z(KeyEvent.VK_Z),
        
        ZERO(KeyEvent.VK_0),
        ONE(KeyEvent.VK_1),
        TWO(KeyEvent.VK_2),
        THREE(KeyEvent.VK_3),
        FOUR(KeyEvent.VK_4),
        FIVE(KeyEvent.VK_5),
        SIX(KeyEvent.VK_6),
        SEVEN(KeyEvent.VK_7),
        EIGHT(KeyEvent.VK_8),
        NINE(KeyEvent.VK_9),
        
        F1(KeyEvent.VK_F1),
        F2(KeyEvent.VK_F2),
        F3(KeyEvent.VK_F3),
        F4(KeyEvent.VK_F4),
        F5(KeyEvent.VK_F5),
        
        SHIFT(KeyEvent.VK_SHIFT),
        SLASH(KeyEvent.VK_SLASH),
        SPACE(KeyEvent.VK_SPACE),
        TAB(KeyEvent.VK_TAB);
        
        private final int value;
        
        private KEYSTRINGS(int v){value=v;}
        public int code(){return value;}
    }
    
    /** Presses and releases a given integer keyboard key. */
    static void typeKey(int key){
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
        public final String name;
        public final Color color;
        public final int number;
        
        public ColorUtilReturn(String s,Color c,int n){
            name=s;
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
    /** Used to determine what 'family' of colors the color is in, may upgrade so one can choose the pallete. 
     * @param c Color to determine the color family of.
     * @return A ColorUtilReturn composed of the color family and shades away from it the given color is.
     */
    static ColorUtilReturn ClosestColor(Color c){
        String strout="";
        int t=765;  //Max difference that can occur between two colors.
        Color cout=null;
        
        //Temp version, need to think of a better way...
        if(simpleColorDif(Color.WHITE,c)<t){strout="WHITE";cout=Color.WHITE; t=simpleColorDif(Color.WHITE,c);}
        if(simpleColorDif(Color.RED,c)<t){strout="RED";cout=Color.RED; t=simpleColorDif(Color.RED,c);}
        if(simpleColorDif(Color.YELLOW,c)<t){strout="YELLOW";cout=Color.YELLOW; t=simpleColorDif(Color.YELLOW,c);}
        if(simpleColorDif(Color.GREEN,c)<t){strout="GREEN";cout=Color.GREEN; t=simpleColorDif(Color.GREEN,c);}
        if(simpleColorDif(Color.MAGENTA,c)<t){strout="MAGENTA";cout=Color.MAGENTA; t=simpleColorDif(Color.MAGENTA,c);}
        if(simpleColorDif(Color.BLUE,c)<t){strout="BLUE";cout=Color.BLUE; t=simpleColorDif(Color.BLUE,c);}
        if(simpleColorDif(Color.CYAN,c)<t){strout="CYAN";cout=Color.CYAN; t=simpleColorDif(Color.CYAN,c);}
        if(simpleColorDif(Color.BLACK,c)<t){strout="BLACK";cout=Color.BLACK; t=simpleColorDif(Color.BLACK,c);}
        
        return new ColorUtilReturn(strout,cout,t);
    }
    /**
     * Returns the difference between two colors, using the positive/negative to denote brighter or darker difference.
     * @param a Origin Color to compare to
     * @param b Comparing Color
     * @return An array denoting first: if the second color brighter,darker, or the same brightness as the first,
     *      and second: the amount of shades the second color is from the first.
     */
    static int[] colorDif(Color a,Color b){
        byte positive;
        int nout;
        
        nout=Math.abs(a.getRed()-b.getRed())+Math.abs(a.getGreen()-b.getGreen())+Math.abs(a.getBlue()-b.getBlue());
        if(((a.getRed()-b.getRed())+(a.getGreen()-b.getGreen())+(a.getBlue()-b.getBlue()))>0){
            positive=1;
        }else if(((a.getRed()-b.getRed())+(a.getGreen()-b.getGreen())+(a.getBlue()-b.getBlue()))<0){
            positive=-1;
        }else{
            positive=0;
        }
        
        return new int[]{positive,nout};
    }
    static int simpleColorDif(Color a,Color b){return Math.abs(a.getRed()-b.getRed())+Math.abs(a.getGreen()-b.getGreen())+Math.abs(a.getBlue()-b.getBlue());}
    static boolean cWithinDev(Color a,int b,int dev){return cWithinDev(a,PixeltoColor(b),dev);}
    static boolean cWithinDev(Color a[],int b,int dev[]){return cWithinDev(a,PixeltoColor(b),dev);}
    static boolean cWithinDev(Color a,Color b,int dev){return Math.abs(simpleColorDif(a,b))<=dev;}
    static boolean cWithinDev(Color a[],Color b,int dev[]){
        for(int i=0;i<a.length;i++){
            if(Math.abs(simpleColorDif(a[i],b))<=dev[i]){return true;}
        }
        return false;
    }
    static Color[] IntArrayToColorArray(ArrayList<int[]> in){
        Color colout[]=new Color[in.size()];
        for(int i=0;i<in.size();i++){colout[i]=new Color(in.get(i)[0],in.get(i)[1],in.get(i)[2]);}
        
        return colout;
    }
    
    static class File{
        String path;
        java.io.File file;
        FileInputStream fin;
        FileOutputStream fout;
        boolean write=false;
        
        public File(String path) throws IOException{
            this.path=path;
            file=new java.io.File(path);
            if(!file.exists()){file.createNewFile();}
        }
        
        public void startRead(){write=false;}
        public char read(){
            if(file.canRead()){
                if(write){
                    if(fout!=null){try{fout.close();}catch(IOException e){}} //If it's not there to close, it's not there to worry about.
                    try{fin=new FileInputStream(file);}catch(FileNotFoundException e){} //Already checked for and handled.
                    write=false;
                }
                try{return (char)fin.read();}catch(IOException e){System.err.println("Unable to read character.");}
            }else{System.err.println("Unable to read from file.");}
            return '-';
        }
        public String readTo(char c){
            String strout="";
            char t;
            
            if(file.canRead()){
                if(write){
                    if(fout!=null){try{fout.close();}catch(IOException e){}} //If it's not there to close, it's not there to worry about.
                    try{fin=new FileInputStream(file);}catch(FileNotFoundException e){} //Already checked for and handled.
                    write=false;
                }
                try{
                    t=(char)fin.read();
                    if(t!=c){strout+=t;}
                    else{return strout;}
                }catch(IOException e){System.err.println("Unable to read character after \""+strout+'"');}
            }else{System.err.println("Unable to read from file.");}
            return "";
        }
        
        public void startWrite(){write=true;}
        public boolean write(char c){
            if(file.canWrite()){
                if(!write){
                    if(fin!=null){try{fin.close();}catch(IOException e){}} //If it's not there to close, it's not there to worry about.
                    try{fout=new FileOutputStream(file);}catch(FileNotFoundException e){} //Already checked for and handled.
                    write=true;
                }
                try{fout.write(c);return true;}catch(IOException e){System.err.println("Unable to write character.");}
            }else{System.err.println("Unable to write to file.");}
            return false;
        }
        public boolean write(String s){
            boolean noError=true;
            
            for(int i=0;i<s.length();i++){if(!write(s.charAt(i))){noError=false;break;}}
            return noError;
        }
        
        public boolean isWriting(){return write;}
        public void close(){
            try{fin.close();}catch(IOException e){}
            try{fout.close();}catch(IOException e){}
        }
    }
}