package br.com.solary.mmo.managers;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.gmail.nossr50.datatypes.skills.SkillType;

import br.com.solary.mmo.SolaryMMO;
import br.com.solary.mmo.objects.PlayerBooster;

public class PlayerBoosterManager {

	public HashMap<String, PlayerBooster> boosters;
	
	public PlayerBoosterManager(SolaryMMO plugin) {
		this.boosters = new HashMap<>();
	}
	
	public void setBooster(String nick, SkillType skillType){
		PlayerBooster playerBooster = new PlayerBooster(nick, skillType);
		this.boosters.put(nick, playerBooster);
		SolaryMMO.sql.setBooster(nick, skillType.getName(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(60));
	}
	
	public void removeBooster(String nick){
		this.boosters.remove(nick);
		SolaryMMO.sql.removeBooster(nick);
	}
	
	public boolean hasBoosterActived(String nick){
		return this.boosters.containsKey(nick);
	}
	
	public boolean hasBoosterActived(String nick, SkillType skillType){
		if(hasBoosterActived(nick) && this.boosters.get(nick).getSkillType().equals(skillType)){
			return true;
		}
		return false;
	}
	
	public PlayerBooster getPlayerBooster(String nick){
		return this.boosters.get(nick);
	}
	
}
