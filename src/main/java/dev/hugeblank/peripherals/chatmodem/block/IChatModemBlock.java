package dev.hugeblank.peripherals.chatmodem.block;

import dev.hugeblank.peripherals.chatmodem.ChatModemBlockEntity;
import dev.hugeblank.peripherals.chatmodem.state.IModemState;

public interface IChatModemBlock {
    IModemState createState(ChatModemBlockEntity entity);
}
