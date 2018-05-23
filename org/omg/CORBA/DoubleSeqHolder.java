package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class DoubleSeqHolder
  implements Streamable
{
  public double[] value = null;
  
  public DoubleSeqHolder() {}
  
  public DoubleSeqHolder(double[] paramArrayOfDouble)
  {
    value = paramArrayOfDouble;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = DoubleSeqHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    DoubleSeqHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return DoubleSeqHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\DoubleSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */