package dev.hugeblank.peripherals.chatmodem;


import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Set;

public interface IChatCatcher {

    int CHAT_MODEM_MAX_RANGE = 128;
    Set<ChatModemState> CATCHERS = new HashSet<>();

    boolean handleChatEvents(String message, ServerPlayerEntity player);
}
