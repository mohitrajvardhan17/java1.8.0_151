package javax.swing.text;

public abstract interface Element
{
  public abstract Document getDocument();
  
  public abstract Element getParentElement();
  
  public abstract String getName();
  
  public abstract AttributeSet getAttributes();
  
  public abstract int getStartOffset();
  
  public abstract int getEndOffset();
  
  public abstract int getElementIndex(int paramInt);
  
  public abstract int getElementCount();
  
  public abstract Element getElement(int paramInt);
  
  public abstract boolean isLeaf();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\Element.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */