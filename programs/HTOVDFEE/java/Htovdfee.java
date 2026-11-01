import java.io.*;
import java.math.*;
import java.util.*;
public class Htovdfee {
private String wsInStatus="  ";
private String wsOutStatus="  ";
private boolean endOfInput=false;
private boolean inputError=false;
private long wsRecordCount=0;
private long wsAcceptCount=0;
private long wsRejectCount=0;
private int wsArithError=0;
private String wsEdgeCode=spaces(24);
private String wsKey=spaces(12);
private String wsPrevKey=spaces(12);
private String wsBranch=spaces(3);
private String wsPrevBranch=spaces(3);
private String wsStatus=spaces(8);
private BigDecimal wsAmount=zero(2);
private BigDecimal wsBalance=zero(2);
private long wsReadTime=0;
private long wsWriteTime=0;
private long wsAbendCode=0;
private String wsReturnReason=spaces(3);
private long wsReturnCount=0;
private BigDecimal wsCycleTotal=zero(2);
private BigDecimal wsDebits=zero(2);
private BigDecimal wsCredits=zero(2);
private BigDecimal wsDelta=zero(2);
private BigDecimal wsReportTotal=zero(2);
private String wsAmountText=spaces(8);
private String wsShortAmount=spaces(6);
private String wsRawBase=spaces(120);
private String wsFeeOverride=" ";
private int wsItemCount=0;
private String[] wsItems=new String[12];
private BigDecimal wsPacked=zero(2);
private BigDecimal wsZoned=zero(2);
private String wsLine=spaces(200);
private BigDecimal wsOdAmount=zero(2);
private BigDecimal wsNsfFee=new BigDecimal("35.00");
private BigDecimal wsPostedFee=zero(2);
private String wsFeeWaived="N";
private BigDecimal wsFee=zero(2);
private BigDecimal wsFxRate=zero(4);
private String wsOldDate="991231";
private int wsCentury=20;
private String wsNewDate="00000000";
private long wsDayBasis=365;
private String wsLeapFlag="N";
private String wsCallKey=spaces(12);
private BigDecimal wsCallAmt=zero(2);
private int wsCallError=0;
private BigDecimal wsCallValue=zero(2);
private String wsCallText=spaces(80);
private String inRecord=spaces(160);
private String outRecord=spaces(200);
private int returnCode=0;
private boolean ebcdic=false;
private BufferedReader inReader=null;
private OutputStream outStream=null;
private boolean inOpen=false;
private boolean outOpen=false;
private static class Svc{
String callName=spaces(8);
String dateRaw="000000";
String dateFull="00000000";
int century=20;
BigDecimal temp=zero(2);
BigDecimal round=zero(2);
long counter=0;
String message=spaces(80);
}
private Svc feepSvc=new Svc();
private Svc postSvc=new Svc();
private Svc audtSvc=new Svc();
private static String spaces(int n){StringBuilder b=new StringBuilder();for(int i=0;i<n;i++)b.append(' ');return b.toString();}
private static BigDecimal zero(int s){return BigDecimal.ZERO.setScale(s);}
private static String padRight(String s,int n){if(s==null)s="";if(s.length()>n)return s.substring(0,n);StringBuilder b=new StringBuilder(s);while(b.length()<n)b.append(' ');return b.toString();}
public static void main(String[] args){Htovdfee p=new Htovdfee();try{p.run();}catch(Throwable t){try{System.out.flush();}catch(Exception e){}try{System.exit(12);}catch(Exception e){}}}
private void run() throws Exception{String c=System.getenv("DIOCLE_COLLATION");ebcdic="EBCDIC".equals(c);paragraph0000Main();if(returnCode!=0)System.exit(returnCode);}
private void abend(int code){try{System.out.flush();}catch(Exception e){}try{if(inReader!=null)inReader.close();}catch(Exception e){}try{if(outStream!=null){outStream.flush();outStream.close();}}catch(Exception e){}inOpen=false;outOpen=false;try{System.err.print("CEE3ABD abend code "+code+"\n");System.err.flush();}catch(Exception e){}System.exit(16);}
private void displayLine(String s){System.out.print(s+"\n");}
private String fmtCount(long v,int d){long a=v&0xFFFFFFFFL;int w=(d==9?10:d);String s=Long.toString(a);if(s.length()>w)s=s.substring(s.length()-w);else while(s.length()<w)s="0"+s;return s;}
private String fmtSigned(BigDecimal v,int id,int fd){boolean neg=v.signum()<0;BigDecimal a=v.abs().setScale(fd,RoundingMode.UNNECESSARY);String dig=a.movePointRight(fd).toBigInteger().toString();int tot=id+fd;if(dig.length()>tot)dig=dig.substring(dig.length()-tot);else while(dig.length()<tot)dig="0"+dig;String ip=dig.substring(0,id);String fp=dig.substring(id);return (neg?"-":"+")+ip+"."+fp;}
private BigDecimal maxVal(int id,int fd){StringBuilder b=new StringBuilder();for(int i=0;i<id;i++)b.append('9');if(fd>0){b.append('.');for(int i=0;i<fd;i++)b.append('9');}return new BigDecimal(b.toString());}
private boolean isSizeError(BigDecimal v,int id,int fd){return v.abs().compareTo(maxVal(id,fd))>0;}
private boolean isNumeric(String s){if(s==null||s.length()==0)return false;for(int i=0;i<s.length();i++){char ch=s.charAt(i);if(ch<'0'||ch>'9')return false;}return true;}
private boolean isAlphabetic(String s){for(int i=0;i<s.length();i++){char ch=s.charAt(i);if(ch==' ')continue;if(ch>='A'&&ch<='Z')continue;if(ch>='a'&&ch<='z')continue;return false;}return true;}
private int compareAlpha(String a,String b){if(ebcdic){try{byte[] ba=a.getBytes("Cp037");byte[] bb=b.getBytes("Cp037");int n=Math.min(ba.length,bb.length);for(int i=0;i<n;i++){int x=ba[i]&0xFF;int y=bb[i]&0xFF;if(x!=y)return x-y;}return ba.length-bb.length;}catch(Exception e){}}return a.compareTo(b);}
private boolean isLowValues(String s){for(int i=0;i<s.length();i++)if(s.charAt(i)!=0)return false;return true;}
private String setSub(String base,int pos1,int len,String val){String v=padRight(val,len).substring(0,len);StringBuilder b=new StringBuilder(base);for(int i=0;i<len;i++)b.setCharAt(pos1-1+i,v.charAt(i));return b.toString();}
private String getSub(String base,int pos1,int len){return base.substring(pos1-1,pos1-1+len);}
private int parseNum(String s){try{return Integer.parseInt(s);}catch(Exception e){return 0;}}
private void paragraph0000Main(){paragraph1000Initialize();while(!endOfInput){paragraph2000ReadInput();if(!endOfInput){paragraph3000ValidateRecord();if(!inputError){paragraph4000ProcessRecord();paragraph5000WriteResult();}else{paragraph6000RejectRecord();}}}paragraph7000Finalize();}
private void paragraph1000Initialize(){String inPath=null;String outPath=null;try{inPath=System.getenv("DD_INPUT-FILE");}catch(Exception e){}try{outPath=System.getenv("DD_OUTPUT-FILE");}catch(Exception e){}if(inPath==null||inPath.equals("")){wsInStatus="35";displayLine("OPEN INPUT FAILED "+wsInStatus);returnCode=12;endOfInput=true;}else{try{inReader=new BufferedReader(new InputStreamReader(new FileInputStream(inPath),"ISO-8859-1"));inOpen=true;wsInStatus="00";}catch(Exception e){wsInStatus="35";displayLine("OPEN INPUT FAILED "+wsInStatus);returnCode=12;endOfInput=true;}}if(outPath==null||outPath.equals("")){wsOutStatus="35";displayLine("OPEN OUTPUT FAILED "+wsOutStatus);returnCode=12;endOfInput=true;}else{try{outStream=new FileOutputStream(outPath);outOpen=true;wsOutStatus="00";}catch(Exception e){wsOutStatus="35";displayLine("OPEN OUTPUT FAILED "+wsOutStatus);returnCode=12;endOfInput=true;}}wsRecordCount=0;wsAcceptCount=0;wsRejectCount=0;wsEdgeCode=spaces(24);paragraph1100CheckRunControl();paragraph1200InitializeTotals();}
private void paragraph1100CheckRunControl(){int yy=parseNum(getSub(wsOldDate,1,2));if(yy>80)wsCentury=19;else wsCentury=20;String cs=fmtCount(wsCentury,2);wsNewDate=setSub(wsNewDate,1,2,cs);wsNewDate=setSub(wsNewDate,3,6,wsOldDate);}
private void paragraph1200InitializeTotals(){wsReportTotal=zero(2);wsCycleTotal=zero(2);wsDebits=zero(2);wsCredits=zero(2);wsDelta=zero(2);wsPrevKey=spaces(12);wsPrevBranch=spaces(3);}
private void paragraph2000ReadInput(){if(!inOpen||inReader==null){endOfInput=true;return;}try{String line=inReader.readLine();if(line==null){endOfInput=true;wsInStatus="10";}else{wsRecordCount=(wsRecordCount+1)&0xFFFFFFFFL;inRecord=padRight(line,160).substring(0,160);wsRawBase=inRecord.substring(0,120);wsFeeOverride=inRecord.substring(119,120);wsKey=inRecord.substring(0,12);wsBranch=inRecord.substring(12,15);wsAmountText=inRecord.substring(15,23);wsStatus=inRecord.substring(31,39);wsInStatus="00";if(!wsInStatus.equals("00")&&!wsInStatus.equals("10")){displayLine("READ FAILURE "+wsInStatus);inputError=true;endOfInput=true;}}}catch(Exception e){wsInStatus="30";displayLine("READ FAILURE "+wsInStatus);inputError=true;endOfInput=true;}}
private void paragraph3000ValidateRecord(){inputError=false;if(wsKey.equals(spaces(12)))paragraph3100BadKey();if(!isNumeric(wsAmountText))paragraph3200BadAmount();if(wsStatus.equals(spaces(8)))paragraph3300BadStatus();if(!isAlphabetic(wsBranch))paragraph3400BadBranch();if(wsRecordCount>1&&compareAlpha(wsKey,wsPrevKey)<0)wsEdgeCode=padRight("SORT-ORDER",24);wsPrevKey=wsKey;paragraph3500CheckMultiRecordType();paragraph3600CheckDateBoundary();}
private void paragraph3100BadKey(){inputError=true;wsEdgeCode=padRight("MISSING-KEY",24);}
private void paragraph3200BadAmount(){inputError=true;wsEdgeCode=padRight("INVALID-AMOUNT",24);}
private void paragraph3300BadStatus(){inputError=true;wsEdgeCode=padRight("INVALID-STATUS",24);}
private void paragraph3400BadBranch(){inputError=true;wsEdgeCode=padRight("INVALID-BRANCH",24);}
private void paragraph3500CheckMultiRecordType(){String c1=inRecord.substring(0,1);if(!c1.equals("A")&&!c1.equals("T"))wsEdgeCode=padRight("UNKNOWN-RECORD-TYPE",24);String c2=inRecord.substring(1,2);if(c2.equals("D"))wsItemCount=2;else wsItemCount=1;}
private void paragraph3600CheckDateBoundary(){if(wsNewDate.equals("20261231"))wsEdgeCode=padRight("YEAR-END-ROLLOVER",24);if(wsNewDate.equals("20260228"))wsEdgeCode=padRight("NON-LEAP-FEB-END",24);}
private void paragraph4000ProcessRecord(){wsArithError=0;long iv=0;try{iv=Long.parseLong(wsAmountText);}catch(Exception e){iv=0;}wsAmount=BigDecimal.valueOf(iv).setScale(2);BigDecimal nb=wsBalance.subtract(wsAmount);if(isSizeError(nb,9,2))wsArithError=1;else wsBalance=nb;wsOdAmount=wsAmount;if(wsFeeOverride.equals("Y"))wsFeeWaived="Y";else wsFeeWaived="N";if(wsBalance.compareTo(BigDecimal.ZERO)<0){wsEdgeCode=padRight("NEGATIVE-BALANCE",24);if(wsFeeWaived.equals("Y")){wsPostedFee=zero(2);wsEdgeCode=padRight("FEE-WAIVED-FLAG-SET",24);}else{wsPostedFee=new BigDecimal("35.00");BigDecimal nb2=wsBalance.subtract(wsPostedFee);if(isSizeError(nb2,9,2))wsArithError=1;else wsBalance=nb2;}}else{wsPostedFee=zero(2);}wsCallKey=wsKey;wsCallAmt=wsPostedFee;callHtfeep();wsCallKey=wsKey;wsCallAmt=wsPostedFee;callHtpost();wsCallKey=wsKey;wsCallAmt=wsPostedFee;callHtaudt();BigDecimal nt=wsReportTotal.add(wsPostedFee);if(isSizeError(nt,11,2))wsArithError=1;else wsReportTotal=nt;wsLine=setSub(wsLine,1,12,wsKey);wsLine=setSub(wsLine,13,3,wsBranch);wsLine=setSub(wsLine,16,24,wsEdgeCode);wsLine=setSub(wsLine,40,12,padRight(wsAmountText,12));wsLine=setSub(wsLine,52,8,wsStatus);wsAcceptCount=(wsAcceptCount+1)&0xFFFFFFFFL;}
private void paragraph5000WriteResult(){outRecord=wsLine;try{writeLine(outRecord);wsOutStatus="00";wsWriteTime++;}catch(Exception e){try{wsOutStatus="30";}catch(Exception ex){}displayLine("WRITE FAILURE "+wsOutStatus);returnCode=12;endOfInput=true;}}
private void writeLine(String rec) throws Exception{if(outStream==null)throw new IOException();int end=rec.length();while(end>0&&rec.charAt(end-1)==' ')end--;String t=rec.substring(0,end);byte[] b=t.getBytes("ISO-8859-1");outStream.write(b);outStream.write('\n');}
private void paragraph6000RejectRecord(){wsRejectCount=(wsRejectCount+1)&0xFFFFFFFFL;displayLine("REJECT "+fmtCount(wsRecordCount,9)+" "+wsEdgeCode);wsLine=spaces(200);wsLine=setSub(wsLine,1,6,"REJECT");wsLine=setSub(wsLine,8,12,wsKey);wsLine=setSub(wsLine,24,24,wsEdgeCode);outRecord=wsLine;try{writeLine(outRecord);}catch(Exception e){}}
private void paragraph7000Finalize(){paragraph7100CheckControlTotals();paragraph7200ReportRunCounts();try{if(inReader!=null){inReader.close();}wsInStatus="00";}catch(Exception e){wsInStatus="30";}inOpen=false;try{if(outStream!=null){outStream.flush();outStream.close();}wsOutStatus="00";}catch(Exception e){wsOutStatus="30";}outOpen=false;if(!wsInStatus.equals("00"))displayLine("CLOSE IN "+wsInStatus);if(!wsOutStatus.equals("00"))displayLine("CLOSE OUT "+wsOutStatus);if(wsRejectCount>0)returnCode=4;}
private void paragraph7100CheckControlTotals(){if(wsAcceptCount+wsRejectCount!=wsRecordCount){displayLine("CONTROL COUNT MISMATCH");returnCode=12;}if(wsArithError!=0){displayLine("ARITHMETIC SIZE ERROR");returnCode=8;}}
private void paragraph7200ReportRunCounts(){displayLine("PROGRAM HTOVDFEE COMPLETE");displayLine("READ="+fmtCount(wsRecordCount,9));displayLine("ACCEPT="+fmtCount(wsAcceptCount,9));displayLine("REJECT="+fmtCount(wsRejectCount,9));displayLine("TOTAL="+fmtSigned(wsReportTotal,11,2));displayLine("EDGE="+wsEdgeCode);}
private void paragraph8000RejectAllZeroAmounts(){if(wsAmount.compareTo(BigDecimal.ZERO)==0&&wsStatus.equals(padRight("ACTIVE",8)))wsEdgeCode=padRight("ZERO-ACTIVE-AMOUNT",24);}
private void paragraph8010CheckNegativeBalance(){if(wsBalance.compareTo(BigDecimal.ZERO)<0)wsEdgeCode=padRight("NEGATIVE-BALANCE",24);}
private void paragraph8020CheckLongKey(){if(getSub(wsKey,12,1).equals(" ")){}else displayLine("FULL-WIDTH-KEY "+wsKey);}
private void paragraph8030CheckRoundedAmount(){BigDecimal r=wsAmount.setScale(2,RoundingMode.HALF_UP);if(isSizeError(r,9,2))wsArithError=1;else wsPacked=r;if(wsPacked.compareTo(BigDecimal.ZERO)==0&&wsAmount.compareTo(BigDecimal.ZERO)!=0)wsEdgeCode=padRight("TRUNCATION-RISK",24);}
private void paragraph8040CheckShortMove(){wsShortAmount=padRight(wsAmountText,6).substring(0,6);if(!getSub(wsAmountText,7,2).equals("00"))wsEdgeCode=padRight("SHORT-PIC-TRUNCATION",24);}
private void paragraph8050CheckFeeOverlay(){if(wsFeeOverride.equals("Y"))wsFeeWaived="Y";else wsFeeWaived="N";}
private void paragraph8060CheckSignedZoned(){wsZoned=wsBalance;if(wsZoned.compareTo(BigDecimal.ZERO)<0&&wsBalance.compareTo(BigDecimal.ZERO)>=0)wsEdgeCode=padRight("SIGN-CONVERSION",24);}
private void paragraph8070CheckDateWindow(){if(getSub(wsOldDate,1,2).equals("00"))wsEdgeCode=padRight("Y2K-WINDOW-REVIEW",24);if(wsOldDate.equals("991231"))wsEdgeCode=padRight("CENTURY-BOUNDARY",24);}
private void paragraph8080CheckRatePrecision(){if(wsFxRate.compareTo(new BigDecimal("1.0825"))!=0)wsEdgeCode=padRight("RATE-IS-FOUR-DECIMAL",24);}
private void paragraph8090CheckRecordType(){String c=inRecord.substring(0,1);if(c.equals("A")){}else if(c.equals("T")){}else wsEdgeCode=padRight("BAD-RECORD-TYPE",24);}
private void paragraph8100CheckReturnCode(){if(!wsReturnReason.equals(spaces(3)))wsReturnCount++;if(wsReturnCount>wsRecordCount)returnCode=12;}
private void paragraph8110CheckAccumulator(){BigDecimal nt=wsReportTotal.add(wsAmount);if(isSizeError(nt,11,2))wsArithError=1;else wsReportTotal=nt;if(wsReportTotal.compareTo(BigDecimal.ZERO)<0)wsEdgeCode=padRight("NEGATIVE-CONTROL-TOTAL",24);}
private void paragraph8120CheckBranchBreak(){if(!wsBranch.equals(wsPrevBranch)){paragraph8130WriteBranchTotal();wsPrevBranch=wsBranch;}}
private void paragraph8130WriteBranchTotal(){if(!wsPrevBranch.equals(spaces(3))){displayLine("BRANCH BREAK "+wsPrevBranch);displayLine("BRANCH TOTAL "+fmtSigned(wsReportTotal,11,2));}wsReportTotal=zero(2);}
private void paragraph8140CheckInputShape(){if(inRecord.equals(spaces(160))){inputError=true;wsEdgeCode=padRight("EMPTY-RECORD",24);}if(inRecord.substring(159,160).equals(" ")==false)displayLine("TRAILING-CONTENT PRESENT");}
private void paragraph8150CheckMonthRollover(){if(wsNewDate.equals("20261231"))wsNewDate="20270101";if(wsNewDate.equals("20260131"))wsNewDate="20260201";}
private void paragraph8160CheckLeapYear(){if(wsNewDate.substring(0,4).equals("2024")){wsLeapFlag="Y";wsDayBasis=366;}else{wsLeapFlag="N";wsDayBasis=365;}}
private void paragraph8170CheckOddCents(){if(getSub(wsAmountText,7,2).equals("50"))wsEdgeCode=padRight("HALF-CENT-ROUNDING-CASE",24);if(getSub(wsAmountText,7,2).equals("99"))wsEdgeCode=padRight("ROUND-UP-BOUNDARY",24);}
private void paragraph8180CheckStatusTransition(){if(wsStatus.equals(padRight("ACTIVE",8))){}else if(wsStatus.equals(padRight("DORMANT",8))){}else if(wsStatus.equals(padRight("BLOCKED",8))){}else wsEdgeCode=padRight("UNKNOWN-STATE",24);}
private void paragraph8190CheckBalanceLimit(){if(wsBalance.compareTo(new BigDecimal("999999999.99"))>0)wsEdgeCode=padRight("BALANCE-OVERFLOW",24);if(wsBalance.compareTo(new BigDecimal("-999999999.99"))<0)wsEdgeCode=padRight("BALANCE-UNDERFLOW",24);}
private void paragraph8200CheckReconciliation(){BigDecimal d=wsDebits.subtract(wsCredits);if(isSizeError(d,11,2))wsArithError=1;else wsDelta=d;if(wsDelta.compareTo(BigDecimal.ZERO)==0){}}
private void paragraph8210CheckCopybookCount(){if(wsItemCount<1||wsItemCount>12)wsEdgeCode=padRight("OCCURS-COUNT-INVALID",24);}
private void paragraph8220CheckCallInterface(){wsLine=setSub(wsLine,1,12,wsKey);wsLine=setSub(wsLine,13,24,wsEdgeCode);if(getSub(wsLine,1,1).equals(" "))wsEdgeCode=padRight("CALL-KEY-MISSING",24);}
private void paragraph8230CheckWriteStatus(){if(wsOutStatus.equals("00")){}else{displayLine("DATASET STATUS "+wsOutStatus);returnCode=12;}}
private void paragraph8240CheckReadStatus(){if(wsInStatus.equals("10"))endOfInput=true;else if(!wsInStatus.equals("00"))displayLine("INPUT STATUS "+wsInStatus);}
private void paragraph8250CheckCurrencyScale(){BigDecimal r=wsAmount.multiply(new BigDecimal("1.0000"));r=r.setScale(2,RoundingMode.HALF_UP);if(isSizeError(r,9,2))wsArithError=1;else wsAmount=r;if(wsFxRate.signum()<0)wsEdgeCode=padRight("NEGATIVE-FX-RATE",24);}
private void paragraph8260CheckNestedCallResult(){wsAbendCode=0;if(wsAbendCode!=0){displayLine("CALLED ROUTINE RETURNED ERROR");returnCode=8;}}
private void paragraph8270CheckAuditBalance(){BigDecimal nt=wsCycleTotal.add(wsAmount);if(isSizeError(nt,11,2))wsArithError=1;else wsCycleTotal=nt;if(wsCycleTotal.compareTo(BigDecimal.ZERO)==0){}}
private void paragraph8280CheckKeyUniqueness(){if(wsKey.equals(wsPrevKey)&&!wsKey.equals(spaces(12)))wsEdgeCode=padRight("DUPLICATE-KEY",24);wsPrevKey=wsKey;}
private void paragraph8290CheckRunId(){if(wsNewDate.equals("00000000")){displayLine("RUN DATE NOT INITIALIZED");returnCode=12;}if(wsRecordCount==0)displayLine("EMPTY INPUT");}
private void paragraph9900OperationalChecks(){paragraph8000RejectAllZeroAmounts();paragraph8010CheckNegativeBalance();paragraph8020CheckLongKey();paragraph8030CheckRoundedAmount();paragraph8040CheckShortMove();paragraph8050CheckFeeOverlay();paragraph8060CheckSignedZoned();paragraph8070CheckDateWindow();paragraph8080CheckRatePrecision();paragraph8090CheckRecordType();paragraph8100CheckReturnCode();paragraph8110CheckAccumulator();paragraph8120CheckBranchBreak();paragraph8130WriteBranchTotal();paragraph8140CheckInputShape();paragraph8150CheckMonthRollover();paragraph8160CheckLeapYear();paragraph8170CheckOddCents();paragraph8180CheckStatusTransition();paragraph8190CheckBalanceLimit();paragraph8200CheckReconciliation();paragraph8210CheckCopybookCount();paragraph8220CheckCallInterface();paragraph8230CheckWriteStatus();paragraph8240CheckReadStatus();paragraph8250CheckCurrencyScale();paragraph8260CheckNestedCallResult();paragraph8270CheckAuditBalance();paragraph8280CheckKeyUniqueness();paragraph8290CheckRunId();}
private void callHtfeep(){htfeep0000Main();}
private void callHtpost(){htpost0000Main();}
private void callHtaudt(){htaudt0000Main();}
private void initResult(){wsCallError=0;wsCallValue=zero(2);wsCallText=spaces(80);}
private void htfeep0000Main(){initResult();htfeep1000ValidateInterface();if(wsCallError==0)htfeep2000ProcessRequest();htfeep3900ServiceControls();}
private void htpost0000Main(){initResult();htpost1000ValidateInterface();if(wsCallError==0)htpost2000ProcessRequest();htpost3900ServiceControls();}
private void htaudt0000Main(){initResult();htaudt1000ValidateInterface();if(wsCallError==0)htaudt2000ProcessRequest();htaudt3900ServiceControls();}
private void htfeep1000ValidateInterface(){validateInterface1000();}
private void htpost1000ValidateInterface(){validateInterface1000();}
private void htaudt1000ValidateInterface(){validateInterface1000();}
private void validateInterface1000(){if(wsCallKey.equals(spaces(12)))wsCallError=4;if(wsCallAmt.compareTo(BigDecimal.ZERO)<0)wsCallError=8;}
private void htfeep2000ProcessRequest(){htfeep3100FeeWaiver();}
private void htpost2000ProcessRequest(){}
private void htaudt2000ProcessRequest(){}
private void htfeep3000DateWindow(){dateWindow3000(feepSvc);}
private void htpost3000DateWindow(){dateWindow3000(postSvc);}
private void htaudt3000DateWindow(){dateWindow3000(audtSvc);}
private void dateWindow3000(Svc s){int yy=parseNum(s.dateRaw.substring(0,2));if(yy>80)s.century=19;else s.century=20;s.dateFull=setSub(s.dateFull,3,6,s.dateRaw);s.dateFull=setSub(s.dateFull,1,2,fmtCount(s.century,2));wsCallText=setSub(wsCallText,1,8,s.dateFull);}
private void htfeep3100FeeWaiver(){feeWaiver3100();}
private void htpost3100FeeWaiver(){feeWaiver3100();}
private void htaudt3100FeeWaiver(){feeWaiver3100();}
private void feeWaiver3100(){if(wsCallKey.substring(11,12).equals("Y")){wsCallValue=zero(2);wsCallText=padRight("WAIVED: REDEFINES FLAG SET",80);}else{BigDecimal raw=wsCallAmt.multiply(new BigDecimal("0.035"));BigDecimal t=raw.setScale(2,RoundingMode.DOWN);if(isSizeError(t,11,2))wsCallError=8;else wsCallValue=t;}}
private void htfeep3200FxQuote(){fxQuote3200(feepSvc);}
private void htpost3200FxQuote(){fxQuote3200(postSvc);}
private void htaudt3200FxQuote(){fxQuote3200(audtSvc);}
private void fxQuote3200(Svc s){s.temp=new BigDecimal("1.08");BigDecimal raw=wsCallAmt.multiply(s.temp);BigDecimal r=raw.setScale(2,RoundingMode.HALF_UP);if(isSizeError(r,11,2))wsCallError=8;else wsCallValue=r;wsCallText=padRight("RATE SCALE 4 DECIMAL PLACES",80);}
private void htfeep3300DynamicCall(){dynamicCall3300(feepSvc);}
private void htpost3300DynamicCall(){dynamicCall3300(postSvc);}
private void htaudt3300DynamicCall(){dynamicCall3300(audtSvc);}
private void dynamicCall3300(Svc s){s.callName=padRight("HTAUDT",8);}
private void htfeep3400PackedPost(){packedPost3400(feepSvc,false);}
private void htpost3400PackedPost(){packedPost3400(postSvc,true);}
private void htaudt3400PackedPost(){packedPost3400(audtSvc,false);}
private void packedPost3400(Svc s,boolean cond){BigDecimal r=wsCallAmt.setScale(2,RoundingMode.HALF_UP);if(isSizeError(r,11,2))wsCallError=8;else wsCallValue=r;if(cond){if(s.counter==999999999L)abend((int)s.counter);}else{abend((int)s.counter);}}
private void htfeep3500AuditFormat(){auditFormat3500(feepSvc);}
private void htpost3500AuditFormat(){auditFormat3500(postSvc);}
private void htaudt3500AuditFormat(){auditFormat3500(audtSvc);}
private void auditFormat3500(Svc s){wsCallText=setSub(wsCallText,1,12,wsCallKey);s.round=wsCallAmt;wsCallValue=s.round;}
private void htfeep3900ServiceControls(){serviceControls3900(feepSvc);}
private void htpost3900ServiceControls(){serviceControls3900(postSvc);}
private void htaudt3900ServiceControls(){serviceControls3900(audtSvc);}
private void serviceControls3900(Svc s){checkKeyLowValues4100(s);checkAmountCap4110(s);checkAmountFloor4120(s);checkResultState4130(s);checkServiceName4140(s);checkDateCalendar4150(s);checkDateDay4160(s);checkDateRollover4170(s);checkWindowLow4180(s);checkWindowHigh4190(s);checkRoundBoundary4200(s);checkPackedDecimal4210(s);checkResultConsistency4220(s);checkDynamicName4230(s);checkKeyCharacters4240(s);checkCallCounter4250(s);checkAuditLength4260(s);checkCurrencyScale4270(s);checkWaiverState4280(s);checkRateValid4290(s);checkReturnText4300(s);checkReferenceKey4310(s);checkContentIsolation4320(s);checkResultInitialized4330(s);checkDecimalRounding4340(s);checkCenturyPolicy4350(s);checkReturnCode4360(s);}
private void checkKeyLowValues4100(Svc s){if(isLowValues(wsCallKey))wsCallError=4;}
private void checkAmountCap4110(Svc s){if(wsCallAmt.compareTo(new BigDecimal("999999999.99"))>0)wsCallError=8;}
private void checkAmountFloor4120(Svc s){if(wsCallAmt.compareTo(new BigDecimal("-999999999.99"))<0)wsCallError=8;}
private void checkResultState4130(Svc s){if(wsCallError==0)wsCallText=setSub(wsCallText,1,2,"OK");}
private void checkServiceName4140(Svc s){if(s.callName.equals(spaces(8)))wsCallText=padRight("SERVICE NAME UNSET",80);}
private void checkDateCalendar4150(Svc s){if(parseNum(s.dateFull.substring(4,6))>12)wsCallError=8;}
private void checkDateDay4160(Svc s){if(parseNum(s.dateFull.substring(6,8))>31)wsCallError=8;}
private void checkDateRollover4170(Svc s){if(s.dateRaw.equals("123199"))wsCallText=padRight("YEAR END",80);}
private void checkWindowLow4180(Svc s){if(s.dateRaw.substring(0,2).equals("00"))s.century=20;}
private void checkWindowHigh4190(Svc s){if(parseNum(s.dateRaw.substring(0,2))>80)s.century=19;}
private void checkRoundBoundary4200(Svc s){BigDecimal raw=wsCallAmt.add(new BigDecimal("0.005"));BigDecimal r=raw.setScale(2,RoundingMode.HALF_UP);if(isSizeError(r,11,2))wsCallError=8;else s.round=r;}
private void checkPackedDecimal4210(Svc s){BigDecimal r=wsCallAmt.setScale(2,RoundingMode.HALF_UP);if(isSizeError(r,11,2))wsCallError=8;else s.temp=r;}
private void checkResultConsistency4220(Svc s){if(wsCallError!=0)wsCallValue=zero(2);}
private void checkDynamicName4230(Svc s){if(s.callName.equals(padRight("HTAUDT",8))){}else wsCallError=8;}
private void checkKeyCharacters4240(Svc s){if(!wsCallKey.substring(0,3).equals("SYN"))wsCallError=4;}
private void checkCallCounter4250(Svc s){s.counter=(s.counter+1)&0xFFFFFFFFL;}
private void checkAuditLength4260(Svc s){if(wsCallText.equals(spaces(80)))wsCallText=padRight("NO AUDIT DETAIL",80);}
private void checkCurrencyScale4270(Svc s){if(s.temp.compareTo(BigDecimal.ZERO)<0)wsCallError=8;}
private void checkWaiverState4280(Svc s){if(wsCallKey.substring(11,12).equals("Y"))wsCallValue=zero(2);}
private void checkRateValid4290(Svc s){if(s.temp.compareTo(new BigDecimal("999.9999"))>0)wsCallError=8;}
private void checkReturnText4300(Svc s){if(wsCallError!=0)wsCallText=padRight("CALL FAILED",80);}
private void checkReferenceKey4310(Svc s){if(wsCallKey.substring(11,12).equals(" ")){}}
private void checkContentIsolation4320(Svc s){s.temp=wsCallAmt;}
private void checkResultInitialized4330(Svc s){}
private void checkDecimalRounding4340(Svc s){if(wsCallAmt.compareTo(new BigDecimal("0.005"))==0)wsCallText=padRight("HALF CENT",80);}
private void checkCenturyPolicy4350(Svc s){if(s.century<19||s.century>20)wsCallError=8;}
private void checkReturnCode4360(Svc s){if(wsCallError>8)wsCallError=8;}
}