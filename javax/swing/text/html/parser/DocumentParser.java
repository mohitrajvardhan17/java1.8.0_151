package javax.swing.text.html.parser;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;

public class DocumentParser
  extends Parser
{
  private int inbody;
  private int intitle;
  private int inhead;
  private int instyle;
  private int inscript;
  private boolean seentitle;
  private HTMLEditorKit.ParserCallback callback = null;
  private boolean ignoreCharSet = false;
  private static final boolean debugFlag = false;
  
  public DocumentParser(DTD paramDTD)
  {
    super(paramDTD);
  }
  
  public void parse(Reader paramReader, HTMLEditorKit.ParserCallback paramParserCallback, boolean paramBoolean)
    throws IOException
  {
    ignoreCharSet = paramBoolean;
    callback = paramParserCallback;
    parse(paramReader);
    paramParserCallback.handleEndOfLineString(getEndOfLineString());
  }
  
  protected void handleStartTag(TagElement paramTagElement)
  {
    Element localElement = paramTagElement.getElement();
    if (localElement == dtd.body) {
      inbody += 1;
    } else if (localElement != dtd.html) {
      if (localElement == dtd.head) {
        inhead += 1;
      } else if (localElement == dtd.title) {
        intitle += 1;
      } else if (localElement == dtd.style) {
        instyle += 1;
      } else if (localElement == dtd.script) {
        inscript += 1;
      }
    }
    if (paramTagElement.fictional())
    {
      SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
      localSimpleAttributeSet.addAttribute(HTMLEditorKit.ParserCallback.IMPLIED, Boolean.TRUE);
      callback.handleStartTag(paramTagElement.getHTMLTag(), localSimpleAttributeSet, getBlockStartPosition());
    }
    else
    {
      callback.handleStartTag(paramTagElement.getHTMLTag(), getAttributes(), getBlockStartPosition());
      flushAttributes();
    }
  }
  
  protected void handleComment(char[] paramArrayOfChar)
  {
    callback.handleComment(paramArrayOfChar, getBlockStartPosition());
  }
  
  protected void handleEmptyTag(TagElement paramTagElement)
    throws ChangedCharSetException
  {
    Element localElement = paramTagElement.getElement();
    SimpleAttributeSet localSimpleAttributeSet;
    if ((localElement == dtd.meta) && (!ignoreCharSet))
    {
      localSimpleAttributeSet = getAttributes();
      if (localSimpleAttributeSet != null)
      {
        String str = (String)localSimpleAttributeSet.getAttribute(HTML.Attribute.CONTENT);
        if (str != null) {
          if ("content-type".equalsIgnoreCase((String)localSimpleAttributeSet.getAttribute(HTML.Attribute.HTTPEQUIV)))
          {
            if ((!str.equalsIgnoreCase("text/html")) && (!str.equalsIgnoreCase("text/plain"))) {
              throw new ChangedCharSetException(str, false);
            }
          }
          else if ("charset".equalsIgnoreCase((String)localSimpleAttributeSet.getAttribute(HTML.Attribute.HTTPEQUIV))) {
            throw new ChangedCharSetException(str, true);
          }
        }
      }
    }
    if ((inbody != 0) || (localElement == dtd.meta) || (localElement == dtd.base) || (localElement == dtd.isindex) || (localElement == dtd.style) || (localElement == dtd.link)) {
      if (paramTagElement.fictional())
      {
        localSimpleAttributeSet = new SimpleAttributeSet();
        localSimpleAttributeSet.addAttribute(HTMLEditorKit.ParserCallback.IMPLIED, Boolean.TRUE);
        callback.handleSimpleTag(paramTagElement.getHTMLTag(), localSimpleAttributeSet, getBlockStartPosition());
      }
      else
      {
        callback.handleSimpleTag(paramTagElement.getHTMLTag(), getAttributes(), getBlockStartPosition());
        flushAttributes();
      }
    }
  }
  
  protected void handleEndTag(TagElement paramTagElement)
  {
    Element localElement = paramTagElement.getElement();
    if (localElement == dtd.body)
    {
      inbody -= 1;
    }
    else if (localElement == dtd.title)
    {
      intitle -= 1;
      seentitle = true;
    }
    else if (localElement == dtd.head)
    {
      inhead -= 1;
    }
    else if (localElement == dtd.style)
    {
      instyle -= 1;
    }
    else if (localElement == dtd.script)
    {
      inscript -= 1;
    }
    callback.handleEndTag(paramTagElement.getHTMLTag(), getBlockStartPosition());
  }
  
  protected void handleText(char[] paramArrayOfChar)
  {
    if (paramArrayOfChar != null)
    {
      if (inscript != 0)
      {
        callback.handleComment(paramArrayOfChar, getBlockStartPosition());
        return;
      }
      if ((inbody != 0) || (instyle != 0) || ((intitle != 0) && (!seentitle))) {
        callback.handleText(paramArrayOfChar, getBlockStartPosition());
      }
    }
  }
  
  protected void handleError(int paramInt, String paramString)
  {
    callback.handleError(paramString, getCurrentPos());
  }
  
  private void debug(String paramString)
  {
    System.out.println(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\parser\DocumentParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */