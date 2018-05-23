package sun.util.locale.provider;

import java.io.IOException;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class DictionaryBasedBreakIterator
  extends RuleBasedBreakIterator
{
  private BreakDictionary dictionary;
  private boolean[] categoryFlags;
  private int dictionaryCharCount;
  private int[] cachedBreakPositions;
  private int positionInCache;
  
  DictionaryBasedBreakIterator(String paramString1, String paramString2)
    throws IOException
  {
    super(paramString1);
    byte[] arrayOfByte = super.getAdditionalData();
    if (arrayOfByte != null)
    {
      prepareCategoryFlags(arrayOfByte);
      super.setAdditionalData(null);
    }
    dictionary = new BreakDictionary(paramString2);
  }
  
  private void prepareCategoryFlags(byte[] paramArrayOfByte)
  {
    categoryFlags = new boolean[paramArrayOfByte.length];
    for (int i = 0; i < paramArrayOfByte.length; i++) {
      categoryFlags[i] = (paramArrayOfByte[i] == 1 ? 1 : false);
    }
  }
  
  public void setText(CharacterIterator paramCharacterIterator)
  {
    super.setText(paramCharacterIterator);
    cachedBreakPositions = null;
    dictionaryCharCount = 0;
    positionInCache = 0;
  }
  
  public int first()
  {
    cachedBreakPositions = null;
    dictionaryCharCount = 0;
    positionInCache = 0;
    return super.first();
  }
  
  public int last()
  {
    cachedBreakPositions = null;
    dictionaryCharCount = 0;
    positionInCache = 0;
    return super.last();
  }
  
  public int previous()
  {
    CharacterIterator localCharacterIterator = getText();
    if ((cachedBreakPositions != null) && (positionInCache > 0))
    {
      positionInCache -= 1;
      localCharacterIterator.setIndex(cachedBreakPositions[positionInCache]);
      return cachedBreakPositions[positionInCache];
    }
    cachedBreakPositions = null;
    int i = super.previous();
    if (cachedBreakPositions != null) {
      positionInCache = (cachedBreakPositions.length - 2);
    }
    return i;
  }
  
  public int preceding(int paramInt)
  {
    CharacterIterator localCharacterIterator = getText();
    checkOffset(paramInt, localCharacterIterator);
    if ((cachedBreakPositions == null) || (paramInt <= cachedBreakPositions[0]) || (paramInt > cachedBreakPositions[(cachedBreakPositions.length - 1)]))
    {
      cachedBreakPositions = null;
      return super.preceding(paramInt);
    }
    for (positionInCache = 0; (positionInCache < cachedBreakPositions.length) && (paramInt > cachedBreakPositions[positionInCache]); positionInCache += 1) {}
    positionInCache -= 1;
    localCharacterIterator.setIndex(cachedBreakPositions[positionInCache]);
    return localCharacterIterator.getIndex();
  }
  
  public int following(int paramInt)
  {
    CharacterIterator localCharacterIterator = getText();
    checkOffset(paramInt, localCharacterIterator);
    if ((cachedBreakPositions == null) || (paramInt < cachedBreakPositions[0]) || (paramInt >= cachedBreakPositions[(cachedBreakPositions.length - 1)]))
    {
      cachedBreakPositions = null;
      return super.following(paramInt);
    }
    for (positionInCache = 0; (positionInCache < cachedBreakPositions.length) && (paramInt >= cachedBreakPositions[positionInCache]); positionInCache += 1) {}
    localCharacterIterator.setIndex(cachedBreakPositions[positionInCache]);
    return localCharacterIterator.getIndex();
  }
  
  protected int handleNext()
  {
    CharacterIterator localCharacterIterator = getText();
    if ((cachedBreakPositions == null) || (positionInCache == cachedBreakPositions.length - 1))
    {
      int i = localCharacterIterator.getIndex();
      dictionaryCharCount = 0;
      int j = super.handleNext();
      if ((dictionaryCharCount > 1) && (j - i > 1))
      {
        divideUpDictionaryRange(i, j);
      }
      else
      {
        cachedBreakPositions = null;
        return j;
      }
    }
    if (cachedBreakPositions != null)
    {
      positionInCache += 1;
      localCharacterIterator.setIndex(cachedBreakPositions[positionInCache]);
      return cachedBreakPositions[positionInCache];
    }
    return 55537;
  }
  
  protected int lookupCategory(int paramInt)
  {
    int i = super.lookupCategory(paramInt);
    if ((i != -1) && (categoryFlags[i] != 0)) {
      dictionaryCharCount += 1;
    }
    return i;
  }
  
  private void divideUpDictionaryRange(int paramInt1, int paramInt2)
  {
    CharacterIterator localCharacterIterator = getText();
    localCharacterIterator.setIndex(paramInt1);
    int i = getCurrent();
    for (int j = lookupCategory(i); (j == -1) || (categoryFlags[j] == 0); j = lookupCategory(i)) {
      i = getNext();
    }
    Object localObject1 = new Stack();
    Stack localStack = new Stack();
    ArrayList localArrayList = new ArrayList();
    int k = 0;
    int m = localCharacterIterator.getIndex();
    Object localObject2 = null;
    i = getCurrent();
    for (;;)
    {
      if (dictionary.getNextState(k, 0) == -1) {
        localStack.push(Integer.valueOf(localCharacterIterator.getIndex()));
      }
      k = dictionary.getNextStateFromCharacter(k, i);
      if (k == -1)
      {
        ((Stack)localObject1).push(Integer.valueOf(localCharacterIterator.getIndex()));
        break;
      }
      if ((k == 0) || (localCharacterIterator.getIndex() >= paramInt2))
      {
        Object localObject3;
        if (localCharacterIterator.getIndex() > m)
        {
          m = localCharacterIterator.getIndex();
          localObject3 = (Stack)((Stack)localObject1).clone();
          localObject2 = localObject3;
        }
        while ((!localStack.isEmpty()) && (localArrayList.contains(localStack.peek()))) {
          localStack.pop();
        }
        if (localStack.isEmpty())
        {
          if (localObject2 != null)
          {
            localObject1 = localObject2;
            if (m >= paramInt2) {
              break;
            }
            localCharacterIterator.setIndex(m + 1);
          }
          else
          {
            if (((((Stack)localObject1).size() == 0) || (((Integer)((Stack)localObject1).peek()).intValue() != localCharacterIterator.getIndex())) && (localCharacterIterator.getIndex() != paramInt1)) {
              ((Stack)localObject1).push(new Integer(localCharacterIterator.getIndex()));
            }
            getNext();
            ((Stack)localObject1).push(new Integer(localCharacterIterator.getIndex()));
          }
        }
        else
        {
          localObject3 = (Integer)localStack.pop();
          Integer localInteger = null;
          while ((!((Stack)localObject1).isEmpty()) && (((Integer)localObject3).intValue() < ((Integer)((Stack)localObject1).peek()).intValue()))
          {
            localInteger = (Integer)((Stack)localObject1).pop();
            localArrayList.add(localInteger);
          }
          ((Stack)localObject1).push(localObject3);
          localCharacterIterator.setIndex(((Integer)((Stack)localObject1).peek()).intValue());
        }
        i = getCurrent();
        if (localCharacterIterator.getIndex() >= paramInt2) {
          break;
        }
      }
      else
      {
        i = getNext();
      }
    }
    if (!((Stack)localObject1).isEmpty()) {
      ((Stack)localObject1).pop();
    }
    ((Stack)localObject1).push(Integer.valueOf(paramInt2));
    cachedBreakPositions = new int[((Stack)localObject1).size() + 1];
    cachedBreakPositions[0] = paramInt1;
    for (int n = 0; n < ((Stack)localObject1).size(); n++) {
      cachedBreakPositions[(n + 1)] = ((Integer)((Stack)localObject1).elementAt(n)).intValue();
    }
    positionInCache = 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\DictionaryBasedBreakIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */