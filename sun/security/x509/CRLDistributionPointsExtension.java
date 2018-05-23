package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class CRLDistributionPointsExtension
  extends Extension
  implements CertAttrSet<String>
{
  public static final String IDENT = "x509.info.extensions.CRLDistributionPoints";
  public static final String NAME = "CRLDistributionPoints";
  public static final String POINTS = "points";
  private List<DistributionPoint> distributionPoints;
  private String extensionName;
  
  public CRLDistributionPointsExtension(List<DistributionPoint> paramList)
    throws IOException
  {
    this(false, paramList);
  }
  
  public CRLDistributionPointsExtension(boolean paramBoolean, List<DistributionPoint> paramList)
    throws IOException
  {
    this(PKIXExtensions.CRLDistributionPoints_Id, paramBoolean, paramList, "CRLDistributionPoints");
  }
  
  protected CRLDistributionPointsExtension(ObjectIdentifier paramObjectIdentifier, boolean paramBoolean, List<DistributionPoint> paramList, String paramString)
    throws IOException
  {
    extensionId = paramObjectIdentifier;
    critical = paramBoolean;
    distributionPoints = paramList;
    encodeThis();
    extensionName = paramString;
  }
  
  public CRLDistributionPointsExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    this(PKIXExtensions.CRLDistributionPoints_Id, paramBoolean, paramObject, "CRLDistributionPoints");
  }
  
  protected CRLDistributionPointsExtension(ObjectIdentifier paramObjectIdentifier, Boolean paramBoolean, Object paramObject, String paramString)
    throws IOException
  {
    extensionId = paramObjectIdentifier;
    critical = paramBoolean.booleanValue();
    if (!(paramObject instanceof byte[])) {
      throw new IOException("Illegal argument type");
    }
    extensionValue = ((byte[])paramObject);
    DerValue localDerValue1 = new DerValue(extensionValue);
    if (tag != 48) {
      throw new IOException("Invalid encoding for " + paramString + " extension.");
    }
    distributionPoints = new ArrayList();
    while (data.available() != 0)
    {
      DerValue localDerValue2 = data.getDerValue();
      DistributionPoint localDistributionPoint = new DistributionPoint(localDerValue2);
      distributionPoints.add(localDistributionPoint);
    }
    extensionName = paramString;
  }
  
  public String getName()
  {
    return extensionName;
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    encode(paramOutputStream, PKIXExtensions.CRLDistributionPoints_Id, false);
  }
  
  protected void encode(OutputStream paramOutputStream, ObjectIdentifier paramObjectIdentifier, boolean paramBoolean)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = paramObjectIdentifier;
      critical = paramBoolean;
      encodeThis();
    }
    super.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("points"))
    {
      if (!(paramObject instanceof List)) {
        throw new IOException("Attribute value should be of type List.");
      }
      distributionPoints = ((List)paramObject);
    }
    else
    {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:" + extensionName + ".");
    }
    encodeThis();
  }
  
  public List<DistributionPoint> get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("points")) {
      return distributionPoints;
    }
    throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:" + extensionName + ".");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("points")) {
      distributionPoints = Collections.emptyList();
    } else {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:" + extensionName + '.');
    }
    encodeThis();
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("points");
    return localAttributeNameEnumeration.elements();
  }
  
  private void encodeThis()
    throws IOException
  {
    if (distributionPoints.isEmpty())
    {
      extensionValue = null;
    }
    else
    {
      DerOutputStream localDerOutputStream = new DerOutputStream();
      Object localObject = distributionPoints.iterator();
      while (((Iterator)localObject).hasNext())
      {
        DistributionPoint localDistributionPoint = (DistributionPoint)((Iterator)localObject).next();
        localDistributionPoint.encode(localDerOutputStream);
      }
      localObject = new DerOutputStream();
      ((DerOutputStream)localObject).write((byte)48, localDerOutputStream);
      extensionValue = ((DerOutputStream)localObject).toByteArray();
    }
  }
  
  public String toString()
  {
    return super.toString() + extensionName + " [\n  " + distributionPoints + "]\n";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\CRLDistributionPointsExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */