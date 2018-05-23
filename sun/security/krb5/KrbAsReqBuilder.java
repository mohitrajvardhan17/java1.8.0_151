package sun.security.krb5;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import javax.security.auth.kerberos.KeyTab;
import sun.security.jgss.krb5.Krb5Util;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.PAData;
import sun.security.krb5.internal.PAData.SaltAndParams;
import sun.security.krb5.internal.crypto.EType;

public final class KrbAsReqBuilder
{
  private KDCOptions options;
  private PrincipalName cname;
  private PrincipalName sname;
  private KerberosTime from;
  private KerberosTime till;
  private KerberosTime rtime;
  private HostAddresses addresses;
  private final char[] password;
  private final KeyTab ktab;
  private PAData[] paList;
  private KrbAsReq req;
  private KrbAsRep rep;
  private State state;
  
  private void init(PrincipalName paramPrincipalName)
    throws KrbException
  {
    cname = paramPrincipalName;
    state = State.INIT;
  }
  
  public KrbAsReqBuilder(PrincipalName paramPrincipalName, KeyTab paramKeyTab)
    throws KrbException
  {
    init(paramPrincipalName);
    ktab = paramKeyTab;
    password = null;
  }
  
  public KrbAsReqBuilder(PrincipalName paramPrincipalName, char[] paramArrayOfChar)
    throws KrbException
  {
    init(paramPrincipalName);
    password = ((char[])paramArrayOfChar.clone());
    ktab = null;
  }
  
  public EncryptionKey[] getKeys(boolean paramBoolean)
    throws KrbException
  {
    checkState(paramBoolean ? State.REQ_OK : State.INIT, "Cannot get keys");
    if (password != null)
    {
      int[] arrayOfInt = EType.getDefaults("default_tkt_enctypes");
      EncryptionKey[] arrayOfEncryptionKey = new EncryptionKey[arrayOfInt.length];
      String str = null;
      try
      {
        for (int i = 0; i < arrayOfInt.length; i++)
        {
          localObject = PAData.getSaltAndParams(arrayOfInt[i], paList);
          if (localObject != null)
          {
            if ((arrayOfInt[i] != 23) && (salt != null)) {
              str = salt;
            }
            arrayOfEncryptionKey[i] = EncryptionKey.acquireSecretKey(cname, password, arrayOfInt[i], (PAData.SaltAndParams)localObject);
          }
        }
        if (str == null) {
          str = cname.getSalt();
        }
        for (i = 0; i < arrayOfInt.length; i++) {
          if (arrayOfEncryptionKey[i] == null) {
            arrayOfEncryptionKey[i] = EncryptionKey.acquireSecretKey(password, str, arrayOfInt[i], null);
          }
        }
      }
      catch (IOException localIOException)
      {
        Object localObject = new KrbException(909);
        ((KrbException)localObject).initCause(localIOException);
        throw ((Throwable)localObject);
      }
      return arrayOfEncryptionKey;
    }
    throw new IllegalStateException("Required password not provided");
  }
  
  public void setOptions(KDCOptions paramKDCOptions)
  {
    checkState(State.INIT, "Cannot specify options");
    options = paramKDCOptions;
  }
  
  public void setTarget(PrincipalName paramPrincipalName)
  {
    checkState(State.INIT, "Cannot specify target");
    sname = paramPrincipalName;
  }
  
  public void setAddresses(HostAddresses paramHostAddresses)
  {
    checkState(State.INIT, "Cannot specify addresses");
    addresses = paramHostAddresses;
  }
  
  private KrbAsReq build(EncryptionKey paramEncryptionKey)
    throws KrbException, IOException
  {
    int[] arrayOfInt;
    if (password != null)
    {
      arrayOfInt = EType.getDefaults("default_tkt_enctypes");
    }
    else
    {
      EncryptionKey[] arrayOfEncryptionKey1 = Krb5Util.keysFromJavaxKeyTab(ktab, cname);
      arrayOfInt = EType.getDefaults("default_tkt_enctypes", arrayOfEncryptionKey1);
      for (EncryptionKey localEncryptionKey : arrayOfEncryptionKey1) {
        localEncryptionKey.destroy();
      }
    }
    return new KrbAsReq(paramEncryptionKey, options, cname, sname, from, till, rtime, arrayOfInt, addresses);
  }
  
  private KrbAsReqBuilder resolve()
    throws KrbException, Asn1Exception, IOException
  {
    if (ktab != null) {
      rep.decryptUsingKeyTab(ktab, req, cname);
    } else {
      rep.decryptUsingPassword(password, req, cname);
    }
    if (rep.getPA() != null) {
      if ((paList == null) || (paList.length == 0))
      {
        paList = rep.getPA();
      }
      else
      {
        int i = rep.getPA().length;
        if (i > 0)
        {
          int j = paList.length;
          paList = ((PAData[])Arrays.copyOf(paList, paList.length + i));
          System.arraycopy(rep.getPA(), 0, paList, j, i);
        }
      }
    }
    return this;
  }
  
  private KrbAsReqBuilder send()
    throws KrbException, IOException
  {
    int i = 0;
    KdcComm localKdcComm = new KdcComm(cname.getRealmAsString());
    EncryptionKey localEncryptionKey1 = null;
    for (;;)
    {
      try
      {
        req = build(localEncryptionKey1);
        rep = new KrbAsRep(localKdcComm.send(req.encoding()));
        return this;
      }
      catch (KrbException localKrbException)
      {
        if ((i == 0) && ((localKrbException.returnCode() == 24) || (localKrbException.returnCode() == 25)))
        {
          if (Krb5.DEBUG) {
            System.out.println("KrbAsReqBuilder: PREAUTH FAILED/REQ, re-send AS-REQ");
          }
          i = 1;
          KRBError localKRBError = localKrbException.getError();
          int j = PAData.getPreferredEType(localKRBError.getPA(), EType.getDefaults("default_tkt_enctypes")[0]);
          if (password == null)
          {
            EncryptionKey[] arrayOfEncryptionKey1 = Krb5Util.keysFromJavaxKeyTab(ktab, cname);
            localEncryptionKey1 = EncryptionKey.findKey(j, arrayOfEncryptionKey1);
            if (localEncryptionKey1 != null) {
              localEncryptionKey1 = (EncryptionKey)localEncryptionKey1.clone();
            }
            EncryptionKey[] arrayOfEncryptionKey2 = arrayOfEncryptionKey1;
            int k = arrayOfEncryptionKey2.length;
            int m = 0;
            if (m < k)
            {
              EncryptionKey localEncryptionKey2 = arrayOfEncryptionKey2[m];
              localEncryptionKey2.destroy();
              m++;
              continue;
            }
          }
          else
          {
            localEncryptionKey1 = EncryptionKey.acquireSecretKey(cname, password, j, PAData.getSaltAndParams(j, localKRBError.getPA()));
          }
          paList = localKRBError.getPA();
        }
        else
        {
          throw localKrbException;
        }
      }
    }
  }
  
  public KrbAsReqBuilder action()
    throws KrbException, Asn1Exception, IOException
  {
    checkState(State.INIT, "Cannot call action");
    state = State.REQ_OK;
    return send().resolve();
  }
  
  public Credentials getCreds()
  {
    checkState(State.REQ_OK, "Cannot retrieve creds");
    return rep.getCreds();
  }
  
  public sun.security.krb5.internal.ccache.Credentials getCCreds()
  {
    checkState(State.REQ_OK, "Cannot retrieve CCreds");
    return rep.getCCreds();
  }
  
  public void destroy()
  {
    state = State.DESTROYED;
    if (password != null) {
      Arrays.fill(password, '\000');
    }
  }
  
  private void checkState(State paramState, String paramString)
  {
    if (state != paramState) {
      throw new IllegalStateException(paramString + " at " + paramState + " state");
    }
  }
  
  private static enum State
  {
    INIT,  REQ_OK,  DESTROYED;
    
    private State() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\KrbAsReqBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */