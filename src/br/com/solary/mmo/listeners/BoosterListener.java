package br.com.solary.mmo.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.nossr50.datatypes.skills.SkillType;

import br.com.solary.core.utils.ItemBuilder;
import br.com.solary.mmo.SolaryMMO;
import br.com.solary.mmo.objects.PlayerBooster;

public class BoosterListener implements Listener {

	private Inventory inventory;

	private void loadInventory() {
		this.inventory = Bukkit.createInventory(null, 27, "Escolha a habilidade:");
		this.inventory.setItem(10, new ItemBuilder(Material.DIAMOND_SWORD).setName("§aEspadas").setLore(" ", " §7Ative o booster nessa habilidade.", " ").toItemStack());
		this.inventory.setItem(11, new ItemBuilder(Material.BOW).setName("§aArqueiro").setLore(" ", " §7Ative o booster nessa habilidade.", " ").toItemStack());
		this.inventory.setItem(12, new ItemBuilder(Material.FEATHER).setName("§aAcrobacia").setLore(" ", " §7Ative o booster nessa habilidade.", " ").toItemStack());
		this.inventory.setItem(13, new ItemBuilder(Material.DIAMOND_AXE).setName("§aMachado").setLore(" ", " §7Ative o booster nessa habilidade.", " ").toItemStack());
		this.inventory.setItem(14, new ItemBuilder(Material.DIAMOND_PICKAXE).setName("§aMineração").setLore(" ", " §7Ative o booster nessa habilidade.", " ").toItemStack());
		this.inventory.setItem(15, new ItemBuilder(Material.DIAMOND_SPADE).setName("§aEscavação").setLore(" ", " §7Ative o booster nessa habilidade.", " ").toItemStack());
		this.inventory.setItem(16, new ItemBuilder(Material.NETHER_STALK).setName("§aHerbalismo").setLore(" ", " §7Ative o booster nessa habilidade.", " ").toItemStack());
	}
	
	@EventHandler
	private void onInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR && player.getItemInHand().getType() == Material.EXP_BOTTLE && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().hasLore()){
			if(player.getItemInHand().getItemMeta().getDisplayName().equals("§aBooster de Experiência")){
				event.setCancelled(true);
				if(SolaryMMO.playerBoosterManager.hasBoosterActived(player.getName())){
					PlayerBooster playerBooster = SolaryMMO.playerBoosterManager.getPlayerBooster(player.getName());
					long time = playerBooster.getEndTime() - System.currentTimeMillis();
					String timer = SolaryMMO.formatDifference(time);
					if(!timer.equals("agora")){
						player.sendMessage("§7* Você já possui um booster ativado na skill §f" + playerBooster.getSkillType().getName() + "§7.");
						player.sendMessage("§7* Tempo restante: §f" + timer + "§7.");
						return;
					}else{
						SolaryMMO.playerBoosterManager.removeBooster(player.getName());
					}
				}
				if(inventory == null) loadInventory();
				
				player.updateInventory();
				player.openInventory(inventory);
			}
		}
	}

	@EventHandler
	private void onClick(InventoryClickEvent event){
		if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR){
			return;
		}
		
		if(inventory == null) loadInventory();
		if(event.getInventory().getName().equals(this.inventory.getName())){
			event.setCancelled(true);
		}
		if(event.getWhoClicked() instanceof Player && event.getClickedInventory().getName().equals(this.inventory.getName())){
			Player player = (Player) event.getWhoClicked();
			ItemStack item = event.getCurrentItem();
			if(item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore() && player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR && player.getItemInHand().getType() == Material.EXP_BOTTLE && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().hasLore() && player.getItemInHand().getItemMeta().getDisplayName().equals("§aBooster de Experiência")){
				if(!SolaryMMO.playerBoosterManager.hasBoosterActived(player.getName())){
					boolean remove = false;
					if(item.getItemMeta().getDisplayName().equals("§aMineração")){
						SolaryMMO.playerBoosterManager.setBooster(player.getName(), SkillType.MINING);
						player.sendMessage("§7* Parabéns, você ativou um booster na habilidade §fMineração§7!");
						remove = true;
					}else if(item.getItemMeta().getDisplayName().equals("§aArqueiro")){
						SolaryMMO.playerBoosterManager.setBooster(player.getName(), SkillType.ARCHERY);
						player.sendMessage("§7*Parabéns, você ativou um booster na habilidade §fArqueiro§7!");
						remove = true;
					}else if(item.getItemMeta().getDisplayName().equals("§aEspadas")){
						SolaryMMO.playerBoosterManager.setBooster(player.getName(), SkillType.SWORDS);
						player.sendMessage("§7* Parabéns, você ativou um booster na habilidade §fEspadas§7!");
						remove = true;
					}else if(item.getItemMeta().getDisplayName().equals("§aEscavação")){
						SolaryMMO.playerBoosterManager.setBooster(player.getName(), SkillType.EXCAVATION);
						player.sendMessage("§7* Parabéns, você ativou um booster na habilidade §fEscavação§7!");
						remove = true;
					}else if(item.getItemMeta().getDisplayName().equals("§aAcrobacia")){
						SolaryMMO.playerBoosterManager.setBooster(player.getName(), SkillType.ACROBATICS);
						player.sendMessage("§7* Parabéns, você ativou um booster na habilidade §fAcrobacia§7!");
						remove = true;
					}else if(item.getItemMeta().getDisplayName().equals("§aMachado")){
						SolaryMMO.playerBoosterManager.setBooster(player.getName(), SkillType.AXES);
						player.sendMessage("§7* Parabéns, você ativou um booster na habilidade §fMachado§7!");
						remove = true;
					}else if(item.getItemMeta().getDisplayName().equals("§aHerbalismo")){
						SolaryMMO.playerBoosterManager.setBooster(player.getName(), SkillType.HERBALISM);
						player.sendMessage("§7* Parabéns, você ativou um booster na habilidade §fHerbalismo§7!");
						remove = true;
					}
					if(remove){
						if(player.getItemInHand().getAmount() - 1 >= 1){
							ItemStack is = new ItemStack(Material.EXP_BOTTLE, player.getItemInHand().getAmount() - 1);
							ItemMeta itemMeta = is.getItemMeta();
							itemMeta.setDisplayName("§aBooster de Experiência");
							List<String> lore = new ArrayList<>();
							lore.add("§7* Receba §f1 hora §7de §fDuplo XP §7em qualquer habilidade!");
							itemMeta.setLore(lore);
							is.setItemMeta(itemMeta);
							player.setItemInHand(is);
						}else{
							player.setItemInHand(null);
						}
						remove = false;
					}
					player.closeInventory();
					player.updateInventory();
				}
			}else{
				player.sendMessage("§7* Você precisa estar segurando um §fbooster §7para selecionar a habilidade.");
				player.closeInventory();
			}
		}
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		if(SolaryMMO.playerBoosterManager.hasBoosterActived(player.getName())){
			PlayerBooster playerBooster = SolaryMMO.playerBoosterManager.getPlayerBooster(player.getName());
			long rest = playerBooster.getEndTime() - System.currentTimeMillis();
			playerBooster.setRest(rest);
			playerBooster.setCheck(false);
			SolaryMMO.sql.setRest(player.getName(), rest);
		}
	}

	@EventHandler
	private void onJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(SolaryMMO.playerBoosterManager.hasBoosterActived(player.getName())){
			PlayerBooster playerBooster = SolaryMMO.playerBoosterManager.getPlayerBooster(player.getName());
			playerBooster.setEndTime(System.currentTimeMillis() + playerBooster.getRest());
			playerBooster.setCheck(true);
		}
	}

}
