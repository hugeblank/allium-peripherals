package dev.hugeblank.peripherals.chatmodem.state;

import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dev.hugeblank.peripherals.chatmodem.ChatModemBlockEntity;
import dev.hugeblank.peripherals.chatmodem.Constants;
import dev.hugeblank.peripherals.chatmodem.IChatCatcher;
import dev.hugeblank.peripherals.chatmodem.block.RangedChatModemBlock;
import dev.hugeblank.util.BooleanRef;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


public class RangedChatModemState extends AbstractModemState implements IChatCatcher, IModemState {
    @Nullable
    public final Box maxListenRange;
    @Nullable
    public final Box maxSendRange;
    @Nullable
    public Box listenRange;
    public boolean isEnabled;
    @Nullable
    private Box sendRange;

    public RangedChatModemState(ChatModemBlockEntity blockEntity, @Nullable Box maxListenRange, @Nullable Box maxSendRange) {
        super(blockEntity, Constants.PeripheralMethods.rangedChatMethods);
        this.maxListenRange = maxListenRange;
        this.listenRange = maxListenRange;
        this.maxSendRange = maxSendRange;
        this.sendRange = maxSendRange;
    }

    @NotNull
    private static MethodResult rangeResult(Box range) {
        if (range == null) return MethodResult.of(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        return MethodResult.of(range.getXLength(), range.getYLength(), range.getZLength());
    }

    private static boolean checkMax(int x, int y, int z, Box max) {
        if (max != null) {
            if (x < 0 || x > max.getXLength() ||
                y < 0 || y > max.getYLength() ||
                z < 0 || z > max.getZLength()
            ) return true;
        }
        return false;
    }

    @Override
    public void onBlockInteraction(PlayerEntity player) {
        setEnabled(!isEnabled, true);
    }

    private void setEnabled(boolean enabled, boolean invokeEvent) {
        if (enabled == isEnabled) return;
        if (enabled) {
            CATCHERS.add(this);
        } else {
            CATCHERS.remove(this);
        }
        isEnabled = enabled;
        if (invokeEvent) blockEntity.markDirty();
    }

    @Override
    public BlockState updateBlockState(BlockState state) {
        return state.with(RangedChatModemBlock.ON, isEnabled);
    }

    public void handleChatEvents(String message, ServerPlayerEntity player, BooleanRef shouldCancel) {
        String username = player.getEntityName();
        String uuid = player.getUuid().toString();
        for (IComputerAccess computer : m_computers) {
            computer.queueEvent(Constants.Events.CHAT_MESSAGE, username, message, uuid);
        }
    }


    public MethodResult getSendRange() {
        return rangeResult(sendRange);
    }

    public MethodResult getMaxSendRange() {
        return rangeResult(maxSendRange);
    }

    public MethodResult getListenRange() {
        return rangeResult(listenRange);
    }

    public MethodResult getMaxListenRange() {
        return rangeResult(maxListenRange);
    }

    public boolean setListenRange(int x, int y, int z) {
        if (checkMax(x, y, z, maxListenRange)) return false;
        listenRange = Box.of(Vec3d.ZERO, x, y, z)
            .offset(blockEntity.getPos());
        return true;
    }

    public boolean setSendRange(int x, int y, int z) {
        if (checkMax(x, y, z, maxSendRange)) return false;
        sendRange = Box.of(Vec3d.ZERO, x, y, z)
            .offset(blockEntity.getPos());
        return true;
    }

    public void say(String message) {
        World world = blockEntity.getWorld();
        if (world != null && !world.isClient()) {
            MutableText literal = Text.literal(message);
            if (sendRange == null) {
                for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
                    player.sendMessage(literal, false);
                }
            } else {
                for (Entity entity : world.getOtherEntities(null, sendRange)) {
                    if (entity instanceof ServerPlayerEntity player) {
                        player.sendMessage(literal, false);
                    }
                }
            }
        }
    }

    @Override
    public boolean matches(ServerPlayerEntity player) {
        if (listenRange == null) return true;
        return listenRange.contains(player.getPos());
    }

    @Override
    public void readNbt(NbtCompound nbt, BlockEntity entity) {
        isEnabled = nbt.contains("enabled") && nbt.getBoolean("enabled");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("enabled", isEnabled);
    }


    @Override
    public void detach() {
        setEnabled(false, false);
    }
}
