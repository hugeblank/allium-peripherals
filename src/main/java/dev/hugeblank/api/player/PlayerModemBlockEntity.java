package dev.hugeblank.api.player;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.peripheral.IPeripheralTile;
import dev.hugeblank.api.base.BaseModemBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    public void setWorld(World world) {
        super.setWorld(world);
        peripheral.onWorld(world);
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        if( world != null )
        {
            boolean paired = peripheral.isBound();
            BlockState state = getCachedState();
            if ( state.get( PlayerModemBlock.PAIRED ) != paired) {
                getWorld().setBlockState(getPos(), state.with(PlayerModemBlock.PAIRED, paired));
            }
        }
    }
}
