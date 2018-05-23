package javax.swing.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import javax.swing.Action;
import javax.swing.JEditorPane;

public abstract class EditorKit
  implements Cloneable, Serializable
{
  public EditorKit() {}
  
  public Object clone()
  {
    Object localObject;
    try
    {
      localObject = super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      localObject = null;
    }
    return localObject;
  }
  
  public void install(JEditorPane paramJEditorPane) {}
  
  public void deinstall(JEditorPane paramJEditorPane) {}
  
  public abstract String getContentType();
  
  public abstract ViewFactory getViewFactory();
  
  public abstract Action[] getActions();
  
  public abstract Caret createCaret();
  
  public abstract Document createDefaultDocument();
  
  public abstract void read(InputStream paramInputStream, Document paramDocument, int paramInt)
    throws IOException, BadLocationException;
  
  public abstract void write(OutputStream paramOutputStream, Document paramDocument, int paramInt1, int paramInt2)
    throws IOException, BadLocationException;
  
  public abstract void read(Reader paramReader, Document paramDocument, int paramInt)
    throws IOException, BadLocationException;
  
  public abstract void write(Writer paramWriter, Document paramDocument, int paramInt1, int paramInt2)
    throws IOException, BadLocationException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\EditorKit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */