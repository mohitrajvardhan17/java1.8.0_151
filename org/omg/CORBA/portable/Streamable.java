package org.omg.CORBA.portable;

import org.omg.CORBA.TypeCode;

public abstract interface Streamable
{
  public abstract void _read(InputStream paramInputStream);
  
  public abstract void _write(OutputStream paramOutputStream);
  
  public abstract TypeCode _type();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\portable\Streamable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */