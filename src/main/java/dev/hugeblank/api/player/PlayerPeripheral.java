package dev.hugeblank.api.player;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.lua.MethodResult;
import dev.hugeblank.api.base.BasePeripheral;
import dev.hugeblank.util.InventoryHelpers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

public abstract class PlayerPeripheral extends BasePeripheral {

    protected PlayerEntity player;
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
            this.player = player;
            return true;
        } else if (matches(player)) {
            player.sendMessage(new LiteralText("Unbound modem from player " + profile.getName()), true);
            this.profile = null;
            this.player = null;
            return false;
        } else {
            player.sendMessage(new LiteralText("Modem currently bound to player " + this.player.getGameProfile().getName()), true);
            return true;
        }
    }

    public boolean bind(GameProfile profile) {
        if (!isBound()) {
            this.profile = profile;
            player = null; //
            return true;
        } else {
            this.profile = null;
            player = null;
            return false;
        }
    }

    public void onWorld(World world) {
        if (!world.isClient() && profile != null) player = InventoryHelpers.getPlayer((ServerWorld)world, profile);
    }

    public void unbind() {
        profile = null;
        player = null;
    }

    boolean matches(PlayerEntity player) {
        return player.equals(player);
    }

    public boolean isBound() {
        return profile != null && player != null;
    }
}
