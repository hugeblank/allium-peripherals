package dev.hugeblank.mixin;

import dev.hugeblank.util.InventoryHelpers;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// cursed hugeblank mixin designed to get away with creating fake players isn't real. It can't hurt you
// cursed hugeblank mixin:
@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(at = @At("HEAD"), method = "moveToSpawn(Lnet/minecraft/server/world/ServerWorld;)V", cancellable = true)
    private void blockMove(ServerWorld world, CallbackInfo ci) {
        // You seriously let this run on your hardware? Fr?
        if (InventoryHelpers.MIXIN_CANCEL_MOVETOSPAWN) ci.cancel();
    }
}
// This is technically a constructor mixin with how I'm using it. I hate it I hate it I hate it I hate it


