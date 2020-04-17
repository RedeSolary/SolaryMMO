package br.com.solary.mmo.listeners;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;

import br.com.solary.core.nms.ActionBar;
import br.com.solary.core.utils.UtilsString;
import br.com.solary.mmo.SolaryMMO;

public class GainXPListener implements Listener{
	
	DecimalFormat df = new DecimalFormat("0.0");
	
	@EventHandler
	private void onGainXp(McMMOPlayerXpGainEvent event){
		Player player = event.getPlayer();
		boolean hasBoost = false;
		if(SolaryMMO.playerBoosterManager.hasBoosterActived(player.getName(), event.getSkill())){
			hasBoost = true;
			float newXpGained = event.getRawXpGained() * 2;
			event.setRawXpGained(newXpGained);
		}
		
		String skillName = event.getSkill().getName();
		float actualXp = ExperienceAPI.getXP(player, skillName) + event.getRawXpGained();
		float maxXp = ExperienceAPI.getXPToNextLevel(player, skillName);
		if(actualXp > maxXp) actualXp = maxXp;
		String progressBar = UtilsString.progressBar(ChatColor.GREEN, ChatColor.DARK_GRAY, '▪', actualXp, maxXp, 30);
		//String percent = df.format(actualXp * 100 / maxXp);
		
		String gainedXp = hasBoost ? "§a+" + df.format(event.getRawXpGained()) + "XP (x2)" : "§a+" + df.format(event.getRawXpGained()) + "XP";
		new ActionBar("§a" + skillName + " " + event.getSkillLevel() + " " + progressBar + "§7 " + (event.getSkillLevel() + 1) + " §a" + gainedXp).sendToPlayer(player);;
		//new ActionBar("§a" + skillName + " " + event.getSkillLevel() + " " + progressBar + "§7 " + (event.getSkillLevel() + 1) + " §a" + gainedXp + " §f(" + percent + "%)").sendToPlayer(player);;
	}
	
}
