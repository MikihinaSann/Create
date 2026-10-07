package com.simibubi.create.foundation.mixin.fabric;

import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.fabricators_of_create.porting_lib.extensions.PackRepositoryExtension;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;

// fabric: porting-lib marks PackRepository with PackRepositoryExtension but never
// implements pl$addPackFinder, so AddPackFindersEvent#addRepositorySource always throws.
// Provide the NeoForge-equivalent implementation.
@Mixin(PackRepository.class)
public abstract class PackRepositoryMixin implements PackRepositoryExtension {
	@Shadow
	@Final
	private Set<RepositorySource> sources;

	@Override
	public void pl$addPackFinder(RepositorySource source) {
		sources.add(source);
	}
}
