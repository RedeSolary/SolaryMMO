package br.com.solary.mmo.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.database.DatabaseManagerFactory;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.SkillType;

import br.com.solary.core.utils.ItemBuilder;
import br.com.solary.mmo.SolaryMMO;

public class MMOManager {

	private PlayerStat nullPlayerStat = new PlayerStat("Ninguém", 0);
	public HashMap<SkillType, List<PlayerStat>> tops;
	private Inventory inventoryTop;

	public MMOManager(SolaryMMO plugin) {
		this.tops = new HashMap<>();

		new BukkitRunnable() {
			@Override
			public void run() {
				tops.put(null, getSkillTop(null, 1, 10));
				tops.put(SkillType.ACROBATICS, getSkillTop(SkillType.ACROBATICS, 1, 10));
				tops.put(SkillType.ARCHERY, getSkillTop(SkillType.ARCHERY, 1, 10));
				tops.put(SkillType.AXES, getSkillTop(SkillType.AXES, 1, 10));
				tops.put(SkillType.EXCAVATION, getSkillTop(SkillType.EXCAVATION, 1, 10));
				tops.put(SkillType.HERBALISM, getSkillTop(SkillType.HERBALISM, 1, 10));
				tops.put(SkillType.MINING, getSkillTop(SkillType.MINING, 1, 10));
				tops.put(SkillType.SWORDS, getSkillTop(SkillType.SWORDS, 1, 10));
				
				inventoryTop = Bukkit.createInventory(null, 6*9, "Habilidades - Rank");
				inventoryTop.setItem(13, getSkillItem(Material.BOOK_AND_QUILL, null));
				inventoryTop.setItem(20, getSkillItem(Material.DIAMOND_SWORD, SkillType.SWORDS));
				inventoryTop.setItem(21, getSkillItem(Material.BOW, SkillType.ARCHERY));
				inventoryTop.setItem(22, getSkillItem(Material.DIAMOND_SPADE, SkillType.EXCAVATION));
				inventoryTop.setItem(23, getSkillItem(Material.DIAMOND_PICKAXE, SkillType.MINING));
				inventoryTop.setItem(24, getSkillItem(Material.FEATHER, SkillType.ACROBATICS));
				inventoryTop.setItem(29, getSkillItem(Material.DIAMOND_AXE, SkillType.AXES));
				inventoryTop.setItem(33, getSkillItem(Material.NETHER_STALK, SkillType.HERBALISM));
				inventoryTop.setItem(49, new ItemBuilder(Material.ARROW).setName("§aVoltar").toItemStack());
			}
		}.runTaskTimerAsynchronously(plugin, 0L, 600 * 20L);
	}

	public void openTopInventory(Player player) {
		player.openInventory(inventoryTop);
	}

	private List<PlayerStat> getSkillTop(SkillType skillType, int page, int count){
		List<PlayerStat> top = DatabaseManagerFactory.getDatabaseManager().readLeaderboard(skillType, page, count);

		for (int i = top.size(); i < 10; i++) {
			top.add(nullPlayerStat);
		}

		return top;
	}
	
	private ItemStack getSkillItem(Material material, SkillType skillType) {
		List<String> lore = new ArrayList<>();
		
		AtomicInteger atomicInteger = new AtomicInteger(1);
		tops.get(skillType).forEach(playstat -> lore.add(coloredInt(atomicInteger.getAndIncrement()) + " " + playstat.name + " §8- §7" + playstat.statVal));
		
		String skillName = skillType == null ? "§eNível Total" : "§e" + skillType.getName();
		return new ItemBuilder(material).setName(skillName).setLore(lore).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).toItemStack();
	}
	
	private String coloredInt(int i) {
		return i == 1 ? "§e1º" : i == 2 ? "§72º" : i == 3 ? "§63º" : "§f" + i + "º";
	}
}
