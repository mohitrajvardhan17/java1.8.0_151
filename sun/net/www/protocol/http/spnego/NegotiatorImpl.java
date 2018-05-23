package sun.net.www.protocol.http.spnego;

import com.sun.security.jgss.ExtendedGSSContext;
import java.io.IOException;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.net.www.protocol.http.HttpCallerInfo;
import sun.net.www.protocol.http.Negotiator;
import sun.security.action.GetBooleanAction;
import sun.security.jgss.GSSManagerImpl;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.HttpCaller;

public class NegotiatorImpl
  extends Negotiator
{
  private static final boolean DEBUG = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.krb5.debug"))).booleanValue();
  private GSSContext context;
  private byte[] oneToken;
  
  private void init(HttpCallerInfo paramHttpCallerInfo)
    throws GSSException
  {
    Oid localOid;
    if (scheme.equalsIgnoreCase("Kerberos"))
    {
      localOid = GSSUtil.GSS_KRB5_MECH_OID;
    }
    else
    {
      localObject = (String)AccessController.doPrivileged(new PrivilegedAction()
      {
        public String run()
        {
          return System.getProperty("http.auth.preference", "spnego");
        }
      });
      if (((String)localObject).equalsIgnoreCase("kerberos")) {
        localOid = GSSUtil.GSS_KRB5_MECH_OID;
      } else {
        localOid = GSSUtil.GSS_SPNEGO_MECH_OID;
      }
    }
    Object localObject = new GSSManagerImpl(new HttpCaller(paramHttpCallerInfo));
    String str = "HTTP@" + host.toLowerCase();
    GSSName localGSSName = ((GSSManagerImpl)localObject).createName(str, GSSName.NT_HOSTBASED_SERVICE);
    context = ((GSSManagerImpl)localObject).createContext(localGSSName, localOid, null, 0);
    if ((context instanceof ExtendedGSSContext)) {
      ((ExtendedGSSContext)context).requestDelegPolicy(true);
    }
    oneToken = context.initSecContext(new byte[0], 0, 0);
  }
  
  public NegotiatorImpl(HttpCallerInfo paramHttpCallerInfo)
    throws IOException
  {
    try
    {
      init(paramHttpCallerInfo);
    }
    catch (GSSException localGSSException)
    {
      if (DEBUG)
      {
        System.out.println("Negotiate support not initiated, will fallback to other scheme if allowed. Reason:");
        localGSSException.printStackTrace();
      }
      IOException localIOException = new IOException("Negotiate support not initiated");
      localIOException.initCause(localGSSException);
      throw localIOException;
    }
  }
  
  public byte[] firstToken()
  {
    return oneToken;
  }
  
  public byte[] nextToken(byte[] paramArrayOfByte)
    throws IOException
  {
    try
    {
      return context.initSecContext(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    catch (GSSException localGSSException)
    {
      if (DEBUG)
      {
        System.out.println("Negotiate support cannot continue. Reason:");
        localGSSException.printStackTrace();
      }
      IOException localIOException = new IOException("Negotiate support cannot continue");
      localIOException.initCause(localGSSException);
      throw localIOException;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\http\spnego\NegotiatorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */