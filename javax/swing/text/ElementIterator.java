package javax.swing.text;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Stack;

public class ElementIterator
  implements Cloneable
{
  private Element root;
  private Stack<StackItem> elementStack = null;
  
  public ElementIterator(Document paramDocument)
  {
    root = paramDocument.getDefaultRootElement();
  }
  
  public ElementIterator(Element paramElement)
  {
    root = paramElement;
  }
  
  public synchronized Object clone()
  {
    try
    {
      ElementIterator localElementIterator = new ElementIterator(root);
      if (elementStack != null)
      {
        elementStack = new Stack();
        for (int i = 0; i < elementStack.size(); i++)
        {
          StackItem localStackItem1 = (StackItem)elementStack.elementAt(i);
          StackItem localStackItem2 = (StackItem)localStackItem1.clone();
          elementStack.push(localStackItem2);
        }
      }
      return localElementIterator;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public Element first()
  {
    if (root == null) {
      return null;
    }
    elementStack = new Stack();
    if (root.getElementCount() != 0) {
      elementStack.push(new StackItem(root, null));
    }
    return root;
  }
  
  public int depth()
  {
    if (elementStack == null) {
      return 0;
    }
    return elementStack.size();
  }
  
  public Element current()
  {
    if (elementStack == null) {
      return first();
    }
    if (!elementStack.empty())
    {
      StackItem localStackItem = (StackItem)elementStack.peek();
      Element localElement = localStackItem.getElement();
      int i = localStackItem.getIndex();
      if (i == -1) {
        return localElement;
      }
      return localElement.getElement(i);
    }
    return null;
  }
  
  public Element next()
  {
    if (elementStack == null) {
      return first();
    }
    if (elementStack.isEmpty()) {
      return null;
    }
    StackItem localStackItem = (StackItem)elementStack.peek();
    Element localElement = localStackItem.getElement();
    int i = localStackItem.getIndex();
    Object localObject;
    if (i + 1 < localElement.getElementCount())
    {
      localObject = localElement.getElement(i + 1);
      if (((Element)localObject).isLeaf()) {
        localStackItem.incrementIndex();
      } else {
        elementStack.push(new StackItem((Element)localObject, null));
      }
      return (Element)localObject;
    }
    elementStack.pop();
    if (!elementStack.isEmpty())
    {
      localObject = (StackItem)elementStack.peek();
      ((StackItem)localObject).incrementIndex();
      return next();
    }
    return null;
  }
  
  public Element previous()
  {
    int i;
    if ((elementStack == null) || ((i = elementStack.size()) == 0)) {
      return null;
    }
    StackItem localStackItem1 = (StackItem)elementStack.peek();
    Element localElement = localStackItem1.getElement();
    int j = localStackItem1.getIndex();
    if (j > 0) {
      return getDeepestLeaf(localElement.getElement(--j));
    }
    if (j == 0) {
      return localElement;
    }
    if (j == -1)
    {
      if (i == 1) {
        return null;
      }
      StackItem localStackItem2 = (StackItem)elementStack.pop();
      localStackItem1 = (StackItem)elementStack.peek();
      elementStack.push(localStackItem2);
      localElement = localStackItem1.getElement();
      j = localStackItem1.getIndex();
      return j == -1 ? localElement : getDeepestLeaf(localElement.getElement(j));
    }
    return null;
  }
  
  private Element getDeepestLeaf(Element paramElement)
  {
    if (paramElement.isLeaf()) {
      return paramElement;
    }
    int i = paramElement.getElementCount();
    if (i == 0) {
      return paramElement;
    }
    return getDeepestLeaf(paramElement.getElement(i - 1));
  }
  
  private void dumpTree()
  {
    Element localElement;
    while ((localElement = next()) != null)
    {
      System.out.println("elem: " + localElement.getName());
      AttributeSet localAttributeSet = localElement.getAttributes();
      String str = "";
      Enumeration localEnumeration = localAttributeSet.getAttributeNames();
      while (localEnumeration.hasMoreElements())
      {
        Object localObject1 = localEnumeration.nextElement();
        Object localObject2 = localAttributeSet.getAttribute(localObject1);
        if ((localObject2 instanceof AttributeSet)) {
          str = str + localObject1 + "=**AttributeSet** ";
        } else {
          str = str + localObject1 + "=" + localObject2 + " ";
        }
      }
      System.out.println("attributes: " + str);
    }
  }
  
  private class StackItem
    implements Cloneable
  {
    Element item;
    int childIndex;
    
    private StackItem(Element paramElement)
    {
      item = paramElement;
      childIndex = -1;
    }
    
    private void incrementIndex()
    {
      childIndex += 1;
    }
    
    private Element getElement()
    {
      return item;
    }
    
    private int getIndex()
    {
      return childIndex;
    }
    
    protected Object clone()
      throws CloneNotSupportedException
    {
      return super.clone();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\ElementIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */