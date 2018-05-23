package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.agent.SnmpMibRequest;
import com.sun.jmx.snmp.internal.SnmpIncomingRequest;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

class SnmpSubRequestHandler
  implements SnmpDefinitions, Runnable
{
  protected SnmpIncomingRequest incRequest = null;
  protected SnmpEngine engine = null;
  protected int version = 0;
  protected int type = 0;
  protected SnmpMibAgent agent;
  protected int errorStatus = 0;
  protected int errorIndex = -1;
  protected Vector<SnmpVarBind> varBind;
  protected int[] translation;
  protected Object data;
  private SnmpMibRequest mibRequest = null;
  private SnmpPdu reqPdu = null;
  
  protected SnmpSubRequestHandler(SnmpEngine paramSnmpEngine, SnmpIncomingRequest paramSnmpIncomingRequest, SnmpMibAgent paramSnmpMibAgent, SnmpPdu paramSnmpPdu)
  {
    this(paramSnmpMibAgent, paramSnmpPdu);
    init(paramSnmpEngine, paramSnmpIncomingRequest);
  }
  
  protected SnmpSubRequestHandler(SnmpEngine paramSnmpEngine, SnmpIncomingRequest paramSnmpIncomingRequest, SnmpMibAgent paramSnmpMibAgent, SnmpPdu paramSnmpPdu, boolean paramBoolean)
  {
    this(paramSnmpMibAgent, paramSnmpPdu, paramBoolean);
    init(paramSnmpEngine, paramSnmpIncomingRequest);
  }
  
  protected SnmpSubRequestHandler(SnmpMibAgent paramSnmpMibAgent, SnmpPdu paramSnmpPdu)
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "constructor", "creating instance for request " + String.valueOf(requestId));
    }
    version = version;
    type = type;
    agent = paramSnmpMibAgent;
    reqPdu = paramSnmpPdu;
    int i = varBindList.length;
    translation = new int[i];
    varBind = new NonSyncVector(i);
  }
  
  protected SnmpSubRequestHandler(SnmpMibAgent paramSnmpMibAgent, SnmpPdu paramSnmpPdu, boolean paramBoolean)
  {
    this(paramSnmpMibAgent, paramSnmpPdu);
    int i = translation.length;
    SnmpVarBind[] arrayOfSnmpVarBind = varBindList;
    for (int j = 0; j < i; j++)
    {
      translation[j] = j;
      ((NonSyncVector)varBind).addNonSyncElement(arrayOfSnmpVarBind[j]);
    }
  }
  
  SnmpMibRequest createMibRequest(Vector<SnmpVarBind> paramVector, int paramInt, Object paramObject)
  {
    if ((type == 163) && (mibRequest != null)) {
      return mibRequest;
    }
    SnmpMibRequest localSnmpMibRequest = null;
    if (incRequest != null) {
      localSnmpMibRequest = SnmpMibAgent.newMibRequest(engine, reqPdu, paramVector, paramInt, paramObject, incRequest.getPrincipal(), incRequest.getSecurityLevel(), incRequest.getSecurityModel(), incRequest.getContextName(), incRequest.getAccessContext());
    } else {
      localSnmpMibRequest = SnmpMibAgent.newMibRequest(reqPdu, paramVector, paramInt, paramObject);
    }
    if (type == 253) {
      mibRequest = localSnmpMibRequest;
    }
    return localSnmpMibRequest;
  }
  
  void setUserData(Object paramObject)
  {
    data = paramObject;
  }
  
  /* Error */
  public void run()
  {
    // Byte code:
    //   0: ldc 4
    //   2: aload_0
    //   3: getfield 251	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:data	Ljava/lang/Object;
    //   6: invokestatic 258	com/sun/jmx/snmp/ThreadContext:push	(Ljava/lang/String;Ljava/lang/Object;)Lcom/sun/jmx/snmp/ThreadContext;
    //   9: astore_1
    //   10: aload_0
    //   11: getfield 243	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:type	I
    //   14: lookupswitch	default:+402->416, 160:+42->56, 161:+132->146, 163:+222->236, 253:+312->326
    //   56: getstatic 236	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   59: getstatic 253	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   62: invokevirtual 287	java/util/logging/Logger:isLoggable	(Ljava/util/logging/Level;)Z
    //   65: ifeq +55 -> 120
    //   68: getstatic 236	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   71: getstatic 253	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   74: ldc 18
    //   76: invokevirtual 276	java/lang/Class:getName	()Ljava/lang/String;
    //   79: ldc 16
    //   81: new 141	java/lang/StringBuilder
    //   84: dup
    //   85: invokespecial 279	java/lang/StringBuilder:<init>	()V
    //   88: ldc 5
    //   90: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   93: invokestatic 284	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   96: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   99: ldc 9
    //   101: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   104: aload_0
    //   105: getfield 248	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:agent	Lcom/sun/jmx/snmp/agent/SnmpMibAgent;
    //   108: invokevirtual 263	com/sun/jmx/snmp/agent/SnmpMibAgent:getMibName	()Ljava/lang/String;
    //   111: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   114: invokevirtual 280	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   117: invokevirtual 288	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   120: aload_0
    //   121: getfield 248	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:agent	Lcom/sun/jmx/snmp/agent/SnmpMibAgent;
    //   124: aload_0
    //   125: aload_0
    //   126: getfield 252	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:varBind	Ljava/util/Vector;
    //   129: aload_0
    //   130: getfield 244	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:version	I
    //   133: aload_0
    //   134: getfield 251	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:data	Ljava/lang/Object;
    //   137: invokevirtual 272	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:createMibRequest	(Ljava/util/Vector;ILjava/lang/Object;)Lcom/sun/jmx/snmp/agent/SnmpMibRequest;
    //   140: invokevirtual 260	com/sun/jmx/snmp/agent/SnmpMibAgent:get	(Lcom/sun/jmx/snmp/agent/SnmpMibRequest;)V
    //   143: goto +359 -> 502
    //   146: getstatic 236	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   149: getstatic 253	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   152: invokevirtual 287	java/util/logging/Logger:isLoggable	(Ljava/util/logging/Level;)Z
    //   155: ifeq +55 -> 210
    //   158: getstatic 236	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   161: getstatic 253	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   164: ldc 18
    //   166: invokevirtual 276	java/lang/Class:getName	()Ljava/lang/String;
    //   169: ldc 16
    //   171: new 141	java/lang/StringBuilder
    //   174: dup
    //   175: invokespecial 279	java/lang/StringBuilder:<init>	()V
    //   178: ldc 5
    //   180: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   183: invokestatic 284	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   186: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   189: ldc 10
    //   191: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   194: aload_0
    //   195: getfield 248	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:agent	Lcom/sun/jmx/snmp/agent/SnmpMibAgent;
    //   198: invokevirtual 263	com/sun/jmx/snmp/agent/SnmpMibAgent:getMibName	()Ljava/lang/String;
    //   201: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   204: invokevirtual 280	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   207: invokevirtual 288	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   210: aload_0
    //   211: getfield 248	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:agent	Lcom/sun/jmx/snmp/agent/SnmpMibAgent;
    //   214: aload_0
    //   215: aload_0
    //   216: getfield 252	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:varBind	Ljava/util/Vector;
    //   219: aload_0
    //   220: getfield 244	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:version	I
    //   223: aload_0
    //   224: getfield 251	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:data	Ljava/lang/Object;
    //   227: invokevirtual 272	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:createMibRequest	(Ljava/util/Vector;ILjava/lang/Object;)Lcom/sun/jmx/snmp/agent/SnmpMibRequest;
    //   230: invokevirtual 261	com/sun/jmx/snmp/agent/SnmpMibAgent:getNext	(Lcom/sun/jmx/snmp/agent/SnmpMibRequest;)V
    //   233: goto +269 -> 502
    //   236: getstatic 236	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   239: getstatic 253	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   242: invokevirtual 287	java/util/logging/Logger:isLoggable	(Ljava/util/logging/Level;)Z
    //   245: ifeq +55 -> 300
    //   248: getstatic 236	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   251: getstatic 253	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   254: ldc 18
    //   256: invokevirtual 276	java/lang/Class:getName	()Ljava/lang/String;
    //   259: ldc 16
    //   261: new 141	java/lang/StringBuilder
    //   264: dup
    //   265: invokespecial 279	java/lang/StringBuilder:<init>	()V
    //   268: ldc 5
    //   270: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   273: invokestatic 284	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   276: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   279: ldc 12
    //   281: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   284: aload_0
    //   285: getfield 248	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:agent	Lcom/sun/jmx/snmp/agent/SnmpMibAgent;
    //   288: invokevirtual 263	com/sun/jmx/snmp/agent/SnmpMibAgent:getMibName	()Ljava/lang/String;
    //   291: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   294: invokevirtual 280	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   297: invokevirtual 288	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   300: aload_0
    //   301: getfield 248	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:agent	Lcom/sun/jmx/snmp/agent/SnmpMibAgent;
    //   304: aload_0
    //   305: aload_0
    //   306: getfield 252	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:varBind	Ljava/util/Vector;
    //   309: aload_0
    //   310: getfield 244	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:version	I
    //   313: aload_0
    //   314: getfield 251	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:data	Ljava/lang/Object;
    //   317: invokevirtual 272	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:createMibRequest	(Ljava/util/Vector;ILjava/lang/Object;)Lcom/sun/jmx/snmp/agent/SnmpMibRequest;
    //   320: invokevirtual 262	com/sun/jmx/snmp/agent/SnmpMibAgent:set	(Lcom/sun/jmx/snmp/agent/SnmpMibRequest;)V
    //   323: goto +179 -> 502
    //   326: getstatic 236	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   329: getstatic 253	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   332: invokevirtual 287	java/util/logging/Logger:isLoggable	(Ljava/util/logging/Level;)Z
    //   335: ifeq +55 -> 390
    //   338: getstatic 236	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   341: getstatic 253	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   344: ldc 18
    //   346: invokevirtual 276	java/lang/Class:getName	()Ljava/lang/String;
    //   349: ldc 16
    //   351: new 141	java/lang/StringBuilder
    //   354: dup
    //   355: invokespecial 279	java/lang/StringBuilder:<init>	()V
    //   358: ldc 5
    //   360: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   363: invokestatic 284	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   366: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   369: ldc 8
    //   371: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   374: aload_0
    //   375: getfield 248	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:agent	Lcom/sun/jmx/snmp/agent/SnmpMibAgent;
    //   378: invokevirtual 263	com/sun/jmx/snmp/agent/SnmpMibAgent:getMibName	()Ljava/lang/String;
    //   381: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   384: invokevirtual 280	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   387: invokevirtual 288	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   390: aload_0
    //   391: getfield 248	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:agent	Lcom/sun/jmx/snmp/agent/SnmpMibAgent;
    //   394: aload_0
    //   395: aload_0
    //   396: getfield 252	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:varBind	Ljava/util/Vector;
    //   399: aload_0
    //   400: getfield 244	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:version	I
    //   403: aload_0
    //   404: getfield 251	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:data	Ljava/lang/Object;
    //   407: invokevirtual 272	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:createMibRequest	(Ljava/util/Vector;ILjava/lang/Object;)Lcom/sun/jmx/snmp/agent/SnmpMibRequest;
    //   410: invokevirtual 259	com/sun/jmx/snmp/agent/SnmpMibAgent:check	(Lcom/sun/jmx/snmp/agent/SnmpMibRequest;)V
    //   413: goto +89 -> 502
    //   416: getstatic 236	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   419: getstatic 254	java/util/logging/Level:FINEST	Ljava/util/logging/Level;
    //   422: invokevirtual 287	java/util/logging/Logger:isLoggable	(Ljava/util/logging/Level;)Z
    //   425: ifeq +67 -> 492
    //   428: getstatic 236	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   431: getstatic 254	java/util/logging/Level:FINEST	Ljava/util/logging/Level;
    //   434: ldc 18
    //   436: invokevirtual 276	java/lang/Class:getName	()Ljava/lang/String;
    //   439: ldc 16
    //   441: new 141	java/lang/StringBuilder
    //   444: dup
    //   445: invokespecial 279	java/lang/StringBuilder:<init>	()V
    //   448: ldc 5
    //   450: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   453: invokestatic 284	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   456: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   459: ldc 13
    //   461: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   464: aload_0
    //   465: getfield 243	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:type	I
    //   468: invokevirtual 281	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   471: ldc 2
    //   473: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   476: aload_0
    //   477: getfield 248	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:agent	Lcom/sun/jmx/snmp/agent/SnmpMibAgent;
    //   480: invokevirtual 263	com/sun/jmx/snmp/agent/SnmpMibAgent:getMibName	()Ljava/lang/String;
    //   483: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   486: invokevirtual 280	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   489: invokevirtual 288	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   492: aload_0
    //   493: iconst_5
    //   494: putfield 242	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:errorStatus	I
    //   497: aload_0
    //   498: iconst_1
    //   499: putfield 241	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:errorIndex	I
    //   502: aload_1
    //   503: invokestatic 257	com/sun/jmx/snmp/ThreadContext:restore	(Lcom/sun/jmx/snmp/ThreadContext;)V
    //   506: goto +10 -> 516
    //   509: astore_2
    //   510: aload_1
    //   511: invokestatic 257	com/sun/jmx/snmp/ThreadContext:restore	(Lcom/sun/jmx/snmp/ThreadContext;)V
    //   514: aload_2
    //   515: athrow
    //   516: goto +139 -> 655
    //   519: astore_1
    //   520: aload_0
    //   521: aload_1
    //   522: invokevirtual 256	com/sun/jmx/snmp/SnmpStatusException:getStatus	()I
    //   525: putfield 242	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:errorStatus	I
    //   528: aload_0
    //   529: aload_1
    //   530: invokevirtual 255	com/sun/jmx/snmp/SnmpStatusException:getErrorIndex	()I
    //   533: putfield 241	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:errorIndex	I
    //   536: getstatic 236	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   539: getstatic 254	java/util/logging/Level:FINEST	Ljava/util/logging/Level;
    //   542: invokevirtual 287	java/util/logging/Logger:isLoggable	(Ljava/util/logging/Level;)Z
    //   545: ifeq +46 -> 591
    //   548: getstatic 236	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   551: getstatic 254	java/util/logging/Level:FINEST	Ljava/util/logging/Level;
    //   554: ldc 18
    //   556: invokevirtual 276	java/lang/Class:getName	()Ljava/lang/String;
    //   559: ldc 16
    //   561: new 141	java/lang/StringBuilder
    //   564: dup
    //   565: invokespecial 279	java/lang/StringBuilder:<init>	()V
    //   568: ldc 5
    //   570: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   573: invokestatic 284	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   576: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   579: ldc 7
    //   581: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   584: invokevirtual 280	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   587: aload_1
    //   588: invokevirtual 289	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   591: goto +64 -> 655
    //   594: astore_1
    //   595: aload_0
    //   596: iconst_5
    //   597: putfield 242	com/sun/jmx/snmp/daemon/SnmpSubRequestHandler:errorStatus	I
    //   600: getstatic 236	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   603: getstatic 254	java/util/logging/Level:FINEST	Ljava/util/logging/Level;
    //   606: invokevirtual 287	java/util/logging/Logger:isLoggable	(Ljava/util/logging/Level;)Z
    //   609: ifeq +46 -> 655
    //   612: getstatic 236	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   615: getstatic 254	java/util/logging/Level:FINEST	Ljava/util/logging/Level;
    //   618: ldc 18
    //   620: invokevirtual 276	java/lang/Class:getName	()Ljava/lang/String;
    //   623: ldc 16
    //   625: new 141	java/lang/StringBuilder
    //   628: dup
    //   629: invokespecial 279	java/lang/StringBuilder:<init>	()V
    //   632: ldc 5
    //   634: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   637: invokestatic 284	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   640: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   643: ldc 6
    //   645: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   648: invokevirtual 280	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   651: aload_1
    //   652: invokevirtual 289	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   655: getstatic 236	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   658: getstatic 253	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   661: invokevirtual 287	java/util/logging/Logger:isLoggable	(Ljava/util/logging/Level;)Z
    //   664: ifeq +45 -> 709
    //   667: getstatic 236	com/sun/jmx/defaults/JmxProperties:SNMP_ADAPTOR_LOGGER	Ljava/util/logging/Logger;
    //   670: getstatic 253	java/util/logging/Level:FINER	Ljava/util/logging/Level;
    //   673: ldc 18
    //   675: invokevirtual 276	java/lang/Class:getName	()Ljava/lang/String;
    //   678: ldc 16
    //   680: new 141	java/lang/StringBuilder
    //   683: dup
    //   684: invokespecial 279	java/lang/StringBuilder:<init>	()V
    //   687: ldc 5
    //   689: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   692: invokestatic 284	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   695: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   698: ldc 11
    //   700: invokevirtual 283	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   703: invokevirtual 280	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   706: invokevirtual 288	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   709: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	710	0	this	SnmpSubRequestHandler
    //   9	502	1	localThreadContext	com.sun.jmx.snmp.ThreadContext
    //   519	69	1	localSnmpStatusException	com.sun.jmx.snmp.SnmpStatusException
    //   594	58	1	localException	Exception
    //   509	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   10	502	509	finally
    //   0	516	519	com/sun/jmx/snmp/SnmpStatusException
    //   0	516	594	java/lang/Exception
  }
  
  static final int mapErrorStatusToV1(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 0) {
      return 0;
    }
    if (paramInt1 == 5) {
      return 5;
    }
    if (paramInt1 == 2) {
      return 2;
    }
    if ((paramInt1 == 224) || (paramInt1 == 225) || (paramInt1 == 6) || (paramInt1 == 18) || (paramInt1 == 16)) {
      return 2;
    }
    if ((paramInt1 == 16) || (paramInt1 == 17))
    {
      if (paramInt2 == 253) {
        return 4;
      }
      return 2;
    }
    if (paramInt1 == 11) {
      return 2;
    }
    if ((paramInt1 == 7) || (paramInt1 == 8) || (paramInt1 == 9) || (paramInt1 == 10) || (paramInt1 == 8) || (paramInt1 == 12))
    {
      if ((paramInt2 == 163) || (paramInt2 == 253)) {
        return 3;
      }
      return 2;
    }
    if ((paramInt1 == 13) || (paramInt1 == 14) || (paramInt1 == 15)) {
      return 5;
    }
    if (paramInt1 == 1) {
      return 1;
    }
    if ((paramInt1 == 3) || (paramInt1 == 4))
    {
      if ((paramInt2 == 163) || (paramInt2 == 253)) {
        return paramInt1;
      }
      return 2;
    }
    return 5;
  }
  
  static final int mapErrorStatusToV2(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 0) {
      return 0;
    }
    if (paramInt1 == 5) {
      return 5;
    }
    if (paramInt1 == 1) {
      return 1;
    }
    if ((paramInt2 != 163) && (paramInt2 != 253))
    {
      if (paramInt1 == 16) {
        return paramInt1;
      }
      return 5;
    }
    if (paramInt1 == 2) {
      return 6;
    }
    if (paramInt1 == 4) {
      return 17;
    }
    if (paramInt1 == 3) {
      return 10;
    }
    if ((paramInt1 == 6) || (paramInt1 == 18) || (paramInt1 == 16) || (paramInt1 == 17) || (paramInt1 == 11) || (paramInt1 == 7) || (paramInt1 == 8) || (paramInt1 == 9) || (paramInt1 == 10) || (paramInt1 == 8) || (paramInt1 == 12) || (paramInt1 == 13) || (paramInt1 == 14) || (paramInt1 == 15)) {
      return paramInt1;
    }
    return 5;
  }
  
  static final int mapErrorStatus(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 == 0) {
      return 0;
    }
    if (paramInt2 == 0) {
      return mapErrorStatusToV1(paramInt1, paramInt3);
    }
    if ((paramInt2 == 1) || (paramInt2 == 3)) {
      return mapErrorStatusToV2(paramInt1, paramInt3);
    }
    return 5;
  }
  
  protected int getErrorStatus()
  {
    if (errorStatus == 0) {
      return 0;
    }
    return mapErrorStatus(errorStatus, version, type);
  }
  
  protected int getErrorIndex()
  {
    if (errorStatus == 0) {
      return -1;
    }
    if ((errorIndex == 0) || (errorIndex == -1)) {
      errorIndex = 1;
    }
    return translation[(errorIndex - 1)];
  }
  
  protected void updateRequest(SnmpVarBind paramSnmpVarBind, int paramInt)
  {
    int i = varBind.size();
    translation[i] = paramInt;
    varBind.addElement(paramSnmpVarBind);
  }
  
  protected void updateResult(SnmpVarBind[] paramArrayOfSnmpVarBind)
  {
    if (paramArrayOfSnmpVarBind == null) {
      return;
    }
    int i = varBind.size();
    int j = paramArrayOfSnmpVarBind.length;
    for (int k = 0; k < i; k++)
    {
      int m = translation[k];
      if (m < j) {
        paramArrayOfSnmpVarBind[m] = ((SnmpVarBind)((NonSyncVector)varBind).elementAtNonSync(k));
      } else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "updateResult", "Position `" + m + "' is out of bound...");
      }
    }
  }
  
  private void init(SnmpEngine paramSnmpEngine, SnmpIncomingRequest paramSnmpIncomingRequest)
  {
    incRequest = paramSnmpIncomingRequest;
    engine = paramSnmpEngine;
  }
  
  class NonSyncVector<E>
    extends Vector<E>
  {
    public NonSyncVector(int paramInt)
    {
      super();
    }
    
    final void addNonSyncElement(E paramE)
    {
      ensureCapacity(elementCount + 1);
      elementData[(elementCount++)] = paramE;
    }
    
    final E elementAtNonSync(int paramInt)
    {
      return (E)elementData[paramInt];
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\SnmpSubRequestHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */