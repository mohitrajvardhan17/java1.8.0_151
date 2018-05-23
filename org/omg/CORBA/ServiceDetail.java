package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class ServiceDetail
  implements IDLEntity
{
  public int service_detail_type;
  public byte[] service_detail;
  
  public ServiceDetail() {}
  
  public ServiceDetail(int paramInt, byte[] paramArrayOfByte)
  {
    service_detail_type = paramInt;
    service_detail = paramArrayOfByte;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ServiceDetail.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */