package dev.hugeblank.peripherals.chatmodem;


import dev.hugeblank.peripherals.chatmodem.state.IModemState;
import dev.hugeblank.util.BooleanRef;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Set;

public interface IChatCatcher {

    int CHAT_MODEM_MAX_RANGE = 128;
    Set<IModemState> CATCHERS = new HashSet<>();

    void handleChatEvents(String message, ServerPlayerEntity player, BooleanRef shouldCancel);
}
