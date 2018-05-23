package sun.security.jgss.spnego;

import java.security.Provider;
import java.util.Vector;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.GSSManagerImpl;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.ProviderList;
import sun.security.jgss.SunProvider;
import sun.security.jgss.krb5.Krb5AcceptCredential;
import sun.security.jgss.krb5.Krb5InitCredential;
import sun.security.jgss.krb5.Krb5MechFactory;
import sun.security.jgss.krb5.Krb5NameElement;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.spi.MechanismFactory;

public final class SpNegoMechFactory
  implements MechanismFactory
{
  static final Provider PROVIDER = new SunProvider();
  static final Oid GSS_SPNEGO_MECH_OID = GSSUtil.createOid("1.3.6.1.5.5.2");
  private static Oid[] nameTypes = { GSSName.NT_USER_NAME, GSSName.NT_HOSTBASED_SERVICE, GSSName.NT_EXPORT_NAME };
  private static final Oid DEFAULT_SPNEGO_MECH_OID = ProviderList.DEFAULT_MECH_OID.equals(GSS_SPNEGO_MECH_OID) ? GSSUtil.GSS_KRB5_MECH_OID : ProviderList.DEFAULT_MECH_OID;
  final GSSManagerImpl manager;
  final Oid[] availableMechs;
  
  private static SpNegoCredElement getCredFromSubject(GSSNameSpi paramGSSNameSpi, boolean paramBoolean)
    throws GSSException
  {
    Vector localVector = GSSUtil.searchSubject(paramGSSNameSpi, GSS_SPNEGO_MECH_OID, paramBoolean, SpNegoCredElement.class);
    SpNegoCredElement localSpNegoCredElement = (localVector == null) || (localVector.isEmpty()) ? null : (SpNegoCredElement)localVector.firstElement();
    if (localSpNegoCredElement != null)
    {
      GSSCredentialSpi localGSSCredentialSpi = localSpNegoCredElement.getInternalCred();
      if (GSSUtil.isKerberosMech(localGSSCredentialSpi.getMechanism()))
      {
        Object localObject;
        if (paramBoolean)
        {
          localObject = (Krb5InitCredential)localGSSCredentialSpi;
          Krb5MechFactory.checkInitCredPermission((Krb5NameElement)((Krb5InitCredential)localObject).getName());
        }
        else
        {
          localObject = (Krb5AcceptCredential)localGSSCredentialSpi;
          Krb5MechFactory.checkAcceptCredPermission((Krb5NameElement)((Krb5AcceptCredential)localObject).getName(), paramGSSNameSpi);
        }
      }
    }
    return localSpNegoCredElement;
  }
  
  public SpNegoMechFactory(GSSCaller paramGSSCaller)
  {
    manager = new GSSManagerImpl(paramGSSCaller, false);
    Oid[] arrayOfOid = manager.getMechs();
    availableMechs = new Oid[arrayOfOid.length - 1];
    int i = 0;
    int j = 0;
    while (i < arrayOfOid.length)
    {
      if (!arrayOfOid[i].equals(GSS_SPNEGO_MECH_OID)) {
        availableMechs[(j++)] = arrayOfOid[i];
      }
      i++;
    }
    for (i = 0; i < availableMechs.length; i++) {
      if (availableMechs[i].equals(DEFAULT_SPNEGO_MECH_OID))
      {
        if (i == 0) {
          break;
        }
        availableMechs[i] = availableMechs[0];
        availableMechs[0] = DEFAULT_SPNEGO_MECH_OID;
        break;
      }
    }
  }
  
  public GSSNameSpi getNameElement(String paramString, Oid paramOid)
    throws GSSException
  {
    return manager.getNameElement(paramString, paramOid, DEFAULT_SPNEGO_MECH_OID);
  }
  
  public GSSNameSpi getNameElement(byte[] paramArrayOfByte, Oid paramOid)
    throws GSSException
  {
    return manager.getNameElement(paramArrayOfByte, paramOid, DEFAULT_SPNEGO_MECH_OID);
  }
  
  public GSSCredentialSpi getCredentialElement(GSSNameSpi paramGSSNameSpi, int paramInt1, int paramInt2, int paramInt3)
    throws GSSException
  {
    SpNegoCredElement localSpNegoCredElement = getCredFromSubject(paramGSSNameSpi, paramInt3 != 2);
    if (localSpNegoCredElement == null) {
      localSpNegoCredElement = new SpNegoCredElement(manager.getCredentialElement(paramGSSNameSpi, paramInt1, paramInt2, null, paramInt3));
    }
    return localSpNegoCredElement;
  }
  
  public GSSContextSpi getMechanismContext(GSSNameSpi paramGSSNameSpi, GSSCredentialSpi paramGSSCredentialSpi, int paramInt)
    throws GSSException
  {
    if (paramGSSCredentialSpi == null)
    {
      paramGSSCredentialSpi = getCredFromSubject(null, true);
    }
    else if (!(paramGSSCredentialSpi instanceof SpNegoCredElement))
    {
      SpNegoCredElement localSpNegoCredElement = new SpNegoCredElement(paramGSSCredentialSpi);
      return new SpNegoContext(this, paramGSSNameSpi, localSpNegoCredElement, paramInt);
    }
    return new SpNegoContext(this, paramGSSNameSpi, paramGSSCredentialSpi, paramInt);
  }
  
  public GSSContextSpi getMechanismContext(GSSCredentialSpi paramGSSCredentialSpi)
    throws GSSException
  {
    if (paramGSSCredentialSpi == null)
    {
      paramGSSCredentialSpi = getCredFromSubject(null, false);
    }
    else if (!(paramGSSCredentialSpi instanceof SpNegoCredElement))
    {
      SpNegoCredElement localSpNegoCredElement = new SpNegoCredElement(paramGSSCredentialSpi);
      return new SpNegoContext(this, localSpNegoCredElement);
    }
    return new SpNegoContext(this, paramGSSCredentialSpi);
  }
  
  public GSSContextSpi getMechanismContext(byte[] paramArrayOfByte)
    throws GSSException
  {
    return new SpNegoContext(this, paramArrayOfByte);
  }
  
  public final Oid getMechanismOid()
  {
    return GSS_SPNEGO_MECH_OID;
  }
  
  public Provider getProvider()
  {
    return PROVIDER;
  }
  
  public Oid[] getNameTypes()
  {
    return nameTypes;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\spnego\SpNegoMechFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */