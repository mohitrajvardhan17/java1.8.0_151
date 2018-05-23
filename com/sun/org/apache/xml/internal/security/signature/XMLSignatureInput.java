package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_OmitComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315OmitComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.CanonicalizerBase;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityRuntimeException;
import com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XMLSignatureInput
{
  private InputStream inputOctetStreamProxy = null;
  private Set<Node> inputNodeSet = null;
  private Node subNode = null;
  private Node excludeNode = null;
  private boolean excludeComments = false;
  private boolean isNodeSet = false;
  private byte[] bytes = null;
  private String mimeType = null;
  private String sourceURI = null;
  private List<NodeFilter> nodeFilters = new ArrayList();
  private boolean needsToBeExpanded = false;
  private OutputStream outputStream = null;
  private DocumentBuilderFactory dfactory;
  
  public XMLSignatureInput(byte[] paramArrayOfByte)
  {
    bytes = paramArrayOfByte;
  }
  
  public XMLSignatureInput(InputStream paramInputStream)
  {
    inputOctetStreamProxy = paramInputStream;
  }
  
  public XMLSignatureInput(Node paramNode)
  {
    subNode = paramNode;
  }
  
  public XMLSignatureInput(Set<Node> paramSet)
  {
    inputNodeSet = paramSet;
  }
  
  public boolean isNeedsToBeExpanded()
  {
    return needsToBeExpanded;
  }
  
  public void setNeedsToBeExpanded(boolean paramBoolean)
  {
    needsToBeExpanded = paramBoolean;
  }
  
  public Set<Node> getNodeSet()
    throws CanonicalizationException, ParserConfigurationException, IOException, SAXException
  {
    return getNodeSet(false);
  }
  
  public Set<Node> getInputNodeSet()
  {
    return inputNodeSet;
  }
  
  public Set<Node> getNodeSet(boolean paramBoolean)
    throws ParserConfigurationException, IOException, SAXException, CanonicalizationException
  {
    if (inputNodeSet != null) {
      return inputNodeSet;
    }
    if ((inputOctetStreamProxy == null) && (subNode != null))
    {
      if (paramBoolean) {
        XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(subNode));
      }
      inputNodeSet = new LinkedHashSet();
      XMLUtils.getSet(subNode, inputNodeSet, excludeNode, excludeComments);
      return inputNodeSet;
    }
    if (isOctetStream())
    {
      convertToNodes();
      LinkedHashSet localLinkedHashSet = new LinkedHashSet();
      XMLUtils.getSet(subNode, localLinkedHashSet, null, false);
      return localLinkedHashSet;
    }
    throw new RuntimeException("getNodeSet() called but no input data present");
  }
  
  public InputStream getOctetStream()
    throws IOException
  {
    if (inputOctetStreamProxy != null) {
      return inputOctetStreamProxy;
    }
    if (bytes != null)
    {
      inputOctetStreamProxy = new ByteArrayInputStream(bytes);
      return inputOctetStreamProxy;
    }
    return null;
  }
  
  public InputStream getOctetStreamReal()
  {
    return inputOctetStreamProxy;
  }
  
  public byte[] getBytes()
    throws IOException, CanonicalizationException
  {
    byte[] arrayOfByte = getBytesFromInputStream();
    if (arrayOfByte != null) {
      return arrayOfByte;
    }
    Canonicalizer20010315OmitComments localCanonicalizer20010315OmitComments = new Canonicalizer20010315OmitComments();
    bytes = localCanonicalizer20010315OmitComments.engineCanonicalize(this);
    return bytes;
  }
  
  public boolean isNodeSet()
  {
    return ((inputOctetStreamProxy == null) && (inputNodeSet != null)) || (isNodeSet);
  }
  
  public boolean isElement()
  {
    return (inputOctetStreamProxy == null) && (subNode != null) && (inputNodeSet == null) && (!isNodeSet);
  }
  
  public boolean isOctetStream()
  {
    return ((inputOctetStreamProxy != null) || (bytes != null)) && (inputNodeSet == null) && (subNode == null);
  }
  
  public boolean isOutputStreamSet()
  {
    return outputStream != null;
  }
  
  public boolean isByteArray()
  {
    return (bytes != null) && (inputNodeSet == null) && (subNode == null);
  }
  
  public boolean isInitialized()
  {
    return (isOctetStream()) || (isNodeSet());
  }
  
  public String getMIMEType()
  {
    return mimeType;
  }
  
  public void setMIMEType(String paramString)
  {
    mimeType = paramString;
  }
  
  public String getSourceURI()
  {
    return sourceURI;
  }
  
  public void setSourceURI(String paramString)
  {
    sourceURI = paramString;
  }
  
  public String toString()
  {
    if (isNodeSet()) {
      return "XMLSignatureInput/NodeSet/" + inputNodeSet.size() + " nodes/" + getSourceURI();
    }
    if (isElement()) {
      return "XMLSignatureInput/Element/" + subNode + " exclude " + excludeNode + " comments:" + excludeComments + "/" + getSourceURI();
    }
    try
    {
      return "XMLSignatureInput/OctetStream/" + getBytes().length + " octets/" + getSourceURI();
    }
    catch (IOException localIOException)
    {
      return "XMLSignatureInput/OctetStream//" + getSourceURI();
    }
    catch (CanonicalizationException localCanonicalizationException) {}
    return "XMLSignatureInput/OctetStream//" + getSourceURI();
  }
  
  public String getHTMLRepresentation()
    throws XMLSignatureException
  {
    XMLSignatureInputDebugger localXMLSignatureInputDebugger = new XMLSignatureInputDebugger(this);
    return localXMLSignatureInputDebugger.getHTMLRepresentation();
  }
  
  public String getHTMLRepresentation(Set<String> paramSet)
    throws XMLSignatureException
  {
    XMLSignatureInputDebugger localXMLSignatureInputDebugger = new XMLSignatureInputDebugger(this, paramSet);
    return localXMLSignatureInputDebugger.getHTMLRepresentation();
  }
  
  public Node getExcludeNode()
  {
    return excludeNode;
  }
  
  public void setExcludeNode(Node paramNode)
  {
    excludeNode = paramNode;
  }
  
  public Node getSubNode()
  {
    return subNode;
  }
  
  public boolean isExcludeComments()
  {
    return excludeComments;
  }
  
  public void setExcludeComments(boolean paramBoolean)
  {
    excludeComments = paramBoolean;
  }
  
  public void updateOutputStream(OutputStream paramOutputStream)
    throws CanonicalizationException, IOException
  {
    updateOutputStream(paramOutputStream, false);
  }
  
  public void updateOutputStream(OutputStream paramOutputStream, boolean paramBoolean)
    throws CanonicalizationException, IOException
  {
    if (paramOutputStream == outputStream) {
      return;
    }
    if (bytes != null)
    {
      paramOutputStream.write(bytes);
    }
    else
    {
      Object localObject;
      if (inputOctetStreamProxy == null)
      {
        localObject = null;
        if (paramBoolean) {
          localObject = new Canonicalizer11_OmitComments();
        } else {
          localObject = new Canonicalizer20010315OmitComments();
        }
        ((CanonicalizerBase)localObject).setWriter(paramOutputStream);
        ((CanonicalizerBase)localObject).engineCanonicalize(this);
      }
      else
      {
        localObject = new byte['က'];
        int i = 0;
        try
        {
          while ((i = inputOctetStreamProxy.read((byte[])localObject)) != -1) {
            paramOutputStream.write((byte[])localObject, 0, i);
          }
        }
        catch (IOException localIOException)
        {
          inputOctetStreamProxy.close();
          throw localIOException;
        }
      }
    }
  }
  
  public void setOutputStream(OutputStream paramOutputStream)
  {
    outputStream = paramOutputStream;
  }
  
  /* Error */
  private byte[] getBytesFromInputStream()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 294	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:bytes	[B
    //   4: ifnull +8 -> 12
    //   7: aload_0
    //   8: getfield 294	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:bytes	[B
    //   11: areturn
    //   12: aload_0
    //   13: getfield 295	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:inputOctetStreamProxy	Ljava/io/InputStream;
    //   16: ifnonnull +5 -> 21
    //   19: aconst_null
    //   20: areturn
    //   21: aload_0
    //   22: aload_0
    //   23: getfield 295	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:inputOctetStreamProxy	Ljava/io/InputStream;
    //   26: invokestatic 325	com/sun/org/apache/xml/internal/security/utils/JavaUtils:getBytesFromStream	(Ljava/io/InputStream;)[B
    //   29: putfield 294	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:bytes	[B
    //   32: aload_0
    //   33: getfield 295	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:inputOctetStreamProxy	Ljava/io/InputStream;
    //   36: invokevirtual 333	java/io/InputStream:close	()V
    //   39: goto +13 -> 52
    //   42: astore_1
    //   43: aload_0
    //   44: getfield 295	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:inputOctetStreamProxy	Ljava/io/InputStream;
    //   47: invokevirtual 333	java/io/InputStream:close	()V
    //   50: aload_1
    //   51: athrow
    //   52: aload_0
    //   53: getfield 294	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:bytes	[B
    //   56: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	57	0	this	XMLSignatureInput
    //   42	9	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   21	32	42	finally
  }
  
  public void addNodeFilter(NodeFilter paramNodeFilter)
  {
    if (isOctetStream()) {
      try
      {
        convertToNodes();
      }
      catch (Exception localException)
      {
        throw new XMLSecurityRuntimeException("signature.XMLSignatureInput.nodesetReference", localException);
      }
    }
    nodeFilters.add(paramNodeFilter);
  }
  
  public List<NodeFilter> getNodeFilters()
  {
    return nodeFilters;
  }
  
  public void setNodeSet(boolean paramBoolean)
  {
    isNodeSet = paramBoolean;
  }
  
  void convertToNodes()
    throws CanonicalizationException, ParserConfigurationException, IOException, SAXException
  {
    if (dfactory == null)
    {
      dfactory = DocumentBuilderFactory.newInstance();
      dfactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
      dfactory.setValidating(false);
      dfactory.setNamespaceAware(true);
    }
    DocumentBuilder localDocumentBuilder = dfactory.newDocumentBuilder();
    try
    {
      localDocumentBuilder.setErrorHandler(new IgnoreAllErrorHandler());
      Document localDocument1 = localDocumentBuilder.parse(getOctetStream());
      subNode = localDocument1;
    }
    catch (SAXException localSAXException)
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      localByteArrayOutputStream.write("<container>".getBytes("UTF-8"));
      localByteArrayOutputStream.write(getBytes());
      localByteArrayOutputStream.write("</container>".getBytes("UTF-8"));
      byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
      Document localDocument2 = localDocumentBuilder.parse(new ByteArrayInputStream(arrayOfByte));
      subNode = localDocument2.getDocumentElement().getFirstChild().getFirstChild();
    }
    finally
    {
      if (inputOctetStreamProxy != null) {
        inputOctetStreamProxy.close();
      }
      inputOctetStreamProxy = null;
      bytes = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\signature\XMLSignatureInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */