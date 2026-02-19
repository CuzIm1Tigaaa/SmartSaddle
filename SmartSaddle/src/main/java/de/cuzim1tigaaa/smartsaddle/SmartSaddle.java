package de.cuzim1tigaaa.smartsaddle;

import de.cuzim1tigaaa.smartsaddle.command.CommandSmartSaddle;
import de.cuzim1tigaaa.smartsaddle.files.Config;
import de.cuzim1tigaaa.smartsaddle.listeners.SaddleEvent;
import de.cuzim1tigaaa.smartsaddle.utils.SaddleUtils;
import de.cuzim1tigaaa.smartsaddle.utils.wrapper.WrapperUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

@Getter
public final class SmartSaddle extends JavaPlugin {

	private EntityData entityData;
	private SaddleUtils saddleUtils;
	private WrapperUtils wrapperUtils;

	@Getter
	private final NamespacedKey ownerKey = new NamespacedKey(this, "owner");

	@Override
	public void onEnable() {
		if(!Bukkit.getServer().getPluginManager().isPluginEnabled("NBTAPI")) {
			getLogger().severe("NBTAPI is not installed but required! Disabling SmartSaddle...");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		VersionMatcher.initAvailability();
		this.entityData = VersionMatcher.match();
		this.wrapperUtils = new WrapperUtils(this);
		this.saddleUtils = new SaddleUtils(this);

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

	public boolean isOwnerOfAbstractHorse(AbstractHorse horse, UUID possibleOwner) {
		if(horse == null || possibleOwner == null)
			return false;

		PersistentDataContainer container = horse.getPersistentDataContainer();
		if(!container.has(ownerKey))
			return false;

		return Objects.equals(container.get(ownerKey, PersistentDataType.STRING), possibleOwner.toString());
	}
}