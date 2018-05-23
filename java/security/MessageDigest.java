package java.security;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import sun.security.util.Debug;

public abstract class MessageDigest
  extends MessageDigestSpi
{
  private static final Debug pdebug = Debug.getInstance("provider", "Provider");
  private static final boolean skipDebug = (Debug.isOn("engine=")) && (!Debug.isOn("messagedigest"));
  private String algorithm;
  private static final int INITIAL = 0;
  private static final int IN_PROGRESS = 1;
  private int state = 0;
  private Provider provider;
  
  protected MessageDigest(String paramString)
  {
    algorithm = paramString;
  }
  
  public static MessageDigest getInstance(String paramString)
    throws NoSuchAlgorithmException
  {
    try
    {
      Object[] arrayOfObject = Security.getImpl(paramString, "MessageDigest", (String)null);
      Object localObject;
      if ((arrayOfObject[0] instanceof MessageDigest)) {
        localObject = (MessageDigest)arrayOfObject[0];
      } else {
        localObject = new Delegate((MessageDigestSpi)arrayOfObject[0], paramString);
      }
      provider = ((Provider)arrayOfObject[1]);
      if ((!skipDebug) && (pdebug != null)) {
        pdebug.println("MessageDigest." + paramString + " algorithm from: " + provider.getName());
      }
      return (MessageDigest)localObject;
    }
    catch (NoSuchProviderException localNoSuchProviderException)
    {
      throw new NoSuchAlgorithmException(paramString + " not found");
    }
  }
  
  public static MessageDigest getInstance(String paramString1, String paramString2)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    if ((paramString2 == null) || (paramString2.length() == 0)) {
      throw new IllegalArgumentException("missing provider");
    }
    Object[] arrayOfObject = Security.getImpl(paramString1, "MessageDigest", paramString2);
    if ((arrayOfObject[0] instanceof MessageDigest))
    {
      localObject = (MessageDigest)arrayOfObject[0];
      provider = ((Provider)arrayOfObject[1]);
      return (MessageDigest)localObject;
    }
    Object localObject = new Delegate((MessageDigestSpi)arrayOfObject[0], paramString1);
    provider = ((Provider)arrayOfObject[1]);
    return (MessageDigest)localObject;
  }
  
  public static MessageDigest getInstance(String paramString, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    if (paramProvider == null) {
      throw new IllegalArgumentException("missing provider");
    }
    Object[] arrayOfObject = Security.getImpl(paramString, "MessageDigest", paramProvider);
    if ((arrayOfObject[0] instanceof MessageDigest))
    {
      localObject = (MessageDigest)arrayOfObject[0];
      provider = ((Provider)arrayOfObject[1]);
      return (MessageDigest)localObject;
    }
    Object localObject = new Delegate((MessageDigestSpi)arrayOfObject[0], paramString);
    provider = ((Provider)arrayOfObject[1]);
    return (MessageDigest)localObject;
  }
  
  public final Provider getProvider()
  {
    return provider;
  }
  
  public void update(byte paramByte)
  {
    engineUpdate(paramByte);
    state = 1;
  }
  
  public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte == null) {
      throw new IllegalArgumentException("No input buffer given");
    }
    if (paramArrayOfByte.length - paramInt1 < paramInt2) {
      throw new IllegalArgumentException("Input buffer too short");
    }
    engineUpdate(paramArrayOfByte, paramInt1, paramInt2);
    state = 1;
  }
  
  public void update(byte[] paramArrayOfByte)
  {
    engineUpdate(paramArrayOfByte, 0, paramArrayOfByte.length);
    state = 1;
  }
  
  public final void update(ByteBuffer paramByteBuffer)
  {
    if (paramByteBuffer == null) {
      throw new NullPointerException();
    }
    engineUpdate(paramByteBuffer);
    state = 1;
  }
  
  public byte[] digest()
  {
    byte[] arrayOfByte = engineDigest();
    state = 0;
    return arrayOfByte;
  }
  
  public int digest(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws DigestException
  {
    if (paramArrayOfByte == null) {
      throw new IllegalArgumentException("No output buffer given");
    }
    if (paramArrayOfByte.length - paramInt1 < paramInt2) {
      throw new IllegalArgumentException("Output buffer too small for specified offset and length");
    }
    int i = engineDigest(paramArrayOfByte, paramInt1, paramInt2);
    state = 0;
    return i;
  }
  
  public byte[] digest(byte[] paramArrayOfByte)
  {
    update(paramArrayOfByte);
    return digest();
  }
  
  public String toString()
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    PrintStream localPrintStream = new PrintStream(localByteArrayOutputStream);
    localPrintStream.print(algorithm + " Message Digest from " + provider.getName() + ", ");
    switch (state)
    {
    case 0: 
      localPrintStream.print("<initialized>");
      break;
    case 1: 
      localPrintStream.print("<in progress>");
    }
    localPrintStream.println();
    return localByteArrayOutputStream.toString();
  }
  
  public static boolean isEqual(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if (paramArrayOfByte1 == paramArrayOfByte2) {
      return true;
    }
    if ((paramArrayOfByte1 == null) || (paramArrayOfByte2 == null)) {
      return false;
    }
    if (paramArrayOfByte1.length != paramArrayOfByte2.length) {
      return false;
    }
    int i = 0;
    for (int j = 0; j < paramArrayOfByte1.length; j++) {
      i |= paramArrayOfByte1[j] ^ paramArrayOfByte2[j];
    }
    return i == 0;
  }
  
  public void reset()
  {
    engineReset();
    state = 0;
  }
  
  public final String getAlgorithm()
  {
    return algorithm;
  }
  
  public final int getDigestLength()
  {
    int i = engineGetDigestLength();
    if (i == 0) {
      try
      {
        MessageDigest localMessageDigest = (MessageDigest)clone();
        byte[] arrayOfByte = localMessageDigest.digest();
        return arrayOfByte.length;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        return i;
      }
    }
    return i;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    if ((this instanceof Cloneable)) {
      return super.clone();
    }
    throw new CloneNotSupportedException();
  }
  
  static class Delegate
    extends MessageDigest
  {
    private MessageDigestSpi digestSpi;
    
    public Delegate(MessageDigestSpi paramMessageDigestSpi, String paramString)
    {
      super();
      digestSpi = paramMessageDigestSpi;
    }
    
    public Object clone()
      throws CloneNotSupportedException
    {
      if ((digestSpi instanceof Cloneable))
      {
        MessageDigestSpi localMessageDigestSpi = (MessageDigestSpi)digestSpi.clone();
        Delegate localDelegate = new Delegate(localMessageDigestSpi, algorithm);
        provider = provider;
        state = state;
        return localDelegate;
      }
      throw new CloneNotSupportedException();
    }
    
    protected int engineGetDigestLength()
    {
      return digestSpi.engineGetDigestLength();
    }
    
    protected void engineUpdate(byte paramByte)
    {
      digestSpi.engineUpdate(paramByte);
    }
    
    protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      digestSpi.engineUpdate(paramArrayOfByte, paramInt1, paramInt2);
    }
    
    protected void engineUpdate(ByteBuffer paramByteBuffer)
    {
      digestSpi.engineUpdate(paramByteBuffer);
    }
    
    protected byte[] engineDigest()
    {
      return digestSpi.engineDigest();
    }
    
    protected int engineDigest(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws DigestException
    {
      return digestSpi.engineDigest(paramArrayOfByte, paramInt1, paramInt2);
    }
    
    protected void engineReset()
    {
      digestSpi.engineReset();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\MessageDigest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */