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
 *  (Stage 3) HealAll~Self and Party: Finished and tested 09/04/2017
 *  (Stage 4) Profile Saving and Loading: Theoretically finished 09/04/2017
 *          - Just broke 1,000 lines (1,004 lines at this stage.) -
 * @author Robert
 */

/* Making notes to redo project to work better/
    be stuctured in a way to better work with the snatches of time to work on it. */

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
/* NOTE: If we're bringing in this overhead, might as well capture more than esc out-focus. */
/* NOTE2: Need to find a way to create multiple points of focus/listeners/sysinputs... */
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import java.util.logging.Logger;
import java.util.logging.Level;

public class SupportAI_Evolution {
    private static SupportAI_Evolution my;  //Need to make this non-static somehow, so that each run can target a new simulation with a new AI...
        /* NOTE: One way to make this non-static is to wrap SAIE in a static manager/scheduler,
                to interface between the static main method and each SAIE sim.*/
    private static GlobalKeyListener gkl;
    private static String filePath="",profileName="";
    private static SAIE_Util.File file;
    
    protected boolean q;    /* NOTE: Protected? So any class in the package can force a quit? */
    private final static boolean setup=false;
    private byte errorLevel;
    private ArrayList<Boolean> optimize=new ArrayList<>();
    
    private String currentGame;
    /* NOTE: In-Game data could be switched over to neural memory? */
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
            /* NOTE: this would cause quiting after init now, need to redo. */
        my.errorLevel=0;
        
        //This is currently going to assume that the filepath/skill(s)/target(s) given is a valid one,
        //  until a later time when implementing more file-utils/error checking.
        /* NOTE: With resource-try and exceptions no assumptions required. */
        for(int i=0;i<args.length;i++){
            System.out.print(args[i]+" : ");
            if(args[i].equals("-fp")&&i+1<args.length){filePath=args[i+1];}     //FilePath
            if(args[i].equals("-fn")&&i+1<args.length){profileName=args[i+1];}  //ProfileName
        }
        
        //Sleeping for 5 sec to switch over to simulation.
        /* NOTE: Why not have a keypress/etc in once simulation is ready in case loading times,
                would make it possible for a pause/restart if needing to reload sim w/o re-running AI.*/
        try{Thread.sleep(5000);}
        catch(InterruptedException e){
            System.out.println("Something interrupted thread sleep.");
            my.errorLevel++;
        }
        
        my.SAIE_Initialize();
        /* NOTE: Instead of qtest after init, just have init throw an exception, which gives better messageing anyways. */
        if(my.q==true){
            System.out.println("Need to terminate from init(). Exiting...");
            my.cleanExit(1);
        }
        
        //Attempting to time AI process loop to once every second first.
        /* NOTE: With system clocking one could actually time the loop to the clock, so as not needing hand-tuning. */
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
    
    /* NOTE: If having init(), this will need to throw exceptions. */
    private void SAIE_Initialize(){
        //Defines AI's 'robot' (human image computer interface)
        System.out.println("Creating Robot...");
        /* NOTE: Since we have a SAIE clean-up function, may want to route all system.exit()s through there. */
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
        
        try{Thread.sleep(500);}
        catch(InterruptedException e){
            System.out.println("Something interrupted thread sleep.");
            my.errorLevel++;
        }
        
        /* Profile Loading */
        if(file==null){
            try{file = new SAIE_Util.File(filePath+profileName);}
            catch(IOException e){
                System.err.println("Unable to (al)locate file to load.");
                cleanExit(1);
            }
        }
        
        //Load Profile from File
        ArrayList<String> profile = loadProfile();
        currentGame=profile.get(0);
        
        //Initializing Self_Target
        SAIE_OpenEyes(profile.get(1));
        
        //Initializing Party Targets
        if(profile.get(2).equals("")){
            System.err.println("Target not provided. Please give a target on next run. Exiting...");
            cleanExit(1);
        }else{ //Only taking the first target for now, but will upgrade to full party with profile implementation.
            party=new SAIE_Target[1];
            party[0]=initTarget(party[0],profile.get(2),optimize.get(1));
        }
        
        //Initializing SkillBar
        if(!profile.get(3).isEmpty()){    //Just dealing with one skill at the moment, until profile loading is done. Would hate to input so many cmd'd...
            //String name,skillType sType,char key
            String temp="",name="",type="";
            char key='-';
            int cd=0;
            byte step=0;
            
            //name:type:key:cd:     ~any~:~any~:stringkey:#...#:
            for(int i=0;i<profile.get(3).length();i++){
                if(profile.get(3).charAt(i)==':'){
                    switch(step){
                        case 0: name=temp; break;
                        case 1: type=temp; break;
                        case 2: key=temp.charAt(0); break;
                        case 3: cd=Integer.parseInt(temp);
                    }
                    step++;
                    temp="";
                }else{temp+=profile.get(3).charAt(i);}
            }
            
            skillBar = new SAIE_Skill[]{
                new SAIE_Skill(name,SAIE_Skill.skillType.valueOf(type),key,cd)
            };
        }else{
            System.err.println("Skills not provided. Please give valid skills on next run. Exiting...");
            cleanExit(1);
        }
    }
    
    private ArrayList<String> loadProfile(){
        //Assuming conversion of above into file format, sans -fp of course.
        //-tag:data:data2;
        //Ex: -t:name:xyhpx:xyhpy:xyhpw:xyhph:chpn:chpr:chpg:chpb:||:chpDev:||;
        //String read="",read2="";   //Used for diagnostic purposes.
        ArrayList<String> args = new ArrayList<>();
        boolean eof=false;  //End of File
        
        //Look for tags -gn = GameName, -ts = Target Self, -tp = Target Party, -s = Skill
        do{
            //Need to look into a more efficient way...
            switch(file.readTo(':')){
                case "-gn": System.out.println("Case -gn");
                            args.add(file.readTo(';'));
                            break;
                case "-ts": System.out.println("Case -ts");
                            optimize.add(file.readTo(':').equals('1'));
                            args.add(file.readTo(';'));
                            break;
                case "-tp": System.out.println("Case -tp");
                            optimize.add(file.readTo(':').equals('1'));
                            args.add(file.readTo(';'));
                            break;
                case "-s":  System.out.println("Case -s");
                            args.add(file.readTo(';'));
                            break;
                case ";":   System.out.println("Case ;");
                            eof=true;
                            break;
                default:    System.err.println("Profile loading error.");
            }
        }while(!eof);
        System.out.println("Profile loaded.");
        
        return args;
    }
    
    private void SAIE_OpenEyes(String arg){   //Using OpenEyes as self-oriented visual initialization.
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
        
        if(arg.equals("")){
            System.err.println("No info on self. Please give on self on next run. Exiting...");
            cleanExit(1);
        }else{
            self=initTarget(self,arg,optimize.get(0));
        }
    }
    
    private SAIE_Target initTarget(SAIE_Target target,String arg,boolean opt){
        //String name,Rectangle xyhp,Color chp,int chpIDev
        String temp="",name="",key="";
        int xyhp[]=new int[4];
        ArrayList<int[]> chp=new ArrayList<>();
        ArrayList<Integer> chpDev=new ArrayList<>();
        byte step=0;
        byte n=-1,nc=-1;

        //name:xyhpx:xyhpy:xyhpw:xyhph:chpn:chpr:chpg:chpb:||:chpDev:||:key:
        //~any~:####:####:####:####:#:###:###:###:||:###:||:~any~:
        for(int i=0;i<arg.length();i++){
            if(arg.charAt(i)==':'){
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
            }else{temp+=arg.charAt(i);}
        }

        int cDev[]=new int[chpDev.size()];
        Iterator<Integer> it=chpDev.iterator();
        for(int i=0;i<cDev.length;i++){cDev[i]=it.next();}
        
        try{target=new SAIE_Target(name,new Rectangle(xyhp[0],xyhp[1],xyhp[2],xyhp[3]),
                SAIE_Util.IntArrayToColorArray(chp),cDev,key,opt);}
        catch(SAIE_Util.InvalidValueException e){cleanExit(1);}
        return target;
    }
    
    /** Where SAI-E processes choices and skill usage. */
    private void SAIECore(){    //Probably will be broken up as reasoning logic gets over-large.
        /* (Stage 2) Starts to look at the target's hp before using the skill. */
        /* (Stage 3) Starts looking at self and party hp before using skill. */
        /* (Stage 4) No real change here. */
        
        float hptarget = 0.8F;
        
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
        System.out.println('\''+target.getKey()+'\'');
        SAIE_Util.typeKey(SAIE_Util.KEYSTRINGS.valueOf(target.getKey()).code());
        currentTarget=target;
    }
    
    private void saveProfile(){
        if(file!=null){
            
                file.write("-gn:"+currentGame+";\n");
                
                //Saving Self
                file.write("-ts:0:"+self.getName()+':'+
                        self.getHpBox().x+':'+self.getHpBox().y+':'+self.getHpBox().width+':'+self.getHpBox().height+':'+
                        self.getHpColor().length+':');
                    for(Color c:self.getHpColor()){file.write(""+c.getRed()+':'+c.getGreen()+':'+c.getBlue()+':');}
                    for(int i:self.getHpDev()){file.write(""+i+':');}
                    file.write(self.getKey()+":;\n");
                
                //Saving each member of the Party
                for(SAIE_Target t:party){
                    file.write("-tp:0:"+t.getName()+':'+
                            t.getHpBox().x+':'+t.getHpBox().y+':'+t.getHpBox().width+':'+t.getHpBox().height+':'+
                            t.getHpColor().length+':');
                        for(Color c:t.getHpColor()){file.write(""+c.getRed()+':'+c.getGreen()+':'+c.getBlue()+':');}
                        for(int i:t.getHpDev()){file.write(""+i+':');}
                        file.write(t.getKey()+":;\n");
                }
                
                //Saving each Skill
                for(SAIE_Skill s:skillBar){
                    file.write("-s:"+s.getName()+':'+s.getSkillType().toString()+':'+s.getKey()+':'+s.getCD()+":;\n");
                }
                
                //End-of-File
                file.write(";:");
            /*}catch(IOException e){
                //Something went wrong writing profile to file.
                System.err.println("Unable to save profile due to error in file-out stream.");
                System.err.println("Please check the profile at "+file.path+" for possible errors.");
                file.close();
            }*/
        }
    }
    
    private void cleanExit(int exitcode){
        //Cleaning up keyboard hook.
        System.out.println("Clearing native key hook post-program.");
        SupportAI_Evolution.gkl.deleteSelf();
        System.out.println("Native key hook has been cleared.");
        
        if(file!=null){
            System.out.println("Saving profile to "+file.path+" .");
            saveProfile();
            System.out.println("Finished saving profile.");
        }
        
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