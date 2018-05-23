package java.awt.color;

import sun.java2d.cmm.Profile;
import sun.java2d.cmm.ProfileDeferralInfo;

public class ICC_ProfileGray
  extends ICC_Profile
{
  static final long serialVersionUID = -1124721290732002649L;
  
  ICC_ProfileGray(Profile paramProfile)
  {
    super(paramProfile);
  }
  
  ICC_ProfileGray(ProfileDeferralInfo paramProfileDeferralInfo)
  {
    super(paramProfileDeferralInfo);
  }
  
  public float[] getMediaWhitePoint()
  {
    return super.getMediaWhitePoint();
  }
  
  public float getGamma()
  {
    float f = super.getGamma(1800688195);
    return f;
  }
  
  public short[] getTRC()
  {
    short[] arrayOfShort = super.getTRC(1800688195);
    return arrayOfShort;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\color\ICC_ProfileGray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */