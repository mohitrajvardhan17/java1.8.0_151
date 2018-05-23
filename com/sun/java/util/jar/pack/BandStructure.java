package com.sun.java.util.jar.pack;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

abstract class BandStructure
{
  static final int MAX_EFFORT = 9;
  static final int MIN_EFFORT = 1;
  static final int DEFAULT_EFFORT = 5;
  PropMap p200 = Utils.currentPropMap();
  int verbose = p200.getInteger("com.sun.java.util.jar.pack.verbose");
  int effort = p200.getInteger("pack.effort");
  boolean optDumpBands;
  boolean optDebugBands;
  boolean optVaryCodings;
  boolean optBigStrings;
  private Package.Version highestClassVersion;
  private final boolean isReader;
  static final Coding BYTE1;
  static final Coding CHAR3;
  static final Coding BCI5;
  static final Coding BRANCH5;
  static final Coding UNSIGNED5;
  static final Coding UDELTA5;
  static final Coding SIGNED5;
  static final Coding DELTA5;
  static final Coding MDELTA5;
  private static final Coding[] basicCodings;
  private static final Map<Coding, Integer> basicCodingIndexes;
  protected byte[] bandHeaderBytes;
  protected int bandHeaderBytePos;
  protected int bandHeaderBytePos0;
  static final int SHORT_BAND_HEURISTIC = 100;
  public static final int NO_PHASE = 0;
  public static final int COLLECT_PHASE = 1;
  public static final int FROZEN_PHASE = 3;
  public static final int WRITE_PHASE = 5;
  public static final int EXPECT_PHASE = 2;
  public static final int READ_PHASE = 4;
  public static final int DISBURSE_PHASE = 6;
  public static final int DONE_PHASE = 8;
  private final List<CPRefBand> allKQBands;
  private List<Object[]> needPredefIndex;
  private CodingChooser codingChooser;
  static final byte[] defaultMetaCoding;
  static final byte[] noMetaCoding;
  ByteCounter outputCounter;
  protected int archiveOptions;
  protected long archiveSize0;
  protected long archiveSize1;
  protected int archiveNextCount;
  static final int AH_LENGTH_0 = 3;
  static final int AH_LENGTH_MIN = 15;
  static final int AH_LENGTH_S = 2;
  static final int AH_ARCHIVE_SIZE_HI = 0;
  static final int AH_ARCHIVE_SIZE_LO = 1;
  static final int AH_FILE_HEADER_LEN = 5;
  static final int AH_SPECIAL_FORMAT_LEN = 2;
  static final int AH_CP_NUMBER_LEN = 4;
  static final int AH_CP_EXTRA_LEN = 4;
  static final int AB_FLAGS_HI = 0;
  static final int AB_FLAGS_LO = 1;
  static final int AB_ATTR_COUNT = 2;
  static final int AB_ATTR_INDEXES = 3;
  static final int AB_ATTR_CALLS = 4;
  private static final boolean NULL_IS_OK = true;
  MultiBand all_bands;
  ByteBand archive_magic;
  IntBand archive_header_0;
  IntBand archive_header_S;
  IntBand archive_header_1;
  ByteBand band_headers;
  MultiBand cp_bands;
  IntBand cp_Utf8_prefix;
  IntBand cp_Utf8_suffix;
  IntBand cp_Utf8_chars;
  IntBand cp_Utf8_big_suffix;
  MultiBand cp_Utf8_big_chars;
  IntBand cp_Int;
  IntBand cp_Float;
  IntBand cp_Long_hi;
  IntBand cp_Long_lo;
  IntBand cp_Double_hi;
  IntBand cp_Double_lo;
  CPRefBand cp_String;
  CPRefBand cp_Class;
  CPRefBand cp_Signature_form;
  CPRefBand cp_Signature_classes;
  CPRefBand cp_Descr_name;
  CPRefBand cp_Descr_type;
  CPRefBand cp_Field_class;
  CPRefBand cp_Field_desc;
  CPRefBand cp_Method_class;
  CPRefBand cp_Method_desc;
  CPRefBand cp_Imethod_class;
  CPRefBand cp_Imethod_desc;
  IntBand cp_MethodHandle_refkind;
  CPRefBand cp_MethodHandle_member;
  CPRefBand cp_MethodType;
  CPRefBand cp_BootstrapMethod_ref;
  IntBand cp_BootstrapMethod_arg_count;
  CPRefBand cp_BootstrapMethod_arg;
  CPRefBand cp_InvokeDynamic_spec;
  CPRefBand cp_InvokeDynamic_desc;
  MultiBand attr_definition_bands;
  ByteBand attr_definition_headers;
  CPRefBand attr_definition_name;
  CPRefBand attr_definition_layout;
  MultiBand ic_bands;
  CPRefBand ic_this_class;
  IntBand ic_flags;
  CPRefBand ic_outer_class;
  CPRefBand ic_name;
  MultiBand class_bands;
  CPRefBand class_this;
  CPRefBand class_super;
  IntBand class_interface_count;
  CPRefBand class_interface;
  IntBand class_field_count;
  IntBand class_method_count;
  CPRefBand field_descr;
  MultiBand field_attr_bands;
  IntBand field_flags_hi;
  IntBand field_flags_lo;
  IntBand field_attr_count;
  IntBand field_attr_indexes;
  IntBand field_attr_calls;
  CPRefBand field_ConstantValue_KQ;
  CPRefBand field_Signature_RS;
  MultiBand field_metadata_bands;
  MultiBand field_type_metadata_bands;
  CPRefBand method_descr;
  MultiBand method_attr_bands;
  IntBand method_flags_hi;
  IntBand method_flags_lo;
  IntBand method_attr_count;
  IntBand method_attr_indexes;
  IntBand method_attr_calls;
  IntBand method_Exceptions_N;
  CPRefBand method_Exceptions_RC;
  CPRefBand method_Signature_RS;
  MultiBand method_metadata_bands;
  IntBand method_MethodParameters_NB;
  CPRefBand method_MethodParameters_name_RUN;
  IntBand method_MethodParameters_flag_FH;
  MultiBand method_type_metadata_bands;
  MultiBand class_attr_bands;
  IntBand class_flags_hi;
  IntBand class_flags_lo;
  IntBand class_attr_count;
  IntBand class_attr_indexes;
  IntBand class_attr_calls;
  CPRefBand class_SourceFile_RUN;
  CPRefBand class_EnclosingMethod_RC;
  CPRefBand class_EnclosingMethod_RDN;
  CPRefBand class_Signature_RS;
  MultiBand class_metadata_bands;
  IntBand class_InnerClasses_N;
  CPRefBand class_InnerClasses_RC;
  IntBand class_InnerClasses_F;
  CPRefBand class_InnerClasses_outer_RCN;
  CPRefBand class_InnerClasses_name_RUN;
  IntBand class_ClassFile_version_minor_H;
  IntBand class_ClassFile_version_major_H;
  MultiBand class_type_metadata_bands;
  MultiBand code_bands;
  ByteBand code_headers;
  IntBand code_max_stack;
  IntBand code_max_na_locals;
  IntBand code_handler_count;
  IntBand code_handler_start_P;
  IntBand code_handler_end_PO;
  IntBand code_handler_catch_PO;
  CPRefBand code_handler_class_RCN;
  MultiBand code_attr_bands;
  IntBand code_flags_hi;
  IntBand code_flags_lo;
  IntBand code_attr_count;
  IntBand code_attr_indexes;
  IntBand code_attr_calls;
  MultiBand stackmap_bands;
  IntBand code_StackMapTable_N;
  IntBand code_StackMapTable_frame_T;
  IntBand code_StackMapTable_local_N;
  IntBand code_StackMapTable_stack_N;
  IntBand code_StackMapTable_offset;
  IntBand code_StackMapTable_T;
  CPRefBand code_StackMapTable_RC;
  IntBand code_StackMapTable_P;
  IntBand code_LineNumberTable_N;
  IntBand code_LineNumberTable_bci_P;
  IntBand code_LineNumberTable_line;
  IntBand code_LocalVariableTable_N;
  IntBand code_LocalVariableTable_bci_P;
  IntBand code_LocalVariableTable_span_O;
  CPRefBand code_LocalVariableTable_name_RU;
  CPRefBand code_LocalVariableTable_type_RS;
  IntBand code_LocalVariableTable_slot;
  IntBand code_LocalVariableTypeTable_N;
  IntBand code_LocalVariableTypeTable_bci_P;
  IntBand code_LocalVariableTypeTable_span_O;
  CPRefBand code_LocalVariableTypeTable_name_RU;
  CPRefBand code_LocalVariableTypeTable_type_RS;
  IntBand code_LocalVariableTypeTable_slot;
  MultiBand code_type_metadata_bands;
  MultiBand bc_bands;
  ByteBand bc_codes;
  IntBand bc_case_count;
  IntBand bc_case_value;
  ByteBand bc_byte;
  IntBand bc_short;
  IntBand bc_local;
  IntBand bc_label;
  CPRefBand bc_intref;
  CPRefBand bc_floatref;
  CPRefBand bc_longref;
  CPRefBand bc_doubleref;
  CPRefBand bc_stringref;
  CPRefBand bc_loadablevalueref;
  CPRefBand bc_classref;
  CPRefBand bc_fieldref;
  CPRefBand bc_methodref;
  CPRefBand bc_imethodref;
  CPRefBand bc_indyref;
  CPRefBand bc_thisfield;
  CPRefBand bc_superfield;
  CPRefBand bc_thismethod;
  CPRefBand bc_supermethod;
  IntBand bc_initref;
  CPRefBand bc_escref;
  IntBand bc_escrefsize;
  IntBand bc_escsize;
  ByteBand bc_escbyte;
  MultiBand file_bands;
  CPRefBand file_name;
  IntBand file_size_hi;
  IntBand file_size_lo;
  IntBand file_modtime;
  IntBand file_options;
  ByteBand file_bits;
  protected MultiBand[] metadataBands;
  protected MultiBand[] typeMetadataBands;
  public static final int ADH_CONTEXT_MASK = 3;
  public static final int ADH_BIT_SHIFT = 2;
  public static final int ADH_BIT_IS_LSB = 1;
  public static final int ATTR_INDEX_OVERFLOW = -1;
  public int[] attrIndexLimit;
  protected long[] attrFlagMask;
  protected long[] attrDefSeen;
  protected int[] attrOverflowMask;
  protected int attrClassFileVersionMask;
  protected Map<Attribute.Layout, Band[]> attrBandTable;
  protected final Attribute.Layout attrCodeEmpty;
  protected final Attribute.Layout attrInnerClassesEmpty;
  protected final Attribute.Layout attrClassFileVersion;
  protected final Attribute.Layout attrConstantValue;
  Map<Attribute.Layout, Integer> attrIndexTable;
  protected List<List<Attribute.Layout>> attrDefs;
  protected MultiBand[] attrBands;
  private static final int[][] shortCodeLimits = { { 12, 12 }, { 8, 8 }, { 7, 7 } };
  public final int shortCodeHeader_h_limit;
  static final int LONG_CODE_HEADER = 0;
  static int nextSeqForDebug;
  static File dumpDir = null;
  private Map<Band, Band> prevForAssertMap;
  static LinkedList<String> bandSequenceList = null;
  
  protected abstract ConstantPool.Index getCPIndex(byte paramByte);
  
  public void initHighestClassVersion(Package.Version paramVersion)
    throws IOException
  {
    if (highestClassVersion != null) {
      throw new IOException("Highest class major version is already initialized to " + highestClassVersion + "; new setting is " + paramVersion);
    }
    highestClassVersion = paramVersion;
    adjustToClassVersion();
  }
  
  public Package.Version getHighestClassVersion()
  {
    return highestClassVersion;
  }
  
  protected BandStructure()
  {
    if (effort == 0) {
      effort = 5;
    }
    optDumpBands = p200.getBoolean("com.sun.java.util.jar.pack.dump.bands");
    optDebugBands = p200.getBoolean("com.sun.java.util.jar.pack.debug.bands");
    optVaryCodings = (!p200.getBoolean("com.sun.java.util.jar.pack.no.vary.codings"));
    optBigStrings = (!p200.getBoolean("com.sun.java.util.jar.pack.no.big.strings"));
    highestClassVersion = null;
    isReader = (this instanceof PackageReader);
    allKQBands = new ArrayList();
    needPredefIndex = new ArrayList();
    all_bands = ((MultiBand)new MultiBand("(package)", UNSIGNED5).init());
    archive_magic = all_bands.newByteBand("archive_magic");
    archive_header_0 = all_bands.newIntBand("archive_header_0", UNSIGNED5);
    archive_header_S = all_bands.newIntBand("archive_header_S", UNSIGNED5);
    archive_header_1 = all_bands.newIntBand("archive_header_1", UNSIGNED5);
    band_headers = all_bands.newByteBand("band_headers");
    cp_bands = all_bands.newMultiBand("(constant_pool)", DELTA5);
    cp_Utf8_prefix = cp_bands.newIntBand("cp_Utf8_prefix");
    cp_Utf8_suffix = cp_bands.newIntBand("cp_Utf8_suffix", UNSIGNED5);
    cp_Utf8_chars = cp_bands.newIntBand("cp_Utf8_chars", CHAR3);
    cp_Utf8_big_suffix = cp_bands.newIntBand("cp_Utf8_big_suffix");
    cp_Utf8_big_chars = cp_bands.newMultiBand("(cp_Utf8_big_chars)", DELTA5);
    cp_Int = cp_bands.newIntBand("cp_Int", UDELTA5);
    cp_Float = cp_bands.newIntBand("cp_Float", UDELTA5);
    cp_Long_hi = cp_bands.newIntBand("cp_Long_hi", UDELTA5);
    cp_Long_lo = cp_bands.newIntBand("cp_Long_lo");
    cp_Double_hi = cp_bands.newIntBand("cp_Double_hi", UDELTA5);
    cp_Double_lo = cp_bands.newIntBand("cp_Double_lo");
    cp_String = cp_bands.newCPRefBand("cp_String", UDELTA5, (byte)1);
    cp_Class = cp_bands.newCPRefBand("cp_Class", UDELTA5, (byte)1);
    cp_Signature_form = cp_bands.newCPRefBand("cp_Signature_form", (byte)1);
    cp_Signature_classes = cp_bands.newCPRefBand("cp_Signature_classes", UDELTA5, (byte)7);
    cp_Descr_name = cp_bands.newCPRefBand("cp_Descr_name", (byte)1);
    cp_Descr_type = cp_bands.newCPRefBand("cp_Descr_type", UDELTA5, (byte)13);
    cp_Field_class = cp_bands.newCPRefBand("cp_Field_class", (byte)7);
    cp_Field_desc = cp_bands.newCPRefBand("cp_Field_desc", UDELTA5, (byte)12);
    cp_Method_class = cp_bands.newCPRefBand("cp_Method_class", (byte)7);
    cp_Method_desc = cp_bands.newCPRefBand("cp_Method_desc", UDELTA5, (byte)12);
    cp_Imethod_class = cp_bands.newCPRefBand("cp_Imethod_class", (byte)7);
    cp_Imethod_desc = cp_bands.newCPRefBand("cp_Imethod_desc", UDELTA5, (byte)12);
    cp_MethodHandle_refkind = cp_bands.newIntBand("cp_MethodHandle_refkind", DELTA5);
    cp_MethodHandle_member = cp_bands.newCPRefBand("cp_MethodHandle_member", UDELTA5, (byte)52);
    cp_MethodType = cp_bands.newCPRefBand("cp_MethodType", UDELTA5, (byte)13);
    cp_BootstrapMethod_ref = cp_bands.newCPRefBand("cp_BootstrapMethod_ref", DELTA5, (byte)15);
    cp_BootstrapMethod_arg_count = cp_bands.newIntBand("cp_BootstrapMethod_arg_count", UDELTA5);
    cp_BootstrapMethod_arg = cp_bands.newCPRefBand("cp_BootstrapMethod_arg", DELTA5, (byte)51);
    cp_InvokeDynamic_spec = cp_bands.newCPRefBand("cp_InvokeDynamic_spec", DELTA5, (byte)17);
    cp_InvokeDynamic_desc = cp_bands.newCPRefBand("cp_InvokeDynamic_desc", UDELTA5, (byte)12);
    attr_definition_bands = all_bands.newMultiBand("(attr_definition_bands)", UNSIGNED5);
    attr_definition_headers = attr_definition_bands.newByteBand("attr_definition_headers");
    attr_definition_name = attr_definition_bands.newCPRefBand("attr_definition_name", (byte)1);
    attr_definition_layout = attr_definition_bands.newCPRefBand("attr_definition_layout", (byte)1);
    ic_bands = all_bands.newMultiBand("(ic_bands)", DELTA5);
    ic_this_class = ic_bands.newCPRefBand("ic_this_class", UDELTA5, (byte)7);
    ic_flags = ic_bands.newIntBand("ic_flags", UNSIGNED5);
    ic_outer_class = ic_bands.newCPRefBand("ic_outer_class", DELTA5, (byte)7, true);
    ic_name = ic_bands.newCPRefBand("ic_name", DELTA5, (byte)1, true);
    class_bands = all_bands.newMultiBand("(class_bands)", DELTA5);
    class_this = class_bands.newCPRefBand("class_this", (byte)7);
    class_super = class_bands.newCPRefBand("class_super", (byte)7);
    class_interface_count = class_bands.newIntBand("class_interface_count");
    class_interface = class_bands.newCPRefBand("class_interface", (byte)7);
    class_field_count = class_bands.newIntBand("class_field_count");
    class_method_count = class_bands.newIntBand("class_method_count");
    field_descr = class_bands.newCPRefBand("field_descr", (byte)12);
    field_attr_bands = class_bands.newMultiBand("(field_attr_bands)", UNSIGNED5);
    field_flags_hi = field_attr_bands.newIntBand("field_flags_hi");
    field_flags_lo = field_attr_bands.newIntBand("field_flags_lo");
    field_attr_count = field_attr_bands.newIntBand("field_attr_count");
    field_attr_indexes = field_attr_bands.newIntBand("field_attr_indexes");
    field_attr_calls = field_attr_bands.newIntBand("field_attr_calls");
    field_ConstantValue_KQ = field_attr_bands.newCPRefBand("field_ConstantValue_KQ", (byte)53);
    field_Signature_RS = field_attr_bands.newCPRefBand("field_Signature_RS", (byte)13);
    field_metadata_bands = field_attr_bands.newMultiBand("(field_metadata_bands)", UNSIGNED5);
    field_type_metadata_bands = field_attr_bands.newMultiBand("(field_type_metadata_bands)", UNSIGNED5);
    method_descr = class_bands.newCPRefBand("method_descr", MDELTA5, (byte)12);
    method_attr_bands = class_bands.newMultiBand("(method_attr_bands)", UNSIGNED5);
    method_flags_hi = method_attr_bands.newIntBand("method_flags_hi");
    method_flags_lo = method_attr_bands.newIntBand("method_flags_lo");
    method_attr_count = method_attr_bands.newIntBand("method_attr_count");
    method_attr_indexes = method_attr_bands.newIntBand("method_attr_indexes");
    method_attr_calls = method_attr_bands.newIntBand("method_attr_calls");
    method_Exceptions_N = method_attr_bands.newIntBand("method_Exceptions_N");
    method_Exceptions_RC = method_attr_bands.newCPRefBand("method_Exceptions_RC", (byte)7);
    method_Signature_RS = method_attr_bands.newCPRefBand("method_Signature_RS", (byte)13);
    method_metadata_bands = method_attr_bands.newMultiBand("(method_metadata_bands)", UNSIGNED5);
    method_MethodParameters_NB = method_attr_bands.newIntBand("method_MethodParameters_NB", BYTE1);
    method_MethodParameters_name_RUN = method_attr_bands.newCPRefBand("method_MethodParameters_name_RUN", UNSIGNED5, (byte)1, true);
    method_MethodParameters_flag_FH = method_attr_bands.newIntBand("method_MethodParameters_flag_FH");
    method_type_metadata_bands = method_attr_bands.newMultiBand("(method_type_metadata_bands)", UNSIGNED5);
    class_attr_bands = class_bands.newMultiBand("(class_attr_bands)", UNSIGNED5);
    class_flags_hi = class_attr_bands.newIntBand("class_flags_hi");
    class_flags_lo = class_attr_bands.newIntBand("class_flags_lo");
    class_attr_count = class_attr_bands.newIntBand("class_attr_count");
    class_attr_indexes = class_attr_bands.newIntBand("class_attr_indexes");
    class_attr_calls = class_attr_bands.newIntBand("class_attr_calls");
    class_SourceFile_RUN = class_attr_bands.newCPRefBand("class_SourceFile_RUN", UNSIGNED5, (byte)1, true);
    class_EnclosingMethod_RC = class_attr_bands.newCPRefBand("class_EnclosingMethod_RC", (byte)7);
    class_EnclosingMethod_RDN = class_attr_bands.newCPRefBand("class_EnclosingMethod_RDN", UNSIGNED5, (byte)12, true);
    class_Signature_RS = class_attr_bands.newCPRefBand("class_Signature_RS", (byte)13);
    class_metadata_bands = class_attr_bands.newMultiBand("(class_metadata_bands)", UNSIGNED5);
    class_InnerClasses_N = class_attr_bands.newIntBand("class_InnerClasses_N");
    class_InnerClasses_RC = class_attr_bands.newCPRefBand("class_InnerClasses_RC", (byte)7);
    class_InnerClasses_F = class_attr_bands.newIntBand("class_InnerClasses_F");
    class_InnerClasses_outer_RCN = class_attr_bands.newCPRefBand("class_InnerClasses_outer_RCN", UNSIGNED5, (byte)7, true);
    class_InnerClasses_name_RUN = class_attr_bands.newCPRefBand("class_InnerClasses_name_RUN", UNSIGNED5, (byte)1, true);
    class_ClassFile_version_minor_H = class_attr_bands.newIntBand("class_ClassFile_version_minor_H");
    class_ClassFile_version_major_H = class_attr_bands.newIntBand("class_ClassFile_version_major_H");
    class_type_metadata_bands = class_attr_bands.newMultiBand("(class_type_metadata_bands)", UNSIGNED5);
    code_bands = class_bands.newMultiBand("(code_bands)", UNSIGNED5);
    code_headers = code_bands.newByteBand("code_headers");
    code_max_stack = code_bands.newIntBand("code_max_stack", UNSIGNED5);
    code_max_na_locals = code_bands.newIntBand("code_max_na_locals", UNSIGNED5);
    code_handler_count = code_bands.newIntBand("code_handler_count", UNSIGNED5);
    code_handler_start_P = code_bands.newIntBand("code_handler_start_P", BCI5);
    code_handler_end_PO = code_bands.newIntBand("code_handler_end_PO", BRANCH5);
    code_handler_catch_PO = code_bands.newIntBand("code_handler_catch_PO", BRANCH5);
    code_handler_class_RCN = code_bands.newCPRefBand("code_handler_class_RCN", UNSIGNED5, (byte)7, true);
    code_attr_bands = class_bands.newMultiBand("(code_attr_bands)", UNSIGNED5);
    code_flags_hi = code_attr_bands.newIntBand("code_flags_hi");
    code_flags_lo = code_attr_bands.newIntBand("code_flags_lo");
    code_attr_count = code_attr_bands.newIntBand("code_attr_count");
    code_attr_indexes = code_attr_bands.newIntBand("code_attr_indexes");
    code_attr_calls = code_attr_bands.newIntBand("code_attr_calls");
    stackmap_bands = code_attr_bands.newMultiBand("(StackMapTable_bands)", UNSIGNED5);
    code_StackMapTable_N = stackmap_bands.newIntBand("code_StackMapTable_N");
    code_StackMapTable_frame_T = stackmap_bands.newIntBand("code_StackMapTable_frame_T", BYTE1);
    code_StackMapTable_local_N = stackmap_bands.newIntBand("code_StackMapTable_local_N");
    code_StackMapTable_stack_N = stackmap_bands.newIntBand("code_StackMapTable_stack_N");
    code_StackMapTable_offset = stackmap_bands.newIntBand("code_StackMapTable_offset", UNSIGNED5);
    code_StackMapTable_T = stackmap_bands.newIntBand("code_StackMapTable_T", BYTE1);
    code_StackMapTable_RC = stackmap_bands.newCPRefBand("code_StackMapTable_RC", (byte)7);
    code_StackMapTable_P = stackmap_bands.newIntBand("code_StackMapTable_P", BCI5);
    code_LineNumberTable_N = code_attr_bands.newIntBand("code_LineNumberTable_N");
    code_LineNumberTable_bci_P = code_attr_bands.newIntBand("code_LineNumberTable_bci_P", BCI5);
    code_LineNumberTable_line = code_attr_bands.newIntBand("code_LineNumberTable_line");
    code_LocalVariableTable_N = code_attr_bands.newIntBand("code_LocalVariableTable_N");
    code_LocalVariableTable_bci_P = code_attr_bands.newIntBand("code_LocalVariableTable_bci_P", BCI5);
    code_LocalVariableTable_span_O = code_attr_bands.newIntBand("code_LocalVariableTable_span_O", BRANCH5);
    code_LocalVariableTable_name_RU = code_attr_bands.newCPRefBand("code_LocalVariableTable_name_RU", (byte)1);
    code_LocalVariableTable_type_RS = code_attr_bands.newCPRefBand("code_LocalVariableTable_type_RS", (byte)13);
    code_LocalVariableTable_slot = code_attr_bands.newIntBand("code_LocalVariableTable_slot");
    code_LocalVariableTypeTable_N = code_attr_bands.newIntBand("code_LocalVariableTypeTable_N");
    code_LocalVariableTypeTable_bci_P = code_attr_bands.newIntBand("code_LocalVariableTypeTable_bci_P", BCI5);
    code_LocalVariableTypeTable_span_O = code_attr_bands.newIntBand("code_LocalVariableTypeTable_span_O", BRANCH5);
    code_LocalVariableTypeTable_name_RU = code_attr_bands.newCPRefBand("code_LocalVariableTypeTable_name_RU", (byte)1);
    code_LocalVariableTypeTable_type_RS = code_attr_bands.newCPRefBand("code_LocalVariableTypeTable_type_RS", (byte)13);
    code_LocalVariableTypeTable_slot = code_attr_bands.newIntBand("code_LocalVariableTypeTable_slot");
    code_type_metadata_bands = code_attr_bands.newMultiBand("(code_type_metadata_bands)", UNSIGNED5);
    bc_bands = all_bands.newMultiBand("(byte_codes)", UNSIGNED5);
    bc_codes = bc_bands.newByteBand("bc_codes");
    bc_case_count = bc_bands.newIntBand("bc_case_count");
    bc_case_value = bc_bands.newIntBand("bc_case_value", DELTA5);
    bc_byte = bc_bands.newByteBand("bc_byte");
    bc_short = bc_bands.newIntBand("bc_short", DELTA5);
    bc_local = bc_bands.newIntBand("bc_local");
    bc_label = bc_bands.newIntBand("bc_label", BRANCH5);
    bc_intref = bc_bands.newCPRefBand("bc_intref", DELTA5, (byte)3);
    bc_floatref = bc_bands.newCPRefBand("bc_floatref", DELTA5, (byte)4);
    bc_longref = bc_bands.newCPRefBand("bc_longref", DELTA5, (byte)5);
    bc_doubleref = bc_bands.newCPRefBand("bc_doubleref", DELTA5, (byte)6);
    bc_stringref = bc_bands.newCPRefBand("bc_stringref", DELTA5, (byte)8);
    bc_loadablevalueref = bc_bands.newCPRefBand("bc_loadablevalueref", DELTA5, (byte)51);
    bc_classref = bc_bands.newCPRefBand("bc_classref", UNSIGNED5, (byte)7, true);
    bc_fieldref = bc_bands.newCPRefBand("bc_fieldref", DELTA5, (byte)9);
    bc_methodref = bc_bands.newCPRefBand("bc_methodref", (byte)10);
    bc_imethodref = bc_bands.newCPRefBand("bc_imethodref", DELTA5, (byte)11);
    bc_indyref = bc_bands.newCPRefBand("bc_indyref", DELTA5, (byte)18);
    bc_thisfield = bc_bands.newCPRefBand("bc_thisfield", (byte)0);
    bc_superfield = bc_bands.newCPRefBand("bc_superfield", (byte)0);
    bc_thismethod = bc_bands.newCPRefBand("bc_thismethod", (byte)0);
    bc_supermethod = bc_bands.newCPRefBand("bc_supermethod", (byte)0);
    bc_initref = bc_bands.newIntBand("bc_initref");
    bc_escref = bc_bands.newCPRefBand("bc_escref", (byte)50);
    bc_escrefsize = bc_bands.newIntBand("bc_escrefsize");
    bc_escsize = bc_bands.newIntBand("bc_escsize");
    bc_escbyte = bc_bands.newByteBand("bc_escbyte");
    file_bands = all_bands.newMultiBand("(file_bands)", UNSIGNED5);
    file_name = file_bands.newCPRefBand("file_name", (byte)1);
    file_size_hi = file_bands.newIntBand("file_size_hi");
    file_size_lo = file_bands.newIntBand("file_size_lo");
    file_modtime = file_bands.newIntBand("file_modtime", DELTA5);
    file_options = file_bands.newIntBand("file_options");
    file_bits = file_bands.newByteBand("file_bits");
    metadataBands = new MultiBand[4];
    metadataBands[0] = class_metadata_bands;
    metadataBands[1] = field_metadata_bands;
    metadataBands[2] = method_metadata_bands;
    typeMetadataBands = new MultiBand[4];
    typeMetadataBands[0] = class_type_metadata_bands;
    typeMetadataBands[1] = field_type_metadata_bands;
    typeMetadataBands[2] = method_type_metadata_bands;
    typeMetadataBands[3] = code_type_metadata_bands;
    attrIndexLimit = new int[4];
    attrFlagMask = new long[4];
    attrDefSeen = new long[4];
    attrOverflowMask = new int[4];
    attrBandTable = new HashMap();
    attrIndexTable = new HashMap();
    attrDefs = new FixedList(4);
    for (int i = 0; i < 4; i++)
    {
      assert (attrIndexLimit[i] == 0);
      attrIndexLimit[i] = 32;
      attrDefs.set(i, new ArrayList(Collections.nCopies(attrIndexLimit[i], (Attribute.Layout)null)));
    }
    attrInnerClassesEmpty = predefineAttribute(23, 0, null, "InnerClasses", "");
    assert (attrInnerClassesEmpty == Package.attrInnerClassesEmpty);
    predefineAttribute(17, 0, new Band[] { class_SourceFile_RUN }, "SourceFile", "RUNH");
    predefineAttribute(18, 0, new Band[] { class_EnclosingMethod_RC, class_EnclosingMethod_RDN }, "EnclosingMethod", "RCHRDNH");
    attrClassFileVersion = predefineAttribute(24, 0, new Band[] { class_ClassFile_version_minor_H, class_ClassFile_version_major_H }, ".ClassFile.version", "HH");
    predefineAttribute(19, 0, new Band[] { class_Signature_RS }, "Signature", "RSH");
    predefineAttribute(20, 0, null, "Deprecated", "");
    predefineAttribute(16, 0, null, ".Overflow", "");
    attrConstantValue = predefineAttribute(17, 1, new Band[] { field_ConstantValue_KQ }, "ConstantValue", "KQH");
    predefineAttribute(19, 1, new Band[] { field_Signature_RS }, "Signature", "RSH");
    predefineAttribute(20, 1, null, "Deprecated", "");
    predefineAttribute(16, 1, null, ".Overflow", "");
    attrCodeEmpty = predefineAttribute(17, 2, null, "Code", "");
    predefineAttribute(18, 2, new Band[] { method_Exceptions_N, method_Exceptions_RC }, "Exceptions", "NH[RCH]");
    predefineAttribute(26, 2, new Band[] { method_MethodParameters_NB, method_MethodParameters_name_RUN, method_MethodParameters_flag_FH }, "MethodParameters", "NB[RUNHFH]");
    assert (attrCodeEmpty == Package.attrCodeEmpty);
    predefineAttribute(19, 2, new Band[] { method_Signature_RS }, "Signature", "RSH");
    predefineAttribute(20, 2, null, "Deprecated", "");
    predefineAttribute(16, 2, null, ".Overflow", "");
    for (i = 0; i < 4; i++)
    {
      MultiBand localMultiBand1 = metadataBands[i];
      if (i != 3)
      {
        predefineAttribute(21, Constants.ATTR_CONTEXT_NAME[i] + "_RVA_", localMultiBand1, Attribute.lookup(null, i, "RuntimeVisibleAnnotations"));
        predefineAttribute(22, Constants.ATTR_CONTEXT_NAME[i] + "_RIA_", localMultiBand1, Attribute.lookup(null, i, "RuntimeInvisibleAnnotations"));
        if (i == 2)
        {
          predefineAttribute(23, "method_RVPA_", localMultiBand1, Attribute.lookup(null, i, "RuntimeVisibleParameterAnnotations"));
          predefineAttribute(24, "method_RIPA_", localMultiBand1, Attribute.lookup(null, i, "RuntimeInvisibleParameterAnnotations"));
          predefineAttribute(25, "method_AD_", localMultiBand1, Attribute.lookup(null, i, "AnnotationDefault"));
        }
      }
      MultiBand localMultiBand2 = typeMetadataBands[i];
      predefineAttribute(27, Constants.ATTR_CONTEXT_NAME[i] + "_RVTA_", localMultiBand2, Attribute.lookup(null, i, "RuntimeVisibleTypeAnnotations"));
      predefineAttribute(28, Constants.ATTR_CONTEXT_NAME[i] + "_RITA_", localMultiBand2, Attribute.lookup(null, i, "RuntimeInvisibleTypeAnnotations"));
    }
    Attribute.Layout localLayout = Attribute.lookup(null, 3, "StackMapTable").layout();
    predefineAttribute(0, 3, stackmap_bands.toArray(), localLayout.name(), localLayout.layout());
    predefineAttribute(1, 3, new Band[] { code_LineNumberTable_N, code_LineNumberTable_bci_P, code_LineNumberTable_line }, "LineNumberTable", "NH[PHH]");
    predefineAttribute(2, 3, new Band[] { code_LocalVariableTable_N, code_LocalVariableTable_bci_P, code_LocalVariableTable_span_O, code_LocalVariableTable_name_RU, code_LocalVariableTable_type_RS, code_LocalVariableTable_slot }, "LocalVariableTable", "NH[PHOHRUHRSHH]");
    predefineAttribute(3, 3, new Band[] { code_LocalVariableTypeTable_N, code_LocalVariableTypeTable_bci_P, code_LocalVariableTypeTable_span_O, code_LocalVariableTypeTable_name_RU, code_LocalVariableTypeTable_type_RS, code_LocalVariableTypeTable_slot }, "LocalVariableTypeTable", "NH[PHOHRUHRSHH]");
    predefineAttribute(16, 3, null, ".Overflow", "");
    for (int j = 0; j < 4; j++) {
      attrDefSeen[j] = 0L;
    }
    for (j = 0; j < 4; j++)
    {
      attrOverflowMask[j] = 65536;
      attrIndexLimit[j] = 0;
    }
    attrClassFileVersionMask = 16777216;
    attrBands = new MultiBand[4];
    attrBands[0] = class_attr_bands;
    attrBands[1] = field_attr_bands;
    attrBands[2] = method_attr_bands;
    attrBands[3] = code_attr_bands;
    shortCodeHeader_h_limit = shortCodeLimits.length;
  }
  
  public static Coding codingForIndex(int paramInt)
  {
    return paramInt < basicCodings.length ? basicCodings[paramInt] : null;
  }
  
  public static int indexOf(Coding paramCoding)
  {
    Integer localInteger = (Integer)basicCodingIndexes.get(paramCoding);
    if (localInteger == null) {
      return 0;
    }
    return localInteger.intValue();
  }
  
  public static Coding[] getBasicCodings()
  {
    return (Coding[])basicCodings.clone();
  }
  
  protected CodingMethod getBandHeader(int paramInt, Coding paramCoding)
  {
    CodingMethod[] arrayOfCodingMethod = { null };
    bandHeaderBytes[(--bandHeaderBytePos)] = ((byte)paramInt);
    bandHeaderBytePos0 = bandHeaderBytePos;
    bandHeaderBytePos = parseMetaCoding(bandHeaderBytes, bandHeaderBytePos, paramCoding, arrayOfCodingMethod);
    return arrayOfCodingMethod[0];
  }
  
  public static int parseMetaCoding(byte[] paramArrayOfByte, int paramInt, Coding paramCoding, CodingMethod[] paramArrayOfCodingMethod)
  {
    if ((paramArrayOfByte[paramInt] & 0xFF) == 0)
    {
      paramArrayOfCodingMethod[0] = paramCoding;
      return paramInt + 1;
    }
    int i = Coding.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, paramArrayOfCodingMethod);
    if (i > paramInt) {
      return i;
    }
    i = PopulationCoding.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, paramArrayOfCodingMethod);
    if (i > paramInt) {
      return i;
    }
    i = AdaptiveCoding.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, paramArrayOfCodingMethod);
    if (i > paramInt) {
      return i;
    }
    throw new RuntimeException("Bad meta-coding op " + (paramArrayOfByte[paramInt] & 0xFF));
  }
  
  static boolean phaseIsRead(int paramInt)
  {
    return paramInt % 2 == 0;
  }
  
  static int phaseCmp(int paramInt1, int paramInt2)
  {
    assert ((paramInt1 % 2 == paramInt2 % 2) || (paramInt1 % 8 == 0) || (paramInt2 % 8 == 0));
    return paramInt1 - paramInt2;
  }
  
  static int getIntTotal(int[] paramArrayOfInt)
  {
    int i = 0;
    for (int j = 0; j < paramArrayOfInt.length; j++) {
      i += paramArrayOfInt[j];
    }
    return i;
  }
  
  int encodeRef(ConstantPool.Entry paramEntry, ConstantPool.Index paramIndex)
  {
    if (paramIndex == null) {
      throw new RuntimeException("null index for " + paramEntry.stringValue());
    }
    int i = paramIndex.indexOf(paramEntry);
    if (verbose > 2) {
      Utils.log.fine("putRef " + i + " => " + paramEntry);
    }
    return i;
  }
  
  ConstantPool.Entry decodeRef(int paramInt, ConstantPool.Index paramIndex)
  {
    if ((paramInt < 0) || (paramInt >= paramIndex.size())) {
      Utils.log.warning("decoding bad ref " + paramInt + " in " + paramIndex);
    }
    ConstantPool.Entry localEntry = paramIndex.getEntry(paramInt);
    if (verbose > 2) {
      Utils.log.fine("getRef " + paramInt + " => " + localEntry);
    }
    return localEntry;
  }
  
  protected CodingChooser getCodingChooser()
  {
    if (codingChooser == null)
    {
      codingChooser = new CodingChooser(effort, basicCodings);
      if ((codingChooser.stress != null) && ((this instanceof PackageWriter)))
      {
        ArrayList localArrayList = pkg.classes;
        if (!localArrayList.isEmpty())
        {
          Package.Class localClass = (Package.Class)localArrayList.get(0);
          codingChooser.addStressSeed(localClass.getName().hashCode());
        }
      }
    }
    return codingChooser;
  }
  
  public CodingMethod chooseCoding(int[] paramArrayOfInt1, int paramInt1, int paramInt2, Coding paramCoding, String paramString, int[] paramArrayOfInt2)
  {
    assert (optVaryCodings);
    if (effort <= 1) {
      return paramCoding;
    }
    CodingChooser localCodingChooser = getCodingChooser();
    if ((verbose > 1) || (verbose > 1)) {
      Utils.log.fine("--- chooseCoding " + paramString);
    }
    return localCodingChooser.choose(paramArrayOfInt1, paramInt1, paramInt2, paramCoding, paramArrayOfInt2);
  }
  
  protected static int decodeEscapeValue(int paramInt, Coding paramCoding)
  {
    if ((paramCoding.B() == 1) || (paramCoding.L() == 0)) {
      return -1;
    }
    int i;
    if (paramCoding.S() != 0)
    {
      if ((65280 <= paramInt) && (paramInt <= -1) && (paramCoding.min() <= 65280))
      {
        i = -1 - paramInt;
        assert ((i >= 0) && (i < 256));
        return i;
      }
    }
    else
    {
      i = paramCoding.L();
      if ((i <= paramInt) && (paramInt <= i + 255) && (paramCoding.max() >= i + 255))
      {
        int j = paramInt - i;
        assert ((j >= 0) && (j < 256));
        return j;
      }
    }
    return -1;
  }
  
  protected static int encodeEscapeValue(int paramInt, Coding paramCoding)
  {
    assert ((paramInt >= 0) && (paramInt < 256));
    assert ((paramCoding.B() > 1) && (paramCoding.L() > 0));
    int i;
    if (paramCoding.S() != 0)
    {
      assert (paramCoding.min() <= 65280);
      i = -1 - paramInt;
    }
    else
    {
      int j = paramCoding.L();
      assert (paramCoding.max() >= j + 255);
      i = paramInt + j;
    }
    assert (decodeEscapeValue(i, paramCoding) == paramInt) : (paramCoding + " XB=" + paramInt + " X=" + i);
    return i;
  }
  
  void writeAllBandsTo(OutputStream paramOutputStream)
    throws IOException
  {
    outputCounter = new ByteCounter(paramOutputStream);
    paramOutputStream = outputCounter;
    all_bands.writeTo(paramOutputStream);
    if (verbose > 0)
    {
      long l = outputCounter.getCount();
      Utils.log.info("Wrote total of " + l + " bytes.");
      assert (l == archiveSize0 + archiveSize1);
    }
    outputCounter = null;
  }
  
  static IntBand getAttrBand(MultiBand paramMultiBand, int paramInt)
  {
    IntBand localIntBand = (IntBand)paramMultiBand.get(paramInt);
    switch (paramInt)
    {
    case 0: 
      if ((!$assertionsDisabled) && (!localIntBand.name().endsWith("_flags_hi"))) {
        throw new AssertionError();
      }
      break;
    case 1: 
      if ((!$assertionsDisabled) && (!localIntBand.name().endsWith("_flags_lo"))) {
        throw new AssertionError();
      }
      break;
    case 2: 
      if ((!$assertionsDisabled) && (!localIntBand.name().endsWith("_attr_count"))) {
        throw new AssertionError();
      }
      break;
    case 3: 
      if ((!$assertionsDisabled) && (!localIntBand.name().endsWith("_attr_indexes"))) {
        throw new AssertionError();
      }
      break;
    case 4: 
      if ((!$assertionsDisabled) && (!localIntBand.name().endsWith("_attr_calls"))) {
        throw new AssertionError();
      }
      break;
    default: 
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      break;
    }
    return localIntBand;
  }
  
  protected void setBandIndexes()
  {
    Iterator localIterator = needPredefIndex.iterator();
    while (localIterator.hasNext())
    {
      Object[] arrayOfObject = (Object[])localIterator.next();
      CPRefBand localCPRefBand = (CPRefBand)arrayOfObject[0];
      Byte localByte = (Byte)arrayOfObject[1];
      localCPRefBand.setIndex(getCPIndex(localByte.byteValue()));
    }
    needPredefIndex = null;
    if (verbose > 3) {
      printCDecl(all_bands);
    }
  }
  
  protected void setBandIndex(CPRefBand paramCPRefBand, byte paramByte)
  {
    Object[] arrayOfObject = { paramCPRefBand, Byte.valueOf(paramByte) };
    if (paramByte == 53) {
      allKQBands.add(paramCPRefBand);
    } else if (needPredefIndex != null) {
      needPredefIndex.add(arrayOfObject);
    } else {
      paramCPRefBand.setIndex(getCPIndex(paramByte));
    }
  }
  
  protected void setConstantValueIndex(Package.Class.Field paramField)
  {
    ConstantPool.Index localIndex = null;
    if (paramField != null)
    {
      byte b = paramField.getLiteralTag();
      localIndex = getCPIndex(b);
      if (verbose > 2) {
        Utils.log.fine("setConstantValueIndex " + paramField + " " + ConstantPool.tagName(b) + " => " + localIndex);
      }
      assert (localIndex != null);
    }
    Iterator localIterator = allKQBands.iterator();
    while (localIterator.hasNext())
    {
      CPRefBand localCPRefBand = (CPRefBand)localIterator.next();
      localCPRefBand.setIndex(localIndex);
    }
  }
  
  private void adjustToClassVersion()
    throws IOException
  {
    if (getHighestClassVersion().lessThan(Constants.JAVA6_MAX_CLASS_VERSION))
    {
      if (verbose > 0) {
        Utils.log.fine("Legacy package version");
      }
      undefineAttribute(0, 3);
    }
  }
  
  protected void initAttrIndexLimit()
  {
    for (int i = 0; i < 4; i++)
    {
      assert (attrIndexLimit[i] == 0);
      attrIndexLimit[i] = (haveFlagsHi(i) ? 63 : 32);
      List localList = (List)attrDefs.get(i);
      assert (localList.size() == 32);
      int j = attrIndexLimit[i] - localList.size();
      localList.addAll(Collections.nCopies(j, (Attribute.Layout)null));
    }
  }
  
  protected boolean haveFlagsHi(int paramInt)
  {
    int i = 1 << 9 + paramInt;
    switch (paramInt)
    {
    case 0: 
      if ((!$assertionsDisabled) && (i != 512)) {
        throw new AssertionError();
      }
      break;
    case 1: 
      if ((!$assertionsDisabled) && (i != 1024)) {
        throw new AssertionError();
      }
      break;
    case 2: 
      if ((!$assertionsDisabled) && (i != 2048)) {
        throw new AssertionError();
      }
      break;
    case 3: 
      if ((!$assertionsDisabled) && (i != 4096)) {
        throw new AssertionError();
      }
      break;
    default: 
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      break;
    }
    return testBit(archiveOptions, i);
  }
  
  protected List<Attribute.Layout> getPredefinedAttrs(int paramInt)
  {
    assert (attrIndexLimit[paramInt] != 0);
    ArrayList localArrayList = new ArrayList(attrIndexLimit[paramInt]);
    for (int i = 0; i < attrIndexLimit[paramInt]; i++) {
      if (!testBit(attrDefSeen[paramInt], 1L << i))
      {
        Attribute.Layout localLayout = (Attribute.Layout)((List)attrDefs.get(paramInt)).get(i);
        if (localLayout != null)
        {
          assert (isPredefinedAttr(paramInt, i));
          localArrayList.add(localLayout);
        }
      }
    }
    return localArrayList;
  }
  
  protected boolean isPredefinedAttr(int paramInt1, int paramInt2)
  {
    assert (attrIndexLimit[paramInt1] != 0);
    if (paramInt2 >= attrIndexLimit[paramInt1]) {
      return false;
    }
    if (testBit(attrDefSeen[paramInt1], 1L << paramInt2)) {
      return false;
    }
    return ((List)attrDefs.get(paramInt1)).get(paramInt2) != null;
  }
  
  protected void adjustSpecialAttrMasks()
  {
    attrClassFileVersionMask = ((int)(attrClassFileVersionMask & (attrDefSeen[0] ^ 0xFFFFFFFFFFFFFFFF)));
    for (int i = 0; i < 4; i++)
    {
      int tmp33_32 = i;
      int[] tmp33_29 = attrOverflowMask;
      tmp33_29[tmp33_32] = ((int)(tmp33_29[tmp33_32] & (attrDefSeen[i] ^ 0xFFFFFFFFFFFFFFFF)));
    }
  }
  
  protected Attribute makeClassFileVersionAttr(Package.Version paramVersion)
  {
    return attrClassFileVersion.addContent(paramVersion.asBytes());
  }
  
  protected Package.Version parseClassFileVersionAttr(Attribute paramAttribute)
  {
    assert (paramAttribute.layout() == attrClassFileVersion);
    assert (paramAttribute.size() == 4);
    return Package.Version.of(paramAttribute.bytes());
  }
  
  private boolean assertBandOKForElems(Band[] paramArrayOfBand, Attribute.Layout.Element[] paramArrayOfElement)
  {
    for (int i = 0; i < paramArrayOfElement.length; i++) {
      assert (assertBandOKForElem(paramArrayOfBand, paramArrayOfElement[i]));
    }
    return true;
  }
  
  private boolean assertBandOKForElem(Band[] paramArrayOfBand, Attribute.Layout.Element paramElement)
  {
    Band localBand = null;
    if (bandIndex != -1) {
      localBand = paramArrayOfBand[bandIndex];
    }
    Coding localCoding = UNSIGNED5;
    int i = 1;
    switch (kind)
    {
    case 1: 
      if (paramElement.flagTest((byte)1)) {
        localCoding = SIGNED5;
      } else if (len == 1) {
        localCoding = BYTE1;
      }
      break;
    case 2: 
      if (!paramElement.flagTest((byte)2)) {
        localCoding = BCI5;
      } else {
        localCoding = BRANCH5;
      }
      break;
    case 3: 
      localCoding = BRANCH5;
      break;
    case 4: 
      if (len == 1) {
        localCoding = BYTE1;
      }
      break;
    case 5: 
      if (len == 1) {
        localCoding = BYTE1;
      }
      assertBandOKForElems(paramArrayOfBand, body);
      break;
    case 7: 
      if (paramElement.flagTest((byte)1)) {
        localCoding = SIGNED5;
      } else if (len == 1) {
        localCoding = BYTE1;
      }
      assertBandOKForElems(paramArrayOfBand, body);
      break;
    case 8: 
      assert (localBand == null);
      assertBandOKForElems(paramArrayOfBand, body);
      return true;
    case 9: 
      assert (localBand == null);
      return true;
    case 10: 
      assert (localBand == null);
      assertBandOKForElems(paramArrayOfBand, body);
      return true;
    case 6: 
      i = 0;
      assert ((localBand instanceof CPRefBand));
      if ((!$assertionsDisabled) && (nullOK != paramElement.flagTest((byte)4))) {
        throw new AssertionError();
      }
      break;
    default: 
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      break;
    }
    assert (regularCoding == localCoding) : (paramElement + " // " + localBand);
    if ((i != 0) && (!$assertionsDisabled) && (!(localBand instanceof IntBand))) {
      throw new AssertionError();
    }
    return true;
  }
  
  private Attribute.Layout predefineAttribute(int paramInt1, int paramInt2, Band[] paramArrayOfBand, String paramString1, String paramString2)
  {
    Attribute.Layout localLayout = Attribute.find(paramInt2, paramString1, paramString2).layout();
    if (paramInt1 >= 0) {
      setAttributeLayoutIndex(localLayout, paramInt1);
    }
    if (paramArrayOfBand == null) {
      paramArrayOfBand = new Band[0];
    }
    assert (attrBandTable.get(localLayout) == null);
    attrBandTable.put(localLayout, paramArrayOfBand);
    assert (bandCount == paramArrayOfBand.length) : (localLayout + " // " + Arrays.asList(paramArrayOfBand));
    assert (assertBandOKForElems(paramArrayOfBand, elems));
    return localLayout;
  }
  
  private Attribute.Layout predefineAttribute(int paramInt, String paramString, MultiBand paramMultiBand, Attribute paramAttribute)
  {
    Attribute.Layout localLayout = paramAttribute.layout();
    int i = localLayout.ctype();
    return predefineAttribute(paramInt, i, makeNewAttributeBands(paramString, localLayout, paramMultiBand), localLayout.name(), localLayout.layout());
  }
  
  private void undefineAttribute(int paramInt1, int paramInt2)
  {
    if (verbose > 1) {
      System.out.println("Removing predefined " + Constants.ATTR_CONTEXT_NAME[paramInt2] + " attribute on bit " + paramInt1);
    }
    List localList = (List)attrDefs.get(paramInt2);
    Attribute.Layout localLayout = (Attribute.Layout)localList.get(paramInt1);
    assert (localLayout != null);
    localList.set(paramInt1, null);
    attrIndexTable.put(localLayout, null);
    assert (paramInt1 < 64);
    attrDefSeen[paramInt2] &= (1L << paramInt1 ^ 0xFFFFFFFFFFFFFFFF);
    attrFlagMask[paramInt2] &= (1L << paramInt1 ^ 0xFFFFFFFFFFFFFFFF);
    Band[] arrayOfBand = (Band[])attrBandTable.get(localLayout);
    for (int i = 0; i < arrayOfBand.length; i++) {
      arrayOfBand[i].doneWithUnusedBand();
    }
  }
  
  void makeNewAttributeBands()
  {
    adjustSpecialAttrMasks();
    for (int i = 0; i < 4; i++)
    {
      String str1 = Constants.ATTR_CONTEXT_NAME[i];
      MultiBand localMultiBand = attrBands[i];
      long l = attrDefSeen[i];
      assert ((l & (attrFlagMask[i] ^ 0xFFFFFFFFFFFFFFFF)) == 0L);
      for (int j = 0; j < ((List)attrDefs.get(i)).size(); j++)
      {
        Attribute.Layout localLayout = (Attribute.Layout)((List)attrDefs.get(i)).get(j);
        if ((localLayout != null) && (bandCount != 0)) {
          if ((j < attrIndexLimit[i]) && (!testBit(l, 1L << j)))
          {
            if ((!$assertionsDisabled) && (attrBandTable.get(localLayout) == null)) {
              throw new AssertionError();
            }
          }
          else
          {
            int k = localMultiBand.size();
            String str2 = str1 + "_" + localLayout.name() + "_";
            if (verbose > 1) {
              Utils.log.fine("Making new bands for " + localLayout);
            }
            Band[] arrayOfBand1 = makeNewAttributeBands(str2, localLayout, localMultiBand);
            assert (arrayOfBand1.length == bandCount);
            Band[] arrayOfBand2 = (Band[])attrBandTable.put(localLayout, arrayOfBand1);
            if (arrayOfBand2 != null) {
              for (int m = 0; m < arrayOfBand2.length; m++) {
                arrayOfBand2[m].doneWithUnusedBand();
              }
            }
          }
        }
      }
    }
  }
  
  private Band[] makeNewAttributeBands(String paramString, Attribute.Layout paramLayout, MultiBand paramMultiBand)
  {
    int i = paramMultiBand.size();
    makeNewAttributeBands(paramString, elems, paramMultiBand);
    int j = paramMultiBand.size() - i;
    Band[] arrayOfBand = new Band[j];
    for (int k = 0; k < j; k++) {
      arrayOfBand[k] = paramMultiBand.get(i + k);
    }
    return arrayOfBand;
  }
  
  private void makeNewAttributeBands(String paramString, Attribute.Layout.Element[] paramArrayOfElement, MultiBand paramMultiBand)
  {
    for (int i = 0; i < paramArrayOfElement.length; i++)
    {
      Attribute.Layout.Element localElement = paramArrayOfElement[i];
      String str = paramString + paramMultiBand.size() + "_" + layout;
      int j;
      if ((j = str.indexOf('[')) > 0) {
        str = str.substring(0, j);
      }
      if ((j = str.indexOf('(')) > 0) {
        str = str.substring(0, j);
      }
      if (str.endsWith("H")) {
        str = str.substring(0, str.length() - 1);
      }
      Object localObject;
      switch (kind)
      {
      case 1: 
        localObject = newElemBand(localElement, str, paramMultiBand);
        break;
      case 2: 
        if (!localElement.flagTest((byte)2)) {
          localObject = paramMultiBand.newIntBand(str, BCI5);
        } else {
          localObject = paramMultiBand.newIntBand(str, BRANCH5);
        }
        break;
      case 3: 
        localObject = paramMultiBand.newIntBand(str, BRANCH5);
        break;
      case 4: 
        assert (!localElement.flagTest((byte)1));
        localObject = newElemBand(localElement, str, paramMultiBand);
        break;
      case 5: 
        assert (!localElement.flagTest((byte)1));
        localObject = newElemBand(localElement, str, paramMultiBand);
        makeNewAttributeBands(paramString, body, paramMultiBand);
        break;
      case 7: 
        localObject = newElemBand(localElement, str, paramMultiBand);
        makeNewAttributeBands(paramString, body, paramMultiBand);
        break;
      case 8: 
        if (localElement.flagTest((byte)8)) {
          continue;
        }
        makeNewAttributeBands(paramString, body, paramMultiBand);
        break;
      case 6: 
        byte b = refKind;
        boolean bool = localElement.flagTest((byte)4);
        localObject = paramMultiBand.newCPRefBand(str, UNSIGNED5, b, bool);
        break;
      case 9: 
        break;
      case 10: 
        makeNewAttributeBands(paramString, body, paramMultiBand);
        break;
      default: 
        if ($assertionsDisabled) {
          continue;
        }
        throw new AssertionError();
      }
      if (verbose > 1) {
        Utils.log.fine("New attribute band " + localObject);
      }
    }
  }
  
  private Band newElemBand(Attribute.Layout.Element paramElement, String paramString, MultiBand paramMultiBand)
  {
    if (paramElement.flagTest((byte)1)) {
      return paramMultiBand.newIntBand(paramString, SIGNED5);
    }
    if (len == 1) {
      return paramMultiBand.newIntBand(paramString, BYTE1);
    }
    return paramMultiBand.newIntBand(paramString, UNSIGNED5);
  }
  
  protected int setAttributeLayoutIndex(Attribute.Layout paramLayout, int paramInt)
  {
    int i = ctype;
    assert ((-1 <= paramInt) && (paramInt < attrIndexLimit[i]));
    List localList = (List)attrDefs.get(i);
    if (paramInt == -1)
    {
      paramInt = localList.size();
      localList.add(paramLayout);
      if (verbose > 0) {
        Utils.log.info("Adding new attribute at " + paramLayout + ": " + paramInt);
      }
      attrIndexTable.put(paramLayout, Integer.valueOf(paramInt));
      return paramInt;
    }
    if (testBit(attrDefSeen[i], 1L << paramInt)) {
      throw new RuntimeException("Multiple explicit definition at " + paramInt + ": " + paramLayout);
    }
    attrDefSeen[i] |= 1L << paramInt;
    assert ((0 <= paramInt) && (paramInt < attrIndexLimit[i]));
    if (verbose > (attrClassFileVersionMask == 0 ? 2 : 0)) {
      Utils.log.fine("Fixing new attribute at " + paramInt + ": " + paramLayout + (localList.get(paramInt) == null ? "" : new StringBuilder().append("; replacing ").append(localList.get(paramInt)).toString()));
    }
    attrFlagMask[i] |= 1L << paramInt;
    attrIndexTable.put(localList.get(paramInt), null);
    localList.set(paramInt, paramLayout);
    attrIndexTable.put(paramLayout, Integer.valueOf(paramInt));
    return paramInt;
  }
  
  static int shortCodeHeader(Code paramCode)
  {
    int i = max_stack;
    int j = max_locals;
    int k = handler_class.length;
    if (k >= shortCodeLimits.length) {
      return 0;
    }
    int m = paramCode.getMethod().getArgumentSize();
    assert (j >= m);
    if (j < m) {
      return 0;
    }
    int n = j - m;
    int i1 = shortCodeLimits[k][0];
    int i2 = shortCodeLimits[k][1];
    if ((i >= i1) || (n >= i2)) {
      return 0;
    }
    int i3 = shortCodeHeader_h_base(k);
    i3 += i + i1 * n;
    if (i3 > 255) {
      return 0;
    }
    assert (shortCodeHeader_max_stack(i3) == i);
    assert (shortCodeHeader_max_na_locals(i3) == n);
    assert (shortCodeHeader_handler_count(i3) == k);
    return i3;
  }
  
  static int shortCodeHeader_handler_count(int paramInt)
  {
    assert ((paramInt > 0) && (paramInt <= 255));
    for (int i = 0;; i++) {
      if (paramInt < shortCodeHeader_h_base(i + 1)) {
        return i;
      }
    }
  }
  
  static int shortCodeHeader_max_stack(int paramInt)
  {
    int i = shortCodeHeader_handler_count(paramInt);
    int j = shortCodeLimits[i][0];
    return (paramInt - shortCodeHeader_h_base(i)) % j;
  }
  
  static int shortCodeHeader_max_na_locals(int paramInt)
  {
    int i = shortCodeHeader_handler_count(paramInt);
    int j = shortCodeLimits[i][0];
    return (paramInt - shortCodeHeader_h_base(i)) / j;
  }
  
  private static int shortCodeHeader_h_base(int paramInt)
  {
    assert (paramInt <= shortCodeLimits.length);
    int i = 1;
    for (int j = 0; j < paramInt; j++)
    {
      int k = shortCodeLimits[j][0];
      int m = shortCodeLimits[j][1];
      i += k * m;
    }
    return i;
  }
  
  protected void putLabel(IntBand paramIntBand, Code paramCode, int paramInt1, int paramInt2)
  {
    paramIntBand.putInt(paramCode.encodeBCI(paramInt2) - paramCode.encodeBCI(paramInt1));
  }
  
  protected int getLabel(IntBand paramIntBand, Code paramCode, int paramInt)
  {
    return paramCode.decodeBCI(paramIntBand.getInt() + paramCode.encodeBCI(paramInt));
  }
  
  protected CPRefBand getCPRefOpBand(int paramInt)
  {
    switch (Instruction.getCPRefOpTag(paramInt))
    {
    case 7: 
      return bc_classref;
    case 9: 
      return bc_fieldref;
    case 10: 
      return bc_methodref;
    case 11: 
      return bc_imethodref;
    case 18: 
      return bc_indyref;
    case 51: 
      switch (paramInt)
      {
      case 234: 
      case 237: 
        return bc_intref;
      case 235: 
      case 238: 
        return bc_floatref;
      case 20: 
        return bc_longref;
      case 239: 
        return bc_doubleref;
      case 18: 
      case 19: 
        return bc_stringref;
      case 233: 
      case 236: 
        return bc_classref;
      case 240: 
      case 241: 
        return bc_loadablevalueref;
      }
      break;
    }
    if (!$assertionsDisabled) {
      throw new AssertionError();
    }
    return null;
  }
  
  protected CPRefBand selfOpRefBand(int paramInt)
  {
    assert (Instruction.isSelfLinkerOp(paramInt));
    int i = paramInt - 202;
    int j = i >= 14 ? 1 : 0;
    if (j != 0) {
      i -= 14;
    }
    int k = i >= 7 ? 1 : 0;
    if (k != 0) {
      i -= 7;
    }
    int m = 178 + i;
    boolean bool = Instruction.isFieldOp(m);
    if (j == 0) {
      return bool ? bc_thisfield : bc_thismethod;
    }
    return bool ? bc_superfield : bc_supermethod;
  }
  
  static OutputStream getDumpStream(Band paramBand, String paramString)
    throws IOException
  {
    return getDumpStream(name, seqForDebug, paramString, paramBand);
  }
  
  static OutputStream getDumpStream(ConstantPool.Index paramIndex, String paramString)
    throws IOException
  {
    if (paramIndex.size() == 0) {
      return new ByteArrayOutputStream();
    }
    int i = ConstantPool.TAG_ORDER[cpMap[0].tag];
    return getDumpStream(debugName, i, paramString, paramIndex);
  }
  
  static OutputStream getDumpStream(String paramString1, int paramInt, String paramString2, Object paramObject)
    throws IOException
  {
    if (dumpDir == null)
    {
      dumpDir = File.createTempFile("BD_", "", new File("."));
      dumpDir.delete();
      if (dumpDir.mkdir()) {
        Utils.log.info("Dumping bands to " + dumpDir);
      }
    }
    paramString1 = paramString1.replace('(', ' ').replace(')', ' ');
    paramString1 = paramString1.replace('/', ' ');
    paramString1 = paramString1.replace('*', ' ');
    paramString1 = paramString1.trim().replace(' ', '_');
    paramString1 = (10000 + paramInt + "_" + paramString1).substring(1);
    File localFile = new File(dumpDir, paramString1 + paramString2);
    Utils.log.info("Dumping " + paramObject + " to " + localFile);
    return new BufferedOutputStream(new FileOutputStream(localFile));
  }
  
  static boolean assertCanChangeLength(Band paramBand)
  {
    switch (phase)
    {
    case 1: 
    case 4: 
      return true;
    }
    return false;
  }
  
  static boolean assertPhase(Band paramBand, int paramInt)
  {
    if (paramBand.phase() != paramInt)
    {
      Utils.log.warning("phase expected " + paramInt + " was " + paramBand.phase() + " in " + paramBand);
      return false;
    }
    return true;
  }
  
  static int verbose()
  {
    return Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose");
  }
  
  static boolean assertPhaseChangeOK(Band paramBand, int paramInt1, int paramInt2)
  {
    switch (paramInt1 * 10 + paramInt2)
    {
    case 1: 
      assert (!paramBand.isReader());
      assert (paramBand.capacity() >= 0);
      assert (paramBand.length() == 0);
      return true;
    case 13: 
    case 33: 
      assert (paramBand.length() == 0);
      return true;
    case 15: 
    case 35: 
      return true;
    case 58: 
      return true;
    case 2: 
      assert (paramBand.isReader());
      assert (paramBand.capacity() < 0);
      return true;
    case 24: 
      assert (Math.max(0, paramBand.capacity()) >= paramBand.valuesExpected());
      assert (paramBand.length() <= 0);
      return true;
    case 46: 
      assert (paramBand.valuesRemainingForDebug() == paramBand.length());
      return true;
    case 68: 
      assert (assertDoneDisbursing(paramBand));
      return true;
    }
    if (paramInt1 == paramInt2) {
      Utils.log.warning("Already in phase " + paramInt1);
    } else {
      Utils.log.warning("Unexpected phase " + paramInt1 + " -> " + paramInt2);
    }
    return false;
  }
  
  private static boolean assertDoneDisbursing(Band paramBand)
  {
    if (phase != 6)
    {
      Utils.log.warning("assertDoneDisbursing: still in phase " + phase + ": " + paramBand);
      if (verbose() <= 1) {
        return false;
      }
    }
    int i = paramBand.valuesRemainingForDebug();
    if (i > 0)
    {
      Utils.log.warning("assertDoneDisbursing: " + i + " values left in " + paramBand);
      if (verbose() <= 1) {
        return false;
      }
    }
    if ((paramBand instanceof MultiBand))
    {
      MultiBand localMultiBand = (MultiBand)paramBand;
      for (int j = 0; j < bandCount; j++)
      {
        Band localBand = bands[j];
        if (phase != 8)
        {
          Utils.log.warning("assertDoneDisbursing: sub-band still in phase " + phase + ": " + localBand);
          if (verbose() <= 1) {
            return false;
          }
        }
      }
    }
    return true;
  }
  
  private static void printCDecl(Band paramBand)
  {
    if ((paramBand instanceof MultiBand))
    {
      localObject1 = (MultiBand)paramBand;
      for (int i = 0; i < bandCount; i++) {
        printCDecl(bands[i]);
      }
      return;
    }
    Object localObject1 = "NULL";
    if ((paramBand instanceof CPRefBand))
    {
      localObject2 = index;
      if (localObject2 != null) {
        localObject1 = "INDEX(" + debugName + ")";
      }
    }
    Object localObject2 = { BYTE1, CHAR3, BCI5, BRANCH5, UNSIGNED5, UDELTA5, SIGNED5, DELTA5, MDELTA5 };
    String[] arrayOfString = { "BYTE1", "CHAR3", "BCI5", "BRANCH5", "UNSIGNED5", "UDELTA5", "SIGNED5", "DELTA5", "MDELTA5" };
    Coding localCoding = regularCoding;
    int j = Arrays.asList((Object[])localObject2).indexOf(localCoding);
    String str;
    if (j >= 0) {
      str = arrayOfString[j];
    } else {
      str = "CODING" + localCoding.keyString();
    }
    System.out.println("  BAND_INIT(\"" + paramBand.name() + "\", " + str + ", " + (String)localObject1 + "),");
  }
  
  boolean notePrevForAssert(Band paramBand1, Band paramBand2)
  {
    if (prevForAssertMap == null) {
      prevForAssertMap = new HashMap();
    }
    prevForAssertMap.put(paramBand1, paramBand2);
    return true;
  }
  
  private boolean assertReadyToReadFrom(Band paramBand, InputStream paramInputStream)
    throws IOException
  {
    Band localBand = (Band)prevForAssertMap.get(paramBand);
    if ((localBand != null) && (phaseCmp(localBand.phase(), 6) < 0))
    {
      Utils.log.warning("Previous band not done reading.");
      Utils.log.info("    Previous band: " + localBand);
      Utils.log.info("        Next band: " + paramBand);
      assert (verbose > 0);
    }
    String str1 = name;
    if ((optDebugBands) && (!str1.startsWith("(")))
    {
      assert (bandSequenceList != null);
      String str2 = (String)bandSequenceList.removeFirst();
      if (!str2.equals(str1))
      {
        Utils.log.warning("Expected " + str1 + " but read: " + str2);
        return false;
      }
      Utils.log.info("Read band in sequence: " + str1);
    }
    return true;
  }
  
  private boolean assertValidCPRefs(CPRefBand paramCPRefBand)
  {
    if (index == null) {
      return true;
    }
    int i = index.size() + 1;
    for (int j = 0; j < paramCPRefBand.length(); j++)
    {
      int k = paramCPRefBand.valueAtForDebug(j);
      if ((k < 0) || (k >= i))
      {
        Utils.log.warning("CP ref out of range [" + j + "] = " + k + " in " + paramCPRefBand);
        return false;
      }
    }
    return true;
  }
  
  private boolean assertReadyToWriteTo(Band paramBand, OutputStream paramOutputStream)
    throws IOException
  {
    Band localBand = (Band)prevForAssertMap.get(paramBand);
    if ((localBand != null) && (phaseCmp(localBand.phase(), 8) < 0))
    {
      Utils.log.warning("Previous band not done writing.");
      Utils.log.info("    Previous band: " + localBand);
      Utils.log.info("        Next band: " + paramBand);
      assert (verbose > 0);
    }
    String str = name;
    if ((optDebugBands) && (!str.startsWith("(")))
    {
      if (bandSequenceList == null) {
        bandSequenceList = new LinkedList();
      }
      bandSequenceList.add(str);
    }
    return true;
  }
  
  protected static boolean testBit(int paramInt1, int paramInt2)
  {
    return (paramInt1 & paramInt2) != 0;
  }
  
  protected static int setBit(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return paramBoolean ? paramInt1 | paramInt2 : paramInt1 & (paramInt2 ^ 0xFFFFFFFF);
  }
  
  protected static boolean testBit(long paramLong1, long paramLong2)
  {
    return (paramLong1 & paramLong2) != 0L;
  }
  
  protected static long setBit(long paramLong1, long paramLong2, boolean paramBoolean)
  {
    return paramBoolean ? paramLong1 | paramLong2 : paramLong1 & (paramLong2 ^ 0xFFFFFFFFFFFFFFFF);
  }
  
  static void printArrayTo(PrintStream paramPrintStream, int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = paramInt2 - paramInt1;
    for (int j = 0; j < i; j++)
    {
      if (j % 10 == 0) {
        paramPrintStream.println();
      } else {
        paramPrintStream.print(" ");
      }
      paramPrintStream.print(paramArrayOfInt[(paramInt1 + j)]);
    }
    paramPrintStream.println();
  }
  
  static void printArrayTo(PrintStream paramPrintStream, ConstantPool.Entry[] paramArrayOfEntry, int paramInt1, int paramInt2)
  {
    printArrayTo(paramPrintStream, paramArrayOfEntry, paramInt1, paramInt2, false);
  }
  
  static void printArrayTo(PrintStream paramPrintStream, ConstantPool.Entry[] paramArrayOfEntry, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = paramInt2 - paramInt1;
    for (int j = 0; j < i; j++)
    {
      ConstantPool.Entry localEntry = paramArrayOfEntry[(paramInt1 + j)];
      paramPrintStream.print(paramInt1 + j);
      paramPrintStream.print("=");
      if (paramBoolean)
      {
        paramPrintStream.print(tag);
        paramPrintStream.print(":");
      }
      String str1 = localEntry.stringValue();
      localStringBuffer.setLength(0);
      for (int k = 0; k < str1.length(); k++)
      {
        char c = str1.charAt(k);
        if ((c >= ' ') && (c <= '~') && (c != '\\'))
        {
          localStringBuffer.append(c);
        }
        else if (c == '\\')
        {
          localStringBuffer.append("\\\\");
        }
        else if (c == '\n')
        {
          localStringBuffer.append("\\n");
        }
        else if (c == '\t')
        {
          localStringBuffer.append("\\t");
        }
        else if (c == '\r')
        {
          localStringBuffer.append("\\r");
        }
        else
        {
          String str2 = "000" + Integer.toHexString(c);
          localStringBuffer.append("\\u").append(str2.substring(str2.length() - 4));
        }
      }
      paramPrintStream.println(localStringBuffer);
    }
  }
  
  protected static Object[] realloc(Object[] paramArrayOfObject, int paramInt)
  {
    Class localClass = paramArrayOfObject.getClass().getComponentType();
    Object[] arrayOfObject = (Object[])Array.newInstance(localClass, paramInt);
    System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 0, Math.min(paramArrayOfObject.length, paramInt));
    return arrayOfObject;
  }
  
  protected static Object[] realloc(Object[] paramArrayOfObject)
  {
    return realloc(paramArrayOfObject, Math.max(10, paramArrayOfObject.length * 2));
  }
  
  protected static int[] realloc(int[] paramArrayOfInt, int paramInt)
  {
    if (paramInt == 0) {
      return Constants.noInts;
    }
    if (paramArrayOfInt == null) {
      return new int[paramInt];
    }
    int[] arrayOfInt = new int[paramInt];
    System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, Math.min(paramArrayOfInt.length, paramInt));
    return arrayOfInt;
  }
  
  protected static int[] realloc(int[] paramArrayOfInt)
  {
    return realloc(paramArrayOfInt, Math.max(10, paramArrayOfInt.length * 2));
  }
  
  protected static byte[] realloc(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramInt == 0) {
      return Constants.noBytes;
    }
    if (paramArrayOfByte == null) {
      return new byte[paramInt];
    }
    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, Math.min(paramArrayOfByte.length, paramInt));
    return arrayOfByte;
  }
  
  protected static byte[] realloc(byte[] paramArrayOfByte)
  {
    return realloc(paramArrayOfByte, Math.max(10, paramArrayOfByte.length * 2));
  }
  
  static
  {
    BYTE1 = Coding.of(1, 256);
    CHAR3 = Coding.of(3, 128);
    BCI5 = Coding.of(5, 4);
    BRANCH5 = Coding.of(5, 4, 2);
    UNSIGNED5 = Coding.of(5, 64);
    UDELTA5 = UNSIGNED5.getDeltaCoding();
    SIGNED5 = Coding.of(5, 64, 1);
    DELTA5 = SIGNED5.getDeltaCoding();
    MDELTA5 = Coding.of(5, 64, 2).getDeltaCoding();
    basicCodings = new Coding[] { null, Coding.of(1, 256, 0), Coding.of(1, 256, 1), Coding.of(1, 256, 0).getDeltaCoding(), Coding.of(1, 256, 1).getDeltaCoding(), Coding.of(2, 256, 0), Coding.of(2, 256, 1), Coding.of(2, 256, 0).getDeltaCoding(), Coding.of(2, 256, 1).getDeltaCoding(), Coding.of(3, 256, 0), Coding.of(3, 256, 1), Coding.of(3, 256, 0).getDeltaCoding(), Coding.of(3, 256, 1).getDeltaCoding(), Coding.of(4, 256, 0), Coding.of(4, 256, 1), Coding.of(4, 256, 0).getDeltaCoding(), Coding.of(4, 256, 1).getDeltaCoding(), Coding.of(5, 4, 0), Coding.of(5, 4, 1), Coding.of(5, 4, 2), Coding.of(5, 16, 0), Coding.of(5, 16, 1), Coding.of(5, 16, 2), Coding.of(5, 32, 0), Coding.of(5, 32, 1), Coding.of(5, 32, 2), Coding.of(5, 64, 0), Coding.of(5, 64, 1), Coding.of(5, 64, 2), Coding.of(5, 128, 0), Coding.of(5, 128, 1), Coding.of(5, 128, 2), Coding.of(5, 4, 0).getDeltaCoding(), Coding.of(5, 4, 1).getDeltaCoding(), Coding.of(5, 4, 2).getDeltaCoding(), Coding.of(5, 16, 0).getDeltaCoding(), Coding.of(5, 16, 1).getDeltaCoding(), Coding.of(5, 16, 2).getDeltaCoding(), Coding.of(5, 32, 0).getDeltaCoding(), Coding.of(5, 32, 1).getDeltaCoding(), Coding.of(5, 32, 2).getDeltaCoding(), Coding.of(5, 64, 0).getDeltaCoding(), Coding.of(5, 64, 1).getDeltaCoding(), Coding.of(5, 64, 2).getDeltaCoding(), Coding.of(5, 128, 0).getDeltaCoding(), Coding.of(5, 128, 1).getDeltaCoding(), Coding.of(5, 128, 2).getDeltaCoding(), Coding.of(2, 192, 0), Coding.of(2, 224, 0), Coding.of(2, 240, 0), Coding.of(2, 248, 0), Coding.of(2, 252, 0), Coding.of(2, 8, 0).getDeltaCoding(), Coding.of(2, 8, 1).getDeltaCoding(), Coding.of(2, 16, 0).getDeltaCoding(), Coding.of(2, 16, 1).getDeltaCoding(), Coding.of(2, 32, 0).getDeltaCoding(), Coding.of(2, 32, 1).getDeltaCoding(), Coding.of(2, 64, 0).getDeltaCoding(), Coding.of(2, 64, 1).getDeltaCoding(), Coding.of(2, 128, 0).getDeltaCoding(), Coding.of(2, 128, 1).getDeltaCoding(), Coding.of(2, 192, 0).getDeltaCoding(), Coding.of(2, 192, 1).getDeltaCoding(), Coding.of(2, 224, 0).getDeltaCoding(), Coding.of(2, 224, 1).getDeltaCoding(), Coding.of(2, 240, 0).getDeltaCoding(), Coding.of(2, 240, 1).getDeltaCoding(), Coding.of(2, 248, 0).getDeltaCoding(), Coding.of(2, 248, 1).getDeltaCoding(), Coding.of(3, 192, 0), Coding.of(3, 224, 0), Coding.of(3, 240, 0), Coding.of(3, 248, 0), Coding.of(3, 252, 0), Coding.of(3, 8, 0).getDeltaCoding(), Coding.of(3, 8, 1).getDeltaCoding(), Coding.of(3, 16, 0).getDeltaCoding(), Coding.of(3, 16, 1).getDeltaCoding(), Coding.of(3, 32, 0).getDeltaCoding(), Coding.of(3, 32, 1).getDeltaCoding(), Coding.of(3, 64, 0).getDeltaCoding(), Coding.of(3, 64, 1).getDeltaCoding(), Coding.of(3, 128, 0).getDeltaCoding(), Coding.of(3, 128, 1).getDeltaCoding(), Coding.of(3, 192, 0).getDeltaCoding(), Coding.of(3, 192, 1).getDeltaCoding(), Coding.of(3, 224, 0).getDeltaCoding(), Coding.of(3, 224, 1).getDeltaCoding(), Coding.of(3, 240, 0).getDeltaCoding(), Coding.of(3, 240, 1).getDeltaCoding(), Coding.of(3, 248, 0).getDeltaCoding(), Coding.of(3, 248, 1).getDeltaCoding(), Coding.of(4, 192, 0), Coding.of(4, 224, 0), Coding.of(4, 240, 0), Coding.of(4, 248, 0), Coding.of(4, 252, 0), Coding.of(4, 8, 0).getDeltaCoding(), Coding.of(4, 8, 1).getDeltaCoding(), Coding.of(4, 16, 0).getDeltaCoding(), Coding.of(4, 16, 1).getDeltaCoding(), Coding.of(4, 32, 0).getDeltaCoding(), Coding.of(4, 32, 1).getDeltaCoding(), Coding.of(4, 64, 0).getDeltaCoding(), Coding.of(4, 64, 1).getDeltaCoding(), Coding.of(4, 128, 0).getDeltaCoding(), Coding.of(4, 128, 1).getDeltaCoding(), Coding.of(4, 192, 0).getDeltaCoding(), Coding.of(4, 192, 1).getDeltaCoding(), Coding.of(4, 224, 0).getDeltaCoding(), Coding.of(4, 224, 1).getDeltaCoding(), Coding.of(4, 240, 0).getDeltaCoding(), Coding.of(4, 240, 1).getDeltaCoding(), Coding.of(4, 248, 0).getDeltaCoding(), Coding.of(4, 248, 1).getDeltaCoding(), null };
    assert (basicCodings[0] == null);
    assert (basicCodings[1] != null);
    assert (basicCodings[115] != null);
    HashMap localHashMap = new HashMap();
    Coding localCoding;
    for (int j = 0; j < basicCodings.length; j++)
    {
      localCoding = basicCodings[j];
      if (localCoding != null)
      {
        assert (j >= 1);
        assert (j <= 115);
        localHashMap.put(localCoding, Integer.valueOf(j));
      }
    }
    basicCodingIndexes = localHashMap;
    defaultMetaCoding = new byte[] { 0 };
    noMetaCoding = new byte[0];
    int i = 0;
    assert ((i = 1) != 0);
    if (i != 0) {
      for (j = 0; j < basicCodings.length; j++)
      {
        localCoding = basicCodings[j];
        if ((localCoding != null) && (localCoding.B() != 1) && (localCoding.L() != 0)) {
          for (int k = 0; k <= 255; k++) {
            encodeEscapeValue(k, localCoding);
          }
        }
      }
    }
  }
  
  abstract class Band
  {
    private int phase = 0;
    private final String name;
    private int valuesExpected;
    protected long outputSize = -1L;
    public final Coding regularCoding;
    public final int seqForDebug;
    public int elementCountForDebug;
    protected int lengthForDebug = -1;
    
    protected Band(String paramString, Coding paramCoding)
    {
      name = paramString;
      regularCoding = paramCoding;
      seqForDebug = (++BandStructure.nextSeqForDebug);
      if (verbose > 2) {
        Utils.log.fine("Band " + seqForDebug + " is " + paramString);
      }
    }
    
    public Band init()
    {
      if (isReader) {
        readyToExpect();
      } else {
        readyToCollect();
      }
      return this;
    }
    
    boolean isReader()
    {
      return isReader;
    }
    
    int phase()
    {
      return phase;
    }
    
    String name()
    {
      return name;
    }
    
    public abstract int capacity();
    
    protected abstract void setCapacity(int paramInt);
    
    public abstract int length();
    
    protected abstract int valuesRemainingForDebug();
    
    public final int valuesExpected()
    {
      return valuesExpected;
    }
    
    public final void writeTo(OutputStream paramOutputStream)
      throws IOException
    {
      assert (BandStructure.this.assertReadyToWriteTo(this, paramOutputStream));
      setPhase(5);
      writeDataTo(paramOutputStream);
      doneWriting();
    }
    
    abstract void chooseBandCodings()
      throws IOException;
    
    public final long outputSize()
    {
      if (outputSize >= 0L)
      {
        long l = outputSize;
        assert (l == computeOutputSize());
        return l;
      }
      return computeOutputSize();
    }
    
    protected abstract long computeOutputSize();
    
    protected abstract void writeDataTo(OutputStream paramOutputStream)
      throws IOException;
    
    void expectLength(int paramInt)
    {
      assert (BandStructure.assertPhase(this, 2));
      assert (valuesExpected == 0);
      assert (paramInt >= 0);
      valuesExpected = paramInt;
    }
    
    void expectMoreLength(int paramInt)
    {
      assert (BandStructure.assertPhase(this, 2));
      valuesExpected += paramInt;
    }
    
    private void readyToCollect()
    {
      setCapacity(1);
      setPhase(1);
    }
    
    protected void doneWriting()
    {
      assert (BandStructure.assertPhase(this, 5));
      setPhase(8);
    }
    
    private void readyToExpect()
    {
      setPhase(2);
    }
    
    public final void readFrom(InputStream paramInputStream)
      throws IOException
    {
      assert (BandStructure.this.assertReadyToReadFrom(this, paramInputStream));
      setCapacity(valuesExpected());
      setPhase(4);
      readDataFrom(paramInputStream);
      readyToDisburse();
    }
    
    protected abstract void readDataFrom(InputStream paramInputStream)
      throws IOException;
    
    protected void readyToDisburse()
    {
      if (verbose > 1) {
        Utils.log.fine("readyToDisburse " + this);
      }
      setPhase(6);
    }
    
    public void doneDisbursing()
    {
      assert (BandStructure.assertPhase(this, 6));
      setPhase(8);
    }
    
    public final void doneWithUnusedBand()
    {
      if (isReader)
      {
        assert (BandStructure.assertPhase(this, 2));
        assert (valuesExpected() == 0);
        setPhase(4);
        setPhase(6);
        setPhase(8);
      }
      else
      {
        setPhase(3);
      }
    }
    
    protected void setPhase(int paramInt)
    {
      assert (BandStructure.assertPhaseChangeOK(this, phase, paramInt));
      phase = paramInt;
    }
    
    public String toString()
    {
      int i = lengthForDebug != -1 ? lengthForDebug : length();
      String str = name;
      if (i != 0) {
        str = str + "[" + i + "]";
      }
      if (elementCountForDebug != 0) {
        str = str + "(" + elementCountForDebug + ")";
      }
      return str;
    }
  }
  
  class ByteBand
    extends BandStructure.Band
  {
    private ByteArrayOutputStream bytes;
    private ByteArrayOutputStream bytesForDump;
    private InputStream in;
    
    public ByteBand(String paramString)
    {
      super(paramString, BandStructure.BYTE1);
    }
    
    public int capacity()
    {
      return bytes == null ? -1 : Integer.MAX_VALUE;
    }
    
    protected void setCapacity(int paramInt)
    {
      assert (bytes == null);
      bytes = new ByteArrayOutputStream(paramInt);
    }
    
    public void destroy()
    {
      lengthForDebug = length();
      bytes = null;
    }
    
    public int length()
    {
      return bytes == null ? -1 : bytes.size();
    }
    
    public void reset()
    {
      bytes.reset();
    }
    
    protected int valuesRemainingForDebug()
    {
      return bytes == null ? -1 : ((ByteArrayInputStream)in).available();
    }
    
    protected void chooseBandCodings()
      throws IOException
    {
      assert (BandStructure.decodeEscapeValue(regularCoding.min(), regularCoding) < 0);
      assert (BandStructure.decodeEscapeValue(regularCoding.max(), regularCoding) < 0);
    }
    
    protected long computeOutputSize()
    {
      return bytes.size();
    }
    
    public void writeDataTo(OutputStream paramOutputStream)
      throws IOException
    {
      if (length() == 0) {
        return;
      }
      bytes.writeTo(paramOutputStream);
      if (optDumpBands) {
        dumpBand();
      }
      destroy();
    }
    
    private void dumpBand()
      throws IOException
    {
      assert (optDumpBands);
      OutputStream localOutputStream = BandStructure.getDumpStream(this, ".bnd");
      Object localObject1 = null;
      try
      {
        if (bytesForDump != null) {
          bytesForDump.writeTo(localOutputStream);
        } else {
          bytes.writeTo(localOutputStream);
        }
      }
      catch (Throwable localThrowable2)
      {
        localObject1 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localOutputStream != null) {
          if (localObject1 != null) {
            try
            {
              localOutputStream.close();
            }
            catch (Throwable localThrowable3)
            {
              ((Throwable)localObject1).addSuppressed(localThrowable3);
            }
          } else {
            localOutputStream.close();
          }
        }
      }
    }
    
    public void readDataFrom(InputStream paramInputStream)
      throws IOException
    {
      int i = valuesExpected();
      if (i == 0) {
        return;
      }
      if (verbose > 1)
      {
        lengthForDebug = i;
        Utils.log.fine("Reading band " + this);
        lengthForDebug = -1;
      }
      byte[] arrayOfByte = new byte[Math.min(i, 16384)];
      while (i > 0)
      {
        int j = paramInputStream.read(arrayOfByte, 0, Math.min(i, arrayOfByte.length));
        if (j < 0) {
          throw new EOFException();
        }
        bytes.write(arrayOfByte, 0, j);
        i -= j;
      }
      if (optDumpBands) {
        dumpBand();
      }
    }
    
    public void readyToDisburse()
    {
      in = new ByteArrayInputStream(bytes.toByteArray());
      super.readyToDisburse();
    }
    
    public void doneDisbursing()
    {
      super.doneDisbursing();
      if ((optDumpBands) && (bytesForDump != null) && (bytesForDump.size() > 0)) {
        try
        {
          dumpBand();
        }
        catch (IOException localIOException)
        {
          throw new RuntimeException(localIOException);
        }
      }
      in = null;
      bytes = null;
      bytesForDump = null;
    }
    
    public void setInputStreamFrom(InputStream paramInputStream)
      throws IOException
    {
      assert (bytes == null);
      assert (BandStructure.this.assertReadyToReadFrom(this, paramInputStream));
      setPhase(4);
      in = paramInputStream;
      if (optDumpBands)
      {
        bytesForDump = new ByteArrayOutputStream();
        in = new FilterInputStream(paramInputStream)
        {
          public int read()
            throws IOException
          {
            int i = in.read();
            if (i >= 0) {
              bytesForDump.write(i);
            }
            return i;
          }
          
          public int read(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
            throws IOException
          {
            int i = in.read(paramAnonymousArrayOfByte, paramAnonymousInt1, paramAnonymousInt2);
            if (i >= 0) {
              bytesForDump.write(paramAnonymousArrayOfByte, paramAnonymousInt1, i);
            }
            return i;
          }
        };
      }
      super.readyToDisburse();
    }
    
    public OutputStream collectorStream()
    {
      assert (phase() == 1);
      assert (bytes != null);
      return bytes;
    }
    
    public InputStream getInputStream()
    {
      assert (phase() == 6);
      assert (in != null);
      return in;
    }
    
    public int getByte()
      throws IOException
    {
      int i = getInputStream().read();
      if (i < 0) {
        throw new EOFException();
      }
      return i;
    }
    
    public void putByte(int paramInt)
      throws IOException
    {
      assert (paramInt == (paramInt & 0xFF));
      collectorStream().write(paramInt);
    }
    
    public String toString()
    {
      return "byte " + super.toString();
    }
  }
  
  private static class ByteCounter
    extends FilterOutputStream
  {
    private long count;
    
    public ByteCounter(OutputStream paramOutputStream)
    {
      super();
    }
    
    public long getCount()
    {
      return count;
    }
    
    public void setCount(long paramLong)
    {
      count = paramLong;
    }
    
    public void write(int paramInt)
      throws IOException
    {
      count += 1L;
      if (out != null) {
        out.write(paramInt);
      }
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      count += paramInt2;
      if (out != null) {
        out.write(paramArrayOfByte, paramInt1, paramInt2);
      }
    }
    
    public String toString()
    {
      return String.valueOf(getCount());
    }
  }
  
  class CPRefBand
    extends BandStructure.ValueBand
  {
    ConstantPool.Index index;
    boolean nullOK;
    
    public CPRefBand(String paramString, Coding paramCoding, byte paramByte, boolean paramBoolean)
    {
      super(paramString, paramCoding);
      nullOK = paramBoolean;
      if (paramByte != 0) {
        setBandIndex(this, paramByte);
      }
    }
    
    public CPRefBand(String paramString, Coding paramCoding, byte paramByte)
    {
      this(paramString, paramCoding, paramByte, false);
    }
    
    public CPRefBand(String paramString, Coding paramCoding, Object paramObject)
    {
      this(paramString, paramCoding, (byte)0, false);
    }
    
    public void setIndex(ConstantPool.Index paramIndex)
    {
      index = paramIndex;
    }
    
    protected void readDataFrom(InputStream paramInputStream)
      throws IOException
    {
      super.readDataFrom(paramInputStream);
      assert (BandStructure.this.assertValidCPRefs(this));
    }
    
    public void putRef(ConstantPool.Entry paramEntry)
    {
      addValue(encodeRefOrNull(paramEntry, index));
    }
    
    public void putRef(ConstantPool.Entry paramEntry, ConstantPool.Index paramIndex)
    {
      assert (index == null);
      addValue(encodeRefOrNull(paramEntry, paramIndex));
    }
    
    public void putRef(ConstantPool.Entry paramEntry, byte paramByte)
    {
      putRef(paramEntry, getCPIndex(paramByte));
    }
    
    public ConstantPool.Entry getRef()
    {
      if (index == null) {
        Utils.log.warning("No index for " + this);
      }
      assert (index != null);
      return decodeRefOrNull(getValue(), index);
    }
    
    public ConstantPool.Entry getRef(ConstantPool.Index paramIndex)
    {
      assert (index == null);
      return decodeRefOrNull(getValue(), paramIndex);
    }
    
    public ConstantPool.Entry getRef(byte paramByte)
    {
      return getRef(getCPIndex(paramByte));
    }
    
    private int encodeRefOrNull(ConstantPool.Entry paramEntry, ConstantPool.Index paramIndex)
    {
      int i;
      if (paramEntry == null) {
        i = -1;
      } else {
        i = encodeRef(paramEntry, paramIndex);
      }
      return (nullOK ? 1 : 0) + i;
    }
    
    private ConstantPool.Entry decodeRefOrNull(int paramInt, ConstantPool.Index paramIndex)
    {
      int i = paramInt - (nullOK ? 1 : 0);
      if (i == -1) {
        return null;
      }
      return decodeRef(i, paramIndex);
    }
  }
  
  class IntBand
    extends BandStructure.ValueBand
  {
    public IntBand(String paramString, Coding paramCoding)
    {
      super(paramString, paramCoding);
    }
    
    public void putInt(int paramInt)
    {
      assert (phase() == 1);
      addValue(paramInt);
    }
    
    public int getInt()
    {
      return getValue();
    }
    
    public int getIntTotal()
    {
      assert (phase() == 6);
      assert (valuesRemainingForDebug() == length());
      int i = 0;
      for (int j = length(); j > 0; j--) {
        i += getInt();
      }
      resetForSecondPass();
      return i;
    }
    
    public int getIntCount(int paramInt)
    {
      assert (phase() == 6);
      assert (valuesRemainingForDebug() == length());
      int i = 0;
      for (int j = length(); j > 0; j--) {
        if (getInt() == paramInt) {
          i++;
        }
      }
      resetForSecondPass();
      return i;
    }
  }
  
  class MultiBand
    extends BandStructure.Band
  {
    BandStructure.Band[] bands = new BandStructure.Band[10];
    int bandCount = 0;
    private int cap = -1;
    
    MultiBand(String paramString, Coding paramCoding)
    {
      super(paramString, paramCoding);
    }
    
    public BandStructure.Band init()
    {
      super.init();
      setCapacity(0);
      if (phase() == 2)
      {
        setPhase(4);
        setPhase(6);
      }
      return this;
    }
    
    int size()
    {
      return bandCount;
    }
    
    BandStructure.Band get(int paramInt)
    {
      assert (paramInt < bandCount);
      return bands[paramInt];
    }
    
    BandStructure.Band[] toArray()
    {
      return (BandStructure.Band[])BandStructure.realloc(bands, bandCount);
    }
    
    void add(BandStructure.Band paramBand)
    {
      assert ((bandCount == 0) || (notePrevForAssert(paramBand, bands[(bandCount - 1)])));
      if (bandCount == bands.length) {
        bands = ((BandStructure.Band[])BandStructure.realloc(bands));
      }
      bands[(bandCount++)] = paramBand;
    }
    
    BandStructure.ByteBand newByteBand(String paramString)
    {
      BandStructure.ByteBand localByteBand = new BandStructure.ByteBand(BandStructure.this, paramString);
      localByteBand.init();
      add(localByteBand);
      return localByteBand;
    }
    
    BandStructure.IntBand newIntBand(String paramString)
    {
      BandStructure.IntBand localIntBand = new BandStructure.IntBand(BandStructure.this, paramString, regularCoding);
      localIntBand.init();
      add(localIntBand);
      return localIntBand;
    }
    
    BandStructure.IntBand newIntBand(String paramString, Coding paramCoding)
    {
      BandStructure.IntBand localIntBand = new BandStructure.IntBand(BandStructure.this, paramString, paramCoding);
      localIntBand.init();
      add(localIntBand);
      return localIntBand;
    }
    
    MultiBand newMultiBand(String paramString, Coding paramCoding)
    {
      MultiBand localMultiBand = new MultiBand(BandStructure.this, paramString, paramCoding);
      localMultiBand.init();
      add(localMultiBand);
      return localMultiBand;
    }
    
    BandStructure.CPRefBand newCPRefBand(String paramString, byte paramByte)
    {
      BandStructure.CPRefBand localCPRefBand = new BandStructure.CPRefBand(BandStructure.this, paramString, regularCoding, paramByte);
      localCPRefBand.init();
      add(localCPRefBand);
      return localCPRefBand;
    }
    
    BandStructure.CPRefBand newCPRefBand(String paramString, Coding paramCoding, byte paramByte)
    {
      BandStructure.CPRefBand localCPRefBand = new BandStructure.CPRefBand(BandStructure.this, paramString, paramCoding, paramByte);
      localCPRefBand.init();
      add(localCPRefBand);
      return localCPRefBand;
    }
    
    BandStructure.CPRefBand newCPRefBand(String paramString, Coding paramCoding, byte paramByte, boolean paramBoolean)
    {
      BandStructure.CPRefBand localCPRefBand = new BandStructure.CPRefBand(BandStructure.this, paramString, paramCoding, paramByte, paramBoolean);
      localCPRefBand.init();
      add(localCPRefBand);
      return localCPRefBand;
    }
    
    int bandCount()
    {
      return bandCount;
    }
    
    public int capacity()
    {
      return cap;
    }
    
    public void setCapacity(int paramInt)
    {
      cap = paramInt;
    }
    
    public int length()
    {
      return 0;
    }
    
    public int valuesRemainingForDebug()
    {
      return 0;
    }
    
    protected void chooseBandCodings()
      throws IOException
    {
      for (int i = 0; i < bandCount; i++)
      {
        BandStructure.Band localBand = bands[i];
        localBand.chooseBandCodings();
      }
    }
    
    protected long computeOutputSize()
    {
      long l1 = 0L;
      for (int i = 0; i < bandCount; i++)
      {
        BandStructure.Band localBand = bands[i];
        long l2 = localBand.outputSize();
        assert (l2 >= 0L) : localBand;
        l1 += l2;
      }
      return l1;
    }
    
    protected void writeDataTo(OutputStream paramOutputStream)
      throws IOException
    {
      long l1 = 0L;
      if (outputCounter != null) {
        l1 = outputCounter.getCount();
      }
      for (int i = 0; i < bandCount; i++)
      {
        BandStructure.Band localBand = bands[i];
        localBand.writeTo(paramOutputStream);
        if (outputCounter != null)
        {
          long l2 = outputCounter.getCount();
          long l3 = l2 - l1;
          l1 = l2;
          if (((verbose > 0) && (l3 > 0L)) || (verbose > 1)) {
            Utils.log.info("  ...wrote " + l3 + " bytes from " + localBand);
          }
        }
      }
    }
    
    protected void readDataFrom(InputStream paramInputStream)
      throws IOException
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      for (int i = 0; i < bandCount; i++)
      {
        BandStructure.Band localBand = bands[i];
        localBand.readFrom(paramInputStream);
        if (((verbose > 0) && (localBand.length() > 0)) || (verbose > 1)) {
          Utils.log.info("  ...read " + localBand);
        }
      }
    }
    
    public String toString()
    {
      return "{" + bandCount() + " bands: " + super.toString() + "}";
    }
  }
  
  class ValueBand
    extends BandStructure.Band
  {
    private int[] values;
    private int length;
    private int valuesDisbursed;
    private CodingMethod bandCoding;
    private byte[] metaCoding;
    
    protected ValueBand(String paramString, Coding paramCoding)
    {
      super(paramString, paramCoding);
    }
    
    public int capacity()
    {
      return values == null ? -1 : values.length;
    }
    
    protected void setCapacity(int paramInt)
    {
      assert (length <= paramInt);
      if (paramInt == -1)
      {
        values = null;
        return;
      }
      values = BandStructure.realloc(values, paramInt);
    }
    
    public int length()
    {
      return length;
    }
    
    protected int valuesRemainingForDebug()
    {
      return length - valuesDisbursed;
    }
    
    protected int valueAtForDebug(int paramInt)
    {
      return values[paramInt];
    }
    
    void patchValue(int paramInt1, int paramInt2)
    {
      assert (this == archive_header_S);
      assert ((paramInt1 == 0) || (paramInt1 == 1));
      assert (paramInt1 < length);
      values[paramInt1] = paramInt2;
      outputSize = -1L;
    }
    
    protected void initializeValues(int[] paramArrayOfInt)
    {
      assert (BandStructure.assertCanChangeLength(this));
      assert (length == 0);
      values = paramArrayOfInt;
      length = paramArrayOfInt.length;
    }
    
    protected void addValue(int paramInt)
    {
      assert (BandStructure.assertCanChangeLength(this));
      if (length == values.length) {
        setCapacity(length < 1000 ? length * 10 : length * 2);
      }
      values[(length++)] = paramInt;
    }
    
    private boolean canVaryCoding()
    {
      if (!optVaryCodings) {
        return false;
      }
      if (length == 0) {
        return false;
      }
      if (this == archive_header_0) {
        return false;
      }
      if (this == archive_header_S) {
        return false;
      }
      if (this == archive_header_1) {
        return false;
      }
      return (regularCoding.min() <= 65280) || (regularCoding.max() >= 256);
    }
    
    private boolean shouldVaryCoding()
    {
      assert (canVaryCoding());
      return (effort >= 9) || (length >= 100);
    }
    
    protected void chooseBandCodings()
      throws IOException
    {
      boolean bool = canVaryCoding();
      Object localObject;
      if ((!bool) || (!shouldVaryCoding()))
      {
        if (regularCoding.canRepresent(values, 0, length))
        {
          bandCoding = regularCoding;
        }
        else
        {
          assert (bool);
          if (verbose > 1) {
            Utils.log.fine("regular coding fails in band " + name());
          }
          bandCoding = BandStructure.UNSIGNED5;
        }
        outputSize = -1L;
      }
      else
      {
        localObject = new int[] { 0, 0 };
        bandCoding = chooseCoding(values, 0, length, regularCoding, name(), (int[])localObject);
        outputSize = localObject[0];
        if (outputSize == 0L) {
          outputSize = -1L;
        }
      }
      if (bandCoding != regularCoding)
      {
        metaCoding = bandCoding.getMetaCoding(regularCoding);
        if (verbose > 1) {
          Utils.log.fine("alternate coding " + this + " " + bandCoding);
        }
      }
      else if ((bool) && (BandStructure.decodeEscapeValue(values[0], regularCoding) >= 0))
      {
        metaCoding = BandStructure.defaultMetaCoding;
      }
      else
      {
        metaCoding = BandStructure.noMetaCoding;
      }
      if ((metaCoding.length > 0) && ((verbose > 2) || ((verbose > 1) && (metaCoding.length > 1))))
      {
        localObject = new StringBuffer();
        for (int j = 0; j < metaCoding.length; j++)
        {
          if (j == 1) {
            ((StringBuffer)localObject).append(" /");
          }
          ((StringBuffer)localObject).append(" ").append(metaCoding[j] & 0xFF);
        }
        Utils.log.fine("   meta-coding " + localObject);
      }
      assert ((outputSize < 0L) || (!(bandCoding instanceof Coding)) || (outputSize == ((Coding)bandCoding).getLength(values, 0, length))) : (bandCoding + " : " + outputSize + " != " + ((Coding)bandCoding).getLength(values, 0, length) + " ?= " + getCodingChooser().computeByteSize(bandCoding, values, 0, length));
      if (metaCoding.length > 0)
      {
        if (outputSize >= 0L) {
          outputSize += computeEscapeSize();
        }
        for (int i = 1; i < metaCoding.length; i++) {
          band_headers.putByte(metaCoding[i] & 0xFF);
        }
      }
    }
    
    protected long computeOutputSize()
    {
      outputSize = getCodingChooser().computeByteSize(bandCoding, values, 0, length);
      assert (outputSize < 2147483647L);
      outputSize += computeEscapeSize();
      return outputSize;
    }
    
    protected int computeEscapeSize()
    {
      if (metaCoding.length == 0) {
        return 0;
      }
      int i = metaCoding[0] & 0xFF;
      int j = BandStructure.encodeEscapeValue(i, regularCoding);
      return regularCoding.setD(0).getLength(j);
    }
    
    protected void writeDataTo(OutputStream paramOutputStream)
      throws IOException
    {
      if (length == 0) {
        return;
      }
      long l = 0L;
      if (paramOutputStream == outputCounter) {
        l = outputCounter.getCount();
      }
      if (metaCoding.length > 0)
      {
        int i = metaCoding[0] & 0xFF;
        int j = BandStructure.encodeEscapeValue(i, regularCoding);
        regularCoding.setD(0).writeTo(paramOutputStream, j);
      }
      bandCoding.writeArrayTo(paramOutputStream, values, 0, length);
      if ((paramOutputStream == outputCounter) && (!$assertionsDisabled) && (outputSize != outputCounter.getCount() - l)) {
        throw new AssertionError(outputSize + " != " + outputCounter.getCount() + "-" + l);
      }
      if (optDumpBands) {
        dumpBand();
      }
    }
    
    protected void readDataFrom(InputStream paramInputStream)
      throws IOException
    {
      length = valuesExpected();
      if (length == 0) {
        return;
      }
      if (verbose > 1) {
        Utils.log.fine("Reading band " + this);
      }
      if (!canVaryCoding())
      {
        bandCoding = regularCoding;
        metaCoding = BandStructure.noMetaCoding;
      }
      else
      {
        assert (paramInputStream.markSupported());
        paramInputStream.mark(5);
        int i = regularCoding.setD(0).readFrom(paramInputStream);
        int j = BandStructure.decodeEscapeValue(i, regularCoding);
        if (j < 0)
        {
          paramInputStream.reset();
          bandCoding = regularCoding;
          metaCoding = BandStructure.noMetaCoding;
        }
        else if (j == 0)
        {
          bandCoding = regularCoding;
          metaCoding = BandStructure.defaultMetaCoding;
        }
        else
        {
          if (verbose > 2) {
            Utils.log.fine("found X=" + i + " => XB=" + j);
          }
          bandCoding = getBandHeader(j, regularCoding);
          int k = bandHeaderBytePos0;
          int m = bandHeaderBytePos;
          metaCoding = new byte[m - k];
          System.arraycopy(bandHeaderBytes, k, metaCoding, 0, metaCoding.length);
        }
      }
      if ((bandCoding != regularCoding) && (verbose > 1)) {
        Utils.log.fine(name() + ": irregular coding " + bandCoding);
      }
      bandCoding.readArrayFrom(paramInputStream, values, 0, length);
      if (optDumpBands) {
        dumpBand();
      }
    }
    
    public void doneDisbursing()
    {
      super.doneDisbursing();
      values = null;
    }
    
    private void dumpBand()
      throws IOException
    {
      assert (optDumpBands);
      Object localObject1 = new PrintStream(BandStructure.getDumpStream(this, ".txt"));
      Object localObject2 = null;
      try
      {
        String str = bandCoding == regularCoding ? "" : " irregular";
        ((PrintStream)localObject1).print("# length=" + length + " size=" + outputSize() + str + " coding=" + bandCoding);
        if (metaCoding != BandStructure.noMetaCoding)
        {
          StringBuffer localStringBuffer = new StringBuffer();
          for (int i = 0; i < metaCoding.length; i++)
          {
            if (i == 1) {
              localStringBuffer.append(" /");
            }
            localStringBuffer.append(" ").append(metaCoding[i] & 0xFF);
          }
          ((PrintStream)localObject1).print(" //header: " + localStringBuffer);
        }
        BandStructure.printArrayTo((PrintStream)localObject1, values, 0, length);
      }
      catch (Throwable localThrowable2)
      {
        localObject2 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localObject1 != null) {
          if (localObject2 != null) {
            try
            {
              ((PrintStream)localObject1).close();
            }
            catch (Throwable localThrowable5)
            {
              ((Throwable)localObject2).addSuppressed(localThrowable5);
            }
          } else {
            ((PrintStream)localObject1).close();
          }
        }
      }
      localObject1 = BandStructure.getDumpStream(this, ".bnd");
      localObject2 = null;
      try
      {
        bandCoding.writeArrayTo((OutputStream)localObject1, values, 0, length);
      }
      catch (Throwable localThrowable4)
      {
        localObject2 = localThrowable4;
        throw localThrowable4;
      }
      finally
      {
        if (localObject1 != null) {
          if (localObject2 != null) {
            try
            {
              ((OutputStream)localObject1).close();
            }
            catch (Throwable localThrowable6)
            {
              ((Throwable)localObject2).addSuppressed(localThrowable6);
            }
          } else {
            ((OutputStream)localObject1).close();
          }
        }
      }
    }
    
    protected int getValue()
    {
      assert (phase() == 6);
      if ((optDebugBands) && (length == 0) && (valuesDisbursed == length)) {
        return 0;
      }
      assert (valuesDisbursed <= length);
      return values[(valuesDisbursed++)];
    }
    
    public void resetForSecondPass()
    {
      assert (phase() == 6);
      assert (valuesDisbursed == length());
      valuesDisbursed = 0;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\util\jar\pack\BandStructure.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */