package dev.hugeblank.util;

import com.mojang.authlib.GameProfile;
import dev.hugeblank.mixin.PlayerManagerSaveHandlerAccessor;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class InventoryHelpers {
    private static final HashMap<UUID, ServerPlayerEntity> playerCache = new HashMap<>();

    public static boolean userExists(World world, GameProfile profile) {
        return Objects.requireNonNull(world.getServer()).getUserCache().getByUuid(profile.getId()) != null;
    }

    public static boolean userOnline(World world, GameProfile profile) {
        return Objects.requireNonNull(world.getServer()).getPlayerManager().getPlayer(profile.getId()) != null;
    }

    public static ServerPlayerEntity getPlayer(ServerWorld world, GameProfile profile) {
        ServerPlayerEntity player;
        if (playerCache.containsKey(profile.getId())) {
            player = playerCache.get(profile.getId());
        } else if (userExists(world, profile) && userOnline(world, profile)) {
            player = Objects.requireNonNull(world.getServer()).getPlayerManager().getPlayer(profile.getId());
        } else if (userExists(world, profile)){
            player = new ServerPlayerEntity(world.getServer(), world, profile, new ServerPlayerInteractionManager(world));
            world.getServer().getPlayerManager().loadPlayerData(player);
        } else {
            return null;
        }
        playerCache.put(profile.getId(), player);
        return player;
    }

    public static void savePlayerInventory(ServerPlayerEntity player) {
        // Christ almighty what is this
        ((PlayerManagerSaveHandlerAccessor) player.getServerWorld().getServer().getPlayerManager()).getSaveHandler().savePlayerData(player);
    }
}
