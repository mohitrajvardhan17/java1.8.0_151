package javax.swing.text;

import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.Bidi;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.ElementChange;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.TreeNode;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import sun.font.BidiUtils;
import sun.swing.SwingUtilities2;

public abstract class AbstractDocument
  implements Document, Serializable
{
  private transient int numReaders;
  private transient Thread currWriter;
  private transient int numWriters;
  private transient boolean notifyingListeners;
  private static Boolean defaultI18NProperty;
  private Dictionary<Object, Object> documentProperties = null;
  protected EventListenerList listenerList = new EventListenerList();
  private Content data;
  private AttributeContext context;
  private transient BranchElement bidiRoot;
  private DocumentFilter documentFilter;
  private transient DocumentFilter.FilterBypass filterBypass;
  private static final String BAD_LOCK_STATE = "document lock failure";
  protected static final String BAD_LOCATION = "document location failure";
  public static final String ParagraphElementName = "paragraph";
  public static final String ContentElementName = "content";
  public static final String SectionElementName = "section";
  public static final String BidiElementName = "bidi level";
  public static final String ElementNameAttribute = "$ename";
  static final String I18NProperty = "i18n";
  static final Object MultiByteProperty = "multiByte";
  static final String AsyncLoadPriority = "load priority";
  
  protected AbstractDocument(Content paramContent)
  {
    this(paramContent, StyleContext.getDefaultStyleContext());
  }
  
  protected AbstractDocument(Content paramContent, AttributeContext paramAttributeContext)
  {
    data = paramContent;
    context = paramAttributeContext;
    bidiRoot = new BidiRootElement();
    Object localObject1;
    if (defaultI18NProperty == null)
    {
      localObject1 = (String)AccessController.doPrivileged(new PrivilegedAction()
      {
        public String run()
        {
          return System.getProperty("i18n");
        }
      });
      if (localObject1 != null) {
        defaultI18NProperty = Boolean.valueOf((String)localObject1);
      } else {
        defaultI18NProperty = Boolean.FALSE;
      }
    }
    putProperty("i18n", defaultI18NProperty);
    writeLock();
    try
    {
      localObject1 = new Element[1];
      localObject1[0] = new BidiElement(bidiRoot, 0, 1, 0);
      bidiRoot.replace(0, 0, (Element[])localObject1);
    }
    finally
    {
      writeUnlock();
    }
  }
  
  public Dictionary<Object, Object> getDocumentProperties()
  {
    if (documentProperties == null) {
      documentProperties = new Hashtable(2);
    }
    return documentProperties;
  }
  
  public void setDocumentProperties(Dictionary<Object, Object> paramDictionary)
  {
    documentProperties = paramDictionary;
  }
  
  protected void fireInsertUpdate(DocumentEvent paramDocumentEvent)
  {
    notifyingListeners = true;
    try
    {
      Object[] arrayOfObject = listenerList.getListenerList();
      for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
        if (arrayOfObject[i] == DocumentListener.class) {
          ((DocumentListener)arrayOfObject[(i + 1)]).insertUpdate(paramDocumentEvent);
        }
      }
    }
    finally
    {
      notifyingListeners = false;
    }
  }
  
  protected void fireChangedUpdate(DocumentEvent paramDocumentEvent)
  {
    notifyingListeners = true;
    try
    {
      Object[] arrayOfObject = listenerList.getListenerList();
      for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
        if (arrayOfObject[i] == DocumentListener.class) {
          ((DocumentListener)arrayOfObject[(i + 1)]).changedUpdate(paramDocumentEvent);
        }
      }
    }
    finally
    {
      notifyingListeners = false;
    }
  }
  
  protected void fireRemoveUpdate(DocumentEvent paramDocumentEvent)
  {
    notifyingListeners = true;
    try
    {
      Object[] arrayOfObject = listenerList.getListenerList();
      for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
        if (arrayOfObject[i] == DocumentListener.class) {
          ((DocumentListener)arrayOfObject[(i + 1)]).removeUpdate(paramDocumentEvent);
        }
      }
    }
    finally
    {
      notifyingListeners = false;
    }
  }
  
  protected void fireUndoableEditUpdate(UndoableEditEvent paramUndoableEditEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == UndoableEditListener.class) {
        ((UndoableEditListener)arrayOfObject[(i + 1)]).undoableEditHappened(paramUndoableEditEvent);
      }
    }
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    return listenerList.getListeners(paramClass);
  }
  
  public int getAsynchronousLoadPriority()
  {
    Integer localInteger = (Integer)getProperty("load priority");
    if (localInteger != null) {
      return localInteger.intValue();
    }
    return -1;
  }
  
  public void setAsynchronousLoadPriority(int paramInt)
  {
    Object localObject = paramInt >= 0 ? Integer.valueOf(paramInt) : null;
    putProperty("load priority", localObject);
  }
  
  public void setDocumentFilter(DocumentFilter paramDocumentFilter)
  {
    documentFilter = paramDocumentFilter;
  }
  
  public DocumentFilter getDocumentFilter()
  {
    return documentFilter;
  }
  
  /* Error */
  public void render(Runnable paramRunnable)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 600	javax/swing/text/AbstractDocument:readLock	()V
    //   4: aload_1
    //   5: invokeinterface 663 1 0
    //   10: aload_0
    //   11: invokevirtual 601	javax/swing/text/AbstractDocument:readUnlock	()V
    //   14: goto +10 -> 24
    //   17: astore_2
    //   18: aload_0
    //   19: invokevirtual 601	javax/swing/text/AbstractDocument:readUnlock	()V
    //   22: aload_2
    //   23: athrow
    //   24: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	25	0	this	AbstractDocument
    //   0	25	1	paramRunnable	Runnable
    //   17	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	10	17	finally
  }
  
  public int getLength()
  {
    return data.length() - 1;
  }
  
  public void addDocumentListener(DocumentListener paramDocumentListener)
  {
    listenerList.add(DocumentListener.class, paramDocumentListener);
  }
  
  public void removeDocumentListener(DocumentListener paramDocumentListener)
  {
    listenerList.remove(DocumentListener.class, paramDocumentListener);
  }
  
  public DocumentListener[] getDocumentListeners()
  {
    return (DocumentListener[])listenerList.getListeners(DocumentListener.class);
  }
  
  public void addUndoableEditListener(UndoableEditListener paramUndoableEditListener)
  {
    listenerList.add(UndoableEditListener.class, paramUndoableEditListener);
  }
  
  public void removeUndoableEditListener(UndoableEditListener paramUndoableEditListener)
  {
    listenerList.remove(UndoableEditListener.class, paramUndoableEditListener);
  }
  
  public UndoableEditListener[] getUndoableEditListeners()
  {
    return (UndoableEditListener[])listenerList.getListeners(UndoableEditListener.class);
  }
  
  public final Object getProperty(Object paramObject)
  {
    return getDocumentProperties().get(paramObject);
  }
  
  public final void putProperty(Object paramObject1, Object paramObject2)
  {
    if (paramObject2 != null) {
      getDocumentProperties().put(paramObject1, paramObject2);
    } else {
      getDocumentProperties().remove(paramObject1);
    }
    if ((paramObject1 == TextAttribute.RUN_DIRECTION) && (Boolean.TRUE.equals(getProperty("i18n"))))
    {
      writeLock();
      try
      {
        DefaultDocumentEvent localDefaultDocumentEvent = new DefaultDocumentEvent(0, getLength(), DocumentEvent.EventType.INSERT);
        updateBidi(localDefaultDocumentEvent);
      }
      finally
      {
        writeUnlock();
      }
    }
  }
  
  public void remove(int paramInt1, int paramInt2)
    throws BadLocationException
  {
    DocumentFilter localDocumentFilter = getDocumentFilter();
    writeLock();
    try
    {
      if (localDocumentFilter != null) {
        localDocumentFilter.remove(getFilterBypass(), paramInt1, paramInt2);
      } else {
        handleRemove(paramInt1, paramInt2);
      }
    }
    finally
    {
      writeUnlock();
    }
  }
  
  void handleRemove(int paramInt1, int paramInt2)
    throws BadLocationException
  {
    if (paramInt2 > 0)
    {
      if ((paramInt1 < 0) || (paramInt1 + paramInt2 > getLength())) {
        throw new BadLocationException("Invalid remove", getLength() + 1);
      }
      DefaultDocumentEvent localDefaultDocumentEvent = new DefaultDocumentEvent(paramInt1, paramInt2, DocumentEvent.EventType.REMOVE);
      boolean bool = Utilities.isComposedTextElement(this, paramInt1);
      removeUpdate(localDefaultDocumentEvent);
      UndoableEdit localUndoableEdit = data.remove(paramInt1, paramInt2);
      if (localUndoableEdit != null) {
        localDefaultDocumentEvent.addEdit(localUndoableEdit);
      }
      postRemoveUpdate(localDefaultDocumentEvent);
      localDefaultDocumentEvent.end();
      fireRemoveUpdate(localDefaultDocumentEvent);
      if ((localUndoableEdit != null) && (!bool)) {
        fireUndoableEditUpdate(new UndoableEditEvent(this, localDefaultDocumentEvent));
      }
    }
  }
  
  public void replace(int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet)
    throws BadLocationException
  {
    if ((paramInt2 == 0) && ((paramString == null) || (paramString.length() == 0))) {
      return;
    }
    DocumentFilter localDocumentFilter = getDocumentFilter();
    writeLock();
    try
    {
      if (localDocumentFilter != null)
      {
        localDocumentFilter.replace(getFilterBypass(), paramInt1, paramInt2, paramString, paramAttributeSet);
      }
      else
      {
        if (paramInt2 > 0) {
          remove(paramInt1, paramInt2);
        }
        if ((paramString != null) && (paramString.length() > 0)) {
          insertString(paramInt1, paramString, paramAttributeSet);
        }
      }
    }
    finally
    {
      writeUnlock();
    }
  }
  
  public void insertString(int paramInt, String paramString, AttributeSet paramAttributeSet)
    throws BadLocationException
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return;
    }
    DocumentFilter localDocumentFilter = getDocumentFilter();
    writeLock();
    try
    {
      if (localDocumentFilter != null) {
        localDocumentFilter.insertString(getFilterBypass(), paramInt, paramString, paramAttributeSet);
      } else {
        handleInsertString(paramInt, paramString, paramAttributeSet);
      }
    }
    finally
    {
      writeUnlock();
    }
  }
  
  private void handleInsertString(int paramInt, String paramString, AttributeSet paramAttributeSet)
    throws BadLocationException
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return;
    }
    UndoableEdit localUndoableEdit = data.insertString(paramInt, paramString);
    DefaultDocumentEvent localDefaultDocumentEvent = new DefaultDocumentEvent(paramInt, paramString.length(), DocumentEvent.EventType.INSERT);
    if (localUndoableEdit != null) {
      localDefaultDocumentEvent.addEdit(localUndoableEdit);
    }
    if (getProperty("i18n").equals(Boolean.FALSE))
    {
      Object localObject = getProperty(TextAttribute.RUN_DIRECTION);
      if ((localObject != null) && (localObject.equals(TextAttribute.RUN_DIRECTION_RTL)))
      {
        putProperty("i18n", Boolean.TRUE);
      }
      else
      {
        char[] arrayOfChar = paramString.toCharArray();
        if (SwingUtilities2.isComplexLayout(arrayOfChar, 0, arrayOfChar.length)) {
          putProperty("i18n", Boolean.TRUE);
        }
      }
    }
    insertUpdate(localDefaultDocumentEvent, paramAttributeSet);
    localDefaultDocumentEvent.end();
    fireInsertUpdate(localDefaultDocumentEvent);
    if ((localUndoableEdit != null) && ((paramAttributeSet == null) || (!paramAttributeSet.isDefined(StyleConstants.ComposedTextAttribute)))) {
      fireUndoableEditUpdate(new UndoableEditEvent(this, localDefaultDocumentEvent));
    }
  }
  
  public String getText(int paramInt1, int paramInt2)
    throws BadLocationException
  {
    if (paramInt2 < 0) {
      throw new BadLocationException("Length must be positive", paramInt2);
    }
    String str = data.getString(paramInt1, paramInt2);
    return str;
  }
  
  public void getText(int paramInt1, int paramInt2, Segment paramSegment)
    throws BadLocationException
  {
    if (paramInt2 < 0) {
      throw new BadLocationException("Length must be positive", paramInt2);
    }
    data.getChars(paramInt1, paramInt2, paramSegment);
  }
  
  public synchronized Position createPosition(int paramInt)
    throws BadLocationException
  {
    return data.createPosition(paramInt);
  }
  
  public final Position getStartPosition()
  {
    Position localPosition;
    try
    {
      localPosition = createPosition(0);
    }
    catch (BadLocationException localBadLocationException)
    {
      localPosition = null;
    }
    return localPosition;
  }
  
  public final Position getEndPosition()
  {
    Position localPosition;
    try
    {
      localPosition = createPosition(data.length());
    }
    catch (BadLocationException localBadLocationException)
    {
      localPosition = null;
    }
    return localPosition;
  }
  
  public Element[] getRootElements()
  {
    Element[] arrayOfElement = new Element[2];
    arrayOfElement[0] = getDefaultRootElement();
    arrayOfElement[1] = getBidiRootElement();
    return arrayOfElement;
  }
  
  public abstract Element getDefaultRootElement();
  
  private DocumentFilter.FilterBypass getFilterBypass()
  {
    if (filterBypass == null) {
      filterBypass = new DefaultFilterBypass(null);
    }
    return filterBypass;
  }
  
  public Element getBidiRootElement()
  {
    return bidiRoot;
  }
  
  static boolean isLeftToRight(Document paramDocument, int paramInt1, int paramInt2)
  {
    if ((Boolean.TRUE.equals(paramDocument.getProperty("i18n"))) && ((paramDocument instanceof AbstractDocument)))
    {
      AbstractDocument localAbstractDocument = (AbstractDocument)paramDocument;
      Element localElement1 = localAbstractDocument.getBidiRootElement();
      int i = localElement1.getElementIndex(paramInt1);
      Element localElement2 = localElement1.getElement(i);
      if (localElement2.getEndOffset() >= paramInt2)
      {
        AttributeSet localAttributeSet = localElement2.getAttributes();
        return StyleConstants.getBidiLevel(localAttributeSet) % 2 == 0;
      }
    }
    return true;
  }
  
  public abstract Element getParagraphElement(int paramInt);
  
  protected final AttributeContext getAttributeContext()
  {
    return context;
  }
  
  protected void insertUpdate(DefaultDocumentEvent paramDefaultDocumentEvent, AttributeSet paramAttributeSet)
  {
    if (getProperty("i18n").equals(Boolean.TRUE)) {
      updateBidi(paramDefaultDocumentEvent);
    }
    if ((type == DocumentEvent.EventType.INSERT) && (paramDefaultDocumentEvent.getLength() > 0) && (!Boolean.TRUE.equals(getProperty(MultiByteProperty))))
    {
      Segment localSegment = SegmentCache.getSharedSegment();
      try
      {
        getText(paramDefaultDocumentEvent.getOffset(), paramDefaultDocumentEvent.getLength(), localSegment);
        localSegment.first();
        do
        {
          if (localSegment.current() > 'ÿ')
          {
            putProperty(MultiByteProperty, Boolean.TRUE);
            break;
          }
        } while (localSegment.next() != 65535);
      }
      catch (BadLocationException localBadLocationException) {}
      SegmentCache.releaseSharedSegment(localSegment);
    }
  }
  
  protected void removeUpdate(DefaultDocumentEvent paramDefaultDocumentEvent) {}
  
  protected void postRemoveUpdate(DefaultDocumentEvent paramDefaultDocumentEvent)
  {
    if (getProperty("i18n").equals(Boolean.TRUE)) {
      updateBidi(paramDefaultDocumentEvent);
    }
  }
  
  void updateBidi(DefaultDocumentEvent paramDefaultDocumentEvent)
  {
    Element localElement1;
    Element localElement2;
    if ((type == DocumentEvent.EventType.INSERT) || (type == DocumentEvent.EventType.CHANGE))
    {
      int i = paramDefaultDocumentEvent.getOffset();
      int j = i + paramDefaultDocumentEvent.getLength();
      localElement1 = getParagraphElement(i).getStartOffset();
      localElement2 = getParagraphElement(j).getEndOffset();
    }
    else if (type == DocumentEvent.EventType.REMOVE)
    {
      localObject1 = getParagraphElement(paramDefaultDocumentEvent.getOffset());
      localElement1 = ((Element)localObject1).getStartOffset();
      localElement2 = ((Element)localObject1).getEndOffset();
    }
    else
    {
      throw new Error("Internal error: unknown event type.");
    }
    Object localObject1 = calculateBidiLevels(localElement1, localElement2);
    Vector localVector = new Vector();
    int k = localElement1;
    int m = 0;
    if (k > 0)
    {
      localElement3 = bidiRoot.getElementIndex(localElement1 - 1);
      m = localElement3;
      localElement4 = bidiRoot.getElement(localElement3);
      int i1 = StyleConstants.getBidiLevel(localElement4.getAttributes());
      if (i1 == localObject1[0]) {
        k = localElement4.getStartOffset();
      } else if (localElement4.getEndOffset() > localElement1) {
        localVector.addElement(new BidiElement(bidiRoot, localElement4.getStartOffset(), localElement1, i1));
      } else {
        m++;
      }
    }
    for (Element localElement3 = 0; (localElement3 < localObject1.length) && (localObject1[localElement3] == localObject1[0]); localElement3++) {}
    Element localElement4 = localElement2;
    BidiElement localBidiElement = null;
    int i2 = bidiRoot.getElementCount() - 1;
    Element localElement6;
    int n;
    if (localElement4 <= getLength())
    {
      localElement5 = bidiRoot.getElementIndex(localElement2);
      i2 = localElement5;
      localElement6 = bidiRoot.getElement(localElement5);
      int i4 = StyleConstants.getBidiLevel(localElement6.getAttributes());
      if (i4 == localObject1[(localObject1.length - 1)]) {
        n = localElement6.getEndOffset();
      } else if (localElement6.getStartOffset() < localElement2) {
        localBidiElement = new BidiElement(bidiRoot, localElement2, localElement6.getEndOffset(), i4);
      } else {
        i2--;
      }
    }
    for (Element localElement5 = localObject1.length; (localElement5 > localElement3) && (localObject1[(localElement5 - 1)] == localObject1[(localObject1.length - 1)]); localElement5--) {}
    if ((localElement3 == localElement5) && (localObject1[0] == localObject1[(localObject1.length - 1)]))
    {
      localVector.addElement(new BidiElement(bidiRoot, k, n, localObject1[0]));
    }
    else
    {
      localVector.addElement(new BidiElement(bidiRoot, k, localElement3 + localElement1, localObject1[0]));
      for (localElement6 = localElement3; localElement6 < localElement5; localElement6 = localObject2)
      {
        for (localObject2 = localElement6; (localObject2 < localObject1.length) && (localObject1[localObject2] == localObject1[localElement6]); localObject2++) {}
        localVector.addElement(new BidiElement(bidiRoot, localElement1 + localElement6, localElement1 + localObject2, localObject1[localElement6]));
      }
      localVector.addElement(new BidiElement(bidiRoot, localElement5 + localElement1, n, localObject1[(localObject1.length - 1)]));
    }
    if (localBidiElement != null) {
      localVector.addElement(localBidiElement);
    }
    int i3 = 0;
    if (bidiRoot.getElementCount() > 0) {
      i3 = i2 - m + 1;
    }
    Object localObject2 = new Element[i3];
    for (int i5 = 0; i5 < i3; i5++) {
      localObject2[i5] = bidiRoot.getElement(m + i5);
    }
    Element[] arrayOfElement = new Element[localVector.size()];
    localVector.copyInto(arrayOfElement);
    ElementEdit localElementEdit = new ElementEdit(bidiRoot, m, (Element[])localObject2, arrayOfElement);
    paramDefaultDocumentEvent.addEdit(localElementEdit);
    bidiRoot.replace(m, localObject2.length, arrayOfElement);
  }
  
  private byte[] calculateBidiLevels(int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte = new byte[paramInt2 - paramInt1];
    int i = 0;
    Boolean localBoolean1 = null;
    Object localObject = getProperty(TextAttribute.RUN_DIRECTION);
    if ((localObject instanceof Boolean)) {
      localBoolean1 = (Boolean)localObject;
    }
    int j = paramInt1;
    while (j < paramInt2)
    {
      Element localElement = getParagraphElement(j);
      int k = localElement.getStartOffset();
      int m = localElement.getEndOffset();
      Boolean localBoolean2 = localBoolean1;
      localObject = localElement.getAttributes().getAttribute(TextAttribute.RUN_DIRECTION);
      if ((localObject instanceof Boolean)) {
        localBoolean2 = (Boolean)localObject;
      }
      Segment localSegment = SegmentCache.getSharedSegment();
      try
      {
        getText(k, m - k, localSegment);
      }
      catch (BadLocationException localBadLocationException)
      {
        throw new Error("Internal error: " + localBadLocationException.toString());
      }
      int n = -2;
      if (localBoolean2 != null) {
        if (TextAttribute.RUN_DIRECTION_LTR.equals(localBoolean2)) {
          n = 0;
        } else {
          n = 1;
        }
      }
      Bidi localBidi = new Bidi(array, offset, null, 0, count, n);
      BidiUtils.getLevels(localBidi, arrayOfByte, i);
      i += localBidi.getLength();
      j = localElement.getEndOffset();
      SegmentCache.releaseSharedSegment(localSegment);
    }
    if (i != arrayOfByte.length) {
      throw new Error("levelsEnd assertion failed.");
    }
    return arrayOfByte;
  }
  
  public void dump(PrintStream paramPrintStream)
  {
    Element localElement = getDefaultRootElement();
    if ((localElement instanceof AbstractElement)) {
      ((AbstractElement)localElement).dump(paramPrintStream, 0);
    }
    bidiRoot.dump(paramPrintStream, 0);
  }
  
  protected final Content getContent()
  {
    return data;
  }
  
  protected Element createLeafElement(Element paramElement, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    return new LeafElement(paramElement, paramAttributeSet, paramInt1, paramInt2);
  }
  
  protected Element createBranchElement(Element paramElement, AttributeSet paramAttributeSet)
  {
    return new BranchElement(paramElement, paramAttributeSet);
  }
  
  protected final synchronized Thread getCurrentWriter()
  {
    return currWriter;
  }
  
  protected final synchronized void writeLock()
  {
    try
    {
      while ((numReaders > 0) || (currWriter != null))
      {
        if (Thread.currentThread() == currWriter)
        {
          if (notifyingListeners) {
            throw new IllegalStateException("Attempt to mutate in notification");
          }
          numWriters += 1;
          return;
        }
        wait();
      }
      currWriter = Thread.currentThread();
      numWriters = 1;
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new Error("Interrupted attempt to acquire write lock");
    }
  }
  
  protected final synchronized void writeUnlock()
  {
    if (--numWriters <= 0)
    {
      numWriters = 0;
      currWriter = null;
      notifyAll();
    }
  }
  
  public final synchronized void readLock()
  {
    try
    {
      while (currWriter != null)
      {
        if (currWriter == Thread.currentThread()) {
          return;
        }
        wait();
      }
      numReaders += 1;
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new Error("Interrupted attempt to acquire read lock");
    }
  }
  
  public final synchronized void readUnlock()
  {
    if (currWriter == Thread.currentThread()) {
      return;
    }
    if (numReaders <= 0) {
      throw new StateInvariantError("document lock failure");
    }
    numReaders -= 1;
    notify();
  }
  
  /* Error */
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 563	java/io/ObjectInputStream:defaultReadObject	()V
    //   4: aload_0
    //   5: new 296	javax/swing/event/EventListenerList
    //   8: dup
    //   9: invokespecial 593	javax/swing/event/EventListenerList:<init>	()V
    //   12: putfield 553	javax/swing/text/AbstractDocument:listenerList	Ljavax/swing/event/EventListenerList;
    //   15: aload_0
    //   16: new 304	javax/swing/text/AbstractDocument$BidiRootElement
    //   19: dup
    //   20: aload_0
    //   21: invokespecial 631	javax/swing/text/AbstractDocument$BidiRootElement:<init>	(Ljavax/swing/text/AbstractDocument;)V
    //   24: putfield 555	javax/swing/text/AbstractDocument:bidiRoot	Ljavax/swing/text/AbstractDocument$BranchElement;
    //   27: aload_0
    //   28: invokevirtual 602	javax/swing/text/AbstractDocument:writeLock	()V
    //   31: iconst_1
    //   32: anewarray 317	javax/swing/text/Element
    //   35: astore_2
    //   36: aload_2
    //   37: iconst_0
    //   38: new 303	javax/swing/text/AbstractDocument$BidiElement
    //   41: dup
    //   42: aload_0
    //   43: aload_0
    //   44: getfield 555	javax/swing/text/AbstractDocument:bidiRoot	Ljavax/swing/text/AbstractDocument$BranchElement;
    //   47: iconst_0
    //   48: iconst_1
    //   49: iconst_0
    //   50: invokespecial 630	javax/swing/text/AbstractDocument$BidiElement:<init>	(Ljavax/swing/text/AbstractDocument;Ljavax/swing/text/Element;III)V
    //   53: aastore
    //   54: aload_0
    //   55: getfield 555	javax/swing/text/AbstractDocument:bidiRoot	Ljavax/swing/text/AbstractDocument$BranchElement;
    //   58: iconst_0
    //   59: iconst_0
    //   60: aload_2
    //   61: invokevirtual 636	javax/swing/text/AbstractDocument$BranchElement:replace	(II[Ljavax/swing/text/Element;)V
    //   64: aload_0
    //   65: invokevirtual 603	javax/swing/text/AbstractDocument:writeUnlock	()V
    //   68: goto +10 -> 78
    //   71: astore_3
    //   72: aload_0
    //   73: invokevirtual 603	javax/swing/text/AbstractDocument:writeUnlock	()V
    //   76: aload_3
    //   77: athrow
    //   78: aload_1
    //   79: new 300	javax/swing/text/AbstractDocument$2
    //   82: dup
    //   83: aload_0
    //   84: invokespecial 628	javax/swing/text/AbstractDocument$2:<init>	(Ljavax/swing/text/AbstractDocument;)V
    //   87: iconst_0
    //   88: invokevirtual 564	java/io/ObjectInputStream:registerValidation	(Ljava/io/ObjectInputValidation;I)V
    //   91: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	92	0	this	AbstractDocument
    //   0	92	1	paramObjectInputStream	ObjectInputStream
    //   35	26	2	arrayOfElement	Element[]
    //   71	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   27	64	71	finally
  }
  
  public abstract class AbstractElement
    implements Element, MutableAttributeSet, Serializable, TreeNode
  {
    private Element parent;
    private transient AttributeSet attributes;
    
    public AbstractElement(Element paramElement, AttributeSet paramAttributeSet)
    {
      parent = paramElement;
      attributes = getAttributeContext().getEmptySet();
      if (paramAttributeSet != null) {
        addAttributes(paramAttributeSet);
      }
    }
    
    private final void indent(PrintWriter paramPrintWriter, int paramInt)
    {
      for (int i = 0; i < paramInt; i++) {
        paramPrintWriter.print("  ");
      }
    }
    
    public void dump(PrintStream paramPrintStream, int paramInt)
    {
      PrintWriter localPrintWriter;
      try
      {
        localPrintWriter = new PrintWriter(new OutputStreamWriter(paramPrintStream, "JavaEsc"), true);
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        localPrintWriter = new PrintWriter(paramPrintStream, true);
      }
      indent(localPrintWriter, paramInt);
      if (getName() == null) {
        localPrintWriter.print("<??");
      } else {
        localPrintWriter.print("<" + getName());
      }
      Object localObject1;
      Object localObject2;
      if (getAttributeCount() > 0)
      {
        localPrintWriter.println("");
        localObject1 = attributes.getAttributeNames();
        while (((Enumeration)localObject1).hasMoreElements())
        {
          localObject2 = ((Enumeration)localObject1).nextElement();
          indent(localPrintWriter, paramInt + 1);
          localPrintWriter.println(localObject2 + "=" + getAttribute(localObject2));
        }
        indent(localPrintWriter, paramInt);
      }
      localPrintWriter.println(">");
      if (isLeaf())
      {
        indent(localPrintWriter, paramInt + 1);
        localPrintWriter.print("[" + getStartOffset() + "," + getEndOffset() + "]");
        localObject1 = getContent();
        try
        {
          localObject2 = ((AbstractDocument.Content)localObject1).getString(getStartOffset(), getEndOffset() - getStartOffset());
          if (((String)localObject2).length() > 40) {
            localObject2 = ((String)localObject2).substring(0, 40) + "...";
          }
          localPrintWriter.println("[" + (String)localObject2 + "]");
        }
        catch (BadLocationException localBadLocationException) {}
      }
      else
      {
        int i = getElementCount();
        for (int j = 0; j < i; j++)
        {
          AbstractElement localAbstractElement = (AbstractElement)getElement(j);
          localAbstractElement.dump(paramPrintStream, paramInt + 1);
        }
      }
    }
    
    public int getAttributeCount()
    {
      return attributes.getAttributeCount();
    }
    
    public boolean isDefined(Object paramObject)
    {
      return attributes.isDefined(paramObject);
    }
    
    public boolean isEqual(AttributeSet paramAttributeSet)
    {
      return attributes.isEqual(paramAttributeSet);
    }
    
    public AttributeSet copyAttributes()
    {
      return attributes.copyAttributes();
    }
    
    public Object getAttribute(Object paramObject)
    {
      Object localObject1 = attributes.getAttribute(paramObject);
      if (localObject1 == null)
      {
        Object localObject2 = parent != null ? parent.getAttributes() : null;
        if (localObject2 != null) {
          localObject1 = ((AttributeSet)localObject2).getAttribute(paramObject);
        }
      }
      return localObject1;
    }
    
    public Enumeration<?> getAttributeNames()
    {
      return attributes.getAttributeNames();
    }
    
    public boolean containsAttribute(Object paramObject1, Object paramObject2)
    {
      return attributes.containsAttribute(paramObject1, paramObject2);
    }
    
    public boolean containsAttributes(AttributeSet paramAttributeSet)
    {
      return attributes.containsAttributes(paramAttributeSet);
    }
    
    public AttributeSet getResolveParent()
    {
      AttributeSet localAttributeSet = attributes.getResolveParent();
      if ((localAttributeSet == null) && (parent != null)) {
        localAttributeSet = parent.getAttributes();
      }
      return localAttributeSet;
    }
    
    public void addAttribute(Object paramObject1, Object paramObject2)
    {
      checkForIllegalCast();
      AbstractDocument.AttributeContext localAttributeContext = getAttributeContext();
      attributes = localAttributeContext.addAttribute(attributes, paramObject1, paramObject2);
    }
    
    public void addAttributes(AttributeSet paramAttributeSet)
    {
      checkForIllegalCast();
      AbstractDocument.AttributeContext localAttributeContext = getAttributeContext();
      attributes = localAttributeContext.addAttributes(attributes, paramAttributeSet);
    }
    
    public void removeAttribute(Object paramObject)
    {
      checkForIllegalCast();
      AbstractDocument.AttributeContext localAttributeContext = getAttributeContext();
      attributes = localAttributeContext.removeAttribute(attributes, paramObject);
    }
    
    public void removeAttributes(Enumeration<?> paramEnumeration)
    {
      checkForIllegalCast();
      AbstractDocument.AttributeContext localAttributeContext = getAttributeContext();
      attributes = localAttributeContext.removeAttributes(attributes, paramEnumeration);
    }
    
    public void removeAttributes(AttributeSet paramAttributeSet)
    {
      checkForIllegalCast();
      AbstractDocument.AttributeContext localAttributeContext = getAttributeContext();
      if (paramAttributeSet == this) {
        attributes = localAttributeContext.getEmptySet();
      } else {
        attributes = localAttributeContext.removeAttributes(attributes, paramAttributeSet);
      }
    }
    
    public void setResolveParent(AttributeSet paramAttributeSet)
    {
      checkForIllegalCast();
      AbstractDocument.AttributeContext localAttributeContext = getAttributeContext();
      if (paramAttributeSet != null) {
        attributes = localAttributeContext.addAttribute(attributes, StyleConstants.ResolveAttribute, paramAttributeSet);
      } else {
        attributes = localAttributeContext.removeAttribute(attributes, StyleConstants.ResolveAttribute);
      }
    }
    
    private final void checkForIllegalCast()
    {
      Thread localThread = getCurrentWriter();
      if ((localThread == null) || (localThread != Thread.currentThread())) {
        throw new StateInvariantError("Illegal cast to MutableAttributeSet");
      }
    }
    
    public Document getDocument()
    {
      return AbstractDocument.this;
    }
    
    public Element getParentElement()
    {
      return parent;
    }
    
    public AttributeSet getAttributes()
    {
      return this;
    }
    
    public String getName()
    {
      if (attributes.isDefined("$ename")) {
        return (String)attributes.getAttribute("$ename");
      }
      return null;
    }
    
    public abstract int getStartOffset();
    
    public abstract int getEndOffset();
    
    public abstract Element getElement(int paramInt);
    
    public abstract int getElementCount();
    
    public abstract int getElementIndex(int paramInt);
    
    public abstract boolean isLeaf();
    
    public TreeNode getChildAt(int paramInt)
    {
      return (TreeNode)getElement(paramInt);
    }
    
    public int getChildCount()
    {
      return getElementCount();
    }
    
    public TreeNode getParent()
    {
      return (TreeNode)getParentElement();
    }
    
    public int getIndex(TreeNode paramTreeNode)
    {
      for (int i = getChildCount() - 1; i >= 0; i--) {
        if (getChildAt(i) == paramTreeNode) {
          return i;
        }
      }
      return -1;
    }
    
    public abstract boolean getAllowsChildren();
    
    public abstract Enumeration children();
    
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      paramObjectOutputStream.defaultWriteObject();
      StyleContext.writeAttributeSet(paramObjectOutputStream, attributes);
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws ClassNotFoundException, IOException
    {
      paramObjectInputStream.defaultReadObject();
      SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
      StyleContext.readAttributeSet(paramObjectInputStream, localSimpleAttributeSet);
      AbstractDocument.AttributeContext localAttributeContext = getAttributeContext();
      attributes = localAttributeContext.addAttributes(SimpleAttributeSet.EMPTY, localSimpleAttributeSet);
    }
  }
  
  public static abstract interface AttributeContext
  {
    public abstract AttributeSet addAttribute(AttributeSet paramAttributeSet, Object paramObject1, Object paramObject2);
    
    public abstract AttributeSet addAttributes(AttributeSet paramAttributeSet1, AttributeSet paramAttributeSet2);
    
    public abstract AttributeSet removeAttribute(AttributeSet paramAttributeSet, Object paramObject);
    
    public abstract AttributeSet removeAttributes(AttributeSet paramAttributeSet, Enumeration<?> paramEnumeration);
    
    public abstract AttributeSet removeAttributes(AttributeSet paramAttributeSet1, AttributeSet paramAttributeSet2);
    
    public abstract AttributeSet getEmptySet();
    
    public abstract void reclaim(AttributeSet paramAttributeSet);
  }
  
  class BidiElement
    extends AbstractDocument.LeafElement
  {
    BidiElement(Element paramElement, int paramInt1, int paramInt2, int paramInt3)
    {
      super(paramElement, new SimpleAttributeSet(), paramInt1, paramInt2);
      addAttribute(StyleConstants.BidiLevel, Integer.valueOf(paramInt3));
    }
    
    public String getName()
    {
      return "bidi level";
    }
    
    int getLevel()
    {
      Integer localInteger = (Integer)getAttribute(StyleConstants.BidiLevel);
      if (localInteger != null) {
        return localInteger.intValue();
      }
      return 0;
    }
    
    boolean isLeftToRight()
    {
      return getLevel() % 2 == 0;
    }
  }
  
  class BidiRootElement
    extends AbstractDocument.BranchElement
  {
    BidiRootElement()
    {
      super(null, null);
    }
    
    public String getName()
    {
      return "bidi root";
    }
  }
  
  public class BranchElement
    extends AbstractDocument.AbstractElement
  {
    private AbstractDocument.AbstractElement[] children = new AbstractDocument.AbstractElement[1];
    private int nchildren = 0;
    private int lastIndex = -1;
    
    public BranchElement(Element paramElement, AttributeSet paramAttributeSet)
    {
      super(paramElement, paramAttributeSet);
    }
    
    public Element positionToElement(int paramInt)
    {
      int i = getElementIndex(paramInt);
      AbstractDocument.AbstractElement localAbstractElement = children[i];
      int j = localAbstractElement.getStartOffset();
      int k = localAbstractElement.getEndOffset();
      if ((paramInt >= j) && (paramInt < k)) {
        return localAbstractElement;
      }
      return null;
    }
    
    public void replace(int paramInt1, int paramInt2, Element[] paramArrayOfElement)
    {
      int i = paramArrayOfElement.length - paramInt2;
      int j = paramInt1 + paramInt2;
      int k = nchildren - j;
      int m = j + i;
      if (nchildren + i >= children.length)
      {
        int n = Math.max(2 * children.length, nchildren + i);
        AbstractDocument.AbstractElement[] arrayOfAbstractElement = new AbstractDocument.AbstractElement[n];
        System.arraycopy(children, 0, arrayOfAbstractElement, 0, paramInt1);
        System.arraycopy(paramArrayOfElement, 0, arrayOfAbstractElement, paramInt1, paramArrayOfElement.length);
        System.arraycopy(children, j, arrayOfAbstractElement, m, k);
        children = arrayOfAbstractElement;
      }
      else
      {
        System.arraycopy(children, j, children, m, k);
        System.arraycopy(paramArrayOfElement, 0, children, paramInt1, paramArrayOfElement.length);
      }
      nchildren += i;
    }
    
    public String toString()
    {
      return "BranchElement(" + getName() + ") " + getStartOffset() + "," + getEndOffset() + "\n";
    }
    
    public String getName()
    {
      String str = super.getName();
      if (str == null) {
        str = "paragraph";
      }
      return str;
    }
    
    public int getStartOffset()
    {
      return children[0].getStartOffset();
    }
    
    public int getEndOffset()
    {
      AbstractDocument.AbstractElement localAbstractElement = nchildren > 0 ? children[(nchildren - 1)] : children[0];
      return localAbstractElement.getEndOffset();
    }
    
    public Element getElement(int paramInt)
    {
      if (paramInt < nchildren) {
        return children[paramInt];
      }
      return null;
    }
    
    public int getElementCount()
    {
      return nchildren;
    }
    
    public int getElementIndex(int paramInt)
    {
      int j = 0;
      int k = nchildren - 1;
      int m = 0;
      int n = getStartOffset();
      if (nchildren == 0) {
        return 0;
      }
      if (paramInt >= getEndOffset()) {
        return nchildren - 1;
      }
      AbstractDocument.AbstractElement localAbstractElement;
      int i1;
      if ((lastIndex >= j) && (lastIndex <= k))
      {
        localAbstractElement = children[lastIndex];
        n = localAbstractElement.getStartOffset();
        i1 = localAbstractElement.getEndOffset();
        if ((paramInt >= n) && (paramInt < i1)) {
          return lastIndex;
        }
        if (paramInt < n) {
          k = lastIndex;
        } else {
          j = lastIndex;
        }
      }
      int i;
      while (j <= k)
      {
        m = j + (k - j) / 2;
        localAbstractElement = children[m];
        n = localAbstractElement.getStartOffset();
        i1 = localAbstractElement.getEndOffset();
        if ((paramInt >= n) && (paramInt < i1))
        {
          i = m;
          lastIndex = i;
          return i;
        }
        if (paramInt < n) {
          k = m - 1;
        } else {
          j = m + 1;
        }
      }
      if (paramInt < n) {
        i = m;
      } else {
        i = m + 1;
      }
      lastIndex = i;
      return i;
    }
    
    public boolean isLeaf()
    {
      return false;
    }
    
    public boolean getAllowsChildren()
    {
      return true;
    }
    
    public Enumeration children()
    {
      if (nchildren == 0) {
        return null;
      }
      Vector localVector = new Vector(nchildren);
      for (int i = 0; i < nchildren; i++) {
        localVector.addElement(children[i]);
      }
      return localVector.elements();
    }
  }
  
  public static abstract interface Content
  {
    public abstract Position createPosition(int paramInt)
      throws BadLocationException;
    
    public abstract int length();
    
    public abstract UndoableEdit insertString(int paramInt, String paramString)
      throws BadLocationException;
    
    public abstract UndoableEdit remove(int paramInt1, int paramInt2)
      throws BadLocationException;
    
    public abstract String getString(int paramInt1, int paramInt2)
      throws BadLocationException;
    
    public abstract void getChars(int paramInt1, int paramInt2, Segment paramSegment)
      throws BadLocationException;
  }
  
  public class DefaultDocumentEvent
    extends CompoundEdit
    implements DocumentEvent
  {
    private int offset;
    private int length;
    private Hashtable<Element, DocumentEvent.ElementChange> changeLookup;
    private DocumentEvent.EventType type;
    
    public DefaultDocumentEvent(int paramInt1, int paramInt2, DocumentEvent.EventType paramEventType)
    {
      offset = paramInt1;
      length = paramInt2;
      type = paramEventType;
    }
    
    public String toString()
    {
      return edits.toString();
    }
    
    public boolean addEdit(UndoableEdit paramUndoableEdit)
    {
      if ((changeLookup == null) && (edits.size() > 10))
      {
        changeLookup = new Hashtable();
        int i = edits.size();
        for (int j = 0; j < i; j++)
        {
          Object localObject = edits.elementAt(j);
          if ((localObject instanceof DocumentEvent.ElementChange))
          {
            DocumentEvent.ElementChange localElementChange2 = (DocumentEvent.ElementChange)localObject;
            changeLookup.put(localElementChange2.getElement(), localElementChange2);
          }
        }
      }
      if ((changeLookup != null) && ((paramUndoableEdit instanceof DocumentEvent.ElementChange)))
      {
        DocumentEvent.ElementChange localElementChange1 = (DocumentEvent.ElementChange)paramUndoableEdit;
        changeLookup.put(localElementChange1.getElement(), localElementChange1);
      }
      return super.addEdit(paramUndoableEdit);
    }
    
    /* Error */
    public void redo()
      throws CannotRedoException
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 145	javax/swing/text/AbstractDocument$DefaultDocumentEvent:this$0	Ljavax/swing/text/AbstractDocument;
      //   4: invokevirtual 157	javax/swing/text/AbstractDocument:writeLock	()V
      //   7: aload_0
      //   8: invokespecial 166	javax/swing/undo/CompoundEdit:redo	()V
      //   11: new 87	javax/swing/text/AbstractDocument$UndoRedoDocumentEvent
      //   14: dup
      //   15: aload_0
      //   16: getfield 145	javax/swing/text/AbstractDocument$DefaultDocumentEvent:this$0	Ljavax/swing/text/AbstractDocument;
      //   19: aload_0
      //   20: iconst_0
      //   21: invokespecial 164	javax/swing/text/AbstractDocument$UndoRedoDocumentEvent:<init>	(Ljavax/swing/text/AbstractDocument;Ljavax/swing/text/AbstractDocument$DefaultDocumentEvent;Z)V
      //   24: astore_1
      //   25: aload_0
      //   26: getfield 144	javax/swing/text/AbstractDocument$DefaultDocumentEvent:type	Ljavax/swing/event/DocumentEvent$EventType;
      //   29: getstatic 138	javax/swing/event/DocumentEvent$EventType:INSERT	Ljavax/swing/event/DocumentEvent$EventType;
      //   32: if_acmpne +14 -> 46
      //   35: aload_0
      //   36: getfield 145	javax/swing/text/AbstractDocument$DefaultDocumentEvent:this$0	Ljavax/swing/text/AbstractDocument;
      //   39: aload_1
      //   40: invokevirtual 160	javax/swing/text/AbstractDocument:fireInsertUpdate	(Ljavax/swing/event/DocumentEvent;)V
      //   43: goto +32 -> 75
      //   46: aload_0
      //   47: getfield 144	javax/swing/text/AbstractDocument$DefaultDocumentEvent:type	Ljavax/swing/event/DocumentEvent$EventType;
      //   50: getstatic 139	javax/swing/event/DocumentEvent$EventType:REMOVE	Ljavax/swing/event/DocumentEvent$EventType;
      //   53: if_acmpne +14 -> 67
      //   56: aload_0
      //   57: getfield 145	javax/swing/text/AbstractDocument$DefaultDocumentEvent:this$0	Ljavax/swing/text/AbstractDocument;
      //   60: aload_1
      //   61: invokevirtual 161	javax/swing/text/AbstractDocument:fireRemoveUpdate	(Ljavax/swing/event/DocumentEvent;)V
      //   64: goto +11 -> 75
      //   67: aload_0
      //   68: getfield 145	javax/swing/text/AbstractDocument$DefaultDocumentEvent:this$0	Ljavax/swing/text/AbstractDocument;
      //   71: aload_1
      //   72: invokevirtual 159	javax/swing/text/AbstractDocument:fireChangedUpdate	(Ljavax/swing/event/DocumentEvent;)V
      //   75: aload_0
      //   76: getfield 145	javax/swing/text/AbstractDocument$DefaultDocumentEvent:this$0	Ljavax/swing/text/AbstractDocument;
      //   79: invokevirtual 158	javax/swing/text/AbstractDocument:writeUnlock	()V
      //   82: goto +13 -> 95
      //   85: astore_2
      //   86: aload_0
      //   87: getfield 145	javax/swing/text/AbstractDocument$DefaultDocumentEvent:this$0	Ljavax/swing/text/AbstractDocument;
      //   90: invokevirtual 158	javax/swing/text/AbstractDocument:writeUnlock	()V
      //   93: aload_2
      //   94: athrow
      //   95: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	96	0	this	DefaultDocumentEvent
      //   24	48	1	localUndoRedoDocumentEvent	AbstractDocument.UndoRedoDocumentEvent
      //   85	9	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   7	75	85	finally
    }
    
    /* Error */
    public void undo()
      throws CannotUndoException
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 145	javax/swing/text/AbstractDocument$DefaultDocumentEvent:this$0	Ljavax/swing/text/AbstractDocument;
      //   4: invokevirtual 157	javax/swing/text/AbstractDocument:writeLock	()V
      //   7: aload_0
      //   8: invokespecial 167	javax/swing/undo/CompoundEdit:undo	()V
      //   11: new 87	javax/swing/text/AbstractDocument$UndoRedoDocumentEvent
      //   14: dup
      //   15: aload_0
      //   16: getfield 145	javax/swing/text/AbstractDocument$DefaultDocumentEvent:this$0	Ljavax/swing/text/AbstractDocument;
      //   19: aload_0
      //   20: iconst_1
      //   21: invokespecial 164	javax/swing/text/AbstractDocument$UndoRedoDocumentEvent:<init>	(Ljavax/swing/text/AbstractDocument;Ljavax/swing/text/AbstractDocument$DefaultDocumentEvent;Z)V
      //   24: astore_1
      //   25: aload_0
      //   26: getfield 144	javax/swing/text/AbstractDocument$DefaultDocumentEvent:type	Ljavax/swing/event/DocumentEvent$EventType;
      //   29: getstatic 139	javax/swing/event/DocumentEvent$EventType:REMOVE	Ljavax/swing/event/DocumentEvent$EventType;
      //   32: if_acmpne +14 -> 46
      //   35: aload_0
      //   36: getfield 145	javax/swing/text/AbstractDocument$DefaultDocumentEvent:this$0	Ljavax/swing/text/AbstractDocument;
      //   39: aload_1
      //   40: invokevirtual 160	javax/swing/text/AbstractDocument:fireInsertUpdate	(Ljavax/swing/event/DocumentEvent;)V
      //   43: goto +32 -> 75
      //   46: aload_0
      //   47: getfield 144	javax/swing/text/AbstractDocument$DefaultDocumentEvent:type	Ljavax/swing/event/DocumentEvent$EventType;
      //   50: getstatic 138	javax/swing/event/DocumentEvent$EventType:INSERT	Ljavax/swing/event/DocumentEvent$EventType;
      //   53: if_acmpne +14 -> 67
      //   56: aload_0
      //   57: getfield 145	javax/swing/text/AbstractDocument$DefaultDocumentEvent:this$0	Ljavax/swing/text/AbstractDocument;
      //   60: aload_1
      //   61: invokevirtual 161	javax/swing/text/AbstractDocument:fireRemoveUpdate	(Ljavax/swing/event/DocumentEvent;)V
      //   64: goto +11 -> 75
      //   67: aload_0
      //   68: getfield 145	javax/swing/text/AbstractDocument$DefaultDocumentEvent:this$0	Ljavax/swing/text/AbstractDocument;
      //   71: aload_1
      //   72: invokevirtual 159	javax/swing/text/AbstractDocument:fireChangedUpdate	(Ljavax/swing/event/DocumentEvent;)V
      //   75: aload_0
      //   76: getfield 145	javax/swing/text/AbstractDocument$DefaultDocumentEvent:this$0	Ljavax/swing/text/AbstractDocument;
      //   79: invokevirtual 158	javax/swing/text/AbstractDocument:writeUnlock	()V
      //   82: goto +13 -> 95
      //   85: astore_2
      //   86: aload_0
      //   87: getfield 145	javax/swing/text/AbstractDocument$DefaultDocumentEvent:this$0	Ljavax/swing/text/AbstractDocument;
      //   90: invokevirtual 158	javax/swing/text/AbstractDocument:writeUnlock	()V
      //   93: aload_2
      //   94: athrow
      //   95: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	96	0	this	DefaultDocumentEvent
      //   24	48	1	localUndoRedoDocumentEvent	AbstractDocument.UndoRedoDocumentEvent
      //   85	9	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   7	75	85	finally
    }
    
    public boolean isSignificant()
    {
      return true;
    }
    
    public String getPresentationName()
    {
      DocumentEvent.EventType localEventType = getType();
      if (localEventType == DocumentEvent.EventType.INSERT) {
        return UIManager.getString("AbstractDocument.additionText");
      }
      if (localEventType == DocumentEvent.EventType.REMOVE) {
        return UIManager.getString("AbstractDocument.deletionText");
      }
      return UIManager.getString("AbstractDocument.styleChangeText");
    }
    
    public String getUndoPresentationName()
    {
      return UIManager.getString("AbstractDocument.undoText") + " " + getPresentationName();
    }
    
    public String getRedoPresentationName()
    {
      return UIManager.getString("AbstractDocument.redoText") + " " + getPresentationName();
    }
    
    public DocumentEvent.EventType getType()
    {
      return type;
    }
    
    public int getOffset()
    {
      return offset;
    }
    
    public int getLength()
    {
      return length;
    }
    
    public Document getDocument()
    {
      return AbstractDocument.this;
    }
    
    public DocumentEvent.ElementChange getChange(Element paramElement)
    {
      if (changeLookup != null) {
        return (DocumentEvent.ElementChange)changeLookup.get(paramElement);
      }
      int i = edits.size();
      for (int j = 0; j < i; j++)
      {
        Object localObject = edits.elementAt(j);
        if ((localObject instanceof DocumentEvent.ElementChange))
        {
          DocumentEvent.ElementChange localElementChange = (DocumentEvent.ElementChange)localObject;
          if (paramElement.equals(localElementChange.getElement())) {
            return localElementChange;
          }
        }
      }
      return null;
    }
  }
  
  private class DefaultFilterBypass
    extends DocumentFilter.FilterBypass
  {
    private DefaultFilterBypass() {}
    
    public Document getDocument()
    {
      return AbstractDocument.this;
    }
    
    public void remove(int paramInt1, int paramInt2)
      throws BadLocationException
    {
      handleRemove(paramInt1, paramInt2);
    }
    
    public void insertString(int paramInt, String paramString, AttributeSet paramAttributeSet)
      throws BadLocationException
    {
      AbstractDocument.this.handleInsertString(paramInt, paramString, paramAttributeSet);
    }
    
    public void replace(int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet)
      throws BadLocationException
    {
      handleRemove(paramInt1, paramInt2);
      AbstractDocument.this.handleInsertString(paramInt1, paramString, paramAttributeSet);
    }
  }
  
  public static class ElementEdit
    extends AbstractUndoableEdit
    implements DocumentEvent.ElementChange
  {
    private Element e;
    private int index;
    private Element[] removed;
    private Element[] added;
    
    public ElementEdit(Element paramElement, int paramInt, Element[] paramArrayOfElement1, Element[] paramArrayOfElement2)
    {
      e = paramElement;
      index = paramInt;
      removed = paramArrayOfElement1;
      added = paramArrayOfElement2;
    }
    
    public Element getElement()
    {
      return e;
    }
    
    public int getIndex()
    {
      return index;
    }
    
    public Element[] getChildrenRemoved()
    {
      return removed;
    }
    
    public Element[] getChildrenAdded()
    {
      return added;
    }
    
    public void redo()
      throws CannotRedoException
    {
      super.redo();
      Element[] arrayOfElement = removed;
      removed = added;
      added = arrayOfElement;
      ((AbstractDocument.BranchElement)e).replace(index, removed.length, added);
    }
    
    public void undo()
      throws CannotUndoException
    {
      super.undo();
      ((AbstractDocument.BranchElement)e).replace(index, added.length, removed);
      Element[] arrayOfElement = removed;
      removed = added;
      added = arrayOfElement;
    }
  }
  
  public class LeafElement
    extends AbstractDocument.AbstractElement
  {
    private transient Position p0;
    private transient Position p1;
    
    public LeafElement(Element paramElement, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
    {
      super(paramElement, paramAttributeSet);
      try
      {
        p0 = createPosition(paramInt1);
        p1 = createPosition(paramInt2);
      }
      catch (BadLocationException localBadLocationException)
      {
        p0 = null;
        p1 = null;
        throw new StateInvariantError("Can't create Position references");
      }
    }
    
    public String toString()
    {
      return "LeafElement(" + getName() + ") " + p0 + "," + p1 + "\n";
    }
    
    public int getStartOffset()
    {
      return p0.getOffset();
    }
    
    public int getEndOffset()
    {
      return p1.getOffset();
    }
    
    public String getName()
    {
      String str = super.getName();
      if (str == null) {
        str = "content";
      }
      return str;
    }
    
    public int getElementIndex(int paramInt)
    {
      return -1;
    }
    
    public Element getElement(int paramInt)
    {
      return null;
    }
    
    public int getElementCount()
    {
      return 0;
    }
    
    public boolean isLeaf()
    {
      return true;
    }
    
    public boolean getAllowsChildren()
    {
      return false;
    }
    
    public Enumeration children()
    {
      return null;
    }
    
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      paramObjectOutputStream.defaultWriteObject();
      paramObjectOutputStream.writeInt(p0.getOffset());
      paramObjectOutputStream.writeInt(p1.getOffset());
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws ClassNotFoundException, IOException
    {
      paramObjectInputStream.defaultReadObject();
      int i = paramObjectInputStream.readInt();
      int j = paramObjectInputStream.readInt();
      try
      {
        p0 = createPosition(i);
        p1 = createPosition(j);
      }
      catch (BadLocationException localBadLocationException)
      {
        p0 = null;
        p1 = null;
        throw new IOException("Can't restore Position references");
      }
    }
  }
  
  class UndoRedoDocumentEvent
    implements DocumentEvent
  {
    private AbstractDocument.DefaultDocumentEvent src = null;
    private DocumentEvent.EventType type = null;
    
    public UndoRedoDocumentEvent(AbstractDocument.DefaultDocumentEvent paramDefaultDocumentEvent, boolean paramBoolean)
    {
      src = paramDefaultDocumentEvent;
      if (paramBoolean)
      {
        if (paramDefaultDocumentEvent.getType().equals(DocumentEvent.EventType.INSERT)) {
          type = DocumentEvent.EventType.REMOVE;
        } else if (paramDefaultDocumentEvent.getType().equals(DocumentEvent.EventType.REMOVE)) {
          type = DocumentEvent.EventType.INSERT;
        } else {
          type = paramDefaultDocumentEvent.getType();
        }
      }
      else {
        type = paramDefaultDocumentEvent.getType();
      }
    }
    
    public AbstractDocument.DefaultDocumentEvent getSource()
    {
      return src;
    }
    
    public int getOffset()
    {
      return src.getOffset();
    }
    
    public int getLength()
    {
      return src.getLength();
    }
    
    public Document getDocument()
    {
      return src.getDocument();
    }
    
    public DocumentEvent.EventType getType()
    {
      return type;
    }
    
    public DocumentEvent.ElementChange getChange(Element paramElement)
    {
      return src.getChange(paramElement);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\AbstractDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */