package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.Composite;
import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;

public abstract class BufferedTextPipe
  extends GlyphListPipe
{
  private static final int BYTES_PER_GLYPH_IMAGE = 8;
  private static final int BYTES_PER_GLYPH_POSITION = 8;
  private static final int OFFSET_CONTRAST = 8;
  private static final int OFFSET_RGBORDER = 2;
  private static final int OFFSET_SUBPIXPOS = 1;
  private static final int OFFSET_POSITIONS = 0;
  protected final RenderQueue rq;
  
  private static int createPackedParams(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList)
  {
    return (paramGlyphList.usePositions() ? 1 : 0) << 0 | (paramGlyphList.isSubPixPos() ? 1 : 0) << 1 | (paramGlyphList.isRGBOrder() ? 1 : 0) << 2 | (lcdTextContrast & 0xFF) << 8;
  }
  
  protected BufferedTextPipe(RenderQueue paramRenderQueue)
  {
    rq = paramRenderQueue;
  }
  
  protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList)
  {
    Object localObject1 = composite;
    if (localObject1 == AlphaComposite.Src) {
      localObject1 = AlphaComposite.SrcOver;
    }
    rq.lock();
    try
    {
      validateContext(paramSunGraphics2D, (Composite)localObject1);
      enqueueGlyphList(paramSunGraphics2D, paramGlyphList);
    }
    finally
    {
      rq.unlock();
    }
  }
  
  private void enqueueGlyphList(final SunGraphics2D paramSunGraphics2D, final GlyphList paramGlyphList)
  {
    RenderBuffer localRenderBuffer = rq.getBuffer();
    final int i = paramGlyphList.getNumGlyphs();
    int j = i * 8;
    int k = paramGlyphList.usePositions() ? i * 8 : 0;
    int m = 24 + j + k;
    final long[] arrayOfLong = paramGlyphList.getImages();
    final float f1 = paramGlyphList.getX() + 0.5F;
    final float f2 = paramGlyphList.getY() + 0.5F;
    rq.addReference(paramGlyphList.getStrike());
    if (m <= localRenderBuffer.capacity())
    {
      if (m > localRenderBuffer.remaining()) {
        rq.flushNow();
      }
      rq.ensureAlignment(20);
      localRenderBuffer.putInt(40);
      localRenderBuffer.putInt(i);
      localRenderBuffer.putInt(createPackedParams(paramSunGraphics2D, paramGlyphList));
      localRenderBuffer.putFloat(f1);
      localRenderBuffer.putFloat(f2);
      localRenderBuffer.put(arrayOfLong, 0, i);
      if (paramGlyphList.usePositions())
      {
        float[] arrayOfFloat = paramGlyphList.getPositions();
        localRenderBuffer.put(arrayOfFloat, 0, 2 * i);
      }
    }
    else
    {
      rq.flushAndInvokeNow(new Runnable()
      {
        public void run()
        {
          drawGlyphList(i, paramGlyphList.usePositions(), paramGlyphList.isSubPixPos(), paramGlyphList.isRGBOrder(), paramSunGraphics2DlcdTextContrast, f1, f2, arrayOfLong, paramGlyphList.getPositions());
        }
      });
    }
  }
  
  protected abstract void drawGlyphList(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt2, float paramFloat1, float paramFloat2, long[] paramArrayOfLong, float[] paramArrayOfFloat);
  
  protected abstract void validateContext(SunGraphics2D paramSunGraphics2D, Composite paramComposite);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\BufferedTextPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */