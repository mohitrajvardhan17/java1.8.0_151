package sun.security.jgss.spnego;

import java.security.Provider;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;

public class SpNegoCredElement
  implements GSSCredentialSpi
{
  private GSSCredentialSpi cred = null;
  
  public SpNegoCredElement(GSSCredentialSpi paramGSSCredentialSpi)
    throws GSSException
  {
    cred = paramGSSCredentialSpi;
  }
  
  Oid getInternalMech()
  {
    return cred.getMechanism();
  }
  
  public GSSCredentialSpi getInternalCred()
  {
    return cred;
  }
  
  public Provider getProvider()
  {
    return SpNegoMechFactory.PROVIDER;
  }
  
  public void dispose()
    throws GSSException
  {
    cred.dispose();
  }
  
  public GSSNameSpi getName()
    throws GSSException
  {
    return cred.getName();
  }
  
  public int getInitLifetime()
    throws GSSException
  {
    return cred.getInitLifetime();
  }
  
  public int getAcceptLifetime()
    throws GSSException
  {
    return cred.getAcceptLifetime();
  }
  
  public boolean isInitiatorCredential()
    throws GSSException
  {
    return cred.isInitiatorCredential();
  }
  
  public boolean isAcceptorCredential()
    throws GSSException
  {
    return cred.isAcceptorCredential();
  }
  
  public Oid getMechanism()
  {
    return GSSUtil.GSS_SPNEGO_MECH_OID;
  }
  
  public GSSCredentialSpi impersonate(GSSNameSpi paramGSSNameSpi)
    throws GSSException
  {
    return cred.impersonate(paramGSSNameSpi);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\spnego\SpNegoCredElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */