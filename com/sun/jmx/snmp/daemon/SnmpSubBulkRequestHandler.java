package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.internal.SnmpIncomingRequest;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

class SnmpSubBulkRequestHandler
  extends SnmpSubRequestHandler
{
  private SnmpAdaptorServer server = null;
  protected int nonRepeat = 0;
  protected int maxRepeat = 0;
  protected int globalR = 0;
  protected int size = 0;
  
  protected SnmpSubBulkRequestHandler(SnmpEngine paramSnmpEngine, SnmpAdaptorServer paramSnmpAdaptorServer, SnmpIncomingRequest paramSnmpIncomingRequest, SnmpMibAgent paramSnmpMibAgent, SnmpPdu paramSnmpPdu, int paramInt1, int paramInt2, int paramInt3)
  {
    super(paramSnmpEngine, paramSnmpIncomingRequest, paramSnmpMibAgent, paramSnmpPdu);
    init(paramSnmpAdaptorServer, paramSnmpPdu, paramInt1, paramInt2, paramInt3);
  }
  
  protected SnmpSubBulkRequestHandler(SnmpAdaptorServer paramSnmpAdaptorServer, SnmpMibAgent paramSnmpMibAgent, SnmpPdu paramSnmpPdu, int paramInt1, int paramInt2, int paramInt3)
  {
    super(paramSnmpMibAgent, paramSnmpPdu);
    init(paramSnmpAdaptorServer, paramSnmpPdu, paramInt1, paramInt2, paramInt3);
  }
  
  /* Error */
  public void run()
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_0
    //   2: getfield 241	com/sun/jmx/snmp/daemon/SnmpSubBulkRequestHandler:varBind	Ljava/util/Vector;
    //   5: invokevirtual 266	java/util/Vector:size	()I
    //   8: putfield 235	com/sun/jmx/snmp/daemon/SnmpSubBulkRequestHandler:size	I
    //   11: ldc 12
    //   13: aload_0
    //   14: getfield 240	com/sun/jmx/snmp/daemon/SnmpSubBulkRequestHandler:data	Ljava/lang/Object;
    //   17: invokestatic 249	com/sun/jmx/snmp/ThreadContext:push	(Ljava/lang/String;Ljava/lang/Object;)Lcom/sun/jmx/snmp/ThreadContext;
    //   20: astore_1
    //   21: getstatic 225	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   24: getstatic 242	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   27: invokevirtual 268	java/util/logging/Logger:isLoggable	(Ljava/util/logging/Level;)Z
    //   30: ifeq +55 -> 85
    //   33: getstatic 225	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   36: getstatic 242	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   39: ldc 26
    //   41: invokevirtual 259	java/lang/Class:getName	()Ljava/lang/String;
    //   44: ldc 23
    //   46: new 139	java/lang/StringBuilder
    //   49: dup
    //   50: invokespecial 260	java/lang/StringBuilder:<init>	()V
    //   53: ldc 17
    //   55: invokevirtual 264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   58: invokestatic 265	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   61: invokevirtual 263	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   64: ldc 20
    //   66: invokevirtual 264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: aload_0
    //   70: getfield 238	com/sun/jmx/snmp/daemon/SnmpSubBulkRequestHandler:agent	Lcom/sun/jmx/snmp/agent/SnmpMibAgent;
    //   73: invokevirtual 251	com/sun/jmx/snmp/agent/SnmpMibAgent:getMibName	()Ljava/lang/String;
    //   76: invokevirtual 264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   79: invokevirtual 261	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   82: invokevirtual 269	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   85: aload_0
    //   86: getfield 238	com/sun/jmx/snmp/daemon/SnmpSubBulkRequestHandler:agent	Lcom/sun/jmx/snmp/agent/SnmpMibAgent;
    //   89: aload_0
    //   90: aload_0
    //   91: getfield 241	com/sun/jmx/snmp/daemon/SnmpSubBulkRequestHandler:varBind	Ljava/util/Vector;
    //   94: aload_0
    //   95: getfield 236	com/sun/jmx/snmp/daemon/SnmpSubBulkRequestHandler:version	I
    //   98: aload_0
    //   99: getfield 240	com/sun/jmx/snmp/daemon/SnmpSubBulkRequestHandler:data	Ljava/lang/Object;
    //   102: invokevirtual 255	com/sun/jmx/snmp/daemon/SnmpSubBulkRequestHandler:createMibRequest	(Ljava/util/Vector;ILjava/lang/Object;)Lcom/sun/jmx/snmp/agent/SnmpMibRequest;
    //   105: aload_0
    //   106: getfield 234	com/sun/jmx/snmp/daemon/SnmpSubBulkRequestHandler:nonRepeat	I
    //   109: aload_0
    //   110: getfield 233	com/sun/jmx/snmp/daemon/SnmpSubBulkRequestHandler:maxRepeat	I
    //   113: invokevirtual 250	com/sun/jmx/snmp/agent/SnmpMibAgent:getBulk	(Lcom/sun/jmx/snmp/agent/SnmpMibRequest;II)V
    //   116: aload_1
    //   117: invokestatic 248	com/sun/jmx/snmp/ThreadContext:restore	(Lcom/sun/jmx/snmp/ThreadContext;)V
    //   120: goto +10 -> 130
    //   123: astore_2
    //   124: aload_1
    //   125: invokestatic 248	com/sun/jmx/snmp/ThreadContext:restore	(Lcom/sun/jmx/snmp/ThreadContext;)V
    //   128: aload_2
    //   129: athrow
    //   130: goto +139 -> 269
    //   133: astore_1
    //   134: aload_0
    //   135: aload_1
    //   136: invokevirtual 246	com/sun/jmx/snmp/SnmpStatusException:getStatus	()I
    //   139: putfield 231	com/sun/jmx/snmp/daemon/SnmpSubBulkRequestHandler:errorStatus	I
    //   142: aload_0
    //   143: aload_1
    //   144: invokevirtual 245	com/sun/jmx/snmp/SnmpStatusException:getErrorIndex	()I
    //   147: putfield 230	com/sun/jmx/snmp/daemon/SnmpSubBulkRequestHandler:errorIndex	I
    //   150: getstatic 225	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   153: getstatic 243	java/util/logging/Level:FINEST	Ljava/util/logging/Level;
    //   156: invokevirtual 268	java/util/logging/Logger:isLoggable	(Ljava/util/logging/Level;)Z
    //   159: ifeq +46 -> 205
    //   162: getstatic 225	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   165: getstatic 243	java/util/logging/Level:FINEST	Ljava/util/logging/Level;
    //   168: ldc 26
    //   170: invokevirtual 259	java/lang/Class:getName	()Ljava/lang/String;
    //   173: ldc 23
    //   175: new 139	java/lang/StringBuilder
    //   178: dup
    //   179: invokespecial 260	java/lang/StringBuilder:<init>	()V
    //   182: ldc 17
    //   184: invokevirtual 264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   187: invokestatic 265	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   190: invokevirtual 263	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   193: ldc 19
    //   195: invokevirtual 264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   198: invokevirtual 261	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   201: aload_1
    //   202: invokevirtual 270	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   205: goto +64 -> 269
    //   208: astore_1
    //   209: aload_0
    //   210: iconst_5
    //   211: putfield 231	com/sun/jmx/snmp/daemon/SnmpSubBulkRequestHandler:errorStatus	I
    //   214: getstatic 225	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   217: getstatic 243	java/util/logging/Level:FINEST	Ljava/util/logging/Level;
    //   220: invokevirtual 268	java/util/logging/Logger:isLoggable	(Ljava/util/logging/Level;)Z
    //   223: ifeq +46 -> 269
    //   226: getstatic 225	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   229: getstatic 243	java/util/logging/Level:FINEST	Ljava/util/logging/Level;
    //   232: ldc 26
    //   234: invokevirtual 259	java/lang/Class:getName	()Ljava/lang/String;
    //   237: ldc 23
    //   239: new 139	java/lang/StringBuilder
    //   242: dup
    //   243: invokespecial 260	java/lang/StringBuilder:<init>	()V
    //   246: ldc 17
    //   248: invokevirtual 264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   251: invokestatic 265	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   254: invokevirtual 263	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   257: ldc 18
    //   259: invokevirtual 264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   262: invokevirtual 261	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   265: aload_1
    //   266: invokevirtual 270	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   269: getstatic 225	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   272: getstatic 242	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   275: invokevirtual 268	java/util/logging/Logger:isLoggable	(Ljava/util/logging/Level;)Z
    //   278: ifeq +45 -> 323
    //   281: getstatic 225	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   284: getstatic 242	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   287: ldc 26
    //   289: invokevirtual 259	java/lang/Class:getName	()Ljava/lang/String;
    //   292: ldc 23
    //   294: new 139	java/lang/StringBuilder
    //   297: dup
    //   298: invokespecial 260	java/lang/StringBuilder:<init>	()V
    //   301: ldc 17
    //   303: invokevirtual 264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   306: invokestatic 265	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   309: invokevirtual 263	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   312: ldc 21
    //   314: invokevirtual 264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   317: invokevirtual 261	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   320: invokevirtual 269	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   323: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	324	0	this	SnmpSubBulkRequestHandler
    //   20	105	1	localThreadContext	com.sun.jmx.snmp.ThreadContext
    //   133	69	1	localSnmpStatusException	com.sun.jmx.snmp.SnmpStatusException
    //   208	58	1	localException	Exception
    //   123	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   21	116	123	finally
    //   11	130	133	com/sun/jmx/snmp/SnmpStatusException
    //   11	130	208	java/lang/Exception
  }
  
  private void init(SnmpAdaptorServer paramSnmpAdaptorServer, SnmpPdu paramSnmpPdu, int paramInt1, int paramInt2, int paramInt3)
  {
    server = paramSnmpAdaptorServer;
    nonRepeat = paramInt1;
    maxRepeat = paramInt2;
    globalR = paramInt3;
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
  
  private SnmpVarBind findVarBind(SnmpVarBind paramSnmpVarBind1, SnmpVarBind paramSnmpVarBind2)
  {
    if (paramSnmpVarBind1 == null) {
      return null;
    }
    if (oid == null) {
      return paramSnmpVarBind1;
    }
    if (value == SnmpVarBind.endOfMibView) {
      return paramSnmpVarBind2;
    }
    if (value == SnmpVarBind.endOfMibView) {
      return paramSnmpVarBind1;
    }
    SnmpValue localSnmpValue = value;
    int i = oid.compareTo(oid);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
    {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "Comparing OID element : " + oid + " with result : " + oid);
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "Values element : " + value + " result : " + value);
    }
    if (i < 0) {
      return paramSnmpVarBind1;
    }
    if (i == 0)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", " oid overlapping. Oid : " + oid + "value :" + value);
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "Already present varBind : " + paramSnmpVarBind2);
      }
      SnmpOid localSnmpOid = oid;
      SnmpMibAgent localSnmpMibAgent = server.getAgentMib(localSnmpOid);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "Deeper agent : " + localSnmpMibAgent);
      }
      if (localSnmpMibAgent == agent)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "The current agent is the deeper one. Update the value with the current one");
        }
        return paramSnmpVarBind1;
      }
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "The current agent is not the deeper one. return the previous one.");
      }
      return paramSnmpVarBind2;
    }
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "The right varBind is the already present one");
    }
    return paramSnmpVarBind2;
  }
  
  protected void updateResult(SnmpVarBind[] paramArrayOfSnmpVarBind)
  {
    Enumeration localEnumeration = varBind.elements();
    int i = paramArrayOfSnmpVarBind.length;
    for (int j = 0; j < size; j++)
    {
      if (!localEnumeration.hasMoreElements()) {
        return;
      }
      k = translation[j];
      if (k >= i)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "updateResult", "Position '" + k + "' is out of bound...");
        }
      }
      else
      {
        SnmpVarBind localSnmpVarBind1 = (SnmpVarBind)localEnumeration.nextElement();
        if (localSnmpVarBind1 != null)
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", "Non repeaters Current element : " + localSnmpVarBind1 + " from agent : " + agent);
          }
          SnmpVarBind localSnmpVarBind2 = findVarBind(localSnmpVarBind1, paramArrayOfSnmpVarBind[k]);
          if (localSnmpVarBind2 != null) {
            paramArrayOfSnmpVarBind[k] = localSnmpVarBind2;
          }
        }
      }
    }
    j = size - nonRepeat;
    for (int k = 2; k <= maxRepeat; k++) {
      for (int m = 0; m < j; m++)
      {
        int n = (k - 1) * globalR + translation[(nonRepeat + m)];
        if (n >= i) {
          return;
        }
        if (!localEnumeration.hasMoreElements()) {
          return;
        }
        SnmpVarBind localSnmpVarBind3 = (SnmpVarBind)localEnumeration.nextElement();
        if (localSnmpVarBind3 != null)
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", "Repeaters Current element : " + localSnmpVarBind3 + " from agent : " + agent);
          }
          SnmpVarBind localSnmpVarBind4 = findVarBind(localSnmpVarBind3, paramArrayOfSnmpVarBind[n]);
          if (localSnmpVarBind4 != null) {
            paramArrayOfSnmpVarBind[n] = localSnmpVarBind4;
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\SnmpSubBulkRequestHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */