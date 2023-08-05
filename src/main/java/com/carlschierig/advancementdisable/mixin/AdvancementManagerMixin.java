package com.carlschierig.advancementdisable.mixin;

import com.carlschierig.advancementdisable.Config;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(ServerAdvancementManager.class)
public class AdvancementManagerMixin {

	@Redirect(
		method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementList;add(Ljava/util/Map;)V")
	)
	void preventAdvancementAddition(AdvancementList list, Map<ResourceLocation, Advancement.Builder> map) {
		// TODO: use a set to avoid O(n) lookups
		var disabledMods = Config.INSTANCE.getDisabledMods();
		map.entrySet().removeIf(entry -> disabledMods.contains(entry.getKey().getNamespace()));
		list.add(map);
	}
}
