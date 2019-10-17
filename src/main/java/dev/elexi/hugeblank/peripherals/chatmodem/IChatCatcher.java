package dev.elexi.hugeblank.peripherals.chatmodem;


import net.minecraft.server.network.ServerPlayerEntity;
import java.util.ArrayList;

public interface IChatCatcher {

    int CHAT_MODEM_MAX_RANGE = 128;
    ArrayList<ChatModemState> catcher = new ArrayList<>();

    boolean handleChatEvents(String message, ServerPlayerEntity player);
}
