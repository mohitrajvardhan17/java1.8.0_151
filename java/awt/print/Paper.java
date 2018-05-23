package java.awt.print;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

public class Paper
  implements Cloneable
{
  private static final int INCH = 72;
  private static final double LETTER_WIDTH = 612.0D;
  private static final double LETTER_HEIGHT = 792.0D;
  private double mHeight = 792.0D;
  private double mWidth = 612.0D;
  private Rectangle2D mImageableArea = new Rectangle2D.Double(72.0D, 72.0D, mWidth - 144.0D, mHeight - 144.0D);
  
  public Paper() {}
  
  public Object clone()
  {
    Paper localPaper;
    try
    {
      localPaper = (Paper)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      localCloneNotSupportedException.printStackTrace();
      localPaper = null;
    }
    return localPaper;
  }
  
  public double getHeight()
  {
    return mHeight;
  }
  
  public void setSize(double paramDouble1, double paramDouble2)
  {
    mWidth = paramDouble1;
    mHeight = paramDouble2;
  }
  
  public double getWidth()
  {
    return mWidth;
  }
  
  public void setImageableArea(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    mImageableArea = new Rectangle2D.Double(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
  }
  
  public double getImageableX()
  {
    return mImageableArea.getX();
  }
  
  public double getImageableY()
  {
    return mImageableArea.getY();
  }
  
  public double getImageableWidth()
  {
    return mImageableArea.getWidth();
  }
  
  public double getImageableHeight()
  {
    return mImageableArea.getHeight();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\print\Paper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */