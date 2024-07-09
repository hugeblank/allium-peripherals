package dev.hugeblank.peripherals.chatmodem.state;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dev.hugeblank.peripherals.chatmodem.ChatModemBlockEntity;
import dev.hugeblank.peripherals.chatmodem.PeripheralMethod;

public abstract class AbstractModemState implements IModemState {

    protected Iterable<IComputerAccess> m_computers;
    protected final ChatModemBlockEntity blockEntity;
    protected final PeripheralMethod<?>[] peripheralMethods;

    @Override
    public PeripheralMethod<?>[] peripheralMethods() {
        return peripheralMethods;
    }

    protected AbstractModemState(ChatModemBlockEntity blockEntity, PeripheralMethod<?>[] peripheralMethods) {
        this.blockEntity = blockEntity;
        this.peripheralMethods = peripheralMethods;
    }

    @Override
    public void setComputers(Iterable<IComputerAccess> m_computers) {
        this.m_computers = m_computers;
    }
}
