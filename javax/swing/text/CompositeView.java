package javax.swing.text;

import java.awt.Rectangle;
import java.awt.Shape;

public abstract class CompositeView
  extends View
{
  private static View[] ZERO = new View[0];
  private View[] children = new View[1];
  private int nchildren = 0;
  private short left;
  private short right;
  private short top;
  private short bottom;
  private Rectangle childAlloc = new Rectangle();
  
  public CompositeView(Element paramElement)
  {
    super(paramElement);
  }
  
  protected void loadChildren(ViewFactory paramViewFactory)
  {
    if (paramViewFactory == null) {
      return;
    }
    Element localElement = getElement();
    int i = localElement.getElementCount();
    if (i > 0)
    {
      View[] arrayOfView = new View[i];
      for (int j = 0; j < i; j++) {
        arrayOfView[j] = paramViewFactory.create(localElement.getElement(j));
      }
      replace(0, 0, arrayOfView);
    }
  }
  
  public void setParent(View paramView)
  {
    super.setParent(paramView);
    if ((paramView != null) && (nchildren == 0))
    {
      ViewFactory localViewFactory = getViewFactory();
      loadChildren(localViewFactory);
    }
  }
  
  public int getViewCount()
  {
    return nchildren;
  }
  
  public View getView(int paramInt)
  {
    return children[paramInt];
  }
  
  public void replace(int paramInt1, int paramInt2, View[] paramArrayOfView)
  {
    if (paramArrayOfView == null) {
      paramArrayOfView = ZERO;
    }
    for (int i = paramInt1; i < paramInt1 + paramInt2; i++)
    {
      if (children[i].getParent() == this) {
        children[i].setParent(null);
      }
      children[i] = null;
    }
    i = paramArrayOfView.length - paramInt2;
    int j = paramInt1 + paramInt2;
    int k = nchildren - j;
    int m = j + i;
    if (nchildren + i >= children.length)
    {
      n = Math.max(2 * children.length, nchildren + i);
      View[] arrayOfView = new View[n];
      System.arraycopy(children, 0, arrayOfView, 0, paramInt1);
      System.arraycopy(paramArrayOfView, 0, arrayOfView, paramInt1, paramArrayOfView.length);
      System.arraycopy(children, j, arrayOfView, m, k);
      children = arrayOfView;
    }
    else
    {
      System.arraycopy(children, j, children, m, k);
      System.arraycopy(paramArrayOfView, 0, children, paramInt1, paramArrayOfView.length);
    }
    nchildren += i;
    for (int n = 0; n < paramArrayOfView.length; n++) {
      paramArrayOfView[n].setParent(this);
    }
  }
  
  public Shape getChildAllocation(int paramInt, Shape paramShape)
  {
    Rectangle localRectangle = getInsideAllocation(paramShape);
    childAllocation(paramInt, localRectangle);
    return localRectangle;
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias)
    throws BadLocationException
  {
    int i = paramBias == Position.Bias.Backward ? 1 : 0;
    int j = i != 0 ? Math.max(0, paramInt - 1) : paramInt;
    if ((i != 0) && (j < getStartOffset())) {
      return null;
    }
    int k = getViewIndexAtPosition(j);
    if ((k != -1) && (k < getViewCount()))
    {
      View localView = getView(k);
      if ((localView != null) && (j >= localView.getStartOffset()) && (j < localView.getEndOffset()))
      {
        Shape localShape1 = getChildAllocation(k, paramShape);
        if (localShape1 == null) {
          return null;
        }
        Shape localShape2 = localView.modelToView(paramInt, localShape1, paramBias);
        if ((localShape2 == null) && (localView.getEndOffset() == paramInt))
        {
          k++;
          if (k < getViewCount())
          {
            localView = getView(k);
            localShape2 = localView.modelToView(paramInt, getChildAllocation(k, paramShape), paramBias);
          }
        }
        return localShape2;
      }
    }
    throw new BadLocationException("Position not represented by view", paramInt);
  }
  
  public Shape modelToView(int paramInt1, Position.Bias paramBias1, int paramInt2, Position.Bias paramBias2, Shape paramShape)
    throws BadLocationException
  {
    if ((paramInt1 == getStartOffset()) && (paramInt2 == getEndOffset())) {
      return paramShape;
    }
    Rectangle localRectangle1 = getInsideAllocation(paramShape);
    Rectangle localRectangle2 = new Rectangle(localRectangle1);
    View localView1 = getViewAtPosition(paramBias1 == Position.Bias.Backward ? Math.max(0, paramInt1 - 1) : paramInt1, localRectangle2);
    Rectangle localRectangle3 = new Rectangle(localRectangle1);
    View localView2 = getViewAtPosition(paramBias2 == Position.Bias.Backward ? Math.max(0, paramInt2 - 1) : paramInt2, localRectangle3);
    if (localView1 == localView2)
    {
      if (localView1 == null) {
        return paramShape;
      }
      return localView1.modelToView(paramInt1, paramBias1, paramInt2, paramBias2, localRectangle2);
    }
    int i = getViewCount();
    for (int j = 0; j < i; j++)
    {
      View localView3;
      if (((localView3 = getView(j)) == localView1) || (localView3 == localView2))
      {
        Rectangle localRectangle5 = new Rectangle();
        Rectangle localRectangle4;
        View localView4;
        if (localView3 == localView1)
        {
          localRectangle4 = localView1.modelToView(paramInt1, paramBias1, localView1.getEndOffset(), Position.Bias.Backward, localRectangle2).getBounds();
          localView4 = localView2;
        }
        else
        {
          localRectangle4 = localView2.modelToView(localView2.getStartOffset(), Position.Bias.Forward, paramInt2, paramBias2, localRectangle3).getBounds();
          localView4 = localView1;
        }
        for (;;)
        {
          j++;
          if ((j >= i) || ((localView3 = getView(j)) == localView4)) {
            break;
          }
          localRectangle5.setBounds(localRectangle1);
          childAllocation(j, localRectangle5);
          localRectangle4.add(localRectangle5);
        }
        if (localView4 != null)
        {
          Shape localShape;
          if (localView4 == localView2) {
            localShape = localView2.modelToView(localView2.getStartOffset(), Position.Bias.Forward, paramInt2, paramBias2, localRectangle3);
          } else {
            localShape = localView1.modelToView(paramInt1, paramBias1, localView1.getEndOffset(), Position.Bias.Backward, localRectangle2);
          }
          if ((localShape instanceof Rectangle)) {
            localRectangle4.add((Rectangle)localShape);
          } else {
            localRectangle4.add(localShape.getBounds());
          }
        }
        return localRectangle4;
      }
    }
    throw new BadLocationException("Position not represented by view", paramInt1);
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias)
  {
    Rectangle localRectangle = getInsideAllocation(paramShape);
    int i;
    if (isBefore((int)paramFloat1, (int)paramFloat2, localRectangle))
    {
      i = -1;
      try
      {
        i = getNextVisualPositionFrom(-1, Position.Bias.Forward, paramShape, 3, paramArrayOfBias);
      }
      catch (BadLocationException localBadLocationException1) {}catch (IllegalArgumentException localIllegalArgumentException1) {}
      if (i == -1)
      {
        i = getStartOffset();
        paramArrayOfBias[0] = Position.Bias.Forward;
      }
      return i;
    }
    if (isAfter((int)paramFloat1, (int)paramFloat2, localRectangle))
    {
      i = -1;
      try
      {
        i = getNextVisualPositionFrom(-1, Position.Bias.Forward, paramShape, 7, paramArrayOfBias);
      }
      catch (BadLocationException localBadLocationException2) {}catch (IllegalArgumentException localIllegalArgumentException2) {}
      if (i == -1)
      {
        i = getEndOffset() - 1;
        paramArrayOfBias[0] = Position.Bias.Forward;
      }
      return i;
    }
    View localView = getViewAtPoint((int)paramFloat1, (int)paramFloat2, localRectangle);
    if (localView != null) {
      return localView.viewToModel(paramFloat1, paramFloat2, localRectangle, paramArrayOfBias);
    }
    return -1;
  }
  
  public int getNextVisualPositionFrom(int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias)
    throws BadLocationException
  {
    if (paramInt1 < -1) {
      throw new BadLocationException("invalid position", paramInt1);
    }
    Rectangle localRectangle = getInsideAllocation(paramShape);
    switch (paramInt2)
    {
    case 1: 
      return getNextNorthSouthVisualPositionFrom(paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias);
    case 5: 
      return getNextNorthSouthVisualPositionFrom(paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias);
    case 3: 
      return getNextEastWestVisualPositionFrom(paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias);
    case 7: 
      return getNextEastWestVisualPositionFrom(paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias);
    }
    throw new IllegalArgumentException("Bad direction: " + paramInt2);
  }
  
  public int getViewIndex(int paramInt, Position.Bias paramBias)
  {
    if (paramBias == Position.Bias.Backward) {
      paramInt--;
    }
    if ((paramInt >= getStartOffset()) && (paramInt < getEndOffset())) {
      return getViewIndexAtPosition(paramInt);
    }
    return -1;
  }
  
  protected abstract boolean isBefore(int paramInt1, int paramInt2, Rectangle paramRectangle);
  
  protected abstract boolean isAfter(int paramInt1, int paramInt2, Rectangle paramRectangle);
  
  protected abstract View getViewAtPoint(int paramInt1, int paramInt2, Rectangle paramRectangle);
  
  protected abstract void childAllocation(int paramInt, Rectangle paramRectangle);
  
  protected View getViewAtPosition(int paramInt, Rectangle paramRectangle)
  {
    int i = getViewIndexAtPosition(paramInt);
    if ((i >= 0) && (i < getViewCount()))
    {
      View localView = getView(i);
      if (paramRectangle != null) {
        childAllocation(i, paramRectangle);
      }
      return localView;
    }
    return null;
  }
  
  protected int getViewIndexAtPosition(int paramInt)
  {
    Element localElement = getElement();
    return localElement.getElementIndex(paramInt);
  }
  
  protected Rectangle getInsideAllocation(Shape paramShape)
  {
    if (paramShape != null)
    {
      Rectangle localRectangle;
      if ((paramShape instanceof Rectangle)) {
        localRectangle = (Rectangle)paramShape;
      } else {
        localRectangle = paramShape.getBounds();
      }
      childAlloc.setBounds(localRectangle);
      childAlloc.x += getLeftInset();
      childAlloc.y += getTopInset();
      childAlloc.width -= getLeftInset() + getRightInset();
      childAlloc.height -= getTopInset() + getBottomInset();
      return childAlloc;
    }
    return null;
  }
  
  protected void setParagraphInsets(AttributeSet paramAttributeSet)
  {
    top = ((short)(int)StyleConstants.getSpaceAbove(paramAttributeSet));
    left = ((short)(int)StyleConstants.getLeftIndent(paramAttributeSet));
    bottom = ((short)(int)StyleConstants.getSpaceBelow(paramAttributeSet));
    right = ((short)(int)StyleConstants.getRightIndent(paramAttributeSet));
  }
  
  protected void setInsets(short paramShort1, short paramShort2, short paramShort3, short paramShort4)
  {
    top = paramShort1;
    left = paramShort2;
    right = paramShort4;
    bottom = paramShort3;
  }
  
  protected short getLeftInset()
  {
    return left;
  }
  
  protected short getRightInset()
  {
    return right;
  }
  
  protected short getTopInset()
  {
    return top;
  }
  
  protected short getBottomInset()
  {
    return bottom;
  }
  
  protected int getNextNorthSouthVisualPositionFrom(int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias)
    throws BadLocationException
  {
    return Utilities.getNextVisualPositionFrom(this, paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias);
  }
  
  protected int getNextEastWestVisualPositionFrom(int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias)
    throws BadLocationException
  {
    return Utilities.getNextVisualPositionFrom(this, paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias);
  }
  
  protected boolean flipEastAndWestAtEnds(int paramInt, Position.Bias paramBias)
  {
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\CompositeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */