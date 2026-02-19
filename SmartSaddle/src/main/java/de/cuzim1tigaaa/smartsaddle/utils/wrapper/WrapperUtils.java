package de.cuzim1tigaaa.smartsaddle.utils.wrapper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.cuzim1tigaaa.smartsaddle.SmartSaddle;
import de.cuzim1tigaaa.smartsaddle.VersionMatcher;
import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class WrapperUtils {

	public static final String TYPE = "type";
	public static final String NAME = "name";
	public static final String JUMP_STRENGTH = "jumpStrength";
	public static final String SPEED = "speed";
	public static final String MAX_HEALTH = "maxHealth";
	public static final String HEALTH = "health";
	public static final String LOVE_MODE_TICKS = "loveModeTicks";

	public static final String IS_CARRYING_CHEST = "isCarryingChest";
	public static final String INVENTORY = "inventory";

	public static final String STYLE = "style";
	public static final String COLOR = "color";
	public static final String VARIANT = "variant";

	@Getter private final Map<EntityWrapper.EntityWrapperType, EntityWrapper<? extends LivingEntity>> wrappers;

	public WrapperUtils(SmartSaddle plugin) {
		wrappers = new EnumMap<>(EntityWrapper.EntityWrapperType.class);
		wrappers.put(EntityWrapper.EntityWrapperType.ABSTRACT_HORSE, new HorseWrapper(plugin));

		if(VersionMatcher.isHappyGhastAvailable())
			wrappers.put(EntityWrapper.EntityWrapperType.HAPPY_GHAST, new HappyGhastWrapper(plugin));

		if(VersionMatcher.isNautilusAvailable())
			wrappers.put(EntityWrapper.EntityWrapperType.ABSTRACT_NAUTILUS, new NautilusWrapper(plugin));
	}

	@SuppressWarnings("unchecked")
	public <T extends LivingEntity> EntityWrapper<T> getWrapper(EntityType entityType) {
		EntityWrapper.EntityWrapperType wrapperType = wrapperTypeFor(entityType);
		EntityWrapper<? extends LivingEntity> wrapper = Objects.requireNonNull(
				wrappers.get(wrapperType),
				() -> "No wrapper registered for " + wrapperType
		);
		return (EntityWrapper<T>) wrapper;
	}

	private EntityWrapper.EntityWrapperType wrapperTypeFor(EntityType entityType) {
		return switch (entityType) {
			case NAUTILUS, ZOMBIE_NAUTILUS -> EntityWrapper.EntityWrapperType.ABSTRACT_NAUTILUS;
			case HAPPY_GHAST -> EntityWrapper.EntityWrapperType.HAPPY_GHAST;
			default -> EntityWrapper.EntityWrapperType.ABSTRACT_HORSE;
		};
	}

	public EntityType getEntityType(String json) {
		JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
		return EntityType.valueOf(EntityWrapper.getOrDefault(jsonObject, "type", EntityType.HORSE).getAsString());
	}
}