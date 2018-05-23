package org.omg.CORBA.portable;

import java.io.Serializable;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract interface ValueFactory
{
  public abstract Serializable read_value(InputStream paramInputStream);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\portable\ValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */