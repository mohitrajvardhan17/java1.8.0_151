package sun.tools.jar;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.Vector;
import sun.net.www.MessageHeader;

public class Manifest
{
  private Vector<MessageHeader> entries = new Vector();
  private byte[] tmpbuf = new byte['Ȁ'];
  private Hashtable<String, MessageHeader> tableEntries = new Hashtable();
  static final String[] hashes = { "SHA" };
  static final byte[] EOL = { 13, 10 };
  static final boolean debug = false;
  static final String VERSION = "1.0";
  
  static final void debug(String paramString) {}
  
  public Manifest() {}
  
  public Manifest(byte[] paramArrayOfByte)
    throws IOException
  {
    this(new ByteArrayInputStream(paramArrayOfByte), false);
  }
  
  public Manifest(InputStream paramInputStream)
    throws IOException
  {
    this(paramInputStream, true);
  }
  
  public Manifest(InputStream paramInputStream, boolean paramBoolean)
    throws IOException
  {
    if (!paramInputStream.markSupported()) {
      paramInputStream = new BufferedInputStream(paramInputStream);
    }
    for (;;)
    {
      paramInputStream.mark(1);
      if (paramInputStream.read() == -1) {
        break;
      }
      paramInputStream.reset();
      MessageHeader localMessageHeader = new MessageHeader(paramInputStream);
      if (paramBoolean) {
        doHashes(localMessageHeader);
      }
      addEntry(localMessageHeader);
    }
  }
  
  public Manifest(String[] paramArrayOfString)
    throws IOException
  {
    MessageHeader localMessageHeader = new MessageHeader();
    localMessageHeader.add("Manifest-Version", "1.0");
    String str = System.getProperty("java.version");
    localMessageHeader.add("Created-By", "Manifest JDK " + str);
    addEntry(localMessageHeader);
    addFiles(null, paramArrayOfString);
  }
  
  public void addEntry(MessageHeader paramMessageHeader)
  {
    entries.addElement(paramMessageHeader);
    String str = paramMessageHeader.findValue("Name");
    debug("addEntry for name: " + str);
    if (str != null) {
      tableEntries.put(str, paramMessageHeader);
    }
  }
  
  public MessageHeader getEntry(String paramString)
  {
    return (MessageHeader)tableEntries.get(paramString);
  }
  
  public MessageHeader entryAt(int paramInt)
  {
    return (MessageHeader)entries.elementAt(paramInt);
  }
  
  public Enumeration<MessageHeader> entries()
  {
    return entries.elements();
  }
  
  public void addFiles(File paramFile, String[] paramArrayOfString)
    throws IOException
  {
    if (paramArrayOfString == null) {
      return;
    }
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      File localFile;
      if (paramFile == null) {
        localFile = new File(paramArrayOfString[i]);
      } else {
        localFile = new File(paramFile, paramArrayOfString[i]);
      }
      if (localFile.isDirectory()) {
        addFiles(localFile, localFile.list());
      } else {
        addFile(localFile);
      }
    }
  }
  
  private final String stdToLocal(String paramString)
  {
    return paramString.replace('/', File.separatorChar);
  }
  
  private final String localToStd(String paramString)
  {
    paramString = paramString.replace(File.separatorChar, '/');
    if (paramString.startsWith("./")) {
      paramString = paramString.substring(2);
    } else if (paramString.startsWith("/")) {
      paramString = paramString.substring(1);
    }
    return paramString;
  }
  
  public void addFile(File paramFile)
    throws IOException
  {
    String str = localToStd(paramFile.getPath());
    if (tableEntries.get(str) == null)
    {
      MessageHeader localMessageHeader = new MessageHeader();
      localMessageHeader.add("Name", str);
      addEntry(localMessageHeader);
    }
  }
  
  public void doHashes(MessageHeader paramMessageHeader)
    throws IOException
  {
    String str = paramMessageHeader.findValue("Name");
    if ((str == null) || (str.endsWith("/"))) {
      return;
    }
    for (int i = 0; i < hashes.length; i++)
    {
      FileInputStream localFileInputStream = new FileInputStream(stdToLocal(str));
      try
      {
        MessageDigest localMessageDigest = MessageDigest.getInstance(hashes[i]);
        int j;
        while ((j = localFileInputStream.read(tmpbuf, 0, tmpbuf.length)) != -1) {
          localMessageDigest.update(tmpbuf, 0, j);
        }
        paramMessageHeader.set(hashes[i] + "-Digest", Base64.getMimeEncoder().encodeToString(localMessageDigest.digest()));
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        throw new JarException("Digest algorithm " + hashes[i] + " not available.");
      }
      finally
      {
        localFileInputStream.close();
      }
    }
  }
  
  public void stream(OutputStream paramOutputStream)
    throws IOException
  {
    PrintStream localPrintStream;
    if ((paramOutputStream instanceof PrintStream)) {
      localPrintStream = (PrintStream)paramOutputStream;
    } else {
      localPrintStream = new PrintStream(paramOutputStream);
    }
    MessageHeader localMessageHeader1 = (MessageHeader)entries.elementAt(0);
    if (localMessageHeader1.findValue("Manifest-Version") == null)
    {
      String str = System.getProperty("java.version");
      if (localMessageHeader1.findValue("Name") == null)
      {
        localMessageHeader1.prepend("Manifest-Version", "1.0");
        localMessageHeader1.add("Created-By", "Manifest JDK " + str);
      }
      else
      {
        localPrintStream.print("Manifest-Version: 1.0\r\nCreated-By: " + str + "\r\n\r\n");
      }
      localPrintStream.flush();
    }
    localMessageHeader1.print(localPrintStream);
    for (int i = 1; i < entries.size(); i++)
    {
      MessageHeader localMessageHeader2 = (MessageHeader)entries.elementAt(i);
      localMessageHeader2.print(localPrintStream);
    }
  }
  
  public static boolean isManifestName(String paramString)
  {
    if (paramString.charAt(0) == '/') {
      paramString = paramString.substring(1, paramString.length());
    }
    paramString = paramString.toUpperCase();
    return paramString.equals("META-INF/MANIFEST.MF");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tools\jar\Manifest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */