package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.XORComposite;
import sun.java2d.pipe.hw.AccelSurface;

public abstract class BufferedContext
{
  public static final int NO_CONTEXT_FLAGS = 0;
  public static final int SRC_IS_OPAQUE = 1;
  public static final int USE_MASK = 2;
  protected RenderQueue rq;
  protected RenderBuffer buf;
  protected static BufferedContext currentContext;
  private Reference<AccelSurface> validSrcDataRef = new WeakReference(null);
  private Reference<AccelSurface> validDstDataRef = new WeakReference(null);
  private Reference<Region> validClipRef = new WeakReference(null);
  private Reference<Composite> validCompRef = new WeakReference(null);
  private Reference<Paint> validPaintRef = new WeakReference(null);
  private boolean isValidatedPaintJustAColor;
  private int validatedRGB;
  private int validatedFlags;
  private boolean xformInUse;
  private AffineTransform transform;
  
  protected BufferedContext(RenderQueue paramRenderQueue)
  {
    rq = paramRenderQueue;
    buf = paramRenderQueue.getBuffer();
  }
  
  public static void validateContext(AccelSurface paramAccelSurface1, AccelSurface paramAccelSurface2, Region paramRegion, Composite paramComposite, AffineTransform paramAffineTransform, Paint paramPaint, SunGraphics2D paramSunGraphics2D, int paramInt)
  {
    BufferedContext localBufferedContext = paramAccelSurface2.getContext();
    localBufferedContext.validate(paramAccelSurface1, paramAccelSurface2, paramRegion, paramComposite, paramAffineTransform, paramPaint, paramSunGraphics2D, paramInt);
  }
  
  public static void validateContext(AccelSurface paramAccelSurface)
  {
    validateContext(paramAccelSurface, paramAccelSurface, null, null, null, null, null, 0);
  }
  
  public void validate(AccelSurface paramAccelSurface1, AccelSurface paramAccelSurface2, Region paramRegion, Composite paramComposite, AffineTransform paramAffineTransform, Paint paramPaint, SunGraphics2D paramSunGraphics2D, int paramInt)
  {
    int i = 0;
    int j = 0;
    if ((!paramAccelSurface2.isValid()) || (paramAccelSurface2.isSurfaceLost()) || (paramAccelSurface1.isSurfaceLost()))
    {
      invalidateContext();
      throw new InvalidPipeException("bounds changed or surface lost");
    }
    if ((paramPaint instanceof Color))
    {
      int k = ((Color)paramPaint).getRGB();
      if (isValidatedPaintJustAColor)
      {
        if (k != validatedRGB)
        {
          validatedRGB = k;
          j = 1;
        }
      }
      else
      {
        validatedRGB = k;
        j = 1;
        isValidatedPaintJustAColor = true;
      }
    }
    else if (validPaintRef.get() != paramPaint)
    {
      j = 1;
      isValidatedPaintJustAColor = false;
    }
    AccelSurface localAccelSurface1 = (AccelSurface)validSrcDataRef.get();
    AccelSurface localAccelSurface2 = (AccelSurface)validDstDataRef.get();
    if ((currentContext != this) || (paramAccelSurface1 != localAccelSurface1) || (paramAccelSurface2 != localAccelSurface2))
    {
      if (paramAccelSurface2 != localAccelSurface2) {
        i = 1;
      }
      if (paramPaint == null) {
        j = 1;
      }
      setSurfaces(paramAccelSurface1, paramAccelSurface2);
      currentContext = this;
      validSrcDataRef = new WeakReference(paramAccelSurface1);
      validDstDataRef = new WeakReference(paramAccelSurface2);
    }
    Region localRegion = (Region)validClipRef.get();
    if ((paramRegion != localRegion) || (i != 0))
    {
      if (paramRegion != null)
      {
        if ((i != 0) || (localRegion == null) || (!localRegion.isRectangular()) || (!paramRegion.isRectangular()) || (paramRegion.getLoX() != localRegion.getLoX()) || (paramRegion.getLoY() != localRegion.getLoY()) || (paramRegion.getHiX() != localRegion.getHiX()) || (paramRegion.getHiY() != localRegion.getHiY())) {
          setClip(paramRegion);
        }
      }
      else {
        resetClip();
      }
      validClipRef = new WeakReference(paramRegion);
    }
    if ((paramComposite != validCompRef.get()) || (paramInt != validatedFlags))
    {
      if (paramComposite != null) {
        setComposite(paramComposite, paramInt);
      } else {
        resetComposite();
      }
      j = 1;
      validCompRef = new WeakReference(paramComposite);
      validatedFlags = paramInt;
    }
    int m = 0;
    if (paramAffineTransform == null)
    {
      if (xformInUse)
      {
        resetTransform();
        xformInUse = false;
        m = 1;
      }
      else if ((paramSunGraphics2D != null) && (!transform.equals(transform)))
      {
        m = 1;
      }
      if ((paramSunGraphics2D != null) && (m != 0)) {
        transform = new AffineTransform(transform);
      }
    }
    else
    {
      setTransform(paramAffineTransform);
      xformInUse = true;
      m = 1;
    }
    if ((!isValidatedPaintJustAColor) && (m != 0)) {
      j = 1;
    }
    if (j != 0)
    {
      if (paramPaint != null) {
        BufferedPaints.setPaint(rq, paramSunGraphics2D, paramPaint, paramInt);
      } else {
        BufferedPaints.resetPaint(rq);
      }
      validPaintRef = new WeakReference(paramPaint);
    }
    paramAccelSurface2.markDirty();
  }
  
  private void invalidateSurfaces()
  {
    validSrcDataRef.clear();
    validDstDataRef.clear();
  }
  
  private void setSurfaces(AccelSurface paramAccelSurface1, AccelSurface paramAccelSurface2)
  {
    rq.ensureCapacityAndAlignment(20, 4);
    buf.putInt(70);
    buf.putLong(paramAccelSurface1.getNativeOps());
    buf.putLong(paramAccelSurface2.getNativeOps());
  }
  
  private void resetClip()
  {
    rq.ensureCapacity(4);
    buf.putInt(55);
  }
  
  private void setClip(Region paramRegion)
  {
    if (paramRegion.isRectangular())
    {
      rq.ensureCapacity(20);
      buf.putInt(51);
      buf.putInt(paramRegion.getLoX()).putInt(paramRegion.getLoY());
      buf.putInt(paramRegion.getHiX()).putInt(paramRegion.getHiY());
    }
    else
    {
      rq.ensureCapacity(28);
      buf.putInt(52);
      buf.putInt(53);
      int i = buf.position();
      buf.putInt(0);
      int j = 0;
      int k = buf.remaining() / 16;
      int[] arrayOfInt = new int[4];
      SpanIterator localSpanIterator = paramRegion.getSpanIterator();
      while (localSpanIterator.nextSpan(arrayOfInt))
      {
        if (k == 0)
        {
          buf.putInt(i, j);
          rq.flushNow();
          buf.putInt(53);
          i = buf.position();
          buf.putInt(0);
          j = 0;
          k = buf.remaining() / 16;
        }
        buf.putInt(arrayOfInt[0]);
        buf.putInt(arrayOfInt[1]);
        buf.putInt(arrayOfInt[2]);
        buf.putInt(arrayOfInt[3]);
        j++;
        k--;
      }
      buf.putInt(i, j);
      rq.ensureCapacity(4);
      buf.putInt(54);
    }
  }
  
  private void resetComposite()
  {
    rq.ensureCapacity(4);
    buf.putInt(58);
  }
  
  private void setComposite(Composite paramComposite, int paramInt)
  {
    if ((paramComposite instanceof AlphaComposite))
    {
      AlphaComposite localAlphaComposite = (AlphaComposite)paramComposite;
      rq.ensureCapacity(16);
      buf.putInt(56);
      buf.putInt(localAlphaComposite.getRule());
      buf.putFloat(localAlphaComposite.getAlpha());
      buf.putInt(paramInt);
    }
    else if ((paramComposite instanceof XORComposite))
    {
      int i = ((XORComposite)paramComposite).getXorPixel();
      rq.ensureCapacity(8);
      buf.putInt(57);
      buf.putInt(i);
    }
    else
    {
      throw new InternalError("not yet implemented");
    }
  }
  
  private void resetTransform()
  {
    rq.ensureCapacity(4);
    buf.putInt(60);
  }
  
  private void setTransform(AffineTransform paramAffineTransform)
  {
    rq.ensureCapacityAndAlignment(52, 4);
    buf.putInt(59);
    buf.putDouble(paramAffineTransform.getScaleX());
    buf.putDouble(paramAffineTransform.getShearY());
    buf.putDouble(paramAffineTransform.getShearX());
    buf.putDouble(paramAffineTransform.getScaleY());
    buf.putDouble(paramAffineTransform.getTranslateX());
    buf.putDouble(paramAffineTransform.getTranslateY());
  }
  
  public void invalidateContext()
  {
    resetTransform();
    resetComposite();
    resetClip();
    BufferedPaints.resetPaint(rq);
    invalidateSurfaces();
    validCompRef.clear();
    validClipRef.clear();
    validPaintRef.clear();
    isValidatedPaintJustAColor = false;
    xformInUse = false;
  }
  
  public abstract RenderQueue getRenderQueue();
  
  public abstract void saveState();
  
  public abstract void restoreState();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\BufferedContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */