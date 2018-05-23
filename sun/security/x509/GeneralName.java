package sun.security.x509;

import java.io.IOException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class GeneralName
{
  private GeneralNameInterface name = null;
  
  public GeneralName(GeneralNameInterface paramGeneralNameInterface)
  {
    if (paramGeneralNameInterface == null) {
      throw new NullPointerException("GeneralName must not be null");
    }
    name = paramGeneralNameInterface;
  }
  
  public GeneralName(DerValue paramDerValue)
    throws IOException
  {
    this(paramDerValue, false);
  }
  
  public GeneralName(DerValue paramDerValue, boolean paramBoolean)
    throws IOException
  {
    int i = (short)(byte)(tag & 0x1F);
    switch (i)
    {
    case 0: 
      if ((paramDerValue.isContextSpecific()) && (paramDerValue.isConstructed()))
      {
        paramDerValue.resetTag((byte)48);
        name = new OtherName(paramDerValue);
      }
      else
      {
        throw new IOException("Invalid encoding of Other-Name");
      }
      break;
    case 1: 
      if ((paramDerValue.isContextSpecific()) && (!paramDerValue.isConstructed()))
      {
        paramDerValue.resetTag((byte)22);
        name = new RFC822Name(paramDerValue);
      }
      else
      {
        throw new IOException("Invalid encoding of RFC822 name");
      }
      break;
    case 2: 
      if ((paramDerValue.isContextSpecific()) && (!paramDerValue.isConstructed()))
      {
        paramDerValue.resetTag((byte)22);
        name = new DNSName(paramDerValue);
      }
      else
      {
        throw new IOException("Invalid encoding of DNS name");
      }
      break;
    case 6: 
      if ((paramDerValue.isContextSpecific()) && (!paramDerValue.isConstructed()))
      {
        paramDerValue.resetTag((byte)22);
        name = (paramBoolean ? URIName.nameConstraint(paramDerValue) : new URIName(paramDerValue));
      }
      else
      {
        throw new IOException("Invalid encoding of URI");
      }
      break;
    case 7: 
      if ((paramDerValue.isContextSpecific()) && (!paramDerValue.isConstructed()))
      {
        paramDerValue.resetTag((byte)4);
        name = new IPAddressName(paramDerValue);
      }
      else
      {
        throw new IOException("Invalid encoding of IP address");
      }
      break;
    case 8: 
      if ((paramDerValue.isContextSpecific()) && (!paramDerValue.isConstructed()))
      {
        paramDerValue.resetTag((byte)6);
        name = new OIDName(paramDerValue);
      }
      else
      {
        throw new IOException("Invalid encoding of OID name");
      }
      break;
    case 4: 
      if ((paramDerValue.isContextSpecific()) && (paramDerValue.isConstructed())) {
        name = new X500Name(paramDerValue.getData());
      } else {
        throw new IOException("Invalid encoding of Directory name");
      }
      break;
    case 5: 
      if ((paramDerValue.isContextSpecific()) && (paramDerValue.isConstructed()))
      {
        paramDerValue.resetTag((byte)48);
        name = new EDIPartyName(paramDerValue);
      }
      else
      {
        throw new IOException("Invalid encoding of EDI name");
      }
      break;
    case 3: 
    default: 
      throw new IOException("Unrecognized GeneralName tag, (" + i + ")");
    }
  }
  
  public int getType()
  {
    return name.getType();
  }
  
  public GeneralNameInterface getName()
  {
    return name;
  }
  
  public String toString()
  {
    return name.toString();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof GeneralName)) {
      return false;
    }
    GeneralNameInterface localGeneralNameInterface = name;
    try
    {
      return name.constrains(localGeneralNameInterface) == 0;
    }
    catch (UnsupportedOperationException localUnsupportedOperationException) {}
    return false;
  }
  
  public int hashCode()
  {
    return name.hashCode();
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    name.encode(localDerOutputStream);
    int i = name.getType();
    if ((i == 0) || (i == 3) || (i == 5)) {
      paramDerOutputStream.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)i), localDerOutputStream);
    } else if (i == 4) {
      paramDerOutputStream.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)i), localDerOutputStream);
    } else {
      paramDerOutputStream.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)i), localDerOutputStream);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\GeneralName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */