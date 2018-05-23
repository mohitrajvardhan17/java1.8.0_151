package sun.security.x509;

import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class GeneralSubtree
{
  private static final byte TAG_MIN = 0;
  private static final byte TAG_MAX = 1;
  private static final int MIN_DEFAULT = 0;
  private GeneralName name;
  private int minimum = 0;
  private int maximum = -1;
  private int myhash = -1;
  
  public GeneralSubtree(GeneralName paramGeneralName, int paramInt1, int paramInt2)
  {
    name = paramGeneralName;
    minimum = paramInt1;
    maximum = paramInt2;
  }
  
  public GeneralSubtree(DerValue paramDerValue)
    throws IOException
  {
    if (tag != 48) {
      throw new IOException("Invalid encoding for GeneralSubtree.");
    }
    name = new GeneralName(data.getDerValue(), true);
    while (data.available() != 0)
    {
      DerValue localDerValue = data.getDerValue();
      if ((localDerValue.isContextSpecific((byte)0)) && (!localDerValue.isConstructed()))
      {
        localDerValue.resetTag((byte)2);
        minimum = localDerValue.getInteger();
      }
      else if ((localDerValue.isContextSpecific((byte)1)) && (!localDerValue.isConstructed()))
      {
        localDerValue.resetTag((byte)2);
        maximum = localDerValue.getInteger();
      }
      else
      {
        throw new IOException("Invalid encoding of GeneralSubtree.");
      }
    }
  }
  
  public GeneralName getName()
  {
    return name;
  }
  
  public int getMinimum()
  {
    return minimum;
  }
  
  public int getMaximum()
  {
    return maximum;
  }
  
  public String toString()
  {
    String str = "\n   GeneralSubtree: [\n    GeneralName: " + (name == null ? "" : name.toString()) + "\n    Minimum: " + minimum;
    if (maximum == -1) {
      str = str + "\t    Maximum: undefined";
    } else {
      str = str + "\t    Maximum: " + maximum;
    }
    str = str + "    ]\n";
    return str;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof GeneralSubtree)) {
      return false;
    }
    GeneralSubtree localGeneralSubtree = (GeneralSubtree)paramObject;
    if (name == null)
    {
      if (name != null) {
        return false;
      }
    }
    else if (!name.equals(name)) {
      return false;
    }
    if (minimum != minimum) {
      return false;
    }
    return maximum == maximum;
  }
  
  public int hashCode()
  {
    if (myhash == -1)
    {
      myhash = 17;
      if (name != null) {
        myhash = (37 * myhash + name.hashCode());
      }
      if (minimum != 0) {
        myhash = (37 * myhash + minimum);
      }
      if (maximum != -1) {
        myhash = (37 * myhash + maximum);
      }
    }
    return myhash;
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    name.encode(localDerOutputStream1);
    DerOutputStream localDerOutputStream2;
    if (minimum != 0)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putInteger(minimum);
      localDerOutputStream1.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)0), localDerOutputStream2);
    }
    if (maximum != -1)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putInteger(maximum);
      localDerOutputStream1.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)1), localDerOutputStream2);
    }
    paramDerOutputStream.write((byte)48, localDerOutputStream1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\GeneralSubtree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */