package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class TaggedComponentHolder
  implements Streamable
{
  public TaggedComponent value = null;
  
  public TaggedComponentHolder() {}
  
  public TaggedComponentHolder(TaggedComponent paramTaggedComponent)
  {
    value = paramTaggedComponent;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = TaggedComponentHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    TaggedComponentHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return TaggedComponentHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\TaggedComponentHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */