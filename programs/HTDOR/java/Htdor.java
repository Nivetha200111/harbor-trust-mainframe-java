import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;

public class Htdor {
  private String wsInStatus = "  ";
  private String wsOutStatus = "  ";
  private String wsEof = "N";
  private String wsError = "N";
  private long wsRecordCount = 0;
  private long wsAcceptCount = 0;
  private long wsRejectCount = 0;
  private int wsArithError = 0;
  private String wsEdgeCode = "                        ";
  private String wsKey = "            ";
  private String wsPrevKey = "            ";
  private String wsBranch = "   ";
  private String wsPrevBranch = "   ";
  private String wsStatus = "        ";
  private BigDecimal wsAmount = BigDecimal.ZERO.setScale(2);
  private BigDecimal wsBalance = BigDecimal.ZERO.setScale(2);
  private BigDecimal wsPrincipal = BigDecimal.ZERO.setScale(2);
  private BigDecimal wsRate = BigDecimal.ZERO.setScale(4);
  private BigDecimal wsFxRate = BigDecimal.ZERO.setScale(4);
  private BigDecimal wsInterest = BigDecimal.ZERO.setScale(2);
  private BigDecimal wsFee = BigDecimal.ZERO.setScale(2);
  private String wsFeeWaived = "N";
  private BigDecimal wsCheckAmt = BigDecimal.ZERO.setScale(2);
  private String wsReturnReason = "   ";
  private long wsReturnCount = 0;
  private BigDecimal wsEntryAmt = BigDecimal.ZERO.setScale(2);
  private BigDecimal wsCycleTotal = BigDecimal.ZERO.setScale(2);
  private BigDecimal wsDebits = BigDecimal.ZERO.setScale(2);
  private BigDecimal wsCredits = BigDecimal.ZERO.setScale(2);
  private BigDecimal wsDelta = BigDecimal.ZERO.setScale(2);
  private long wsOutOfBalance = 0;
  private long wsDaysInactive = 0;
  private long wsDayBasis = 365;
  private String wsLeapFlag = "N";
  private String wsAmountText = "        ";
  private String wsShortAmount = "      ";
  private String wsRawBase = "                                                                                                                        ";
  private int wsItemCount = 0;
  private BigDecimal wsPacked = BigDecimal.ZERO.setScale(2);
  private BigDecimal wsZoned = BigDecimal.ZERO.setScale(2);
  private String wsOldDate = "991231";
  private String wsCentury = "20";
  private String wsNewDate = "00000000";
  private String wsLine = "                                                                                                                                                                                                        ";
  private BigDecimal wsReportTotal = BigDecimal.ZERO.setScale(2);
  private long wsReadTime = 0;
  private long wsWriteTime = 0;
  private long wsAbendCode = 0;
  private String inRecord = "                                                                                                                                                                ";
  private String outRecord = "                                                                                                                                                                                                        ";
  private int returnCode = 0;
  private BufferedReader inReader = null;
  private BufferedWriter outWriter = null;
  private boolean ebcdicMode = false;

  public static void main(String[] args) throws Exception {
    Htdor h = new Htdor();
    h._0000main();
    if (h.returnCode != 0) System.exit(h.returnCode);
  }

  private void _0000main() throws Exception {
    _1000initialize();
    while (!wsEof.equals("Y")) {
      _2000readInput();
      if (!wsEof.equals("Y")) {
        _3000validateRecord();
        if (!wsError.equals("Y")) {
          _4000processRecord();
          _5000writeResult();
        } else {
          _6000rejectRecord();
        }
      }
    }
    _7000finalize();
  }

  private void _1000initialize() throws Exception {
    String inPath = System.getenv("DD_INPUT-FILE");
    String outPath = System.getenv("DD_OUTPUT-FILE");
    String coll = System.getenv("DIOCLE_COLLATION");
    String clk = System.getenv("DIOCLE_CLOCK");
    if (clk != null && clk.length() == 0) ebcdicMode = false;
    ebcdicMode = "EBCDIC".equals(coll);
    try {
      if (inPath == null) throw new java.io.IOException("missing");
      inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inPath), StandardCharsets.ISO_8859_1));
      wsInStatus = "00";
    } catch (Exception e) {
      wsInStatus = "35";
      emit("OPEN INPUT FAILED " + wsInStatus);
      returnCode = 12;
      wsEof = "Y";
    }
    try {
      if (outPath == null) throw new java.io.IOException("missing");
      outWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPath), StandardCharsets.ISO_8859_1));
      wsOutStatus = "00";
    } catch (Exception e) {
      wsOutStatus = "35";
      emit("OPEN OUTPUT FAILED " + wsOutStatus);
      returnCode = 12;
      wsEof = "Y";
    }
    wsRecordCount = 0;
    wsAcceptCount = 0;
    wsRejectCount = 0;
    wsEdgeCode = spaces(24);
    _1100checkRunControl();
    _1200initializeTotals();
  }

  private void _1100checkRunControl() {
    String part = wsOldDate.substring(0, 2);
    int v = 0;
    try { v = Integer.parseInt(part); } catch (Exception e) { v = 0; }
    if (v > 80) wsCentury = "19";
    else wsCentury = "20";
    wsNewDate = wsCentury + wsOldDate;
  }

  private void _1200initializeTotals() {
    wsReportTotal = BigDecimal.ZERO.setScale(2);
    wsCycleTotal = BigDecimal.ZERO.setScale(2);
    wsDebits = BigDecimal.ZERO.setScale(2);
    wsCredits = BigDecimal.ZERO.setScale(2);
    wsDelta = BigDecimal.ZERO.setScale(2);
    wsPrevKey = spaces(12);
    wsPrevBranch = spaces(3);
  }

  private void _2000readInput() throws Exception {
    if (inReader == null) {
      wsInStatus = "10";
      wsEof = "Y";
      return;
    }
    String line = null;
    try {
      line = inReader.readLine();
    } catch (Exception e) {
      wsInStatus = "30";
      emit("READ FAILURE " + wsInStatus);
      wsError = "Y";
      wsEof = "Y";
      return;
    }
    if (line == null) {
      wsInStatus = "10";
      wsEof = "Y";
    } else {
      wsInStatus = "00";
      wsRecordCount = wsRecordCount + 1;
      if (line.length() < 160) line = line + spaces(160 - line.length());
      else if (line.length() > 160) line = line.substring(0, 160);
      inRecord = line;
      wsRawBase = inRecord.substring(0, 120);
      wsKey = inRecord.substring(0, 12);
      wsBranch = inRecord.substring(12, 15);
      wsAmountText = inRecord.substring(15, 23);
      String s24 = inRecord.substring(23, 31);
      if (s24.length() >= 6) wsShortAmount = s24.substring(0, 6);
      else wsShortAmount = padRight(s24, 6);
      wsStatus = s24;
    }
    if (!wsInStatus.equals("00") && !wsInStatus.equals("10")) {
      emit("READ FAILURE " + wsInStatus);
      wsError = "Y";
      wsEof = "Y";
    }
  }

  private void _3000validateRecord() {
    wsError = "N";
    if (wsKey.equals(spaces(12))) _3100badKey();
    if (!isNumeric(wsAmountText)) _3200badAmount();
    if (wsStatus.equals(spaces(8))) _3300badStatus();
    if (!isAlphabetic(wsBranch)) _3400badBranch();
    if (wsRecordCount > 1 && compareAlpha(wsKey, wsPrevKey) < 0) wsEdgeCode = padRight("SORT-ORDER", 24);
    wsPrevKey = wsKey;
    _3500checkMultiRecordType();
    _3600checkDateBoundary();
  }

  private void _3100badKey() {
    wsError = "Y";
    wsEdgeCode = padRight("MISSING-KEY", 24);
  }

  private void _3200badAmount() {
    wsError = "Y";
    wsEdgeCode = padRight("INVALID-AMOUNT", 24);
  }

  private void _3300badStatus() {
    wsError = "Y";
    wsEdgeCode = padRight("INVALID-STATUS", 24);
  }

  private void _3400badBranch() {
    wsError = "Y";
    wsEdgeCode = padRight("INVALID-BRANCH", 24);
  }

  private void _3500checkMultiRecordType() {
    String c1 = inRecord.substring(0, 1);
    if (!c1.equals("A") && !c1.equals("T")) wsEdgeCode = padRight("UNKNOWN-RECORD-TYPE", 24);
    String c2 = inRecord.substring(1, 2);
    if (c2.equals("D")) wsItemCount = 2;
    else wsItemCount = 1;
  }

  private void _3600checkDateBoundary() {
    String d = inRecord.substring(39, 47);
    if (d.equals("20261231")) wsEdgeCode = padRight("YEAR-END-ROLLOVER", 24);
    if (d.equals("20260228")) wsEdgeCode = padRight("NON-LEAP-FEB-END", 24);
  }

  private void _4000processRecord() {
    wsArithError = 0;
    long iv = 0;
    try { iv = Long.parseLong(wsAmountText); } catch (Exception e) { iv = 0; }
    wsAmount = BigDecimal.valueOf(iv).setScale(2);
    wsBalance = wsAmount;
    wsPrincipal = wsAmount;
    BigDecimal sum = wsReportTotal.add(wsAmount);
    BigDecimal mx = new BigDecimal("99999999999.99");
    BigDecimal mn = new BigDecimal("-99999999999.99");
    if (sum.compareTo(mx) > 0 || sum.compareTo(mn) < 0) wsArithError = 1;
    else wsReportTotal = sum;
    wsRate = new BigDecimal("0.0525");
    wsFxRate = new BigDecimal("1.0825");
    wsCheckAmt = wsAmount;
    wsEntryAmt = wsAmount;
    wsFee = BigDecimal.ZERO.setScale(2);
    wsFeeWaived = inRecord.substring(119, 120);
    wsInterest = BigDecimal.ZERO.setScale(2);
    if (wsDaysInactive >= 730 && eqPadded(wsStatus, "OPEN")) wsStatus = padRight("DORMANT", 8);
    if (wsBalance.compareTo(BigDecimal.ZERO.setScale(2)) < 0) wsEdgeCode = padRight("REVIEW", 24);
    wsLine = setRef(wsLine, 1, 12, wsKey);
    wsLine = setRef(wsLine, 13, 3, wsBranch);
    wsLine = setRef(wsLine, 16, 24, wsEdgeCode);
    wsLine = setRef(wsLine, 40, 12, wsAmountText);
    wsLine = setRef(wsLine, 52, 8, wsStatus);
    wsAcceptCount = wsAcceptCount + 1;
  }

  private void _5000writeResult() throws Exception {
    outRecord = wsLine;
    try {
      outWriter.write(rtrim(outRecord));
      outWriter.newLine();
      outWriter.flush();
      wsOutStatus = "00";
    } catch (Exception e) {
      wsOutStatus = "30";
    }
    if (!wsOutStatus.equals("00")) {
      emit("WRITE FAILURE " + wsOutStatus);
      returnCode = 12;
      wsEof = "Y";
    } else {
      wsWriteTime = wsWriteTime + 1;
    }
  }

  private void _6000rejectRecord() throws Exception {
    wsRejectCount = wsRejectCount + 1;
    emit("REJECT " + formatCount(wsRecordCount) + " " + wsEdgeCode);
    wsLine = spaces(200);
    wsLine = setRef(wsLine, 1, 6, "REJECT");
    wsLine = setRef(wsLine, 8, 12, wsKey);
    wsLine = setRef(wsLine, 24, 24, wsEdgeCode);
    outRecord = wsLine;
    try {
      outWriter.write(rtrim(outRecord));
      outWriter.newLine();
      outWriter.flush();
      wsOutStatus = "00";
    } catch (Exception e) {
      wsOutStatus = "30";
    }
  }

  private void _7000finalize() throws Exception {
    _7100checkControlTotals();
    _7200reportRunCounts();
    try { if (inReader != null) inReader.close(); wsInStatus = "00"; } catch (Exception e) { wsInStatus = "30"; }
    if (inReader == null && wsEof.equals("Y") && wsRecordCount == 0 && returnCode == 0) wsInStatus = "00";
    try { if (outWriter != null) outWriter.close(); wsOutStatus = "00"; } catch (Exception e) { wsOutStatus = "30"; }
    if (outWriter == null && returnCode == 12) { }
    else {
      if (!wsInStatus.equals("00") && !wsInStatus.equals("10")) { }
    }
    if (!wsInStatus.equals("00") && !wsInStatus.equals("10") && wsRecordCount > 0) emit("CLOSE IN " + wsInStatus);
    if (!wsOutStatus.equals("00")) emit("CLOSE OUT " + wsOutStatus);
    if (wsRejectCount > 0) returnCode = 4;
  }

  private void _7100checkControlTotals() {
    if (wsAcceptCount + wsRejectCount != wsRecordCount) {
      emit("CONTROL COUNT MISMATCH");
      returnCode = 12;
    }
    if (wsArithError != 0) {
      emit("ARITHMETIC SIZE ERROR");
      returnCode = 8;
    }
  }

  private void _7200reportRunCounts() {
    emit("PROGRAM HTDOR COMPLETE");
    emit("READ=" + formatCount(wsRecordCount));
    emit("ACCEPT=" + formatCount(wsAcceptCount));
    emit("REJECT=" + formatCount(wsRejectCount));
    emit("TOTAL=" + formatTotal(wsReportTotal));
    emit("EDGE=" + wsEdgeCode);
  }

  private void _8000rejectAllZeroAmounts() {
    if (wsAmount.compareTo(BigDecimal.ZERO.setScale(2)) == 0 && eqPadded(wsStatus, "ACTIVE")) wsEdgeCode = padRight("ZERO-ACTIVE-AMOUNT", 24);
  }

  private void _8010checkNegativeBalance() {
    if (wsBalance.compareTo(BigDecimal.ZERO.setScale(2)) < 0) wsEdgeCode = padRight("NEGATIVE-BALANCE", 24);
  }

  private void _8020checkLongKey() {
    if (wsKey.substring(11, 12).equals(" ")) { } else emit("FULL-WIDTH-KEY " + wsKey);
  }

  private void _8030checkRoundedAmount() {
    wsPacked = wsAmount.setScale(2, RoundingMode.HALF_UP);
    if (wsPacked.compareTo(BigDecimal.ZERO.setScale(2)) == 0 && wsAmount.compareTo(BigDecimal.ZERO.setScale(2)) != 0) wsEdgeCode = padRight("TRUNCATION-RISK", 24);
  }

  private void _8040checkShortMove() {
    String t = wsAmountText;
    if (t.length() >= 6) wsShortAmount = t.substring(0, 6);
    else wsShortAmount = padRight(t, 6);
    if (!wsAmountText.substring(6, 8).equals("00")) wsEdgeCode = padRight("SHORT-PIC-TRUNCATION", 24);
  }

  private void _8050checkFeeOverlay() {
    String ov = wsRawBase.substring(119, 120);
    if (ov.equals("Y")) wsFeeWaived = "Y";
    else wsFeeWaived = "N";
  }

  private void _8060checkSignedZoned() {
    wsZoned = wsBalance;
    if (wsZoned.compareTo(BigDecimal.ZERO.setScale(2)) < 0 && wsBalance.compareTo(BigDecimal.ZERO.setScale(2)) >= 0) wsEdgeCode = padRight("SIGN-CONVERSION", 24);
  }

  private void _8070checkDateWindow() {
    if (wsOldDate.substring(0, 2).equals("00")) wsEdgeCode = padRight("Y2K-WINDOW-REVIEW", 24);
    if (wsOldDate.equals("991231")) wsEdgeCode = padRight("CENTURY-BOUNDARY", 24);
  }

  private void _8080checkRatePrecision() {
    if (wsFxRate.compareTo(new BigDecimal("1.0825")) != 0) wsEdgeCode = padRight("RATE-IS-FOUR-DECIMAL", 24);
  }

  private void _8090checkRecordType() {
    String c = inRecord.substring(0, 1);
    if (c.equals("A")) { }
    else if (c.equals("T")) { }
    else wsEdgeCode = padRight("BAD-RECORD-TYPE", 24);
  }

  private void _8100checkReturnCode() {
    if (!wsReturnReason.equals(spaces(3))) wsReturnCount = wsReturnCount + 1;
    if (wsReturnCount > wsRecordCount) returnCode = 12;
  }

  private void _8110checkAccumulator() {
    BigDecimal sum = wsReportTotal.add(wsAmount);
    BigDecimal mx = new BigDecimal("99999999999.99");
    BigDecimal mn = new BigDecimal("-99999999999.99");
    if (sum.compareTo(mx) > 0 || sum.compareTo(mn) < 0) wsArithError = 1;
    else wsReportTotal = sum;
    if (wsReportTotal.compareTo(BigDecimal.ZERO.setScale(2)) < 0) wsEdgeCode = padRight("NEGATIVE-CONTROL-TOTAL", 24);
  }

  private void _8120checkBranchBreak() throws Exception {
    if (!wsBranch.equals(wsPrevBranch)) {
      _8130writeBranchTotal();
      wsPrevBranch = wsBranch;
    }
  }

  private void _8130writeBranchTotal() {
    if (!wsPrevBranch.equals(spaces(3))) {
      emit("BRANCH BREAK " + wsPrevBranch);
      emit("BRANCH TOTAL " + formatTotal(wsReportTotal));
    }
    wsReportTotal = BigDecimal.ZERO.setScale(2);
  }

  private void _8140checkInputShape() {
    if (inRecord.equals(spaces(160))) {
      wsError = "Y";
      wsEdgeCode = padRight("EMPTY-RECORD", 24);
    }
    if (!inRecord.substring(159, 160).equals(" ")) emit("TRAILING-CONTENT PRESENT");
  }

  private void _8150checkMonthRollover() {
    String d = inRecord.substring(39, 47);
    if (d.equals("20261231")) wsNewDate = "20270101";
    if (d.equals("20260131")) wsNewDate = "20260201";
  }

  private void _8160checkLeapYear() {
    String d = inRecord.substring(39, 43);
    if (d.equals("2024")) {
      wsLeapFlag = "Y";
      wsDayBasis = 366;
    } else {
      wsLeapFlag = "N";
      wsDayBasis = 365;
    }
  }

  private void _8170checkOddCents() {
    if (wsAmountText.substring(6, 8).equals("50")) wsEdgeCode = padRight("HALF-CENT-ROUNDING-CASE", 24);
    if (wsAmountText.substring(6, 8).equals("99")) wsEdgeCode = padRight("ROUND-UP-BOUNDARY", 24);
  }

  private void _8180checkStatusTransition() {
    if (eqPadded(wsStatus, "ACTIVE")) { }
    else if (eqPadded(wsStatus, "DORMANT")) { }
    else if (eqPadded(wsStatus, "BLOCKED")) { }
    else wsEdgeCode = padRight("UNKNOWN-STATE", 24);
  }

  private void _8190checkBalanceLimit() {
    if (wsBalance.compareTo(new BigDecimal("999999999.99")) > 0) wsEdgeCode = padRight("BALANCE-OVERFLOW", 24);
    if (wsBalance.compareTo(new BigDecimal("-999999999.99")) < 0) wsEdgeCode = padRight("BALANCE-UNDERFLOW", 24);
  }

  private void _8200checkReconciliation() {
    BigDecimal d = wsDebits.subtract(wsCredits);
    BigDecimal mx = new BigDecimal("99999999999.99");
    BigDecimal mn = new BigDecimal("-99999999999.99");
    if (d.compareTo(mx) > 0 || d.compareTo(mn) < 0) wsArithError = 1;
    else wsDelta = d;
    if (wsDelta.compareTo(BigDecimal.ZERO.setScale(2)) == 0) { }
  }

  private void _8210checkCopybookCount() {
    if (wsItemCount < 1 || wsItemCount > 12) wsEdgeCode = padRight("OCCURS-COUNT-INVALID", 24);
    for (wsItemCount = 1; wsItemCount <= 1; ) {
      break;
    }
  }

  private void _8220checkCallInterface() {
    wsLine = setRef(wsLine, 1, 12, wsKey);
    wsLine = setRef(wsLine, 13, 24, wsEdgeCode);
    if (wsLine.substring(0, 1).equals(" ")) wsEdgeCode = padRight("CALL-KEY-MISSING", 24);
  }

  private void _8230checkWriteStatus() {
    if (wsOutStatus.equals("00")) { }
    else {
      emit("DATASET STATUS " + wsOutStatus);
      returnCode = 12;
    }
  }

  private void _8240checkReadStatus() {
    if (wsInStatus.equals("10")) wsEof = "Y";
    else {
      if (!wsInStatus.equals("00")) emit("INPUT STATUS " + wsInStatus);
    }
  }

  private void _8250checkCurrencyScale() {
    BigDecimal p = wsAmount.multiply(new BigDecimal("1.0000"));
    try { p = p.setScale(2, RoundingMode.HALF_UP); } catch (Exception e) { wsArithError = 1; return; }
    BigDecimal mx = new BigDecimal("999999999.99");
    BigDecimal mn = new BigDecimal("-999999999.99");
    if (p.compareTo(mx) > 0 || p.compareTo(mn) < 0) wsArithError = 1;
    else wsAmount = p;
    if (wsFxRate.compareTo(BigDecimal.ZERO.setScale(4)) < 0) wsEdgeCode = padRight("NEGATIVE-FX-RATE", 24);
  }

  private void _8260checkNestedCallResult() {
    wsAbendCode = 0;
    if (wsAbendCode != 0) {
      emit("CALLED ROUTINE RETURNED ERROR");
      returnCode = 8;
    }
  }

  private void _8270checkAuditBalance() {
    BigDecimal sum = wsCycleTotal.add(wsAmount);
    BigDecimal mx = new BigDecimal("99999999999.99");
    BigDecimal mn = new BigDecimal("-99999999999.99");
    if (sum.compareTo(mx) > 0 || sum.compareTo(mn) < 0) wsArithError = 1;
    else wsCycleTotal = sum;
    if (wsCycleTotal.compareTo(BigDecimal.ZERO.setScale(2)) == 0) { }
  }

  private void _8280checkKeyUniqueness() {
    if (wsKey.equals(wsPrevKey) && !wsKey.equals(spaces(12))) wsEdgeCode = padRight("DUPLICATE-KEY", 24);
    wsPrevKey = wsKey;
  }

  private void _8290checkRunId() {
    if (wsNewDate.equals("00000000")) {
      emit("RUN DATE NOT INITIALIZED");
      returnCode = 12;
    }
    if (wsRecordCount == 0) emit("EMPTY INPUT");
  }

  private void _9900operationalChecks() throws Exception {
    _8000rejectAllZeroAmounts();
    _8010checkNegativeBalance();
    _8020checkLongKey();
    _8030checkRoundedAmount();
    _8040checkShortMove();
    _8050checkFeeOverlay();
    _8060checkSignedZoned();
    _8070checkDateWindow();
    _8080checkRatePrecision();
    _8090checkRecordType();
    _8100checkReturnCode();
    _8110checkAccumulator();
    _8120checkBranchBreak();
    _8130writeBranchTotal();
    _8140checkInputShape();
    _8150checkMonthRollover();
    _8160checkLeapYear();
    _8170checkOddCents();
    _8180checkStatusTransition();
    _8190checkBalanceLimit();
    _8200checkReconciliation();
    _8210checkCopybookCount();
    _8220checkCallInterface();
    _8230checkWriteStatus();
    _8240checkReadStatus();
    _8250checkCurrencyScale();
    _8260checkNestedCallResult();
    _8270checkAuditBalance();
    _8280checkKeyUniqueness();
    _8290checkRunId();
  }

  int compareAlpha(String a, String b) {
    if (!ebcdicMode) return a.compareTo(b);
    byte[] ba = toCp037(a);
    byte[] bb = toCp037(b);
    int n = Math.min(ba.length, bb.length);
    for (int i = 0; i < n; i++) {
      int va = ba[i] & 255;
      int vb = bb[i] & 255;
      if (va != vb) return va < vb ? -1 : 1;
    }
    if (ba.length == bb.length) return 0;
    return ba.length < bb.length ? -1 : 1;
  }

  static byte[] toCp037(String s) {
    byte[] r = new byte[s.length()];
    for (int i = 0; i < s.length(); i++) r[i] = charToCp037(s.charAt(i));
    return r;
  }

  static byte charToCp037(char c) {
    if (c == ' ') return 0x40;
    if (c >= 'a' && c <= 'i') return (byte)(0x81 + (c - 'a'));
    if (c >= 'j' && c <= 'r') return (byte)(0x91 + (c - 'j'));
    if (c >= 's' && c <= 'z') return (byte)(0xA2 + (c - 's'));
    if (c >= 'A' && c <= 'I') return (byte)(0xC1 + (c - 'A'));
    if (c >= 'J' && c <= 'R') return (byte)(0xD1 + (c - 'J'));
    if (c >= 'S' && c <= 'Z') return (byte)(0xE2 + (c - 'S'));
    if (c >= '0' && c <= '9') return (byte)(0xF0 + (c - '0'));
    if (c < 256) return (byte)c;
    return 0x3F;
  }

  static String spaces(int n) {
    if (n <= 0) return "";
    StringBuilder sb = new StringBuilder(n);
    for (int i = 0; i < n; i++) sb.append(' ');
    return sb.toString();
  }

  static String padRight(String s, int len) {
    if (s == null) s = "";
    if (s.length() > len) return s.substring(0, len);
    if (s.length() == len) return s;
    return s + spaces(len - s.length());
  }

  static String setRef(String base, int start1, int len, String val) {
    String v = padRight(val, len);
    int st = start1 - 1;
    return base.substring(0, st) + v + base.substring(st + len);
  }

  static boolean isNumeric(String s) {
    if (s.length() == 0) return false;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c < '0' || c > '9') return false;
    }
    return true;
  }

  static boolean isAlphabetic(String s) {
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == ' ') continue;
      return false;
    }
    return true;
  }

  static boolean eqPadded(String a, String b) {
    int n = Math.max(a.length(), b.length());
    return padRight(a, n).equals(padRight(b, n));
  }

  static String formatCount(long v) {
    String s = Long.toString(v);
    boolean neg = s.startsWith("-");
    if (neg) s = s.substring(1);
    while (s.length() < 10) s = "0" + s;
    if (neg) s = "-" + s;
    return s;
  }

  static String formatTotal(BigDecimal v) {
    boolean neg = v.signum() < 0;
    BigDecimal a = v.abs().setScale(2, RoundingMode.HALF_UP);
    String digits = a.movePointRight(2).toBigIntegerExact().toString();
    while (digits.length() < 13) digits = "0" + digits;
    String ip = digits.substring(0, digits.length() - 2);
    String fp = digits.substring(digits.length() - 2);
    return (neg ? "-" : "+") + ip + "." + fp;
  }

  static String rtrim(String s) {
    int e = s.length();
    while (e > 0 && s.charAt(e - 1) == ' ') e--;
    return s.substring(0, e);
  }

  static void emit(String s) {
    byte[] b = s.getBytes(StandardCharsets.ISO_8859_1);
    System.out.write(b, 0, b.length);
    System.out.write(10);
    System.out.flush();
  }
}
