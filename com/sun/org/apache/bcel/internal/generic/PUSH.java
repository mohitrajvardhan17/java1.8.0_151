package com.sun.org.apache.bcel.internal.generic;

public final class PUSH
  implements CompoundInstruction, VariableLengthInstruction, InstructionConstants
{
  private Instruction instruction;
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, int paramInt)
  {
    if ((paramInt >= -1) && (paramInt <= 5)) {
      instruction = INSTRUCTIONS[(3 + paramInt)];
    } else if ((paramInt >= -128) && (paramInt <= 127)) {
      instruction = new BIPUSH((byte)paramInt);
    } else if ((paramInt >= 32768) && (paramInt <= 32767)) {
      instruction = new SIPUSH((short)paramInt);
    } else {
      instruction = new LDC(paramConstantPoolGen.addInteger(paramInt));
    }
  }
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, boolean paramBoolean)
  {
    instruction = INSTRUCTIONS[(3 + 0)];
  }
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, float paramFloat)
  {
    if (paramFloat == 0.0D) {
      instruction = FCONST_0;
    } else if (paramFloat == 1.0D) {
      instruction = FCONST_1;
    } else if (paramFloat == 2.0D) {
      instruction = FCONST_2;
    } else {
      instruction = new LDC(paramConstantPoolGen.addFloat(paramFloat));
    }
  }
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, long paramLong)
  {
    if (paramLong == 0L) {
      instruction = LCONST_0;
    } else if (paramLong == 1L) {
      instruction = LCONST_1;
    } else {
      instruction = new LDC2_W(paramConstantPoolGen.addLong(paramLong));
    }
  }
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, double paramDouble)
  {
    if (paramDouble == 0.0D) {
      instruction = DCONST_0;
    } else if (paramDouble == 1.0D) {
      instruction = DCONST_1;
    } else {
      instruction = new LDC2_W(paramConstantPoolGen.addDouble(paramDouble));
    }
  }
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, String paramString)
  {
    if (paramString == null) {
      instruction = ACONST_NULL;
    } else {
      instruction = new LDC(paramConstantPoolGen.addString(paramString));
    }
  }
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, Number paramNumber)
  {
    if (((paramNumber instanceof Integer)) || ((paramNumber instanceof Short)) || ((paramNumber instanceof Byte))) {
      instruction = PUSHintValueinstruction;
    } else if ((paramNumber instanceof Double)) {
      instruction = PUSHdoubleValueinstruction;
    } else if ((paramNumber instanceof Float)) {
      instruction = PUSHfloatValueinstruction;
    } else if ((paramNumber instanceof Long)) {
      instruction = PUSHlongValueinstruction;
    } else {
      throw new ClassGenException("What's this: " + paramNumber);
    }
  }
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, Character paramCharacter)
  {
    this(paramConstantPoolGen, paramCharacter.charValue());
  }
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, Boolean paramBoolean)
  {
    this(paramConstantPoolGen, paramBoolean.booleanValue());
  }
  
  public final InstructionList getInstructionList()
  {
    return new InstructionList(instruction);
  }
  
  public final Instruction getInstruction()
  {
    return instruction;
  }
  
  public String toString()
  {
    return instruction.toString() + " (PUSH)";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\PUSH.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */