package com.carlschierig.advancementdisable.mixin;

import com.carlschierig.advancementdisable.AdvancementDisable;
import com.google.gson.JsonElement;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.regex.Pattern;

@Mixin(ServerAdvancementManager.class)
public class AdvancementManagerMixin {

	@Inject(
		method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
		at = @At("HEAD")
	)
	void preventAdvancementAddition(Map<Identifier, JsonElement> preparations, ResourceManager manager, ProfilerFiller profiler, CallbackInfo ci) {
		var disabledMods = AdvancementDisable.DISABLED_MODS;

		var patterns = disabledMods.stream().map(Pattern::compile).toList();

		preparations.entrySet()
			.removeIf((entry) -> patterns.stream().anyMatch(p -> p.matcher(entry.getKey().toString()).matches()));
	}
}
