package dev.elexi.hugeblank.util;

import dev.elexi.hugeblank.Allium;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class CreativeTabMain extends ItemGroup
{
    public CreativeTabMain( int i )
    {
        super( i, Allium.MOD_ID );
    }

    @Nonnull
    @Override
    @Environment( EnvType.CLIENT )
    public ItemStack createIcon()
    {
        return new ItemStack( Allium.Blocks.chatModemCreative );
    }
}
