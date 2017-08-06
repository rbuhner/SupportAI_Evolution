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
    
    public SAIE_Skill(String name,skillType stype,char key){
        this.name=name;
        this.stype=stype;
        this.key=key;
    }
    
    public String getName(){return name;}
    public skillType getSkillType(){return stype;}
    public char getKey(){return key;}
    
    /**
     * Function guarding use of the skill, will contain cooldowns, costs, etc. later.
     * @return Returns success of skill use.
    */
    public boolean use(){
        SAIE_Util.typeKey(key);
        return true;
    }
}
