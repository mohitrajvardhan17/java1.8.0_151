package javax.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.JTextComponent.AccessibleJTextComponent;
import javax.swing.text.PlainDocument;

public class JTextArea
  extends JTextComponent
{
  private static final String uiClassID = "TextAreaUI";
  private int rows;
  private int columns;
  private int columnWidth;
  private int rowHeight;
  private boolean wrap;
  private boolean word;
  
  public JTextArea()
  {
    this(null, null, 0, 0);
  }
  
  public JTextArea(String paramString)
  {
    this(null, paramString, 0, 0);
  }
  
  public JTextArea(int paramInt1, int paramInt2)
  {
    this(null, null, paramInt1, paramInt2);
  }
  
  public JTextArea(String paramString, int paramInt1, int paramInt2)
  {
    this(null, paramString, paramInt1, paramInt2);
  }
  
  public JTextArea(Document paramDocument)
  {
    this(paramDocument, null, 0, 0);
  }
  
  public JTextArea(Document paramDocument, String paramString, int paramInt1, int paramInt2)
  {
    rows = paramInt1;
    columns = paramInt2;
    if (paramDocument == null) {
      paramDocument = createDefaultModel();
    }
    setDocument(paramDocument);
    if (paramString != null)
    {
      setText(paramString);
      select(0, 0);
    }
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("rows: " + paramInt1);
    }
    if (paramInt2 < 0) {
      throw new IllegalArgumentException("columns: " + paramInt2);
    }
    LookAndFeel.installProperty(this, "focusTraversalKeysForward", JComponent.getManagingFocusForwardTraversalKeys());
    LookAndFeel.installProperty(this, "focusTraversalKeysBackward", JComponent.getManagingFocusBackwardTraversalKeys());
  }
  
  public String getUIClassID()
  {
    return "TextAreaUI";
  }
  
  protected Document createDefaultModel()
  {
    return new PlainDocument();
  }
  
  public void setTabSize(int paramInt)
  {
    Document localDocument = getDocument();
    if (localDocument != null)
    {
      int i = getTabSize();
      localDocument.putProperty("tabSize", Integer.valueOf(paramInt));
      firePropertyChange("tabSize", i, paramInt);
    }
  }
  
  public int getTabSize()
  {
    int i = 8;
    Document localDocument = getDocument();
    if (localDocument != null)
    {
      Integer localInteger = (Integer)localDocument.getProperty("tabSize");
      if (localInteger != null) {
        i = localInteger.intValue();
      }
    }
    return i;
  }
  
  public void setLineWrap(boolean paramBoolean)
  {
    boolean bool = wrap;
    wrap = paramBoolean;
    firePropertyChange("lineWrap", bool, paramBoolean);
  }
  
  public boolean getLineWrap()
  {
    return wrap;
  }
  
  public void setWrapStyleWord(boolean paramBoolean)
  {
    boolean bool = word;
    word = paramBoolean;
    firePropertyChange("wrapStyleWord", bool, paramBoolean);
  }
  
  public boolean getWrapStyleWord()
  {
    return word;
  }
  
  public int getLineOfOffset(int paramInt)
    throws BadLocationException
  {
    Document localDocument = getDocument();
    if (paramInt < 0) {
      throw new BadLocationException("Can't translate offset to line", -1);
    }
    if (paramInt > localDocument.getLength()) {
      throw new BadLocationException("Can't translate offset to line", localDocument.getLength() + 1);
    }
    Element localElement = getDocument().getDefaultRootElement();
    return localElement.getElementIndex(paramInt);
  }
  
  public int getLineCount()
  {
    Element localElement = getDocument().getDefaultRootElement();
    return localElement.getElementCount();
  }
  
  public int getLineStartOffset(int paramInt)
    throws BadLocationException
  {
    int i = getLineCount();
    if (paramInt < 0) {
      throw new BadLocationException("Negative line", -1);
    }
    if (paramInt >= i) {
      throw new BadLocationException("No such line", getDocument().getLength() + 1);
    }
    Element localElement1 = getDocument().getDefaultRootElement();
    Element localElement2 = localElement1.getElement(paramInt);
    return localElement2.getStartOffset();
  }
  
  public int getLineEndOffset(int paramInt)
    throws BadLocationException
  {
    int i = getLineCount();
    if (paramInt < 0) {
      throw new BadLocationException("Negative line", -1);
    }
    if (paramInt >= i) {
      throw new BadLocationException("No such line", getDocument().getLength() + 1);
    }
    Element localElement1 = getDocument().getDefaultRootElement();
    Element localElement2 = localElement1.getElement(paramInt);
    int j = localElement2.getEndOffset();
    return paramInt == i - 1 ? j - 1 : j;
  }
  
  public void insert(String paramString, int paramInt)
  {
    Document localDocument = getDocument();
    if (localDocument != null) {
      try
      {
        localDocument.insertString(paramInt, paramString, null);
      }
      catch (BadLocationException localBadLocationException)
      {
        throw new IllegalArgumentException(localBadLocationException.getMessage());
      }
    }
  }
  
  public void append(String paramString)
  {
    Document localDocument = getDocument();
    if (localDocument != null) {
      try
      {
        localDocument.insertString(localDocument.getLength(), paramString, null);
      }
      catch (BadLocationException localBadLocationException) {}
    }
  }
  
  public void replaceRange(String paramString, int paramInt1, int paramInt2)
  {
    if (paramInt2 < paramInt1) {
      throw new IllegalArgumentException("end before start");
    }
    Document localDocument = getDocument();
    if (localDocument != null) {
      try
      {
        if ((localDocument instanceof AbstractDocument))
        {
          ((AbstractDocument)localDocument).replace(paramInt1, paramInt2 - paramInt1, paramString, null);
        }
        else
        {
          localDocument.remove(paramInt1, paramInt2 - paramInt1);
          localDocument.insertString(paramInt1, paramString, null);
        }
      }
      catch (BadLocationException localBadLocationException)
      {
        throw new IllegalArgumentException(localBadLocationException.getMessage());
      }
    }
  }
  
  public int getRows()
  {
    return rows;
  }
  
  public void setRows(int paramInt)
  {
    int i = rows;
    if (paramInt < 0) {
      throw new IllegalArgumentException("rows less than zero.");
    }
    if (paramInt != i)
    {
      rows = paramInt;
      invalidate();
    }
  }
  
  protected int getRowHeight()
  {
    if (rowHeight == 0)
    {
      FontMetrics localFontMetrics = getFontMetrics(getFont());
      rowHeight = localFontMetrics.getHeight();
    }
    return rowHeight;
  }
  
  public int getColumns()
  {
    return columns;
  }
  
  public void setColumns(int paramInt)
  {
    int i = columns;
    if (paramInt < 0) {
      throw new IllegalArgumentException("columns less than zero.");
    }
    if (paramInt != i)
    {
      columns = paramInt;
      invalidate();
    }
  }
  
  protected int getColumnWidth()
  {
    if (columnWidth == 0)
    {
      FontMetrics localFontMetrics = getFontMetrics(getFont());
      columnWidth = localFontMetrics.charWidth('m');
    }
    return columnWidth;
  }
  
  public Dimension getPreferredSize()
  {
    Dimension localDimension = super.getPreferredSize();
    localDimension = localDimension == null ? new Dimension(400, 400) : localDimension;
    Insets localInsets = getInsets();
    if (columns != 0) {
      width = Math.max(width, columns * getColumnWidth() + left + right);
    }
    if (rows != 0) {
      height = Math.max(height, rows * getRowHeight() + top + bottom);
    }
    return localDimension;
  }
  
  public void setFont(Font paramFont)
  {
    super.setFont(paramFont);
    rowHeight = 0;
    columnWidth = 0;
  }
  
  protected String paramString()
  {
    String str1 = wrap ? "true" : "false";
    String str2 = word ? "true" : "false";
    return super.paramString() + ",colums=" + columns + ",columWidth=" + columnWidth + ",rows=" + rows + ",rowHeight=" + rowHeight + ",word=" + str2 + ",wrap=" + str1;
  }
  
  public boolean getScrollableTracksViewportWidth()
  {
    return wrap ? true : super.getScrollableTracksViewportWidth();
  }
  
  public Dimension getPreferredScrollableViewportSize()
  {
    Dimension localDimension = super.getPreferredScrollableViewportSize();
    localDimension = localDimension == null ? new Dimension(400, 400) : localDimension;
    Insets localInsets = getInsets();
    width = (columns == 0 ? width : columns * getColumnWidth() + left + right);
    height = (rows == 0 ? height : rows * getRowHeight() + top + bottom);
    return localDimension;
  }
  
  public int getScrollableUnitIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    case 1: 
      return getRowHeight();
    case 0: 
      return getColumnWidth();
    }
    throw new IllegalArgumentException("Invalid orientation: " + paramInt1);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("TextAreaUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJTextArea();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJTextArea
    extends JTextComponent.AccessibleJTextComponent
  {
    protected AccessibleJTextArea()
    {
      super();
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      localAccessibleStateSet.add(AccessibleState.MULTI_LINE);
      return localAccessibleStateSet;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JTextArea.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */