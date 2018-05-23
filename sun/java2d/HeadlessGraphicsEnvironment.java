package sun.java2d;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Locale;

public class HeadlessGraphicsEnvironment
  extends GraphicsEnvironment
{
  private GraphicsEnvironment ge;
  
  public HeadlessGraphicsEnvironment(GraphicsEnvironment paramGraphicsEnvironment)
  {
    ge = paramGraphicsEnvironment;
  }
  
  public GraphicsDevice[] getScreenDevices()
    throws HeadlessException
  {
    throw new HeadlessException();
  }
  
  public GraphicsDevice getDefaultScreenDevice()
    throws HeadlessException
  {
    throw new HeadlessException();
  }
  
  public Point getCenterPoint()
    throws HeadlessException
  {
    throw new HeadlessException();
  }
  
  public Rectangle getMaximumWindowBounds()
    throws HeadlessException
  {
    throw new HeadlessException();
  }
  
  public Graphics2D createGraphics(BufferedImage paramBufferedImage)
  {
    return ge.createGraphics(paramBufferedImage);
  }
  
  public Font[] getAllFonts()
  {
    return ge.getAllFonts();
  }
  
  public String[] getAvailableFontFamilyNames()
  {
    return ge.getAvailableFontFamilyNames();
  }
  
  public String[] getAvailableFontFamilyNames(Locale paramLocale)
  {
    return ge.getAvailableFontFamilyNames(paramLocale);
  }
  
  public GraphicsEnvironment getSunGraphicsEnvironment()
  {
    return ge;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\HeadlessGraphicsEnvironment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */