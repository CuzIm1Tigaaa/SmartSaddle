package de.cuzim1tigaaa.smartsaddle.utils;

import com.google.gson.JsonElement;
import de.cuzim1tigaaa.smartsaddle.SmartSaddle;
import de.cuzim1tigaaa.smartsaddle.files.Config;
import de.cuzim1tigaaa.smartsaddle.files.Paths;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class SaddleUtils {

	private final SmartSaddle plugin;
	@Getter
	private final NamespacedKey horseDataKey;
	private final HorseWrapper horseWrapper;

	public SaddleUtils(SmartSaddle plugin) {
		this.plugin = plugin;
		this.horseDataKey = new NamespacedKey(plugin, "data");
		this.horseWrapper = new HorseWrapper(plugin);
	}

	public ItemStack saveHorseToSaddle(AbstractHorse abstractHorse) {
		ItemStack saddle = abstractHorse.getInventory().getSaddle();
		ItemMeta meta = saddle.getItemMeta();

		JsonElement horseSerialized = horseWrapper.serialize(abstractHorse);
		meta.getPersistentDataContainer().set(horseDataKey, PersistentDataType.STRING, horseSerialized.toString());
		meta.addEnchant(Registry.ENCHANTMENT.get(NamespacedKey.minecraft("unbreaking")), 3, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		Config config = Config.getConfig();

		meta.setDisplayName(config.getMessage(Paths.MESSAGES_SADDLE_NAME, "TYPE",
				SmartSaddle.capitalizeFully(config.getTranslation("entity." + abstractHorse.getType().name().toLowerCase()))));

		String message;
		if(abstractHorse instanceof Horse horse) {
			message = Config.getConfig().getMessage(Paths.MESSAGES_SADDLE_LORE_HORSE,
					"TYPE", SmartSaddle.capitalizeFully(config.getTranslation("entity." + horse.getType().name().toLowerCase())),
					"CUSTOM_NAME", abstractHorse.getCustomName() == null ? ChatColor.RED + "/" : abstractHorse.getCustomName(),
					"JUMP_STRENGTH", String.format("%.2f", abstractHorse.getJumpStrength()),
					"SPEED", String.format("%.2f", plugin.getHorseData().getMovementSpeed(abstractHorse).getBaseValue()),
					"MAX_HEALTH", String.format("%.2f", plugin.getHorseData().getMaxHealth(abstractHorse).getValue()),
					"HEALTH", String.format("%.2f", abstractHorse.getHealth()),
					"COLOR", SmartSaddle.capitalizeFully(config.getTranslation("horse.color." + horse.getColor().name().toLowerCase())),
					"STYLE", SmartSaddle.capitalizeFully(config.getTranslation("horse.style." + horse.getStyle().name().toLowerCase())));

		}else {
			message = Config.getConfig().getMessage(Paths.MESSAGES_SADDLE_LORE,
					"TYPE", SmartSaddle.capitalizeFully(config.getTranslation("entity." + abstractHorse.getType().name().toLowerCase())),
					"CUSTOM_NAME", abstractHorse.getCustomName() == null ? ChatColor.RED + "/" : abstractHorse.getCustomName(),
					"JUMP_STRENGTH", String.format("%.2f", abstractHorse.getJumpStrength()),
					"SPEED", String.format("%.2f", plugin.getHorseData().getMovementSpeed(abstractHorse).getValue()),
					"MAX_HEALTH", String.format("%.2f", plugin.getHorseData().getMaxHealth(abstractHorse).getValue()),
					"HEALTH", String.format("%.2f", abstractHorse.getHealth()));
		}

		meta.setLore(new ArrayList<>(List.of(message.split("\n"))));
		saddle.setItemMeta(meta);
		return saddle;
	}

	public boolean spawnHorseFromSaddle(ItemStack saddle, Location location) {
		ItemMeta meta = saddle.getItemMeta();
		String data = meta.getPersistentDataContainer().get(horseDataKey, PersistentDataType.STRING);
		if(data == null)
			return false;

		AbstractHorse horse = horseWrapper.deserialize(data, location);
		return horse != null && horse.isValid();
	}
}