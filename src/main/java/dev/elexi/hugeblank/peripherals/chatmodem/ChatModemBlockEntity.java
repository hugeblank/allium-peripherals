package dev.elexi.hugeblank.peripherals.chatmodem;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralTile;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class ChatModemBlockEntity extends BlockEntity implements IPeripheralTile {

    public static BlockEntityType<ChatModemBlockEntity> normalChatModem;
    public static BlockEntityType<ChatModemBlockEntity> creativeChatModem;

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
    private Peripheral modem;

    public ChatModemBlockEntity(BlockEntityType<? extends ChatModemBlockEntity> type, BlockPos pos, BlockState state, boolean creative ) {
        super( type, pos, state );
        modem = new Peripheral( this, creative);
    }

    public static boolean registryCreativeMode = false;
    public ChatModemBlockEntity(BlockPos pos, BlockState state) {
        this(registryCreativeMode ? ChatModemBlockEntity.creativeChatModem : ChatModemBlockEntity.normalChatModem, pos, state, registryCreativeMode);
    }

    public void onBlockInteraction(PlayerEntity player) {
        modem.setPlayer(player);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        if (modem.creative) return;
        String[] playerInfo = modem.getBoundPlayer();
        if (playerInfo == null) return;

        if (modem.getModemState().isBound()) {
            NbtList boundPlayer = new NbtList();
            if (playerInfo[0] != null)
                boundPlayer.add(0, NbtString.of(playerInfo[0]));
            if (playerInfo[1] != null)
                boundPlayer.add(1, NbtString.of(playerInfo[1]));

            tag.put("boundPlayer", boundPlayer);
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if (modem.creative) return;
        if (!tag.contains("boundPlayer")) return;
        if (tag.getType("boundPlayer") != 0 && !modem.getModemState().isBound()) {
            NbtList boundPlayer = tag.getList("boundPlayer", 8);
            modem.setBoundPlayer(boundPlayer.getString(0), boundPlayer.getString(1));
        }
    }

    public void destroy()
    {
        if( !destroyed )
        {
            modem.destroy();
            destroyed = true;
        }
    }

    @Override
    public void markDirty()
    {
        if (destroyed) return;
        super.markDirty();
        if( world != null )
        {
            updateDirection();
            updateBlockState();
        }
        else
        {
            hasModemDirection = false;
        }
    }

    @Override
    public void markRemoved()
    {
        super.markRemoved();
        hasModemDirection = false;
        Objects.requireNonNull(world).createAndScheduleBlockTick( getPos(), getCachedState().getBlock(), 0 );
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