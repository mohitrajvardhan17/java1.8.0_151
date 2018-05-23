package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMDOMException;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

public class DTMNodeIterator
  implements NodeIterator
{
  private DTMIterator dtm_iter;
  private boolean valid = true;
  
  public DTMNodeIterator(DTMIterator paramDTMIterator)
  {
    try
    {
      dtm_iter = ((DTMIterator)paramDTMIterator.clone());
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new WrappedRuntimeException(localCloneNotSupportedException);
    }
  }
  
  public DTMIterator getDTMIterator()
  {
    return dtm_iter;
  }
  
  public void detach()
  {
    valid = false;
  }
  
  public boolean getExpandEntityReferences()
  {
    return false;
  }
  
  public NodeFilter getFilter()
  {
    throw new DTMDOMException((short)9);
  }
  
  public Node getRoot()
  {
    int i = dtm_iter.getRoot();
    return dtm_iter.getDTM(i).getNode(i);
  }
  
  public int getWhatToShow()
  {
    return dtm_iter.getWhatToShow();
  }
  
  public Node nextNode()
    throws DOMException
  {
    if (!valid) {
      throw new DTMDOMException((short)11);
    }
    int i = dtm_iter.nextNode();
    if (i == -1) {
      return null;
    }
    return dtm_iter.getDTM(i).getNode(i);
  }
  
  public Node previousNode()
  {
    if (!valid) {
      throw new DTMDOMException((short)11);
    }
    int i = dtm_iter.previousNode();
    if (i == -1) {
      return null;
    }
    return dtm_iter.getDTM(i).getNode(i);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMNodeIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */