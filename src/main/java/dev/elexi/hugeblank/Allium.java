package dev.elexi.hugeblank;

import dev.elexi.hugeblank.peripherals.chatmodem.BlockChatModem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.MutableRegistry;

public class Allium implements ModInitializer {

    public static final String MOD_ID = "allium";
    public static final class Blocks {
        public static BlockChatModem chatModem = new BlockChatModem(
                FabricBlockSettings.of(Material.STONE).hardness(2).build(),
                false);

        public static BlockChatModem chatModemCreative = new BlockChatModem(
                FabricBlockSettings.of(Material.STONE).hardness(2).build(),
                true);

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
