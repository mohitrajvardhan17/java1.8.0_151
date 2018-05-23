package sun.security.provider.certpath;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.Extension;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import sun.misc.HexDumpEncoder;
import sun.security.util.Debug;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.PKIXExtensions;

class OCSPRequest
{
  private static final Debug debug = Debug.getInstance("certpath");
  private static final boolean dump = (debug != null) && (Debug.isOn("ocsp"));
  private final List<CertId> certIds;
  private final List<Extension> extensions;
  private byte[] nonce;
  
  OCSPRequest(CertId paramCertId)
  {
    this(Collections.singletonList(paramCertId));
  }
  
  OCSPRequest(List<CertId> paramList)
  {
    certIds = paramList;
    extensions = Collections.emptyList();
  }
  
  OCSPRequest(List<CertId> paramList, List<Extension> paramList1)
  {
    certIds = paramList;
    extensions = paramList1;
  }
  
  byte[] encodeBytes()
    throws IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    Object localObject1 = certIds.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (CertId)((Iterator)localObject1).next();
      localObject3 = new DerOutputStream();
      ((CertId)localObject2).encode((DerOutputStream)localObject3);
      localDerOutputStream2.write((byte)48, (DerOutputStream)localObject3);
    }
    localDerOutputStream1.write((byte)48, localDerOutputStream2);
    if (!extensions.isEmpty())
    {
      localObject1 = new DerOutputStream();
      localObject2 = extensions.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Extension)((Iterator)localObject2).next();
        ((Extension)localObject3).encode((OutputStream)localObject1);
        if (((Extension)localObject3).getId().equals(PKIXExtensions.OCSPNonce_Id.toString())) {
          nonce = ((Extension)localObject3).getValue();
        }
      }
      localObject2 = new DerOutputStream();
      ((DerOutputStream)localObject2).write((byte)48, (DerOutputStream)localObject1);
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), (DerOutputStream)localObject2);
    }
    localObject1 = new DerOutputStream();
    ((DerOutputStream)localObject1).write((byte)48, localDerOutputStream1);
    Object localObject2 = new DerOutputStream();
    ((DerOutputStream)localObject2).write((byte)48, (DerOutputStream)localObject1);
    Object localObject3 = ((DerOutputStream)localObject2).toByteArray();
    if (dump)
    {
      HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
      debug.println("OCSPRequest bytes...\n\n" + localHexDumpEncoder.encode((byte[])localObject3) + "\n");
    }
    return (byte[])localObject3;
  }
  
  List<CertId> getCertIds()
  {
    return certIds;
  }
  
  byte[] getNonce()
  {
    return nonce;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\OCSPRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */