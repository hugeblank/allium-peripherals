package dev.elexi.hugeblank.peripherals.chatmodem;

import dan200.computercraft.shared.peripheral.modem.ModemShapes;
import dan200.computercraft.shared.util.WaterloggableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockChatModem extends Block implements WaterloggableBlock, BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty ON = BooleanProperty.of( "on" );
    public static final BooleanProperty PAIRED = BooleanProperty.of( "paired" );
    private boolean creative;

    public BlockChatModem(Settings settings, boolean creative) {
        super(settings);
        setDefaultState( getStateFactory().getDefaultState()
            .with( FACING, Direction.NORTH )
            .with( ON, false )
            .with( PAIRED, false)
            .with( WATERLOGGED, false ) );
        this.creative = creative;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        BlockEntityType<ChatModemBlockEntity> use;
        if (this.creative) {
            use = ChatModemBlockEntity.creativeChatModem;
        } else {
            use = ChatModemBlockEntity.normalChatModem;
        }
        return new ChatModemBlockEntity(use, this.creative);
    }

    @Override
    @Deprecated
    public boolean activate(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1, Hand hand_1, BlockHitResult blockHitResult_1) {
        if (hand_1 == Hand.MAIN_HAND && !world_1.isClient) {
            BlockEntity be = world_1.getBlockEntity(blockPos_1);
            if (be instanceof ChatModemBlockEntity) {
                ChatModemBlockEntity chatmodem = (ChatModemBlockEntity) be;
                chatmodem.onBlockInteraction(playerEntity_1);
            };
        }
        return false;
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
    public final void onBlockRemoved(@Nonnull BlockState block, @Nonnull World world, @Nonnull BlockPos pos, BlockState replace, boolean bool )
    {
        if( block.getBlock() == replace.getBlock() ) return;

        BlockEntity tile = world.getBlockEntity( pos );
        super.onBlockRemoved( block, world, pos, replace, bool );
        world.removeBlockEntity( pos );
        if( tile instanceof ChatModemBlockEntity) ((ChatModemBlockEntity) tile).destroy();
    }

    @Override
    protected void appendProperties( StateFactory.Builder<Block, BlockState> builder )
    {
        builder.add( FACING, ON, PAIRED, WATERLOGGED );
    }

    @Nonnull
    @Override
    @Deprecated
    public VoxelShape getOutlineShape(BlockState blockState, BlockView world, BlockPos pos, EntityContext position )
    {
        return ModemShapes.getBounds( blockState.get( FACING ) );
    }

    @Nonnull
    @Override
    @Deprecated
    public FluidState getFluidState(BlockState state )
    {
        return getWaterloggedFluidState( state );
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockState getStateForNeighborUpdate(BlockState state, Direction side, BlockState otherState, IWorld world, BlockPos pos, BlockPos otherPos )
    {
        updateWaterloggedPostPlacement( state, world, pos );
        return side == state.get( FACING ) && !state.canPlaceAt( world, pos )
                ? state.getFluidState().getBlockState()
                : state;
    }

    @Override
    @Deprecated
    public boolean canPlaceAt(BlockState state, ViewableWorld world, BlockPos pos )
    {
        Direction facing = state.get( FACING );
        BlockPos offsetPos = pos.offset( facing );
        BlockState offsetState = world.getBlockState( offsetPos );
        return Block.isFaceFullSquare( offsetState.getCollisionShape( world, offsetPos ), facing.getOpposite() );
    }

    @Nullable
    @Override
    public BlockState getPlacementState( ItemPlacementContext placement )
    {
        return getDefaultState()
                .with( FACING, placement.getSide().getOpposite() )
                .with( WATERLOGGED, getWaterloggedStateForPlacement( placement ) );
    }
}
