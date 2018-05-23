package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.packaging.mime.Header;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.BMMimeMultipart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePullMultipart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParameterList;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParseException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.SharedInputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import com.sun.xml.internal.messaging.saaj.util.SAAJUtil;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;

public abstract class MessageImpl
  extends SOAPMessage
  implements SOAPConstants
{
  public static final String CONTENT_ID = "Content-ID";
  public static final String CONTENT_LOCATION = "Content-Location";
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
  protected static final int PLAIN_XML_FLAG = 1;
  protected static final int MIME_MULTIPART_FLAG = 2;
  protected static final int SOAP1_1_FLAG = 4;
  protected static final int SOAP1_2_FLAG = 8;
  protected static final int MIME_MULTIPART_XOP_SOAP1_1_FLAG = 6;
  protected static final int MIME_MULTIPART_XOP_SOAP1_2_FLAG = 10;
  protected static final int XOP_FLAG = 13;
  protected static final int FI_ENCODED_FLAG = 16;
  protected MimeHeaders headers;
  protected ContentType contentType;
  protected SOAPPartImpl soapPartImpl;
  protected FinalArrayList attachments;
  protected boolean saved = false;
  protected byte[] messageBytes;
  protected int messageByteCount;
  protected HashMap properties = new HashMap();
  protected MimeMultipart multiPart = null;
  protected boolean attachmentsInitialized = false;
  protected boolean isFastInfoset = false;
  protected boolean acceptFastInfoset = false;
  protected MimeMultipart mmp = null;
  private boolean optimizeAttachmentProcessing = true;
  private InputStream inputStreamAfterSaveChanges = null;
  private static boolean switchOffBM = false;
  private static boolean switchOffLazyAttachment = false;
  private static boolean useMimePull = SAAJUtil.getSystemBoolean("saaj.use.mimepull");
  private boolean lazyAttachments = false;
  private static final Iterator nullIter = Collections.EMPTY_LIST.iterator();
  
  private static boolean isSoap1_1Type(String paramString1, String paramString2)
  {
    return ((paramString1.equalsIgnoreCase("text")) && (paramString2.equalsIgnoreCase("xml"))) || ((paramString1.equalsIgnoreCase("text")) && (paramString2.equalsIgnoreCase("xml-soap"))) || ((paramString1.equals("application")) && (paramString2.equals("fastinfoset")));
  }
  
  private static boolean isEqualToSoap1_1Type(String paramString)
  {
    return (paramString.startsWith("text/xml")) || (paramString.startsWith("application/fastinfoset"));
  }
  
  private static boolean isSoap1_2Type(String paramString1, String paramString2)
  {
    return (paramString1.equals("application")) && ((paramString2.equals("soap+xml")) || (paramString2.equals("soap+fastinfoset")));
  }
  
  private static boolean isEqualToSoap1_2Type(String paramString)
  {
    return (paramString.startsWith("application/soap+xml")) || (paramString.startsWith("application/soap+fastinfoset"));
  }
  
  protected MessageImpl()
  {
    this(false, false);
    attachmentsInitialized = true;
  }
  
  protected MessageImpl(boolean paramBoolean1, boolean paramBoolean2)
  {
    isFastInfoset = paramBoolean1;
    acceptFastInfoset = paramBoolean2;
    headers = new MimeHeaders();
    headers.setHeader("Accept", getExpectedAcceptHeader());
    contentType = new ContentType();
  }
  
  protected MessageImpl(SOAPMessage paramSOAPMessage)
  {
    if (!(paramSOAPMessage instanceof MessageImpl)) {}
    MessageImpl localMessageImpl = (MessageImpl)paramSOAPMessage;
    headers = headers;
    soapPartImpl = soapPartImpl;
    attachments = attachments;
    saved = saved;
    messageBytes = messageBytes;
    messageByteCount = messageByteCount;
    properties = properties;
    contentType = contentType;
  }
  
  protected static boolean isSoap1_1Content(int paramInt)
  {
    return (paramInt & 0x4) != 0;
  }
  
  protected static boolean isSoap1_2Content(int paramInt)
  {
    return (paramInt & 0x8) != 0;
  }
  
  private static boolean isMimeMultipartXOPSoap1_2Package(ContentType paramContentType)
  {
    String str1 = paramContentType.getParameter("type");
    if (str1 == null) {
      return false;
    }
    str1 = str1.toLowerCase();
    if (!str1.startsWith("application/xop+xml")) {
      return false;
    }
    String str2 = paramContentType.getParameter("start-info");
    if (str2 == null) {
      return false;
    }
    str2 = str2.toLowerCase();
    return isEqualToSoap1_2Type(str2);
  }
  
  private static boolean isMimeMultipartXOPSoap1_1Package(ContentType paramContentType)
  {
    String str1 = paramContentType.getParameter("type");
    if (str1 == null) {
      return false;
    }
    str1 = str1.toLowerCase();
    if (!str1.startsWith("application/xop+xml")) {
      return false;
    }
    String str2 = paramContentType.getParameter("start-info");
    if (str2 == null) {
      return false;
    }
    str2 = str2.toLowerCase();
    return isEqualToSoap1_1Type(str2);
  }
  
  private static boolean isSOAPBodyXOPPackage(ContentType paramContentType)
  {
    String str1 = paramContentType.getPrimaryType();
    String str2 = paramContentType.getSubType();
    if ((str1.equalsIgnoreCase("application")) && (str2.equalsIgnoreCase("xop+xml")))
    {
      String str3 = getTypeParameter(paramContentType);
      return (isEqualToSoap1_2Type(str3)) || (isEqualToSoap1_1Type(str3));
    }
    return false;
  }
  
  protected MessageImpl(MimeHeaders paramMimeHeaders, InputStream paramInputStream)
    throws SOAPExceptionImpl
  {
    contentType = parseContentType(paramMimeHeaders);
    init(paramMimeHeaders, identifyContentType(contentType), contentType, paramInputStream);
  }
  
  private static ContentType parseContentType(MimeHeaders paramMimeHeaders)
    throws SOAPExceptionImpl
  {
    String str;
    if (paramMimeHeaders != null)
    {
      str = getContentType(paramMimeHeaders);
    }
    else
    {
      log.severe("SAAJ0550.soap.null.headers");
      throw new SOAPExceptionImpl("Cannot create message: Headers can't be null");
    }
    if (str == null)
    {
      log.severe("SAAJ0532.soap.no.Content-Type");
      throw new SOAPExceptionImpl("Absent Content-Type");
    }
    try
    {
      return new ContentType(str);
    }
    catch (Throwable localThrowable)
    {
      log.severe("SAAJ0535.soap.cannot.internalize.message");
      throw new SOAPExceptionImpl("Unable to internalize message", localThrowable);
    }
  }
  
  protected MessageImpl(MimeHeaders paramMimeHeaders, ContentType paramContentType, int paramInt, InputStream paramInputStream)
    throws SOAPExceptionImpl
  {
    init(paramMimeHeaders, paramInt, paramContentType, paramInputStream);
  }
  
  private void init(MimeHeaders paramMimeHeaders, int paramInt, final ContentType paramContentType, final InputStream paramInputStream)
    throws SOAPExceptionImpl
  {
    headers = paramMimeHeaders;
    try
    {
      if ((paramInt & 0x10) > 0) {
        isFastInfoset = (acceptFastInfoset = 1);
      }
      Object localObject1;
      Object localObject2;
      Object localObject3;
      if (!isFastInfoset)
      {
        localObject1 = paramMimeHeaders.getHeader("Accept");
        if (localObject1 != null) {
          for (int i = 0; i < localObject1.length; i++)
          {
            localObject2 = new StringTokenizer(localObject1[i], ",");
            while (((StringTokenizer)localObject2).hasMoreTokens())
            {
              localObject3 = ((StringTokenizer)localObject2).nextToken().trim();
              if ((((String)localObject3).equalsIgnoreCase("application/fastinfoset")) || (((String)localObject3).equalsIgnoreCase("application/soap+fastinfoset")))
              {
                acceptFastInfoset = true;
                break;
              }
            }
          }
        }
      }
      if (!isCorrectSoapVersion(paramInt))
      {
        log.log(Level.SEVERE, "SAAJ0533.soap.incorrect.Content-Type", new String[] { paramContentType.toString(), getExpectedContentType() });
        throw new SOAPVersionMismatchException("Cannot create message: incorrect content-type for SOAP version. Got: " + paramContentType + " Expected: " + getExpectedContentType());
      }
      if ((paramInt & 0x1) != 0)
      {
        if (isFastInfoset)
        {
          getSOAPPart().setContent(FastInfosetReflection.FastInfosetSource_new(paramInputStream));
        }
        else
        {
          initCharsetProperty(paramContentType);
          getSOAPPart().setContent(new StreamSource(paramInputStream));
        }
      }
      else if ((paramInt & 0x2) != 0)
      {
        localObject1 = new DataSource()
        {
          public InputStream getInputStream()
          {
            return paramInputStream;
          }
          
          public OutputStream getOutputStream()
          {
            return null;
          }
          
          public String getContentType()
          {
            return paramContentType.toString();
          }
          
          public String getName()
          {
            return "";
          }
        };
        multiPart = null;
        if (useMimePull) {
          multiPart = new MimePullMultipart((DataSource)localObject1, paramContentType);
        } else if (switchOffBM) {
          multiPart = new MimeMultipart((DataSource)localObject1, paramContentType);
        } else {
          multiPart = new BMMimeMultipart((DataSource)localObject1, paramContentType);
        }
        String str1 = paramContentType.getParameter("start");
        localObject2 = null;
        localObject3 = null;
        String str2 = null;
        Object localObject4 = null;
        if ((switchOffBM) || (switchOffLazyAttachment))
        {
          int j;
          if (str1 == null)
          {
            localObject2 = multiPart.getBodyPart(0);
            for (j = 1; j < multiPart.getCount(); j++) {
              initializeAttachment(multiPart, j);
            }
          }
          else
          {
            localObject2 = multiPart.getBodyPart(str1);
            for (j = 0; j < multiPart.getCount(); j++)
            {
              str2 = multiPart.getBodyPart(j).getContentID();
              localObject4 = str2 != null ? str2.replaceFirst("^<", "").replaceFirst(">$", "") : null;
              if ((!str1.equals(str2)) && (!str1.equals(localObject4))) {
                initializeAttachment(multiPart, j);
              }
            }
          }
        }
        else if (useMimePull)
        {
          localObject5 = (MimePullMultipart)multiPart;
          localObject6 = ((MimePullMultipart)localObject5).readAndReturnSOAPPart();
          localObject2 = new MimeBodyPart((MIMEPart)localObject6);
          localObject3 = ((MIMEPart)localObject6).readOnce();
        }
        else
        {
          localObject5 = (BMMimeMultipart)multiPart;
          localObject6 = ((BMMimeMultipart)localObject5).initStream();
          localObject7 = null;
          if ((localObject6 instanceof SharedInputStream)) {
            localObject7 = (SharedInputStream)localObject6;
          }
          String str3 = "--" + paramContentType.getParameter("boundary");
          byte[] arrayOfByte = ASCIIUtility.getBytes(str3);
          if (str1 == null)
          {
            localObject2 = ((BMMimeMultipart)localObject5).getNextPart((InputStream)localObject6, arrayOfByte, (SharedInputStream)localObject7);
            ((BMMimeMultipart)localObject5).removeBodyPart((MimeBodyPart)localObject2);
          }
          else
          {
            MimeBodyPart localMimeBodyPart = null;
            try
            {
              while ((!str1.equals(str2)) && (!str1.equals(localObject4)))
              {
                localMimeBodyPart = ((BMMimeMultipart)localObject5).getNextPart((InputStream)localObject6, arrayOfByte, (SharedInputStream)localObject7);
                str2 = localMimeBodyPart.getContentID();
                localObject4 = str2 != null ? str2.replaceFirst("^<", "").replaceFirst(">$", "") : null;
              }
              localObject2 = localMimeBodyPart;
              ((BMMimeMultipart)localObject5).removeBodyPart(localMimeBodyPart);
            }
            catch (Exception localException)
            {
              throw new SOAPExceptionImpl(localException);
            }
          }
        }
        if ((localObject3 == null) && (localObject2 != null)) {
          localObject3 = ((MimeBodyPart)localObject2).getInputStream();
        }
        Object localObject5 = new ContentType(((MimeBodyPart)localObject2).getContentType());
        initCharsetProperty((ContentType)localObject5);
        Object localObject6 = ((ContentType)localObject5).getBaseType().toLowerCase();
        if ((!isEqualToSoap1_1Type((String)localObject6)) && (!isEqualToSoap1_2Type((String)localObject6)) && (!isSOAPBodyXOPPackage((ContentType)localObject5)))
        {
          log.log(Level.SEVERE, "SAAJ0549.soap.part.invalid.Content-Type", new Object[] { localObject6 });
          throw new SOAPExceptionImpl("Bad Content-Type for SOAP Part : " + (String)localObject6);
        }
        Object localObject7 = getSOAPPart();
        setMimeHeaders((SOAPPart)localObject7, (MimeBodyPart)localObject2);
        ((SOAPPart)localObject7).setContent(isFastInfoset ? FastInfosetReflection.FastInfosetSource_new((InputStream)localObject3) : new StreamSource((InputStream)localObject3));
      }
      else
      {
        log.severe("SAAJ0534.soap.unknown.Content-Type");
        throw new SOAPExceptionImpl("Unrecognized Content-Type");
      }
    }
    catch (Throwable localThrowable)
    {
      log.severe("SAAJ0535.soap.cannot.internalize.message");
      throw new SOAPExceptionImpl("Unable to internalize message", localThrowable);
    }
    needsSave();
  }
  
  public boolean isFastInfoset()
  {
    return isFastInfoset;
  }
  
  public boolean acceptFastInfoset()
  {
    return acceptFastInfoset;
  }
  
  public void setIsFastInfoset(boolean paramBoolean)
  {
    if (paramBoolean != isFastInfoset)
    {
      isFastInfoset = paramBoolean;
      if (isFastInfoset) {
        acceptFastInfoset = true;
      }
      saved = false;
    }
  }
  
  public Object getProperty(String paramString)
  {
    return (String)properties.get(paramString);
  }
  
  public void setProperty(String paramString, Object paramObject)
  {
    verify(paramString, paramObject);
    properties.put(paramString, paramObject);
  }
  
  private void verify(String paramString, Object paramObject)
  {
    if (paramString.equalsIgnoreCase("javax.xml.soap.write-xml-declaration"))
    {
      if ((!"true".equals(paramObject)) && (!"false".equals(paramObject))) {
        throw new RuntimeException(paramString + " must have value false or true");
      }
      try
      {
        EnvelopeImpl localEnvelopeImpl = (EnvelopeImpl)getSOAPPart().getEnvelope();
        if ("true".equalsIgnoreCase((String)paramObject)) {
          localEnvelopeImpl.setOmitXmlDecl("no");
        } else if ("false".equalsIgnoreCase((String)paramObject)) {
          localEnvelopeImpl.setOmitXmlDecl("yes");
        }
      }
      catch (Exception localException1)
      {
        log.log(Level.SEVERE, "SAAJ0591.soap.exception.in.set.property", new Object[] { localException1.getMessage(), "javax.xml.soap.write-xml-declaration" });
        throw new RuntimeException(localException1);
      }
      return;
    }
    if (paramString.equalsIgnoreCase("javax.xml.soap.character-set-encoding")) {
      try
      {
        ((EnvelopeImpl)getSOAPPart().getEnvelope()).setCharsetEncoding((String)paramObject);
      }
      catch (Exception localException2)
      {
        log.log(Level.SEVERE, "SAAJ0591.soap.exception.in.set.property", new Object[] { localException2.getMessage(), "javax.xml.soap.character-set-encoding" });
        throw new RuntimeException(localException2);
      }
    }
  }
  
  protected abstract boolean isCorrectSoapVersion(int paramInt);
  
  protected abstract String getExpectedContentType();
  
  protected abstract String getExpectedAcceptHeader();
  
  static int identifyContentType(ContentType paramContentType)
    throws SOAPExceptionImpl
  {
    String str1 = paramContentType.getPrimaryType().toLowerCase();
    String str2 = paramContentType.getSubType().toLowerCase();
    if (str1.equals("multipart"))
    {
      if (str2.equals("related"))
      {
        String str3 = getTypeParameter(paramContentType);
        if (isEqualToSoap1_1Type(str3)) {
          return (str3.equals("application/fastinfoset") ? 16 : 0) | 0x2 | 0x4;
        }
        if (isEqualToSoap1_2Type(str3)) {
          return (str3.equals("application/soap+fastinfoset") ? 16 : 0) | 0x2 | 0x8;
        }
        if (isMimeMultipartXOPSoap1_1Package(paramContentType)) {
          return 6;
        }
        if (isMimeMultipartXOPSoap1_2Package(paramContentType)) {
          return 10;
        }
        log.severe("SAAJ0536.soap.content-type.mustbe.multipart");
        throw new SOAPExceptionImpl("Content-Type needs to be Multipart/Related and with \"type=text/xml\" or \"type=application/soap+xml\"");
      }
      log.severe("SAAJ0537.soap.invalid.content-type");
      throw new SOAPExceptionImpl("Invalid Content-Type: " + str1 + '/' + str2);
    }
    if (isSoap1_1Type(str1, str2)) {
      return ((str1.equalsIgnoreCase("application")) && (str2.equalsIgnoreCase("fastinfoset")) ? 16 : 0) | 0x1 | 0x4;
    }
    if (isSoap1_2Type(str1, str2)) {
      return ((str1.equalsIgnoreCase("application")) && (str2.equalsIgnoreCase("soap+fastinfoset")) ? 16 : 0) | 0x1 | 0x8;
    }
    if (isSOAPBodyXOPPackage(paramContentType)) {
      return 13;
    }
    log.severe("SAAJ0537.soap.invalid.content-type");
    throw new SOAPExceptionImpl("Invalid Content-Type:" + str1 + '/' + str2 + ". Is this an error message instead of a SOAP response?");
  }
  
  private static String getTypeParameter(ContentType paramContentType)
  {
    String str = paramContentType.getParameter("type");
    if (str != null) {
      return str.toLowerCase();
    }
    return "text/xml";
  }
  
  public MimeHeaders getMimeHeaders()
  {
    return headers;
  }
  
  static final String getContentType(MimeHeaders paramMimeHeaders)
  {
    String[] arrayOfString = paramMimeHeaders.getHeader("Content-Type");
    if (arrayOfString == null) {
      return null;
    }
    return arrayOfString[0];
  }
  
  public String getContentType()
  {
    return getContentType(headers);
  }
  
  public void setContentType(String paramString)
  {
    headers.setHeader("Content-Type", paramString);
    needsSave();
  }
  
  private ContentType contentType()
  {
    ContentType localContentType = null;
    try
    {
      String str = getContentType();
      if (str == null) {
        return contentType;
      }
      localContentType = new ContentType(str);
    }
    catch (Exception localException) {}
    return localContentType;
  }
  
  public String getBaseType()
  {
    return contentType().getBaseType();
  }
  
  public void setBaseType(String paramString)
  {
    ContentType localContentType = contentType();
    localContentType.setParameter("type", paramString);
    headers.setHeader("Content-Type", localContentType.toString());
    needsSave();
  }
  
  public String getAction()
  {
    return contentType().getParameter("action");
  }
  
  public void setAction(String paramString)
  {
    ContentType localContentType = contentType();
    localContentType.setParameter("action", paramString);
    headers.setHeader("Content-Type", localContentType.toString());
    needsSave();
  }
  
  public String getCharset()
  {
    return contentType().getParameter("charset");
  }
  
  public void setCharset(String paramString)
  {
    ContentType localContentType = contentType();
    localContentType.setParameter("charset", paramString);
    headers.setHeader("Content-Type", localContentType.toString());
    needsSave();
  }
  
  private final void needsSave()
  {
    saved = false;
  }
  
  public boolean saveRequired()
  {
    return saved != true;
  }
  
  public String getContentDescription()
  {
    String[] arrayOfString = headers.getHeader("Content-Description");
    if ((arrayOfString != null) && (arrayOfString.length > 0)) {
      return arrayOfString[0];
    }
    return null;
  }
  
  public void setContentDescription(String paramString)
  {
    headers.setHeader("Content-Description", paramString);
    needsSave();
  }
  
  public abstract SOAPPart getSOAPPart();
  
  public void removeAllAttachments()
  {
    try
    {
      initializeAllAttachments();
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
    if (attachments != null)
    {
      attachments.clear();
      needsSave();
    }
  }
  
  public int countAttachments()
  {
    try
    {
      initializeAllAttachments();
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
    if (attachments != null) {
      return attachments.size();
    }
    return 0;
  }
  
  public void addAttachmentPart(AttachmentPart paramAttachmentPart)
  {
    try
    {
      initializeAllAttachments();
      optimizeAttachmentProcessing = true;
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
    if (attachments == null) {
      attachments = new FinalArrayList();
    }
    attachments.add(paramAttachmentPart);
    needsSave();
  }
  
  public Iterator getAttachments()
  {
    try
    {
      initializeAllAttachments();
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
    if (attachments == null) {
      return nullIter;
    }
    return attachments.iterator();
  }
  
  private void setFinalContentType(String paramString)
  {
    ContentType localContentType = contentType();
    if (localContentType == null) {
      localContentType = new ContentType();
    }
    String[] arrayOfString = getExpectedContentType().split("/");
    localContentType.setPrimaryType(arrayOfString[0]);
    localContentType.setSubType(arrayOfString[1]);
    localContentType.setParameter("charset", paramString);
    headers.setHeader("Content-Type", localContentType.toString());
  }
  
  public Iterator getAttachments(MimeHeaders paramMimeHeaders)
  {
    try
    {
      initializeAllAttachments();
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
    if (attachments == null) {
      return nullIter;
    }
    return new MimeMatchingIterator(paramMimeHeaders);
  }
  
  public void removeAttachments(MimeHeaders paramMimeHeaders)
  {
    try
    {
      initializeAllAttachments();
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
    if (attachments == null) {
      return;
    }
    MimeMatchingIterator localMimeMatchingIterator = new MimeMatchingIterator(paramMimeHeaders);
    while (localMimeMatchingIterator.hasNext())
    {
      int i = attachments.indexOf(localMimeMatchingIterator.next());
      attachments.set(i, null);
    }
    FinalArrayList localFinalArrayList = new FinalArrayList();
    for (int j = 0; j < attachments.size(); j++) {
      if (attachments.get(j) != null) {
        localFinalArrayList.add(attachments.get(j));
      }
    }
    attachments = localFinalArrayList;
  }
  
  public AttachmentPart createAttachmentPart()
  {
    return new AttachmentPartImpl();
  }
  
  public AttachmentPart getAttachment(SOAPElement paramSOAPElement)
    throws SOAPException
  {
    try
    {
      initializeAllAttachments();
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
    String str1 = paramSOAPElement.getAttribute("href");
    Object localObject;
    if ("".equals(str1))
    {
      Node localNode = getValueNodeStrict(paramSOAPElement);
      String str2 = null;
      if (localNode != null) {
        str2 = localNode.getValue();
      }
      if ((str2 == null) || ("".equals(str2))) {
        return null;
      }
      localObject = str2;
    }
    else
    {
      localObject = str1;
    }
    return getAttachmentPart((String)localObject);
  }
  
  private Node getValueNodeStrict(SOAPElement paramSOAPElement)
  {
    Node localNode = (Node)paramSOAPElement.getFirstChild();
    if (localNode != null)
    {
      if ((localNode.getNextSibling() == null) && (localNode.getNodeType() == 3)) {
        return localNode;
      }
      return null;
    }
    return null;
  }
  
  private AttachmentPart getAttachmentPart(String paramString)
    throws SOAPException
  {
    Object localObject1;
    try
    {
      Object localObject2;
      Object localObject3;
      if (paramString.startsWith("cid:"))
      {
        paramString = '<' + paramString.substring("cid:".length()) + '>';
        localObject2 = new MimeHeaders();
        ((MimeHeaders)localObject2).addHeader("Content-ID", paramString);
        localObject3 = getAttachments((MimeHeaders)localObject2);
        localObject1 = localObject3 == null ? null : (AttachmentPart)((Iterator)localObject3).next();
      }
      else
      {
        localObject2 = new MimeHeaders();
        ((MimeHeaders)localObject2).addHeader("Content-Location", paramString);
        localObject3 = getAttachments((MimeHeaders)localObject2);
        localObject1 = localObject3 == null ? null : (AttachmentPart)((Iterator)localObject3).next();
      }
      if (localObject1 == null)
      {
        localObject2 = getAttachments();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (AttachmentPart)((Iterator)localObject2).next();
          String str = ((AttachmentPart)localObject3).getContentId();
          if (str != null)
          {
            int i = str.indexOf("=");
            if (i > -1)
            {
              str = str.substring(1, i);
              if (str.equalsIgnoreCase(paramString))
              {
                localObject1 = localObject3;
                break;
              }
            }
          }
        }
      }
    }
    catch (Exception localException)
    {
      log.log(Level.SEVERE, "SAAJ0590.soap.unable.to.locate.attachment", new Object[] { paramString });
      throw new SOAPExceptionImpl(localException);
    }
    return (AttachmentPart)localObject1;
  }
  
  private final InputStream getHeaderBytes()
    throws IOException
  {
    SOAPPartImpl localSOAPPartImpl = (SOAPPartImpl)getSOAPPart();
    return localSOAPPartImpl.getContentAsStream();
  }
  
  private String convertToSingleLine(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if ((c != '\r') && (c != '\n') && (c != '\t')) {
        localStringBuffer.append(c);
      }
    }
    return localStringBuffer.toString();
  }
  
  private MimeMultipart getMimeMessage()
    throws SOAPException
  {
    try
    {
      SOAPPartImpl localSOAPPartImpl = (SOAPPartImpl)getSOAPPart();
      MimeBodyPart localMimeBodyPart = localSOAPPartImpl.getMimePart();
      ContentType localContentType1 = new ContentType(getExpectedContentType());
      if (!isFastInfoset) {
        localContentType1.setParameter("charset", initCharset());
      }
      localMimeBodyPart.setHeader("Content-Type", localContentType1.toString());
      Object localObject1 = null;
      if ((!switchOffBM) && (!switchOffLazyAttachment) && (multiPart != null) && (!attachmentsInitialized))
      {
        localObject1 = new BMMimeMultipart();
        ((MimeMultipart)localObject1).addBodyPart(localMimeBodyPart);
        if (attachments != null)
        {
          localObject2 = attachments.iterator();
          while (((Iterator)localObject2).hasNext()) {
            ((MimeMultipart)localObject1).addBodyPart(((AttachmentPartImpl)((Iterator)localObject2).next()).getMimePart());
          }
        }
        localObject2 = ((BMMimeMultipart)multiPart).getInputStream();
        if ((!((BMMimeMultipart)multiPart).lastBodyPartFound()) && (!((BMMimeMultipart)multiPart).isEndOfStream()))
        {
          ((BMMimeMultipart)localObject1).setInputStream((InputStream)localObject2);
          ((BMMimeMultipart)localObject1).setBoundary(((BMMimeMultipart)multiPart).getBoundary());
          ((BMMimeMultipart)localObject1).setLazyAttachments(lazyAttachments);
        }
      }
      else
      {
        localObject1 = new MimeMultipart();
        ((MimeMultipart)localObject1).addBodyPart(localMimeBodyPart);
        localObject2 = getAttachments();
        while (((Iterator)localObject2).hasNext()) {
          ((MimeMultipart)localObject1).addBodyPart(((AttachmentPartImpl)((Iterator)localObject2).next()).getMimePart());
        }
      }
      Object localObject2 = ((MimeMultipart)localObject1).getContentType();
      ParameterList localParameterList = ((ContentType)localObject2).getParameterList();
      localParameterList.set("type", getExpectedContentType());
      localParameterList.set("boundary", ((ContentType)localObject2).getParameter("boundary"));
      ContentType localContentType2 = new ContentType("multipart", "related", localParameterList);
      headers.setHeader("Content-Type", convertToSingleLine(localContentType2.toString()));
      return (MimeMultipart)localObject1;
    }
    catch (SOAPException localSOAPException)
    {
      throw localSOAPException;
    }
    catch (Throwable localThrowable)
    {
      log.severe("SAAJ0538.soap.cannot.convert.msg.to.multipart.obj");
      throw new SOAPExceptionImpl("Unable to convert SOAP message into a MimeMultipart object", localThrowable);
    }
  }
  
  private String initCharset()
  {
    String str = null;
    String[] arrayOfString = getMimeHeaders().getHeader("Content-Type");
    if ((arrayOfString != null) && (arrayOfString[0] != null)) {
      str = getCharsetString(arrayOfString[0]);
    }
    if (str == null) {
      str = (String)getProperty("javax.xml.soap.character-set-encoding");
    }
    if (str != null) {
      return str;
    }
    return "utf-8";
  }
  
  private String getCharsetString(String paramString)
  {
    try
    {
      int i = paramString.indexOf(";");
      if (i < 0) {
        return null;
      }
      ParameterList localParameterList = new ParameterList(paramString.substring(i));
      return localParameterList.get("charset");
    }
    catch (Exception localException) {}
    return null;
  }
  
  public void saveChanges()
    throws SOAPException
  {
    String str = initCharset();
    int i = attachments == null ? 0 : attachments.size();
    if ((i == 0) && (!switchOffBM) && (!switchOffLazyAttachment) && (!attachmentsInitialized) && (multiPart != null)) {
      i = 1;
    }
    try
    {
      if ((i == 0) && (!hasXOPContent()))
      {
        InputStream localInputStream;
        try
        {
          localInputStream = getHeaderBytes();
          optimizeAttachmentProcessing = false;
          if (SOAPPartImpl.lazyContentLength) {
            inputStreamAfterSaveChanges = localInputStream;
          }
        }
        catch (IOException localIOException)
        {
          log.severe("SAAJ0539.soap.cannot.get.header.stream");
          throw new SOAPExceptionImpl("Unable to get header stream in saveChanges: ", localIOException);
        }
        if ((localInputStream instanceof ByteInputStream))
        {
          ByteInputStream localByteInputStream = (ByteInputStream)localInputStream;
          messageBytes = localByteInputStream.getBytes();
          messageByteCount = localByteInputStream.getCount();
        }
        setFinalContentType(str);
        if (messageByteCount > 0) {
          headers.setHeader("Content-Length", Integer.toString(messageByteCount));
        }
      }
      else if (hasXOPContent())
      {
        mmp = getXOPMessage();
      }
      else
      {
        mmp = getMimeMessage();
      }
    }
    catch (Throwable localThrowable)
    {
      log.severe("SAAJ0540.soap.err.saving.multipart.msg");
      throw new SOAPExceptionImpl("Error during saving a multipart message", localThrowable);
    }
    saved = true;
  }
  
  private MimeMultipart getXOPMessage()
    throws SOAPException
  {
    try
    {
      MimeMultipart localMimeMultipart = new MimeMultipart();
      SOAPPartImpl localSOAPPartImpl = (SOAPPartImpl)getSOAPPart();
      MimeBodyPart localMimeBodyPart = localSOAPPartImpl.getMimePart();
      ContentType localContentType = new ContentType("application/xop+xml");
      localContentType.setParameter("type", getExpectedContentType());
      String str = initCharset();
      localContentType.setParameter("charset", str);
      localMimeBodyPart.setHeader("Content-Type", localContentType.toString());
      localMimeMultipart.addBodyPart(localMimeBodyPart);
      Object localObject1 = getAttachments();
      while (((Iterator)localObject1).hasNext()) {
        localMimeMultipart.addBodyPart(((AttachmentPartImpl)((Iterator)localObject1).next()).getMimePart());
      }
      localObject1 = localMimeMultipart.getContentType();
      ParameterList localParameterList = ((ContentType)localObject1).getParameterList();
      localParameterList.set("start-info", getExpectedContentType());
      localParameterList.set("type", "application/xop+xml");
      if (isCorrectSoapVersion(8))
      {
        localObject2 = getAction();
        if (localObject2 != null) {
          localParameterList.set("action", (String)localObject2);
        }
      }
      localParameterList.set("boundary", ((ContentType)localObject1).getParameter("boundary"));
      Object localObject2 = new ContentType("Multipart", "Related", localParameterList);
      headers.setHeader("Content-Type", convertToSingleLine(((ContentType)localObject2).toString()));
      return localMimeMultipart;
    }
    catch (SOAPException localSOAPException)
    {
      throw localSOAPException;
    }
    catch (Throwable localThrowable)
    {
      log.severe("SAAJ0538.soap.cannot.convert.msg.to.multipart.obj");
      throw new SOAPExceptionImpl("Unable to convert SOAP message into a MimeMultipart object", localThrowable);
    }
  }
  
  private boolean hasXOPContent()
    throws ParseException
  {
    String str = getContentType();
    if (str == null) {
      return false;
    }
    ContentType localContentType = new ContentType(str);
    return (isMimeMultipartXOPSoap1_1Package(localContentType)) || (isMimeMultipartXOPSoap1_2Package(localContentType)) || (isSOAPBodyXOPPackage(localContentType));
  }
  
  public void writeTo(OutputStream paramOutputStream)
    throws SOAPException, IOException
  {
    if (saveRequired())
    {
      optimizeAttachmentProcessing = true;
      saveChanges();
    }
    if (!optimizeAttachmentProcessing)
    {
      if ((SOAPPartImpl.lazyContentLength) && (messageByteCount <= 0))
      {
        byte[] arrayOfByte = new byte['Ѐ'];
        int i = 0;
        while ((i = inputStreamAfterSaveChanges.read(arrayOfByte)) != -1)
        {
          paramOutputStream.write(arrayOfByte, 0, i);
          messageByteCount += i;
        }
        if (messageByteCount > 0) {
          headers.setHeader("Content-Length", Integer.toString(messageByteCount));
        }
      }
      else
      {
        paramOutputStream.write(messageBytes, 0, messageByteCount);
      }
    }
    else {
      try
      {
        if (hasXOPContent())
        {
          mmp.writeTo(paramOutputStream);
        }
        else
        {
          mmp.writeTo(paramOutputStream);
          if ((!switchOffBM) && (!switchOffLazyAttachment) && (multiPart != null) && (!attachmentsInitialized)) {
            ((BMMimeMultipart)multiPart).setInputStream(((BMMimeMultipart)mmp).getInputStream());
          }
        }
      }
      catch (Exception localException)
      {
        log.severe("SAAJ0540.soap.err.saving.multipart.msg");
        throw new SOAPExceptionImpl("Error during saving a multipart message", localException);
      }
    }
    if (isCorrectSoapVersion(4))
    {
      String[] arrayOfString = headers.getHeader("SOAPAction");
      if ((arrayOfString == null) || (arrayOfString.length == 0)) {
        headers.setHeader("SOAPAction", "\"\"");
      }
    }
    messageBytes = null;
    needsSave();
  }
  
  public SOAPBody getSOAPBody()
    throws SOAPException
  {
    SOAPBody localSOAPBody = getSOAPPart().getEnvelope().getBody();
    return localSOAPBody;
  }
  
  public SOAPHeader getSOAPHeader()
    throws SOAPException
  {
    SOAPHeader localSOAPHeader = getSOAPPart().getEnvelope().getHeader();
    return localSOAPHeader;
  }
  
  private void initializeAllAttachments()
    throws MessagingException, SOAPException
  {
    if ((switchOffBM) || (switchOffLazyAttachment)) {
      return;
    }
    if ((attachmentsInitialized) || (multiPart == null)) {
      return;
    }
    if (attachments == null) {
      attachments = new FinalArrayList();
    }
    int i = multiPart.getCount();
    for (int j = 0; j < i; j++) {
      initializeAttachment(multiPart.getBodyPart(j));
    }
    attachmentsInitialized = true;
    needsSave();
  }
  
  private void initializeAttachment(MimeBodyPart paramMimeBodyPart)
    throws SOAPException
  {
    AttachmentPartImpl localAttachmentPartImpl = new AttachmentPartImpl();
    DataHandler localDataHandler = paramMimeBodyPart.getDataHandler();
    localAttachmentPartImpl.setDataHandler(localDataHandler);
    AttachmentPartImpl.copyMimeHeaders(paramMimeBodyPart, localAttachmentPartImpl);
    attachments.add(localAttachmentPartImpl);
  }
  
  private void initializeAttachment(MimeMultipart paramMimeMultipart, int paramInt)
    throws Exception
  {
    MimeBodyPart localMimeBodyPart = paramMimeMultipart.getBodyPart(paramInt);
    AttachmentPartImpl localAttachmentPartImpl = new AttachmentPartImpl();
    DataHandler localDataHandler = localMimeBodyPart.getDataHandler();
    localAttachmentPartImpl.setDataHandler(localDataHandler);
    AttachmentPartImpl.copyMimeHeaders(localMimeBodyPart, localAttachmentPartImpl);
    addAttachmentPart(localAttachmentPartImpl);
  }
  
  private void setMimeHeaders(SOAPPart paramSOAPPart, MimeBodyPart paramMimeBodyPart)
    throws Exception
  {
    paramSOAPPart.removeAllMimeHeaders();
    FinalArrayList localFinalArrayList = paramMimeBodyPart.getAllHeaders();
    int i = localFinalArrayList.size();
    for (int j = 0; j < i; j++)
    {
      Header localHeader = (Header)localFinalArrayList.get(j);
      paramSOAPPart.addMimeHeader(localHeader.getName(), localHeader.getValue());
    }
  }
  
  private void initCharsetProperty(ContentType paramContentType)
  {
    String str = paramContentType.getParameter("charset");
    if (str != null)
    {
      ((SOAPPartImpl)getSOAPPart()).setSourceCharsetEncoding(str);
      if (!str.equalsIgnoreCase("utf-8")) {
        setProperty("javax.xml.soap.character-set-encoding", str);
      }
    }
  }
  
  public void setLazyAttachments(boolean paramBoolean)
  {
    lazyAttachments = paramBoolean;
  }
  
  static
  {
    String str = SAAJUtil.getSystemProperty("saaj.mime.optimization");
    if ((str != null) && (str.equals("false"))) {
      switchOffBM = true;
    }
    str = SAAJUtil.getSystemProperty("saaj.lazy.mime.optimization");
    if ((str != null) && (str.equals("false"))) {
      switchOffLazyAttachment = true;
    }
  }
  
  private class MimeMatchingIterator
    implements Iterator
  {
    private Iterator iter;
    private MimeHeaders headers;
    private Object nextAttachment;
    
    public MimeMatchingIterator(MimeHeaders paramMimeHeaders)
    {
      headers = paramMimeHeaders;
      iter = attachments.iterator();
    }
    
    public boolean hasNext()
    {
      if (nextAttachment == null) {
        nextAttachment = nextMatch();
      }
      return nextAttachment != null;
    }
    
    public Object next()
    {
      if (nextAttachment != null)
      {
        Object localObject = nextAttachment;
        nextAttachment = null;
        return localObject;
      }
      if (hasNext()) {
        return nextAttachment;
      }
      return null;
    }
    
    Object nextMatch()
    {
      while (iter.hasNext())
      {
        AttachmentPartImpl localAttachmentPartImpl = (AttachmentPartImpl)iter.next();
        if (localAttachmentPartImpl.hasAllHeaders(headers)) {
          return localAttachmentPartImpl;
        }
      }
      return null;
    }
    
    public void remove()
    {
      iter.remove();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\MessageImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */