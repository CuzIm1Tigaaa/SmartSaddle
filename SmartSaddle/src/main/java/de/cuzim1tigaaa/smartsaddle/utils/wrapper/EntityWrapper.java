package de.cuzim1tigaaa.smartsaddle.utils.wrapper;

import com.google.gson.*;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.InventoryHolder;

public interface EntityWrapper<T extends LivingEntity> {

	enum EntityWrapperType {
		ABSTRACT_HORSE,
		ABSTRACT_NAUTILUS,
		HAPPY_GHAST,
	}

	T deserialize(String json, Location location);

	JsonElement serialize(T entity);

	boolean isHolderInstance(InventoryHolder holder);

	default int getSaddleSlot(InventoryHolder holder) {
		return 0;
	}

	static JsonElement getOrDefault(JsonObject jsonObject, String key, Object defaultValue) {
		return jsonObject.has(key) ? jsonObject.get(key) : new Gson().toJsonTree(defaultValue);
	}
}