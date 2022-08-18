package dev.hugeblank.peripherals.chatmodem;

import dev.hugeblank.api.player.PlayerModemBlock;
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
import org.jetbrains.annotations.Nullable;

public class ChatModemBlock extends PlayerModemBlock {
    public static final BooleanProperty ON = BooleanProperty.of( "on" );

    public ChatModemBlock(Settings settings) {
        super(settings);
        setDefaultState( this.getDefaultState().with( ON, false ));
    }

    @Override
    @Deprecated
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        return ( // If the modem isn't creative then bind, otherwise ignore.
                world.getBlockEntity(pos) instanceof ChatModemBlockEntity modem &&
                modem.getModemPeripheral() instanceof ChatPeripheral chatPeripheral &&
                !chatPeripheral.isCreative()
        ) ? super.onUse(state, world, pos, player, hand, hitResult) : ActionResult.FAIL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChatModemBlockEntity(ChatModemBlockEntity.TYPE, pos, state);
    }

    @Override
    protected void appendProperties( StateManager.Builder<Block, BlockState> builder )
    {
        super.appendProperties(builder);
        builder.add( ON );
    }
}
