package de.cuzim1tigaaa.smartsaddle.utils;

import de.cuzim1tigaaa.smartsaddle.SmartSaddle;
import de.cuzim1tigaaa.smartsaddle.files.Config;
import de.cuzim1tigaaa.smartsaddle.files.Paths;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
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
		meta.getPersistentDataContainer()
				.set(horseDataKey, PersistentDataType.STRING, horseWrapper.serialize(abstractHorse).toString());

		meta.setDisplayName(Config.getConfig().getMessage(Paths.MESSAGES_SADDLE_NAME, "TYPE",
				SmartSaddle.capitalizeFully(abstractHorse.getType().name())));
		meta.addEnchant(Registry.ENCHANTMENT.get(NamespacedKey.minecraft("unbreaking")), 3, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		String message = Config.getConfig().getMessage(Paths.MESSAGES_SADDLE_LORE,
				"CUSTOM_NAME", abstractHorse.getCustomName() == null ? ChatColor.RED + "/" : abstractHorse.getCustomName(),
				"JUMP_STRENGTH", String.format("%.2f", abstractHorse.getJumpStrength()),
				"SPEED", String.format("%.2f", plugin.getHorseData().getMovementSpeed(abstractHorse).getValue()),
				"MAX_HEALTH", String.format("%.2f", plugin.getHorseData().getMaxHealth(abstractHorse).getValue()),
				"HEALTH", String.format("%.2f", abstractHorse.getHealth()));

		if(abstractHorse instanceof Horse horse) {
			message += Config.getConfig().getMessage(Paths.MESSAGES_SADDLE_LORE_HORSE,
					"COLOR", SmartSaddle.capitalizeFully(horse.getColor().name()),
					"STYLE", SmartSaddle.capitalizeFully(horse.getStyle().name()));
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
		return true;
	}
}