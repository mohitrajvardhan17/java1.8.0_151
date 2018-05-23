package sun.awt.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import sun.java2d.DestSurfaceProvider;
import sun.java2d.SunGraphics2D;
import sun.java2d.Surface;
import sun.java2d.SurfaceManagerFactory;
import sun.print.PrinterGraphicsConfig;

public class SunVolatileImage
  extends VolatileImage
  implements DestSurfaceProvider
{
  protected VolatileSurfaceManager volSurfaceManager;
  protected Component comp;
  private GraphicsConfiguration graphicsConfig;
  private Font defaultFont;
  private int width;
  private int height;
  private int forcedAccelSurfaceType;
  
  protected SunVolatileImage(Component paramComponent, GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object paramObject, int paramInt3, ImageCapabilities paramImageCapabilities, int paramInt4)
  {
    comp = paramComponent;
    graphicsConfig = paramGraphicsConfiguration;
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      throw new IllegalArgumentException("Width (" + paramInt1 + ") and height (" + paramInt2 + ") cannot be <= 0");
    }
    width = paramInt1;
    height = paramInt2;
    forcedAccelSurfaceType = paramInt4;
    if ((paramInt3 != 1) && (paramInt3 != 2) && (paramInt3 != 3)) {
      throw new IllegalArgumentException("Unknown transparency type:" + paramInt3);
    }
    transparency = paramInt3;
    volSurfaceManager = createSurfaceManager(paramObject, paramImageCapabilities);
    SurfaceManager.setManager(this, volSurfaceManager);
    volSurfaceManager.initialize();
    volSurfaceManager.initContents();
  }
  
  private SunVolatileImage(Component paramComponent, GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object paramObject, ImageCapabilities paramImageCapabilities)
  {
    this(paramComponent, paramGraphicsConfiguration, paramInt1, paramInt2, paramObject, 1, paramImageCapabilities, 0);
  }
  
  public SunVolatileImage(Component paramComponent, int paramInt1, int paramInt2)
  {
    this(paramComponent, paramInt1, paramInt2, null);
  }
  
  public SunVolatileImage(Component paramComponent, int paramInt1, int paramInt2, Object paramObject)
  {
    this(paramComponent, paramComponent.getGraphicsConfiguration(), paramInt1, paramInt2, paramObject, null);
  }
  
  public SunVolatileImage(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, int paramInt3, ImageCapabilities paramImageCapabilities)
  {
    this(null, paramGraphicsConfiguration, paramInt1, paramInt2, null, paramInt3, paramImageCapabilities, 0);
  }
  
  public int getWidth()
  {
    return width;
  }
  
  public int getHeight()
  {
    return height;
  }
  
  public GraphicsConfiguration getGraphicsConfig()
  {
    return graphicsConfig;
  }
  
  public void updateGraphicsConfig()
  {
    if (comp != null)
    {
      GraphicsConfiguration localGraphicsConfiguration = comp.getGraphicsConfiguration();
      if (localGraphicsConfiguration != null) {
        graphicsConfig = localGraphicsConfiguration;
      }
    }
  }
  
  public Component getComponent()
  {
    return comp;
  }
  
  public int getForcedAccelSurfaceType()
  {
    return forcedAccelSurfaceType;
  }
  
  protected VolatileSurfaceManager createSurfaceManager(Object paramObject, ImageCapabilities paramImageCapabilities)
  {
    if (((graphicsConfig instanceof BufferedImageGraphicsConfig)) || ((graphicsConfig instanceof PrinterGraphicsConfig)) || ((paramImageCapabilities != null) && (!paramImageCapabilities.isAccelerated()))) {
      return new BufImgVolatileSurfaceManager(this, paramObject);
    }
    SurfaceManagerFactory localSurfaceManagerFactory = SurfaceManagerFactory.getInstance();
    return localSurfaceManagerFactory.createVolatileManager(this, paramObject);
  }
  
  private Color getForeground()
  {
    if (comp != null) {
      return comp.getForeground();
    }
    return Color.black;
  }
  
  private Color getBackground()
  {
    if (comp != null) {
      return comp.getBackground();
    }
    return Color.white;
  }
  
  private Font getFont()
  {
    if (comp != null) {
      return comp.getFont();
    }
    if (defaultFont == null) {
      defaultFont = new Font("Dialog", 0, 12);
    }
    return defaultFont;
  }
  
  public Graphics2D createGraphics()
  {
    return new SunGraphics2D(volSurfaceManager.getPrimarySurfaceData(), getForeground(), getBackground(), getFont());
  }
  
  public Object getProperty(String paramString, ImageObserver paramImageObserver)
  {
    if (paramString == null) {
      throw new NullPointerException("null property name is not allowed");
    }
    return Image.UndefinedProperty;
  }
  
  public int getWidth(ImageObserver paramImageObserver)
  {
    return getWidth();
  }
  
  public int getHeight(ImageObserver paramImageObserver)
  {
    return getHeight();
  }
  
  public BufferedImage getBackupImage()
  {
    return graphicsConfig.createCompatibleImage(getWidth(), getHeight(), getTransparency());
  }
  
  public BufferedImage getSnapshot()
  {
    BufferedImage localBufferedImage = getBackupImage();
    Graphics2D localGraphics2D = localBufferedImage.createGraphics();
    localGraphics2D.setComposite(AlphaComposite.Src);
    localGraphics2D.drawImage(this, 0, 0, null);
    localGraphics2D.dispose();
    return localBufferedImage;
  }
  
  public int validate(GraphicsConfiguration paramGraphicsConfiguration)
  {
    return volSurfaceManager.validate(paramGraphicsConfiguration);
  }
  
  public boolean contentsLost()
  {
    return volSurfaceManager.contentsLost();
  }
  
  public ImageCapabilities getCapabilities()
  {
    return volSurfaceManager.getCapabilities(graphicsConfig);
  }
  
  public Surface getDestSurface()
  {
    return volSurfaceManager.getPrimarySurfaceData();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\SunVolatileImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */