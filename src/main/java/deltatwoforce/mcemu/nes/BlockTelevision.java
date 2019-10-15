package deltatwoforce.mcemu.nes;

import deltatwoforce.mcemu.MCEmu;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTelevision extends Block{
	public BlockTelevision() {
		super(Material.WOOD);
		setRegistryName(MCEmu.MODID, "television");
		setUnlocalizedName("television");
		setCreativeTab(MCEmu.tabNES);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TelevisionTileEntity();
	}
}
