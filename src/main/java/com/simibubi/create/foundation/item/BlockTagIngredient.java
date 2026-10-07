package com.simibubi.create.foundation.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.Create;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/**
 * Fabric equivalent of NeoForge's BlockTagIngredient: matches any item whose
 * block is contained in the given block tag.
 */
public class BlockTagIngredient implements CustomIngredient {

	public static final MapCodec<BlockTagIngredient> CODEC = TagKey.codec(Registries.BLOCK)
		.xmap(BlockTagIngredient::new, ingredient -> ingredient.tag)
		.fieldOf("tag");

	public static final StreamCodec<RegistryFriendlyByteBuf, BlockTagIngredient> STREAM_CODEC =
		StreamCodec.of(
			(buf, ingredient) -> buf.writeResourceLocation(ingredient.tag.location()),
			buf -> new BlockTagIngredient(TagKey.create(Registries.BLOCK, buf.readResourceLocation())));

	public static final CustomIngredientSerializer<BlockTagIngredient> SERIALIZER =
		new CustomIngredientSerializer<>() {
			@Override
			public net.minecraft.resources.ResourceLocation getIdentifier() {
				return Create.asResource("block_tag");
			}

			@Override
			public MapCodec<BlockTagIngredient> getCodec(boolean allowEmpty) {
				return CODEC;
			}

			@Override
			public StreamCodec<RegistryFriendlyByteBuf, BlockTagIngredient> getPacketCodec() {
				return STREAM_CODEC;
			}
		};

	public static Ingredient of(TagKey<Block> tag) {
		return new BlockTagIngredient(tag).toVanilla();
	}

	private final TagKey<Block> tag;

	public BlockTagIngredient(TagKey<Block> tag) {
		this.tag = tag;
	}

	@Override
	public boolean test(ItemStack stack) {
		return stack.getItem() instanceof BlockItem blockItem
			&& blockItem.getBlock().builtInRegistryHolder().is(tag);
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		List<ItemStack> stacks = new ArrayList<>();
		BuiltInRegistries.BLOCK.getTag(tag).ifPresent(holders ->
			holders.forEach(holder -> stacks.add(new ItemStack(holder.value()))));
		return stacks;
	}

	@Override
	public boolean requiresTesting() {
		return true;
	}

	@Override
	public CustomIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof BlockTagIngredient other && Objects.equals(tag, other.tag);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tag);
	}
}
