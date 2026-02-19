package de.cuzim1tigaaa.smartsaddle;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

public class EntityData1_21_1 implements EntityData {

	@Override
	public AttributeInstance getMovementSpeed(LivingEntity livingEntity) {
		return livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
	}

	@Override
	public AttributeInstance getMaxHealth(LivingEntity livingEntity) {
		return livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
	}
}