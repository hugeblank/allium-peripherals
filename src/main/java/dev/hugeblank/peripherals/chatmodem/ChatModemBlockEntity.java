package dev.hugeblank.peripherals.chatmodem;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralTile;
import dev.hugeblank.Allium;
import dev.hugeblank.util.PlayerInfo;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChatModemBlockEntity extends BlockEntity implements IPeripheralTile {
    public static BlockEntityType<ChatModemBlockEntity> CHAT_MODEM_TYPE;

    private static class Peripheral extends ChatPeripheral {
        private final ChatModemBlockEntity entity;

        Peripheral(ChatModemBlockEntity entity, boolean creative)
        {
            super( new ChatModemState(entity, creative) );
            this.entity = entity;
        }

        public void destroy() {
            this.getModemState().uncapture(null);
        }

        @Override
        public boolean equals( IPeripheral other )
        {
            return this == other || (other instanceof Peripheral && entity == ((Peripheral) other).entity);
        }
    }

    private Direction modemDirection = Direction.DOWN;
    private boolean hasModemDirection = false;
    private boolean destroyed = false;
    private final Peripheral modem;

    public ChatModemBlockEntity(BlockEntityType<? extends ChatModemBlockEntity> type, BlockPos pos, BlockState state ) {
        super( type, pos, state );
        modem = new Peripheral( this, !state.getBlock().equals(Allium.Blocks.CHAT_MODEM));
    }

    public ChatModemBlockEntity(BlockPos pos, BlockState state) {
        this(ChatModemBlockEntity.CHAT_MODEM_TYPE, pos, state);
    }

    public void onBlockInteraction(PlayerEntity player) {
        modem.setPlayer(player);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        if (modem.creative) return;
        if (modem.getModemState().isBound()) {
            PlayerInfo info = modem.getBoundPlayer();
            NbtCompound boundTag = new NbtCompound();
            boundTag.putString("name", info.playerName());
            boundTag.put("uuid", NbtHelper.fromUuid(info.uuid()));
            tag.put("playerInfo", boundTag);
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if (modem.creative) return;
        if (tag.contains("playerInfo") && !modem.getModemState().isBound()) {
            NbtCompound boundTag = tag.getCompound("playerInfo");
            if (boundTag.contains("name") && boundTag.contains("uuid")) {
                //noinspection ConstantConditions
                modem.setBoundPlayer(new PlayerInfo(
                        boundTag.getString("name"),
                        NbtHelper.toUuid(boundTag.get("uuid"))
                ));
            } else {
                throw new IllegalStateException(
                        "Allium Peripherals - Expected playerInfo tag of chat modem at " +
                                this.pos +
                                "to contain name and UUID, got: " +
                                boundTag
                );
            }
        }
    }

    public void destroy()
    {
        if( !destroyed ) {
            modem.destroy();
            destroyed = true;
        }
    }

    @Override
    public void markDirty()
    {
        if (destroyed) return;
        super.markDirty();
        if( world != null ) {
            updateDirection();
            updateBlockState();
        } else {
            hasModemDirection = false;
        }
    }

    @Override
    public void markRemoved()
    {
        super.markRemoved();
        hasModemDirection = false;
        if (world == null) return;
        world.createAndScheduleBlockTick( getPos(), getCachedState().getBlock(), 0 );
    }

    private void updateDirection()
    {
        if( hasModemDirection ) return;

        hasModemDirection = true;
        modemDirection = getCachedState().get( BlockChatModem.FACING );
    }

    private void updateBlockState()
    {
        boolean on = modem.getModemState().isOpen();
        boolean paired = modem.getModemState().isBound();
        BlockState state = getCachedState();
        if( state.get( BlockChatModem.ON ) != on )
        {
            getWorld().setBlockState( getPos(), state.with( BlockChatModem.ON, on ) );
        }
        if ( state.get( BlockChatModem.PAIRED ) != paired) {
            getWorld().setBlockState(getPos(), state.with(BlockChatModem.PAIRED, paired));
        }
    }

    @Nullable
    @Override
    public IPeripheral getPeripheral( @Nonnull Direction side )
    {
        updateDirection();
        return side == modemDirection ? modem : null;
    }
}
