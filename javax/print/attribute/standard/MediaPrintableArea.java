package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class MediaPrintableArea
  implements DocAttribute, PrintRequestAttribute, PrintJobAttribute
{
  private int x;
  private int y;
  private int w;
  private int h;
  private int units;
  private static final long serialVersionUID = -1597171464050795793L;
  public static final int INCH = 25400;
  public static final int MM = 1000;
  
  public MediaPrintableArea(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt)
  {
    if ((paramFloat1 < 0.0D) || (paramFloat2 < 0.0D) || (paramFloat3 <= 0.0D) || (paramFloat4 <= 0.0D) || (paramInt < 1)) {
      throw new IllegalArgumentException("0 or negative value argument");
    }
    x = ((int)(paramFloat1 * paramInt + 0.5F));
    y = ((int)(paramFloat2 * paramInt + 0.5F));
    w = ((int)(paramFloat3 * paramInt + 0.5F));
    h = ((int)(paramFloat4 * paramInt + 0.5F));
  }
  
  public MediaPrintableArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt3 <= 0) || (paramInt4 <= 0) || (paramInt5 < 1)) {
      throw new IllegalArgumentException("0 or negative value argument");
    }
    x = (paramInt1 * paramInt5);
    y = (paramInt2 * paramInt5);
    w = (paramInt3 * paramInt5);
    h = (paramInt4 * paramInt5);
  }
  
  public float[] getPrintableArea(int paramInt)
  {
    return new float[] { getX(paramInt), getY(paramInt), getWidth(paramInt), getHeight(paramInt) };
  }
  
  public float getX(int paramInt)
  {
    return convertFromMicrometers(x, paramInt);
  }
  
  public float getY(int paramInt)
  {
    return convertFromMicrometers(y, paramInt);
  }
  
  public float getWidth(int paramInt)
  {
    return convertFromMicrometers(w, paramInt);
  }
  
  public float getHeight(int paramInt)
  {
    return convertFromMicrometers(h, paramInt);
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if ((paramObject instanceof MediaPrintableArea))
    {
      MediaPrintableArea localMediaPrintableArea = (MediaPrintableArea)paramObject;
      if ((x == x) && (y == y) && (w == w) && (h == h)) {
        bool = true;
      }
    }
    return bool;
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return MediaPrintableArea.class;
  }
  
  public final String getName()
  {
    return "media-printable-area";
  }
  
  public String toString(int paramInt, String paramString)
  {
    if (paramString == null) {
      paramString = "";
    }
    float[] arrayOfFloat = getPrintableArea(paramInt);
    String str = "(" + arrayOfFloat[0] + "," + arrayOfFloat[1] + ")->(" + arrayOfFloat[2] + "," + arrayOfFloat[3] + ")";
    return str + paramString;
  }
  
  public String toString()
  {
    return toString(1000, "mm");
  }
  
  public int hashCode()
  {
    return x + 37 * y + 43 * w + 47 * h;
  }
  
  private static float convertFromMicrometers(int paramInt1, int paramInt2)
  {
    if (paramInt2 < 1) {
      throw new IllegalArgumentException("units is < 1");
    }
    return paramInt1 / paramInt2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\MediaPrintableArea.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */