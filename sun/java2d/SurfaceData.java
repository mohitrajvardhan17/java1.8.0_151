package sun.java2d;

import java.awt.AWTPermission;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.security.Permission;
import sun.awt.image.SurfaceManager;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.DrawGlyphList;
import sun.java2d.loops.DrawGlyphListAA;
import sun.java2d.loops.DrawGlyphListLCD;
import sun.java2d.loops.DrawLine;
import sun.java2d.loops.DrawParallelogram;
import sun.java2d.loops.DrawPath;
import sun.java2d.loops.DrawPolygons;
import sun.java2d.loops.DrawRect;
import sun.java2d.loops.FillParallelogram;
import sun.java2d.loops.FillPath;
import sun.java2d.loops.FillRect;
import sun.java2d.loops.FillSpans;
import sun.java2d.loops.FontInfo;
import sun.java2d.loops.MaskFill;
import sun.java2d.loops.RenderCache;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.AAShapePipe;
import sun.java2d.pipe.AATextRenderer;
import sun.java2d.pipe.AlphaColorPipe;
import sun.java2d.pipe.AlphaPaintPipe;
import sun.java2d.pipe.CompositePipe;
import sun.java2d.pipe.DrawImage;
import sun.java2d.pipe.DrawImagePipe;
import sun.java2d.pipe.GeneralCompositePipe;
import sun.java2d.pipe.LCDTextRenderer;
import sun.java2d.pipe.LoopBasedPipe;
import sun.java2d.pipe.LoopPipe;
import sun.java2d.pipe.OutlineTextRenderer;
import sun.java2d.pipe.ParallelogramPipe;
import sun.java2d.pipe.PixelDrawPipe;
import sun.java2d.pipe.PixelFillPipe;
import sun.java2d.pipe.PixelToParallelogramConverter;
import sun.java2d.pipe.PixelToShapeConverter;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.SolidTextRenderer;
import sun.java2d.pipe.SpanClipRenderer;
import sun.java2d.pipe.SpanShapeRenderer;
import sun.java2d.pipe.SpanShapeRenderer.Composite;
import sun.java2d.pipe.TextPipe;
import sun.java2d.pipe.TextRenderer;

public abstract class SurfaceData
  implements Transparency, DisposerTarget, StateTrackable, Surface
{
  private long pData;
  private boolean valid;
  private boolean surfaceLost;
  private SurfaceType surfaceType;
  private ColorModel colorModel;
  private Object disposerReferent = new Object();
  private Object blitProxyKey;
  private StateTrackableDelegate stateDelegate;
  protected static final LoopPipe colorPrimitives = new LoopPipe();
  public static final TextPipe outlineTextRenderer = new OutlineTextRenderer();
  public static final TextPipe solidTextRenderer = new SolidTextRenderer();
  public static final TextPipe aaTextRenderer = new AATextRenderer();
  public static final TextPipe lcdTextRenderer = new LCDTextRenderer();
  protected static final AlphaColorPipe colorPipe = new AlphaColorPipe();
  protected static final PixelToShapeConverter colorViaShape = new PixelToShapeLoopConverter(colorPrimitives);
  protected static final PixelToParallelogramConverter colorViaPgram = new PixelToPgramLoopConverter(colorPrimitives, colorPrimitives, 1.0D, 0.25D, true);
  protected static final TextPipe colorText = new TextRenderer(colorPipe);
  protected static final CompositePipe clipColorPipe = new SpanClipRenderer(colorPipe);
  protected static final TextPipe clipColorText = new TextRenderer(clipColorPipe);
  protected static final AAShapePipe AAColorShape = new AAShapePipe(colorPipe);
  protected static final PixelToParallelogramConverter AAColorViaShape = makeConverter(AAColorShape);
  protected static final PixelToParallelogramConverter AAColorViaPgram = makeConverter(AAColorShape, colorPipe);
  protected static final AAShapePipe AAClipColorShape = new AAShapePipe(clipColorPipe);
  protected static final PixelToParallelogramConverter AAClipColorViaShape = makeConverter(AAClipColorShape);
  protected static final CompositePipe paintPipe = new AlphaPaintPipe();
  protected static final SpanShapeRenderer paintShape = new SpanShapeRenderer.Composite(paintPipe);
  protected static final PixelToShapeConverter paintViaShape = new PixelToShapeConverter(paintShape);
  protected static final TextPipe paintText = new TextRenderer(paintPipe);
  protected static final CompositePipe clipPaintPipe = new SpanClipRenderer(paintPipe);
  protected static final TextPipe clipPaintText = new TextRenderer(clipPaintPipe);
  protected static final AAShapePipe AAPaintShape = new AAShapePipe(paintPipe);
  protected static final PixelToParallelogramConverter AAPaintViaShape = makeConverter(AAPaintShape);
  protected static final AAShapePipe AAClipPaintShape = new AAShapePipe(clipPaintPipe);
  protected static final PixelToParallelogramConverter AAClipPaintViaShape = makeConverter(AAClipPaintShape);
  protected static final CompositePipe compPipe = new GeneralCompositePipe();
  protected static final SpanShapeRenderer compShape = new SpanShapeRenderer.Composite(compPipe);
  protected static final PixelToShapeConverter compViaShape = new PixelToShapeConverter(compShape);
  protected static final TextPipe compText = new TextRenderer(compPipe);
  protected static final CompositePipe clipCompPipe = new SpanClipRenderer(compPipe);
  protected static final TextPipe clipCompText = new TextRenderer(clipCompPipe);
  protected static final AAShapePipe AACompShape = new AAShapePipe(compPipe);
  protected static final PixelToParallelogramConverter AACompViaShape = makeConverter(AACompShape);
  protected static final AAShapePipe AAClipCompShape = new AAShapePipe(clipCompPipe);
  protected static final PixelToParallelogramConverter AAClipCompViaShape = makeConverter(AAClipCompShape);
  protected static final DrawImagePipe imagepipe = new DrawImage();
  static final int LOOP_UNKNOWN = 0;
  static final int LOOP_FOUND = 1;
  static final int LOOP_NOTFOUND = 2;
  int haveLCDLoop;
  int havePgramXORLoop;
  int havePgramSolidLoop;
  private static RenderCache loopcache = new RenderCache(30);
  static Permission compPermission;
  
  private static native void initIDs();
  
  protected SurfaceData(SurfaceType paramSurfaceType, ColorModel paramColorModel)
  {
    this(StateTrackable.State.STABLE, paramSurfaceType, paramColorModel);
  }
  
  protected SurfaceData(StateTrackable.State paramState, SurfaceType paramSurfaceType, ColorModel paramColorModel)
  {
    this(StateTrackableDelegate.createInstance(paramState), paramSurfaceType, paramColorModel);
  }
  
  protected SurfaceData(StateTrackableDelegate paramStateTrackableDelegate, SurfaceType paramSurfaceType, ColorModel paramColorModel)
  {
    stateDelegate = paramStateTrackableDelegate;
    colorModel = paramColorModel;
    surfaceType = paramSurfaceType;
    valid = true;
  }
  
  protected SurfaceData(StateTrackable.State paramState)
  {
    stateDelegate = StateTrackableDelegate.createInstance(paramState);
    valid = true;
  }
  
  protected void setBlitProxyKey(Object paramObject)
  {
    if (SurfaceDataProxy.isCachingAllowed()) {
      blitProxyKey = paramObject;
    }
  }
  
  public SurfaceData getSourceSurfaceData(Image paramImage, int paramInt, CompositeType paramCompositeType, Color paramColor)
  {
    SurfaceManager localSurfaceManager = SurfaceManager.getManager(paramImage);
    SurfaceData localSurfaceData = localSurfaceManager.getPrimarySurfaceData();
    if ((paramImage.getAccelerationPriority() > 0.0F) && (blitProxyKey != null))
    {
      SurfaceDataProxy localSurfaceDataProxy = (SurfaceDataProxy)localSurfaceManager.getCacheData(blitProxyKey);
      if ((localSurfaceDataProxy == null) || (!localSurfaceDataProxy.isValid()))
      {
        if (localSurfaceData.getState() == StateTrackable.State.UNTRACKABLE) {
          localSurfaceDataProxy = SurfaceDataProxy.UNCACHED;
        } else {
          localSurfaceDataProxy = makeProxyFor(localSurfaceData);
        }
        localSurfaceManager.setCacheData(blitProxyKey, localSurfaceDataProxy);
      }
      localSurfaceData = localSurfaceDataProxy.replaceData(localSurfaceData, paramInt, paramCompositeType, paramColor);
    }
    return localSurfaceData;
  }
  
  public SurfaceDataProxy makeProxyFor(SurfaceData paramSurfaceData)
  {
    return SurfaceDataProxy.UNCACHED;
  }
  
  public static SurfaceData getPrimarySurfaceData(Image paramImage)
  {
    SurfaceManager localSurfaceManager = SurfaceManager.getManager(paramImage);
    return localSurfaceManager.getPrimarySurfaceData();
  }
  
  public static SurfaceData restoreContents(Image paramImage)
  {
    SurfaceManager localSurfaceManager = SurfaceManager.getManager(paramImage);
    return localSurfaceManager.restoreContents();
  }
  
  public StateTrackable.State getState()
  {
    return stateDelegate.getState();
  }
  
  public StateTracker getStateTracker()
  {
    return stateDelegate.getStateTracker();
  }
  
  public final void markDirty()
  {
    stateDelegate.markDirty();
  }
  
  public void setSurfaceLost(boolean paramBoolean)
  {
    surfaceLost = paramBoolean;
    stateDelegate.markDirty();
  }
  
  public boolean isSurfaceLost()
  {
    return surfaceLost;
  }
  
  public final boolean isValid()
  {
    return valid;
  }
  
  public Object getDisposerReferent()
  {
    return disposerReferent;
  }
  
  public long getNativeOps()
  {
    return pData;
  }
  
  public void invalidate()
  {
    valid = false;
    stateDelegate.markDirty();
  }
  
  public abstract SurfaceData getReplacement();
  
  private static PixelToParallelogramConverter makeConverter(AAShapePipe paramAAShapePipe, ParallelogramPipe paramParallelogramPipe)
  {
    return new PixelToParallelogramConverter(paramAAShapePipe, paramParallelogramPipe, 0.125D, 0.499D, false);
  }
  
  private static PixelToParallelogramConverter makeConverter(AAShapePipe paramAAShapePipe)
  {
    return makeConverter(paramAAShapePipe, paramAAShapePipe);
  }
  
  public boolean canRenderLCDText(SunGraphics2D paramSunGraphics2D)
  {
    if ((compositeState <= 0) && (paintState <= 1) && (clipState <= 1) && (surfaceData.getTransparency() == 1))
    {
      if (haveLCDLoop == 0)
      {
        DrawGlyphListLCD localDrawGlyphListLCD = DrawGlyphListLCD.locate(SurfaceType.AnyColor, CompositeType.SrcNoEa, getSurfaceType());
        haveLCDLoop = (localDrawGlyphListLCD != null ? 1 : 2);
      }
      return haveLCDLoop == 1;
    }
    return false;
  }
  
  public boolean canRenderParallelograms(SunGraphics2D paramSunGraphics2D)
  {
    if (paintState <= 1)
    {
      FillParallelogram localFillParallelogram;
      if (compositeState == 2)
      {
        if (havePgramXORLoop == 0)
        {
          localFillParallelogram = FillParallelogram.locate(SurfaceType.AnyColor, CompositeType.Xor, getSurfaceType());
          havePgramXORLoop = (localFillParallelogram != null ? 1 : 2);
        }
        return havePgramXORLoop == 1;
      }
      if ((compositeState <= 0) && (antialiasHint != 2) && (clipState != 2))
      {
        if (havePgramSolidLoop == 0)
        {
          localFillParallelogram = FillParallelogram.locate(SurfaceType.AnyColor, CompositeType.SrcNoEa, getSurfaceType());
          havePgramSolidLoop = (localFillParallelogram != null ? 1 : 2);
        }
        return havePgramSolidLoop == 1;
      }
    }
    return false;
  }
  
  public void validatePipe(SunGraphics2D paramSunGraphics2D)
  {
    imagepipe = imagepipe;
    Object localObject;
    if (compositeState == 2)
    {
      if (paintState > 1)
      {
        drawpipe = paintViaShape;
        fillpipe = paintViaShape;
        shapepipe = paintShape;
        textpipe = outlineTextRenderer;
      }
      else
      {
        if (canRenderParallelograms(paramSunGraphics2D))
        {
          localObject = colorViaPgram;
          shapepipe = colorViaPgram;
        }
        else
        {
          localObject = colorViaShape;
          shapepipe = colorPrimitives;
        }
        if (clipState == 2)
        {
          drawpipe = ((PixelDrawPipe)localObject);
          fillpipe = ((PixelFillPipe)localObject);
          textpipe = outlineTextRenderer;
        }
        else
        {
          if (transformState >= 3)
          {
            drawpipe = ((PixelDrawPipe)localObject);
            fillpipe = ((PixelFillPipe)localObject);
          }
          else
          {
            if (strokeState != 0) {
              drawpipe = ((PixelDrawPipe)localObject);
            } else {
              drawpipe = colorPrimitives;
            }
            fillpipe = colorPrimitives;
          }
          textpipe = solidTextRenderer;
        }
      }
    }
    else if (compositeState == 3)
    {
      if (antialiasHint == 2)
      {
        if (clipState == 2)
        {
          drawpipe = AAClipCompViaShape;
          fillpipe = AAClipCompViaShape;
          shapepipe = AAClipCompViaShape;
          textpipe = clipCompText;
        }
        else
        {
          drawpipe = AACompViaShape;
          fillpipe = AACompViaShape;
          shapepipe = AACompViaShape;
          textpipe = compText;
        }
      }
      else
      {
        drawpipe = compViaShape;
        fillpipe = compViaShape;
        shapepipe = compShape;
        if (clipState == 2) {
          textpipe = clipCompText;
        } else {
          textpipe = compText;
        }
      }
    }
    else if (antialiasHint == 2)
    {
      alphafill = getMaskFill(paramSunGraphics2D);
      if (alphafill != null)
      {
        if (clipState == 2)
        {
          drawpipe = AAClipColorViaShape;
          fillpipe = AAClipColorViaShape;
          shapepipe = AAClipColorViaShape;
          textpipe = clipColorText;
        }
        else
        {
          localObject = alphafill.canDoParallelograms() ? AAColorViaPgram : AAColorViaShape;
          drawpipe = ((PixelDrawPipe)localObject);
          fillpipe = ((PixelFillPipe)localObject);
          shapepipe = ((ShapeDrawPipe)localObject);
          if ((paintState > 1) || (compositeState > 0)) {
            textpipe = colorText;
          } else {
            textpipe = getTextPipe(paramSunGraphics2D, true);
          }
        }
      }
      else if (clipState == 2)
      {
        drawpipe = AAClipPaintViaShape;
        fillpipe = AAClipPaintViaShape;
        shapepipe = AAClipPaintViaShape;
        textpipe = clipPaintText;
      }
      else
      {
        drawpipe = AAPaintViaShape;
        fillpipe = AAPaintViaShape;
        shapepipe = AAPaintViaShape;
        textpipe = paintText;
      }
    }
    else if ((paintState > 1) || (compositeState > 0) || (clipState == 2))
    {
      drawpipe = paintViaShape;
      fillpipe = paintViaShape;
      shapepipe = paintShape;
      alphafill = getMaskFill(paramSunGraphics2D);
      if (alphafill != null)
      {
        if (clipState == 2) {
          textpipe = clipColorText;
        } else {
          textpipe = colorText;
        }
      }
      else if (clipState == 2) {
        textpipe = clipPaintText;
      } else {
        textpipe = paintText;
      }
    }
    else
    {
      if (canRenderParallelograms(paramSunGraphics2D))
      {
        localObject = colorViaPgram;
        shapepipe = colorViaPgram;
      }
      else
      {
        localObject = colorViaShape;
        shapepipe = colorPrimitives;
      }
      if (transformState >= 3)
      {
        drawpipe = ((PixelDrawPipe)localObject);
        fillpipe = ((PixelFillPipe)localObject);
      }
      else
      {
        if (strokeState != 0) {
          drawpipe = ((PixelDrawPipe)localObject);
        } else {
          drawpipe = colorPrimitives;
        }
        fillpipe = colorPrimitives;
      }
      textpipe = getTextPipe(paramSunGraphics2D, false);
    }
    if (((textpipe instanceof LoopBasedPipe)) || ((shapepipe instanceof LoopBasedPipe)) || ((fillpipe instanceof LoopBasedPipe)) || ((drawpipe instanceof LoopBasedPipe)) || ((imagepipe instanceof LoopBasedPipe))) {
      loops = getRenderLoops(paramSunGraphics2D);
    }
  }
  
  private TextPipe getTextPipe(SunGraphics2D paramSunGraphics2D, boolean paramBoolean)
  {
    switch (textAntialiasHint)
    {
    case 0: 
      if (paramBoolean) {
        return aaTextRenderer;
      }
      return solidTextRenderer;
    case 1: 
      return solidTextRenderer;
    case 2: 
      return aaTextRenderer;
    }
    switch (getFontInfoaaHint)
    {
    case 4: 
    case 6: 
      return lcdTextRenderer;
    case 2: 
      return aaTextRenderer;
    case 1: 
      return solidTextRenderer;
    }
    if (paramBoolean) {
      return aaTextRenderer;
    }
    return solidTextRenderer;
  }
  
  private static SurfaceType getPaintSurfaceType(SunGraphics2D paramSunGraphics2D)
  {
    switch (paintState)
    {
    case 0: 
      return SurfaceType.OpaqueColor;
    case 1: 
      return SurfaceType.AnyColor;
    case 2: 
      if (paint.getTransparency() == 1) {
        return SurfaceType.OpaqueGradientPaint;
      }
      return SurfaceType.GradientPaint;
    case 3: 
      if (paint.getTransparency() == 1) {
        return SurfaceType.OpaqueLinearGradientPaint;
      }
      return SurfaceType.LinearGradientPaint;
    case 4: 
      if (paint.getTransparency() == 1) {
        return SurfaceType.OpaqueRadialGradientPaint;
      }
      return SurfaceType.RadialGradientPaint;
    case 5: 
      if (paint.getTransparency() == 1) {
        return SurfaceType.OpaqueTexturePaint;
      }
      return SurfaceType.TexturePaint;
    }
    return SurfaceType.AnyPaint;
  }
  
  private static CompositeType getFillCompositeType(SunGraphics2D paramSunGraphics2D)
  {
    CompositeType localCompositeType = imageComp;
    if (compositeState == 0) {
      if (localCompositeType == CompositeType.SrcOverNoEa) {
        localCompositeType = CompositeType.OpaqueSrcOverNoEa;
      } else {
        localCompositeType = CompositeType.SrcNoEa;
      }
    }
    return localCompositeType;
  }
  
  protected MaskFill getMaskFill(SunGraphics2D paramSunGraphics2D)
  {
    SurfaceType localSurfaceType1 = getPaintSurfaceType(paramSunGraphics2D);
    CompositeType localCompositeType = getFillCompositeType(paramSunGraphics2D);
    SurfaceType localSurfaceType2 = getSurfaceType();
    return MaskFill.getFromCache(localSurfaceType1, localCompositeType, localSurfaceType2);
  }
  
  public RenderLoops getRenderLoops(SunGraphics2D paramSunGraphics2D)
  {
    SurfaceType localSurfaceType1 = getPaintSurfaceType(paramSunGraphics2D);
    CompositeType localCompositeType = getFillCompositeType(paramSunGraphics2D);
    SurfaceType localSurfaceType2 = paramSunGraphics2D.getSurfaceData().getSurfaceType();
    Object localObject = loopcache.get(localSurfaceType1, localCompositeType, localSurfaceType2);
    if (localObject != null) {
      return (RenderLoops)localObject;
    }
    RenderLoops localRenderLoops = makeRenderLoops(localSurfaceType1, localCompositeType, localSurfaceType2);
    loopcache.put(localSurfaceType1, localCompositeType, localSurfaceType2, localRenderLoops);
    return localRenderLoops;
  }
  
  public static RenderLoops makeRenderLoops(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    RenderLoops localRenderLoops = new RenderLoops();
    drawLineLoop = DrawLine.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    fillRectLoop = FillRect.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    drawRectLoop = DrawRect.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    drawPolygonsLoop = DrawPolygons.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    drawPathLoop = DrawPath.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    fillPathLoop = FillPath.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    fillSpansLoop = FillSpans.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    fillParallelogramLoop = FillParallelogram.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    drawParallelogramLoop = DrawParallelogram.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    drawGlyphListLoop = DrawGlyphList.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    drawGlyphListAALoop = DrawGlyphListAA.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    drawGlyphListLCDLoop = DrawGlyphListLCD.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    return localRenderLoops;
  }
  
  public abstract GraphicsConfiguration getDeviceConfiguration();
  
  public final SurfaceType getSurfaceType()
  {
    return surfaceType;
  }
  
  public final ColorModel getColorModel()
  {
    return colorModel;
  }
  
  public int getTransparency()
  {
    return getColorModel().getTransparency();
  }
  
  public abstract Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public boolean useTightBBoxes()
  {
    return true;
  }
  
  public int pixelFor(int paramInt)
  {
    return surfaceType.pixelFor(paramInt, colorModel);
  }
  
  public int pixelFor(Color paramColor)
  {
    return pixelFor(paramColor.getRGB());
  }
  
  public int rgbFor(int paramInt)
  {
    return surfaceType.rgbFor(paramInt, colorModel);
  }
  
  public abstract Rectangle getBounds();
  
  protected void checkCustomComposite()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      if (compPermission == null) {
        compPermission = new AWTPermission("readDisplayPixels");
      }
      localSecurityManager.checkPermission(compPermission);
    }
  }
  
  protected static native boolean isOpaqueGray(IndexColorModel paramIndexColorModel);
  
  public static boolean isNull(SurfaceData paramSurfaceData)
  {
    return (paramSurfaceData == null) || (paramSurfaceData == NullSurfaceData.theInstance);
  }
  
  public boolean copyArea(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    return false;
  }
  
  public void flush() {}
  
  public abstract Object getDestination();
  
  public int getDefaultScale()
  {
    return 1;
  }
  
  static {}
  
  static class PixelToPgramLoopConverter
    extends PixelToParallelogramConverter
    implements LoopBasedPipe
  {
    public PixelToPgramLoopConverter(ShapeDrawPipe paramShapeDrawPipe, ParallelogramPipe paramParallelogramPipe, double paramDouble1, double paramDouble2, boolean paramBoolean)
    {
      super(paramParallelogramPipe, paramDouble1, paramDouble2, paramBoolean);
    }
  }
  
  static class PixelToShapeLoopConverter
    extends PixelToShapeConverter
    implements LoopBasedPipe
  {
    public PixelToShapeLoopConverter(ShapeDrawPipe paramShapeDrawPipe)
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\SurfaceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */