package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMFilter;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2RTFDTM;
import com.sun.org.apache.xml.internal.utils.DefaultErrorHandler;
import com.sun.org.apache.xml.internal.utils.IntStack;
import com.sun.org.apache.xml.internal.utils.NodeVector;
import com.sun.org.apache.xml.internal.utils.ObjectStack;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.axes.OneStepIteratorForward;
import com.sun.org.apache.xpath.internal.axes.SubContextList;
import com.sun.org.apache.xpath.internal.objects.DTMXRTreeFrag;
import com.sun.org.apache.xpath.internal.objects.XMLStringFactoryImpl;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.XMLReader;

public class XPathContext
  extends DTMManager
{
  IntStack m_last_pushed_rtfdtm = new IntStack();
  private Vector m_rtfdtm_stack = null;
  private int m_which_rtfdtm = -1;
  private SAX2RTFDTM m_global_rtfdtm = null;
  private HashMap m_DTMXRTreeFrags = null;
  private boolean m_isSecureProcessing = false;
  private boolean m_useServicesMechanism = true;
  protected DTMManager m_dtmManager = null;
  ObjectStack m_saxLocations = new ObjectStack(4096);
  private Object m_owner;
  private Method m_ownerGetErrorListener;
  private VariableStack m_variableStacks = new VariableStack();
  private SourceTreeManager m_sourceTreeManager = new SourceTreeManager();
  private ErrorListener m_errorListener;
  private ErrorListener m_defaultErrorListener;
  private URIResolver m_uriResolver;
  public XMLReader m_primaryReader;
  private Stack m_contextNodeLists = new Stack();
  public static final int RECURSIONLIMIT = 4096;
  private IntStack m_currentNodes = new IntStack(4096);
  private NodeVector m_iteratorRoots = new NodeVector();
  private NodeVector m_predicateRoots = new NodeVector();
  private IntStack m_currentExpressionNodes = new IntStack(4096);
  private IntStack m_predicatePos = new IntStack();
  private ObjectStack m_prefixResolvers = new ObjectStack(4096);
  private Stack m_axesIteratorStack = new Stack();
  XPathExpressionContext expressionContext = new XPathExpressionContext();
  
  public DTMManager getDTMManager()
  {
    return m_dtmManager;
  }
  
  public void setSecureProcessing(boolean paramBoolean)
  {
    m_isSecureProcessing = paramBoolean;
  }
  
  public boolean isSecureProcessing()
  {
    return m_isSecureProcessing;
  }
  
  public DTM getDTM(Source paramSource, boolean paramBoolean1, DTMWSFilter paramDTMWSFilter, boolean paramBoolean2, boolean paramBoolean3)
  {
    return m_dtmManager.getDTM(paramSource, paramBoolean1, paramDTMWSFilter, paramBoolean2, paramBoolean3);
  }
  
  public DTM getDTM(int paramInt)
  {
    return m_dtmManager.getDTM(paramInt);
  }
  
  public int getDTMHandleFromNode(Node paramNode)
  {
    return m_dtmManager.getDTMHandleFromNode(paramNode);
  }
  
  public int getDTMIdentity(DTM paramDTM)
  {
    return m_dtmManager.getDTMIdentity(paramDTM);
  }
  
  public DTM createDocumentFragment()
  {
    return m_dtmManager.createDocumentFragment();
  }
  
  public boolean release(DTM paramDTM, boolean paramBoolean)
  {
    if ((m_rtfdtm_stack != null) && (m_rtfdtm_stack.contains(paramDTM))) {
      return false;
    }
    return m_dtmManager.release(paramDTM, paramBoolean);
  }
  
  public DTMIterator createDTMIterator(Object paramObject, int paramInt)
  {
    return m_dtmManager.createDTMIterator(paramObject, paramInt);
  }
  
  public DTMIterator createDTMIterator(String paramString, PrefixResolver paramPrefixResolver)
  {
    return m_dtmManager.createDTMIterator(paramString, paramPrefixResolver);
  }
  
  public DTMIterator createDTMIterator(int paramInt, DTMFilter paramDTMFilter, boolean paramBoolean)
  {
    return m_dtmManager.createDTMIterator(paramInt, paramDTMFilter, paramBoolean);
  }
  
  public DTMIterator createDTMIterator(int paramInt)
  {
    OneStepIteratorForward localOneStepIteratorForward = new OneStepIteratorForward(13);
    localOneStepIteratorForward.setRoot(paramInt, this);
    return localOneStepIteratorForward;
  }
  
  public XPathContext()
  {
    this(true);
  }
  
  public XPathContext(boolean paramBoolean)
  {
    init(paramBoolean);
  }
  
  public XPathContext(Object paramObject)
  {
    m_owner = paramObject;
    try
    {
      m_ownerGetErrorListener = m_owner.getClass().getMethod("getErrorListener", new Class[0]);
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}
    init(true);
  }
  
  private void init(boolean paramBoolean)
  {
    m_prefixResolvers.push(null);
    m_currentNodes.push(-1);
    m_currentExpressionNodes.push(-1);
    m_saxLocations.push(null);
    m_useServicesMechanism = paramBoolean;
    m_dtmManager = DTMManager.newInstance(XMLStringFactoryImpl.getFactory());
  }
  
  public void reset()
  {
    releaseDTMXRTreeFrags();
    if (m_rtfdtm_stack != null)
    {
      Enumeration localEnumeration = m_rtfdtm_stack.elements();
      while (localEnumeration.hasMoreElements()) {
        m_dtmManager.release((DTM)localEnumeration.nextElement(), true);
      }
    }
    m_rtfdtm_stack = null;
    m_which_rtfdtm = -1;
    if (m_global_rtfdtm != null) {
      m_dtmManager.release(m_global_rtfdtm, true);
    }
    m_global_rtfdtm = null;
    m_dtmManager = DTMManager.newInstance(XMLStringFactoryImpl.getFactory());
    m_saxLocations.removeAllElements();
    m_axesIteratorStack.removeAllElements();
    m_contextNodeLists.removeAllElements();
    m_currentExpressionNodes.removeAllElements();
    m_currentNodes.removeAllElements();
    m_iteratorRoots.RemoveAllNoClear();
    m_predicatePos.removeAllElements();
    m_predicateRoots.RemoveAllNoClear();
    m_prefixResolvers.removeAllElements();
    m_prefixResolvers.push(null);
    m_currentNodes.push(-1);
    m_currentExpressionNodes.push(-1);
    m_saxLocations.push(null);
  }
  
  public void setSAXLocator(SourceLocator paramSourceLocator)
  {
    m_saxLocations.setTop(paramSourceLocator);
  }
  
  public void pushSAXLocator(SourceLocator paramSourceLocator)
  {
    m_saxLocations.push(paramSourceLocator);
  }
  
  public void pushSAXLocatorNull()
  {
    m_saxLocations.push(null);
  }
  
  public void popSAXLocator()
  {
    m_saxLocations.pop();
  }
  
  public SourceLocator getSAXLocator()
  {
    return (SourceLocator)m_saxLocations.peek();
  }
  
  public Object getOwnerObject()
  {
    return m_owner;
  }
  
  public final VariableStack getVarStack()
  {
    return m_variableStacks;
  }
  
  public final void setVarStack(VariableStack paramVariableStack)
  {
    m_variableStacks = paramVariableStack;
  }
  
  public final SourceTreeManager getSourceTreeManager()
  {
    return m_sourceTreeManager;
  }
  
  public void setSourceTreeManager(SourceTreeManager paramSourceTreeManager)
  {
    m_sourceTreeManager = paramSourceTreeManager;
  }
  
  public final ErrorListener getErrorListener()
  {
    if (null != m_errorListener) {
      return m_errorListener;
    }
    ErrorListener localErrorListener = null;
    try
    {
      if (null != m_ownerGetErrorListener) {
        localErrorListener = (ErrorListener)m_ownerGetErrorListener.invoke(m_owner, new Object[0]);
      }
    }
    catch (Exception localException) {}
    if (null == localErrorListener)
    {
      if (null == m_defaultErrorListener) {
        m_defaultErrorListener = new DefaultErrorHandler();
      }
      localErrorListener = m_defaultErrorListener;
    }
    return localErrorListener;
  }
  
  public void setErrorListener(ErrorListener paramErrorListener)
    throws IllegalArgumentException
  {
    if (paramErrorListener == null) {
      throw new IllegalArgumentException(XSLMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", null));
    }
    m_errorListener = paramErrorListener;
  }
  
  public final URIResolver getURIResolver()
  {
    return m_uriResolver;
  }
  
  public void setURIResolver(URIResolver paramURIResolver)
  {
    m_uriResolver = paramURIResolver;
  }
  
  public final XMLReader getPrimaryReader()
  {
    return m_primaryReader;
  }
  
  public void setPrimaryReader(XMLReader paramXMLReader)
  {
    m_primaryReader = paramXMLReader;
  }
  
  public Stack getContextNodeListsStack()
  {
    return m_contextNodeLists;
  }
  
  public void setContextNodeListsStack(Stack paramStack)
  {
    m_contextNodeLists = paramStack;
  }
  
  public final DTMIterator getContextNodeList()
  {
    if (m_contextNodeLists.size() > 0) {
      return (DTMIterator)m_contextNodeLists.peek();
    }
    return null;
  }
  
  public final void pushContextNodeList(DTMIterator paramDTMIterator)
  {
    m_contextNodeLists.push(paramDTMIterator);
  }
  
  public final void popContextNodeList()
  {
    if (m_contextNodeLists.isEmpty()) {
      System.err.println("Warning: popContextNodeList when stack is empty!");
    } else {
      m_contextNodeLists.pop();
    }
  }
  
  public IntStack getCurrentNodeStack()
  {
    return m_currentNodes;
  }
  
  public void setCurrentNodeStack(IntStack paramIntStack)
  {
    m_currentNodes = paramIntStack;
  }
  
  public final int getCurrentNode()
  {
    return m_currentNodes.peek();
  }
  
  public final void pushCurrentNodeAndExpression(int paramInt1, int paramInt2)
  {
    m_currentNodes.push(paramInt1);
    m_currentExpressionNodes.push(paramInt1);
  }
  
  public final void popCurrentNodeAndExpression()
  {
    m_currentNodes.quickPop(1);
    m_currentExpressionNodes.quickPop(1);
  }
  
  public final void pushExpressionState(int paramInt1, int paramInt2, PrefixResolver paramPrefixResolver)
  {
    m_currentNodes.push(paramInt1);
    m_currentExpressionNodes.push(paramInt1);
    m_prefixResolvers.push(paramPrefixResolver);
  }
  
  public final void popExpressionState()
  {
    m_currentNodes.quickPop(1);
    m_currentExpressionNodes.quickPop(1);
    m_prefixResolvers.pop();
  }
  
  public final void pushCurrentNode(int paramInt)
  {
    m_currentNodes.push(paramInt);
  }
  
  public final void popCurrentNode()
  {
    m_currentNodes.quickPop(1);
  }
  
  public final void pushPredicateRoot(int paramInt)
  {
    m_predicateRoots.push(paramInt);
  }
  
  public final void popPredicateRoot()
  {
    m_predicateRoots.popQuick();
  }
  
  public final int getPredicateRoot()
  {
    return m_predicateRoots.peepOrNull();
  }
  
  public final void pushIteratorRoot(int paramInt)
  {
    m_iteratorRoots.push(paramInt);
  }
  
  public final void popIteratorRoot()
  {
    m_iteratorRoots.popQuick();
  }
  
  public final int getIteratorRoot()
  {
    return m_iteratorRoots.peepOrNull();
  }
  
  public IntStack getCurrentExpressionNodeStack()
  {
    return m_currentExpressionNodes;
  }
  
  public void setCurrentExpressionNodeStack(IntStack paramIntStack)
  {
    m_currentExpressionNodes = paramIntStack;
  }
  
  public final int getPredicatePos()
  {
    return m_predicatePos.peek();
  }
  
  public final void pushPredicatePos(int paramInt)
  {
    m_predicatePos.push(paramInt);
  }
  
  public final void popPredicatePos()
  {
    m_predicatePos.pop();
  }
  
  public final int getCurrentExpressionNode()
  {
    return m_currentExpressionNodes.peek();
  }
  
  public final void pushCurrentExpressionNode(int paramInt)
  {
    m_currentExpressionNodes.push(paramInt);
  }
  
  public final void popCurrentExpressionNode()
  {
    m_currentExpressionNodes.quickPop(1);
  }
  
  public final PrefixResolver getNamespaceContext()
  {
    return (PrefixResolver)m_prefixResolvers.peek();
  }
  
  public final void setNamespaceContext(PrefixResolver paramPrefixResolver)
  {
    m_prefixResolvers.setTop(paramPrefixResolver);
  }
  
  public final void pushNamespaceContext(PrefixResolver paramPrefixResolver)
  {
    m_prefixResolvers.push(paramPrefixResolver);
  }
  
  public final void pushNamespaceContextNull()
  {
    m_prefixResolvers.push(null);
  }
  
  public final void popNamespaceContext()
  {
    m_prefixResolvers.pop();
  }
  
  public Stack getAxesIteratorStackStacks()
  {
    return m_axesIteratorStack;
  }
  
  public void setAxesIteratorStackStacks(Stack paramStack)
  {
    m_axesIteratorStack = paramStack;
  }
  
  public final void pushSubContextList(SubContextList paramSubContextList)
  {
    m_axesIteratorStack.push(paramSubContextList);
  }
  
  public final void popSubContextList()
  {
    m_axesIteratorStack.pop();
  }
  
  public SubContextList getSubContextList()
  {
    return m_axesIteratorStack.isEmpty() ? null : (SubContextList)m_axesIteratorStack.peek();
  }
  
  public SubContextList getCurrentNodeList()
  {
    return m_axesIteratorStack.isEmpty() ? null : (SubContextList)m_axesIteratorStack.elementAt(0);
  }
  
  public final int getContextNode()
  {
    return getCurrentNode();
  }
  
  public final DTMIterator getContextNodes()
  {
    try
    {
      DTMIterator localDTMIterator = getContextNodeList();
      if (null != localDTMIterator) {
        return localDTMIterator.cloneWithReset();
      }
      return null;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
  
  public ExpressionContext getExpressionContext()
  {
    return expressionContext;
  }
  
  public DTM getGlobalRTFDTM()
  {
    if ((m_global_rtfdtm == null) || (m_global_rtfdtm.isTreeIncomplete())) {
      m_global_rtfdtm = ((SAX2RTFDTM)m_dtmManager.getDTM(null, true, null, false, false));
    }
    return m_global_rtfdtm;
  }
  
  public DTM getRTFDTM()
  {
    SAX2RTFDTM localSAX2RTFDTM;
    if (m_rtfdtm_stack == null)
    {
      m_rtfdtm_stack = new Vector();
      localSAX2RTFDTM = (SAX2RTFDTM)m_dtmManager.getDTM(null, true, null, false, false);
      m_rtfdtm_stack.addElement(localSAX2RTFDTM);
      m_which_rtfdtm += 1;
    }
    else if (m_which_rtfdtm < 0)
    {
      localSAX2RTFDTM = (SAX2RTFDTM)m_rtfdtm_stack.elementAt(++m_which_rtfdtm);
    }
    else
    {
      localSAX2RTFDTM = (SAX2RTFDTM)m_rtfdtm_stack.elementAt(m_which_rtfdtm);
      if (localSAX2RTFDTM.isTreeIncomplete()) {
        if (++m_which_rtfdtm < m_rtfdtm_stack.size())
        {
          localSAX2RTFDTM = (SAX2RTFDTM)m_rtfdtm_stack.elementAt(m_which_rtfdtm);
        }
        else
        {
          localSAX2RTFDTM = (SAX2RTFDTM)m_dtmManager.getDTM(null, true, null, false, false);
          m_rtfdtm_stack.addElement(localSAX2RTFDTM);
        }
      }
    }
    return localSAX2RTFDTM;
  }
  
  public void pushRTFContext()
  {
    m_last_pushed_rtfdtm.push(m_which_rtfdtm);
    if (null != m_rtfdtm_stack) {
      ((SAX2RTFDTM)getRTFDTM()).pushRewindMark();
    }
  }
  
  public void popRTFContext()
  {
    int i = m_last_pushed_rtfdtm.pop();
    if (null == m_rtfdtm_stack) {
      return;
    }
    boolean bool;
    if (m_which_rtfdtm == i)
    {
      if (i >= 0) {
        bool = ((SAX2RTFDTM)m_rtfdtm_stack.elementAt(i)).popRewindMark();
      }
    }
    else {
      while (m_which_rtfdtm != i)
      {
        bool = ((SAX2RTFDTM)m_rtfdtm_stack.elementAt(m_which_rtfdtm)).popRewindMark();
        m_which_rtfdtm -= 1;
      }
    }
  }
  
  public DTMXRTreeFrag getDTMXRTreeFrag(int paramInt)
  {
    if (m_DTMXRTreeFrags == null) {
      m_DTMXRTreeFrags = new HashMap();
    }
    if (m_DTMXRTreeFrags.containsKey(new Integer(paramInt))) {
      return (DTMXRTreeFrag)m_DTMXRTreeFrags.get(new Integer(paramInt));
    }
    DTMXRTreeFrag localDTMXRTreeFrag = new DTMXRTreeFrag(paramInt, this);
    m_DTMXRTreeFrags.put(new Integer(paramInt), localDTMXRTreeFrag);
    return localDTMXRTreeFrag;
  }
  
  private final void releaseDTMXRTreeFrags()
  {
    if (m_DTMXRTreeFrags == null) {
      return;
    }
    Iterator localIterator = m_DTMXRTreeFrags.values().iterator();
    while (localIterator.hasNext())
    {
      DTMXRTreeFrag localDTMXRTreeFrag = (DTMXRTreeFrag)localIterator.next();
      localDTMXRTreeFrag.destruct();
      localIterator.remove();
    }
    m_DTMXRTreeFrags = null;
  }
  
  public class XPathExpressionContext
    implements ExpressionContext
  {
    public XPathExpressionContext() {}
    
    public XPathContext getXPathContext()
    {
      return XPathContext.this;
    }
    
    public DTMManager getDTMManager()
    {
      return m_dtmManager;
    }
    
    public Node getContextNode()
    {
      int i = getCurrentNode();
      return getDTM(i).getNode(i);
    }
    
    public NodeIterator getContextNodes()
    {
      return new DTMNodeIterator(getContextNodeList());
    }
    
    public ErrorListener getErrorListener()
    {
      return XPathContext.this.getErrorListener();
    }
    
    public boolean useServicesMechnism()
    {
      return m_useServicesMechanism;
    }
    
    public void setServicesMechnism(boolean paramBoolean)
    {
      m_useServicesMechanism = paramBoolean;
    }
    
    public double toNumber(Node paramNode)
    {
      int i = getDTMHandleFromNode(paramNode);
      DTM localDTM = getDTM(i);
      XString localXString = (XString)localDTM.getStringValue(i);
      return localXString.num();
    }
    
    public String toString(Node paramNode)
    {
      int i = getDTMHandleFromNode(paramNode);
      DTM localDTM = getDTM(i);
      XMLString localXMLString = localDTM.getStringValue(i);
      return localXMLString.toString();
    }
    
    public final XObject getVariableOrParam(QName paramQName)
      throws TransformerException
    {
      return m_variableStacks.getVariableOrParam(XPathContext.this, paramQName);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\XPathContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */