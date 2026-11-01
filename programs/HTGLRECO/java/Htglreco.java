public class Htglreco {
  private String wsInStatus = "  ";
  private String wsOutStatus = "  ";
  private String wsAuditStatus = "  ";
  private boolean endOfInput = false;
  private boolean inputError = false;
  private long wsRecordCount = 0;
  private long wsAcceptCount = 0;
  private long wsRejectCount = 0;
  private long wsReturnCount = 0;
  private long wsOutOfBalance = 0;
  private String wsEdgeCode = "                        ";
  private String wsKey = "            ";
  private String wsPrevKey = "            ";
  private String wsBranch = "   ";
  private String wsPrevBranch = "   ";
  private String wsStatus = "        ";
  private String wsAmountText = "        ";
  private String wsShortAmount = "      ";
  private String wsOldDate = "991231";
  private String wsCentury = "20";
  private String wsNewDate = "00000000";
  private String wsLine = "                                                                                                                                                                                                        ";
  private String inRecord = "                                                                                                                                                                ";
  private String outRecord = "                                                                                                                                                                                                        ";
  private String auditRecord = "                                                                                                                                                                                                        ";
  private String wsRawBase = "                                                                                                                        ";
  private long wsReadTime = 0;
  private long wsWriteTime = 0;
  private int wsItemCount = 0;
  private String[] wsItems = new String[]{"    ","    ","    ","    ","    ","    ","    ","    ","    ","    ","    ","    "};
  private long wsDayBasis = 365;
  private String wsLeapFlag = "N";
  private String wsFeeWaived = "N";
  private long wsCountDisp = 0;
  private java.math.BigDecimal wsGlDebit = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsGlCredit = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsSubDebit = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsSubCredit = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsReconDiff = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsTolerance = new java.math.BigDecimal("0.01");
  private java.math.BigDecimal wsEntryAmt = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsCycleTotal = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsDebits = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsCredits = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsDelta = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsReportTotal = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsPacked = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsZoned = java.math.BigDecimal.ZERO;
  private String wsGlAcct = "          ";
  private String wsMatchFlag = "N";
  private String wsSuspenseAcct = "SUSP000001";
  private int returnCode = 0;
  private boolean ebcdicMode = false;
  private java.io.BufferedReader inReader = null;
  private java.io.BufferedWriter outWriter = null;
  private java.io.BufferedWriter auditWriter = null;
  private int dtYear = 0;
  private int dtMonth = 0;
  private int dtDay = 0;
  private String dtLeap = "N";
  private int dtQuotient = 0;
  private int dtRemainder = 0;
  private int dtDayCount = 0;
  private int dtBasisWork = 365;
  private String dtValidFlag = "Y";
  private int dtQty = 0;
  private int dtIdx = 0;
  private int[] dtMdays = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};
  public static void main(String[] args) throws Exception {
    Htglreco o = new Htglreco();
    o.paragraph0000Main();
    if (o.returnCode != 0) System.exit(o.returnCode);
  }
  private void paragraph0000Main() throws Exception {
    paragraph1000Initialize();
    while (!endOfInput) {
      paragraph2000ReadInput();
      if (!endOfInput) {
        paragraph3000ValidateRecord();
        if (!inputError) {
          paragraph4000ProcessRecord();
          paragraph5000WriteResult();
        } else {
          paragraph6000RejectRecord();
        }
      }
    }
    paragraph7000Finalize();
  }
  private void paragraph1000Initialize() throws Exception {
    String inPath = System.getenv("DD_INPUT-FILE");
    String outPath = System.getenv("DD_OUTPUT-FILE");
    String auditPath = System.getenv("DD_AUDIT-FILE");
    String coll = System.getenv("DIOCLE_COLLATION");
    String clk = System.getenv("DIOCLE_CLOCK");
    if (clk != null && clk.length() == 0) ebcdicMode = false;
    ebcdicMode = "EBCDIC".equals(coll);
    try {
      if (inPath == null) throw new java.io.IOException("no env");
      inReader = java.nio.file.Files.newBufferedReader(java.nio.file.Paths.get(inPath), java.nio.charset.Charset.forName("ISO-8859-1"));
      wsInStatus = "00";
    } catch (Exception e) {
      wsInStatus = "35";
      System.out.println("OPEN INPUT FAILED " + wsInStatus);
      returnCode = 12;
      endOfInput = true;
    }
    try {
      if (outPath == null) throw new java.io.IOException("no env");
      outWriter = java.nio.file.Files.newBufferedWriter(java.nio.file.Paths.get(outPath), java.nio.charset.Charset.forName("ISO-8859-1"), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING, java.nio.file.StandardOpenOption.WRITE);
      wsOutStatus = "00";
    } catch (Exception e) {
      wsOutStatus = "35";
      System.out.println("OPEN OUTPUT FAILED " + wsOutStatus);
      returnCode = 12;
      endOfInput = true;
    }
    try {
      if (auditPath == null) throw new java.io.IOException("no env");
      auditWriter = java.nio.file.Files.newBufferedWriter(java.nio.file.Paths.get(auditPath), java.nio.charset.Charset.forName("ISO-8859-1"), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING, java.nio.file.StandardOpenOption.WRITE);
      wsAuditStatus = "00";
    } catch (Exception e) {
      wsAuditStatus = "35";
      System.out.println("OPEN AUDIT FAILED " + wsAuditStatus);
      returnCode = 12;
      endOfInput = true;
    }
    wsRecordCount = 0;
    wsAcceptCount = 0;
    wsRejectCount = 0;
    wsReturnCount = 0;
    wsEdgeCode = padRight("", 24);
    paragraph1100CheckRunControl();
    paragraph1200InitializeTotals();
  }
  private void paragraph1100CheckRunControl() {
    String part = wsOldDate.substring(0, 2);
    int v = Integer.parseInt(part);
    if (v > 80) wsCentury = "19";
    else wsCentury = "20";
    wsNewDate = wsCentury + wsNewDate.substring(2);
    wsNewDate = wsNewDate.substring(0, 2) + wsOldDate;
    if (wsNewDate.equals("00000000")) wsEdgeCode = padRight("BAD-RUN-DATE", 24);
    wsReadTime = Long.parseLong(wsNewDate);
  }
  private void paragraph1200InitializeTotals() {
    wsReportTotal = java.math.BigDecimal.ZERO;
    wsCycleTotal = java.math.BigDecimal.ZERO;
    wsDebits = java.math.BigDecimal.ZERO;
    wsCredits = java.math.BigDecimal.ZERO;
    wsDelta = java.math.BigDecimal.ZERO;
    wsOutOfBalance = 0;
    wsPrevKey = padRight("", 12);
    wsPrevBranch = padRight("", 3);
    wsDayBasis = 365;
    wsLeapFlag = "N";
    wsFeeWaived = "N";
  }
  private void paragraph2000ReadInput() throws Exception {
    if (inReader == null) {
      endOfInput = true;
      return;
    }
    String line = null;
    try {
      line = inReader.readLine();
    } catch (Exception e) {
      wsInStatus = "99";
      System.out.println("READ FAILURE " + wsInStatus);
      inputError = true;
      endOfInput = true;
      return;
    }
    if (line == null) {
      endOfInput = true;
      wsInStatus = "10";
    } else {
      wsInStatus = "00";
      wsRecordCount = wsRecordCount + 1;
      if (line.length() > 160) inRecord = line.substring(0, 160);
      else inRecord = padRight(line, 160);
      wsRawBase = inRecord.substring(0, 120);
      wsKey = inRecord.substring(0, 12);
      wsBranch = inRecord.substring(12, 15);
      wsAmountText = inRecord.substring(15, 23);
      String tmp8 = inRecord.substring(23, 31);
      wsShortAmount = tmp8.substring(0, 6);
      wsStatus = inRecord.substring(31, 39);
    }
    if (!wsInStatus.equals("00") && !wsInStatus.equals("10")) {
      System.out.println("READ FAILURE " + wsInStatus);
      inputError = true;
      endOfInput = true;
    }
  }
  private void paragraph3000ValidateRecord() {
    inputError = false;
    if (wsKey.equals(padRight("", 12))) paragraph3100BadKey();
    if (!isNumericTest(wsAmountText)) paragraph3200BadAmount();
    if (wsStatus.equals(padRight("", 8))) paragraph3300BadStatus();
    if (!isAlphabeticTest(wsBranch)) paragraph3400BadBranch();
    if (wsRecordCount > 1 && compareKeys(wsKey, wsPrevKey) < 0) wsEdgeCode = padRight("SORT-ORDER", 24);
    wsPrevKey = wsKey;
    paragraph3500CheckMultiRecordType();
    paragraph3600CheckDateBoundary();
  }
  private void paragraph3100BadKey() {
    inputError = true;
    wsEdgeCode = padRight("MISSING-KEY", 24);
  }
  private void paragraph3200BadAmount() {
    inputError = true;
    wsEdgeCode = padRight("INVALID-AMOUNT", 24);
  }
  private void paragraph3300BadStatus() {
    inputError = true;
    wsEdgeCode = padRight("INVALID-STATUS", 24);
  }
  private void paragraph3400BadBranch() {
    inputError = true;
    wsEdgeCode = padRight("INVALID-BRANCH", 24);
  }
  private void paragraph3500CheckMultiRecordType() {
    char c0 = inRecord.charAt(0);
    if (c0 != 'A' && c0 != 'T') wsEdgeCode = padRight("UNKNOWN-RECORD-TYPE", 24);
    char c1 = inRecord.charAt(1);
    if (c1 == 'D') wsItemCount = 2;
    else wsItemCount = 1;
    wsReadTime = 1;
    while (!(wsReadTime > wsItemCount)) {
      wsItems[(int) wsReadTime - 1] = "0000";
      wsReadTime = wsReadTime + 1;
    }
  }
  private void paragraph3600CheckDateBoundary() {
    if (wsNewDate.equals("00000000")) {
      inputError = true;
      wsEdgeCode = padRight("MISSING-GL-DATE", 24);
    }
  }
  private void paragraph4000ProcessRecord() throws Exception {
    paragraph4100ParseGlEntry();
    paragraph4200AccumulateSubledger();
    paragraph4300ReconcileEntry();
    paragraph4400PostSuspense();
    paragraph8000CallDateUtility();
  }
  private void paragraph4100ParseGlEntry() {
    wsGlAcct = inRecord.substring(39, 49);
    if (isNumericTest(wsAmountText)) {
      java.math.BigDecimal num = new java.math.BigDecimal(wsAmountText);
      java.math.BigDecimal res = num.movePointLeft(2);
      res = res.setScale(2, java.math.RoundingMode.DOWN);
      java.math.BigDecimal limit = new java.math.BigDecimal("99999999999.99");
      if (res.abs().compareTo(limit) > 0) wsGlDebit = java.math.BigDecimal.ZERO.setScale(2);
      else wsGlDebit = res;
    }
    wsEntryAmt = wsGlDebit;
    wsGlCredit = wsGlDebit;
    if (wsGlAcct.equals(padRight("", 10))) {
      inputError = true;
      wsEdgeCode = padRight("MISSING-GL-ACCT", 24);
    }
  }
  private void paragraph4200AccumulateSubledger() {
    char c0 = inRecord.charAt(0);
    if (c0 == 'D') {
      wsSubDebit = wsSubDebit.add(wsGlDebit);
      wsDebits = wsDebits.add(wsGlDebit);
    } else {
      wsSubCredit = wsSubCredit.add(wsGlCredit);
      wsCredits = wsCredits.add(wsGlCredit);
    }
  }
  private void paragraph4300ReconcileEntry() {
    java.math.BigDecimal inner = wsDebits.subtract(wsCredits);
    java.math.BigDecimal diff = wsSubDebit.subtract(wsSubCredit).subtract(inner);
    diff = diff.setScale(2, java.math.RoundingMode.DOWN);
    java.math.BigDecimal limit = new java.math.BigDecimal("99999999999.99");
    if (diff.abs().compareTo(limit) > 0) diff = java.math.BigDecimal.ZERO.setScale(2);
    if (diff.compareTo(java.math.BigDecimal.ZERO) < 0) diff = diff.multiply(new java.math.BigDecimal("-1"));
    wsReconDiff = diff;
    if (wsReconDiff.compareTo(wsTolerance) <= 0) wsMatchFlag = "Y";
    else {
      wsMatchFlag = "N";
      wsOutOfBalance = wsOutOfBalance + 1;
      wsEdgeCode = padRight("GL-MISMATCH", 24);
    }
  }
  private void paragraph4400PostSuspense() throws Exception {
    if (!wsMatchFlag.equals("Y")) {
      auditRecord = padRight("", 200);
      String built = "SUSPENSE " + wsGlAcct + " POSTED TO " + wsSuspenseAcct;
      auditRecord = padRight(built, 200);
      try {
        String w = rtrim(auditRecord);
        auditWriter.write(w);
        auditWriter.write("\n");
        auditWriter.flush();
        wsAuditStatus = "00";
      } catch (Exception e) {
        wsAuditStatus = "99";
      }
      if (!wsAuditStatus.equals("00")) System.out.println("SUSPENSE AUDIT FAIL " + wsAuditStatus);
    }
  }
  private void paragraph5000WriteResult() throws Exception {
    wsLine = padRight("", 200);
    outRecord = padRight("", 200);
    String built = wsKey + "|" + wsBranch + "|" + wsEdgeCode;
    outRecord = padRight(built, 200);
    try {
      String w = rtrim(outRecord);
      outWriter.write(w);
      outWriter.write("\n");
      outWriter.flush();
      wsOutStatus = "00";
    } catch (Exception e) {
      wsOutStatus = "99";
    }
    if (!wsOutStatus.equals("00")) {
      System.out.println("WRITE FAILURE " + wsOutStatus);
      returnCode = 12;
    } else {
      wsAcceptCount = wsAcceptCount + 1;
      wsCycleTotal = wsCycleTotal.add(wsEntryAmt);
    }
    auditRecord = outRecord;
    try {
      String w2 = rtrim(auditRecord);
      auditWriter.write(w2);
      auditWriter.write("\n");
      auditWriter.flush();
      wsAuditStatus = "00";
    } catch (Exception e) {
      wsAuditStatus = "99";
    }
    if (!wsAuditStatus.equals("00")) System.out.println("AUDIT WRITE FAILURE " + wsAuditStatus);
  }
  private void paragraph6000RejectRecord() throws Exception {
    wsRejectCount = wsRejectCount + 1;
    wsLine = padRight("", 200);
    outRecord = padRight("", 200);
    String built = "REJECT:" + wsKey + "|" + wsEdgeCode;
    outRecord = padRight(built, 200);
    try {
      String w = rtrim(outRecord);
      outWriter.write(w);
      outWriter.write("\n");
      outWriter.flush();
      wsOutStatus = "00";
    } catch (Exception e) {
      wsOutStatus = "99";
    }
    if (!wsOutStatus.equals("00")) System.out.println("REJECT WRITE FAILURE " + wsOutStatus);
  }
  private void paragraph7000Finalize() throws Exception {
    paragraph7100CloseFiles();
    paragraph7200WriteTrailer();
    paragraph7300ReportCounts();
    if (wsRejectCount > 0) System.out.println("REJECTS PRESENT: " + format9(wsRejectCount, 10));
    returnCode = 0;
  }
  private void paragraph7100CloseFiles() throws Exception {
    try {
      if (inReader != null) inReader.close();
      wsInStatus = "00";
    } catch (Exception e) {
      wsInStatus = "99";
    }
    if (!wsInStatus.equals("00")) System.out.println("CLOSE INPUT ISSUE " + wsInStatus);
    try {
      if (outWriter != null) outWriter.close();
      wsOutStatus = "00";
    } catch (Exception e) {
      wsOutStatus = "99";
    }
    if (!wsOutStatus.equals("00")) System.out.println("CLOSE OUTPUT ISSUE " + wsOutStatus);
    try {
      if (auditWriter != null) auditWriter.close();
      wsAuditStatus = "00";
    } catch (Exception e) {
      wsAuditStatus = "99";
    }
    if (!wsAuditStatus.equals("00")) System.out.println("CLOSE AUDIT ISSUE " + wsAuditStatus);
  }
  private void paragraph7200WriteTrailer() {
    outRecord = padRight("", 200);
    wsLine = padRight("", 200);
    wsCountDisp = wsRecordCount;
    String built = "TRAILER COUNT=" + format9(wsCountDisp, 9);
    outRecord = padRight(built, 200);
    wsPacked = truncate2(wsCycleTotal);
    wsZoned = truncate2(wsPacked);
    wsReportTotal = wsReportTotal.add(wsDebits);
    wsReportTotal = wsReportTotal.subtract(wsCredits);
    java.math.BigDecimal d = wsDebits.subtract(wsCredits);
    d = truncate2(d);
    java.math.BigDecimal limit = new java.math.BigDecimal("99999999999.99");
    if (d.abs().compareTo(limit) > 0) wsDelta = java.math.BigDecimal.ZERO.setScale(2);
    else wsDelta = d;
  }
  private void paragraph7300ReportCounts() {
    System.out.println("READ: " + format9(wsRecordCount, 10));
    System.out.println("ACCEPTED: " + format9(wsAcceptCount, 10));
    System.out.println("REJECTED: " + format9(wsRejectCount, 10));
    System.out.println("RETURNS: " + format9(wsReturnCount, 10));
    System.out.println("OUT-OF-BALANCE: " + format9(wsOutOfBalance, 10));
  }
  private void paragraph8000CallDateUtility() {
    paragraph0000MainDateUtil();
    if (returnCode != 0) {
      System.out.println("DATE UTILITY RC=" + format9(returnCode, 9));
      wsEdgeCode = padRight("DATEUT-FAIL", 24);
    }
    if (wsLeapFlag.equals("Y")) wsDayBasis = 366;
    else wsDayBasis = 365;
  }
  private void paragraph9000AbendHandler() {
    System.out.println("FATAL ERROR " + wsEdgeCode);
    returnCode = 16;
  }
  private void paragraph0000MainDateUtil() {
    paragraph1000SplitDate();
    paragraph2000CheckLeapYear();
    paragraph3000ValidateDate();
    paragraph4000NormalizeBasis();
    paragraph5000ComputeDayCount();
  }
  private void paragraph1000SplitDate() {
    String s = wsNewDate;
    if (s.length() < 8) s = padLeftZero(s, 8);
    dtYear = Integer.parseInt(s.substring(0, 4));
    dtMonth = Integer.parseInt(s.substring(4, 6));
    dtDay = Integer.parseInt(s.substring(6, 8));
    if (dtMonth < 1 || dtMonth > 12) dtValidFlag = "N";
    if (dtDay < 1 || dtDay > 31) dtValidFlag = "N";
  }
  private void paragraph2000CheckLeapYear() {
    dtQuotient = dtYear / 4;
    dtRemainder = dtYear % 4;
    if (dtRemainder == 0) dtLeap = "Y";
    else dtLeap = "N";
    dtQuotient = dtYear / 100;
    dtRemainder = dtYear % 100;
    if (dtRemainder == 0) dtLeap = "N";
    dtQuotient = dtYear / 400;
    dtRemainder = dtYear % 400;
    if (dtRemainder == 0) dtLeap = "Y";
  }
  private void paragraph3000ValidateDate() {
    if (dtMonth >= 1 && dtMonth <= 12) {
      int ml = dtMdays[dtMonth - 1];
      if (dtDay > ml) {
        if (!(dtMonth == 2 && dtLeap.equals("Y") && dtDay == 29)) dtValidFlag = "N";
      }
    }
    if (!dtValidFlag.equals("Y")) returnCode = 4;
    else returnCode = 0;
  }
  private void paragraph4000NormalizeBasis() {
    dtBasisWork = (int) wsDayBasis;
    if (dtBasisWork == 360) {}
    else if (dtBasisWork == 365) {}
    else if (dtBasisWork == 366) {}
    else dtBasisWork = 365;
    if (dtLeap.equals("Y")) dtBasisWork = 366;
  }
  private void paragraph5000ComputeDayCount() {
    dtDayCount = 0;
    dtIdx = 1;
    while (!(dtIdx >= dtMonth)) {
      dtDayCount = dtDayCount + dtMdays[dtIdx - 1];
      dtIdx = dtIdx + 1;
    }
    dtDayCount = dtDayCount + dtDay;
    if (dtMonth > 2 && dtLeap.equals("Y")) dtDayCount = dtDayCount + 1;
    dtQty = dtDayCount;
  }
  private int compareKeys(String a, String b) {
    if (ebcdicMode) {
      try {
        byte[] ab = a.getBytes("Cp037");
        byte[] bb = b.getBytes("Cp037");
        int n = Math.min(ab.length, bb.length);
        for (int i = 0; i < n; i++) {
          int av = ab[i] & 0xFF;
          int bv = bb[i] & 0xFF;
          if (av != bv) return av - bv;
        }
        return ab.length - bb.length;
      } catch (Exception e) {}
    }
    return a.compareTo(b);
  }
  static String padRight(String s, int n) {
    if (s.length() >= n) return s.substring(0, n);
    StringBuilder sb = new StringBuilder(s);
    while (sb.length() < n) sb.append(' ');
    return sb.toString();
  }
  static String padLeftZero(String s, int n) {
    if (s.length() >= n) return s.substring(s.length() - n);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < n - s.length(); i++) sb.append('0');
    sb.append(s);
    return sb.toString();
  }
  static String rtrim(String s) {
    int i = s.length();
    while (i > 0 && s.charAt(i - 1) == ' ') i--;
    return s.substring(0, i);
  }
  static boolean isNumericTest(String s) {
    if (s.length() == 0) return false;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c < '0' || c > '9') return false;
    }
    return true;
  }
  static boolean isAlphabeticTest(String s) {
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == ' ') continue;
      if (c >= 'A' && c <= 'Z') continue;
      if (c >= 'a' && c <= 'z') continue;
      return false;
    }
    return true;
  }
  static String format9(long v, int d) {
    boolean neg = v < 0;
    long a = neg ? -v : v;
    String s = Long.toString(a);
    if (s.length() > d) s = s.substring(s.length() - d);
    else if (s.length() < d) s = padLeftZero(s, d);
    if (neg && s.length() > 0) s = "-" + s.substring(1);
    return s;
  }
  static String format9(int v, int d) {
    return format9((long) v, d);
  }
  static java.math.BigDecimal truncate2(java.math.BigDecimal v) {
    return v.setScale(2, java.math.RoundingMode.DOWN);
  }
}