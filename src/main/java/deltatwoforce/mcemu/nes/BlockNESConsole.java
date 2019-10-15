package deltatwoforce.mcemu.nes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;

import deltatwoforce.mcemu.MCEmu;
import jp.tanakh.bjne.nes.Nes;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockNESConsole extends Block{
	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.0625, 0, 0.0625*3, 0.0625*15, 0.0625*5, 0.0625*13);
	
	public BlockNESConsole() {
		super(Material.IRON);
		setRegistryName(MCEmu.MODID, "nesconsole");
		setUnlocalizedName("nesconsole");
		setCreativeTab(MCEmu.tabNES);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		if(!worldIn.isRemote) {
			ItemStack is;
			is = playerIn.getHeldItemMainhand();
			
			if(is.getItem() instanceof ItemNESCartridge) {
				if(MCEmu.running) {
					MCEmu.running = false;
				}
					MCEmu.CURRENTLYPLAYING = new ItemStack(is.getItem());
					MCEmu.NES_INSTANCE = new Nes(MCEmu.NESRenderer);
					try {
						MCEmu.NES_INSTANCE.load(((ItemNESCartridge) is.getItem()).rom.getPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
					MCEmu.running = true;
					if(!MCEmu.NES_THREAD.isAlive()) {
						MCEmu.NES_THREAD.start();
					}
			}else {
				if(MCEmu.running) {
					MCEmu.running = false;
					Graphics2D g2d = MCEmu.BUFIMG.createGraphics();
					g2d.setColor(Color.BLACK);
					g2d.fillRect(0, 0, MCEmu.BUFIMG.getWidth(), MCEmu.BUFIMG.getHeight());
					g2d.setColor(Color.WHITE);
					g2d.drawString("No cartridge inserted", 8, 20); 
					g2d.drawString("Get cartridges from your creative menu", 8, 40); 
					g2d.drawString("or put roms into the folder if you haven't.", 8, 56); 
					g2d.drawString("ROM Folder: /.minecraft/roms/nes/", 8, 76); 
					g2d.setFont(g2d.getFont().deriveFont(64f));
					g2d.drawString("MCEmu", 8, 222);
				}
			}
		}
		
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
	
	public static AxisAlignedBB getAabb() {
		return AABB;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}
}
