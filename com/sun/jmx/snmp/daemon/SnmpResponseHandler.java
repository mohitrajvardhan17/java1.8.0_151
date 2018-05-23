package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpMessage;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpPduPacket;
import com.sun.jmx.snmp.SnmpPduRequest;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

class SnmpResponseHandler
{
  SnmpAdaptorServer adaptor = null;
  SnmpQManager snmpq = null;
  
  public SnmpResponseHandler(SnmpAdaptorServer paramSnmpAdaptorServer, SnmpQManager paramSnmpQManager)
  {
    adaptor = paramSnmpAdaptorServer;
    snmpq = paramSnmpQManager;
  }
  
  public synchronized void processDatagram(DatagramPacket paramDatagramPacket)
  {
    byte[] arrayOfByte = paramDatagramPacket.getData();
    int i = paramDatagramPacket.getLength();
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpResponseHandler.class.getName(), "action", "processDatagram", "Received from " + paramDatagramPacket.getAddress().toString() + " Length = " + i + "\nDump : \n" + SnmpMessage.dumpHexBuffer(arrayOfByte, 0, i));
    }
    try
    {
      SnmpMessage localSnmpMessage = new SnmpMessage();
      localSnmpMessage.decodeMessage(arrayOfByte, i);
      address = paramDatagramPacket.getAddress();
      port = paramDatagramPacket.getPort();
      SnmpPduFactory localSnmpPduFactory = adaptor.getPduFactory();
      if (localSnmpPduFactory == null)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(), "processDatagram", "Dropping packet. Unable to find the pdu factory of the SNMP adaptor server");
        }
      }
      else
      {
        SnmpPduPacket localSnmpPduPacket = (SnmpPduPacket)localSnmpPduFactory.decodeSnmpPdu(localSnmpMessage);
        if (localSnmpPduPacket == null)
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(), "processDatagram", "Dropping packet. Pdu factory returned a null value");
          }
        }
        else if ((localSnmpPduPacket instanceof SnmpPduRequest))
        {
          SnmpPduRequest localSnmpPduRequest = (SnmpPduRequest)localSnmpPduPacket;
          SnmpInformRequest localSnmpInformRequest = snmpq.removeRequest(requestId);
          if (localSnmpInformRequest != null) {
            localSnmpInformRequest.invokeOnResponse(localSnmpPduRequest);
          } else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(), "processDatagram", "Dropping packet. Unable to find corresponding for InformRequestId = " + requestId);
          }
        }
        else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(), "processDatagram", "Dropping packet. The packet does not contain an inform response");
        }
        localSnmpPduPacket = null;
      }
    }
    catch (Exception localException)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(), "processDatagram", "Exception while processsing", localException);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\SnmpResponseHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */