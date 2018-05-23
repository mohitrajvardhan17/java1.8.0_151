package javax.swing.text;

import java.awt.ComponentOrientation;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import javax.swing.Action;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.TextUI;
import sun.awt.SunToolkit;

public class DefaultEditorKit
  extends EditorKit
{
  public static final String EndOfLineStringProperty = "__EndOfLine__";
  public static final String insertContentAction = "insert-content";
  public static final String insertBreakAction = "insert-break";
  public static final String insertTabAction = "insert-tab";
  public static final String deletePrevCharAction = "delete-previous";
  public static final String deleteNextCharAction = "delete-next";
  public static final String deleteNextWordAction = "delete-next-word";
  public static final String deletePrevWordAction = "delete-previous-word";
  public static final String readOnlyAction = "set-read-only";
  public static final String writableAction = "set-writable";
  public static final String cutAction = "cut-to-clipboard";
  public static final String copyAction = "copy-to-clipboard";
  public static final String pasteAction = "paste-from-clipboard";
  public static final String beepAction = "beep";
  public static final String pageUpAction = "page-up";
  public static final String pageDownAction = "page-down";
  static final String selectionPageUpAction = "selection-page-up";
  static final String selectionPageDownAction = "selection-page-down";
  static final String selectionPageLeftAction = "selection-page-left";
  static final String selectionPageRightAction = "selection-page-right";
  public static final String forwardAction = "caret-forward";
  public static final String backwardAction = "caret-backward";
  public static final String selectionForwardAction = "selection-forward";
  public static final String selectionBackwardAction = "selection-backward";
  public static final String upAction = "caret-up";
  public static final String downAction = "caret-down";
  public static final String selectionUpAction = "selection-up";
  public static final String selectionDownAction = "selection-down";
  public static final String beginWordAction = "caret-begin-word";
  public static final String endWordAction = "caret-end-word";
  public static final String selectionBeginWordAction = "selection-begin-word";
  public static final String selectionEndWordAction = "selection-end-word";
  public static final String previousWordAction = "caret-previous-word";
  public static final String nextWordAction = "caret-next-word";
  public static final String selectionPreviousWordAction = "selection-previous-word";
  public static final String selectionNextWordAction = "selection-next-word";
  public static final String beginLineAction = "caret-begin-line";
  public static final String endLineAction = "caret-end-line";
  public static final String selectionBeginLineAction = "selection-begin-line";
  public static final String selectionEndLineAction = "selection-end-line";
  public static final String beginParagraphAction = "caret-begin-paragraph";
  public static final String endParagraphAction = "caret-end-paragraph";
  public static final String selectionBeginParagraphAction = "selection-begin-paragraph";
  public static final String selectionEndParagraphAction = "selection-end-paragraph";
  public static final String beginAction = "caret-begin";
  public static final String endAction = "caret-end";
  public static final String selectionBeginAction = "selection-begin";
  public static final String selectionEndAction = "selection-end";
  public static final String selectWordAction = "select-word";
  public static final String selectLineAction = "select-line";
  public static final String selectParagraphAction = "select-paragraph";
  public static final String selectAllAction = "select-all";
  static final String unselectAction = "unselect";
  static final String toggleComponentOrientationAction = "toggle-componentOrientation";
  public static final String defaultKeyTypedAction = "default-typed";
  private static final Action[] defaultActions = { new InsertContentAction(), new DeletePrevCharAction(), new DeleteNextCharAction(), new ReadOnlyAction(), new DeleteWordAction("delete-previous-word"), new DeleteWordAction("delete-next-word"), new WritableAction(), new CutAction(), new CopyAction(), new PasteAction(), new VerticalPageAction("page-up", -1, false), new VerticalPageAction("page-down", 1, false), new VerticalPageAction("selection-page-up", -1, true), new VerticalPageAction("selection-page-down", 1, true), new PageAction("selection-page-left", true, true), new PageAction("selection-page-right", false, true), new InsertBreakAction(), new BeepAction(), new NextVisualPositionAction("caret-forward", false, 3), new NextVisualPositionAction("caret-backward", false, 7), new NextVisualPositionAction("selection-forward", true, 3), new NextVisualPositionAction("selection-backward", true, 7), new NextVisualPositionAction("caret-up", false, 1), new NextVisualPositionAction("caret-down", false, 5), new NextVisualPositionAction("selection-up", true, 1), new NextVisualPositionAction("selection-down", true, 5), new BeginWordAction("caret-begin-word", false), new EndWordAction("caret-end-word", false), new BeginWordAction("selection-begin-word", true), new EndWordAction("selection-end-word", true), new PreviousWordAction("caret-previous-word", false), new NextWordAction("caret-next-word", false), new PreviousWordAction("selection-previous-word", true), new NextWordAction("selection-next-word", true), new BeginLineAction("caret-begin-line", false), new EndLineAction("caret-end-line", false), new BeginLineAction("selection-begin-line", true), new EndLineAction("selection-end-line", true), new BeginParagraphAction("caret-begin-paragraph", false), new EndParagraphAction("caret-end-paragraph", false), new BeginParagraphAction("selection-begin-paragraph", true), new EndParagraphAction("selection-end-paragraph", true), new BeginAction("caret-begin", false), new EndAction("caret-end", false), new BeginAction("selection-begin", true), new EndAction("selection-end", true), new DefaultKeyTypedAction(), new InsertTabAction(), new SelectWordAction(), new SelectLineAction(), new SelectParagraphAction(), new SelectAllAction(), new UnselectAction(), new ToggleComponentOrientationAction(), new DumpModelAction() };
  
  public DefaultEditorKit() {}
  
  public String getContentType()
  {
    return "text/plain";
  }
  
  public ViewFactory getViewFactory()
  {
    return null;
  }
  
  public Action[] getActions()
  {
    return defaultActions;
  }
  
  public Caret createCaret()
  {
    return null;
  }
  
  public Document createDefaultDocument()
  {
    return new PlainDocument();
  }
  
  public void read(InputStream paramInputStream, Document paramDocument, int paramInt)
    throws IOException, BadLocationException
  {
    read(new InputStreamReader(paramInputStream), paramDocument, paramInt);
  }
  
  public void write(OutputStream paramOutputStream, Document paramDocument, int paramInt1, int paramInt2)
    throws IOException, BadLocationException
  {
    OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(paramOutputStream);
    write(localOutputStreamWriter, paramDocument, paramInt1, paramInt2);
    localOutputStreamWriter.flush();
  }
  
  MutableAttributeSet getInputAttributes()
  {
    return null;
  }
  
  public void read(Reader paramReader, Document paramDocument, int paramInt)
    throws IOException, BadLocationException
  {
    char[] arrayOfChar = new char['က'];
    int j = 0;
    int k = 0;
    int m = 0;
    int i1 = paramDocument.getLength() == 0 ? 1 : 0;
    MutableAttributeSet localMutableAttributeSet = getInputAttributes();
    int i;
    while ((i = paramReader.read(arrayOfChar, 0, arrayOfChar.length)) != -1)
    {
      int n = 0;
      for (int i2 = 0; i2 < i; i2++) {
        switch (arrayOfChar[i2])
        {
        case '\r': 
          if (j != 0)
          {
            m = 1;
            if (i2 == 0)
            {
              paramDocument.insertString(paramInt, "\n", localMutableAttributeSet);
              paramInt++;
            }
            else
            {
              arrayOfChar[(i2 - 1)] = '\n';
            }
          }
          else
          {
            j = 1;
          }
          break;
        case '\n': 
          if (j != 0)
          {
            if (i2 > n + 1)
            {
              paramDocument.insertString(paramInt, new String(arrayOfChar, n, i2 - n - 1), localMutableAttributeSet);
              paramInt += i2 - n - 1;
            }
            j = 0;
            n = i2;
            k = 1;
          }
          break;
        default: 
          if (j != 0)
          {
            m = 1;
            if (i2 == 0)
            {
              paramDocument.insertString(paramInt, "\n", localMutableAttributeSet);
              paramInt++;
            }
            else
            {
              arrayOfChar[(i2 - 1)] = '\n';
            }
            j = 0;
          }
          break;
        }
      }
      if (n < i) {
        if (j != 0)
        {
          if (n < i - 1)
          {
            paramDocument.insertString(paramInt, new String(arrayOfChar, n, i - n - 1), localMutableAttributeSet);
            paramInt += i - n - 1;
          }
        }
        else
        {
          paramDocument.insertString(paramInt, new String(arrayOfChar, n, i - n), localMutableAttributeSet);
          paramInt += i - n;
        }
      }
    }
    if (j != 0)
    {
      paramDocument.insertString(paramInt, "\n", localMutableAttributeSet);
      m = 1;
    }
    if (i1 != 0) {
      if (k != 0) {
        paramDocument.putProperty("__EndOfLine__", "\r\n");
      } else if (m != 0) {
        paramDocument.putProperty("__EndOfLine__", "\r");
      } else {
        paramDocument.putProperty("__EndOfLine__", "\n");
      }
    }
  }
  
  public void write(Writer paramWriter, Document paramDocument, int paramInt1, int paramInt2)
    throws IOException, BadLocationException
  {
    if ((paramInt1 < 0) || (paramInt1 + paramInt2 > paramDocument.getLength())) {
      throw new BadLocationException("DefaultEditorKit.write", paramInt1);
    }
    Segment localSegment = new Segment();
    int i = paramInt2;
    int j = paramInt1;
    Object localObject = paramDocument.getProperty("__EndOfLine__");
    if (localObject == null) {
      try
      {
        localObject = System.getProperty("line.separator");
      }
      catch (SecurityException localSecurityException) {}
    }
    String str;
    if ((localObject instanceof String)) {
      str = (String)localObject;
    } else {
      str = null;
    }
    if ((localObject != null) && (!str.equals("\n"))) {}
    while (i > 0)
    {
      int k = Math.min(i, 4096);
      paramDocument.getText(j, k, localSegment);
      int m = offset;
      char[] arrayOfChar = array;
      int n = m + count;
      for (int i1 = m; i1 < n; i1++) {
        if (arrayOfChar[i1] == '\n')
        {
          if (i1 > m) {
            paramWriter.write(arrayOfChar, m, i1 - m);
          }
          paramWriter.write(str);
          m = i1 + 1;
        }
      }
      if (n > m) {
        paramWriter.write(arrayOfChar, m, n - m);
      }
      j += k;
      i -= k;
      continue;
      while (i > 0)
      {
        k = Math.min(i, 4096);
        paramDocument.getText(j, k, localSegment);
        paramWriter.write(array, offset, count);
        j += k;
        i -= k;
      }
    }
    paramWriter.flush();
  }
  
  public static class BeepAction
    extends TextAction
  {
    public BeepAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      UIManager.getLookAndFeel().provideErrorFeedback(localJTextComponent);
    }
  }
  
  static class BeginAction
    extends TextAction
  {
    private boolean select;
    
    BeginAction(String paramString, boolean paramBoolean)
    {
      super();
      select = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null) {
        if (select) {
          localJTextComponent.moveCaretPosition(0);
        } else {
          localJTextComponent.setCaretPosition(0);
        }
      }
    }
  }
  
  static class BeginLineAction
    extends TextAction
  {
    private boolean select;
    
    BeginLineAction(String paramString, boolean paramBoolean)
    {
      super();
      select = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null) {
        try
        {
          int i = localJTextComponent.getCaretPosition();
          int j = Utilities.getRowStart(localJTextComponent, i);
          if (select) {
            localJTextComponent.moveCaretPosition(j);
          } else {
            localJTextComponent.setCaretPosition(j);
          }
        }
        catch (BadLocationException localBadLocationException)
        {
          UIManager.getLookAndFeel().provideErrorFeedback(localJTextComponent);
        }
      }
    }
  }
  
  static class BeginParagraphAction
    extends TextAction
  {
    private boolean select;
    
    BeginParagraphAction(String paramString, boolean paramBoolean)
    {
      super();
      select = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null)
      {
        int i = localJTextComponent.getCaretPosition();
        Element localElement = Utilities.getParagraphElement(localJTextComponent, i);
        i = localElement.getStartOffset();
        if (select) {
          localJTextComponent.moveCaretPosition(i);
        } else {
          localJTextComponent.setCaretPosition(i);
        }
      }
    }
  }
  
  static class BeginWordAction
    extends TextAction
  {
    private boolean select;
    
    BeginWordAction(String paramString, boolean paramBoolean)
    {
      super();
      select = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null) {
        try
        {
          int i = localJTextComponent.getCaretPosition();
          int j = Utilities.getWordStart(localJTextComponent, i);
          if (select) {
            localJTextComponent.moveCaretPosition(j);
          } else {
            localJTextComponent.setCaretPosition(j);
          }
        }
        catch (BadLocationException localBadLocationException)
        {
          UIManager.getLookAndFeel().provideErrorFeedback(localJTextComponent);
        }
      }
    }
  }
  
  public static class CopyAction
    extends TextAction
  {
    public CopyAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null) {
        localJTextComponent.copy();
      }
    }
  }
  
  public static class CutAction
    extends TextAction
  {
    public CutAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null) {
        localJTextComponent.cut();
      }
    }
  }
  
  public static class DefaultKeyTypedAction
    extends TextAction
  {
    public DefaultKeyTypedAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if ((localJTextComponent != null) && (paramActionEvent != null))
      {
        if ((!localJTextComponent.isEditable()) || (!localJTextComponent.isEnabled())) {
          return;
        }
        String str = paramActionEvent.getActionCommand();
        int i = paramActionEvent.getModifiers();
        if ((str != null) && (str.length() > 0))
        {
          boolean bool = true;
          Toolkit localToolkit = Toolkit.getDefaultToolkit();
          if ((localToolkit instanceof SunToolkit)) {
            bool = ((SunToolkit)localToolkit).isPrintableCharacterModifiersMask(i);
          }
          if (bool)
          {
            int j = str.charAt(0);
            if ((j >= 32) && (j != 127)) {
              localJTextComponent.replaceSelection(str);
            }
          }
        }
      }
    }
  }
  
  static class DeleteNextCharAction
    extends TextAction
  {
    DeleteNextCharAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      int i = 1;
      if ((localJTextComponent != null) && (localJTextComponent.isEditable())) {
        try
        {
          Document localDocument = localJTextComponent.getDocument();
          Caret localCaret = localJTextComponent.getCaret();
          int j = localCaret.getDot();
          int k = localCaret.getMark();
          if (j != k)
          {
            localDocument.remove(Math.min(j, k), Math.abs(j - k));
            i = 0;
          }
          else if (j < localDocument.getLength())
          {
            int m = 1;
            if (j < localDocument.getLength() - 1)
            {
              String str = localDocument.getText(j, 2);
              int n = str.charAt(0);
              int i1 = str.charAt(1);
              if ((n >= 55296) && (n <= 56319) && (i1 >= 56320) && (i1 <= 57343)) {
                m = 2;
              }
            }
            localDocument.remove(j, m);
            i = 0;
          }
        }
        catch (BadLocationException localBadLocationException) {}
      }
      if (i != 0) {
        UIManager.getLookAndFeel().provideErrorFeedback(localJTextComponent);
      }
    }
  }
  
  static class DeletePrevCharAction
    extends TextAction
  {
    DeletePrevCharAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      int i = 1;
      if ((localJTextComponent != null) && (localJTextComponent.isEditable())) {
        try
        {
          Document localDocument = localJTextComponent.getDocument();
          Caret localCaret = localJTextComponent.getCaret();
          int j = localCaret.getDot();
          int k = localCaret.getMark();
          if (j != k)
          {
            localDocument.remove(Math.min(j, k), Math.abs(j - k));
            i = 0;
          }
          else if (j > 0)
          {
            int m = 1;
            if (j > 1)
            {
              String str = localDocument.getText(j - 2, 2);
              int n = str.charAt(0);
              int i1 = str.charAt(1);
              if ((n >= 55296) && (n <= 56319) && (i1 >= 56320) && (i1 <= 57343)) {
                m = 2;
              }
            }
            localDocument.remove(j - m, m);
            i = 0;
          }
        }
        catch (BadLocationException localBadLocationException) {}
      }
      if (i != 0) {
        UIManager.getLookAndFeel().provideErrorFeedback(localJTextComponent);
      }
    }
  }
  
  static class DeleteWordAction
    extends TextAction
  {
    DeleteWordAction(String paramString)
    {
      super();
      assert ((paramString == "delete-previous-word") || (paramString == "delete-next-word"));
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if ((localJTextComponent != null) && (paramActionEvent != null))
      {
        if ((!localJTextComponent.isEditable()) || (!localJTextComponent.isEnabled()))
        {
          UIManager.getLookAndFeel().provideErrorFeedback(localJTextComponent);
          return;
        }
        int i = 1;
        try
        {
          int j = localJTextComponent.getSelectionStart();
          Element localElement = Utilities.getParagraphElement(localJTextComponent, j);
          int k;
          if ("delete-next-word" == getValue("Name"))
          {
            k = Utilities.getNextWordInParagraph(localJTextComponent, localElement, j, false);
            if (k == -1)
            {
              m = localElement.getEndOffset();
              if (j == m - 1) {
                k = m;
              } else {
                k = m - 1;
              }
            }
          }
          else
          {
            k = Utilities.getPrevWordInParagraph(localJTextComponent, localElement, j);
            if (k == -1)
            {
              m = localElement.getStartOffset();
              if (j == m) {
                k = m - 1;
              } else {
                k = m;
              }
            }
          }
          int m = Math.min(j, k);
          int n = Math.abs(k - j);
          if (m >= 0)
          {
            localJTextComponent.getDocument().remove(m, n);
            i = 0;
          }
        }
        catch (BadLocationException localBadLocationException) {}
        if (i != 0) {
          UIManager.getLookAndFeel().provideErrorFeedback(localJTextComponent);
        }
      }
    }
  }
  
  static class DumpModelAction
    extends TextAction
  {
    DumpModelAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null)
      {
        Document localDocument = localJTextComponent.getDocument();
        if ((localDocument instanceof AbstractDocument)) {
          ((AbstractDocument)localDocument).dump(System.err);
        }
      }
    }
  }
  
  static class EndAction
    extends TextAction
  {
    private boolean select;
    
    EndAction(String paramString, boolean paramBoolean)
    {
      super();
      select = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null)
      {
        Document localDocument = localJTextComponent.getDocument();
        int i = localDocument.getLength();
        if (select) {
          localJTextComponent.moveCaretPosition(i);
        } else {
          localJTextComponent.setCaretPosition(i);
        }
      }
    }
  }
  
  static class EndLineAction
    extends TextAction
  {
    private boolean select;
    
    EndLineAction(String paramString, boolean paramBoolean)
    {
      super();
      select = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null) {
        try
        {
          int i = localJTextComponent.getCaretPosition();
          int j = Utilities.getRowEnd(localJTextComponent, i);
          if (select) {
            localJTextComponent.moveCaretPosition(j);
          } else {
            localJTextComponent.setCaretPosition(j);
          }
        }
        catch (BadLocationException localBadLocationException)
        {
          UIManager.getLookAndFeel().provideErrorFeedback(localJTextComponent);
        }
      }
    }
  }
  
  static class EndParagraphAction
    extends TextAction
  {
    private boolean select;
    
    EndParagraphAction(String paramString, boolean paramBoolean)
    {
      super();
      select = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null)
      {
        int i = localJTextComponent.getCaretPosition();
        Element localElement = Utilities.getParagraphElement(localJTextComponent, i);
        i = Math.min(localJTextComponent.getDocument().getLength(), localElement.getEndOffset());
        if (select) {
          localJTextComponent.moveCaretPosition(i);
        } else {
          localJTextComponent.setCaretPosition(i);
        }
      }
    }
  }
  
  static class EndWordAction
    extends TextAction
  {
    private boolean select;
    
    EndWordAction(String paramString, boolean paramBoolean)
    {
      super();
      select = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null) {
        try
        {
          int i = localJTextComponent.getCaretPosition();
          int j = Utilities.getWordEnd(localJTextComponent, i);
          if (select) {
            localJTextComponent.moveCaretPosition(j);
          } else {
            localJTextComponent.setCaretPosition(j);
          }
        }
        catch (BadLocationException localBadLocationException)
        {
          UIManager.getLookAndFeel().provideErrorFeedback(localJTextComponent);
        }
      }
    }
  }
  
  public static class InsertBreakAction
    extends TextAction
  {
    public InsertBreakAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null)
      {
        if ((!localJTextComponent.isEditable()) || (!localJTextComponent.isEnabled()))
        {
          UIManager.getLookAndFeel().provideErrorFeedback(localJTextComponent);
          return;
        }
        localJTextComponent.replaceSelection("\n");
      }
    }
  }
  
  public static class InsertContentAction
    extends TextAction
  {
    public InsertContentAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if ((localJTextComponent != null) && (paramActionEvent != null))
      {
        if ((!localJTextComponent.isEditable()) || (!localJTextComponent.isEnabled()))
        {
          UIManager.getLookAndFeel().provideErrorFeedback(localJTextComponent);
          return;
        }
        String str = paramActionEvent.getActionCommand();
        if (str != null) {
          localJTextComponent.replaceSelection(str);
        } else {
          UIManager.getLookAndFeel().provideErrorFeedback(localJTextComponent);
        }
      }
    }
  }
  
  public static class InsertTabAction
    extends TextAction
  {
    public InsertTabAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null)
      {
        if ((!localJTextComponent.isEditable()) || (!localJTextComponent.isEnabled()))
        {
          UIManager.getLookAndFeel().provideErrorFeedback(localJTextComponent);
          return;
        }
        localJTextComponent.replaceSelection("\t");
      }
    }
  }
  
  static class NextVisualPositionAction
    extends TextAction
  {
    private boolean select;
    private int direction;
    
    NextVisualPositionAction(String paramString, boolean paramBoolean, int paramInt)
    {
      super();
      select = paramBoolean;
      direction = paramInt;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null)
      {
        Caret localCaret = localJTextComponent.getCaret();
        Object localObject1 = (localCaret instanceof DefaultCaret) ? (DefaultCaret)localCaret : null;
        int i = localCaret.getDot();
        Position.Bias[] arrayOfBias = new Position.Bias[1];
        Point localPoint = localCaret.getMagicCaretPosition();
        try
        {
          if ((localPoint == null) && ((direction == 1) || (direction == 5)))
          {
            localObject2 = localObject1 != null ? localJTextComponent.getUI().modelToView(localJTextComponent, i, ((DefaultCaret)localObject1).getDotBias()) : localJTextComponent.modelToView(i);
            localPoint = new Point(x, y);
          }
          Object localObject2 = localJTextComponent.getNavigationFilter();
          if (localObject2 != null) {
            i = ((NavigationFilter)localObject2).getNextVisualPositionFrom(localJTextComponent, i, localObject1 != null ? ((DefaultCaret)localObject1).getDotBias() : Position.Bias.Forward, direction, arrayOfBias);
          } else {
            i = localJTextComponent.getUI().getNextVisualPositionFrom(localJTextComponent, i, localObject1 != null ? ((DefaultCaret)localObject1).getDotBias() : Position.Bias.Forward, direction, arrayOfBias);
          }
          if (arrayOfBias[0] == null) {
            arrayOfBias[0] = Position.Bias.Forward;
          }
          if (localObject1 != null)
          {
            if (select) {
              ((DefaultCaret)localObject1).moveDot(i, arrayOfBias[0]);
            } else {
              ((DefaultCaret)localObject1).setDot(i, arrayOfBias[0]);
            }
          }
          else if (select) {
            localCaret.moveDot(i);
          } else {
            localCaret.setDot(i);
          }
          if ((localPoint != null) && ((direction == 1) || (direction == 5))) {
            localJTextComponent.getCaret().setMagicCaretPosition(localPoint);
          }
        }
        catch (BadLocationException localBadLocationException) {}
      }
    }
  }
  
  static class NextWordAction
    extends TextAction
  {
    private boolean select;
    
    NextWordAction(String paramString, boolean paramBoolean)
    {
      super();
      select = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null)
      {
        int i = localJTextComponent.getCaretPosition();
        int j = 0;
        int k = i;
        Element localElement = Utilities.getParagraphElement(localJTextComponent, i);
        try
        {
          i = Utilities.getNextWord(localJTextComponent, i);
          if ((i >= localElement.getEndOffset()) && (k != localElement.getEndOffset() - 1)) {
            i = localElement.getEndOffset() - 1;
          }
        }
        catch (BadLocationException localBadLocationException)
        {
          int m = localJTextComponent.getDocument().getLength();
          if (i != m)
          {
            if (k != localElement.getEndOffset() - 1) {
              i = localElement.getEndOffset() - 1;
            } else {
              i = m;
            }
          }
          else {
            j = 1;
          }
        }
        if (j == 0)
        {
          if (select) {
            localJTextComponent.moveCaretPosition(i);
          } else {
            localJTextComponent.setCaretPosition(i);
          }
        }
        else {
          UIManager.getLookAndFeel().provideErrorFeedback(localJTextComponent);
        }
      }
    }
  }
  
  static class PageAction
    extends TextAction
  {
    private boolean select;
    private boolean left;
    
    public PageAction(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    {
      super();
      select = paramBoolean2;
      left = paramBoolean1;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null)
      {
        Rectangle localRectangle = new Rectangle();
        localJTextComponent.computeVisibleRect(localRectangle);
        if (left) {
          x = Math.max(0, x - width);
        } else {
          x += width;
        }
        int i = localJTextComponent.getCaretPosition();
        if (i != -1)
        {
          if (left) {
            i = localJTextComponent.viewToModel(new Point(x, y));
          } else {
            i = localJTextComponent.viewToModel(new Point(x + width - 1, y + height - 1));
          }
          Document localDocument = localJTextComponent.getDocument();
          if ((i != 0) && (i > localDocument.getLength() - 1)) {
            i = localDocument.getLength() - 1;
          } else if (i < 0) {
            i = 0;
          }
          if (select) {
            localJTextComponent.moveCaretPosition(i);
          } else {
            localJTextComponent.setCaretPosition(i);
          }
        }
      }
    }
  }
  
  public static class PasteAction
    extends TextAction
  {
    public PasteAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null) {
        localJTextComponent.paste();
      }
    }
  }
  
  static class PreviousWordAction
    extends TextAction
  {
    private boolean select;
    
    PreviousWordAction(String paramString, boolean paramBoolean)
    {
      super();
      select = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null)
      {
        int i = localJTextComponent.getCaretPosition();
        int j = 0;
        try
        {
          Element localElement = Utilities.getParagraphElement(localJTextComponent, i);
          i = Utilities.getPreviousWord(localJTextComponent, i);
          if (i < localElement.getStartOffset()) {
            i = Utilities.getParagraphElement(localJTextComponent, i).getEndOffset() - 1;
          }
        }
        catch (BadLocationException localBadLocationException)
        {
          if (i != 0) {
            i = 0;
          } else {
            j = 1;
          }
        }
        if (j == 0)
        {
          if (select) {
            localJTextComponent.moveCaretPosition(i);
          } else {
            localJTextComponent.setCaretPosition(i);
          }
        }
        else {
          UIManager.getLookAndFeel().provideErrorFeedback(localJTextComponent);
        }
      }
    }
  }
  
  static class ReadOnlyAction
    extends TextAction
  {
    ReadOnlyAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null) {
        localJTextComponent.setEditable(false);
      }
    }
  }
  
  static class SelectAllAction
    extends TextAction
  {
    SelectAllAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null)
      {
        Document localDocument = localJTextComponent.getDocument();
        localJTextComponent.setCaretPosition(0);
        localJTextComponent.moveCaretPosition(localDocument.getLength());
      }
    }
  }
  
  static class SelectLineAction
    extends TextAction
  {
    private Action start = new DefaultEditorKit.BeginLineAction("pigdog", false);
    private Action end = new DefaultEditorKit.EndLineAction("pigdog", true);
    
    SelectLineAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      start.actionPerformed(paramActionEvent);
      end.actionPerformed(paramActionEvent);
    }
  }
  
  static class SelectParagraphAction
    extends TextAction
  {
    private Action start = new DefaultEditorKit.BeginParagraphAction("pigdog", false);
    private Action end = new DefaultEditorKit.EndParagraphAction("pigdog", true);
    
    SelectParagraphAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      start.actionPerformed(paramActionEvent);
      end.actionPerformed(paramActionEvent);
    }
  }
  
  static class SelectWordAction
    extends TextAction
  {
    private Action start = new DefaultEditorKit.BeginWordAction("pigdog", false);
    private Action end = new DefaultEditorKit.EndWordAction("pigdog", true);
    
    SelectWordAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      start.actionPerformed(paramActionEvent);
      end.actionPerformed(paramActionEvent);
    }
  }
  
  static class ToggleComponentOrientationAction
    extends TextAction
  {
    ToggleComponentOrientationAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null)
      {
        ComponentOrientation localComponentOrientation1 = localJTextComponent.getComponentOrientation();
        ComponentOrientation localComponentOrientation2;
        if (localComponentOrientation1 == ComponentOrientation.RIGHT_TO_LEFT) {
          localComponentOrientation2 = ComponentOrientation.LEFT_TO_RIGHT;
        } else {
          localComponentOrientation2 = ComponentOrientation.RIGHT_TO_LEFT;
        }
        localJTextComponent.setComponentOrientation(localComponentOrientation2);
        localJTextComponent.repaint();
      }
    }
  }
  
  static class UnselectAction
    extends TextAction
  {
    UnselectAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null) {
        localJTextComponent.setCaretPosition(localJTextComponent.getCaretPosition());
      }
    }
  }
  
  static class VerticalPageAction
    extends TextAction
  {
    private boolean select;
    private int direction;
    
    public VerticalPageAction(String paramString, int paramInt, boolean paramBoolean)
    {
      super();
      select = paramBoolean;
      direction = paramInt;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null)
      {
        Rectangle localRectangle1 = localJTextComponent.getVisibleRect();
        Rectangle localRectangle2 = new Rectangle(localRectangle1);
        int i = localJTextComponent.getCaretPosition();
        int j = direction * localJTextComponent.getScrollableBlockIncrement(localRectangle1, 1, direction);
        int k = y;
        Caret localCaret = localJTextComponent.getCaret();
        Point localPoint = localCaret.getMagicCaretPosition();
        if (i != -1) {
          try
          {
            Rectangle localRectangle3 = localJTextComponent.modelToView(i);
            int m = localPoint != null ? x : x;
            int n = height;
            if (n > 0) {
              j = j / n * n;
            }
            y = constrainY(localJTextComponent, k + j, height);
            if (localRectangle1.contains(x, y)) {
              i1 = localJTextComponent.viewToModel(new Point(m, constrainY(localJTextComponent, y + j, 0)));
            } else if (direction == -1) {
              i1 = localJTextComponent.viewToModel(new Point(m, y));
            } else {
              i1 = localJTextComponent.viewToModel(new Point(m, y + height));
            }
            int i1 = constrainOffset(localJTextComponent, i1);
            if (i1 != i)
            {
              int i2 = getAdjustedY(localJTextComponent, localRectangle2, i1);
              if (((direction == -1) && (i2 <= k)) || ((direction == 1) && (i2 >= k)))
              {
                y = i2;
                if (select) {
                  localJTextComponent.moveCaretPosition(i1);
                } else {
                  localJTextComponent.setCaretPosition(i1);
                }
              }
            }
          }
          catch (BadLocationException localBadLocationException) {}
        } else {
          y = constrainY(localJTextComponent, k + j, height);
        }
        if (localPoint != null) {
          localCaret.setMagicCaretPosition(localPoint);
        }
        localJTextComponent.scrollRectToVisible(localRectangle2);
      }
    }
    
    private int constrainY(JTextComponent paramJTextComponent, int paramInt1, int paramInt2)
    {
      if (paramInt1 < 0) {
        paramInt1 = 0;
      } else if (paramInt1 + paramInt2 > paramJTextComponent.getHeight()) {
        paramInt1 = Math.max(0, paramJTextComponent.getHeight() - paramInt2);
      }
      return paramInt1;
    }
    
    private int constrainOffset(JTextComponent paramJTextComponent, int paramInt)
    {
      Document localDocument = paramJTextComponent.getDocument();
      if ((paramInt != 0) && (paramInt > localDocument.getLength())) {
        paramInt = localDocument.getLength();
      }
      if (paramInt < 0) {
        paramInt = 0;
      }
      return paramInt;
    }
    
    private int getAdjustedY(JTextComponent paramJTextComponent, Rectangle paramRectangle, int paramInt)
    {
      int i = y;
      try
      {
        Rectangle localRectangle = paramJTextComponent.modelToView(paramInt);
        if (y < y) {
          i = y;
        } else if ((y > y + height) || (y + height > y + height)) {
          i = y + height - height;
        }
      }
      catch (BadLocationException localBadLocationException) {}
      return i;
    }
  }
  
  static class WritableAction
    extends TextAction
  {
    WritableAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if (localJTextComponent != null) {
        localJTextComponent.setEditable(true);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\DefaultEditorKit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */