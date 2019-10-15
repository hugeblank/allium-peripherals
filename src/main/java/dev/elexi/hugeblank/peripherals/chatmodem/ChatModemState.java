package dev.elexi.hugeblank.peripherals.chatmodem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import java.util.ArrayList;
import java.util.UUID;


public class ChatModemState {

    private ArrayList<String> captures = new ArrayList<>();
    private ChatModemBlockEntity blockEntity;
    private boolean open;
    private boolean bound;


    public ChatModemState(ChatModemBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    public boolean isOpen() { return open;}

    private void setOpen( boolean state) {
        if(state == this.open) return;
        this.open = state;
        blockEntity.markDirty();
    }

    public synchronized void setBound(boolean state) {
        if(state == this.bound) return;
        this.bound = state;
        blockEntity.markDirty();
    }

    public boolean isBound() {
        return this.bound;
    }

    public void capture(String capture) {
        synchronized (captures) {
            boolean exists = false;
            for (int i = 0; i < captures.size(); i++) {
                if (captures.get(i).equals(capture)) {
                    exists = true;
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
            System.out.println("Parameter: " + capture);
            if (capture == null) {
                System.out.println("Clearing all");
                for (int i = 0; i < captures.size(); i++) {
                    captures.remove(i);
                    out = true;
                }
            } else {
                System.out.println("Clearing out one");
                for (int i = 0; i < captures.size(); i++) {
                    if (capture.equals(captures.get(i))) {
                        captures.remove(i);
                    }
                    out = true;
                }
            }
            if (captures.isEmpty()) setOpen(false);
            return out;
        }
    }

    public boolean say(String uuid, String message) {
        PlayerEntity player = blockEntity.getWorld().getPlayerByUuid( UUID.fromString(uuid) );
        if (player != null) {
            player.addChatMessage(new LiteralText(message), false);
            //player.sendMessage(new LiteralText(message));
        }
        return false;
    }
}
