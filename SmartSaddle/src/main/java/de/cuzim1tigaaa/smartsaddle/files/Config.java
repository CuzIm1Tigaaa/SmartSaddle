package de.cuzim1tigaaa.smartsaddle.files;

import de.cuzim1tigaaa.smartsaddle.SmartSaddle;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {

	private FileConfiguration messagesFile, translationFile;
	@Getter private List<String> disabledWorlds;

	private static Config config;

	public static Config getConfig() {
		if(config == null)
			config = new Config();
		return config;
	}

	private static final Pattern pattern = Pattern.compile("(#[a-fA-F0-9]{6})");

	public String getMessage(String path, Object... replace) {
		StringBuilder message;
		if(messagesFile.isList(path)) {
			List<String> messageList = messagesFile.getStringList(path);
			message = new StringBuilder();
			for(String line : messageList) {
				message.append(line).append("\n");
			}
		}else {
			if(messagesFile.getString(path) == null)
				return ChatColor.RED + "Error: Path " + ChatColor.GRAY + "'" + path + "' " + ChatColor.RED + "does not exist!";
			message = new StringBuilder(messagesFile.getString(path));
		}

		if(message.isEmpty())
			return null;

		for(int i = 0; i < replace.length; i++) {
			String target = replace[i] == null ? null : (String) replace[i];
			if(target == null)
				continue;
			i++;
			String replacement = replace[i] == null ? null : replace[i].toString();
			if(messagesFile != null) message = new StringBuilder(replacement == null ? message.toString() : message.toString().replace("%" + target + "%", replacement));
		}

		Matcher matcher = pattern.matcher(message.toString());
		while(matcher.find()) {
			String hex = message.substring(matcher.start(), matcher.end());
			message = new StringBuilder(message.toString().replace(hex, ChatColor.of(hex) + ""));
			matcher = pattern.matcher(message.toString());
		}

		return ChatColor.translateAlternateColorCodes('&', message.toString());
	}

	public String getTranslation(String path) {
		return translationFile.getString(path);
	}

	private void set(FileConfiguration fileConfig, String path, Object value, String... comments) {
		fileConfig.set(path, fileConfig.get(path, value));
		if(comments.length > 0)
			fileConfig.setComments(path, List.of(comments));
	}

	public void loadConfig(SmartSaddle plugin) {
		FileConfiguration configFile;
		try {
			File file = new File(plugin.getDataFolder(), "config.yml");
			if(!file.exists()) {
				configFile = new YamlConfiguration();
				configFile.save(file);
			}
			configFile = YamlConfiguration.loadConfiguration(file);
			set(configFile, Paths.CONFIG_DISABLED_WORLDS, List.of("worldToDisable"), "List of disabled worlds");
			configFile.save(file);
		}catch(IOException e) {
			plugin.getLogger().severe("Failed to load config file!");
			this.disabledWorlds = List.of();
			return;
		}

		this.disabledWorlds = configFile.getStringList(Paths.CONFIG_DISABLED_WORLDS);
	}

	public void loadMessages(SmartSaddle plugin) {
		try {
			File file = new File(plugin.getDataFolder(), "messages.yml");
			if(!file.exists()) {
				messagesFile = new YamlConfiguration();
				messagesFile.save(file);
			}
			messagesFile = YamlConfiguration.loadConfiguration(file);
			set(messagesFile, Paths.MESSAGES_PERMISSION, "&cYou do not have permission to use this command");
			set(messagesFile, Paths.MESSAGES_RELOADED, "&7The plugin has been reloaded.");
			set(messagesFile, Paths.MESSAGES_INVENTORY_FULL, "&cYou cannot save this horse because your inventory is full!");

			set(messagesFile, Paths.MESSAGES_SADDLE_NAME, "&6%TYPE%",
					"Name of the saddle. The following placeholders can be used:",
					"%TYPE% - The type of the horse");
			set(messagesFile, Paths.MESSAGES_SADDLE_LORE, List.of(
							"",
							"&7Custom Name: &6%CUSTOM_NAME%",
							"&7Jump Strength: &6%JUMP_STRENGTH%",
							"&7Speed: &6%SPEED%",
							"&7Max Health: &6%MAX_HEALTH%",
							"&7Health: &6%HEALTH%"
					),
					"General Lore data for saddles. The following placeholders can be used:",
					"%TYPE% - The type of the mount",
					"%CUSTOM_NAME% - The custom name of the mount",
					"%JUMP_STRENGTH% - The jump strength of the mount",
					"%SPEED% - The speed of the mount",
					"%MAX_HEALTH% - The max health of the mount",
					"%HEALTH% - The health of the mount");

			set(messagesFile, Paths.MESSAGES_SADDLE_LORE_HORSE, List.of(
							"",
							"&7Custom Name: &6%CUSTOM_NAME%",
							"&7Jump Strength: &6%JUMP_STRENGTH%",
							"&7Speed: &6%SPEED%",
							"&7Max Health: &6%MAX_HEALTH%",
							"&7Health: &6%HEALTH%",
							"",
							"&7Color: &6%COLOR%",
							"&7Style: &6%STYLE%"
					),
					"Lore data for horse saddles. The following placeholders can be used:",
					"%TYPE% - The type of the horse",
					"%CUSTOM_NAME% - The custom name of the horse",
					"%JUMP_STRENGTH% - The jump strength of the horse",
					"%SPEED% - The speed of the horse",
					"%MAX_HEALTH% - The max health of the horse",
					"%HEALTH% - The health of the horse",
					"%COLOR% - The color of the horse",
					"%STYLE% - The style of the horse");

			messagesFile.save(file);
		}catch(IOException e) {
			plugin.getLogger().severe("Failed to load messages file!");
		}
	}

	public void loadTranslations(SmartSaddle plugin) {
		try {
			File file = new File(plugin.getDataFolder(), "translations.yml");
			if(!file.exists()) {
				translationFile = new YamlConfiguration();
				translationFile.save(file);
			}
			translationFile = YamlConfiguration.loadConfiguration(file);

			set(translationFile, Paths.TRANSLATION_ENTITY, null, "translations for all mount names");
			set(translationFile, Paths.TRANSLATION_ENTITY_HORSE, "Horse");
			set(translationFile, Paths.TRANSLATION_ENTITY_DONKEY, "Donkey");
			set(translationFile, Paths.TRANSLATION_ENTITY_MULE, "Mule");
			set(translationFile, Paths.TRANSLATION_ENTITY_CAMEL, "Camel");
			set(translationFile, Paths.TRANSLATION_ENTITY_SKELETON_HORSE, "Skeleton Horse");
			set(translationFile, Paths.TRANSLATION_ENTITY_ZOMBIE_HORSE, "Zombie Horse");

			set(translationFile, Paths.TRANSLATION_HORSE, null, "translations for all horse colors and styles");
			set(translationFile, Paths.TRANSLATION_COLOR, null,
					"See here:",
					"https://minecraft.wiki/w/Horse#Appearance");

			set(translationFile, Paths.TRANSLATION_COLOR_WHITE, "White");
			set(translationFile, Paths.TRANSLATION_COLOR_CREAMY, "Creamy");
			set(translationFile, Paths.TRANSLATION_COLOR_CHESTNUT, "Chestnut");
			set(translationFile, Paths.TRANSLATION_COLOR_BROWN, "Brown");
			set(translationFile, Paths.TRANSLATION_COLOR_BLACK, "Black");
			set(translationFile, Paths.TRANSLATION_COLOR_GRAY, "Gray");
			set(translationFile, Paths.TRANSLATION_COLOR_DARK_BROWN, "Dark Brown");

			set(translationFile, Paths.TRANSLATION_STYLE, null,
					"See here:",
					"https://minecraft.wiki/w/Horse#Appearance");

			set(translationFile, Paths.TRANSLATION_STYLE_NONE, "None");
			set(translationFile, Paths.TRANSLATION_STYLE_WHITE, "White");
			set(translationFile, Paths.TRANSLATION_STYLE_WHITEFIELD, "Whitefield");
			set(translationFile, Paths.TRANSLATION_STYLE_WHITE_DOTS, "White Dots");
			set(translationFile, Paths.TRANSLATION_STYLE_BLACK_DOTS, "Black Dots");

			translationFile.save(file);
		}catch(IOException e) {
			plugin.getLogger().severe("Failed to load translation file!");
		}
	}
}