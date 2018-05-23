package java.awt.color;

import sun.java2d.cmm.Profile;
import sun.java2d.cmm.ProfileDeferralInfo;

public class ICC_ProfileRGB
  extends ICC_Profile
{
  static final long serialVersionUID = 8505067385152579334L;
  public static final int REDCOMPONENT = 0;
  public static final int GREENCOMPONENT = 1;
  public static final int BLUECOMPONENT = 2;
  
  ICC_ProfileRGB(Profile paramProfile)
  {
    super(paramProfile);
  }
  
  ICC_ProfileRGB(ProfileDeferralInfo paramProfileDeferralInfo)
  {
    super(paramProfileDeferralInfo);
  }
  
  public float[] getMediaWhitePoint()
  {
    return super.getMediaWhitePoint();
  }
  
  public float[][] getMatrix()
  {
    float[][] arrayOfFloat = new float[3][3];
    float[] arrayOfFloat1 = getXYZTag(1918392666);
    arrayOfFloat[0][0] = arrayOfFloat1[0];
    arrayOfFloat[1][0] = arrayOfFloat1[1];
    arrayOfFloat[2][0] = arrayOfFloat1[2];
    arrayOfFloat1 = getXYZTag(1733843290);
    arrayOfFloat[0][1] = arrayOfFloat1[0];
    arrayOfFloat[1][1] = arrayOfFloat1[1];
    arrayOfFloat[2][1] = arrayOfFloat1[2];
    arrayOfFloat1 = getXYZTag(1649957210);
    arrayOfFloat[0][2] = arrayOfFloat1[0];
    arrayOfFloat[1][2] = arrayOfFloat1[1];
    arrayOfFloat[2][2] = arrayOfFloat1[2];
    return arrayOfFloat;
  }
  
  public float getGamma(int paramInt)
  {
    int i;
    switch (paramInt)
    {
    case 0: 
      i = 1918128707;
      break;
    case 1: 
      i = 1733579331;
      break;
    case 2: 
      i = 1649693251;
      break;
    default: 
      throw new IllegalArgumentException("Must be Red, Green, or Blue");
    }
    float f = super.getGamma(i);
    return f;
  }
  
  public short[] getTRC(int paramInt)
  {
    int i;
    switch (paramInt)
    {
    case 0: 
      i = 1918128707;
      break;
    case 1: 
      i = 1733579331;
      break;
    case 2: 
      i = 1649693251;
      break;
    default: 
      throw new IllegalArgumentException("Must be Red, Green, or Blue");
    }
    short[] arrayOfShort = super.getTRC(i);
    return arrayOfShort;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\color\ICC_ProfileRGB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */