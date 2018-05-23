package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class FloatSeqHolder
  implements Streamable
{
  public float[] value = null;
  
  public FloatSeqHolder() {}
  
  public FloatSeqHolder(float[] paramArrayOfFloat)
  {
    value = paramArrayOfFloat;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = FloatSeqHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    FloatSeqHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return FloatSeqHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\FloatSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */