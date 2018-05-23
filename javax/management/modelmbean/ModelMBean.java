package javax.management.modelmbean;

import javax.management.DynamicMBean;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.PersistentMBean;
import javax.management.RuntimeOperationsException;

public abstract interface ModelMBean
  extends DynamicMBean, PersistentMBean, ModelMBeanNotificationBroadcaster
{
  public abstract void setModelMBeanInfo(ModelMBeanInfo paramModelMBeanInfo)
    throws MBeanException, RuntimeOperationsException;
  
  public abstract void setManagedResource(Object paramObject, String paramString)
    throws MBeanException, RuntimeOperationsException, InstanceNotFoundException, InvalidTargetObjectTypeException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\modelmbean\ModelMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */