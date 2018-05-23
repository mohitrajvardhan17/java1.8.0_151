package sun.java2d.cmm;

import java.io.IOException;
import java.io.InputStream;

public class ProfileDeferralInfo
  extends InputStream
{
  public int colorSpaceType;
  public int numComponents;
  public int profileClass;
  public String filename;
  
  public ProfileDeferralInfo(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    filename = paramString;
    colorSpaceType = paramInt1;
    numComponents = paramInt2;
    profileClass = paramInt3;
  }
  
  public int read()
    throws IOException
  {
    return 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\cmm\ProfileDeferralInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */