package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpMessage;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpPduPacket;
import com.sun.jmx.snmp.SnmpPduRequest;
import com.sun.jmx.snmp.SnmpPduRequestType;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpVarBindList;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SnmpInformRequest
  implements SnmpDefinitions
{
  private static SnmpRequestCounter requestCounter = new SnmpRequestCounter();
  private SnmpVarBindList varBindList = null;
  int errorStatus = 0;
  int errorIndex = 0;
  SnmpVarBind[] internalVarBind = null;
  String reason = null;
  private transient SnmpAdaptorServer adaptor;
  private transient SnmpSession informSession;
  private SnmpInformHandler callback = null;
  SnmpPdu requestPdu;
  SnmpPduRequestType responsePdu;
  private static final int stBase = 1;
  public static final int stInProgress = 1;
  public static final int stWaitingToSend = 3;
  public static final int stWaitingForReply = 5;
  public static final int stReceivedReply = 9;
  public static final int stAborted = 16;
  public static final int stTimeout = 32;
  public static final int stInternalError = 64;
  public static final int stResultsAvailable = 128;
  public static final int stNeverUsed = 256;
  private int numTries = 0;
  private int timeout = 3000;
  private int reqState = 256;
  private long prevPollTime = 0L;
  private long nextPollTime = 0L;
  private long waitTimeForResponse;
  private Date debugDate = new Date();
  private int requestId = 0;
  private int port = 0;
  private InetAddress address = null;
  private String communityString = null;
  
  SnmpInformRequest(SnmpSession paramSnmpSession, SnmpAdaptorServer paramSnmpAdaptorServer, InetAddress paramInetAddress, String paramString, int paramInt, SnmpInformHandler paramSnmpInformHandler)
    throws SnmpStatusException
  {
    informSession = paramSnmpSession;
    adaptor = paramSnmpAdaptorServer;
    address = paramInetAddress;
    communityString = paramString;
    port = paramInt;
    callback = paramSnmpInformHandler;
    informSession.addInformRequest(this);
    setTimeout(adaptor.getTimeout());
  }
  
  public final synchronized int getRequestId()
  {
    return requestId;
  }
  
  synchronized InetAddress getAddress()
  {
    return address;
  }
  
  public final synchronized int getRequestStatus()
  {
    return reqState;
  }
  
  public final synchronized boolean isAborted()
  {
    return (reqState & 0x10) == 16;
  }
  
  public final synchronized boolean inProgress()
  {
    return (reqState & 0x1) == 1;
  }
  
  public final synchronized boolean isResultAvailable()
  {
    return reqState == 128;
  }
  
  public final synchronized int getErrorStatus()
  {
    return errorStatus;
  }
  
  public final synchronized int getErrorIndex()
  {
    return errorIndex;
  }
  
  public final int getMaxTries()
  {
    return adaptor.getMaxTries();
  }
  
  public final synchronized int getNumTries()
  {
    return numTries;
  }
  
  final synchronized void setTimeout(int paramInt)
  {
    timeout = paramInt;
  }
  
  public final synchronized long getAbsNextPollTime()
  {
    return nextPollTime;
  }
  
  public final synchronized long getAbsMaxTimeToWait()
  {
    if (prevPollTime == 0L) {
      return System.currentTimeMillis();
    }
    return waitTimeForResponse;
  }
  
  public final synchronized SnmpVarBindList getResponseVarBindList()
  {
    if (inProgress()) {
      return null;
    }
    return varBindList;
  }
  
  public final boolean waitForCompletion(long paramLong)
  {
    if (!inProgress()) {
      return true;
    }
    if (informSession.thisSessionContext())
    {
      SnmpInformHandler localSnmpInformHandler1 = callback;
      callback = null;
      informSession.waitForResponse(this, paramLong);
      callback = localSnmpInformHandler1;
    }
    else
    {
      synchronized (this)
      {
        SnmpInformHandler localSnmpInformHandler2 = callback;
        try
        {
          callback = null;
          wait(paramLong);
        }
        catch (InterruptedException localInterruptedException) {}
        callback = localSnmpInformHandler2;
      }
    }
    return !inProgress();
  }
  
  public final void cancelRequest()
  {
    errorStatus = 225;
    stopRequest();
    deleteRequest();
    notifyClient();
  }
  
  public final synchronized void notifyClient()
  {
    notifyAll();
  }
  
  protected void finalize()
  {
    callback = null;
    varBindList = null;
    internalVarBind = null;
    adaptor = null;
    informSession = null;
    requestPdu = null;
    responsePdu = null;
  }
  
  public static String snmpErrorToString(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return "noError";
    case 1: 
      return "tooBig";
    case 2: 
      return "noSuchName";
    case 3: 
      return "badValue";
    case 4: 
      return "readOnly";
    case 5: 
      return "genErr";
    case 6: 
      return "noAccess";
    case 7: 
      return "wrongType";
    case 8: 
      return "wrongLength";
    case 9: 
      return "wrongEncoding";
    case 10: 
      return "wrongValue";
    case 11: 
      return "noCreation";
    case 12: 
      return "inconsistentValue";
    case 13: 
      return "resourceUnavailable";
    case 14: 
      return "commitFailed";
    case 15: 
      return "undoFailed";
    case 16: 
      return "authorizationError";
    case 17: 
      return "notWritable";
    case 18: 
      return "inconsistentName";
    case 224: 
      return "reqTimeout";
    case 225: 
      return "reqAborted";
    case 226: 
      return "rspDecodingError";
    case 227: 
      return "reqEncodingError";
    case 228: 
      return "reqPacketOverflow";
    case 229: 
      return "rspEndOfTable";
    case 230: 
      return "reqRefireAfterVbFix";
    case 231: 
      return "reqHandleTooBig";
    case 232: 
      return "reqTooBigImpossible";
    case 240: 
      return "reqInternalError";
    case 241: 
      return "reqSocketIOError";
    case 242: 
      return "reqUnknownError";
    case 243: 
      return "wrongSnmpVersion";
    case 244: 
      return "snmpUnknownPrincipal";
    case 245: 
      return "snmpAuthNotSupported";
    case 246: 
      return "snmpPrivNotSupported";
    case 249: 
      return "snmpBadSecurityLevel";
    case 247: 
      return "snmpUsmBadEngineId";
    case 248: 
      return "snmpUsmInvalidTimeliness";
    }
    return "Unknown Error = " + paramInt;
  }
  
  synchronized void start(SnmpVarBindList paramSnmpVarBindList)
    throws SnmpStatusException
  {
    if (inProgress()) {
      throw new SnmpStatusException("Inform request already in progress.");
    }
    setVarBindList(paramSnmpVarBindList);
    initializeAndFire();
  }
  
  private synchronized void initializeAndFire()
  {
    requestPdu = null;
    responsePdu = null;
    reason = null;
    startRequest(System.currentTimeMillis());
    setErrorStatusAndIndex(0, 0);
  }
  
  private synchronized void startRequest(long paramLong)
  {
    nextPollTime = paramLong;
    prevPollTime = 0L;
    schedulePoll();
  }
  
  private void schedulePoll()
  {
    numTries = 0;
    initNewRequest();
    setRequestStatus(3);
    informSession.getSnmpQManager().addRequest(this);
  }
  
  void action()
  {
    if (!inProgress()) {
      return;
    }
    for (;;)
    {
      try
      {
        if (numTries == 0) {
          invokeOnReady();
        } else if (numTries < getMaxTries()) {
          invokeOnRetry();
        } else {
          invokeOnTimeout();
        }
        return;
      }
      catch (OutOfMemoryError localOutOfMemoryError)
      {
        numTries += 1;
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "action", "Inform request hit out of memory situation...");
        }
        Thread.yield();
      }
    }
  }
  
  private void invokeOnReady()
  {
    if (requestPdu == null) {
      requestPdu = constructPduPacket();
    }
    if ((requestPdu != null) && (!sendPdu())) {
      queueResponse();
    }
  }
  
  private void invokeOnRetry()
  {
    invokeOnReady();
  }
  
  private void invokeOnTimeout()
  {
    errorStatus = 224;
    queueResponse();
  }
  
  private void queueResponse()
  {
    informSession.addResponse(this);
  }
  
  synchronized SnmpPdu constructPduPacket()
  {
    SnmpPduRequest localSnmpPduRequest = null;
    Object localObject = null;
    try
    {
      localSnmpPduRequest = new SnmpPduRequest();
      port = port;
      type = 166;
      version = 1;
      community = communityString.getBytes("8859_1");
      requestId = getRequestId();
      varBindList = internalVarBind;
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "constructPduPacket", "Packet built");
      }
    }
    catch (Exception localException)
    {
      localObject = localException;
      errorStatus = 242;
      reason = localException.getMessage();
    }
    if (localObject != null)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "constructPduPacket", "Got unexpected exception", (Throwable)localObject);
      }
      localSnmpPduRequest = null;
      queueResponse();
    }
    return localSnmpPduRequest;
  }
  
  boolean sendPdu()
  {
    try
    {
      responsePdu = null;
      SnmpPduFactory localSnmpPduFactory = adaptor.getPduFactory();
      SnmpMessage localSnmpMessage = (SnmpMessage)localSnmpPduFactory.encodeSnmpPdu((SnmpPduPacket)requestPdu, adaptor.getBufferSize().intValue());
      if (localSnmpMessage == null)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "sendPdu", "pdu factory returned a null value");
        }
        throw new SnmpStatusException(242);
      }
      int i = adaptor.getBufferSize().intValue();
      byte[] arrayOfByte = new byte[i];
      int j = localSnmpMessage.encodeMessage(arrayOfByte);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "sendPdu", "Dump : \n" + localSnmpMessage.printMessage());
      }
      sendPduPacket(arrayOfByte, j);
      return true;
    }
    catch (SnmpTooBigException localSnmpTooBigException)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "sendPdu", "Got unexpected exception", localSnmpTooBigException);
      }
      setErrorStatusAndIndex(228, localSnmpTooBigException.getVarBindCount());
      requestPdu = null;
      reason = localSnmpTooBigException.getMessage();
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "sendPdu", "Packet Overflow while building inform request");
      }
    }
    catch (IOException localIOException)
    {
      setErrorStatusAndIndex(241, 0);
      reason = localIOException.getMessage();
    }
    catch (Exception localException)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "sendPdu", "Got unexpected exception", localException);
      }
      setErrorStatusAndIndex(242, 0);
      reason = localException.getMessage();
    }
    return false;
  }
  
  final void sendPduPacket(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "sendPduPacket", "Send to peer. Peer/Port : " + address.getHostName() + "/" + port + ". Length = " + paramInt + "\nDump : \n" + SnmpMessage.dumpHexBuffer(paramArrayOfByte, 0, paramInt));
    }
    SnmpSocket localSnmpSocket = informSession.getSocket();
    synchronized (localSnmpSocket)
    {
      localSnmpSocket.sendPacket(paramArrayOfByte, paramInt, address, port);
      setRequestSentTime(System.currentTimeMillis());
    }
  }
  
  final void processResponse()
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "processResponse", "errstatus = " + errorStatus);
    }
    if (!inProgress())
    {
      responsePdu = null;
      return;
    }
    if (errorStatus >= 240)
    {
      handleInternalError("Internal Error...");
      return;
    }
    try
    {
      parsePduPacket(responsePdu);
      switch (errorStatus)
      {
      case 0: 
        handleSuccess();
        return;
      case 224: 
        handleTimeout();
        return;
      case 240: 
        handleInternalError("Unknown internal error.  deal with it later!");
        return;
      case 231: 
        setErrorStatusAndIndex(1, 0);
        handleError("Cannot handle too-big situation...");
        return;
      case 230: 
        initializeAndFire();
        return;
      }
      handleError("Error status set in packet...!!");
      return;
    }
    catch (Exception localException)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "processResponse", "Got unexpected exception", localException);
      }
      reason = localException.getMessage();
      handleInternalError(reason);
    }
  }
  
  synchronized void parsePduPacket(SnmpPduRequestType paramSnmpPduRequestType)
  {
    if (paramSnmpPduRequestType == null) {
      return;
    }
    errorStatus = paramSnmpPduRequestType.getErrorStatus();
    errorIndex = paramSnmpPduRequestType.getErrorIndex();
    if (errorStatus == 0)
    {
      updateInternalVarBindWithResult(varBindList);
      return;
    }
    if (errorStatus != 0) {
      errorIndex -= 1;
    }
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "parsePduPacket", "received inform response. ErrorStatus/ErrorIndex = " + errorStatus + "/" + errorIndex);
    }
  }
  
  private void handleSuccess()
  {
    setRequestStatus(128);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "handleSuccess", "Invoking user defined callback...");
    }
    deleteRequest();
    notifyClient();
    requestPdu = null;
    internalVarBind = null;
    try
    {
      if (callback != null) {
        callback.processSnmpPollData(this, errorStatus, errorIndex, getVarBindList());
      }
    }
    catch (Exception localException)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleSuccess", "Exception generated by user callback", localException);
      }
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleSuccess", "OutOfMemory Error generated by user callback", localOutOfMemoryError);
      }
      Thread.yield();
    }
  }
  
  private void handleTimeout()
  {
    setRequestStatus(32);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleTimeout", "Snmp error/index = " + snmpErrorToString(errorStatus) + "/" + errorIndex + ". Invoking timeout user defined callback...");
    }
    deleteRequest();
    notifyClient();
    requestPdu = null;
    responsePdu = null;
    internalVarBind = null;
    try
    {
      if (callback != null) {
        callback.processSnmpPollTimeout(this);
      }
    }
    catch (Exception localException)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleTimeout", "Exception generated by user callback", localException);
      }
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleTimeout", "OutOfMemory Error generated by user callback", localOutOfMemoryError);
      }
      Thread.yield();
    }
  }
  
  private void handleError(String paramString)
  {
    setRequestStatus(128);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleError", "Snmp error/index = " + snmpErrorToString(errorStatus) + "/" + errorIndex + ". Invoking error user defined callback...\n" + getVarBindList());
    }
    deleteRequest();
    notifyClient();
    requestPdu = null;
    responsePdu = null;
    internalVarBind = null;
    try
    {
      if (callback != null) {
        callback.processSnmpPollData(this, getErrorStatus(), getErrorIndex(), getVarBindList());
      }
    }
    catch (Exception localException)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleError", "Exception generated by user callback", localException);
      }
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleError", "OutOfMemory Error generated by user callback", localOutOfMemoryError);
      }
      Thread.yield();
    }
  }
  
  private void handleInternalError(String paramString)
  {
    setRequestStatus(64);
    if (reason == null) {
      reason = paramString;
    }
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleInternalError", "Snmp error/index = " + snmpErrorToString(errorStatus) + "/" + errorIndex + ". Invoking internal error user defined callback...\n" + getVarBindList());
    }
    deleteRequest();
    notifyClient();
    requestPdu = null;
    responsePdu = null;
    internalVarBind = null;
    try
    {
      if (callback != null) {
        callback.processSnmpInternalError(this, reason);
      }
    }
    catch (Exception localException)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleInternalError", "Exception generated by user callback", localException);
      }
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleInternalError", "OutOfMemory Error generated by user callback", localOutOfMemoryError);
      }
      Thread.yield();
    }
  }
  
  void updateInternalVarBindWithResult(SnmpVarBind[] paramArrayOfSnmpVarBind)
  {
    if ((paramArrayOfSnmpVarBind == null) || (paramArrayOfSnmpVarBind.length == 0)) {
      return;
    }
    int i = 0;
    for (int j = 0; (j < internalVarBind.length) && (i < paramArrayOfSnmpVarBind.length); j++)
    {
      SnmpVarBind localSnmpVarBind1 = internalVarBind[j];
      if (localSnmpVarBind1 != null)
      {
        SnmpVarBind localSnmpVarBind2 = paramArrayOfSnmpVarBind[i];
        localSnmpVarBind1.setSnmpValue(localSnmpVarBind2.getSnmpValue());
        i++;
      }
    }
  }
  
  final void invokeOnResponse(Object paramObject)
  {
    if (paramObject != null) {
      if ((paramObject instanceof SnmpPduRequestType)) {
        responsePdu = ((SnmpPduRequestType)paramObject);
      } else {
        return;
      }
    }
    setRequestStatus(9);
    queueResponse();
  }
  
  private void stopRequest()
  {
    synchronized (this)
    {
      setRequestStatus(16);
    }
    informSession.getSnmpQManager().removeRequest(this);
    synchronized (this)
    {
      requestId = 0;
    }
  }
  
  final synchronized void deleteRequest()
  {
    informSession.removeInformRequest(this);
  }
  
  final synchronized SnmpVarBindList getVarBindList()
  {
    return varBindList;
  }
  
  final synchronized void setVarBindList(SnmpVarBindList paramSnmpVarBindList)
  {
    varBindList = paramSnmpVarBindList;
    if ((internalVarBind == null) || (internalVarBind.length != varBindList.size())) {
      internalVarBind = new SnmpVarBind[varBindList.size()];
    }
    varBindList.copyInto(internalVarBind);
  }
  
  final synchronized void setErrorStatusAndIndex(int paramInt1, int paramInt2)
  {
    errorStatus = paramInt1;
    errorIndex = paramInt2;
  }
  
  final synchronized void setPrevPollTime(long paramLong)
  {
    prevPollTime = paramLong;
  }
  
  final void setRequestSentTime(long paramLong)
  {
    numTries += 1;
    setPrevPollTime(paramLong);
    waitTimeForResponse = (prevPollTime + timeout * numTries);
    setRequestStatus(5);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "setRequestSentTime", "Inform request Successfully sent");
    }
    informSession.getSnmpQManager().addWaiting(this);
  }
  
  final synchronized void initNewRequest()
  {
    requestId = requestCounter.getNewId();
  }
  
  long timeRemainingForAction(long paramLong)
  {
    switch (reqState)
    {
    case 3: 
      return nextPollTime - paramLong;
    case 5: 
      return waitTimeForResponse - paramLong;
    }
    return -1L;
  }
  
  static String statusDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 3: 
      return "Waiting to send.";
    case 5: 
      return "Waiting for reply.";
    case 9: 
      return "Response arrived.";
    case 16: 
      return "Aborted by user.";
    case 32: 
      return "Timeout Occured.";
    case 64: 
      return "Internal error.";
    case 128: 
      return "Results available";
    case 256: 
      return "Inform request in createAndWait state";
    }
    return "Unknown inform request state.";
  }
  
  final synchronized void setRequestStatus(int paramInt)
  {
    reqState = paramInt;
  }
  
  public synchronized String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer(300);
    localStringBuffer.append(tostring());
    localStringBuffer.append("\nPeer/Port : " + address.getHostName() + "/" + port);
    return localStringBuffer.toString();
  }
  
  private synchronized String tostring()
  {
    StringBuffer localStringBuffer = new StringBuffer("InformRequestId = " + requestId);
    localStringBuffer.append("   Status = " + statusDescription(reqState));
    localStringBuffer.append("  Timeout/MaxTries/NumTries = " + timeout * numTries + "/" + getMaxTries() + "/" + numTries);
    if (prevPollTime > 0L)
    {
      debugDate.setTime(prevPollTime);
      localStringBuffer.append("\nPrevPolled = " + debugDate.toString());
    }
    else
    {
      localStringBuffer.append("\nNeverPolled");
    }
    localStringBuffer.append(" / RemainingTime(millis) = " + timeRemainingForAction(System.currentTimeMillis()));
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\SnmpInformRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */