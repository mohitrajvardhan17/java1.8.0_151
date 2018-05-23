package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMException;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import javax.xml.transform.Source;

public abstract class DTMDefaultBaseTraversers
  extends DTMDefaultBase
{
  public DTMDefaultBaseTraversers(DTMManager paramDTMManager, Source paramSource, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean)
  {
    super(paramDTMManager, paramSource, paramInt, paramDTMWSFilter, paramXMLStringFactory, paramBoolean);
  }
  
  public DTMDefaultBaseTraversers(DTMManager paramDTMManager, Source paramSource, int paramInt1, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, boolean paramBoolean3)
  {
    super(paramDTMManager, paramSource, paramInt1, paramDTMWSFilter, paramXMLStringFactory, paramBoolean1, paramInt2, paramBoolean2, paramBoolean3);
  }
  
  public DTMAxisTraverser getAxisTraverser(int paramInt)
  {
    Object localObject;
    if (null == m_traversers)
    {
      m_traversers = new DTMAxisTraverser[Axis.getNamesLength()];
      localObject = null;
    }
    else
    {
      localObject = m_traversers[paramInt];
      if (localObject != null) {
        return (DTMAxisTraverser)localObject;
      }
    }
    switch (paramInt)
    {
    case 0: 
      localObject = new AncestorTraverser(null);
      break;
    case 1: 
      localObject = new AncestorOrSelfTraverser(null);
      break;
    case 2: 
      localObject = new AttributeTraverser(null);
      break;
    case 3: 
      localObject = new ChildTraverser(null);
      break;
    case 4: 
      localObject = new DescendantTraverser(null);
      break;
    case 5: 
      localObject = new DescendantOrSelfTraverser(null);
      break;
    case 6: 
      localObject = new FollowingTraverser(null);
      break;
    case 7: 
      localObject = new FollowingSiblingTraverser(null);
      break;
    case 9: 
      localObject = new NamespaceTraverser(null);
      break;
    case 8: 
      localObject = new NamespaceDeclsTraverser(null);
      break;
    case 10: 
      localObject = new ParentTraverser(null);
      break;
    case 11: 
      localObject = new PrecedingTraverser(null);
      break;
    case 12: 
      localObject = new PrecedingSiblingTraverser(null);
      break;
    case 13: 
      localObject = new SelfTraverser(null);
      break;
    case 16: 
      localObject = new AllFromRootTraverser(null);
      break;
    case 14: 
      localObject = new AllFromNodeTraverser(null);
      break;
    case 15: 
      localObject = new PrecedingAndAncestorTraverser(null);
      break;
    case 17: 
      localObject = new DescendantFromRootTraverser(null);
      break;
    case 18: 
      localObject = new DescendantOrSelfFromRootTraverser(null);
      break;
    case 19: 
      localObject = new RootTraverser(null);
      break;
    case 20: 
      return null;
    default: 
      throw new DTMException(XMLMessages.createXMLMessage("ER_UNKNOWN_AXIS_TYPE", new Object[] { Integer.toString(paramInt) }));
    }
    if (null == localObject) {
      throw new DTMException(XMLMessages.createXMLMessage("ER_AXIS_TRAVERSER_NOT_SUPPORTED", new Object[] { Axis.getNames(paramInt) }));
    }
    m_traversers[paramInt] = localObject;
    return (DTMAxisTraverser)localObject;
  }
  
  private class AllFromNodeTraverser
    extends DTMDefaultBaseTraversers.DescendantOrSelfTraverser
  {
    private AllFromNodeTraverser()
    {
      super(null);
    }
    
    public int next(int paramInt1, int paramInt2)
    {
      int i = makeNodeIdentity(paramInt1);
      paramInt2 = makeNodeIdentity(paramInt2) + 1;
      _exptype(paramInt2);
      if (!isDescendant(i, paramInt2)) {
        return -1;
      }
      return makeNodeHandle(paramInt2);
    }
  }
  
  private class AllFromRootTraverser
    extends DTMDefaultBaseTraversers.AllFromNodeTraverser
  {
    private AllFromRootTraverser()
    {
      super(null);
    }
    
    public int first(int paramInt)
    {
      return getDocumentRoot(paramInt);
    }
    
    public int first(int paramInt1, int paramInt2)
    {
      return getExpandedTypeID(getDocumentRoot(paramInt1)) == paramInt2 ? paramInt1 : next(paramInt1, paramInt1, paramInt2);
    }
    
    public int next(int paramInt1, int paramInt2)
    {
      int i = makeNodeIdentity(paramInt1);
      paramInt2 = makeNodeIdentity(paramInt2) + 1;
      int j = _type(paramInt2);
      if (j == -1) {
        return -1;
      }
      return makeNodeHandle(paramInt2);
    }
    
    public int next(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = makeNodeIdentity(paramInt1);
      for (paramInt2 = makeNodeIdentity(paramInt2) + 1;; paramInt2++)
      {
        int j = _exptype(paramInt2);
        if (j == -1) {
          return -1;
        }
        if (j == paramInt3) {
          return makeNodeHandle(paramInt2);
        }
      }
    }
  }
  
  private class AncestorOrSelfTraverser
    extends DTMDefaultBaseTraversers.AncestorTraverser
  {
    private AncestorOrSelfTraverser()
    {
      super(null);
    }
    
    public int first(int paramInt)
    {
      return paramInt;
    }
    
    public int first(int paramInt1, int paramInt2)
    {
      return getExpandedTypeID(paramInt1) == paramInt2 ? paramInt1 : next(paramInt1, paramInt1, paramInt2);
    }
  }
  
  private class AncestorTraverser
    extends DTMAxisTraverser
  {
    private AncestorTraverser() {}
    
    public int next(int paramInt1, int paramInt2)
    {
      return getParent(paramInt2);
    }
    
    public int next(int paramInt1, int paramInt2, int paramInt3)
    {
      paramInt2 = makeNodeIdentity(paramInt2);
      while (-1 != (paramInt2 = m_parent.elementAt(paramInt2))) {
        if (m_exptype.elementAt(paramInt2) == paramInt3) {
          return makeNodeHandle(paramInt2);
        }
      }
      return -1;
    }
  }
  
  private class AttributeTraverser
    extends DTMAxisTraverser
  {
    private AttributeTraverser() {}
    
    public int next(int paramInt1, int paramInt2)
    {
      return paramInt1 == paramInt2 ? getFirstAttribute(paramInt1) : getNextAttribute(paramInt2);
    }
    
    public int next(int paramInt1, int paramInt2, int paramInt3)
    {
      paramInt2 = paramInt1 == paramInt2 ? getFirstAttribute(paramInt1) : getNextAttribute(paramInt2);
      do
      {
        if (getExpandedTypeID(paramInt2) == paramInt3) {
          return paramInt2;
        }
      } while (-1 != (paramInt2 = getNextAttribute(paramInt2)));
      return -1;
    }
  }
  
  private class ChildTraverser
    extends DTMAxisTraverser
  {
    private ChildTraverser() {}
    
    protected int getNextIndexed(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = m_expandedNameTable.getNamespaceID(paramInt3);
      int j = m_expandedNameTable.getLocalNameID(paramInt3);
      for (;;)
      {
        int k = findElementFromIndex(i, j, paramInt2);
        if (-2 != k)
        {
          int m = m_parent.elementAt(k);
          if (m == paramInt1) {
            return k;
          }
          if (m < paramInt1) {
            return -1;
          }
          do
          {
            m = m_parent.elementAt(m);
            if (m < paramInt1) {
              return -1;
            }
          } while (m > paramInt1);
          paramInt2 = k + 1;
        }
        else
        {
          nextNode();
          if (m_nextsib.elementAt(paramInt1) != -2) {
            break;
          }
        }
      }
      return -1;
    }
    
    public int first(int paramInt)
    {
      return getFirstChild(paramInt);
    }
    
    public int first(int paramInt1, int paramInt2)
    {
      int i = makeNodeIdentity(paramInt1);
      int j = getNextIndexed(i, _firstch(i), paramInt2);
      return makeNodeHandle(j);
    }
    
    public int next(int paramInt1, int paramInt2)
    {
      return getNextSibling(paramInt2);
    }
    
    public int next(int paramInt1, int paramInt2, int paramInt3)
    {
      for (paramInt2 = _nextsib(makeNodeIdentity(paramInt2)); -1 != paramInt2; paramInt2 = _nextsib(paramInt2)) {
        if (m_exptype.elementAt(paramInt2) == paramInt3) {
          return makeNodeHandle(paramInt2);
        }
      }
      return -1;
    }
  }
  
  private class DescendantFromRootTraverser
    extends DTMDefaultBaseTraversers.DescendantTraverser
  {
    private DescendantFromRootTraverser()
    {
      super(null);
    }
    
    protected int getFirstPotential(int paramInt)
    {
      return _firstch(0);
    }
    
    protected int getSubtreeRoot(int paramInt)
    {
      return 0;
    }
    
    public int first(int paramInt)
    {
      return makeNodeHandle(_firstch(0));
    }
    
    public int first(int paramInt1, int paramInt2)
    {
      if (isIndexed(paramInt2))
      {
        i = 0;
        int j = getFirstPotential(i);
        return makeNodeHandle(getNextIndexed(i, j, paramInt2));
      }
      int i = getDocumentRoot(paramInt1);
      return next(i, i, paramInt2);
    }
  }
  
  private class DescendantOrSelfFromRootTraverser
    extends DTMDefaultBaseTraversers.DescendantTraverser
  {
    private DescendantOrSelfFromRootTraverser()
    {
      super(null);
    }
    
    protected int getFirstPotential(int paramInt)
    {
      return paramInt;
    }
    
    protected int getSubtreeRoot(int paramInt)
    {
      return makeNodeIdentity(getDocument());
    }
    
    public int first(int paramInt)
    {
      return getDocumentRoot(paramInt);
    }
    
    public int first(int paramInt1, int paramInt2)
    {
      if (isIndexed(paramInt2))
      {
        i = 0;
        int j = getFirstPotential(i);
        return makeNodeHandle(getNextIndexed(i, j, paramInt2));
      }
      int i = first(paramInt1);
      return next(i, i, paramInt2);
    }
  }
  
  private class DescendantOrSelfTraverser
    extends DTMDefaultBaseTraversers.DescendantTraverser
  {
    private DescendantOrSelfTraverser()
    {
      super(null);
    }
    
    protected int getFirstPotential(int paramInt)
    {
      return paramInt;
    }
    
    public int first(int paramInt)
    {
      return paramInt;
    }
  }
  
  private class DescendantTraverser
    extends DTMDefaultBaseTraversers.IndexedDTMAxisTraverser
  {
    private DescendantTraverser()
    {
      super(null);
    }
    
    protected int getFirstPotential(int paramInt)
    {
      return paramInt + 1;
    }
    
    protected boolean axisHasBeenProcessed(int paramInt)
    {
      return m_nextsib.elementAt(paramInt) != -2;
    }
    
    protected int getSubtreeRoot(int paramInt)
    {
      return makeNodeIdentity(paramInt);
    }
    
    protected boolean isDescendant(int paramInt1, int paramInt2)
    {
      return _parent(paramInt2) >= paramInt1;
    }
    
    protected boolean isAfterAxis(int paramInt1, int paramInt2)
    {
      do
      {
        if (paramInt2 == paramInt1) {
          return false;
        }
        paramInt2 = m_parent.elementAt(paramInt2);
      } while (paramInt2 >= paramInt1);
      return true;
    }
    
    public int first(int paramInt1, int paramInt2)
    {
      if (isIndexed(paramInt2))
      {
        int i = getSubtreeRoot(paramInt1);
        int j = getFirstPotential(i);
        return makeNodeHandle(getNextIndexed(i, j, paramInt2));
      }
      return next(paramInt1, paramInt1, paramInt2);
    }
    
    public int next(int paramInt1, int paramInt2)
    {
      int i = getSubtreeRoot(paramInt1);
      for (paramInt2 = makeNodeIdentity(paramInt2) + 1;; paramInt2++)
      {
        int j = _type(paramInt2);
        if (!isDescendant(i, paramInt2)) {
          return -1;
        }
        if ((2 != j) && (13 != j)) {
          return makeNodeHandle(paramInt2);
        }
      }
    }
    
    public int next(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = getSubtreeRoot(paramInt1);
      paramInt2 = makeNodeIdentity(paramInt2) + 1;
      if (isIndexed(paramInt3)) {
        return makeNodeHandle(getNextIndexed(i, paramInt2, paramInt3));
      }
      for (;;)
      {
        int j = _exptype(paramInt2);
        if (!isDescendant(i, paramInt2)) {
          return -1;
        }
        if (j == paramInt3) {
          return makeNodeHandle(paramInt2);
        }
        paramInt2++;
      }
    }
  }
  
  private class FollowingSiblingTraverser
    extends DTMAxisTraverser
  {
    private FollowingSiblingTraverser() {}
    
    public int next(int paramInt1, int paramInt2)
    {
      return getNextSibling(paramInt2);
    }
    
    public int next(int paramInt1, int paramInt2, int paramInt3)
    {
      while (-1 != (paramInt2 = getNextSibling(paramInt2))) {
        if (getExpandedTypeID(paramInt2) == paramInt3) {
          return paramInt2;
        }
      }
      return -1;
    }
  }
  
  private class FollowingTraverser
    extends DTMDefaultBaseTraversers.DescendantTraverser
  {
    private FollowingTraverser()
    {
      super(null);
    }
    
    public int first(int paramInt)
    {
      paramInt = makeNodeIdentity(paramInt);
      int j = _type(paramInt);
      int i;
      if ((2 == j) || (13 == j))
      {
        paramInt = _parent(paramInt);
        i = _firstch(paramInt);
        if (-1 != i) {
          return makeNodeHandle(i);
        }
      }
      do
      {
        i = _nextsib(paramInt);
        if (-1 == i) {
          paramInt = _parent(paramInt);
        }
      } while ((-1 == i) && (-1 != paramInt));
      return makeNodeHandle(i);
    }
    
    public int first(int paramInt1, int paramInt2)
    {
      int j = getNodeType(paramInt1);
      int i;
      if ((2 == j) || (13 == j))
      {
        paramInt1 = getParent(paramInt1);
        i = getFirstChild(paramInt1);
        if (-1 != i)
        {
          if (getExpandedTypeID(i) == paramInt2) {
            return i;
          }
          return next(paramInt1, i, paramInt2);
        }
      }
      do
      {
        i = getNextSibling(paramInt1);
        if (-1 == i)
        {
          paramInt1 = getParent(paramInt1);
        }
        else
        {
          if (getExpandedTypeID(i) == paramInt2) {
            return i;
          }
          return next(paramInt1, i, paramInt2);
        }
      } while ((-1 == i) && (-1 != paramInt1));
      return i;
    }
    
    public int next(int paramInt1, int paramInt2)
    {
      paramInt2 = makeNodeIdentity(paramInt2);
      int i;
      do
      {
        paramInt2++;
        i = _type(paramInt2);
        if (-1 == i) {
          return -1;
        }
      } while ((2 == i) || (13 == i));
      return makeNodeHandle(paramInt2);
    }
    
    public int next(int paramInt1, int paramInt2, int paramInt3)
    {
      paramInt2 = makeNodeIdentity(paramInt2);
      int i;
      do
      {
        paramInt2++;
        i = _exptype(paramInt2);
        if (-1 == i) {
          return -1;
        }
      } while (i != paramInt3);
      return makeNodeHandle(paramInt2);
    }
  }
  
  private abstract class IndexedDTMAxisTraverser
    extends DTMAxisTraverser
  {
    private IndexedDTMAxisTraverser() {}
    
    protected final boolean isIndexed(int paramInt)
    {
      return (m_indexing) && (1 == m_expandedNameTable.getType(paramInt));
    }
    
    protected abstract boolean isAfterAxis(int paramInt1, int paramInt2);
    
    protected abstract boolean axisHasBeenProcessed(int paramInt);
    
    protected int getNextIndexed(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = m_expandedNameTable.getNamespaceID(paramInt3);
      int j = m_expandedNameTable.getLocalNameID(paramInt3);
      for (;;)
      {
        int k = findElementFromIndex(i, j, paramInt2);
        if (-2 != k)
        {
          if (isAfterAxis(paramInt1, k)) {
            return -1;
          }
          return k;
        }
        if (axisHasBeenProcessed(paramInt1)) {
          break;
        }
        nextNode();
      }
      return -1;
    }
  }
  
  private class NamespaceDeclsTraverser
    extends DTMAxisTraverser
  {
    private NamespaceDeclsTraverser() {}
    
    public int next(int paramInt1, int paramInt2)
    {
      return paramInt1 == paramInt2 ? getFirstNamespaceNode(paramInt1, false) : getNextNamespaceNode(paramInt1, paramInt2, false);
    }
    
    public int next(int paramInt1, int paramInt2, int paramInt3)
    {
      paramInt2 = paramInt1 == paramInt2 ? getFirstNamespaceNode(paramInt1, false) : getNextNamespaceNode(paramInt1, paramInt2, false);
      do
      {
        if (getExpandedTypeID(paramInt2) == paramInt3) {
          return paramInt2;
        }
      } while (-1 != (paramInt2 = getNextNamespaceNode(paramInt1, paramInt2, false)));
      return -1;
    }
  }
  
  private class NamespaceTraverser
    extends DTMAxisTraverser
  {
    private NamespaceTraverser() {}
    
    public int next(int paramInt1, int paramInt2)
    {
      return paramInt1 == paramInt2 ? getFirstNamespaceNode(paramInt1, true) : getNextNamespaceNode(paramInt1, paramInt2, true);
    }
    
    public int next(int paramInt1, int paramInt2, int paramInt3)
    {
      paramInt2 = paramInt1 == paramInt2 ? getFirstNamespaceNode(paramInt1, true) : getNextNamespaceNode(paramInt1, paramInt2, true);
      do
      {
        if (getExpandedTypeID(paramInt2) == paramInt3) {
          return paramInt2;
        }
      } while (-1 != (paramInt2 = getNextNamespaceNode(paramInt1, paramInt2, true)));
      return -1;
    }
  }
  
  private class ParentTraverser
    extends DTMAxisTraverser
  {
    private ParentTraverser() {}
    
    public int first(int paramInt)
    {
      return getParent(paramInt);
    }
    
    public int first(int paramInt1, int paramInt2)
    {
      paramInt1 = makeNodeIdentity(paramInt1);
      while (-1 != (paramInt1 = m_parent.elementAt(paramInt1))) {
        if (m_exptype.elementAt(paramInt1) == paramInt2) {
          return makeNodeHandle(paramInt1);
        }
      }
      return -1;
    }
    
    public int next(int paramInt1, int paramInt2)
    {
      return -1;
    }
    
    public int next(int paramInt1, int paramInt2, int paramInt3)
    {
      return -1;
    }
  }
  
  private class PrecedingAndAncestorTraverser
    extends DTMAxisTraverser
  {
    private PrecedingAndAncestorTraverser() {}
    
    public int next(int paramInt1, int paramInt2)
    {
      int i = makeNodeIdentity(paramInt1);
      for (paramInt2 = makeNodeIdentity(paramInt2) - 1; paramInt2 >= 0; paramInt2--)
      {
        int j = _type(paramInt2);
        if ((2 != j) && (13 != j)) {
          return makeNodeHandle(paramInt2);
        }
      }
      return -1;
    }
    
    public int next(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = makeNodeIdentity(paramInt1);
      for (paramInt2 = makeNodeIdentity(paramInt2) - 1; paramInt2 >= 0; paramInt2--)
      {
        int j = m_exptype.elementAt(paramInt2);
        if (j == paramInt3) {
          return makeNodeHandle(paramInt2);
        }
      }
      return -1;
    }
  }
  
  private class PrecedingSiblingTraverser
    extends DTMAxisTraverser
  {
    private PrecedingSiblingTraverser() {}
    
    public int next(int paramInt1, int paramInt2)
    {
      return getPreviousSibling(paramInt2);
    }
    
    public int next(int paramInt1, int paramInt2, int paramInt3)
    {
      while (-1 != (paramInt2 = getPreviousSibling(paramInt2))) {
        if (getExpandedTypeID(paramInt2) == paramInt3) {
          return paramInt2;
        }
      }
      return -1;
    }
  }
  
  private class PrecedingTraverser
    extends DTMAxisTraverser
  {
    private PrecedingTraverser() {}
    
    protected boolean isAncestor(int paramInt1, int paramInt2)
    {
      for (paramInt1 = m_parent.elementAt(paramInt1); -1 != paramInt1; paramInt1 = m_parent.elementAt(paramInt1)) {
        if (paramInt1 == paramInt2) {
          return true;
        }
      }
      return false;
    }
    
    public int next(int paramInt1, int paramInt2)
    {
      int i = makeNodeIdentity(paramInt1);
      for (paramInt2 = makeNodeIdentity(paramInt2) - 1; paramInt2 >= 0; paramInt2--)
      {
        int j = _type(paramInt2);
        if ((2 != j) && (13 != j) && (!isAncestor(i, paramInt2))) {
          return makeNodeHandle(paramInt2);
        }
      }
      return -1;
    }
    
    public int next(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = makeNodeIdentity(paramInt1);
      for (paramInt2 = makeNodeIdentity(paramInt2) - 1; paramInt2 >= 0; paramInt2--)
      {
        int j = m_exptype.elementAt(paramInt2);
        if ((j == paramInt3) && (!isAncestor(i, paramInt2))) {
          return makeNodeHandle(paramInt2);
        }
      }
      return -1;
    }
  }
  
  private class RootTraverser
    extends DTMDefaultBaseTraversers.AllFromRootTraverser
  {
    private RootTraverser()
    {
      super(null);
    }
    
    public int first(int paramInt1, int paramInt2)
    {
      int i = getDocumentRoot(paramInt1);
      return getExpandedTypeID(i) == paramInt2 ? i : -1;
    }
    
    public int next(int paramInt1, int paramInt2)
    {
      return -1;
    }
    
    public int next(int paramInt1, int paramInt2, int paramInt3)
    {
      return -1;
    }
  }
  
  private class SelfTraverser
    extends DTMAxisTraverser
  {
    private SelfTraverser() {}
    
    public int first(int paramInt)
    {
      return paramInt;
    }
    
    public int first(int paramInt1, int paramInt2)
    {
      return getExpandedTypeID(paramInt1) == paramInt2 ? paramInt1 : -1;
    }
    
    public int next(int paramInt1, int paramInt2)
    {
      return -1;
    }
    
    public int next(int paramInt1, int paramInt2, int paramInt3)
    {
      return -1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMDefaultBaseTraversers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */