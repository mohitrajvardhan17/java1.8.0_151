package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMException;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.NodeVector;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import javax.xml.transform.Source;

public abstract class DTMDefaultBaseIterators
  extends DTMDefaultBaseTraversers
{
  public DTMDefaultBaseIterators(DTMManager paramDTMManager, Source paramSource, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean)
  {
    super(paramDTMManager, paramSource, paramInt, paramDTMWSFilter, paramXMLStringFactory, paramBoolean);
  }
  
  public DTMDefaultBaseIterators(DTMManager paramDTMManager, Source paramSource, int paramInt1, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, boolean paramBoolean3)
  {
    super(paramDTMManager, paramSource, paramInt1, paramDTMWSFilter, paramXMLStringFactory, paramBoolean1, paramInt2, paramBoolean2, paramBoolean3);
  }
  
  public DTMAxisIterator getTypedAxisIterator(int paramInt1, int paramInt2)
  {
    Object localObject = null;
    switch (paramInt1)
    {
    case 13: 
      localObject = new TypedSingletonIterator(paramInt2);
      break;
    case 3: 
      localObject = new TypedChildrenIterator(paramInt2);
      break;
    case 10: 
      return new ParentIterator().setNodeType(paramInt2);
    case 0: 
      return new TypedAncestorIterator(paramInt2);
    case 1: 
      return new TypedAncestorIterator(paramInt2).includeSelf();
    case 2: 
      return new TypedAttributeIterator(paramInt2);
    case 4: 
      localObject = new TypedDescendantIterator(paramInt2);
      break;
    case 5: 
      localObject = new TypedDescendantIterator(paramInt2).includeSelf();
      break;
    case 6: 
      localObject = new TypedFollowingIterator(paramInt2);
      break;
    case 11: 
      localObject = new TypedPrecedingIterator(paramInt2);
      break;
    case 7: 
      localObject = new TypedFollowingSiblingIterator(paramInt2);
      break;
    case 12: 
      localObject = new TypedPrecedingSiblingIterator(paramInt2);
      break;
    case 9: 
      localObject = new TypedNamespaceIterator(paramInt2);
      break;
    case 19: 
      localObject = new TypedRootIterator(paramInt2);
      break;
    case 8: 
    case 14: 
    case 15: 
    case 16: 
    case 17: 
    case 18: 
    default: 
      throw new DTMException(XMLMessages.createXMLMessage("ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED", new Object[] { Axis.getNames(paramInt1) }));
    }
    return (DTMAxisIterator)localObject;
  }
  
  public DTMAxisIterator getAxisIterator(int paramInt)
  {
    Object localObject = null;
    switch (paramInt)
    {
    case 13: 
      localObject = new SingletonIterator();
      break;
    case 3: 
      localObject = new ChildrenIterator();
      break;
    case 10: 
      return new ParentIterator();
    case 0: 
      return new AncestorIterator();
    case 1: 
      return new AncestorIterator().includeSelf();
    case 2: 
      return new AttributeIterator();
    case 4: 
      localObject = new DescendantIterator();
      break;
    case 5: 
      localObject = new DescendantIterator().includeSelf();
      break;
    case 6: 
      localObject = new FollowingIterator();
      break;
    case 11: 
      localObject = new PrecedingIterator();
      break;
    case 7: 
      localObject = new FollowingSiblingIterator();
      break;
    case 12: 
      localObject = new PrecedingSiblingIterator();
      break;
    case 9: 
      localObject = new NamespaceIterator();
      break;
    case 19: 
      localObject = new RootIterator();
      break;
    case 8: 
    case 14: 
    case 15: 
    case 16: 
    case 17: 
    case 18: 
    default: 
      throw new DTMException(XMLMessages.createXMLMessage("ER_ITERATOR_AXIS_NOT_IMPLEMENTED", new Object[] { Axis.getNames(paramInt) }));
    }
    return (DTMAxisIterator)localObject;
  }
  
  public class AncestorIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    NodeVector m_ancestors = new NodeVector();
    int m_ancestorsPos;
    int m_markedPos;
    int m_realStartNode;
    
    public AncestorIterator()
    {
      super();
    }
    
    public int getStartNode()
    {
      return m_realStartNode;
    }
    
    public final boolean isReverse()
    {
      return true;
    }
    
    public DTMAxisIterator cloneIterator()
    {
      _isRestartable = false;
      try
      {
        AncestorIterator localAncestorIterator = (AncestorIterator)super.clone();
        _startNode = _startNode;
        return localAncestorIterator;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new DTMException(XMLMessages.createXMLMessage("ER_ITERATOR_CLONE_NOT_SUPPORTED", null));
      }
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      m_realStartNode = paramInt;
      if (_isRestartable)
      {
        int i = makeNodeIdentity(paramInt);
        if ((!_includeSelf) && (paramInt != -1))
        {
          i = _parent(i);
          paramInt = makeNodeHandle(i);
        }
        _startNode = paramInt;
        while (i != -1)
        {
          m_ancestors.addElement(paramInt);
          i = _parent(i);
          paramInt = makeNodeHandle(i);
        }
        m_ancestorsPos = (m_ancestors.size() - 1);
        _currentNode = (m_ancestorsPos >= 0 ? m_ancestors.elementAt(m_ancestorsPos) : -1);
        return resetPosition();
      }
      return this;
    }
    
    public DTMAxisIterator reset()
    {
      m_ancestorsPos = (m_ancestors.size() - 1);
      _currentNode = (m_ancestorsPos >= 0 ? m_ancestors.elementAt(m_ancestorsPos) : -1);
      return resetPosition();
    }
    
    public int next()
    {
      int i = _currentNode;
      int j = --m_ancestorsPos;
      _currentNode = (j >= 0 ? m_ancestors.elementAt(m_ancestorsPos) : -1);
      return returnNode(i);
    }
    
    public void setMark()
    {
      m_markedPos = m_ancestorsPos;
    }
    
    public void gotoMark()
    {
      m_ancestorsPos = m_markedPos;
      _currentNode = (m_ancestorsPos >= 0 ? m_ancestors.elementAt(m_ancestorsPos) : -1);
    }
  }
  
  public final class AttributeIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    public AttributeIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        _startNode = paramInt;
        _currentNode = getFirstAttributeIdentity(makeNodeIdentity(paramInt));
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      int i = _currentNode;
      if (i != -1)
      {
        _currentNode = getNextAttributeIdentity(i);
        return returnNode(makeNodeHandle(i));
      }
      return -1;
    }
  }
  
  public final class ChildrenIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    public ChildrenIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        _startNode = paramInt;
        _currentNode = (paramInt == -1 ? -1 : _firstch(makeNodeIdentity(paramInt)));
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      if (_currentNode != -1)
      {
        int i = _currentNode;
        _currentNode = _nextsib(i);
        return returnNode(makeNodeHandle(i));
      }
      return -1;
    }
  }
  
  public class DescendantIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    public DescendantIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        paramInt = makeNodeIdentity(paramInt);
        _startNode = paramInt;
        if (_includeSelf) {
          paramInt--;
        }
        _currentNode = paramInt;
        return resetPosition();
      }
      return this;
    }
    
    protected boolean isDescendant(int paramInt)
    {
      return (_parent(paramInt) >= _startNode) || (_startNode == paramInt);
    }
    
    public int next()
    {
      if (_startNode == -1) {
        return -1;
      }
      if ((_includeSelf) && (_currentNode + 1 == _startNode)) {
        return returnNode(makeNodeHandle(++_currentNode));
      }
      int i = _currentNode;
      int j;
      do
      {
        i++;
        j = _type(i);
        if ((-1 == j) || (!isDescendant(i)))
        {
          _currentNode = -1;
          return -1;
        }
      } while ((2 == j) || (3 == j) || (13 == j));
      _currentNode = i;
      return returnNode(makeNodeHandle(i));
    }
    
    public DTMAxisIterator reset()
    {
      boolean bool = _isRestartable;
      _isRestartable = true;
      setStartNode(makeNodeHandle(_startNode));
      _isRestartable = bool;
      return this;
    }
  }
  
  public class FollowingIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    DTMAxisTraverser m_traverser = getAxisTraverser(6);
    
    public FollowingIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        _startNode = paramInt;
        _currentNode = m_traverser.first(paramInt);
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      int i = _currentNode;
      _currentNode = m_traverser.next(_startNode, _currentNode);
      return returnNode(i);
    }
  }
  
  public class FollowingSiblingIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    public FollowingSiblingIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        _startNode = paramInt;
        _currentNode = makeNodeIdentity(paramInt);
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      _currentNode = (_currentNode == -1 ? -1 : _nextsib(_currentNode));
      return returnNode(makeNodeHandle(_currentNode));
    }
  }
  
  public abstract class InternalAxisIteratorBase
    extends DTMAxisIteratorBase
  {
    protected int _currentNode;
    
    public InternalAxisIteratorBase() {}
    
    public void setMark()
    {
      _markedNode = _currentNode;
    }
    
    public void gotoMark()
    {
      _currentNode = _markedNode;
    }
  }
  
  public final class NamespaceAttributeIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private final int _nsType;
    
    public NamespaceAttributeIterator(int paramInt)
    {
      super();
      _nsType = paramInt;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        _startNode = paramInt;
        _currentNode = getFirstNamespaceNode(paramInt, false);
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      int i = _currentNode;
      if (-1 != i) {
        _currentNode = getNextNamespaceNode(_startNode, i, false);
      }
      return returnNode(i);
    }
  }
  
  public final class NamespaceChildrenIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private final int _nsType;
    
    public NamespaceChildrenIterator(int paramInt)
    {
      super();
      _nsType = paramInt;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        _startNode = paramInt;
        _currentNode = (paramInt == -1 ? -1 : -2);
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      if (_currentNode != -1) {
        for (int i = -2 == _currentNode ? _firstch(makeNodeIdentity(_startNode)) : _nextsib(_currentNode); i != -1; i = _nextsib(i)) {
          if (m_expandedNameTable.getNamespaceID(_exptype(i)) == _nsType)
          {
            _currentNode = i;
            return returnNode(i);
          }
        }
      }
      return -1;
    }
  }
  
  public class NamespaceIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    public NamespaceIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        _startNode = paramInt;
        _currentNode = getFirstNamespaceNode(paramInt, true);
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      int i = _currentNode;
      if (-1 != i) {
        _currentNode = getNextNamespaceNode(_startNode, i, true);
      }
      return returnNode(i);
    }
  }
  
  public class NthDescendantIterator
    extends DTMDefaultBaseIterators.DescendantIterator
  {
    int _pos;
    
    public NthDescendantIterator(int paramInt)
    {
      super();
      _pos = paramInt;
    }
    
    public int next()
    {
      int i;
      while ((i = super.next()) != -1)
      {
        i = makeNodeIdentity(i);
        int j = _parent(i);
        int k = _firstch(j);
        int m = 0;
        do
        {
          int n = _type(k);
          if (1 == n) {
            m++;
          }
        } while ((m < _pos) && ((k = _nextsib(k)) != -1));
        if (i == k) {
          return i;
        }
      }
      return -1;
    }
  }
  
  public final class ParentIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private int _nodeType = -1;
    
    public ParentIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        _startNode = paramInt;
        _currentNode = getParent(paramInt);
        return resetPosition();
      }
      return this;
    }
    
    public DTMAxisIterator setNodeType(int paramInt)
    {
      _nodeType = paramInt;
      return this;
    }
    
    public int next()
    {
      int i = _currentNode;
      if (_nodeType >= 14)
      {
        if (_nodeType != getExpandedTypeID(_currentNode)) {
          i = -1;
        }
      }
      else if ((_nodeType != -1) && (_nodeType != getNodeType(_currentNode))) {
        i = -1;
      }
      _currentNode = -1;
      return returnNode(i);
    }
  }
  
  public class PrecedingIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private final int _maxAncestors = 8;
    protected int[] _stack = new int[8];
    protected int _sp;
    protected int _oldsp;
    protected int _markedsp;
    protected int _markedNode;
    protected int _markedDescendant;
    
    public PrecedingIterator()
    {
      super();
    }
    
    public boolean isReverse()
    {
      return true;
    }
    
    public DTMAxisIterator cloneIterator()
    {
      _isRestartable = false;
      try
      {
        PrecedingIterator localPrecedingIterator = (PrecedingIterator)super.clone();
        int[] arrayOfInt = new int[_stack.length];
        System.arraycopy(_stack, 0, arrayOfInt, 0, _stack.length);
        _stack = arrayOfInt;
        return localPrecedingIterator;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new DTMException(XMLMessages.createXMLMessage("ER_ITERATOR_CLONE_NOT_SUPPORTED", null));
      }
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        paramInt = makeNodeIdentity(paramInt);
        if (_type(paramInt) == 2) {
          paramInt = _parent(paramInt);
        }
        _startNode = paramInt;
        int j;
        _stack[(j = 0)] = paramInt;
        int i = paramInt;
        while ((i = _parent(i)) != -1)
        {
          j++;
          if (j == _stack.length)
          {
            int[] arrayOfInt = new int[j + 4];
            System.arraycopy(_stack, 0, arrayOfInt, 0, j);
            _stack = arrayOfInt;
          }
          _stack[j] = i;
        }
        if (j > 0) {
          j--;
        }
        _currentNode = _stack[j];
        _oldsp = (_sp = j);
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      for (_currentNode += 1; _sp >= 0; _currentNode += 1) {
        if (_currentNode < _stack[_sp])
        {
          if ((_type(_currentNode) != 2) && (_type(_currentNode) != 13)) {
            return returnNode(makeNodeHandle(_currentNode));
          }
        }
        else {
          _sp -= 1;
        }
      }
      return -1;
    }
    
    public DTMAxisIterator reset()
    {
      _sp = _oldsp;
      return resetPosition();
    }
    
    public void setMark()
    {
      _markedsp = _sp;
      _markedNode = _currentNode;
      _markedDescendant = _stack[0];
    }
    
    public void gotoMark()
    {
      _sp = _markedsp;
      _currentNode = _markedNode;
    }
  }
  
  public class PrecedingSiblingIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    protected int _startNodeID;
    
    public PrecedingSiblingIterator()
    {
      super();
    }
    
    public boolean isReverse()
    {
      return true;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        _startNode = paramInt;
        paramInt = _startNodeID = makeNodeIdentity(paramInt);
        if (paramInt == -1)
        {
          _currentNode = paramInt;
          return resetPosition();
        }
        int i = m_expandedNameTable.getType(_exptype(paramInt));
        if ((2 == i) || (13 == i))
        {
          _currentNode = paramInt;
        }
        else
        {
          _currentNode = _parent(paramInt);
          if (-1 != _currentNode) {
            _currentNode = _firstch(_currentNode);
          } else {
            _currentNode = paramInt;
          }
        }
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      if ((_currentNode == _startNodeID) || (_currentNode == -1)) {
        return -1;
      }
      int i = _currentNode;
      _currentNode = _nextsib(i);
      return returnNode(makeNodeHandle(i));
    }
  }
  
  public class RootIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    public RootIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (_isRestartable)
      {
        _startNode = getDocumentRoot(paramInt);
        _currentNode = -1;
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      if (_startNode == _currentNode) {
        return -1;
      }
      _currentNode = _startNode;
      return returnNode(_startNode);
    }
  }
  
  public class SingletonIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private boolean _isConstant;
    
    public SingletonIterator()
    {
      this(Integer.MIN_VALUE, false);
    }
    
    public SingletonIterator(int paramInt)
    {
      this(paramInt, false);
    }
    
    public SingletonIterator(int paramInt, boolean paramBoolean)
    {
      super();
      _currentNode = (_startNode = paramInt);
      _isConstant = paramBoolean;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isConstant)
      {
        _currentNode = _startNode;
        return resetPosition();
      }
      if (_isRestartable)
      {
        _currentNode = (_startNode = paramInt);
        return resetPosition();
      }
      return this;
    }
    
    public DTMAxisIterator reset()
    {
      if (_isConstant)
      {
        _currentNode = _startNode;
        return resetPosition();
      }
      boolean bool = _isRestartable;
      _isRestartable = true;
      setStartNode(_startNode);
      _isRestartable = bool;
      return this;
    }
    
    public int next()
    {
      int i = _currentNode;
      _currentNode = -1;
      return returnNode(i);
    }
  }
  
  public final class TypedAncestorIterator
    extends DTMDefaultBaseIterators.AncestorIterator
  {
    private final int _nodeType;
    
    public TypedAncestorIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      m_realStartNode = paramInt;
      if (_isRestartable)
      {
        int i = makeNodeIdentity(paramInt);
        int j = _nodeType;
        if ((!_includeSelf) && (paramInt != -1)) {
          i = _parent(i);
        }
        _startNode = paramInt;
        int k;
        if (j >= 14) {
          while (i != -1)
          {
            k = _exptype(i);
            if (k == j) {
              m_ancestors.addElement(makeNodeHandle(i));
            }
            i = _parent(i);
          }
        }
        while (i != -1)
        {
          k = _exptype(i);
          if (((k >= 14) && (m_expandedNameTable.getType(k) == j)) || ((k < 14) && (k == j))) {
            m_ancestors.addElement(makeNodeHandle(i));
          }
          i = _parent(i);
        }
        m_ancestorsPos = (m_ancestors.size() - 1);
        _currentNode = (m_ancestorsPos >= 0 ? m_ancestors.elementAt(m_ancestorsPos) : -1);
        return resetPosition();
      }
      return this;
    }
  }
  
  public final class TypedAttributeIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private final int _nodeType;
    
    public TypedAttributeIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (_isRestartable)
      {
        _startNode = paramInt;
        _currentNode = getTypedAttribute(paramInt, _nodeType);
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      int i = _currentNode;
      _currentNode = -1;
      return returnNode(i);
    }
  }
  
  public final class TypedChildrenIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private final int _nodeType;
    
    public TypedChildrenIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        _startNode = paramInt;
        _currentNode = (paramInt == -1 ? -1 : _firstch(makeNodeIdentity(_startNode)));
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      int j = _currentNode;
      int k = _nodeType;
      if (k >= 14) {
        while ((j != -1) && (_exptype(j) != k)) {
          j = _nextsib(j);
        }
      }
      while (j != -1)
      {
        int i = _exptype(j);
        if (i < 14 ? i == k : m_expandedNameTable.getType(i) == k) {
          break;
        }
        j = _nextsib(j);
      }
      if (j == -1)
      {
        _currentNode = -1;
        return -1;
      }
      _currentNode = _nextsib(j);
      return returnNode(makeNodeHandle(j));
    }
  }
  
  public final class TypedDescendantIterator
    extends DTMDefaultBaseIterators.DescendantIterator
  {
    private final int _nodeType;
    
    public TypedDescendantIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public int next()
    {
      if (_startNode == -1) {
        return -1;
      }
      int i = _currentNode;
      int j;
      do
      {
        i++;
        j = _type(i);
        if ((-1 == j) || (!isDescendant(i)))
        {
          _currentNode = -1;
          return -1;
        }
      } while ((j != _nodeType) && (_exptype(i) != _nodeType));
      _currentNode = i;
      return returnNode(makeNodeHandle(i));
    }
  }
  
  public final class TypedFollowingIterator
    extends DTMDefaultBaseIterators.FollowingIterator
  {
    private final int _nodeType;
    
    public TypedFollowingIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public int next()
    {
      int i;
      do
      {
        i = _currentNode;
        _currentNode = m_traverser.next(_startNode, _currentNode);
      } while ((i != -1) && (getExpandedTypeID(i) != _nodeType) && (getNodeType(i) != _nodeType));
      return i == -1 ? -1 : returnNode(i);
    }
  }
  
  public final class TypedFollowingSiblingIterator
    extends DTMDefaultBaseIterators.FollowingSiblingIterator
  {
    private final int _nodeType;
    
    public TypedFollowingSiblingIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public int next()
    {
      if (_currentNode == -1) {
        return -1;
      }
      int i = _currentNode;
      int k = _nodeType;
      if (k >= 14) {
        do
        {
          i = _nextsib(i);
          if (i == -1) {
            break;
          }
        } while (_exptype(i) != k);
      } else {
        while ((i = _nextsib(i)) != -1)
        {
          int j = _exptype(i);
          if (j < 14)
          {
            if (j == k) {
              break;
            }
          }
          else if (m_expandedNameTable.getType(j) == k) {
            break;
          }
        }
      }
      _currentNode = i;
      return _currentNode == -1 ? -1 : returnNode(makeNodeHandle(_currentNode));
    }
  }
  
  public class TypedNamespaceIterator
    extends DTMDefaultBaseIterators.NamespaceIterator
  {
    private final int _nodeType;
    
    public TypedNamespaceIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public int next()
    {
      for (int i = _currentNode; i != -1; i = getNextNamespaceNode(_startNode, i, true)) {
        if ((getExpandedTypeID(i) == _nodeType) || (getNodeType(i) == _nodeType) || (getNamespaceType(i) == _nodeType))
        {
          _currentNode = i;
          return returnNode(i);
        }
      }
      return _currentNode = -1;
    }
  }
  
  public final class TypedPrecedingIterator
    extends DTMDefaultBaseIterators.PrecedingIterator
  {
    private final int _nodeType;
    
    public TypedPrecedingIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public int next()
    {
      int i = _currentNode;
      int j = _nodeType;
      if (j >= 14) {
        do
        {
          do
          {
            i += 1;
            if (_sp < 0)
            {
              i = -1;
              break label168;
            }
            if (i < _stack[_sp]) {
              break;
            }
          } while (--_sp >= 0);
          i = -1;
          break;
        } while (_exptype(i) != j);
      } else {
        for (;;)
        {
          i += 1;
          if (_sp < 0)
          {
            i = -1;
          }
          else if (i >= _stack[_sp])
          {
            if (--_sp < 0) {
              i = -1;
            }
          }
          else
          {
            int k = _exptype(i);
            if (k < 14)
            {
              if (k == j) {
                break;
              }
            }
            else if (m_expandedNameTable.getType(k) == j) {
              break;
            }
          }
        }
      }
      label168:
      _currentNode = i;
      return i == -1 ? -1 : returnNode(makeNodeHandle(i));
    }
  }
  
  public final class TypedPrecedingSiblingIterator
    extends DTMDefaultBaseIterators.PrecedingSiblingIterator
  {
    private final int _nodeType;
    
    public TypedPrecedingSiblingIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public int next()
    {
      int i = _currentNode;
      int k = _nodeType;
      int m = _startNodeID;
      if (k >= 14) {
        while ((i != -1) && (i != m) && (_exptype(i) != k)) {
          i = _nextsib(i);
        }
      }
      while ((i != -1) && (i != m))
      {
        int j = _exptype(i);
        if (j < 14 ? j == k : m_expandedNameTable.getType(j) == k) {
          break;
        }
        i = _nextsib(i);
      }
      if ((i == -1) || (i == _startNodeID))
      {
        _currentNode = -1;
        return -1;
      }
      _currentNode = _nextsib(i);
      return returnNode(makeNodeHandle(i));
    }
  }
  
  public class TypedRootIterator
    extends DTMDefaultBaseIterators.RootIterator
  {
    private final int _nodeType;
    
    public TypedRootIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public int next()
    {
      if (_startNode == _currentNode) {
        return -1;
      }
      int i = _nodeType;
      int j = _startNode;
      int k = getExpandedTypeID(j);
      _currentNode = j;
      if (i >= 14)
      {
        if (i == k) {
          return returnNode(j);
        }
      }
      else if (k < 14)
      {
        if (k == i) {
          return returnNode(j);
        }
      }
      else if (m_expandedNameTable.getType(k) == i) {
        return returnNode(j);
      }
      return -1;
    }
  }
  
  public final class TypedSingletonIterator
    extends DTMDefaultBaseIterators.SingletonIterator
  {
    private final int _nodeType;
    
    public TypedSingletonIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public int next()
    {
      int i = _currentNode;
      int j = _nodeType;
      _currentNode = -1;
      if (j >= 14)
      {
        if (getExpandedTypeID(i) == j) {
          return returnNode(i);
        }
      }
      else if (getNodeType(i) == j) {
        return returnNode(i);
      }
      return -1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMDefaultBaseIterators.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */