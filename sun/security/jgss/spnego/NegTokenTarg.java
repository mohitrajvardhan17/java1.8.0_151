package sun.security.jgss.spnego;

import java.io.IOException;
import java.io.PrintStream;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSUtil;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class NegTokenTarg
  extends SpNegoToken
{
  private int negResult = 0;
  private Oid supportedMech = null;
  private byte[] responseToken = null;
  private byte[] mechListMIC = null;
  
  NegTokenTarg(int paramInt, Oid paramOid, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    super(1);
    negResult = paramInt;
    supportedMech = paramOid;
    responseToken = paramArrayOfByte1;
    mechListMIC = paramArrayOfByte2;
  }
  
  public NegTokenTarg(byte[] paramArrayOfByte)
    throws GSSException
  {
    super(1);
    parseToken(paramArrayOfByte);
  }
  
  final byte[] encode()
    throws GSSException
  {
    try
    {
      DerOutputStream localDerOutputStream1 = new DerOutputStream();
      DerOutputStream localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putEnumerated(negResult);
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
      if (supportedMech != null)
      {
        localDerOutputStream3 = new DerOutputStream();
        byte[] arrayOfByte = supportedMech.getDER();
        localDerOutputStream3.write(arrayOfByte);
        localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream3);
      }
      if (responseToken != null)
      {
        localDerOutputStream3 = new DerOutputStream();
        localDerOutputStream3.putOctetString(responseToken);
        localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localDerOutputStream3);
      }
      if (mechListMIC != null)
      {
        if (DEBUG) {
          System.out.println("SpNegoToken NegTokenTarg: sending MechListMIC");
        }
        localDerOutputStream3 = new DerOutputStream();
        localDerOutputStream3.putOctetString(mechListMIC);
        localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), localDerOutputStream3);
      }
      else if ((GSSUtil.useMSInterop()) && (responseToken != null))
      {
        if (DEBUG) {
          System.out.println("SpNegoToken NegTokenTarg: sending additional token for MS Interop");
        }
        localDerOutputStream3 = new DerOutputStream();
        localDerOutputStream3.putOctetString(responseToken);
        localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), localDerOutputStream3);
      }
      DerOutputStream localDerOutputStream3 = new DerOutputStream();
      localDerOutputStream3.write((byte)48, localDerOutputStream1);
      return localDerOutputStream3.toByteArray();
    }
    catch (IOException localIOException)
    {
      throw new GSSException(10, -1, "Invalid SPNEGO NegTokenTarg token : " + localIOException.getMessage());
    }
  }
  
  private void parseToken(byte[] paramArrayOfByte)
    throws GSSException
  {
    try
    {
      DerValue localDerValue1 = new DerValue(paramArrayOfByte);
      if (!localDerValue1.isContextSpecific((byte)1)) {
        throw new IOException("SPNEGO NegoTokenTarg : did not have the right token type");
      }
      DerValue localDerValue2 = data.getDerValue();
      if (tag != 48) {
        throw new IOException("SPNEGO NegoTokenTarg : did not have the Sequence tag");
      }
      int i = -1;
      while (data.available() > 0)
      {
        DerValue localDerValue3 = data.getDerValue();
        if (localDerValue3.isContextSpecific((byte)0))
        {
          i = checkNextField(i, 0);
          negResult = data.getEnumerated();
          if (DEBUG) {
            System.out.println("SpNegoToken NegTokenTarg: negotiated result = " + getNegoResultString(negResult));
          }
        }
        else if (localDerValue3.isContextSpecific((byte)1))
        {
          i = checkNextField(i, 1);
          ObjectIdentifier localObjectIdentifier = data.getOID();
          supportedMech = new Oid(localObjectIdentifier.toString());
          if (DEBUG) {
            System.out.println("SpNegoToken NegTokenTarg: supported mechanism = " + supportedMech);
          }
        }
        else if (localDerValue3.isContextSpecific((byte)2))
        {
          i = checkNextField(i, 2);
          responseToken = data.getOctetString();
        }
        else if (localDerValue3.isContextSpecific((byte)3))
        {
          i = checkNextField(i, 3);
          if (!GSSUtil.useMSInterop())
          {
            mechListMIC = data.getOctetString();
            if (DEBUG) {
              System.out.println("SpNegoToken NegTokenTarg: MechListMIC Token = " + getHexBytes(mechListMIC));
            }
          }
        }
      }
    }
    catch (IOException localIOException)
    {
      throw new GSSException(10, -1, "Invalid SPNEGO NegTokenTarg token : " + localIOException.getMessage());
    }
  }
  
  int getNegotiatedResult()
  {
    return negResult;
  }
  
  public Oid getSupportedMech()
  {
    return supportedMech;
  }
  
  byte[] getResponseToken()
  {
    return responseToken;
  }
  
  byte[] getMechListMIC()
  {
    return mechListMIC;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\spnego\NegTokenTarg.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */