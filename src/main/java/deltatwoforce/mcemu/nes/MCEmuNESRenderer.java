package deltatwoforce.mcemu.nes;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.google.common.collect.ImmutableMap;

import deltatwoforce.mcemu.MCEmu;
import jp.tanakh.bjne.nes.Renderer;
import jp.tanakh.bjne.nes.Renderer.InputInfo;
import jp.tanakh.bjne.nes.Renderer.ScreenInfo;
import jp.tanakh.bjne.nes.Renderer.SoundInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MCEmuNESRenderer implements Renderer{
	private static final int SCREEN_WIDTH = 256;
	private static final int SCREEN_HEIGHT = 240;

	private static final int SAMPLE_RATE = 48000;
	private static final int BPS = 16;
	private static final int CHANNELS = 2;
	private static final int BUFFER_FRAMES = 2;

	private static final int FPS = 60;
	private static final int SAMPLES_PER_FRAME = SAMPLE_RATE / FPS;

	private ScreenInfo scri = new ScreenInfo();
	private SoundInfo sndi = new SoundInfo();
	private InputInfo inpi = new InputInfo();

	private BufferedImage image = new BufferedImage(SCREEN_WIDTH,
			SCREEN_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);

	private int lineBufferSize;

	public MCEmuNESRenderer() throws LineUnavailableException {
		AudioFormat format = new AudioFormat(SAMPLE_RATE, BPS, CHANNELS, true,
				false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

		int bufSamples = SAMPLES_PER_FRAME;

		sndi.bps = 16;
		sndi.buf = new byte[bufSamples * (BPS / 8) * CHANNELS];
		sndi.ch = 2;
		sndi.freq = SAMPLE_RATE;
		sndi.sample = bufSamples;

		inpi.buf = new int[16];
	}

	@Override
	public void outputMessage(String msg) {
		System.out.println(msg);
	}

	@Override
	public ScreenInfo requestScreen(int width, int height) {
		if (!(scri.width == width && scri.height == height)) {
			scri.width = width;
			scri.height = height;
			scri.buf = new byte[3 * width * height];
			scri.pitch = 3 * width;
			scri.bpp = 24;
		}
		return scri;
	}

	@Override
	public void outputScreen(ScreenInfo info) {
		byte[] bgr = ((DataBufferByte) image.getRaster().getDataBuffer())
				.getData();

		for (int i = 0; i < SCREEN_WIDTH * SCREEN_HEIGHT; i++) {
			bgr[i * 3] = info.buf[i * 3 + 2];
			bgr[i * 3 + 1] = info.buf[i * 3 + 1];
			bgr[i * 3 + 2] = info.buf[i * 3 + 0];
		}
		
		MCEmu.BUFIMG = image;
	}

	@Override
	public SoundInfo requestSound() {
		if (getSoundBufferState() <= 0)
			return sndi;
		else
			return null;
	}

	@Override
	public void outputSound(SoundInfo info) {
		//line.write(info.buf, 0, info.sample * (info.bps / 8) * info.ch);
	}

	public int getSoundBufferState() {
		/*int rest = (lineBufferSize - line.available()) / (sndi.bps / 8)
				/ sndi.ch;
		if (rest < SAMPLES_PER_FRAME * BUFFER_FRAMES)
			return -1;
		if (rest == SAMPLES_PER_FRAME * BUFFER_FRAMES)
			return 0*/
		return 1;
	}

	static final int[][] keyDef = {
			{ MCEmu.P1NES_A.getKeyCode(), MCEmu.P1NES_B.getKeyCode(), MCEmu.P1NES_SELECT.getKeyCode(),
					MCEmu.P1NES_START.getKeyCode(), MCEmu.P1NES_UP.getKeyCode(), MCEmu.P1NES_DOWN.getKeyCode(),
					MCEmu.P1NES_LEFT.getKeyCode(), MCEmu.P1NES_RIGHT.getKeyCode(), },
			{ MCEmu.P2NES_A.getKeyCode(), MCEmu.P2NES_B.getKeyCode(), MCEmu.P2NES_SELECT.getKeyCode(),
						MCEmu.P2NES_START.getKeyCode(), MCEmu.P2NES_UP.getKeyCode(), MCEmu.P2NES_DOWN.getKeyCode(),
						MCEmu.P2NES_LEFT.getKeyCode(), MCEmu.P2NES_RIGHT.getKeyCode(), } };

	public void onKey(int keyCode, boolean press) {
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 8; j++)
				if (keyCode == keyDef[i][j])
					inpi.buf[i * 8 + j] = (press ? 1 : 0);
	}

	@Override
	public InputInfo requestInput(int padCount, int buttonCount) {
		return inpi;
	}
}
