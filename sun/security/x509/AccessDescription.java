package sun.security.x509;

import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public final class AccessDescription
{
  private int myhash = -1;
  private ObjectIdentifier accessMethod;
  private GeneralName accessLocation;
  public static final ObjectIdentifier Ad_OCSP_Id = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 1 });
  public static final ObjectIdentifier Ad_CAISSUERS_Id = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 2 });
  public static final ObjectIdentifier Ad_TIMESTAMPING_Id = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 3 });
  public static final ObjectIdentifier Ad_CAREPOSITORY_Id = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 5 });
  
  public AccessDescription(ObjectIdentifier paramObjectIdentifier, GeneralName paramGeneralName)
  {
    accessMethod = paramObjectIdentifier;
    accessLocation = paramGeneralName;
  }
  
  public AccessDescription(DerValue paramDerValue)
    throws IOException
  {
    DerInputStream localDerInputStream = paramDerValue.getData();
    accessMethod = localDerInputStream.getOID();
    accessLocation = new GeneralName(localDerInputStream.getDerValue());
  }
  
  public ObjectIdentifier getAccessMethod()
  {
    return accessMethod;
  }
  
  public GeneralName getAccessLocation()
  {
    return accessLocation;
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putOID(accessMethod);
    accessLocation.encode(localDerOutputStream);
    paramDerOutputStream.write((byte)48, localDerOutputStream);
  }
  
  public int hashCode()
  {
    if (myhash == -1) {
      myhash = (accessMethod.hashCode() + accessLocation.hashCode());
    }
    return myhash;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof AccessDescription))) {
      return false;
    }
    AccessDescription localAccessDescription = (AccessDescription)paramObject;
    if (this == localAccessDescription) {
      return true;
    }
    return (accessMethod.equals(localAccessDescription.getAccessMethod())) && (accessLocation.equals(localAccessDescription.getAccessLocation()));
  }
  
  public String toString()
  {
    String str = null;
    if (accessMethod.equals(Ad_CAISSUERS_Id)) {
      str = "caIssuers";
    } else if (accessMethod.equals(Ad_CAREPOSITORY_Id)) {
      str = "caRepository";
    } else if (accessMethod.equals(Ad_TIMESTAMPING_Id)) {
      str = "timeStamping";
    } else if (accessMethod.equals(Ad_OCSP_Id)) {
      str = "ocsp";
    } else {
      str = accessMethod.toString();
    }
    return "\n   accessMethod: " + str + "\n   accessLocation: " + accessLocation.toString() + "\n";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\AccessDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */