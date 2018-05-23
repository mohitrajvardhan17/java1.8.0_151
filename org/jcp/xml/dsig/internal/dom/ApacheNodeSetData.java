package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.xml.crypto.NodeSetData;
import org.w3c.dom.Node;

public class ApacheNodeSetData
  implements ApacheData, NodeSetData
{
  private XMLSignatureInput xi;
  
  public ApacheNodeSetData(XMLSignatureInput paramXMLSignatureInput)
  {
    xi = paramXMLSignatureInput;
  }
  
  public Iterator iterator()
  {
    if ((xi.getNodeFilters() != null) && (!xi.getNodeFilters().isEmpty())) {
      return Collections.unmodifiableSet(getNodeSet(xi.getNodeFilters())).iterator();
    }
    try
    {
      return Collections.unmodifiableSet(xi.getNodeSet()).iterator();
    }
    catch (Exception localException)
    {
      throw new RuntimeException("unrecoverable error retrieving nodeset", localException);
    }
  }
  
  public XMLSignatureInput getXMLSignatureInput()
  {
    return xi;
  }
  
  private Set<Node> getNodeSet(List<NodeFilter> paramList)
  {
    if (xi.isNeedsToBeExpanded()) {
      XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(xi.getSubNode()));
    }
    LinkedHashSet localLinkedHashSet1 = new LinkedHashSet();
    XMLUtils.getSet(xi.getSubNode(), localLinkedHashSet1, null, !xi.isExcludeComments());
    LinkedHashSet localLinkedHashSet2 = new LinkedHashSet();
    Iterator localIterator1 = localLinkedHashSet1.iterator();
    while (localIterator1.hasNext())
    {
      Node localNode = (Node)localIterator1.next();
      Iterator localIterator2 = paramList.iterator();
      int i = 0;
      while ((localIterator2.hasNext()) && (i == 0))
      {
        NodeFilter localNodeFilter = (NodeFilter)localIterator2.next();
        if (localNodeFilter.isNodeInclude(localNode) != 1) {
          i = 1;
        }
      }
      if (i == 0) {
        localLinkedHashSet2.add(localNode);
      }
    }
    return localLinkedHashSet2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\ApacheNodeSetData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */