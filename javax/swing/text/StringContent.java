package javax.swing.text;

import java.io.Serializable;
import java.util.Vector;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public final class StringContent
  implements AbstractDocument.Content, Serializable
{
  private static final char[] empty = new char[0];
  private char[] data;
  private int count;
  transient Vector<PosRec> marks;
  
  public StringContent()
  {
    this(10);
  }
  
  public StringContent(int paramInt)
  {
    if (paramInt < 1) {
      paramInt = 1;
    }
    data = new char[paramInt];
    data[0] = '\n';
    count = 1;
  }
  
  public int length()
  {
    return count;
  }
  
  public UndoableEdit insertString(int paramInt, String paramString)
    throws BadLocationException
  {
    if ((paramInt >= count) || (paramInt < 0)) {
      throw new BadLocationException("Invalid location", count);
    }
    char[] arrayOfChar = paramString.toCharArray();
    replace(paramInt, 0, arrayOfChar, 0, arrayOfChar.length);
    if (marks != null) {
      updateMarksForInsert(paramInt, paramString.length());
    }
    return new InsertUndo(paramInt, paramString.length());
  }
  
  public UndoableEdit remove(int paramInt1, int paramInt2)
    throws BadLocationException
  {
    if (paramInt1 + paramInt2 >= count) {
      throw new BadLocationException("Invalid range", count);
    }
    String str = getString(paramInt1, paramInt2);
    RemoveUndo localRemoveUndo = new RemoveUndo(paramInt1, str);
    replace(paramInt1, paramInt2, empty, 0, 0);
    if (marks != null) {
      updateMarksForRemove(paramInt1, paramInt2);
    }
    return localRemoveUndo;
  }
  
  public String getString(int paramInt1, int paramInt2)
    throws BadLocationException
  {
    if (paramInt1 + paramInt2 > count) {
      throw new BadLocationException("Invalid range", count);
    }
    return new String(data, paramInt1, paramInt2);
  }
  
  public void getChars(int paramInt1, int paramInt2, Segment paramSegment)
    throws BadLocationException
  {
    if (paramInt1 + paramInt2 > count) {
      throw new BadLocationException("Invalid location", count);
    }
    array = data;
    offset = paramInt1;
    count = paramInt2;
  }
  
  public Position createPosition(int paramInt)
    throws BadLocationException
  {
    if (marks == null) {
      marks = new Vector();
    }
    return new StickyPosition(paramInt);
  }
  
  void replace(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, int paramInt4)
  {
    int i = paramInt4 - paramInt2;
    int j = paramInt1 + paramInt2;
    int k = count - j;
    int m = j + i;
    if (count + i >= data.length)
    {
      int n = Math.max(2 * data.length, count + i);
      char[] arrayOfChar = new char[n];
      System.arraycopy(data, 0, arrayOfChar, 0, paramInt1);
      System.arraycopy(paramArrayOfChar, paramInt3, arrayOfChar, paramInt1, paramInt4);
      System.arraycopy(data, j, arrayOfChar, m, k);
      data = arrayOfChar;
    }
    else
    {
      System.arraycopy(data, j, data, m, k);
      System.arraycopy(paramArrayOfChar, paramInt3, data, paramInt1, paramInt4);
    }
    count += i;
  }
  
  void resize(int paramInt)
  {
    char[] arrayOfChar = new char[paramInt];
    System.arraycopy(data, 0, arrayOfChar, 0, Math.min(paramInt, count));
    data = arrayOfChar;
  }
  
  synchronized void updateMarksForInsert(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 0) {
      paramInt1 = 1;
    }
    int i = marks.size();
    for (int j = 0; j < i; j++)
    {
      PosRec localPosRec = (PosRec)marks.elementAt(j);
      if (unused)
      {
        marks.removeElementAt(j);
        j--;
        i--;
      }
      else if (offset >= paramInt1)
      {
        offset += paramInt2;
      }
    }
  }
  
  synchronized void updateMarksForRemove(int paramInt1, int paramInt2)
  {
    int i = marks.size();
    for (int j = 0; j < i; j++)
    {
      PosRec localPosRec = (PosRec)marks.elementAt(j);
      if (unused)
      {
        marks.removeElementAt(j);
        j--;
        i--;
      }
      else if (offset >= paramInt1 + paramInt2)
      {
        offset -= paramInt2;
      }
      else if (offset >= paramInt1)
      {
        offset = paramInt1;
      }
    }
  }
  
  protected Vector getPositionsInRange(Vector paramVector, int paramInt1, int paramInt2)
  {
    int i = marks.size();
    int j = paramInt1 + paramInt2;
    Vector localVector = paramVector == null ? new Vector() : paramVector;
    for (int k = 0; k < i; k++)
    {
      PosRec localPosRec = (PosRec)marks.elementAt(k);
      if (unused)
      {
        marks.removeElementAt(k);
        k--;
        i--;
      }
      else if ((offset >= paramInt1) && (offset <= j))
      {
        localVector.addElement(new UndoPosRef(localPosRec));
      }
    }
    return localVector;
  }
  
  protected void updateUndoPositions(Vector paramVector)
  {
    for (int i = paramVector.size() - 1; i >= 0; i--)
    {
      UndoPosRef localUndoPosRef = (UndoPosRef)paramVector.elementAt(i);
      if (rec.unused) {
        paramVector.removeElementAt(i);
      } else {
        localUndoPosRef.resetLocation();
      }
    }
  }
  
  class InsertUndo
    extends AbstractUndoableEdit
  {
    protected int offset;
    protected int length;
    protected String string;
    protected Vector posRefs;
    
    protected InsertUndo(int paramInt1, int paramInt2)
    {
      offset = paramInt1;
      length = paramInt2;
    }
    
    public void undo()
      throws CannotUndoException
    {
      super.undo();
      try
      {
        synchronized (StringContent.this)
        {
          if (marks != null) {
            posRefs = getPositionsInRange(null, offset, length);
          }
          string = getString(offset, length);
          remove(offset, length);
        }
      }
      catch (BadLocationException localBadLocationException)
      {
        throw new CannotUndoException();
      }
    }
    
    public void redo()
      throws CannotRedoException
    {
      super.redo();
      try
      {
        synchronized (StringContent.this)
        {
          insertString(offset, string);
          string = null;
          if (posRefs != null)
          {
            updateUndoPositions(posRefs);
            posRefs = null;
          }
        }
      }
      catch (BadLocationException localBadLocationException)
      {
        throw new CannotRedoException();
      }
    }
  }
  
  final class PosRec
  {
    int offset;
    boolean unused;
    
    PosRec(int paramInt)
    {
      offset = paramInt;
    }
  }
  
  class RemoveUndo
    extends AbstractUndoableEdit
  {
    protected int offset;
    protected int length;
    protected String string;
    protected Vector posRefs;
    
    protected RemoveUndo(int paramInt, String paramString)
    {
      offset = paramInt;
      string = paramString;
      length = paramString.length();
      if (marks != null) {
        posRefs = getPositionsInRange(null, paramInt, length);
      }
    }
    
    public void undo()
      throws CannotUndoException
    {
      super.undo();
      try
      {
        synchronized (StringContent.this)
        {
          insertString(offset, string);
          if (posRefs != null)
          {
            updateUndoPositions(posRefs);
            posRefs = null;
          }
          string = null;
        }
      }
      catch (BadLocationException localBadLocationException)
      {
        throw new CannotUndoException();
      }
    }
    
    public void redo()
      throws CannotRedoException
    {
      super.redo();
      try
      {
        synchronized (StringContent.this)
        {
          string = getString(offset, length);
          if (marks != null) {
            posRefs = getPositionsInRange(null, offset, length);
          }
          remove(offset, length);
        }
      }
      catch (BadLocationException localBadLocationException)
      {
        throw new CannotRedoException();
      }
    }
  }
  
  final class StickyPosition
    implements Position
  {
    StringContent.PosRec rec;
    
    StickyPosition(int paramInt)
    {
      rec = new StringContent.PosRec(StringContent.this, paramInt);
      marks.addElement(rec);
    }
    
    public int getOffset()
    {
      return rec.offset;
    }
    
    protected void finalize()
      throws Throwable
    {
      rec.unused = true;
    }
    
    public String toString()
    {
      return Integer.toString(getOffset());
    }
  }
  
  final class UndoPosRef
  {
    protected int undoLocation;
    protected StringContent.PosRec rec;
    
    UndoPosRef(StringContent.PosRec paramPosRec)
    {
      rec = paramPosRec;
      undoLocation = offset;
    }
    
    protected void resetLocation()
    {
      rec.offset = undoLocation;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\StringContent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */