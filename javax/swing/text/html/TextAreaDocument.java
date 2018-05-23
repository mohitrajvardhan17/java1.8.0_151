package javax.swing.text.html;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

class TextAreaDocument
  extends PlainDocument
{
  String initialText;
  
  TextAreaDocument() {}
  
  void reset()
  {
    try
    {
      remove(0, getLength());
      if (initialText != null) {
        insertString(0, initialText, null);
      }
    }
    catch (BadLocationException localBadLocationException) {}
  }
  
  void storeInitialText()
  {
    try
    {
      initialText = getText(0, getLength());
    }
    catch (BadLocationException localBadLocationException) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\TextAreaDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */