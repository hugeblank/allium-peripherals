package dev.elexi.hugeblank.peripherals.chatmodem;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dev.elexi.hugeblank.util.LuaPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class ChatModemState implements IChatCatcher {

    private ArrayList<String> captures = new ArrayList<>();
    private Set<IComputerAccess> m_computers;
    private ChatModemBlockEntity blockEntity;
    private boolean open;
    private String boundid;
    public final boolean creative;


    public ChatModemState(ChatModemBlockEntity blockEntity, boolean creative) {

        this.blockEntity = blockEntity;
        this.creative = creative;
    }

    public boolean isOpen() { return open;}

    private void setOpen( boolean state) {
        if(state && !catcher.contains(this)) {
            catcher.add(this);
        } else if (!state){
            catcher.remove(this);
        }
        this.open = state;
        blockEntity.markDirty();
    }

    public synchronized void setBound(String uuid) {
        this.boundid = uuid;
        if(uuid != null) {
            catcher.add(this);
        } else {
            catcher.remove(this);
        }
        blockEntity.markDirty();
    }
    public String getBound() {
        return boundid;
    }

    public void setComputers(Set<IComputerAccess> m_computers) {
        this.m_computers = m_computers;
    }

    public boolean isBound() {
        return this.boundid != null;
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
            boolean exists = false;
            for (String s : captures) {
                if (s.equals(capture)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
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
                for (int i = 0; i < captures.size(); i++) {
                    captures.remove(i);
                }
                out = true;
            } else {
                for (int i = 0; i < captures.size(); i++) {
                    if (capture.equals(captures.get(i))) {
                        captures.remove(i);
                    }
                    out = true;
                }
            }
            if (captures.isEmpty()) catcher.remove(this);
            return out;
        }
    }

    public void say(String message) {
        PlayerEntity player = blockEntity.getWorld().getPlayerByUuid( UUID.fromString(boundid) );
        if (player != null) {
            player.sendMessage(Text.literal(message), false);
        }
    }
}
