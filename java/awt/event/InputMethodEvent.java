package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.font.TextHitInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.AttributedCharacterIterator;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.EventQueueAccessor;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

public class InputMethodEvent
  extends AWTEvent
{
  private static final long serialVersionUID = 4727190874778922661L;
  public static final int INPUT_METHOD_FIRST = 1100;
  public static final int INPUT_METHOD_TEXT_CHANGED = 1100;
  public static final int CARET_POSITION_CHANGED = 1101;
  public static final int INPUT_METHOD_LAST = 1101;
  long when;
  private transient AttributedCharacterIterator text;
  private transient int committedCharacterCount;
  private transient TextHitInfo caret;
  private transient TextHitInfo visiblePosition;
  
  public InputMethodEvent(Component paramComponent, int paramInt1, long paramLong, AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt2, TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2)
  {
    super(paramComponent, paramInt1);
    if ((paramInt1 < 1100) || (paramInt1 > 1101)) {
      throw new IllegalArgumentException("id outside of valid range");
    }
    if ((paramInt1 == 1101) && (paramAttributedCharacterIterator != null)) {
      throw new IllegalArgumentException("text must be null for CARET_POSITION_CHANGED");
    }
    when = paramLong;
    text = paramAttributedCharacterIterator;
    int i = 0;
    if (paramAttributedCharacterIterator != null) {
      i = paramAttributedCharacterIterator.getEndIndex() - paramAttributedCharacterIterator.getBeginIndex();
    }
    if ((paramInt2 < 0) || (paramInt2 > i)) {
      throw new IllegalArgumentException("committedCharacterCount outside of valid range");
    }
    committedCharacterCount = paramInt2;
    caret = paramTextHitInfo1;
    visiblePosition = paramTextHitInfo2;
  }
  
  public InputMethodEvent(Component paramComponent, int paramInt1, AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt2, TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2)
  {
    this(paramComponent, paramInt1, getMostRecentEventTimeForSource(paramComponent), paramAttributedCharacterIterator, paramInt2, paramTextHitInfo1, paramTextHitInfo2);
  }
  
  public InputMethodEvent(Component paramComponent, int paramInt, TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2)
  {
    this(paramComponent, paramInt, getMostRecentEventTimeForSource(paramComponent), null, 0, paramTextHitInfo1, paramTextHitInfo2);
  }
  
  public AttributedCharacterIterator getText()
  {
    return text;
  }
  
  public int getCommittedCharacterCount()
  {
    return committedCharacterCount;
  }
  
  public TextHitInfo getCaret()
  {
    return caret;
  }
  
  public TextHitInfo getVisiblePosition()
  {
    return visiblePosition;
  }
  
  public void consume()
  {
    consumed = true;
  }
  
  public boolean isConsumed()
  {
    return consumed;
  }
  
  public long getWhen()
  {
    return when;
  }
  
  public String paramString()
  {
    String str1;
    switch (id)
    {
    case 1100: 
      str1 = "INPUT_METHOD_TEXT_CHANGED";
      break;
    case 1101: 
      str1 = "CARET_POSITION_CHANGED";
      break;
    default: 
      str1 = "unknown type";
    }
    String str2;
    if (text == null)
    {
      str2 = "no text";
    }
    else
    {
      localObject = new StringBuilder("\"");
      int i = committedCharacterCount;
      for (int j = text.first(); i-- > 0; j = text.next()) {
        ((StringBuilder)localObject).append(j);
      }
      ((StringBuilder)localObject).append("\" + \"");
      while (j != 65535)
      {
        ((StringBuilder)localObject).append(j);
        int k = text.next();
      }
      ((StringBuilder)localObject).append("\"");
      str2 = ((StringBuilder)localObject).toString();
    }
    Object localObject = committedCharacterCount + " characters committed";
    String str3;
    if (caret == null) {
      str3 = "no caret";
    } else {
      str3 = "caret: " + caret.toString();
    }
    String str4;
    if (visiblePosition == null) {
      str4 = "no visible position";
    } else {
      str4 = "visible position: " + visiblePosition.toString();
    }
    return str1 + ", " + str2 + ", " + (String)localObject + ", " + str3 + ", " + str4;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    if (when == 0L) {
      when = EventQueue.getMostRecentEventTime();
    }
  }
  
  private static long getMostRecentEventTimeForSource(Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("null source");
    }
    AppContext localAppContext = SunToolkit.targetToAppContext(paramObject);
    EventQueue localEventQueue = SunToolkit.getSystemEventQueueImplPP(localAppContext);
    return AWTAccessor.getEventQueueAccessor().getMostRecentEventTime(localEventQueue);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\InputMethodEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */