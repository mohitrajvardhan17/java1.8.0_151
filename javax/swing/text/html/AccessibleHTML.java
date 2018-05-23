package javax.swing.text.html;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.IllegalComponentStateException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleTable;
import javax.accessibility.AccessibleText;
import javax.swing.JEditorPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position.Bias;
import javax.swing.text.Segment;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;

class AccessibleHTML
  implements Accessible
{
  private JEditorPane editor;
  private Document model;
  private DocumentListener docListener;
  private PropertyChangeListener propChangeListener;
  private ElementInfo rootElementInfo;
  private RootHTMLAccessibleContext rootHTMLAccessibleContext;
  
  public AccessibleHTML(JEditorPane paramJEditorPane)
  {
    editor = paramJEditorPane;
    propChangeListener = new PropertyChangeHandler(null);
    setDocument(editor.getDocument());
    docListener = new DocumentHandler(null);
  }
  
  private void setDocument(Document paramDocument)
  {
    if (model != null) {
      model.removeDocumentListener(docListener);
    }
    if (editor != null) {
      editor.removePropertyChangeListener(propChangeListener);
    }
    model = paramDocument;
    if (model != null)
    {
      if (rootElementInfo != null) {
        rootElementInfo.invalidate(false);
      }
      buildInfo();
      model.addDocumentListener(docListener);
    }
    else
    {
      rootElementInfo = null;
    }
    if (editor != null) {
      editor.addPropertyChangeListener(propChangeListener);
    }
  }
  
  private Document getDocument()
  {
    return model;
  }
  
  private JEditorPane getTextComponent()
  {
    return editor;
  }
  
  private ElementInfo getRootInfo()
  {
    return rootElementInfo;
  }
  
  private View getRootView()
  {
    return getTextComponent().getUI().getRootView(getTextComponent());
  }
  
  private Rectangle getRootEditorRect()
  {
    Rectangle localRectangle = getTextComponent().getBounds();
    if ((width > 0) && (height > 0))
    {
      x = (y = 0);
      Insets localInsets = editor.getInsets();
      x += left;
      y += top;
      width -= left + right;
      height -= top + bottom;
      return localRectangle;
    }
    return null;
  }
  
  private Object lock()
  {
    Document localDocument = getDocument();
    if ((localDocument instanceof AbstractDocument))
    {
      ((AbstractDocument)localDocument).readLock();
      return localDocument;
    }
    return null;
  }
  
  private void unlock(Object paramObject)
  {
    if (paramObject != null) {
      ((AbstractDocument)paramObject).readUnlock();
    }
  }
  
  private void buildInfo()
  {
    Object localObject1 = lock();
    try
    {
      Document localDocument = getDocument();
      Element localElement = localDocument.getDefaultRootElement();
      rootElementInfo = new ElementInfo(localElement);
      rootElementInfo.validate();
    }
    finally
    {
      unlock(localObject1);
    }
  }
  
  ElementInfo createElementInfo(Element paramElement, ElementInfo paramElementInfo)
  {
    AttributeSet localAttributeSet = paramElement.getAttributes();
    if (localAttributeSet != null)
    {
      Object localObject = localAttributeSet.getAttribute(StyleConstants.NameAttribute);
      if (localObject == HTML.Tag.IMG) {
        return new IconElementInfo(paramElement, paramElementInfo);
      }
      if ((localObject == HTML.Tag.CONTENT) || (localObject == HTML.Tag.CAPTION)) {
        return new TextElementInfo(paramElement, paramElementInfo);
      }
      if (localObject == HTML.Tag.TABLE) {
        return new TableElementInfo(paramElement, paramElementInfo);
      }
    }
    return null;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (rootHTMLAccessibleContext == null) {
      rootHTMLAccessibleContext = new RootHTMLAccessibleContext(rootElementInfo);
    }
    return rootHTMLAccessibleContext;
  }
  
  private class DocumentHandler
    implements DocumentListener
  {
    private DocumentHandler() {}
    
    public void insertUpdate(DocumentEvent paramDocumentEvent)
    {
      AccessibleHTML.ElementInfo.access$1800(AccessibleHTML.this.getRootInfo(), paramDocumentEvent);
    }
    
    public void removeUpdate(DocumentEvent paramDocumentEvent)
    {
      AccessibleHTML.ElementInfo.access$1800(AccessibleHTML.this.getRootInfo(), paramDocumentEvent);
    }
    
    public void changedUpdate(DocumentEvent paramDocumentEvent)
    {
      AccessibleHTML.ElementInfo.access$1800(AccessibleHTML.this.getRootInfo(), paramDocumentEvent);
    }
  }
  
  private class ElementInfo
  {
    private ArrayList<ElementInfo> children;
    private Element element;
    private ElementInfo parent;
    private boolean isValid;
    private boolean canBeValid;
    
    ElementInfo(Element paramElement)
    {
      this(paramElement, null);
    }
    
    ElementInfo(Element paramElement, ElementInfo paramElementInfo)
    {
      element = paramElement;
      parent = paramElementInfo;
      isValid = false;
      canBeValid = true;
    }
    
    protected void validate()
    {
      isValid = true;
      loadChildren(getElement());
    }
    
    protected void loadChildren(Element paramElement)
    {
      if (!paramElement.isLeaf())
      {
        int i = 0;
        int j = paramElement.getElementCount();
        while (i < j)
        {
          Element localElement = paramElement.getElement(i);
          ElementInfo localElementInfo = createElementInfo(localElement, this);
          if (localElementInfo != null) {
            addChild(localElementInfo);
          } else {
            loadChildren(localElement);
          }
          i++;
        }
      }
    }
    
    public int getIndexInParent()
    {
      if ((parent == null) || (!parent.isValid())) {
        return -1;
      }
      return parent.indexOf(this);
    }
    
    public Element getElement()
    {
      return element;
    }
    
    public ElementInfo getParent()
    {
      return parent;
    }
    
    public int indexOf(ElementInfo paramElementInfo)
    {
      ArrayList localArrayList = children;
      if (localArrayList != null) {
        return localArrayList.indexOf(paramElementInfo);
      }
      return -1;
    }
    
    public ElementInfo getChild(int paramInt)
    {
      if (validateIfNecessary())
      {
        ArrayList localArrayList = children;
        if ((localArrayList != null) && (paramInt >= 0) && (paramInt < localArrayList.size())) {
          return (ElementInfo)localArrayList.get(paramInt);
        }
      }
      return null;
    }
    
    public int getChildCount()
    {
      validateIfNecessary();
      return children == null ? 0 : children.size();
    }
    
    protected void addChild(ElementInfo paramElementInfo)
    {
      if (children == null) {
        children = new ArrayList();
      }
      children.add(paramElementInfo);
    }
    
    protected View getView()
    {
      if (!validateIfNecessary()) {
        return null;
      }
      Object localObject1 = AccessibleHTML.this.lock();
      try
      {
        View localView1 = AccessibleHTML.this.getRootView();
        Element localElement = getElement();
        int i = localElement.getStartOffset();
        if (localView1 != null)
        {
          localView2 = getView(localView1, localElement, i);
          return localView2;
        }
        View localView2 = null;
        return localView2;
      }
      finally
      {
        AccessibleHTML.this.unlock(localObject1);
      }
    }
    
    public Rectangle getBounds()
    {
      if (!validateIfNecessary()) {
        return null;
      }
      Object localObject1 = AccessibleHTML.this.lock();
      try
      {
        Rectangle localRectangle1 = AccessibleHTML.this.getRootEditorRect();
        View localView = AccessibleHTML.this.getRootView();
        Element localElement = getElement();
        if ((localRectangle1 != null) && (localView != null)) {
          try
          {
            Rectangle localRectangle2 = localView.modelToView(localElement.getStartOffset(), Position.Bias.Forward, localElement.getEndOffset(), Position.Bias.Backward, localRectangle1).getBounds();
            return localRectangle2;
          }
          catch (BadLocationException localBadLocationException) {}
        }
      }
      finally
      {
        AccessibleHTML.this.unlock(localObject1);
      }
      return null;
    }
    
    protected boolean isValid()
    {
      return isValid;
    }
    
    protected AttributeSet getAttributes()
    {
      if (validateIfNecessary()) {
        return getElement().getAttributes();
      }
      return null;
    }
    
    protected AttributeSet getViewAttributes()
    {
      if (validateIfNecessary())
      {
        View localView = getView();
        if (localView != null) {
          return localView.getElement().getAttributes();
        }
        return getElement().getAttributes();
      }
      return null;
    }
    
    protected int getIntAttr(AttributeSet paramAttributeSet, Object paramObject, int paramInt)
    {
      if ((paramAttributeSet != null) && (paramAttributeSet.isDefined(paramObject)))
      {
        String str = (String)paramAttributeSet.getAttribute(paramObject);
        int i;
        if (str == null) {
          i = paramInt;
        } else {
          try
          {
            i = Math.max(0, Integer.parseInt(str));
          }
          catch (NumberFormatException localNumberFormatException)
          {
            i = paramInt;
          }
        }
        return i;
      }
      return paramInt;
    }
    
    /* Error */
    protected boolean validateIfNecessary()
    {
      // Byte code:
      //   0: aload_0
      //   1: invokevirtual 242	javax/swing/text/html/AccessibleHTML$ElementInfo:isValid	()Z
      //   4: ifne +49 -> 53
      //   7: aload_0
      //   8: getfield 214	javax/swing/text/html/AccessibleHTML$ElementInfo:canBeValid	Z
      //   11: ifeq +42 -> 53
      //   14: aload_0
      //   15: aconst_null
      //   16: putfield 216	javax/swing/text/html/AccessibleHTML$ElementInfo:children	Ljava/util/ArrayList;
      //   19: aload_0
      //   20: getfield 218	javax/swing/text/html/AccessibleHTML$ElementInfo:this$0	Ljavax/swing/text/html/AccessibleHTML;
      //   23: invokestatic 235	javax/swing/text/html/AccessibleHTML:access$1300	(Ljavax/swing/text/html/AccessibleHTML;)Ljava/lang/Object;
      //   26: astore_1
      //   27: aload_0
      //   28: invokevirtual 241	javax/swing/text/html/AccessibleHTML$ElementInfo:validate	()V
      //   31: aload_0
      //   32: getfield 218	javax/swing/text/html/AccessibleHTML$ElementInfo:this$0	Ljavax/swing/text/html/AccessibleHTML;
      //   35: aload_1
      //   36: invokestatic 236	javax/swing/text/html/AccessibleHTML:access$1500	(Ljavax/swing/text/html/AccessibleHTML;Ljava/lang/Object;)V
      //   39: goto +14 -> 53
      //   42: astore_2
      //   43: aload_0
      //   44: getfield 218	javax/swing/text/html/AccessibleHTML$ElementInfo:this$0	Ljavax/swing/text/html/AccessibleHTML;
      //   47: aload_1
      //   48: invokestatic 236	javax/swing/text/html/AccessibleHTML:access$1500	(Ljavax/swing/text/html/AccessibleHTML;Ljava/lang/Object;)V
      //   51: aload_2
      //   52: athrow
      //   53: aload_0
      //   54: invokevirtual 242	javax/swing/text/html/AccessibleHTML$ElementInfo:isValid	()Z
      //   57: ireturn
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	58	0	this	ElementInfo
      //   26	22	1	localObject1	Object
      //   42	10	2	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   27	31	42	finally
    }
    
    protected void invalidate(boolean paramBoolean)
    {
      if (!isValid())
      {
        if ((canBeValid) && (!paramBoolean)) {
          canBeValid = false;
        }
        return;
      }
      isValid = false;
      canBeValid = paramBoolean;
      if (children != null)
      {
        Iterator localIterator = children.iterator();
        while (localIterator.hasNext())
        {
          ElementInfo localElementInfo = (ElementInfo)localIterator.next();
          localElementInfo.invalidate(false);
        }
        children = null;
      }
    }
    
    private View getView(View paramView, Element paramElement, int paramInt)
    {
      if (paramView.getElement() == paramElement) {
        return paramView;
      }
      int i = paramView.getViewIndex(paramInt, Position.Bias.Forward);
      if ((i != -1) && (i < paramView.getViewCount())) {
        return getView(paramView.getView(i), paramElement, paramInt);
      }
      return null;
    }
    
    private int getClosestInfoIndex(int paramInt)
    {
      for (int i = 0; i < getChildCount(); i++)
      {
        ElementInfo localElementInfo = getChild(i);
        if ((paramInt < localElementInfo.getElement().getEndOffset()) || (paramInt == localElementInfo.getElement().getStartOffset())) {
          return i;
        }
      }
      return -1;
    }
    
    private void update(DocumentEvent paramDocumentEvent)
    {
      if (!isValid()) {
        return;
      }
      ElementInfo localElementInfo = getParent();
      Element localElement = getElement();
      Object localObject1;
      do
      {
        localObject1 = paramDocumentEvent.getChange(localElement);
        if (localObject1 != null)
        {
          if (localElement == getElement()) {
            invalidate(true);
          } else if (localElementInfo != null) {
            localElementInfo.invalidate(localElementInfo == AccessibleHTML.this.getRootInfo());
          }
          return;
        }
        localElement = localElement.getParentElement();
      } while ((localElementInfo != null) && (localElement != null) && (localElement != localElementInfo.getElement()));
      if (getChildCount() > 0)
      {
        localObject1 = getElement();
        int i = paramDocumentEvent.getOffset();
        int j = getClosestInfoIndex(i);
        if ((j == -1) && (paramDocumentEvent.getType() == DocumentEvent.EventType.REMOVE) && (i >= ((Element)localObject1).getEndOffset())) {
          j = getChildCount() - 1;
        }
        Object localObject2 = j >= 0 ? getChild(j) : null;
        if ((localObject2 != null) && (((ElementInfo)localObject2).getElement().getStartOffset() == i) && (i > 0)) {
          j = Math.max(j - 1, 0);
        }
        int k;
        if (paramDocumentEvent.getType() != DocumentEvent.EventType.REMOVE)
        {
          k = getClosestInfoIndex(i + paramDocumentEvent.getLength());
          if (k < 0) {
            k = getChildCount() - 1;
          }
        }
        else
        {
          for (k = j; (k + 1 < getChildCount()) && (getChild(k + 1).getElement().getEndOffset() == getChild(k + 1).getElement().getStartOffset()); k++) {}
        }
        j = Math.max(j, 0);
        for (int m = j; (m <= k) && (isValid()); m++) {
          getChild(m).update(paramDocumentEvent);
        }
      }
    }
  }
  
  protected abstract class HTMLAccessibleContext
    extends AccessibleContext
    implements Accessible, AccessibleComponent
  {
    protected AccessibleHTML.ElementInfo elementInfo;
    
    public HTMLAccessibleContext(AccessibleHTML.ElementInfo paramElementInfo)
    {
      elementInfo = paramElementInfo;
    }
    
    public AccessibleContext getAccessibleContext()
    {
      return this;
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = new AccessibleStateSet();
      JEditorPane localJEditorPane = AccessibleHTML.this.getTextComponent();
      if (localJEditorPane.isEnabled()) {
        localAccessibleStateSet.add(AccessibleState.ENABLED);
      }
      if (((localJEditorPane instanceof JTextComponent)) && (((JTextComponent)localJEditorPane).isEditable()))
      {
        localAccessibleStateSet.add(AccessibleState.EDITABLE);
        localAccessibleStateSet.add(AccessibleState.FOCUSABLE);
      }
      if (localJEditorPane.isVisible()) {
        localAccessibleStateSet.add(AccessibleState.VISIBLE);
      }
      if (localJEditorPane.isShowing()) {
        localAccessibleStateSet.add(AccessibleState.SHOWING);
      }
      return localAccessibleStateSet;
    }
    
    public int getAccessibleIndexInParent()
    {
      return elementInfo.getIndexInParent();
    }
    
    public int getAccessibleChildrenCount()
    {
      return elementInfo.getChildCount();
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      AccessibleHTML.ElementInfo localElementInfo = elementInfo.getChild(paramInt);
      if ((localElementInfo != null) && ((localElementInfo instanceof Accessible))) {
        return (Accessible)localElementInfo;
      }
      return null;
    }
    
    public Locale getLocale()
      throws IllegalComponentStateException
    {
      return editor.getLocale();
    }
    
    public AccessibleComponent getAccessibleComponent()
    {
      return this;
    }
    
    public Color getBackground()
    {
      return AccessibleHTML.this.getTextComponent().getBackground();
    }
    
    public void setBackground(Color paramColor)
    {
      AccessibleHTML.this.getTextComponent().setBackground(paramColor);
    }
    
    public Color getForeground()
    {
      return AccessibleHTML.this.getTextComponent().getForeground();
    }
    
    public void setForeground(Color paramColor)
    {
      AccessibleHTML.this.getTextComponent().setForeground(paramColor);
    }
    
    public Cursor getCursor()
    {
      return AccessibleHTML.this.getTextComponent().getCursor();
    }
    
    public void setCursor(Cursor paramCursor)
    {
      AccessibleHTML.this.getTextComponent().setCursor(paramCursor);
    }
    
    public Font getFont()
    {
      return AccessibleHTML.this.getTextComponent().getFont();
    }
    
    public void setFont(Font paramFont)
    {
      AccessibleHTML.this.getTextComponent().setFont(paramFont);
    }
    
    public FontMetrics getFontMetrics(Font paramFont)
    {
      return AccessibleHTML.this.getTextComponent().getFontMetrics(paramFont);
    }
    
    public boolean isEnabled()
    {
      return AccessibleHTML.this.getTextComponent().isEnabled();
    }
    
    public void setEnabled(boolean paramBoolean)
    {
      AccessibleHTML.this.getTextComponent().setEnabled(paramBoolean);
    }
    
    public boolean isVisible()
    {
      return AccessibleHTML.this.getTextComponent().isVisible();
    }
    
    public void setVisible(boolean paramBoolean)
    {
      AccessibleHTML.this.getTextComponent().setVisible(paramBoolean);
    }
    
    public boolean isShowing()
    {
      return AccessibleHTML.this.getTextComponent().isShowing();
    }
    
    public boolean contains(Point paramPoint)
    {
      Rectangle localRectangle = getBounds();
      if (localRectangle != null) {
        return localRectangle.contains(x, y);
      }
      return false;
    }
    
    public Point getLocationOnScreen()
    {
      Point localPoint = AccessibleHTML.this.getTextComponent().getLocationOnScreen();
      Rectangle localRectangle = getBounds();
      if (localRectangle != null) {
        return new Point(x + x, y + y);
      }
      return null;
    }
    
    public Point getLocation()
    {
      Rectangle localRectangle = getBounds();
      if (localRectangle != null) {
        return new Point(x, y);
      }
      return null;
    }
    
    public void setLocation(Point paramPoint) {}
    
    public Rectangle getBounds()
    {
      return elementInfo.getBounds();
    }
    
    public void setBounds(Rectangle paramRectangle) {}
    
    public Dimension getSize()
    {
      Rectangle localRectangle = getBounds();
      if (localRectangle != null) {
        return new Dimension(width, height);
      }
      return null;
    }
    
    public void setSize(Dimension paramDimension)
    {
      JEditorPane localJEditorPane = AccessibleHTML.this.getTextComponent();
      localJEditorPane.setSize(paramDimension);
    }
    
    public Accessible getAccessibleAt(Point paramPoint)
    {
      AccessibleHTML.ElementInfo localElementInfo = getElementInfoAt(rootElementInfo, paramPoint);
      if ((localElementInfo instanceof Accessible)) {
        return (Accessible)localElementInfo;
      }
      return null;
    }
    
    private AccessibleHTML.ElementInfo getElementInfoAt(AccessibleHTML.ElementInfo paramElementInfo, Point paramPoint)
    {
      if (paramElementInfo.getBounds() == null) {
        return null;
      }
      if ((paramElementInfo.getChildCount() == 0) && (paramElementInfo.getBounds().contains(paramPoint))) {
        return paramElementInfo;
      }
      Object localObject;
      if ((paramElementInfo instanceof AccessibleHTML.TableElementInfo))
      {
        AccessibleHTML.ElementInfo localElementInfo1 = ((AccessibleHTML.TableElementInfo)paramElementInfo).getCaptionInfo();
        if (localElementInfo1 != null)
        {
          localObject = localElementInfo1.getBounds();
          if ((localObject != null) && (((Rectangle)localObject).contains(paramPoint))) {
            return localElementInfo1;
          }
        }
      }
      for (int i = 0; i < paramElementInfo.getChildCount(); i++)
      {
        localObject = paramElementInfo.getChild(i);
        AccessibleHTML.ElementInfo localElementInfo2 = getElementInfoAt((AccessibleHTML.ElementInfo)localObject, paramPoint);
        if (localElementInfo2 != null) {
          return localElementInfo2;
        }
      }
      return null;
    }
    
    public boolean isFocusTraversable()
    {
      JEditorPane localJEditorPane = AccessibleHTML.this.getTextComponent();
      return ((localJEditorPane instanceof JTextComponent)) && (((JTextComponent)localJEditorPane).isEditable());
    }
    
    public void requestFocus()
    {
      if (!isFocusTraversable()) {
        return;
      }
      JEditorPane localJEditorPane = AccessibleHTML.this.getTextComponent();
      if ((localJEditorPane instanceof JTextComponent))
      {
        localJEditorPane.requestFocusInWindow();
        try
        {
          if (elementInfo.validateIfNecessary())
          {
            Element localElement = elementInfo.getElement();
            ((JTextComponent)localJEditorPane).setCaretPosition(localElement.getStartOffset());
            AccessibleContext localAccessibleContext = editor.getAccessibleContext();
            PropertyChangeEvent localPropertyChangeEvent = new PropertyChangeEvent(this, "AccessibleState", null, AccessibleState.FOCUSED);
            localAccessibleContext.firePropertyChange("AccessibleState", null, localPropertyChangeEvent);
          }
        }
        catch (IllegalArgumentException localIllegalArgumentException) {}
      }
    }
    
    public void addFocusListener(FocusListener paramFocusListener)
    {
      AccessibleHTML.this.getTextComponent().addFocusListener(paramFocusListener);
    }
    
    public void removeFocusListener(FocusListener paramFocusListener)
    {
      AccessibleHTML.this.getTextComponent().removeFocusListener(paramFocusListener);
    }
  }
  
  private class IconElementInfo
    extends AccessibleHTML.ElementInfo
    implements Accessible
  {
    private int width = -1;
    private int height = -1;
    private AccessibleContext accessibleContext;
    
    IconElementInfo(Element paramElement, AccessibleHTML.ElementInfo paramElementInfo)
    {
      super(paramElement, paramElementInfo);
    }
    
    protected void invalidate(boolean paramBoolean)
    {
      super.invalidate(paramBoolean);
      width = (height = -1);
    }
    
    private int getImageSize(Object paramObject)
    {
      if (validateIfNecessary())
      {
        int i = getIntAttr(getAttributes(), paramObject, -1);
        if (i == -1)
        {
          View localView = getView();
          i = 0;
          if ((localView instanceof ImageView))
          {
            Image localImage = ((ImageView)localView).getImage();
            if (localImage != null) {
              if (paramObject == HTML.Attribute.WIDTH) {
                i = localImage.getWidth(null);
              } else {
                i = localImage.getHeight(null);
              }
            }
          }
        }
        return i;
      }
      return 0;
    }
    
    public AccessibleContext getAccessibleContext()
    {
      if (accessibleContext == null) {
        accessibleContext = new IconAccessibleContext(this);
      }
      return accessibleContext;
    }
    
    protected class IconAccessibleContext
      extends AccessibleHTML.HTMLAccessibleContext
      implements AccessibleIcon
    {
      public IconAccessibleContext(AccessibleHTML.ElementInfo paramElementInfo)
      {
        super(paramElementInfo);
      }
      
      public String getAccessibleName()
      {
        return getAccessibleIconDescription();
      }
      
      public String getAccessibleDescription()
      {
        return editor.getContentType();
      }
      
      public AccessibleRole getAccessibleRole()
      {
        return AccessibleRole.ICON;
      }
      
      public AccessibleIcon[] getAccessibleIcon()
      {
        AccessibleIcon[] arrayOfAccessibleIcon = new AccessibleIcon[1];
        arrayOfAccessibleIcon[0] = this;
        return arrayOfAccessibleIcon;
      }
      
      public String getAccessibleIconDescription()
      {
        return ((ImageView)getView()).getAltText();
      }
      
      public void setAccessibleIconDescription(String paramString) {}
      
      public int getAccessibleIconWidth()
      {
        if (width == -1) {
          width = AccessibleHTML.IconElementInfo.this.getImageSize(HTML.Attribute.WIDTH);
        }
        return width;
      }
      
      public int getAccessibleIconHeight()
      {
        if (height == -1) {
          height = AccessibleHTML.IconElementInfo.this.getImageSize(HTML.Attribute.HEIGHT);
        }
        return height;
      }
    }
  }
  
  private class PropertyChangeHandler
    implements PropertyChangeListener
  {
    private PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      if (paramPropertyChangeEvent.getPropertyName().equals("document")) {
        AccessibleHTML.this.setDocument(editor.getDocument());
      }
    }
  }
  
  private class RootHTMLAccessibleContext
    extends AccessibleHTML.HTMLAccessibleContext
  {
    public RootHTMLAccessibleContext(AccessibleHTML.ElementInfo paramElementInfo)
    {
      super(paramElementInfo);
    }
    
    public String getAccessibleName()
    {
      if (model != null) {
        return (String)model.getProperty("title");
      }
      return null;
    }
    
    public String getAccessibleDescription()
    {
      return editor.getContentType();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.TEXT;
    }
  }
  
  private class TableElementInfo
    extends AccessibleHTML.ElementInfo
    implements Accessible
  {
    protected AccessibleHTML.ElementInfo caption;
    private TableCellElementInfo[][] grid;
    private AccessibleContext accessibleContext;
    
    TableElementInfo(Element paramElement, AccessibleHTML.ElementInfo paramElementInfo)
    {
      super(paramElement, paramElementInfo);
    }
    
    public AccessibleHTML.ElementInfo getCaptionInfo()
    {
      return caption;
    }
    
    protected void validate()
    {
      super.validate();
      updateGrid();
    }
    
    protected void loadChildren(Element paramElement)
    {
      for (int i = 0; i < paramElement.getElementCount(); i++)
      {
        Element localElement = paramElement.getElement(i);
        AttributeSet localAttributeSet = localElement.getAttributes();
        if (localAttributeSet.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.TR) {
          addChild(new TableRowElementInfo(localElement, this, i));
        } else if (localAttributeSet.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.CAPTION) {
          caption = createElementInfo(localElement, this);
        }
      }
    }
    
    private void updateGrid()
    {
      int i = 0;
      int j = 0;
      for (int m = 0; m < getChildCount(); m++)
      {
        TableRowElementInfo localTableRowElementInfo = getRow(m);
        int n = 0;
        for (int i1 = 0; i1 < i; i1++) {
          n = Math.max(n, getRow(m - i1 - 1).getColumnCount(i1 + 2));
        }
        i = Math.max(localTableRowElementInfo.getRowCount(), i);
        i--;
        j = Math.max(j, localTableRowElementInfo.getColumnCount() + n);
      }
      int k = getChildCount() + i;
      grid = new TableCellElementInfo[k][];
      for (m = 0; m < k; m++) {
        grid[m] = new TableCellElementInfo[j];
      }
      for (m = 0; m < k; m++) {
        getRow(m).updateGrid(m);
      }
    }
    
    public TableRowElementInfo getRow(int paramInt)
    {
      return (TableRowElementInfo)getChild(paramInt);
    }
    
    public TableCellElementInfo getCell(int paramInt1, int paramInt2)
    {
      if ((validateIfNecessary()) && (paramInt1 < grid.length) && (paramInt2 < grid[0].length)) {
        return grid[paramInt1][paramInt2];
      }
      return null;
    }
    
    public int getRowExtentAt(int paramInt1, int paramInt2)
    {
      TableCellElementInfo localTableCellElementInfo = getCell(paramInt1, paramInt2);
      if (localTableCellElementInfo != null)
      {
        int i = localTableCellElementInfo.getRowCount();
        for (int j = 1; (paramInt1 - j >= 0) && (grid[(paramInt1 - j)][paramInt2] == localTableCellElementInfo); j++) {}
        return i - j + 1;
      }
      return 0;
    }
    
    public int getColumnExtentAt(int paramInt1, int paramInt2)
    {
      TableCellElementInfo localTableCellElementInfo = getCell(paramInt1, paramInt2);
      if (localTableCellElementInfo != null)
      {
        int i = localTableCellElementInfo.getColumnCount();
        for (int j = 1; (paramInt2 - j >= 0) && (grid[paramInt1][(paramInt2 - j)] == localTableCellElementInfo); j++) {}
        return i - j + 1;
      }
      return 0;
    }
    
    public int getRowCount()
    {
      if (validateIfNecessary()) {
        return grid.length;
      }
      return 0;
    }
    
    public int getColumnCount()
    {
      if ((validateIfNecessary()) && (grid.length > 0)) {
        return grid[0].length;
      }
      return 0;
    }
    
    public AccessibleContext getAccessibleContext()
    {
      if (accessibleContext == null) {
        accessibleContext = new TableAccessibleContext(this);
      }
      return accessibleContext;
    }
    
    public class TableAccessibleContext
      extends AccessibleHTML.HTMLAccessibleContext
      implements AccessibleTable
    {
      private AccessibleHeadersTable rowHeadersTable;
      
      public TableAccessibleContext(AccessibleHTML.ElementInfo paramElementInfo)
      {
        super(paramElementInfo);
      }
      
      public String getAccessibleName()
      {
        return getAccessibleRole().toString();
      }
      
      public String getAccessibleDescription()
      {
        return editor.getContentType();
      }
      
      public AccessibleRole getAccessibleRole()
      {
        return AccessibleRole.TABLE;
      }
      
      public int getAccessibleIndexInParent()
      {
        return elementInfo.getIndexInParent();
      }
      
      public int getAccessibleChildrenCount()
      {
        return ((AccessibleHTML.TableElementInfo)elementInfo).getRowCount() * ((AccessibleHTML.TableElementInfo)elementInfo).getColumnCount();
      }
      
      public Accessible getAccessibleChild(int paramInt)
      {
        int i = ((AccessibleHTML.TableElementInfo)elementInfo).getRowCount();
        int j = ((AccessibleHTML.TableElementInfo)elementInfo).getColumnCount();
        int k = paramInt / i;
        int m = paramInt % j;
        if ((k < 0) || (k >= i) || (m < 0) || (m >= j)) {
          return null;
        }
        return getAccessibleAt(k, m);
      }
      
      public AccessibleTable getAccessibleTable()
      {
        return this;
      }
      
      public Accessible getAccessibleCaption()
      {
        AccessibleHTML.ElementInfo localElementInfo = getCaptionInfo();
        if ((localElementInfo instanceof Accessible)) {
          return (Accessible)caption;
        }
        return null;
      }
      
      public void setAccessibleCaption(Accessible paramAccessible) {}
      
      public Accessible getAccessibleSummary()
      {
        return null;
      }
      
      public void setAccessibleSummary(Accessible paramAccessible) {}
      
      public int getAccessibleRowCount()
      {
        return ((AccessibleHTML.TableElementInfo)elementInfo).getRowCount();
      }
      
      public int getAccessibleColumnCount()
      {
        return ((AccessibleHTML.TableElementInfo)elementInfo).getColumnCount();
      }
      
      public Accessible getAccessibleAt(int paramInt1, int paramInt2)
      {
        AccessibleHTML.TableElementInfo.TableCellElementInfo localTableCellElementInfo = getCell(paramInt1, paramInt2);
        if (localTableCellElementInfo != null) {
          return localTableCellElementInfo.getAccessible();
        }
        return null;
      }
      
      public int getAccessibleRowExtentAt(int paramInt1, int paramInt2)
      {
        return ((AccessibleHTML.TableElementInfo)elementInfo).getRowExtentAt(paramInt1, paramInt2);
      }
      
      public int getAccessibleColumnExtentAt(int paramInt1, int paramInt2)
      {
        return ((AccessibleHTML.TableElementInfo)elementInfo).getColumnExtentAt(paramInt1, paramInt2);
      }
      
      public AccessibleTable getAccessibleRowHeader()
      {
        return rowHeadersTable;
      }
      
      public void setAccessibleRowHeader(AccessibleTable paramAccessibleTable) {}
      
      public AccessibleTable getAccessibleColumnHeader()
      {
        return null;
      }
      
      public void setAccessibleColumnHeader(AccessibleTable paramAccessibleTable) {}
      
      public Accessible getAccessibleRowDescription(int paramInt)
      {
        return null;
      }
      
      public void setAccessibleRowDescription(int paramInt, Accessible paramAccessible) {}
      
      public Accessible getAccessibleColumnDescription(int paramInt)
      {
        return null;
      }
      
      public void setAccessibleColumnDescription(int paramInt, Accessible paramAccessible) {}
      
      public boolean isAccessibleSelected(int paramInt1, int paramInt2)
      {
        if (validateIfNecessary())
        {
          if ((paramInt1 < 0) || (paramInt1 >= getAccessibleRowCount()) || (paramInt2 < 0) || (paramInt2 >= getAccessibleColumnCount())) {
            return false;
          }
          AccessibleHTML.TableElementInfo.TableCellElementInfo localTableCellElementInfo = getCell(paramInt1, paramInt2);
          if (localTableCellElementInfo != null)
          {
            Element localElement = localTableCellElementInfo.getElement();
            int i = localElement.getStartOffset();
            int j = localElement.getEndOffset();
            return (i >= editor.getSelectionStart()) && (j <= editor.getSelectionEnd());
          }
        }
        return false;
      }
      
      public boolean isAccessibleRowSelected(int paramInt)
      {
        if (validateIfNecessary())
        {
          if ((paramInt < 0) || (paramInt >= getAccessibleRowCount())) {
            return false;
          }
          int i = getAccessibleColumnCount();
          AccessibleHTML.TableElementInfo.TableCellElementInfo localTableCellElementInfo1 = getCell(paramInt, 0);
          if (localTableCellElementInfo1 == null) {
            return false;
          }
          int j = localTableCellElementInfo1.getElement().getStartOffset();
          AccessibleHTML.TableElementInfo.TableCellElementInfo localTableCellElementInfo2 = getCell(paramInt, i - 1);
          if (localTableCellElementInfo2 == null) {
            return false;
          }
          int k = localTableCellElementInfo2.getElement().getEndOffset();
          return (j >= editor.getSelectionStart()) && (k <= editor.getSelectionEnd());
        }
        return false;
      }
      
      public boolean isAccessibleColumnSelected(int paramInt)
      {
        if (validateIfNecessary())
        {
          if ((paramInt < 0) || (paramInt >= getAccessibleColumnCount())) {
            return false;
          }
          int i = getAccessibleRowCount();
          AccessibleHTML.TableElementInfo.TableCellElementInfo localTableCellElementInfo1 = getCell(0, paramInt);
          if (localTableCellElementInfo1 == null) {
            return false;
          }
          int j = localTableCellElementInfo1.getElement().getStartOffset();
          AccessibleHTML.TableElementInfo.TableCellElementInfo localTableCellElementInfo2 = getCell(i - 1, paramInt);
          if (localTableCellElementInfo2 == null) {
            return false;
          }
          int k = localTableCellElementInfo2.getElement().getEndOffset();
          return (j >= editor.getSelectionStart()) && (k <= editor.getSelectionEnd());
        }
        return false;
      }
      
      public int[] getSelectedAccessibleRows()
      {
        if (validateIfNecessary())
        {
          int i = getAccessibleRowCount();
          Vector localVector = new Vector();
          for (int j = 0; j < i; j++) {
            if (isAccessibleRowSelected(j)) {
              localVector.addElement(Integer.valueOf(j));
            }
          }
          int[] arrayOfInt = new int[localVector.size()];
          for (int k = 0; k < arrayOfInt.length; k++) {
            arrayOfInt[k] = ((Integer)localVector.elementAt(k)).intValue();
          }
          return arrayOfInt;
        }
        return new int[0];
      }
      
      public int[] getSelectedAccessibleColumns()
      {
        if (validateIfNecessary())
        {
          int i = getAccessibleRowCount();
          Vector localVector = new Vector();
          for (int j = 0; j < i; j++) {
            if (isAccessibleColumnSelected(j)) {
              localVector.addElement(Integer.valueOf(j));
            }
          }
          int[] arrayOfInt = new int[localVector.size()];
          for (int k = 0; k < arrayOfInt.length; k++) {
            arrayOfInt[k] = ((Integer)localVector.elementAt(k)).intValue();
          }
          return arrayOfInt;
        }
        return new int[0];
      }
      
      public int getAccessibleRow(int paramInt)
      {
        if (validateIfNecessary())
        {
          int i = getAccessibleColumnCount() * getAccessibleRowCount();
          if (paramInt >= i) {
            return -1;
          }
          return paramInt / getAccessibleColumnCount();
        }
        return -1;
      }
      
      public int getAccessibleColumn(int paramInt)
      {
        if (validateIfNecessary())
        {
          int i = getAccessibleColumnCount() * getAccessibleRowCount();
          if (paramInt >= i) {
            return -1;
          }
          return paramInt % getAccessibleColumnCount();
        }
        return -1;
      }
      
      public int getAccessibleIndex(int paramInt1, int paramInt2)
      {
        if (validateIfNecessary())
        {
          if ((paramInt1 >= getAccessibleRowCount()) || (paramInt2 >= getAccessibleColumnCount())) {
            return -1;
          }
          return paramInt1 * getAccessibleColumnCount() + paramInt2;
        }
        return -1;
      }
      
      public String getAccessibleRowHeader(int paramInt)
      {
        if (validateIfNecessary())
        {
          AccessibleHTML.TableElementInfo.TableCellElementInfo localTableCellElementInfo = getCell(paramInt, 0);
          if (localTableCellElementInfo.isHeaderCell())
          {
            View localView = localTableCellElementInfo.getView();
            if ((localView != null) && (model != null)) {
              try
              {
                return model.getText(localView.getStartOffset(), localView.getEndOffset() - localView.getStartOffset());
              }
              catch (BadLocationException localBadLocationException)
              {
                return null;
              }
            }
          }
        }
        return null;
      }
      
      public String getAccessibleColumnHeader(int paramInt)
      {
        if (validateIfNecessary())
        {
          AccessibleHTML.TableElementInfo.TableCellElementInfo localTableCellElementInfo = getCell(0, paramInt);
          if (localTableCellElementInfo.isHeaderCell())
          {
            View localView = localTableCellElementInfo.getView();
            if ((localView != null) && (model != null)) {
              try
              {
                return model.getText(localView.getStartOffset(), localView.getEndOffset() - localView.getStartOffset());
              }
              catch (BadLocationException localBadLocationException)
              {
                return null;
              }
            }
          }
        }
        return null;
      }
      
      public void addRowHeader(AccessibleHTML.TableElementInfo.TableCellElementInfo paramTableCellElementInfo, int paramInt)
      {
        if (rowHeadersTable == null) {
          rowHeadersTable = new AccessibleHeadersTable();
        }
        rowHeadersTable.addHeader(paramTableCellElementInfo, paramInt);
      }
      
      protected class AccessibleHeadersTable
        implements AccessibleTable
      {
        private Hashtable<Integer, ArrayList<AccessibleHTML.TableElementInfo.TableCellElementInfo>> headers = new Hashtable();
        private int rowCount = 0;
        private int columnCount = 0;
        
        protected AccessibleHeadersTable() {}
        
        public void addHeader(AccessibleHTML.TableElementInfo.TableCellElementInfo paramTableCellElementInfo, int paramInt)
        {
          Integer localInteger = Integer.valueOf(paramInt);
          ArrayList localArrayList = (ArrayList)headers.get(localInteger);
          if (localArrayList == null)
          {
            localArrayList = new ArrayList();
            headers.put(localInteger, localArrayList);
          }
          localArrayList.add(paramTableCellElementInfo);
        }
        
        public Accessible getAccessibleCaption()
        {
          return null;
        }
        
        public void setAccessibleCaption(Accessible paramAccessible) {}
        
        public Accessible getAccessibleSummary()
        {
          return null;
        }
        
        public void setAccessibleSummary(Accessible paramAccessible) {}
        
        public int getAccessibleRowCount()
        {
          return rowCount;
        }
        
        public int getAccessibleColumnCount()
        {
          return columnCount;
        }
        
        private AccessibleHTML.TableElementInfo.TableCellElementInfo getElementInfoAt(int paramInt1, int paramInt2)
        {
          ArrayList localArrayList = (ArrayList)headers.get(Integer.valueOf(paramInt1));
          if (localArrayList != null) {
            return (AccessibleHTML.TableElementInfo.TableCellElementInfo)localArrayList.get(paramInt2);
          }
          return null;
        }
        
        public Accessible getAccessibleAt(int paramInt1, int paramInt2)
        {
          AccessibleHTML.TableElementInfo.TableCellElementInfo localTableCellElementInfo = getElementInfoAt(paramInt1, paramInt2);
          if ((localTableCellElementInfo instanceof Accessible)) {
            return (Accessible)localTableCellElementInfo;
          }
          return null;
        }
        
        public int getAccessibleRowExtentAt(int paramInt1, int paramInt2)
        {
          AccessibleHTML.TableElementInfo.TableCellElementInfo localTableCellElementInfo = getElementInfoAt(paramInt1, paramInt2);
          if (localTableCellElementInfo != null) {
            return localTableCellElementInfo.getRowCount();
          }
          return 0;
        }
        
        public int getAccessibleColumnExtentAt(int paramInt1, int paramInt2)
        {
          AccessibleHTML.TableElementInfo.TableCellElementInfo localTableCellElementInfo = getElementInfoAt(paramInt1, paramInt2);
          if (localTableCellElementInfo != null) {
            return localTableCellElementInfo.getRowCount();
          }
          return 0;
        }
        
        public AccessibleTable getAccessibleRowHeader()
        {
          return null;
        }
        
        public void setAccessibleRowHeader(AccessibleTable paramAccessibleTable) {}
        
        public AccessibleTable getAccessibleColumnHeader()
        {
          return null;
        }
        
        public void setAccessibleColumnHeader(AccessibleTable paramAccessibleTable) {}
        
        public Accessible getAccessibleRowDescription(int paramInt)
        {
          return null;
        }
        
        public void setAccessibleRowDescription(int paramInt, Accessible paramAccessible) {}
        
        public Accessible getAccessibleColumnDescription(int paramInt)
        {
          return null;
        }
        
        public void setAccessibleColumnDescription(int paramInt, Accessible paramAccessible) {}
        
        public boolean isAccessibleSelected(int paramInt1, int paramInt2)
        {
          return false;
        }
        
        public boolean isAccessibleRowSelected(int paramInt)
        {
          return false;
        }
        
        public boolean isAccessibleColumnSelected(int paramInt)
        {
          return false;
        }
        
        public int[] getSelectedAccessibleRows()
        {
          return new int[0];
        }
        
        public int[] getSelectedAccessibleColumns()
        {
          return new int[0];
        }
      }
    }
    
    private class TableCellElementInfo
      extends AccessibleHTML.ElementInfo
    {
      private Accessible accessible;
      private boolean isHeaderCell;
      
      TableCellElementInfo(Element paramElement, AccessibleHTML.ElementInfo paramElementInfo)
      {
        super(paramElement, paramElementInfo);
        isHeaderCell = false;
      }
      
      TableCellElementInfo(Element paramElement, AccessibleHTML.ElementInfo paramElementInfo, boolean paramBoolean)
      {
        super(paramElement, paramElementInfo);
        isHeaderCell = paramBoolean;
      }
      
      public boolean isHeaderCell()
      {
        return isHeaderCell;
      }
      
      public Accessible getAccessible()
      {
        accessible = null;
        getAccessible(this);
        return accessible;
      }
      
      private void getAccessible(AccessibleHTML.ElementInfo paramElementInfo)
      {
        if ((paramElementInfo instanceof Accessible)) {
          accessible = ((Accessible)paramElementInfo);
        } else {
          for (int i = 0; i < paramElementInfo.getChildCount(); i++) {
            getAccessible(paramElementInfo.getChild(i));
          }
        }
      }
      
      public int getRowCount()
      {
        if (validateIfNecessary()) {
          return Math.max(1, getIntAttr(getAttributes(), HTML.Attribute.ROWSPAN, 1));
        }
        return 0;
      }
      
      public int getColumnCount()
      {
        if (validateIfNecessary()) {
          return Math.max(1, getIntAttr(getAttributes(), HTML.Attribute.COLSPAN, 1));
        }
        return 0;
      }
      
      protected void invalidate(boolean paramBoolean)
      {
        super.invalidate(paramBoolean);
        getParent().invalidate(true);
      }
    }
    
    private class TableRowElementInfo
      extends AccessibleHTML.ElementInfo
    {
      private AccessibleHTML.TableElementInfo parent;
      private int rowNumber;
      
      TableRowElementInfo(Element paramElement, AccessibleHTML.TableElementInfo paramTableElementInfo, int paramInt)
      {
        super(paramElement, paramTableElementInfo);
        parent = paramTableElementInfo;
        rowNumber = paramInt;
      }
      
      protected void loadChildren(Element paramElement)
      {
        for (int i = 0; i < paramElement.getElementCount(); i++)
        {
          AttributeSet localAttributeSet = paramElement.getElement(i).getAttributes();
          if (localAttributeSet.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.TH)
          {
            AccessibleHTML.TableElementInfo.TableCellElementInfo localTableCellElementInfo = new AccessibleHTML.TableElementInfo.TableCellElementInfo(AccessibleHTML.TableElementInfo.this, paramElement.getElement(i), this, true);
            addChild(localTableCellElementInfo);
            AccessibleTable localAccessibleTable = parent.getAccessibleContext().getAccessibleTable();
            AccessibleHTML.TableElementInfo.TableAccessibleContext localTableAccessibleContext = (AccessibleHTML.TableElementInfo.TableAccessibleContext)localAccessibleTable;
            localTableAccessibleContext.addRowHeader(localTableCellElementInfo, rowNumber);
          }
          else if (localAttributeSet.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.TD)
          {
            addChild(new AccessibleHTML.TableElementInfo.TableCellElementInfo(AccessibleHTML.TableElementInfo.this, paramElement.getElement(i), this, false));
          }
        }
      }
      
      public int getRowCount()
      {
        int i = 1;
        if (validateIfNecessary()) {
          for (int j = 0; j < getChildCount(); j++)
          {
            AccessibleHTML.TableElementInfo.TableCellElementInfo localTableCellElementInfo = (AccessibleHTML.TableElementInfo.TableCellElementInfo)getChild(j);
            if (localTableCellElementInfo.validateIfNecessary()) {
              i = Math.max(i, localTableCellElementInfo.getRowCount());
            }
          }
        }
        return i;
      }
      
      public int getColumnCount()
      {
        int i = 0;
        if (validateIfNecessary()) {
          for (int j = 0; j < getChildCount(); j++)
          {
            AccessibleHTML.TableElementInfo.TableCellElementInfo localTableCellElementInfo = (AccessibleHTML.TableElementInfo.TableCellElementInfo)getChild(j);
            if (localTableCellElementInfo.validateIfNecessary()) {
              i += localTableCellElementInfo.getColumnCount();
            }
          }
        }
        return i;
      }
      
      protected void invalidate(boolean paramBoolean)
      {
        super.invalidate(paramBoolean);
        getParent().invalidate(true);
      }
      
      private void updateGrid(int paramInt)
      {
        if (validateIfNecessary())
        {
          int i = 0;
          while (i == 0)
          {
            for (j = 0; j < grid[paramInt].length; j++) {
              if (grid[paramInt][j] == null)
              {
                i = 1;
                break;
              }
            }
            if (i == 0) {
              paramInt++;
            }
          }
          int j = 0;
          for (int k = 0; k < getChildCount(); k++)
          {
            AccessibleHTML.TableElementInfo.TableCellElementInfo localTableCellElementInfo = (AccessibleHTML.TableElementInfo.TableCellElementInfo)getChild(k);
            while (grid[paramInt][j] != null) {
              j++;
            }
            for (int m = localTableCellElementInfo.getRowCount() - 1; m >= 0; m--) {
              for (int n = localTableCellElementInfo.getColumnCount() - 1; n >= 0; n--) {
                grid[(paramInt + m)][(j + n)] = localTableCellElementInfo;
              }
            }
            j += localTableCellElementInfo.getColumnCount();
          }
        }
      }
      
      private int getColumnCount(int paramInt)
      {
        if (validateIfNecessary())
        {
          int i = 0;
          for (int j = 0; j < getChildCount(); j++)
          {
            AccessibleHTML.TableElementInfo.TableCellElementInfo localTableCellElementInfo = (AccessibleHTML.TableElementInfo.TableCellElementInfo)getChild(j);
            if (localTableCellElementInfo.getRowCount() >= paramInt) {
              i += localTableCellElementInfo.getColumnCount();
            }
          }
          return i;
        }
        return 0;
      }
    }
  }
  
  class TextElementInfo
    extends AccessibleHTML.ElementInfo
    implements Accessible
  {
    private AccessibleContext accessibleContext;
    
    TextElementInfo(Element paramElement, AccessibleHTML.ElementInfo paramElementInfo)
    {
      super(paramElement, paramElementInfo);
    }
    
    public AccessibleContext getAccessibleContext()
    {
      if (accessibleContext == null) {
        accessibleContext = new TextAccessibleContext(this);
      }
      return accessibleContext;
    }
    
    public class TextAccessibleContext
      extends AccessibleHTML.HTMLAccessibleContext
      implements AccessibleText
    {
      public TextAccessibleContext(AccessibleHTML.ElementInfo paramElementInfo)
      {
        super(paramElementInfo);
      }
      
      public AccessibleText getAccessibleText()
      {
        return this;
      }
      
      public String getAccessibleName()
      {
        if (model != null) {
          return (String)model.getProperty("title");
        }
        return null;
      }
      
      public String getAccessibleDescription()
      {
        return editor.getContentType();
      }
      
      public AccessibleRole getAccessibleRole()
      {
        return AccessibleRole.TEXT;
      }
      
      public int getIndexAtPoint(Point paramPoint)
      {
        View localView = getView();
        if (localView != null) {
          return localView.viewToModel(x, y, getBounds());
        }
        return -1;
      }
      
      public Rectangle getCharacterBounds(int paramInt)
      {
        try
        {
          return editor.getUI().modelToView(editor, paramInt);
        }
        catch (BadLocationException localBadLocationException) {}
        return null;
      }
      
      public int getCharCount()
      {
        if (validateIfNecessary())
        {
          Element localElement = elementInfo.getElement();
          return localElement.getEndOffset() - localElement.getStartOffset();
        }
        return 0;
      }
      
      public int getCaretPosition()
      {
        View localView = getView();
        if (localView == null) {
          return -1;
        }
        Container localContainer = localView.getContainer();
        if (localContainer == null) {
          return -1;
        }
        if ((localContainer instanceof JTextComponent)) {
          return ((JTextComponent)localContainer).getCaretPosition();
        }
        return -1;
      }
      
      public String getAtIndex(int paramInt1, int paramInt2)
      {
        return getAtIndex(paramInt1, paramInt2, 0);
      }
      
      public String getAfterIndex(int paramInt1, int paramInt2)
      {
        return getAtIndex(paramInt1, paramInt2, 1);
      }
      
      public String getBeforeIndex(int paramInt1, int paramInt2)
      {
        return getAtIndex(paramInt1, paramInt2, -1);
      }
      
      private String getAtIndex(int paramInt1, int paramInt2, int paramInt3)
      {
        if ((model instanceof AbstractDocument)) {
          ((AbstractDocument)model).readLock();
        }
        try
        {
          Object localObject1;
          if ((paramInt2 < 0) || (paramInt2 >= model.getLength()))
          {
            localObject1 = null;
            return (String)localObject1;
          }
          switch (paramInt1)
          {
          case 1: 
            if ((paramInt2 + paramInt3 < model.getLength()) && (paramInt2 + paramInt3 >= 0))
            {
              localObject1 = model.getText(paramInt2 + paramInt3, 1);
              return (String)localObject1;
            }
            break;
          case 2: 
          case 3: 
            localObject1 = getSegmentAt(paramInt1, paramInt2);
            if (localObject1 != null)
            {
              if (paramInt3 != 0)
              {
                int i;
                if (paramInt3 < 0) {
                  i = modelOffset - 1;
                } else {
                  i = modelOffset + paramInt3 * count;
                }
                if ((i >= 0) && (i <= model.getLength())) {
                  localObject1 = getSegmentAt(paramInt1, i);
                } else {
                  localObject1 = null;
                }
              }
              if (localObject1 != null)
              {
                String str = new String(array, offset, count);
                return str;
              }
            }
            break;
          }
        }
        catch (BadLocationException localBadLocationException) {}finally
        {
          if ((model instanceof AbstractDocument)) {
            ((AbstractDocument)model).readUnlock();
          }
        }
        return null;
      }
      
      private Element getParagraphElement(int paramInt)
      {
        if ((model instanceof PlainDocument))
        {
          localObject = (PlainDocument)model;
          return ((PlainDocument)localObject).getParagraphElement(paramInt);
        }
        if ((model instanceof StyledDocument))
        {
          localObject = (StyledDocument)model;
          return ((StyledDocument)localObject).getParagraphElement(paramInt);
        }
        int i;
        for (Object localObject = model.getDefaultRootElement(); !((Element)localObject).isLeaf(); localObject = ((Element)localObject).getElement(i)) {
          i = ((Element)localObject).getElementIndex(paramInt);
        }
        if (localObject == null) {
          return null;
        }
        return ((Element)localObject).getParentElement();
      }
      
      private IndexedSegment getParagraphElementText(int paramInt)
        throws BadLocationException
      {
        Element localElement = getParagraphElement(paramInt);
        if (localElement != null)
        {
          IndexedSegment localIndexedSegment = new IndexedSegment(null);
          try
          {
            int i = localElement.getEndOffset() - localElement.getStartOffset();
            model.getText(localElement.getStartOffset(), i, localIndexedSegment);
          }
          catch (BadLocationException localBadLocationException)
          {
            return null;
          }
          modelOffset = localElement.getStartOffset();
          return localIndexedSegment;
        }
        return null;
      }
      
      private IndexedSegment getSegmentAt(int paramInt1, int paramInt2)
        throws BadLocationException
      {
        IndexedSegment localIndexedSegment = getParagraphElementText(paramInt2);
        if (localIndexedSegment == null) {
          return null;
        }
        BreakIterator localBreakIterator;
        switch (paramInt1)
        {
        case 2: 
          localBreakIterator = BreakIterator.getWordInstance(getLocale());
          break;
        case 3: 
          localBreakIterator = BreakIterator.getSentenceInstance(getLocale());
          break;
        default: 
          return null;
        }
        localIndexedSegment.first();
        localBreakIterator.setText(localIndexedSegment);
        int i = localBreakIterator.following(paramInt2 - modelOffset + offset);
        if (i == -1) {
          return null;
        }
        if (i > offset + count) {
          return null;
        }
        int j = localBreakIterator.previous();
        if ((j == -1) || (j >= offset + count)) {
          return null;
        }
        modelOffset = (modelOffset + j - offset);
        offset = j;
        count = (i - j);
        return localIndexedSegment;
      }
      
      public AttributeSet getCharacterAttribute(int paramInt)
      {
        if ((model instanceof StyledDocument))
        {
          StyledDocument localStyledDocument = (StyledDocument)model;
          Element localElement = localStyledDocument.getCharacterElement(paramInt);
          if (localElement != null) {
            return localElement.getAttributes();
          }
        }
        return null;
      }
      
      public int getSelectionStart()
      {
        return editor.getSelectionStart();
      }
      
      public int getSelectionEnd()
      {
        return editor.getSelectionEnd();
      }
      
      public String getSelectedText()
      {
        return editor.getSelectedText();
      }
      
      private String getText(int paramInt1, int paramInt2)
        throws BadLocationException
      {
        if ((model != null) && ((model instanceof StyledDocument)))
        {
          StyledDocument localStyledDocument = (StyledDocument)model;
          return model.getText(paramInt1, paramInt2);
        }
        return null;
      }
      
      private class IndexedSegment
        extends Segment
      {
        public int modelOffset;
        
        private IndexedSegment() {}
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\AccessibleHTML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */