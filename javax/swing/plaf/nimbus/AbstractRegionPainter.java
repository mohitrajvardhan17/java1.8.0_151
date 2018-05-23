package javax.swing.plaf.nimbus;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.image.VolatileImage;
import java.awt.print.PrinterGraphics;
import java.lang.reflect.Method;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.Painter;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import sun.reflect.misc.MethodUtil;

public abstract class AbstractRegionPainter
  implements Painter<JComponent>
{
  private PaintContext ctx;
  private float f;
  private float leftWidth;
  private float topHeight;
  private float centerWidth;
  private float centerHeight;
  private float rightWidth;
  private float bottomHeight;
  private float leftScale;
  private float topScale;
  private float centerHScale;
  private float centerVScale;
  private float rightScale;
  private float bottomScale;
  
  protected AbstractRegionPainter() {}
  
  public final void paint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      return;
    }
    Object[] arrayOfObject = getExtendedCacheKeys(paramJComponent);
    ctx = getPaintContext();
    AbstractRegionPainter.PaintContext.CacheMode localCacheMode = ctx == null ? AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING : ctx.cacheMode;
    if ((localCacheMode == AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING) || (!ImageCache.getInstance().isImageCachable(paramInt1, paramInt2)) || ((paramGraphics2D instanceof PrinterGraphics))) {
      paint0(paramGraphics2D, paramJComponent, paramInt1, paramInt2, arrayOfObject);
    } else if (localCacheMode == AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES) {
      paintWithFixedSizeCaching(paramGraphics2D, paramJComponent, paramInt1, paramInt2, arrayOfObject);
    } else {
      paintWith9SquareCaching(paramGraphics2D, ctx, paramJComponent, paramInt1, paramInt2, arrayOfObject);
    }
  }
  
  protected Object[] getExtendedCacheKeys(JComponent paramJComponent)
  {
    return null;
  }
  
  protected abstract PaintContext getPaintContext();
  
  protected void configureGraphics(Graphics2D paramGraphics2D)
  {
    paramGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  }
  
  protected abstract void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject);
  
  protected final float decodeX(float paramFloat)
  {
    if ((paramFloat >= 0.0F) && (paramFloat <= 1.0F)) {
      return paramFloat * leftWidth;
    }
    if ((paramFloat > 1.0F) && (paramFloat < 2.0F)) {
      return (paramFloat - 1.0F) * centerWidth + leftWidth;
    }
    if ((paramFloat >= 2.0F) && (paramFloat <= 3.0F)) {
      return (paramFloat - 2.0F) * rightWidth + leftWidth + centerWidth;
    }
    throw new IllegalArgumentException("Invalid x");
  }
  
  protected final float decodeY(float paramFloat)
  {
    if ((paramFloat >= 0.0F) && (paramFloat <= 1.0F)) {
      return paramFloat * topHeight;
    }
    if ((paramFloat > 1.0F) && (paramFloat < 2.0F)) {
      return (paramFloat - 1.0F) * centerHeight + topHeight;
    }
    if ((paramFloat >= 2.0F) && (paramFloat <= 3.0F)) {
      return (paramFloat - 2.0F) * bottomHeight + topHeight + centerHeight;
    }
    throw new IllegalArgumentException("Invalid y");
  }
  
  protected final float decodeAnchorX(float paramFloat1, float paramFloat2)
  {
    if ((paramFloat1 >= 0.0F) && (paramFloat1 <= 1.0F)) {
      return decodeX(paramFloat1) + paramFloat2 * leftScale;
    }
    if ((paramFloat1 > 1.0F) && (paramFloat1 < 2.0F)) {
      return decodeX(paramFloat1) + paramFloat2 * centerHScale;
    }
    if ((paramFloat1 >= 2.0F) && (paramFloat1 <= 3.0F)) {
      return decodeX(paramFloat1) + paramFloat2 * rightScale;
    }
    throw new IllegalArgumentException("Invalid x");
  }
  
  protected final float decodeAnchorY(float paramFloat1, float paramFloat2)
  {
    if ((paramFloat1 >= 0.0F) && (paramFloat1 <= 1.0F)) {
      return decodeY(paramFloat1) + paramFloat2 * topScale;
    }
    if ((paramFloat1 > 1.0F) && (paramFloat1 < 2.0F)) {
      return decodeY(paramFloat1) + paramFloat2 * centerVScale;
    }
    if ((paramFloat1 >= 2.0F) && (paramFloat1 <= 3.0F)) {
      return decodeY(paramFloat1) + paramFloat2 * bottomScale;
    }
    throw new IllegalArgumentException("Invalid y");
  }
  
  protected final Color decodeColor(String paramString, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt)
  {
    if ((UIManager.getLookAndFeel() instanceof NimbusLookAndFeel))
    {
      NimbusLookAndFeel localNimbusLookAndFeel = (NimbusLookAndFeel)UIManager.getLookAndFeel();
      return localNimbusLookAndFeel.getDerivedColor(paramString, paramFloat1, paramFloat2, paramFloat3, paramInt, true);
    }
    return Color.getHSBColor(paramFloat1, paramFloat2, paramFloat3);
  }
  
  protected final Color decodeColor(Color paramColor1, Color paramColor2, float paramFloat)
  {
    return new Color(NimbusLookAndFeel.deriveARGB(paramColor1, paramColor2, paramFloat));
  }
  
  protected final LinearGradientPaint decodeGradient(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float[] paramArrayOfFloat, Color[] paramArrayOfColor)
  {
    if ((paramFloat1 == paramFloat3) && (paramFloat2 == paramFloat4)) {
      paramFloat4 += 1.0E-5F;
    }
    return new LinearGradientPaint(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramArrayOfFloat, paramArrayOfColor);
  }
  
  protected final RadialGradientPaint decodeRadialGradient(float paramFloat1, float paramFloat2, float paramFloat3, float[] paramArrayOfFloat, Color[] paramArrayOfColor)
  {
    if (paramFloat3 == 0.0F) {
      paramFloat3 = 1.0E-5F;
    }
    return new RadialGradientPaint(paramFloat1, paramFloat2, paramFloat3, paramArrayOfFloat, paramArrayOfColor);
  }
  
  protected final Color getComponentColor(JComponent paramJComponent, String paramString, Color paramColor, float paramFloat1, float paramFloat2, int paramInt)
  {
    Color localColor = null;
    Object localObject1;
    if (paramJComponent != null) {
      if ("background".equals(paramString))
      {
        localColor = paramJComponent.getBackground();
      }
      else if ("foreground".equals(paramString))
      {
        localColor = paramJComponent.getForeground();
      }
      else if (((paramJComponent instanceof JList)) && ("selectionForeground".equals(paramString)))
      {
        localColor = ((JList)paramJComponent).getSelectionForeground();
      }
      else if (((paramJComponent instanceof JList)) && ("selectionBackground".equals(paramString)))
      {
        localColor = ((JList)paramJComponent).getSelectionBackground();
      }
      else if (((paramJComponent instanceof JTable)) && ("selectionForeground".equals(paramString)))
      {
        localColor = ((JTable)paramJComponent).getSelectionForeground();
      }
      else if (((paramJComponent instanceof JTable)) && ("selectionBackground".equals(paramString)))
      {
        localColor = ((JTable)paramJComponent).getSelectionBackground();
      }
      else
      {
        localObject1 = "get" + Character.toUpperCase(paramString.charAt(0)) + paramString.substring(1);
        try
        {
          Method localMethod = MethodUtil.getMethod(paramJComponent.getClass(), (String)localObject1, null);
          localColor = (Color)MethodUtil.invoke(localMethod, paramJComponent, null);
        }
        catch (Exception localException) {}
        if (localColor == null)
        {
          Object localObject2 = paramJComponent.getClientProperty(paramString);
          if ((localObject2 instanceof Color)) {
            localColor = (Color)localObject2;
          }
        }
      }
    }
    if ((localColor == null) || ((localColor instanceof UIResource))) {
      return paramColor;
    }
    if ((paramFloat1 != 0.0F) || (paramFloat2 != 0.0F) || (paramInt != 0))
    {
      localObject1 = Color.RGBtoHSB(localColor.getRed(), localColor.getGreen(), localColor.getBlue(), null);
      localObject1[1] = clamp(localObject1[1] + paramFloat1);
      localObject1[2] = clamp(localObject1[2] + paramFloat2);
      int i = clamp(localColor.getAlpha() + paramInt);
      return new Color(Color.HSBtoRGB(localObject1[0], localObject1[1], localObject1[2]) & 0xFFFFFF | i << 24);
    }
    return localColor;
  }
  
  private void prepare(float paramFloat1, float paramFloat2)
  {
    if ((ctx == null) || (ctx.canvasSize == null))
    {
      f = 1.0F;
      leftWidth = (centerWidth = rightWidth = 0.0F);
      topHeight = (centerHeight = bottomHeight = 0.0F);
      leftScale = (centerHScale = rightScale = 0.0F);
      topScale = (centerVScale = bottomScale = 0.0F);
      return;
    }
    Number localNumber = (Number)UIManager.get("scale");
    f = (localNumber == null ? 1.0F : localNumber.floatValue());
    if (ctx.inverted)
    {
      centerWidth = ((ctx.b - ctx.a) * f);
      float f1 = paramFloat1 - centerWidth;
      leftWidth = (f1 * ctx.aPercent);
      rightWidth = (f1 * ctx.bPercent);
      centerHeight = ((ctx.d - ctx.c) * f);
      f1 = paramFloat2 - centerHeight;
      topHeight = (f1 * ctx.cPercent);
      bottomHeight = (f1 * ctx.dPercent);
    }
    else
    {
      leftWidth = (ctx.a * f);
      rightWidth = ((float)(ctx.canvasSize.getWidth() - ctx.b) * f);
      centerWidth = (paramFloat1 - leftWidth - rightWidth);
      topHeight = (ctx.c * f);
      bottomHeight = ((float)(ctx.canvasSize.getHeight() - ctx.d) * f);
      centerHeight = (paramFloat2 - topHeight - bottomHeight);
    }
    leftScale = (ctx.a == 0.0F ? 0.0F : leftWidth / ctx.a);
    centerHScale = (ctx.b - ctx.a == 0.0F ? 0.0F : centerWidth / (ctx.b - ctx.a));
    rightScale = (ctx.canvasSize.width - ctx.b == 0.0F ? 0.0F : rightWidth / (ctx.canvasSize.width - ctx.b));
    topScale = (ctx.c == 0.0F ? 0.0F : topHeight / ctx.c);
    centerVScale = (ctx.d - ctx.c == 0.0F ? 0.0F : centerHeight / (ctx.d - ctx.c));
    bottomScale = (ctx.canvasSize.height - ctx.d == 0.0F ? 0.0F : bottomHeight / (ctx.canvasSize.height - ctx.d));
  }
  
  private void paintWith9SquareCaching(Graphics2D paramGraphics2D, PaintContext paramPaintContext, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
  {
    Dimension localDimension = canvasSize;
    Insets localInsets1 = stretchingInsets;
    if ((paramInt1 <= width * maxHorizontalScaleFactor) && (paramInt2 <= height * maxVerticalScaleFactor))
    {
      VolatileImage localVolatileImage = getImage(paramGraphics2D.getDeviceConfiguration(), paramJComponent, width, height, paramArrayOfObject);
      if (localVolatileImage != null)
      {
        Insets localInsets2;
        if (inverted)
        {
          int i = (paramInt1 - (width - (left + right))) / 2;
          int j = (paramInt2 - (height - (top + bottom))) / 2;
          localInsets2 = new Insets(j, i, j, i);
        }
        else
        {
          localInsets2 = localInsets1;
        }
        Object localObject = paramGraphics2D.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        paramGraphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        ImageScalingHelper.paint(paramGraphics2D, 0, 0, paramInt1, paramInt2, localVolatileImage, localInsets1, localInsets2, ImageScalingHelper.PaintType.PAINT9_STRETCH, 512);
        paramGraphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, localObject != null ? localObject : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
      }
      else
      {
        paint0(paramGraphics2D, paramJComponent, paramInt1, paramInt2, paramArrayOfObject);
      }
    }
    else
    {
      paint0(paramGraphics2D, paramJComponent, paramInt1, paramInt2, paramArrayOfObject);
    }
  }
  
  private void paintWithFixedSizeCaching(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
  {
    VolatileImage localVolatileImage = getImage(paramGraphics2D.getDeviceConfiguration(), paramJComponent, paramInt1, paramInt2, paramArrayOfObject);
    if (localVolatileImage != null) {
      paramGraphics2D.drawImage(localVolatileImage, 0, 0, null);
    } else {
      paint0(paramGraphics2D, paramJComponent, paramInt1, paramInt2, paramArrayOfObject);
    }
  }
  
  private VolatileImage getImage(GraphicsConfiguration paramGraphicsConfiguration, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
  {
    ImageCache localImageCache = ImageCache.getInstance();
    VolatileImage localVolatileImage = (VolatileImage)localImageCache.getImage(paramGraphicsConfiguration, paramInt1, paramInt2, new Object[] { this, paramArrayOfObject });
    int i = 0;
    do
    {
      int j = 2;
      if (localVolatileImage != null) {
        j = localVolatileImage.validate(paramGraphicsConfiguration);
      }
      if ((j == 2) || (j == 1))
      {
        if ((localVolatileImage == null) || (localVolatileImage.getWidth() != paramInt1) || (localVolatileImage.getHeight() != paramInt2) || (j == 2))
        {
          if (localVolatileImage != null)
          {
            localVolatileImage.flush();
            localVolatileImage = null;
          }
          localVolatileImage = paramGraphicsConfiguration.createCompatibleVolatileImage(paramInt1, paramInt2, 3);
          localImageCache.setImage(localVolatileImage, paramGraphicsConfiguration, paramInt1, paramInt2, new Object[] { this, paramArrayOfObject });
        }
        Graphics2D localGraphics2D = localVolatileImage.createGraphics();
        localGraphics2D.setComposite(AlphaComposite.Clear);
        localGraphics2D.fillRect(0, 0, paramInt1, paramInt2);
        localGraphics2D.setComposite(AlphaComposite.SrcOver);
        configureGraphics(localGraphics2D);
        paint0(localGraphics2D, paramJComponent, paramInt1, paramInt2, paramArrayOfObject);
        localGraphics2D.dispose();
      }
    } while ((localVolatileImage.contentsLost()) && (i++ < 3));
    if (i == 3) {
      return null;
    }
    return localVolatileImage;
  }
  
  private void paint0(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
  {
    prepare(paramInt1, paramInt2);
    paramGraphics2D = (Graphics2D)paramGraphics2D.create();
    configureGraphics(paramGraphics2D);
    doPaint(paramGraphics2D, paramJComponent, paramInt1, paramInt2, paramArrayOfObject);
    paramGraphics2D.dispose();
  }
  
  private float clamp(float paramFloat)
  {
    if (paramFloat < 0.0F) {
      paramFloat = 0.0F;
    } else if (paramFloat > 1.0F) {
      paramFloat = 1.0F;
    }
    return paramFloat;
  }
  
  private int clamp(int paramInt)
  {
    if (paramInt < 0) {
      paramInt = 0;
    } else if (paramInt > 255) {
      paramInt = 255;
    }
    return paramInt;
  }
  
  protected static class PaintContext
  {
    private static Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);
    private Insets stretchingInsets;
    private Dimension canvasSize;
    private boolean inverted;
    private CacheMode cacheMode;
    private double maxHorizontalScaleFactor;
    private double maxVerticalScaleFactor;
    private float a;
    private float b;
    private float c;
    private float d;
    private float aPercent;
    private float bPercent;
    private float cPercent;
    private float dPercent;
    
    public PaintContext(Insets paramInsets, Dimension paramDimension, boolean paramBoolean)
    {
      this(paramInsets, paramDimension, paramBoolean, null, 1.0D, 1.0D);
    }
    
    public PaintContext(Insets paramInsets, Dimension paramDimension, boolean paramBoolean, CacheMode paramCacheMode, double paramDouble1, double paramDouble2)
    {
      if ((paramDouble1 < 1.0D) || (paramDouble1 < 1.0D)) {
        throw new IllegalArgumentException("Both maxH and maxV must be >= 1");
      }
      stretchingInsets = (paramInsets == null ? EMPTY_INSETS : paramInsets);
      canvasSize = paramDimension;
      inverted = paramBoolean;
      cacheMode = (paramCacheMode == null ? CacheMode.NO_CACHING : paramCacheMode);
      maxHorizontalScaleFactor = paramDouble1;
      maxVerticalScaleFactor = paramDouble2;
      if (paramDimension != null)
      {
        a = stretchingInsets.left;
        b = (width - stretchingInsets.right);
        c = stretchingInsets.top;
        d = (height - stretchingInsets.bottom);
        canvasSize = paramDimension;
        inverted = paramBoolean;
        if (paramBoolean)
        {
          float f = width - (b - a);
          aPercent = (f > 0.0F ? a / f : 0.0F);
          bPercent = (f > 0.0F ? b / f : 0.0F);
          f = height - (d - c);
          cPercent = (f > 0.0F ? c / f : 0.0F);
          dPercent = (f > 0.0F ? d / f : 0.0F);
        }
      }
    }
    
    protected static enum CacheMode
    {
      NO_CACHING,  FIXED_SIZES,  NINE_SQUARE_SCALE;
      
      private CacheMode() {}
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\AbstractRegionPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */