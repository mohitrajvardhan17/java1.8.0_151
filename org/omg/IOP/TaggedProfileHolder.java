package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class TaggedProfileHolder
  implements Streamable
{
  public TaggedProfile value = null;
  
  public TaggedProfileHolder() {}
  
  public TaggedProfileHolder(TaggedProfile paramTaggedProfile)
  {
    value = paramTaggedProfile;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = TaggedProfileHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    TaggedProfileHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return TaggedProfileHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\TaggedProfileHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */