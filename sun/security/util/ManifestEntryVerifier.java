package sun.security.util;

import java.io.IOException;
import java.security.CodeSigner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarException;
import java.util.jar.Manifest;
import sun.security.jca.Providers;

public class ManifestEntryVerifier
{
  private static final Debug debug = Debug.getInstance("jar");
  HashMap<String, MessageDigest> createdDigests = new HashMap(11);
  ArrayList<MessageDigest> digests = new ArrayList();
  ArrayList<byte[]> manifestHashes = new ArrayList();
  private String name = null;
  private Manifest man;
  private boolean skip = true;
  private JarEntry entry;
  private CodeSigner[] signers = null;
  private static final char[] hexc = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
  
  public ManifestEntryVerifier(Manifest paramManifest)
  {
    man = paramManifest;
  }
  
  public void setEntry(String paramString, JarEntry paramJarEntry)
    throws IOException
  {
    digests.clear();
    manifestHashes.clear();
    name = paramString;
    entry = paramJarEntry;
    skip = true;
    signers = null;
    if ((man == null) || (paramString == null)) {
      return;
    }
    skip = false;
    Attributes localAttributes = man.getAttributes(paramString);
    if (localAttributes == null)
    {
      localAttributes = man.getAttributes("./" + paramString);
      if (localAttributes == null)
      {
        localAttributes = man.getAttributes("/" + paramString);
        if (localAttributes == null) {
          return;
        }
      }
    }
    Iterator localIterator = localAttributes.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str1 = localEntry.getKey().toString();
      if (str1.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST"))
      {
        String str2 = str1.substring(0, str1.length() - 7);
        MessageDigest localMessageDigest = (MessageDigest)createdDigests.get(str2);
        if (localMessageDigest == null) {
          try
          {
            localMessageDigest = MessageDigest.getInstance(str2, SunProviderHolder.instance);
            createdDigests.put(str2, localMessageDigest);
          }
          catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {}
        }
        if (localMessageDigest != null)
        {
          localMessageDigest.reset();
          digests.add(localMessageDigest);
          manifestHashes.add(Base64.getMimeDecoder().decode((String)localEntry.getValue()));
        }
      }
    }
  }
  
  public void update(byte paramByte)
  {
    if (skip) {
      return;
    }
    for (int i = 0; i < digests.size(); i++) {
      ((MessageDigest)digests.get(i)).update(paramByte);
    }
  }
  
  public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (skip) {
      return;
    }
    for (int i = 0; i < digests.size(); i++) {
      ((MessageDigest)digests.get(i)).update(paramArrayOfByte, paramInt1, paramInt2);
    }
  }
  
  public JarEntry getEntry()
  {
    return entry;
  }
  
  public CodeSigner[] verify(Hashtable<String, CodeSigner[]> paramHashtable1, Hashtable<String, CodeSigner[]> paramHashtable2)
    throws JarException
  {
    if (skip) {
      return null;
    }
    if (digests.isEmpty()) {
      throw new SecurityException("digest missing for " + name);
    }
    if (signers != null) {
      return signers;
    }
    for (int i = 0; i < digests.size(); i++)
    {
      MessageDigest localMessageDigest = (MessageDigest)digests.get(i);
      byte[] arrayOfByte1 = (byte[])manifestHashes.get(i);
      byte[] arrayOfByte2 = localMessageDigest.digest();
      if (debug != null)
      {
        debug.println("Manifest Entry: " + name + " digest=" + localMessageDigest.getAlgorithm());
        debug.println("  manifest " + toHex(arrayOfByte1));
        debug.println("  computed " + toHex(arrayOfByte2));
        debug.println();
      }
      if (!MessageDigest.isEqual(arrayOfByte2, arrayOfByte1)) {
        throw new SecurityException(localMessageDigest.getAlgorithm() + " digest error for " + name);
      }
    }
    signers = ((CodeSigner[])paramHashtable2.remove(name));
    if (signers != null) {
      paramHashtable1.put(name, signers);
    }
    return signers;
  }
  
  static String toHex(byte[] paramArrayOfByte)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramArrayOfByte.length * 2);
    for (int i = 0; i < paramArrayOfByte.length; i++)
    {
      localStringBuffer.append(hexc[(paramArrayOfByte[i] >> 4 & 0xF)]);
      localStringBuffer.append(hexc[(paramArrayOfByte[i] & 0xF)]);
    }
    return localStringBuffer.toString();
  }
  
  private static class SunProviderHolder
  {
    private static final Provider instance = ;
    
    private SunProviderHolder() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\ManifestEntryVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */