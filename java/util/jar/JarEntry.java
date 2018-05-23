package java.util.jar;

import java.io.IOException;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.zip.ZipEntry;

public class JarEntry
  extends ZipEntry
{
  Attributes attr;
  Certificate[] certs;
  CodeSigner[] signers;
  
  public JarEntry(String paramString)
  {
    super(paramString);
  }
  
  public JarEntry(ZipEntry paramZipEntry)
  {
    super(paramZipEntry);
  }
  
  public JarEntry(JarEntry paramJarEntry)
  {
    this(paramJarEntry);
    attr = attr;
    certs = certs;
    signers = signers;
  }
  
  public Attributes getAttributes()
    throws IOException
  {
    return attr;
  }
  
  public Certificate[] getCertificates()
  {
    return certs == null ? null : (Certificate[])certs.clone();
  }
  
  public CodeSigner[] getCodeSigners()
  {
    return signers == null ? null : (CodeSigner[])signers.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\jar\JarEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */