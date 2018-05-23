package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ListUI;
import javax.swing.text.Position.Bias;
import sun.swing.SwingUtilities2;
import sun.swing.SwingUtilities2.Section;

public class JList<E>
  extends JComponent
  implements Scrollable, Accessible
{
  private static final String uiClassID = "ListUI";
  public static final int VERTICAL = 0;
  public static final int VERTICAL_WRAP = 1;
  public static final int HORIZONTAL_WRAP = 2;
  private int fixedCellWidth = -1;
  private int fixedCellHeight = -1;
  private int horizontalScrollIncrement = -1;
  private E prototypeCellValue;
  private int visibleRowCount = 8;
  private Color selectionForeground;
  private Color selectionBackground;
  private boolean dragEnabled;
  private ListSelectionModel selectionModel;
  private ListModel<E> dataModel;
  private ListCellRenderer<? super E> cellRenderer;
  private ListSelectionListener selectionListener;
  private int layoutOrientation;
  private DropMode dropMode = DropMode.USE_SELECTION;
  private transient DropLocation dropLocation;
  
  public JList(ListModel<E> paramListModel)
  {
    if (paramListModel == null) {
      throw new IllegalArgumentException("dataModel must be non null");
    }
    ToolTipManager localToolTipManager = ToolTipManager.sharedInstance();
    localToolTipManager.registerComponent(this);
    layoutOrientation = 0;
    dataModel = paramListModel;
    selectionModel = createSelectionModel();
    setAutoscrolls(true);
    setOpaque(true);
    updateUI();
  }
  
  public JList(E[] paramArrayOfE)
  {
    this(new AbstractListModel()
    {
      public int getSize()
      {
        return JList.this.length;
      }
      
      public E getElementAt(int paramAnonymousInt)
      {
        return JList.this[paramAnonymousInt];
      }
    });
  }
  
  public JList(Vector<? extends E> paramVector)
  {
    this(new AbstractListModel()
    {
      public int getSize()
      {
        return size();
      }
      
      public E getElementAt(int paramAnonymousInt)
      {
        return (E)elementAt(paramAnonymousInt);
      }
    });
  }
  
  public JList()
  {
    this(new AbstractListModel()
    {
      public int getSize()
      {
        return 0;
      }
      
      public E getElementAt(int paramAnonymousInt)
      {
        throw new IndexOutOfBoundsException("No Data Model");
      }
    });
  }
  
  public ListUI getUI()
  {
    return (ListUI)ui;
  }
  
  public void setUI(ListUI paramListUI)
  {
    super.setUI(paramListUI);
  }
  
  public void updateUI()
  {
    setUI((ListUI)UIManager.getUI(this));
    ListCellRenderer localListCellRenderer = getCellRenderer();
    if ((localListCellRenderer instanceof Component)) {
      SwingUtilities.updateComponentTreeUI((Component)localListCellRenderer);
    }
  }
  
  public String getUIClassID()
  {
    return "ListUI";
  }
  
  private void updateFixedCellSize()
  {
    ListCellRenderer localListCellRenderer = getCellRenderer();
    Object localObject = getPrototypeCellValue();
    if ((localListCellRenderer != null) && (localObject != null))
    {
      Component localComponent = localListCellRenderer.getListCellRendererComponent(this, localObject, 0, false, false);
      Font localFont = localComponent.getFont();
      localComponent.setFont(getFont());
      Dimension localDimension = localComponent.getPreferredSize();
      fixedCellWidth = width;
      fixedCellHeight = height;
      localComponent.setFont(localFont);
    }
  }
  
  public E getPrototypeCellValue()
  {
    return (E)prototypeCellValue;
  }
  
  public void setPrototypeCellValue(E paramE)
  {
    Object localObject = prototypeCellValue;
    prototypeCellValue = paramE;
    if ((paramE != null) && (!paramE.equals(localObject))) {
      updateFixedCellSize();
    }
    firePropertyChange("prototypeCellValue", localObject, paramE);
  }
  
  public int getFixedCellWidth()
  {
    return fixedCellWidth;
  }
  
  public void setFixedCellWidth(int paramInt)
  {
    int i = fixedCellWidth;
    fixedCellWidth = paramInt;
    firePropertyChange("fixedCellWidth", i, fixedCellWidth);
  }
  
  public int getFixedCellHeight()
  {
    return fixedCellHeight;
  }
  
  public void setFixedCellHeight(int paramInt)
  {
    int i = fixedCellHeight;
    fixedCellHeight = paramInt;
    firePropertyChange("fixedCellHeight", i, fixedCellHeight);
  }
  
  @Transient
  public ListCellRenderer<? super E> getCellRenderer()
  {
    return cellRenderer;
  }
  
  public void setCellRenderer(ListCellRenderer<? super E> paramListCellRenderer)
  {
    ListCellRenderer localListCellRenderer = cellRenderer;
    cellRenderer = paramListCellRenderer;
    if ((paramListCellRenderer != null) && (!paramListCellRenderer.equals(localListCellRenderer))) {
      updateFixedCellSize();
    }
    firePropertyChange("cellRenderer", localListCellRenderer, paramListCellRenderer);
  }
  
  public Color getSelectionForeground()
  {
    return selectionForeground;
  }
  
  public void setSelectionForeground(Color paramColor)
  {
    Color localColor = selectionForeground;
    selectionForeground = paramColor;
    firePropertyChange("selectionForeground", localColor, paramColor);
  }
  
  public Color getSelectionBackground()
  {
    return selectionBackground;
  }
  
  public void setSelectionBackground(Color paramColor)
  {
    Color localColor = selectionBackground;
    selectionBackground = paramColor;
    firePropertyChange("selectionBackground", localColor, paramColor);
  }
  
  public int getVisibleRowCount()
  {
    return visibleRowCount;
  }
  
  public void setVisibleRowCount(int paramInt)
  {
    int i = visibleRowCount;
    visibleRowCount = Math.max(0, paramInt);
    firePropertyChange("visibleRowCount", i, paramInt);
  }
  
  public int getLayoutOrientation()
  {
    return layoutOrientation;
  }
  
  public void setLayoutOrientation(int paramInt)
  {
    int i = layoutOrientation;
    switch (paramInt)
    {
    case 0: 
    case 1: 
    case 2: 
      layoutOrientation = paramInt;
      firePropertyChange("layoutOrientation", i, paramInt);
      break;
    default: 
      throw new IllegalArgumentException("layoutOrientation must be one of: VERTICAL, HORIZONTAL_WRAP or VERTICAL_WRAP");
    }
  }
  
  public int getFirstVisibleIndex()
  {
    Rectangle localRectangle1 = getVisibleRect();
    int i;
    if (getComponentOrientation().isLeftToRight()) {
      i = locationToIndex(localRectangle1.getLocation());
    } else {
      i = locationToIndex(new Point(x + width - 1, y));
    }
    if (i != -1)
    {
      Rectangle localRectangle2 = getCellBounds(i, i);
      if (localRectangle2 != null)
      {
        SwingUtilities.computeIntersection(x, y, width, height, localRectangle2);
        if ((width == 0) || (height == 0)) {
          i = -1;
        }
      }
    }
    return i;
  }
  
  public int getLastVisibleIndex()
  {
    boolean bool = getComponentOrientation().isLeftToRight();
    Rectangle localRectangle1 = getVisibleRect();
    Point localPoint1;
    if (bool) {
      localPoint1 = new Point(x + width - 1, y + height - 1);
    } else {
      localPoint1 = new Point(x, y + height - 1);
    }
    int i = locationToIndex(localPoint1);
    if (i != -1)
    {
      Rectangle localRectangle2 = getCellBounds(i, i);
      if (localRectangle2 != null)
      {
        SwingUtilities.computeIntersection(x, y, width, height, localRectangle2);
        if ((width == 0) || (height == 0))
        {
          int j = getLayoutOrientation() == 2 ? 1 : 0;
          Point localPoint2 = j != 0 ? new Point(x, y) : new Point(x, y);
          int m = -1;
          int n = i;
          i = -1;
          int k;
          do
          {
            k = m;
            m = locationToIndex(localPoint2);
            if (m != -1)
            {
              localRectangle2 = getCellBounds(m, m);
              if ((m != n) && (localRectangle2 != null) && (localRectangle2.contains(localPoint2)))
              {
                i = m;
                if (j != 0)
                {
                  y = (y + height);
                  if (y >= y) {
                    k = m;
                  }
                }
                else
                {
                  x = (x + width);
                  if (x >= x) {
                    k = m;
                  }
                }
              }
              else
              {
                k = m;
              }
            }
          } while ((m != -1) && (k != m));
        }
      }
    }
    return i;
  }
  
  public void ensureIndexIsVisible(int paramInt)
  {
    Rectangle localRectangle = getCellBounds(paramInt, paramInt);
    if (localRectangle != null) {
      scrollRectToVisible(localRectangle);
    }
  }
  
  public void setDragEnabled(boolean paramBoolean)
  {
    if ((paramBoolean) && (GraphicsEnvironment.isHeadless())) {
      throw new HeadlessException();
    }
    dragEnabled = paramBoolean;
  }
  
  public boolean getDragEnabled()
  {
    return dragEnabled;
  }
  
  public final void setDropMode(DropMode paramDropMode)
  {
    if (paramDropMode != null) {
      switch (paramDropMode)
      {
      case USE_SELECTION: 
      case ON: 
      case INSERT: 
      case ON_OR_INSERT: 
        dropMode = paramDropMode;
        return;
      }
    }
    throw new IllegalArgumentException(paramDropMode + ": Unsupported drop mode for list");
  }
  
  public final DropMode getDropMode()
  {
    return dropMode;
  }
  
  DropLocation dropLocationForPoint(Point paramPoint)
  {
    DropLocation localDropLocation = null;
    Rectangle localRectangle = null;
    int i = locationToIndex(paramPoint);
    if (i != -1) {
      localRectangle = getCellBounds(i, i);
    }
    boolean bool1;
    switch (dropMode)
    {
    case USE_SELECTION: 
    case ON: 
      localDropLocation = new DropLocation(paramPoint, (localRectangle != null) && (localRectangle.contains(paramPoint)) ? i : -1, false, null);
      break;
    case INSERT: 
      if (i == -1)
      {
        localDropLocation = new DropLocation(paramPoint, getModel().getSize(), true, null);
      }
      else
      {
        if (layoutOrientation == 2)
        {
          bool1 = getComponentOrientation().isLeftToRight();
          if (SwingUtilities2.liesInHorizontal(localRectangle, paramPoint, bool1, false) == SwingUtilities2.Section.TRAILING) {
            i++;
          } else if ((i == getModel().getSize() - 1) && (y >= y + height)) {
            i++;
          }
        }
        else if (SwingUtilities2.liesInVertical(localRectangle, paramPoint, false) == SwingUtilities2.Section.TRAILING)
        {
          i++;
        }
        localDropLocation = new DropLocation(paramPoint, i, true, null);
      }
      break;
    case ON_OR_INSERT: 
      if (i == -1)
      {
        localDropLocation = new DropLocation(paramPoint, getModel().getSize(), true, null);
      }
      else
      {
        bool1 = false;
        if (layoutOrientation == 2)
        {
          boolean bool2 = getComponentOrientation().isLeftToRight();
          SwingUtilities2.Section localSection2 = SwingUtilities2.liesInHorizontal(localRectangle, paramPoint, bool2, true);
          if (localSection2 == SwingUtilities2.Section.TRAILING)
          {
            i++;
            bool1 = true;
          }
          else if ((i == getModel().getSize() - 1) && (y >= y + height))
          {
            i++;
            bool1 = true;
          }
          else if (localSection2 == SwingUtilities2.Section.LEADING)
          {
            bool1 = true;
          }
        }
        else
        {
          SwingUtilities2.Section localSection1 = SwingUtilities2.liesInVertical(localRectangle, paramPoint, true);
          if (localSection1 == SwingUtilities2.Section.LEADING)
          {
            bool1 = true;
          }
          else if (localSection1 == SwingUtilities2.Section.TRAILING)
          {
            i++;
            bool1 = true;
          }
        }
        localDropLocation = new DropLocation(paramPoint, i, bool1, null);
      }
      break;
    default: 
      if (!$assertionsDisabled) {
        throw new AssertionError("Unexpected drop mode");
      }
      break;
    }
    return localDropLocation;
  }
  
  Object setDropLocation(TransferHandler.DropLocation paramDropLocation, Object paramObject, boolean paramBoolean)
  {
    Object localObject = null;
    DropLocation localDropLocation1 = (DropLocation)paramDropLocation;
    if (dropMode == DropMode.USE_SELECTION) {
      if (localDropLocation1 == null)
      {
        if ((!paramBoolean) && (paramObject != null))
        {
          setSelectedIndices(((int[][])(int[][])paramObject)[0]);
          int i = ((int[][])(int[][])paramObject)[1][0];
          int k = ((int[][])(int[][])paramObject)[1][1];
          SwingUtilities2.setLeadAnchorWithoutSelection(getSelectionModel(), k, i);
        }
      }
      else
      {
        if (dropLocation == null)
        {
          int[] arrayOfInt = getSelectedIndices();
          localObject = new int[][] { arrayOfInt, { getAnchorSelectionIndex(), getLeadSelectionIndex() } };
        }
        else
        {
          localObject = paramObject;
        }
        int j = localDropLocation1.getIndex();
        if (j == -1)
        {
          clearSelection();
          getSelectionModel().setAnchorSelectionIndex(-1);
          getSelectionModel().setLeadSelectionIndex(-1);
        }
        else
        {
          setSelectionInterval(j, j);
        }
      }
    }
    DropLocation localDropLocation2 = dropLocation;
    dropLocation = localDropLocation1;
    firePropertyChange("dropLocation", localDropLocation2, dropLocation);
    return localObject;
  }
  
  public final DropLocation getDropLocation()
  {
    return dropLocation;
  }
  
  public int getNextMatch(String paramString, int paramInt, Position.Bias paramBias)
  {
    ListModel localListModel = getModel();
    int i = localListModel.getSize();
    if (paramString == null) {
      throw new IllegalArgumentException();
    }
    if ((paramInt < 0) || (paramInt >= i)) {
      throw new IllegalArgumentException();
    }
    paramString = paramString.toUpperCase();
    int j = paramBias == Position.Bias.Forward ? 1 : -1;
    int k = paramInt;
    do
    {
      Object localObject = localListModel.getElementAt(k);
      if (localObject != null)
      {
        String str;
        if ((localObject instanceof String))
        {
          str = ((String)localObject).toUpperCase();
        }
        else
        {
          str = localObject.toString();
          if (str != null) {
            str = str.toUpperCase();
          }
        }
        if ((str != null) && (str.startsWith(paramString))) {
          return k;
        }
      }
      k = (k + j + i) % i;
    } while (k != paramInt);
    return -1;
  }
  
  public String getToolTipText(MouseEvent paramMouseEvent)
  {
    if (paramMouseEvent != null)
    {
      Point localPoint = paramMouseEvent.getPoint();
      int i = locationToIndex(localPoint);
      ListCellRenderer localListCellRenderer = getCellRenderer();
      Rectangle localRectangle;
      if ((i != -1) && (localListCellRenderer != null) && ((localRectangle = getCellBounds(i, i)) != null) && (localRectangle.contains(x, y)))
      {
        ListSelectionModel localListSelectionModel = getSelectionModel();
        Component localComponent = localListCellRenderer.getListCellRendererComponent(this, getModel().getElementAt(i), i, localListSelectionModel.isSelectedIndex(i), (hasFocus()) && (localListSelectionModel.getLeadSelectionIndex() == i));
        if ((localComponent instanceof JComponent))
        {
          localPoint.translate(-x, -y);
          MouseEvent localMouseEvent = new MouseEvent(localComponent, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), x, y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
          String str = ((JComponent)localComponent).getToolTipText(localMouseEvent);
          if (str != null) {
            return str;
          }
        }
      }
    }
    return super.getToolTipText();
  }
  
  public int locationToIndex(Point paramPoint)
  {
    ListUI localListUI = getUI();
    return localListUI != null ? localListUI.locationToIndex(this, paramPoint) : -1;
  }
  
  public Point indexToLocation(int paramInt)
  {
    ListUI localListUI = getUI();
    return localListUI != null ? localListUI.indexToLocation(this, paramInt) : null;
  }
  
  public Rectangle getCellBounds(int paramInt1, int paramInt2)
  {
    ListUI localListUI = getUI();
    return localListUI != null ? localListUI.getCellBounds(this, paramInt1, paramInt2) : null;
  }
  
  public ListModel<E> getModel()
  {
    return dataModel;
  }
  
  public void setModel(ListModel<E> paramListModel)
  {
    if (paramListModel == null) {
      throw new IllegalArgumentException("model must be non null");
    }
    ListModel localListModel = dataModel;
    dataModel = paramListModel;
    firePropertyChange("model", localListModel, dataModel);
    clearSelection();
  }
  
  public void setListData(final E[] paramArrayOfE)
  {
    setModel(new AbstractListModel()
    {
      public int getSize()
      {
        return paramArrayOfE.length;
      }
      
      public E getElementAt(int paramAnonymousInt)
      {
        return (E)paramArrayOfE[paramAnonymousInt];
      }
    });
  }
  
  public void setListData(final Vector<? extends E> paramVector)
  {
    setModel(new AbstractListModel()
    {
      public int getSize()
      {
        return paramVector.size();
      }
      
      public E getElementAt(int paramAnonymousInt)
      {
        return (E)paramVector.elementAt(paramAnonymousInt);
      }
    });
  }
  
  protected ListSelectionModel createSelectionModel()
  {
    return new DefaultListSelectionModel();
  }
  
  public ListSelectionModel getSelectionModel()
  {
    return selectionModel;
  }
  
  protected void fireSelectionValueChanged(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    ListSelectionEvent localListSelectionEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ListSelectionListener.class)
      {
        if (localListSelectionEvent == null) {
          localListSelectionEvent = new ListSelectionEvent(this, paramInt1, paramInt2, paramBoolean);
        }
        ((ListSelectionListener)arrayOfObject[(i + 1)]).valueChanged(localListSelectionEvent);
      }
    }
  }
  
  public void addListSelectionListener(ListSelectionListener paramListSelectionListener)
  {
    if (selectionListener == null)
    {
      selectionListener = new ListSelectionHandler(null);
      getSelectionModel().addListSelectionListener(selectionListener);
    }
    listenerList.add(ListSelectionListener.class, paramListSelectionListener);
  }
  
  public void removeListSelectionListener(ListSelectionListener paramListSelectionListener)
  {
    listenerList.remove(ListSelectionListener.class, paramListSelectionListener);
  }
  
  public ListSelectionListener[] getListSelectionListeners()
  {
    return (ListSelectionListener[])listenerList.getListeners(ListSelectionListener.class);
  }
  
  public void setSelectionModel(ListSelectionModel paramListSelectionModel)
  {
    if (paramListSelectionModel == null) {
      throw new IllegalArgumentException("selectionModel must be non null");
    }
    if (selectionListener != null)
    {
      selectionModel.removeListSelectionListener(selectionListener);
      paramListSelectionModel.addListSelectionListener(selectionListener);
    }
    ListSelectionModel localListSelectionModel = selectionModel;
    selectionModel = paramListSelectionModel;
    firePropertyChange("selectionModel", localListSelectionModel, paramListSelectionModel);
  }
  
  public void setSelectionMode(int paramInt)
  {
    getSelectionModel().setSelectionMode(paramInt);
  }
  
  public int getSelectionMode()
  {
    return getSelectionModel().getSelectionMode();
  }
  
  public int getAnchorSelectionIndex()
  {
    return getSelectionModel().getAnchorSelectionIndex();
  }
  
  public int getLeadSelectionIndex()
  {
    return getSelectionModel().getLeadSelectionIndex();
  }
  
  public int getMinSelectionIndex()
  {
    return getSelectionModel().getMinSelectionIndex();
  }
  
  public int getMaxSelectionIndex()
  {
    return getSelectionModel().getMaxSelectionIndex();
  }
  
  public boolean isSelectedIndex(int paramInt)
  {
    return getSelectionModel().isSelectedIndex(paramInt);
  }
  
  public boolean isSelectionEmpty()
  {
    return getSelectionModel().isSelectionEmpty();
  }
  
  public void clearSelection()
  {
    getSelectionModel().clearSelection();
  }
  
  public void setSelectionInterval(int paramInt1, int paramInt2)
  {
    getSelectionModel().setSelectionInterval(paramInt1, paramInt2);
  }
  
  public void addSelectionInterval(int paramInt1, int paramInt2)
  {
    getSelectionModel().addSelectionInterval(paramInt1, paramInt2);
  }
  
  public void removeSelectionInterval(int paramInt1, int paramInt2)
  {
    getSelectionModel().removeSelectionInterval(paramInt1, paramInt2);
  }
  
  public void setValueIsAdjusting(boolean paramBoolean)
  {
    getSelectionModel().setValueIsAdjusting(paramBoolean);
  }
  
  public boolean getValueIsAdjusting()
  {
    return getSelectionModel().getValueIsAdjusting();
  }
  
  @Transient
  public int[] getSelectedIndices()
  {
    ListSelectionModel localListSelectionModel = getSelectionModel();
    int i = localListSelectionModel.getMinSelectionIndex();
    int j = localListSelectionModel.getMaxSelectionIndex();
    if ((i < 0) || (j < 0)) {
      return new int[0];
    }
    int[] arrayOfInt1 = new int[1 + (j - i)];
    int k = 0;
    for (int m = i; m <= j; m++) {
      if (localListSelectionModel.isSelectedIndex(m)) {
        arrayOfInt1[(k++)] = m;
      }
    }
    int[] arrayOfInt2 = new int[k];
    System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, k);
    return arrayOfInt2;
  }
  
  public void setSelectedIndex(int paramInt)
  {
    if (paramInt >= getModel().getSize()) {
      return;
    }
    getSelectionModel().setSelectionInterval(paramInt, paramInt);
  }
  
  public void setSelectedIndices(int[] paramArrayOfInt)
  {
    ListSelectionModel localListSelectionModel = getSelectionModel();
    localListSelectionModel.clearSelection();
    int i = getModel().getSize();
    for (int m : paramArrayOfInt) {
      if (m < i) {
        localListSelectionModel.addSelectionInterval(m, m);
      }
    }
  }
  
  @Deprecated
  public Object[] getSelectedValues()
  {
    ListSelectionModel localListSelectionModel = getSelectionModel();
    ListModel localListModel = getModel();
    int i = localListSelectionModel.getMinSelectionIndex();
    int j = localListSelectionModel.getMaxSelectionIndex();
    if ((i < 0) || (j < 0)) {
      return new Object[0];
    }
    Object[] arrayOfObject1 = new Object[1 + (j - i)];
    int k = 0;
    for (int m = i; m <= j; m++) {
      if (localListSelectionModel.isSelectedIndex(m)) {
        arrayOfObject1[(k++)] = localListModel.getElementAt(m);
      }
    }
    Object[] arrayOfObject2 = new Object[k];
    System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, k);
    return arrayOfObject2;
  }
  
  public List<E> getSelectedValuesList()
  {
    ListSelectionModel localListSelectionModel = getSelectionModel();
    ListModel localListModel = getModel();
    int i = localListSelectionModel.getMinSelectionIndex();
    int j = localListSelectionModel.getMaxSelectionIndex();
    if ((i < 0) || (j < 0)) {
      return Collections.emptyList();
    }
    ArrayList localArrayList = new ArrayList();
    for (int k = i; k <= j; k++) {
      if (localListSelectionModel.isSelectedIndex(k)) {
        localArrayList.add(localListModel.getElementAt(k));
      }
    }
    return localArrayList;
  }
  
  public int getSelectedIndex()
  {
    return getMinSelectionIndex();
  }
  
  public E getSelectedValue()
  {
    int i = getMinSelectionIndex();
    return i == -1 ? null : getModel().getElementAt(i);
  }
  
  public void setSelectedValue(Object paramObject, boolean paramBoolean)
  {
    if (paramObject == null)
    {
      setSelectedIndex(-1);
    }
    else if (!paramObject.equals(getSelectedValue()))
    {
      ListModel localListModel = getModel();
      int i = 0;
      int j = localListModel.getSize();
      while (i < j)
      {
        if (paramObject.equals(localListModel.getElementAt(i)))
        {
          setSelectedIndex(i);
          if (paramBoolean) {
            ensureIndexIsVisible(i);
          }
          repaint();
          return;
        }
        i++;
      }
      setSelectedIndex(-1);
    }
    repaint();
  }
  
  private void checkScrollableParameters(Rectangle paramRectangle, int paramInt)
  {
    if (paramRectangle == null) {
      throw new IllegalArgumentException("visibleRect must be non-null");
    }
    switch (paramInt)
    {
    case 0: 
    case 1: 
      break;
    default: 
      throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
    }
  }
  
  public Dimension getPreferredScrollableViewportSize()
  {
    if (getLayoutOrientation() != 0) {
      return getPreferredSize();
    }
    Insets localInsets = getInsets();
    int i = left + right;
    int j = top + bottom;
    int k = getVisibleRowCount();
    int m = getFixedCellWidth();
    int n = getFixedCellHeight();
    int i1;
    int i2;
    if ((m > 0) && (n > 0))
    {
      i1 = m + i;
      i2 = k * n + j;
      return new Dimension(i1, i2);
    }
    if (getModel().getSize() > 0)
    {
      i1 = getPreferredSizewidth;
      Rectangle localRectangle = getCellBounds(0, 0);
      if (localRectangle != null) {
        i2 = k * height + j;
      } else {
        i2 = 1;
      }
      return new Dimension(i1, i2);
    }
    m = m > 0 ? m : 256;
    n = n > 0 ? n : 16;
    return new Dimension(m, n * k);
  }
  
  public int getScrollableUnitIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2)
  {
    checkScrollableParameters(paramRectangle, paramInt1);
    Point localPoint;
    if (paramInt1 == 1)
    {
      int i = locationToIndex(paramRectangle.getLocation());
      if (i == -1) {
        return 0;
      }
      if (paramInt2 > 0)
      {
        localRectangle1 = getCellBounds(i, i);
        return localRectangle1 == null ? 0 : height - (y - y);
      }
      Rectangle localRectangle1 = getCellBounds(i, i);
      if ((y == y) && (i == 0)) {
        return 0;
      }
      if (y == y)
      {
        localPoint = localRectangle1.getLocation();
        y -= 1;
        int k = locationToIndex(localPoint);
        Rectangle localRectangle3 = getCellBounds(k, k);
        if ((localRectangle3 == null) || (y >= y)) {
          return 0;
        }
        return height;
      }
      return y - y;
    }
    if ((paramInt1 == 0) && (getLayoutOrientation() != 0))
    {
      boolean bool = getComponentOrientation().isLeftToRight();
      if (bool) {
        localPoint = paramRectangle.getLocation();
      } else {
        localPoint = new Point(x + width - 1, y);
      }
      int j = locationToIndex(localPoint);
      if (j != -1)
      {
        Rectangle localRectangle2 = getCellBounds(j, j);
        if ((localRectangle2 != null) && (localRectangle2.contains(localPoint)))
        {
          int m;
          int n;
          if (bool)
          {
            m = x;
            n = x;
          }
          else
          {
            m = x + width;
            n = x + width;
          }
          if (n != m)
          {
            if (paramInt2 < 0) {
              return Math.abs(m - n);
            }
            if (bool) {
              return n + width - m;
            }
            return m - x;
          }
          return width;
        }
      }
    }
    Font localFont = getFont();
    return localFont != null ? localFont.getSize() : 1;
  }
  
  public int getScrollableBlockIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2)
  {
    checkScrollableParameters(paramRectangle, paramInt1);
    int j;
    int k;
    Rectangle localRectangle3;
    if (paramInt1 == 1)
    {
      int i = height;
      if (paramInt2 > 0)
      {
        j = locationToIndex(new Point(x, y + height - 1));
        if (j != -1)
        {
          Rectangle localRectangle1 = getCellBounds(j, j);
          if (localRectangle1 != null)
          {
            i = y - y;
            if ((i == 0) && (j < getModel().getSize() - 1)) {
              i = height;
            }
          }
        }
      }
      else
      {
        j = locationToIndex(new Point(x, y - height));
        k = getFirstVisibleIndex();
        if (j != -1)
        {
          if (k == -1) {
            k = locationToIndex(paramRectangle.getLocation());
          }
          Rectangle localRectangle2 = getCellBounds(j, j);
          localRectangle3 = getCellBounds(k, k);
          if ((localRectangle2 != null) && (localRectangle3 != null))
          {
            while ((y + height < y + height) && (y < y))
            {
              j++;
              localRectangle2 = getCellBounds(j, j);
            }
            i = y - y;
            if ((i <= 0) && (y > 0))
            {
              j--;
              localRectangle2 = getCellBounds(j, j);
              if (localRectangle2 != null) {
                i = y - y;
              }
            }
          }
        }
      }
      return i;
    }
    if ((paramInt1 == 0) && (getLayoutOrientation() != 0))
    {
      boolean bool = getComponentOrientation().isLeftToRight();
      j = width;
      int m;
      if (paramInt2 > 0)
      {
        k = x + (bool ? width - 1 : 0);
        m = locationToIndex(new Point(k, y));
        if (m != -1)
        {
          localRectangle3 = getCellBounds(m, m);
          if (localRectangle3 != null)
          {
            if (bool) {
              j = x - x;
            } else {
              j = x + width - (x + width);
            }
            if (j < 0) {
              j += width;
            } else if ((j == 0) && (m < getModel().getSize() - 1)) {
              j = width;
            }
          }
        }
      }
      else
      {
        k = x + (bool ? -width : width - 1 + width);
        m = locationToIndex(new Point(k, y));
        if (m != -1)
        {
          localRectangle3 = getCellBounds(m, m);
          if (localRectangle3 != null)
          {
            int n = x + width;
            if (bool)
            {
              if ((x < x - width) && (n < x)) {
                j = x - n;
              } else {
                j = x - x;
              }
            }
            else
            {
              int i1 = x + width;
              if ((n > i1 + width) && (x > i1)) {
                j = x - i1;
              } else {
                j = n - i1;
              }
            }
          }
        }
      }
      return j;
    }
    return width;
  }
  
  public boolean getScrollableTracksViewportWidth()
  {
    if ((getLayoutOrientation() == 2) && (getVisibleRowCount() <= 0)) {
      return true;
    }
    Container localContainer = SwingUtilities.getUnwrappedParent(this);
    if ((localContainer instanceof JViewport)) {
      return localContainer.getWidth() > getPreferredSizewidth;
    }
    return false;
  }
  
  public boolean getScrollableTracksViewportHeight()
  {
    if ((getLayoutOrientation() == 1) && (getVisibleRowCount() <= 0)) {
      return true;
    }
    Container localContainer = SwingUtilities.getUnwrappedParent(this);
    if ((localContainer instanceof JViewport)) {
      return localContainer.getHeight() > getPreferredSizeheight;
    }
    return false;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ListUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  protected String paramString()
  {
    String str1 = selectionForeground != null ? selectionForeground.toString() : "";
    String str2 = selectionBackground != null ? selectionBackground.toString() : "";
    return super.paramString() + ",fixedCellHeight=" + fixedCellHeight + ",fixedCellWidth=" + fixedCellWidth + ",horizontalScrollIncrement=" + horizontalScrollIncrement + ",selectionBackground=" + str2 + ",selectionForeground=" + str1 + ",visibleRowCount=" + visibleRowCount + ",layoutOrientation=" + layoutOrientation;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJList();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJList
    extends JComponent.AccessibleJComponent
    implements AccessibleSelection, PropertyChangeListener, ListSelectionListener, ListDataListener
  {
    int leadSelectionIndex;
    
    public AccessibleJList()
    {
      super();
      addPropertyChangeListener(this);
      getSelectionModel().addListSelectionListener(this);
      getModel().addListDataListener(this);
      leadSelectionIndex = getLeadSelectionIndex();
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      Object localObject1 = paramPropertyChangeEvent.getOldValue();
      Object localObject2 = paramPropertyChangeEvent.getNewValue();
      if (str.compareTo("model") == 0)
      {
        if ((localObject1 != null) && ((localObject1 instanceof ListModel))) {
          ((ListModel)localObject1).removeListDataListener(this);
        }
        if ((localObject2 != null) && ((localObject2 instanceof ListModel))) {
          ((ListModel)localObject2).addListDataListener(this);
        }
      }
      else if (str.compareTo("selectionModel") == 0)
      {
        if ((localObject1 != null) && ((localObject1 instanceof ListSelectionModel))) {
          ((ListSelectionModel)localObject1).removeListSelectionListener(this);
        }
        if ((localObject2 != null) && ((localObject2 instanceof ListSelectionModel))) {
          ((ListSelectionModel)localObject2).addListSelectionListener(this);
        }
        firePropertyChange("AccessibleSelection", Boolean.valueOf(false), Boolean.valueOf(true));
      }
    }
    
    public void valueChanged(ListSelectionEvent paramListSelectionEvent)
    {
      int i = leadSelectionIndex;
      leadSelectionIndex = getLeadSelectionIndex();
      if (i != leadSelectionIndex)
      {
        localObject1 = i >= 0 ? getAccessibleChild(i) : null;
        localObject2 = leadSelectionIndex >= 0 ? getAccessibleChild(leadSelectionIndex) : null;
        firePropertyChange("AccessibleActiveDescendant", localObject1, localObject2);
      }
      firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true));
      firePropertyChange("AccessibleSelection", Boolean.valueOf(false), Boolean.valueOf(true));
      Object localObject1 = getAccessibleStateSet();
      Object localObject2 = getSelectionModel();
      if (((ListSelectionModel)localObject2).getSelectionMode() != 0)
      {
        if (!((AccessibleStateSet)localObject1).contains(AccessibleState.MULTISELECTABLE))
        {
          ((AccessibleStateSet)localObject1).add(AccessibleState.MULTISELECTABLE);
          firePropertyChange("AccessibleState", null, AccessibleState.MULTISELECTABLE);
        }
      }
      else if (((AccessibleStateSet)localObject1).contains(AccessibleState.MULTISELECTABLE))
      {
        ((AccessibleStateSet)localObject1).remove(AccessibleState.MULTISELECTABLE);
        firePropertyChange("AccessibleState", AccessibleState.MULTISELECTABLE, null);
      }
    }
    
    public void intervalAdded(ListDataEvent paramListDataEvent)
    {
      firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true));
    }
    
    public void intervalRemoved(ListDataEvent paramListDataEvent)
    {
      firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true));
    }
    
    public void contentsChanged(ListDataEvent paramListDataEvent)
    {
      firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true));
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (selectionModel.getSelectionMode() != 0) {
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
      int i = locationToIndex(paramPoint);
      if (i >= 0) {
        return new AccessibleJListChild(JList.this, i);
      }
      return null;
    }
    
    public int getAccessibleChildrenCount()
    {
      return getModel().getSize();
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      if (paramInt >= getModel().getSize()) {
        return null;
      }
      return new AccessibleJListChild(JList.this, paramInt);
    }
    
    public AccessibleSelection getAccessibleSelection()
    {
      return this;
    }
    
    public int getAccessibleSelectionCount()
    {
      return getSelectedIndices().length;
    }
    
    public Accessible getAccessibleSelection(int paramInt)
    {
      int i = getAccessibleSelectionCount();
      if ((paramInt < 0) || (paramInt >= i)) {
        return null;
      }
      return getAccessibleChild(getSelectedIndices()[paramInt]);
    }
    
    public boolean isAccessibleChildSelected(int paramInt)
    {
      return isSelectedIndex(paramInt);
    }
    
    public void addAccessibleSelection(int paramInt)
    {
      addSelectionInterval(paramInt, paramInt);
    }
    
    public void removeAccessibleSelection(int paramInt)
    {
      removeSelectionInterval(paramInt, paramInt);
    }
    
    public void clearAccessibleSelection()
    {
      clearSelection();
    }
    
    public void selectAllAccessibleSelection()
    {
      addSelectionInterval(0, getAccessibleChildrenCount() - 1);
    }
    
    protected class AccessibleJListChild
      extends AccessibleContext
      implements Accessible, AccessibleComponent
    {
      private JList<E> parent = null;
      private int indexInParent;
      private Component component = null;
      private AccessibleContext accessibleContext = null;
      private ListModel<E> listModel;
      private ListCellRenderer<? super E> cellRenderer = null;
      
      public AccessibleJListChild(int paramInt)
      {
        parent = paramInt;
        setAccessibleParent(paramInt);
        int i;
        indexInParent = i;
        if (paramInt != null)
        {
          listModel = paramInt.getModel();
          cellRenderer = paramInt.getCellRenderer();
        }
      }
      
      private Component getCurrentComponent()
      {
        return getComponentAtIndex(indexInParent);
      }
      
      private AccessibleContext getCurrentAccessibleContext()
      {
        Component localComponent = getComponentAtIndex(indexInParent);
        if ((localComponent instanceof Accessible)) {
          return localComponent.getAccessibleContext();
        }
        return null;
      }
      
      private Component getComponentAtIndex(int paramInt)
      {
        if ((paramInt < 0) || (paramInt >= listModel.getSize())) {
          return null;
        }
        if ((parent != null) && (listModel != null) && (cellRenderer != null))
        {
          Object localObject = listModel.getElementAt(paramInt);
          boolean bool1 = parent.isSelectedIndex(paramInt);
          boolean bool2 = (parent.isFocusOwner()) && (paramInt == parent.getLeadSelectionIndex());
          return cellRenderer.getListCellRendererComponent(parent, localObject, paramInt, bool1, bool2);
        }
        return null;
      }
      
      public AccessibleContext getAccessibleContext()
      {
        return this;
      }
      
      public String getAccessibleName()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return localAccessibleContext.getAccessibleName();
        }
        return null;
      }
      
      public void setAccessibleName(String paramString)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          localAccessibleContext.setAccessibleName(paramString);
        }
      }
      
      public String getAccessibleDescription()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return localAccessibleContext.getAccessibleDescription();
        }
        return null;
      }
      
      public void setAccessibleDescription(String paramString)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          localAccessibleContext.setAccessibleDescription(paramString);
        }
      }
      
      public AccessibleRole getAccessibleRole()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return localAccessibleContext.getAccessibleRole();
        }
        return null;
      }
      
      public AccessibleStateSet getAccessibleStateSet()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        AccessibleStateSet localAccessibleStateSet;
        if (localAccessibleContext != null) {
          localAccessibleStateSet = localAccessibleContext.getAccessibleStateSet();
        } else {
          localAccessibleStateSet = new AccessibleStateSet();
        }
        localAccessibleStateSet.add(AccessibleState.SELECTABLE);
        if ((parent.isFocusOwner()) && (indexInParent == parent.getLeadSelectionIndex())) {
          localAccessibleStateSet.add(AccessibleState.ACTIVE);
        }
        if (parent.isSelectedIndex(indexInParent)) {
          localAccessibleStateSet.add(AccessibleState.SELECTED);
        }
        if (isShowing()) {
          localAccessibleStateSet.add(AccessibleState.SHOWING);
        } else if (localAccessibleStateSet.contains(AccessibleState.SHOWING)) {
          localAccessibleStateSet.remove(AccessibleState.SHOWING);
        }
        if (isVisible()) {
          localAccessibleStateSet.add(AccessibleState.VISIBLE);
        } else if (localAccessibleStateSet.contains(AccessibleState.VISIBLE)) {
          localAccessibleStateSet.remove(AccessibleState.VISIBLE);
        }
        localAccessibleStateSet.add(AccessibleState.TRANSIENT);
        return localAccessibleStateSet;
      }
      
      public int getAccessibleIndexInParent()
      {
        return indexInParent;
      }
      
      public int getAccessibleChildrenCount()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return localAccessibleContext.getAccessibleChildrenCount();
        }
        return 0;
      }
      
      public Accessible getAccessibleChild(int paramInt)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null)
        {
          Accessible localAccessible = localAccessibleContext.getAccessibleChild(paramInt);
          localAccessibleContext.setAccessibleParent(this);
          return localAccessible;
        }
        return null;
      }
      
      public Locale getLocale()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return localAccessibleContext.getLocale();
        }
        return null;
      }
      
      public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          localAccessibleContext.addPropertyChangeListener(paramPropertyChangeListener);
        }
      }
      
      public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          localAccessibleContext.removePropertyChangeListener(paramPropertyChangeListener);
        }
      }
      
      public AccessibleAction getAccessibleAction()
      {
        return getCurrentAccessibleContext().getAccessibleAction();
      }
      
      public AccessibleComponent getAccessibleComponent()
      {
        return this;
      }
      
      public AccessibleSelection getAccessibleSelection()
      {
        return getCurrentAccessibleContext().getAccessibleSelection();
      }
      
      public AccessibleText getAccessibleText()
      {
        return getCurrentAccessibleContext().getAccessibleText();
      }
      
      public AccessibleValue getAccessibleValue()
      {
        return getCurrentAccessibleContext().getAccessibleValue();
      }
      
      public Color getBackground()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getBackground();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.getBackground();
        }
        return null;
      }
      
      public void setBackground(Color paramColor)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setBackground(paramColor);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setBackground(paramColor);
          }
        }
      }
      
      public Color getForeground()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getForeground();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.getForeground();
        }
        return null;
      }
      
      public void setForeground(Color paramColor)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setForeground(paramColor);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setForeground(paramColor);
          }
        }
      }
      
      public Cursor getCursor()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getCursor();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.getCursor();
        }
        Accessible localAccessible = getAccessibleParent();
        if ((localAccessible instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessible).getCursor();
        }
        return null;
      }
      
      public void setCursor(Cursor paramCursor)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setCursor(paramCursor);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setCursor(paramCursor);
          }
        }
      }
      
      public Font getFont()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getFont();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.getFont();
        }
        return null;
      }
      
      public void setFont(Font paramFont)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setFont(paramFont);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setFont(paramFont);
          }
        }
      }
      
      public FontMetrics getFontMetrics(Font paramFont)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getFontMetrics(paramFont);
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.getFontMetrics(paramFont);
        }
        return null;
      }
      
      public boolean isEnabled()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).isEnabled();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.isEnabled();
        }
        return false;
      }
      
      public void setEnabled(boolean paramBoolean)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setEnabled(paramBoolean);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setEnabled(paramBoolean);
          }
        }
      }
      
      public boolean isVisible()
      {
        int i = parent.getFirstVisibleIndex();
        int j = parent.getLastVisibleIndex();
        if (j == -1) {
          j = parent.getModel().getSize() - 1;
        }
        return (indexInParent >= i) && (indexInParent <= j);
      }
      
      public void setVisible(boolean paramBoolean) {}
      
      public boolean isShowing()
      {
        return (parent.isShowing()) && (isVisible());
      }
      
      public boolean contains(Point paramPoint)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          localObject = ((AccessibleComponent)localAccessibleContext).getBounds();
          return ((Rectangle)localObject).contains(paramPoint);
        }
        Object localObject = getCurrentComponent();
        if (localObject != null)
        {
          Rectangle localRectangle = ((Component)localObject).getBounds();
          return localRectangle.contains(paramPoint);
        }
        return getBounds().contains(paramPoint);
      }
      
      public Point getLocationOnScreen()
      {
        if (parent != null)
        {
          Point localPoint1 = parent.getLocationOnScreen();
          Point localPoint2 = parent.indexToLocation(indexInParent);
          if (localPoint2 != null)
          {
            localPoint2.translate(x, y);
            return localPoint2;
          }
          return null;
        }
        return null;
      }
      
      public Point getLocation()
      {
        if (parent != null) {
          return parent.indexToLocation(indexInParent);
        }
        return null;
      }
      
      public void setLocation(Point paramPoint)
      {
        if ((parent != null) && (parent.contains(paramPoint))) {
          ensureIndexIsVisible(indexInParent);
        }
      }
      
      public Rectangle getBounds()
      {
        if (parent != null) {
          return parent.getCellBounds(indexInParent, indexInParent);
        }
        return null;
      }
      
      public void setBounds(Rectangle paramRectangle)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          ((AccessibleComponent)localAccessibleContext).setBounds(paramRectangle);
        }
      }
      
      public Dimension getSize()
      {
        Rectangle localRectangle = getBounds();
        if (localRectangle != null) {
          return localRectangle.getSize();
        }
        return null;
      }
      
      public void setSize(Dimension paramDimension)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setSize(paramDimension);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setSize(paramDimension);
          }
        }
      }
      
      public Accessible getAccessibleAt(Point paramPoint)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getAccessibleAt(paramPoint);
        }
        return null;
      }
      
      public boolean isFocusTraversable()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).isFocusTraversable();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.isFocusTraversable();
        }
        return false;
      }
      
      public void requestFocus()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).requestFocus();
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.requestFocus();
          }
        }
      }
      
      public void addFocusListener(FocusListener paramFocusListener)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).addFocusListener(paramFocusListener);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.addFocusListener(paramFocusListener);
          }
        }
      }
      
      public void removeFocusListener(FocusListener paramFocusListener)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).removeFocusListener(paramFocusListener);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.removeFocusListener(paramFocusListener);
          }
        }
      }
      
      public AccessibleIcon[] getAccessibleIcon()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return localAccessibleContext.getAccessibleIcon();
        }
        return null;
      }
    }
  }
  
  public static final class DropLocation
    extends TransferHandler.DropLocation
  {
    private final int index;
    private final boolean isInsert;
    
    private DropLocation(Point paramPoint, int paramInt, boolean paramBoolean)
    {
      super();
      index = paramInt;
      isInsert = paramBoolean;
    }
    
    public int getIndex()
    {
      return index;
    }
    
    public boolean isInsert()
    {
      return isInsert;
    }
    
    public String toString()
    {
      return getClass().getName() + "[dropPoint=" + getDropPoint() + ",index=" + index + ",insert=" + isInsert + "]";
    }
  }
  
  private class ListSelectionHandler
    implements ListSelectionListener, Serializable
  {
    private ListSelectionHandler() {}
    
    public void valueChanged(ListSelectionEvent paramListSelectionEvent)
    {
      fireSelectionValueChanged(paramListSelectionEvent.getFirstIndex(), paramListSelectionEvent.getLastIndex(), paramListSelectionEvent.getValueIsAdjusting());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */