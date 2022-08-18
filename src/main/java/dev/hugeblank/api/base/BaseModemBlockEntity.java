package dev.hugeblank.api.base;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralTile;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Random;

public abstract class BaseModemBlockEntity<T extends BasePeripheral> extends BlockEntity implements IPeripheralTile {

    private Direction modemDirection = Direction.DOWN;
    private boolean hasModemDirection = false;
    protected final T peripheral;

    public BaseModemBlockEntity(BlockEntityType<? extends BaseModemBlockEntity> type, BlockPos pos, BlockState state, T modem) {
        super(type, pos, state);
        modem.setBlockEntity(this);
        this.peripheral = modem;
    }

    // Event for when the block is allowed to change states, triggered by the Peripheral (generally)
    public void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {}

    @Override
    public void markDirty()
    {
        if (removed) return;
        super.markDirty();
        if( this.hasWorld() )
        {
            updateDirection();
        }
        else
        {
            hasModemDirection = false;
        }
    }

    @Override
    public void markRemoved()
    {
        super.markRemoved();
        hasModemDirection = false;
        getWorld().createAndScheduleBlockTick( getPos(), getCachedState().getBlock(), 0 );
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket()
    {
        return BlockEntityUpdateS2CPacket.create( this );
    }

    @Nullable
    @Override
    public IPeripheral getPeripheral(@Nonnull Direction side )
    {
        return side == updateDirection() ? peripheral : null;
    }

    protected Direction updateDirection()
    {
        if( hasModemDirection ) return modemDirection;

        hasModemDirection = true;
        modemDirection = getCachedState().get( BaseModemBlock.FACING );
        return modemDirection;
    }
}

