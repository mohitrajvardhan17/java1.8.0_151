package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class ServiceInformation
  implements IDLEntity
{
  public int[] service_options;
  public ServiceDetail[] service_details;
  
  public ServiceInformation() {}
  
  public ServiceInformation(int[] paramArrayOfInt, ServiceDetail[] paramArrayOfServiceDetail)
  {
    service_options = paramArrayOfInt;
    service_details = paramArrayOfServiceDetail;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ServiceInformation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */