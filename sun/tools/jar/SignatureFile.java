package sun.tools.jar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import sun.net.www.MessageHeader;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.SignerInfo;
import sun.security.x509.AlgorithmId;

public class SignatureFile
{
  static final boolean debug = false;
  private Vector<MessageHeader> entries = new Vector();
  static final String[] hashes = { "SHA" };
  private Manifest manifest;
  private String rawName;
  private PKCS7 signatureBlock;
  private Hashtable<String, MessageDigest> digests = new Hashtable();
  
  static final void debug(String paramString) {}
  
  private SignatureFile(String paramString)
    throws JarException
  {
    if (paramString != null)
    {
      if ((paramString.length() > 8) || (paramString.indexOf('.') != -1)) {
        throw new JarException("invalid file name");
      }
      rawName = paramString.toUpperCase(Locale.ENGLISH);
    }
  }
  
  private SignatureFile(String paramString, boolean paramBoolean)
    throws JarException
  {
    this(paramString);
    if (paramBoolean)
    {
      MessageHeader localMessageHeader = new MessageHeader();
      localMessageHeader.set("Signature-Version", "1.0");
      entries.addElement(localMessageHeader);
    }
  }
  
  public SignatureFile(Manifest paramManifest, String paramString)
    throws JarException
  {
    this(paramString, true);
    manifest = paramManifest;
    Enumeration localEnumeration = paramManifest.entries();
    while (localEnumeration.hasMoreElements())
    {
      MessageHeader localMessageHeader = (MessageHeader)localEnumeration.nextElement();
      String str = localMessageHeader.findValue("Name");
      if (str != null) {
        add(str);
      }
    }
  }
  
  public SignatureFile(Manifest paramManifest, String[] paramArrayOfString, String paramString)
    throws JarException
  {
    this(paramString, true);
    manifest = paramManifest;
    add(paramArrayOfString);
  }
  
  public SignatureFile(InputStream paramInputStream, String paramString)
    throws IOException
  {
    this(paramString);
    while (paramInputStream.available() > 0)
    {
      MessageHeader localMessageHeader = new MessageHeader(paramInputStream);
      entries.addElement(localMessageHeader);
    }
  }
  
  public SignatureFile(InputStream paramInputStream)
    throws IOException
  {
    this(paramInputStream, null);
  }
  
  public SignatureFile(byte[] paramArrayOfByte)
    throws IOException
  {
    this(new ByteArrayInputStream(paramArrayOfByte));
  }
  
  public String getName()
  {
    return "META-INF/" + rawName + ".SF";
  }
  
  public String getBlockName()
  {
    Object localObject = "DSA";
    if (signatureBlock != null)
    {
      SignerInfo localSignerInfo = signatureBlock.getSignerInfos()[0];
      localObject = localSignerInfo.getDigestEncryptionAlgorithmId().getName();
      String str = AlgorithmId.getEncAlgFromSigAlg((String)localObject);
      if (str != null) {
        localObject = str;
      }
    }
    return "META-INF/" + rawName + "." + (String)localObject;
  }
  
  public PKCS7 getBlock()
  {
    return signatureBlock;
  }
  
  public void setBlock(PKCS7 paramPKCS7)
  {
    signatureBlock = paramPKCS7;
  }
  
  public void add(String[] paramArrayOfString)
    throws JarException
  {
    for (int i = 0; i < paramArrayOfString.length; i++) {
      add(paramArrayOfString[i]);
    }
  }
  
  public void add(String paramString)
    throws JarException
  {
    MessageHeader localMessageHeader1 = manifest.getEntry(paramString);
    if (localMessageHeader1 == null) {
      throw new JarException("entry " + paramString + " not in manifest");
    }
    MessageHeader localMessageHeader2;
    try
    {
      localMessageHeader2 = computeEntry(localMessageHeader1);
    }
    catch (IOException localIOException)
    {
      throw new JarException(localIOException.getMessage());
    }
    entries.addElement(localMessageHeader2);
  }
  
  public MessageHeader getEntry(String paramString)
  {
    Enumeration localEnumeration = entries();
    while (localEnumeration.hasMoreElements())
    {
      MessageHeader localMessageHeader = (MessageHeader)localEnumeration.nextElement();
      if (paramString.equals(localMessageHeader.findValue("Name"))) {
        return localMessageHeader;
      }
    }
    return null;
  }
  
  public MessageHeader entryAt(int paramInt)
  {
    return (MessageHeader)entries.elementAt(paramInt);
  }
  
  public Enumeration<MessageHeader> entries()
  {
    return entries.elements();
  }
  
  private MessageHeader computeEntry(MessageHeader paramMessageHeader)
    throws IOException
  {
    MessageHeader localMessageHeader = new MessageHeader();
    String str = paramMessageHeader.findValue("Name");
    if (str == null) {
      return null;
    }
    localMessageHeader.set("Name", str);
    try
    {
      for (int i = 0; i < hashes.length; i++)
      {
        MessageDigest localMessageDigest = getDigest(hashes[i]);
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream localPrintStream = new PrintStream(localByteArrayOutputStream);
        paramMessageHeader.print(localPrintStream);
        byte[] arrayOfByte1 = localByteArrayOutputStream.toByteArray();
        byte[] arrayOfByte2 = localMessageDigest.digest(arrayOfByte1);
        localMessageHeader.set(hashes[i] + "-Digest", Base64.getMimeEncoder().encodeToString(arrayOfByte2));
      }
      return localMessageHeader;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new JarException(localNoSuchAlgorithmException.getMessage());
    }
  }
  
  private MessageDigest getDigest(String paramString)
    throws NoSuchAlgorithmException
  {
    MessageDigest localMessageDigest = (MessageDigest)digests.get(paramString);
    if (localMessageDigest == null)
    {
      localMessageDigest = MessageDigest.getInstance(paramString);
      digests.put(paramString, localMessageDigest);
    }
    localMessageDigest.reset();
    return localMessageDigest;
  }
  
  public void stream(OutputStream paramOutputStream)
    throws IOException
  {
    MessageHeader localMessageHeader1 = (MessageHeader)entries.elementAt(0);
    if (localMessageHeader1.findValue("Signature-Version") == null) {
      throw new JarException("Signature file requires Signature-Version: 1.0 in 1st header");
    }
    PrintStream localPrintStream = new PrintStream(paramOutputStream);
    localMessageHeader1.print(localPrintStream);
    for (int i = 1; i < entries.size(); i++)
    {
      MessageHeader localMessageHeader2 = (MessageHeader)entries.elementAt(i);
      localMessageHeader2.print(localPrintStream);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tools\jar\SignatureFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */