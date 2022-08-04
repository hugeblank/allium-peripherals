package dev.hugeblank.peripherals.chatmodem;

import dan200.computercraft.shared.peripheral.modem.ModemShapes;
import dan200.computercraft.shared.util.WaterloggableHelpers;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static dan200.computercraft.shared.util.WaterloggableHelpers.*;
import static net.minecraft.state.property.Properties.WATERLOGGED;

public class BlockChatModem extends Block implements Waterloggable, BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty ON = BooleanProperty.of( "on" );
    public static final BooleanProperty PAIRED = BooleanProperty.of( "paired" );

    public BlockChatModem(Settings settings) {
        super(settings);
        setDefaultState( getStateManager().getDefaultState()
            .with( FACING, Direction.NORTH )
            .with( ON, false )
            .with( PAIRED, false)
            .with( WATERLOGGED, false ) );
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChatModemBlockEntity(ChatModemBlockEntity.CHAT_MODEM_TYPE, pos, state);
    }

    @Override
    @Deprecated
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (hand == Hand.MAIN_HAND && !world.isClient) {
            BlockEntity be = world.getBlockEntity(blockPos);
            if (be instanceof ChatModemBlockEntity chat_modem) {
                chat_modem.onBlockInteraction(playerEntity);
                chat_modem.markDirty();
                return ActionResult.SUCCESS;
            };
        }
        return ActionResult.FAIL;
    }

    @Override
    @Deprecated
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos1, boolean bl) {
        super.neighborUpdate(blockState, world, blockPos, block, blockPos1, bl);
        BlockEntity be = world.getBlockEntity(blockPos);
        if (be instanceof ChatModemBlockEntity chat_modem) {
            chat_modem.markDirty();
        }
    }

    @Override
    @Deprecated
    public final void onStateReplaced(@Nonnull BlockState block, @Nonnull World world, @Nonnull BlockPos pos, BlockState replace, boolean bool )
    {
        if( block.getBlock().equals(replace.getBlock()) ) return;
        super.onStateReplaced( block, world, pos, replace, bool );
        world.removeBlockEntity( pos );
        if( world.getBlockEntity( pos ) instanceof ChatModemBlockEntity be) be.destroy();
    }

    @Override
    protected void appendProperties( StateManager.Builder<Block, BlockState> builder )
    {
        builder.add( FACING, ON, PAIRED, WATERLOGGED );
    }

    @Nonnull
    @Override
    @Deprecated
    public VoxelShape getOutlineShape(BlockState blockState, BlockView world, BlockPos pos, ShapeContext position )
    {
        return ModemShapes.getBounds( blockState.get( FACING ) );
    }

    @Nonnull
    @Override
    @Deprecated
    public FluidState getFluidState(BlockState state )
    {
        return WaterloggableHelpers.getFluidState(state);
    }

    @Nonnull
    @Deprecated
    public BlockState getStateForNeighborUpdate(BlockState state, Direction side, BlockState otherState, World world, BlockPos pos, BlockPos otherPos )
    {
        updateShape( state, world, pos );
        return side == state.get( FACING ) && !state.canPlaceAt( world, pos )
                ? state.getFluidState().getBlockState()
                : state;
    }

    @Override
    @Deprecated
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos )
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
                .with( WATERLOGGED, getFluidStateForPlacement( placement ) );
    }
}
