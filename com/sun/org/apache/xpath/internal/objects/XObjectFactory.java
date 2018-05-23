package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.axes.OneStepIterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

public class XObjectFactory
{
  public XObjectFactory() {}
  
  public static XObject create(Object paramObject)
  {
    Object localObject;
    if ((paramObject instanceof XObject)) {
      localObject = (XObject)paramObject;
    } else if ((paramObject instanceof String)) {
      localObject = new XString((String)paramObject);
    } else if ((paramObject instanceof Boolean)) {
      localObject = new XBoolean((Boolean)paramObject);
    } else if ((paramObject instanceof Double)) {
      localObject = new XNumber((Double)paramObject);
    } else {
      localObject = new XObject(paramObject);
    }
    return (XObject)localObject;
  }
  
  public static XObject create(Object paramObject, XPathContext paramXPathContext)
  {
    Object localObject1;
    if ((paramObject instanceof XObject))
    {
      localObject1 = (XObject)paramObject;
    }
    else if ((paramObject instanceof String))
    {
      localObject1 = new XString((String)paramObject);
    }
    else if ((paramObject instanceof Boolean))
    {
      localObject1 = new XBoolean((Boolean)paramObject);
    }
    else if ((paramObject instanceof Number))
    {
      localObject1 = new XNumber((Number)paramObject);
    }
    else
    {
      Object localObject2;
      if ((paramObject instanceof DTM))
      {
        localObject2 = (DTM)paramObject;
        try
        {
          int i = ((DTM)localObject2).getDocument();
          DTMAxisIterator localDTMAxisIterator = ((DTM)localObject2).getAxisIterator(13);
          localDTMAxisIterator.setStartNode(i);
          OneStepIterator localOneStepIterator2 = new OneStepIterator(localDTMAxisIterator, 13);
          localOneStepIterator2.setRoot(i, paramXPathContext);
          localObject1 = new XNodeSet(localOneStepIterator2);
        }
        catch (Exception localException1)
        {
          throw new WrappedRuntimeException(localException1);
        }
      }
      else if ((paramObject instanceof DTMAxisIterator))
      {
        localObject2 = (DTMAxisIterator)paramObject;
        try
        {
          OneStepIterator localOneStepIterator1 = new OneStepIterator((DTMAxisIterator)localObject2, 13);
          localOneStepIterator1.setRoot(((DTMAxisIterator)localObject2).getStartNode(), paramXPathContext);
          localObject1 = new XNodeSet(localOneStepIterator1);
        }
        catch (Exception localException2)
        {
          throw new WrappedRuntimeException(localException2);
        }
      }
      else if ((paramObject instanceof DTMIterator))
      {
        localObject1 = new XNodeSet((DTMIterator)paramObject);
      }
      else if ((paramObject instanceof Node))
      {
        localObject1 = new XNodeSetForDOM((Node)paramObject, paramXPathContext);
      }
      else if ((paramObject instanceof NodeList))
      {
        localObject1 = new XNodeSetForDOM((NodeList)paramObject, paramXPathContext);
      }
      else if ((paramObject instanceof NodeIterator))
      {
        localObject1 = new XNodeSetForDOM((NodeIterator)paramObject, paramXPathContext);
      }
      else
      {
        localObject1 = new XObject(paramObject);
      }
    }
    return (XObject)localObject1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\objects\XObjectFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */