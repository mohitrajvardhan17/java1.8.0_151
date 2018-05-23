package org.omg.PortableInterceptor;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ObjectReferenceTemplateSeqHolder
  implements Streamable
{
  public ObjectReferenceTemplate[] value = null;
  
  public ObjectReferenceTemplateSeqHolder() {}
  
  public ObjectReferenceTemplateSeqHolder(ObjectReferenceTemplate[] paramArrayOfObjectReferenceTemplate)
  {
    value = paramArrayOfObjectReferenceTemplate;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ObjectReferenceTemplateSeqHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ObjectReferenceTemplateSeqHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ObjectReferenceTemplateSeqHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\ObjectReferenceTemplateSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */