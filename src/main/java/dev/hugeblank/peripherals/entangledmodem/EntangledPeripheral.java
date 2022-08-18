package dev.hugeblank.peripherals.entangledmodem;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.generic.methods.InventoryMethods;
import dev.hugeblank.api.player.PlayerPeripheral;
import dev.hugeblank.util.PlayerDataHelper;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntangledPeripheral extends PlayerPeripheral {
    boolean creative;
    private static final HashMap<PlayerEntity, InventoryPeripheral> invCache = new HashMap<>();
    private static final HashMap<PlayerEntity, InventoryPeripheral> enderCache = new HashMap<>();

    protected EntangledPeripheral(boolean creative)
    {
        super();
        this.creative = creative;

        addMethod("getInventory", (computer, context, args) -> MethodResult.of(getInventory(computer)));
        addMethod("getEnderInventory", (computer, context, args) -> MethodResult.of(getEnderInventory(computer)));
        addMethod("getStatusEffects", (computer, context, args) -> MethodResult.of(getStatusEffects()));
        addMethod("getAttributes", (computer, context, args) -> MethodResult.of(getAttributes()));
    }

    public InventoryPeripheral getInventory(IComputerAccess computer) {
        if (isBound()) {
            ServerPlayerEntity player = getPlayer();
            if (player != null) {
                if (invCache.containsKey(player)) {
                    return invCache.get(player);
                } else {
                    InventoryPeripheral peripheral = new InventoryPeripheral(player, player.getInventory(), computer);
                    invCache.put(player, peripheral);
                    return peripheral;
                }
            }
        }
        return null;
    }

    public InventoryPeripheral getEnderInventory(IComputerAccess computer) {
        if (isBound()) {
            ServerPlayerEntity player = getPlayer();
            if (player != null) {
                if (enderCache.containsKey(player)) {
                    return enderCache.get(player);
                } else {
                    InventoryPeripheral peripheral = new InventoryPeripheral(player, player.getEnderChestInventory(), computer);
                    enderCache.put(player, peripheral);
                    return peripheral;
                }
            }
        }
        return null;
    }

    public ArrayList<Map<String, Object>> getStatusEffects() {
        ArrayList<Map<String, Object>> out = new ArrayList<>();
        if (isBound() && PlayerDataHelper.userOnline(Objects.requireNonNull(entity.getWorld()), profile)) {
            ServerPlayerEntity player = Objects.requireNonNull(entity.getWorld().getServer()).getPlayerManager().getPlayer(profile.getId());
            Collection<StatusEffectInstance> effects = player.getStatusEffects();
            for (StatusEffectInstance effect : effects) {
                Map<String, Object> stats = new HashMap<>();
                // Effects are registered using a raw ID, we have to interpret the translation key instead.
                stats.put("effect", effect
                        .getEffectType()
                        .getTranslationKey()
                        .replace("effect.", "")
                        .replace(".", ":")
                );
                stats.put("duration", effect.getDuration()/20);
                stats.put("amplifier", effect.getAmplifier()+1);
                stats.put("showParticles", effect.shouldShowParticles());
                out.add(stats);
            }
            return out;
        }
        return null;
    }

    public Map<String, Object> getAttributes() {
        Map<String, Object> out = new HashMap<>();
        ServerPlayerEntity player = getPlayer();
        if (player != null) {
            // Statistical values
            out.put("health", player.getHealth());
            out.put("maxHealth", player.getMaxHealth());
            out.put("breath", player.getAir());
            out.put("maxBreath", player.getMaxAir());
            out.put("hunger", player.getHungerManager().getFoodLevel());
            out.put("saturation", player.getHungerManager().getSaturationLevel());
            out.put("fireTick", player.getFireTicks());
            out.put("movementSpeed", player.getMovementSpeed());
            out.put("experience", player.totalExperience);
            out.put("experienceLevel", player.experienceLevel);
            out.put("nextLevelExperience", player.getNextLevelExperience());


            // Binary actions
            out.put("sneaking", player.isSneaking());
            out.put("sprinting", player.isSprinting());
            out.put("swimming", player.isSwimming());
            out.put("freezing", player.isFrozen());
            out.put("flying", player.isFallFlying());
            out.put("climbing", player.isHoldingOntoLadder());
            out.put("sleeping", player.isSleeping());
            out.put("onGround", player.isOnGround());
            out.put("onFire", player.isOnFire());
            out.put("inWater", player.isSubmergedInWater());
            out.put("inLava", player.isInLava());

            // Position
            out.put("x", player.getX());
            out.put("y", player.getY());
            out.put("z", player.getZ());
            out.put("pitch", player.getPitch());
            out.put("yaw", player.getYaw());
            MinecraftServer server = player.getServer();
            if (server != null) {
                for (RegistryKey<World> worldRegistryKey : server.getWorldRegistryKeys()) {
                    if (Objects.equals(server.getWorld(worldRegistryKey), player.getWorld())) {
                        // If a player reports an issue about the 'world' key not existing, it's upstream.
                        out.put("world", worldRegistryKey.getValue().toString());
                    }
                }
            }
            return out;
        }
        return null;
    }

    @NotNull
    @Override
    public String getType() {
        return "entangled_modem";
    }

    public static class InventoryPeripheral {

        private final ServerPlayerEntity player;
        private final Inventory inventory;
        private final IComputerAccess computer;

        InventoryPeripheral(ServerPlayerEntity player, Inventory inventory, IComputerAccess computer) {
            this.player = player;
            this.inventory = inventory;
            this.computer = computer;
        }

        @LuaFunction(mainThread = true)
        public final int pullItems( String fromName, int fromSlot, Optional<Integer> limit, Optional<Integer> toSlot ) throws LuaException {
            int out = InventoryMethods.pullItems(inventory, computer, fromName, fromSlot, limit, toSlot);
            PlayerDataHelper.savePlayerInventory(player);
            return out;
        }

        @LuaFunction(mainThread = true)
        public final int pushItems( String toName, int fromSlot, Optional<Integer> limit, Optional<Integer> toSlot) throws LuaException {
            int out = InventoryMethods.pushItems(inventory, computer, toName, fromSlot, limit, toSlot);
            PlayerDataHelper.savePlayerInventory(player);
            return out;
        }

        @LuaFunction(mainThread = true)
        public final Map<String, ?> getItemDetail( int slot ) throws LuaException {
            return InventoryMethods.getItemDetail(inventory, slot);
        }

        @LuaFunction( mainThread = true )
        public final Map<Integer, Map<String, ?>> list() {
            return InventoryMethods.list(inventory);
        }

        @LuaFunction( mainThread = true )
        public final int size() {
            return InventoryMethods.size(inventory);
        }
    }

    @Override
    public boolean equals(IPeripheral other) {
        return this == other || (other instanceof EntangledPeripheral && entity == ((EntangledPeripheral) other).entity);
    }
}
