package javax.print.attribute;

import java.io.Serializable;

public abstract class ResolutionSyntax
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = 2706743076526672017L;
  private int crossFeedResolution;
  private int feedResolution;
  public static final int DPI = 100;
  public static final int DPCM = 254;
  
  public ResolutionSyntax(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 < 1) {
      throw new IllegalArgumentException("crossFeedResolution is < 1");
    }
    if (paramInt2 < 1) {
      throw new IllegalArgumentException("feedResolution is < 1");
    }
    if (paramInt3 < 1) {
      throw new IllegalArgumentException("units is < 1");
    }
    crossFeedResolution = (paramInt1 * paramInt3);
    feedResolution = (paramInt2 * paramInt3);
  }
  
  private static int convertFromDphi(int paramInt1, int paramInt2)
  {
    if (paramInt2 < 1) {
      throw new IllegalArgumentException(": units is < 1");
    }
    int i = paramInt2 / 2;
    return (paramInt1 + i) / paramInt2;
  }
  
  public int[] getResolution(int paramInt)
  {
    return new int[] { getCrossFeedResolution(paramInt), getFeedResolution(paramInt) };
  }
  
  public int getCrossFeedResolution(int paramInt)
  {
    return convertFromDphi(crossFeedResolution, paramInt);
  }
  
  public int getFeedResolution(int paramInt)
  {
    return convertFromDphi(feedResolution, paramInt);
  }
  
  public String toString(int paramInt, String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(getCrossFeedResolution(paramInt));
    localStringBuffer.append('x');
    localStringBuffer.append(getFeedResolution(paramInt));
    if (paramString != null)
    {
      localStringBuffer.append(' ');
      localStringBuffer.append(paramString);
    }
    return localStringBuffer.toString();
  }
  
  public boolean lessThanOrEquals(ResolutionSyntax paramResolutionSyntax)
  {
    return (crossFeedResolution <= crossFeedResolution) && (feedResolution <= feedResolution);
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject != null) && ((paramObject instanceof ResolutionSyntax)) && (crossFeedResolution == crossFeedResolution) && (feedResolution == feedResolution);
  }
  
  public int hashCode()
  {
    return crossFeedResolution & 0xFFFF | (feedResolution & 0xFFFF) << 16;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(crossFeedResolution);
    localStringBuffer.append('x');
    localStringBuffer.append(feedResolution);
    localStringBuffer.append(" dphi");
    return localStringBuffer.toString();
  }
  
  protected int getCrossFeedResolutionDphi()
  {
    return crossFeedResolution;
  }
  
  protected int getFeedResolutionDphi()
  {
    return feedResolution;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\ResolutionSyntax.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */