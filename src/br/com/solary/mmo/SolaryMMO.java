package br.com.solary.mmo;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.solary.core.SolaryPlugin;
import br.com.solary.core.loaders.ListenerLoader;
import br.com.solary.mmo.commands.BoosterCommand;
import br.com.solary.mmo.database.Database;
import br.com.solary.mmo.database.Database.SQLType;
import br.com.solary.mmo.managers.MMOManager;
import br.com.solary.mmo.managers.PlayerBoosterManager;
import br.com.solary.mmo.objects.PlayerBooster;

public class SolaryMMO extends SolaryPlugin {

	public static MMOManager mmoManager;
	
	public static PlayerBoosterManager playerBoosterManager;
	public static Database sql;

	@Override
	public void onEnableInner() {
		saveDefaultConfig();
		if(!getConfig().getBoolean("MySQL.Ativar")){
			File db = new File(getDataFolder() + File.separator + "Boosters.db");
			if(!db.exists()){
				try {
					db.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(getConfig().getBoolean("MySQL.Ativar")){
			String user, password, host, database;
			user = getConfig().getString("MySQL.User");
			password = getConfig().getString("MySQL.Password");
			host = getConfig().getString("MySQL.Host");
			database = getConfig().getString("MySQL.Database");
			sql = new Database(user, password, host, database, SQLType.MySQL, this);
			sql.startConnection();
		}else{
			sql = new Database("Boosters", getDataFolder(), SQLType.SQLite, this);
			sql.startConnection();
		}
		
		mmoManager = new MMOManager(this);
		
		playerBoosterManager = new PlayerBoosterManager(this);
		sql.loadBoosters();
		new BoosterCommand(this);
		
		ListenerLoader.load(this, "br.com.solary.mmo.listeners");
		startBoosterExpireTask();
	}

	@Override
	public void onDisableInner() {
		sql.closeConnection();
	}

	public static String formatDifference(long time) {
		if (time == 0) {
			return "nunca";
		}
		long day = TimeUnit.MILLISECONDS.toDays(time);
		long hours = TimeUnit.MILLISECONDS.toHours(time) - (day * 24);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - (TimeUnit.MILLISECONDS.toHours(time) * 60);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(time) - (TimeUnit.MILLISECONDS.toMinutes(time) * 60);
		StringBuilder sb = new StringBuilder();
		if (day > 0) {
			sb.append(day).append(" ").append(day == 1 ? "dia" : "dias").append(" ");
		}
		if (hours > 0) {
			sb.append(hours).append(" ").append(hours == 1 ? "hora" : "horas").append(" ");
		}
		if (minutes > 0) {
			sb.append(minutes).append(" ").append(minutes == 1 ? "minuto" : "minutos").append(" ");
		}
		if (seconds > 0) {
			sb.append(seconds).append(" ").append(seconds == 1 ? "segundo" : "segundos");
		}
		String diff = sb.toString();
		return diff.isEmpty() ? "agora" : diff;
	}

	private void startBoosterExpireTask(){
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!playerBoosterManager.boosters.isEmpty()){
					for(PlayerBooster pb : playerBoosterManager.boosters.values()){
						if(pb.isCheck() && System.currentTimeMillis() >= pb.getEndTime()){
							if(Bukkit.getPlayerExact(pb.getNick()) != null)
							Bukkit.getPlayerExact(pb.getNick()).sendMessage("§7* O seu booster ativado na habilidade §f"+pb.getSkillType().getName()+ " §7acabou!");
							playerBoosterManager.removeBooster(pb.getNick());
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 20L, 20L);
	}
	

}
