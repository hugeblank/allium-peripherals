package dev.elexi.hugeblank.api.base;

import dan200.computercraft.shared.peripheral.modem.ModemShapes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static dan200.computercraft.shared.util.WaterloggableHelpers.*;
import static net.minecraft.state.property.Properties.WATERLOGGED;

public class BaseModemBlock extends Block implements Waterloggable {
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
        return getWaterloggedFluidState( state );
    }

    @Nonnull
    @Deprecated
    public BlockState getStateForNeighborUpdate(BlockState state, Direction side, BlockState otherState, World world, BlockPos pos, BlockPos otherPos )
    {
        updateWaterloggedPostPlacement( state, world, pos );
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
                .with( WATERLOGGED, getWaterloggedStateForPlacement( placement ) );
    }
}
