package com.sun.jmx.snmp.IPAcl;

import java.io.IOException;

class ParserTokenManager
  implements ParserConstants
{
  static final long[] jjbitVec0 = { 0L, 0L, -1L, -1L };
  static final int[] jjnextStates = { 18, 19, 21, 28, 29, 39, 23, 24, 26, 27, 41, 42, 7, 8, 10, 18, 20, 21, 44, 46, 13, 1, 2, 4, 37, 28, 38, 26, 27, 37, 28, 38, 15, 16 };
  public static final String[] jjstrLiteralImages = { "", null, null, null, null, null, null, "access", "acl", "=", "communities", "enterprise", "hosts", "{", "managers", "-", "}", "read-only", "read-write", "trap", "inform", "trap-community", "inform-community", "trap-num", null, null, null, null, null, null, null, null, null, null, null, null, ",", ".", "!", "/" };
  public static final String[] lexStateNames = { "DEFAULT" };
  static final long[] jjtoToken = { 1067601362817L };
  static final long[] jjtoSkip = { 126L };
  private ASCII_CharStream input_stream;
  private final int[] jjrounds = new int[47];
  private final int[] jjstateSet = new int[94];
  protected char curChar;
  int curLexState = 0;
  int defaultLexState = 0;
  int jjnewStateCnt;
  int jjround;
  int jjmatchedPos;
  int jjmatchedKind;
  
  private final int jjStopStringLiteralDfa_0(int paramInt, long paramLong)
  {
    switch (paramInt)
    {
    case 0: 
      if ((paramLong & 0x8000) != 0L) {
        return 0;
      }
      if ((paramLong & 0xFE5000) != 0L)
      {
        jjmatchedKind = 31;
        return 47;
      }
      if ((paramLong & 0xD80) != 0L)
      {
        jjmatchedKind = 31;
        return 48;
      }
      return -1;
    case 1: 
      if ((paramLong & 0xFE5C00) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 1;
        return 49;
      }
      if ((paramLong & 0x180) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 1;
        return 50;
      }
      return -1;
    case 2: 
      if ((paramLong & 0xFE5C00) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 2;
        return 49;
      }
      if ((paramLong & 0x100) != 0L) {
        return 49;
      }
      if ((paramLong & 0x80) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 2;
        return 50;
      }
      return -1;
    case 3: 
      if ((paramLong & 0x565C00) != 0L)
      {
        if (jjmatchedPos != 3)
        {
          jjmatchedKind = 31;
          jjmatchedPos = 3;
        }
        return 49;
      }
      if ((paramLong & 0xA80000) != 0L) {
        return 49;
      }
      if ((paramLong & 0x80) != 0L)
      {
        if (jjmatchedPos != 3)
        {
          jjmatchedKind = 31;
          jjmatchedPos = 3;
        }
        return 50;
      }
      return -1;
    case 4: 
      if ((paramLong & 0xA00000) != 0L) {
        return 51;
      }
      if ((paramLong & 0x60000) != 0L)
      {
        if (jjmatchedPos < 3)
        {
          jjmatchedKind = 31;
          jjmatchedPos = 3;
        }
        return 51;
      }
      if ((paramLong & 0x1000) != 0L) {
        return 49;
      }
      if ((paramLong & 0x504C80) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 4;
        return 49;
      }
      return -1;
    case 5: 
      if ((paramLong & 0x500080) != 0L) {
        return 49;
      }
      if ((paramLong & 0x4C00) != 0L)
      {
        if (jjmatchedPos != 5)
        {
          jjmatchedKind = 31;
          jjmatchedPos = 5;
        }
        return 49;
      }
      if ((paramLong & 0xA60000) != 0L)
      {
        if (jjmatchedPos != 5)
        {
          jjmatchedKind = 31;
          jjmatchedPos = 5;
        }
        return 51;
      }
      return -1;
    case 6: 
      if ((paramLong & 0x400000) != 0L) {
        return 51;
      }
      if ((paramLong & 0x4C00) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 6;
        return 49;
      }
      if ((paramLong & 0xA60000) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 6;
        return 51;
      }
      return -1;
    case 7: 
      if ((paramLong & 0x660000) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 7;
        return 51;
      }
      if ((paramLong & 0x800000) != 0L) {
        return 51;
      }
      if ((paramLong & 0x4000) != 0L) {
        return 49;
      }
      if ((paramLong & 0xC00) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 7;
        return 49;
      }
      return -1;
    case 8: 
      if ((paramLong & 0x20000) != 0L) {
        return 51;
      }
      if ((paramLong & 0xC00) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 8;
        return 49;
      }
      if ((paramLong & 0x640000) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 8;
        return 51;
      }
      return -1;
    case 9: 
      if ((paramLong & 0x40000) != 0L) {
        return 51;
      }
      if ((paramLong & 0x800) != 0L) {
        return 49;
      }
      if ((paramLong & 0x600000) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 9;
        return 51;
      }
      if ((paramLong & 0x400) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 9;
        return 49;
      }
      return -1;
    case 10: 
      if ((paramLong & 0x600000) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 10;
        return 51;
      }
      if ((paramLong & 0x400) != 0L) {
        return 49;
      }
      return -1;
    case 11: 
      if ((paramLong & 0x600000) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 11;
        return 51;
      }
      return -1;
    case 12: 
      if ((paramLong & 0x600000) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 12;
        return 51;
      }
      return -1;
    case 13: 
      if ((paramLong & 0x400000) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 13;
        return 51;
      }
      if ((paramLong & 0x200000) != 0L) {
        return 51;
      }
      return -1;
    case 14: 
      if ((paramLong & 0x400000) != 0L)
      {
        jjmatchedKind = 31;
        jjmatchedPos = 14;
        return 51;
      }
      return -1;
    }
    return -1;
  }
  
  private final int jjStartNfa_0(int paramInt, long paramLong)
  {
    return jjMoveNfa_0(jjStopStringLiteralDfa_0(paramInt, paramLong), paramInt + 1);
  }
  
  private final int jjStopAtPos(int paramInt1, int paramInt2)
  {
    jjmatchedKind = paramInt2;
    jjmatchedPos = paramInt1;
    return paramInt1 + 1;
  }
  
  private final int jjStartNfaWithStates_0(int paramInt1, int paramInt2, int paramInt3)
  {
    jjmatchedKind = paramInt2;
    jjmatchedPos = paramInt1;
    try
    {
      curChar = input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      return paramInt1 + 1;
    }
    return jjMoveNfa_0(paramInt3, paramInt1 + 1);
  }
  
  private final int jjMoveStringLiteralDfa0_0()
  {
    switch (curChar)
    {
    case '!': 
      return jjStopAtPos(0, 38);
    case ',': 
      return jjStopAtPos(0, 36);
    case '-': 
      return jjStartNfaWithStates_0(0, 15, 0);
    case '.': 
      return jjStopAtPos(0, 37);
    case '/': 
      return jjStopAtPos(0, 39);
    case '=': 
      return jjStopAtPos(0, 9);
    case 'a': 
      return jjMoveStringLiteralDfa1_0(384L);
    case 'c': 
      return jjMoveStringLiteralDfa1_0(1024L);
    case 'e': 
      return jjMoveStringLiteralDfa1_0(2048L);
    case 'h': 
      return jjMoveStringLiteralDfa1_0(4096L);
    case 'i': 
      return jjMoveStringLiteralDfa1_0(5242880L);
    case 'm': 
      return jjMoveStringLiteralDfa1_0(16384L);
    case 'r': 
      return jjMoveStringLiteralDfa1_0(393216L);
    case 't': 
      return jjMoveStringLiteralDfa1_0(11010048L);
    case '{': 
      return jjStopAtPos(0, 13);
    case '}': 
      return jjStopAtPos(0, 16);
    }
    return jjMoveNfa_0(5, 0);
  }
  
  private final int jjMoveStringLiteralDfa1_0(long paramLong)
  {
    try
    {
      curChar = input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(0, paramLong);
      return 1;
    }
    switch (curChar)
    {
    case 'a': 
      return jjMoveStringLiteralDfa2_0(paramLong, 16384L);
    case 'c': 
      return jjMoveStringLiteralDfa2_0(paramLong, 384L);
    case 'e': 
      return jjMoveStringLiteralDfa2_0(paramLong, 393216L);
    case 'n': 
      return jjMoveStringLiteralDfa2_0(paramLong, 5244928L);
    case 'o': 
      return jjMoveStringLiteralDfa2_0(paramLong, 5120L);
    case 'r': 
      return jjMoveStringLiteralDfa2_0(paramLong, 11010048L);
    }
    return jjStartNfa_0(0, paramLong);
  }
  
  private final int jjMoveStringLiteralDfa2_0(long paramLong1, long paramLong2)
  {
    if ((paramLong2 &= paramLong1) == 0L) {
      return jjStartNfa_0(0, paramLong1);
    }
    try
    {
      curChar = input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(1, paramLong2);
      return 2;
    }
    switch (curChar)
    {
    case 'a': 
      return jjMoveStringLiteralDfa3_0(paramLong2, 11403264L);
    case 'c': 
      return jjMoveStringLiteralDfa3_0(paramLong2, 128L);
    case 'f': 
      return jjMoveStringLiteralDfa3_0(paramLong2, 5242880L);
    case 'l': 
      if ((paramLong2 & 0x100) != 0L) {
        return jjStartNfaWithStates_0(2, 8, 49);
      }
      break;
    case 'm': 
      return jjMoveStringLiteralDfa3_0(paramLong2, 1024L);
    case 'n': 
      return jjMoveStringLiteralDfa3_0(paramLong2, 16384L);
    case 's': 
      return jjMoveStringLiteralDfa3_0(paramLong2, 4096L);
    case 't': 
      return jjMoveStringLiteralDfa3_0(paramLong2, 2048L);
    }
    return jjStartNfa_0(1, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa3_0(long paramLong1, long paramLong2)
  {
    if ((paramLong2 &= paramLong1) == 0L) {
      return jjStartNfa_0(1, paramLong1);
    }
    try
    {
      curChar = input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(2, paramLong2);
      return 3;
    }
    switch (curChar)
    {
    case 'a': 
      return jjMoveStringLiteralDfa4_0(paramLong2, 16384L);
    case 'd': 
      return jjMoveStringLiteralDfa4_0(paramLong2, 393216L);
    case 'e': 
      return jjMoveStringLiteralDfa4_0(paramLong2, 2176L);
    case 'm': 
      return jjMoveStringLiteralDfa4_0(paramLong2, 1024L);
    case 'o': 
      return jjMoveStringLiteralDfa4_0(paramLong2, 5242880L);
    case 'p': 
      if ((paramLong2 & 0x80000) != 0L)
      {
        jjmatchedKind = 19;
        jjmatchedPos = 3;
      }
      return jjMoveStringLiteralDfa4_0(paramLong2, 10485760L);
    case 't': 
      return jjMoveStringLiteralDfa4_0(paramLong2, 4096L);
    }
    return jjStartNfa_0(2, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa4_0(long paramLong1, long paramLong2)
  {
    if ((paramLong2 &= paramLong1) == 0L) {
      return jjStartNfa_0(2, paramLong1);
    }
    try
    {
      curChar = input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(3, paramLong2);
      return 4;
    }
    switch (curChar)
    {
    case '-': 
      return jjMoveStringLiteralDfa5_0(paramLong2, 10878976L);
    case 'g': 
      return jjMoveStringLiteralDfa5_0(paramLong2, 16384L);
    case 'r': 
      return jjMoveStringLiteralDfa5_0(paramLong2, 5244928L);
    case 's': 
      if ((paramLong2 & 0x1000) != 0L) {
        return jjStartNfaWithStates_0(4, 12, 49);
      }
      return jjMoveStringLiteralDfa5_0(paramLong2, 128L);
    case 'u': 
      return jjMoveStringLiteralDfa5_0(paramLong2, 1024L);
    }
    return jjStartNfa_0(3, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa5_0(long paramLong1, long paramLong2)
  {
    if ((paramLong2 &= paramLong1) == 0L) {
      return jjStartNfa_0(3, paramLong1);
    }
    try
    {
      curChar = input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(4, paramLong2);
      return 5;
    }
    switch (curChar)
    {
    case 'c': 
      return jjMoveStringLiteralDfa6_0(paramLong2, 2097152L);
    case 'e': 
      return jjMoveStringLiteralDfa6_0(paramLong2, 16384L);
    case 'm': 
      if ((paramLong2 & 0x100000) != 0L)
      {
        jjmatchedKind = 20;
        jjmatchedPos = 5;
      }
      return jjMoveStringLiteralDfa6_0(paramLong2, 4194304L);
    case 'n': 
      return jjMoveStringLiteralDfa6_0(paramLong2, 8389632L);
    case 'o': 
      return jjMoveStringLiteralDfa6_0(paramLong2, 131072L);
    case 'p': 
      return jjMoveStringLiteralDfa6_0(paramLong2, 2048L);
    case 's': 
      if ((paramLong2 & 0x80) != 0L) {
        return jjStartNfaWithStates_0(5, 7, 49);
      }
      break;
    case 'w': 
      return jjMoveStringLiteralDfa6_0(paramLong2, 262144L);
    }
    return jjStartNfa_0(4, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa6_0(long paramLong1, long paramLong2)
  {
    if ((paramLong2 &= paramLong1) == 0L) {
      return jjStartNfa_0(4, paramLong1);
    }
    try
    {
      curChar = input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(5, paramLong2);
      return 6;
    }
    switch (curChar)
    {
    case '-': 
      return jjMoveStringLiteralDfa7_0(paramLong2, 4194304L);
    case 'i': 
      return jjMoveStringLiteralDfa7_0(paramLong2, 1024L);
    case 'n': 
      return jjMoveStringLiteralDfa7_0(paramLong2, 131072L);
    case 'o': 
      return jjMoveStringLiteralDfa7_0(paramLong2, 2097152L);
    case 'r': 
      return jjMoveStringLiteralDfa7_0(paramLong2, 280576L);
    case 'u': 
      return jjMoveStringLiteralDfa7_0(paramLong2, 8388608L);
    }
    return jjStartNfa_0(5, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa7_0(long paramLong1, long paramLong2)
  {
    if ((paramLong2 &= paramLong1) == 0L) {
      return jjStartNfa_0(5, paramLong1);
    }
    try
    {
      curChar = input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(6, paramLong2);
      return 7;
    }
    switch (curChar)
    {
    case 'c': 
      return jjMoveStringLiteralDfa8_0(paramLong2, 4194304L);
    case 'i': 
      return jjMoveStringLiteralDfa8_0(paramLong2, 264192L);
    case 'l': 
      return jjMoveStringLiteralDfa8_0(paramLong2, 131072L);
    case 'm': 
      if ((paramLong2 & 0x800000) != 0L) {
        return jjStartNfaWithStates_0(7, 23, 51);
      }
      return jjMoveStringLiteralDfa8_0(paramLong2, 2097152L);
    case 's': 
      if ((paramLong2 & 0x4000) != 0L) {
        return jjStartNfaWithStates_0(7, 14, 49);
      }
      break;
    case 't': 
      return jjMoveStringLiteralDfa8_0(paramLong2, 1024L);
    }
    return jjStartNfa_0(6, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa8_0(long paramLong1, long paramLong2)
  {
    if ((paramLong2 &= paramLong1) == 0L) {
      return jjStartNfa_0(6, paramLong1);
    }
    try
    {
      curChar = input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(7, paramLong2);
      return 8;
    }
    switch (curChar)
    {
    case 'i': 
      return jjMoveStringLiteralDfa9_0(paramLong2, 1024L);
    case 'm': 
      return jjMoveStringLiteralDfa9_0(paramLong2, 2097152L);
    case 'o': 
      return jjMoveStringLiteralDfa9_0(paramLong2, 4194304L);
    case 's': 
      return jjMoveStringLiteralDfa9_0(paramLong2, 2048L);
    case 't': 
      return jjMoveStringLiteralDfa9_0(paramLong2, 262144L);
    case 'y': 
      if ((paramLong2 & 0x20000) != 0L) {
        return jjStartNfaWithStates_0(8, 17, 51);
      }
      break;
    }
    return jjStartNfa_0(7, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa9_0(long paramLong1, long paramLong2)
  {
    if ((paramLong2 &= paramLong1) == 0L) {
      return jjStartNfa_0(7, paramLong1);
    }
    try
    {
      curChar = input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(8, paramLong2);
      return 9;
    }
    switch (curChar)
    {
    case 'e': 
      if ((paramLong2 & 0x800) != 0L) {
        return jjStartNfaWithStates_0(9, 11, 49);
      }
      if ((paramLong2 & 0x40000) != 0L) {
        return jjStartNfaWithStates_0(9, 18, 51);
      }
      return jjMoveStringLiteralDfa10_0(paramLong2, 1024L);
    case 'm': 
      return jjMoveStringLiteralDfa10_0(paramLong2, 4194304L);
    case 'u': 
      return jjMoveStringLiteralDfa10_0(paramLong2, 2097152L);
    }
    return jjStartNfa_0(8, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa10_0(long paramLong1, long paramLong2)
  {
    if ((paramLong2 &= paramLong1) == 0L) {
      return jjStartNfa_0(8, paramLong1);
    }
    try
    {
      curChar = input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(9, paramLong2);
      return 10;
    }
    switch (curChar)
    {
    case 'm': 
      return jjMoveStringLiteralDfa11_0(paramLong2, 4194304L);
    case 'n': 
      return jjMoveStringLiteralDfa11_0(paramLong2, 2097152L);
    case 's': 
      if ((paramLong2 & 0x400) != 0L) {
        return jjStartNfaWithStates_0(10, 10, 49);
      }
      break;
    }
    return jjStartNfa_0(9, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa11_0(long paramLong1, long paramLong2)
  {
    if ((paramLong2 &= paramLong1) == 0L) {
      return jjStartNfa_0(9, paramLong1);
    }
    try
    {
      curChar = input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(10, paramLong2);
      return 11;
    }
    switch (curChar)
    {
    case 'i': 
      return jjMoveStringLiteralDfa12_0(paramLong2, 2097152L);
    case 'u': 
      return jjMoveStringLiteralDfa12_0(paramLong2, 4194304L);
    }
    return jjStartNfa_0(10, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa12_0(long paramLong1, long paramLong2)
  {
    if ((paramLong2 &= paramLong1) == 0L) {
      return jjStartNfa_0(10, paramLong1);
    }
    try
    {
      curChar = input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(11, paramLong2);
      return 12;
    }
    switch (curChar)
    {
    case 'n': 
      return jjMoveStringLiteralDfa13_0(paramLong2, 4194304L);
    case 't': 
      return jjMoveStringLiteralDfa13_0(paramLong2, 2097152L);
    }
    return jjStartNfa_0(11, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa13_0(long paramLong1, long paramLong2)
  {
    if ((paramLong2 &= paramLong1) == 0L) {
      return jjStartNfa_0(11, paramLong1);
    }
    try
    {
      curChar = input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(12, paramLong2);
      return 13;
    }
    switch (curChar)
    {
    case 'i': 
      return jjMoveStringLiteralDfa14_0(paramLong2, 4194304L);
    case 'y': 
      if ((paramLong2 & 0x200000) != 0L) {
        return jjStartNfaWithStates_0(13, 21, 51);
      }
      break;
    }
    return jjStartNfa_0(12, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa14_0(long paramLong1, long paramLong2)
  {
    if ((paramLong2 &= paramLong1) == 0L) {
      return jjStartNfa_0(12, paramLong1);
    }
    try
    {
      curChar = input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(13, paramLong2);
      return 14;
    }
    switch (curChar)
    {
    case 't': 
      return jjMoveStringLiteralDfa15_0(paramLong2, 4194304L);
    }
    return jjStartNfa_0(13, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa15_0(long paramLong1, long paramLong2)
  {
    if ((paramLong2 &= paramLong1) == 0L) {
      return jjStartNfa_0(13, paramLong1);
    }
    try
    {
      curChar = input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(14, paramLong2);
      return 15;
    }
    switch (curChar)
    {
    case 'y': 
      if ((paramLong2 & 0x400000) != 0L) {
        return jjStartNfaWithStates_0(15, 22, 51);
      }
      break;
    }
    return jjStartNfa_0(14, paramLong2);
  }
  
  private final void jjCheckNAdd(int paramInt)
  {
    if (jjrounds[paramInt] != jjround)
    {
      jjstateSet[(jjnewStateCnt++)] = paramInt;
      jjrounds[paramInt] = jjround;
    }
  }
  
  private final void jjAddStates(int paramInt1, int paramInt2)
  {
    do
    {
      jjstateSet[(jjnewStateCnt++)] = jjnextStates[paramInt1];
    } while (paramInt1++ != paramInt2);
  }
  
  private final void jjCheckNAddTwoStates(int paramInt1, int paramInt2)
  {
    jjCheckNAdd(paramInt1);
    jjCheckNAdd(paramInt2);
  }
  
  private final void jjCheckNAddStates(int paramInt1, int paramInt2)
  {
    do
    {
      jjCheckNAdd(jjnextStates[paramInt1]);
    } while (paramInt1++ != paramInt2);
  }
  
  private final void jjCheckNAddStates(int paramInt)
  {
    jjCheckNAdd(jjnextStates[paramInt]);
    jjCheckNAdd(jjnextStates[(paramInt + 1)]);
  }
  
  private final int jjMoveNfa_0(int paramInt1, int paramInt2)
  {
    int i = 0;
    jjnewStateCnt = 47;
    int j = 1;
    jjstateSet[0] = paramInt1;
    int k = Integer.MAX_VALUE;
    for (;;)
    {
      if (++jjround == Integer.MAX_VALUE) {
        ReInitRounds();
      }
      long l1;
      if (curChar < '@')
      {
        l1 = 1L << curChar;
        do
        {
          switch (jjstateSet[(--j)])
          {
          case 49: 
            if ((0x3FF200000000000 & l1) != 0L) {
              jjCheckNAddTwoStates(18, 19);
            }
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAddStates(0, 2);
            }
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(20);
            }
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(19);
            }
            break;
          case 48: 
            if ((0x3FF200000000000 & l1) != 0L) {
              jjCheckNAddTwoStates(18, 19);
            } else if (curChar == ':') {
              jjCheckNAddStates(3, 5);
            }
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAddStates(0, 2);
            }
            else if (curChar == ':')
            {
              jjCheckNAddTwoStates(23, 25);
            }
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(20);
            }
            if ((0x3FF000000000000 & l1) != 0L) {
              jjCheckNAddTwoStates(26, 27);
            }
            if ((0x3FF000000000000 & l1) != 0L) {
              jjCheckNAddTwoStates(23, 24);
            }
            break;
          case 47: 
            if ((0x3FF200000000000 & l1) != 0L) {
              jjCheckNAddTwoStates(18, 19);
            }
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAddStates(0, 2);
            }
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(20);
            }
            break;
          case 50: 
            if ((0x3FF200000000000 & l1) != 0L) {
              jjCheckNAddTwoStates(18, 19);
            } else if (curChar == ':') {
              jjCheckNAddStates(3, 5);
            }
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAddStates(0, 2);
            }
            else if (curChar == ':')
            {
              jjCheckNAddTwoStates(23, 25);
            }
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(20);
            }
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(19);
            }
            if ((0x3FF000000000000 & l1) != 0L) {
              jjCheckNAddTwoStates(26, 27);
            }
            if ((0x3FF000000000000 & l1) != 0L) {
              jjCheckNAddTwoStates(23, 24);
            }
            break;
          case 5: 
            if ((0x3FF000000000000 & l1) != 0L) {
              jjCheckNAddStates(6, 9);
            } else if (curChar == ':') {
              jjAddStates(10, 11);
            } else if (curChar == '"') {
              jjCheckNAddTwoStates(15, 16);
            } else if (curChar == '#') {
              jjCheckNAddStates(12, 14);
            } else if (curChar == '-') {
              jjstateSet[(jjnewStateCnt++)] = 0;
            }
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAddStates(15, 17);
            }
            if ((0x3FE000000000000 & l1) != 0L)
            {
              if (k > 24) {
                k = 24;
              }
              jjCheckNAddTwoStates(12, 13);
            }
            else if (curChar == '0')
            {
              if (k > 24) {
                k = 24;
              }
              jjCheckNAddStates(18, 20);
            }
            break;
          case 51: 
            if ((0x3FF200000000000 & l1) != 0L) {
              jjCheckNAddTwoStates(18, 19);
            }
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(19);
            }
            break;
          case 0: 
            if (curChar == '-') {
              jjCheckNAddStates(21, 23);
            }
            break;
          case 1: 
            if ((0xFFFFFFFFFFFFDBFF & l1) != 0L) {
              jjCheckNAddStates(21, 23);
            }
            break;
          case 2: 
            if (((0x2400 & l1) != 0L) && (k > 5)) {
              k = 5;
            }
            break;
          case 3: 
            if ((curChar == '\n') && (k > 5)) {
              k = 5;
            }
            break;
          case 4: 
            if (curChar == '\r') {
              jjstateSet[(jjnewStateCnt++)] = 3;
            }
            break;
          case 6: 
            if (curChar == '#') {
              jjCheckNAddStates(12, 14);
            }
            break;
          case 7: 
            if ((0xFFFFFFFFFFFFDBFF & l1) != 0L) {
              jjCheckNAddStates(12, 14);
            }
            break;
          case 8: 
            if (((0x2400 & l1) != 0L) && (k > 6)) {
              k = 6;
            }
            break;
          case 9: 
            if ((curChar == '\n') && (k > 6)) {
              k = 6;
            }
            break;
          case 10: 
            if (curChar == '\r') {
              jjstateSet[(jjnewStateCnt++)] = 9;
            }
            break;
          case 11: 
            if ((0x3FE000000000000 & l1) != 0L)
            {
              if (k > 24) {
                k = 24;
              }
              jjCheckNAddTwoStates(12, 13);
            }
            break;
          case 12: 
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 24) {
                k = 24;
              }
              jjCheckNAddTwoStates(12, 13);
            }
            break;
          case 14: 
            if (curChar == '"') {
              jjCheckNAddTwoStates(15, 16);
            }
            break;
          case 15: 
            if ((0xFFFFFFFBFFFFFFFF & l1) != 0L) {
              jjCheckNAddTwoStates(15, 16);
            }
            break;
          case 16: 
            if ((curChar == '"') && (k > 35)) {
              k = 35;
            }
            break;
          case 17: 
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAddStates(15, 17);
            }
            break;
          case 18: 
            if ((0x3FF200000000000 & l1) != 0L) {
              jjCheckNAddTwoStates(18, 19);
            }
            break;
          case 19: 
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(19);
            }
            break;
          case 20: 
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(20);
            }
            break;
          case 21: 
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAddStates(0, 2);
            }
            break;
          case 22: 
            if ((0x3FF000000000000 & l1) != 0L) {
              jjCheckNAddStates(6, 9);
            }
            break;
          case 23: 
            if ((0x3FF000000000000 & l1) != 0L) {
              jjCheckNAddTwoStates(23, 24);
            }
            break;
          case 24: 
            if (curChar == ':') {
              jjCheckNAddTwoStates(23, 25);
            }
            break;
          case 25: 
          case 41: 
            if ((curChar == ':') && (k > 28)) {
              k = 28;
            }
            break;
          case 26: 
            if ((0x3FF000000000000 & l1) != 0L) {
              jjCheckNAddTwoStates(26, 27);
            }
            break;
          case 27: 
            if (curChar == ':') {
              jjCheckNAddStates(3, 5);
            }
            break;
          case 28: 
          case 42: 
            if (curChar == ':') {
              jjCheckNAddTwoStates(29, 36);
            }
            break;
          case 29: 
            if ((0x3FF000000000000 & l1) != 0L) {
              jjCheckNAddTwoStates(29, 30);
            }
            break;
          case 30: 
            if (curChar == '.') {
              jjCheckNAdd(31);
            }
            break;
          case 31: 
            if ((0x3FF000000000000 & l1) != 0L) {
              jjCheckNAddTwoStates(31, 32);
            }
            break;
          case 32: 
            if (curChar == '.') {
              jjCheckNAdd(33);
            }
            break;
          case 33: 
            if ((0x3FF000000000000 & l1) != 0L) {
              jjCheckNAddTwoStates(33, 34);
            }
            break;
          case 34: 
            if (curChar == '.') {
              jjCheckNAdd(35);
            }
            break;
          case 35: 
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 28) {
                k = 28;
              }
              jjCheckNAdd(35);
            }
            break;
          case 36: 
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 28) {
                k = 28;
              }
              jjCheckNAddStates(24, 26);
            }
            break;
          case 37: 
            if ((0x3FF000000000000 & l1) != 0L) {
              jjCheckNAddTwoStates(37, 28);
            }
            break;
          case 38: 
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 28) {
                k = 28;
              }
              jjCheckNAdd(38);
            }
            break;
          case 39: 
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 28) {
                k = 28;
              }
              jjCheckNAddStates(27, 31);
            }
            break;
          case 40: 
            if (curChar == ':') {
              jjAddStates(10, 11);
            }
            break;
          case 43: 
            if (curChar == '0')
            {
              if (k > 24) {
                k = 24;
              }
              jjCheckNAddStates(18, 20);
            }
            break;
          case 45: 
            if ((0x3FF000000000000 & l1) != 0L)
            {
              if (k > 24) {
                k = 24;
              }
              jjCheckNAddTwoStates(45, 13);
            }
            break;
          case 46: 
            if ((0xFF000000000000 & l1) != 0L)
            {
              if (k > 24) {
                k = 24;
              }
              jjCheckNAddTwoStates(46, 13);
            }
            break;
          }
        } while (j != i);
      }
      else if (curChar < '')
      {
        l1 = 1L << (curChar & 0x3F);
        do
        {
          switch (jjstateSet[(--j)])
          {
          case 49: 
            if ((0x7FFFFFE87FFFFFE & l1) != 0L) {
              jjCheckNAddTwoStates(18, 19);
            }
            if ((0x7FFFFFE07FFFFFE & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAddStates(0, 2);
            }
            if ((0x7FFFFFE07FFFFFE & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(20);
            }
            if ((0x7FFFFFE07FFFFFE & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(19);
            }
            break;
          case 48: 
            if ((0x7FFFFFE87FFFFFE & l1) != 0L) {
              jjCheckNAddTwoStates(18, 19);
            }
            if ((0x7FFFFFE07FFFFFE & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAddStates(0, 2);
            }
            if ((0x7FFFFFE07FFFFFE & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(20);
            }
            if ((0x7E0000007E & l1) != 0L) {
              jjCheckNAddTwoStates(26, 27);
            }
            if ((0x7E0000007E & l1) != 0L) {
              jjCheckNAddTwoStates(23, 24);
            }
            break;
          case 47: 
            if ((0x7FFFFFE87FFFFFE & l1) != 0L) {
              jjCheckNAddTwoStates(18, 19);
            }
            if ((0x7FFFFFE07FFFFFE & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAddStates(0, 2);
            }
            if ((0x7FFFFFE07FFFFFE & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(20);
            }
            break;
          case 50: 
            if ((0x7FFFFFE87FFFFFE & l1) != 0L) {
              jjCheckNAddTwoStates(18, 19);
            }
            if ((0x7FFFFFE07FFFFFE & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAddStates(0, 2);
            }
            if ((0x7FFFFFE07FFFFFE & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(20);
            }
            if ((0x7FFFFFE07FFFFFE & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(19);
            }
            if ((0x7E0000007E & l1) != 0L) {
              jjCheckNAddTwoStates(26, 27);
            }
            if ((0x7E0000007E & l1) != 0L) {
              jjCheckNAddTwoStates(23, 24);
            }
            break;
          case 5: 
            if ((0x7FFFFFE07FFFFFE & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAddStates(15, 17);
            }
            if ((0x7E0000007E & l1) != 0L) {
              jjCheckNAddStates(6, 9);
            }
            break;
          case 51: 
            if ((0x7FFFFFE87FFFFFE & l1) != 0L) {
              jjCheckNAddTwoStates(18, 19);
            }
            if ((0x7FFFFFE07FFFFFE & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(19);
            }
            break;
          case 1: 
            jjAddStates(21, 23);
            break;
          case 7: 
            jjAddStates(12, 14);
            break;
          case 13: 
            if (((0x100000001000 & l1) != 0L) && (k > 24)) {
              k = 24;
            }
            break;
          case 15: 
            jjAddStates(32, 33);
            break;
          case 17: 
            if ((0x7FFFFFE07FFFFFE & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAddStates(15, 17);
            }
            break;
          case 18: 
            if ((0x7FFFFFE87FFFFFE & l1) != 0L) {
              jjCheckNAddTwoStates(18, 19);
            }
            break;
          case 19: 
            if ((0x7FFFFFE07FFFFFE & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(19);
            }
            break;
          case 20: 
            if ((0x7FFFFFE07FFFFFE & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAdd(20);
            }
            break;
          case 21: 
            if ((0x7FFFFFE07FFFFFE & l1) != 0L)
            {
              if (k > 31) {
                k = 31;
              }
              jjCheckNAddStates(0, 2);
            }
            break;
          case 22: 
            if ((0x7E0000007E & l1) != 0L) {
              jjCheckNAddStates(6, 9);
            }
            break;
          case 23: 
            if ((0x7E0000007E & l1) != 0L) {
              jjCheckNAddTwoStates(23, 24);
            }
            break;
          case 26: 
            if ((0x7E0000007E & l1) != 0L) {
              jjCheckNAddTwoStates(26, 27);
            }
            break;
          case 36: 
            if ((0x7E0000007E & l1) != 0L)
            {
              if (k > 28) {
                k = 28;
              }
              jjCheckNAddStates(24, 26);
            }
            break;
          case 37: 
            if ((0x7E0000007E & l1) != 0L) {
              jjCheckNAddTwoStates(37, 28);
            }
            break;
          case 38: 
            if ((0x7E0000007E & l1) != 0L)
            {
              if (k > 28) {
                k = 28;
              }
              jjCheckNAdd(38);
            }
            break;
          case 39: 
            if ((0x7E0000007E & l1) != 0L)
            {
              if (k > 28) {
                k = 28;
              }
              jjCheckNAddStates(27, 31);
            }
            break;
          case 44: 
            if ((0x100000001000000 & l1) != 0L) {
              jjCheckNAdd(45);
            }
            break;
          case 45: 
            if ((0x7E0000007E & l1) != 0L)
            {
              if (k > 24) {
                k = 24;
              }
              jjCheckNAddTwoStates(45, 13);
            }
            break;
          }
        } while (j != i);
      }
      else
      {
        int m = (curChar & 0xFF) >> '\006';
        long l2 = 1L << (curChar & 0x3F);
        do
        {
          switch (jjstateSet[(--j)])
          {
          case 1: 
            if ((jjbitVec0[m] & l2) != 0L) {
              jjAddStates(21, 23);
            }
            break;
          case 7: 
            if ((jjbitVec0[m] & l2) != 0L) {
              jjAddStates(12, 14);
            }
            break;
          case 15: 
            if ((jjbitVec0[m] & l2) != 0L) {
              jjAddStates(32, 33);
            }
            break;
          }
        } while (j != i);
      }
      if (k != Integer.MAX_VALUE)
      {
        jjmatchedKind = k;
        jjmatchedPos = paramInt2;
        k = Integer.MAX_VALUE;
      }
      paramInt2++;
      if ((j = jjnewStateCnt) == (i = 47 - (jjnewStateCnt = i))) {
        return paramInt2;
      }
      try
      {
        curChar = input_stream.readChar();
      }
      catch (IOException localIOException) {}
    }
    return paramInt2;
  }
  
  public ParserTokenManager(ASCII_CharStream paramASCII_CharStream)
  {
    input_stream = paramASCII_CharStream;
  }
  
  public ParserTokenManager(ASCII_CharStream paramASCII_CharStream, int paramInt)
  {
    this(paramASCII_CharStream);
    SwitchTo(paramInt);
  }
  
  public void ReInit(ASCII_CharStream paramASCII_CharStream)
  {
    jjmatchedPos = (jjnewStateCnt = 0);
    curLexState = defaultLexState;
    input_stream = paramASCII_CharStream;
    ReInitRounds();
  }
  
  private final void ReInitRounds()
  {
    jjround = -2147483647;
    int i = 47;
    while (i-- > 0) {
      jjrounds[i] = Integer.MIN_VALUE;
    }
  }
  
  public void ReInit(ASCII_CharStream paramASCII_CharStream, int paramInt)
  {
    ReInit(paramASCII_CharStream);
    SwitchTo(paramInt);
  }
  
  public void SwitchTo(int paramInt)
  {
    if ((paramInt >= 1) || (paramInt < 0)) {
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + paramInt + ". State unchanged.", 2);
    }
    curLexState = paramInt;
  }
  
  private final Token jjFillToken()
  {
    Token localToken = Token.newToken(jjmatchedKind);
    kind = jjmatchedKind;
    String str = jjstrLiteralImages[jjmatchedKind];
    image = (str == null ? input_stream.GetImage() : str);
    beginLine = input_stream.getBeginLine();
    beginColumn = input_stream.getBeginColumn();
    endLine = input_stream.getEndLine();
    endColumn = input_stream.getEndColumn();
    return localToken;
  }
  
  public final Token getNextToken()
  {
    Object localObject = null;
    int i = 0;
    do
    {
      for (;;)
      {
        try
        {
          curChar = input_stream.BeginToken();
        }
        catch (IOException localIOException1)
        {
          jjmatchedKind = 0;
          localToken = jjFillToken();
          return localToken;
        }
        try
        {
          input_stream.backup(0);
          while ((curChar <= ' ') && ((0x100002600 & 1L << curChar) != 0L)) {
            curChar = input_stream.BeginToken();
          }
        }
        catch (IOException localIOException2) {}
      }
      jjmatchedKind = Integer.MAX_VALUE;
      jjmatchedPos = 0;
      i = jjMoveStringLiteralDfa0_0();
      if (jjmatchedKind == Integer.MAX_VALUE) {
        break;
      }
      if (jjmatchedPos + 1 < i) {
        input_stream.backup(i - jjmatchedPos - 1);
      }
    } while ((jjtoToken[(jjmatchedKind >> 6)] & 1L << (jjmatchedKind & 0x3F)) == 0L);
    Token localToken = jjFillToken();
    return localToken;
    int j = input_stream.getEndLine();
    int k = input_stream.getEndColumn();
    String str = null;
    boolean bool = false;
    try
    {
      input_stream.readChar();
      input_stream.backup(1);
    }
    catch (IOException localIOException3)
    {
      bool = true;
      str = i <= 1 ? "" : input_stream.GetImage();
      if ((curChar == '\n') || (curChar == '\r'))
      {
        j++;
        k = 0;
      }
      else
      {
        k++;
      }
    }
    if (!bool)
    {
      input_stream.backup(1);
      str = i <= 1 ? "" : input_stream.GetImage();
    }
    throw new TokenMgrError(bool, curLexState, j, k, str, curChar, 0);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\ParserTokenManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */