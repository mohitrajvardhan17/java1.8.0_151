package javax.swing.text.html.parser;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Attribute;

public class Parser
  implements DTDConstants
{
  private char[] text = new char['Ѐ'];
  private int textpos = 0;
  private TagElement last;
  private boolean space;
  private char[] str = new char[''];
  private int strpos = 0;
  protected DTD dtd = null;
  private int ch;
  private int ln;
  private Reader in;
  private Element recent;
  private TagStack stack;
  private boolean skipTag = false;
  private TagElement lastFormSent = null;
  private SimpleAttributeSet attributes = new SimpleAttributeSet();
  private boolean seenHtml = false;
  private boolean seenHead = false;
  private boolean seenBody = false;
  private boolean ignoreSpace;
  protected boolean strict = false;
  private int crlfCount;
  private int crCount;
  private int lfCount;
  private int currentBlockStartPos;
  private int lastBlockStartPos;
  private static final char[] cp1252Map = { '‚', 'ƒ', '„', '…', '†', '‡', 'ˆ', '‰', 'Š', '‹', 'Œ', '', '', '', '', '‘', '’', '“', '”', '•', '–', '—', '˜', '™', 'š', '›', 'œ', '', '', 'Ÿ' };
  private static final String START_COMMENT = "<!--";
  private static final String END_COMMENT = "-->";
  private static final char[] SCRIPT_END_TAG = "</script>".toCharArray();
  private static final char[] SCRIPT_END_TAG_UPPER_CASE = "</SCRIPT>".toCharArray();
  private char[] buf = new char[1];
  private int pos;
  private int len;
  private int currentPosition;
  
  public Parser(DTD paramDTD)
  {
    dtd = paramDTD;
  }
  
  protected int getCurrentLine()
  {
    return ln;
  }
  
  int getBlockStartPosition()
  {
    return Math.max(0, lastBlockStartPos - 1);
  }
  
  protected TagElement makeTag(Element paramElement, boolean paramBoolean)
  {
    return new TagElement(paramElement, paramBoolean);
  }
  
  protected TagElement makeTag(Element paramElement)
  {
    return makeTag(paramElement, false);
  }
  
  protected SimpleAttributeSet getAttributes()
  {
    return attributes;
  }
  
  protected void flushAttributes()
  {
    attributes.removeAttributes(attributes);
  }
  
  protected void handleText(char[] paramArrayOfChar) {}
  
  protected void handleTitle(char[] paramArrayOfChar)
  {
    handleText(paramArrayOfChar);
  }
  
  protected void handleComment(char[] paramArrayOfChar) {}
  
  protected void handleEOFInComment()
  {
    int i = strIndexOf('\n');
    if (i >= 0)
    {
      handleComment(getChars(0, i));
      try
      {
        in.close();
        in = new CharArrayReader(getChars(i + 1));
        ch = 62;
      }
      catch (IOException localIOException)
      {
        error("ioexception");
      }
      resetStrBuffer();
    }
    else
    {
      error("eof.comment");
    }
  }
  
  protected void handleEmptyTag(TagElement paramTagElement)
    throws ChangedCharSetException
  {}
  
  protected void handleStartTag(TagElement paramTagElement) {}
  
  protected void handleEndTag(TagElement paramTagElement) {}
  
  protected void handleError(int paramInt, String paramString) {}
  
  void handleText(TagElement paramTagElement)
  {
    if (paramTagElement.breaksFlow())
    {
      space = false;
      if (!strict) {
        ignoreSpace = true;
      }
    }
    if ((textpos == 0) && ((!space) || (stack == null) || (last.breaksFlow()) || (!stack.advance(dtd.pcdata))))
    {
      last = paramTagElement;
      space = false;
      lastBlockStartPos = currentBlockStartPos;
      return;
    }
    if (space)
    {
      if (!ignoreSpace)
      {
        if (textpos + 1 > text.length)
        {
          arrayOfChar = new char[text.length + 200];
          System.arraycopy(text, 0, arrayOfChar, 0, text.length);
          text = arrayOfChar;
        }
        text[(textpos++)] = ' ';
        if ((!strict) && (!paramTagElement.getElement().isEmpty())) {
          ignoreSpace = true;
        }
      }
      space = false;
    }
    char[] arrayOfChar = new char[textpos];
    System.arraycopy(text, 0, arrayOfChar, 0, textpos);
    if (paramTagElement.getElement().getName().equals("title")) {
      handleTitle(arrayOfChar);
    } else {
      handleText(arrayOfChar);
    }
    lastBlockStartPos = currentBlockStartPos;
    textpos = 0;
    last = paramTagElement;
    space = false;
  }
  
  protected void error(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    handleError(ln, paramString1 + " " + paramString2 + " " + paramString3 + " " + paramString4);
  }
  
  protected void error(String paramString1, String paramString2, String paramString3)
  {
    error(paramString1, paramString2, paramString3, "?");
  }
  
  protected void error(String paramString1, String paramString2)
  {
    error(paramString1, paramString2, "?", "?");
  }
  
  protected void error(String paramString)
  {
    error(paramString, "?", "?", "?");
  }
  
  protected void startTag(TagElement paramTagElement)
    throws ChangedCharSetException
  {
    Element localElement = paramTagElement.getElement();
    if ((!localElement.isEmpty()) || ((last != null) && (!last.breaksFlow())) || (textpos != 0))
    {
      handleText(paramTagElement);
    }
    else
    {
      last = paramTagElement;
      space = false;
    }
    lastBlockStartPos = currentBlockStartPos;
    for (AttributeList localAttributeList = atts; localAttributeList != null; localAttributeList = next) {
      if ((modifier == 2) && ((attributes.isEmpty()) || ((!attributes.isDefined(name)) && (!attributes.isDefined(HTML.getAttributeKey(name)))))) {
        error("req.att ", localAttributeList.getName(), localElement.getName());
      }
    }
    if (localElement.isEmpty())
    {
      handleEmptyTag(paramTagElement);
    }
    else
    {
      recent = localElement;
      stack = new TagStack(paramTagElement, stack);
      handleStartTag(paramTagElement);
    }
  }
  
  protected void endTag(boolean paramBoolean)
  {
    handleText(stack.tag);
    if ((paramBoolean) && (!stack.elem.omitEnd())) {
      error("end.missing", stack.elem.getName());
    } else if (!stack.terminate()) {
      error("end.unexpected", stack.elem.getName());
    }
    handleEndTag(stack.tag);
    stack = stack.next;
    recent = (stack != null ? stack.elem : null);
  }
  
  boolean ignoreElement(Element paramElement)
  {
    String str1 = stack.elem.getName();
    String str2 = paramElement.getName();
    if (((str2.equals("html")) && (seenHtml)) || ((str2.equals("head")) && (seenHead)) || ((str2.equals("body")) && (seenBody))) {
      return true;
    }
    if ((str2.equals("dt")) || (str2.equals("dd")))
    {
      for (TagStack localTagStack = stack; (localTagStack != null) && (!elem.getName().equals("dl")); localTagStack = next) {}
      if (localTagStack == null) {
        return true;
      }
    }
    return ((str1.equals("table")) && (!str2.equals("#pcdata")) && (!str2.equals("input"))) || ((str2.equals("font")) && ((str1.equals("ul")) || (str1.equals("ol")))) || ((str2.equals("meta")) && (stack != null)) || ((str2.equals("style")) && (seenBody)) || ((str1.equals("table")) && (str2.equals("a")));
  }
  
  protected void markFirstTime(Element paramElement)
  {
    String str1 = paramElement.getName();
    if (str1.equals("html"))
    {
      seenHtml = true;
    }
    else if (str1.equals("head"))
    {
      seenHead = true;
    }
    else if (str1.equals("body"))
    {
      if (buf.length == 1)
      {
        char[] arrayOfChar = new char['Ā'];
        arrayOfChar[0] = buf[0];
        buf = arrayOfChar;
      }
      seenBody = true;
    }
  }
  
  boolean legalElementContext(Element paramElement)
    throws ChangedCharSetException
  {
    if (stack == null)
    {
      if (paramElement != dtd.html)
      {
        startTag(makeTag(dtd.html, true));
        return legalElementContext(paramElement);
      }
      return true;
    }
    if (stack.advance(paramElement))
    {
      markFirstTime(paramElement);
      return true;
    }
    int i = 0;
    String str1 = stack.elem.getName();
    String str2 = paramElement.getName();
    if ((!strict) && (((str1.equals("table")) && (str2.equals("td"))) || ((str1.equals("table")) && (str2.equals("th"))) || ((str1.equals("tr")) && (!str2.equals("tr"))))) {
      i = 1;
    }
    if ((!strict) && (i == 0) && ((stack.elem.getName() != paramElement.getName()) || (paramElement.getName().equals("body"))) && ((skipTag = ignoreElement(paramElement))))
    {
      error("tag.ignore", paramElement.getName());
      return skipTag;
    }
    Object localObject2;
    if ((!strict) && (str1.equals("table")) && (!str2.equals("tr")) && (!str2.equals("td")) && (!str2.equals("th")) && (!str2.equals("caption")))
    {
      localObject1 = dtd.getElement("tr");
      localObject2 = makeTag((Element)localObject1, true);
      legalTagContext((TagElement)localObject2);
      startTag((TagElement)localObject2);
      error("start.missing", paramElement.getName());
      return legalElementContext(paramElement);
    }
    if ((i == 0) && (stack.terminate()) && ((!strict) || (stack.elem.omitEnd()))) {
      for (localObject1 = stack.next; localObject1 != null; localObject1 = next)
      {
        if (((TagStack)localObject1).advance(paramElement))
        {
          while (stack != localObject1) {
            endTag(true);
          }
          return true;
        }
        if ((!((TagStack)localObject1).terminate()) || ((strict) && (!elem.omitEnd()))) {
          break;
        }
      }
    }
    Object localObject1 = stack.first();
    if ((localObject1 != null) && ((!strict) || (((Element)localObject1).omitStart())) && ((localObject1 != dtd.head) || (paramElement != dtd.pcdata)))
    {
      localObject2 = makeTag((Element)localObject1, true);
      legalTagContext((TagElement)localObject2);
      startTag((TagElement)localObject2);
      if (!((Element)localObject1).omitStart()) {
        error("start.missing", paramElement.getName());
      }
      return legalElementContext(paramElement);
    }
    if (!strict)
    {
      localObject2 = stack.contentModel();
      Vector localVector = new Vector();
      if (localObject2 != null)
      {
        ((ContentModel)localObject2).getElements(localVector);
        Iterator localIterator = localVector.iterator();
        while (localIterator.hasNext())
        {
          Element localElement = (Element)localIterator.next();
          if (!stack.excluded(localElement.getIndex()))
          {
            int j = 0;
            for (Object localObject3 = localElement.getAttributes(); localObject3 != null; localObject3 = next) {
              if (modifier == 2)
              {
                j = 1;
                break;
              }
            }
            if (j == 0)
            {
              localObject3 = localElement.getContent();
              if ((localObject3 != null) && (((ContentModel)localObject3).first(paramElement)))
              {
                TagElement localTagElement = makeTag(localElement, true);
                legalTagContext(localTagElement);
                startTag(localTagElement);
                error("start.missing", localElement.getName());
                return legalElementContext(paramElement);
              }
            }
          }
        }
      }
    }
    if ((stack.terminate()) && (stack.elem != dtd.body) && ((!strict) || (stack.elem.omitEnd())))
    {
      if (!stack.elem.omitEnd()) {
        error("end.missing", paramElement.getName());
      }
      endTag(true);
      return legalElementContext(paramElement);
    }
    return false;
  }
  
  void legalTagContext(TagElement paramTagElement)
    throws ChangedCharSetException
  {
    if (legalElementContext(paramTagElement.getElement()))
    {
      markFirstTime(paramTagElement.getElement());
      return;
    }
    if ((paramTagElement.breaksFlow()) && (stack != null) && (!stack.tag.breaksFlow()))
    {
      endTag(true);
      legalTagContext(paramTagElement);
      return;
    }
    for (TagStack localTagStack = stack; localTagStack != null; localTagStack = next) {
      if (tag.getElement() == dtd.head)
      {
        while (stack != localTagStack) {
          endTag(true);
        }
        endTag(true);
        legalTagContext(paramTagElement);
        return;
      }
    }
    error("tag.unexpected", paramTagElement.getElement().getName());
  }
  
  void errorContext()
    throws ChangedCharSetException
  {
    while ((stack != null) && (stack.tag.getElement() != dtd.body))
    {
      handleEndTag(stack.tag);
      stack = stack.next;
    }
    if (stack == null)
    {
      legalElementContext(dtd.body);
      startTag(makeTag(dtd.body, true));
    }
  }
  
  void addString(int paramInt)
  {
    if (strpos == str.length)
    {
      char[] arrayOfChar = new char[str.length + 128];
      System.arraycopy(str, 0, arrayOfChar, 0, str.length);
      str = arrayOfChar;
    }
    str[(strpos++)] = ((char)paramInt);
  }
  
  String getString(int paramInt)
  {
    char[] arrayOfChar = new char[strpos - paramInt];
    System.arraycopy(str, paramInt, arrayOfChar, 0, strpos - paramInt);
    strpos = paramInt;
    return new String(arrayOfChar);
  }
  
  char[] getChars(int paramInt)
  {
    char[] arrayOfChar = new char[strpos - paramInt];
    System.arraycopy(str, paramInt, arrayOfChar, 0, strpos - paramInt);
    strpos = paramInt;
    return arrayOfChar;
  }
  
  char[] getChars(int paramInt1, int paramInt2)
  {
    char[] arrayOfChar = new char[paramInt2 - paramInt1];
    System.arraycopy(str, paramInt1, arrayOfChar, 0, paramInt2 - paramInt1);
    return arrayOfChar;
  }
  
  void resetStrBuffer()
  {
    strpos = 0;
  }
  
  int strIndexOf(char paramChar)
  {
    for (int i = 0; i < strpos; i++) {
      if (str[i] == paramChar) {
        return i;
      }
    }
    return -1;
  }
  
  void skipSpace()
    throws IOException
  {
    for (;;)
    {
      switch (ch)
      {
      case 10: 
        ln += 1;
        ch = readCh();
        lfCount += 1;
        break;
      case 13: 
        ln += 1;
        if ((ch = readCh()) == 10)
        {
          ch = readCh();
          crlfCount += 1;
        }
        else
        {
          crCount += 1;
        }
        break;
      case 9: 
      case 32: 
        ch = readCh();
      }
    }
  }
  
  boolean parseIdentifier(boolean paramBoolean)
    throws IOException
  {
    switch (ch)
    {
    case 65: 
    case 66: 
    case 67: 
    case 68: 
    case 69: 
    case 70: 
    case 71: 
    case 72: 
    case 73: 
    case 74: 
    case 75: 
    case 76: 
    case 77: 
    case 78: 
    case 79: 
    case 80: 
    case 81: 
    case 82: 
    case 83: 
    case 84: 
    case 85: 
    case 86: 
    case 87: 
    case 88: 
    case 89: 
    case 90: 
      if (paramBoolean) {
        ch = (97 + (ch - 65));
      }
    case 97: 
    case 98: 
    case 99: 
    case 100: 
    case 101: 
    case 102: 
    case 103: 
    case 104: 
    case 105: 
    case 106: 
    case 107: 
    case 108: 
    case 109: 
    case 110: 
    case 111: 
    case 112: 
    case 113: 
    case 114: 
    case 115: 
    case 116: 
    case 117: 
    case 118: 
    case 119: 
    case 120: 
    case 121: 
    case 122: 
      break;
    }
    return false;
    for (;;)
    {
      addString(ch);
      switch (ch = readCh())
      {
      case 65: 
      case 66: 
      case 67: 
      case 68: 
      case 69: 
      case 70: 
      case 71: 
      case 72: 
      case 73: 
      case 74: 
      case 75: 
      case 76: 
      case 77: 
      case 78: 
      case 79: 
      case 80: 
      case 81: 
      case 82: 
      case 83: 
      case 84: 
      case 85: 
      case 86: 
      case 87: 
      case 88: 
      case 89: 
      case 90: 
        if (paramBoolean) {
          ch = (97 + (ch - 65));
        }
        break;
      }
    }
    return true;
  }
  
  private char[] parseEntityReference()
    throws IOException
  {
    int i = strpos;
    if ((ch = readCh()) == 35)
    {
      int j = 0;
      ch = readCh();
      if (((ch >= 48) && (ch <= 57)) || (ch == 120) || (ch == 88))
      {
        if ((ch >= 48) && (ch <= 57)) {}
        while ((ch >= 48) && (ch <= 57))
        {
          j = j * 10 + ch - 48;
          ch = readCh();
          continue;
          ch = readCh();
          for (int m = (char)Character.toLowerCase(ch); ((m >= 48) && (m <= 57)) || ((m >= 97) && (m <= 102)); m = (char)Character.toLowerCase(ch))
          {
            if ((m >= 48) && (m <= 57)) {
              j = j * 16 + m - 48;
            } else {
              j = j * 16 + m - 97 + 10;
            }
            ch = readCh();
          }
        }
        switch (ch)
        {
        case 10: 
          ln += 1;
          ch = readCh();
          lfCount += 1;
          break;
        case 13: 
          ln += 1;
          if ((ch = readCh()) == 10)
          {
            ch = readCh();
            crlfCount += 1;
          }
          else
          {
            crCount += 1;
          }
          break;
        case 59: 
          ch = readCh();
        }
        localObject = mapNumericReference(j);
        return (char[])localObject;
      }
      addString(35);
      if (!parseIdentifier(false))
      {
        error("ident.expected");
        strpos = i;
        localObject = new char[] { '&', '#' };
        return (char[])localObject;
      }
    }
    else if (!parseIdentifier(false))
    {
      char[] arrayOfChar1 = { '&' };
      return arrayOfChar1;
    }
    int k = 0;
    switch (ch)
    {
    case 10: 
      ln += 1;
      ch = readCh();
      lfCount += 1;
      break;
    case 13: 
      ln += 1;
      if ((ch = readCh()) == 10)
      {
        ch = readCh();
        crlfCount += 1;
      }
      else
      {
        crCount += 1;
      }
      break;
    case 59: 
      k = 1;
      ch = readCh();
    }
    Object localObject = getString(i);
    Entity localEntity = dtd.getEntity((String)localObject);
    if ((!strict) && (localEntity == null)) {
      localEntity = dtd.getEntity(((String)localObject).toLowerCase());
    }
    if ((localEntity == null) || (!localEntity.isGeneral()))
    {
      if (((String)localObject).length() == 0)
      {
        error("invalid.entref", (String)localObject);
        return new char[0];
      }
      String str1 = "&" + (String)localObject + (k != 0 ? ";" : "");
      char[] arrayOfChar2 = new char[str1.length()];
      str1.getChars(0, arrayOfChar2.length, arrayOfChar2, 0);
      return arrayOfChar2;
    }
    return localEntity.getData();
  }
  
  private char[] mapNumericReference(int paramInt)
  {
    char[] arrayOfChar;
    if (paramInt >= 65535)
    {
      try
      {
        arrayOfChar = Character.toChars(paramInt);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        arrayOfChar = new char[0];
      }
    }
    else
    {
      arrayOfChar = new char[1];
      arrayOfChar[0] = ((paramInt < 130) || (paramInt > 159) ? (char)paramInt : cp1252Map[(paramInt - 130)]);
    }
    return arrayOfChar;
  }
  
  void parseComment()
    throws IOException
  {
    for (;;)
    {
      int i = ch;
      switch (i)
      {
      case 45: 
        if ((!strict) && (strpos != 0) && (str[(strpos - 1)] == '-'))
        {
          if ((ch = readCh()) == 62) {
            return;
          }
          if (ch == 33)
          {
            if ((ch = readCh()) == 62) {
              return;
            }
            addString(45);
            addString(33);
          }
        }
        else if ((ch = readCh()) == 45)
        {
          ch = readCh();
          if ((strict) || (ch == 62)) {
            return;
          }
          if (ch == 33)
          {
            if ((ch = readCh()) == 62) {
              return;
            }
            addString(45);
            addString(33);
            continue;
          }
          addString(45);
        }
        break;
      case -1: 
        handleEOFInComment();
        return;
      case 10: 
        ln += 1;
        ch = readCh();
        lfCount += 1;
        break;
      case 62: 
        ch = readCh();
        break;
      case 13: 
        ln += 1;
        if ((ch = readCh()) == 10)
        {
          ch = readCh();
          crlfCount += 1;
        }
        else
        {
          crCount += 1;
        }
        i = 10;
        break;
      default: 
        ch = readCh();
        addString(i);
      }
    }
  }
  
  void parseLiteral(boolean paramBoolean)
    throws IOException
  {
    for (;;)
    {
      int i = ch;
      switch (i)
      {
      case -1: 
        error("eof.literal", stack.elem.getName());
        endTag(true);
        return;
      case 62: 
        ch = readCh();
        int j = textpos - (stack.elem.name.length() + 2);
        int k = 0;
        if ((j >= 0) && (text[(j++)] == '<') && (text[j] == '/'))
        {
          do
          {
            j++;
          } while ((j < textpos) && (Character.toLowerCase(text[j]) == stack.elem.name.charAt(k++)));
          if (j == textpos)
          {
            textpos -= stack.elem.name.length() + 2;
            if ((textpos > 0) && (text[(textpos - 1)] == '\n')) {
              textpos -= 1;
            }
            endTag(false);
            return;
          }
        }
      case 38: 
        char[] arrayOfChar2 = parseEntityReference();
        if (textpos + arrayOfChar2.length > text.length)
        {
          char[] arrayOfChar3 = new char[Math.max(textpos + arrayOfChar2.length + 128, text.length * 2)];
          System.arraycopy(text, 0, arrayOfChar3, 0, text.length);
          text = arrayOfChar3;
        }
        System.arraycopy(arrayOfChar2, 0, text, textpos, arrayOfChar2.length);
        textpos += arrayOfChar2.length;
        break;
      case 10: 
        ln += 1;
        ch = readCh();
        lfCount += 1;
        break;
      case 13: 
        ln += 1;
        if ((ch = readCh()) == 10)
        {
          ch = readCh();
          crlfCount += 1;
        }
        else
        {
          crCount += 1;
        }
        i = 10;
        break;
      default: 
        ch = readCh();
        if (textpos == text.length)
        {
          char[] arrayOfChar1 = new char[text.length + 128];
          System.arraycopy(text, 0, arrayOfChar1, 0, text.length);
          text = arrayOfChar1;
        }
        text[(textpos++)] = ((char)i);
      }
    }
  }
  
  String parseAttributeValue(boolean paramBoolean)
    throws IOException
  {
    int i = -1;
    switch (ch)
    {
    case 34: 
    case 39: 
      i = ch;
      ch = readCh();
    }
    for (;;)
    {
      int j = ch;
      switch (j)
      {
      case 10: 
        ln += 1;
        ch = readCh();
        lfCount += 1;
        if (i < 0) {
          return getString(0);
        }
      case 13: 
        ln += 1;
        if ((ch = readCh()) == 10)
        {
          ch = readCh();
          crlfCount += 1;
        }
        else
        {
          crCount += 1;
        }
        if (i < 0) {
          return getString(0);
        }
      case 9: 
        if (i < 0) {
          j = 32;
        }
      case 32: 
        ch = readCh();
        if (i < 0) {
          return getString(0);
        }
      case 60: 
      case 62: 
        if (i < 0) {
          return getString(0);
        }
        ch = readCh();
        break;
      case 34: 
      case 39: 
        ch = readCh();
        if (j == i) {
          return getString(0);
        }
        if (i == -1)
        {
          error("attvalerr");
          if ((!strict) && (ch != 32)) {
            continue;
          }
          return getString(0);
        }
      case 61: 
        if (i < 0)
        {
          error("attvalerr");
          if (strict) {
            return getString(0);
          }
        }
        ch = readCh();
        break;
      case 38: 
        if ((strict) && (i < 0))
        {
          ch = readCh();
        }
        else
        {
          char[] arrayOfChar = parseEntityReference();
          for (int k = 0; k < arrayOfChar.length; k++)
          {
            j = arrayOfChar[k];
            addString((paramBoolean) && (j >= 65) && (j <= 90) ? 97 + j - 65 : j);
          }
        }
        break;
      case -1: 
        return getString(0);
      default: 
        if ((paramBoolean) && (j >= 65) && (j <= 90)) {
          j = 97 + j - 65;
        }
        ch = readCh();
        addString(j);
      }
    }
  }
  
  void parseAttributeSpecificationList(Element paramElement)
    throws IOException
  {
    for (;;)
    {
      skipSpace();
      switch (ch)
      {
      case -1: 
      case 47: 
      case 60: 
      case 62: 
        return;
      case 45: 
        if ((ch = readCh()) == 45)
        {
          ch = readCh();
          parseComment();
          strpos = 0;
        }
        else
        {
          error("invalid.tagchar", "-", paramElement.getName());
          ch = readCh();
        }
        break;
      default: 
        String str1;
        AttributeList localAttributeList;
        String str2;
        if (parseIdentifier(true))
        {
          str1 = getString(0);
          skipSpace();
          if (ch == 61)
          {
            ch = readCh();
            skipSpace();
            localAttributeList = paramElement.getAttribute(str1);
            str2 = parseAttributeValue((localAttributeList != null) && (type != 1) && (type != 11) && (type != 7));
          }
          else
          {
            str2 = str1;
            localAttributeList = paramElement.getAttributeByValue(str2);
            if (localAttributeList == null)
            {
              localAttributeList = paramElement.getAttribute(str1);
              if (localAttributeList != null) {
                str2 = localAttributeList.getValue();
              } else {
                str2 = null;
              }
            }
          }
        }
        else
        {
          if ((!strict) && (ch == 44))
          {
            ch = readCh();
            continue;
          }
          if ((!strict) && (ch == 34))
          {
            ch = readCh();
            skipSpace();
            if (parseIdentifier(true))
            {
              str1 = getString(0);
              if (ch == 34) {
                ch = readCh();
              }
              skipSpace();
              if (ch == 61)
              {
                ch = readCh();
                skipSpace();
                localAttributeList = paramElement.getAttribute(str1);
                str2 = parseAttributeValue((localAttributeList != null) && (type != 1) && (type != 11));
              }
              else
              {
                str2 = str1;
                localAttributeList = paramElement.getAttributeByValue(str2);
                if (localAttributeList == null)
                {
                  localAttributeList = paramElement.getAttribute(str1);
                  if (localAttributeList != null) {
                    str2 = localAttributeList.getValue();
                  }
                }
              }
            }
            else
            {
              localObject = new char[] { (char)ch };
              error("invalid.tagchar", new String((char[])localObject), paramElement.getName());
              ch = readCh();
            }
          }
          else if ((!strict) && (attributes.isEmpty()) && (ch == 61))
          {
            ch = readCh();
            skipSpace();
            str1 = paramElement.getName();
            localAttributeList = paramElement.getAttribute(str1);
            str2 = parseAttributeValue((localAttributeList != null) && (type != 1) && (type != 11));
          }
          else
          {
            if ((!strict) && (ch == 61))
            {
              ch = readCh();
              skipSpace();
              str2 = parseAttributeValue(true);
              error("attvalerr");
              return;
            }
            localObject = new char[] { (char)ch };
            error("invalid.tagchar", new String((char[])localObject), paramElement.getName());
            if (!strict)
            {
              ch = readCh();
              continue;
            }
            return;
          }
        }
        if (localAttributeList != null) {
          str1 = localAttributeList.getName();
        } else {
          error("invalid.tagatt", str1, paramElement.getName());
        }
        if (attributes.isDefined(str1)) {
          error("multi.tagatt", str1, paramElement.getName());
        }
        if (str2 == null) {
          str2 = (localAttributeList != null) && (value != null) ? value : "#DEFAULT";
        } else if ((localAttributeList != null) && (values != null) && (!values.contains(str2))) {
          error("invalid.tagattval", str1, paramElement.getName());
        }
        Object localObject = HTML.getAttributeKey(str1);
        if (localObject == null) {
          attributes.addAttribute(str1, str2);
        } else {
          attributes.addAttribute(localObject, str2);
        }
        break;
      }
    }
  }
  
  public String parseDTDMarkup()
    throws IOException
  {
    StringBuilder localStringBuilder = new StringBuilder();
    ch = readCh();
    for (;;)
    {
      switch (ch)
      {
      case 62: 
        ch = readCh();
        return localStringBuilder.toString();
      case -1: 
        error("invalid.markup");
        return localStringBuilder.toString();
      case 10: 
        ln += 1;
        ch = readCh();
        lfCount += 1;
        break;
      case 34: 
        ch = readCh();
        break;
      case 13: 
        ln += 1;
        if ((ch = readCh()) == 10)
        {
          ch = readCh();
          crlfCount += 1;
        }
        else
        {
          crCount += 1;
        }
        break;
      default: 
        localStringBuilder.append((char)(ch & 0xFF));
        ch = readCh();
      }
    }
  }
  
  protected boolean parseMarkupDeclarations(StringBuffer paramStringBuffer)
    throws IOException
  {
    if ((paramStringBuffer.length() == "DOCTYPE".length()) && (paramStringBuffer.toString().toUpperCase().equals("DOCTYPE")))
    {
      parseDTDMarkup();
      return true;
    }
    return false;
  }
  
  void parseInvalidTag()
    throws IOException
  {
    for (;;)
    {
      skipSpace();
      switch (ch)
      {
      case -1: 
      case 62: 
        ch = readCh();
        return;
      case 60: 
        return;
      }
      ch = readCh();
    }
  }
  
  void parseTag()
    throws IOException
  {
    boolean bool = false;
    int i = 0;
    int j = 0;
    Element localElement;
    switch (ch = readCh())
    {
    case 33: 
      switch (ch = readCh())
      {
      case 45: 
        for (;;)
        {
          if (ch == 45)
          {
            if ((!strict) || ((ch = readCh()) == 45))
            {
              ch = readCh();
              if ((!strict) && (ch == 45)) {
                ch = readCh();
              }
              if (textpos != 0)
              {
                localObject = new char[textpos];
                System.arraycopy(text, 0, localObject, 0, textpos);
                handleText((char[])localObject);
                lastBlockStartPos = currentBlockStartPos;
                textpos = 0;
              }
              parseComment();
              last = makeTag(dtd.getElement("comment"), true);
              handleComment(getChars(0));
              continue;
            }
            if (i == 0)
            {
              i = 1;
              error("invalid.commentchar", "-");
            }
          }
          skipSpace();
          switch (ch)
          {
          case 45: 
            break;
          case 62: 
            ch = readCh();
          case -1: 
            return;
          default: 
            ch = readCh();
            if (i == 0)
            {
              i = 1;
              error("invalid.commentchar", String.valueOf((char)ch));
            }
            break;
          }
        }
      }
      localObject = new StringBuffer();
      for (;;)
      {
        ((StringBuffer)localObject).append((char)ch);
        if (parseMarkupDeclarations((StringBuffer)localObject)) {
          return;
        }
        switch (ch)
        {
        case 62: 
          ch = readCh();
        case -1: 
          error("invalid.markup");
          return;
        case 10: 
          ln += 1;
          ch = readCh();
          lfCount += 1;
          break;
        case 13: 
          ln += 1;
          if ((ch = readCh()) == 10)
          {
            ch = readCh();
            crlfCount += 1;
          }
          else
          {
            crCount += 1;
          }
          break;
        default: 
          ch = readCh();
        }
      }
    case 47: 
      switch (ch = readCh())
      {
      case 62: 
        ch = readCh();
      case 60: 
        if (recent == null)
        {
          error("invalid.shortend");
          return;
        }
        localElement = recent;
        break;
      default: 
        if (!parseIdentifier(true))
        {
          error("expected.endtagname");
          return;
        }
        skipSpace();
        switch (ch)
        {
        case 62: 
          ch = readCh();
        case 60: 
          break;
        default: 
          error("expected", "'>'");
          while ((ch != -1) && (ch != 10) && (ch != 62)) {
            ch = readCh();
          }
          if (ch == 62) {
            ch = readCh();
          }
          break;
        }
        localObject = getString(0);
        if (!dtd.elementExists((String)localObject))
        {
          error("end.unrecognized", (String)localObject);
          if ((textpos > 0) && (text[(textpos - 1)] == '\n')) {
            textpos -= 1;
          }
          localElement = dtd.getElement("unknown");
          name = ((String)localObject);
          j = 1;
        }
        else
        {
          localElement = dtd.getElement((String)localObject);
        }
        break;
      }
      if (stack == null)
      {
        error("end.extra.tag", localElement.getName());
        return;
      }
      if ((textpos > 0) && (text[(textpos - 1)] == '\n')) {
        if (stack.pre)
        {
          if ((textpos > 1) && (text[(textpos - 2)] != '\n')) {
            textpos -= 1;
          }
        }
        else {
          textpos -= 1;
        }
      }
      if (j != 0)
      {
        localObject = makeTag(localElement);
        handleText((TagElement)localObject);
        attributes.addAttribute(HTML.Attribute.ENDTAG, "true");
        handleEmptyTag(makeTag(localElement));
        j = 0;
        return;
      }
      if (!strict)
      {
        localObject = stack.elem.getName();
        if ((((String)localObject).equals("table")) && (!localElement.getName().equals(localObject)))
        {
          error("tag.ignore", localElement.getName());
          return;
        }
        if (((((String)localObject).equals("tr")) || (((String)localObject).equals("td"))) && (!localElement.getName().equals("table")) && (!localElement.getName().equals(localObject)))
        {
          error("tag.ignore", localElement.getName());
          return;
        }
      }
      for (localObject = stack; (localObject != null) && (localElement != elem); localObject = next) {}
      if (localObject == null)
      {
        error("unmatched.endtag", localElement.getName());
        return;
      }
      String str1 = localElement.getName();
      if ((stack != localObject) && ((str1.equals("font")) || (str1.equals("center"))))
      {
        if (str1.equals("center"))
        {
          while ((stack.elem.omitEnd()) && (stack != localObject)) {
            endTag(true);
          }
          if (stack.elem == localElement) {
            endTag(false);
          }
        }
        return;
      }
      while (stack != localObject) {
        endTag(true);
      }
      endTag(false);
      return;
    case -1: 
      error("eof");
      return;
    }
    if (!parseIdentifier(true))
    {
      localElement = recent;
      if ((ch != 62) || (localElement == null)) {
        error("expected.tagname");
      }
    }
    else
    {
      localObject = getString(0);
      if (((String)localObject).equals("image")) {
        localObject = "img";
      }
      if (!dtd.elementExists((String)localObject))
      {
        error("tag.unrecognized ", (String)localObject);
        localElement = dtd.getElement("unknown");
        name = ((String)localObject);
        j = 1;
      }
      else
      {
        localElement = dtd.getElement((String)localObject);
      }
    }
    parseAttributeSpecificationList(localElement);
    switch (ch)
    {
    case 47: 
      bool = true;
    case 62: 
      ch = readCh();
      if ((ch == 62) && (bool)) {
        ch = readCh();
      }
    case 60: 
      break;
    }
    error("expected", "'>'");
    if ((!strict) && (localElement.getName().equals("script"))) {
      error("javascript.unsupported");
    }
    if (!localElement.isEmpty()) {
      if (ch == 10)
      {
        ln += 1;
        lfCount += 1;
        ch = readCh();
      }
      else if (ch == 13)
      {
        ln += 1;
        if ((ch = readCh()) == 10)
        {
          ch = readCh();
          crlfCount += 1;
        }
        else
        {
          crCount += 1;
        }
      }
    }
    Object localObject = makeTag(localElement, false);
    if (j == 0)
    {
      legalTagContext((TagElement)localObject);
      if ((!strict) && (skipTag))
      {
        skipTag = false;
        return;
      }
    }
    startTag((TagElement)localObject);
    if (!localElement.isEmpty()) {
      switch (localElement.getType())
      {
      case 1: 
        parseLiteral(false);
        break;
      case 16: 
        parseLiteral(true);
        break;
      default: 
        if (stack != null) {
          stack.net = bool;
        }
        break;
      }
    }
  }
  
  void parseScript()
    throws IOException
  {
    char[] arrayOfChar = new char[SCRIPT_END_TAG.length];
    int i = 0;
    for (;;)
    {
      for (int j = 0; (i == 0) && (j < SCRIPT_END_TAG.length) && ((SCRIPT_END_TAG[j] == ch) || (SCRIPT_END_TAG_UPPER_CASE[j] == ch)); j++)
      {
        arrayOfChar[j] = ((char)ch);
        ch = readCh();
      }
      if (j == SCRIPT_END_TAG.length) {
        return;
      }
      if ((i == 0) && (j == 1) && (arrayOfChar[0] == "<!--".charAt(0)))
      {
        while ((j < "<!--".length()) && ("<!--".charAt(j) == ch))
        {
          arrayOfChar[j] = ((char)ch);
          ch = readCh();
          j++;
        }
        if (j == "<!--".length()) {
          i = 1;
        }
      }
      if (i != 0)
      {
        while ((j < "-->".length()) && ("-->".charAt(j) == ch))
        {
          arrayOfChar[j] = ((char)ch);
          ch = readCh();
          j++;
        }
        if (j == "-->".length()) {
          i = 0;
        }
      }
      for (int k = 0; k < j; k++) {
        addString(arrayOfChar[k]);
      }
      switch (ch)
      {
      case -1: 
        error("eof.script");
        return;
      case 10: 
        ln += 1;
        ch = readCh();
        lfCount += 1;
        addString(10);
        break;
      case 13: 
        ln += 1;
        if ((ch = readCh()) == 10)
        {
          ch = readCh();
          crlfCount += 1;
        }
        else
        {
          crCount += 1;
        }
        addString(10);
        break;
      default: 
        addString(ch);
        ch = readCh();
      }
    }
  }
  
  void parseContent()
    throws IOException
  {
    Thread localThread = Thread.currentThread();
    for (;;)
    {
      if (localThread.isInterrupted())
      {
        localThread.interrupt();
        break;
      }
      int i = ch;
      currentBlockStartPos = currentPosition;
      Object localObject;
      if (recent == dtd.script)
      {
        parseScript();
        last = makeTag(dtd.getElement("comment"), true);
        localObject = new String(getChars(0)).trim();
        int j = "<!--".length() + "-->".length();
        if ((((String)localObject).startsWith("<!--")) && (((String)localObject).endsWith("-->")) && (((String)localObject).length() >= j)) {
          localObject = ((String)localObject).substring("<!--".length(), ((String)localObject).length() - "-->".length());
        }
        handleComment(((String)localObject).toCharArray());
        endTag(false);
        lastBlockStartPos = currentPosition;
      }
      else
      {
        switch (i)
        {
        case 60: 
          parseTag();
          lastBlockStartPos = currentPosition;
          break;
        case 47: 
          ch = readCh();
          if ((stack != null) && (stack.net))
          {
            endTag(false);
          }
          else if (textpos == 0)
          {
            if (!legalElementContext(dtd.pcdata)) {
              error("unexpected.pcdata");
            }
            if (last.breaksFlow()) {
              space = false;
            }
          }
          break;
        case -1: 
          return;
        case 38: 
          if (textpos == 0)
          {
            if (!legalElementContext(dtd.pcdata)) {
              error("unexpected.pcdata");
            }
            if (last.breaksFlow()) {
              space = false;
            }
          }
          localObject = parseEntityReference();
          if (textpos + localObject.length + 1 > text.length)
          {
            char[] arrayOfChar = new char[Math.max(textpos + localObject.length + 128, text.length * 2)];
            System.arraycopy(text, 0, arrayOfChar, 0, text.length);
            text = arrayOfChar;
          }
          if (space)
          {
            space = false;
            text[(textpos++)] = ' ';
          }
          System.arraycopy(localObject, 0, text, textpos, localObject.length);
          textpos += localObject.length;
          ignoreSpace = false;
          break;
        case 10: 
          ln += 1;
          lfCount += 1;
          ch = readCh();
          if ((stack == null) || (!stack.pre))
          {
            if (textpos == 0) {
              lastBlockStartPos = currentPosition;
            }
            if (ignoreSpace) {
              continue;
            }
            space = true;
          }
          break;
        case 13: 
          ln += 1;
          i = 10;
          if ((ch = readCh()) == 10)
          {
            ch = readCh();
            crlfCount += 1;
          }
          else
          {
            crCount += 1;
          }
          if ((stack == null) || (!stack.pre))
          {
            if (textpos == 0) {
              lastBlockStartPos = currentPosition;
            }
            if (ignoreSpace) {
              continue;
            }
            space = true;
          }
          break;
        case 9: 
        case 32: 
          ch = readCh();
          if ((stack == null) || (!stack.pre))
          {
            if (textpos == 0) {
              lastBlockStartPos = currentPosition;
            }
            if (ignoreSpace) {
              continue;
            }
            space = true;
          }
          break;
        default: 
          if (textpos == 0)
          {
            if (!legalElementContext(dtd.pcdata)) {
              error("unexpected.pcdata");
            }
            if (last.breaksFlow()) {
              space = false;
            }
          }
          ch = readCh();
          if (textpos + 2 > text.length)
          {
            localObject = new char[text.length + 128];
            System.arraycopy(text, 0, localObject, 0, text.length);
            text = ((char[])localObject);
          }
          if (space)
          {
            if (textpos == 0) {
              lastBlockStartPos -= 1;
            }
            text[(textpos++)] = ' ';
            space = false;
          }
          text[(textpos++)] = ((char)i);
          ignoreSpace = false;
        }
      }
    }
  }
  
  String getEndOfLineString()
  {
    if (crlfCount >= crCount)
    {
      if (lfCount >= crlfCount) {
        return "\n";
      }
      return "\r\n";
    }
    if (crCount > lfCount) {
      return "\r";
    }
    return "\n";
  }
  
  public synchronized void parse(Reader paramReader)
    throws IOException
  {
    in = paramReader;
    ln = 1;
    seenHtml = false;
    seenHead = false;
    seenBody = false;
    crCount = (lfCount = crlfCount = 0);
    try
    {
      ch = readCh();
      text = new char['Ѐ'];
      str = new char[''];
      parseContent();
      while (stack != null) {
        endTag(true);
      }
      paramReader.close();
    }
    catch (IOException localIOException)
    {
      errorContext();
      error("ioexception");
      throw localIOException;
    }
    catch (Exception localException)
    {
      errorContext();
      error("exception", localException.getClass().getName(), localException.getMessage());
      localException.printStackTrace();
    }
    catch (ThreadDeath localThreadDeath)
    {
      errorContext();
      error("terminated");
      localThreadDeath.printStackTrace();
      throw localThreadDeath;
    }
    finally
    {
      while (stack != null)
      {
        handleEndTag(stack.tag);
        stack = stack.next;
      }
      text = null;
      str = null;
    }
  }
  
  private final int readCh()
    throws IOException
  {
    if (pos >= len)
    {
      try
      {
        len = in.read(buf);
      }
      catch (InterruptedIOException localInterruptedIOException)
      {
        throw localInterruptedIOException;
      }
      if (len <= 0) {
        return -1;
      }
      pos = 0;
    }
    currentPosition += 1;
    return buf[(pos++)];
  }
  
  protected int getCurrentPos()
  {
    return currentPosition;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\parser\Parser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */