public class Htnsf {
  static String wsInStatus = "  ";
  static String wsOutStatus = "  ";
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
  static long wsDaysInactive = 0;
  static long wsDayBasis = 365;
  static String wsLeapFlag = "N";
  static String wsAmountText = "        ";
  static String wsShortAmount = "      ";
  static String wsRawBase = "                                                                                                                        ";
  static String wsFeeOverride = " ";
  static int wsItemCount = 0;
  static String[] wsItems = new String[12];
  static java.math.BigDecimal wsPacked = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsZoned = java.math.BigDecimal.ZERO;
  static String wsOldDate = "991231";
  static int wsCentury = 20;
  static String wsNewDate = "00000000";
  static char[] wsLine = new char[200];
  static java.math.BigDecimal wsReportTotal = java.math.BigDecimal.ZERO;
  static long wsReadTime = 0;
  static long wsWriteTime = 0;
  static long wsAbendCode = 0;
  static int wsServiceError = 0;
  static java.math.BigDecimal wsServiceValue = java.math.BigDecimal.ZERO;
  static String wsServiceText = "                                                                                ";
  static String inRecord = "                                                                                                                                                                ";
  static String outRecord = "                                                                                                                                                                                                        ";
  static java.util.List<String> inLines = new java.util.ArrayList<>();
  static int inIndex = 0;
  static java.io.BufferedWriter outWriter = null;
  static boolean inOpened = false;
  static boolean outOpened = false;
  static int returnCode = 0;
  static {
    java.util.Arrays.fill(wsLine, ' ');
    for (int i = 0; i < 12; i++) wsItems[i] = "    ";
  }
  public static void main(String[] args) throws Exception {
    p0000Main();
    if (returnCode != 0) System.exit(returnCode);
  }
  static String spaces(int n) { return " ".repeat(Math.max(0, n)); }
  static String padRight(String s, int len) {
    if (s == null) s = "";
    if (s.length() > len) return s.substring(0, len);
    if (s.length() < len) return s + spaces(len - s.length());
    return s;
  }
  static String rtrim(String s) {
    int i = s.length();
    while (i > 0 && s.charAt(i - 1) == ' ') i--;
    return s.substring(0, i);
  }
  static void setSlice(int off, int len, String v) {
    if (v == null) v = "";
    for (int i = 0; i < len; i++) {
      char c = ' ';
      if (i < v.length()) c = v.charAt(i);
      wsLine[off + i] = c;
    }
  }
  static boolean isEbcdic() {
    String v = System.getenv("DIOCLE_COLLATION");
    return "EBCDIC".equals(v);
  }
  static int compareAlpha(String a, String b) {
    if (isEbcdic()) {
      try {
        java.nio.charset.Charset cs = java.nio.charset.Charset.forName("Cp037");
        byte[] ba = a.getBytes(cs);
        byte[] bb = b.getBytes(cs);
        int n = Math.min(ba.length, bb.length);
        for (int i = 0; i < n; i++) {
          int x = ba[i] & 255;
          int y = bb[i] & 255;
          if (x != y) return x - y;
        }
        return ba.length - bb.length;
      } catch (Exception e) { }
    }
    int n = Math.min(a.length(), b.length());
    for (int i = 0; i < n; i++) {
      char x = a.charAt(i);
      char y = b.charAt(i);
      if (x != y) return x - y;
    }
    return a.length() - b.length();
  }
  static boolean isNumericText(String s) {
    if (s.length() == 0) return false;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c < '0' || c > '9') return false;
    }
    return true;
  }
  static boolean isAlphabeticText(String s) {
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == ' ') continue;
      if (c >= 'A' && c <= 'Z') continue;
      if (c >= 'a' && c <= 'z') continue;
      return false;
    }
    return true;
  }
  static long pow10(int n) { long p = 1; for (int i = 0; i < n; i++) p = p * 10; return p; }
  static String fmtCount(long v, int digits) {
    long m = pow10(digits);
    long t = v % m;
    if (t < 0) t += m;
    String s = Long.toString(t);
    while (s.length() < digits) s = "0" + s;
    if (s.length() > digits) s = s.substring(s.length() - digits);
    return s;
  }
  static boolean fits(java.math.BigDecimal v, int intD, int decD, boolean signed) {
    java.math.BigDecimal max = java.math.BigDecimal.TEN.pow(intD).subtract(java.math.BigDecimal.ONE.movePointLeft(decD));
    if (!signed) {
      if (v.signum() < 0) return false;
      return v.compareTo(max) <= 0;
    } else {
      java.math.BigDecimal av = v.abs();
      return av.compareTo(max) <= 0;
    }
  }
  static String fmtPacked(java.math.BigDecimal v, int intD, int decD) {
    boolean neg = v.signum() < 0;
    java.math.BigDecimal av = v.abs().setScale(decD, java.math.RoundingMode.DOWN);
    String plain = av.toPlainString();
    int dot = plain.indexOf('.');
    String ip;
    String fp;
    if (dot >= 0) { ip = plain.substring(0, dot); fp = plain.substring(dot + 1); }
    else { ip = plain; fp = ""; }
    while (fp.length() < decD) fp = fp + "0";
    if (fp.length() > decD) fp = fp.substring(0, decD);
    while (ip.length() < intD) ip = "0" + ip;
    if (ip.length() > intD) ip = ip.substring(ip.length() - intD);
    return (neg ? "-" : "+") + ip + "." + fp;
  }
  static void cobDisplay(String s) throws java.io.IOException {
    byte[] b = (s + "\n").getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
    System.out.write(b);
    System.out.flush();
  }
  static void writeLine(String rec) {
    String t = rtrim(rec);
    try {
      outWriter.write(t);
      outWriter.write("\n");
      wsOutStatus = "00";
    } catch (Exception e) {
      wsOutStatus = "30";
    }
  }
  private static void p0000Main() throws Exception {
    p1000Initialize();
    while (!wsEof.equals("Y")) {
      p2000ReadInput();
      if (!wsEof.equals("Y")) {
        p3000ValidateRecord();
        if (!wsError.equals("Y")) {
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
    String clockRef = System.getenv("DIOCLE_CLOCK");
    if (clockRef != null && clockRef.length() == 0) { }
    String inPath = System.getenv("DD_INPUT-FILE");
    String outPath = System.getenv("DD_OUTPUT-FILE");
    try {
      if (inPath == null) throw new java.io.IOException("no dd");
      java.nio.file.Path p = java.nio.file.Paths.get(inPath);
      inLines = java.nio.file.Files.readAllLines(p, java.nio.charset.StandardCharsets.ISO_8859_1);
      wsInStatus = "00";
      inOpened = true;
    } catch (Exception e) {
      wsInStatus = "35";
      inOpened = false;
      inLines = new java.util.ArrayList<>();
    }
    if (!wsInStatus.equals("00")) {
      cobDisplay("OPEN INPUT FAILED " + wsInStatus);
      returnCode = 12;
      wsEof = "Y";
    }
    try {
      if (outPath == null) throw new java.io.IOException("no dd");
      java.nio.file.Path op = java.nio.file.Paths.get(outPath);
      java.nio.file.Path par = op.getParent();
      if (par != null) java.nio.file.Files.createDirectories(par);
      outWriter = java.nio.file.Files.newBufferedWriter(op, java.nio.charset.StandardCharsets.ISO_8859_1, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING, java.nio.file.StandardOpenOption.WRITE);
      wsOutStatus = "00";
      outOpened = true;
    } catch (Exception e) {
      wsOutStatus = "35";
      outOpened = false;
    }
    if (!wsOutStatus.equals("00")) {
      cobDisplay("OPEN OUTPUT FAILED " + wsOutStatus);
      returnCode = 12;
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
    String part = wsOldDate.substring(0, 2);
    int v = Integer.parseInt(part);
    if (v > 80) wsCentury = 19;
    else wsCentury = 20;
    String cent = fmtCount(wsCentury, 2);
    wsNewDate = cent + wsOldDate;
    wsNewDate = padRight(wsNewDate, 8);
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
    if (wsEof.equals("Y")) return;
    if (!inOpened) return;
    if (inIndex >= inLines.size()) {
      wsEof = "Y";
      wsInStatus = "10";
    } else {
      String line = inLines.get(inIndex++);
      if (line.length() > 160) line = line.substring(0, 160);
      else if (line.length() < 160) line = line + spaces(160 - line.length());
      inRecord = line;
      wsRecordCount = (wsRecordCount + 1) & 0xFFFFFFFFL;
      wsRawBase = inRecord.substring(0, 120);
      wsFeeOverride = wsRawBase.substring(119, 120);
      wsKey = inRecord.substring(0, 12);
      wsBranch = inRecord.substring(12, 15);
      wsAmountText = inRecord.substring(15, 23);
      String s24 = inRecord.substring(23, 31);
      wsShortAmount = s24.substring(0, 6);
      wsStatus = s24;
      wsInStatus = "00";
    }
    if (!wsInStatus.equals("00") && !wsInStatus.equals("10")) {
      cobDisplay("READ FAILURE " + wsInStatus);
      wsError = "Y";
      wsEof = "Y";
    }
  }
  private static void p3000ValidateRecord() {
    wsError = "N";
    if (wsKey.equals(spaces(12))) p3100BadKey();
    if (!isNumericText(wsAmountText)) p3200BadAmount();
    if (wsStatus.equals(spaces(8))) p3300BadStatus();
    if (!isAlphabeticText(wsBranch)) p3400BadBranch();
    if (wsRecordCount > 1 && compareAlpha(wsKey, wsPrevKey) < 0) {
      wsEdgeCode = padRight("SORT-ORDER", 24);
    }
    wsPrevKey = wsKey;
    p3500CheckMultiRecordType();
    p3600CheckDateBoundary();
  }
  private static void p3100BadKey() {
    wsError = "Y";
    wsEdgeCode = padRight("MISSING-KEY", 24);
  }
  private static void p3200BadAmount() {
    wsError = "Y";
    wsEdgeCode = padRight("INVALID-AMOUNT", 24);
  }
  private static void p3300BadStatus() {
    wsError = "Y";
    wsEdgeCode = padRight("INVALID-STATUS", 24);
  }
  private static void p3400BadBranch() {
    wsError = "Y";
    wsEdgeCode = padRight("INVALID-BRANCH", 24);
  }
  private static void p3500CheckMultiRecordType() {
    char c0 = inRecord.charAt(0);
    if (c0 != 'A' && c0 != 'T') wsEdgeCode = padRight("UNKNOWN-RECORD-TYPE", 24);
    char c1 = inRecord.charAt(1);
    if (c1 == 'D') wsItemCount = 2;
    else wsItemCount = 1;
  }
  private static void p3600CheckDateBoundary() {
    String d = inRecord.substring(39, 47);
    if (d.equals("20261231")) wsEdgeCode = padRight("YEAR-END-ROLLOVER", 24);
    if (d.equals("20260228")) wsEdgeCode = padRight("NON-LEAP-FEB-END", 24);
  }
  private static void p4000ProcessRecord() {
    wsArithError = 0;
    java.math.BigDecimal amt;
    try { amt = new java.math.BigDecimal(wsAmountText); }
    catch (Exception e) { amt = java.math.BigDecimal.ZERO; }
    wsAmount = amt;
    wsBalance = wsAmount;
    wsPrincipal = wsAmount;
    java.math.BigDecimal sum = wsReportTotal.add(wsAmount);
    if (!fits(sum, 11, 2, true)) wsArithError = 1;
    else wsReportTotal = sum;
    wsRate = new java.math.BigDecimal("0.0525");
    wsFxRate = new java.math.BigDecimal("1.0825");
    wsCheckAmt = wsAmount;
    wsEntryAmt = wsAmount;
    wsFee = java.math.BigDecimal.ZERO;
    wsInterest = java.math.BigDecimal.ZERO;
    if (wsBalance.compareTo(java.math.BigDecimal.ZERO) < 0 && !wsFeeOverride.equals("Y")) {
      java.math.BigDecimal fsum = wsFee.add(new java.math.BigDecimal("35.00"));
      if (!fits(fsum, 5, 2, false)) wsArithError = 1;
      else wsFee = fsum;
    }
    if (wsFeeOverride.equals("Y")) wsFee = java.math.BigDecimal.ZERO;
    setSlice(0, 12, wsKey);
    setSlice(12, 3, wsBranch);
    setSlice(15, 24, wsEdgeCode);
    setSlice(39, 12, wsAmountText);
    setSlice(51, 8, wsStatus);
    wsAcceptCount = (wsAcceptCount + 1) & 0xFFFFFFFFL;
  }
  private static void p5000WriteResult() throws Exception {
    outRecord = new String(wsLine);
    if (!outOpened) { wsOutStatus = "35"; }
    else writeLine(outRecord);
    if (!wsOutStatus.equals("00")) {
      cobDisplay("WRITE FAILURE " + wsOutStatus);
      returnCode = 12;
      wsEof = "Y";
    } else {
      wsWriteTime = (wsWriteTime + 1) % pow10(8);
    }
  }
  private static void p6000RejectRecord() throws Exception {
    wsRejectCount = (wsRejectCount + 1) & 0xFFFFFFFFL;
    cobDisplay("REJECT " + fmtCount(wsRecordCount, 10) + " " + wsEdgeCode);
    java.util.Arrays.fill(wsLine, ' ');
    setSlice(0, 6, "REJECT");
    setSlice(7, 12, wsKey);
    setSlice(23, 24, wsEdgeCode);
    outRecord = new String(wsLine);
    if (outOpened) writeLine(outRecord);
  }
  private static void p7000Finalize() throws Exception {
    p7100CheckControlTotals();
    p7200ReportRunCounts();
    try { if (outWriter != null) outWriter.close(); if (outOpened) wsOutStatus = "00"; }
    catch (Exception e) { if (outOpened) wsOutStatus = "30"; }
    if (inOpened) wsInStatus = "00";
    if (!wsInStatus.equals("00")) cobDisplay("CLOSE IN " + wsInStatus);
    if (!wsOutStatus.equals("00")) cobDisplay("CLOSE OUT " + wsOutStatus);
    if (wsRejectCount > 0) returnCode = 4;
  }
  private static void p7100CheckControlTotals() throws Exception {
    if (((wsAcceptCount + wsRejectCount) & 0xFFFFFFFFL) != wsRecordCount) {
      cobDisplay("CONTROL COUNT MISMATCH");
      returnCode = 12;
    }
    if (wsArithError != 0) {
      cobDisplay("ARITHMETIC SIZE ERROR");
      returnCode = 8;
    }
  }
  private static void p7200ReportRunCounts() throws Exception {
    cobDisplay("PROGRAM HTNSF COMPLETE");
    cobDisplay("READ=" + fmtCount(wsRecordCount, 10));
    cobDisplay("ACCEPT=" + fmtCount(wsAcceptCount, 10));
    cobDisplay("REJECT=" + fmtCount(wsRejectCount, 10));
    cobDisplay("TOTAL=" + fmtPacked(wsReportTotal, 11, 2));
    cobDisplay("EDGE=" + wsEdgeCode);
  }
  private static void p8000RejectAllZeroAmounts() {
    if (wsAmount.compareTo(java.math.BigDecimal.ZERO) == 0 && wsStatus.equals(padRight("ACTIVE", 8))) {
      wsEdgeCode = padRight("ZERO-ACTIVE-AMOUNT", 24);
    }
  }
  private static void p8010CheckNegativeBalance() {
    if (wsBalance.compareTo(java.math.BigDecimal.ZERO) < 0) wsEdgeCode = padRight("NEGATIVE-BALANCE", 24);
  }
  private static void p8020CheckLongKey() throws Exception {
    if (wsKey.substring(11, 12).equals(" ")) { }
    else cobDisplay("FULL-WIDTH-KEY " + wsKey);
  }
  private static void p8030CheckRoundedAmount() {
    java.math.BigDecimal r = wsAmount.setScale(2, java.math.RoundingMode.HALF_UP);
    if (!fits(r, 9, 2, true)) wsArithError = 1;
    else wsPacked = r;
    if (wsPacked.compareTo(java.math.BigDecimal.ZERO) == 0 && wsAmount.compareTo(java.math.BigDecimal.ZERO) != 0) wsEdgeCode = padRight("TRUNCATION-RISK", 24);
  }
  private static void p8040CheckShortMove() {
    wsShortAmount = padRight(wsAmountText, 6).substring(0, 6);
    if (!wsAmountText.substring(6, 8).equals("00")) wsEdgeCode = padRight("SHORT-PIC-TRUNCATION", 24);
  }
  private static void p8050CheckFeeOverlay() {
    if (wsFeeOverride.equals("Y")) wsFeeWaived = "Y";
    else wsFeeWaived = "N";
  }
  private static void p8060CheckSignedZoned() {
    wsZoned = wsBalance;
    if (wsZoned.compareTo(java.math.BigDecimal.ZERO) < 0 && wsBalance.compareTo(java.math.BigDecimal.ZERO) >= 0) wsEdgeCode = padRight("SIGN-CONVERSION", 24);
  }
  private static void p8070CheckDateWindow() {
    if (wsOldDate.substring(0, 2).equals("00")) wsEdgeCode = padRight("Y2K-WINDOW-REVIEW", 24);
    if (wsOldDate.equals("991231")) wsEdgeCode = padRight("CENTURY-BOUNDARY", 24);
  }
  private static void p8080CheckRatePrecision() {
    if (wsFxRate.compareTo(new java.math.BigDecimal("1.0825")) != 0) wsEdgeCode = padRight("RATE-IS-FOUR-DECIMAL", 24);
  }
  private static void p8090CheckRecordType() {
    char c = inRecord.charAt(0);
    if (c == 'A') { }
    else if (c == 'T') { }
    else wsEdgeCode = padRight("BAD-RECORD-TYPE", 24);
  }
  private static void p8100CheckReturnCode() {
    if (!wsReturnReason.equals(spaces(3))) wsReturnCount = (wsReturnCount + 1) & 0xFFFFFFFFL;
    if (wsReturnCount > wsRecordCount) returnCode = 12;
  }
  private static void p8110CheckAccumulator() {
    java.math.BigDecimal s = wsReportTotal.add(wsAmount);
    if (!fits(s, 11, 2, true)) wsArithError = 1;
    else wsReportTotal = s;
    if (wsReportTotal.compareTo(java.math.BigDecimal.ZERO) < 0) wsEdgeCode = padRight("NEGATIVE-CONTROL-TOTAL", 24);
  }
  private static void p8120CheckBranchBreak() throws Exception {
    if (!wsBranch.equals(wsPrevBranch)) {
      p8130WriteBranchTotal();
      wsPrevBranch = wsBranch;
    }
  }
  private static void p8130WriteBranchTotal() throws Exception {
    if (!wsPrevBranch.equals(spaces(3))) {
      cobDisplay("BRANCH BREAK " + wsPrevBranch);
      cobDisplay("BRANCH TOTAL " + fmtPacked(wsReportTotal, 11, 2));
    }
    wsReportTotal = java.math.BigDecimal.ZERO;
  }
  private static void p8140CheckInputShape() throws Exception {
    if (inRecord.equals(spaces(160))) { wsError = "Y"; wsEdgeCode = padRight("EMPTY-RECORD", 24); }
    if (inRecord.charAt(159) != ' ') cobDisplay("TRAILING-CONTENT PRESENT");
  }
  private static void p8150CheckMonthRollover() {
    if (inRecord.substring(39, 47).equals("20261231")) wsNewDate = "20270101";
    if (inRecord.substring(39, 47).equals("20260131")) wsNewDate = "20260201";
  }
  private static void p8160CheckLeapYear() {
    if (inRecord.substring(39, 43).equals("2024")) { wsLeapFlag = "Y"; wsDayBasis = 366; }
    else { wsLeapFlag = "N"; wsDayBasis = 365; }
  }
  private static void p8170CheckOddCents() {
    if (wsAmountText.substring(6, 8).equals("50")) wsEdgeCode = padRight("HALF-CENT-ROUNDING-CASE", 24);
    if (wsAmountText.substring(6, 8).equals("99")) wsEdgeCode = padRight("ROUND-UP-BOUNDARY", 24);
  }
  private static void p8180CheckStatusTransition() {
    if (wsStatus.equals(padRight("ACTIVE", 8))) { }
    else if (wsStatus.equals(padRight("DORMANT", 8))) { }
    else if (wsStatus.equals(padRight("BLOCKED", 8))) { }
    else wsEdgeCode = padRight("UNKNOWN-STATE", 24);
  }
  private static void p8190CheckBalanceLimit() {
    java.math.BigDecimal hi = new java.math.BigDecimal("999999999.99");
    java.math.BigDecimal lo = new java.math.BigDecimal("-999999999.99");
    if (wsBalance.compareTo(hi) > 0) wsEdgeCode = padRight("BALANCE-OVERFLOW", 24);
    if (wsBalance.compareTo(lo) < 0) wsEdgeCode = padRight("BALANCE-UNDERFLOW", 24);
  }
  private static void p8200CheckReconciliation() {
    java.math.BigDecimal d = wsDebits.subtract(wsCredits);
    if (!fits(d, 11, 2, true)) wsArithError = 1;
    else wsDelta = d;
    if (wsDelta.compareTo(java.math.BigDecimal.ZERO) == 0) { }
  }
  private static void p8210CheckCopybookCount() {
    if (wsItemCount < 1 || wsItemCount > 12) wsEdgeCode = padRight("OCCURS-COUNT-INVALID", 24);
    for (int i = 1; i <= 1; i++) { }
  }
  private static void p8220CheckCallInterface() {
    setSlice(0, 12, wsKey);
    setSlice(12, 24, wsEdgeCode);
    if (new String(wsLine, 0, 1).equals(" ")) wsEdgeCode = padRight("CALL-KEY-MISSING", 24);
  }
  private static void p8230CheckWriteStatus() throws Exception {
    if (wsOutStatus.equals("00")) { }
    else { cobDisplay("DATASET STATUS " + wsOutStatus); returnCode = 12; }
  }
  private static void p8240CheckReadStatus() throws Exception {
    if (wsInStatus.equals("10")) wsEof = "Y";
    else { if (!wsInStatus.equals("00")) cobDisplay("INPUT STATUS " + wsInStatus); }
  }
  private static void p8250CheckCurrencyScale() {
    java.math.BigDecimal m = wsAmount.multiply(new java.math.BigDecimal("1.0000"));
    java.math.BigDecimal r = m.setScale(2, java.math.RoundingMode.HALF_UP);
    if (!fits(r, 9, 2, true)) wsArithError = 1;
    else wsAmount = r;
    if (wsFxRate.signum() < 0) wsEdgeCode = padRight("NEGATIVE-FX-RATE", 24);
  }
  private static void p8260CheckNestedCallResult() throws Exception {
    wsAbendCode = 0;
    if (wsAbendCode != 0) { cobDisplay("CALLED ROUTINE RETURNED ERROR"); returnCode = 8; }
  }
  private static void p8270CheckAuditBalance() {
    java.math.BigDecimal s = wsCycleTotal.add(wsAmount);
    if (!fits(s, 11, 2, true)) wsArithError = 1;
    else wsCycleTotal = s;
    if (wsCycleTotal.compareTo(java.math.BigDecimal.ZERO) == 0) { }
  }
  private static void p8280CheckKeyUniqueness() {
    if (wsKey.equals(wsPrevKey) && !wsKey.equals(spaces(12))) wsEdgeCode = padRight("DUPLICATE-KEY", 24);
    wsPrevKey = wsKey;
  }
  private static void p8290CheckRunId() throws Exception {
    if (wsNewDate.equals("00000000")) { cobDisplay("RUN DATE NOT INITIALIZED"); returnCode = 12; }
    if (wsRecordCount == 0) cobDisplay("EMPTY INPUT");
  }
  private static void p9900OperationalChecks() throws Exception {
    p8000RejectAllZeroAmounts();
    p8010CheckNegativeBalance();
    p8020CheckLongKey();
    p8030CheckRoundedAmount();
    p8040CheckShortMove();
    p8050CheckFeeOverlay();
    p8060CheckSignedZoned();
    p8070CheckDateWindow();
    p8080CheckRatePrecision();
    p8090CheckRecordType();
    p8100CheckReturnCode();
    p8110CheckAccumulator();
    p8120CheckBranchBreak();
    p8130WriteBranchTotal();
    p8140CheckInputShape();
    p8150CheckMonthRollover();
    p8160CheckLeapYear();
    p8170CheckOddCents();
    p8180CheckStatusTransition();
    p8190CheckBalanceLimit();
    p8200CheckReconciliation();
    p8210CheckCopybookCount();
    p8220CheckCallInterface();
    p8230CheckWriteStatus();
    p8240CheckReadStatus();
    p8250CheckCurrencyScale();
    p8260CheckNestedCallResult();
    p8270CheckAuditBalance();
    p8280CheckKeyUniqueness();
    p8290CheckRunId();
  }
}
