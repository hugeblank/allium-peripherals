package dev.hugeblank.peripherals.chatmodem;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dev.hugeblank.util.LuaPattern;
import dev.hugeblank.util.PlayerInfo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Set;


public class ChatModemState implements IChatCatcher {

    private final ArrayList<String> captures = new ArrayList<>();
    private Set<IComputerAccess> m_computers;
    private final ChatModemBlockEntity blockEntity;
    private boolean open;
    private PlayerInfo playerInfo;
    public final boolean creative;


    public ChatModemState(ChatModemBlockEntity blockEntity, boolean creative) {

        this.blockEntity = blockEntity;
        this.creative = creative;
    }

    public boolean isOpen() { return open;}

    private void setOpen( boolean state) {
        if(state && !CATCHERS.contains(this)) {
            CATCHERS.add(this);
        } else if (!state){
            CATCHERS.remove(this);
        }
        this.open = state;
        blockEntity.markDirty();
    }

    public synchronized void setBound(PlayerInfo info) {
        this.playerInfo = info;
    }

    public @Nullable PlayerInfo getBound() {
        return playerInfo;
    }

    public void setComputers(Set<IComputerAccess> m_computers) {
        this.m_computers = m_computers;
    }

    public boolean isBound() {
        return this.playerInfo != null;
    }

    public boolean handleChatEvents(String message, ServerPlayerEntity player) {
        boolean out = false;
        String username = player.getEntityName();
        String id = player.getUuid().toString();
        String[] captures = getCaptures();
        for (IComputerAccess computer : m_computers) {
            computer.queueEvent("chat_message", username, message, id);
            for (String capture : captures) {
                if (LuaPattern.matches(message, capture)) {
                    computer.queueEvent("chat_capture", message, capture, username, id);
                    out = true;
                }
            }
        }
        return out;
    }

    public void capture(String capture) {
        synchronized (captures) {
            if (!captures.contains(capture)) {
                captures.add(capture);
            }
        }
        setOpen(true);
    }

    public String[] getCaptures() {
        synchronized (captures) {
            String[] captureList = new String[captures.size()];
            for (int i = 0; i < captures.size(); i++) {
                captureList[i] = captures.get(i);
            }
            return captureList;
        }
    }

    public boolean uncapture(String capture) {
        boolean out = false;
        synchronized (captures) {
            if (capture == null) {
                captures.clear();
                out = true;
            } else {
                int id = captures.indexOf(capture);
                if (id > -1) {
                    captures.remove(id);
                    out = true;
                }
            }
            if (captures.isEmpty()) {
                CATCHERS.remove(this);
                setOpen(false);
            }
            return out;
        }
    }

    public void say(String message) {
        World world = blockEntity.getWorld();
        if ( world != null && !world.isClient()) {
            //noinspection ConstantConditions
            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(playerInfo.uuid());
            if (player != null) {
                player.sendMessage(new LiteralText(message), false);
            }
        }
    }
}
