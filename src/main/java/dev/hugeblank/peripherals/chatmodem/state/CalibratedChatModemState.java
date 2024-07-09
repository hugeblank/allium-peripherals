package dev.hugeblank.peripherals.chatmodem.state;

import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dev.hugeblank.peripherals.chatmodem.ChatModemBlockEntity;
import dev.hugeblank.peripherals.chatmodem.Constants;
import dev.hugeblank.peripherals.chatmodem.IChatCatcher;
import dev.hugeblank.peripherals.chatmodem.block.CalibratedChatModemBlock;
import dev.hugeblank.util.BooleanRef;
import dev.hugeblank.util.LuaPattern;
import dev.hugeblank.util.PlayerInfo;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Set;


public class CalibratedChatModemState extends AbstractModemState implements IChatCatcher, IModemState {
    private final Set<String> captures = new ObjectArraySet<>();
    private boolean open;
    @Nullable
    private PlayerInfo playerInfo;

    public CalibratedChatModemState(ChatModemBlockEntity blockEntity) {
        super(blockEntity, Constants.PeripheralMethods.calibratedChatMethdos);
    }


    @Override
    public void onBlockInteraction(PlayerEntity player) {
        if (player.getServer() != null) {
            if (playerInfo == null) {
                playerInfo = new PlayerInfo(player);
                player.sendMessage(Text.literal("Bound modem to " + player.getName().getString()), true);
            } else if (playerInfo.uuid().equals(player.getUuid())) {
                player.sendMessage(Text.literal("Unbound modem from player " + playerInfo.playerName()), true);
                playerInfo = null;
            } else {
                player.sendMessage(Text.literal("Modem currently bound to player " + playerInfo.playerName()), true);
            }
        }
    }


    private void setOpen(boolean state, boolean invokeEvent) {
        if (state) {
            CATCHERS.add(this);
        } else {
            CATCHERS.remove(this);
        }
        this.open = state;
        if (invokeEvent) blockEntity.markDirty();
    }


    public boolean isBound() {
        return this.playerInfo != null;
    }

    public void handleChatEvents(String message, ServerPlayerEntity player, BooleanRef shouldCancel) {

        String username = player.getEntityName();
        String uuid = player.getUuid().toString();
        String[] captures = getCaptures();
        for (IComputerAccess computer : m_computers) {
            computer.queueEvent(Constants.Events.CHAT_MESSAGE, username, message, uuid);
            for (String capture : captures) {
                if (LuaPattern.matches(message, capture)) {
                    computer.queueEvent(Constants.Events.CHAT_CAPTURE, message, capture, username, uuid);
                    shouldCancel.set(true);
                }
            }
        }
    }

    public boolean capture(String capture) {
        boolean added = false;
        synchronized (captures) {
            if (!captures.contains(capture)) {
                captures.add(capture);
                added = true;
            }
        }
        setOpen(true, true);
        return added;
    }

    public String[] getCaptures() {
        synchronized (captures) {
            return captures.toArray(String[]::new);
        }
    }

    public boolean uncapture(String capture) {
        synchronized (captures) {
            if (capture == null) {
                captures.clear();
                setOpen(false, true);
                return true;
            }
            if (captures.remove(capture)) {
                if (captures.isEmpty()) {
                    setOpen(false, true);
                }
                return true;
            }

        }
        return false;
    }

    public void say(String message) {
        World world = blockEntity.getWorld();
        if (world != null && !world.isClient()) {
            //noinspection ConstantConditions
            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(playerInfo.uuid());
            if (player != null) {
                player.sendMessage(Text.literal(message), false);
            }
        }
    }

    @Override
    public boolean matches(ServerPlayerEntity player) {
        PlayerInfo bound = playerInfo;
        return bound != null && bound.uuid().equals(player.getUuid());
    }

    public void detach() {
        setOpen(false, false);
    }

    @Override
    public BlockState updateBlockState(BlockState state) {
        return state.with(CalibratedChatModemBlock.ON, open)
            .with(CalibratedChatModemBlock.PAIRED, isBound());
    }

    @Override
    public void readNbt(NbtCompound nbt, BlockEntity entity) {
        if (nbt.contains("playerInfo") && !isBound()) {
            NbtCompound boundTag = nbt.getCompound("playerInfo");
            if (boundTag.contains("name") && boundTag.contains("uuid")) {
                //noinspection ConstantConditions
                PlayerInfo info = new PlayerInfo(
                    boundTag.getString("name"),
                    NbtHelper.toUuid(boundTag.get("uuid"))
                );
                synchronized (this) {
                    this.playerInfo = info;
                }
            } else {
                throw new IllegalStateException(
                    "Allium Peripherals - Expected playerInfo tag of chat modem at " +
                        entity.getPos() +
                        "to contain name and UUID, got: " +
                        boundTag
                );
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        if (playerInfo != null) {
            NbtCompound boundTag = new NbtCompound();
            boundTag.putString("name", playerInfo.playerName());
            boundTag.put("uuid", NbtHelper.fromUuid(playerInfo.uuid()));
            nbt.put("playerInfo", boundTag);
        }
    }

    public MethodResult getBoundPlayer() {
        PlayerInfo info = playerInfo;
        if (info == null) return MethodResult.of();
        return MethodResult.of(info.playerName(), info.uuid().toString());
    }
}
