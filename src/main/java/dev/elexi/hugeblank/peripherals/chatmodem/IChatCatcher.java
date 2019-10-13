package dev.elexi.hugeblank.peripherals.chatmodem;


import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public interface IChatCatcher {

    int CHAT_MODEM_MAX_RANGE = 128;
    ArrayList<ChatPeripheral> catcher = new ArrayList<>();

    @Nonnull
    World getWorld();

    @Nonnull
    Vec3d getPosition();

    boolean handleChatEvents(String message, ServerPlayerEntity player);
}
