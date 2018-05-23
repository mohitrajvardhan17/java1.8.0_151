package sun.text.normalizer;

public abstract interface RangeValueIterator
{
  public abstract boolean next(Element paramElement);
  
  public abstract void reset();
  
  public static class Element
  {
    public int start;
    public int limit;
    public int value;
    
    public Element() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\RangeValueIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */