package com.sun.org.glassfish.external.amx;

import com.sun.org.glassfish.external.arc.Stability;
import com.sun.org.glassfish.external.arc.Taxonomy;
import javax.management.ObjectName;
import javax.management.remote.JMXServiceURL;

@Taxonomy(stability=Stability.UNCOMMITTED)
public abstract interface BootAMXMBean
{
  public static final String BOOT_AMX_OPERATION_NAME = "bootAMX";
  
  public abstract ObjectName bootAMX();
  
  public abstract JMXServiceURL[] getJMXServiceURLs();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\amx\BootAMXMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */