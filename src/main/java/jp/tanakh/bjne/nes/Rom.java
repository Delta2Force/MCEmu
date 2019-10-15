package jp.tanakh.bjne.nes;

import java.io.FileInputStream;
import java.io.IOException;

public class Rom {
	public Rom(Nes n) {
	}

	public void reset() {

	}

	public void release() {
		romDat = null;
		chrDat = null;
		sram = null;
		vram = null;
	}

	public void load(String fname) throws IOException {
		release();

		FileInputStream is = new FileInputStream(fname);
		byte[] dat = new byte[is.available()];
		is.read(dat, 0, dat.length);
		is.close();

		if (!(dat[0] == 'N' && dat[1] == 'E' && dat[2] == 'S' && dat[3] == '\u001A'))
			throw new IOException("rom signature is invalid");

		prgPageCnt = dat[4] & 0xff;
		chrPageCnt = dat[5] & 0xff;

		mirroring = (dat[6] & 1) != 0 ? MirrorType.VERTICAL
				: MirrorType.HORIZONTAL;
		sramEnable = (dat[6] & 2) != 0;
		trainerEnable = (dat[6] & 4) != 0;
		fourScreen = (dat[6] & 8) != 0;

		mapper = ((dat[6] & 0xff) >> 4) | (dat[7] & 0xf0);

		int romSize = 0x4000 * prgPageCnt;
		int chrSize = 0x2000 * chrPageCnt;

		romDat = new byte[romSize];
		if (chrSize != 0)
			chrDat = new byte[chrSize];
		sram = new byte[0x2000];
		vram = new byte[0x2000];

		if (romSize > 0)
			System.arraycopy(dat, 16, romDat, 0, romSize);
		if (chrSize > 0)
			System.arraycopy(dat, 16 + romSize, chrDat, 0, chrSize);

		System.out.printf("Cartridge information:\n");
		System.out.printf("%d KB rom, %d KB vrom\n", romSize / 1024,
				chrSize / 1024);
		System.out.printf("mapper #%d\n", mapper);
		System.out.printf("%s mirroring\n",
				mirroring == MirrorType.VERTICAL ? "vertical" : "holizontal");
		System.out.printf("sram        : %s\n", sramEnable ? "Y" : "N");
		System.out.printf("trainer     : %s\n", trainerEnable ? "Y" : "N");
		System.out.printf("four screen : %s\n", fourScreen ? "Y" : "N");
	}

	public void saveSram(String fname) {
		// TODO
	}

	public void loadSram(String fname) {
		// TODO
	}

	public byte[] getRom() {
		return romDat;
	}

	public byte[] getChr() {
		return chrDat;
	}

	public byte[] getSram() {
		return sram;
	}

	public byte[] getVram() {
		return vram;
	}

	public int romSize() {
		return prgPageCnt;
	}

	public int chrSize() {
		return chrPageCnt;
	}

	public int mapperNo() {
		return mapper;
	}

	public boolean hasSram() {
		return sramEnable;
	}

	public boolean hasTrainer() {
		return trainerEnable;
	}

	public boolean isFourScreen() {
		return fourScreen;
	}

	public enum MirrorType {
		HORIZONTAL, VERTICAL,
	}

	public MirrorType mirror() {
		return mirroring;
	}

	private int prgPageCnt, chrPageCnt;
	private MirrorType mirroring;
	private boolean sramEnable, trainerEnable;
	private boolean fourScreen;
	private int mapper;
	private byte[] romDat, chrDat, sram, vram;
}
