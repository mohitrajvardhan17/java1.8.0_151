package sun.java2d.cmm;

public abstract class CMMServiceProvider
{
  public CMMServiceProvider() {}
  
  public final PCMM getColorManagementModule()
  {
    if (CMSManager.canCreateModule()) {
      return getModule();
    }
    return null;
  }
  
  protected abstract PCMM getModule();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\cmm\CMMServiceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */