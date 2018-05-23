package java.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.SocketPermission;
import java.net.URL;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import sun.misc.IOUtils;

public class CodeSource
  implements Serializable
{
  private static final long serialVersionUID = 4977541819976013951L;
  private URL location;
  private transient CodeSigner[] signers = null;
  private transient Certificate[] certs = null;
  private transient SocketPermission sp;
  private transient CertificateFactory factory = null;
  
  public CodeSource(URL paramURL, Certificate[] paramArrayOfCertificate)
  {
    location = paramURL;
    if (paramArrayOfCertificate != null) {
      certs = ((Certificate[])paramArrayOfCertificate.clone());
    }
  }
  
  public CodeSource(URL paramURL, CodeSigner[] paramArrayOfCodeSigner)
  {
    location = paramURL;
    if (paramArrayOfCodeSigner != null) {
      signers = ((CodeSigner[])paramArrayOfCodeSigner.clone());
    }
  }
  
  public int hashCode()
  {
    if (location != null) {
      return location.hashCode();
    }
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof CodeSource)) {
      return false;
    }
    CodeSource localCodeSource = (CodeSource)paramObject;
    if (location == null)
    {
      if (location != null) {
        return false;
      }
    }
    else if (!location.equals(location)) {
      return false;
    }
    return matchCerts(localCodeSource, true);
  }
  
  public final URL getLocation()
  {
    return location;
  }
  
  public final Certificate[] getCertificates()
  {
    if (certs != null) {
      return (Certificate[])certs.clone();
    }
    if (signers != null)
    {
      ArrayList localArrayList = new ArrayList();
      for (int i = 0; i < signers.length; i++) {
        localArrayList.addAll(signers[i].getSignerCertPath().getCertificates());
      }
      certs = ((Certificate[])localArrayList.toArray(new Certificate[localArrayList.size()]));
      return (Certificate[])certs.clone();
    }
    return null;
  }
  
  public final CodeSigner[] getCodeSigners()
  {
    if (signers != null) {
      return (CodeSigner[])signers.clone();
    }
    if (certs != null)
    {
      signers = convertCertArrayToSignerArray(certs);
      return (CodeSigner[])signers.clone();
    }
    return null;
  }
  
  public boolean implies(CodeSource paramCodeSource)
  {
    if (paramCodeSource == null) {
      return false;
    }
    return (matchCerts(paramCodeSource, false)) && (matchLocation(paramCodeSource));
  }
  
  private boolean matchCerts(CodeSource paramCodeSource, boolean paramBoolean)
  {
    if ((certs == null) && (signers == null))
    {
      if (paramBoolean) {
        return (certs == null) && (signers == null);
      }
      return true;
    }
    int j;
    int i;
    int k;
    if ((signers != null) && (signers != null))
    {
      if ((paramBoolean) && (signers.length != signers.length)) {
        return false;
      }
      for (j = 0; j < signers.length; j++)
      {
        i = 0;
        for (k = 0; k < signers.length; k++) {
          if (signers[j].equals(signers[k]))
          {
            i = 1;
            break;
          }
        }
        if (i == 0) {
          return false;
        }
      }
      return true;
    }
    if ((certs != null) && (certs != null))
    {
      if ((paramBoolean) && (certs.length != certs.length)) {
        return false;
      }
      for (j = 0; j < certs.length; j++)
      {
        i = 0;
        for (k = 0; k < certs.length; k++) {
          if (certs[j].equals(certs[k]))
          {
            i = 1;
            break;
          }
        }
        if (i == 0) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  private boolean matchLocation(CodeSource paramCodeSource)
  {
    if (location == null) {
      return true;
    }
    if ((paramCodeSource == null) || (location == null)) {
      return false;
    }
    if (location.equals(location)) {
      return true;
    }
    if (!location.getProtocol().equalsIgnoreCase(location.getProtocol())) {
      return false;
    }
    int i = location.getPort();
    if (i != -1)
    {
      int j = location.getPort();
      int m = j != -1 ? j : location.getDefaultPort();
      if (i != m) {
        return false;
      }
    }
    if (location.getFile().endsWith("/-"))
    {
      String str1 = location.getFile().substring(0, location.getFile().length() - 1);
      if (!location.getFile().startsWith(str1)) {
        return false;
      }
    }
    else if (location.getFile().endsWith("/*"))
    {
      int k = location.getFile().lastIndexOf('/');
      if (k == -1) {
        return false;
      }
      str3 = location.getFile().substring(0, location.getFile().length() - 1);
      String str4 = location.getFile().substring(0, k + 1);
      if (!str4.equals(str3)) {
        return false;
      }
    }
    else if ((!location.getFile().equals(location.getFile())) && (!location.getFile().equals(location.getFile() + "/")))
    {
      return false;
    }
    if ((location.getRef() != null) && (!location.getRef().equals(location.getRef()))) {
      return false;
    }
    String str2 = location.getHost();
    String str3 = location.getHost();
    if ((str2 != null) && (((!"".equals(str2)) && (!"localhost".equals(str2))) || ((!"".equals(str3)) && (!"localhost".equals(str3)) && (!str2.equals(str3)))))
    {
      if (str3 == null) {
        return false;
      }
      if (sp == null) {
        sp = new SocketPermission(str2, "resolve");
      }
      if (sp == null) {
        sp = new SocketPermission(str3, "resolve");
      }
      if (!sp.implies(sp)) {
        return false;
      }
    }
    return true;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("(");
    localStringBuilder.append(location);
    int i;
    if ((certs != null) && (certs.length > 0)) {
      for (i = 0; i < certs.length; i++) {
        localStringBuilder.append(" " + certs[i]);
      }
    } else if ((signers != null) && (signers.length > 0)) {
      for (i = 0; i < signers.length; i++) {
        localStringBuilder.append(" " + signers[i]);
      }
    } else {
      localStringBuilder.append(" <no signer certificates>");
    }
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if ((certs == null) || (certs.length == 0))
    {
      paramObjectOutputStream.writeInt(0);
    }
    else
    {
      paramObjectOutputStream.writeInt(certs.length);
      for (int i = 0; i < certs.length; i++)
      {
        Certificate localCertificate = certs[i];
        try
        {
          paramObjectOutputStream.writeUTF(localCertificate.getType());
          byte[] arrayOfByte = localCertificate.getEncoded();
          paramObjectOutputStream.writeInt(arrayOfByte.length);
          paramObjectOutputStream.write(arrayOfByte);
        }
        catch (CertificateEncodingException localCertificateEncodingException)
        {
          throw new IOException(localCertificateEncodingException.getMessage());
        }
      }
    }
    if ((signers != null) && (signers.length > 0)) {
      paramObjectOutputStream.writeObject(signers);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    Hashtable localHashtable = null;
    ArrayList localArrayList = null;
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    if (i > 0)
    {
      localHashtable = new Hashtable(3);
      localArrayList = new ArrayList(i > 20 ? 20 : i);
    }
    else if (i < 0)
    {
      throw new IOException("size cannot be negative");
    }
    for (int j = 0; j < i; j++)
    {
      String str = paramObjectInputStream.readUTF();
      CertificateFactory localCertificateFactory;
      if (localHashtable.containsKey(str))
      {
        localCertificateFactory = (CertificateFactory)localHashtable.get(str);
      }
      else
      {
        try
        {
          localCertificateFactory = CertificateFactory.getInstance(str);
        }
        catch (CertificateException localCertificateException1)
        {
          throw new ClassNotFoundException("Certificate factory for " + str + " not found");
        }
        localHashtable.put(str, localCertificateFactory);
      }
      byte[] arrayOfByte = IOUtils.readNBytes(paramObjectInputStream, paramObjectInputStream.readInt());
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
      try
      {
        localArrayList.add(localCertificateFactory.generateCertificate(localByteArrayInputStream));
      }
      catch (CertificateException localCertificateException2)
      {
        throw new IOException(localCertificateException2.getMessage());
      }
      localByteArrayInputStream.close();
    }
    if (localArrayList != null) {
      certs = ((Certificate[])localArrayList.toArray(new Certificate[i]));
    }
    try
    {
      signers = ((CodeSigner[])((CodeSigner[])paramObjectInputStream.readObject()).clone());
    }
    catch (IOException localIOException) {}
  }
  
  private CodeSigner[] convertCertArrayToSignerArray(Certificate[] paramArrayOfCertificate)
  {
    if (paramArrayOfCertificate == null) {
      return null;
    }
    try
    {
      if (factory == null) {
        factory = CertificateFactory.getInstance("X.509");
      }
      int i = 0;
      ArrayList localArrayList1 = new ArrayList();
      while (i < paramArrayOfCertificate.length)
      {
        ArrayList localArrayList2 = new ArrayList();
        localArrayList2.add(paramArrayOfCertificate[(i++)]);
        for (int j = i; (j < paramArrayOfCertificate.length) && ((paramArrayOfCertificate[j] instanceof X509Certificate)) && (((X509Certificate)paramArrayOfCertificate[j]).getBasicConstraints() != -1); j++) {
          localArrayList2.add(paramArrayOfCertificate[j]);
        }
        i = j;
        CertPath localCertPath = factory.generateCertPath(localArrayList2);
        localArrayList1.add(new CodeSigner(localCertPath, null));
      }
      if (localArrayList1.isEmpty()) {
        return null;
      }
      return (CodeSigner[])localArrayList1.toArray(new CodeSigner[localArrayList1.size()]);
    }
    catch (CertificateException localCertificateException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\CodeSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */