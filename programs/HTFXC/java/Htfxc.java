import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
public class Htfxc {
  static String wsInStatus = "  ";
  static String wsOutStatus = "  ";
  static boolean endOfInput = false;
  static boolean inputError = false;
  static long wsRecordCount = 0;
  static long wsAcceptCount = 0;
  static long wsRejectCount = 0;
  static int wsArithError = 0;
  static String wsEdgeCode = "                        ";
  static String wsKey = "            ";
  static String wsPrevKey = "            ";
  static String wsBranch = "   ";
  static String wsPrevBranch = "   ";
  static String wsStatus = "        ";
  static BigDecimal wsAmount = BigDecimal.ZERO;
  static BigDecimal wsBalance = BigDecimal.ZERO;
  static BigDecimal wsPrincipal = BigDecimal.ZERO;
  static BigDecimal wsRate = BigDecimal.ZERO;
  static BigDecimal wsFxRate = BigDecimal.ZERO;
  static BigDecimal wsConverted = BigDecimal.ZERO;
  static BigDecimal wsInterest = BigDecimal.ZERO;
  static BigDecimal wsFee = BigDecimal.ZERO;
  static String wsFeeWaived = "N";
  static BigDecimal wsCheckAmt = BigDecimal.ZERO;
  static String wsReturnReason = "   ";
  static long wsReturnCount = 0;
  static BigDecimal wsEntryAmt = BigDecimal.ZERO;
  static BigDecimal wsCycleTotal = BigDecimal.ZERO;
  static BigDecimal wsDebits = BigDecimal.ZERO;
  static BigDecimal wsCredits = BigDecimal.ZERO;
  static BigDecimal wsDelta = BigDecimal.ZERO;
  static long wsOutOfBalance = 0;
  static long wsDaysInactive = 0;
  static long wsDayBasis = 365;
  static String wsLeapFlag = "N";
  static String wsAmountText = "        ";
  static String wsShortAmount = "      ";
  static String wsRawBase = "                                                                                                                        ";
  static String wsFeeOverride = " ";
  static int wsItemCount = 0;
  static String[] wsItems = new String[12];
  static BigDecimal wsPacked = BigDecimal.ZERO;
  static BigDecimal wsZoned = BigDecimal.ZERO;
  static String wsOldDate = "991231";
  static String wsCentury = "20";
  static String wsNewDate = "00000000";
  static char[] wsLine = new char[200];
  static BigDecimal wsReportTotal = BigDecimal.ZERO;
  static long wsReadTime = 0;
  static long wsWriteTime = 0;
  static long wsAbendCode = 0;
  static String inRecord = "";
  static String outRecord = "";
  static int returnCode = 0;
  static BufferedReader inReader = null;
  static BufferedWriter outWriter = null;
  static boolean ebcdicMode = false;
  public static void main(String[] args) throws Exception {
    for (int i = 0; i < 200; i++) wsLine[i] = ' ';
    for (int i = 0; i < 12; i++) wsItems[i] = "    ";
    String c = System.getenv("DIOCLE_COLLATION");
    if (c != null && c.equals("EBCDIC")) ebcdicMode = true;
    n0000Main();
    if (returnCode != 0) System.exit(returnCode);
  }
  static String padRight(String s, int n) {
    if (s == null) s = "";
    if (s.length() > n) return s.substring(0, n);
    if (s.length() == n) return s;
    StringBuilder b = new StringBuilder(s);
    while (b.length() < n) b.append(' ');
    return b.toString();
  }
  static String refMod(String s, int start, int len) {
    int from = start - 1;
    if (from < 0) from = 0;
    int to = from + len;
    if (from >= s.length()) return padRight("", len);
    if (to > s.length()) return padRight(s.substring(from), len);
    return s.substring(from, to);
  }
  static void setLineSlice(int start, int len, String val) {
    String v = padRight(val, len);
    if (v.length() > len) v = v.substring(0, len);
    for (int i = 0; i < len; i++) wsLine[start - 1 + i] = v.charAt(i);
  }
  static String getLineSlice(int start, int len) {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < len; i++) b.append(wsLine[start - 1 + i]);
    return b.toString();
  }
  static String lineString() { return new String(wsLine); }
  static boolean isNumericText(String s) {
    if (s == null || s.length() == 0) return false;
    for (int i = 0; i < s.length(); i++) { char ch = s.charAt(i); if (ch < '0' || ch > '9') return false; }
    return true;
  }
  static boolean isAlphabeticText(String s) {
    for (int i = 0; i < s.length(); i++) { char ch = s.charAt(i); if (ch == ' ') continue; if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) continue; return false; }
    return true;
  }
  static int compareKeys(String a, String b) {
    if (!ebcdicMode) return a.compareTo(b);
    try {
      Charset cs = Charset.forName("Cp037");
      byte[] ba = a.getBytes(cs);
      byte[] bb = b.getBytes(cs);
      int n = Math.min(ba.length, bb.length);
      for (int i = 0; i < n; i++) { int x = ba[i] & 0xFF; int y = bb[i] & 0xFF; if (x != y) return x - y; }
      return ba.length - bb.length;
    } catch (Exception e) { return a.compareTo(b); }
  }
  static String formatCount9(long v) {
    boolean neg = v < 0;
    long a = neg ? -v : v;
    String s = Long.toString(a);
    while (s.length() < 10) s = "0" + s;
    if (s.length() > 10) s = s.substring(s.length() - 10);
    if (neg) return "-" + s.substring(1);
    return s;
  }
  static String formatCount9Generic(long v) { return formatCount9(v); }
  static String formatTotal(BigDecimal v) {
    BigDecimal a = v.abs().setScale(2, RoundingMode.UNNECESSARY);
    String plain = a.toPlainString();
    int dot = plain.indexOf('.');
    String ip = dot >= 0 ? plain.substring(0, dot) : plain;
    String fp = dot >= 0 ? plain.substring(dot + 1) : "00";
    while (fp.length() < 2) fp = fp + "0";
    if (fp.length() > 2) fp = fp.substring(0, 2);
    while (ip.length() < 11) ip = "0" + ip;
    String sign = v.signum() < 0 ? "-" : "+";
    return sign + ip + "." + fp;
  }
  static BigDecimal parseAmountText(String t) {
    String s = t.trim();
    if (s.length() == 0) return BigDecimal.ZERO;
    return new BigDecimal(s);
  }
  static boolean fitsS11V99(BigDecimal v) {
    BigDecimal lim = new BigDecimal("99999999999.99");
    BigDecimal neg = new BigDecimal("-99999999999.99");
    return v.compareTo(lim) <= 0 && v.compareTo(neg) >= 0;
  }
  static boolean fitsS9V99(BigDecimal v) {
    BigDecimal lim = new BigDecimal("999999999.99");
    BigDecimal neg = new BigDecimal("-999999999.99");
    return v.compareTo(lim) <= 0 && v.compareTo(neg) >= 0;
  }
  private static void n0000Main() throws Exception {
    n1000Initialize();
    while (!endOfInput) {
      n2000ReadInput();
      if (!endOfInput) {
        n3000ValidateRecord();
        if (!inputError) {
          n4000ProcessRecord();
          n5000WriteResult();
        } else {
          n6000RejectRecord();
        }
      }
    }
    n7000Finalize();
  }
  private static void n1000Initialize() throws Exception {
    String inPath = System.getenv("DD_INPUT-FILE");
    String outPath = System.getenv("DD_OUTPUT-FILE");
    try {
      if (inPath == null) { wsInStatus = "35"; } else {
        try {
          inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inPath), StandardCharsets.ISO_8859_1));
          wsInStatus = "00";
        } catch (Exception e) { wsInStatus = "35"; inReader = null; }
      }
    } catch (Exception e) { wsInStatus = "35"; }
    if (!wsInStatus.equals("00")) {
      System.out.println("OPEN INPUT FAILED " + wsInStatus);
      returnCode = 12;
      endOfInput = true;
    }
    try {
      if (outPath == null) { wsOutStatus = "35"; } else {
        try {
          outWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPath), StandardCharsets.ISO_8859_1));
          wsOutStatus = "00";
        } catch (Exception e) { wsOutStatus = "35"; outWriter = null; }
      }
    } catch (Exception e) { wsOutStatus = "35"; }
    if (!wsOutStatus.equals("00")) {
      System.out.println("OPEN OUTPUT FAILED " + wsOutStatus);
      returnCode = 12;
      endOfInput = true;
    }
    wsRecordCount = 0; wsAcceptCount = 0; wsRejectCount = 0;
    wsEdgeCode = padRight("", 24);
    n1100CheckRunControl();
    n1200InitializeTotals();
  }
  private static void n1100CheckRunControl() {
    String part = refMod(wsOldDate, 1, 2);
    int pv = 0;
    try { pv = Integer.parseInt(part); } catch (Exception e) { pv = 0; }
    if (pv > 80) wsCentury = "19";
    else wsCentury = "20";
    StringBuilder nb = new StringBuilder(padRight(wsNewDate, 8));
    while (nb.length() < 8) nb.append(' ');
    String cc = padRight(wsCentury, 2);
    nb.setCharAt(0, cc.charAt(0)); nb.setCharAt(1, cc.charAt(1));
    String od = padRight(wsOldDate, 6);
    for (int i = 0; i < 6; i++) nb.setCharAt(2 + i, od.charAt(i));
    wsNewDate = nb.toString();
  }
  private static void n1200InitializeTotals() {
    wsReportTotal = BigDecimal.ZERO;
    wsCycleTotal = BigDecimal.ZERO;
    wsDebits = BigDecimal.ZERO;
    wsCredits = BigDecimal.ZERO;
    wsDelta = BigDecimal.ZERO;
    wsPrevKey = padRight("", 12);
    wsPrevBranch = padRight("", 3);
  }
  private static void n2000ReadInput() throws Exception {
    if (inReader == null) { endOfInput = true; return; }
    String line = inReader.readLine();
    if (line == null) {
      endOfInput = true;
      wsInStatus = "10";
    } else {
      wsInStatus = "00";
      wsRecordCount = wsRecordCount + 1;
      String rec = line;
      if (rec.length() > 160) rec = rec.substring(0, 160);
      if (rec.length() < 160) rec = padRight(rec, 160);
      inRecord = rec;
      wsRawBase = refMod(inRecord, 1, 120);
      if (wsRawBase.length() >= 120) wsFeeOverride = wsRawBase.substring(119, 120);
      else wsFeeOverride = " ";
      wsKey = refMod(inRecord, 1, 12);
      wsBranch = refMod(inRecord, 13, 3);
      wsAmountText = refMod(inRecord, 16, 8);
      String s8 = refMod(inRecord, 24, 8);
      if (s8.length() >= 6) wsShortAmount = s8.substring(0, 6);
      else wsShortAmount = padRight(s8, 6);
      wsStatus = s8;
    }
    if (!wsInStatus.equals("00") && !wsInStatus.equals("10")) {
      System.out.println("READ FAILURE " + wsInStatus);
      inputError = true;
      endOfInput = true;
    }
  }
  private static void n3000ValidateRecord() {
    inputError = false;
    if (wsKey.equals(padRight("", 12))) n3100BadKey();
    if (!isNumericText(wsAmountText)) n3200BadAmount();
    if (wsStatus.equals(padRight("", 8))) n3300BadStatus();
    if (!isAlphabeticText(wsBranch)) n3400BadBranch();
    if (wsRecordCount > 1 && compareKeys(wsKey, wsPrevKey) < 0) wsEdgeCode = padRight("SORT-ORDER", 24);
    wsPrevKey = wsKey;
    n3500CheckMultiRecordType();
    n3600CheckDateBoundary();
  }
  private static void n3100BadKey() { inputError = true; wsEdgeCode = padRight("MISSING-KEY", 24); }
  private static void n3200BadAmount() { inputError = true; wsEdgeCode = padRight("INVALID-AMOUNT", 24); }
  private static void n3300BadStatus() { inputError = true; wsEdgeCode = padRight("INVALID-STATUS", 24); }
  private static void n3400BadBranch() { inputError = true; wsEdgeCode = padRight("INVALID-BRANCH", 24); }
  private static void n3500CheckMultiRecordType() {
    String c1 = refMod(inRecord, 1, 1);
    if (!c1.equals("A") && !c1.equals("T")) wsEdgeCode = padRight("UNKNOWN-RECORD-TYPE", 24);
    String c2 = refMod(inRecord, 2, 1);
    if (c2.equals("D")) wsItemCount = 2;
    else wsItemCount = 1;
  }
  private static void n3600CheckDateBoundary() {
    String d = refMod(inRecord, 40, 8);
    if (d.equals("20261231")) wsEdgeCode = padRight("YEAR-END-ROLLOVER", 24);
    if (d.equals("20260228")) wsEdgeCode = padRight("NON-LEAP-FEB-END", 24);
  }
  private static void n4000ProcessRecord() {
    wsArithError = 0;
    wsAmount = parseAmountText(wsAmountText);
    wsBalance = wsAmount;
    wsPrincipal = wsAmount;
    BigDecimal sum = wsReportTotal.add(wsAmount);
    if (!fitsS11V99(sum)) wsArithError = 1;
    else wsReportTotal = sum;
    wsRate = new BigDecimal("0.0525");
    wsFxRate = new BigDecimal("1.0825");
    wsCheckAmt = wsAmount;
    wsEntryAmt = wsAmount;
    wsFee = BigDecimal.ZERO;
    wsFeeWaived = refMod(inRecord, 120, 1);
    wsInterest = BigDecimal.ZERO;
    BigDecimal prod = wsAmount.multiply(wsFxRate);
    BigDecimal rounded = prod.setScale(2, RoundingMode.HALF_UP);
    if (!fitsS11V99(rounded)) wsArithError = 1;
    else wsConverted = rounded;
    if (wsFxRate.compareTo(BigDecimal.ZERO) == 0) wsEdgeCode = padRight("NO-RATE", 24);
    setLineSlice(1, 12, wsKey);
    setLineSlice(13, 3, wsBranch);
    setLineSlice(16, 24, wsEdgeCode);
    setLineSlice(40, 12, wsAmountText);
    setLineSlice(52, 8, wsStatus);
    wsAcceptCount = wsAcceptCount + 1;
  }
  private static void n5000WriteResult() throws Exception {
    outRecord = lineString();
    try {
      String toWrite = outRecord;
      int end = toWrite.length();
      while (end > 0 && toWrite.charAt(end - 1) == ' ') end--;
      toWrite = toWrite.substring(0, end);
      outWriter.write(toWrite);
      outWriter.newLine();
      wsOutStatus = "00";
    } catch (Exception e) { wsOutStatus = "30"; }
    if (!wsOutStatus.equals("00")) {
      System.out.println("WRITE FAILURE " + wsOutStatus);
      returnCode = 12;
      endOfInput = true;
    } else {
      wsWriteTime = wsWriteTime + 1;
    }
  }
  private static void n6000RejectRecord() throws Exception {
    wsRejectCount = wsRejectCount + 1;
    System.out.println("REJECT " + formatCount9(wsRecordCount) + " " + wsEdgeCode);
    for (int i = 0; i < 200; i++) wsLine[i] = ' ';
    setLineSlice(1, 6, "REJECT");
    setLineSlice(8, 12, wsKey);
    setLineSlice(24, 24, wsEdgeCode);
    outRecord = lineString();
    try {
      String toWrite = outRecord;
      int end = toWrite.length();
      while (end > 0 && toWrite.charAt(end - 1) == ' ') end--;
      toWrite = toWrite.substring(0, end);
      outWriter.write(toWrite);
      outWriter.newLine();
      wsOutStatus = "00";
    } catch (Exception e) { wsOutStatus = "30"; }
  }
  private static void n7000Finalize() throws Exception {
    n7100CheckControlTotals();
    n7200ReportRunCounts();
    try { if (inReader != null) inReader.close(); } catch (Exception e) {}
    try { if (outWriter != null) { outWriter.flush(); outWriter.close(); } } catch (Exception e) {}
    wsInStatus = "00";
    wsOutStatus = "00";
    if (!wsInStatus.equals("00")) System.out.println("CLOSE IN " + wsInStatus);
    if (!wsOutStatus.equals("00")) System.out.println("CLOSE OUT " + wsOutStatus);
    if (wsRejectCount > 0) returnCode = 4;
  }
  private static void n7100CheckControlTotals() {
    long tot = wsAcceptCount + wsRejectCount;
    if (tot != wsRecordCount) { System.out.println("CONTROL COUNT MISMATCH"); returnCode = 12; }
    if (wsArithError != 0) { System.out.println("ARITHMETIC SIZE ERROR"); returnCode = 8; }
  }
  private static void n7200ReportRunCounts() {
    System.out.println("PROGRAM HTFXC COMPLETE");
    System.out.println("READ=" + formatCount9(wsRecordCount));
    System.out.println("ACCEPT=" + formatCount9(wsAcceptCount));
    System.out.println("REJECT=" + formatCount9(wsRejectCount));
    System.out.println("TOTAL=" + formatTotal(wsReportTotal));
    System.out.println("EDGE=" + wsEdgeCode);
  }
  private static void n8000RejectAllZeroAmounts() {
    boolean isZero = wsAmount.compareTo(BigDecimal.ZERO) == 0;
    boolean isActive = wsStatus.trim().equals("ACTIVE");
    if (isZero && isActive) wsEdgeCode = padRight("ZERO-ACTIVE-AMOUNT", 24);
  }
  private static void n8010CheckNegativeBalance() {
    if (wsBalance.compareTo(BigDecimal.ZERO) < 0) wsEdgeCode = padRight("NEGATIVE-BALANCE", 24);
  }
  private static void n8020CheckLongKey() {
    if (refMod(wsKey, 12, 1).equals(" ")) { } else { System.out.println("FULL-WIDTH-KEY " + wsKey); }
  }
  private static void n8030CheckRoundedAmount() {
    BigDecimal r = wsAmount.setScale(2, RoundingMode.HALF_UP);
    if (!fitsS9V99(r)) wsArithError = 1; else wsPacked = r;
    if (wsPacked.compareTo(BigDecimal.ZERO) == 0 && wsAmount.compareTo(BigDecimal.ZERO) != 0) wsEdgeCode = padRight("TRUNCATION-RISK", 24);
  }
  private static void n8040CheckShortMove() {
    String v = padRight(wsAmountText, 6).substring(0, 6);
    wsShortAmount = v;
    if (!refMod(wsAmountText, 7, 2).equals("00")) wsEdgeCode = padRight("SHORT-PIC-TRUNCATION", 24);
  }
  private static void n8050CheckFeeOverlay() {
    if (wsFeeOverride.equals("Y")) wsFeeWaived = "Y"; else wsFeeWaived = "N";
  }
  private static void n8060CheckSignedZoned() {
    wsZoned = wsBalance;
    if (wsZoned.compareTo(BigDecimal.ZERO) < 0 && wsBalance.compareTo(BigDecimal.ZERO) >= 0) wsEdgeCode = padRight("SIGN-CONVERSION", 24);
  }
  private static void n8070CheckDateWindow() {
    if (refMod(wsOldDate, 1, 2).equals("00")) wsEdgeCode = padRight("Y2K-WINDOW-REVIEW", 24);
    if (wsOldDate.equals("991231")) wsEdgeCode = padRight("CENTURY-BOUNDARY", 24);
  }
  private static void n8080CheckRatePrecision() {
    if (wsFxRate.compareTo(new BigDecimal("1.0825")) != 0) wsEdgeCode = padRight("RATE-IS-FOUR-DECIMAL", 24);
  }
  private static void n8090CheckRecordType() {
    String c = refMod(inRecord, 1, 1);
    if (c.equals("A")) { } else if (c.equals("T")) { } else wsEdgeCode = padRight("BAD-RECORD-TYPE", 24);
  }
  private static void n8100CheckReturnCode() {
    if (!wsReturnReason.equals(padRight("", 3))) wsReturnCount = wsReturnCount + 1;
    if (wsReturnCount > wsRecordCount) returnCode = 12;
  }
  private static void n8110CheckAccumulator() {
    BigDecimal sum = wsReportTotal.add(wsAmount);
    if (!fitsS11V99(sum)) wsArithError = 1; else wsReportTotal = sum;
    if (wsReportTotal.compareTo(BigDecimal.ZERO) < 0) wsEdgeCode = padRight("NEGATIVE-CONTROL-TOTAL", 24);
  }
  private static void n8120CheckBranchBreak() {
    if (!wsBranch.equals(wsPrevBranch)) { n8130WriteBranchTotal(); wsPrevBranch = wsBranch; }
  }
  private static void n8130WriteBranchTotal() {
    if (!wsPrevBranch.equals(padRight("", 3))) {
      System.out.println("BRANCH BREAK " + wsPrevBranch);
      System.out.println("BRANCH TOTAL " + formatTotal(wsReportTotal));
    }
    wsReportTotal = BigDecimal.ZERO;
  }
  private static void n8140CheckInputShape() {
    if (inRecord.equals(padRight("", 160))) { inputError = true; wsEdgeCode = padRight("EMPTY-RECORD", 24); }
    if (!refMod(inRecord, 160, 1).equals(" ")) System.out.println("TRAILING-CONTENT PRESENT");
  }
  private static void n8150CheckMonthRollover() {
    if (refMod(inRecord, 40, 8).equals("20261231")) wsNewDate = "20270101";
    if (refMod(inRecord, 40, 8).equals("20260131")) wsNewDate = "20260201";
  }
  private static void n8160CheckLeapYear() {
    if (refMod(inRecord, 40, 4).equals("2024")) { wsLeapFlag = "Y"; wsDayBasis = 366; } else { wsLeapFlag = "N"; wsDayBasis = 365; }
  }
  private static void n8170CheckOddCents() {
    if (refMod(wsAmountText, 7, 2).equals("50")) wsEdgeCode = padRight("HALF-CENT-ROUNDING-CASE", 24);
    if (refMod(wsAmountText, 7, 2).equals("99")) wsEdgeCode = padRight("ROUND-UP-BOUNDARY", 24);
  }
  private static void n8180CheckStatusTransition() {
    String s = wsStatus.trim();
    if (s.equals("ACTIVE")) { } else if (s.equals("DORMANT")) { } else if (s.equals("BLOCKED")) { } else wsEdgeCode = padRight("UNKNOWN-STATE", 24);
  }
  private static void n8190CheckBalanceLimit() {
    if (wsBalance.compareTo(new BigDecimal("999999999.99")) > 0) wsEdgeCode = padRight("BALANCE-OVERFLOW", 24);
    if (wsBalance.compareTo(new BigDecimal("-999999999.99")) < 0) wsEdgeCode = padRight("BALANCE-UNDERFLOW", 24);
  }
  private static void n8200CheckReconciliation() {
    BigDecimal d = wsDebits.subtract(wsCredits);
    if (!fitsS11V99(d)) wsArithError = 1; else wsDelta = d;
    if (wsDelta.compareTo(BigDecimal.ZERO) == 0) { }
  }
  private static void n8210CheckCopybookCount() {
    if (wsItemCount < 1 || wsItemCount > 12) wsEdgeCode = padRight("OCCURS-COUNT-INVALID", 24);
    for (wsItemCount = 1; wsItemCount <= 1; wsItemCount++) { }
  }
  private static void n8220CheckCallInterface() {
    setLineSlice(1, 12, wsKey);
    setLineSlice(13, 24, wsEdgeCode);
    if (getLineSlice(1, 1).equals(" ")) wsEdgeCode = padRight("CALL-KEY-MISSING", 24);
  }
  private static void n8230CheckWriteStatus() {
    if (wsOutStatus.equals("00")) { } else { System.out.println("DATASET STATUS " + wsOutStatus); returnCode = 12; }
  }
  private static void n8240CheckReadStatus() {
    if (wsInStatus.equals("10")) endOfInput = true;
    else { if (!wsInStatus.equals("00")) System.out.println("INPUT STATUS " + wsInStatus); }
  }
  private static void n8250CheckCurrencyScale() {
    BigDecimal prod = wsAmount.multiply(new BigDecimal("1.0000"));
    BigDecimal r = prod.setScale(2, RoundingMode.HALF_UP);
    if (!fitsS9V99(r)) wsArithError = 1; else wsAmount = r;
    if (wsFxRate.compareTo(BigDecimal.ZERO) < 0) wsEdgeCode = padRight("NEGATIVE-FX-RATE", 24);
  }
  private static void n8260CheckNestedCallResult() {
    wsAbendCode = 0;
    if (wsAbendCode != 0) { System.out.println("CALLED ROUTINE RETURNED ERROR"); returnCode = 8; }
  }
  private static void n8270CheckAuditBalance() {
    BigDecimal sum = wsCycleTotal.add(wsAmount);
    if (!fitsS11V99(sum)) wsArithError = 1; else wsCycleTotal = sum;
    if (wsCycleTotal.compareTo(BigDecimal.ZERO) == 0) { }
  }
  private static void n8280CheckKeyUniqueness() {
    if (wsKey.equals(wsPrevKey) && !wsKey.equals(padRight("", 12))) wsEdgeCode = padRight("DUPLICATE-KEY", 24);
    wsPrevKey = wsKey;
  }
  private static void n8290CheckRunId() {
    if (wsNewDate.equals("00000000")) { System.out.println("RUN DATE NOT INITIALIZED"); returnCode = 12; }
    if (wsRecordCount == 0) System.out.println("EMPTY INPUT");
  }
  private static void n9900OperationalChecks() {
    n8000RejectAllZeroAmounts();
    n8010CheckNegativeBalance();
    n8020CheckLongKey();
    n8030CheckRoundedAmount();
    n8040CheckShortMove();
    n8050CheckFeeOverlay();
    n8060CheckSignedZoned();
    n8070CheckDateWindow();
    n8080CheckRatePrecision();
    n8090CheckRecordType();
    n8100CheckReturnCode();
    n8110CheckAccumulator();
    n8120CheckBranchBreak();
    n8130WriteBranchTotal();
    n8140CheckInputShape();
    n8150CheckMonthRollover();
    n8160CheckLeapYear();
    n8170CheckOddCents();
    n8180CheckStatusTransition();
    n8190CheckBalanceLimit();
    n8200CheckReconciliation();
    n8210CheckCopybookCount();
    n8220CheckCallInterface();
    n8230CheckWriteStatus();
    n8240CheckReadStatus();
    n8250CheckCurrencyScale();
    n8260CheckNestedCallResult();
    n8270CheckAuditBalance();
    n8280CheckKeyUniqueness();
    n8290CheckRunId();
  }
}
