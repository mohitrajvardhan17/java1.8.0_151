package java.security.cert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.misc.IOUtils;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.InvalidityDateExtension;

public class CertificateRevokedException
  extends CertificateException
{
  private static final long serialVersionUID = 7839996631571608627L;
  private Date revocationDate;
  private final CRLReason reason;
  private final X500Principal authority;
  private transient Map<String, Extension> extensions;
  
  public CertificateRevokedException(Date paramDate, CRLReason paramCRLReason, X500Principal paramX500Principal, Map<String, Extension> paramMap)
  {
    if ((paramDate == null) || (paramCRLReason == null) || (paramX500Principal == null) || (paramMap == null)) {
      throw new NullPointerException();
    }
    revocationDate = new Date(paramDate.getTime());
    reason = paramCRLReason;
    authority = paramX500Principal;
    extensions = Collections.checkedMap(new HashMap(), String.class, Extension.class);
    extensions.putAll(paramMap);
  }
  
  public Date getRevocationDate()
  {
    return (Date)revocationDate.clone();
  }
  
  public CRLReason getRevocationReason()
  {
    return reason;
  }
  
  public X500Principal getAuthorityName()
  {
    return authority;
  }
  
  public Date getInvalidityDate()
  {
    Extension localExtension = (Extension)getExtensions().get("2.5.29.24");
    if (localExtension == null) {
      return null;
    }
    try
    {
      Date localDate = InvalidityDateExtension.toImpl(localExtension).get("DATE");
      return new Date(localDate.getTime());
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  public Map<String, Extension> getExtensions()
  {
    return Collections.unmodifiableMap(extensions);
  }
  
  public String getMessage()
  {
    return "Certificate has been revoked, reason: " + reason + ", revocation date: " + revocationDate + ", authority: " + authority + ", extension OIDs: " + extensions.keySet();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(extensions.size());
    Iterator localIterator = extensions.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Extension localExtension = (Extension)localEntry.getValue();
      paramObjectOutputStream.writeObject(localExtension.getId());
      paramObjectOutputStream.writeBoolean(localExtension.isCritical());
      byte[] arrayOfByte = localExtension.getValue();
      paramObjectOutputStream.writeInt(arrayOfByte.length);
      paramObjectOutputStream.write(arrayOfByte);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    revocationDate = new Date(revocationDate.getTime());
    int i = paramObjectInputStream.readInt();
    if (i == 0)
    {
      extensions = Collections.emptyMap();
    }
    else
    {
      if (i < 0) {
        throw new IOException("size cannot be negative");
      }
      extensions = new HashMap(i > 20 ? 20 : i);
    }
    for (int j = 0; j < i; j++)
    {
      String str = (String)paramObjectInputStream.readObject();
      boolean bool = paramObjectInputStream.readBoolean();
      byte[] arrayOfByte = IOUtils.readNBytes(paramObjectInputStream, paramObjectInputStream.readInt());
      sun.security.x509.Extension localExtension = sun.security.x509.Extension.newExtension(new ObjectIdentifier(str), bool, arrayOfByte);
      extensions.put(str, localExtension);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CertificateRevokedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */