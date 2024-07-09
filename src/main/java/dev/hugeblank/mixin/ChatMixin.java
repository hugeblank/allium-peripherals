package dev.hugeblank.mixin;

import dev.hugeblank.Allium;
import dev.hugeblank.peripherals.chatmodem.IChatCatcher;
import dev.hugeblank.peripherals.chatmodem.state.IModemState;
import dev.hugeblank.util.BooleanRef;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ChatMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    protected abstract SignedMessage getSignedMessage(ChatMessageC2SPacket packet);

    @Shadow
    protected abstract boolean canAcceptMessage(SignedMessage packet);

    @Inject(method = "onChatMessage(Lnet/minecraft/network/packet/c2s/play/ChatMessageC2SPacket;)V",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/MinecraftServer;submit(Ljava/lang/Runnable;)Ljava/util/concurrent/CompletableFuture;"

        ), cancellable = true)
    protected void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        if (!player.getEntityWorld().isClient) {
            BooleanRef shouldCancel = new BooleanRef();
            SignedMessage signedMessage = this.getSignedMessage(packet);
            if (!this.canAcceptMessage(signedMessage)) {
                return;
            }
            Allium.debug("Catchers: " + IChatCatcher.CATCHERS);
            for (IModemState modem : IChatCatcher.CATCHERS) {
                if (!modem.matches(player)) continue;
                modem.handleChatEvents(packet.chatMessage(), player, shouldCancel);
                Allium.debug("World: " + (player.getEntityWorld().isClient() ? "client" : "server") + ", cancelled: " + (shouldCancel.value ? "yes" : "no"));
            }
            if (shouldCancel.value) ci.cancel();
        }
    }
}