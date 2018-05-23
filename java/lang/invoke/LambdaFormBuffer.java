package java.lang.invoke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

final class LambdaFormBuffer
{
  private int arity;
  private int length;
  private LambdaForm.Name[] names;
  private LambdaForm.Name[] originalNames;
  private byte flags;
  private int firstChange;
  private LambdaForm.Name resultName;
  private String debugName;
  private ArrayList<LambdaForm.Name> dups;
  private static final int F_TRANS = 16;
  private static final int F_OWNED = 3;
  
  LambdaFormBuffer(LambdaForm paramLambdaForm)
  {
    arity = arity;
    setNames(names);
    int i = result;
    if (i == -2) {
      i = length - 1;
    }
    if ((i >= 0) && (names[i].type != LambdaForm.BasicType.V_TYPE)) {
      resultName = names[i];
    }
    debugName = debugName;
    assert (paramLambdaForm.nameRefsAreLegal());
  }
  
  private LambdaForm lambdaForm()
  {
    assert (!inTrans());
    return new LambdaForm(debugName, arity, nameArray(), resultIndex());
  }
  
  LambdaForm.Name name(int paramInt)
  {
    assert (paramInt < length);
    return names[paramInt];
  }
  
  LambdaForm.Name[] nameArray()
  {
    return (LambdaForm.Name[])Arrays.copyOf(names, length);
  }
  
  int resultIndex()
  {
    if (resultName == null) {
      return -1;
    }
    int i = indexOf(resultName, names);
    assert (i >= 0);
    return i;
  }
  
  void setNames(LambdaForm.Name[] paramArrayOfName)
  {
    names = (originalNames = paramArrayOfName);
    length = paramArrayOfName.length;
    flags = 0;
  }
  
  private boolean verifyArity()
  {
    for (int i = 0; (i < arity) && (i < firstChange); i++) {
      assert (names[i].isParam()) : ("#" + i + "=" + names[i]);
    }
    for (i = arity; i < length; i++) {
      assert (!names[i].isParam()) : ("#" + i + "=" + names[i]);
    }
    for (i = length; i < names.length; i++) {
      assert (names[i] == null) : ("#" + i + "=" + names[i]);
    }
    if (resultName != null)
    {
      i = indexOf(resultName, names);
      assert (i >= 0) : ("not found: " + resultName.exprString() + Arrays.asList(names));
      assert (names[i] == resultName);
    }
    return true;
  }
  
  private boolean verifyFirstChange()
  {
    assert (inTrans());
    for (int i = 0; i < length; i++) {
      if (names[i] != originalNames[i])
      {
        if ((!$assertionsDisabled) && (firstChange != i)) {
          throw new AssertionError(Arrays.asList(new Object[] { Integer.valueOf(firstChange), Integer.valueOf(i), originalNames[i].exprString(), Arrays.asList(names) }));
        }
        return true;
      }
    }
    if ((!$assertionsDisabled) && (firstChange != length)) {
      throw new AssertionError(Arrays.asList(new Object[] { Integer.valueOf(firstChange), Arrays.asList(names) }));
    }
    return true;
  }
  
  private static int indexOf(LambdaForm.NamedFunction paramNamedFunction, LambdaForm.NamedFunction[] paramArrayOfNamedFunction)
  {
    for (int i = 0; i < paramArrayOfNamedFunction.length; i++) {
      if (paramArrayOfNamedFunction[i] == paramNamedFunction) {
        return i;
      }
    }
    return -1;
  }
  
  private static int indexOf(LambdaForm.Name paramName, LambdaForm.Name[] paramArrayOfName)
  {
    for (int i = 0; i < paramArrayOfName.length; i++) {
      if (paramArrayOfName[i] == paramName) {
        return i;
      }
    }
    return -1;
  }
  
  boolean inTrans()
  {
    return (flags & 0x10) != 0;
  }
  
  int ownedCount()
  {
    return flags & 0x3;
  }
  
  void growNames(int paramInt1, int paramInt2)
  {
    int i = length;
    int j = i + paramInt2;
    int k = ownedCount();
    if ((k == 0) || (j > names.length))
    {
      names = ((LambdaForm.Name[])Arrays.copyOf(names, (names.length + paramInt2) * 5 / 4));
      if (k == 0)
      {
        flags = ((byte)(flags + 1));
        k++;
        assert (ownedCount() == k);
      }
    }
    if ((originalNames != null) && (originalNames.length < names.length))
    {
      originalNames = ((LambdaForm.Name[])Arrays.copyOf(originalNames, names.length));
      if (k == 1)
      {
        flags = ((byte)(flags + 1));
        k++;
        assert (ownedCount() == k);
      }
    }
    if (paramInt2 == 0) {
      return;
    }
    int m = paramInt1 + paramInt2;
    int n = i - paramInt1;
    System.arraycopy(names, paramInt1, names, m, n);
    Arrays.fill(names, paramInt1, m, null);
    if (originalNames != null)
    {
      System.arraycopy(originalNames, paramInt1, originalNames, m, n);
      Arrays.fill(originalNames, paramInt1, m, null);
    }
    length = j;
    if (firstChange >= paramInt1) {
      firstChange += paramInt2;
    }
  }
  
  int lastIndexOf(LambdaForm.Name paramName)
  {
    int i = -1;
    for (int j = 0; j < length; j++) {
      if (names[j] == paramName) {
        i = j;
      }
    }
    return i;
  }
  
  private void noteDuplicate(int paramInt1, int paramInt2)
  {
    LambdaForm.Name localName = names[paramInt1];
    assert (localName == names[paramInt2]);
    assert (originalNames[paramInt1] != null);
    assert ((originalNames[paramInt2] == null) || (originalNames[paramInt2] == localName));
    if (dups == null) {
      dups = new ArrayList();
    }
    dups.add(localName);
  }
  
  private void clearDuplicatesAndNulls()
  {
    if (dups != null)
    {
      assert (ownedCount() >= 1);
      Iterator localIterator = dups.iterator();
      while (localIterator.hasNext())
      {
        LambdaForm.Name localName = (LambdaForm.Name)localIterator.next();
        for (int k = firstChange; k < length; k++) {
          if ((names[k] == localName) && (originalNames[k] != localName))
          {
            names[k] = null;
            if (($assertionsDisabled) || (Arrays.asList(names).contains(localName))) {
              break;
            }
            throw new AssertionError();
          }
        }
      }
      dups.clear();
    }
    int i = length;
    for (int j = firstChange; j < length; j++) {
      if (names[j] == null)
      {
        System.arraycopy(names, j + 1, names, j, --length - j);
        j--;
      }
    }
    if (length < i) {
      Arrays.fill(names, length, i, null);
    }
    assert (!Arrays.asList(names).subList(0, length).contains(null));
  }
  
  void startEdit()
  {
    assert (verifyArity());
    int i = ownedCount();
    assert (!inTrans());
    flags = ((byte)(flags | 0x10));
    LambdaForm.Name[] arrayOfName1 = names;
    LambdaForm.Name[] arrayOfName2 = i == 2 ? originalNames : null;
    assert (arrayOfName2 != arrayOfName1);
    if ((arrayOfName2 != null) && (arrayOfName2.length >= length))
    {
      names = copyNamesInto(arrayOfName2);
    }
    else
    {
      names = ((LambdaForm.Name[])Arrays.copyOf(arrayOfName1, Math.max(length + 2, arrayOfName1.length)));
      if (i < 2) {
        flags = ((byte)(flags + 1));
      }
      assert (ownedCount() == i + 1);
    }
    originalNames = arrayOfName1;
    assert (originalNames != names);
    firstChange = length;
    assert (inTrans());
  }
  
  private void changeName(int paramInt, LambdaForm.Name paramName)
  {
    assert (inTrans());
    assert (paramInt < length);
    LambdaForm.Name localName = names[paramInt];
    assert (localName == originalNames[paramInt]);
    assert (verifyFirstChange());
    if (ownedCount() == 0) {
      growNames(0, 0);
    }
    names[paramInt] = paramName;
    if (firstChange > paramInt) {
      firstChange = paramInt;
    }
    if ((resultName != null) && (resultName == localName)) {
      resultName = paramName;
    }
  }
  
  void setResult(LambdaForm.Name paramName)
  {
    assert ((paramName == null) || (lastIndexOf(paramName) >= 0));
    resultName = paramName;
  }
  
  LambdaForm endEdit()
  {
    assert (verifyFirstChange());
    for (int i = Math.max(firstChange, arity); i < length; i++)
    {
      LambdaForm.Name localName1 = names[i];
      if (localName1 != null)
      {
        LambdaForm.Name localName2 = localName1.replaceNames(originalNames, names, firstChange, i);
        if (localName2 != localName1)
        {
          names[i] = localName2;
          if (resultName == localName1) {
            resultName = localName2;
          }
        }
      }
    }
    assert (inTrans());
    flags = ((byte)(flags & 0xFFFFFFEF));
    clearDuplicatesAndNulls();
    originalNames = null;
    if (firstChange < arity)
    {
      LambdaForm.Name[] arrayOfName = new LambdaForm.Name[arity - firstChange];
      int j = firstChange;
      int k = 0;
      for (int m = firstChange; m < arity; m++)
      {
        LambdaForm.Name localName3 = names[m];
        if (localName3.isParam()) {
          names[(j++)] = localName3;
        } else {
          arrayOfName[(k++)] = localName3;
        }
      }
      assert (k == arity - j);
      System.arraycopy(arrayOfName, 0, names, j, k);
      arity -= k;
    }
    assert (verifyArity());
    return lambdaForm();
  }
  
  private LambdaForm.Name[] copyNamesInto(LambdaForm.Name[] paramArrayOfName)
  {
    System.arraycopy(names, 0, paramArrayOfName, 0, length);
    Arrays.fill(paramArrayOfName, length, paramArrayOfName.length, null);
    return paramArrayOfName;
  }
  
  LambdaFormBuffer replaceFunctions(LambdaForm.NamedFunction[] paramArrayOfNamedFunction1, LambdaForm.NamedFunction[] paramArrayOfNamedFunction2, Object... paramVarArgs)
  {
    assert (inTrans());
    if (paramArrayOfNamedFunction1.length == 0) {
      return this;
    }
    for (int i = arity; i < length; i++)
    {
      LambdaForm.Name localName = names[i];
      int j = indexOf(function, paramArrayOfNamedFunction1);
      if ((j >= 0) && (Arrays.equals(arguments, paramVarArgs))) {
        changeName(i, new LambdaForm.Name(paramArrayOfNamedFunction2[j], arguments));
      }
    }
    return this;
  }
  
  private void replaceName(int paramInt, LambdaForm.Name paramName)
  {
    assert (inTrans());
    assert (verifyArity());
    assert (paramInt < arity);
    LambdaForm.Name localName = names[paramInt];
    assert (localName.isParam());
    assert (type == type);
    changeName(paramInt, paramName);
  }
  
  LambdaFormBuffer renameParameter(int paramInt, LambdaForm.Name paramName)
  {
    assert (paramName.isParam());
    replaceName(paramInt, paramName);
    return this;
  }
  
  LambdaFormBuffer replaceParameterByNewExpression(int paramInt, LambdaForm.Name paramName)
  {
    assert (!paramName.isParam());
    assert (lastIndexOf(paramName) < 0);
    replaceName(paramInt, paramName);
    return this;
  }
  
  LambdaFormBuffer replaceParameterByCopy(int paramInt1, int paramInt2)
  {
    assert (paramInt1 != paramInt2);
    replaceName(paramInt1, names[paramInt2]);
    noteDuplicate(paramInt1, paramInt2);
    return this;
  }
  
  private void insertName(int paramInt, LambdaForm.Name paramName, boolean paramBoolean)
  {
    assert (inTrans());
    assert (verifyArity());
    assert (paramBoolean ? paramInt <= arity : paramInt >= arity);
    growNames(paramInt, 1);
    if (paramBoolean) {
      arity += 1;
    }
    changeName(paramInt, paramName);
  }
  
  LambdaFormBuffer insertExpression(int paramInt, LambdaForm.Name paramName)
  {
    assert (!paramName.isParam());
    insertName(paramInt, paramName, false);
    return this;
  }
  
  LambdaFormBuffer insertParameter(int paramInt, LambdaForm.Name paramName)
  {
    assert (paramName.isParam());
    insertName(paramInt, paramName, true);
    return this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\LambdaFormBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */