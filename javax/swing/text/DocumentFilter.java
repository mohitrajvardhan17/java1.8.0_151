package javax.swing.text;

public class DocumentFilter
{
  public DocumentFilter() {}
  
  public void remove(FilterBypass paramFilterBypass, int paramInt1, int paramInt2)
    throws BadLocationException
  {
    paramFilterBypass.remove(paramInt1, paramInt2);
  }
  
  public void insertString(FilterBypass paramFilterBypass, int paramInt, String paramString, AttributeSet paramAttributeSet)
    throws BadLocationException
  {
    paramFilterBypass.insertString(paramInt, paramString, paramAttributeSet);
  }
  
  public void replace(FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet)
    throws BadLocationException
  {
    paramFilterBypass.replace(paramInt1, paramInt2, paramString, paramAttributeSet);
  }
  
  public static abstract class FilterBypass
  {
    public FilterBypass() {}
    
    public abstract Document getDocument();
    
    public abstract void remove(int paramInt1, int paramInt2)
      throws BadLocationException;
    
    public abstract void insertString(int paramInt, String paramString, AttributeSet paramAttributeSet)
      throws BadLocationException;
    
    public abstract void replace(int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet)
      throws BadLocationException;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\DocumentFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */