package sun.java2d.cmm.lcms;

import java.awt.color.CMMException;
import java.awt.color.ICC_Profile;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.PCMM;
import sun.java2d.cmm.Profile;

public class LCMS
  implements PCMM
{
  private static LCMS theLcms = null;
  
  public Profile loadProfile(byte[] paramArrayOfByte)
  {
    Object localObject = new Object();
    long l = loadProfileNative(paramArrayOfByte, localObject);
    if (l != 0L) {
      return new LCMSProfile(l, localObject);
    }
    return null;
  }
  
  private native long loadProfileNative(byte[] paramArrayOfByte, Object paramObject);
  
  private LCMSProfile getLcmsProfile(Profile paramProfile)
  {
    if ((paramProfile instanceof LCMSProfile)) {
      return (LCMSProfile)paramProfile;
    }
    throw new CMMException("Invalid profile: " + paramProfile);
  }
  
  public void freeProfile(Profile paramProfile) {}
  
  /* Error */
  public int getProfileSize(Profile paramProfile)
  {
    // Byte code:
    //   0: aload_1
    //   1: dup
    //   2: astore_2
    //   3: monitorenter
    //   4: aload_0
    //   5: aload_0
    //   6: aload_1
    //   7: invokespecial 142	sun/java2d/cmm/lcms/LCMS:getLcmsProfile	(Lsun/java2d/cmm/Profile;)Lsun/java2d/cmm/lcms/LCMSProfile;
    //   10: invokevirtual 145	sun/java2d/cmm/lcms/LCMSProfile:getLcmsPtr	()J
    //   13: invokespecial 137	sun/java2d/cmm/lcms/LCMS:getProfileSizeNative	(J)I
    //   16: aload_2
    //   17: monitorexit
    //   18: ireturn
    //   19: astore_3
    //   20: aload_2
    //   21: monitorexit
    //   22: aload_3
    //   23: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	24	0	this	LCMS
    //   0	24	1	paramProfile	Profile
    //   2	19	2	Ljava/lang/Object;	Object
    //   19	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   4	18	19	finally
    //   19	22	19	finally
  }
  
  private native int getProfileSizeNative(long paramLong);
  
  public void getProfileData(Profile paramProfile, byte[] paramArrayOfByte)
  {
    synchronized (paramProfile)
    {
      getProfileDataNative(getLcmsProfile(paramProfile).getLcmsPtr(), paramArrayOfByte);
    }
  }
  
  private native void getProfileDataNative(long paramLong, byte[] paramArrayOfByte);
  
  public int getTagSize(Profile paramProfile, int paramInt)
  {
    LCMSProfile localLCMSProfile = getLcmsProfile(paramProfile);
    synchronized (localLCMSProfile)
    {
      LCMSProfile.TagData localTagData = localLCMSProfile.getTag(paramInt);
      return localTagData == null ? 0 : localTagData.getSize();
    }
  }
  
  static native byte[] getTagNative(long paramLong, int paramInt);
  
  public void getTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte)
  {
    LCMSProfile localLCMSProfile = getLcmsProfile(paramProfile);
    synchronized (localLCMSProfile)
    {
      LCMSProfile.TagData localTagData = localLCMSProfile.getTag(paramInt);
      if (localTagData != null) {
        localTagData.copyDataTo(paramArrayOfByte);
      }
    }
  }
  
  public synchronized void setTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte)
  {
    LCMSProfile localLCMSProfile = getLcmsProfile(paramProfile);
    synchronized (localLCMSProfile)
    {
      localLCMSProfile.clearTagCache();
      setTagDataNative(localLCMSProfile.getLcmsPtr(), paramInt, paramArrayOfByte);
    }
  }
  
  private native void setTagDataNative(long paramLong, int paramInt, byte[] paramArrayOfByte);
  
  public static synchronized native LCMSProfile getProfileID(ICC_Profile paramICC_Profile);
  
  static long createTransform(LCMSProfile[] paramArrayOfLCMSProfile, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2, Object paramObject)
  {
    long[] arrayOfLong = new long[paramArrayOfLCMSProfile.length];
    for (int i = 0; i < paramArrayOfLCMSProfile.length; i++)
    {
      if (paramArrayOfLCMSProfile[i] == null) {
        throw new CMMException("Unknown profile ID");
      }
      arrayOfLong[i] = paramArrayOfLCMSProfile[i].getLcmsPtr();
    }
    return createNativeTransform(arrayOfLong, paramInt1, paramInt2, paramBoolean1, paramInt3, paramBoolean2, paramObject);
  }
  
  private static native long createNativeTransform(long[] paramArrayOfLong, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2, Object paramObject);
  
  public ColorTransform createTransform(ICC_Profile paramICC_Profile, int paramInt1, int paramInt2)
  {
    return new LCMSTransform(paramICC_Profile, paramInt1, paramInt1);
  }
  
  public synchronized ColorTransform createTransform(ColorTransform[] paramArrayOfColorTransform)
  {
    return new LCMSTransform(paramArrayOfColorTransform);
  }
  
  public static native void colorConvert(LCMSTransform paramLCMSTransform, LCMSImageLayout paramLCMSImageLayout1, LCMSImageLayout paramLCMSImageLayout2);
  
  public static native void freeTransform(long paramLong);
  
  public static native void initLCMS(Class paramClass1, Class paramClass2, Class paramClass3);
  
  private LCMS() {}
  
  static synchronized PCMM getModule()
  {
    if (theLcms != null) {
      return theLcms;
    }
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        System.loadLibrary("awt");
        System.loadLibrary("lcms");
        return null;
      }
    });
    initLCMS(LCMSTransform.class, LCMSImageLayout.class, ICC_Profile.class);
    theLcms = new LCMS();
    return theLcms;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\cmm\lcms\LCMS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */