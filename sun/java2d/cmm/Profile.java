package sun.java2d.cmm;

import java.awt.color.CMMException;

public class Profile
{
  private final long nativePtr;
  
  protected Profile(long paramLong)
  {
    nativePtr = paramLong;
  }
  
  protected final long getNativePtr()
  {
    if (nativePtr == 0L) {
      throw new CMMException("Invalid profile: ptr is null");
    }
    return nativePtr;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\cmm\Profile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */