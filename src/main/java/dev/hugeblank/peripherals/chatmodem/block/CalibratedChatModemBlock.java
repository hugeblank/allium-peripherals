package dev.hugeblank.peripherals.chatmodem.block;

import dev.hugeblank.peripherals.chatmodem.state.CalibratedChatModemState;
import dev.hugeblank.peripherals.chatmodem.ChatModemBlockEntity;
import dev.hugeblank.peripherals.chatmodem.state.IModemState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;

public class CalibratedChatModemBlock extends AbstractChatModemBlock {
    public static final BooleanProperty ON = BooleanProperty.of("on");
    public static final BooleanProperty PAIRED = BooleanProperty.of("paired");

    public CalibratedChatModemBlock(Settings settings) {
        super(settings);
    }


    @Override
    protected BlockState calculateDefaultState(BlockState defaultState) {
        return super.calculateDefaultState(defaultState)
            .with(ON,false)
            .with(PAIRED,false);
    }



    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add( ON, PAIRED);
    }

    @Override
    public IModemState createState(ChatModemBlockEntity entity) {
        return new CalibratedChatModemState(entity);
    }
}
