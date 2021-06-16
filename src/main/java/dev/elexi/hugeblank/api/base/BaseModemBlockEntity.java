package dev.elexi.hugeblank.api.base;

import dan200.computercraft.api.peripheral.IPeripheralTile;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Direction;

public abstract class BaseModemBlockEntity extends BlockEntity implements IPeripheralTile {

    private Direction modemDirection = Direction.DOWN;
    private boolean hasModemDirection = false;
    private boolean destroyed = false;
    protected BasePeripheral modem;


    public BaseModemBlockEntity(BlockEntityType<? extends BaseModemBlockEntity> type, BasePeripheral modem) {
        super( type );
        this.modem = modem;
    }

    public void destroy()
    {
        if( !destroyed )
        {
            destroyed = true;
        }
    }

    @Override
    public void markDirty()
    {
        if (destroyed) return;
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
    public void resetBlock()
    {
        super.resetBlock();
        hasModemDirection = false;
        world.getBlockTickScheduler().schedule( getPos(), getCachedState().getBlock(), 0 );
    }

    protected Direction updateDirection()
    {
        if( hasModemDirection ) return modemDirection;

        hasModemDirection = true;
        modemDirection = getCachedState().get( BaseModemBlock.FACING );
        return modemDirection;
    }
}

