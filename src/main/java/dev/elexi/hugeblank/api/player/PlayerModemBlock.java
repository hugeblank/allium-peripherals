package dev.elexi.hugeblank.api.player;

import dev.elexi.hugeblank.api.base.BaseModemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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
        if (hand == Hand.MAIN_HAND && !world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof PlayerModemBlockEntity) {
                PlayerModemBlockEntity entity = (PlayerModemBlockEntity) be;
                boolean result = entity.onBlockInteraction(player);
                world.setBlockState(pos, state.with(PAIRED, result));
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.FAIL;
    }

    @Override
    protected void appendProperties( StateManager.Builder<Block, BlockState> builder )
    {
        super.appendProperties(builder);
        builder.add( PAIRED );
    }
}
