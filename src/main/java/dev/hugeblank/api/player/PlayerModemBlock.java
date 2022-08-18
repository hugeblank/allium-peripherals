package dev.hugeblank.api.player;

import dev.hugeblank.api.base.BaseModemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class PlayerModemBlock extends BaseModemBlock {
    public static final BooleanProperty PAIRED = BooleanProperty.of( "paired" );

    public PlayerModemBlock(Settings settings) {
        super(settings);
        setDefaultState( this.getDefaultState()
                .with(PAIRED, false)
        );
    }

    @Deprecated
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (hand == Hand.MAIN_HAND && !world.isClient()) {
            if (world.getBlockEntity(pos) instanceof PlayerModemBlockEntity<?> modem) {
                boolean result = modem.onBlockInteraction(player);
                world.setBlockState(pos, state.with(PAIRED, result));
                return ActionResult.SUCCESS;
            }
        }
        return super.onUse(state, world, pos, player, hand, hitResult);
    }

    @Override
    protected void appendProperties( StateManager.Builder<Block, BlockState> builder )
    {
        super.appendProperties(builder);
        builder.add( PAIRED );
    }
}
