package sun.security.pkcs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Hashtable;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class PKCS9Attributes
{
  private final Hashtable<ObjectIdentifier, PKCS9Attribute> attributes = new Hashtable(3);
  private final Hashtable<ObjectIdentifier, ObjectIdentifier> permittedAttributes;
  private final byte[] derEncoding;
  private boolean ignoreUnsupportedAttributes = false;
  
  public PKCS9Attributes(ObjectIdentifier[] paramArrayOfObjectIdentifier, DerInputStream paramDerInputStream)
    throws IOException
  {
    if (paramArrayOfObjectIdentifier != null)
    {
      permittedAttributes = new Hashtable(paramArrayOfObjectIdentifier.length);
      for (int i = 0; i < paramArrayOfObjectIdentifier.length; i++) {
        permittedAttributes.put(paramArrayOfObjectIdentifier[i], paramArrayOfObjectIdentifier[i]);
      }
    }
    else
    {
      permittedAttributes = null;
    }
    derEncoding = decode(paramDerInputStream);
  }
  
  public PKCS9Attributes(DerInputStream paramDerInputStream)
    throws IOException
  {
    this(paramDerInputStream, false);
  }
  
  public PKCS9Attributes(DerInputStream paramDerInputStream, boolean paramBoolean)
    throws IOException
  {
    ignoreUnsupportedAttributes = paramBoolean;
    derEncoding = decode(paramDerInputStream);
    permittedAttributes = null;
  }
  
  public PKCS9Attributes(PKCS9Attribute[] paramArrayOfPKCS9Attribute)
    throws IllegalArgumentException, IOException
  {
    for (int i = 0; i < paramArrayOfPKCS9Attribute.length; i++)
    {
      ObjectIdentifier localObjectIdentifier = paramArrayOfPKCS9Attribute[i].getOID();
      if (attributes.containsKey(localObjectIdentifier)) {
        throw new IllegalArgumentException("PKCSAttribute " + paramArrayOfPKCS9Attribute[i].getOID() + " duplicated while constructing PKCS9Attributes.");
      }
      attributes.put(localObjectIdentifier, paramArrayOfPKCS9Attribute[i]);
    }
    derEncoding = generateDerEncoding();
    permittedAttributes = null;
  }
  
  private byte[] decode(DerInputStream paramDerInputStream)
    throws IOException
  {
    DerValue localDerValue = paramDerInputStream.getDerValue();
    byte[] arrayOfByte = localDerValue.toByteArray();
    arrayOfByte[0] = 49;
    DerInputStream localDerInputStream = new DerInputStream(arrayOfByte);
    DerValue[] arrayOfDerValue = localDerInputStream.getSet(3, true);
    int i = 1;
    for (int j = 0; j < arrayOfDerValue.length; j++)
    {
      PKCS9Attribute localPKCS9Attribute;
      try
      {
        localPKCS9Attribute = new PKCS9Attribute(arrayOfDerValue[j]);
      }
      catch (ParsingException localParsingException)
      {
        if (ignoreUnsupportedAttributes)
        {
          i = 0;
          continue;
        }
        throw localParsingException;
      }
      ObjectIdentifier localObjectIdentifier = localPKCS9Attribute.getOID();
      if (attributes.get(localObjectIdentifier) != null) {
        throw new IOException("Duplicate PKCS9 attribute: " + localObjectIdentifier);
      }
      if ((permittedAttributes != null) && (!permittedAttributes.containsKey(localObjectIdentifier))) {
        throw new IOException("Attribute " + localObjectIdentifier + " not permitted in this attribute set");
      }
      attributes.put(localObjectIdentifier, localPKCS9Attribute);
    }
    return i != 0 ? arrayOfByte : generateDerEncoding();
  }
  
  public void encode(byte paramByte, OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream.write(paramByte);
    paramOutputStream.write(derEncoding, 1, derEncoding.length - 1);
  }
  
  private byte[] generateDerEncoding()
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    Object[] arrayOfObject = attributes.values().toArray();
    localDerOutputStream.putOrderedSetOf((byte)49, castToDerEncoder(arrayOfObject));
    return localDerOutputStream.toByteArray();
  }
  
  public byte[] getDerEncoding()
    throws IOException
  {
    return (byte[])derEncoding.clone();
  }
  
  public PKCS9Attribute getAttribute(ObjectIdentifier paramObjectIdentifier)
  {
    return (PKCS9Attribute)attributes.get(paramObjectIdentifier);
  }
  
  public PKCS9Attribute getAttribute(String paramString)
  {
    return (PKCS9Attribute)attributes.get(PKCS9Attribute.getOID(paramString));
  }
  
  public PKCS9Attribute[] getAttributes()
  {
    PKCS9Attribute[] arrayOfPKCS9Attribute = new PKCS9Attribute[attributes.size()];
    int i = 0;
    for (int j = 1; (j < PKCS9Attribute.PKCS9_OIDS.length) && (i < arrayOfPKCS9Attribute.length); j++)
    {
      arrayOfPKCS9Attribute[i] = getAttribute(PKCS9Attribute.PKCS9_OIDS[j]);
      if (arrayOfPKCS9Attribute[i] != null) {
        i++;
      }
    }
    return arrayOfPKCS9Attribute;
  }
  
  public Object getAttributeValue(ObjectIdentifier paramObjectIdentifier)
    throws IOException
  {
    try
    {
      Object localObject = getAttribute(paramObjectIdentifier).getValue();
      return localObject;
    }
    catch (NullPointerException localNullPointerException)
    {
      throw new IOException("No value found for attribute " + paramObjectIdentifier);
    }
  }
  
  public Object getAttributeValue(String paramString)
    throws IOException
  {
    ObjectIdentifier localObjectIdentifier = PKCS9Attribute.getOID(paramString);
    if (localObjectIdentifier == null) {
      throw new IOException("Attribute name " + paramString + " not recognized or not supported.");
    }
    return getAttributeValue(localObjectIdentifier);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer(200);
    localStringBuffer.append("PKCS9 Attributes: [\n\t");
    int i = 1;
    for (int j = 1; j < PKCS9Attribute.PKCS9_OIDS.length; j++)
    {
      PKCS9Attribute localPKCS9Attribute = getAttribute(PKCS9Attribute.PKCS9_OIDS[j]);
      if (localPKCS9Attribute != null)
      {
        if (i != 0) {
          i = 0;
        } else {
          localStringBuffer.append(";\n\t");
        }
        localStringBuffer.append(localPKCS9Attribute.toString());
      }
    }
    localStringBuffer.append("\n\t] (end PKCS9 Attributes)");
    return localStringBuffer.toString();
  }
  
  static DerEncoder[] castToDerEncoder(Object[] paramArrayOfObject)
  {
    DerEncoder[] arrayOfDerEncoder = new DerEncoder[paramArrayOfObject.length];
    for (int i = 0; i < arrayOfDerEncoder.length; i++) {
      arrayOfDerEncoder[i] = ((DerEncoder)paramArrayOfObject[i]);
    }
    return arrayOfDerEncoder;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\pkcs\PKCS9Attributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */