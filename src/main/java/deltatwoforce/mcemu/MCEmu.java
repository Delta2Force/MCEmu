package deltatwoforce.mcemu;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;

import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import deltatwoforce.mcemu.nes.ItemNESCartridge;
import deltatwoforce.mcemu.nes.MCEmuNESRenderer;
import deltatwoforce.mcemu.nes.TelevisionSpecialRenderer;
import deltatwoforce.mcemu.nes.TelevisionTileEntity;
import jp.tanakh.bjne.nes.Nes;

@Mod(modid = MCEmu.MODID, name = MCEmu.NAME, version = MCEmu.VERSION)
@Mod.EventBusSubscriber(modid=MCEmu.MODID)
public class MCEmu
{
    public static final String MODID = "mcemu";
    public static final String NAME = "MCEmu";
    public static final String VERSION = "1.0";
    
    public static int INDEX = 1;
    
    public static final CreativeTabs tabNES = (new CreativeTabs("mcemu.nes") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(MCEmuItems.nesconsole);
		}
	});
    
    public static final KeyBinding P1NES_LEFT = new KeyBinding("NES: P1 Left", Keyboard.KEY_LEFT, "NES");
    public static final KeyBinding P1NES_RIGHT = new KeyBinding("NES: P1 Right", Keyboard.KEY_RIGHT, "NES");
    public static final KeyBinding P1NES_UP = new KeyBinding("NES: P1 Up", Keyboard.KEY_UP, "NES");
    public static final KeyBinding P1NES_DOWN = new KeyBinding("NES: P1 Down", Keyboard.KEY_DOWN, "NES");
    public static final KeyBinding P1NES_A = new KeyBinding("NES: P1 A", Keyboard.KEY_RSHIFT, "NES");
    public static final KeyBinding P1NES_B = new KeyBinding("NES: P1 B", Keyboard.KEY_RCONTROL, "NES");
    public static final KeyBinding P1NES_START = new KeyBinding("NES: P1 Start", Keyboard.KEY_RETURN, "NES");
    public static final KeyBinding P1NES_SELECT = new KeyBinding("NES: P1 Select", Keyboard.KEY_BACK, "NES");
    
    public static final KeyBinding P2NES_LEFT = new KeyBinding("NES: P2 Left", Keyboard.KEY_NUMPAD1, "NES");
    public static final KeyBinding P2NES_RIGHT = new KeyBinding("NES: P2 Right", Keyboard.KEY_NUMPAD3, "NES");
    public static final KeyBinding P2NES_UP = new KeyBinding("NES: P2 Up", Keyboard.KEY_NUMPAD5, "NES");
    public static final KeyBinding P2NES_DOWN = new KeyBinding("NES: P2 Down", Keyboard.KEY_NUMPAD2, "NES");
    public static final KeyBinding P2NES_A = new KeyBinding("NES: P2 A", Keyboard.KEY_NUMPADENTER, "NES");
    public static final KeyBinding P2NES_B = new KeyBinding("NES: P2 B", Keyboard.KEY_NUMPAD0, "NES");
    public static final KeyBinding P2NES_START = new KeyBinding("NES: P2 Start", Keyboard.KEY_NUMPAD7, "NES");
    public static final KeyBinding P2NES_SELECT = new KeyBinding("NES: P2 Select", Keyboard.KEY_NUMPAD9, "NES");
    
    public static final KeyBinding[] bindings = {P1NES_LEFT, P1NES_RIGHT, P1NES_UP, P1NES_DOWN, P1NES_A, P1NES_B, P1NES_START, P1NES_SELECT,
    											 P2NES_LEFT, P2NES_RIGHT, P2NES_UP, P2NES_DOWN, P2NES_A, P2NES_B, P2NES_START, P2NES_SELECT};
    
    public static MCEmuNESRenderer NESRenderer;
    public static Nes NES_INSTANCE;
    public static BufferedImage BUFIMG;
    public static ItemStack CURRENTLYPLAYING;
    public static boolean running = false;
    static {
    	BUFIMG = new BufferedImage(256, 240, BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g = BUFIMG.createGraphics();
    	g.setColor(Color.BLACK);
		g.fillRect(0, 0, MCEmu.BUFIMG.getWidth(), MCEmu.BUFIMG.getHeight());
		g.setColor(Color.WHITE);
		g.drawString("No cartridge inserted", 8, 20); 
		g.drawString("Get cartridges from your creative menu", 8, 40); 
		g.drawString("or put roms into the folder if you haven't.", 8, 56); 
		g.drawString("ROM Folder: /.minecraft/roms/nes/", 8, 76); 
		g.setFont(g.getFont().deriveFont(64f));
		g.drawString("MCEmu", 8, 222);
    }
    public static Thread NES_THREAD = new Thread(new Runnable() {
		@Override
		public void run() {
			for (;;) {
					if (NES_INSTANCE == null)
						continue;

					/*long start = System.nanoTime();
					NES_INSTANCE.execFrame();

					for (;;) {
						long elapsed = System.nanoTime() - start;
						long wait = (long) (1.0 / 60 - elapsed / 1e-9);
						try {
							if (wait > 0)
								Thread.sleep(wait);
						} catch (InterruptedException e) {
						}
						break;
					}*/
					if(running) {
						NES_INSTANCE.execFrame();
					}
					try {
						Thread.sleep(1000/60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
		}
	});
    
    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	try {
			NESRenderer = new MCEmuNESRenderer();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
    	
        logger = event.getModLog();
    	File nesroms = new File(event.getModConfigurationDirectory().getParentFile(), "roms/nes");
    	nesroms.mkdirs();
        logger.info("Loading NES roms from " + nesroms.getPath() + "...");
		MCEmuItems.cartidges = new ArrayList<ItemNESCartridge>();
    	for(File f : nesroms.listFiles()) {
    		MCEmuItems.cartidges.add(new ItemNESCartridge(f));
    		logger.info("Loaded " + f.getName() + "...");
    	}
    	logger.info("Loaded " + MCEmuItems.cartidges.size() + " NES roms!");
    	
    	GameRegistry.registerTileEntity(TelevisionTileEntity.class, new ResourceLocation(MODID, "televisionTileEntity"));
    }

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	for(KeyBinding binding : bindings) {
            ClientRegistry.registerKeyBinding(binding);
    	}
    	ClientRegistry.bindTileEntitySpecialRenderer(TelevisionTileEntity.class, new TelevisionSpecialRenderer());
    }
    
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
    	event.getRegistry().register(MCEmuBlocks.nesconsole);
    	event.getRegistry().register(MCEmuBlocks.television);
    }
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
    	event.getRegistry().register(MCEmuItems.nesconsole);
    	event.getRegistry().register(MCEmuItems.television);
    	for(ItemNESCartridge inc : MCEmuItems.cartidges) {
    		event.getRegistry().register(inc);
    	}
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerKeyBindings(ModelRegistryEvent event) {
    	ModelLoader.setCustomModelResourceLocation(MCEmuItems.nesconsole, 0, new ModelResourceLocation(MODID + ":nesconsole"));
    	ModelLoader.setCustomModelResourceLocation(MCEmuItems.television, 0, new ModelResourceLocation(MODID + ":television"));
    	for(ItemNESCartridge inc : MCEmuItems.cartidges) {
    		ModelLoader.setCustomModelResourceLocation(inc, 0, new ModelResourceLocation(MODID + ":nescartridge"));
    	}
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onKeyEvent(KeyInputEvent event) {
    	for(KeyBinding kb : bindings) {
    		NESRenderer.onKey(kb.getKeyCode(), kb.isPressed());
    	}
    }
}
