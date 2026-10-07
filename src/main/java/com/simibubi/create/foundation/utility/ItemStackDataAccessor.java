package com.simibubi.create.foundation.utility;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;

// duck interface applied to ItemStackComponentizationFix$ItemStackData by ItemStackDataMixin
public interface ItemStackDataAccessor {
	boolean is(String item);

	boolean is(java.util.Set<String> items);

	OptionalDynamic<?> removeTag(String key);

	void setComponent(String key, Dynamic<?> dynamic);

	void moveTagToComponent(String from, String to);
}
