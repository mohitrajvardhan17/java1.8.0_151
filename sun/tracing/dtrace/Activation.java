package sun.tracing.dtrace;

import java.security.Permission;

class Activation
{
  private SystemResource resource;
  private int referenceCount;
  
  Activation(String paramString, DTraceProvider[] paramArrayOfDTraceProvider)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    Object localObject1;
    if (localSecurityManager != null)
    {
      localObject1 = new RuntimePermission("com.sun.tracing.dtrace.createProvider");
      localSecurityManager.checkPermission((Permission)localObject1);
    }
    referenceCount = paramArrayOfDTraceProvider.length;
    for (Object localObject2 : paramArrayOfDTraceProvider) {
      ((DTraceProvider)localObject2).setActivation(this);
    }
    resource = new SystemResource(this, JVM.activate(paramString, paramArrayOfDTraceProvider));
  }
  
  void disposeProvider(DTraceProvider paramDTraceProvider)
  {
    if (--referenceCount == 0) {
      resource.dispose();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tracing\dtrace\Activation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */