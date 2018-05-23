package sun.security.jgss.spnego;

import java.io.IOException;
import java.io.PrintStream;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSUtil;
import sun.security.util.BitArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class NegTokenInit
  extends SpNegoToken
{
  private byte[] mechTypes = null;
  private Oid[] mechTypeList = null;
  private BitArray reqFlags = null;
  private byte[] mechToken = null;
  private byte[] mechListMIC = null;
  
  NegTokenInit(byte[] paramArrayOfByte1, BitArray paramBitArray, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
  {
    super(0);
    mechTypes = paramArrayOfByte1;
    reqFlags = paramBitArray;
    mechToken = paramArrayOfByte2;
    mechListMIC = paramArrayOfByte3;
  }
  
  public NegTokenInit(byte[] paramArrayOfByte)
    throws GSSException
  {
    super(0);
    parseToken(paramArrayOfByte);
  }
  
  final byte[] encode()
    throws GSSException
  {
    try
    {
      DerOutputStream localDerOutputStream1 = new DerOutputStream();
      if (mechTypes != null) {
        localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), mechTypes);
      }
      if (reqFlags != null)
      {
        localDerOutputStream2 = new DerOutputStream();
        localDerOutputStream2.putUnalignedBitString(reqFlags);
        localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
      }
      if (mechToken != null)
      {
        localDerOutputStream2 = new DerOutputStream();
        localDerOutputStream2.putOctetString(mechToken);
        localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localDerOutputStream2);
      }
      if (mechListMIC != null)
      {
        if (DEBUG) {
          System.out.println("SpNegoToken NegTokenInit: sending MechListMIC");
        }
        localDerOutputStream2 = new DerOutputStream();
        localDerOutputStream2.putOctetString(mechListMIC);
        localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), localDerOutputStream2);
      }
      DerOutputStream localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.write((byte)48, localDerOutputStream1);
      return localDerOutputStream2.toByteArray();
    }
    catch (IOException localIOException)
    {
      throw new GSSException(10, -1, "Invalid SPNEGO NegTokenInit token : " + localIOException.getMessage());
    }
  }
  
  private void parseToken(byte[] paramArrayOfByte)
    throws GSSException
  {
    try
    {
      DerValue localDerValue1 = new DerValue(paramArrayOfByte);
      if (!localDerValue1.isContextSpecific((byte)0)) {
        throw new IOException("SPNEGO NegoTokenInit : did not have right token type");
      }
      DerValue localDerValue2 = data.getDerValue();
      if (tag != 48) {
        throw new IOException("SPNEGO NegoTokenInit : did not have the Sequence tag");
      }
      int i = -1;
      while (data.available() > 0)
      {
        DerValue localDerValue3 = data.getDerValue();
        if (localDerValue3.isContextSpecific((byte)0))
        {
          i = checkNextField(i, 0);
          DerInputStream localDerInputStream = data;
          mechTypes = localDerInputStream.toByteArray();
          DerValue[] arrayOfDerValue = localDerInputStream.getSequence(0);
          mechTypeList = new Oid[arrayOfDerValue.length];
          ObjectIdentifier localObjectIdentifier = null;
          for (int j = 0; j < arrayOfDerValue.length; j++)
          {
            localObjectIdentifier = arrayOfDerValue[j].getOID();
            if (DEBUG) {
              System.out.println("SpNegoToken NegTokenInit: reading Mechanism Oid = " + localObjectIdentifier);
            }
            mechTypeList[j] = new Oid(localObjectIdentifier.toString());
          }
        }
        else if (localDerValue3.isContextSpecific((byte)1))
        {
          i = checkNextField(i, 1);
        }
        else if (localDerValue3.isContextSpecific((byte)2))
        {
          i = checkNextField(i, 2);
          if (DEBUG) {
            System.out.println("SpNegoToken NegTokenInit: reading Mech Token");
          }
          mechToken = data.getOctetString();
        }
        else if (localDerValue3.isContextSpecific((byte)3))
        {
          i = checkNextField(i, 3);
          if (!GSSUtil.useMSInterop())
          {
            mechListMIC = data.getOctetString();
            if (DEBUG) {
              System.out.println("SpNegoToken NegTokenInit: MechListMIC Token = " + getHexBytes(mechListMIC));
            }
          }
        }
      }
    }
    catch (IOException localIOException)
    {
      throw new GSSException(10, -1, "Invalid SPNEGO NegTokenInit token : " + localIOException.getMessage());
    }
  }
  
  byte[] getMechTypes()
  {
    return mechTypes;
  }
  
  public Oid[] getMechTypeList()
  {
    return mechTypeList;
  }
  
  BitArray getReqFlags()
  {
    return reqFlags;
  }
  
  public byte[] getMechToken()
  {
    return mechToken;
  }
  
  byte[] getMechListMIC()
  {
    return mechListMIC;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\spnego\NegTokenInit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */