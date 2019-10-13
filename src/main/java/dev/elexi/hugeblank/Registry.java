/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dev.elexi.hugeblank;

import dev.elexi.hugeblank.peripherals.chatmodem.BlockChatModem;
import dev.elexi.hugeblank.peripherals.chatmodem.ChatModemBlockEntity;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;

import java.util.function.Supplier;

public final class Registry
{

    public static Supplier<ChatModemBlockEntity> normalSupplier;
    public static Supplier<ChatModemBlockEntity> creativeSupplier;

    private static final ItemGroup mainItemGroup = FabricItemGroupBuilder
            .create( new Identifier( Allium.MOD_ID, "main" ) )
            .icon( () -> new ItemStack( Allium.Blocks.chatModemCreative ) )
            .build();

    private Registry()
    {
    }

    public static void registerBlocks( MutableRegistry<Block> registry ) {

        registry.add(new Identifier(Allium.MOD_ID, "chat_modem"), Allium.Blocks.chatModem);
        registry.add(new Identifier(Allium.MOD_ID, "chat_modem_creative"), Allium.Blocks.chatModemCreative);
    }

    public static void registerTileEntities( MutableRegistry<BlockEntityType<?>> registry )
    {
        normalSupplier = () -> {
            ChatModemBlockEntity be = new ChatModemBlockEntity(ChatModemBlockEntity.normalChatModem, false);
            return be;
        };
        creativeSupplier = () -> {
            ChatModemBlockEntity be = new ChatModemBlockEntity(ChatModemBlockEntity.creativeChatModem, true);
            return be;
        };

    }

    private static void registerItemBlock( MutableRegistry<Item> registry, BlockItem item )
    {
        registry.add( net.minecraft.util.registry.Registry.BLOCK.getId( item.getBlock() ), item );
    }

    private static Item.Settings defaultItem()
    {
        return new Item.Settings().group( mainItemGroup );
    }

    public static void registerItems( MutableRegistry<Item> registry )
    {
        //registerItemBlock( registry, new BlockItem( Allium.Blocks.chatModem, defaultItem() ) );
        //registerItemBlock( registry, new BlockItem( Allium.Blocks.chatModemCreative, defaultItem() ) );
    }
}
