package javax.rmi.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.OutputStream;

public abstract interface ValueHandlerMultiFormat
  extends ValueHandler
{
  public abstract byte getMaximumStreamFormatVersion();
  
  public abstract void writeValue(OutputStream paramOutputStream, Serializable paramSerializable, byte paramByte);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\rmi\CORBA\ValueHandlerMultiFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */