package dev.elexi.hugeblank.peripherals.chatmodem;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralTile;
import dan200.computercraft.shared.common.TileGeneric;
import dan200.computercraft.shared.util.NamedBlockEntityType;
import dan200.computercraft.shared.util.TickScheduler;
import dev.elexi.hugeblank.Allium;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileChatModem extends TileGeneric implements IPeripheralTile {

    public static final NamedBlockEntityType<TileChatModem> FACTORY_NORMAL = NamedBlockEntityType.create(
        new Identifier( Allium.MOD_ID, "chat_modem" ),
        f -> new TileChatModem( f , false)
    );
    public static final NamedBlockEntityType<TileChatModem> FACTORY_CREATIVE = NamedBlockEntityType.create(
        new Identifier( Allium.MOD_ID, "chat_modem_creative" ),
        f -> new TileChatModem( f , true)
    );

    private static class Peripheral extends ChatPeripheral {
        private final TileChatModem entity;

        Peripheral(TileChatModem entity)
        {
            super( new ChatModemState( () -> TickScheduler.schedule( entity ) ), entity.creative );;
            this.entity = entity;
        }

        @Nonnull
        @Override
        public World getWorld()
        {
            return entity.getWorld();
        }

        public void destroy() {
            this.getModemState().uncapture(null);
        }

        @Nonnull
        @Override
        public Vec3d getPosition()
        {
            BlockPos pos = entity.getPos().offset( entity.modemDirection );
            return new Vec3d( pos.getX(), pos.getY(), pos.getZ() );
        }

        @Override
        public boolean equals( IPeripheral other )
        {
            return this == other || (other instanceof Peripheral && entity == ((Peripheral) other).entity);
        }
    }

    private final boolean creative;
    private boolean hasModemDirection = false;
    private Direction modemDirection = Direction.DOWN;
    private boolean destroyed = false;
    private Peripheral modem;

    public TileChatModem(BlockEntityType<? extends TileChatModem> type, boolean creative ) {
        super( type );
        this.creative = creative;
        modem = new Peripheral( this );
    }

    @Override
    public void validate()
    {
        super.validate();
        TickScheduler.schedule( this );
    }

    @Override
    public void destroy()
    {
        if( !destroyed )
        {
            modem.destroy();
            destroyed = true;
        }
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        if( world != null )
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

    @Override
    public void blockTick()
    {
        updateDirection();

        if( modem.getModemState().pollChanged() ) updateBlockState();
    }

    private void updateDirection()
    {
        if( hasModemDirection ) return;

        hasModemDirection = true;
        modemDirection = getCachedState().get( BlockChatModem.FACING );
    }

    private void updateBlockState()
    {
        boolean on = modem.getModemState().isOpen();
        BlockState state = getCachedState();
        if( state.get( BlockChatModem.ON ) != on )
        {
            getWorld().setBlockState( getPos(), state.with( BlockChatModem.ON, on ) );
        }
    }

    @Nullable
    @Override
    public IPeripheral getPeripheral( @Nonnull Direction side )
    {
        updateDirection();
        return side == modemDirection ? modem : null;
    }
}
