package org.omg.PortableInterceptor;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ObjectReferenceFactoryHolder
  implements Streamable
{
  public ObjectReferenceFactory value = null;
  
  public ObjectReferenceFactoryHolder() {}
  
  public ObjectReferenceFactoryHolder(ObjectReferenceFactory paramObjectReferenceFactory)
  {
    value = paramObjectReferenceFactory;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ObjectReferenceFactoryHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ObjectReferenceFactoryHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ObjectReferenceFactoryHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\ObjectReferenceFactoryHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */