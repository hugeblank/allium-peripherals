package dev.hugeblank.util;

import com.mojang.authlib.GameProfile;
import dev.hugeblank.Allium;
import dev.hugeblank.mixin.PlayerManagerSaveHandlerAccessor;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PlayerDataHelper {
    public static boolean MIXIN_CANCEL_MOVETOSPAWN = false;

    private static final HashMap<UUID, ServerPlayerEntity> playerCache = new HashMap<>();

    public static boolean userExists(World world, GameProfile profile) {
        return Objects.requireNonNull(world.getServer()).getUserCache().getByUuid(profile.getId()).isPresent();
    }

    public static boolean userOnline(World world, GameProfile profile) {
        return Objects.requireNonNull(world.getServer()).getPlayerManager().getPlayer(profile.getId()) != null;
    }

    public static ServerPlayerEntity getPlayer(ServerWorld world, GameProfile profile) {
        ServerPlayerEntity player;
        if (userExists(world, profile) && userOnline(world, profile)) {
            // Prefer getting an online player
            player = Objects.requireNonNull(world.getServer()).getPlayerManager().getPlayer(profile.getId());
            playerCache.remove(profile.getId());
            Allium.debug("Real player");
        } else if (playerCache.containsKey(profile.getId())) {
            // Otherwise use the cache
            player = playerCache.get(profile.getId());
            Allium.debug("From cache");
        } else if (userExists(world, profile)){
            // Otherwise create the fake player. Yuck.
            MIXIN_CANCEL_MOVETOSPAWN = true;
            player = new ServerPlayerEntity(world.getServer(), world, profile);
            MIXIN_CANCEL_MOVETOSPAWN = false;
            world.getServer().getPlayerManager().loadPlayerData(player);
            playerCache.put(profile.getId(), player);
            Allium.debug("Fake player");
        } else {
            Allium.debug("Does not exist");
            return null;
        }
        return player;
    }

    public static void savePlayerInventory(ServerPlayerEntity player) {
        // Christ almighty what is this
        if (player.getServer() == null) return;
        ((PlayerManagerSaveHandlerAccessor) player.getServer().getPlayerManager())
                .getSaveHandler()
                .savePlayerData(player);
    }
}
