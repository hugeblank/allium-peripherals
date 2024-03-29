package dev.hugeblank.mixin;

import dev.hugeblank.Allium;
import dev.hugeblank.peripherals.chatmodem.ChatModemState;
import dev.hugeblank.peripherals.chatmodem.IChatCatcher;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ChatMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onChatMessage(Lnet/minecraft/network/packet/c2s/play/ChatMessageC2SPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;filterText(Ljava/lang/String;Ljava/util/function/Consumer;)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    protected void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        boolean cancel = false;
        if (!player.getEntityWorld().isClient) {
            Allium.debug( "Catchers: " + IChatCatcher.CATCHERS);
            for (ChatModemState modem : IChatCatcher.CATCHERS) {
                if (modem.creative || modem.isBound() && player.getUuid().equals(modem.getBound().uuid())) {
                    boolean c = modem.handleChatEvents(packet.getChatMessage(), player);
                    if (c) cancel = true;
                    Allium.debug("World: " + (player.getEntityWorld().isClient() ? "client" : "server") + ", cancelled: " + (cancel ? "yes" : "no"));
                } else if (!modem.isBound()) { // This should never happen.
                    Allium.debug("Modem " + modem + " is registered as a handler, but has no bound player");
                }
            }
            if (cancel) ci.cancel();
        }
    }
}