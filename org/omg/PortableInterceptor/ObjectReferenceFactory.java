package org.omg.PortableInterceptor;

import org.omg.CORBA.portable.ValueBase;

public abstract interface ObjectReferenceFactory
  extends ValueBase
{
  public abstract org.omg.CORBA.Object make_object(String paramString, byte[] paramArrayOfByte);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\ObjectReferenceFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */