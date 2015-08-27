import java.io.*;
public class Crc {
static int[] t=new int[256];
static int c(int c, byte[] b) {
 c=~c;
 for (int i=0; b.length>i; ++i)
  c=t[(c^b[i])&0xff]^((c>>8)&0xffffff);
 return ~c;
}
static String h(int c) {
 String s=Integer.toHexString(c);
 while (8>s.length()) s='0'+s;
 return s;
}
public static void main(String[] a) throws Throwable {
 for (int n=0; 256>n; ++n) {
  int c=n;
  for (int k=0; 8>k; ++k)
   c=(0!=(c&1)?0xedb88320:0)^((c>>1)&0x7fffffff);
  t[n]=c;
 }
 BufferedReader r=new BufferedReader(new FileReader(a[0]));
 int c=0, ln=1;
 for (String l; null!=(l=r.readLine()); ++ln) {
  c=c(c, (l.trim()+'\n').getBytes());
  System.out.println(h(c)+'|'+ln+'|'+l);
 }
}
}
