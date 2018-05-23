package sun.security.jgss.wrapper;

import java.security.Provider;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;

public class GSSCredElement
  implements GSSCredentialSpi
{
  private int usage;
  long pCred;
  private GSSNameElement name = null;
  private GSSLibStub cStub;
  
  void doServicePermCheck()
    throws GSSException
  {
    if ((GSSUtil.isKerberosMech(cStub.getMech())) && (System.getSecurityManager() != null))
    {
      String str;
      if (isInitiatorCredential())
      {
        str = Krb5Util.getTGSName(name);
        Krb5Util.checkServicePermission(str, "initiate");
      }
      if ((isAcceptorCredential()) && (name != GSSNameElement.DEF_ACCEPTOR))
      {
        str = name.getKrbName();
        Krb5Util.checkServicePermission(str, "accept");
      }
    }
  }
  
  GSSCredElement(long paramLong, GSSNameElement paramGSSNameElement, Oid paramOid)
    throws GSSException
  {
    pCred = paramLong;
    cStub = GSSLibStub.getInstance(paramOid);
    usage = 1;
    name = paramGSSNameElement;
  }
  
  GSSCredElement(GSSNameElement paramGSSNameElement, int paramInt1, int paramInt2, GSSLibStub paramGSSLibStub)
    throws GSSException
  {
    cStub = paramGSSLibStub;
    usage = paramInt2;
    if (paramGSSNameElement != null)
    {
      name = paramGSSNameElement;
      doServicePermCheck();
      pCred = cStub.acquireCred(name.pName, paramInt1, paramInt2);
    }
    else
    {
      pCred = cStub.acquireCred(0L, paramInt1, paramInt2);
      name = new GSSNameElement(cStub.getCredName(pCred), cStub);
      doServicePermCheck();
    }
  }
  
  public Provider getProvider()
  {
    return SunNativeProvider.INSTANCE;
  }
  
  public void dispose()
    throws GSSException
  {
    name = null;
    if (pCred != 0L) {
      pCred = cStub.releaseCred(pCred);
    }
  }
  
  public GSSNameElement getName()
    throws GSSException
  {
    return name == GSSNameElement.DEF_ACCEPTOR ? null : name;
  }
  
  public int getInitLifetime()
    throws GSSException
  {
    if (isInitiatorCredential()) {
      return cStub.getCredTime(pCred);
    }
    return 0;
  }
  
  public int getAcceptLifetime()
    throws GSSException
  {
    if (isAcceptorCredential()) {
      return cStub.getCredTime(pCred);
    }
    return 0;
  }
  
  public boolean isInitiatorCredential()
  {
    return usage != 2;
  }
  
  public boolean isAcceptorCredential()
  {
    return usage != 1;
  }
  
  public Oid getMechanism()
  {
    return cStub.getMech();
  }
  
  public String toString()
  {
    return "N/A";
  }
  
  protected void finalize()
    throws Throwable
  {
    dispose();
  }
  
  public GSSCredentialSpi impersonate(GSSNameSpi paramGSSNameSpi)
    throws GSSException
  {
    throw new GSSException(11, -1, "Not supported yet");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\wrapper\GSSCredElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */