package sun.java2d.cmm.kcms;

import sun.java2d.cmm.CMMServiceProvider;
import sun.java2d.cmm.PCMM;

public final class KcmsServiceProvider
  extends CMMServiceProvider
{
  public KcmsServiceProvider() {}
  
  protected PCMM getModule()
  {
    return CMM.getModule();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\cmm\kcms\KcmsServiceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */