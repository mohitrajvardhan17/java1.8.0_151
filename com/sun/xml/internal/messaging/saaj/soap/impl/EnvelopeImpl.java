package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.Envelope;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import com.sun.xml.internal.messaging.saaj.util.transform.EfficientStreamingTransformer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

public abstract class EnvelopeImpl
  extends ElementImpl
  implements Envelope
{
  protected HeaderImpl header;
  protected BodyImpl body;
  String omitXmlDecl = "yes";
  String charset = "utf-8";
  String xmlDecl = null;
  
  protected EnvelopeImpl(SOAPDocumentImpl paramSOAPDocumentImpl, Name paramName)
  {
    super(paramSOAPDocumentImpl, paramName);
  }
  
  protected EnvelopeImpl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName)
  {
    super(paramSOAPDocumentImpl, paramQName);
  }
  
  protected EnvelopeImpl(SOAPDocumentImpl paramSOAPDocumentImpl, NameImpl paramNameImpl, boolean paramBoolean1, boolean paramBoolean2)
    throws SOAPException
  {
    this(paramSOAPDocumentImpl, paramNameImpl);
    ensureNamespaceIsDeclared(getElementQName().getPrefix(), getElementQName().getNamespaceURI());
    if (paramBoolean1) {
      addHeader();
    }
    if (paramBoolean2) {
      addBody();
    }
  }
  
  protected abstract NameImpl getHeaderName(String paramString);
  
  protected abstract NameImpl getBodyName(String paramString);
  
  public SOAPHeader addHeader()
    throws SOAPException
  {
    return addHeader(null);
  }
  
  public SOAPHeader addHeader(String paramString)
    throws SOAPException
  {
    if ((paramString == null) || (paramString.equals(""))) {
      paramString = getPrefix();
    }
    NameImpl localNameImpl1 = getHeaderName(paramString);
    NameImpl localNameImpl2 = getBodyName(paramString);
    HeaderImpl localHeaderImpl = null;
    SOAPElement localSOAPElement = null;
    Iterator localIterator = getChildElementNodes();
    if (localIterator.hasNext())
    {
      localSOAPElement = (SOAPElement)localIterator.next();
      if (localSOAPElement.getElementName().equals(localNameImpl1))
      {
        log.severe("SAAJ0120.impl.header.already.exists");
        throw new SOAPExceptionImpl("Can't add a header when one is already present.");
      }
      if (!localSOAPElement.getElementName().equals(localNameImpl2))
      {
        log.severe("SAAJ0121.impl.invalid.first.child.of.envelope");
        throw new SOAPExceptionImpl("First child of Envelope must be either a Header or Body");
      }
    }
    localHeaderImpl = (HeaderImpl)createElement(localNameImpl1);
    insertBefore(localHeaderImpl, localSOAPElement);
    localHeaderImpl.ensureNamespaceIsDeclared(localNameImpl1.getPrefix(), localNameImpl1.getURI());
    return localHeaderImpl;
  }
  
  protected void lookForHeader()
    throws SOAPException
  {
    NameImpl localNameImpl = getHeaderName(null);
    HeaderImpl localHeaderImpl = (HeaderImpl)findChild(localNameImpl);
    header = localHeaderImpl;
  }
  
  public SOAPHeader getHeader()
    throws SOAPException
  {
    lookForHeader();
    return header;
  }
  
  protected void lookForBody()
    throws SOAPException
  {
    NameImpl localNameImpl = getBodyName(null);
    BodyImpl localBodyImpl = (BodyImpl)findChild(localNameImpl);
    body = localBodyImpl;
  }
  
  public SOAPBody addBody()
    throws SOAPException
  {
    return addBody(null);
  }
  
  public SOAPBody addBody(String paramString)
    throws SOAPException
  {
    lookForBody();
    if ((paramString == null) || (paramString.equals(""))) {
      paramString = getPrefix();
    }
    if (body == null)
    {
      NameImpl localNameImpl = getBodyName(paramString);
      body = ((BodyImpl)createElement(localNameImpl));
      insertBefore(body, null);
      body.ensureNamespaceIsDeclared(localNameImpl.getPrefix(), localNameImpl.getURI());
    }
    else
    {
      log.severe("SAAJ0122.impl.body.already.exists");
      throw new SOAPExceptionImpl("Can't add a body when one is already present.");
    }
    return body;
  }
  
  protected SOAPElement addElement(Name paramName)
    throws SOAPException
  {
    if (getBodyName(null).equals(paramName)) {
      return addBody(paramName.getPrefix());
    }
    if (getHeaderName(null).equals(paramName)) {
      return addHeader(paramName.getPrefix());
    }
    return super.addElement(paramName);
  }
  
  protected SOAPElement addElement(QName paramQName)
    throws SOAPException
  {
    if (getBodyName(null).equals(NameImpl.convertToName(paramQName))) {
      return addBody(paramQName.getPrefix());
    }
    if (getHeaderName(null).equals(NameImpl.convertToName(paramQName))) {
      return addHeader(paramQName.getPrefix());
    }
    return super.addElement(paramQName);
  }
  
  public SOAPBody getBody()
    throws SOAPException
  {
    lookForBody();
    return body;
  }
  
  public Source getContent()
  {
    return new DOMSource(getOwnerDocument());
  }
  
  public Name createName(String paramString1, String paramString2, String paramString3)
    throws SOAPException
  {
    if ("xmlns".equals(paramString2))
    {
      log.severe("SAAJ0123.impl.no.reserved.xmlns");
      throw new SOAPExceptionImpl("Cannot declare reserved xmlns prefix");
    }
    if ((paramString2 == null) && ("xmlns".equals(paramString1)))
    {
      log.severe("SAAJ0124.impl.qualified.name.cannot.be.xmlns");
      throw new SOAPExceptionImpl("Qualified name cannot be xmlns");
    }
    return NameImpl.create(paramString1, paramString2, paramString3);
  }
  
  public Name createName(String paramString1, String paramString2)
    throws SOAPException
  {
    String str = getNamespaceURI(paramString2);
    if (str == null)
    {
      log.log(Level.SEVERE, "SAAJ0126.impl.cannot.locate.ns", new String[] { paramString2 });
      throw new SOAPExceptionImpl("Unable to locate namespace for prefix " + paramString2);
    }
    return NameImpl.create(paramString1, paramString2, str);
  }
  
  public Name createName(String paramString)
    throws SOAPException
  {
    return NameImpl.createFromUnqualifiedName(paramString);
  }
  
  public void setOmitXmlDecl(String paramString)
  {
    omitXmlDecl = paramString;
  }
  
  public void setXmlDecl(String paramString)
  {
    xmlDecl = paramString;
  }
  
  private String getOmitXmlDecl()
  {
    return omitXmlDecl;
  }
  
  public void setCharsetEncoding(String paramString)
  {
    charset = paramString;
  }
  
  public void output(OutputStream paramOutputStream)
    throws IOException
  {
    try
    {
      Transformer localTransformer = EfficientStreamingTransformer.newTransformer();
      localTransformer.setOutputProperty("omit-xml-declaration", "yes");
      localTransformer.setOutputProperty("encoding", charset);
      if ((omitXmlDecl.equals("no")) && (xmlDecl == null)) {
        xmlDecl = ("<?xml version=\"" + getOwnerDocument().getXmlVersion() + "\" encoding=\"" + charset + "\" ?>");
      }
      StreamResult localStreamResult = new StreamResult(paramOutputStream);
      if (xmlDecl != null)
      {
        OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(paramOutputStream, charset);
        localOutputStreamWriter.write(xmlDecl);
        localOutputStreamWriter.flush();
        localStreamResult = new StreamResult(localOutputStreamWriter);
      }
      if (log.isLoggable(Level.FINE))
      {
        log.log(Level.FINE, "SAAJ0190.impl.set.xml.declaration", new String[] { omitXmlDecl });
        log.log(Level.FINE, "SAAJ0191.impl.set.encoding", new String[] { charset });
      }
      localTransformer.transform(getContent(), localStreamResult);
    }
    catch (Exception localException)
    {
      throw new IOException(localException.getMessage());
    }
  }
  
  public void output(OutputStream paramOutputStream, boolean paramBoolean)
    throws IOException
  {
    if (!paramBoolean) {
      output(paramOutputStream);
    } else {
      try
      {
        Source localSource = getContent();
        Transformer localTransformer = EfficientStreamingTransformer.newTransformer();
        localTransformer.transform(getContent(), FastInfosetReflection.FastInfosetResult_new(paramOutputStream));
      }
      catch (Exception localException)
      {
        throw new IOException(localException.getMessage());
      }
    }
  }
  
  public SOAPElement setElementQName(QName paramQName)
    throws SOAPException
  {
    log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[] { elementQName.getLocalPart(), paramQName.getLocalPart() });
    throw new SOAPException("Cannot change name for " + elementQName.getLocalPart() + " to " + paramQName.getLocalPart());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\impl\EnvelopeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */