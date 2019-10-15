package deltatwoforce.mcemu;

import deltatwoforce.mcemu.nes.BlockNESConsole;
import deltatwoforce.mcemu.nes.BlockTelevision;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(MCEmu.MODID)
public class MCEmuBlocks {
	public static final BlockNESConsole nesconsole = new BlockNESConsole();
	public static final BlockTelevision television = new BlockTelevision();
}
