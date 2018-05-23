package sun.java2d.cmm.lcms;

import sun.java2d.cmm.CMMServiceProvider;
import sun.java2d.cmm.PCMM;

public final class LcmsServiceProvider
  extends CMMServiceProvider
{
  public LcmsServiceProvider() {}
  
  protected PCMM getModule()
  {
    return LCMS.getModule();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\cmm\lcms\LcmsServiceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */