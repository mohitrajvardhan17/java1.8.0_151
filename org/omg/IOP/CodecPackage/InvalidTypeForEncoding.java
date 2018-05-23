package org.omg.IOP.CodecPackage;

import org.omg.CORBA.UserException;

public final class InvalidTypeForEncoding
  extends UserException
{
  public InvalidTypeForEncoding()
  {
    super(InvalidTypeForEncodingHelper.id());
  }
  
  public InvalidTypeForEncoding(String paramString)
  {
    super(InvalidTypeForEncodingHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\CodecPackage\InvalidTypeForEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */