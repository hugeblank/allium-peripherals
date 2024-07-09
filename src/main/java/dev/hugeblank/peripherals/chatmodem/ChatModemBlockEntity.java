package dev.hugeblank.peripherals.chatmodem;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralTile;
import dev.hugeblank.peripherals.chatmodem.block.CalibratedChatModemBlock;
import dev.hugeblank.peripherals.chatmodem.block.IChatModemBlock;
import dev.hugeblank.peripherals.chatmodem.state.IModemState;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChatModemBlockEntity extends BlockEntity implements IPeripheralTile {
    public static BlockEntityType<ChatModemBlockEntity> CHAT_MODEM_TYPE;
    private final ChatPeripheral modem;
    private Direction modemDirection = Direction.DOWN;
    private boolean hasModemDirection = false;
    private boolean destroyed = false;


    public ChatModemBlockEntity(BlockPos pos, BlockState state) {
        super(ChatModemBlockEntity.CHAT_MODEM_TYPE, pos, state);
        IChatModemBlock block = (IChatModemBlock) state.getBlock();
        modem=new Peripheral(this,block.createState(this));
    }

    public void onBlockInteraction(PlayerEntity player) {
        modem.onBlockInteraction(player);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        modem.getState().writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        modem.getState().readNbt(tag, this);
    }

    public void destroy() {
        if (!destroyed) {
            modem.destroy();
            destroyed = true;
        }
    }

    @Override
    public void markDirty() {
        if (destroyed) return;
        super.markDirty();
        if (world != null) {
            updateDirection();
            updateBlockState();
        } else {
            hasModemDirection = false;
        }
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        hasModemDirection = false;
        if (world == null) return;
        world.createAndScheduleBlockTick(getPos(), getCachedState().getBlock(), 0);
    }

    private void updateDirection() {
        if (hasModemDirection) return;

        hasModemDirection = true;
        modemDirection = getCachedState().get(CalibratedChatModemBlock.FACING);
    }

    private void updateBlockState() {
        BlockState cachedState = getCachedState();
        BlockState state =modem.getState().updateBlockState(cachedState);
        if(cachedState!=state){
            getWorld().setBlockState(getPos(),state);
        }
    }

    @Nullable
    @Override
    public IPeripheral getPeripheral(@Nonnull Direction side) {
        updateDirection();
        return side == modemDirection ? modem : null;
    }

    private static class Peripheral extends ChatPeripheral {
        private final ChatModemBlockEntity entity;

        Peripheral(ChatModemBlockEntity entity, IModemState state) {
            super(state, state.peripheralMethods());
            this.entity = entity;
        }


        @Override
        public boolean equals(IPeripheral other) {
            return this == other || (other instanceof Peripheral && entity == ((Peripheral) other).entity);
        }
    }
}
