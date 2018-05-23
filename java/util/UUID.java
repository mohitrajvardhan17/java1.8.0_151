package java.util;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class UUID
  implements Serializable, Comparable<UUID>
{
  private static final long serialVersionUID = -4856846361193249489L;
  private final long mostSigBits;
  private final long leastSigBits;
  
  private UUID(byte[] paramArrayOfByte)
  {
    long l1 = 0L;
    long l2 = 0L;
    assert (paramArrayOfByte.length == 16) : "data must be 16 bytes in length";
    for (int i = 0; i < 8; i++) {
      l1 = l1 << 8 | paramArrayOfByte[i] & 0xFF;
    }
    for (i = 8; i < 16; i++) {
      l2 = l2 << 8 | paramArrayOfByte[i] & 0xFF;
    }
    mostSigBits = l1;
    leastSigBits = l2;
  }
  
  public UUID(long paramLong1, long paramLong2)
  {
    mostSigBits = paramLong1;
    leastSigBits = paramLong2;
  }
  
  public static UUID randomUUID()
  {
    SecureRandom localSecureRandom = Holder.numberGenerator;
    byte[] arrayOfByte = new byte[16];
    localSecureRandom.nextBytes(arrayOfByte);
    byte[] tmp17_14 = arrayOfByte;
    tmp17_14[6] = ((byte)(tmp17_14[6] & 0xF));
    byte[] tmp27_24 = arrayOfByte;
    tmp27_24[6] = ((byte)(tmp27_24[6] | 0x40));
    byte[] tmp37_34 = arrayOfByte;
    tmp37_34[8] = ((byte)(tmp37_34[8] & 0x3F));
    byte[] tmp47_44 = arrayOfByte;
    tmp47_44[8] = ((byte)(tmp47_44[8] | 0x80));
    return new UUID(arrayOfByte);
  }
  
  public static UUID nameUUIDFromBytes(byte[] paramArrayOfByte)
  {
    MessageDigest localMessageDigest;
    try
    {
      localMessageDigest = MessageDigest.getInstance("MD5");
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new InternalError("MD5 not supported", localNoSuchAlgorithmException);
    }
    byte[] arrayOfByte = localMessageDigest.digest(paramArrayOfByte);
    byte[] tmp30_27 = arrayOfByte;
    tmp30_27[6] = ((byte)(tmp30_27[6] & 0xF));
    byte[] tmp40_37 = arrayOfByte;
    tmp40_37[6] = ((byte)(tmp40_37[6] | 0x30));
    byte[] tmp50_47 = arrayOfByte;
    tmp50_47[8] = ((byte)(tmp50_47[8] & 0x3F));
    byte[] tmp60_57 = arrayOfByte;
    tmp60_57[8] = ((byte)(tmp60_57[8] | 0x80));
    return new UUID(arrayOfByte);
  }
  
  public static UUID fromString(String paramString)
  {
    String[] arrayOfString = paramString.split("-");
    if (arrayOfString.length != 5) {
      throw new IllegalArgumentException("Invalid UUID string: " + paramString);
    }
    for (int i = 0; i < 5; i++) {
      arrayOfString[i] = ("0x" + arrayOfString[i]);
    }
    long l1 = Long.decode(arrayOfString[0]).longValue();
    l1 <<= 16;
    l1 |= Long.decode(arrayOfString[1]).longValue();
    l1 <<= 16;
    l1 |= Long.decode(arrayOfString[2]).longValue();
    long l2 = Long.decode(arrayOfString[3]).longValue();
    l2 <<= 48;
    l2 |= Long.decode(arrayOfString[4]).longValue();
    return new UUID(l1, l2);
  }
  
  public long getLeastSignificantBits()
  {
    return leastSigBits;
  }
  
  public long getMostSignificantBits()
  {
    return mostSigBits;
  }
  
  public int version()
  {
    return (int)(mostSigBits >> 12 & 0xF);
  }
  
  public int variant()
  {
    return (int)(leastSigBits >>> (int)(64L - (leastSigBits >>> 62)) & leastSigBits >> 63);
  }
  
  public long timestamp()
  {
    if (version() != 1) {
      throw new UnsupportedOperationException("Not a time-based UUID");
    }
    return (mostSigBits & 0xFFF) << 48 | (mostSigBits >> 16 & 0xFFFF) << 32 | mostSigBits >>> 32;
  }
  
  public int clockSequence()
  {
    if (version() != 1) {
      throw new UnsupportedOperationException("Not a time-based UUID");
    }
    return (int)((leastSigBits & 0x3FFF000000000000) >>> 48);
  }
  
  public long node()
  {
    if (version() != 1) {
      throw new UnsupportedOperationException("Not a time-based UUID");
    }
    return leastSigBits & 0xFFFFFFFFFFFF;
  }
  
  public String toString()
  {
    return digits(mostSigBits >> 32, 8) + "-" + digits(mostSigBits >> 16, 4) + "-" + digits(mostSigBits, 4) + "-" + digits(leastSigBits >> 48, 4) + "-" + digits(leastSigBits, 12);
  }
  
  private static String digits(long paramLong, int paramInt)
  {
    long l = 1L << paramInt * 4;
    return Long.toHexString(l | paramLong & l - 1L).substring(1);
  }
  
  public int hashCode()
  {
    long l = mostSigBits ^ leastSigBits;
    return (int)(l >> 32) ^ (int)l;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((null == paramObject) || (paramObject.getClass() != UUID.class)) {
      return false;
    }
    UUID localUUID = (UUID)paramObject;
    return (mostSigBits == mostSigBits) && (leastSigBits == leastSigBits);
  }
  
  public int compareTo(UUID paramUUID)
  {
    return leastSigBits > leastSigBits ? 1 : leastSigBits < leastSigBits ? -1 : mostSigBits > mostSigBits ? 1 : mostSigBits < mostSigBits ? -1 : 0;
  }
  
  private static class Holder
  {
    static final SecureRandom numberGenerator = new SecureRandom();
    
    private Holder() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\UUID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */