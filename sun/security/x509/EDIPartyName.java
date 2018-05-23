package sun.security.x509;

import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EDIPartyName
  implements GeneralNameInterface
{
  private static final byte TAG_ASSIGNER = 0;
  private static final byte TAG_PARTYNAME = 1;
  private String assigner = null;
  private String party = null;
  private int myhash = -1;
  
  public EDIPartyName(String paramString1, String paramString2)
  {
    assigner = paramString1;
    party = paramString2;
  }
  
  public EDIPartyName(String paramString)
  {
    party = paramString;
  }
  
  public EDIPartyName(DerValue paramDerValue)
    throws IOException
  {
    DerInputStream localDerInputStream = new DerInputStream(paramDerValue.toByteArray());
    DerValue[] arrayOfDerValue = localDerInputStream.getSequence(2);
    int i = arrayOfDerValue.length;
    if ((i < 1) || (i > 2)) {
      throw new IOException("Invalid encoding of EDIPartyName");
    }
    for (int j = 0; j < i; j++)
    {
      DerValue localDerValue = arrayOfDerValue[j];
      if ((localDerValue.isContextSpecific((byte)0)) && (!localDerValue.isConstructed()))
      {
        if (assigner != null) {
          throw new IOException("Duplicate nameAssigner found in EDIPartyName");
        }
        localDerValue = data.getDerValue();
        assigner = localDerValue.getAsString();
      }
      if ((localDerValue.isContextSpecific((byte)1)) && (!localDerValue.isConstructed()))
      {
        if (party != null) {
          throw new IOException("Duplicate partyName found in EDIPartyName");
        }
        localDerValue = data.getDerValue();
        party = localDerValue.getAsString();
      }
    }
  }
  
  public int getType()
  {
    return 5;
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    if (assigner != null)
    {
      DerOutputStream localDerOutputStream3 = new DerOutputStream();
      localDerOutputStream3.putPrintableString(assigner);
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)0), localDerOutputStream3);
    }
    if (party == null) {
      throw new IOException("Cannot have null partyName");
    }
    localDerOutputStream2.putPrintableString(party);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)1), localDerOutputStream2);
    paramDerOutputStream.write((byte)48, localDerOutputStream1);
  }
  
  public String getAssignerName()
  {
    return assigner;
  }
  
  public String getPartyName()
  {
    return party;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof EDIPartyName)) {
      return false;
    }
    String str1 = assigner;
    if (assigner == null)
    {
      if (str1 != null) {
        return false;
      }
    }
    else if (!assigner.equals(str1)) {
      return false;
    }
    String str2 = party;
    if (party == null)
    {
      if (str2 != null) {
        return false;
      }
    }
    else if (!party.equals(str2)) {
      return false;
    }
    return true;
  }
  
  public int hashCode()
  {
    if (myhash == -1)
    {
      myhash = (37 + (party == null ? 1 : party.hashCode()));
      if (assigner != null) {
        myhash = (37 * myhash + assigner.hashCode());
      }
    }
    return myhash;
  }
  
  public String toString()
  {
    return "EDIPartyName: " + (assigner == null ? "" : new StringBuilder().append("  nameAssigner = ").append(assigner).append(",").toString()) + "  partyName = " + party;
  }
  
  public int constrains(GeneralNameInterface paramGeneralNameInterface)
    throws UnsupportedOperationException
  {
    int i;
    if (paramGeneralNameInterface == null) {
      i = -1;
    } else if (paramGeneralNameInterface.getType() != 5) {
      i = -1;
    } else {
      throw new UnsupportedOperationException("Narrowing, widening, and matching of names not supported for EDIPartyName");
    }
    return i;
  }
  
  public int subtreeDepth()
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("subtreeDepth() not supported for EDIPartyName");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\EDIPartyName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */