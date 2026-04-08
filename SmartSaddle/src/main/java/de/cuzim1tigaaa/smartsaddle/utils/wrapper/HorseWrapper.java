package de.cuzim1tigaaa.smartsaddle.utils.wrapper;

import com.google.gson.*;
import de.cuzim1tigaaa.smartsaddle.SmartSaddle;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class HorseWrapper implements EntityWrapper<AbstractHorse> {

	private final SmartSaddle plugin;

	public HorseWrapper(SmartSaddle plugin) {
		this.plugin = plugin;
	}

	public AbstractHorse deserialize(String json, Location location) throws JsonParseException {
		JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
		EntityType type = EntityType.valueOf(EntityWrapper.getOrDefault(jsonObject, WrapperUtils.TYPE, EntityType.HORSE).getAsString());

		AbstractHorse horse = (AbstractHorse) location.getWorld().spawnEntity(location, type);
		switch(type) {
			case HORSE -> horse = deserializeHorse(horse, jsonObject);
			case SKELETON_HORSE -> horse = deserializeSkeletonHorse(horse, jsonObject);
			case ZOMBIE_HORSE, DONKEY, MULE, CAMEL, LLAMA, TRADER_LLAMA -> {}
			default -> throw new JsonParseException("Invalid horse type: " + type);
		}

		if(horse instanceof ChestedHorse chestedHorse)
			chestedHorse.setCarryingChest(EntityWrapper.getOrDefault(jsonObject, WrapperUtils.IS_CARRYING_CHEST, false).getAsBoolean());

		horse.setCustomName(EntityWrapper.getOrDefault(jsonObject, WrapperUtils.NAME, "").getAsString());
		horse.setJumpStrength(EntityWrapper.getOrDefault(jsonObject, WrapperUtils.JUMP_STRENGTH, horse.getJumpStrength()).getAsDouble());
		horse.setLoveModeTicks(EntityWrapper.getOrDefault(jsonObject, WrapperUtils.LOVE_MODE_TICKS, horse.getLoveModeTicks()).getAsInt());
		plugin.getEntityData().getMovementSpeed(horse).setBaseValue(
				EntityWrapper.getOrDefault(jsonObject, WrapperUtils.SPEED, plugin.getEntityData().getMovementSpeed(horse)).getAsDouble());
		plugin.getEntityData().getMaxHealth(horse).setBaseValue(
				EntityWrapper.getOrDefault(jsonObject, WrapperUtils.MAX_HEALTH, plugin.getEntityData().getMaxHealth(horse)).getAsDouble());
		horse.setHealth(EntityWrapper.getOrDefault(jsonObject, WrapperUtils.HEALTH, horse.getHealth()).getAsDouble());
		horse.setTamed(true);

		ReadWriteNBT nbt = NBT.parseNBT(
				EntityWrapper.getOrDefault(jsonObject, WrapperUtils.INVENTORY, NBT.itemStackArrayToNBT(new ItemStack[0])).getAsString());
		ItemStack[] contents = NBT.itemStackArrayFromNBT(nbt);
		horse.getInventory().setContents(contents);
		return horse;
	}

	public JsonElement serialize(AbstractHorse src) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(WrapperUtils.TYPE, src.getType().name());
		jsonObject.addProperty(WrapperUtils.NAME, src.getCustomName() == null ? "" : src.getCustomName());
		jsonObject.addProperty(WrapperUtils.JUMP_STRENGTH, src.getJumpStrength());
		jsonObject.addProperty(WrapperUtils.LOVE_MODE_TICKS, src.getLoveModeTicks());
		jsonObject.addProperty(WrapperUtils.SPEED, plugin.getEntityData().getMovementSpeed(src).getBaseValue());
		jsonObject.addProperty(WrapperUtils.MAX_HEALTH, plugin.getEntityData().getMaxHealth(src).getValue());
		jsonObject.addProperty(WrapperUtils.HEALTH, src.getHealth());

		ItemStack[] inventory = src.getInventory().getContents();
		jsonObject.addProperty(WrapperUtils.INVENTORY, NBT.itemStackArrayToNBT(inventory).toString());

		if(src instanceof Horse horse) {
			jsonObject.addProperty(WrapperUtils.STYLE, horse.getStyle().name());
			jsonObject.addProperty(WrapperUtils.COLOR, horse.getColor().name());
		}

		if(src instanceof ChestedHorse chestedHorse)
			jsonObject.addProperty(WrapperUtils.IS_CARRYING_CHEST, chestedHorse.isCarryingChest());

		return jsonObject;
	}

	@Override
	public boolean isHolderInstance(InventoryHolder holder) {
		return holder instanceof AbstractHorse;
	}

	@Override
	public int getSaddleSlot(InventoryHolder holder) {
		return (holder instanceof Llama) ? 1 : 0;
	}

	private Horse deserializeHorse(AbstractHorse abstractHorse, JsonObject jsonObject) {
		Horse horse = (Horse) abstractHorse;

		Horse.Style style = Horse.Style.valueOf(EntityWrapper.getOrDefault(jsonObject, WrapperUtils.STYLE, Horse.Style.NONE).getAsString());
		Horse.Color color = Horse.Color.valueOf(EntityWrapper.getOrDefault(jsonObject, WrapperUtils.COLOR, Horse.Color.WHITE).getAsString());

		horse.setStyle(style);
		horse.setColor(color);
		return horse;
	}

	private SkeletonHorse deserializeSkeletonHorse(AbstractHorse abstractHorse, JsonObject jsonObject) {
		SkeletonHorse sHorse = (SkeletonHorse) abstractHorse;
		sHorse.setTrapped(false);
		return sHorse;
	}
}