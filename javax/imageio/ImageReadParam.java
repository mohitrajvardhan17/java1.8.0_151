package javax.imageio;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class ImageReadParam
  extends IIOParam
{
  protected boolean canSetSourceRenderSize = false;
  protected Dimension sourceRenderSize = null;
  protected BufferedImage destination = null;
  protected int[] destinationBands = null;
  protected int minProgressivePass = 0;
  protected int numProgressivePasses = Integer.MAX_VALUE;
  
  public ImageReadParam() {}
  
  public void setDestinationType(ImageTypeSpecifier paramImageTypeSpecifier)
  {
    super.setDestinationType(paramImageTypeSpecifier);
    setDestination(null);
  }
  
  public void setDestination(BufferedImage paramBufferedImage)
  {
    destination = paramBufferedImage;
  }
  
  public BufferedImage getDestination()
  {
    return destination;
  }
  
  public void setDestinationBands(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null)
    {
      destinationBands = null;
    }
    else
    {
      int i = paramArrayOfInt.length;
      for (int j = 0; j < i; j++)
      {
        int k = paramArrayOfInt[j];
        if (k < 0) {
          throw new IllegalArgumentException("Band value < 0!");
        }
        for (int m = j + 1; m < i; m++) {
          if (k == paramArrayOfInt[m]) {
            throw new IllegalArgumentException("Duplicate band value!");
          }
        }
      }
      destinationBands = ((int[])paramArrayOfInt.clone());
    }
  }
  
  public int[] getDestinationBands()
  {
    if (destinationBands == null) {
      return null;
    }
    return (int[])destinationBands.clone();
  }
  
  public boolean canSetSourceRenderSize()
  {
    return canSetSourceRenderSize;
  }
  
  public void setSourceRenderSize(Dimension paramDimension)
    throws UnsupportedOperationException
  {
    if (!canSetSourceRenderSize()) {
      throw new UnsupportedOperationException("Can't set source render size!");
    }
    if (paramDimension == null)
    {
      sourceRenderSize = null;
    }
    else
    {
      if ((width <= 0) || (height <= 0)) {
        throw new IllegalArgumentException("width or height <= 0!");
      }
      sourceRenderSize = ((Dimension)paramDimension.clone());
    }
  }
  
  public Dimension getSourceRenderSize()
  {
    return sourceRenderSize == null ? null : (Dimension)sourceRenderSize.clone();
  }
  
  public void setSourceProgressivePasses(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("minPass < 0!");
    }
    if (paramInt2 <= 0) {
      throw new IllegalArgumentException("numPasses <= 0!");
    }
    if ((paramInt2 != Integer.MAX_VALUE) && ((paramInt1 + paramInt2 - 1 & 0x80000000) != 0)) {
      throw new IllegalArgumentException("minPass + numPasses - 1 > INTEGER.MAX_VALUE!");
    }
    minProgressivePass = paramInt1;
    numProgressivePasses = paramInt2;
  }
  
  public int getSourceMinProgressivePass()
  {
    return minProgressivePass;
  }
  
  public int getSourceMaxProgressivePass()
  {
    if (numProgressivePasses == Integer.MAX_VALUE) {
      return Integer.MAX_VALUE;
    }
    return minProgressivePass + numProgressivePasses - 1;
  }
  
  public int getSourceNumProgressivePasses()
  {
    return numProgressivePasses;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\ImageReadParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */