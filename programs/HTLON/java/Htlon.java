public class Htlon {
  static String wsInStatus = "  ";
  static String wsOutStatus = "  ";
  static boolean endOfInput = false;
  static String wsEof = "N";
  static String wsError = "N";
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
  static java.math.BigDecimal wsAmount = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsBalance = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsPrincipal = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsRate = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsFxRate = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsInterest = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsFee = java.math.BigDecimal.ZERO;
  static String wsFeeWaived = "N";
  static java.math.BigDecimal wsCheckAmt = java.math.BigDecimal.ZERO;
  static String wsReturnReason = "   ";
  static long wsReturnCount = 0;
  static java.math.BigDecimal wsEntryAmt = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsCycleTotal = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsDebits = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsCredits = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsDelta = java.math.BigDecimal.ZERO;
  static long wsOutOfBalance = 0;
  static int wsDaysInactive = 0;
  static int wsDayBasis = 365;
  static String wsLeapFlag = "N";
  static String wsAmountText = "        ";
  static String wsShortAmount = "      ";
  static String wsRawBase = "                                                                                                                        ";
  static int wsItemCount = 0;
  static String[] wsItems = new String[12];
  static java.math.BigDecimal wsPacked = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsZoned = java.math.BigDecimal.ZERO;
  static String wsOldDate = "991231";
  static int wsCentury = 20;
  static String wsNewDate = "00000000";
  static StringBuilder wsLine = new StringBuilder("                                                                                                                                                                                                        ");
  static java.math.BigDecimal wsReportTotal = java.math.BigDecimal.ZERO;
  static long wsReadTime = 0;
  static long wsWriteTime = 0;
  static int wsAbendCode = 0;
  static String inRecord = "                                                                                                                                                                ";
  static String outRecord = "                                                                                                                                                                                                        ";
  static int returnCode = 0;
  static boolean ebcdicMode = false;
  static java.io.BufferedReader inReader = null;
  static java.io.BufferedWriter outWriter = null;
  public static void main(String[] args) throws Exception {
    p0000Main();
    if (returnCode != 0) System.exit(returnCode);
  }
  static boolean isInputError() { return wsError.equals("Y"); }
  static String spaces(int n) { StringBuilder b = new StringBuilder(); for (int i = 0; i < n; i++) b.append(' '); return b.toString(); }
  static String rpad(String s, int len) { if (s == null) s = ""; if (s.length() > len) return s.substring(0, len); if (s.length() == len) return s; StringBuilder b = new StringBuilder(s); while (b.length() < len) b.append(' '); return b.toString(); }
  static String rtrim(String s) { int i = s.length(); while (i > 0 && s.charAt(i - 1) == ' ') i--; return s.substring(0, i); }
  static String slice(String s, int pos, int len) { int start = pos - 1; int end = start + len; if (start >= s.length()) return spaces(len); if (end > s.length()) { String part = s.substring(start); return rpad(part, len); } return s.substring(start, end); }
  static void setSlice(StringBuilder sb, int pos, int len, String val) { String v = rpad(val, len); for (int i = 0; i < len; i++) { int idx = pos - 1 + i; if (idx >= 0 && idx < sb.length()) sb.setCharAt(idx, v.charAt(i)); } }
  static boolean isNumeric(String s) { if (s.length() == 0) return false; for (int i = 0; i < s.length(); i++) { char c = s.charAt(i); if (c < '0' || c > '9') return false; } return true; }
  static boolean isAlphabetic(String s) { for (int i = 0; i < s.length(); i++) { char c = s.charAt(i); if (c == ' ') continue; if (c >= 'A' && c <= 'Z') continue; if (c >= 'a' && c <= 'z') continue; return false; } return true; }
  static int compareAlpha(String a, String b) { if (ebcdicMode) { try { java.nio.charset.Charset cs = java.nio.charset.Charset.forName("Cp037"); byte[] ba = a.getBytes(cs); byte[] bb = b.getBytes(cs); int n = Math.min(ba.length, bb.length); for (int i = 0; i < n; i++) { int x = ba[i] & 0xFF; int y = bb[i] & 0xFF; if (x != y) return x - y; } return ba.length - bb.length; } catch (Exception e) { } } return a.compareTo(b); }
  static boolean alphaEquals(String a, String b) { int m = Math.max(a.length(), b.length()); String ap = rpad(a, m); String bp = rpad(b, m); if (ebcdicMode) return compareAlpha(ap, bp) == 0; return ap.equals(bp); }
  static String formatCount9(long v) { long t = v % 1000000000L; if (t < 0) t += 1000000000L; String s = Long.toString(t); while (s.length() < 10) s = "0" + s; return s; }
  static String formatTotal(java.math.BigDecimal v) { boolean neg = v.signum() < 0; java.math.BigDecimal a = v.abs().setScale(2, java.math.RoundingMode.HALF_UP); String u = a.unscaledValue().toString(); if (u.startsWith("-")) u = u.substring(1); while (u.length() < 13) u = "0" + u; String ip = u.substring(0, u.length() - 2); String fp = u.substring(u.length() - 2); if (neg) return "-" + ip + "." + fp; return "+" + ip + "." + fp; }
  private static void p0000Main() throws Exception {
    p1000Initialize();
    while (!endOfInput) {
      p2000ReadInput();
      if (!endOfInput) {
        p3000ValidateRecord();
        if (!isInputError()) {
          p4000ProcessRecord();
          p5000WriteResult();
        } else {
          p6000RejectRecord();
        }
      }
    }
    p7000Finalize();
  }
  private static void p1000Initialize() throws Exception {
    String clk = System.getenv("DIOCLE_CLOCK");
    String coll = System.getenv("DIOCLE_COLLATION");
    ebcdicMode = "EBCDIC".equals(coll);
    if (clk != null && clk.length() == 0) ebcdicMode = ebcdicMode;
    String inPath = System.getenv("DD_INPUT-FILE");
    String outPath = System.getenv("DD_OUTPUT-FILE");
    try {
      if (inPath == null) throw new java.io.IOException("missing");
      inReader = java.nio.file.Files.newBufferedReader(java.nio.file.Paths.get(inPath), java.nio.charset.StandardCharsets.ISO_8859_1);
      wsInStatus = "00";
    } catch (Exception e) {
      wsInStatus = "35";
      System.out.println("OPEN INPUT FAILED " + wsInStatus);
      returnCode = 12;
      endOfInput = true;
      wsEof = "Y";
    }
    try {
      if (outPath == null) throw new java.io.IOException("missing");
      outWriter = java.nio.file.Files.newBufferedWriter(java.nio.file.Paths.get(outPath), java.nio.charset.StandardCharsets.ISO_8859_1, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING, java.nio.file.StandardOpenOption.WRITE);
      wsOutStatus = "00";
    } catch (Exception e) {
      wsOutStatus = "35";
      System.out.println("OPEN OUTPUT FAILED " + wsOutStatus);
      returnCode = 12;
      endOfInput = true;
      wsEof = "Y";
    }
    wsRecordCount = 0;
    wsAcceptCount = 0;
    wsRejectCount = 0;
    wsEdgeCode = spaces(24);
    p1100CheckRunControl();
    p1200InitializeTotals();
  }
  private static void p1100CheckRunControl() {
    int pref = 0;
    try { pref = Integer.parseInt(wsOldDate.substring(0, 2)); } catch (Exception e) { pref = 0; }
    if (pref > 80) wsCentury = 19; else wsCentury = 20;
    String c = wsCentury < 10 ? "0" + wsCentury : Integer.toString(wsCentury);
    wsNewDate = c + wsOldDate;
  }
  private static void p1200InitializeTotals() {
    wsReportTotal = java.math.BigDecimal.ZERO;
    wsCycleTotal = java.math.BigDecimal.ZERO;
    wsDebits = java.math.BigDecimal.ZERO;
    wsCredits = java.math.BigDecimal.ZERO;
    wsDelta = java.math.BigDecimal.ZERO;
    wsPrevKey = spaces(12);
    wsPrevBranch = spaces(3);
  }
  private static void p2000ReadInput() throws Exception {
    if (endOfInput) return;
    try {
      String line = null;
      if (inReader != null) line = inReader.readLine();
      if (line == null) {
        endOfInput = true;
        wsEof = "Y";
        wsInStatus = "10";
      } else {
        wsInStatus = "00";
        wsRecordCount = (wsRecordCount + 1) % 1000000000L;
        if (line.length() > 160) inRecord = line.substring(0, 160);
        else if (line.length() < 160) inRecord = rpad(line, 160);
        else inRecord = line;
        wsRawBase = inRecord.length() >= 120 ? inRecord.substring(0, 120) : rpad(inRecord, 120);
        wsKey = slice(inRecord, 1, 12);
        wsBranch = slice(inRecord, 13, 3);
        wsAmountText = slice(inRecord, 16, 8);
        String s8 = slice(inRecord, 24, 8);
        if (s8.length() > 6) wsShortAmount = s8.substring(0, 6); else wsShortAmount = rpad(s8, 6);
        wsStatus = s8;
      }
    } catch (Exception e) {
      wsInStatus = "30";
      System.out.println("READ FAILURE " + wsInStatus);
      wsError = "Y";
      endOfInput = true;
      wsEof = "Y";
      return;
    }
    if (!wsInStatus.equals("00") && !wsInStatus.equals("10")) {
      System.out.println("READ FAILURE " + wsInStatus);
      wsError = "Y";
      endOfInput = true;
      wsEof = "Y";
    }
  }
  private static void p3000ValidateRecord() {
    wsError = "N";
    if (wsKey.equals(spaces(12))) p3100BadKey();
    if (!isNumeric(wsAmountText)) p3200BadAmount();
    if (wsStatus.equals(spaces(8))) p3300BadStatus();
    if (!isAlphabetic(wsBranch)) p3400BadBranch();
    if (wsRecordCount > 1 && compareAlpha(wsKey, wsPrevKey) < 0) wsEdgeCode = rpad("SORT-ORDER", 24);
    wsPrevKey = wsKey;
    p3500CheckMultiRecordType();
    p3600CheckDateBoundary();
  }
  private static void p3100BadKey() { wsError = "Y"; wsEdgeCode = rpad("MISSING-KEY", 24); }
  private static void p3200BadAmount() { wsError = "Y"; wsEdgeCode = rpad("INVALID-AMOUNT", 24); }
  private static void p3300BadStatus() { wsError = "Y"; wsEdgeCode = rpad("INVALID-STATUS", 24); }
  private static void p3400BadBranch() { wsError = "Y"; wsEdgeCode = rpad("INVALID-BRANCH", 24); }
  private static void p3500CheckMultiRecordType() {
    String c1 = slice(inRecord, 1, 1);
    if (!c1.equals("A") && !c1.equals("T")) wsEdgeCode = rpad("UNKNOWN-RECORD-TYPE", 24);
    String c2 = slice(inRecord, 2, 1);
    if (c2.equals("D")) wsItemCount = 2; else wsItemCount = 1;
  }
  private static void p3600CheckDateBoundary() {
    String d = slice(inRecord, 40, 8);
    if (d.equals("20261231")) wsEdgeCode = rpad("YEAR-END-ROLLOVER", 24);
    if (d.equals("20260228")) wsEdgeCode = rpad("NON-LEAP-FEB-END", 24);
  }
  private static void p4000ProcessRecord() {
    wsArithError = 0;
    try { java.math.BigDecimal p = new java.math.BigDecimal(wsAmountText); p = p.setScale(2, java.math.RoundingMode.UNNECESSARY); wsAmount = p; } catch (Exception e) { wsAmount = java.math.BigDecimal.ZERO; }
    wsBalance = wsAmount;
    wsPrincipal = wsAmount;
    {
      java.math.BigDecimal sum = wsReportTotal.add(wsAmount);
      java.math.BigDecimal mx = new java.math.BigDecimal("99999999999.99");
      java.math.BigDecimal mn = new java.math.BigDecimal("-99999999999.99");
      if (sum.compareTo(mx) > 0 || sum.compareTo(mn) < 0) wsArithError = 1; else wsReportTotal = sum;
    }
    wsRate = new java.math.BigDecimal("0.0525");
    wsFxRate = new java.math.BigDecimal("1.0825");
    wsCheckAmt = wsAmount;
    wsEntryAmt = wsAmount;
    wsFee = new java.math.BigDecimal("0.00");
    wsFeeWaived = slice(inRecord, 120, 1);
    wsInterest = java.math.BigDecimal.ZERO;
    try {
      java.math.BigDecimal basis = new java.math.BigDecimal(Integer.toString(wsDayBasis));
      java.math.BigDecimal num = wsPrincipal.multiply(wsRate);
      java.math.BigDecimal div = num.divide(basis, 10, java.math.RoundingMode.HALF_UP);
      java.math.BigDecimal rnd = div.setScale(2, java.math.RoundingMode.HALF_UP);
      java.math.BigDecimal mx = new java.math.BigDecimal("999999999.99");
      java.math.BigDecimal mn = new java.math.BigDecimal("-999999999.99");
      if (rnd.compareTo(mx) > 0 || rnd.compareTo(mn) < 0) wsArithError = 1; else wsInterest = rnd;
    } catch (Exception e) { wsArithError = 1; }
    if (wsLeapFlag.equals("Y")) wsDayBasis = 366; else wsDayBasis = 365;
    setSlice(wsLine, 1, 12, wsKey);
    setSlice(wsLine, 13, 3, wsBranch);
    setSlice(wsLine, 16, 24, wsEdgeCode);
    setSlice(wsLine, 40, 12, wsAmountText);
    setSlice(wsLine, 52, 8, wsStatus);
    wsAcceptCount = (wsAcceptCount + 1) % 1000000000L;
  }
  private static void p5000WriteResult() throws Exception {
    outRecord = wsLine.toString();
    try {
      String tw = rtrim(outRecord);
      outWriter.write(tw);
      outWriter.write("\n");
      wsOutStatus = "00";
      wsWriteTime = (wsWriteTime + 1) % 100000000L;
    } catch (Exception e) {
      wsOutStatus = "30";
      System.out.println("WRITE FAILURE " + wsOutStatus);
      returnCode = 12;
      endOfInput = true;
      wsEof = "Y";
      return;
    }
    if (!wsOutStatus.equals("00")) { System.out.println("WRITE FAILURE " + wsOutStatus); returnCode = 12; endOfInput = true; wsEof = "Y"; }
  }
  private static void p6000RejectRecord() throws Exception {
    wsRejectCount = (wsRejectCount + 1) % 1000000000L;
    System.out.println("REJECT " + formatCount9(wsRecordCount) + " " + wsEdgeCode);
    for (int i = 0; i < wsLine.length(); i++) wsLine.setCharAt(i, ' ');
    setSlice(wsLine, 1, 6, "REJECT");
    setSlice(wsLine, 8, 12, wsKey);
    setSlice(wsLine, 24, 24, wsEdgeCode);
    outRecord = wsLine.toString();
    try { String tw = rtrim(outRecord); outWriter.write(tw); outWriter.write("\n"); wsOutStatus = "00"; } catch (Exception e) { wsOutStatus = "30"; }
  }
  private static void p7000Finalize() throws Exception {
    p7100CheckControlTotals();
    p7200ReportRunCounts();
    try { if (inReader != null) { inReader.close(); wsInStatus = "00"; } } catch (Exception e) { wsInStatus = "30"; }
    try { if (outWriter != null) { outWriter.close(); wsOutStatus = "00"; } } catch (Exception e) { wsOutStatus = "30"; }
    if (!wsInStatus.equals("00")) System.out.println("CLOSE IN " + wsInStatus);
    if (!wsOutStatus.equals("00")) System.out.println("CLOSE OUT " + wsOutStatus);
    if (wsRejectCount > 0) returnCode = 4;
  }
  private static void p7100CheckControlTotals() {
    if (wsAcceptCount + wsRejectCount != wsRecordCount) { System.out.println("CONTROL COUNT MISMATCH"); returnCode = 12; }
    if (wsArithError != 0) { System.out.println("ARITHMETIC SIZE ERROR"); returnCode = 8; }
  }
  private static void p7200ReportRunCounts() {
    System.out.println("PROGRAM HTLON COMPLETE");
    System.out.println("READ=" + formatCount9(wsRecordCount));
    System.out.println("ACCEPT=" + formatCount9(wsAcceptCount));
    System.out.println("REJECT=" + formatCount9(wsRejectCount));
    System.out.println("TOTAL=" + formatTotal(wsReportTotal));
    System.out.println("EDGE=" + wsEdgeCode);
  }
  private static void p8000RejectAllZeroAmounts() { if (wsAmount.compareTo(java.math.BigDecimal.ZERO) == 0 && alphaEquals(wsStatus, "ACTIVE")) wsEdgeCode = rpad("ZERO-ACTIVE-AMOUNT", 24); }
  private static void p8010CheckNegativeBalance() { if (wsBalance.compareTo(java.math.BigDecimal.ZERO) < 0) wsEdgeCode = rpad("NEGATIVE-BALANCE", 24); }
  private static void p8020CheckLongKey() { if (slice(wsKey, 12, 1).equals(" ")) { } else { System.out.println("FULL-WIDTH-KEY " + wsKey); } }
  private static void p8030CheckRoundedAmount() { try { java.math.BigDecimal r = wsAmount.setScale(2, java.math.RoundingMode.HALF_UP); java.math.BigDecimal mx = new java.math.BigDecimal("999999999.99"); java.math.BigDecimal mn = new java.math.BigDecimal("-999999999.99"); if (r.compareTo(mx) > 0 || r.compareTo(mn) < 0) wsArithError = 1; else { wsPacked = r; if (wsPacked.compareTo(java.math.BigDecimal.ZERO) == 0 && wsAmount.compareTo(java.math.BigDecimal.ZERO) != 0) wsEdgeCode = rpad("TRUNCATION-RISK", 24); } } catch (Exception e) { wsArithError = 1; } }
  private static void p8040CheckShortMove() { wsShortAmount = rpad(wsAmountText, 6).substring(0, 6); if (!slice(wsAmountText, 7, 2).equals("00")) wsEdgeCode = rpad("SHORT-PIC-TRUNCATION", 24); }
  private static void p8050CheckFeeOverlay() { String ov = slice(wsRawBase, 120, 1); if (ov.equals("Y")) wsFeeWaived = "Y"; else wsFeeWaived = "N"; }
  private static void p8060CheckSignedZoned() { wsZoned = wsBalance; if (wsZoned.compareTo(java.math.BigDecimal.ZERO) < 0 && wsBalance.compareTo(java.math.BigDecimal.ZERO) >= 0) wsEdgeCode = rpad("SIGN-CONVERSION", 24); }
  private static void p8070CheckDateWindow() { if (slice(wsOldDate, 1, 2).equals("00")) wsEdgeCode = rpad("Y2K-WINDOW-REVIEW", 24); if (wsOldDate.equals("991231")) wsEdgeCode = rpad("CENTURY-BOUNDARY", 24); }
  private static void p8080CheckRatePrecision() { if (wsFxRate.compareTo(new java.math.BigDecimal("1.0825")) != 0) wsEdgeCode = rpad("RATE-IS-FOUR-DECIMAL", 24); }
  private static void p8090CheckRecordType() { String c = slice(inRecord, 1, 1); if (c.equals("A")) { } else if (c.equals("T")) { } else wsEdgeCode = rpad("BAD-RECORD-TYPE", 24); }
  private static void p8100CheckReturnCode() { if (!alphaEquals(wsReturnReason, "")) { if (!wsReturnReason.equals(spaces(3))) wsReturnCount = (wsReturnCount + 1) % 10000000L; } if (wsReturnCount > wsRecordCount) returnCode = 12; }
  private static void p8110CheckAccumulator() { java.math.BigDecimal sum = wsReportTotal.add(wsAmount); java.math.BigDecimal mx = new java.math.BigDecimal("99999999999.99"); java.math.BigDecimal mn = new java.math.BigDecimal("-99999999999.99"); if (sum.compareTo(mx) > 0 || sum.compareTo(mn) < 0) wsArithError = 1; else { wsReportTotal = sum; if (wsReportTotal.compareTo(java.math.BigDecimal.ZERO) < 0) wsEdgeCode = rpad("NEGATIVE-CONTROL-TOTAL", 24); } }
  private static void p8120CheckBranchBreak() { if (compareAlpha(wsBranch, wsPrevBranch) != 0) { p8130WriteBranchTotal(); wsPrevBranch = wsBranch; } }
  private static void p8130WriteBranchTotal() { if (!wsPrevBranch.equals(spaces(3))) { System.out.println("BRANCH BREAK " + wsPrevBranch); System.out.println("BRANCH TOTAL " + formatTotal(wsReportTotal)); } wsReportTotal = java.math.BigDecimal.ZERO; }
  private static void p8140CheckInputShape() { if (inRecord.equals(spaces(160))) { wsError = "Y"; wsEdgeCode = rpad("EMPTY-RECORD", 24); } if (!slice(inRecord, 160, 1).equals(" ")) System.out.println("TRAILING-CONTENT PRESENT"); }
  private static void p8150CheckMonthRollover() { if (slice(inRecord, 40, 8).equals("20261231")) wsNewDate = "20270101"; if (slice(inRecord, 40, 8).equals("20260131")) wsNewDate = "20260201"; }
  private static void p8160CheckLeapYear() { if (slice(inRecord, 40, 4).equals("2024")) { wsLeapFlag = "Y"; wsDayBasis = 366; } else { wsLeapFlag = "N"; wsDayBasis = 365; } }
  private static void p8170CheckOddCents() { if (slice(wsAmountText, 7, 2).equals("50")) wsEdgeCode = rpad("HALF-CENT-ROUNDING-CASE", 24); if (slice(wsAmountText, 7, 2).equals("99")) wsEdgeCode = rpad("ROUND-UP-BOUNDARY", 24); }
  private static void p8180CheckStatusTransition() { if (alphaEquals(wsStatus, "ACTIVE") || alphaEquals(wsStatus, "DORMANT") || alphaEquals(wsStatus, "BLOCKED")) { } else wsEdgeCode = rpad("UNKNOWN-STATE", 24); }
  private static void p8190CheckBalanceLimit() { if (wsBalance.compareTo(new java.math.BigDecimal("999999999.99")) > 0) wsEdgeCode = rpad("BALANCE-OVERFLOW", 24); if (wsBalance.compareTo(new java.math.BigDecimal("-999999999.99")) < 0) wsEdgeCode = rpad("BALANCE-UNDERFLOW", 24); }
  private static void p8200CheckReconciliation() { try { java.math.BigDecimal d = wsDebits.subtract(wsCredits); java.math.BigDecimal mx = new java.math.BigDecimal("99999999999.99"); java.math.BigDecimal mn = new java.math.BigDecimal("-99999999999.99"); if (d.compareTo(mx) > 0 || d.compareTo(mn) < 0) wsArithError = 1; else { wsDelta = d; if (wsDelta.compareTo(java.math.BigDecimal.ZERO) == 0) { } } } catch (Exception e) { wsArithError = 1; } }
  private static void p8210CheckCopybookCount() { if (wsItemCount < 1 || wsItemCount > 12) wsEdgeCode = rpad("OCCURS-COUNT-INVALID", 24); wsItemCount = 1; while (!(wsItemCount > 1)) { wsItemCount = wsItemCount + 1; } }
  private static void p8220CheckCallInterface() { setSlice(wsLine, 1, 12, wsKey); setSlice(wsLine, 13, 24, wsEdgeCode); if (slice(wsLine.toString(), 1, 1).equals(" ")) wsEdgeCode = rpad("CALL-KEY-MISSING", 24); }
  private static void p8230CheckWriteStatus() { if (wsOutStatus.equals("00")) { } else { System.out.println("DATASET STATUS " + wsOutStatus); returnCode = 12; } }
  private static void p8240CheckReadStatus() { if (wsInStatus.equals("10")) { endOfInput = true; wsEof = "Y"; } else { if (!wsInStatus.equals("00")) System.out.println("INPUT STATUS " + wsInStatus); } }
  private static void p8250CheckCurrencyScale() { try { java.math.BigDecimal m = wsAmount.multiply(new java.math.BigDecimal("1.0000")); java.math.BigDecimal r = m.setScale(2, java.math.RoundingMode.HALF_UP); java.math.BigDecimal mx = new java.math.BigDecimal("999999999.99"); java.math.BigDecimal mn = new java.math.BigDecimal("-999999999.99"); if (r.compareTo(mx) > 0 || r.compareTo(mn) < 0) wsArithError = 1; else wsAmount = r; } catch (Exception e) { wsArithError = 1; } if (wsFxRate.compareTo(java.math.BigDecimal.ZERO) < 0) wsEdgeCode = rpad("NEGATIVE-FX-RATE", 24); }
  private static void p8260CheckNestedCallResult() { wsAbendCode = 0; if (wsAbendCode != 0) { System.out.println("CALLED ROUTINE RETURNED ERROR"); returnCode = 8; } }
  private static void p8270CheckAuditBalance() { java.math.BigDecimal sum = wsCycleTotal.add(wsAmount); java.math.BigDecimal mx = new java.math.BigDecimal("99999999999.99"); java.math.BigDecimal mn = new java.math.BigDecimal("-99999999999.99"); if (sum.compareTo(mx) > 0 || sum.compareTo(mn) < 0) wsArithError = 1; else { wsCycleTotal = sum; if (wsCycleTotal.compareTo(java.math.BigDecimal.ZERO) == 0) { } } }
  private static void p8280CheckKeyUniqueness() { if (compareAlpha(wsKey, wsPrevKey) == 0 && !wsKey.equals(spaces(12))) wsEdgeCode = rpad("DUPLICATE-KEY", 24); wsPrevKey = wsKey; }
  private static void p8290CheckRunId() { if (wsNewDate.equals("00000000")) { System.out.println("RUN DATE NOT INITIALIZED"); returnCode = 12; } if (wsRecordCount == 0) System.out.println("EMPTY INPUT"); }
  private static void p9900OperationalChecks() { p8000RejectAllZeroAmounts(); p8010CheckNegativeBalance(); p8020CheckLongKey(); p8030CheckRoundedAmount(); p8040CheckShortMove(); p8050CheckFeeOverlay(); p8060CheckSignedZoned(); p8070CheckDateWindow(); p8080CheckRatePrecision(); p8090CheckRecordType(); p8100CheckReturnCode(); p8110CheckAccumulator(); p8120CheckBranchBreak(); p8130WriteBranchTotal(); p8140CheckInputShape(); p8150CheckMonthRollover(); p8160CheckLeapYear(); p8170CheckOddCents(); p8180CheckStatusTransition(); p8190CheckBalanceLimit(); p8200CheckReconciliation(); p8210CheckCopybookCount(); p8220CheckCallInterface(); p8230CheckWriteStatus(); p8240CheckReadStatus(); p8250CheckCurrencyScale(); p8260CheckNestedCallResult(); p8270CheckAuditBalance(); p8280CheckKeyUniqueness(); p8290CheckRunId(); }
}
