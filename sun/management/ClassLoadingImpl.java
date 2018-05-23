package sun.management;

import java.lang.management.ClassLoadingMXBean;
import javax.management.ObjectName;

class ClassLoadingImpl
  implements ClassLoadingMXBean
{
  private final VMManagement jvm;
  
  ClassLoadingImpl(VMManagement paramVMManagement)
  {
    jvm = paramVMManagement;
  }
  
  public long getTotalLoadedClassCount()
  {
    return jvm.getTotalClassCount();
  }
  
  public int getLoadedClassCount()
  {
    return jvm.getLoadedClassCount();
  }
  
  public long getUnloadedClassCount()
  {
    return jvm.getUnloadedClassCount();
  }
  
  public boolean isVerbose()
  {
    return jvm.getVerboseClass();
  }
  
  public void setVerbose(boolean paramBoolean)
  {
    Util.checkControlAccess();
    setVerboseClass(paramBoolean);
  }
  
  static native void setVerboseClass(boolean paramBoolean);
  
  public ObjectName getObjectName()
  {
    return Util.newObjectName("java.lang:type=ClassLoading");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\ClassLoadingImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */