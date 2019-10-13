package dev.elexi.hugeblank;

import dev.elexi.hugeblank.peripherals.chatmodem.BlockChatModem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.MutableRegistry;

public class Allium implements ModInitializer {

    public static final String MOD_ID = "allium";
    public static final class Blocks {
        public static BlockChatModem chatModem;
        public static BlockChatModem chatModemCreative;

        public Blocks() {}
    }

    @Override
    public void onInitialize()
    {
        Registry.registerBlocks( net.minecraft.util.registry.Registry.BLOCK );
        Registry.registerTileEntities( (MutableRegistry<BlockEntityType<?>>) net.minecraft.util.registry.Registry.BLOCK_ENTITY );
        Registry.registerItems( net.minecraft.util.registry.Registry.ITEM );
        //registerRecipes( (MutableRegistry<RecipeSerializer<?>>) net.minecraft.util.registry.Registry.RECIPE_SERIALIZER );
    }
}
