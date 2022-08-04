package dev.hugeblank.peripherals.chatmodem;


import net.minecraft.server.network.ServerPlayerEntity;

import java.util.LinkedList;

public interface IChatCatcher {

    int CHAT_MODEM_MAX_RANGE = 128;
    LinkedList<ChatPeripheral> catcher = new LinkedList<>();

    boolean handleChatEvents(String message, ServerPlayerEntity player);
}
