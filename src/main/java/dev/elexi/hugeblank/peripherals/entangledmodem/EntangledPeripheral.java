package dev.elexi.hugeblank.peripherals.entangledmodem;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.generic.methods.InventoryMethods;
import dev.elexi.hugeblank.Allium;
import dev.elexi.hugeblank.api.player.PlayerPeripheral;
import dev.elexi.hugeblank.util.InventoryHelpers;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
        // addMethod("getStatusEffects", (computer, context, args) -> MethodResult.of(getStatusEffects()));
    }

    public PlayerEntity getPlayer() {
        return InventoryHelpers.getPlayer((ServerWorld) entity.getWorld(), profile);
    }

    public InventoryPeripheral getInventory(IComputerAccess computer) {
        if (isBound()) {
            PlayerEntity player = getPlayer();
            if (player instanceof ServerPlayerEntity) {
                if (invCache.containsKey(player)) {
                    return invCache.get(player);
                } else {
                    InventoryPeripheral peripheral = new InventoryPeripheral((ServerPlayerEntity) player, player.inventory, computer);
                    invCache.put(player, peripheral);
                    return peripheral;
                }
            }
        }
        return null;
    }

    public InventoryPeripheral getEnderInventory(IComputerAccess computer) {
        if (isBound()) {
            PlayerEntity player = getPlayer();
            if (player instanceof ServerPlayerEntity) {
                if (enderCache.containsKey(player)) {
                    return enderCache.get(player);
                } else {
                    InventoryPeripheral peripheral = new InventoryPeripheral((ServerPlayerEntity) player, player.getEnderChestInventory(), computer);
                    enderCache.put(player, peripheral);
                    return peripheral;
                }
            }
        }
        return null;
    }

    public ArrayList<Object[]> getStatusEffects() {
        ArrayList<Object[]> out = new ArrayList<>();
        if (isBound() && InventoryHelpers.userOnline(Objects.requireNonNull(entity.getWorld()), profile)) {
            ServerPlayerEntity player = Objects.requireNonNull(entity.getWorld().getServer()).getPlayerManager().getPlayer(profile.getId());
            Collection<StatusEffectInstance> effects = player.getStatusEffects();
            for (StatusEffectInstance effect : effects) {
                Object[] stats = new Object[4];
                Allium.debug(effect.getEffectType().getName().asString());
                stats[0] = effect.getEffectType().getName().asString(); // Effect Name
                stats[1] = effect.getDuration()/20; // Effect duration (seconds)
                stats[2] = effect.getAmplifier()+1; // Amplifier (1 indexed)
                stats[3] = effect.shouldShowParticles(); // Particles enabled
                out.add(stats);
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
            InventoryHelpers.savePlayerInventory(player);
            return out;
        }

        @LuaFunction(mainThread = true)
        public final int pushItems( String toName, int fromSlot, Optional<Integer> limit, Optional<Integer> toSlot) throws LuaException {
            int out = InventoryMethods.pushItems(inventory, computer, toName, fromSlot, limit, toSlot);
            InventoryHelpers.savePlayerInventory(player);
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

        @LuaFunction(mainThread = true)
        public final String name() {
            return InventoryMethods.name(inventory);
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
