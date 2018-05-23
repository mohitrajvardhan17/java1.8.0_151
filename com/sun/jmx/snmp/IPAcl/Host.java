package com.sun.jmx.snmp.IPAcl;

import com.sun.jmx.defaults.JmxProperties;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.acl.NotOwnerException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class Host
  extends SimpleNode
  implements Serializable
{
  public Host(int paramInt)
  {
    super(paramInt);
  }
  
  public Host(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  protected abstract PrincipalImpl createAssociatedPrincipal()
    throws UnknownHostException;
  
  protected abstract String getHname();
  
  public void buildAclEntries(PrincipalImpl paramPrincipalImpl, AclImpl paramAclImpl)
  {
    PrincipalImpl localPrincipalImpl = null;
    try
    {
      localPrincipalImpl = createAssociatedPrincipal();
    }
    catch (UnknownHostException localUnknownHostException1)
    {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, Host.class.getName(), "buildAclEntries", "Cannot create ACL entry; got exception", localUnknownHostException1);
      }
      throw new IllegalArgumentException("Cannot create ACL entry for " + localUnknownHostException1.getMessage());
    }
    AclEntryImpl localAclEntryImpl = null;
    try
    {
      localAclEntryImpl = new AclEntryImpl(localPrincipalImpl);
      registerPermission(localAclEntryImpl);
      paramAclImpl.addEntry(paramPrincipalImpl, localAclEntryImpl);
    }
    catch (UnknownHostException localUnknownHostException2)
    {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, Host.class.getName(), "buildAclEntries", "Cannot create ACL entry; got exception", localUnknownHostException2);
      }
      return;
    }
    catch (NotOwnerException localNotOwnerException)
    {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, Host.class.getName(), "buildAclEntries", "Cannot create ACL entry; got exception", localNotOwnerException);
      }
      return;
    }
  }
  
  private void registerPermission(AclEntryImpl paramAclEntryImpl)
  {
    JDMHost localJDMHost = (JDMHost)jjtGetParent();
    JDMManagers localJDMManagers = (JDMManagers)localJDMHost.jjtGetParent();
    JDMAclItem localJDMAclItem = (JDMAclItem)localJDMManagers.jjtGetParent();
    JDMAccess localJDMAccess = localJDMAclItem.getAccess();
    localJDMAccess.putPermission(paramAclEntryImpl);
    JDMCommunities localJDMCommunities = localJDMAclItem.getCommunities();
    localJDMCommunities.buildCommunities(paramAclEntryImpl);
  }
  
  public void buildTrapEntries(Hashtable<InetAddress, Vector<String>> paramHashtable)
  {
    JDMHostTrap localJDMHostTrap = (JDMHostTrap)jjtGetParent();
    JDMTrapInterestedHost localJDMTrapInterestedHost = (JDMTrapInterestedHost)localJDMHostTrap.jjtGetParent();
    JDMTrapItem localJDMTrapItem = (JDMTrapItem)localJDMTrapInterestedHost.jjtGetParent();
    JDMTrapCommunity localJDMTrapCommunity = localJDMTrapItem.getCommunity();
    String str = localJDMTrapCommunity.getCommunity();
    InetAddress localInetAddress = null;
    try
    {
      localInetAddress = InetAddress.getByName(getHname());
    }
    catch (UnknownHostException localUnknownHostException)
    {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, Host.class.getName(), "buildTrapEntries", "Cannot create TRAP entry; got exception", localUnknownHostException);
      }
      return;
    }
    Vector localVector = null;
    if (paramHashtable.containsKey(localInetAddress))
    {
      localVector = (Vector)paramHashtable.get(localInetAddress);
      if (!localVector.contains(str)) {
        localVector.addElement(str);
      }
    }
    else
    {
      localVector = new Vector();
      localVector.addElement(str);
      paramHashtable.put(localInetAddress, localVector);
    }
  }
  
  public void buildInformEntries(Hashtable<InetAddress, Vector<String>> paramHashtable)
  {
    JDMHostInform localJDMHostInform = (JDMHostInform)jjtGetParent();
    JDMInformInterestedHost localJDMInformInterestedHost = (JDMInformInterestedHost)localJDMHostInform.jjtGetParent();
    JDMInformItem localJDMInformItem = (JDMInformItem)localJDMInformInterestedHost.jjtGetParent();
    JDMInformCommunity localJDMInformCommunity = localJDMInformItem.getCommunity();
    String str = localJDMInformCommunity.getCommunity();
    InetAddress localInetAddress = null;
    try
    {
      localInetAddress = InetAddress.getByName(getHname());
    }
    catch (UnknownHostException localUnknownHostException)
    {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, Host.class.getName(), "buildTrapEntries", "Cannot create INFORM entry; got exception", localUnknownHostException);
      }
      return;
    }
    Vector localVector = null;
    if (paramHashtable.containsKey(localInetAddress))
    {
      localVector = (Vector)paramHashtable.get(localInetAddress);
      if (!localVector.contains(str)) {
        localVector.addElement(str);
      }
    }
    else
    {
      localVector = new Vector();
      localVector.addElement(str);
      paramHashtable.put(localInetAddress, localVector);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\Host.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */