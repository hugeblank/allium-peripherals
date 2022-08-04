package dev.hugeblank.util;

import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class PlayerInfo {

    private final UUID uuid;
    private final String playerName;

    public PlayerInfo(PlayerEntity player) {
        uuid = player.getUuid();
        playerName = player.getName().getString();
    }

    public PlayerInfo(String playerName, UUID uuid) {
        this.playerName = playerName;
        this.uuid = uuid;
    }

    public UUID uuid() {
        return uuid;
    }

    public String playerName() {
        return playerName;
    }
}
