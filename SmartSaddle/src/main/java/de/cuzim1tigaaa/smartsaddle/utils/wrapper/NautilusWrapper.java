package de.cuzim1tigaaa.smartsaddle.utils.wrapper;

import com.google.gson.*;
import de.cuzim1tigaaa.smartsaddle.SmartSaddle;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class NautilusWrapper implements EntityWrapper<AbstractNautilus> {

	private final SmartSaddle plugin;

	public NautilusWrapper(SmartSaddle plugin) {
		this.plugin = plugin;
	}

	public AbstractNautilus deserialize(String json, Location location) {
		JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
		EntityType type = EntityType.valueOf(EntityWrapper.getOrDefault(jsonObject, WrapperUtils.TYPE, EntityType.NAUTILUS).getAsString());

		AbstractNautilus nautilus = (AbstractNautilus) location.getWorld().spawnEntity(location, type);
		switch(type) {
			case NAUTILUS -> {}
			case ZOMBIE_NAUTILUS -> nautilus = deserializeZombieNautilus(nautilus, jsonObject);
			default -> throw new JsonParseException("Invalid nautilus type: " + type);
		}

		nautilus.setCustomName(EntityWrapper.getOrDefault(jsonObject, WrapperUtils.NAME, "").getAsString());
		nautilus.setLoveModeTicks(EntityWrapper.getOrDefault(jsonObject, WrapperUtils.LOVE_MODE_TICKS, nautilus.getLoveModeTicks()).getAsInt());
		plugin.getEntityData().getMovementSpeed(nautilus).setBaseValue(
				EntityWrapper.getOrDefault(jsonObject, WrapperUtils.SPEED, plugin.getEntityData().getMovementSpeed(nautilus)).getAsDouble());
		plugin.getEntityData().getMaxHealth(nautilus).setBaseValue(
				EntityWrapper.getOrDefault(jsonObject, WrapperUtils.MAX_HEALTH, plugin.getEntityData().getMaxHealth(nautilus)).getAsDouble());
		nautilus.setHealth(EntityWrapper.getOrDefault(jsonObject, WrapperUtils.HEALTH, nautilus.getHealth()).getAsDouble());
		nautilus.setTamed(true);

		ReadWriteNBT nbt = NBT.parseNBT(
				EntityWrapper.getOrDefault(jsonObject, WrapperUtils.INVENTORY, NBT.itemStackArrayToNBT(new ItemStack[0])).getAsString());
		ItemStack[] contents = NBT.itemStackArrayFromNBT(nbt);
		nautilus.getInventory().setContents(contents);
		return nautilus;
	}

	@Override
	public JsonElement serialize(AbstractNautilus src) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(WrapperUtils.TYPE, src.getType().name());
		jsonObject.addProperty(WrapperUtils.NAME, src.getCustomName() == null ? "" : src.getCustomName());
		jsonObject.addProperty(WrapperUtils.LOVE_MODE_TICKS, src.getLoveModeTicks());
		jsonObject.addProperty(WrapperUtils.SPEED, src.getAttribute(Attribute.MOVEMENT_SPEED).getValue());
		jsonObject.addProperty(WrapperUtils.MAX_HEALTH, src.getAttribute(Attribute.MAX_HEALTH).getValue());
		jsonObject.addProperty(WrapperUtils.HEALTH, src.getHealth());

		ItemStack[] inventory = src.getInventory().getContents();
		jsonObject.addProperty(WrapperUtils.INVENTORY, NBT.itemStackArrayToNBT(inventory).toString());

		if(src instanceof ZombieNautilus zombieNautilus) {
			NamespacedKey key = ((Keyed) zombieNautilus.getVariant()).getKey();
			jsonObject.addProperty(WrapperUtils.VARIANT, key.getKey());
		}

		return jsonObject;
	}

	@Override
	public boolean isHolderInstance(InventoryHolder holder) {
		try {
			Class<?> nautilusClass = Class.forName("org.bukkit.entity.AbstractNautilus");
			return nautilusClass.isInstance(holder);
		}catch(ClassNotFoundException exception) {
			return false;
		}
	}

	private ZombieNautilus deserializeZombieNautilus(AbstractNautilus abstractNautilus, JsonObject jsonObject) {
		ZombieNautilus zNautilus = (ZombieNautilus) abstractNautilus;
		String variant = EntityWrapper.getOrDefault(jsonObject, WrapperUtils.VARIANT, "temperate").getAsString();
		zNautilus.setVariant(Registry.ZOMBIE_NAUTILUS_VARIANT.get(new NamespacedKey(plugin, variant)));
		return zNautilus;
	}
}