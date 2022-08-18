package dev.hugeblank.peripherals.chatmodem;

import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dev.hugeblank.api.player.PlayerPeripheral;
import dev.hugeblank.util.LuaPattern;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class ChatPeripheral extends PlayerPeripheral implements IChatCatcher {

    public final boolean creative;
    private final ArrayList<String> captures = new ArrayList<>();
    private boolean open;

    protected ChatPeripheral(boolean creative)
    {
        super();
        this.creative = creative;

        if (!creative) {
            addMethod("say", (comps, context, args)->{
                say(args.getString(0));
                return MethodResult.of(true);
            });
        } else {
            removeMethod("getBoundPlayer");
        }
        addMethod("capture", (computer, context, args) -> {
            String capture = args.getString(0);
            return MethodResult.of(capture(capture));
        });
        addMethod("uncapture",
                (computer, context, args) -> {
            boolean out = uncapture(args.optString(0, null));
            entity.markDirty();
            return MethodResult.of(out);
        }
        );
        addMethod("getCaptures",
                (computer, context, args) -> MethodResult.of((Object) getCaptures())
        );
    }

    @Override
    @NotNull
    public String getType() {
        return "chat_modem";
    }

    public boolean isOpen() { return open;}

    public boolean isCreative() { return creative; }

    private void setOpen( boolean state) {
        if (state == open) return;
        if(state) {
            CATCHERS.add(this);
        } else {
            captures.clear();
            CATCHERS.remove(this);
        }
        open = state;
        scheduleTick();
    }

    public boolean handleChatEvents(String message, ServerPlayerEntity player) {
        boolean out = false;
        if ( !( this.creative || isBound() && player.getUuid().equals( getPlayer().getUuid() ) ) ) return false;
        String username = player.getEntityName();
        String id = player.getUuidAsString();
        String[] captures = getCaptures();
        for (IComputerAccess computer : getComputers()) {
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

    public boolean capture(String capture) {
        if (!isBound() && !creative) return false;
        synchronized (captures) {
            if (!captures.contains(capture)) {
                captures.add(capture);
                setOpen(true);
                entity.markDirty();
            }
        }
        return true;
    }

    public String[] getCaptures() {
        synchronized (captures) {
            return captures.toArray(new String[0]);
        }
    }

    public boolean uncapture(String capture) {
        boolean out = false;
        synchronized (captures) {
            if (capture == null) {
                setOpen(false);
                out = true;
            } else {
                int id = captures.indexOf(capture);
                if (id > -1) {
                    captures.remove(id);
                    out = true;
                }
                if (captures.isEmpty()) setOpen(false);
            }
            scheduleTick();
            return out;
        }
    }

    public void say(String message) {
        World world = entity.getWorld();
        if ( world != null && !world.isClient()) {
            //noinspection ConstantConditions
            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(profile.getId());
            if (player != null) {
                player.sendMessage(new LiteralText(message), false);
            }
        }
    }

    @Override
    public boolean equals(IPeripheral other) {
        return this == other || (other instanceof ChatPeripheral && entity == ((ChatPeripheral) other).entity);
    }

    @Override
    public synchronized void attach(@NotNull IComputerAccess computer) {
        super.attach(computer);
    }

    @Override
    public synchronized void detach(@Nonnull IComputerAccess computer) {
        super.detach(computer);
        synchronized (computers) {
            if (computers.isEmpty()) setOpen(false);
        }
    }
}
