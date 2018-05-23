package javax.swing.text;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.ElementChange;
import javax.swing.event.DocumentEvent.EventType;

public abstract class View
  implements SwingConstants
{
  public static final int BadBreakWeight = 0;
  public static final int GoodBreakWeight = 1000;
  public static final int ExcellentBreakWeight = 2000;
  public static final int ForcedBreakWeight = 3000;
  public static final int X_AXIS = 0;
  public static final int Y_AXIS = 1;
  static final Position.Bias[] sharedBiasReturn = new Position.Bias[1];
  private View parent;
  private Element elem;
  int firstUpdateIndex;
  int lastUpdateIndex;
  
  public View(Element paramElement)
  {
    elem = paramElement;
  }
  
  public View getParent()
  {
    return parent;
  }
  
  public boolean isVisible()
  {
    return true;
  }
  
  public abstract float getPreferredSpan(int paramInt);
  
  public float getMinimumSpan(int paramInt)
  {
    int i = getResizeWeight(paramInt);
    if (i == 0) {
      return getPreferredSpan(paramInt);
    }
    return 0.0F;
  }
  
  public float getMaximumSpan(int paramInt)
  {
    int i = getResizeWeight(paramInt);
    if (i == 0) {
      return getPreferredSpan(paramInt);
    }
    return 2.14748365E9F;
  }
  
  public void preferenceChanged(View paramView, boolean paramBoolean1, boolean paramBoolean2)
  {
    View localView = getParent();
    if (localView != null) {
      localView.preferenceChanged(this, paramBoolean1, paramBoolean2);
    }
  }
  
  public float getAlignment(int paramInt)
  {
    return 0.5F;
  }
  
  public abstract void paint(Graphics paramGraphics, Shape paramShape);
  
  public void setParent(View paramView)
  {
    if (paramView == null) {
      for (int i = 0; i < getViewCount(); i++) {
        if (getView(i).getParent() == this) {
          getView(i).setParent(null);
        }
      }
    }
    parent = paramView;
  }
  
  public int getViewCount()
  {
    return 0;
  }
  
  public View getView(int paramInt)
  {
    return null;
  }
  
  public void removeAll()
  {
    replace(0, getViewCount(), null);
  }
  
  public void remove(int paramInt)
  {
    replace(paramInt, 1, null);
  }
  
  public void insert(int paramInt, View paramView)
  {
    View[] arrayOfView = new View[1];
    arrayOfView[0] = paramView;
    replace(paramInt, 0, arrayOfView);
  }
  
  public void append(View paramView)
  {
    View[] arrayOfView = new View[1];
    arrayOfView[0] = paramView;
    replace(getViewCount(), 0, arrayOfView);
  }
  
  public void replace(int paramInt1, int paramInt2, View[] paramArrayOfView) {}
  
  public int getViewIndex(int paramInt, Position.Bias paramBias)
  {
    return -1;
  }
  
  public Shape getChildAllocation(int paramInt, Shape paramShape)
  {
    return null;
  }
  
  public int getNextVisualPositionFrom(int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias)
    throws BadLocationException
  {
    if (paramInt1 < -1) {
      throw new BadLocationException("Invalid position", paramInt1);
    }
    paramArrayOfBias[0] = Position.Bias.Forward;
    switch (paramInt2)
    {
    case 1: 
    case 5: 
      if (paramInt1 == -1)
      {
        paramInt1 = paramInt2 == 1 ? Math.max(0, getEndOffset() - 1) : getStartOffset();
      }
      else
      {
        JTextComponent localJTextComponent = (JTextComponent)getContainer();
        Object localObject = localJTextComponent != null ? localJTextComponent.getCaret() : null;
        Point localPoint;
        if (localObject != null) {
          localPoint = ((Caret)localObject).getMagicCaretPosition();
        } else {
          localPoint = null;
        }
        int i;
        if (localPoint == null)
        {
          Rectangle localRectangle = localJTextComponent.modelToView(paramInt1);
          i = localRectangle == null ? 0 : x;
        }
        else
        {
          i = x;
        }
        if (paramInt2 == 1) {
          paramInt1 = Utilities.getPositionAbove(localJTextComponent, paramInt1, i);
        } else {
          paramInt1 = Utilities.getPositionBelow(localJTextComponent, paramInt1, i);
        }
      }
      break;
    case 7: 
      if (paramInt1 == -1) {
        paramInt1 = Math.max(0, getEndOffset() - 1);
      } else {
        paramInt1 = Math.max(0, paramInt1 - 1);
      }
      break;
    case 3: 
      if (paramInt1 == -1) {
        paramInt1 = getStartOffset();
      } else {
        paramInt1 = Math.min(paramInt1 + 1, getDocument().getLength());
      }
      break;
    case 2: 
    case 4: 
    case 6: 
    default: 
      throw new IllegalArgumentException("Bad direction: " + paramInt2);
    }
    return paramInt1;
  }
  
  public abstract Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias)
    throws BadLocationException;
  
  public Shape modelToView(int paramInt1, Position.Bias paramBias1, int paramInt2, Position.Bias paramBias2, Shape paramShape)
    throws BadLocationException
  {
    Shape localShape = modelToView(paramInt1, paramShape, paramBias1);
    Object localObject;
    if (paramInt2 == getEndOffset())
    {
      try
      {
        localObject = modelToView(paramInt2, paramShape, paramBias2);
      }
      catch (BadLocationException localBadLocationException)
      {
        localObject = null;
      }
      if (localObject == null)
      {
        localRectangle1 = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
        localObject = new Rectangle(x + width - 1, y, 1, height);
      }
    }
    else
    {
      localObject = modelToView(paramInt2, paramShape, paramBias2);
    }
    Rectangle localRectangle1 = localShape.getBounds();
    Rectangle localRectangle2 = (localObject instanceof Rectangle) ? (Rectangle)localObject : ((Shape)localObject).getBounds();
    if (y != y)
    {
      Rectangle localRectangle3 = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
      x = x;
      width = width;
    }
    localRectangle1.add(localRectangle2);
    return localRectangle1;
  }
  
  public abstract int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias);
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    if (getViewCount() > 0)
    {
      Element localElement = getElement();
      DocumentEvent.ElementChange localElementChange = paramDocumentEvent.getChange(localElement);
      if ((localElementChange != null) && (!updateChildren(localElementChange, paramDocumentEvent, paramViewFactory))) {
        localElementChange = null;
      }
      forwardUpdate(localElementChange, paramDocumentEvent, paramShape, paramViewFactory);
      updateLayout(localElementChange, paramDocumentEvent, paramShape);
    }
  }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    if (getViewCount() > 0)
    {
      Element localElement = getElement();
      DocumentEvent.ElementChange localElementChange = paramDocumentEvent.getChange(localElement);
      if ((localElementChange != null) && (!updateChildren(localElementChange, paramDocumentEvent, paramViewFactory))) {
        localElementChange = null;
      }
      forwardUpdate(localElementChange, paramDocumentEvent, paramShape, paramViewFactory);
      updateLayout(localElementChange, paramDocumentEvent, paramShape);
    }
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    if (getViewCount() > 0)
    {
      Element localElement = getElement();
      DocumentEvent.ElementChange localElementChange = paramDocumentEvent.getChange(localElement);
      if ((localElementChange != null) && (!updateChildren(localElementChange, paramDocumentEvent, paramViewFactory))) {
        localElementChange = null;
      }
      forwardUpdate(localElementChange, paramDocumentEvent, paramShape, paramViewFactory);
      updateLayout(localElementChange, paramDocumentEvent, paramShape);
    }
  }
  
  public Document getDocument()
  {
    return elem.getDocument();
  }
  
  public int getStartOffset()
  {
    return elem.getStartOffset();
  }
  
  public int getEndOffset()
  {
    return elem.getEndOffset();
  }
  
  public Element getElement()
  {
    return elem;
  }
  
  public Graphics getGraphics()
  {
    Container localContainer = getContainer();
    return localContainer.getGraphics();
  }
  
  public AttributeSet getAttributes()
  {
    return elem.getAttributes();
  }
  
  public View breakView(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
  {
    return this;
  }
  
  public View createFragment(int paramInt1, int paramInt2)
  {
    return this;
  }
  
  public int getBreakWeight(int paramInt, float paramFloat1, float paramFloat2)
  {
    if (paramFloat2 > getPreferredSpan(paramInt)) {
      return 1000;
    }
    return 0;
  }
  
  public int getResizeWeight(int paramInt)
  {
    return 0;
  }
  
  public void setSize(float paramFloat1, float paramFloat2) {}
  
  public Container getContainer()
  {
    View localView = getParent();
    return localView != null ? localView.getContainer() : null;
  }
  
  public ViewFactory getViewFactory()
  {
    View localView = getParent();
    return localView != null ? localView.getViewFactory() : null;
  }
  
  public String getToolTipText(float paramFloat1, float paramFloat2, Shape paramShape)
  {
    int i = getViewIndex(paramFloat1, paramFloat2, paramShape);
    if (i >= 0)
    {
      paramShape = getChildAllocation(i, paramShape);
      Rectangle localRectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
      if (localRectangle.contains(paramFloat1, paramFloat2)) {
        return getView(i).getToolTipText(paramFloat1, paramFloat2, paramShape);
      }
    }
    return null;
  }
  
  public int getViewIndex(float paramFloat1, float paramFloat2, Shape paramShape)
  {
    for (int i = getViewCount() - 1; i >= 0; i--)
    {
      Shape localShape = getChildAllocation(i, paramShape);
      if (localShape != null)
      {
        Rectangle localRectangle = (localShape instanceof Rectangle) ? (Rectangle)localShape : localShape.getBounds();
        if (localRectangle.contains(paramFloat1, paramFloat2)) {
          return i;
        }
      }
    }
    return -1;
  }
  
  protected boolean updateChildren(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, ViewFactory paramViewFactory)
  {
    Element[] arrayOfElement1 = paramElementChange.getChildrenRemoved();
    Element[] arrayOfElement2 = paramElementChange.getChildrenAdded();
    View[] arrayOfView = null;
    if (arrayOfElement2 != null)
    {
      arrayOfView = new View[arrayOfElement2.length];
      for (i = 0; i < arrayOfElement2.length; i++) {
        arrayOfView[i] = paramViewFactory.create(arrayOfElement2[i]);
      }
    }
    int i = 0;
    int j = paramElementChange.getIndex();
    if (arrayOfElement1 != null) {
      i = arrayOfElement1.length;
    }
    replace(j, i, arrayOfView);
    return true;
  }
  
  protected void forwardUpdate(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    calculateUpdateIndexes(paramDocumentEvent);
    int i = lastUpdateIndex + 1;
    int j = i;
    Object localObject = paramElementChange != null ? paramElementChange.getChildrenAdded() : null;
    if ((localObject != null) && (localObject.length > 0))
    {
      i = paramElementChange.getIndex();
      j = i + localObject.length - 1;
    }
    for (int k = firstUpdateIndex; k <= lastUpdateIndex; k++) {
      if ((k < i) || (k > j))
      {
        View localView = getView(k);
        if (localView != null)
        {
          Shape localShape = getChildAllocation(k, paramShape);
          forwardUpdateToView(localView, paramDocumentEvent, localShape, paramViewFactory);
        }
      }
    }
  }
  
  void calculateUpdateIndexes(DocumentEvent paramDocumentEvent)
  {
    int i = paramDocumentEvent.getOffset();
    firstUpdateIndex = getViewIndex(i, Position.Bias.Forward);
    if ((firstUpdateIndex == -1) && (paramDocumentEvent.getType() == DocumentEvent.EventType.REMOVE) && (i >= getEndOffset())) {
      firstUpdateIndex = (getViewCount() - 1);
    }
    lastUpdateIndex = firstUpdateIndex;
    Object localObject = firstUpdateIndex >= 0 ? getView(firstUpdateIndex) : null;
    if ((localObject != null) && (((View)localObject).getStartOffset() == i) && (i > 0)) {
      firstUpdateIndex = Math.max(firstUpdateIndex - 1, 0);
    }
    if (paramDocumentEvent.getType() != DocumentEvent.EventType.REMOVE)
    {
      lastUpdateIndex = getViewIndex(i + paramDocumentEvent.getLength(), Position.Bias.Forward);
      if (lastUpdateIndex < 0) {
        lastUpdateIndex = (getViewCount() - 1);
      }
    }
    firstUpdateIndex = Math.max(firstUpdateIndex, 0);
  }
  
  void updateAfterChange() {}
  
  protected void forwardUpdateToView(View paramView, DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    DocumentEvent.EventType localEventType = paramDocumentEvent.getType();
    if (localEventType == DocumentEvent.EventType.INSERT) {
      paramView.insertUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    } else if (localEventType == DocumentEvent.EventType.REMOVE) {
      paramView.removeUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    } else {
      paramView.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    }
  }
  
  protected void updateLayout(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, Shape paramShape)
  {
    if ((paramElementChange != null) && (paramShape != null))
    {
      preferenceChanged(null, true, true);
      Container localContainer = getContainer();
      if (localContainer != null) {
        localContainer.repaint();
      }
    }
  }
  
  @Deprecated
  public Shape modelToView(int paramInt, Shape paramShape)
    throws BadLocationException
  {
    return modelToView(paramInt, paramShape, Position.Bias.Forward);
  }
  
  @Deprecated
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape)
  {
    sharedBiasReturn[0] = Position.Bias.Forward;
    return viewToModel(paramFloat1, paramFloat2, paramShape, sharedBiasReturn);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\View.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */