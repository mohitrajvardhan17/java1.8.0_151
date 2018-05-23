package javax.accessibility;

public abstract interface AccessibleHypertext
  extends AccessibleText
{
  public abstract int getLinkCount();
  
  public abstract AccessibleHyperlink getLink(int paramInt);
  
  public abstract int getLinkIndex(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\accessibility\AccessibleHypertext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */