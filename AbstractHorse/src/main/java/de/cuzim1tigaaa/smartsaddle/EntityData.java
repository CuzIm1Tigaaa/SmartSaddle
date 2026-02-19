package de.cuzim1tigaaa.smartsaddle;

import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

public interface EntityData {

	AttributeInstance getMovementSpeed(LivingEntity livingEntity);

	AttributeInstance getMaxHealth(LivingEntity livingEntity);

}