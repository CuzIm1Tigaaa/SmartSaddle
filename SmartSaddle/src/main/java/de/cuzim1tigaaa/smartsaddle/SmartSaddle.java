package de.cuzim1tigaaa.smartsaddle;

import de.cuzim1tigaaa.smartsaddle.command.CommandSmartSaddle;
import de.cuzim1tigaaa.smartsaddle.files.Config;
import de.cuzim1tigaaa.smartsaddle.listeners.SaddleEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

@Getter
public final class SmartSaddle extends JavaPlugin {

	private HorseData horseData;

	@Override
	public void onEnable() {
		if(!Bukkit.getServer().getPluginManager().isPluginEnabled("NBTAPI")) {
			getLogger().severe("NBTAPI is not installed but required! Disabling SmartSaddle...");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		this.horseData = new VersionMatcher().match();

		reload();
		// Plugin startup logic
		new SaddleEvent(this);
		new CommandSmartSaddle(this);
	}

	public void reload() {
		getLogger().log(Level.INFO, "Loading plugin files...");
		Config config = Config.getConfig();
		config.loadConfig(this);
		config.loadMessages(this);
		config.loadTranslations(this);
	}

	public static String capitalizeFully(String s) {
		if(s == null || s.isEmpty())
			return s;

		s = s.replace("_", " ");
		String[] words = s.toLowerCase().split(" ");
		StringBuilder sb = new StringBuilder();

		for(String word : words)
			sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");

		return sb.toString().trim();
	}
}