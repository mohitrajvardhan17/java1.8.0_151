package com.sun.org.apache.xml.internal.utils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class Hashtree2Node
{
  public Hashtree2Node() {}
  
  public static void appendHashToNode(Hashtable paramHashtable, String paramString, Node paramNode, Document paramDocument)
  {
    if ((null == paramNode) || (null == paramDocument) || (null == paramHashtable)) {
      return;
    }
    String str = null;
    if ((null == paramString) || ("".equals(paramString))) {
      str = "appendHashToNode";
    } else {
      str = paramString;
    }
    try
    {
      Element localElement1 = paramDocument.createElement(str);
      paramNode.appendChild(localElement1);
      Enumeration localEnumeration = paramHashtable.keys();
      Vector localVector = new Vector();
      Object localObject1;
      Object localObject2;
      while (localEnumeration.hasMoreElements())
      {
        localObject1 = localEnumeration.nextElement();
        localObject2 = localObject1.toString();
        Object localObject3 = paramHashtable.get(localObject1);
        if ((localObject3 instanceof Hashtable))
        {
          localVector.addElement(localObject2);
          localVector.addElement((Hashtable)localObject3);
        }
        else
        {
          try
          {
            Element localElement2 = paramDocument.createElement("item");
            localElement2.setAttribute("key", (String)localObject2);
            localElement2.appendChild(paramDocument.createTextNode((String)localObject3));
            localElement1.appendChild(localElement2);
          }
          catch (Exception localException2)
          {
            Element localElement3 = paramDocument.createElement("item");
            localElement3.setAttribute("key", (String)localObject2);
            localElement3.appendChild(paramDocument.createTextNode("ERROR: Reading " + localObject1 + " threw: " + localException2.toString()));
            localElement1.appendChild(localElement3);
          }
        }
      }
      localEnumeration = localVector.elements();
      while (localEnumeration.hasMoreElements())
      {
        localObject1 = (String)localEnumeration.nextElement();
        localObject2 = (Hashtable)localEnumeration.nextElement();
        appendHashToNode((Hashtable)localObject2, (String)localObject1, localElement1, paramDocument);
      }
    }
    catch (Exception localException1)
    {
      localException1.printStackTrace();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\Hashtree2Node.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */