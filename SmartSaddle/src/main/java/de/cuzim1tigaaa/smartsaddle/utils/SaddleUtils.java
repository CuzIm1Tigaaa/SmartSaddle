package de.cuzim1tigaaa.smartsaddle.utils;

import de.cuzim1tigaaa.smartsaddle.SmartSaddle;
import de.cuzim1tigaaa.smartsaddle.VersionMatcher;
import de.cuzim1tigaaa.smartsaddle.files.Config;
import de.cuzim1tigaaa.smartsaddle.files.Paths;
import de.cuzim1tigaaa.smartsaddle.utils.wrapper.EntityWrapper;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class SaddleUtils {

	private final SmartSaddle plugin;

	@Getter private final NamespacedKey typeKey, dataKey;

	public SaddleUtils(SmartSaddle plugin) {
		this.plugin = plugin;
		this.typeKey = new NamespacedKey(plugin, "type");
		this.dataKey = new NamespacedKey(plugin, "data");
	}

	public ItemStack getSaddle(LivingEntity entity) {
		if(VersionMatcher.isNautilusAvailable()) {
			if(entity instanceof AbstractNautilus) {
				Inventory inventory = ((AbstractNautilus) entity).getInventory();
				return inventory.getItem(0);
			}
		}

		if(VersionMatcher.isHappyGhastAvailable())
			if(entity instanceof HappyGhast)
				return entity.getEquipment().getItem(EquipmentSlot.BODY);

		switch(entity.getType()) {
			case HORSE, ZOMBIE_HORSE, SKELETON_HORSE, DONKEY, MULE, CAMEL, CAMEL_HUSK -> {
				AbstractHorseInventory abstractHorseInventory = ((AbstractHorse) entity).getInventory();
				return abstractHorseInventory.getItem(0);
			}
			case LLAMA, TRADER_LLAMA -> {
				AbstractHorseInventory abstractHorseInventory = ((AbstractHorse) entity).getInventory();
				LlamaInventory llamaInventory = (LlamaInventory) abstractHorseInventory;
				return llamaInventory.getDecor();
			}
			default -> throw new IllegalArgumentException("Entity is not a horse!");
		}
	}

	public ItemStack saveEntityToSaddle(LivingEntity entity) {
		ItemStack saddle = getSaddle(entity);
		ItemMeta meta = saddle.getItemMeta();

		EntityType entityType = entity.getType();
		EntityWrapper<LivingEntity> wrapper = plugin.getWrapperUtils().getWrapper(entityType);
		meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, entityType.name());
		meta.getPersistentDataContainer().set(dataKey, PersistentDataType.STRING, wrapper.serialize(entity).toString());
		meta.addEnchant(Registry.ENCHANTMENT.get(NamespacedKey.minecraft("unbreaking")), 3, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		Config config = Config.getConfig();

		meta.setDisplayName(config.getMessage(Paths.MESSAGES_SADDLE_NAME, "TYPE",
				SmartSaddle.capitalizeFully(config.getTranslation("entity." + entity.getType().name().toLowerCase()))));

		String message;
		switch(entity) {
			case Horse horse -> message = Config.getConfig().getMessage(Paths.MESSAGES_SADDLE_LORE_HORSE,
					"TYPE", SmartSaddle.capitalizeFully(config.getTranslation("entity." + horse.getType().name().toLowerCase())),
					"CUSTOM_NAME", horse.getCustomName() == null ? ChatColor.RED + "/" : horse.getCustomName(),
					"JUMP_STRENGTH", String.format("%.2f", horse.getJumpStrength()),
					"SPEED", String.format("%.2f", plugin.getEntityData().getMovementSpeed(horse).getBaseValue()),
					"MAX_HEALTH", String.format("%.2f", plugin.getEntityData().getMaxHealth(horse).getValue()),
					"HEALTH", String.format("%.2f", horse.getHealth()),
					"COLOR", SmartSaddle.capitalizeFully(config.getTranslation("horse.color." + horse.getColor().name().toLowerCase())),
					"STYLE", SmartSaddle.capitalizeFully(config.getTranslation("horse.style." + horse.getStyle().name().toLowerCase())));
			case Llama llama -> message = Config.getConfig().getMessage(Paths.MESSAGES_SADDLE_LORE_LLAMA,
					"TYPE", SmartSaddle.capitalizeFully(config.getTranslation("entity." + llama.getType().name().toLowerCase())),
					"CUSTOM_NAME", llama.getCustomName() == null ? ChatColor.RED + "/" : llama.getCustomName(),
					"JUMP_STRENGTH", String.format("%.2f", llama.getJumpStrength()),
					"SPEED", String.format("%.2f", plugin.getEntityData().getMovementSpeed(llama).getBaseValue()),
					"MAX_HEALTH", String.format("%.2f", plugin.getEntityData().getMaxHealth(llama).getValue()),
					"HEALTH", String.format("%.2f", llama.getHealth()),
					"COLOR", SmartSaddle.capitalizeFully(config.getTranslation("horse.color." + llama.getColor().name().toLowerCase())));
			case AbstractHorse abstractHorse -> message = Config.getConfig().getMessage(Paths.MESSAGES_SADDLE_LORE,
					"TYPE", SmartSaddle.capitalizeFully(config.getTranslation("entity." + abstractHorse.getType().name().toLowerCase())),
					"CUSTOM_NAME", abstractHorse.getCustomName() == null ? ChatColor.RED + "/" : abstractHorse.getCustomName(),
					"JUMP_STRENGTH", String.format("%.2f", abstractHorse.getJumpStrength()),
					"SPEED", String.format("%.2f", plugin.getEntityData().getMovementSpeed(abstractHorse).getBaseValue()),
					"MAX_HEALTH", String.format("%.2f", plugin.getEntityData().getMaxHealth(abstractHorse).getValue()),
					"HEALTH", String.format("%.2f", abstractHorse.getHealth()));
			default -> message = null;
		}

		if(VersionMatcher.isNautilusAvailable()) {
			if(entity instanceof AbstractNautilus nautilus) {
				message = Config.getConfig().getMessage(Paths.MESSAGES_SADDLE_LORE_NAUTILUS,
						"TYPE", SmartSaddle.capitalizeFully(config.getTranslation("entity." + nautilus.getType().name().toLowerCase())),
						"CUSTOM_NAME", nautilus.getCustomName() == null ? ChatColor.RED + "/" : nautilus.getCustomName(),
						"SPEED", String.format("%.2f", plugin.getEntityData().getMovementSpeed(nautilus).getBaseValue()),
						"MAX_HEALTH", String.format("%.2f", plugin.getEntityData().getMaxHealth(nautilus).getValue()),
						"HEALTH", String.format("%.2f", nautilus.getHealth()));
			}

			if(entity instanceof ZombieNautilus zNautilus) {
				message = Config.getConfig().getMessage(Paths.MESSAGES_SADDLE_LORE_ZOMBIE_NAUTILUS,
						"TYPE", SmartSaddle.capitalizeFully(config.getTranslation("entity." + zNautilus.getType().name().toLowerCase())),
						"CUSTOM_NAME", zNautilus.getCustomName() == null ? ChatColor.RED + "/" : zNautilus.getCustomName(),
						"SPEED", String.format("%.2f", plugin.getEntityData().getMovementSpeed(zNautilus).getBaseValue()),
						"MAX_HEALTH", String.format("%.2f", plugin.getEntityData().getMaxHealth(zNautilus).getValue()),
						"HEALTH", String.format("%.2f", zNautilus.getHealth()),
						"VARIANT", SmartSaddle.capitalizeFully(config.getTranslation("nautilus.variant." + ((Keyed) zNautilus.getVariant()).getKey().getKey().toLowerCase())));
			}
		}

		if(VersionMatcher.isHappyGhastAvailable()) {
			if(entity instanceof HappyGhast happyGhast) {
				message = Config.getConfig().getMessage(Paths.MESSAGES_SADDLE_LORE_HAPPY_GHAST,
						"TYPE", SmartSaddle.capitalizeFully(config.getTranslation("entity." + happyGhast.getType().name().toLowerCase())),
						"CUSTOM_NAME", happyGhast.getCustomName() == null ? ChatColor.RED + "/" : happyGhast.getCustomName(),
						"SPEED", String.format("%.2f", plugin.getEntityData().getMovementSpeed(happyGhast).getBaseValue()),
						"MAX_HEALTH", String.format("%.2f", plugin.getEntityData().getMaxHealth(happyGhast).getValue()),
						"HEALTH", String.format("%.2f", happyGhast.getHealth()));
			}
		}

		if(message == null)
			throw new UnsupportedOperationException("Entity type " + entity.getType() + " is not supported!");

		meta.setLore(new ArrayList<>(List.of(message.split("\n"))));
		saddle.setItemMeta(meta);
		return saddle;
	}

	public boolean spawnEntityFromSaddle(UUID placedBy, ItemStack saddle, Location location) {
		ItemMeta meta = saddle.getItemMeta();
		String data = meta.getPersistentDataContainer().get(dataKey, PersistentDataType.STRING);
		if(data == null)
			return false;

		String type = meta.getPersistentDataContainer().get(typeKey, PersistentDataType.STRING);
		if(type == null)
			type = plugin.getWrapperUtils().getEntityType(data).name();

		EntityType entityType = EntityType.valueOf(type);
		LivingEntity entity = plugin.getWrapperUtils().getWrapper(entityType).deserialize(data, location);
		if(entity == null)
			return false;

		entity.getPersistentDataContainer().set(plugin.getOwnerKey(), PersistentDataType.STRING, placedBy.toString());
		return entity.isValid();
	}
}