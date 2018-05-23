package java.awt;

import java.awt.image.ColorModel;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

public abstract class GraphicsDevice
{
  private Window fullScreenWindow;
  private AppContext fullScreenAppContext;
  private final Object fsAppContextLock = new Object();
  private Rectangle windowedModeBounds;
  public static final int TYPE_RASTER_SCREEN = 0;
  public static final int TYPE_PRINTER = 1;
  public static final int TYPE_IMAGE_BUFFER = 2;
  
  protected GraphicsDevice() {}
  
  public abstract int getType();
  
  public abstract String getIDstring();
  
  public abstract GraphicsConfiguration[] getConfigurations();
  
  public abstract GraphicsConfiguration getDefaultConfiguration();
  
  public GraphicsConfiguration getBestConfiguration(GraphicsConfigTemplate paramGraphicsConfigTemplate)
  {
    GraphicsConfiguration[] arrayOfGraphicsConfiguration = getConfigurations();
    return paramGraphicsConfigTemplate.getBestConfiguration(arrayOfGraphicsConfiguration);
  }
  
  public boolean isFullScreenSupported()
  {
    return false;
  }
  
  public void setFullScreenWindow(Window paramWindow)
  {
    if (paramWindow != null)
    {
      if (paramWindow.getShape() != null) {
        paramWindow.setShape(null);
      }
      if (paramWindow.getOpacity() < 1.0F) {
        paramWindow.setOpacity(1.0F);
      }
      if (!paramWindow.isOpaque())
      {
        localObject1 = paramWindow.getBackground();
        localObject1 = new Color(((Color)localObject1).getRed(), ((Color)localObject1).getGreen(), ((Color)localObject1).getBlue(), 255);
        paramWindow.setBackground((Color)localObject1);
      }
      Object localObject1 = paramWindow.getGraphicsConfiguration();
      if ((localObject1 != null) && (((GraphicsConfiguration)localObject1).getDevice() != this) && (((GraphicsConfiguration)localObject1).getDevice().getFullScreenWindow() == paramWindow)) {
        ((GraphicsConfiguration)localObject1).getDevice().setFullScreenWindow(null);
      }
    }
    if ((fullScreenWindow != null) && (windowedModeBounds != null))
    {
      if (windowedModeBounds.width == 0) {
        windowedModeBounds.width = 1;
      }
      if (windowedModeBounds.height == 0) {
        windowedModeBounds.height = 1;
      }
      fullScreenWindow.setBounds(windowedModeBounds);
    }
    synchronized (fsAppContextLock)
    {
      if (paramWindow == null) {
        fullScreenAppContext = null;
      } else {
        fullScreenAppContext = AppContext.getAppContext();
      }
      fullScreenWindow = paramWindow;
    }
    if (fullScreenWindow != null)
    {
      windowedModeBounds = fullScreenWindow.getBounds();
      ??? = getDefaultConfiguration();
      Rectangle localRectangle = ((GraphicsConfiguration)???).getBounds();
      if (SunToolkit.isDispatchThreadForAppContext(fullScreenWindow)) {
        fullScreenWindow.setGraphicsConfiguration((GraphicsConfiguration)???);
      }
      fullScreenWindow.setBounds(x, y, width, height);
      fullScreenWindow.setVisible(true);
      fullScreenWindow.toFront();
    }
  }
  
  public Window getFullScreenWindow()
  {
    Window localWindow = null;
    synchronized (fsAppContextLock)
    {
      if (fullScreenAppContext == AppContext.getAppContext()) {
        localWindow = fullScreenWindow;
      }
    }
    return localWindow;
  }
  
  public boolean isDisplayChangeSupported()
  {
    return false;
  }
  
  public void setDisplayMode(DisplayMode paramDisplayMode)
  {
    throw new UnsupportedOperationException("Cannot change display mode");
  }
  
  public DisplayMode getDisplayMode()
  {
    GraphicsConfiguration localGraphicsConfiguration = getDefaultConfiguration();
    Rectangle localRectangle = localGraphicsConfiguration.getBounds();
    ColorModel localColorModel = localGraphicsConfiguration.getColorModel();
    return new DisplayMode(width, height, localColorModel.getPixelSize(), 0);
  }
  
  public DisplayMode[] getDisplayModes()
  {
    return new DisplayMode[] { getDisplayMode() };
  }
  
  public int getAvailableAcceleratedMemory()
  {
    return -1;
  }
  
  public boolean isWindowTranslucencySupported(WindowTranslucency paramWindowTranslucency)
  {
    switch (paramWindowTranslucency)
    {
    case PERPIXEL_TRANSPARENT: 
      return isWindowShapingSupported();
    case TRANSLUCENT: 
      return isWindowOpacitySupported();
    case PERPIXEL_TRANSLUCENT: 
      return isWindowPerpixelTranslucencySupported();
    }
    return false;
  }
  
  static boolean isWindowShapingSupported()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if (!(localToolkit instanceof SunToolkit)) {
      return false;
    }
    return ((SunToolkit)localToolkit).isWindowShapingSupported();
  }
  
  static boolean isWindowOpacitySupported()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if (!(localToolkit instanceof SunToolkit)) {
      return false;
    }
    return ((SunToolkit)localToolkit).isWindowOpacitySupported();
  }
  
  boolean isWindowPerpixelTranslucencySupported()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if (!(localToolkit instanceof SunToolkit)) {
      return false;
    }
    if (!((SunToolkit)localToolkit).isWindowTranslucencySupported()) {
      return false;
    }
    return getTranslucencyCapableGC() != null;
  }
  
  GraphicsConfiguration getTranslucencyCapableGC()
  {
    GraphicsConfiguration localGraphicsConfiguration = getDefaultConfiguration();
    if (localGraphicsConfiguration.isTranslucencyCapable()) {
      return localGraphicsConfiguration;
    }
    GraphicsConfiguration[] arrayOfGraphicsConfiguration = getConfigurations();
    for (int i = 0; i < arrayOfGraphicsConfiguration.length; i++) {
      if (arrayOfGraphicsConfiguration[i].isTranslucencyCapable()) {
        return arrayOfGraphicsConfiguration[i];
      }
    }
    return null;
  }
  
  public static enum WindowTranslucency
  {
    PERPIXEL_TRANSPARENT,  TRANSLUCENT,  PERPIXEL_TRANSLUCENT;
    
    private WindowTranslucency() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\GraphicsDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */