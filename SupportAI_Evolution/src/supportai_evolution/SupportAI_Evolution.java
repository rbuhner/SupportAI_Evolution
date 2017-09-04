/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supportai_evolution;

/**
 *  This is an evolving compilation of research and work to create a (hopefully) self-sufficient MMO Support AI, SAI-E
 *  (Stage 1) First Activation Loops: Theoretically finished 08/06/2017
 *  (Stage 2) EyesOpen~Smart Healing: Finished and tested 08/31/2017
<<<<<<< HEAD
 *  (Stage 4) Profile Saving and Loading: In-Progress...
=======
 *  (Stage 3) HealAll~Self and Party: Theoretically finished 09/02/2017
>>>>>>> master
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
import java.util.ArrayList;
import java.util.Iterator;

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
    private static String filePath="";
    
    //These will likely be temporary until profile saving/loading is implemented.
    private ArrayList<String> savedArgs[]=new ArrayList[]{new ArrayList<>(),new ArrayList<>()};
    private ArrayList<String> savedArgSelf=new ArrayList<>();
    
    protected boolean q;
    private final static boolean setup=false;
    private byte errorLevel;
    
    private SAIE_Skill skillBar[];
    private SAIE_Target currentTarget;
    private SAIE_Target self;
    private SAIE_Target party[];
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        my=new SupportAI_Evolution();
        
        my.q=false;   //Turn to false when ready to run more than one loop.
        my.errorLevel=0;
        
        //This is currently going to assume that the filepath/skill(s)/target(s) given is a valid one,
        //  until a later time when implementing more file-utils/error checking.
        for(int i=0;i<args.length;i++){
            System.out.print(args[i]+" : ");
            if(args[i].equals("-fp")&&i+1<args.length){filePath=args[i+1];}                     //FilePath
            else if(args[i].equals("-ts")&&i+1<args.length){my.savedArgSelf.add(args[i+1]);}         //Target (Self)
            else if(args[i].equals("-tp")&&i+1<args.length){my.savedArgs[0].add(args[i+1]);}    //Target (Party)
            else if(args[i].equals("-s")&&i+1<args.length){my.savedArgs[1].add(args[i+1]);}     //Skills(s)
        }
        
        //Sleeping for 5 sec to switch over to simulation.
        try{Thread.sleep(5000);}
        catch(InterruptedException e){
            System.out.println("Something interrupted thread sleep.");
            my.errorLevel++;
        }
        
        my.SAIE_Initialize();
        if(my.q==true){
            System.out.println("Need to terminate from init(). Exiting...");
            my.cleanExit(1);
        }
        
        //Attempting to time AI process loop to once every second first.
        int temp=120;
        System.out.println("I'm starting to do things! ~"+temp/2+"seconds available.");
        do{
            try{Thread.sleep(500);}
            catch(InterruptedException e){
                System.out.println("Something interrupted thread sleep.");
                my.errorLevel++;
            }
            
            my.SAIECore();
            
            //Quit and error checking
            if(my.q==true){System.out.println("Instructed to terminate. Exiting...");}
            else if(my.errorLevel>3){my.q=true;System.out.println("Too many errors. Exiting...");}
            else if(my.self.isDead()){my.q=true;System.out.println("I have died. Exiting...");}
            else if(my.party[0].isDead()){my.q=true;System.out.println("Target "+my.party[0].getName()+" has died. Exiting...");}
            else if(temp==0){System.out.println("I've run out of testing time. Exiting...");}
        }while(!my.q&&temp-->0);
        
        //Cleanup at the end of the program.
        my.cleanExit(0);
    }
    
    private void SAIE_Initialize(){
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
        //Looks like I'm doing basic profile loading a bit early this time.
        my.SAIE_OpenEyes();
        
        //Only taking the first target for now, but will upgrade to full party with profile implementation.
        if(!savedArgs[0].isEmpty()){
            party=new SAIE_Target[1];
            party[0]=initTarget(party[0],savedArgs[0]);
        }else{
            System.err.println("Target not provided. Please give a target on next run. Exiting...");
            cleanExit(1);
        }
        
        if(!savedArgs[1].isEmpty()){    //Just dealing with one skill at the moment, until profile loading is done. Would hate to input so many cmd'd...
            //String name,skillType sType,char key
            String temp="",name="",type="";
            char key='-';
            int cd=0;
            byte step=0;
            
            //name:type:key:cd:     ~any~:~any~:stringkey:#...#:
            for(int i=0;i<savedArgs[1].get(0).length();i++){
                if(savedArgs[1].get(0).charAt(i)==':'){
                    switch(step){
                        case 0: name=temp; break;
                        case 1: type=temp; break;
                        case 2: key=temp.charAt(0); break;
                        case 3: cd=Integer.getInteger(temp);
                    }
                    step++;
                    temp="";
                }else{temp+=savedArgs[1].get(0).charAt(i);}
            }
            
            skillBar = new SAIE_Skill[]{
                new SAIE_Skill(name,SAIE_Skill.skillType.valueOf(type),key,cd)
            };
        }else{
            System.err.println("Skills not provided. Please give valid skills on next run. Exiting...");
            cleanExit(1);
        }
    }
    private SAIE_Target initTarget(SAIE_Target target,ArrayList<String> arg){
        //String name,Rectangle xyhp,Color chp,int chpIDev
        String temp="",name="",key="";
        int xyhp[]=new int[4];
        ArrayList<int[]> chp=new ArrayList<>();
        ArrayList<Integer> chpDev=new ArrayList<>();
        byte step=0;
        byte n=-1,nc=-1;

        //name:xyhpx:xyhpy:xyhpw:xyhph:chpn:chpr:chpg:chpb:||:chpDev:||:key:
        //~any~:####:####:####:####:#:###:###:###:||:###:||:~any~:
        for(int i=0;i<arg.get(0).length();i++){
            if(arg.get(0).charAt(i)==':'){
                switch(step){
                    case 0:     name=temp; break;

                    case 1:     //Hp Rectangle area
                    case 2:
                    case 3:
                    case 4:     xyhp[step-1]=Integer.parseInt(temp); break;

                    case 5:     n=Byte.parseByte(temp); nc=n; break;

                    case 6:     chp.add(new int[3]);
                    case 7:     chp.get(n-nc)[step-6]=Integer.parseInt(temp); break;

                    case 8:     chp.get(n-nc)[2]=Integer.parseInt(temp);
                                if(nc>1){nc--; step=5;}
                                else{nc=n;}
                                break;

                    case 9:     chpDev.add(Integer.parseInt(temp));
                                if(nc>1){nc--; step=8;}
                                break;

                    case 10:    key=temp;
                }
                step++;
                temp="";
            }else{temp+=arg.get(0).charAt(i);}
        }

        int cDev[]=new int[chpDev.size()];
        Iterator<Integer> it=chpDev.iterator();
        for(int i=0;i<cDev.length;i++){cDev[i]=it.next();}

        try{target=new SAIE_Target(name,new Rectangle(xyhp[0],xyhp[1],xyhp[2],xyhp[3]),
                SAIE_Util.IntArrayToColorArray(chp),cDev,key,true);}
        catch(SAIE_Util.InvalidValueException e){cleanExit(1);}
        return target;
    }
    
    private void SAIE_OpenEyes(){   //Using OpenEyes as self-oriented visual initialization.
        //--Run once for manual values
        if(setup){
            my.q=true;
            try{
                System.out.println("Capturing Screen...");
                BufferedImage scap = SAIE_Util.r.createScreenCapture(new Rectangle(
                        Toolkit.getDefaultToolkit().getScreenSize()));
                File sfile = new File(filePath+"screencapInit.jpg");
                ImageIO.write(scap,"jpg",sfile);
                System.out.println("Screen captured and saved at "+filePath+"screencapInit.jpg");
                cleanExit(0);
            }catch(IOException e){
                System.out.println("Unable image to write to file. Exiting...");
                cleanExit(1);
            }
        }
        
        if(savedArgSelf.equals("")){
            System.err.println("No info on self. Please give on self on next run. Exiting...");
            cleanExit(1);
        }else{
            self=initTarget(self,savedArgSelf);
        }
    }
    
    private void loadProfile(){
        //Assuming conversion of above into file format, sans -fp of course.
        //-tag:data:data2;
        //Ex: -t:name:xyhpx:xyhpy:xyhpw:xyhph:chpn:chpr:chpg:chpb:||:chpDev:||;
        String read="";
        String GameName;
        class saveTarget{String name="";Rectangle xyr=new Rectangle();Color cBar[]=new Color[2];float cDev[]=new float[2];}
        saveTarget sTarget=new saveTarget();
        class SaveSkill{String name;int cd;char key;}
        SaveSkill sskill=new SaveSkill();
        
        
    }
    
    /** Where SAI-E processes choices and skill usage. */
    private void SAIECore(){    //Probably will be broken up as reasoning logic gets over-large.
        /* (Stage 2) Starts to look at the target's hp before using the skill. */
        /* (Stage 3) Starts looking at self and party hp before using skill. */
        
        float hptarget = 0.75F;
        
        self.update();
        System.out.println("My hp is currently "+self.getHp()+"%.");
        party[0].update();
        System.out.println("Target "+party[0].getName()+"'s hp is currently "+party[0].getHp()+"%.");
        //Need an update all function or something later...
        for(SAIE_Skill s:skillBar){s.update();}
        
        //Self preservation instinct, check and heal self before healing another.
        if(self.getHp()<hptarget){
            if(currentTarget!=self){
                System.out.println("Selecting self.");
                selectTarget(self);
            }
            if(skillBar[0].getCDLeft()<=0){
                System.out.println("Attempting "+skillBar[0].getName()+" skill use on self.");
                skillBar[0].use();
            }
        }else if(party[0].getHp()<hptarget){
            if(currentTarget!=party[0]){
                System.out.println("Selecting party[0].");
                selectTarget(party[0]);
            }
            if(skillBar[0].getCDLeft()<=0){
                System.out.println("Attempting "+skillBar[0].getName()+" skill use on "+party[0].getName()+".");
                skillBar[0].use();
            }
        }
    }
    private void selectTarget(SAIE_Target target){
        //Currently assumes a single key at this time, will upgrade to multikey (like shift-key) later.
        SAIE_Util.typeKey(SAIE_Util.KEYSTRINGS.valueOf(target.getKey()).code());
        currentTarget=target;
    }
    
    private void cleanExit(int exitcode){
        //Cleaning up keyboard hook.
        System.out.println("Clearing native key hook post-program.");
        SupportAI_Evolution.gkl.deleteSelf();
        System.out.println("Native key hook has been cleared.");
        
        System.out.println("\nProgram has ended.\n");
        
        System.exit(exitcode);
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
        
        if (e.getKeyCode()==NativeKeyEvent.VC_ESCAPE){
            me.q=true;
            unregisterSelf();
        }
    }
    @Override
    public void nativeKeyReleased(NativeKeyEvent e){System.out.println("Key Released: "+NativeKeyEvent.getKeyText(e.getKeyCode()));}
    @Override
    public void nativeKeyTyped(NativeKeyEvent e){System.out.println("Key Typed: "+NativeKeyEvent.getKeyText(e.getKeyCode()));}
}
