package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.ListPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public class List
  extends Component
  implements ItemSelectable, Accessible
{
  Vector<String> items = new Vector();
  int rows = 0;
  boolean multipleMode = false;
  int[] selected = new int[0];
  int visibleIndex = -1;
  transient ActionListener actionListener;
  transient ItemListener itemListener;
  private static final String base = "list";
  private static int nameCounter = 0;
  private static final long serialVersionUID = -3304312411574666869L;
  static final int DEFAULT_VISIBLE_ROWS = 4;
  private int listSerializedDataVersion = 1;
  
  public List()
    throws HeadlessException
  {
    this(0, false);
  }
  
  public List(int paramInt)
    throws HeadlessException
  {
    this(paramInt, false);
  }
  
  public List(int paramInt, boolean paramBoolean)
    throws HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    rows = (paramInt != 0 ? paramInt : 4);
    multipleMode = paramBoolean;
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 8
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 198	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 397	java/lang/StringBuilder:<init>	()V
    //   12: ldc 7
    //   14: invokevirtual 400	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 330	java/awt/List:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 330	java/awt/List:nameCounter	I
    //   26: invokevirtual 399	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 398	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   32: aload_1
    //   33: monitorexit
    //   34: areturn
    //   35: astore_2
    //   36: aload_1
    //   37: monitorexit
    //   38: aload_2
    //   39: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	40	0	this	List
    //   3	34	1	Ljava/lang/Object;	Object
    //   35	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   5	34	35	finally
    //   35	38	35	finally
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      if (peer == null) {
        peer = getToolkit().createList(this);
      }
      super.addNotify();
    }
  }
  
  public void removeNotify()
  {
    synchronized (getTreeLock())
    {
      ListPeer localListPeer = (ListPeer)peer;
      if (localListPeer != null) {
        selected = localListPeer.getSelectedIndexes();
      }
      super.removeNotify();
    }
  }
  
  public int getItemCount()
  {
    return countItems();
  }
  
  @Deprecated
  public int countItems()
  {
    return items.size();
  }
  
  public String getItem(int paramInt)
  {
    return getItemImpl(paramInt);
  }
  
  final String getItemImpl(int paramInt)
  {
    return (String)items.elementAt(paramInt);
  }
  
  public synchronized String[] getItems()
  {
    String[] arrayOfString = new String[items.size()];
    items.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  public void add(String paramString)
  {
    addItem(paramString);
  }
  
  @Deprecated
  public void addItem(String paramString)
  {
    addItem(paramString, -1);
  }
  
  public void add(String paramString, int paramInt)
  {
    addItem(paramString, paramInt);
  }
  
  @Deprecated
  public synchronized void addItem(String paramString, int paramInt)
  {
    if ((paramInt < -1) || (paramInt >= items.size())) {
      paramInt = -1;
    }
    if (paramString == null) {
      paramString = "";
    }
    if (paramInt == -1) {
      items.addElement(paramString);
    } else {
      items.insertElementAt(paramString, paramInt);
    }
    ListPeer localListPeer = (ListPeer)peer;
    if (localListPeer != null) {
      localListPeer.add(paramString, paramInt);
    }
  }
  
  public synchronized void replaceItem(String paramString, int paramInt)
  {
    remove(paramInt);
    add(paramString, paramInt);
  }
  
  public void removeAll()
  {
    clear();
  }
  
  @Deprecated
  public synchronized void clear()
  {
    ListPeer localListPeer = (ListPeer)peer;
    if (localListPeer != null) {
      localListPeer.removeAll();
    }
    items = new Vector();
    selected = new int[0];
  }
  
  public synchronized void remove(String paramString)
  {
    int i = items.indexOf(paramString);
    if (i < 0) {
      throw new IllegalArgumentException("item " + paramString + " not found in list");
    }
    remove(i);
  }
  
  public void remove(int paramInt)
  {
    delItem(paramInt);
  }
  
  @Deprecated
  public void delItem(int paramInt)
  {
    delItems(paramInt, paramInt);
  }
  
  public synchronized int getSelectedIndex()
  {
    int[] arrayOfInt = getSelectedIndexes();
    return arrayOfInt.length == 1 ? arrayOfInt[0] : -1;
  }
  
  public synchronized int[] getSelectedIndexes()
  {
    ListPeer localListPeer = (ListPeer)peer;
    if (localListPeer != null) {
      selected = localListPeer.getSelectedIndexes();
    }
    return (int[])selected.clone();
  }
  
  public synchronized String getSelectedItem()
  {
    int i = getSelectedIndex();
    return i < 0 ? null : getItem(i);
  }
  
  public synchronized String[] getSelectedItems()
  {
    int[] arrayOfInt = getSelectedIndexes();
    String[] arrayOfString = new String[arrayOfInt.length];
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfString[i] = getItem(arrayOfInt[i]);
    }
    return arrayOfString;
  }
  
  public Object[] getSelectedObjects()
  {
    return getSelectedItems();
  }
  
  public void select(int paramInt)
  {
    ListPeer localListPeer;
    do
    {
      localListPeer = (ListPeer)peer;
      if (localListPeer != null)
      {
        localListPeer.select(paramInt);
        return;
      }
      synchronized (this)
      {
        int i = 0;
        for (int j = 0; j < selected.length; j++) {
          if (selected[j] == paramInt)
          {
            i = 1;
            break;
          }
        }
        if (i == 0) {
          if (!multipleMode)
          {
            selected = new int[1];
            selected[0] = paramInt;
          }
          else
          {
            int[] arrayOfInt = new int[selected.length + 1];
            System.arraycopy(selected, 0, arrayOfInt, 0, selected.length);
            arrayOfInt[selected.length] = paramInt;
            selected = arrayOfInt;
          }
        }
      }
    } while (localListPeer != peer);
  }
  
  public synchronized void deselect(int paramInt)
  {
    ListPeer localListPeer = (ListPeer)peer;
    if ((localListPeer != null) && ((isMultipleMode()) || (getSelectedIndex() == paramInt))) {
      localListPeer.deselect(paramInt);
    }
    for (int i = 0; i < selected.length; i++) {
      if (selected[i] == paramInt)
      {
        int[] arrayOfInt = new int[selected.length - 1];
        System.arraycopy(selected, 0, arrayOfInt, 0, i);
        System.arraycopy(selected, i + 1, arrayOfInt, i, selected.length - (i + 1));
        selected = arrayOfInt;
        return;
      }
    }
  }
  
  public boolean isIndexSelected(int paramInt)
  {
    return isSelected(paramInt);
  }
  
  @Deprecated
  public boolean isSelected(int paramInt)
  {
    int[] arrayOfInt = getSelectedIndexes();
    for (int i = 0; i < arrayOfInt.length; i++) {
      if (arrayOfInt[i] == paramInt) {
        return true;
      }
    }
    return false;
  }
  
  public int getRows()
  {
    return rows;
  }
  
  public boolean isMultipleMode()
  {
    return allowsMultipleSelections();
  }
  
  @Deprecated
  public boolean allowsMultipleSelections()
  {
    return multipleMode;
  }
  
  public void setMultipleMode(boolean paramBoolean)
  {
    setMultipleSelections(paramBoolean);
  }
  
  @Deprecated
  public synchronized void setMultipleSelections(boolean paramBoolean)
  {
    if (paramBoolean != multipleMode)
    {
      multipleMode = paramBoolean;
      ListPeer localListPeer = (ListPeer)peer;
      if (localListPeer != null) {
        localListPeer.setMultipleMode(paramBoolean);
      }
    }
  }
  
  public int getVisibleIndex()
  {
    return visibleIndex;
  }
  
  public synchronized void makeVisible(int paramInt)
  {
    visibleIndex = paramInt;
    ListPeer localListPeer = (ListPeer)peer;
    if (localListPeer != null) {
      localListPeer.makeVisible(paramInt);
    }
  }
  
  public Dimension getPreferredSize(int paramInt)
  {
    return preferredSize(paramInt);
  }
  
  @Deprecated
  public Dimension preferredSize(int paramInt)
  {
    synchronized (getTreeLock())
    {
      ListPeer localListPeer = (ListPeer)peer;
      return localListPeer != null ? localListPeer.getPreferredSize(paramInt) : super.preferredSize();
    }
  }
  
  public Dimension getPreferredSize()
  {
    return preferredSize();
  }
  
  @Deprecated
  public Dimension preferredSize()
  {
    synchronized (getTreeLock())
    {
      return rows > 0 ? preferredSize(rows) : super.preferredSize();
    }
  }
  
  public Dimension getMinimumSize(int paramInt)
  {
    return minimumSize(paramInt);
  }
  
  @Deprecated
  public Dimension minimumSize(int paramInt)
  {
    synchronized (getTreeLock())
    {
      ListPeer localListPeer = (ListPeer)peer;
      return localListPeer != null ? localListPeer.getMinimumSize(paramInt) : super.minimumSize();
    }
  }
  
  public Dimension getMinimumSize()
  {
    return minimumSize();
  }
  
  @Deprecated
  public Dimension minimumSize()
  {
    synchronized (getTreeLock())
    {
      return rows > 0 ? minimumSize(rows) : super.minimumSize();
    }
  }
  
  public synchronized void addItemListener(ItemListener paramItemListener)
  {
    if (paramItemListener == null) {
      return;
    }
    itemListener = AWTEventMulticaster.add(itemListener, paramItemListener);
    newEventsOnly = true;
  }
  
  public synchronized void removeItemListener(ItemListener paramItemListener)
  {
    if (paramItemListener == null) {
      return;
    }
    itemListener = AWTEventMulticaster.remove(itemListener, paramItemListener);
  }
  
  public synchronized ItemListener[] getItemListeners()
  {
    return (ItemListener[])getListeners(ItemListener.class);
  }
  
  public synchronized void addActionListener(ActionListener paramActionListener)
  {
    if (paramActionListener == null) {
      return;
    }
    actionListener = AWTEventMulticaster.add(actionListener, paramActionListener);
    newEventsOnly = true;
  }
  
  public synchronized void removeActionListener(ActionListener paramActionListener)
  {
    if (paramActionListener == null) {
      return;
    }
    actionListener = AWTEventMulticaster.remove(actionListener, paramActionListener);
  }
  
  public synchronized ActionListener[] getActionListeners()
  {
    return (ActionListener[])getListeners(ActionListener.class);
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    Object localObject = null;
    if (paramClass == ActionListener.class) {
      localObject = actionListener;
    } else if (paramClass == ItemListener.class) {
      localObject = itemListener;
    } else {
      return super.getListeners(paramClass);
    }
    return AWTEventMulticaster.getListeners((EventListener)localObject, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent)
  {
    switch (id)
    {
    case 1001: 
      return ((eventMask & 0x80) != 0L) || (actionListener != null);
    case 701: 
      return ((eventMask & 0x200) != 0L) || (itemListener != null);
    }
    return super.eventEnabled(paramAWTEvent);
  }
  
  protected void processEvent(AWTEvent paramAWTEvent)
  {
    if ((paramAWTEvent instanceof ItemEvent))
    {
      processItemEvent((ItemEvent)paramAWTEvent);
      return;
    }
    if ((paramAWTEvent instanceof ActionEvent))
    {
      processActionEvent((ActionEvent)paramAWTEvent);
      return;
    }
    super.processEvent(paramAWTEvent);
  }
  
  protected void processItemEvent(ItemEvent paramItemEvent)
  {
    ItemListener localItemListener = itemListener;
    if (localItemListener != null) {
      localItemListener.itemStateChanged(paramItemEvent);
    }
  }
  
  protected void processActionEvent(ActionEvent paramActionEvent)
  {
    ActionListener localActionListener = actionListener;
    if (localActionListener != null) {
      localActionListener.actionPerformed(paramActionEvent);
    }
  }
  
  protected String paramString()
  {
    return super.paramString() + ",selected=" + getSelectedItem();
  }
  
  @Deprecated
  public synchronized void delItems(int paramInt1, int paramInt2)
  {
    for (int i = paramInt2; i >= paramInt1; i--) {
      items.removeElementAt(i);
    }
    ListPeer localListPeer = (ListPeer)peer;
    if (localListPeer != null) {
      localListPeer.delItems(paramInt1, paramInt2);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    synchronized (this)
    {
      ListPeer localListPeer = (ListPeer)peer;
      if (localListPeer != null) {
        selected = localListPeer.getSelectedIndexes();
      }
    }
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "itemL", itemListener);
    AWTEventMulticaster.save(paramObjectOutputStream, "actionL", actionListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    paramObjectInputStream.defaultReadObject();
    Object localObject;
    while (null != (localObject = paramObjectInputStream.readObject()))
    {
      String str = ((String)localObject).intern();
      if ("itemL" == str) {
        addItemListener((ItemListener)paramObjectInputStream.readObject());
      } else if ("actionL" == str) {
        addActionListener((ActionListener)paramObjectInputStream.readObject());
      } else {
        paramObjectInputStream.readObject();
      }
    }
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTList();
    }
    return accessibleContext;
  }
  
  protected class AccessibleAWTList
    extends Component.AccessibleAWTComponent
    implements AccessibleSelection, ItemListener, ActionListener
  {
    private static final long serialVersionUID = 7924617370136012829L;
    
    public AccessibleAWTList()
    {
      super();
      addActionListener(this);
      addItemListener(this);
    }
    
    public void actionPerformed(ActionEvent paramActionEvent) {}
    
    public void itemStateChanged(ItemEvent paramItemEvent) {}
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (isMultipleMode()) {
        localAccessibleStateSet.add(AccessibleState.MULTISELECTABLE);
      }
      return localAccessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.LIST;
    }
    
    public Accessible getAccessibleAt(Point paramPoint)
    {
      return null;
    }
    
    public int getAccessibleChildrenCount()
    {
      return getItemCount();
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      synchronized (List.this)
      {
        if (paramInt >= getItemCount()) {
          return null;
        }
        return new AccessibleAWTListChild(List.this, paramInt);
      }
    }
    
    public AccessibleSelection getAccessibleSelection()
    {
      return this;
    }
    
    public int getAccessibleSelectionCount()
    {
      return getSelectedIndexes().length;
    }
    
    public Accessible getAccessibleSelection(int paramInt)
    {
      synchronized (List.this)
      {
        int i = getAccessibleSelectionCount();
        if ((paramInt < 0) || (paramInt >= i)) {
          return null;
        }
        return getAccessibleChild(getSelectedIndexes()[paramInt]);
      }
    }
    
    public boolean isAccessibleChildSelected(int paramInt)
    {
      return isIndexSelected(paramInt);
    }
    
    public void addAccessibleSelection(int paramInt)
    {
      select(paramInt);
    }
    
    public void removeAccessibleSelection(int paramInt)
    {
      deselect(paramInt);
    }
    
    public void clearAccessibleSelection()
    {
      synchronized (List.this)
      {
        int[] arrayOfInt = getSelectedIndexes();
        if (arrayOfInt == null) {
          return;
        }
        for (int i = arrayOfInt.length - 1; i >= 0; i--) {
          deselect(arrayOfInt[i]);
        }
      }
    }
    
    public void selectAllAccessibleSelection()
    {
      synchronized (List.this)
      {
        for (int i = getItemCount() - 1; i >= 0; i--) {
          select(i);
        }
      }
    }
    
    protected class AccessibleAWTListChild
      extends Component.AccessibleAWTComponent
      implements Accessible
    {
      private static final long serialVersionUID = 4412022926028300317L;
      private List parent;
      private int indexInParent;
      
      public AccessibleAWTListChild(List paramList, int paramInt)
      {
        super();
        parent = paramList;
        setAccessibleParent(paramList);
        indexInParent = paramInt;
      }
      
      public AccessibleContext getAccessibleContext()
      {
        return this;
      }
      
      public AccessibleRole getAccessibleRole()
      {
        return AccessibleRole.LIST_ITEM;
      }
      
      public AccessibleStateSet getAccessibleStateSet()
      {
        AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
        if (parent.isIndexSelected(indexInParent)) {
          localAccessibleStateSet.add(AccessibleState.SELECTED);
        }
        return localAccessibleStateSet;
      }
      
      public Locale getLocale()
      {
        return parent.getLocale();
      }
      
      public int getAccessibleIndexInParent()
      {
        return indexInParent;
      }
      
      public int getAccessibleChildrenCount()
      {
        return 0;
      }
      
      public Accessible getAccessibleChild(int paramInt)
      {
        return null;
      }
      
      public Color getBackground()
      {
        return parent.getBackground();
      }
      
      public void setBackground(Color paramColor)
      {
        parent.setBackground(paramColor);
      }
      
      public Color getForeground()
      {
        return parent.getForeground();
      }
      
      public void setForeground(Color paramColor)
      {
        parent.setForeground(paramColor);
      }
      
      public Cursor getCursor()
      {
        return parent.getCursor();
      }
      
      public void setCursor(Cursor paramCursor)
      {
        parent.setCursor(paramCursor);
      }
      
      public Font getFont()
      {
        return parent.getFont();
      }
      
      public void setFont(Font paramFont)
      {
        parent.setFont(paramFont);
      }
      
      public FontMetrics getFontMetrics(Font paramFont)
      {
        return parent.getFontMetrics(paramFont);
      }
      
      public boolean isEnabled()
      {
        return parent.isEnabled();
      }
      
      public void setEnabled(boolean paramBoolean)
      {
        parent.setEnabled(paramBoolean);
      }
      
      public boolean isVisible()
      {
        return false;
      }
      
      public void setVisible(boolean paramBoolean)
      {
        parent.setVisible(paramBoolean);
      }
      
      public boolean isShowing()
      {
        return false;
      }
      
      public boolean contains(Point paramPoint)
      {
        return false;
      }
      
      public Point getLocationOnScreen()
      {
        return null;
      }
      
      public Point getLocation()
      {
        return null;
      }
      
      public void setLocation(Point paramPoint) {}
      
      public Rectangle getBounds()
      {
        return null;
      }
      
      public void setBounds(Rectangle paramRectangle) {}
      
      public Dimension getSize()
      {
        return null;
      }
      
      public void setSize(Dimension paramDimension) {}
      
      public Accessible getAccessibleAt(Point paramPoint)
      {
        return null;
      }
      
      public boolean isFocusTraversable()
      {
        return false;
      }
      
      public void requestFocus() {}
      
      public void addFocusListener(FocusListener paramFocusListener) {}
      
      public void removeFocusListener(FocusListener paramFocusListener) {}
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\List.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */