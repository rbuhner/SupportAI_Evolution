/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supportai_evolution;

/**
 *  An encapsulation of any actions SAI-E is able to take, like healing, refocusing, or moving.
 * @author Robert
 */
public class SAIE_Skill {
    public enum skillType{shortcut,heal,heal_self,heal_other}
    
    private final String name;
    private final skillType stype;
    private final char key;
    
    private final int cooldown;
    private int cdLeft;
    
    public SAIE_Skill(String name,skillType stype,char key,int cd){
        this.name=name;
        this.stype=stype;
        this.key=key;
        cooldown=cd;
    }
    
    public String getName(){return name;}
    public skillType getSkillType(){return stype;}
    public char getKey(){return key;}
    public int getCDLeft(){return cdLeft;}
    
    /**
     * Function guarding use of the skill, will contain cooldowns, costs, etc. later.
     * @return Returns success of skill use.
    */
    public boolean use(){
        if(cdLeft<=0){
            SAIE_Util.typeKey(key);
            cdLeft=cooldown;
            return true;
        }else{return false;}
    }
    
    public void update(){
        if(cdLeft>0){cdLeft--;}
    }
}
