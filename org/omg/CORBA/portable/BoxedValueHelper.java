package org.omg.CORBA.portable;

import java.io.Serializable;

public abstract interface BoxedValueHelper
{
  public abstract Serializable read_value(InputStream paramInputStream);
  
  public abstract void write_value(OutputStream paramOutputStream, Serializable paramSerializable);
  
  public abstract String get_id();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\portable\BoxedValueHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */