package com.simibubi.create.foundation.utility.fabric;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class ReachUtil {

	public static double reach(Player player) {
		AttributeInstance attribute = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
		return attribute == null ? 4.5D : attribute.getValue();
	}
}
