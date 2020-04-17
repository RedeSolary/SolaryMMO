package br.com.solary.mmo.listeners;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.Sets;

import br.com.solary.core.utils.ItemBuilder;
import br.com.solary.mmo.SolaryMMO;

public class PlayerListeners implements Listener{

	private Set<McMMOPlayer> mmoUsers = Sets.newConcurrentHashSet();

	@EventHandler
	private void onPreCommand(PlayerCommandPreprocessEvent e) {
		String[] args = e.getMessage().split(" ");
		if(args[0].equalsIgnoreCase("/skill") || args[0].equalsIgnoreCase("/stats") || args[0].equalsIgnoreCase("/mctop")) {
			McMMOPlayer mmoPlayer = getMmoPlayer(e.getPlayer());
			if(mmoPlayer == null) {
				mmoPlayer = UserManager.getPlayer(e.getPlayer());
				mmoUsers.add(mmoPlayer);
			}
			openSkillInventory(mmoPlayer);
			e.setCancelled(true);
		}
	}
 
	private McMMOPlayer getMmoPlayer(Player player) {
		return mmoUsers.stream().filter(user -> user.getPlayer().equals(player)).findFirst().orElse(null);
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		mmoUsers.removeIf(mmoUser -> mmoUser.getPlayer().equals(e.getPlayer()));
	}

	@EventHandler	
	private void onClick(InventoryClickEvent e) {
		if(e.getInventory().getTitle().startsWith("Habilidades - ")) {
			e.setCancelled(true);
			if(!(e.getWhoClicked() instanceof Player)) return;
			Player player = (Player) e.getWhoClicked();
			player.updateInventory();
			if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) {
				if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§eRank de habilidades")) SolaryMMO.mmoManager.openTopInventory(player);
				if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aVoltar")) openSkillInventory(getMmoPlayer(player));
			}
		}
	}

	private void openSkillInventory(McMMOPlayer mmoPlayer){
		Player player = mmoPlayer.getPlayer();
		Inventory inv = Bukkit.createInventory(null, 5*9, "Habilidades - " + player.getName());

		String rankGeral = "Indefinido";
		
		try {
			int i = mcMMO.getDatabaseManager().readRank(player.getName()).get(null);
			rankGeral = String.valueOf(i);
		} catch (Exception e) {}
		
		inv.setItem(12, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setName(player.getName()).setLore("§7[§e⚒§7] Nível total: §e" + mmoPlayer.getPowerLevel(), "", "§fSua posição no rank: §e" + rankGeral, "§f1º colocado no rank: §e" + SolaryMMO.mmoManager.tops.get(null).get(0).name).toItemStack());
		inv.setItem(14, new ItemBuilder(Material.BOOK_AND_QUILL).setName("§eRank de habilidades").setLore("", "§7Clique para ver os melhores jogadores", "§7em cada habilidade", "").toItemStack());

		inv.setItem(20, getSkillItem(Material.DIAMOND_SWORD, SkillType.SWORDS, mmoPlayer));
		inv.setItem(21, getSkillItem(Material.BOW, SkillType.ARCHERY, mmoPlayer));
		inv.setItem(22, getSkillItem(Material.DIAMOND_SPADE, SkillType.EXCAVATION, mmoPlayer));
		inv.setItem(23, getSkillItem(Material.DIAMOND_PICKAXE, SkillType.MINING, mmoPlayer));
		inv.setItem(24, getSkillItem(Material.FEATHER, SkillType.ACROBATICS, mmoPlayer));
		inv.setItem(29, getSkillItem(Material.DIAMOND_AXE, SkillType.AXES, mmoPlayer));
		inv.setItem(33, getSkillItem(Material.NETHER_STALK, SkillType.HERBALISM, mmoPlayer));

		player.openInventory(inv);
	}

	private ItemStack getSkillItem(Material material, SkillType skillType, McMMOPlayer mmoPlayer) {
		Player player = mmoPlayer.getPlayer();
		String vipBoost = player.hasPermission("mcmmo.perks.xp.double") ? "§e2.0" : player.hasPermission("mcmmo.perks.xp.triple") ? "§e3.0" : player.hasPermission("mcmmo.perks.xp.quadruple") ? "§e4.0" : player.hasPermission("mcmmo.perks.xp.10percentboost") ? "§e1.1" : player.hasPermission("mcmmo.perks.xp.50percentboost") ? "§e1.5" : player.hasPermission("mcmmo.perks.xp.150percentboost") ? "§e2.5" : "Nenhum";
		String boost = SolaryMMO.playerBoosterManager.hasBoosterActived(player.getName()) ? "§e2.0" : "§7Nenhum";

		String rank = "Indefinido";
		try {
			int i = mcMMO.getDatabaseManager().readRank(player.getName()).get(skillType);
			rank = String.valueOf(i);
		} catch (Exception e) {}
		
		int actualXp = ExperienceAPI.getXP(player, skillType.getName());
		int actualLevel = ExperienceAPI.getLevel(player, skillType.getName());
		int nextLevelXp = ExperienceAPI.getXPToNextLevel(player, skillType.getName());
		return new ItemBuilder(material).setName("§e" + skillType.getName()).setLore("§fNível: §7" + actualLevel, "§fExperiência: §7" + actualXp + "/" + nextLevelXp, "", "§fBônus §6VIP§7: " + vipBoost, "§fBooster§7: " + boost, "", "§fSua posição no rank: §e" + rank, "§f1º colocado no rank: §e" + SolaryMMO.mmoManager.tops.get(skillType).get(0).name, "").addItemFlag(ItemFlag.HIDE_ATTRIBUTES).toItemStack();
	}

}
