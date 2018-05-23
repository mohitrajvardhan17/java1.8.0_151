package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.internal.SnmpIncomingRequest;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

class SnmpSubNextRequestHandler
  extends SnmpSubRequestHandler
{
  private SnmpAdaptorServer server = null;
  
  protected SnmpSubNextRequestHandler(SnmpAdaptorServer paramSnmpAdaptorServer, SnmpMibAgent paramSnmpMibAgent, SnmpPdu paramSnmpPdu)
  {
    super(paramSnmpMibAgent, paramSnmpPdu);
    init(paramSnmpPdu, paramSnmpAdaptorServer);
  }
  
  protected SnmpSubNextRequestHandler(SnmpEngine paramSnmpEngine, SnmpAdaptorServer paramSnmpAdaptorServer, SnmpIncomingRequest paramSnmpIncomingRequest, SnmpMibAgent paramSnmpMibAgent, SnmpPdu paramSnmpPdu)
  {
    super(paramSnmpEngine, paramSnmpIncomingRequest, paramSnmpMibAgent, paramSnmpPdu);
    init(paramSnmpPdu, paramSnmpAdaptorServer);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubNextRequestHandler.class.getName(), "SnmpSubNextRequestHandler", "Constructor : " + this);
    }
  }
  
  private void init(SnmpPdu paramSnmpPdu, SnmpAdaptorServer paramSnmpAdaptorServer)
  {
    server = paramSnmpAdaptorServer;
    int i = translation.length;
    SnmpVarBind[] arrayOfSnmpVarBind = varBindList;
    SnmpSubRequestHandler.NonSyncVector localNonSyncVector = (SnmpSubRequestHandler.NonSyncVector)varBind;
    for (int j = 0; j < i; j++)
    {
      translation[j] = j;
      SnmpVarBind localSnmpVarBind = new SnmpVarBind(oid, value);
      localNonSyncVector.addNonSyncElement(localSnmpVarBind);
    }
  }
  
  /* Error */
  public void run()
  {
    // Byte code:
    //   0: ldc 8
    //   2: aload_0
    //   3: getfield 207	com/sun/jmx/snmp/daemon/SnmpSubNextRequestHandler:data	Ljava/lang/Object;
    //   6: invokestatic 216	com/sun/jmx/snmp/ThreadContext:push	(Ljava/lang/String;Ljava/lang/Object;)Lcom/sun/jmx/snmp/ThreadContext;
    //   9: astore_1
    //   10: getstatic 197	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   13: getstatic 209	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   16: invokevirtual 234	java/util/logging/Logger:isLoggable	(Ljava/util/logging/Level;)Z
    //   19: ifeq +55 -> 74
    //   22: getstatic 197	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   25: getstatic 209	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   28: ldc 20
    //   30: invokevirtual 226	java/lang/Class:getName	()Ljava/lang/String;
    //   33: ldc 15
    //   35: new 121	java/lang/StringBuilder
    //   38: dup
    //   39: invokespecial 227	java/lang/StringBuilder:<init>	()V
    //   42: ldc 10
    //   44: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   47: invokestatic 231	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   50: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   53: ldc 13
    //   55: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   58: aload_0
    //   59: getfield 205	com/sun/jmx/snmp/daemon/SnmpSubNextRequestHandler:agent	Lcom/sun/jmx/snmp/agent/SnmpMibAgent;
    //   62: invokevirtual 218	com/sun/jmx/snmp/agent/SnmpMibAgent:getMibName	()Ljava/lang/String;
    //   65: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   68: invokevirtual 228	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   71: invokevirtual 235	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   74: aload_0
    //   75: getfield 205	com/sun/jmx/snmp/daemon/SnmpSubNextRequestHandler:agent	Lcom/sun/jmx/snmp/agent/SnmpMibAgent;
    //   78: aload_0
    //   79: aload_0
    //   80: getfield 208	com/sun/jmx/snmp/daemon/SnmpSubNextRequestHandler:varBind	Ljava/util/Vector;
    //   83: iconst_1
    //   84: aload_0
    //   85: getfield 207	com/sun/jmx/snmp/daemon/SnmpSubNextRequestHandler:data	Ljava/lang/Object;
    //   88: invokevirtual 221	com/sun/jmx/snmp/daemon/SnmpSubNextRequestHandler:createMibRequest	(Ljava/util/Vector;ILjava/lang/Object;)Lcom/sun/jmx/snmp/agent/SnmpMibRequest;
    //   91: invokevirtual 217	com/sun/jmx/snmp/agent/SnmpMibAgent:getNext	(Lcom/sun/jmx/snmp/agent/SnmpMibRequest;)V
    //   94: aload_1
    //   95: invokestatic 215	com/sun/jmx/snmp/ThreadContext:restore	(Lcom/sun/jmx/snmp/ThreadContext;)V
    //   98: goto +10 -> 108
    //   101: astore_2
    //   102: aload_1
    //   103: invokestatic 215	com/sun/jmx/snmp/ThreadContext:restore	(Lcom/sun/jmx/snmp/ThreadContext;)V
    //   106: aload_2
    //   107: athrow
    //   108: goto +139 -> 247
    //   111: astore_1
    //   112: aload_0
    //   113: aload_1
    //   114: invokevirtual 213	com/sun/jmx/snmp/SnmpStatusException:getStatus	()I
    //   117: putfield 203	com/sun/jmx/snmp/daemon/SnmpSubNextRequestHandler:errorStatus	I
    //   120: aload_0
    //   121: aload_1
    //   122: invokevirtual 212	com/sun/jmx/snmp/SnmpStatusException:getErrorIndex	()I
    //   125: putfield 202	com/sun/jmx/snmp/daemon/SnmpSubNextRequestHandler:errorIndex	I
    //   128: getstatic 197	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   131: getstatic 210	java/util/logging/Level:FINEST	Ljava/util/logging/Level;
    //   134: invokevirtual 234	java/util/logging/Logger:isLoggable	(Ljava/util/logging/Level;)Z
    //   137: ifeq +46 -> 183
    //   140: getstatic 197	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   143: getstatic 210	java/util/logging/Level:FINEST	Ljava/util/logging/Level;
    //   146: ldc 20
    //   148: invokevirtual 226	java/lang/Class:getName	()Ljava/lang/String;
    //   151: ldc 15
    //   153: new 121	java/lang/StringBuilder
    //   156: dup
    //   157: invokespecial 227	java/lang/StringBuilder:<init>	()V
    //   160: ldc 10
    //   162: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   165: invokestatic 231	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   168: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   171: ldc 12
    //   173: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   176: invokevirtual 228	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   179: aload_1
    //   180: invokevirtual 236	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   183: goto +64 -> 247
    //   186: astore_1
    //   187: aload_0
    //   188: iconst_5
    //   189: putfield 203	com/sun/jmx/snmp/daemon/SnmpSubNextRequestHandler:errorStatus	I
    //   192: getstatic 197	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   195: getstatic 210	java/util/logging/Level:FINEST	Ljava/util/logging/Level;
    //   198: invokevirtual 234	java/util/logging/Logger:isLoggable	(Ljava/util/logging/Level;)Z
    //   201: ifeq +46 -> 247
    //   204: getstatic 197	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   207: getstatic 210	java/util/logging/Level:FINEST	Ljava/util/logging/Level;
    //   210: ldc 20
    //   212: invokevirtual 226	java/lang/Class:getName	()Ljava/lang/String;
    //   215: ldc 15
    //   217: new 121	java/lang/StringBuilder
    //   220: dup
    //   221: invokespecial 227	java/lang/StringBuilder:<init>	()V
    //   224: ldc 10
    //   226: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   229: invokestatic 231	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   232: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   235: ldc 11
    //   237: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   240: invokevirtual 228	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   243: aload_1
    //   244: invokevirtual 236	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   247: getstatic 197	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   250: getstatic 209	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   253: invokevirtual 234	java/util/logging/Logger:isLoggable	(Ljava/util/logging/Level;)Z
    //   256: ifeq +45 -> 301
    //   259: getstatic 197	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   262: getstatic 209	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   265: ldc 20
    //   267: invokevirtual 226	java/lang/Class:getName	()Ljava/lang/String;
    //   270: ldc 15
    //   272: new 121	java/lang/StringBuilder
    //   275: dup
    //   276: invokespecial 227	java/lang/StringBuilder:<init>	()V
    //   279: ldc 10
    //   281: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   284: invokestatic 231	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   287: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   290: ldc 14
    //   292: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   295: invokevirtual 228	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   298: invokevirtual 235	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   301: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	302	0	this	SnmpSubNextRequestHandler
    //   9	94	1	localThreadContext	com.sun.jmx.snmp.ThreadContext
    //   111	69	1	localSnmpStatusException	com.sun.jmx.snmp.SnmpStatusException
    //   186	58	1	localException	Exception
    //   101	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   10	94	101	finally
    //   0	108	111	com/sun/jmx/snmp/SnmpStatusException
    //   0	108	186	java/lang/Exception
  }
  
  protected void updateRequest(SnmpVarBind paramSnmpVarBind, int paramInt)
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "updateRequest", "Copy :" + paramSnmpVarBind);
    }
    int i = varBind.size();
    translation[i] = paramInt;
    SnmpVarBind localSnmpVarBind = new SnmpVarBind(oid, value);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "updateRequest", "Copied :" + localSnmpVarBind);
    }
    varBind.addElement(localSnmpVarBind);
  }
  
  protected void updateResult(SnmpVarBind[] paramArrayOfSnmpVarBind)
  {
    int i = varBind.size();
    for (int j = 0; j < i; j++)
    {
      int k = translation[j];
      SnmpVarBind localSnmpVarBind1 = (SnmpVarBind)((SnmpSubRequestHandler.NonSyncVector)varBind).elementAtNonSync(j);
      SnmpVarBind localSnmpVarBind2 = paramArrayOfSnmpVarBind[k];
      if (localSnmpVarBind2 == null)
      {
        paramArrayOfSnmpVarBind[k] = localSnmpVarBind1;
      }
      else
      {
        SnmpValue localSnmpValue = value;
        if ((localSnmpValue == null) || (localSnmpValue == SnmpVarBind.endOfMibView))
        {
          if ((localSnmpVarBind1 != null) && (value != SnmpVarBind.endOfMibView)) {
            paramArrayOfSnmpVarBind[k] = localSnmpVarBind1;
          }
        }
        else if ((localSnmpVarBind1 != null) && (value != SnmpVarBind.endOfMibView))
        {
          int m = oid.compareTo(oid);
          if (m < 0)
          {
            paramArrayOfSnmpVarBind[k] = localSnmpVarBind1;
          }
          else if (m == 0)
          {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
            {
              JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", " oid overlapping. Oid : " + oid + "value :" + value);
              JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", "Already present varBind : " + localSnmpVarBind2);
            }
            SnmpOid localSnmpOid = oid;
            SnmpMibAgent localSnmpMibAgent = server.getAgentMib(localSnmpOid);
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
              JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", "Deeper agent : " + localSnmpMibAgent);
            }
            if (localSnmpMibAgent == agent)
            {
              if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", "The current agent is the deeper one. Update the value with the current one");
              }
              value = value;
            }
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\SnmpSubNextRequestHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */