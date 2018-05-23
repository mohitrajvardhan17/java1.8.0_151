package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

class BumpBuffer
{
  static final int IMAGE_SIZE = 64;
  transient Image image;
  Color topColor;
  Color shadowColor;
  Color backColor;
  private GraphicsConfiguration gc;
  
  public BumpBuffer(GraphicsConfiguration paramGraphicsConfiguration, Color paramColor1, Color paramColor2, Color paramColor3)
  {
    gc = paramGraphicsConfiguration;
    topColor = paramColor1;
    shadowColor = paramColor2;
    backColor = paramColor3;
    createImage();
    fillBumpBuffer();
  }
  
  public boolean hasSameConfiguration(GraphicsConfiguration paramGraphicsConfiguration, Color paramColor1, Color paramColor2, Color paramColor3)
  {
    if (gc != null)
    {
      if (!gc.equals(paramGraphicsConfiguration)) {
        return false;
      }
    }
    else if (paramGraphicsConfiguration != null) {
      return false;
    }
    return (topColor.equals(paramColor1)) && (shadowColor.equals(paramColor2)) && (backColor.equals(paramColor3));
  }
  
  public Image getImage()
  {
    return image;
  }
  
  private void fillBumpBuffer()
  {
    Graphics localGraphics = image.getGraphics();
    localGraphics.setColor(backColor);
    localGraphics.fillRect(0, 0, 64, 64);
    localGraphics.setColor(topColor);
    int j;
    for (int i = 0; i < 64; i += 4) {
      for (j = 0; j < 64; j += 4)
      {
        localGraphics.drawLine(i, j, i, j);
        localGraphics.drawLine(i + 2, j + 2, i + 2, j + 2);
      }
    }
    localGraphics.setColor(shadowColor);
    for (i = 0; i < 64; i += 4) {
      for (j = 0; j < 64; j += 4)
      {
        localGraphics.drawLine(i + 1, j + 1, i + 1, j + 1);
        localGraphics.drawLine(i + 3, j + 3, i + 3, j + 3);
      }
    }
    localGraphics.dispose();
  }
  
  private void createImage()
  {
    if (gc != null)
    {
      image = gc.createCompatibleImage(64, 64, backColor != MetalBumps.ALPHA ? 1 : 2);
    }
    else
    {
      int[] arrayOfInt = { backColor.getRGB(), topColor.getRGB(), shadowColor.getRGB() };
      IndexColorModel localIndexColorModel = new IndexColorModel(8, 3, arrayOfInt, 0, false, backColor == MetalBumps.ALPHA ? 0 : -1, 0);
      image = new BufferedImage(64, 64, 13, localIndexColorModel);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\BumpBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */