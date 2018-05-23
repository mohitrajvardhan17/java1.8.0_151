package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.c14n.helper.AttrCompare;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

public class XMLSignatureInputDebugger
{
  private Set<Node> xpathNodeSet;
  private Set<String> inclusiveNamespaces;
  private Document doc = null;
  private Writer writer = null;
  static final String HTMLPrefix = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>\n<head>\n<title>Caninical XML node set</title>\n<style type=\"text/css\">\n<!-- \n.INCLUDED { \n   color: #000000; \n   background-color: \n   #FFFFFF; \n   font-weight: bold; } \n.EXCLUDED { \n   color: #666666; \n   background-color: \n   #999999; } \n.INCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #FFFFFF; \n   font-weight: bold; \n   font-style: italic; } \n.EXCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #999999; \n   font-style: italic; } \n--> \n</style> \n</head>\n<body bgcolor=\"#999999\">\n<h1>Explanation of the output</h1>\n<p>The following text contains the nodeset of the given Reference before it is canonicalized. There exist four different styles to indicate how a given node is treated.</p>\n<ul>\n<li class=\"INCLUDED\">A node which is in the node set is labeled using the INCLUDED style.</li>\n<li class=\"EXCLUDED\">A node which is <em>NOT</em> in the node set is labeled EXCLUDED style.</li>\n<li class=\"INCLUDEDINCLUSIVENAMESPACE\">A namespace which is in the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n<li class=\"EXCLUDEDINCLUSIVENAMESPACE\">A namespace which is in NOT the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n</ul>\n<h1>Output</h1>\n<pre>\n";
  static final String HTMLSuffix = "</pre></body></html>";
  static final String HTMLExcludePrefix = "<span class=\"EXCLUDED\">";
  static final String HTMLIncludePrefix = "<span class=\"INCLUDED\">";
  static final String HTMLIncludeOrExcludeSuffix = "</span>";
  static final String HTMLIncludedInclusiveNamespacePrefix = "<span class=\"INCLUDEDINCLUSIVENAMESPACE\">";
  static final String HTMLExcludedInclusiveNamespacePrefix = "<span class=\"EXCLUDEDINCLUSIVENAMESPACE\">";
  private static final int NODE_BEFORE_DOCUMENT_ELEMENT = -1;
  private static final int NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT = 0;
  private static final int NODE_AFTER_DOCUMENT_ELEMENT = 1;
  static final AttrCompare ATTR_COMPARE = new AttrCompare();
  
  public XMLSignatureInputDebugger(XMLSignatureInput paramXMLSignatureInput)
  {
    if (!paramXMLSignatureInput.isNodeSet()) {
      xpathNodeSet = null;
    } else {
      xpathNodeSet = paramXMLSignatureInput.getInputNodeSet();
    }
  }
  
  public XMLSignatureInputDebugger(XMLSignatureInput paramXMLSignatureInput, Set<String> paramSet)
  {
    this(paramXMLSignatureInput);
    inclusiveNamespaces = paramSet;
  }
  
  public String getHTMLRepresentation()
    throws XMLSignatureException
  {
    if ((xpathNodeSet == null) || (xpathNodeSet.size() == 0)) {
      return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>\n<head>\n<title>Caninical XML node set</title>\n<style type=\"text/css\">\n<!-- \n.INCLUDED { \n   color: #000000; \n   background-color: \n   #FFFFFF; \n   font-weight: bold; } \n.EXCLUDED { \n   color: #666666; \n   background-color: \n   #999999; } \n.INCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #FFFFFF; \n   font-weight: bold; \n   font-style: italic; } \n.EXCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #999999; \n   font-style: italic; } \n--> \n</style> \n</head>\n<body bgcolor=\"#999999\">\n<h1>Explanation of the output</h1>\n<p>The following text contains the nodeset of the given Reference before it is canonicalized. There exist four different styles to indicate how a given node is treated.</p>\n<ul>\n<li class=\"INCLUDED\">A node which is in the node set is labeled using the INCLUDED style.</li>\n<li class=\"EXCLUDED\">A node which is <em>NOT</em> in the node set is labeled EXCLUDED style.</li>\n<li class=\"INCLUDEDINCLUSIVENAMESPACE\">A namespace which is in the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n<li class=\"EXCLUDEDINCLUSIVENAMESPACE\">A namespace which is in NOT the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n</ul>\n<h1>Output</h1>\n<pre>\n<blink>no node set, sorry</blink></pre></body></html>";
    }
    Node localNode = (Node)xpathNodeSet.iterator().next();
    doc = XMLUtils.getOwnerDocument(localNode);
    try
    {
      writer = new StringWriter();
      canonicalizeXPathNodeSet(doc);
      writer.close();
      String str = writer.toString();
      return str;
    }
    catch (IOException localIOException)
    {
      throw new XMLSignatureException("empty", localIOException);
    }
    finally
    {
      xpathNodeSet = null;
      doc = null;
      writer = null;
    }
  }
  
  private void canonicalizeXPathNodeSet(Node paramNode)
    throws XMLSignatureException, IOException
  {
    int i = paramNode.getNodeType();
    int j;
    Object localObject;
    switch (i)
    {
    case 2: 
    case 6: 
    case 11: 
    case 12: 
      throw new XMLSignatureException("empty");
    case 9: 
      writer.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>\n<head>\n<title>Caninical XML node set</title>\n<style type=\"text/css\">\n<!-- \n.INCLUDED { \n   color: #000000; \n   background-color: \n   #FFFFFF; \n   font-weight: bold; } \n.EXCLUDED { \n   color: #666666; \n   background-color: \n   #999999; } \n.INCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #FFFFFF; \n   font-weight: bold; \n   font-style: italic; } \n.EXCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #999999; \n   font-style: italic; } \n--> \n</style> \n</head>\n<body bgcolor=\"#999999\">\n<h1>Explanation of the output</h1>\n<p>The following text contains the nodeset of the given Reference before it is canonicalized. There exist four different styles to indicate how a given node is treated.</p>\n<ul>\n<li class=\"INCLUDED\">A node which is in the node set is labeled using the INCLUDED style.</li>\n<li class=\"EXCLUDED\">A node which is <em>NOT</em> in the node set is labeled EXCLUDED style.</li>\n<li class=\"INCLUDEDINCLUSIVENAMESPACE\">A namespace which is in the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n<li class=\"EXCLUDEDINCLUSIVENAMESPACE\">A namespace which is in NOT the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n</ul>\n<h1>Output</h1>\n<pre>\n");
      for (Node localNode1 = paramNode.getFirstChild(); localNode1 != null; localNode1 = localNode1.getNextSibling()) {
        canonicalizeXPathNodeSet(localNode1);
      }
      writer.write("</pre></body></html>");
      break;
    case 8: 
      if (xpathNodeSet.contains(paramNode)) {
        writer.write("<span class=\"INCLUDED\">");
      } else {
        writer.write("<span class=\"EXCLUDED\">");
      }
      j = getPositionRelativeToDocumentElement(paramNode);
      if (j == 1) {
        writer.write("\n");
      }
      outputCommentToWriter((Comment)paramNode);
      if (j == -1) {
        writer.write("\n");
      }
      writer.write("</span>");
      break;
    case 7: 
      if (xpathNodeSet.contains(paramNode)) {
        writer.write("<span class=\"INCLUDED\">");
      } else {
        writer.write("<span class=\"EXCLUDED\">");
      }
      j = getPositionRelativeToDocumentElement(paramNode);
      if (j == 1) {
        writer.write("\n");
      }
      outputPItoWriter((ProcessingInstruction)paramNode);
      if (j == -1) {
        writer.write("\n");
      }
      writer.write("</span>");
      break;
    case 3: 
    case 4: 
      if (xpathNodeSet.contains(paramNode)) {
        writer.write("<span class=\"INCLUDED\">");
      } else {
        writer.write("<span class=\"EXCLUDED\">");
      }
      outputTextToWriter(paramNode.getNodeValue());
      for (localObject = paramNode.getNextSibling(); (localObject != null) && ((((Node)localObject).getNodeType() == 3) || (((Node)localObject).getNodeType() == 4)); localObject = ((Node)localObject).getNextSibling()) {
        outputTextToWriter(((Node)localObject).getNodeValue());
      }
      writer.write("</span>");
      break;
    case 1: 
      localObject = (Element)paramNode;
      if (xpathNodeSet.contains(paramNode)) {
        writer.write("<span class=\"INCLUDED\">");
      } else {
        writer.write("<span class=\"EXCLUDED\">");
      }
      writer.write("&lt;");
      writer.write(((Element)localObject).getTagName());
      writer.write("</span>");
      NamedNodeMap localNamedNodeMap = ((Element)localObject).getAttributes();
      int k = localNamedNodeMap.getLength();
      Attr[] arrayOfAttr1 = new Attr[k];
      for (int m = 0; m < k; m++) {
        arrayOfAttr1[m] = ((Attr)localNamedNodeMap.item(m));
      }
      Arrays.sort(arrayOfAttr1, ATTR_COMPARE);
      Attr[] arrayOfAttr2 = arrayOfAttr1;
      for (int n = 0; n < k; n++)
      {
        Attr localAttr = (Attr)arrayOfAttr2[n];
        boolean bool1 = xpathNodeSet.contains(localAttr);
        boolean bool2 = inclusiveNamespaces.contains(localAttr.getName());
        if (bool1)
        {
          if (bool2) {
            writer.write("<span class=\"INCLUDEDINCLUSIVENAMESPACE\">");
          } else {
            writer.write("<span class=\"INCLUDED\">");
          }
        }
        else if (bool2) {
          writer.write("<span class=\"EXCLUDEDINCLUSIVENAMESPACE\">");
        } else {
          writer.write("<span class=\"EXCLUDED\">");
        }
        outputAttrToWriter(localAttr.getNodeName(), localAttr.getNodeValue());
        writer.write("</span>");
      }
      if (xpathNodeSet.contains(paramNode)) {
        writer.write("<span class=\"INCLUDED\">");
      } else {
        writer.write("<span class=\"EXCLUDED\">");
      }
      writer.write("&gt;");
      writer.write("</span>");
      for (Node localNode2 = paramNode.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling()) {
        canonicalizeXPathNodeSet(localNode2);
      }
      if (xpathNodeSet.contains(paramNode)) {
        writer.write("<span class=\"INCLUDED\">");
      } else {
        writer.write("<span class=\"EXCLUDED\">");
      }
      writer.write("&lt;/");
      writer.write(((Element)localObject).getTagName());
      writer.write("&gt;");
      writer.write("</span>");
      break;
    }
  }
  
  private int getPositionRelativeToDocumentElement(Node paramNode)
  {
    if (paramNode == null) {
      return 0;
    }
    Document localDocument = paramNode.getOwnerDocument();
    if (paramNode.getParentNode() != localDocument) {
      return 0;
    }
    Element localElement = localDocument.getDocumentElement();
    if (localElement == null) {
      return 0;
    }
    if (localElement == paramNode) {
      return 0;
    }
    for (Node localNode = paramNode; localNode != null; localNode = localNode.getNextSibling()) {
      if (localNode == localElement) {
        return -1;
      }
    }
    return 1;
  }
  
  private void outputAttrToWriter(String paramString1, String paramString2)
    throws IOException
  {
    writer.write(" ");
    writer.write(paramString1);
    writer.write("=\"");
    int i = paramString2.length();
    for (int j = 0; j < i; j++)
    {
      int k = paramString2.charAt(j);
      switch (k)
      {
      case 38: 
        writer.write("&amp;amp;");
        break;
      case 60: 
        writer.write("&amp;lt;");
        break;
      case 34: 
        writer.write("&amp;quot;");
        break;
      case 9: 
        writer.write("&amp;#x9;");
        break;
      case 10: 
        writer.write("&amp;#xA;");
        break;
      case 13: 
        writer.write("&amp;#xD;");
        break;
      default: 
        writer.write(k);
      }
    }
    writer.write("\"");
  }
  
  private void outputPItoWriter(ProcessingInstruction paramProcessingInstruction)
    throws IOException
  {
    if (paramProcessingInstruction == null) {
      return;
    }
    writer.write("&lt;?");
    String str1 = paramProcessingInstruction.getTarget();
    int i = str1.length();
    int k;
    for (int j = 0; j < i; j++)
    {
      k = str1.charAt(j);
      switch (k)
      {
      case 13: 
        writer.write("&amp;#xD;");
        break;
      case 32: 
        writer.write("&middot;");
        break;
      case 10: 
        writer.write("&para;\n");
        break;
      default: 
        writer.write(k);
      }
    }
    String str2 = paramProcessingInstruction.getData();
    i = str2.length();
    if (i > 0)
    {
      writer.write(" ");
      for (k = 0; k < i; k++)
      {
        int m = str2.charAt(k);
        switch (m)
        {
        case 13: 
          writer.write("&amp;#xD;");
          break;
        default: 
          writer.write(m);
        }
      }
    }
    writer.write("?&gt;");
  }
  
  private void outputCommentToWriter(Comment paramComment)
    throws IOException
  {
    if (paramComment == null) {
      return;
    }
    writer.write("&lt;!--");
    String str = paramComment.getData();
    int i = str.length();
    for (int j = 0; j < i; j++)
    {
      int k = str.charAt(j);
      switch (k)
      {
      case 13: 
        writer.write("&amp;#xD;");
        break;
      case 32: 
        writer.write("&middot;");
        break;
      case 10: 
        writer.write("&para;\n");
        break;
      default: 
        writer.write(k);
      }
    }
    writer.write("--&gt;");
  }
  
  private void outputTextToWriter(String paramString)
    throws IOException
  {
    if (paramString == null) {
      return;
    }
    int i = paramString.length();
    for (int j = 0; j < i; j++)
    {
      int k = paramString.charAt(j);
      switch (k)
      {
      case 38: 
        writer.write("&amp;amp;");
        break;
      case 60: 
        writer.write("&amp;lt;");
        break;
      case 62: 
        writer.write("&amp;gt;");
        break;
      case 13: 
        writer.write("&amp;#xD;");
        break;
      case 32: 
        writer.write("&middot;");
        break;
      case 10: 
        writer.write("&para;\n");
        break;
      default: 
        writer.write(k);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\signature\XMLSignatureInputDebugger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */