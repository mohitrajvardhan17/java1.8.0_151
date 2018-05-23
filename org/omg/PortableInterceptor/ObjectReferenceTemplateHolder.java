package org.omg.PortableInterceptor;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ObjectReferenceTemplateHolder
  implements Streamable
{
  public ObjectReferenceTemplate value = null;
  
  public ObjectReferenceTemplateHolder() {}
  
  public ObjectReferenceTemplateHolder(ObjectReferenceTemplate paramObjectReferenceTemplate)
  {
    value = paramObjectReferenceTemplate;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ObjectReferenceTemplateHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ObjectReferenceTemplateHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ObjectReferenceTemplateHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\ObjectReferenceTemplateHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */