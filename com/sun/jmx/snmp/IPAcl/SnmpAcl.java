package com.sun.jmx.snmp.IPAcl;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.InetAddressAcl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.security.acl.AclEntry;
import java.security.acl.NotOwnerException;
import java.security.acl.Permission;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SnmpAcl
  implements InetAddressAcl, Serializable
{
  private static final long serialVersionUID = -6702287103824397063L;
  static final PermissionImpl READ = new PermissionImpl("READ");
  static final PermissionImpl WRITE = new PermissionImpl("WRITE");
  private AclImpl acl = null;
  private boolean alwaysAuthorized = false;
  private String authorizedListFile = null;
  private Hashtable<InetAddress, Vector<String>> trapDestList = null;
  private Hashtable<InetAddress, Vector<String>> informDestList = null;
  private PrincipalImpl owner = null;
  
  public SnmpAcl(String paramString)
    throws UnknownHostException, IllegalArgumentException
  {
    this(paramString, null);
  }
  
  public SnmpAcl(String paramString1, String paramString2)
    throws UnknownHostException, IllegalArgumentException
  {
    try
    {
      acl = new AclImpl(owner, paramString1);
      AclEntryImpl localAclEntryImpl = new AclEntryImpl(owner);
      localAclEntryImpl.addPermission(READ);
      localAclEntryImpl.addPermission(WRITE);
      acl.addEntry(owner, localAclEntryImpl);
    }
    catch (NotOwnerException localNotOwnerException)
    {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpAcl.class.getName(), "SnmpAcl(String,String)", "Should never get NotOwnerException as the owner is built in this constructor");
      }
    }
    if (paramString2 == null) {
      setDefaultFileName();
    } else {
      setAuthorizedListFile(paramString2);
    }
    readAuthorizedListFile();
  }
  
  public Enumeration<AclEntry> entries()
  {
    return acl.entries();
  }
  
  public Enumeration<String> communities()
  {
    HashSet localHashSet = new HashSet();
    Vector localVector = new Vector();
    Object localObject = acl.entries();
    while (((Enumeration)localObject).hasMoreElements())
    {
      AclEntryImpl localAclEntryImpl = (AclEntryImpl)((Enumeration)localObject).nextElement();
      Enumeration localEnumeration = localAclEntryImpl.communities();
      while (localEnumeration.hasMoreElements()) {
        localHashSet.add(localEnumeration.nextElement());
      }
    }
    localObject = (String[])localHashSet.toArray(new String[0]);
    for (int i = 0; i < localObject.length; i++) {
      localVector.addElement(localObject[i]);
    }
    return localVector.elements();
  }
  
  public String getName()
  {
    return acl.getName();
  }
  
  public static PermissionImpl getREAD()
  {
    return READ;
  }
  
  public static PermissionImpl getWRITE()
  {
    return WRITE;
  }
  
  public static String getDefaultAclFileName()
  {
    String str = System.getProperty("file.separator");
    StringBuffer localStringBuffer = new StringBuffer(System.getProperty("java.home")).append(str).append("lib").append(str).append("snmp.acl");
    return localStringBuffer.toString();
  }
  
  public void setAuthorizedListFile(String paramString)
    throws IllegalArgumentException
  {
    File localFile = new File(paramString);
    if (!localFile.isFile())
    {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpAcl.class.getName(), "setAuthorizedListFile", "ACL file not found: " + paramString);
      }
      throw new IllegalArgumentException("The specified file [" + localFile + "] doesn't exist or is not a file, no configuration loaded");
    }
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "setAuthorizedListFile", "Default file set to " + paramString);
    }
    authorizedListFile = paramString;
  }
  
  public void rereadTheFile()
    throws NotOwnerException, UnknownHostException
  {
    alwaysAuthorized = false;
    acl.removeAll(owner);
    trapDestList.clear();
    informDestList.clear();
    AclEntryImpl localAclEntryImpl = new AclEntryImpl(owner);
    localAclEntryImpl.addPermission(READ);
    localAclEntryImpl.addPermission(WRITE);
    acl.addEntry(owner, localAclEntryImpl);
    readAuthorizedListFile();
  }
  
  public String getAuthorizedListFile()
  {
    return authorizedListFile;
  }
  
  public boolean checkReadPermission(InetAddress paramInetAddress)
  {
    if (alwaysAuthorized) {
      return true;
    }
    PrincipalImpl localPrincipalImpl = new PrincipalImpl(paramInetAddress);
    return acl.checkPermission(localPrincipalImpl, READ);
  }
  
  public boolean checkReadPermission(InetAddress paramInetAddress, String paramString)
  {
    if (alwaysAuthorized) {
      return true;
    }
    PrincipalImpl localPrincipalImpl = new PrincipalImpl(paramInetAddress);
    return acl.checkPermission(localPrincipalImpl, paramString, READ);
  }
  
  public boolean checkCommunity(String paramString)
  {
    return acl.checkCommunity(paramString);
  }
  
  public boolean checkWritePermission(InetAddress paramInetAddress)
  {
    if (alwaysAuthorized) {
      return true;
    }
    PrincipalImpl localPrincipalImpl = new PrincipalImpl(paramInetAddress);
    return acl.checkPermission(localPrincipalImpl, WRITE);
  }
  
  public boolean checkWritePermission(InetAddress paramInetAddress, String paramString)
  {
    if (alwaysAuthorized) {
      return true;
    }
    PrincipalImpl localPrincipalImpl = new PrincipalImpl(paramInetAddress);
    return acl.checkPermission(localPrincipalImpl, paramString, WRITE);
  }
  
  public Enumeration<InetAddress> getTrapDestinations()
  {
    return trapDestList.keys();
  }
  
  public Enumeration<String> getTrapCommunities(InetAddress paramInetAddress)
  {
    Vector localVector = null;
    if ((localVector = (Vector)trapDestList.get(paramInetAddress)) != null)
    {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "getTrapCommunities", "[" + paramInetAddress.toString() + "] is in list");
      }
      return localVector.elements();
    }
    localVector = new Vector();
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "getTrapCommunities", "[" + paramInetAddress.toString() + "] is not in list");
    }
    return localVector.elements();
  }
  
  public Enumeration<InetAddress> getInformDestinations()
  {
    return informDestList.keys();
  }
  
  public Enumeration<String> getInformCommunities(InetAddress paramInetAddress)
  {
    Vector localVector = null;
    if ((localVector = (Vector)informDestList.get(paramInetAddress)) != null)
    {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "getInformCommunities", "[" + paramInetAddress.toString() + "] is in list");
      }
      return localVector.elements();
    }
    localVector = new Vector();
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "getInformCommunities", "[" + paramInetAddress.toString() + "] is not in list");
    }
    return localVector.elements();
  }
  
  private void readAuthorizedListFile()
  {
    alwaysAuthorized = false;
    if (authorizedListFile == null)
    {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "readAuthorizedListFile", "alwaysAuthorized set to true");
      }
      alwaysAuthorized = true;
    }
    else
    {
      Parser localParser = null;
      try
      {
        localParser = new Parser(new FileInputStream(getAuthorizedListFile()));
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpAcl.class.getName(), "readAuthorizedListFile", "The specified file was not found, authorize everybody");
        }
        alwaysAuthorized = true;
        return;
      }
      try
      {
        JDMSecurityDefs localJDMSecurityDefs = localParser.SecurityDefs();
        localJDMSecurityDefs.buildAclEntries(owner, acl);
        localJDMSecurityDefs.buildTrapEntries(trapDestList);
        localJDMSecurityDefs.buildInformEntries(informDestList);
      }
      catch (ParseException localParseException)
      {
        if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpAcl.class.getName(), "readAuthorizedListFile", "Got parsing exception", localParseException);
        }
        throw new IllegalArgumentException(localParseException.getMessage());
      }
      catch (Error localError)
      {
        if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpAcl.class.getName(), "readAuthorizedListFile", "Got unexpected error", localError);
        }
        throw new IllegalArgumentException(localError.getMessage());
      }
      Enumeration localEnumeration1 = acl.entries();
      while (localEnumeration1.hasMoreElements())
      {
        AclEntryImpl localAclEntryImpl = (AclEntryImpl)localEnumeration1.nextElement();
        if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "readAuthorizedListFile", "===> " + localAclEntryImpl.getPrincipal().toString());
        }
        Enumeration localEnumeration2 = localAclEntryImpl.permissions();
        while (localEnumeration2.hasMoreElements())
        {
          Permission localPermission = (Permission)localEnumeration2.nextElement();
          if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "readAuthorizedListFile", "perm = " + localPermission);
          }
        }
      }
    }
  }
  
  private void setDefaultFileName()
  {
    try
    {
      setAuthorizedListFile(getDefaultAclFileName());
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\SnmpAcl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */