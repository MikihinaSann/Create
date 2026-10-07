package net.fabricmc.fabric.api.item.v1;

import net.minecraft.world.item.Item;

// Compatibility stub: FabricItemSettings was removed from Fabric API but milk-lib's
// bytecode still invokes it (recipeRemainder / maxCount with covariant returns).
// Providing it here lets milk-lib initialize on 1.21.1 without forking the mod.
public class FabricItemSettings extends Item.Properties {
	public FabricItemSettings recipeRemainder(Item item) {
		return (FabricItemSettings) this.craftRemainder(item);
	}

	public FabricItemSettings maxCount(int count) {
		return (FabricItemSettings) this.stacksTo(count);
	}
}
