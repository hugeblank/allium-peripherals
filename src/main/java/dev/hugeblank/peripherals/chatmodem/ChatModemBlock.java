package dev.hugeblank.peripherals.chatmodem;

import dev.hugeblank.api.player.PlayerModemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class ChatModemBlock extends PlayerModemBlock implements BlockEntityProvider {
    public static final BooleanProperty ON = BooleanProperty.of( "on" );

    public ChatModemBlock(Settings settings) {
        super(settings);
        setDefaultState( this.getDefaultState().with( ON, false ));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChatModemBlockEntity(ChatModemBlockEntity.TYPE, pos, state);
    }

    @Override
    @Deprecated
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ChatModemBlockEntity modem) {
            modem.markDirty();
        }
    }

    @Override
    @Deprecated
    public final void onStateReplaced(@Nonnull BlockState block, @Nonnull World world, @Nonnull BlockPos pos, BlockState replace, boolean bool )
    {
        if( block.getBlock() == replace.getBlock() ) return;

        BlockEntity tile = world.getBlockEntity( pos );
        super.onStateReplaced( block, world, pos, replace, bool );
        world.removeBlockEntity( pos );
        if( tile instanceof ChatModemBlockEntity) ((ChatModemBlockEntity) tile).destroy();
    }

    @Override
    protected void appendProperties( StateManager.Builder<Block, BlockState> builder )
    {
        super.appendProperties(builder);
        builder.add( ON );
    }
}
