package com.sun.xml.internal.ws.fault;

import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
import com.sun.xml.internal.ws.developer.ServerSideException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@XmlRootElement(namespace="http://jax-ws.dev.java.net/", name="exception")
final class ExceptionBean
{
  @XmlAttribute(name="class")
  public String className;
  @XmlElement
  public String message;
  @XmlElementWrapper(namespace="http://jax-ws.dev.java.net/", name="stackTrace")
  @XmlElement(namespace="http://jax-ws.dev.java.net/", name="frame")
  public List<StackFrame> stackTrace = new ArrayList();
  @XmlElement(namespace="http://jax-ws.dev.java.net/", name="cause")
  public ExceptionBean cause;
  @XmlAttribute
  public String note = "To disable this feature, set " + SOAPFaultBuilder.CAPTURE_STACK_TRACE_PROPERTY + " system property to false";
  private static final JAXBContext JAXB_CONTEXT;
  static final String NS = "http://jax-ws.dev.java.net/";
  static final String LOCAL_NAME = "exception";
  private static final NamespacePrefixMapper nsp = new NamespacePrefixMapper()
  {
    public String getPreferredPrefix(String paramAnonymousString1, String paramAnonymousString2, boolean paramAnonymousBoolean)
    {
      if ("http://jax-ws.dev.java.net/".equals(paramAnonymousString1)) {
        return "";
      }
      return paramAnonymousString2;
    }
  };
  
  public static void marshal(Throwable paramThrowable, Node paramNode)
    throws JAXBException
  {
    Marshaller localMarshaller = JAXB_CONTEXT.createMarshaller();
    try
    {
      localMarshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", nsp);
    }
    catch (PropertyException localPropertyException) {}
    localMarshaller.marshal(new ExceptionBean(paramThrowable), paramNode);
  }
  
  public static ServerSideException unmarshal(Node paramNode)
    throws JAXBException
  {
    ExceptionBean localExceptionBean = (ExceptionBean)JAXB_CONTEXT.createUnmarshaller().unmarshal(paramNode);
    return localExceptionBean.toException();
  }
  
  ExceptionBean() {}
  
  private ExceptionBean(Throwable paramThrowable)
  {
    className = paramThrowable.getClass().getName();
    message = paramThrowable.getMessage();
    for (StackTraceElement localStackTraceElement : paramThrowable.getStackTrace()) {
      stackTrace.add(new StackFrame(localStackTraceElement));
    }
    ??? = paramThrowable.getCause();
    if ((paramThrowable != ???) && (??? != null)) {
      cause = new ExceptionBean((Throwable)???);
    }
  }
  
  private ServerSideException toException()
  {
    ServerSideException localServerSideException = new ServerSideException(className, message);
    if (stackTrace != null)
    {
      StackTraceElement[] arrayOfStackTraceElement = new StackTraceElement[stackTrace.size()];
      for (int i = 0; i < stackTrace.size(); i++) {
        arrayOfStackTraceElement[i] = ((StackFrame)stackTrace.get(i)).toStackTraceElement();
      }
      localServerSideException.setStackTrace(arrayOfStackTraceElement);
    }
    if (cause != null) {
      localServerSideException.initCause(cause.toException());
    }
    return localServerSideException;
  }
  
  public static boolean isStackTraceXml(Element paramElement)
  {
    return ("exception".equals(paramElement.getLocalName())) && ("http://jax-ws.dev.java.net/".equals(paramElement.getNamespaceURI()));
  }
  
  static
  {
    try
    {
      JAXB_CONTEXT = JAXBContext.newInstance(new Class[] { ExceptionBean.class });
    }
    catch (JAXBException localJAXBException)
    {
      throw new Error(localJAXBException);
    }
  }
  
  static final class StackFrame
  {
    @XmlAttribute(name="class")
    public String declaringClass;
    @XmlAttribute(name="method")
    public String methodName;
    @XmlAttribute(name="file")
    public String fileName;
    @XmlAttribute(name="line")
    public String lineNumber;
    
    StackFrame() {}
    
    public StackFrame(StackTraceElement paramStackTraceElement)
    {
      declaringClass = paramStackTraceElement.getClassName();
      methodName = paramStackTraceElement.getMethodName();
      fileName = paramStackTraceElement.getFileName();
      lineNumber = box(paramStackTraceElement.getLineNumber());
    }
    
    private String box(int paramInt)
    {
      if (paramInt >= 0) {
        return String.valueOf(paramInt);
      }
      if (paramInt == -2) {
        return "native";
      }
      return "unknown";
    }
    
    private int unbox(String paramString)
    {
      try
      {
        return Integer.parseInt(paramString);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        if ("native".equals(paramString)) {
          return -2;
        }
      }
      return -1;
    }
    
    private StackTraceElement toStackTraceElement()
    {
      return new StackTraceElement(declaringClass, methodName, fileName, unbox(lineNumber));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\fault\ExceptionBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */