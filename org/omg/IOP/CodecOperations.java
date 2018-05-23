package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.IOP.CodecPackage.FormatMismatch;
import org.omg.IOP.CodecPackage.InvalidTypeForEncoding;
import org.omg.IOP.CodecPackage.TypeMismatch;

public abstract interface CodecOperations
{
  public abstract byte[] encode(Any paramAny)
    throws InvalidTypeForEncoding;
  
  public abstract Any decode(byte[] paramArrayOfByte)
    throws FormatMismatch;
  
  public abstract byte[] encode_value(Any paramAny)
    throws InvalidTypeForEncoding;
  
  public abstract Any decode_value(byte[] paramArrayOfByte, TypeCode paramTypeCode)
    throws FormatMismatch, TypeMismatch;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\CodecOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */