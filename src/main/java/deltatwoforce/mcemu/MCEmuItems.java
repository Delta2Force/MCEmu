package deltatwoforce.mcemu;

import java.util.ArrayList;

import deltatwoforce.mcemu.nes.BlockNESConsole;
import deltatwoforce.mcemu.nes.ItemNESCartridge;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(MCEmu.MODID)
public class MCEmuItems {
	public static final Item nesconsole = new ItemBlock(MCEmuBlocks.nesconsole).setRegistryName(MCEmuBlocks.nesconsole.getRegistryName());
	public static final Item television = new ItemBlock(MCEmuBlocks.television).setRegistryName(MCEmuBlocks.television.getRegistryName());
	
	public static ArrayList<ItemNESCartridge> cartidges;
}
