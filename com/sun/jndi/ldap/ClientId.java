package com.sun.jndi.ldap;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import javax.naming.ldap.Control;
import javax.net.SocketFactory;

class ClientId
{
  private final int version;
  private final String hostname;
  private final int port;
  private final String protocol;
  private final Control[] bindCtls;
  private final OutputStream trace;
  private final String socketFactory;
  private final int myHash;
  private final int ctlHash;
  private SocketFactory factory = null;
  private Method sockComparator = null;
  private boolean isDefaultSockFactory = false;
  public static final boolean debug = false;
  
  ClientId(int paramInt1, String paramString1, int paramInt2, String paramString2, Control[] paramArrayOfControl, OutputStream paramOutputStream, String paramString3)
  {
    version = paramInt1;
    hostname = paramString1.toLowerCase(Locale.ENGLISH);
    port = paramInt2;
    protocol = paramString2;
    bindCtls = (paramArrayOfControl != null ? (Control[])paramArrayOfControl.clone() : null);
    trace = paramOutputStream;
    socketFactory = paramString3;
    if ((paramString3 != null) && (!paramString3.equals("javax.net.ssl.SSLSocketFactory"))) {
      try
      {
        Class localClass1 = Obj.helper.loadClass(paramString3);
        Class localClass2 = Class.forName("java.lang.Object");
        sockComparator = localClass1.getMethod("compare", new Class[] { localClass2, localClass2 });
        Method localMethod = localClass1.getMethod("getDefault", new Class[0]);
        factory = ((SocketFactory)localMethod.invoke(null, new Object[0]));
      }
      catch (Exception localException) {}
    } else {
      isDefaultSockFactory = true;
    }
    myHash = (paramInt1 + paramInt2 + (paramOutputStream != null ? paramOutputStream.hashCode() : 0) + (hostname != null ? hostname.hashCode() : 0) + (paramString2 != null ? paramString2.hashCode() : 0) + (ctlHash = hashCodeControls(paramArrayOfControl)));
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ClientId)) {
      return false;
    }
    ClientId localClientId = (ClientId)paramObject;
    return (myHash == myHash) && (version == version) && (port == port) && (trace == trace) && ((hostname == hostname) || ((hostname != null) && (hostname.equals(hostname)))) && ((protocol == protocol) || ((protocol != null) && (protocol.equals(protocol)))) && (ctlHash == ctlHash) && (equalsControls(bindCtls, bindCtls)) && (equalsSockFactory(localClientId));
  }
  
  public int hashCode()
  {
    return myHash;
  }
  
  private static int hashCodeControls(Control[] paramArrayOfControl)
  {
    if (paramArrayOfControl == null) {
      return 0;
    }
    int i = 0;
    for (int j = 0; j < paramArrayOfControl.length; j++) {
      i = i * 31 + paramArrayOfControl[j].getID().hashCode();
    }
    return i;
  }
  
  private static boolean equalsControls(Control[] paramArrayOfControl1, Control[] paramArrayOfControl2)
  {
    if (paramArrayOfControl1 == paramArrayOfControl2) {
      return true;
    }
    if ((paramArrayOfControl1 == null) || (paramArrayOfControl2 == null)) {
      return false;
    }
    if (paramArrayOfControl1.length != paramArrayOfControl2.length) {
      return false;
    }
    for (int i = 0; i < paramArrayOfControl1.length; i++) {
      if ((!paramArrayOfControl1[i].getID().equals(paramArrayOfControl2[i].getID())) || (paramArrayOfControl1[i].isCritical() != paramArrayOfControl2[i].isCritical()) || (!Arrays.equals(paramArrayOfControl1[i].getEncodedValue(), paramArrayOfControl2[i].getEncodedValue()))) {
        return false;
      }
    }
    return true;
  }
  
  private boolean equalsSockFactory(ClientId paramClientId)
  {
    if ((isDefaultSockFactory) && (isDefaultSockFactory)) {
      return true;
    }
    if (!isDefaultSockFactory) {
      return invokeComparator(paramClientId, this);
    }
    return invokeComparator(this, paramClientId);
  }
  
  private boolean invokeComparator(ClientId paramClientId1, ClientId paramClientId2)
  {
    Object localObject;
    try
    {
      localObject = sockComparator.invoke(factory, new Object[] { socketFactory, socketFactory });
    }
    catch (Exception localException)
    {
      return false;
    }
    return ((Integer)localObject).intValue() == 0;
  }
  
  private static String toStringControls(Control[] paramArrayOfControl)
  {
    if (paramArrayOfControl == null) {
      return "";
    }
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramArrayOfControl.length; i++)
    {
      localStringBuffer.append(paramArrayOfControl[i].getID());
      localStringBuffer.append(' ');
    }
    return localStringBuffer.toString();
  }
  
  public String toString()
  {
    return hostname + ":" + port + ":" + (protocol != null ? protocol : "") + ":" + toStringControls(bindCtls) + ":" + socketFactory;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\ClientId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */