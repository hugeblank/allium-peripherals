package dev.hugeblank.peripherals.chatmodem;

import dan200.computercraft.api.peripheral.IPeripheralTile;
import dev.hugeblank.Allium;
import dev.hugeblank.api.player.PlayerModemBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class ChatModemBlockEntity extends PlayerModemBlockEntity<ChatPeripheral> implements IPeripheralTile {
    public static BlockEntityType<ChatModemBlockEntity> TYPE = FabricBlockEntityTypeBuilder
            .create((pos, state) ->
                    new ChatModemBlockEntity(ChatModemBlockEntity.TYPE, pos, state),
                    Allium.Blocks.CHAT_MODEM,
                    Allium.Blocks.CHAT_MODEM_CREATIVE
            ).build(null);

    public ChatModemBlockEntity(BlockEntityType<? extends ChatModemBlockEntity> type, BlockPos pos, BlockState state ) {
        super( type, pos, state, new ChatPeripheral(!state.getBlock().equals(Allium.Blocks.CHAT_MODEM)) );
    }

    public void destroy()
    {
        super.destroy();
        peripheral.destroy();
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        if( world != null )
        {
            boolean on = peripheral.isOpen();
            BlockState state = getCachedState();
            if( state.get( ChatModemBlock.ON ) != on )
            {
                getWorld().setBlockState( getPos(), state.with( ChatModemBlock.ON, on ) );
            }
        }
    }

    @Override
    public void markRemoved()
    {
        super.markRemoved();
        if (world == null) return;
        world.createAndScheduleBlockTick( getPos(), getCachedState().getBlock(), 0 );
    }
}
