package com.sun.org.apache.xml.internal.security.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecurityPermission;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaUtils
{
  private static Logger log = Logger.getLogger(JavaUtils.class.getName());
  private static final SecurityPermission REGISTER_PERMISSION = new SecurityPermission("com.sun.org.apache.xml.internal.security.register");
  
  private JavaUtils() {}
  
  public static byte[] getBytesFromFile(String paramString)
    throws FileNotFoundException, IOException
  {
    byte[] arrayOfByte1 = null;
    FileInputStream localFileInputStream = null;
    UnsyncByteArrayOutputStream localUnsyncByteArrayOutputStream = null;
    try
    {
      localFileInputStream = new FileInputStream(paramString);
      localUnsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream();
      byte[] arrayOfByte2 = new byte['Ѐ'];
      int i;
      while ((i = localFileInputStream.read(arrayOfByte2)) > 0) {
        localUnsyncByteArrayOutputStream.write(arrayOfByte2, 0, i);
      }
      arrayOfByte1 = localUnsyncByteArrayOutputStream.toByteArray();
    }
    finally
    {
      if (localUnsyncByteArrayOutputStream != null) {
        localUnsyncByteArrayOutputStream.close();
      }
      if (localFileInputStream != null) {
        localFileInputStream.close();
      }
    }
    return arrayOfByte1;
  }
  
  public static void writeBytesToFilename(String paramString, byte[] paramArrayOfByte)
  {
    FileOutputStream localFileOutputStream = null;
    try
    {
      if ((paramString != null) && (paramArrayOfByte != null))
      {
        File localFile = new File(paramString);
        localFileOutputStream = new FileOutputStream(localFile);
        localFileOutputStream.write(paramArrayOfByte);
        localFileOutputStream.close();
      }
      else if (log.isLoggable(Level.FINE))
      {
        log.log(Level.FINE, "writeBytesToFilename got null byte[] pointed");
      }
    }
    catch (IOException localIOException1)
    {
      if (localFileOutputStream != null) {
        try
        {
          localFileOutputStream.close();
        }
        catch (IOException localIOException2)
        {
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, localIOException2.getMessage(), localIOException2);
          }
        }
      }
    }
  }
  
  public static byte[] getBytesFromStream(InputStream paramInputStream)
    throws IOException
  {
    UnsyncByteArrayOutputStream localUnsyncByteArrayOutputStream = null;
    byte[] arrayOfByte1 = null;
    try
    {
      localUnsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream();
      byte[] arrayOfByte2 = new byte['က'];
      int i;
      while ((i = paramInputStream.read(arrayOfByte2)) > 0) {
        localUnsyncByteArrayOutputStream.write(arrayOfByte2, 0, i);
      }
      arrayOfByte1 = localUnsyncByteArrayOutputStream.toByteArray();
    }
    finally
    {
      localUnsyncByteArrayOutputStream.close();
    }
    return arrayOfByte1;
  }
  
  public static byte[] convertDsaASN1toXMLDSIG(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    if ((paramArrayOfByte[0] != 48) || (paramArrayOfByte[1] != paramArrayOfByte.length - 2) || (paramArrayOfByte[2] != 2)) {
      throw new IOException("Invalid ASN.1 format of DSA signature");
    }
    int i = paramArrayOfByte[3];
    for (int j = i; (j > 0) && (paramArrayOfByte[(4 + i - j)] == 0); j--) {}
    int k = paramArrayOfByte[(5 + i)];
    for (int m = k; (m > 0) && (paramArrayOfByte[(6 + i + k - m)] == 0); m--) {}
    if ((j > paramInt) || (paramArrayOfByte[(4 + i)] != 2) || (m > paramInt)) {
      throw new IOException("Invalid ASN.1 format of DSA signature");
    }
    byte[] arrayOfByte = new byte[paramInt * 2];
    System.arraycopy(paramArrayOfByte, 4 + i - j, arrayOfByte, paramInt - j, j);
    System.arraycopy(paramArrayOfByte, 6 + i + k - m, arrayOfByte, paramInt * 2 - m, m);
    return arrayOfByte;
  }
  
  public static byte[] convertDsaXMLDSIGtoASN1(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    int i = paramInt * 2;
    if (paramArrayOfByte.length != i) {
      throw new IOException("Invalid XMLDSIG format of DSA signature");
    }
    for (int j = paramInt; (j > 0) && (paramArrayOfByte[(paramInt - j)] == 0); j--) {}
    int k = j;
    if (paramArrayOfByte[(paramInt - j)] < 0) {
      k++;
    }
    for (int m = paramInt; (m > 0) && (paramArrayOfByte[(i - m)] == 0); m--) {}
    int n = m;
    if (paramArrayOfByte[(i - m)] < 0) {
      n++;
    }
    byte[] arrayOfByte = new byte[6 + k + n];
    arrayOfByte[0] = 48;
    arrayOfByte[1] = ((byte)(4 + k + n));
    arrayOfByte[2] = 2;
    arrayOfByte[3] = ((byte)k);
    System.arraycopy(paramArrayOfByte, paramInt - j, arrayOfByte, 4 + k - j, j);
    arrayOfByte[(4 + k)] = 2;
    arrayOfByte[(5 + k)] = ((byte)n);
    System.arraycopy(paramArrayOfByte, i - m, arrayOfByte, 6 + k + n - m, m);
    return arrayOfByte;
  }
  
  public static void checkRegisterPermission()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(REGISTER_PERMISSION);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\JavaUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */