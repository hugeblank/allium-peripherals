package dev.hugeblank.api.player;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.lua.MethodResult;
import dev.hugeblank.api.base.BasePeripheral;
import dev.hugeblank.util.PlayerDataHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class PlayerPeripheral extends BasePeripheral {

    protected final AtomicBoolean changed = new AtomicBoolean();

    protected GameProfile profile;

    public PlayerPeripheral() {
        super();
        addMethod("getBoundPlayer", (comps, context, args) -> {
            if (isBound()) {
                return MethodResult.of(profile.getName(), profile.getId().toString());
            } else {
                return MethodResult.of();
            }
        });
    }

    // TODO - Change from LiteralText to Translatable Text
    public boolean bind(PlayerEntity player) {
        // Boolean return determines whether the block stay paired or not after the interaction
        if (!isBound()) {
            player.sendMessage(new LiteralText("Bound modem to " + player.getName().getString()), true);
            this.profile = player.getGameProfile();
            if (!changed.getAndSet(true)) scheduleTick();
            return true;
        } else if (matches(player)) {
            player.sendMessage(new LiteralText("Unbound modem from player " + profile.getName()), true);
            profile = null;
            if (!changed.getAndSet(true)) scheduleTick();
            return false;
        } else {
            player.sendMessage(new LiteralText("Modem currently bound to player " + getPlayer().getGameProfile().getName()), true);
            return true;
        }
    }

    public void bind(GameProfile profile) {
        if (!isBound()) {
            this.profile = profile;
        } else {
            this.profile = null;
        }
        if (!changed.getAndSet(true)) scheduleTick();
    }

    protected ServerPlayerEntity getPlayer() {
        return PlayerDataHelper.getPlayer((ServerWorld) entity.getWorld(), profile);
    }

    boolean matches(PlayerEntity player) {
        return getPlayer().equals(player);
    }

    public boolean isBound() {
        return profile != null;
    }
}
