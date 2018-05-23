package sun.security.x509;

import java.lang.reflect.Field;
import sun.misc.HexDumpEncoder;

class UnparseableExtension
  extends Extension
{
  private String name = "";
  private Throwable why;
  
  public UnparseableExtension(Extension paramExtension, Throwable paramThrowable)
  {
    super(paramExtension);
    try
    {
      Class localClass = OIDMap.getClass(paramExtension.getExtensionId());
      if (localClass != null)
      {
        Field localField = localClass.getDeclaredField("NAME");
        name = ((String)localField.get(null) + " ");
      }
    }
    catch (Exception localException) {}
    why = paramThrowable;
  }
  
  public String toString()
  {
    return super.toString() + "Unparseable " + name + "extension due to\n" + why + "\n\n" + new HexDumpEncoder().encodeBuffer(getExtensionValue());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\UnparseableExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */