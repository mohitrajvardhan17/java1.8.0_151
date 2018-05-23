package javax.swing.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class DefaultStyledDocument
  extends AbstractDocument
  implements StyledDocument
{
  public static final int BUFFER_SIZE_DEFAULT = 4096;
  protected ElementBuffer buffer = new ElementBuffer(createDefaultRoot());
  private transient Vector<Style> listeningStyles = new Vector();
  private transient ChangeListener styleChangeListener;
  private transient ChangeListener styleContextChangeListener;
  private transient ChangeUpdateRunnable updateRunnable;
  
  public DefaultStyledDocument(AbstractDocument.Content paramContent, StyleContext paramStyleContext)
  {
    super(paramContent, paramStyleContext);
    Style localStyle = paramStyleContext.getStyle("default");
    setLogicalStyle(0, localStyle);
  }
  
  public DefaultStyledDocument(StyleContext paramStyleContext)
  {
    this(new GapContent(4096), paramStyleContext);
  }
  
  public DefaultStyledDocument()
  {
    this(new GapContent(4096), new StyleContext());
  }
  
  public Element getDefaultRootElement()
  {
    return buffer.getRootElement();
  }
  
  protected void create(ElementSpec[] paramArrayOfElementSpec)
  {
    try
    {
      if (getLength() != 0) {
        remove(0, getLength());
      }
      writeLock();
      AbstractDocument.Content localContent = getContent();
      int i = paramArrayOfElementSpec.length;
      StringBuilder localStringBuilder = new StringBuilder();
      for (int j = 0; j < i; j++)
      {
        ElementSpec localElementSpec = paramArrayOfElementSpec[j];
        if (localElementSpec.getLength() > 0) {
          localStringBuilder.append(localElementSpec.getArray(), localElementSpec.getOffset(), localElementSpec.getLength());
        }
      }
      UndoableEdit localUndoableEdit = localContent.insertString(0, localStringBuilder.toString());
      int k = localStringBuilder.length();
      AbstractDocument.DefaultDocumentEvent localDefaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, 0, k, DocumentEvent.EventType.INSERT);
      localDefaultDocumentEvent.addEdit(localUndoableEdit);
      buffer.create(k, paramArrayOfElementSpec, localDefaultDocumentEvent);
      super.insertUpdate(localDefaultDocumentEvent, null);
      localDefaultDocumentEvent.end();
      fireInsertUpdate(localDefaultDocumentEvent);
      fireUndoableEditUpdate(new UndoableEditEvent(this, localDefaultDocumentEvent));
    }
    catch (BadLocationException localBadLocationException)
    {
      throw new StateInvariantError("problem initializing");
    }
    finally
    {
      writeUnlock();
    }
  }
  
  protected void insert(int paramInt, ElementSpec[] paramArrayOfElementSpec)
    throws BadLocationException
  {
    if ((paramArrayOfElementSpec == null) || (paramArrayOfElementSpec.length == 0)) {
      return;
    }
    try
    {
      writeLock();
      AbstractDocument.Content localContent = getContent();
      int i = paramArrayOfElementSpec.length;
      StringBuilder localStringBuilder = new StringBuilder();
      for (int j = 0; j < i; j++)
      {
        ElementSpec localElementSpec = paramArrayOfElementSpec[j];
        if (localElementSpec.getLength() > 0) {
          localStringBuilder.append(localElementSpec.getArray(), localElementSpec.getOffset(), localElementSpec.getLength());
        }
      }
      if (localStringBuilder.length() == 0) {
        return;
      }
      UndoableEdit localUndoableEdit = localContent.insertString(paramInt, localStringBuilder.toString());
      int k = localStringBuilder.length();
      AbstractDocument.DefaultDocumentEvent localDefaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, paramInt, k, DocumentEvent.EventType.INSERT);
      localDefaultDocumentEvent.addEdit(localUndoableEdit);
      buffer.insert(paramInt, k, paramArrayOfElementSpec, localDefaultDocumentEvent);
      super.insertUpdate(localDefaultDocumentEvent, null);
      localDefaultDocumentEvent.end();
      fireInsertUpdate(localDefaultDocumentEvent);
      fireUndoableEditUpdate(new UndoableEditEvent(this, localDefaultDocumentEvent));
    }
    finally
    {
      writeUnlock();
    }
  }
  
  /* Error */
  public void removeElement(Element paramElement)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 556	javax/swing/text/DefaultStyledDocument:writeLock	()V
    //   4: aload_0
    //   5: aload_1
    //   6: invokespecial 573	javax/swing/text/DefaultStyledDocument:removeElementImpl	(Ljavax/swing/text/Element;)V
    //   9: aload_0
    //   10: invokevirtual 557	javax/swing/text/DefaultStyledDocument:writeUnlock	()V
    //   13: goto +10 -> 23
    //   16: astore_2
    //   17: aload_0
    //   18: invokevirtual 557	javax/swing/text/DefaultStyledDocument:writeUnlock	()V
    //   21: aload_2
    //   22: athrow
    //   23: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	24	0	this	DefaultStyledDocument
    //   0	24	1	paramElement	Element
    //   16	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	9	16	finally
  }
  
  private void removeElementImpl(Element paramElement)
  {
    if (paramElement.getDocument() != this) {
      throw new IllegalArgumentException("element doesn't belong to document");
    }
    AbstractDocument.BranchElement localBranchElement1 = (AbstractDocument.BranchElement)paramElement.getParentElement();
    if (localBranchElement1 == null) {
      throw new IllegalArgumentException("can't remove the root element");
    }
    int i = paramElement.getStartOffset();
    int j = i;
    int k = paramElement.getEndOffset();
    int m = k;
    int n = getLength() + 1;
    AbstractDocument.Content localContent = getContent();
    int i1 = 0;
    boolean bool = Utilities.isComposedTextElement(paramElement);
    if (k >= n)
    {
      if (i <= 0) {
        throw new IllegalArgumentException("can't remove the whole content");
      }
      m = n - 1;
      try
      {
        if (localContent.getString(i - 1, 1).charAt(0) == '\n') {
          j--;
        }
      }
      catch (BadLocationException localBadLocationException1)
      {
        throw new IllegalStateException(localBadLocationException1);
      }
      i1 = 1;
    }
    int i2 = m - j;
    AbstractDocument.DefaultDocumentEvent localDefaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, j, i2, DocumentEvent.EventType.REMOVE);
    UndoableEdit localUndoableEdit = null;
    while (localBranchElement1.getElementCount() == 1)
    {
      paramElement = localBranchElement1;
      localBranchElement1 = (AbstractDocument.BranchElement)localBranchElement1.getParentElement();
      if (localBranchElement1 == null) {
        throw new IllegalStateException("invalid element structure");
      }
    }
    Element[] arrayOfElement1 = { paramElement };
    Element[] arrayOfElement2 = new Element[0];
    int i3 = localBranchElement1.getElementIndex(i);
    localBranchElement1.replace(i3, 1, arrayOfElement2);
    localDefaultDocumentEvent.addEdit(new AbstractDocument.ElementEdit(localBranchElement1, i3, arrayOfElement1, arrayOfElement2));
    if (i2 > 0)
    {
      try
      {
        localUndoableEdit = localContent.remove(j, i2);
        if (localUndoableEdit != null) {
          localDefaultDocumentEvent.addEdit(localUndoableEdit);
        }
      }
      catch (BadLocationException localBadLocationException2)
      {
        throw new IllegalStateException(localBadLocationException2);
      }
      n -= i2;
    }
    if (i1 != 0)
    {
      for (Element localElement1 = localBranchElement1.getElement(localBranchElement1.getElementCount() - 1); (localElement1 != null) && (!localElement1.isLeaf()); localElement1 = localElement1.getElement(localElement1.getElementCount() - 1)) {}
      if (localElement1 == null) {
        throw new IllegalStateException("invalid element structure");
      }
      int i4 = localElement1.getStartOffset();
      AbstractDocument.BranchElement localBranchElement2 = (AbstractDocument.BranchElement)localElement1.getParentElement();
      int i5 = localBranchElement2.getElementIndex(i4);
      Element localElement2 = createLeafElement(localBranchElement2, localElement1.getAttributes(), i4, n);
      Element[] arrayOfElement3 = { localElement1 };
      Element[] arrayOfElement4 = { localElement2 };
      localBranchElement2.replace(i5, 1, arrayOfElement4);
      localDefaultDocumentEvent.addEdit(new AbstractDocument.ElementEdit(localBranchElement2, i5, arrayOfElement3, arrayOfElement4));
    }
    postRemoveUpdate(localDefaultDocumentEvent);
    localDefaultDocumentEvent.end();
    fireRemoveUpdate(localDefaultDocumentEvent);
    if ((!bool) || (localUndoableEdit == null)) {
      fireUndoableEditUpdate(new UndoableEditEvent(this, localDefaultDocumentEvent));
    }
  }
  
  public Style addStyle(String paramString, Style paramStyle)
  {
    StyleContext localStyleContext = (StyleContext)getAttributeContext();
    return localStyleContext.addStyle(paramString, paramStyle);
  }
  
  public void removeStyle(String paramString)
  {
    StyleContext localStyleContext = (StyleContext)getAttributeContext();
    localStyleContext.removeStyle(paramString);
  }
  
  public Style getStyle(String paramString)
  {
    StyleContext localStyleContext = (StyleContext)getAttributeContext();
    return localStyleContext.getStyle(paramString);
  }
  
  public Enumeration<?> getStyleNames()
  {
    return ((StyleContext)getAttributeContext()).getStyleNames();
  }
  
  public void setLogicalStyle(int paramInt, Style paramStyle)
  {
    Element localElement = getParagraphElement(paramInt);
    if ((localElement != null) && ((localElement instanceof AbstractDocument.AbstractElement))) {
      try
      {
        writeLock();
        StyleChangeUndoableEdit localStyleChangeUndoableEdit = new StyleChangeUndoableEdit((AbstractDocument.AbstractElement)localElement, paramStyle);
        ((AbstractDocument.AbstractElement)localElement).setResolveParent(paramStyle);
        int i = localElement.getStartOffset();
        int j = localElement.getEndOffset();
        AbstractDocument.DefaultDocumentEvent localDefaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, i, j - i, DocumentEvent.EventType.CHANGE);
        localDefaultDocumentEvent.addEdit(localStyleChangeUndoableEdit);
        localDefaultDocumentEvent.end();
        fireChangedUpdate(localDefaultDocumentEvent);
        fireUndoableEditUpdate(new UndoableEditEvent(this, localDefaultDocumentEvent));
      }
      finally
      {
        writeUnlock();
      }
    }
  }
  
  public Style getLogicalStyle(int paramInt)
  {
    Style localStyle = null;
    Element localElement = getParagraphElement(paramInt);
    if (localElement != null)
    {
      AttributeSet localAttributeSet1 = localElement.getAttributes();
      AttributeSet localAttributeSet2 = localAttributeSet1.getResolveParent();
      if ((localAttributeSet2 instanceof Style)) {
        localStyle = (Style)localAttributeSet2;
      }
    }
    return localStyle;
  }
  
  public void setCharacterAttributes(int paramInt1, int paramInt2, AttributeSet paramAttributeSet, boolean paramBoolean)
  {
    if (paramInt2 == 0) {
      return;
    }
    try
    {
      writeLock();
      AbstractDocument.DefaultDocumentEvent localDefaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, paramInt1, paramInt2, DocumentEvent.EventType.CHANGE);
      buffer.change(paramInt1, paramInt2, localDefaultDocumentEvent);
      AttributeSet localAttributeSet = paramAttributeSet.copyAttributes();
      int i;
      for (int j = paramInt1; j < paramInt1 + paramInt2; j = i)
      {
        Element localElement = getCharacterElement(j);
        i = localElement.getEndOffset();
        if (j == i) {
          break;
        }
        MutableAttributeSet localMutableAttributeSet = (MutableAttributeSet)localElement.getAttributes();
        localDefaultDocumentEvent.addEdit(new AttributeUndoableEdit(localElement, localAttributeSet, paramBoolean));
        if (paramBoolean) {
          localMutableAttributeSet.removeAttributes(localMutableAttributeSet);
        }
        localMutableAttributeSet.addAttributes(paramAttributeSet);
      }
      localDefaultDocumentEvent.end();
      fireChangedUpdate(localDefaultDocumentEvent);
      fireUndoableEditUpdate(new UndoableEditEvent(this, localDefaultDocumentEvent));
    }
    finally
    {
      writeUnlock();
    }
  }
  
  public void setParagraphAttributes(int paramInt1, int paramInt2, AttributeSet paramAttributeSet, boolean paramBoolean)
  {
    try
    {
      writeLock();
      AbstractDocument.DefaultDocumentEvent localDefaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, paramInt1, paramInt2, DocumentEvent.EventType.CHANGE);
      AttributeSet localAttributeSet = paramAttributeSet.copyAttributes();
      Element localElement1 = getDefaultRootElement();
      int i = localElement1.getElementIndex(paramInt1);
      int j = localElement1.getElementIndex(paramInt1 + (paramInt2 > 0 ? paramInt2 - 1 : 0));
      boolean bool = Boolean.TRUE.equals(getProperty("i18n"));
      int k = 0;
      for (int m = i; m <= j; m++)
      {
        Element localElement2 = localElement1.getElement(m);
        MutableAttributeSet localMutableAttributeSet = (MutableAttributeSet)localElement2.getAttributes();
        localDefaultDocumentEvent.addEdit(new AttributeUndoableEdit(localElement2, localAttributeSet, paramBoolean));
        if (paramBoolean) {
          localMutableAttributeSet.removeAttributes(localMutableAttributeSet);
        }
        localMutableAttributeSet.addAttributes(paramAttributeSet);
        if ((bool) && (k == 0)) {
          k = localMutableAttributeSet.getAttribute(TextAttribute.RUN_DIRECTION) != null ? 1 : 0;
        }
      }
      if (k != 0) {
        updateBidi(localDefaultDocumentEvent);
      }
      localDefaultDocumentEvent.end();
      fireChangedUpdate(localDefaultDocumentEvent);
      fireUndoableEditUpdate(new UndoableEditEvent(this, localDefaultDocumentEvent));
    }
    finally
    {
      writeUnlock();
    }
  }
  
  public Element getParagraphElement(int paramInt)
  {
    int i;
    for (Element localElement = getDefaultRootElement(); !localElement.isLeaf(); localElement = localElement.getElement(i)) {
      i = localElement.getElementIndex(paramInt);
    }
    if (localElement != null) {
      return localElement.getParentElement();
    }
    return localElement;
  }
  
  public Element getCharacterElement(int paramInt)
  {
    int i;
    for (Element localElement = getDefaultRootElement(); !localElement.isLeaf(); localElement = localElement.getElement(i)) {
      i = localElement.getElementIndex(paramInt);
    }
    return localElement;
  }
  
  protected void insertUpdate(AbstractDocument.DefaultDocumentEvent paramDefaultDocumentEvent, AttributeSet paramAttributeSet)
  {
    int i = paramDefaultDocumentEvent.getOffset();
    int j = paramDefaultDocumentEvent.getLength();
    if (paramAttributeSet == null) {
      paramAttributeSet = SimpleAttributeSet.EMPTY;
    }
    Element localElement1 = getParagraphElement(i + j);
    AttributeSet localAttributeSet1 = localElement1.getAttributes();
    Element localElement2 = getParagraphElement(i);
    Element localElement3 = localElement2.getElement(localElement2.getElementIndex(i));
    int k = i + j;
    int m = localElement3.getEndOffset() == k ? 1 : 0;
    AttributeSet localAttributeSet2 = localElement3.getAttributes();
    try
    {
      Segment localSegment = new Segment();
      Vector localVector = new Vector();
      Object localObject1 = null;
      int n = 0;
      short s = 6;
      if (i > 0)
      {
        getText(i - 1, 1, localSegment);
        if (array[offset] == '\n')
        {
          n = 1;
          s = createSpecsForInsertAfterNewline(localElement1, localElement2, localAttributeSet1, localVector, i, k);
          for (int i1 = localVector.size() - 1; i1 >= 0; i1--)
          {
            ElementSpec localElementSpec1 = (ElementSpec)localVector.elementAt(i1);
            if (localElementSpec1.getType() == 1)
            {
              localObject1 = localElementSpec1;
              break;
            }
          }
        }
      }
      if (n == 0) {
        localAttributeSet1 = localElement2.getAttributes();
      }
      getText(i, j, localSegment);
      char[] arrayOfChar = array;
      int i2 = offset + count;
      int i3 = offset;
      for (int i4 = offset; i4 < i2; i4++) {
        if (arrayOfChar[i4] == '\n')
        {
          i5 = i4 + 1;
          localVector.addElement(new ElementSpec(paramAttributeSet, (short)3, i5 - i3));
          localVector.addElement(new ElementSpec(null, (short)2));
          localObject1 = new ElementSpec(localAttributeSet1, (short)1);
          localVector.addElement(localObject1);
          i3 = i5;
        }
      }
      if (i3 < i2) {
        localVector.addElement(new ElementSpec(paramAttributeSet, (short)3, i2 - i3));
      }
      ElementSpec localElementSpec2 = (ElementSpec)localVector.firstElement();
      int i5 = getLength();
      if ((localElementSpec2.getType() == 3) && (localAttributeSet2.isEqual(paramAttributeSet))) {
        localElementSpec2.setDirection((short)4);
      }
      if (localObject1 != null) {
        if (n != 0)
        {
          ((ElementSpec)localObject1).setDirection(s);
        }
        else if (localElement2.getEndOffset() != k)
        {
          ((ElementSpec)localObject1).setDirection((short)7);
        }
        else
        {
          localObject2 = localElement2.getParentElement();
          int i6 = ((Element)localObject2).getElementIndex(i);
          if ((i6 + 1 < ((Element)localObject2).getElementCount()) && (!((Element)localObject2).getElement(i6 + 1).isLeaf())) {
            ((ElementSpec)localObject1).setDirection((short)5);
          }
        }
      }
      if ((m != 0) && (k < i5))
      {
        localObject2 = (ElementSpec)localVector.lastElement();
        if ((((ElementSpec)localObject2).getType() == 3) && (((ElementSpec)localObject2).getDirection() != 4) && (((localObject1 == null) && ((localElement1 == localElement2) || (n != 0))) || ((localObject1 != null) && (((ElementSpec)localObject1).getDirection() != 6))))
        {
          Element localElement4 = localElement1.getElement(localElement1.getElementIndex(k));
          if ((localElement4.isLeaf()) && (paramAttributeSet.isEqual(localElement4.getAttributes()))) {
            ((ElementSpec)localObject2).setDirection((short)5);
          }
        }
      }
      else if ((m == 0) && (localObject1 != null) && (((ElementSpec)localObject1).getDirection() == 7))
      {
        localObject2 = (ElementSpec)localVector.lastElement();
        if ((((ElementSpec)localObject2).getType() == 3) && (((ElementSpec)localObject2).getDirection() != 4) && (paramAttributeSet.isEqual(localAttributeSet2))) {
          ((ElementSpec)localObject2).setDirection((short)5);
        }
      }
      if (Utilities.isComposedTextAttributeDefined(paramAttributeSet))
      {
        localObject2 = (MutableAttributeSet)paramAttributeSet;
        ((MutableAttributeSet)localObject2).addAttributes(localAttributeSet2);
        ((MutableAttributeSet)localObject2).addAttribute("$ename", "content");
        ((MutableAttributeSet)localObject2).addAttribute(StyleConstants.NameAttribute, "content");
        if (((MutableAttributeSet)localObject2).isDefined("CR")) {
          ((MutableAttributeSet)localObject2).removeAttribute("CR");
        }
      }
      Object localObject2 = new ElementSpec[localVector.size()];
      localVector.copyInto((Object[])localObject2);
      buffer.insert(i, j, (ElementSpec[])localObject2, paramDefaultDocumentEvent);
    }
    catch (BadLocationException localBadLocationException) {}
    super.insertUpdate(paramDefaultDocumentEvent, paramAttributeSet);
  }
  
  short createSpecsForInsertAfterNewline(Element paramElement1, Element paramElement2, AttributeSet paramAttributeSet, Vector<ElementSpec> paramVector, int paramInt1, int paramInt2)
  {
    Object localObject1;
    Object localObject2;
    if (paramElement1.getParentElement() == paramElement2.getParentElement())
    {
      localObject1 = new ElementSpec(paramAttributeSet, (short)2);
      paramVector.addElement(localObject1);
      localObject1 = new ElementSpec(paramAttributeSet, (short)1);
      paramVector.addElement(localObject1);
      if (paramElement2.getEndOffset() != paramInt2) {
        return 7;
      }
      localObject2 = paramElement2.getParentElement();
      if (((Element)localObject2).getElementIndex(paramInt1) + 1 < ((Element)localObject2).getElementCount()) {
        return 5;
      }
    }
    else
    {
      localObject1 = new Vector();
      localObject2 = new Vector();
      for (Element localElement = paramElement2; localElement != null; localElement = localElement.getParentElement()) {
        ((Vector)localObject1).addElement(localElement);
      }
      localElement = paramElement1;
      int i = -1;
      while ((localElement != null) && ((i = ((Vector)localObject1).indexOf(localElement)) == -1))
      {
        ((Vector)localObject2).addElement(localElement);
        localElement = localElement.getParentElement();
      }
      if (localElement != null)
      {
        for (int j = 0; j < i; j++) {
          paramVector.addElement(new ElementSpec(null, (short)2));
        }
        for (int k = ((Vector)localObject2).size() - 1; k >= 0; k--)
        {
          ElementSpec localElementSpec = new ElementSpec(((Element)((Vector)localObject2).elementAt(k)).getAttributes(), (short)1);
          if (k > 0) {
            localElementSpec.setDirection((short)5);
          }
          paramVector.addElement(localElementSpec);
        }
        if (((Vector)localObject2).size() > 0) {
          return 5;
        }
        return 7;
      }
    }
    return 6;
  }
  
  protected void removeUpdate(AbstractDocument.DefaultDocumentEvent paramDefaultDocumentEvent)
  {
    super.removeUpdate(paramDefaultDocumentEvent);
    buffer.remove(paramDefaultDocumentEvent.getOffset(), paramDefaultDocumentEvent.getLength(), paramDefaultDocumentEvent);
  }
  
  protected AbstractDocument.AbstractElement createDefaultRoot()
  {
    writeLock();
    SectionElement localSectionElement = new SectionElement();
    AbstractDocument.BranchElement localBranchElement = new AbstractDocument.BranchElement(this, localSectionElement, null);
    AbstractDocument.LeafElement localLeafElement = new AbstractDocument.LeafElement(this, localBranchElement, null, 0, 1);
    Element[] arrayOfElement = new Element[1];
    arrayOfElement[0] = localLeafElement;
    localBranchElement.replace(0, 0, arrayOfElement);
    arrayOfElement[0] = localBranchElement;
    localSectionElement.replace(0, 0, arrayOfElement);
    writeUnlock();
    return localSectionElement;
  }
  
  public Color getForeground(AttributeSet paramAttributeSet)
  {
    StyleContext localStyleContext = (StyleContext)getAttributeContext();
    return localStyleContext.getForeground(paramAttributeSet);
  }
  
  public Color getBackground(AttributeSet paramAttributeSet)
  {
    StyleContext localStyleContext = (StyleContext)getAttributeContext();
    return localStyleContext.getBackground(paramAttributeSet);
  }
  
  public Font getFont(AttributeSet paramAttributeSet)
  {
    StyleContext localStyleContext = (StyleContext)getAttributeContext();
    return localStyleContext.getFont(paramAttributeSet);
  }
  
  protected void styleChanged(Style paramStyle)
  {
    if (getLength() != 0)
    {
      if (updateRunnable == null) {
        updateRunnable = new ChangeUpdateRunnable();
      }
      synchronized (updateRunnable)
      {
        if (!updateRunnable.isPending)
        {
          SwingUtilities.invokeLater(updateRunnable);
          updateRunnable.isPending = true;
        }
      }
    }
  }
  
  public void addDocumentListener(DocumentListener paramDocumentListener)
  {
    synchronized (listeningStyles)
    {
      int i = listenerList.getListenerCount(DocumentListener.class);
      super.addDocumentListener(paramDocumentListener);
      if (i == 0)
      {
        if (styleContextChangeListener == null) {
          styleContextChangeListener = createStyleContextChangeListener();
        }
        if (styleContextChangeListener != null)
        {
          StyleContext localStyleContext = (StyleContext)getAttributeContext();
          List localList = AbstractChangeHandler.getStaleListeners(styleContextChangeListener);
          Iterator localIterator = localList.iterator();
          while (localIterator.hasNext())
          {
            ChangeListener localChangeListener = (ChangeListener)localIterator.next();
            localStyleContext.removeChangeListener(localChangeListener);
          }
          localStyleContext.addChangeListener(styleContextChangeListener);
        }
        updateStylesListeningTo();
      }
    }
  }
  
  public void removeDocumentListener(DocumentListener paramDocumentListener)
  {
    synchronized (listeningStyles)
    {
      super.removeDocumentListener(paramDocumentListener);
      if (listenerList.getListenerCount(DocumentListener.class) == 0)
      {
        for (int i = listeningStyles.size() - 1; i >= 0; i--) {
          ((Style)listeningStyles.elementAt(i)).removeChangeListener(styleChangeListener);
        }
        listeningStyles.removeAllElements();
        if (styleContextChangeListener != null)
        {
          StyleContext localStyleContext = (StyleContext)getAttributeContext();
          localStyleContext.removeChangeListener(styleContextChangeListener);
        }
      }
    }
  }
  
  ChangeListener createStyleChangeListener()
  {
    return new StyleChangeHandler(this);
  }
  
  ChangeListener createStyleContextChangeListener()
  {
    return new StyleContextChangeHandler(this);
  }
  
  void updateStylesListeningTo()
  {
    synchronized (listeningStyles)
    {
      StyleContext localStyleContext = (StyleContext)getAttributeContext();
      if (styleChangeListener == null) {
        styleChangeListener = createStyleChangeListener();
      }
      if ((styleChangeListener != null) && (localStyleContext != null))
      {
        Enumeration localEnumeration = localStyleContext.getStyleNames();
        Vector localVector = (Vector)listeningStyles.clone();
        listeningStyles.removeAllElements();
        List localList = AbstractChangeHandler.getStaleListeners(styleChangeListener);
        Style localStyle;
        while (localEnumeration.hasMoreElements())
        {
          String str = (String)localEnumeration.nextElement();
          localStyle = localStyleContext.getStyle(str);
          int j = localVector.indexOf(localStyle);
          listeningStyles.addElement(localStyle);
          if (j == -1)
          {
            Iterator localIterator = localList.iterator();
            while (localIterator.hasNext())
            {
              ChangeListener localChangeListener = (ChangeListener)localIterator.next();
              localStyle.removeChangeListener(localChangeListener);
            }
            localStyle.addChangeListener(styleChangeListener);
          }
          else
          {
            localVector.removeElementAt(j);
          }
        }
        for (int i = localVector.size() - 1; i >= 0; i--)
        {
          localStyle = (Style)localVector.elementAt(i);
          localStyle.removeChangeListener(styleChangeListener);
        }
        if (listeningStyles.size() == 0) {
          styleChangeListener = null;
        }
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    listeningStyles = new Vector();
    paramObjectInputStream.defaultReadObject();
    if ((styleContextChangeListener == null) && (listenerList.getListenerCount(DocumentListener.class) > 0))
    {
      styleContextChangeListener = createStyleContextChangeListener();
      if (styleContextChangeListener != null)
      {
        StyleContext localStyleContext = (StyleContext)getAttributeContext();
        localStyleContext.addChangeListener(styleContextChangeListener);
      }
      updateStylesListeningTo();
    }
  }
  
  static abstract class AbstractChangeHandler
    implements ChangeListener
  {
    private static final Map<Class, ReferenceQueue<DefaultStyledDocument>> queueMap = new HashMap();
    private DocReference doc;
    
    AbstractChangeHandler(DefaultStyledDocument paramDefaultStyledDocument)
    {
      Class localClass = getClass();
      ReferenceQueue localReferenceQueue;
      synchronized (queueMap)
      {
        localReferenceQueue = (ReferenceQueue)queueMap.get(localClass);
        if (localReferenceQueue == null)
        {
          localReferenceQueue = new ReferenceQueue();
          queueMap.put(localClass, localReferenceQueue);
        }
      }
      doc = new DocReference(paramDefaultStyledDocument, localReferenceQueue);
    }
    
    static List<ChangeListener> getStaleListeners(ChangeListener paramChangeListener)
    {
      ArrayList localArrayList = new ArrayList();
      ReferenceQueue localReferenceQueue = (ReferenceQueue)queueMap.get(paramChangeListener.getClass());
      if (localReferenceQueue != null) {
        synchronized (localReferenceQueue)
        {
          DocReference localDocReference;
          while ((localDocReference = (DocReference)localReferenceQueue.poll()) != null) {
            localArrayList.add(localDocReference.getListener());
          }
        }
      }
      return localArrayList;
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      DefaultStyledDocument localDefaultStyledDocument = (DefaultStyledDocument)doc.get();
      if (localDefaultStyledDocument != null) {
        fireStateChanged(localDefaultStyledDocument, paramChangeEvent);
      }
    }
    
    abstract void fireStateChanged(DefaultStyledDocument paramDefaultStyledDocument, ChangeEvent paramChangeEvent);
    
    private class DocReference
      extends WeakReference<DefaultStyledDocument>
    {
      DocReference(ReferenceQueue<DefaultStyledDocument> paramReferenceQueue)
      {
        super(localReferenceQueue);
      }
      
      ChangeListener getListener()
      {
        return DefaultStyledDocument.AbstractChangeHandler.this;
      }
    }
  }
  
  public static class AttributeUndoableEdit
    extends AbstractUndoableEdit
  {
    protected AttributeSet newAttributes;
    protected AttributeSet copy;
    protected boolean isReplacing;
    protected Element element;
    
    public AttributeUndoableEdit(Element paramElement, AttributeSet paramAttributeSet, boolean paramBoolean)
    {
      element = paramElement;
      newAttributes = paramAttributeSet;
      isReplacing = paramBoolean;
      copy = paramElement.getAttributes().copyAttributes();
    }
    
    public void redo()
      throws CannotRedoException
    {
      super.redo();
      MutableAttributeSet localMutableAttributeSet = (MutableAttributeSet)element.getAttributes();
      if (isReplacing) {
        localMutableAttributeSet.removeAttributes(localMutableAttributeSet);
      }
      localMutableAttributeSet.addAttributes(newAttributes);
    }
    
    public void undo()
      throws CannotUndoException
    {
      super.undo();
      MutableAttributeSet localMutableAttributeSet = (MutableAttributeSet)element.getAttributes();
      localMutableAttributeSet.removeAttributes(localMutableAttributeSet);
      localMutableAttributeSet.addAttributes(copy);
    }
  }
  
  class ChangeUpdateRunnable
    implements Runnable
  {
    boolean isPending = false;
    
    ChangeUpdateRunnable() {}
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: dup
      //   2: astore_1
      //   3: monitorenter
      //   4: aload_0
      //   5: iconst_0
      //   6: putfield 51	javax/swing/text/DefaultStyledDocument$ChangeUpdateRunnable:isPending	Z
      //   9: aload_1
      //   10: monitorexit
      //   11: goto +8 -> 19
      //   14: astore_2
      //   15: aload_1
      //   16: monitorexit
      //   17: aload_2
      //   18: athrow
      //   19: aload_0
      //   20: getfield 52	javax/swing/text/DefaultStyledDocument$ChangeUpdateRunnable:this$0	Ljavax/swing/text/DefaultStyledDocument;
      //   23: invokevirtual 57	javax/swing/text/DefaultStyledDocument:writeLock	()V
      //   26: new 32	javax/swing/text/AbstractDocument$DefaultDocumentEvent
      //   29: dup
      //   30: aload_0
      //   31: getfield 52	javax/swing/text/DefaultStyledDocument$ChangeUpdateRunnable:this$0	Ljavax/swing/text/DefaultStyledDocument;
      //   34: iconst_0
      //   35: aload_0
      //   36: getfield 52	javax/swing/text/DefaultStyledDocument$ChangeUpdateRunnable:this$0	Ljavax/swing/text/DefaultStyledDocument;
      //   39: invokevirtual 56	javax/swing/text/DefaultStyledDocument:getLength	()I
      //   42: getstatic 50	javax/swing/event/DocumentEvent$EventType:CHANGE	Ljavax/swing/event/DocumentEvent$EventType;
      //   45: invokespecial 55	javax/swing/text/AbstractDocument$DefaultDocumentEvent:<init>	(Ljavax/swing/text/AbstractDocument;IILjavax/swing/event/DocumentEvent$EventType;)V
      //   48: astore_1
      //   49: aload_1
      //   50: invokevirtual 54	javax/swing/text/AbstractDocument$DefaultDocumentEvent:end	()V
      //   53: aload_0
      //   54: getfield 52	javax/swing/text/DefaultStyledDocument$ChangeUpdateRunnable:this$0	Ljavax/swing/text/DefaultStyledDocument;
      //   57: aload_1
      //   58: invokevirtual 59	javax/swing/text/DefaultStyledDocument:fireChangedUpdate	(Ljavax/swing/event/DocumentEvent;)V
      //   61: aload_0
      //   62: getfield 52	javax/swing/text/DefaultStyledDocument$ChangeUpdateRunnable:this$0	Ljavax/swing/text/DefaultStyledDocument;
      //   65: invokevirtual 58	javax/swing/text/DefaultStyledDocument:writeUnlock	()V
      //   68: goto +13 -> 81
      //   71: astore_3
      //   72: aload_0
      //   73: getfield 52	javax/swing/text/DefaultStyledDocument$ChangeUpdateRunnable:this$0	Ljavax/swing/text/DefaultStyledDocument;
      //   76: invokevirtual 58	javax/swing/text/DefaultStyledDocument:writeUnlock	()V
      //   79: aload_3
      //   80: athrow
      //   81: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	82	0	this	ChangeUpdateRunnable
      //   2	56	1	Ljava/lang/Object;	Object
      //   14	4	2	localObject1	Object
      //   71	9	3	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   4	11	14	finally
      //   14	17	14	finally
      //   19	61	71	finally
    }
  }
  
  public class ElementBuffer
    implements Serializable
  {
    Element root;
    transient int pos;
    transient int offset;
    transient int length;
    transient int endOffset;
    transient Vector<ElemChanges> changes;
    transient Stack<ElemChanges> path;
    transient boolean insertOp;
    transient boolean recreateLeafs;
    transient ElemChanges[] insertPath;
    transient boolean createdFracture;
    transient Element fracturedParent;
    transient Element fracturedChild;
    transient boolean offsetLastIndex;
    transient boolean offsetLastIndexOnReplace;
    
    public ElementBuffer(Element paramElement)
    {
      root = paramElement;
      changes = new Vector();
      path = new Stack();
    }
    
    public Element getRootElement()
    {
      return root;
    }
    
    public void insert(int paramInt1, int paramInt2, DefaultStyledDocument.ElementSpec[] paramArrayOfElementSpec, AbstractDocument.DefaultDocumentEvent paramDefaultDocumentEvent)
    {
      if (paramInt2 == 0) {
        return;
      }
      insertOp = true;
      beginEdits(paramInt1, paramInt2);
      insertUpdate(paramArrayOfElementSpec);
      endEdits(paramDefaultDocumentEvent);
      insertOp = false;
    }
    
    void create(int paramInt, DefaultStyledDocument.ElementSpec[] paramArrayOfElementSpec, AbstractDocument.DefaultDocumentEvent paramDefaultDocumentEvent)
    {
      insertOp = true;
      beginEdits(offset, paramInt);
      Object localObject1 = root;
      for (int i = ((Element)localObject1).getElementIndex(0); !((Element)localObject1).isLeaf(); i = ((Element)localObject1).getElementIndex(0))
      {
        localObject2 = ((Element)localObject1).getElement(i);
        push((Element)localObject1, i);
        localObject1 = localObject2;
      }
      Object localObject2 = (ElemChanges)path.peek();
      Element localElement = parent.getElement(index);
      added.addElement(createLeafElement(parent, localElement.getAttributes(), getLength(), localElement.getEndOffset()));
      removed.addElement(localElement);
      while (path.size() > 1) {
        pop();
      }
      int j = paramArrayOfElementSpec.length;
      AttributeSet localAttributeSet = null;
      if ((j > 0) && (paramArrayOfElementSpec[0].getType() == 1)) {
        localAttributeSet = paramArrayOfElementSpec[0].getAttributes();
      }
      if (localAttributeSet == null) {
        localAttributeSet = SimpleAttributeSet.EMPTY;
      }
      MutableAttributeSet localMutableAttributeSet = (MutableAttributeSet)root.getAttributes();
      paramDefaultDocumentEvent.addEdit(new DefaultStyledDocument.AttributeUndoableEdit(root, localAttributeSet, true));
      localMutableAttributeSet.removeAttributes(localMutableAttributeSet);
      localMutableAttributeSet.addAttributes(localAttributeSet);
      for (int k = 1; k < j; k++) {
        insertElement(paramArrayOfElementSpec[k]);
      }
      while (path.size() != 0) {
        pop();
      }
      endEdits(paramDefaultDocumentEvent);
      insertOp = false;
    }
    
    public void remove(int paramInt1, int paramInt2, AbstractDocument.DefaultDocumentEvent paramDefaultDocumentEvent)
    {
      beginEdits(paramInt1, paramInt2);
      removeUpdate();
      endEdits(paramDefaultDocumentEvent);
    }
    
    public void change(int paramInt1, int paramInt2, AbstractDocument.DefaultDocumentEvent paramDefaultDocumentEvent)
    {
      beginEdits(paramInt1, paramInt2);
      changeUpdate();
      endEdits(paramDefaultDocumentEvent);
    }
    
    protected void insertUpdate(DefaultStyledDocument.ElementSpec[] paramArrayOfElementSpec)
    {
      Object localObject = root;
      for (int i = ((Element)localObject).getElementIndex(offset); !((Element)localObject).isLeaf(); i = ((Element)localObject).getElementIndex(offset))
      {
        Element localElement = ((Element)localObject).getElement(i);
        push((Element)localObject, localElement.isLeaf() ? i : i + 1);
        localObject = localElement;
      }
      insertPath = new ElemChanges[path.size()];
      path.copyInto(insertPath);
      createdFracture = false;
      recreateLeafs = false;
      int j;
      if (paramArrayOfElementSpec[0].getType() == 3)
      {
        insertFirstContent(paramArrayOfElementSpec);
        pos += paramArrayOfElementSpec[0].getLength();
        j = 1;
      }
      else
      {
        fractureDeepestLeaf(paramArrayOfElementSpec);
        j = 0;
      }
      int k = paramArrayOfElementSpec.length;
      while (j < k)
      {
        insertElement(paramArrayOfElementSpec[j]);
        j++;
      }
      if (!createdFracture) {
        fracture(-1);
      }
      while (path.size() != 0) {
        pop();
      }
      if ((offsetLastIndex) && (offsetLastIndexOnReplace)) {
        insertPath[(insertPath.length - 1)].index += 1;
      }
      ElemChanges localElemChanges;
      for (int m = insertPath.length - 1; m >= 0; m--)
      {
        localElemChanges = insertPath[m];
        if (parent == fracturedParent) {
          added.addElement(fracturedChild);
        }
        if (((added.size() > 0) || (removed.size() > 0)) && (!changes.contains(localElemChanges))) {
          changes.addElement(localElemChanges);
        }
      }
      if ((offset == 0) && (fracturedParent != null) && (paramArrayOfElementSpec[0].getType() == 2))
      {
        for (m = 0; (m < paramArrayOfElementSpec.length) && (paramArrayOfElementSpec[m].getType() == 2); m++) {}
        localElemChanges = insertPath[(insertPath.length - m - 1)];
        removed.insertElementAt(parent.getElement(--index), 0);
      }
    }
    
    protected void removeUpdate()
    {
      removeElements(root, offset, offset + length);
    }
    
    protected void changeUpdate()
    {
      boolean bool = split(offset, length);
      if (!bool)
      {
        while (path.size() != 0) {
          pop();
        }
        split(offset + length, 0);
      }
      while (path.size() != 0) {
        pop();
      }
    }
    
    boolean split(int paramInt1, int paramInt2)
    {
      boolean bool = false;
      Element localElement1 = root;
      for (int i = localElement1.getElementIndex(paramInt1); !localElement1.isLeaf(); i = localElement1.getElementIndex(paramInt1))
      {
        push(localElement1, i);
        localElement1 = localElement1.getElement(i);
      }
      ElemChanges localElemChanges = (ElemChanges)path.peek();
      Element localElement2 = parent.getElement(index);
      if ((localElement2.getStartOffset() < paramInt1) && (paramInt1 < localElement2.getEndOffset()))
      {
        int j = index;
        int k = j;
        if ((paramInt1 + paramInt2 < parent.getEndOffset()) && (paramInt2 != 0))
        {
          k = parent.getElementIndex(paramInt1 + paramInt2);
          if (k == j)
          {
            removed.addElement(localElement2);
            localElement1 = createLeafElement(parent, localElement2.getAttributes(), localElement2.getStartOffset(), paramInt1);
            added.addElement(localElement1);
            localElement1 = createLeafElement(parent, localElement2.getAttributes(), paramInt1, paramInt1 + paramInt2);
            added.addElement(localElement1);
            localElement1 = createLeafElement(parent, localElement2.getAttributes(), paramInt1 + paramInt2, localElement2.getEndOffset());
            added.addElement(localElement1);
            return true;
          }
          localElement2 = parent.getElement(k);
          if (paramInt1 + paramInt2 == localElement2.getStartOffset()) {
            k = j;
          }
          bool = true;
        }
        pos = paramInt1;
        localElement2 = parent.getElement(j);
        removed.addElement(localElement2);
        localElement1 = createLeafElement(parent, localElement2.getAttributes(), localElement2.getStartOffset(), pos);
        added.addElement(localElement1);
        localElement1 = createLeafElement(parent, localElement2.getAttributes(), pos, localElement2.getEndOffset());
        added.addElement(localElement1);
        for (int m = j + 1; m < k; m++)
        {
          localElement2 = parent.getElement(m);
          removed.addElement(localElement2);
          added.addElement(localElement2);
        }
        if (k != j)
        {
          localElement2 = parent.getElement(k);
          pos = (paramInt1 + paramInt2);
          removed.addElement(localElement2);
          localElement1 = createLeafElement(parent, localElement2.getAttributes(), localElement2.getStartOffset(), pos);
          added.addElement(localElement1);
          localElement1 = createLeafElement(parent, localElement2.getAttributes(), pos, localElement2.getEndOffset());
          added.addElement(localElement1);
        }
      }
      return bool;
    }
    
    void endEdits(AbstractDocument.DefaultDocumentEvent paramDefaultDocumentEvent)
    {
      int i = changes.size();
      for (int j = 0; j < i; j++)
      {
        ElemChanges localElemChanges = (ElemChanges)changes.elementAt(j);
        Element[] arrayOfElement1 = new Element[removed.size()];
        removed.copyInto(arrayOfElement1);
        Element[] arrayOfElement2 = new Element[added.size()];
        added.copyInto(arrayOfElement2);
        int k = index;
        ((AbstractDocument.BranchElement)parent).replace(k, arrayOfElement1.length, arrayOfElement2);
        AbstractDocument.ElementEdit localElementEdit = new AbstractDocument.ElementEdit(parent, k, arrayOfElement1, arrayOfElement2);
        paramDefaultDocumentEvent.addEdit(localElementEdit);
      }
      changes.removeAllElements();
      path.removeAllElements();
    }
    
    void beginEdits(int paramInt1, int paramInt2)
    {
      offset = paramInt1;
      length = paramInt2;
      endOffset = (paramInt1 + paramInt2);
      pos = paramInt1;
      if (changes == null) {
        changes = new Vector();
      } else {
        changes.removeAllElements();
      }
      if (path == null) {
        path = new Stack();
      } else {
        path.removeAllElements();
      }
      fracturedParent = null;
      fracturedChild = null;
      offsetLastIndex = (offsetLastIndexOnReplace = 0);
    }
    
    void push(Element paramElement, int paramInt, boolean paramBoolean)
    {
      ElemChanges localElemChanges = new ElemChanges(paramElement, paramInt, paramBoolean);
      path.push(localElemChanges);
    }
    
    void push(Element paramElement, int paramInt)
    {
      push(paramElement, paramInt, false);
    }
    
    void pop()
    {
      ElemChanges localElemChanges = (ElemChanges)path.peek();
      path.pop();
      if ((added.size() > 0) || (removed.size() > 0))
      {
        changes.addElement(localElemChanges);
      }
      else if (!path.isEmpty())
      {
        Element localElement = parent;
        if (localElement.getElementCount() == 0)
        {
          localElemChanges = (ElemChanges)path.peek();
          added.removeElement(localElement);
        }
      }
    }
    
    void advance(int paramInt)
    {
      pos += paramInt;
    }
    
    void insertElement(DefaultStyledDocument.ElementSpec paramElementSpec)
    {
      ElemChanges localElemChanges = (ElemChanges)path.peek();
      Element localElement2;
      switch (paramElementSpec.getType())
      {
      case 1: 
        switch (paramElementSpec.getDirection())
        {
        case 5: 
          Element localElement1 = parent.getElement(index);
          if (localElement1.isLeaf()) {
            if (index + 1 < parent.getElementCount()) {
              localElement1 = parent.getElement(index + 1);
            } else {
              throw new StateInvariantError("Join next to leaf");
            }
          }
          push(localElement1, 0, true);
          break;
        case 7: 
          if (!createdFracture) {
            fracture(path.size() - 1);
          }
          if (!isFracture) {
            push(fracturedChild, 0, true);
          } else {
            push(parent.getElement(0), 0, true);
          }
          break;
        default: 
          localElement2 = createBranchElement(parent, paramElementSpec.getAttributes());
          added.addElement(localElement2);
          push(localElement2, 0);
        }
        break;
      case 2: 
        pop();
        break;
      case 3: 
        int i = paramElementSpec.getLength();
        if (paramElementSpec.getDirection() != 5)
        {
          localElement2 = createLeafElement(parent, paramElementSpec.getAttributes(), pos, pos + i);
          added.addElement(localElement2);
        }
        else
        {
          Element localElement3;
          if (!isFracture)
          {
            localElement2 = null;
            if (insertPath != null) {
              for (int j = insertPath.length - 1; j >= 0; j--) {
                if (insertPath[j] == localElemChanges)
                {
                  if (j == insertPath.length - 1) {
                    break;
                  }
                  localElement2 = parent.getElement(index);
                  break;
                }
              }
            }
            if (localElement2 == null) {
              localElement2 = parent.getElement(index + 1);
            }
            localElement3 = createLeafElement(parent, localElement2.getAttributes(), pos, localElement2.getEndOffset());
            added.addElement(localElement3);
            removed.addElement(localElement2);
          }
          else
          {
            localElement2 = parent.getElement(0);
            localElement3 = createLeafElement(parent, localElement2.getAttributes(), pos, localElement2.getEndOffset());
            added.addElement(localElement3);
            removed.addElement(localElement2);
          }
        }
        pos += i;
      }
    }
    
    boolean removeElements(Element paramElement, int paramInt1, int paramInt2)
    {
      if (!paramElement.isLeaf())
      {
        int i = paramElement.getElementIndex(paramInt1);
        int j = paramElement.getElementIndex(paramInt2);
        push(paramElement, i);
        ElemChanges localElemChanges = (ElemChanges)path.peek();
        Element localElement1;
        if (i == j)
        {
          localElement1 = paramElement.getElement(i);
          if ((paramInt1 <= localElement1.getStartOffset()) && (paramInt2 >= localElement1.getEndOffset())) {
            removed.addElement(localElement1);
          } else if (removeElements(localElement1, paramInt1, paramInt2)) {
            removed.addElement(localElement1);
          }
        }
        else
        {
          localElement1 = paramElement.getElement(i);
          Element localElement2 = paramElement.getElement(j);
          int k = paramInt2 < paramElement.getEndOffset() ? 1 : 0;
          if ((k != 0) && (canJoin(localElement1, localElement2)))
          {
            for (int m = i; m <= j; m++) {
              removed.addElement(paramElement.getElement(m));
            }
            Element localElement3 = join(paramElement, localElement1, localElement2, paramInt1, paramInt2);
            added.addElement(localElement3);
          }
          else
          {
            int n = i + 1;
            int i1 = j - 1;
            if ((localElement1.getStartOffset() == paramInt1) || ((i == 0) && (localElement1.getStartOffset() > paramInt1) && (localElement1.getEndOffset() <= paramInt2)))
            {
              localElement1 = null;
              n = i;
            }
            if (k == 0)
            {
              localElement2 = null;
              i1++;
            }
            else if (localElement2.getStartOffset() == paramInt2)
            {
              localElement2 = null;
            }
            if (n <= i1) {
              index = n;
            }
            for (int i2 = n; i2 <= i1; i2++) {
              removed.addElement(paramElement.getElement(i2));
            }
            if ((localElement1 != null) && (removeElements(localElement1, paramInt1, paramInt2)))
            {
              removed.insertElementAt(localElement1, 0);
              index = i;
            }
            if ((localElement2 != null) && (removeElements(localElement2, paramInt1, paramInt2))) {
              removed.addElement(localElement2);
            }
          }
        }
        pop();
        if (paramElement.getElementCount() == removed.size() - added.size()) {
          return true;
        }
      }
      return false;
    }
    
    boolean canJoin(Element paramElement1, Element paramElement2)
    {
      if ((paramElement1 == null) || (paramElement2 == null)) {
        return false;
      }
      boolean bool1 = paramElement1.isLeaf();
      boolean bool2 = paramElement2.isLeaf();
      if (bool1 != bool2) {
        return false;
      }
      if (bool1) {
        return paramElement1.getAttributes().isEqual(paramElement2.getAttributes());
      }
      String str1 = paramElement1.getName();
      String str2 = paramElement2.getName();
      if (str1 != null) {
        return str1.equals(str2);
      }
      if (str2 != null) {
        return str2.equals(str1);
      }
      return true;
    }
    
    Element join(Element paramElement1, Element paramElement2, Element paramElement3, int paramInt1, int paramInt2)
    {
      if ((paramElement2.isLeaf()) && (paramElement3.isLeaf())) {
        return createLeafElement(paramElement1, paramElement2.getAttributes(), paramElement2.getStartOffset(), paramElement3.getEndOffset());
      }
      if ((!paramElement2.isLeaf()) && (!paramElement3.isLeaf()))
      {
        Element localElement1 = createBranchElement(paramElement1, paramElement2.getAttributes());
        int i = paramElement2.getElementIndex(paramInt1);
        int j = paramElement3.getElementIndex(paramInt2);
        Element localElement2 = paramElement2.getElement(i);
        if (localElement2.getStartOffset() >= paramInt1) {
          localElement2 = null;
        }
        Element localElement3 = paramElement3.getElement(j);
        if (localElement3.getStartOffset() == paramInt2) {
          localElement3 = null;
        }
        Vector localVector = new Vector();
        for (int k = 0; k < i; k++) {
          localVector.addElement(clone(localElement1, paramElement2.getElement(k)));
        }
        if (canJoin(localElement2, localElement3))
        {
          Element localElement4 = join(localElement1, localElement2, localElement3, paramInt1, paramInt2);
          localVector.addElement(localElement4);
        }
        else
        {
          if (localElement2 != null) {
            localVector.addElement(cloneAsNecessary(localElement1, localElement2, paramInt1, paramInt2));
          }
          if (localElement3 != null) {
            localVector.addElement(cloneAsNecessary(localElement1, localElement3, paramInt1, paramInt2));
          }
        }
        int m = paramElement3.getElementCount();
        for (int n = localElement3 == null ? j : j + 1; n < m; n++) {
          localVector.addElement(clone(localElement1, paramElement3.getElement(n)));
        }
        Element[] arrayOfElement = new Element[localVector.size()];
        localVector.copyInto(arrayOfElement);
        ((AbstractDocument.BranchElement)localElement1).replace(0, 0, arrayOfElement);
        return localElement1;
      }
      throw new StateInvariantError("No support to join leaf element with non-leaf element");
    }
    
    public Element clone(Element paramElement1, Element paramElement2)
    {
      if (paramElement2.isLeaf()) {
        return createLeafElement(paramElement1, paramElement2.getAttributes(), paramElement2.getStartOffset(), paramElement2.getEndOffset());
      }
      Element localElement = createBranchElement(paramElement1, paramElement2.getAttributes());
      int i = paramElement2.getElementCount();
      Element[] arrayOfElement = new Element[i];
      for (int j = 0; j < i; j++) {
        arrayOfElement[j] = clone(localElement, paramElement2.getElement(j));
      }
      ((AbstractDocument.BranchElement)localElement).replace(0, 0, arrayOfElement);
      return localElement;
    }
    
    Element cloneAsNecessary(Element paramElement1, Element paramElement2, int paramInt1, int paramInt2)
    {
      if (paramElement2.isLeaf()) {
        return createLeafElement(paramElement1, paramElement2.getAttributes(), paramElement2.getStartOffset(), paramElement2.getEndOffset());
      }
      Element localElement1 = createBranchElement(paramElement1, paramElement2.getAttributes());
      int i = paramElement2.getElementCount();
      ArrayList localArrayList = new ArrayList(i);
      for (int j = 0; j < i; j++)
      {
        Element localElement2 = paramElement2.getElement(j);
        if ((localElement2.getStartOffset() < paramInt1) || (localElement2.getEndOffset() > paramInt2)) {
          localArrayList.add(cloneAsNecessary(localElement1, localElement2, paramInt1, paramInt2));
        }
      }
      Element[] arrayOfElement = new Element[localArrayList.size()];
      arrayOfElement = (Element[])localArrayList.toArray(arrayOfElement);
      ((AbstractDocument.BranchElement)localElement1).replace(0, 0, arrayOfElement);
      return localElement1;
    }
    
    void fracture(int paramInt)
    {
      int i = insertPath.length;
      int j = -1;
      boolean bool = recreateLeafs;
      ElemChanges localElemChanges1 = insertPath[(i - 1)];
      int k = index + 1 < parent.getElementCount() ? 1 : 0;
      int m = bool ? i : -1;
      int n = i - 1;
      createdFracture = true;
      for (int i1 = i - 2; i1 >= 0; i1--)
      {
        ElemChanges localElemChanges2 = insertPath[i1];
        if ((added.size() > 0) || (i1 == paramInt))
        {
          j = i1;
          if ((!bool) && (k != 0))
          {
            bool = true;
            if (m == -1) {
              m = n + 1;
            }
          }
        }
        if ((k == 0) && (index < parent.getElementCount()))
        {
          k = 1;
          n = i1;
        }
      }
      if (bool)
      {
        if (j == -1) {
          j = i - 1;
        }
        fractureFrom(insertPath, j, m);
      }
    }
    
    void fractureFrom(ElemChanges[] paramArrayOfElemChanges, int paramInt1, int paramInt2)
    {
      ElemChanges localElemChanges = paramArrayOfElemChanges[paramInt1];
      int i = paramArrayOfElemChanges.length;
      Element localElement1;
      if (paramInt1 + 1 == i) {
        localElement1 = parent.getElement(index);
      } else {
        localElement1 = parent.getElement(index - 1);
      }
      Element localElement2;
      if (localElement1.isLeaf()) {
        localElement2 = createLeafElement(parent, localElement1.getAttributes(), Math.max(endOffset, localElement1.getStartOffset()), localElement1.getEndOffset());
      } else {
        localElement2 = createBranchElement(parent, localElement1.getAttributes());
      }
      fracturedParent = parent;
      fracturedChild = localElement2;
      for (Element localElement3 = localElement2;; localElement3 = localElement2)
      {
        paramInt1++;
        if (paramInt1 >= paramInt2) {
          break;
        }
        int j = paramInt1 + 1 == paramInt2 ? 1 : 0;
        int k = paramInt1 + 1 == i ? 1 : 0;
        localElemChanges = paramArrayOfElemChanges[paramInt1];
        if (j != 0)
        {
          if ((offsetLastIndex) || (k == 0)) {
            localElement1 = null;
          } else {
            localElement1 = parent.getElement(index);
          }
        }
        else {
          localElement1 = parent.getElement(index - 1);
        }
        if (localElement1 != null)
        {
          if (localElement1.isLeaf()) {
            localElement2 = createLeafElement(localElement3, localElement1.getAttributes(), Math.max(endOffset, localElement1.getStartOffset()), localElement1.getEndOffset());
          } else {
            localElement2 = createBranchElement(localElement3, localElement1.getAttributes());
          }
        }
        else {
          localElement2 = null;
        }
        int m = parent.getElementCount() - index;
        int i1 = 1;
        int n;
        Element[] arrayOfElement;
        if (localElement2 == null)
        {
          if (k != 0)
          {
            m--;
            n = index + 1;
          }
          else
          {
            n = index;
          }
          i1 = 0;
          arrayOfElement = new Element[m];
        }
        else
        {
          if (j == 0)
          {
            m++;
            n = index;
          }
          else
          {
            n = index + 1;
          }
          arrayOfElement = new Element[m];
          arrayOfElement[0] = localElement2;
        }
        for (int i2 = i1; i2 < m; i2++)
        {
          Element localElement4 = parent.getElement(n++);
          arrayOfElement[i2] = recreateFracturedElement(localElement3, localElement4);
          removed.addElement(localElement4);
        }
        ((AbstractDocument.BranchElement)localElement3).replace(0, 0, arrayOfElement);
      }
    }
    
    Element recreateFracturedElement(Element paramElement1, Element paramElement2)
    {
      if (paramElement2.isLeaf()) {
        return createLeafElement(paramElement1, paramElement2.getAttributes(), Math.max(paramElement2.getStartOffset(), endOffset), paramElement2.getEndOffset());
      }
      Element localElement = createBranchElement(paramElement1, paramElement2.getAttributes());
      int i = paramElement2.getElementCount();
      Element[] arrayOfElement = new Element[i];
      for (int j = 0; j < i; j++) {
        arrayOfElement[j] = recreateFracturedElement(localElement, paramElement2.getElement(j));
      }
      ((AbstractDocument.BranchElement)localElement).replace(0, 0, arrayOfElement);
      return localElement;
    }
    
    void fractureDeepestLeaf(DefaultStyledDocument.ElementSpec[] paramArrayOfElementSpec)
    {
      ElemChanges localElemChanges = (ElemChanges)path.peek();
      Element localElement1 = parent.getElement(index);
      if (offset != 0)
      {
        Element localElement2 = createLeafElement(parent, localElement1.getAttributes(), localElement1.getStartOffset(), offset);
        added.addElement(localElement2);
      }
      removed.addElement(localElement1);
      if (localElement1.getEndOffset() != endOffset) {
        recreateLeafs = true;
      } else {
        offsetLastIndex = true;
      }
    }
    
    void insertFirstContent(DefaultStyledDocument.ElementSpec[] paramArrayOfElementSpec)
    {
      DefaultStyledDocument.ElementSpec localElementSpec = paramArrayOfElementSpec[0];
      ElemChanges localElemChanges = (ElemChanges)path.peek();
      Element localElement1 = parent.getElement(index);
      int i = offset + localElementSpec.getLength();
      int j = paramArrayOfElementSpec.length == 1 ? 1 : 0;
      Element localElement2;
      switch (localElementSpec.getDirection())
      {
      case 4: 
        if ((localElement1.getEndOffset() != i) && (j == 0))
        {
          localElement2 = createLeafElement(parent, localElement1.getAttributes(), localElement1.getStartOffset(), i);
          added.addElement(localElement2);
          removed.addElement(localElement1);
          if (localElement1.getEndOffset() != endOffset) {
            recreateLeafs = true;
          } else {
            offsetLastIndex = true;
          }
        }
        else
        {
          offsetLastIndex = true;
          offsetLastIndexOnReplace = true;
        }
        break;
      case 5: 
        if (offset != 0)
        {
          localElement2 = createLeafElement(parent, localElement1.getAttributes(), localElement1.getStartOffset(), offset);
          added.addElement(localElement2);
          Element localElement3 = parent.getElement(index + 1);
          if (j != 0) {
            localElement2 = createLeafElement(parent, localElement3.getAttributes(), offset, localElement3.getEndOffset());
          } else {
            localElement2 = createLeafElement(parent, localElement3.getAttributes(), offset, i);
          }
          added.addElement(localElement2);
          removed.addElement(localElement1);
          removed.addElement(localElement3);
        }
        break;
      default: 
        if (localElement1.getStartOffset() != offset)
        {
          localElement2 = createLeafElement(parent, localElement1.getAttributes(), localElement1.getStartOffset(), offset);
          added.addElement(localElement2);
        }
        removed.addElement(localElement1);
        localElement2 = createLeafElement(parent, localElementSpec.getAttributes(), offset, i);
        added.addElement(localElement2);
        if (localElement1.getEndOffset() != endOffset) {
          recreateLeafs = true;
        } else {
          offsetLastIndex = true;
        }
        break;
      }
    }
    
    class ElemChanges
    {
      Element parent;
      int index;
      Vector<Element> added;
      Vector<Element> removed;
      boolean isFracture;
      
      ElemChanges(Element paramElement, int paramInt, boolean paramBoolean)
      {
        parent = paramElement;
        index = paramInt;
        isFracture = paramBoolean;
        added = new Vector();
        removed = new Vector();
      }
      
      public String toString()
      {
        return "added: " + added + "\nremoved: " + removed + "\n";
      }
    }
  }
  
  public static class ElementSpec
  {
    public static final short StartTagType = 1;
    public static final short EndTagType = 2;
    public static final short ContentType = 3;
    public static final short JoinPreviousDirection = 4;
    public static final short JoinNextDirection = 5;
    public static final short OriginateDirection = 6;
    public static final short JoinFractureDirection = 7;
    private AttributeSet attr;
    private int len;
    private short type;
    private short direction;
    private int offs;
    private char[] data;
    
    public ElementSpec(AttributeSet paramAttributeSet, short paramShort)
    {
      this(paramAttributeSet, paramShort, null, 0, 0);
    }
    
    public ElementSpec(AttributeSet paramAttributeSet, short paramShort, int paramInt)
    {
      this(paramAttributeSet, paramShort, null, 0, paramInt);
    }
    
    public ElementSpec(AttributeSet paramAttributeSet, short paramShort, char[] paramArrayOfChar, int paramInt1, int paramInt2)
    {
      attr = paramAttributeSet;
      type = paramShort;
      data = paramArrayOfChar;
      offs = paramInt1;
      len = paramInt2;
      direction = 6;
    }
    
    public void setType(short paramShort)
    {
      type = paramShort;
    }
    
    public short getType()
    {
      return type;
    }
    
    public void setDirection(short paramShort)
    {
      direction = paramShort;
    }
    
    public short getDirection()
    {
      return direction;
    }
    
    public AttributeSet getAttributes()
    {
      return attr;
    }
    
    public char[] getArray()
    {
      return data;
    }
    
    public int getOffset()
    {
      return offs;
    }
    
    public int getLength()
    {
      return len;
    }
    
    public String toString()
    {
      String str1 = "??";
      String str2 = "??";
      switch (type)
      {
      case 1: 
        str1 = "StartTag";
        break;
      case 3: 
        str1 = "Content";
        break;
      case 2: 
        str1 = "EndTag";
      }
      switch (direction)
      {
      case 4: 
        str2 = "JoinPrevious";
        break;
      case 5: 
        str2 = "JoinNext";
        break;
      case 6: 
        str2 = "Originate";
        break;
      case 7: 
        str2 = "Fracture";
      }
      return str1 + ":" + str2 + ":" + getLength();
    }
  }
  
  protected class SectionElement
    extends AbstractDocument.BranchElement
  {
    public SectionElement()
    {
      super(null, null);
    }
    
    public String getName()
    {
      return "section";
    }
  }
  
  static class StyleChangeHandler
    extends DefaultStyledDocument.AbstractChangeHandler
  {
    StyleChangeHandler(DefaultStyledDocument paramDefaultStyledDocument)
    {
      super();
    }
    
    void fireStateChanged(DefaultStyledDocument paramDefaultStyledDocument, ChangeEvent paramChangeEvent)
    {
      Object localObject = paramChangeEvent.getSource();
      if ((localObject instanceof Style)) {
        paramDefaultStyledDocument.styleChanged((Style)localObject);
      } else {
        paramDefaultStyledDocument.styleChanged(null);
      }
    }
  }
  
  static class StyleChangeUndoableEdit
    extends AbstractUndoableEdit
  {
    protected AbstractDocument.AbstractElement element;
    protected Style newStyle;
    protected AttributeSet oldStyle;
    
    public StyleChangeUndoableEdit(AbstractDocument.AbstractElement paramAbstractElement, Style paramStyle)
    {
      element = paramAbstractElement;
      newStyle = paramStyle;
      oldStyle = paramAbstractElement.getResolveParent();
    }
    
    public void redo()
      throws CannotRedoException
    {
      super.redo();
      element.setResolveParent(newStyle);
    }
    
    public void undo()
      throws CannotUndoException
    {
      super.undo();
      element.setResolveParent(oldStyle);
    }
  }
  
  static class StyleContextChangeHandler
    extends DefaultStyledDocument.AbstractChangeHandler
  {
    StyleContextChangeHandler(DefaultStyledDocument paramDefaultStyledDocument)
    {
      super();
    }
    
    void fireStateChanged(DefaultStyledDocument paramDefaultStyledDocument, ChangeEvent paramChangeEvent)
    {
      paramDefaultStyledDocument.updateStylesListeningTo();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\DefaultStyledDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */