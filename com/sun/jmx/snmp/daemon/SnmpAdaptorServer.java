package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.IPAcl.SnmpAcl;
import com.sun.jmx.snmp.InetAddressAcl;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpIpAddress;
import com.sun.jmx.snmp.SnmpMessage;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpParameters;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpPduFactoryBER;
import com.sun.jmx.snmp.SnmpPduPacket;
import com.sun.jmx.snmp.SnmpPduRequest;
import com.sun.jmx.snmp.SnmpPduTrap;
import com.sun.jmx.snmp.SnmpPeer;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTimeticks;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpVarBindList;
import com.sun.jmx.snmp.agent.SnmpErrorHandlerAgent;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.agent.SnmpMibHandler;
import com.sun.jmx.snmp.agent.SnmpUserDataFactory;
import com.sun.jmx.snmp.tasks.ThreadService;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class SnmpAdaptorServer
  extends CommunicatorServer
  implements SnmpAdaptorServerMBean, MBeanRegistration, SnmpDefinitions, SnmpMibHandler
{
  private int trapPort = 162;
  private int informPort = 162;
  InetAddress address = null;
  private InetAddressAcl ipacl = null;
  private SnmpPduFactory pduFactory = null;
  private SnmpUserDataFactory userDataFactory = null;
  private boolean authRespEnabled = true;
  private boolean authTrapEnabled = true;
  private SnmpOid enterpriseOid = new SnmpOid("1.3.6.1.4.1.42");
  int bufferSize = 1024;
  private transient long startUpTime = 0L;
  private transient DatagramSocket socket = null;
  transient DatagramSocket trapSocket = null;
  private transient SnmpSession informSession = null;
  private transient DatagramPacket packet = null;
  transient Vector<SnmpMibAgent> mibs = new Vector();
  private transient SnmpMibTree root;
  private transient boolean useAcl = true;
  private int maxTries = 3;
  private int timeout = 3000;
  int snmpOutTraps = 0;
  private int snmpOutGetResponses = 0;
  private int snmpOutGenErrs = 0;
  private int snmpOutBadValues = 0;
  private int snmpOutNoSuchNames = 0;
  private int snmpOutTooBigs = 0;
  int snmpOutPkts = 0;
  private int snmpInASNParseErrs = 0;
  private int snmpInBadCommunityUses = 0;
  private int snmpInBadCommunityNames = 0;
  private int snmpInBadVersions = 0;
  private int snmpInGetRequests = 0;
  private int snmpInGetNexts = 0;
  private int snmpInSetRequests = 0;
  private int snmpInPkts = 0;
  private int snmpInTotalReqVars = 0;
  private int snmpInTotalSetVars = 0;
  private int snmpSilentDrops = 0;
  private static final String InterruptSysCallMsg = "Interrupted system call";
  static final SnmpOid sysUpTimeOid = new SnmpOid("1.3.6.1.2.1.1.3.0");
  static final SnmpOid snmpTrapOidOid = new SnmpOid("1.3.6.1.6.3.1.1.4.1.0");
  private ThreadService threadService;
  private static int threadNumber = 6;
  
  public SnmpAdaptorServer()
  {
    this(true, null, 161, null);
  }
  
  public SnmpAdaptorServer(int paramInt)
  {
    this(true, null, paramInt, null);
  }
  
  public SnmpAdaptorServer(InetAddressAcl paramInetAddressAcl)
  {
    this(false, paramInetAddressAcl, 161, null);
  }
  
  public SnmpAdaptorServer(InetAddress paramInetAddress)
  {
    this(true, null, 161, paramInetAddress);
  }
  
  public SnmpAdaptorServer(InetAddressAcl paramInetAddressAcl, int paramInt)
  {
    this(false, paramInetAddressAcl, paramInt, null);
  }
  
  public SnmpAdaptorServer(int paramInt, InetAddress paramInetAddress)
  {
    this(true, null, paramInt, paramInetAddress);
  }
  
  public SnmpAdaptorServer(InetAddressAcl paramInetAddressAcl, InetAddress paramInetAddress)
  {
    this(false, paramInetAddressAcl, 161, paramInetAddress);
  }
  
  public SnmpAdaptorServer(InetAddressAcl paramInetAddressAcl, int paramInt, InetAddress paramInetAddress)
  {
    this(false, paramInetAddressAcl, paramInt, paramInetAddress);
  }
  
  public SnmpAdaptorServer(boolean paramBoolean, int paramInt, InetAddress paramInetAddress)
  {
    this(paramBoolean, null, paramInt, paramInetAddress);
  }
  
  private SnmpAdaptorServer(boolean paramBoolean, InetAddressAcl paramInetAddressAcl, int paramInt, InetAddress paramInetAddress)
  {
    super(4);
    if ((paramInetAddressAcl == null) && (paramBoolean)) {
      try
      {
        paramInetAddressAcl = new SnmpAcl("SNMP protocol adaptor IP ACL");
      }
      catch (UnknownHostException localUnknownHostException)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "constructor", "UnknowHostException when creating ACL", localUnknownHostException);
        }
      }
    } else {
      useAcl = ((paramInetAddressAcl != null) || (paramBoolean));
    }
    init(paramInetAddressAcl, paramInt, paramInetAddress);
  }
  
  public int getServedClientCount()
  {
    return super.getServedClientCount();
  }
  
  public int getActiveClientCount()
  {
    return super.getActiveClientCount();
  }
  
  public int getMaxActiveClientCount()
  {
    return super.getMaxActiveClientCount();
  }
  
  public void setMaxActiveClientCount(int paramInt)
    throws IllegalStateException
  {
    super.setMaxActiveClientCount(paramInt);
  }
  
  public InetAddressAcl getInetAddressAcl()
  {
    return ipacl;
  }
  
  public Integer getTrapPort()
  {
    return new Integer(trapPort);
  }
  
  public void setTrapPort(Integer paramInteger)
  {
    setTrapPort(paramInteger.intValue());
  }
  
  public void setTrapPort(int paramInt)
  {
    int i = paramInt;
    if (i < 0) {
      throw new IllegalArgumentException("Trap port cannot be a negative value");
    }
    trapPort = i;
  }
  
  public int getInformPort()
  {
    return informPort;
  }
  
  public void setInformPort(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Inform request port cannot be a negative value");
    }
    informPort = paramInt;
  }
  
  public String getProtocol()
  {
    return "snmp";
  }
  
  public Integer getBufferSize()
  {
    return new Integer(bufferSize);
  }
  
  public void setBufferSize(Integer paramInteger)
    throws IllegalStateException
  {
    if ((state == 0) || (state == 3)) {
      throw new IllegalStateException("Stop server before carrying out this operation");
    }
    bufferSize = paramInteger.intValue();
  }
  
  public final int getMaxTries()
  {
    return maxTries;
  }
  
  public final synchronized void setMaxTries(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    maxTries = paramInt;
  }
  
  public final int getTimeout()
  {
    return timeout;
  }
  
  public final synchronized void setTimeout(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    timeout = paramInt;
  }
  
  public SnmpPduFactory getPduFactory()
  {
    return pduFactory;
  }
  
  public void setPduFactory(SnmpPduFactory paramSnmpPduFactory)
  {
    if (paramSnmpPduFactory == null) {
      pduFactory = new SnmpPduFactoryBER();
    } else {
      pduFactory = paramSnmpPduFactory;
    }
  }
  
  public void setUserDataFactory(SnmpUserDataFactory paramSnmpUserDataFactory)
  {
    userDataFactory = paramSnmpUserDataFactory;
  }
  
  public SnmpUserDataFactory getUserDataFactory()
  {
    return userDataFactory;
  }
  
  public boolean getAuthTrapEnabled()
  {
    return authTrapEnabled;
  }
  
  public void setAuthTrapEnabled(boolean paramBoolean)
  {
    authTrapEnabled = paramBoolean;
  }
  
  public boolean getAuthRespEnabled()
  {
    return authRespEnabled;
  }
  
  public void setAuthRespEnabled(boolean paramBoolean)
  {
    authRespEnabled = paramBoolean;
  }
  
  public String getEnterpriseOid()
  {
    return enterpriseOid.toString();
  }
  
  public void setEnterpriseOid(String paramString)
    throws IllegalArgumentException
  {
    enterpriseOid = new SnmpOid(paramString);
  }
  
  public String[] getMibs()
  {
    String[] arrayOfString = new String[mibs.size()];
    int i = 0;
    Enumeration localEnumeration = mibs.elements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpMibAgent localSnmpMibAgent = (SnmpMibAgent)localEnumeration.nextElement();
      arrayOfString[(i++)] = localSnmpMibAgent.getMibName();
    }
    return arrayOfString;
  }
  
  public Long getSnmpOutTraps()
  {
    return new Long(snmpOutTraps);
  }
  
  public Long getSnmpOutGetResponses()
  {
    return new Long(snmpOutGetResponses);
  }
  
  public Long getSnmpOutGenErrs()
  {
    return new Long(snmpOutGenErrs);
  }
  
  public Long getSnmpOutBadValues()
  {
    return new Long(snmpOutBadValues);
  }
  
  public Long getSnmpOutNoSuchNames()
  {
    return new Long(snmpOutNoSuchNames);
  }
  
  public Long getSnmpOutTooBigs()
  {
    return new Long(snmpOutTooBigs);
  }
  
  public Long getSnmpInASNParseErrs()
  {
    return new Long(snmpInASNParseErrs);
  }
  
  public Long getSnmpInBadCommunityUses()
  {
    return new Long(snmpInBadCommunityUses);
  }
  
  public Long getSnmpInBadCommunityNames()
  {
    return new Long(snmpInBadCommunityNames);
  }
  
  public Long getSnmpInBadVersions()
  {
    return new Long(snmpInBadVersions);
  }
  
  public Long getSnmpOutPkts()
  {
    return new Long(snmpOutPkts);
  }
  
  public Long getSnmpInPkts()
  {
    return new Long(snmpInPkts);
  }
  
  public Long getSnmpInGetRequests()
  {
    return new Long(snmpInGetRequests);
  }
  
  public Long getSnmpInGetNexts()
  {
    return new Long(snmpInGetNexts);
  }
  
  public Long getSnmpInSetRequests()
  {
    return new Long(snmpInSetRequests);
  }
  
  public Long getSnmpInTotalSetVars()
  {
    return new Long(snmpInTotalSetVars);
  }
  
  public Long getSnmpInTotalReqVars()
  {
    return new Long(snmpInTotalReqVars);
  }
  
  public Long getSnmpSilentDrops()
  {
    return new Long(snmpSilentDrops);
  }
  
  public Long getSnmpProxyDrops()
  {
    return new Long(0L);
  }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception
  {
    if (paramObjectName == null) {
      paramObjectName = new ObjectName(paramMBeanServer.getDefaultDomain() + ":" + "name=SnmpAdaptorServer");
    }
    return super.preRegister(paramMBeanServer, paramObjectName);
  }
  
  public void postRegister(Boolean paramBoolean)
  {
    super.postRegister(paramBoolean);
  }
  
  public void preDeregister()
    throws Exception
  {
    super.preDeregister();
  }
  
  public void postDeregister()
  {
    super.postDeregister();
  }
  
  public SnmpMibHandler addMib(SnmpMibAgent paramSnmpMibAgent)
    throws IllegalArgumentException
  {
    if (paramSnmpMibAgent == null) {
      throw new IllegalArgumentException();
    }
    if (!mibs.contains(paramSnmpMibAgent)) {
      mibs.addElement(paramSnmpMibAgent);
    }
    root.register(paramSnmpMibAgent);
    return this;
  }
  
  public SnmpMibHandler addMib(SnmpMibAgent paramSnmpMibAgent, SnmpOid[] paramArrayOfSnmpOid)
    throws IllegalArgumentException
  {
    if (paramSnmpMibAgent == null) {
      throw new IllegalArgumentException();
    }
    if (paramArrayOfSnmpOid == null) {
      return addMib(paramSnmpMibAgent);
    }
    if (!mibs.contains(paramSnmpMibAgent)) {
      mibs.addElement(paramSnmpMibAgent);
    }
    for (int i = 0; i < paramArrayOfSnmpOid.length; i++) {
      root.register(paramSnmpMibAgent, paramArrayOfSnmpOid[i].longValue());
    }
    return this;
  }
  
  public SnmpMibHandler addMib(SnmpMibAgent paramSnmpMibAgent, String paramString)
    throws IllegalArgumentException
  {
    return addMib(paramSnmpMibAgent);
  }
  
  public SnmpMibHandler addMib(SnmpMibAgent paramSnmpMibAgent, String paramString, SnmpOid[] paramArrayOfSnmpOid)
    throws IllegalArgumentException
  {
    return addMib(paramSnmpMibAgent, paramArrayOfSnmpOid);
  }
  
  public boolean removeMib(SnmpMibAgent paramSnmpMibAgent, String paramString)
  {
    return removeMib(paramSnmpMibAgent);
  }
  
  public boolean removeMib(SnmpMibAgent paramSnmpMibAgent)
  {
    root.unregister(paramSnmpMibAgent);
    return mibs.removeElement(paramSnmpMibAgent);
  }
  
  public boolean removeMib(SnmpMibAgent paramSnmpMibAgent, SnmpOid[] paramArrayOfSnmpOid)
  {
    root.unregister(paramSnmpMibAgent, paramArrayOfSnmpOid);
    return mibs.removeElement(paramSnmpMibAgent);
  }
  
  public boolean removeMib(SnmpMibAgent paramSnmpMibAgent, String paramString, SnmpOid[] paramArrayOfSnmpOid)
  {
    return removeMib(paramSnmpMibAgent, paramArrayOfSnmpOid);
  }
  
  protected void doBind()
    throws CommunicationException, InterruptedException
  {
    try
    {
      synchronized (this)
      {
        socket = new DatagramSocket(port, address);
      }
      dbgTag = makeDebugTag();
    }
    catch (SocketException localSocketException)
    {
      if (localSocketException.getMessage().equals("Interrupted system call")) {
        throw new InterruptedException(localSocketException.toString());
      }
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "doBind", "cannot bind on port " + port);
      }
      throw new CommunicationException(localSocketException);
    }
  }
  
  public int getPort()
  {
    synchronized (this)
    {
      if (socket != null) {
        return socket.getLocalPort();
      }
    }
    return super.getPort();
  }
  
  protected void doUnbind()
    throws CommunicationException, InterruptedException
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "doUnbind", "Finally close the socket");
    }
    synchronized (this)
    {
      if (socket != null)
      {
        socket.close();
        socket = null;
      }
    }
    closeTrapSocketIfNeeded();
    closeInformSocketIfNeeded();
  }
  
  private void createSnmpRequestHandler(SnmpAdaptorServer paramSnmpAdaptorServer, int paramInt, DatagramSocket paramDatagramSocket, DatagramPacket paramDatagramPacket, SnmpMibTree paramSnmpMibTree, Vector<SnmpMibAgent> paramVector, InetAddressAcl paramInetAddressAcl, SnmpPduFactory paramSnmpPduFactory, SnmpUserDataFactory paramSnmpUserDataFactory, MBeanServer paramMBeanServer, ObjectName paramObjectName)
  {
    SnmpRequestHandler localSnmpRequestHandler = new SnmpRequestHandler(this, paramInt, paramDatagramSocket, paramDatagramPacket, paramSnmpMibTree, paramVector, paramInetAddressAcl, paramSnmpPduFactory, paramSnmpUserDataFactory, paramMBeanServer, paramObjectName);
    threadService.submitTask(localSnmpRequestHandler);
  }
  
  protected void doReceive()
    throws CommunicationException, InterruptedException
  {
    try
    {
      packet = new DatagramPacket(new byte[bufferSize], bufferSize);
      socket.receive(packet);
      int i = getState();
      if (i != 0)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "doReceive", "received a message but state not online, returning.");
        }
        return;
      }
      createSnmpRequestHandler(this, servedClientCount, socket, packet, root, mibs, ipacl, pduFactory, userDataFactory, topMBS, objectName);
    }
    catch (SocketException localSocketException)
    {
      if (localSocketException.getMessage().equals("Interrupted system call")) {
        throw new InterruptedException(localSocketException.toString());
      }
      throw new CommunicationException(localSocketException);
    }
    catch (InterruptedIOException localInterruptedIOException)
    {
      throw new InterruptedException(localInterruptedIOException.toString());
    }
    catch (CommunicationException localCommunicationException)
    {
      throw localCommunicationException;
    }
    catch (Exception localException)
    {
      throw new CommunicationException(localException);
    }
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "doReceive", "received a message");
    }
  }
  
  protected void doError(Exception paramException)
    throws CommunicationException
  {}
  
  protected void doProcess()
    throws CommunicationException, InterruptedException
  {}
  
  protected int getBindTries()
  {
    return 1;
  }
  
  public void stop()
  {
    int i = getPort();
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "stop", "Stopping: using port " + i);
    }
    if ((state == 0) || (state == 3))
    {
      super.stop();
      try
      {
        DatagramSocket localDatagramSocket = new DatagramSocket(0);
        try
        {
          byte[] arrayOfByte = new byte[1];
          DatagramPacket localDatagramPacket;
          if (address != null) {
            localDatagramPacket = new DatagramPacket(arrayOfByte, 1, address, i);
          } else {
            localDatagramPacket = new DatagramPacket(arrayOfByte, 1, InetAddress.getLocalHost(), i);
          }
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "stop", "Sending: using port " + i);
          }
          localDatagramSocket.send(localDatagramPacket);
        }
        finally
        {
          localDatagramSocket.close();
        }
      }
      catch (Throwable localThrowable)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "stop", "Got unexpected Throwable", localThrowable);
        }
      }
    }
  }
  
  public void snmpV1Trap(int paramInt1, int paramInt2, SnmpVarBindList paramSnmpVarBindList)
    throws IOException, SnmpStatusException
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "snmpV1Trap", "generic=" + paramInt1 + ", specific=" + paramInt2);
    }
    SnmpPduTrap localSnmpPduTrap = new SnmpPduTrap();
    address = null;
    port = trapPort;
    type = 164;
    version = 0;
    community = null;
    enterprise = enterpriseOid;
    genericTrap = paramInt1;
    specificTrap = paramInt2;
    timeStamp = getSysUpTime();
    if (paramSnmpVarBindList != null)
    {
      varBindList = new SnmpVarBind[paramSnmpVarBindList.size()];
      paramSnmpVarBindList.copyInto(varBindList);
    }
    else
    {
      varBindList = null;
    }
    try
    {
      if (address != null) {
        agentAddr = handleMultipleIpVersion(address.getAddress());
      } else {
        agentAddr = handleMultipleIpVersion(InetAddress.getLocalHost().getAddress());
      }
    }
    catch (UnknownHostException localUnknownHostException)
    {
      byte[] arrayOfByte = new byte[4];
      agentAddr = handleMultipleIpVersion(arrayOfByte);
    }
    sendTrapPdu(localSnmpPduTrap);
  }
  
  private SnmpIpAddress handleMultipleIpVersion(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length == 4) {
      return new SnmpIpAddress(paramArrayOfByte);
    }
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "handleMultipleIPVersion", "Not an IPv4 address, return null");
    }
    return null;
  }
  
  public void snmpV1Trap(InetAddress paramInetAddress, String paramString, int paramInt1, int paramInt2, SnmpVarBindList paramSnmpVarBindList)
    throws IOException, SnmpStatusException
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "snmpV1Trap", "generic=" + paramInt1 + ", specific=" + paramInt2);
    }
    SnmpPduTrap localSnmpPduTrap = new SnmpPduTrap();
    address = null;
    port = trapPort;
    type = 164;
    version = 0;
    if (paramString != null) {
      community = paramString.getBytes();
    } else {
      community = null;
    }
    enterprise = enterpriseOid;
    genericTrap = paramInt1;
    specificTrap = paramInt2;
    timeStamp = getSysUpTime();
    if (paramSnmpVarBindList != null)
    {
      varBindList = new SnmpVarBind[paramSnmpVarBindList.size()];
      paramSnmpVarBindList.copyInto(varBindList);
    }
    else
    {
      varBindList = null;
    }
    try
    {
      if (address != null) {
        agentAddr = handleMultipleIpVersion(address.getAddress());
      } else {
        agentAddr = handleMultipleIpVersion(InetAddress.getLocalHost().getAddress());
      }
    }
    catch (UnknownHostException localUnknownHostException)
    {
      byte[] arrayOfByte = new byte[4];
      agentAddr = handleMultipleIpVersion(arrayOfByte);
    }
    if (paramInetAddress != null) {
      sendTrapPdu(paramInetAddress, localSnmpPduTrap);
    } else {
      sendTrapPdu(localSnmpPduTrap);
    }
  }
  
  public void snmpV1Trap(InetAddress paramInetAddress, SnmpIpAddress paramSnmpIpAddress, String paramString, SnmpOid paramSnmpOid, int paramInt1, int paramInt2, SnmpVarBindList paramSnmpVarBindList, SnmpTimeticks paramSnmpTimeticks)
    throws IOException, SnmpStatusException
  {
    snmpV1Trap(paramInetAddress, trapPort, paramSnmpIpAddress, paramString, paramSnmpOid, paramInt1, paramInt2, paramSnmpVarBindList, paramSnmpTimeticks);
  }
  
  public void snmpV1Trap(SnmpPeer paramSnmpPeer, SnmpIpAddress paramSnmpIpAddress, SnmpOid paramSnmpOid, int paramInt1, int paramInt2, SnmpVarBindList paramSnmpVarBindList, SnmpTimeticks paramSnmpTimeticks)
    throws IOException, SnmpStatusException
  {
    SnmpParameters localSnmpParameters = (SnmpParameters)paramSnmpPeer.getParams();
    snmpV1Trap(paramSnmpPeer.getDestAddr(), paramSnmpPeer.getDestPort(), paramSnmpIpAddress, localSnmpParameters.getRdCommunity(), paramSnmpOid, paramInt1, paramInt2, paramSnmpVarBindList, paramSnmpTimeticks);
  }
  
  private void snmpV1Trap(InetAddress paramInetAddress, int paramInt1, SnmpIpAddress paramSnmpIpAddress, String paramString, SnmpOid paramSnmpOid, int paramInt2, int paramInt3, SnmpVarBindList paramSnmpVarBindList, SnmpTimeticks paramSnmpTimeticks)
    throws IOException, SnmpStatusException
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "snmpV1Trap", "generic=" + paramInt2 + ", specific=" + paramInt3);
    }
    SnmpPduTrap localSnmpPduTrap = new SnmpPduTrap();
    address = null;
    port = paramInt1;
    type = 164;
    version = 0;
    if (paramString != null) {
      community = paramString.getBytes();
    } else {
      community = null;
    }
    if (paramSnmpOid != null) {
      enterprise = paramSnmpOid;
    } else {
      enterprise = enterpriseOid;
    }
    genericTrap = paramInt2;
    specificTrap = paramInt3;
    if (paramSnmpTimeticks != null) {
      timeStamp = paramSnmpTimeticks.longValue();
    } else {
      timeStamp = getSysUpTime();
    }
    if (paramSnmpVarBindList != null)
    {
      varBindList = new SnmpVarBind[paramSnmpVarBindList.size()];
      paramSnmpVarBindList.copyInto(varBindList);
    }
    else
    {
      varBindList = null;
    }
    if (paramSnmpIpAddress == null) {
      try
      {
        InetAddress localInetAddress = address != null ? address : InetAddress.getLocalHost();
        paramSnmpIpAddress = handleMultipleIpVersion(localInetAddress.getAddress());
      }
      catch (UnknownHostException localUnknownHostException)
      {
        byte[] arrayOfByte = new byte[4];
        paramSnmpIpAddress = handleMultipleIpVersion(arrayOfByte);
      }
    }
    agentAddr = paramSnmpIpAddress;
    if (paramInetAddress != null) {
      sendTrapPdu(paramInetAddress, localSnmpPduTrap);
    } else {
      sendTrapPdu(localSnmpPduTrap);
    }
  }
  
  public void snmpV2Trap(SnmpPeer paramSnmpPeer, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList, SnmpTimeticks paramSnmpTimeticks)
    throws IOException, SnmpStatusException
  {
    SnmpParameters localSnmpParameters = (SnmpParameters)paramSnmpPeer.getParams();
    snmpV2Trap(paramSnmpPeer.getDestAddr(), paramSnmpPeer.getDestPort(), localSnmpParameters.getRdCommunity(), paramSnmpOid, paramSnmpVarBindList, paramSnmpTimeticks);
  }
  
  public void snmpV2Trap(SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList)
    throws IOException, SnmpStatusException
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "snmpV2Trap", "trapOid=" + paramSnmpOid);
    }
    SnmpPduRequest localSnmpPduRequest = new SnmpPduRequest();
    address = null;
    port = trapPort;
    type = 167;
    version = 1;
    community = null;
    SnmpVarBindList localSnmpVarBindList;
    if (paramSnmpVarBindList != null) {
      localSnmpVarBindList = paramSnmpVarBindList.clone();
    } else {
      localSnmpVarBindList = new SnmpVarBindList(2);
    }
    SnmpTimeticks localSnmpTimeticks = new SnmpTimeticks(getSysUpTime());
    localSnmpVarBindList.insertElementAt(new SnmpVarBind(snmpTrapOidOid, paramSnmpOid), 0);
    localSnmpVarBindList.insertElementAt(new SnmpVarBind(sysUpTimeOid, localSnmpTimeticks), 0);
    varBindList = new SnmpVarBind[localSnmpVarBindList.size()];
    localSnmpVarBindList.copyInto(varBindList);
    sendTrapPdu(localSnmpPduRequest);
  }
  
  public void snmpV2Trap(InetAddress paramInetAddress, String paramString, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList)
    throws IOException, SnmpStatusException
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "snmpV2Trap", "trapOid=" + paramSnmpOid);
    }
    SnmpPduRequest localSnmpPduRequest = new SnmpPduRequest();
    address = null;
    port = trapPort;
    type = 167;
    version = 1;
    if (paramString != null) {
      community = paramString.getBytes();
    } else {
      community = null;
    }
    SnmpVarBindList localSnmpVarBindList;
    if (paramSnmpVarBindList != null) {
      localSnmpVarBindList = paramSnmpVarBindList.clone();
    } else {
      localSnmpVarBindList = new SnmpVarBindList(2);
    }
    SnmpTimeticks localSnmpTimeticks = new SnmpTimeticks(getSysUpTime());
    localSnmpVarBindList.insertElementAt(new SnmpVarBind(snmpTrapOidOid, paramSnmpOid), 0);
    localSnmpVarBindList.insertElementAt(new SnmpVarBind(sysUpTimeOid, localSnmpTimeticks), 0);
    varBindList = new SnmpVarBind[localSnmpVarBindList.size()];
    localSnmpVarBindList.copyInto(varBindList);
    if (paramInetAddress != null) {
      sendTrapPdu(paramInetAddress, localSnmpPduRequest);
    } else {
      sendTrapPdu(localSnmpPduRequest);
    }
  }
  
  public void snmpV2Trap(InetAddress paramInetAddress, String paramString, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList, SnmpTimeticks paramSnmpTimeticks)
    throws IOException, SnmpStatusException
  {
    snmpV2Trap(paramInetAddress, trapPort, paramString, paramSnmpOid, paramSnmpVarBindList, paramSnmpTimeticks);
  }
  
  private void snmpV2Trap(InetAddress paramInetAddress, int paramInt, String paramString, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList, SnmpTimeticks paramSnmpTimeticks)
    throws IOException, SnmpStatusException
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
    {
      localObject = new StringBuilder().append("trapOid=").append(paramSnmpOid).append("\ncommunity=").append(paramString).append("\naddr=").append(paramInetAddress).append("\nvarBindList=").append(paramSnmpVarBindList).append("\ntime=").append(paramSnmpTimeticks).append("\ntrapPort=").append(paramInt);
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "snmpV2Trap", ((StringBuilder)localObject).toString());
    }
    Object localObject = new SnmpPduRequest();
    address = null;
    port = paramInt;
    type = 167;
    version = 1;
    if (paramString != null) {
      community = paramString.getBytes();
    } else {
      community = null;
    }
    SnmpVarBindList localSnmpVarBindList;
    if (paramSnmpVarBindList != null) {
      localSnmpVarBindList = paramSnmpVarBindList.clone();
    } else {
      localSnmpVarBindList = new SnmpVarBindList(2);
    }
    SnmpTimeticks localSnmpTimeticks;
    if (paramSnmpTimeticks != null) {
      localSnmpTimeticks = paramSnmpTimeticks;
    } else {
      localSnmpTimeticks = new SnmpTimeticks(getSysUpTime());
    }
    localSnmpVarBindList.insertElementAt(new SnmpVarBind(snmpTrapOidOid, paramSnmpOid), 0);
    localSnmpVarBindList.insertElementAt(new SnmpVarBind(sysUpTimeOid, localSnmpTimeticks), 0);
    varBindList = new SnmpVarBind[localSnmpVarBindList.size()];
    localSnmpVarBindList.copyInto(varBindList);
    if (paramInetAddress != null) {
      sendTrapPdu(paramInetAddress, (SnmpPduPacket)localObject);
    } else {
      sendTrapPdu((SnmpPduPacket)localObject);
    }
  }
  
  public void snmpPduTrap(InetAddress paramInetAddress, SnmpPduPacket paramSnmpPduPacket)
    throws IOException, SnmpStatusException
  {
    if (paramInetAddress != null) {
      sendTrapPdu(paramInetAddress, paramSnmpPduPacket);
    } else {
      sendTrapPdu(paramSnmpPduPacket);
    }
  }
  
  public void snmpPduTrap(SnmpPeer paramSnmpPeer, SnmpPduPacket paramSnmpPduPacket)
    throws IOException, SnmpStatusException
  {
    if (paramSnmpPeer != null)
    {
      port = paramSnmpPeer.getDestPort();
      sendTrapPdu(paramSnmpPeer.getDestAddr(), paramSnmpPduPacket);
    }
    else
    {
      port = getTrapPort().intValue();
      sendTrapPdu(paramSnmpPduPacket);
    }
  }
  
  private void sendTrapPdu(SnmpPduPacket paramSnmpPduPacket)
    throws SnmpStatusException, IOException
  {
    SnmpMessage localSnmpMessage = null;
    try
    {
      localSnmpMessage = (SnmpMessage)pduFactory.encodeSnmpPdu(paramSnmpPduPacket, bufferSize);
      if (localSnmpMessage == null) {
        throw new SnmpStatusException(16);
      }
    }
    catch (SnmpTooBigException localSnmpTooBigException1)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent to anyone");
      }
      throw new SnmpStatusException(1);
    }
    int i = 0;
    openTrapSocketIfNeeded();
    if (ipacl != null)
    {
      Enumeration localEnumeration1 = ipacl.getTrapDestinations();
      while (localEnumeration1.hasMoreElements())
      {
        address = ((InetAddress)localEnumeration1.nextElement());
        Enumeration localEnumeration2 = ipacl.getTrapCommunities(address);
        while (localEnumeration2.hasMoreElements())
        {
          community = ((String)localEnumeration2.nextElement()).getBytes();
          try
          {
            sendTrapMessage(localSnmpMessage);
            i++;
          }
          catch (SnmpTooBigException localSnmpTooBigException3)
          {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
              JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent to " + address);
            }
          }
        }
      }
    }
    if (i == 0) {
      try
      {
        address = InetAddress.getLocalHost();
        sendTrapMessage(localSnmpMessage);
      }
      catch (SnmpTooBigException localSnmpTooBigException2)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent.");
        }
      }
      catch (UnknownHostException localUnknownHostException)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent.");
        }
      }
    }
    closeTrapSocketIfNeeded();
  }
  
  private void sendTrapPdu(InetAddress paramInetAddress, SnmpPduPacket paramSnmpPduPacket)
    throws SnmpStatusException, IOException
  {
    SnmpMessage localSnmpMessage = null;
    try
    {
      localSnmpMessage = (SnmpMessage)pduFactory.encodeSnmpPdu(paramSnmpPduPacket, bufferSize);
      if (localSnmpMessage == null) {
        throw new SnmpStatusException(16);
      }
    }
    catch (SnmpTooBigException localSnmpTooBigException1)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent to the specified host.");
      }
      throw new SnmpStatusException(1);
    }
    openTrapSocketIfNeeded();
    if (paramInetAddress != null)
    {
      address = paramInetAddress;
      try
      {
        sendTrapMessage(localSnmpMessage);
      }
      catch (SnmpTooBigException localSnmpTooBigException2)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent to " + address);
        }
      }
    }
    closeTrapSocketIfNeeded();
  }
  
  private void sendTrapMessage(SnmpMessage paramSnmpMessage)
    throws IOException, SnmpTooBigException
  {
    byte[] arrayOfByte = new byte[bufferSize];
    DatagramPacket localDatagramPacket = new DatagramPacket(arrayOfByte, arrayOfByte.length);
    int i = paramSnmpMessage.encodeMessage(arrayOfByte);
    localDatagramPacket.setLength(i);
    localDatagramPacket.setAddress(address);
    localDatagramPacket.setPort(port);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "sendTrapMessage", "sending trap to " + address + ":" + port);
    }
    trapSocket.send(localDatagramPacket);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "sendTrapMessage", "sent to " + address + ":" + port);
    }
    snmpOutTraps += 1;
    snmpOutPkts += 1;
  }
  
  synchronized void openTrapSocketIfNeeded()
    throws SocketException
  {
    if (trapSocket == null)
    {
      trapSocket = new DatagramSocket(0, address);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "openTrapSocketIfNeeded", "using port " + trapSocket.getLocalPort() + " to send traps");
      }
    }
  }
  
  synchronized void closeTrapSocketIfNeeded()
  {
    if ((trapSocket != null) && (state != 0))
    {
      trapSocket.close();
      trapSocket = null;
    }
  }
  
  public Vector<SnmpInformRequest> snmpInformRequest(SnmpInformHandler paramSnmpInformHandler, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList)
    throws IllegalStateException, IOException, SnmpStatusException
  {
    if (!isActive()) {
      throw new IllegalStateException("Start SNMP adaptor server before carrying out this operation");
    }
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "snmpInformRequest", "trapOid=" + paramSnmpOid);
    }
    SnmpVarBindList localSnmpVarBindList;
    if (paramSnmpVarBindList != null) {
      localSnmpVarBindList = paramSnmpVarBindList.clone();
    } else {
      localSnmpVarBindList = new SnmpVarBindList(2);
    }
    SnmpTimeticks localSnmpTimeticks = new SnmpTimeticks(getSysUpTime());
    localSnmpVarBindList.insertElementAt(new SnmpVarBind(snmpTrapOidOid, paramSnmpOid), 0);
    localSnmpVarBindList.insertElementAt(new SnmpVarBind(sysUpTimeOid, localSnmpTimeticks), 0);
    openInformSocketIfNeeded();
    Vector localVector = new Vector();
    if (ipacl != null)
    {
      Enumeration localEnumeration1 = ipacl.getInformDestinations();
      while (localEnumeration1.hasMoreElements())
      {
        InetAddress localInetAddress = (InetAddress)localEnumeration1.nextElement();
        Enumeration localEnumeration2 = ipacl.getInformCommunities(localInetAddress);
        while (localEnumeration2.hasMoreElements())
        {
          String str = (String)localEnumeration2.nextElement();
          localVector.addElement(informSession.makeAsyncRequest(localInetAddress, str, paramSnmpInformHandler, localSnmpVarBindList, getInformPort()));
        }
      }
    }
    return localVector;
  }
  
  public SnmpInformRequest snmpInformRequest(InetAddress paramInetAddress, String paramString, SnmpInformHandler paramSnmpInformHandler, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList)
    throws IllegalStateException, IOException, SnmpStatusException
  {
    return snmpInformRequest(paramInetAddress, getInformPort(), paramString, paramSnmpInformHandler, paramSnmpOid, paramSnmpVarBindList);
  }
  
  public SnmpInformRequest snmpInformRequest(SnmpPeer paramSnmpPeer, SnmpInformHandler paramSnmpInformHandler, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList)
    throws IllegalStateException, IOException, SnmpStatusException
  {
    SnmpParameters localSnmpParameters = (SnmpParameters)paramSnmpPeer.getParams();
    return snmpInformRequest(paramSnmpPeer.getDestAddr(), paramSnmpPeer.getDestPort(), localSnmpParameters.getInformCommunity(), paramSnmpInformHandler, paramSnmpOid, paramSnmpVarBindList);
  }
  
  public static int mapErrorStatus(int paramInt1, int paramInt2, int paramInt3)
  {
    return SnmpSubRequestHandler.mapErrorStatus(paramInt1, paramInt2, paramInt3);
  }
  
  private SnmpInformRequest snmpInformRequest(InetAddress paramInetAddress, int paramInt, String paramString, SnmpInformHandler paramSnmpInformHandler, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList)
    throws IllegalStateException, IOException, SnmpStatusException
  {
    if (!isActive()) {
      throw new IllegalStateException("Start SNMP adaptor server before carrying out this operation");
    }
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "snmpInformRequest", "trapOid=" + paramSnmpOid);
    }
    SnmpVarBindList localSnmpVarBindList;
    if (paramSnmpVarBindList != null) {
      localSnmpVarBindList = paramSnmpVarBindList.clone();
    } else {
      localSnmpVarBindList = new SnmpVarBindList(2);
    }
    SnmpTimeticks localSnmpTimeticks = new SnmpTimeticks(getSysUpTime());
    localSnmpVarBindList.insertElementAt(new SnmpVarBind(snmpTrapOidOid, paramSnmpOid), 0);
    localSnmpVarBindList.insertElementAt(new SnmpVarBind(sysUpTimeOid, localSnmpTimeticks), 0);
    openInformSocketIfNeeded();
    return informSession.makeAsyncRequest(paramInetAddress, paramString, paramSnmpInformHandler, localSnmpVarBindList, paramInt);
  }
  
  synchronized void openInformSocketIfNeeded()
    throws SocketException
  {
    if (informSession == null)
    {
      informSession = new SnmpSession(this);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "openInformSocketIfNeeded", "to send inform requests and receive inform responses");
      }
    }
  }
  
  synchronized void closeInformSocketIfNeeded()
  {
    if ((informSession != null) && (state != 0))
    {
      informSession.destroySession();
      informSession = null;
    }
  }
  
  InetAddress getAddress()
  {
    return address;
  }
  
  protected void finalize()
  {
    try
    {
      if (socket != null)
      {
        socket.close();
        socket = null;
      }
      threadService.terminate();
    }
    catch (Exception localException)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "finalize", "Exception in finalizer", localException);
      }
    }
  }
  
  String makeDebugTag()
  {
    return "SnmpAdaptorServer[" + getProtocol() + ":" + getPort() + "]";
  }
  
  void updateRequestCounters(int paramInt)
  {
    switch (paramInt)
    {
    case 160: 
      snmpInGetRequests += 1;
      break;
    case 161: 
      snmpInGetNexts += 1;
      break;
    case 163: 
      snmpInSetRequests += 1;
      break;
    }
    snmpInPkts += 1;
  }
  
  void updateErrorCounters(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      snmpOutGetResponses += 1;
      break;
    case 5: 
      snmpOutGenErrs += 1;
      break;
    case 3: 
      snmpOutBadValues += 1;
      break;
    case 2: 
      snmpOutNoSuchNames += 1;
      break;
    case 1: 
      snmpOutTooBigs += 1;
      break;
    }
    snmpOutPkts += 1;
  }
  
  void updateVarCounters(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    case 160: 
    case 161: 
    case 165: 
      snmpInTotalReqVars += paramInt2;
      break;
    case 163: 
      snmpInTotalSetVars += paramInt2;
    }
  }
  
  void incSnmpInASNParseErrs(int paramInt)
  {
    snmpInASNParseErrs += paramInt;
  }
  
  void incSnmpInBadVersions(int paramInt)
  {
    snmpInBadVersions += paramInt;
  }
  
  void incSnmpInBadCommunityUses(int paramInt)
  {
    snmpInBadCommunityUses += paramInt;
  }
  
  void incSnmpInBadCommunityNames(int paramInt)
  {
    snmpInBadCommunityNames += paramInt;
  }
  
  void incSnmpSilentDrops(int paramInt)
  {
    snmpSilentDrops += paramInt;
  }
  
  long getSysUpTime()
  {
    return (System.currentTimeMillis() - startUpTime) / 10L;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    mibs = new Vector();
  }
  
  private void init(InetAddressAcl paramInetAddressAcl, int paramInt, InetAddress paramInetAddress)
  {
    root = new SnmpMibTree();
    root.setDefaultAgent(new SnmpErrorHandlerAgent());
    startUpTime = System.currentTimeMillis();
    maxActiveClientCount = 10;
    pduFactory = new SnmpPduFactoryBER();
    port = paramInt;
    ipacl = paramInetAddressAcl;
    address = paramInetAddress;
    if ((ipacl == null) && (useAcl == true)) {
      throw new IllegalArgumentException("ACL object cannot be null");
    }
    threadService = new ThreadService(threadNumber);
  }
  
  SnmpMibAgent getAgentMib(SnmpOid paramSnmpOid)
  {
    return root.getAgentMib(paramSnmpOid);
  }
  
  protected Thread createMainThread()
  {
    Thread localThread = super.createMainThread();
    localThread.setDaemon(true);
    return localThread;
  }
  
  static
  {
    String str = System.getProperty("com.sun.jmx.snmp.threadnumber");
    if (str != null) {
      try
      {
        threadNumber = Integer.parseInt(System.getProperty(str));
      }
      catch (Exception localException)
      {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpAdaptorServer.class.getName(), "<static init>", "Got wrong value for com.sun.jmx.snmp.threadnumber: " + str + ". Use the default value: " + threadNumber);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\SnmpAdaptorServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */