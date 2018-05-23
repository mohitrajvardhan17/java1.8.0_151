package sun.java2d.cmm;

import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceLoader;
import sun.security.action.GetPropertyAction;

public class CMSManager
{
  public static ColorSpace GRAYspace;
  public static ColorSpace LINEAR_RGBspace;
  private static PCMM cmmImpl = null;
  
  public CMSManager() {}
  
  public static synchronized PCMM getModule()
  {
    if (cmmImpl != null) {
      return cmmImpl;
    }
    CMMServiceProvider localCMMServiceProvider = (CMMServiceProvider)AccessController.doPrivileged(new PrivilegedAction()
    {
      public CMMServiceProvider run()
      {
        String str = System.getProperty("sun.java2d.cmm", "sun.java2d.cmm.lcms.LcmsServiceProvider");
        ServiceLoader localServiceLoader = ServiceLoader.loadInstalled(CMMServiceProvider.class);
        Object localObject = null;
        Iterator localIterator = localServiceLoader.iterator();
        while (localIterator.hasNext())
        {
          CMMServiceProvider localCMMServiceProvider = (CMMServiceProvider)localIterator.next();
          localObject = localCMMServiceProvider;
          if (localCMMServiceProvider.getClass().getName().equals(str)) {
            break;
          }
        }
        return (CMMServiceProvider)localObject;
      }
    });
    cmmImpl = localCMMServiceProvider.getColorManagementModule();
    if (cmmImpl == null) {
      throw new CMMException("Cannot initialize Color Management System.No CM module found");
    }
    GetPropertyAction localGetPropertyAction = new GetPropertyAction("sun.java2d.cmm.trace");
    String str = (String)AccessController.doPrivileged(localGetPropertyAction);
    if (str != null) {
      cmmImpl = new CMMTracer(cmmImpl);
    }
    return cmmImpl;
  }
  
  static synchronized boolean canCreateModule()
  {
    return cmmImpl == null;
  }
  
  public static class CMMTracer
    implements PCMM
  {
    PCMM tcmm;
    String cName;
    
    public CMMTracer(PCMM paramPCMM)
    {
      tcmm = paramPCMM;
      cName = paramPCMM.getClass().getName();
    }
    
    public Profile loadProfile(byte[] paramArrayOfByte)
    {
      System.err.print(cName + ".loadProfile");
      Profile localProfile = tcmm.loadProfile(paramArrayOfByte);
      System.err.printf("(ID=%s)\n", new Object[] { localProfile.toString() });
      return localProfile;
    }
    
    public void freeProfile(Profile paramProfile)
    {
      System.err.printf(cName + ".freeProfile(ID=%s)\n", new Object[] { paramProfile.toString() });
      tcmm.freeProfile(paramProfile);
    }
    
    public int getProfileSize(Profile paramProfile)
    {
      System.err.print(cName + ".getProfileSize(ID=" + paramProfile + ")");
      int i = tcmm.getProfileSize(paramProfile);
      System.err.println("=" + i);
      return i;
    }
    
    public void getProfileData(Profile paramProfile, byte[] paramArrayOfByte)
    {
      System.err.print(cName + ".getProfileData(ID=" + paramProfile + ") ");
      System.err.println("requested " + paramArrayOfByte.length + " byte(s)");
      tcmm.getProfileData(paramProfile, paramArrayOfByte);
    }
    
    public int getTagSize(Profile paramProfile, int paramInt)
    {
      System.err.printf(cName + ".getTagSize(ID=%x, TagSig=%s)", new Object[] { paramProfile, signatureToString(paramInt) });
      int i = tcmm.getTagSize(paramProfile, paramInt);
      System.err.println("=" + i);
      return i;
    }
    
    public void getTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte)
    {
      System.err.printf(cName + ".getTagData(ID=%x, TagSig=%s)", new Object[] { paramProfile, signatureToString(paramInt) });
      System.err.println(" requested " + paramArrayOfByte.length + " byte(s)");
      tcmm.getTagData(paramProfile, paramInt, paramArrayOfByte);
    }
    
    public void setTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte)
    {
      System.err.print(cName + ".setTagData(ID=" + paramProfile + ", TagSig=" + paramInt + ")");
      System.err.println(" sending " + paramArrayOfByte.length + " byte(s)");
      tcmm.setTagData(paramProfile, paramInt, paramArrayOfByte);
    }
    
    public ColorTransform createTransform(ICC_Profile paramICC_Profile, int paramInt1, int paramInt2)
    {
      System.err.println(cName + ".createTransform(ICC_Profile,int,int)");
      return tcmm.createTransform(paramICC_Profile, paramInt1, paramInt2);
    }
    
    public ColorTransform createTransform(ColorTransform[] paramArrayOfColorTransform)
    {
      System.err.println(cName + ".createTransform(ColorTransform[])");
      return tcmm.createTransform(paramArrayOfColorTransform);
    }
    
    private static String signatureToString(int paramInt)
    {
      return String.format("%c%c%c%c", new Object[] { Character.valueOf((char)(0xFF & paramInt >> 24)), Character.valueOf((char)(0xFF & paramInt >> 16)), Character.valueOf((char)(0xFF & paramInt >> 8)), Character.valueOf((char)(0xFF & paramInt)) });
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\cmm\CMSManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */