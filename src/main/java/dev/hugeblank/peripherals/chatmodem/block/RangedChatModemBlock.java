package dev.hugeblank.peripherals.chatmodem.block;

import dev.hugeblank.peripherals.chatmodem.ChatModemBlockEntity;
import dev.hugeblank.peripherals.chatmodem.state.IModemState;
import dev.hugeblank.peripherals.chatmodem.state.RangedChatModemState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import javax.annotation.Nullable;

public class RangedChatModemBlock extends AbstractChatModemBlock {

    public static final BooleanProperty ON = CalibratedChatModemBlock.ON;
    public Box listenRange;
    public Box sendRange;

    public RangedChatModemBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    private static Box offset(Box box, BlockPos pos) {
        return box == null ? null : box.offset(pos);
    }

    @Override
    protected BlockState calculateDefaultState(BlockState defaultState) {
        return super.calculateDefaultState(defaultState).with(ON, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ON);
    }

    public RangedChatModemBlock sendRange(Box sendRange) {
        this.sendRange = sendRange;
        return this;
    }

    public RangedChatModemBlock listenRange(Box rangeBox) {
        this.listenRange = rangeBox;
        return this;
    }

    @Override
    public IModemState createState(ChatModemBlockEntity entity) {
        BlockPos pos = entity.getPos();
        return new RangedChatModemState(entity,
            offset(listenRange, pos),
            offset(sendRange, pos)
        );
    }
}
