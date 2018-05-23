package javax.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Window;
import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

final class LayoutComparator
  implements Comparator<Component>, Serializable
{
  private static final int ROW_TOLERANCE = 10;
  private boolean horizontal = true;
  private boolean leftToRight = true;
  
  LayoutComparator() {}
  
  void setComponentOrientation(ComponentOrientation paramComponentOrientation)
  {
    horizontal = paramComponentOrientation.isHorizontal();
    leftToRight = paramComponentOrientation.isLeftToRight();
  }
  
  public int compare(Component paramComponent1, Component paramComponent2)
  {
    if (paramComponent1 == paramComponent2) {
      return 0;
    }
    if (paramComponent1.getParent() != paramComponent2.getParent())
    {
      LinkedList localLinkedList1 = new LinkedList();
      while (paramComponent1 != null)
      {
        localLinkedList1.add(paramComponent1);
        if ((paramComponent1 instanceof Window)) {
          break;
        }
        paramComponent1 = paramComponent1.getParent();
      }
      if (paramComponent1 == null) {
        throw new ClassCastException();
      }
      LinkedList localLinkedList2 = new LinkedList();
      while (paramComponent2 != null)
      {
        localLinkedList2.add(paramComponent2);
        if ((paramComponent2 instanceof Window)) {
          break;
        }
        paramComponent2 = paramComponent2.getParent();
      }
      if (paramComponent2 == null) {
        throw new ClassCastException();
      }
      ListIterator localListIterator1 = localLinkedList1.listIterator(localLinkedList1.size());
      ListIterator localListIterator2 = localLinkedList2.listIterator(localLinkedList2.size());
      for (;;)
      {
        if (localListIterator1.hasPrevious()) {
          paramComponent1 = (Component)localListIterator1.previous();
        } else {
          return -1;
        }
        if (localListIterator2.hasPrevious()) {
          paramComponent2 = (Component)localListIterator2.previous();
        } else {
          return 1;
        }
        if (paramComponent1 != paramComponent2) {
          break;
        }
      }
    }
    int i = paramComponent1.getX();
    int j = paramComponent1.getY();
    int k = paramComponent2.getX();
    int m = paramComponent2.getY();
    int n = paramComponent1.getParent().getComponentZOrder(paramComponent1) - paramComponent2.getParent().getComponentZOrder(paramComponent2);
    if (horizontal)
    {
      if (leftToRight)
      {
        if (Math.abs(j - m) < 10) {
          return i > k ? 1 : i < k ? -1 : n;
        }
        return j < m ? -1 : 1;
      }
      if (Math.abs(j - m) < 10) {
        return i < k ? 1 : i > k ? -1 : n;
      }
      return j < m ? -1 : 1;
    }
    if (leftToRight)
    {
      if (Math.abs(i - k) < 10) {
        return j > m ? 1 : j < m ? -1 : n;
      }
      return i < k ? -1 : 1;
    }
    if (Math.abs(i - k) < 10) {
      return j > m ? 1 : j < m ? -1 : n;
    }
    return i > k ? -1 : 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\LayoutComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */