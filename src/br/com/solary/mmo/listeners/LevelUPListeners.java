package br.com.solary.mmo.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;

public class LevelUPListeners implements Listener{

	@EventHandler
	private void onLevelUP(McMMOPlayerLevelUpEvent e){
		 if (!(e.getPlayer() instanceof Player)) return;
		 if (Bukkit.getServer().getPlayer(e.getPlayer().getName()) == null) return;
		 
		 Player p = e.getPlayer();
		 
		 String abilityName = e.getSkill().getName();
		 int currentSkillLevel = e.getSkillLevel();
		 
		 if(currentSkillLevel % 50 == 0){
			Bukkit.broadcastMessage("§6§lHABILIDADES: §e" + p.getName() + " atingiu o §6nível " + currentSkillLevel + " §ena habilidade de §6" + abilityName + "§e!");
		 }
	}
}
