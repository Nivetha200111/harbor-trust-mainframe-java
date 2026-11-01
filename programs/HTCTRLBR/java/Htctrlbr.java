import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
public class Htctrlbr {
 static String wsInStatus = "  ";
 static String wsOutStatus = "  ";
 static String wsAuditStatus = "  ";
 static boolean endOfInput = false;
 static String wsError = "N";
 static long wsRecordCount = 0;
 static String wsCountDisp = "000000000";
 static long wsAcceptCount = 0;
 static long wsRejectCount = 0;
 static long wsReturnCount = 0;
 static String wsEdgeCode = "                        ";
 static String wsKey = "            ";
 static String wsPrevKey = "            ";
 static String wsBranch = "   ";
 static String wsPrevBranch = "   ";
 static String wsStatus = "        ";
 static BigDecimal wsEntryAmt = BigDecimal.ZERO;
 static BigDecimal wsCycleTotal = BigDecimal.ZERO;
 static BigDecimal wsDebits = BigDecimal.ZERO;
 static BigDecimal wsCredits = BigDecimal.ZERO;
 static BigDecimal wsDelta = BigDecimal.ZERO;
 static BigDecimal wsReportTotal = BigDecimal.ZERO;
 static BigDecimal wsBranchTotal = BigDecimal.ZERO;
 static BigDecimal wsAcctTotal = BigDecimal.ZERO;
 static BigDecimal wsGrandTotal = BigDecimal.ZERO;
 static BigDecimal wsPacked = BigDecimal.ZERO;
 static long wsOutOfBalance = 0;
 static long wsDayBasis = 365;
 static String wsLeapFlag = "N";
 static String wsFeeWaived = "N";
 static String wsAmountText = "        ";
 static String wsShortAmount = "      ";
 static String wsOldDate = "991231";
 static String wsCentury = "20";
 static String wsNewDate = "00000000";
 static String wsLine = "                                                                                                                                                                                                        ";
 static long wsReadTime = 0;
 static long wsWriteTime = 0;
 static long wsAbendCode = 0;
 static int wsItemCount = 0;
 static String[] wsItems = new String[12];
 static String wsRawBase = "                                                                                                                        ";
 static String inRecord = "                                                                                                                                                                ";
 static String outRecord = "                                                                                                                                                                                                        ";
 static String auditRecord = "                                                                                                                                                                                                        ";
 static long wsPageNo = 0;
 static long wsLineNo = 99;
 static long wsPageSize = 55;
 static String wsCtlBranch = "   ";
 static String wsCtlAcct = "            ";
 static String wsSrcText = "                                        ";
 static String wsDstText = "                  ";
 static String wsTruncFlag = "N";
 static int returnCode = 0;
 static boolean ebcdicMode = false;
 static List<String> inLines = null;
 static int inPos = 0;
 static BufferedWriter outWriter = null;
 static BufferedWriter auditWriter = null;
 static String lsDateYyyymmdd = "00000000";
 static long lsDayBasis = 365;
 static int htYear = 0;
 static int htMonth = 0;
 static int htDay = 0;
 static String htLeap = "N";
 static String htValid = "Y";
 static long htQty = 0;
 static int htIdx = 0;
 static long htQuot = 0;
 static long htRem = 0;
 static long htDayCount = 0;
 static long htBasisWork = 365;
 static int[] htMdays = {31,28,31,30,31,30,31,31,30,31,30,31};
 static String spaces(int n) {
  StringBuilder sb = new StringBuilder(n);
  for (int i=0;i<n;i++) sb.append(' ');
  return sb.toString();
 }
 static String padRight(String s, int n) {
  if (s.length() > n) return s.substring(0,n);
  if (s.length() == n) return s;
  StringBuilder sb = new StringBuilder(n);
  sb.append(s);
  for (int i=s.length();i<n;i++) sb.append(' ');
  return sb.toString();
 }
 static String trimEnd(String s) {
  int e = s.length();
  while (e>0 && s.charAt(e-1)==' ') e--;
  return s.substring(0,e);
 }
 static String fmtU(long v, int d) {
  long p = 1;
  for (int i=0;i<d;i++) p = p * 10;
  long t = v % p;
  if (t < 0) t = t + p;
  String s = Long.toString(t);
  while (s.length() < d) s = "0" + s;
  return s;
 }
 static int compareAlpha(String a, String b) {
  if (!ebcdicMode) {
   int n = a.length() < b.length() ? a.length() : b.length();
   for (int i=0;i<n;i++) {
    int ca = a.charAt(i);
    int cb = b.charAt(i);
    if (ca != cb) return ca - cb;
   }
   return a.length() - b.length();
  } else {
   try {
    Charset ec = Charset.forName("Cp037");
    byte[] ba = a.getBytes(ec);
    byte[] bb = b.getBytes(ec);
    int n = ba.length < bb.length ? ba.length : bb.length;
    for (int i=0;i<n;i++) {
     int ca = ba[i] & 255;
     int cb = bb[i] & 255;
     if (ca != cb) return ca - cb;
    }
    return ba.length - bb.length;
   } catch (Exception e) {
    int n = a.length() < b.length() ? a.length() : b.length();
    for (int i=0;i<n;i++) {
     int ca = a.charAt(i);
     int cb = b.charAt(i);
     if (ca != cb) return ca - cb;
    }
    return a.length() - b.length();
   }
  }
 }
 static boolean isNumericTxt(String s) {
  if (s.length()==0) return false;
  for (int i=0;i<s.length();i++) {
   char c = s.charAt(i);
   if (c < '0' || c > '9') return false;
  }
  return true;
 }
 static boolean isAlphabeticTxt(String s) {
  for (int i=0;i<s.length();i++) {
   char c = s.charAt(i);
   boolean ok = (c>='A' && c<='Z') || (c>='a' && c<='z') || c==' ';
   if (!ok) return false;
  }
  return true;
 }
 static void writeOutRec() throws Exception {
  try {
   String line = trimEnd(outRecord);
   outWriter.write(line);
   outWriter.write("\n");
   wsOutStatus = "00";
  } catch (Exception e) {
   wsOutStatus = "90";
  }
 }
 static void writeAuditRec() throws Exception {
  try {
   String line = trimEnd(auditRecord);
   auditWriter.write(line);
   auditWriter.write("\n");
   wsAuditStatus = "00";
  } catch (Exception e) {
   wsAuditStatus = "90";
  }
 }
 public static void main(String[] args) throws Exception {
  String col = System.getenv("DIOCLE_COLLATION");
  ebcdicMode = "EBCDIC".equals(col);
  String clk = System.getenv("DIOCLE_CLOCK");
  if (clk != null && clk.length() < 0) ebcdicMode = false;
  for (int i=0;i<12;i++) wsItems[i] = "    ";
  m0000Main();
  if (returnCode != 0) System.exit(returnCode);
 }
 private static void m0000Main() throws Exception {
  m1000Initialize();
  while (!endOfInput) {
   m2000ReadInput();
   if (!endOfInput) {
    m3000ValidateRecord();
    if (!wsError.equals("Y")) {
     m4000ProcessRecord();
     m5000WriteResult();
    } else {
     m6000RejectRecord();
    }
   }
  }
  m7000Finalize();
 }
 private static void m1000Initialize() throws Exception {
  try {
   String p = System.getenv("DD_INPUT-FILE");
   if (p == null) throw new Exception("missing");
   inLines = Files.readAllLines(Paths.get(p), StandardCharsets.ISO_8859_1);
   inPos = 0;
   wsInStatus = "00";
  } catch (Exception e) {
   wsInStatus = "35";
   System.out.println("OPEN INPUT FAILED " + wsInStatus);
   returnCode = 12;
   endOfInput = true;
  }
  try {
   String p = System.getenv("DD_OUTPUT-FILE");
   if (p == null) throw new Exception("missing");
   outWriter = Files.newBufferedWriter(Paths.get(p), StandardCharsets.ISO_8859_1, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING, java.nio.file.StandardOpenOption.WRITE);
   wsOutStatus = "00";
  } catch (Exception e) {
   wsOutStatus = "35";
   System.out.println("OPEN OUTPUT FAILED " + wsOutStatus);
   returnCode = 12;
   endOfInput = true;
  }
  try {
   String p = System.getenv("DD_AUDIT-FILE");
   if (p == null) throw new Exception("missing");
   auditWriter = Files.newBufferedWriter(Paths.get(p), StandardCharsets.ISO_8859_1, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING, java.nio.file.StandardOpenOption.WRITE);
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
  wsEdgeCode = spaces(24);
  m1100CheckRunControl();
  m1200InitializeTotals();
 }
 private static void m1100CheckRunControl() {
  String p12 = wsOldDate.substring(0,2);
  int v = Integer.parseInt(p12);
  if (v > 80) wsCentury = "19";
  else wsCentury = "20";
  wsNewDate = wsCentury + wsOldDate;
  if (wsNewDate.equals("00000000")) wsEdgeCode = padRight("BAD-RUN-DATE",24);
  wsReadTime = Long.parseLong(wsNewDate);
 }
 private static void m1200InitializeTotals() {
  wsReportTotal = BigDecimal.ZERO;
  wsCycleTotal = BigDecimal.ZERO;
  wsDebits = BigDecimal.ZERO;
  wsCredits = BigDecimal.ZERO;
  wsDelta = BigDecimal.ZERO;
  wsOutOfBalance = 0;
  wsPrevKey = spaces(12);
  wsPrevBranch = spaces(3);
  wsDayBasis = 365;
  wsLeapFlag = "N";
  wsFeeWaived = "N";
 }
 private static void m2000ReadInput() {
  if (inLines == null || inPos >= inLines.size()) {
   wsInStatus = "10";
   endOfInput = true;
  } else {
   String raw = inLines.get(inPos);
   inPos++;
   wsInStatus = "00";
   wsRecordCount = wsRecordCount + 1;
   inRecord = padRight(raw,160);
   wsRawBase = inRecord.substring(0,120);
   wsKey = inRecord.substring(0,12);
   wsBranch = inRecord.substring(12,15);
   wsAmountText = inRecord.substring(15,23);
   String t = inRecord.substring(23,31);
   wsShortAmount = t.substring(0,6);
   wsStatus = inRecord.substring(31,39);
  }
  if (!wsInStatus.equals("00") && !wsInStatus.equals("10")) {
   System.out.println("READ FAILURE " + wsInStatus);
   wsError = "Y";
   endOfInput = true;
  }
 }
 private static void m3000ValidateRecord() {
  wsError = "N";
  if (wsKey.equals(spaces(12))) m3100BadKey();
  if (!isNumericTxt(wsAmountText)) m3200BadAmount();
  if (wsStatus.equals(spaces(8))) m3300BadStatus();
  if (!isAlphabeticTxt(wsBranch)) m3400BadBranch();
  if (wsRecordCount > 1 && compareAlpha(wsKey, wsPrevKey) < 0) wsEdgeCode = padRight("SORT-ORDER",24);
  wsPrevKey = wsKey;
  m3500CheckMultiRecordType();
  m3600CheckDateBoundary();
 }
 private static void m3100BadKey() {
  wsError = "Y";
  wsEdgeCode = padRight("MISSING-KEY",24);
 }
 private static void m3200BadAmount() {
  wsError = "Y";
  wsEdgeCode = padRight("INVALID-AMOUNT",24);
 }
 private static void m3300BadStatus() {
  wsError = "Y";
  wsEdgeCode = padRight("INVALID-STATUS",24);
 }
 private static void m3400BadBranch() {
  wsError = "Y";
  wsEdgeCode = padRight("INVALID-BRANCH",24);
 }
 private static void m3500CheckMultiRecordType() {
  String c1 = inRecord.substring(0,1);
  if (!c1.equals("A") && !c1.equals("T")) wsEdgeCode = padRight("UNKNOWN-RECORD-TYPE",24);
  String c2 = inRecord.substring(1,2);
  if (c2.equals("D")) wsItemCount = 2;
  else wsItemCount = 1;
  wsReadTime = 1;
  while (!(wsReadTime > wsItemCount)) {
   wsItems[(int)wsReadTime - 1] = "0000";
   wsReadTime = wsReadTime + 1;
  }
 }
 private static void m3600CheckDateBoundary() {
  String mm = wsNewDate.substring(4,6);
  if (compareAlpha(mm, "12") > 0) {
   wsError = "Y";
   wsEdgeCode = padRight("BAD-RPT-MONTH",24);
  }
 }
 private static void m4000ProcessRecord() throws Exception {
  m4100ControlBreakCheck();
  m4200PrintDetailLine();
  m4300SafeTruncMove();
  m8000CallDateUtility();
 }
 private static void m4100ControlBreakCheck() throws Exception {
  if (!wsBranch.equals(wsCtlBranch)) {
   m4110BranchFooting();
   m4120BranchHeading();
  }
  if (!wsKey.equals(wsCtlAcct)) {
   m4130AccountFooting();
   wsCtlAcct = wsKey;
   wsAcctTotal = BigDecimal.ZERO;
  }
 }
 private static void m4110BranchFooting() throws Exception {
  if (!wsCtlBranch.equals(spaces(3))) {
   outRecord = spaces(200);
   String s = "BRANCH TOTAL " + wsCtlBranch;
   outRecord = padRight(s,200);
   writeOutRec();
   wsGrandTotal = wsGrandTotal.add(wsBranchTotal);
   wsBranchTotal = BigDecimal.ZERO;
  }
 }
 private static void m4120BranchHeading() throws Exception {
  wsCtlBranch = wsBranch;
  wsPageNo = wsPageNo + 1;
  wsLineNo = 0;
  outRecord = spaces(200);
  String s = "BRANCH " + wsCtlBranch + " PAGE ";
  outRecord = padRight(s,200);
  writeOutRec();
  if (!wsOutStatus.equals("00")) System.out.println("HEADING WRITE FAIL " + wsOutStatus);
 }
 private static void m4130AccountFooting() throws Exception {
  if (!wsCtlAcct.equals(spaces(12))) {
   outRecord = spaces(200);
   String s = "ACCOUNT TOTAL " + wsCtlAcct;
   outRecord = padRight(s,200);
   writeOutRec();
   wsBranchTotal = wsBranchTotal.add(wsAcctTotal);
   wsAcctTotal = BigDecimal.ZERO;
  }
 }
 private static void m4200PrintDetailLine() throws Exception {
  if (wsLineNo >= wsPageSize) {
   wsPageNo = wsPageNo + 1;
   wsLineNo = 0;
   outRecord = spaces(200);
   String s = "PAGE " + " CONTINUED";
   outRecord = padRight(s,200);
   writeOutRec();
  }
  wsLineNo = wsLineNo + 1;
  outRecord = spaces(200);
  String s = wsKey + " " + wsAmountText;
  outRecord = padRight(s,200);
  writeOutRec();
  wsAcctTotal = wsAcctTotal.add(wsEntryAmt);
  wsCycleTotal = wsCycleTotal.add(wsEntryAmt);
 }
 private static void m4300SafeTruncMove() throws Exception {
  wsSrcText = inRecord.substring(0,40);
  int lenS = 40;
  int lenD = 18;
  if (lenS > lenD) {
   wsTruncFlag = "Y";
   wsEdgeCode = padRight("TRUNCATED",24);
   auditRecord = spaces(200);
   String s = "TRUNCATION HELD " + wsSrcText;
   auditRecord = padRight(s,200);
   writeAuditRec();
   wsDstText = wsSrcText.substring(0,18);
  } else {
   wsTruncFlag = "N";
   wsDstText = padRight(wsSrcText,18);
  }
 }
 private static void m5000WriteResult() throws Exception {
  wsLine = spaces(200);
  outRecord = spaces(200);
  String s = wsKey + "|" + wsBranch + "|" + wsEdgeCode;
  outRecord = padRight(s,200);
  writeOutRec();
  if (!wsOutStatus.equals("00")) {
   System.out.println("WRITE FAILURE " + wsOutStatus);
   returnCode = 12;
  } else {
   wsAcceptCount = wsAcceptCount + 1;
   wsCycleTotal = wsCycleTotal.add(wsEntryAmt);
  }
  auditRecord = outRecord;
  writeAuditRec();
  if (!wsAuditStatus.equals("00")) System.out.println("AUDIT WRITE FAILURE " + wsAuditStatus);
 }
 private static void m6000RejectRecord() throws Exception {
  wsRejectCount = wsRejectCount + 1;
  wsLine = spaces(200);
  outRecord = spaces(200);
  String s = "REJECT:" + wsKey + "|" + wsEdgeCode;
  outRecord = padRight(s,200);
  writeOutRec();
  if (!wsOutStatus.equals("00")) System.out.println("REJECT WRITE FAILURE " + wsOutStatus);
 }
 private static void m7000Finalize() throws Exception {
  m7100CloseFiles();
  m7200WriteTrailer();
  m7300ReportCounts();
  if (wsRejectCount > 0) System.out.println("REJECTS PRESENT: " + fmtU(wsRejectCount,10));
  returnCode = 0;
 }
 private static void m7100CloseFiles() throws Exception {
  try {
   if (outWriter != null) outWriter.close();
   wsOutStatus = "00";
  } catch (Exception e) {
   wsOutStatus = "90";
  }
  if (!wsInStatus.equals("00") && !wsInStatus.equals("10")) System.out.println("CLOSE INPUT ISSUE " + wsInStatus);
  if (!wsOutStatus.equals("00")) System.out.println("CLOSE OUTPUT ISSUE " + wsOutStatus);
  try {
   if (auditWriter != null) auditWriter.close();
   wsAuditStatus = "00";
  } catch (Exception e) {
   wsAuditStatus = "90";
  }
  if (!wsAuditStatus.equals("00")) System.out.println("CLOSE AUDIT ISSUE " + wsAuditStatus);
  if (!wsInStatus.equals("10") && !wsInStatus.equals("00")) System.out.println("CLOSE INPUT ISSUE " + wsInStatus);
 }
 private static void m7200WriteTrailer() {
  outRecord = spaces(200);
  wsLine = spaces(200);
  wsCountDisp = fmtU(wsRecordCount,9);
  String s = "TRAILER COUNT=" + wsCountDisp;
  outRecord = padRight(s,200);
  wsPacked = wsCycleTotal;
  wsDebitsToReport();
 }
 private static void wsDebitsToReport() {
  wsReportTotal = wsReportTotal.add(wsDebits);
  wsReportTotal = wsReportTotal.subtract(wsCredits);
  BigDecimal d = wsDebits.subtract(wsCredits);
  BigDecimal limit = new BigDecimal("99999999999.99");
  BigDecimal low = new BigDecimal("-99999999999.99");
  if (d.compareTo(limit) > 0 || d.compareTo(low) < 0) wsDelta = BigDecimal.ZERO;
  else wsDelta = d;
 }
 private static void m7300ReportCounts() {
  System.out.println("READ: " + fmtU(wsRecordCount,10));
  System.out.println("ACCEPTED: " + fmtU(wsAcceptCount,10));
  System.out.println("REJECTED: " + fmtU(wsRejectCount,10));
  System.out.println("RETURNS: " + fmtU(wsReturnCount,10));
  System.out.println("OUT-OF-BALANCE: " + fmtU(wsOutOfBalance,10));
 }
 private static void m8000CallDateUtility() {
  lsDateYyyymmdd = wsNewDate;
  lsDayBasis = wsDayBasis;
  m0000MainHtdateut();
  if (returnCode != 0) {
   System.out.println("DATE UTILITY RC=" + fmtU(returnCode,9));
   wsEdgeCode = padRight("DATEUT-FAIL",24);
  }
  if (wsLeapFlag.equals("Y")) wsDayBasis = 366;
  else wsDayBasis = 365;
 }
 private static void m9000AbendHandler() {
  System.out.println("FATAL ERROR " + wsEdgeCode);
  returnCode = 16;
 }
 private static void m0000MainHtdateut() {
  m1000SplitDate();
  m2000CheckLeapYear();
  m3000ValidateDate();
  m4000NormalizeBasis();
  m5000ComputeDayCount();
 }
 private static void m1000SplitDate() {
  String y = lsDateYyyymmdd.substring(0,4);
  String m = lsDateYyyymmdd.substring(4,6);
  String d = lsDateYyyymmdd.substring(6,8);
  htYear = Integer.parseInt(y);
  htMonth = Integer.parseInt(m);
  htDay = Integer.parseInt(d);
  if (htMonth < 1 || htMonth > 12) htValid = "N";
  if (htDay < 1 || htDay > 31) htValid = "N";
 }
 private static void m2000CheckLeapYear() {
  long r4 = htYear % 4;
  if (r4 == 0) htLeap = "Y";
  else htLeap = "N";
  htQuot = htYear / 4;
  htRem = r4;
  long r100 = htYear % 100;
  htQuot = htYear / 100;
  htRem = r100;
  if (r100 == 0) htLeap = "N";
  long r400 = htYear % 400;
  htQuot = htYear / 400;
  htRem = r400;
  if (r400 == 0) htLeap = "Y";
 }
 private static void m3000ValidateDate() {
  if (htMonth >= 1 && htMonth <= 12) {
   int ml = htMdays[htMonth - 1];
   if (htDay > ml) {
    if (!(htMonth == 2 && htLeap.equals("Y") && htDay == 29)) htValid = "N";
   }
  }
  if (!htValid.equals("Y")) returnCode = 4;
  else returnCode = 0;
 }
 private static void m4000NormalizeBasis() {
  htBasisWork = lsDayBasis;
  if (htBasisWork != 360 && htBasisWork != 365 && htBasisWork != 366) htBasisWork = 365;
  if (htLeap.equals("Y")) htBasisWork = 366;
 }
 private static void m5000ComputeDayCount() {
  htDayCount = 0;
  htIdx = 1;
  while (!(htIdx >= htMonth)) {
   htDayCount = htDayCount + htMdays[htIdx - 1];
   htIdx = htIdx + 1;
  }
  htDayCount = htDayCount + htDay;
  if (htMonth > 2 && htLeap.equals("Y")) htDayCount = htDayCount + 1;
  htQty = htDayCount;
 }
}
