package java.awt.print;

public class PageFormat
  implements Cloneable
{
  public static final int LANDSCAPE = 0;
  public static final int PORTRAIT = 1;
  public static final int REVERSE_LANDSCAPE = 2;
  private Paper mPaper = new Paper();
  private int mOrientation = 1;
  
  public PageFormat() {}
  
  public Object clone()
  {
    PageFormat localPageFormat;
    try
    {
      localPageFormat = (PageFormat)super.clone();
      mPaper = ((Paper)mPaper.clone());
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      localCloneNotSupportedException.printStackTrace();
      localPageFormat = null;
    }
    return localPageFormat;
  }
  
  public double getWidth()
  {
    int i = getOrientation();
    double d;
    if (i == 1) {
      d = mPaper.getWidth();
    } else {
      d = mPaper.getHeight();
    }
    return d;
  }
  
  public double getHeight()
  {
    int i = getOrientation();
    double d;
    if (i == 1) {
      d = mPaper.getHeight();
    } else {
      d = mPaper.getWidth();
    }
    return d;
  }
  
  public double getImageableX()
  {
    double d;
    switch (getOrientation())
    {
    case 0: 
      d = mPaper.getHeight() - (mPaper.getImageableY() + mPaper.getImageableHeight());
      break;
    case 1: 
      d = mPaper.getImageableX();
      break;
    case 2: 
      d = mPaper.getImageableY();
      break;
    default: 
      throw new InternalError("unrecognized orientation");
    }
    return d;
  }
  
  public double getImageableY()
  {
    double d;
    switch (getOrientation())
    {
    case 0: 
      d = mPaper.getImageableX();
      break;
    case 1: 
      d = mPaper.getImageableY();
      break;
    case 2: 
      d = mPaper.getWidth() - (mPaper.getImageableX() + mPaper.getImageableWidth());
      break;
    default: 
      throw new InternalError("unrecognized orientation");
    }
    return d;
  }
  
  public double getImageableWidth()
  {
    double d;
    if (getOrientation() == 1) {
      d = mPaper.getImageableWidth();
    } else {
      d = mPaper.getImageableHeight();
    }
    return d;
  }
  
  public double getImageableHeight()
  {
    double d;
    if (getOrientation() == 1) {
      d = mPaper.getImageableHeight();
    } else {
      d = mPaper.getImageableWidth();
    }
    return d;
  }
  
  public Paper getPaper()
  {
    return (Paper)mPaper.clone();
  }
  
  public void setPaper(Paper paramPaper)
  {
    mPaper = ((Paper)paramPaper.clone());
  }
  
  public void setOrientation(int paramInt)
    throws IllegalArgumentException
  {
    if ((0 <= paramInt) && (paramInt <= 2)) {
      mOrientation = paramInt;
    } else {
      throw new IllegalArgumentException();
    }
  }
  
  public int getOrientation()
  {
    return mOrientation;
  }
  
  public double[] getMatrix()
  {
    double[] arrayOfDouble = new double[6];
    switch (mOrientation)
    {
    case 0: 
      arrayOfDouble[0] = 0.0D;
      arrayOfDouble[1] = -1.0D;
      arrayOfDouble[2] = 1.0D;
      arrayOfDouble[3] = 0.0D;
      arrayOfDouble[4] = 0.0D;
      arrayOfDouble[5] = mPaper.getHeight();
      break;
    case 1: 
      arrayOfDouble[0] = 1.0D;
      arrayOfDouble[1] = 0.0D;
      arrayOfDouble[2] = 0.0D;
      arrayOfDouble[3] = 1.0D;
      arrayOfDouble[4] = 0.0D;
      arrayOfDouble[5] = 0.0D;
      break;
    case 2: 
      arrayOfDouble[0] = 0.0D;
      arrayOfDouble[1] = 1.0D;
      arrayOfDouble[2] = -1.0D;
      arrayOfDouble[3] = 0.0D;
      arrayOfDouble[4] = mPaper.getWidth();
      arrayOfDouble[5] = 0.0D;
      break;
    default: 
      throw new IllegalArgumentException();
    }
    return arrayOfDouble;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\print\PageFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */