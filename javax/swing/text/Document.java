package javax.swing.text;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;

public abstract interface Document
{
  public static final String StreamDescriptionProperty = "stream";
  public static final String TitleProperty = "title";
  
  public abstract int getLength();
  
  public abstract void addDocumentListener(DocumentListener paramDocumentListener);
  
  public abstract void removeDocumentListener(DocumentListener paramDocumentListener);
  
  public abstract void addUndoableEditListener(UndoableEditListener paramUndoableEditListener);
  
  public abstract void removeUndoableEditListener(UndoableEditListener paramUndoableEditListener);
  
  public abstract Object getProperty(Object paramObject);
  
  public abstract void putProperty(Object paramObject1, Object paramObject2);
  
  public abstract void remove(int paramInt1, int paramInt2)
    throws BadLocationException;
  
  public abstract void insertString(int paramInt, String paramString, AttributeSet paramAttributeSet)
    throws BadLocationException;
  
  public abstract String getText(int paramInt1, int paramInt2)
    throws BadLocationException;
  
  public abstract void getText(int paramInt1, int paramInt2, Segment paramSegment)
    throws BadLocationException;
  
  public abstract Position getStartPosition();
  
  public abstract Position getEndPosition();
  
  public abstract Position createPosition(int paramInt)
    throws BadLocationException;
  
  public abstract Element[] getRootElements();
  
  public abstract Element getDefaultRootElement();
  
  public abstract void render(Runnable paramRunnable);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\Document.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */