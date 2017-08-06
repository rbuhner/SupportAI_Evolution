/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supportai_evolution;

/**
 *  This is an evolving compilation of research and work to create a (hopefully) self-sufficient MMO Support AI, SAI-E
 *  (Stage 1) Theoretically finished 08/06/2017
 * @author Robert
 */

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.IOException;
import javax.imageio.ImageIO;

//For the global keyboard listening. All this to capture a 'Esc' press to quit...
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import java.util.logging.Logger;
import java.util.logging.Level;

public class SupportAI_Evolution {
    private static SupportAI_Evolution my;  //Need to make this non-static somehow, so that each run can target a new simulation with a new AI...
    private static GlobalKeyListener gkl;
    
    protected boolean q;
    private byte errorLevel;
    
    private SAIE_Skill skillBar[];
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        my=new SupportAI_Evolution();
        
        my.q=true;   //Turn to false when ready to run.
        my.errorLevel=0;
        
        //Sleeping for 5 sec to switch over to simulation.
        try{Thread.sleep(5000);}
        catch(InterruptedException e){
            System.out.println("Something interrupted thread sleep.");
            my.errorLevel++;
        }
        
        my.init();
        if(my.q==true){
            System.out.println("Need to terminate from init(). Exiting...");
            my.cleanExit();
        }
        
        //Attempting to time AI process loop to once every second first.
        int temp=10;
        do{
            try{Thread.sleep(500);} //
            catch(InterruptedException e){
                System.out.println("Something interrupted thread sleep.");
                my.errorLevel++;
            }
            
            my.SAIECore();
            
            //Quit and error checking
            if(my.q==true){System.out.println("Instructed to terminate. Exiting...");}
            else if(my.errorLevel>3){my.q=true;System.out.println("Too many errors. Exiting...");}
            else if(temp==0){System.out.println("I've run out of testing time. Exiting...");}
        }while(!my.q&&temp-->0);
        
        //Cleanup at the end of the program.
        my.cleanExit();
    }
    
    private void init(){
        //Defines AI's 'robot' (human image computer interface)
        System.out.println("Creating Robot...");
        try{SAIE_Util.r=new Robot();}
        catch(AWTException e){
            System.out.println("No low-level input control. Exiting...");
            System.exit(1);
        }catch(SecurityException e){
            System.out.println("Robot permission not granted. Exiting...");
            System.exit(1);
        }
        System.out.println("Robot created successfully.");
        
        //Defines AI's 'globalnativehook' (system-side ear, for keypress events)
        System.out.println("Registering native hook...");
        try{
            GlobalScreen.registerNativeHook();
        }
        catch(NativeHookException ex){
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            
            System.exit(1);
        }
        gkl=new GlobalKeyListener();
        GlobalScreen.addNativeKeyListener(gkl);
        gkl.initMe(my);
        //Getting the logger for "org.jnativehook" and setting the output to off,
        //  so that I can do it myself.
        Logger logger=Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
        System.out.println("Native hook registered. Now listening for 'Esc'.");
        
        /* Profile Loading */
        skillBar = new SAIE_Skill[]{
            new SAIE_Skill("Generic Heal",SAIE_Skill.skillType.heal,'5')
        };
    }
    
    /** Where SAI-E processes choices and skill usage. */
    private void SAIECore(){    //Probably will be broken up as reasoning logic gets over-large.
        /* (Stage 1) Spams a single known heal. */
        
        System.out.println("Attempting "+skillBar[0].getName()+" skill use on target.");
        skillBar[0].use();
    }
    
    private void cleanExit(){
        //Cleaning up keyboard hook.
        System.out.println("Clearing native key hook post-program.");
        SupportAI_Evolution.gkl.deleteSelf();
        System.out.println("Native key hook has been cleared.");
        
        System.out.println("\nProgram has ended.\n");
        
        System.exit(0);
    }
}

/**
 *  SAI-E's interface with the jnativehook system.
 *  Thanks to Alex Barker for creating the kwhat/jnativehook system.
 */
class GlobalKeyListener implements NativeKeyListener{
    private SupportAI_Evolution me;
    
    public void initMe(SupportAI_Evolution me){this.me=me;}
    public void deleteSelf(){unregisterSelf();GlobalScreen.removeNativeKeyListener(this);}
    public boolean GSIsRegistered(){return GlobalScreen.isNativeHookRegistered();}
    public void unregisterSelf(){
        try{GlobalScreen.unregisterNativeHook();}
        catch(NativeHookException ex){
            System.out.println("Error occured while unregistering the native hook.");
            System.out.println(ex.getMessage());

            System.exit(1);
        }
        if(GSIsRegistered()){
            System.out.println("Something went wrong with unregistering the native hook. A native hook is still registered.");
            System.out.println("Attempting to unregister again...");
            try{GlobalScreen.unregisterNativeHook();}
            catch(NativeHookException ex){
                System.out.println("Error occured while unregistering the native hook.");
                System.out.println(ex.getMessage());

                System.exit(1);
            }
            if(GSIsRegistered()){
                System.err.println("Native Hook is failing to respond to unregistration. Force exiting...");

                System.exit(1);
            }
        }
    }
    
    @Override
    public void nativeKeyPressed(NativeKeyEvent e){
        System.out.println("Key Pressed: "+NativeKeyEvent.getKeyText(e.getKeyCode()));
        
        if (e.getKeyCode()==NativeKeyEvent.VC_ESCAPE||e.getKeyCode()==NativeKeyEvent.VC_Q){
            me.q=true;
            unregisterSelf();
        }
    }
    @Override
    public void nativeKeyReleased(NativeKeyEvent e){System.out.println("Key Released: "+NativeKeyEvent.getKeyText(e.getKeyCode()));}
    @Override
    public void nativeKeyTyped(NativeKeyEvent e){System.out.println("Key Typed: "+NativeKeyEvent.getKeyText(e.getKeyCode()));}
}
