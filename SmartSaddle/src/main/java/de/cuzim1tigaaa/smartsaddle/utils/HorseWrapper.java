package de.cuzim1tigaaa.smartsaddle.utils;

import com.google.gson.*;
import de.cuzim1tigaaa.smartsaddle.SmartSaddle;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

public class HorseWrapper {

	private static final String TYPE = "type";
	private static final String NAME = "name";
	private static final String JUMP_STRENGTH = "jumpStrength";
	private static final String SPEED = "speed";
	private static final String MAX_HEALTH = "maxHealth";
	private static final String HEALTH = "health";
	private static final String LOVE_MODE_TICKS = "loveModeTicks";

	private static final String IS_CARRYING_CHEST = "isCarryingChest";
	private static final String INVENTORY = "inventory";

	private static final String STYLE = "style";
	private static final String COLOR = "color";

	private final SmartSaddle plugin;

	public HorseWrapper(SmartSaddle plugin) {
		this.plugin = plugin;
	}

	public AbstractHorse deserialize(String json, Location location) throws JsonParseException {
		JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
		EntityType type = EntityType.valueOf(getOrDefault(jsonObject, TYPE, EntityType.HORSE).getAsString());

		AbstractHorse h = (AbstractHorse) location.getWorld().spawnEntity(location, type);
		switch(type) {
			case HORSE -> h = deserializeHorse(h, jsonObject);
			case SKELETON_HORSE -> h = deserializeSkeletonHorse(h, jsonObject);
			case ZOMBIE_HORSE, DONKEY, MULE, CAMEL -> {}
			default -> throw new JsonParseException("Invalid horse type: " + type);
		}

		if(h instanceof ChestedHorse chestedHorse)
			chestedHorse.setCarryingChest(getOrDefault(jsonObject, IS_CARRYING_CHEST, false).getAsBoolean());

		h.setCustomName(getOrDefault(jsonObject, NAME, "").getAsString());
		h.setJumpStrength(getOrDefault(jsonObject, JUMP_STRENGTH, h.getJumpStrength()).getAsDouble());
		h.setLoveModeTicks(getOrDefault(jsonObject, LOVE_MODE_TICKS, h.getLoveModeTicks()).getAsInt());
		plugin.getHorseData().getMovementSpeed(h).setBaseValue(
				getOrDefault(jsonObject, SPEED, plugin.getHorseData().getMovementSpeed(h)).getAsDouble());
		plugin.getHorseData().getMaxHealth(h).setBaseValue(
				getOrDefault(jsonObject, MAX_HEALTH, plugin.getHorseData().getMaxHealth(h)).getAsDouble());
		h.setHealth(getOrDefault(jsonObject, HEALTH, h.getHealth()).getAsDouble());
		h.setTamed(true);

		ReadWriteNBT nbt = NBT.parseNBT(
				getOrDefault(jsonObject, INVENTORY, NBT.itemStackArrayToNBT(new ItemStack[0])).getAsString());
		ItemStack[] contents = NBT.itemStackArrayFromNBT(nbt);
		h.getInventory().setContents(contents);
		return h;
	}

	public JsonElement serialize(AbstractHorse src) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(TYPE, src.getType().name());
		jsonObject.addProperty(NAME, src.getCustomName() == null ? "" : src.getCustomName());
		jsonObject.addProperty(JUMP_STRENGTH, src.getJumpStrength());
		jsonObject.addProperty(LOVE_MODE_TICKS, src.getLoveModeTicks());
		jsonObject.addProperty(SPEED, plugin.getHorseData().getMovementSpeed(src).getValue());
		jsonObject.addProperty(MAX_HEALTH, plugin.getHorseData().getMaxHealth(src).getValue());
		jsonObject.addProperty(HEALTH, src.getHealth());

		ItemStack[] inventory = src.getInventory().getContents();
		jsonObject.addProperty(INVENTORY, NBT.itemStackArrayToNBT(inventory).toString());

		if(src instanceof Horse horse) {
			jsonObject.addProperty(STYLE, horse.getStyle().name());
			jsonObject.addProperty(COLOR, horse.getColor().name());
		}

		if(src instanceof ChestedHorse chestedHorse)
			jsonObject.addProperty(IS_CARRYING_CHEST, chestedHorse.isCarryingChest());

		return jsonObject;
	}

	private Horse deserializeHorse(AbstractHorse abstractHorse, JsonObject jsonObject) {
		Horse horse = (Horse) abstractHorse;

		Horse.Style style = Horse.Style.valueOf(getOrDefault(jsonObject, STYLE, Horse.Style.NONE).getAsString());
		Horse.Color color = Horse.Color.valueOf(getOrDefault(jsonObject, COLOR, Horse.Color.WHITE).getAsString());

		horse.setStyle(style);
		horse.setColor(color);
		return horse;
	}

	private SkeletonHorse deserializeSkeletonHorse(AbstractHorse abstractHorse, JsonObject jsonObject) {
		SkeletonHorse sHorse = (SkeletonHorse) abstractHorse;
		sHorse.setTrapped(false);
		return sHorse;
	}

	private JsonElement getOrDefault(JsonObject jsonObject, String key, Object defaultValue) {
		return jsonObject.has(key) ? jsonObject.get(key) : new Gson().toJsonTree(defaultValue);
	}
}