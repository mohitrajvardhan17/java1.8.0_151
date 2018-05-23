package org.omg.IOP.CodecFactoryPackage;

import org.omg.CORBA.UserException;

public final class UnknownEncoding
  extends UserException
{
  public UnknownEncoding()
  {
    super(UnknownEncodingHelper.id());
  }
  
  public UnknownEncoding(String paramString)
  {
    super(UnknownEncodingHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\CodecFactoryPackage\UnknownEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */