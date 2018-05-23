package org.omg.IOP.CodecPackage;

import org.omg.CORBA.UserException;

public final class FormatMismatch
  extends UserException
{
  public FormatMismatch()
  {
    super(FormatMismatchHelper.id());
  }
  
  public FormatMismatch(String paramString)
  {
    super(FormatMismatchHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\CodecPackage\FormatMismatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */