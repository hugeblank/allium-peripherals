package dev.elexi.hugeblank.peripherals.chatmodem;

import dev.elexi.hugeblank.api.player.PlayerModemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ChatModemBlock extends PlayerModemBlock implements BlockEntityProvider {
    private final boolean creative;
    public static final BooleanProperty ON = BooleanProperty.of( "on" );

    public ChatModemBlock(Settings settings, boolean creative) {
        super(settings);
        setDefaultState( this.getDefaultState()
                .with( ON, false )
        );
        this.creative = creative;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        BlockEntityType<ChatModemBlockEntity> use;
        if (this.creative) {
            use = ChatModemBlockEntity.TYPE_CREATIVE;
        } else {
            use = ChatModemBlockEntity.TYPE;
        }
        return new ChatModemBlockEntity(use, this.creative);
    }

    @Override
    @Deprecated
    public void neighborUpdate(BlockState blockState_1, World world_1, BlockPos blockPos_1, Block block_1, BlockPos blockPos_2, boolean boolean_1) {
        super.neighborUpdate(blockState_1, world_1, blockPos_1, block_1, blockPos_2, boolean_1);
        BlockEntity be = world_1.getBlockEntity(blockPos_1);
        if (be instanceof ChatModemBlockEntity) {
            ChatModemBlockEntity chatmodem = (ChatModemBlockEntity) be;
            chatmodem.markDirty();
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
