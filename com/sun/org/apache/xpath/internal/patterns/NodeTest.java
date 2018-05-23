package com.sun.org.apache.xpath.internal.patterns;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.io.PrintStream;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class NodeTest
  extends Expression
{
  static final long serialVersionUID = -5736721866747906182L;
  public static final String WILD = "*";
  public static final String SUPPORTS_PRE_STRIPPING = "http://xml.apache.org/xpath/features/whitespace-pre-stripping";
  protected int m_whatToShow;
  public static final int SHOW_BYFUNCTION = 65536;
  String m_namespace;
  protected String m_name;
  XNumber m_score;
  public static final XNumber SCORE_NODETEST = new XNumber(-0.5D);
  public static final XNumber SCORE_NSWILD = new XNumber(-0.25D);
  public static final XNumber SCORE_QNAME = new XNumber(0.0D);
  public static final XNumber SCORE_OTHER = new XNumber(0.5D);
  public static final XNumber SCORE_NONE = new XNumber(Double.NEGATIVE_INFINITY);
  private boolean m_isTotallyWild;
  
  public int getWhatToShow()
  {
    return m_whatToShow;
  }
  
  public void setWhatToShow(int paramInt)
  {
    m_whatToShow = paramInt;
  }
  
  public String getNamespace()
  {
    return m_namespace;
  }
  
  public void setNamespace(String paramString)
  {
    m_namespace = paramString;
  }
  
  public String getLocalName()
  {
    return null == m_name ? "" : m_name;
  }
  
  public void setLocalName(String paramString)
  {
    m_name = paramString;
  }
  
  public NodeTest(int paramInt, String paramString1, String paramString2)
  {
    initNodeTest(paramInt, paramString1, paramString2);
  }
  
  public NodeTest(int paramInt)
  {
    initNodeTest(paramInt);
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!isSameClass(paramExpression)) {
      return false;
    }
    NodeTest localNodeTest = (NodeTest)paramExpression;
    if (null != m_name)
    {
      if (null == m_name) {
        return false;
      }
      if (!m_name.equals(m_name)) {
        return false;
      }
    }
    else if (null != m_name)
    {
      return false;
    }
    if (null != m_namespace)
    {
      if (null == m_namespace) {
        return false;
      }
      if (!m_namespace.equals(m_namespace)) {
        return false;
      }
    }
    else if (null != m_namespace)
    {
      return false;
    }
    if (m_whatToShow != m_whatToShow) {
      return false;
    }
    return m_isTotallyWild == m_isTotallyWild;
  }
  
  public NodeTest() {}
  
  public void initNodeTest(int paramInt)
  {
    m_whatToShow = paramInt;
    calcScore();
  }
  
  public void initNodeTest(int paramInt, String paramString1, String paramString2)
  {
    m_whatToShow = paramInt;
    m_namespace = paramString1;
    m_name = paramString2;
    calcScore();
  }
  
  public XNumber getStaticScore()
  {
    return m_score;
  }
  
  public void setStaticScore(XNumber paramXNumber)
  {
    m_score = paramXNumber;
  }
  
  protected void calcScore()
  {
    if ((m_namespace == null) && (m_name == null)) {
      m_score = SCORE_NODETEST;
    } else if (((m_namespace == "*") || (m_namespace == null)) && (m_name == "*")) {
      m_score = SCORE_NODETEST;
    } else if ((m_namespace != "*") && (m_name == "*")) {
      m_score = SCORE_NSWILD;
    } else {
      m_score = SCORE_QNAME;
    }
    m_isTotallyWild = ((m_namespace == null) && (m_name == "*"));
  }
  
  public double getDefaultScore()
  {
    return m_score.num();
  }
  
  public static int getNodeTypeTest(int paramInt)
  {
    if (0 != (paramInt & 0x1)) {
      return 1;
    }
    if (0 != (paramInt & 0x2)) {
      return 2;
    }
    if (0 != (paramInt & 0x4)) {
      return 3;
    }
    if (0 != (paramInt & 0x100)) {
      return 9;
    }
    if (0 != (paramInt & 0x400)) {
      return 11;
    }
    if (0 != (paramInt & 0x1000)) {
      return 13;
    }
    if (0 != (paramInt & 0x80)) {
      return 8;
    }
    if (0 != (paramInt & 0x40)) {
      return 7;
    }
    if (0 != (paramInt & 0x200)) {
      return 10;
    }
    if (0 != (paramInt & 0x20)) {
      return 6;
    }
    if (0 != (paramInt & 0x10)) {
      return 5;
    }
    if (0 != (paramInt & 0x800)) {
      return 12;
    }
    if (0 != (paramInt & 0x8)) {
      return 4;
    }
    return 0;
  }
  
  public static void debugWhatToShow(int paramInt)
  {
    Vector localVector = new Vector();
    if (0 != (paramInt & 0x2)) {
      localVector.addElement("SHOW_ATTRIBUTE");
    }
    if (0 != (paramInt & 0x1000)) {
      localVector.addElement("SHOW_NAMESPACE");
    }
    if (0 != (paramInt & 0x8)) {
      localVector.addElement("SHOW_CDATA_SECTION");
    }
    if (0 != (paramInt & 0x80)) {
      localVector.addElement("SHOW_COMMENT");
    }
    if (0 != (paramInt & 0x100)) {
      localVector.addElement("SHOW_DOCUMENT");
    }
    if (0 != (paramInt & 0x400)) {
      localVector.addElement("SHOW_DOCUMENT_FRAGMENT");
    }
    if (0 != (paramInt & 0x200)) {
      localVector.addElement("SHOW_DOCUMENT_TYPE");
    }
    if (0 != (paramInt & 0x1)) {
      localVector.addElement("SHOW_ELEMENT");
    }
    if (0 != (paramInt & 0x20)) {
      localVector.addElement("SHOW_ENTITY");
    }
    if (0 != (paramInt & 0x10)) {
      localVector.addElement("SHOW_ENTITY_REFERENCE");
    }
    if (0 != (paramInt & 0x800)) {
      localVector.addElement("SHOW_NOTATION");
    }
    if (0 != (paramInt & 0x40)) {
      localVector.addElement("SHOW_PROCESSING_INSTRUCTION");
    }
    if (0 != (paramInt & 0x4)) {
      localVector.addElement("SHOW_TEXT");
    }
    int i = localVector.size();
    for (int j = 0; j < i; j++)
    {
      if (j > 0) {
        System.out.print(" | ");
      }
      System.out.print(localVector.elementAt(j));
    }
    if (0 == i) {
      System.out.print("empty whatToShow: " + paramInt);
    }
    System.out.println();
  }
  
  private static final boolean subPartMatch(String paramString1, String paramString2)
  {
    return (paramString1 == paramString2) || ((null != paramString1) && ((paramString2 == "*") || (paramString1.equals(paramString2))));
  }
  
  private static final boolean subPartMatchNS(String paramString1, String paramString2)
  {
    return (paramString1 == paramString2) || ((null != paramString1) && (paramString1.length() > 0 ? (paramString2 == "*") || (paramString1.equals(paramString2)) : null == paramString2));
  }
  
  public XObject execute(XPathContext paramXPathContext, int paramInt)
    throws TransformerException
  {
    DTM localDTM = paramXPathContext.getDTM(paramInt);
    int i = localDTM.getNodeType(paramInt);
    if (m_whatToShow == -1) {
      return m_score;
    }
    int j = m_whatToShow & 1 << i - 1;
    switch (j)
    {
    case 256: 
    case 1024: 
      return SCORE_OTHER;
    case 128: 
      return m_score;
    case 4: 
    case 8: 
      return m_score;
    case 64: 
      return subPartMatch(localDTM.getNodeName(paramInt), m_name) ? m_score : SCORE_NONE;
    case 4096: 
      String str = localDTM.getLocalName(paramInt);
      return subPartMatch(str, m_name) ? m_score : SCORE_NONE;
    case 1: 
    case 2: 
      return (m_isTotallyWild) || ((subPartMatchNS(localDTM.getNamespaceURI(paramInt), m_namespace)) && (subPartMatch(localDTM.getLocalName(paramInt), m_name))) ? m_score : SCORE_NONE;
    }
    return SCORE_NONE;
  }
  
  public XObject execute(XPathContext paramXPathContext, int paramInt1, DTM paramDTM, int paramInt2)
    throws TransformerException
  {
    if (m_whatToShow == -1) {
      return m_score;
    }
    int i = m_whatToShow & 1 << paramDTM.getNodeType(paramInt1) - 1;
    switch (i)
    {
    case 256: 
    case 1024: 
      return SCORE_OTHER;
    case 128: 
      return m_score;
    case 4: 
    case 8: 
      return m_score;
    case 64: 
      return subPartMatch(paramDTM.getNodeName(paramInt1), m_name) ? m_score : SCORE_NONE;
    case 4096: 
      String str = paramDTM.getLocalName(paramInt1);
      return subPartMatch(str, m_name) ? m_score : SCORE_NONE;
    case 1: 
    case 2: 
      return (m_isTotallyWild) || ((subPartMatchNS(paramDTM.getNamespaceURI(paramInt1), m_namespace)) && (subPartMatch(paramDTM.getLocalName(paramInt1), m_name))) ? m_score : SCORE_NONE;
    }
    return SCORE_NONE;
  }
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    return execute(paramXPathContext, paramXPathContext.getCurrentNode());
  }
  
  public void fixupVariables(Vector paramVector, int paramInt) {}
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor)
  {
    assertion(false, "callVisitors should not be called for this object!!!");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\patterns\NodeTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */