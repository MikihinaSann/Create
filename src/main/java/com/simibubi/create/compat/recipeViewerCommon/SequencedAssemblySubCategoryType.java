package com.simibubi.create.compat.recipeViewerCommon;

import java.util.function.Supplier;

import com.simibubi.create.compat.emi.EmiSequencedAssemblySubCategory;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.compat.rei.category.sequencedAssembly.ReiSequencedAssemblySubCategory;

public record SequencedAssemblySubCategoryType(Supplier<Supplier<SequencedAssemblySubCategory>> jei,
											   Supplier<Supplier<ReiSequencedAssemblySubCategory>> rei,
											   Supplier<Supplier<EmiSequencedAssemblySubCategory>> emi) {

	public static final SequencedAssemblySubCategoryType PRESSING = new SequencedAssemblySubCategoryType(
			() -> SequencedAssemblySubCategory.AssemblyPressing::new,
			() -> ReiSequencedAssemblySubCategory.AssemblyPressing::new,
			() -> EmiSequencedAssemblySubCategory.AssemblyPressing::new
	);
	public static final SequencedAssemblySubCategoryType SPOUTING = new SequencedAssemblySubCategoryType(
			() -> SequencedAssemblySubCategory.AssemblySpouting::new,
			() -> ReiSequencedAssemblySubCategory.AssemblySpouting::new,
			() -> EmiSequencedAssemblySubCategory.AssemblySpouting::new
	);
	public static final SequencedAssemblySubCategoryType DEPLOYING = new SequencedAssemblySubCategoryType(
			() -> SequencedAssemblySubCategory.AssemblyDeploying::new,
			() -> ReiSequencedAssemblySubCategory.AssemblyDeploying::new,
			() -> EmiSequencedAssemblySubCategory.AssemblyDeploying::new
	);
	public static final SequencedAssemblySubCategoryType CUTTING = new SequencedAssemblySubCategoryType(
			() -> SequencedAssemblySubCategory.AssemblyCutting::new,
			() -> ReiSequencedAssemblySubCategory.AssemblyCutting::new,
			() -> EmiSequencedAssemblySubCategory.AssemblyCutting::new
	);
}
