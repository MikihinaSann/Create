package com.simibubi.create.api.contraption.storage.item;

import com.google.common.collect.ImmutableMap;
import com.simibubi.create.foundation.item.CombinedSlottedStackStorage;

import net.minecraft.core.BlockPos;

/**
 * Wrapper around many MountedItemStorages, providing access to all of them as one storage.
 * They can still be accessed individually through the map.
 * 
 * Uses O(1) lookup arrays instead of O(n) linear scan.
 */
public class MountedItemStorageWrapper extends CombinedSlottedStackStorage<MountedItemStorage> {
	public final ImmutableMap<BlockPos, MountedItemStorage> storages;
	
	// Lookup arrays
	private final int[] slotToStorage;   // Maps each slot to its storage index
	private final int[] slotOffsets;     // Starting slot for each storage

	public MountedItemStorageWrapper(ImmutableMap<BlockPos, MountedItemStorage> storages) {
		super(storages.values().stream().toList());
		this.storages = storages;
		
		// Build lookup arrays
		int totalSlots = getSlotCount();
		this.slotToStorage = new int[totalSlots];
		this.slotOffsets = new int[parts.size()];
		
		int currentSlot = 0;
		for (int storageIdx = 0; storageIdx < parts.size(); storageIdx++) {
			slotOffsets[storageIdx] = currentSlot;
			int slotsInStorage = parts.get(storageIdx).getSlotCount();
			
			for (int i = 0; i < slotsInStorage; i++) {
				slotToStorage[currentSlot + i] = storageIdx;
			}
			
			currentSlot += slotsInStorage;
		}
	}
}
