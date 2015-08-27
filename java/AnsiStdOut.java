import java.io.*;
public class AnsiStdOut {
int br=107, ch=1, cw=1, cx1, cx2, cy1, cy2, de=300, dh=1, ds,
 dw=1, fr, da=40, hh, id=2000, th=1, tw=2, ww, xx=2, yy=2;
int[] ct=new int[256];
String fn;
boolean[][] im;
byte[] td=new byte[32*dh*dw];
int c(int c, int o, int l) {
 c=~c;
 for (; 0<l; --l,++o)
  c=ct[(c^td[o])&0xff]^((c>>8)&0xffffff);
 return ~c;
}
void d(int x, int y, int i) {
 f(3*tw+x*cw, 3*th+y*ch, cw, ch, 0!=(td[i]&1));
 td[i]>>=1;
}
void e() {
 f(0, 0, ww, hh, false);
 t(1, 1);
 for (int i=dw; 0<i; --i) t(1+2*i, 1);
 t(-4, 1);
 t(-2, 1);
 for (int i=dh; 0<i; --i) t(1, 1+2*i);
 t(1, -2);
 t(-2, -2);
}
int e(int c, int d, int t) {
 int s=(1+2*d)*t-16*d*c;
 if (0<s) {
  c+=s/(16*d);
  if (0!=s%(16*d)) ++c;
 }
 return c;
}
void f(int x, int y, int w, int h, boolean b) {
 for (int y2=y+h-1; y<=y2; --y2)
  for (int x2=x+w-1; x<=x2; --x2)
   im[y2][x2]=b;
}
void m() throws Throwable {
 ch=e(ch, dh, th);
 cw=e(cw, dw, tw);
 hh=4*th+16*dh*ch;
 ww=6*tw+16*dw*cw;
 ds=28*dh*dw-1;
 cx1=8*dw;
 cx2=16*dw-1;
 cy1=8*dh;
 cy2=16*dh-1;
 im=new boolean[hh][ww];
 for (int n=0; 256>n; ++n) {
  int c=n;
  for (int k=0; 8>k; ++k)
   c=(0!=(c&1)?0xedb88320:0)^((c>>1)&0x7fffffff);
  ct[n]=c;
 }
 File fi=new File(fn);
 fn=fi.getName();
 ByteArrayOutputStream os=new ByteArrayOutputStream();
 os.write(fn.length());
 for (int ii=0; fn.length()>ii; ++ii)
  os.write(fn.charAt(ii));
 int ll=(int)fi.length();
 for (int ii=0; 32>ii; ii+=8)
  os.write(ll>>ii);
 fr+=ds-os.size();
 byte[] buf=new byte[4096];
 DataInputStream is=new DataInputStream(new FileInputStream(fi));
 for (int rr; 0<=(rr=is.read(buf)); )
  os.write(buf, 0, rr);
 if (0!=os.size()%ds)
  os.write(new byte[ds-os.size()%ds]);
 byte[] dt=os.toByteArray();
 e();
 r();
 Thread.sleep(id);
 int c=(dh<<16)|dw;
 boolean cl=false;
 for (int of=0; dt.length>of; cl=!cl) {
  td[0]=(byte)(cl?0xa5:0x5a);
  for (int di=0; dh*dw>di; ++di) {
   int dj=(0==di)?1:0;
   System.arraycopy(dt, of, td, 32*di+dj, 28-dj);
   of+=28-dj;
   c=c(c, 32*di, 28);
   for (int cc=c, tdc=4, tdi=32*di+28; 0<tdc; --tdc, ++tdi) {
    td[tdi]=(byte)(cc&0xff);
    cc>>=8;
   }
  }
  if (fr<=of) {
   e();
   for (int tdc=8, tdi=1, dy=0; 16*dh>dy; ++dy) {
    boolean my=cy1==dy, sy=(0==dy) || (cy2==dy);
    for (int dx=0; 16*dw>dx; ++dx) {
     boolean mx=cx1==dx, sx=(0==dx) || (cx2==dx);
     if ((sx && sy) || (mx && sy) || (sx && my))
      d(dx, dy, 0);
     else {
      d(dx, dy, tdi);
      --tdc;
      if (0>=tdc) {
       ++tdi;
       tdc=8;
      }
     }
    }
   }
   r();
   Thread.sleep(de);
  }
 }
 System.out.print("\u001b[m\u001b[2J\u001b[0;0H");
}
public static void main(String[] a) throws Throwable {
 AnsiStdOut m=new AnsiStdOut();
 m.fr=Integer.parseInt(a[0]);
 m.fn=a[1];
 m.m();
}
void r() {
 StringBuffer s=new StringBuffer(8*(ww+xx+2)*(hh+yy));
 s.append("\u001b[0;");
 s.append(da);
 s.append("m\u001b[2J");
 boolean b=false;
 for (int y=0; hh>y; ++y) {
  s.append("\u001b[");
  s.append(yy+y);
  s.append(';');
  s.append(xx);
  s.append('H');
  for (int x=0; ww>x; ++x) {
   boolean b2=im[y][x];
   if (b!=b2) {
    b=b2;
    s.append("\u001b[");
    s.append(b?br:da);
    s.append('m');
   }
   s.append('\u0020');
  }
 }
 s.append("\u001b[0;0H");
 System.out.print(s);
 System.out.flush();
}
void t(int x, int y) {
 x*=tw;
 if (0>x) x+=ww;
 y*=th;
 if (0>y) y+=hh;
 f(x, y, tw, th, true);
}
}
