package dev.hugeblank.api.player;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.peripheral.IPeripheralTile;
import dev.hugeblank.api.base.BaseModemBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public abstract class PlayerModemBlockEntity<T extends PlayerPeripheral> extends BaseModemBlockEntity<T> implements IPeripheralTile {

    public PlayerModemBlockEntity(BlockEntityType<? extends BaseModemBlockEntity<T>> type, BlockPos pos, BlockState state, T peripheral) {
        super(type, pos, state, peripheral);
    }

    public boolean onBlockInteraction(PlayerEntity player) {
        return getModemPeripheral().bind(player);
    }

    public PlayerPeripheral getModemPeripheral() {
        return peripheral;
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        if (peripheral.isBound()) {
            NbtCompound boundTag = new NbtCompound();
            boundTag.putUuid("uuid", peripheral.profile.getId());
            boundTag.putString("name", peripheral.profile.getName());
            tag.put("gameProfile", boundTag);
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if (tag.contains("gameProfile")) {
            NbtCompound boundTag = tag.getCompound("gameProfile");
            if (boundTag.contains("uuid") && boundTag.contains("name")) {
                peripheral.bind(new GameProfile(boundTag.getUuid("uuid"), boundTag.getString("name")));
            }
        }
    }

    @Override
    public void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // Parameter values are passed in from scheduledTick, which as far as I know is non-null
        if(!world.isClient())
        {
            boolean paired = peripheral.isBound();
            if ( state.get( PlayerModemBlock.PAIRED ) != paired) {
                world.setBlockState(pos, state.with(PlayerModemBlock.PAIRED, paired));
            }
        }
    }
}
