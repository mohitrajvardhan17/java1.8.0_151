package com.sun.management;

import java.io.IOException;
import java.lang.management.PlatformManagedObject;
import java.util.List;
import jdk.Exported;

@Exported
public abstract interface HotSpotDiagnosticMXBean
  extends PlatformManagedObject
{
  public abstract void dumpHeap(String paramString, boolean paramBoolean)
    throws IOException;
  
  public abstract List<VMOption> getDiagnosticOptions();
  
  public abstract VMOption getVMOption(String paramString);
  
  public abstract void setVMOption(String paramString1, String paramString2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\management\HotSpotDiagnosticMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */