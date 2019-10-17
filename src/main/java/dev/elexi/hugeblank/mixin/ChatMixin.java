package dev.elexi.hugeblank.mixin;

import dev.elexi.hugeblank.peripherals.chatmodem.ChatModemState;
import dev.elexi.hugeblank.peripherals.chatmodem.IChatCatcher;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.ChatMessageC2SPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ChatMixin {

    @Shadow
    private ServerPlayerEntity player;

    @Inject(method = "onChatMessage", at = @At(value = "INVOKE", target = "net/minecraft/server/PlayerManager.broadcastChatMessage(Lnet/minecraft/text/Text;Z)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    protected void onChatMessage(ChatMessageC2SPacket chatMessageC2SPacket_1, CallbackInfo ci, String message, Text text_1) {
        boolean cancel = false;
        if (!player.getEntityWorld().isClient) {
            for (int i = 0; i < IChatCatcher.catcher.size(); i++) {
                ChatModemState modem = IChatCatcher.catcher.get(i);
                if (player.getUuidAsString().equals(modem.getBound()) || modem.creative) {
                    cancel = modem.handleChatEvents(message, player);
                    System.out.println("World: " + (player.getEntityWorld().isClient() ? "client" : "server") + ", cancelled: " + (cancel ? "yes" : "no"));
                }
            }
            if (cancel) ci.cancel();
        }
    }
}