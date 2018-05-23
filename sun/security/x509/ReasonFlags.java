package sun.security.x509;

import java.io.IOException;
import java.util.Enumeration;
import sun.security.util.BitArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class ReasonFlags
{
  public static final String UNUSED = "unused";
  public static final String KEY_COMPROMISE = "key_compromise";
  public static final String CA_COMPROMISE = "ca_compromise";
  public static final String AFFILIATION_CHANGED = "affiliation_changed";
  public static final String SUPERSEDED = "superseded";
  public static final String CESSATION_OF_OPERATION = "cessation_of_operation";
  public static final String CERTIFICATE_HOLD = "certificate_hold";
  public static final String PRIVILEGE_WITHDRAWN = "privilege_withdrawn";
  public static final String AA_COMPROMISE = "aa_compromise";
  private static final String[] NAMES = { "unused", "key_compromise", "ca_compromise", "affiliation_changed", "superseded", "cessation_of_operation", "certificate_hold", "privilege_withdrawn", "aa_compromise" };
  private boolean[] bitString;
  
  private static int name2Index(String paramString)
    throws IOException
  {
    for (int i = 0; i < NAMES.length; i++) {
      if (NAMES[i].equalsIgnoreCase(paramString)) {
        return i;
      }
    }
    throw new IOException("Name not recognized by ReasonFlags");
  }
  
  private boolean isSet(int paramInt)
  {
    return (paramInt < bitString.length) && (bitString[paramInt] != 0);
  }
  
  private void set(int paramInt, boolean paramBoolean)
  {
    if (paramInt >= bitString.length)
    {
      boolean[] arrayOfBoolean = new boolean[paramInt + 1];
      System.arraycopy(bitString, 0, arrayOfBoolean, 0, bitString.length);
      bitString = arrayOfBoolean;
    }
    bitString[paramInt] = paramBoolean;
  }
  
  public ReasonFlags(byte[] paramArrayOfByte)
  {
    bitString = new BitArray(paramArrayOfByte.length * 8, paramArrayOfByte).toBooleanArray();
  }
  
  public ReasonFlags(boolean[] paramArrayOfBoolean)
  {
    bitString = paramArrayOfBoolean;
  }
  
  public ReasonFlags(BitArray paramBitArray)
  {
    bitString = paramBitArray.toBooleanArray();
  }
  
  public ReasonFlags(DerInputStream paramDerInputStream)
    throws IOException
  {
    DerValue localDerValue = paramDerInputStream.getDerValue();
    bitString = localDerValue.getUnalignedBitString(true).toBooleanArray();
  }
  
  public ReasonFlags(DerValue paramDerValue)
    throws IOException
  {
    bitString = paramDerValue.getUnalignedBitString(true).toBooleanArray();
  }
  
  public boolean[] getFlags()
  {
    return bitString;
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (!(paramObject instanceof Boolean)) {
      throw new IOException("Attribute must be of type Boolean.");
    }
    boolean bool = ((Boolean)paramObject).booleanValue();
    set(name2Index(paramString), bool);
  }
  
  public Object get(String paramString)
    throws IOException
  {
    return Boolean.valueOf(isSet(name2Index(paramString)));
  }
  
  public void delete(String paramString)
    throws IOException
  {
    set(paramString, Boolean.FALSE);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("Reason Flags [\n");
    if (isSet(0)) {
      localStringBuilder.append("  Unused\n");
    }
    if (isSet(1)) {
      localStringBuilder.append("  Key Compromise\n");
    }
    if (isSet(2)) {
      localStringBuilder.append("  CA Compromise\n");
    }
    if (isSet(3)) {
      localStringBuilder.append("  Affiliation_Changed\n");
    }
    if (isSet(4)) {
      localStringBuilder.append("  Superseded\n");
    }
    if (isSet(5)) {
      localStringBuilder.append("  Cessation Of Operation\n");
    }
    if (isSet(6)) {
      localStringBuilder.append("  Certificate Hold\n");
    }
    if (isSet(7)) {
      localStringBuilder.append("  Privilege Withdrawn\n");
    }
    if (isSet(8)) {
      localStringBuilder.append("  AA Compromise\n");
    }
    localStringBuilder.append("]\n");
    return localStringBuilder.toString();
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    paramDerOutputStream.putTruncatedUnalignedBitString(new BitArray(bitString));
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    for (int i = 0; i < NAMES.length; i++) {
      localAttributeNameEnumeration.addElement(NAMES[i]);
    }
    return localAttributeNameEnumeration.elements();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\ReasonFlags.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */