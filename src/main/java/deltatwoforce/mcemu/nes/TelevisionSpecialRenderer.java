package deltatwoforce.mcemu.nes;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;

import deltatwoforce.mcemu.MCEmu;

public class TelevisionSpecialRenderer extends TileEntitySpecialRenderer<TelevisionTileEntity>{
	public int lastTex = -1;
	
	@Override
	public void render(TelevisionTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		//super.render(te, x, y, z, partialTicks, destroyStage, alpha);
		
		if(lastTex != -1) {
			glDeleteTextures(lastTex);
		}
		
		RenderHelper.disableStandardItemLighting();
        setLightmapDisabled(true);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_CULL_FACE);
        glDisable(GL_BLEND);
        
        glPushMatrix();
        glTranslated(x+0.5, y+0.5, z-(0.0625*-9));
        glScaled(0.5-(0.0625), 0.5-(0.0625), 1);
        glRotated(180, 1, 0, 0);
        
        int tex = convertImageData(MCEmu.BUFIMG);
        glBindTexture(GL_TEXTURE_2D, tex);
        glBegin(GL_QUADS);
        int sw=1,sh=1;
        glColor4f(1.f, 1.f, 1.f, 1.f); glTexCoord2f(0.f, 1.f); glVertex3f( sw,  sh, 0.505f);
        glColor4f(1.f, 1.f, 1.f, 1.f); glTexCoord2f(1.f, 1.f); glVertex3f(-sw,  sh, 0.505f);
        glColor4f(1.f, 1.f, 1.f, 1.f); glTexCoord2f(1.f, 0.f); glVertex3f(-sw, -sh, 0.505f);
        glColor4f(1.f, 1.f, 1.f, 1.f); glTexCoord2f(0.f, 0.f); glVertex3f( sw, -sh, 0.505f);
        glEnd();
        GlStateManager.bindTexture(0);
        glPopMatrix();
        
        lastTex = tex;
        
        RenderHelper.enableStandardItemLighting();
        setLightmapDisabled(false);
        glEnable(GL_CULL_FACE);
	}
	
	private int convertImageData(BufferedImage image) {
		int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB

        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }
        
        buffer.flip();

        int textureID = glGenTextures(); //Generate texture ID
        glBindTexture(GL_TEXTURE_2D, textureID); //Bind texture ID
        
        //Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        
        //Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
      
        //Return the texture ID so we can bind it later again
      return textureID;
    }
}
