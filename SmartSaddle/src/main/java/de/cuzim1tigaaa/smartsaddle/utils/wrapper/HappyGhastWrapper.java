package de.cuzim1tigaaa.smartsaddle.utils.wrapper;

import com.google.gson.*;
import de.cuzim1tigaaa.smartsaddle.SmartSaddle;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HappyGhast;
import org.bukkit.inventory.*;

public class HappyGhastWrapper implements EntityWrapper<HappyGhast> {

	private final SmartSaddle plugin;

	public HappyGhastWrapper(SmartSaddle plugin) {
		this.plugin = plugin;
	}

	@Override
	public HappyGhast deserialize(String json, Location location) {
		JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
		EntityType type = EntityType.HAPPY_GHAST;

		HappyGhast happyGhast = (HappyGhast) location.getWorld().spawnEntity(location, type);

		happyGhast.setCustomName(EntityWrapper.getOrDefault(jsonObject, WrapperUtils.NAME, "").getAsString());
		happyGhast.setLoveModeTicks(EntityWrapper.getOrDefault(jsonObject, WrapperUtils.LOVE_MODE_TICKS, happyGhast.getLoveModeTicks()).getAsInt());
		plugin.getEntityData().getMovementSpeed(happyGhast).setBaseValue(
				EntityWrapper.getOrDefault(jsonObject, WrapperUtils.SPEED, plugin.getEntityData().getMovementSpeed(happyGhast)).getAsDouble());
		plugin.getEntityData().getMaxHealth(happyGhast).setBaseValue(
				EntityWrapper.getOrDefault(jsonObject, WrapperUtils.MAX_HEALTH, plugin.getEntityData().getMaxHealth(happyGhast)).getAsDouble());
		happyGhast.setHealth(EntityWrapper.getOrDefault(jsonObject, WrapperUtils.HEALTH, happyGhast.getHealth()).getAsDouble());

		ReadWriteNBT nbt = NBT.parseNBT(
				EntityWrapper.getOrDefault(jsonObject, WrapperUtils.INVENTORY, "").getAsString());
		ItemStack item = NBT.itemStackFromNBT(nbt);
		happyGhast.getEquipment().setItem(EquipmentSlot.BODY, item);

		return happyGhast;
	}

	@Override
	public JsonElement serialize(HappyGhast src) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(WrapperUtils.TYPE, src.getType().name());
		jsonObject.addProperty(WrapperUtils.NAME, src.getCustomName() == null ? "" : src.getCustomName());
		jsonObject.addProperty(WrapperUtils.LOVE_MODE_TICKS, src.getLoveModeTicks());
		jsonObject.addProperty(WrapperUtils.SPEED, src.getAttribute(Attribute.MOVEMENT_SPEED).getValue());
		jsonObject.addProperty(WrapperUtils.MAX_HEALTH, src.getAttribute(Attribute.MAX_HEALTH).getValue());
		jsonObject.addProperty(WrapperUtils.HEALTH, src.getHealth());

		ItemStack harness = src.getEquipment().getItem(EquipmentSlot.BODY);
		jsonObject.addProperty(WrapperUtils.INVENTORY, NBT.itemStackToNBT(harness).toString());

		return jsonObject;
	}

	@Override
	public boolean isHolderInstance(InventoryHolder holder) {
		return false;
	}

	@Override
	public int getSaddleSlot(InventoryHolder holder) {
		return -1;
	}
}