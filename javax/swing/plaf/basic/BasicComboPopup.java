package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.accessibility.AccessibleContext;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class BasicComboPopup
  extends JPopupMenu
  implements ComboPopup
{
  static final ListModel EmptyListModel = new EmptyListModelClass(null);
  private static Border LIST_BORDER = new LineBorder(Color.BLACK, 1);
  protected JComboBox comboBox;
  protected JList list;
  protected JScrollPane scroller;
  protected boolean valueIsAdjusting = false;
  private Handler handler;
  protected MouseMotionListener mouseMotionListener;
  protected MouseListener mouseListener;
  protected KeyListener keyListener;
  protected ListSelectionListener listSelectionListener;
  protected MouseListener listMouseListener;
  protected MouseMotionListener listMouseMotionListener;
  protected PropertyChangeListener propertyChangeListener;
  protected ListDataListener listDataListener;
  protected ItemListener itemListener;
  private MouseWheelListener scrollerMouseWheelListener;
  protected Timer autoscrollTimer;
  protected boolean hasEntered = false;
  protected boolean isAutoScrolling = false;
  protected int scrollDirection = 0;
  protected static final int SCROLL_UP = 0;
  protected static final int SCROLL_DOWN = 1;
  
  public void show()
  {
    comboBox.firePopupMenuWillBecomeVisible();
    setListSelection(comboBox.getSelectedIndex());
    Point localPoint = getPopupLocation();
    show(comboBox, x, y);
  }
  
  public void hide()
  {
    MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
    MenuElement[] arrayOfMenuElement = localMenuSelectionManager.getSelectedPath();
    for (int i = 0; i < arrayOfMenuElement.length; i++) {
      if (arrayOfMenuElement[i] == this)
      {
        localMenuSelectionManager.clearSelectedPath();
        break;
      }
    }
    if (arrayOfMenuElement.length > 0) {
      comboBox.repaint();
    }
  }
  
  public JList getList()
  {
    return list;
  }
  
  public MouseListener getMouseListener()
  {
    if (mouseListener == null) {
      mouseListener = createMouseListener();
    }
    return mouseListener;
  }
  
  public MouseMotionListener getMouseMotionListener()
  {
    if (mouseMotionListener == null) {
      mouseMotionListener = createMouseMotionListener();
    }
    return mouseMotionListener;
  }
  
  public KeyListener getKeyListener()
  {
    if (keyListener == null) {
      keyListener = createKeyListener();
    }
    return keyListener;
  }
  
  public void uninstallingUI()
  {
    if (propertyChangeListener != null) {
      comboBox.removePropertyChangeListener(propertyChangeListener);
    }
    if (itemListener != null) {
      comboBox.removeItemListener(itemListener);
    }
    uninstallComboBoxModelListeners(comboBox.getModel());
    uninstallKeyboardActions();
    uninstallListListeners();
    uninstallScrollerListeners();
    list.setModel(EmptyListModel);
  }
  
  protected void uninstallComboBoxModelListeners(ComboBoxModel paramComboBoxModel)
  {
    if ((paramComboBoxModel != null) && (listDataListener != null)) {
      paramComboBoxModel.removeListDataListener(listDataListener);
    }
  }
  
  protected void uninstallKeyboardActions() {}
  
  public BasicComboPopup(JComboBox paramJComboBox)
  {
    setName("ComboPopup.popup");
    comboBox = paramJComboBox;
    setLightWeightPopupEnabled(comboBox.isLightWeightPopupEnabled());
    list = createList();
    list.setName("ComboBox.list");
    configureList();
    scroller = createScroller();
    scroller.setName("ComboBox.scrollPane");
    configureScroller();
    configurePopup();
    installComboBoxListeners();
    installKeyboardActions();
  }
  
  protected void firePopupMenuWillBecomeVisible()
  {
    if (scrollerMouseWheelListener != null) {
      comboBox.addMouseWheelListener(scrollerMouseWheelListener);
    }
    super.firePopupMenuWillBecomeVisible();
  }
  
  protected void firePopupMenuWillBecomeInvisible()
  {
    if (scrollerMouseWheelListener != null) {
      comboBox.removeMouseWheelListener(scrollerMouseWheelListener);
    }
    super.firePopupMenuWillBecomeInvisible();
    comboBox.firePopupMenuWillBecomeInvisible();
  }
  
  protected void firePopupMenuCanceled()
  {
    if (scrollerMouseWheelListener != null) {
      comboBox.removeMouseWheelListener(scrollerMouseWheelListener);
    }
    super.firePopupMenuCanceled();
    comboBox.firePopupMenuCanceled();
  }
  
  protected MouseListener createMouseListener()
  {
    return getHandler();
  }
  
  protected MouseMotionListener createMouseMotionListener()
  {
    return getHandler();
  }
  
  protected KeyListener createKeyListener()
  {
    return null;
  }
  
  protected ListSelectionListener createListSelectionListener()
  {
    return null;
  }
  
  protected ListDataListener createListDataListener()
  {
    return null;
  }
  
  protected MouseListener createListMouseListener()
  {
    return getHandler();
  }
  
  protected MouseMotionListener createListMouseMotionListener()
  {
    return getHandler();
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return getHandler();
  }
  
  protected ItemListener createItemListener()
  {
    return getHandler();
  }
  
  private Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler(null);
    }
    return handler;
  }
  
  protected JList createList()
  {
    new JList(comboBox.getModel())
    {
      public void processMouseEvent(MouseEvent paramAnonymousMouseEvent)
      {
        if (BasicGraphicsUtils.isMenuShortcutKeyDown(paramAnonymousMouseEvent))
        {
          Toolkit localToolkit = Toolkit.getDefaultToolkit();
          paramAnonymousMouseEvent = new MouseEvent((Component)paramAnonymousMouseEvent.getSource(), paramAnonymousMouseEvent.getID(), paramAnonymousMouseEvent.getWhen(), paramAnonymousMouseEvent.getModifiers() ^ localToolkit.getMenuShortcutKeyMask(), paramAnonymousMouseEvent.getX(), paramAnonymousMouseEvent.getY(), paramAnonymousMouseEvent.getXOnScreen(), paramAnonymousMouseEvent.getYOnScreen(), paramAnonymousMouseEvent.getClickCount(), paramAnonymousMouseEvent.isPopupTrigger(), 0);
        }
        super.processMouseEvent(paramAnonymousMouseEvent);
      }
    };
  }
  
  protected void configureList()
  {
    list.setFont(comboBox.getFont());
    list.setForeground(comboBox.getForeground());
    list.setBackground(comboBox.getBackground());
    list.setSelectionForeground(UIManager.getColor("ComboBox.selectionForeground"));
    list.setSelectionBackground(UIManager.getColor("ComboBox.selectionBackground"));
    list.setBorder(null);
    list.setCellRenderer(comboBox.getRenderer());
    list.setFocusable(false);
    list.setSelectionMode(0);
    setListSelection(comboBox.getSelectedIndex());
    installListListeners();
  }
  
  protected void installListListeners()
  {
    if ((listMouseListener = createListMouseListener()) != null) {
      list.addMouseListener(listMouseListener);
    }
    if ((listMouseMotionListener = createListMouseMotionListener()) != null) {
      list.addMouseMotionListener(listMouseMotionListener);
    }
    if ((listSelectionListener = createListSelectionListener()) != null) {
      list.addListSelectionListener(listSelectionListener);
    }
  }
  
  void uninstallListListeners()
  {
    if (listMouseListener != null)
    {
      list.removeMouseListener(listMouseListener);
      listMouseListener = null;
    }
    if (listMouseMotionListener != null)
    {
      list.removeMouseMotionListener(listMouseMotionListener);
      listMouseMotionListener = null;
    }
    if (listSelectionListener != null)
    {
      list.removeListSelectionListener(listSelectionListener);
      listSelectionListener = null;
    }
    handler = null;
  }
  
  protected JScrollPane createScroller()
  {
    JScrollPane localJScrollPane = new JScrollPane(list, 20, 31);
    localJScrollPane.setHorizontalScrollBar(null);
    return localJScrollPane;
  }
  
  protected void configureScroller()
  {
    scroller.setFocusable(false);
    scroller.getVerticalScrollBar().setFocusable(false);
    scroller.setBorder(null);
    installScrollerListeners();
  }
  
  protected void configurePopup()
  {
    setLayout(new BoxLayout(this, 1));
    setBorderPainted(true);
    setBorder(LIST_BORDER);
    setOpaque(false);
    add(scroller);
    setDoubleBuffered(true);
    setFocusable(false);
  }
  
  private void installScrollerListeners()
  {
    scrollerMouseWheelListener = getHandler();
    if (scrollerMouseWheelListener != null) {
      scroller.addMouseWheelListener(scrollerMouseWheelListener);
    }
  }
  
  private void uninstallScrollerListeners()
  {
    if (scrollerMouseWheelListener != null)
    {
      scroller.removeMouseWheelListener(scrollerMouseWheelListener);
      scrollerMouseWheelListener = null;
    }
  }
  
  protected void installComboBoxListeners()
  {
    if ((propertyChangeListener = createPropertyChangeListener()) != null) {
      comboBox.addPropertyChangeListener(propertyChangeListener);
    }
    if ((itemListener = createItemListener()) != null) {
      comboBox.addItemListener(itemListener);
    }
    installComboBoxModelListeners(comboBox.getModel());
  }
  
  protected void installComboBoxModelListeners(ComboBoxModel paramComboBoxModel)
  {
    if ((paramComboBoxModel != null) && ((listDataListener = createListDataListener()) != null)) {
      paramComboBoxModel.addListDataListener(listDataListener);
    }
  }
  
  protected void installKeyboardActions() {}
  
  public boolean isFocusTraversable()
  {
    return false;
  }
  
  protected void startAutoScrolling(int paramInt)
  {
    if (isAutoScrolling) {
      autoscrollTimer.stop();
    }
    isAutoScrolling = true;
    Object localObject;
    if (paramInt == 0)
    {
      scrollDirection = 0;
      localObject = SwingUtilities.convertPoint(scroller, new Point(1, 1), list);
      int i = list.locationToIndex((Point)localObject);
      list.setSelectedIndex(i);
      autoscrollTimer = new Timer(100, new AutoScrollActionHandler(0));
    }
    else if (paramInt == 1)
    {
      scrollDirection = 1;
      localObject = scroller.getSize();
      Point localPoint = SwingUtilities.convertPoint(scroller, new Point(1, height - 1 - 2), list);
      int j = list.locationToIndex(localPoint);
      list.setSelectedIndex(j);
      autoscrollTimer = new Timer(100, new AutoScrollActionHandler(1));
    }
    autoscrollTimer.start();
  }
  
  protected void stopAutoScrolling()
  {
    isAutoScrolling = false;
    if (autoscrollTimer != null)
    {
      autoscrollTimer.stop();
      autoscrollTimer = null;
    }
  }
  
  protected void autoScrollUp()
  {
    int i = list.getSelectedIndex();
    if (i > 0)
    {
      list.setSelectedIndex(i - 1);
      list.ensureIndexIsVisible(i - 1);
    }
  }
  
  protected void autoScrollDown()
  {
    int i = list.getSelectedIndex();
    int j = list.getModel().getSize() - 1;
    if (i < j)
    {
      list.setSelectedIndex(i + 1);
      list.ensureIndexIsVisible(i + 1);
    }
  }
  
  public AccessibleContext getAccessibleContext()
  {
    AccessibleContext localAccessibleContext = super.getAccessibleContext();
    localAccessibleContext.setAccessibleParent(comboBox);
    return localAccessibleContext;
  }
  
  protected void delegateFocus(MouseEvent paramMouseEvent)
  {
    if (comboBox.isEditable())
    {
      Component localComponent = comboBox.getEditor().getEditorComponent();
      if ((!(localComponent instanceof JComponent)) || (((JComponent)localComponent).isRequestFocusEnabled())) {
        localComponent.requestFocus();
      }
    }
    else if (comboBox.isRequestFocusEnabled())
    {
      comboBox.requestFocus();
    }
  }
  
  protected void togglePopup()
  {
    if (isVisible()) {
      hide();
    } else {
      show();
    }
  }
  
  private void setListSelection(int paramInt)
  {
    if (paramInt == -1)
    {
      list.clearSelection();
    }
    else
    {
      list.setSelectedIndex(paramInt);
      list.ensureIndexIsVisible(paramInt);
    }
  }
  
  protected MouseEvent convertMouseEvent(MouseEvent paramMouseEvent)
  {
    Point localPoint = SwingUtilities.convertPoint((Component)paramMouseEvent.getSource(), paramMouseEvent.getPoint(), list);
    MouseEvent localMouseEvent = new MouseEvent((Component)paramMouseEvent.getSource(), paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), x, y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
    return localMouseEvent;
  }
  
  protected int getPopupHeightForRowCount(int paramInt)
  {
    int i = Math.min(paramInt, comboBox.getItemCount());
    int j = 0;
    ListCellRenderer localListCellRenderer = list.getCellRenderer();
    Object localObject1 = null;
    Object localObject2;
    for (int k = 0; k < i; k++)
    {
      localObject1 = list.getModel().getElementAt(k);
      localObject2 = localListCellRenderer.getListCellRendererComponent(list, localObject1, k, false, false);
      j += getPreferredSizeheight;
    }
    if (j == 0) {
      j = comboBox.getHeight();
    }
    Border localBorder = scroller.getViewportBorder();
    if (localBorder != null)
    {
      localObject2 = localBorder.getBorderInsets(null);
      j += top + bottom;
    }
    localBorder = scroller.getBorder();
    if (localBorder != null)
    {
      localObject2 = localBorder.getBorderInsets(null);
      j += top + bottom;
    }
    return j;
  }
  
  protected Rectangle computePopupBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    GraphicsConfiguration localGraphicsConfiguration = comboBox.getGraphicsConfiguration();
    Point localPoint = new Point();
    SwingUtilities.convertPointFromScreen(localPoint, comboBox);
    Rectangle localRectangle;
    if (localGraphicsConfiguration != null)
    {
      localObject = localToolkit.getScreenInsets(localGraphicsConfiguration);
      localRectangle = localGraphicsConfiguration.getBounds();
      width -= left + right;
      height -= top + bottom;
      x += x + left;
      y += y + top;
    }
    else
    {
      localRectangle = new Rectangle(localPoint, localToolkit.getScreenSize());
    }
    Object localObject = new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4);
    if ((paramInt2 + paramInt4 > y + height) && (paramInt4 < height)) {
      y = (-height);
    }
    return (Rectangle)localObject;
  }
  
  private Point getPopupLocation()
  {
    Dimension localDimension1 = comboBox.getSize();
    Insets localInsets = getInsets();
    localDimension1.setSize(width - (right + left), getPopupHeightForRowCount(comboBox.getMaximumRowCount()));
    Rectangle localRectangle = computePopupBounds(0, comboBox.getBounds().height, width, height);
    Dimension localDimension2 = localRectangle.getSize();
    Point localPoint = localRectangle.getLocation();
    scroller.setMaximumSize(localDimension2);
    scroller.setPreferredSize(localDimension2);
    scroller.setMinimumSize(localDimension2);
    list.revalidate();
    return localPoint;
  }
  
  protected void updateListBoxSelectionForEvent(MouseEvent paramMouseEvent, boolean paramBoolean)
  {
    Point localPoint = paramMouseEvent.getPoint();
    if (list == null) {
      return;
    }
    int i = list.locationToIndex(localPoint);
    if (i == -1) {
      if (y < 0) {
        i = 0;
      } else {
        i = comboBox.getModel().getSize() - 1;
      }
    }
    if (list.getSelectedIndex() != i)
    {
      list.setSelectedIndex(i);
      if (paramBoolean) {
        list.ensureIndexIsVisible(i);
      }
    }
  }
  
  private class AutoScrollActionHandler
    implements ActionListener
  {
    private int direction;
    
    AutoScrollActionHandler(int paramInt)
    {
      direction = paramInt;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (direction == 0) {
        autoScrollUp();
      } else {
        autoScrollDown();
      }
    }
  }
  
  private static class EmptyListModelClass
    implements ListModel<Object>, Serializable
  {
    private EmptyListModelClass() {}
    
    public int getSize()
    {
      return 0;
    }
    
    public Object getElementAt(int paramInt)
    {
      return null;
    }
    
    public void addListDataListener(ListDataListener paramListDataListener) {}
    
    public void removeListDataListener(ListDataListener paramListDataListener) {}
  }
  
  private class Handler
    implements ItemListener, MouseListener, MouseMotionListener, MouseWheelListener, PropertyChangeListener, Serializable
  {
    private Handler() {}
    
    public void mouseClicked(MouseEvent paramMouseEvent) {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (paramMouseEvent.getSource() == list) {
        return;
      }
      if ((!SwingUtilities.isLeftMouseButton(paramMouseEvent)) || (!comboBox.isEnabled())) {
        return;
      }
      if (comboBox.isEditable())
      {
        Component localComponent = comboBox.getEditor().getEditorComponent();
        if ((!(localComponent instanceof JComponent)) || (((JComponent)localComponent).isRequestFocusEnabled())) {
          localComponent.requestFocus();
        }
      }
      else if (comboBox.isRequestFocusEnabled())
      {
        comboBox.requestFocus();
      }
      togglePopup();
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if (paramMouseEvent.getSource() == list)
      {
        if (list.getModel().getSize() > 0)
        {
          if (comboBox.getSelectedIndex() == list.getSelectedIndex()) {
            comboBox.getEditor().setItem(list.getSelectedValue());
          }
          comboBox.setSelectedIndex(list.getSelectedIndex());
        }
        comboBox.setPopupVisible(false);
        if ((comboBox.isEditable()) && (comboBox.getEditor() != null)) {
          comboBox.configureEditor(comboBox.getEditor(), comboBox.getSelectedItem());
        }
        return;
      }
      Component localComponent = (Component)paramMouseEvent.getSource();
      Dimension localDimension = localComponent.getSize();
      Rectangle localRectangle1 = new Rectangle(0, 0, width - 1, height - 1);
      if (!localRectangle1.contains(paramMouseEvent.getPoint()))
      {
        MouseEvent localMouseEvent = convertMouseEvent(paramMouseEvent);
        Point localPoint = localMouseEvent.getPoint();
        Rectangle localRectangle2 = new Rectangle();
        list.computeVisibleRect(localRectangle2);
        if (localRectangle2.contains(localPoint))
        {
          if (comboBox.getSelectedIndex() == list.getSelectedIndex()) {
            comboBox.getEditor().setItem(list.getSelectedValue());
          }
          comboBox.setSelectedIndex(list.getSelectedIndex());
        }
        comboBox.setPopupVisible(false);
      }
      hasEntered = false;
      stopAutoScrolling();
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent) {}
    
    public void mouseExited(MouseEvent paramMouseEvent) {}
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      if (paramMouseEvent.getSource() == list)
      {
        Point localPoint = paramMouseEvent.getPoint();
        Rectangle localRectangle = new Rectangle();
        list.computeVisibleRect(localRectangle);
        if (localRectangle.contains(localPoint)) {
          updateListBoxSelectionForEvent(paramMouseEvent, false);
        }
      }
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      if (paramMouseEvent.getSource() == list) {
        return;
      }
      if (isVisible())
      {
        MouseEvent localMouseEvent = convertMouseEvent(paramMouseEvent);
        Rectangle localRectangle = new Rectangle();
        list.computeVisibleRect(localRectangle);
        if ((getPointy >= y) && (getPointy <= y + height - 1))
        {
          hasEntered = true;
          if (isAutoScrolling) {
            stopAutoScrolling();
          }
          Point localPoint = localMouseEvent.getPoint();
          if (localRectangle.contains(localPoint)) {
            updateListBoxSelectionForEvent(localMouseEvent, false);
          }
        }
        else if (hasEntered)
        {
          int i = getPointy < y ? 0 : 1;
          if ((isAutoScrolling) && (scrollDirection != i))
          {
            stopAutoScrolling();
            startAutoScrolling(i);
          }
          else if (!isAutoScrolling)
          {
            startAutoScrolling(i);
          }
        }
        else if (getPointy < 0)
        {
          hasEntered = true;
          startAutoScrolling(0);
        }
      }
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      JComboBox localJComboBox = (JComboBox)paramPropertyChangeEvent.getSource();
      String str = paramPropertyChangeEvent.getPropertyName();
      Object localObject1;
      Object localObject2;
      if (str == "model")
      {
        localObject1 = (ComboBoxModel)paramPropertyChangeEvent.getOldValue();
        localObject2 = (ComboBoxModel)paramPropertyChangeEvent.getNewValue();
        uninstallComboBoxModelListeners((ComboBoxModel)localObject1);
        installComboBoxModelListeners((ComboBoxModel)localObject2);
        list.setModel((ListModel)localObject2);
        if (isVisible()) {
          hide();
        }
      }
      else if (str == "renderer")
      {
        list.setCellRenderer(localJComboBox.getRenderer());
        if (isVisible()) {
          hide();
        }
      }
      else if (str == "componentOrientation")
      {
        localObject1 = (ComponentOrientation)paramPropertyChangeEvent.getNewValue();
        localObject2 = getList();
        if ((localObject2 != null) && (((JList)localObject2).getComponentOrientation() != localObject1)) {
          ((JList)localObject2).setComponentOrientation((ComponentOrientation)localObject1);
        }
        if ((scroller != null) && (scroller.getComponentOrientation() != localObject1)) {
          scroller.setComponentOrientation((ComponentOrientation)localObject1);
        }
        if (localObject1 != getComponentOrientation()) {
          setComponentOrientation((ComponentOrientation)localObject1);
        }
      }
      else if (str == "lightWeightPopupEnabled")
      {
        setLightWeightPopupEnabled(localJComboBox.isLightWeightPopupEnabled());
      }
    }
    
    public void itemStateChanged(ItemEvent paramItemEvent)
    {
      if (paramItemEvent.getStateChange() == 1)
      {
        JComboBox localJComboBox = (JComboBox)paramItemEvent.getSource();
        BasicComboPopup.this.setListSelection(localJComboBox.getSelectedIndex());
      }
      else
      {
        BasicComboPopup.this.setListSelection(-1);
      }
    }
    
    public void mouseWheelMoved(MouseWheelEvent paramMouseWheelEvent)
    {
      paramMouseWheelEvent.consume();
    }
  }
  
  public class InvocationKeyHandler
    extends KeyAdapter
  {
    public InvocationKeyHandler() {}
    
    public void keyReleased(KeyEvent paramKeyEvent) {}
  }
  
  protected class InvocationMouseHandler
    extends MouseAdapter
  {
    protected InvocationMouseHandler() {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      BasicComboPopup.this.getHandler().mousePressed(paramMouseEvent);
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      BasicComboPopup.this.getHandler().mouseReleased(paramMouseEvent);
    }
  }
  
  protected class InvocationMouseMotionHandler
    extends MouseMotionAdapter
  {
    protected InvocationMouseMotionHandler() {}
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      BasicComboPopup.this.getHandler().mouseDragged(paramMouseEvent);
    }
  }
  
  protected class ItemHandler
    implements ItemListener
  {
    protected ItemHandler() {}
    
    public void itemStateChanged(ItemEvent paramItemEvent)
    {
      BasicComboPopup.this.getHandler().itemStateChanged(paramItemEvent);
    }
  }
  
  public class ListDataHandler
    implements ListDataListener
  {
    public ListDataHandler() {}
    
    public void contentsChanged(ListDataEvent paramListDataEvent) {}
    
    public void intervalAdded(ListDataEvent paramListDataEvent) {}
    
    public void intervalRemoved(ListDataEvent paramListDataEvent) {}
  }
  
  protected class ListMouseHandler
    extends MouseAdapter
  {
    protected ListMouseHandler() {}
    
    public void mousePressed(MouseEvent paramMouseEvent) {}
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      BasicComboPopup.this.getHandler().mouseReleased(paramMouseEvent);
    }
  }
  
  protected class ListMouseMotionHandler
    extends MouseMotionAdapter
  {
    protected ListMouseMotionHandler() {}
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      BasicComboPopup.this.getHandler().mouseMoved(paramMouseEvent);
    }
  }
  
  protected class ListSelectionHandler
    implements ListSelectionListener
  {
    protected ListSelectionHandler() {}
    
    public void valueChanged(ListSelectionEvent paramListSelectionEvent) {}
  }
  
  protected class PropertyChangeHandler
    implements PropertyChangeListener
  {
    protected PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicComboPopup.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicComboPopup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */