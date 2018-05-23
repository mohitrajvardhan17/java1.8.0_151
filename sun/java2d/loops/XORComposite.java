package sun.java2d.loops;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import sun.java2d.SunCompositeContext;
import sun.java2d.SurfaceData;

public final class XORComposite
  implements Composite
{
  Color xorColor;
  int xorPixel;
  int alphaMask;
  
  public XORComposite(Color paramColor, SurfaceData paramSurfaceData)
  {
    xorColor = paramColor;
    SurfaceType localSurfaceType = paramSurfaceData.getSurfaceType();
    xorPixel = paramSurfaceData.pixelFor(paramColor.getRGB());
    alphaMask = localSurfaceType.getAlphaMask();
  }
  
  public Color getXorColor()
  {
    return xorColor;
  }
  
  public int getXorPixel()
  {
    return xorPixel;
  }
  
  public int getAlphaMask()
  {
    return alphaMask;
  }
  
  public CompositeContext createContext(ColorModel paramColorModel1, ColorModel paramColorModel2, RenderingHints paramRenderingHints)
  {
    return new SunCompositeContext(this, paramColorModel1, paramColorModel2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\XORComposite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */