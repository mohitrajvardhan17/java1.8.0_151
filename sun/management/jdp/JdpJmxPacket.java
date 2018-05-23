package sun.management.jdp;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class JdpJmxPacket
  extends JdpGenericPacket
  implements JdpPacket
{
  public static final String UUID_KEY = "DISCOVERABLE_SESSION_UUID";
  public static final String MAIN_CLASS_KEY = "MAIN_CLASS";
  public static final String JMX_SERVICE_URL_KEY = "JMX_SERVICE_URL";
  public static final String INSTANCE_NAME_KEY = "INSTANCE_NAME";
  public static final String PROCESS_ID_KEY = "PROCESS_ID";
  public static final String RMI_HOSTNAME_KEY = "RMI_HOSTNAME";
  public static final String BROADCAST_INTERVAL_KEY = "BROADCAST_INTERVAL";
  private UUID id;
  private String mainClass;
  private String jmxServiceUrl;
  private String instanceName;
  private String processId;
  private String rmiHostname;
  private String broadcastInterval;
  
  public JdpJmxPacket(UUID paramUUID, String paramString)
  {
    id = paramUUID;
    jmxServiceUrl = paramString;
  }
  
  public JdpJmxPacket(byte[] paramArrayOfByte)
    throws JdpException
  {
    JdpPacketReader localJdpPacketReader = new JdpPacketReader(paramArrayOfByte);
    Map localMap = localJdpPacketReader.getDiscoveryDataAsMap();
    String str = (String)localMap.get("DISCOVERABLE_SESSION_UUID");
    id = (str == null ? null : UUID.fromString(str));
    jmxServiceUrl = ((String)localMap.get("JMX_SERVICE_URL"));
    mainClass = ((String)localMap.get("MAIN_CLASS"));
    instanceName = ((String)localMap.get("INSTANCE_NAME"));
    processId = ((String)localMap.get("PROCESS_ID"));
    rmiHostname = ((String)localMap.get("RMI_HOSTNAME"));
    broadcastInterval = ((String)localMap.get("BROADCAST_INTERVAL"));
  }
  
  public void setMainClass(String paramString)
  {
    mainClass = paramString;
  }
  
  public void setInstanceName(String paramString)
  {
    instanceName = paramString;
  }
  
  public UUID getId()
  {
    return id;
  }
  
  public String getMainClass()
  {
    return mainClass;
  }
  
  public String getJmxServiceUrl()
  {
    return jmxServiceUrl;
  }
  
  public String getInstanceName()
  {
    return instanceName;
  }
  
  public String getProcessId()
  {
    return processId;
  }
  
  public void setProcessId(String paramString)
  {
    processId = paramString;
  }
  
  public String getRmiHostname()
  {
    return rmiHostname;
  }
  
  public void setRmiHostname(String paramString)
  {
    rmiHostname = paramString;
  }
  
  public String getBroadcastInterval()
  {
    return broadcastInterval;
  }
  
  public void setBroadcastInterval(String paramString)
  {
    broadcastInterval = paramString;
  }
  
  public byte[] getPacketData()
    throws IOException
  {
    JdpPacketWriter localJdpPacketWriter = new JdpPacketWriter();
    localJdpPacketWriter.addEntry("DISCOVERABLE_SESSION_UUID", id == null ? null : id.toString());
    localJdpPacketWriter.addEntry("MAIN_CLASS", mainClass);
    localJdpPacketWriter.addEntry("JMX_SERVICE_URL", jmxServiceUrl);
    localJdpPacketWriter.addEntry("INSTANCE_NAME", instanceName);
    localJdpPacketWriter.addEntry("PROCESS_ID", processId);
    localJdpPacketWriter.addEntry("RMI_HOSTNAME", rmiHostname);
    localJdpPacketWriter.addEntry("BROADCAST_INTERVAL", broadcastInterval);
    return localJdpPacketWriter.getPacketBytes();
  }
  
  public int hashCode()
  {
    int i = 1;
    i = i * 31 + id.hashCode();
    i = i * 31 + jmxServiceUrl.hashCode();
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof JdpJmxPacket))) {
      return false;
    }
    JdpJmxPacket localJdpJmxPacket = (JdpJmxPacket)paramObject;
    return (Objects.equals(id, localJdpJmxPacket.getId())) && (Objects.equals(jmxServiceUrl, localJdpJmxPacket.getJmxServiceUrl()));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\jdp\JdpJmxPacket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */