package deltatwoforce.mcemu.nes;

import java.io.File;
import java.util.List;

import deltatwoforce.mcemu.MCEmu;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemNESCartridge extends Item{
	private String name;
	public File rom;
	
	public ItemNESCartridge(File rom) {
		name = rom.getName();
		int i = name.lastIndexOf('.');
		if(i>0) {
			name = name.replace(name.substring(i), "");
		}
		String smol = rom.getName().toLowerCase().replace(" ", "");
		setRegistryName(MCEmu.MODID, "nescartridge->" + smol);
		setUnlocalizedName("nescartridge");
		setCreativeTab(MCEmu.tabNES);
		
		this.rom = rom;
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return name;
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add("A ROM File for the NES.");
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
}
