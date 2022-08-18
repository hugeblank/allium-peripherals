package dev.hugeblank.api.base;

import dan200.computercraft.shared.peripheral.modem.ModemShapes;
import dan200.computercraft.shared.util.WaterloggableHelpers;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

import static net.minecraft.state.property.Properties.WATERLOGGED;

public abstract class BaseModemBlock extends BlockWithEntity implements Waterloggable {
    public static final DirectionProperty FACING = Properties.FACING;

    public BaseModemBlock(Settings settings) {
        super(settings);
        setDefaultState( this.getDefaultState()
                .with( FACING, Direction.NORTH )
                .with( WATERLOGGED, false ) );
    }

    @Override
    protected void appendProperties( StateManager.Builder<Block, BlockState> builder )
    {
        builder.add( FACING, WATERLOGGED );
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
         if (world.getBlockEntity(pos) instanceof BaseModemBlockEntity<?> be) {
             be.onScheduledTick(state, world, pos, random);
         }
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
        return WaterloggableHelpers.getFluidState( state );
    }

    @Nonnull
    @Deprecated
    @Override
    public BlockState getStateForNeighborUpdate(@Nonnull BlockState state, @Nonnull Direction side, @Nonnull BlockState otherState, @Nonnull WorldAccess world, @Nonnull BlockPos pos, @Nonnull BlockPos otherPos ) {
        WaterloggableHelpers.updateShape( state, world, pos );
        return side == state.get( FACING ) && !state.canPlaceAt( world, pos )
                ? state.getFluidState().getBlockState()
                : state;
    }

    @Override
    @Deprecated
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos ) {
        Direction facing = state.get( FACING );
        return sideCoversSmallSquare(world, pos.offset(facing), facing.getOpposite() );
    }

    @Nullable
    @Override
    public BlockState getPlacementState( ItemPlacementContext placement )
    {
        return getDefaultState()
                .with( FACING, placement.getSide().getOpposite() )
                .with( WATERLOGGED, WaterloggableHelpers.getFluidStateForPlacement( placement ) );
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }


}
