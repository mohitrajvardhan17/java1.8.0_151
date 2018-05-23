package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMException;
import com.sun.org.apache.xml.internal.dtm.DTMFilter;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.ref.dom2dtm.DOM2DTM;
import com.sun.org.apache.xml.internal.dtm.ref.dom2dtm.DOM2DTMdefaultNamespaceDeclarationNode;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2RTFDTM;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xml.internal.utils.XMLReaderManager;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import java.io.PrintStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class DTMManagerDefault
  extends DTMManager
{
  private static final boolean DUMPTREE = false;
  private static final boolean DEBUG = false;
  protected DTM[] m_dtms = new DTM['Ā'];
  int[] m_dtm_offsets = new int['Ā'];
  protected XMLReaderManager m_readerManager = null;
  protected DefaultHandler m_defaultHandler = new DefaultHandler();
  private ExpandedNameTable m_expandedNameTable = new ExpandedNameTable();
  
  public synchronized void addDTM(DTM paramDTM, int paramInt)
  {
    addDTM(paramDTM, paramInt, 0);
  }
  
  public synchronized void addDTM(DTM paramDTM, int paramInt1, int paramInt2)
  {
    if (paramInt1 >= 65536) {
      throw new DTMException(XMLMessages.createXMLMessage("ER_NO_DTMIDS_AVAIL", null));
    }
    int i = m_dtms.length;
    if (i <= paramInt1)
    {
      int j = Math.min(paramInt1 + 256, 65536);
      DTM[] arrayOfDTM = new DTM[j];
      System.arraycopy(m_dtms, 0, arrayOfDTM, 0, i);
      m_dtms = arrayOfDTM;
      int[] arrayOfInt = new int[j];
      System.arraycopy(m_dtm_offsets, 0, arrayOfInt, 0, i);
      m_dtm_offsets = arrayOfInt;
    }
    m_dtms[paramInt1] = paramDTM;
    m_dtm_offsets[paramInt1] = paramInt2;
    paramDTM.documentRegistration();
  }
  
  public synchronized int getFirstFreeDTMID()
  {
    int i = m_dtms.length;
    for (int j = 1; j < i; j++) {
      if (null == m_dtms[j]) {
        return j;
      }
    }
    return i;
  }
  
  public DTMManagerDefault() {}
  
  public synchronized DTM getDTM(Source paramSource, boolean paramBoolean1, DTMWSFilter paramDTMWSFilter, boolean paramBoolean2, boolean paramBoolean3)
  {
    XMLStringFactory localXMLStringFactory = m_xsf;
    int i = getFirstFreeDTMID();
    int j = i << 16;
    if ((null != paramSource) && ((paramSource instanceof DOMSource)))
    {
      DOM2DTM localDOM2DTM = new DOM2DTM(this, (DOMSource)paramSource, j, paramDTMWSFilter, localXMLStringFactory, paramBoolean3);
      addDTM(localDOM2DTM, i, 0);
      return localDOM2DTM;
    }
    int k = null != paramSource ? paramSource instanceof SAXSource : 1;
    int m = null != paramSource ? paramSource instanceof StreamSource : 0;
    if ((k != 0) || (m != 0))
    {
      XMLReader localXMLReader = null;
      try
      {
        InputSource localInputSource;
        if (null == paramSource)
        {
          localInputSource = null;
        }
        else
        {
          localXMLReader = getXMLReader(paramSource);
          localInputSource = SAXSource.sourceToInputSource(paramSource);
          String str = localInputSource.getSystemId();
          if (null != str)
          {
            try
            {
              str = SystemIDResolver.getAbsoluteURI(str);
            }
            catch (Exception localException1)
            {
              System.err.println("Can not absolutize URL: " + str);
            }
            localInputSource.setSystemId(str);
          }
        }
        Object localObject1;
        if ((paramSource == null) && (paramBoolean1) && (!paramBoolean2) && (!paramBoolean3)) {
          localObject1 = new SAX2RTFDTM(this, paramSource, j, paramDTMWSFilter, localXMLStringFactory, paramBoolean3);
        } else {
          localObject1 = new SAX2DTM(this, paramSource, j, paramDTMWSFilter, localXMLStringFactory, paramBoolean3);
        }
        addDTM((DTM)localObject1, i, 0);
        int n = (null != localXMLReader) && (localXMLReader.getClass().getName().equals("com.sun.org.apache.xerces.internal.parsers.SAXParser")) ? 1 : 0;
        if (n != 0) {
          paramBoolean2 = true;
        }
        Object localObject2;
        if ((m_incremental) && (paramBoolean2))
        {
          localObject2 = null;
          if (n != 0) {
            try
            {
              localObject2 = (IncrementalSAXSource)Class.forName("com.sun.org.apache.xml.internal.dtm.ref.IncrementalSAXSource_Xerces").newInstance();
            }
            catch (Exception localException3)
            {
              localException3.printStackTrace();
              localObject2 = null;
            }
          }
          Object localObject4;
          if (localObject2 == null) {
            if (null == localXMLReader)
            {
              localObject2 = new IncrementalSAXSource_Filter();
            }
            else
            {
              localObject4 = new IncrementalSAXSource_Filter();
              ((IncrementalSAXSource_Filter)localObject4).setXMLReader(localXMLReader);
              localObject2 = localObject4;
            }
          }
          ((SAX2DTM)localObject1).setIncrementalSAXSource((IncrementalSAXSource)localObject2);
          if (null == localInputSource)
          {
            localObject4 = localObject1;
            return (DTM)localObject4;
          }
          if (null == localXMLReader.getErrorHandler()) {
            localXMLReader.setErrorHandler((ErrorHandler)localObject1);
          }
          localXMLReader.setDTDHandler((DTDHandler)localObject1);
          try
          {
            ((IncrementalSAXSource)localObject2).startParse(localInputSource);
          }
          catch (RuntimeException localRuntimeException2)
          {
            ((SAX2DTM)localObject1).clearCoRoutine();
            throw localRuntimeException2;
          }
          catch (Exception localException4)
          {
            ((SAX2DTM)localObject1).clearCoRoutine();
            throw new WrappedRuntimeException(localException4);
          }
        }
        else
        {
          if (null == localXMLReader)
          {
            localObject2 = localObject1;
            return (DTM)localObject2;
          }
          localXMLReader.setContentHandler((ContentHandler)localObject1);
          localXMLReader.setDTDHandler((DTDHandler)localObject1);
          if (null == localXMLReader.getErrorHandler()) {
            localXMLReader.setErrorHandler((ErrorHandler)localObject1);
          }
          try
          {
            localXMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", localObject1);
          }
          catch (SAXNotRecognizedException localSAXNotRecognizedException) {}catch (SAXNotSupportedException localSAXNotSupportedException) {}
          try
          {
            localXMLReader.parse(localInputSource);
          }
          catch (RuntimeException localRuntimeException1)
          {
            ((SAX2DTM)localObject1).clearCoRoutine();
            throw localRuntimeException1;
          }
          catch (Exception localException2)
          {
            ((SAX2DTM)localObject1).clearCoRoutine();
            throw new WrappedRuntimeException(localException2);
          }
        }
        Object localObject3 = localObject1;
        return (DTM)localObject3;
      }
      finally
      {
        if ((localXMLReader != null) && ((!m_incremental) || (!paramBoolean2)))
        {
          localXMLReader.setContentHandler(m_defaultHandler);
          localXMLReader.setDTDHandler(m_defaultHandler);
          localXMLReader.setErrorHandler(m_defaultHandler);
          try
          {
            localXMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", null);
          }
          catch (Exception localException8) {}
        }
        releaseXMLReader(localXMLReader);
      }
    }
    throw new DTMException(XMLMessages.createXMLMessage("ER_NOT_SUPPORTED", new Object[] { paramSource }));
  }
  
  public synchronized int getDTMHandleFromNode(Node paramNode)
  {
    if (null == paramNode) {
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_NODE_NON_NULL", null));
    }
    if ((paramNode instanceof DTMNodeProxy)) {
      return ((DTMNodeProxy)paramNode).getDTMNodeNumber();
    }
    int i = m_dtms.length;
    for (int j = 0; j < i; j++)
    {
      localObject2 = m_dtms[j];
      if ((null != localObject2) && ((localObject2 instanceof DOM2DTM)))
      {
        int k = ((DOM2DTM)localObject2).getHandleOfNode(paramNode);
        if (k != -1) {
          return k;
        }
      }
    }
    Object localObject1 = paramNode;
    for (Object localObject2 = ((Node)localObject1).getNodeType() == 2 ? ((Attr)localObject1).getOwnerElement() : ((Node)localObject1).getParentNode(); localObject2 != null; localObject2 = ((Node)localObject2).getParentNode()) {
      localObject1 = localObject2;
    }
    DOM2DTM localDOM2DTM = (DOM2DTM)getDTM(new DOMSource((Node)localObject1), false, null, true, true);
    int m;
    if ((paramNode instanceof DOM2DTMdefaultNamespaceDeclarationNode))
    {
      m = localDOM2DTM.getHandleOfNode(((Attr)paramNode).getOwnerElement());
      m = localDOM2DTM.getAttributeNode(m, paramNode.getNamespaceURI(), paramNode.getLocalName());
    }
    else
    {
      m = localDOM2DTM.getHandleOfNode(paramNode);
    }
    if (-1 == m) {
      throw new RuntimeException(XMLMessages.createXMLMessage("ER_COULD_NOT_RESOLVE_NODE", null));
    }
    return m;
  }
  
  public synchronized XMLReader getXMLReader(Source paramSource)
  {
    try
    {
      XMLReader localXMLReader = (paramSource instanceof SAXSource) ? ((SAXSource)paramSource).getXMLReader() : null;
      if (null == localXMLReader)
      {
        if (m_readerManager == null) {
          m_readerManager = XMLReaderManager.getInstance(super.useServicesMechnism());
        }
        localXMLReader = m_readerManager.getXMLReader();
      }
      return localXMLReader;
    }
    catch (SAXException localSAXException)
    {
      throw new DTMException(localSAXException.getMessage(), localSAXException);
    }
  }
  
  public synchronized void releaseXMLReader(XMLReader paramXMLReader)
  {
    if (m_readerManager != null) {
      m_readerManager.releaseXMLReader(paramXMLReader);
    }
  }
  
  public synchronized DTM getDTM(int paramInt)
  {
    try
    {
      return m_dtms[(paramInt >>> 16)];
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      if (paramInt == -1) {
        return null;
      }
      throw localArrayIndexOutOfBoundsException;
    }
  }
  
  public synchronized int getDTMIdentity(DTM paramDTM)
  {
    if ((paramDTM instanceof DTMDefaultBase))
    {
      DTMDefaultBase localDTMDefaultBase = (DTMDefaultBase)paramDTM;
      if (localDTMDefaultBase.getManager() == this) {
        return localDTMDefaultBase.getDTMIDs().elementAt(0);
      }
      return -1;
    }
    int i = m_dtms.length;
    for (int j = 0; j < i; j++)
    {
      DTM localDTM = m_dtms[j];
      if ((localDTM == paramDTM) && (m_dtm_offsets[j] == 0)) {
        return j << 16;
      }
    }
    return -1;
  }
  
  public synchronized boolean release(DTM paramDTM, boolean paramBoolean)
  {
    if ((paramDTM instanceof SAX2DTM)) {
      ((SAX2DTM)paramDTM).clearCoRoutine();
    }
    if ((paramDTM instanceof DTMDefaultBase))
    {
      SuballocatedIntVector localSuballocatedIntVector = ((DTMDefaultBase)paramDTM).getDTMIDs();
      for (int j = localSuballocatedIntVector.size() - 1; j >= 0; j--) {
        m_dtms[(localSuballocatedIntVector.elementAt(j) >>> 16)] = null;
      }
    }
    else
    {
      int i = getDTMIdentity(paramDTM);
      if (i >= 0) {
        m_dtms[(i >>> 16)] = null;
      }
    }
    paramDTM.documentRelease();
    return true;
  }
  
  public synchronized DTM createDocumentFragment()
  {
    try
    {
      DocumentBuilderFactory localDocumentBuilderFactory = FactoryImpl.getDOMFactory(super.useServicesMechnism());
      localDocumentBuilderFactory.setNamespaceAware(true);
      DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
      Document localDocument = localDocumentBuilder.newDocument();
      DocumentFragment localDocumentFragment = localDocument.createDocumentFragment();
      return getDTM(new DOMSource(localDocumentFragment), true, null, false, false);
    }
    catch (Exception localException)
    {
      throw new DTMException(localException);
    }
  }
  
  public synchronized DTMIterator createDTMIterator(int paramInt, DTMFilter paramDTMFilter, boolean paramBoolean)
  {
    return null;
  }
  
  public synchronized DTMIterator createDTMIterator(String paramString, PrefixResolver paramPrefixResolver)
  {
    return null;
  }
  
  public synchronized DTMIterator createDTMIterator(int paramInt)
  {
    return null;
  }
  
  public synchronized DTMIterator createDTMIterator(Object paramObject, int paramInt)
  {
    return null;
  }
  
  public ExpandedNameTable getExpandedNameTable(DTM paramDTM)
  {
    return m_expandedNameTable;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMManagerDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */