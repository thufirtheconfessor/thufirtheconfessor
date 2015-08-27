#If VBA7 Then
 Private Declare PtrSafe Function GetTickCount Lib "kernel32" () As Long
#Else
 Private Declare Function GetTickCount Lib "kernel32" () As Long
#End If
Dim c&, cl As Boolean, ct&(255)
Dim dt1() As Byte, dt2() As Byte, of&, td() As Byte
Dim br&, da&, ch&, cw&, cx1&, cx2&, cy1&, cy2&, de&, dh&
Dim ds&, dw&, fr&, hh&, id&, pw&, ph&, th&, tw&, ww&
Function cr&(c&, b() As Byte, o&, l&)
 cr = Not c
 For i& = o To o + l - 1
  cr = ct((cr Xor b(i)) And &HFF&) Xor sr(cr, 8)
 Next i
 cr = Not cr
End Function
Function crs&(c&, s$)
Dim b() As Byte
ReDim b(Len(s) - 1)
For i& = 1 To Len(s)
 b(i - 1) = Asc(Mid(s, i, 1)) And &HFF
Next i
crs = cr(c, b, 0, Len(s))
End Function
Sub crc()
 Sheets(1).Activate
 ic
 Rows.Delete (Rows.Count)
 With ThisWorkbook.VBProject.VBComponents("Module1").CodeModule
  Dim c&
  c = 0&
  For i& = 1 To .CountOfLines
   c = crs(c, Trim(.Lines(i, 1)) & Chr(10))
   Cells(i, 1).Value = h(c)
   Cells(i, 2).Value = i
   Cells(i, 3).Value = .Lines(i, 1)
  Next i
 End With
 Range("A1:C" & Rows.Count).Font.Name = "Courier New"
 Range("A1:C" & Rows.Count).NumberFormat = "@"
 Range("B1:B" & Rows.Count).NumberFormat = "#"
 Columns(1).AutoFit
 Columns(2).AutoFit
 Columns(3).AutoFit
End Sub
Function h$(c&)
 h = StrConv(Hex(c), vbLowerCase)
 While 8 > Len(h)
  h = "0" & h
 Wend
End Function
Sub ic()
 Dim c&
 For n& = 0 To 255
  c = n
  For k = 0 To 7
   c = IIf(0& <> (c And 1&), &HEDB88320, 0&) Xor sr(c, 1)
  Next k
  ct(n) = c
 Next n
End Sub
Function sr&(ByVal n&, ByVal s&)
 sr = n
 If 0 < s Then
  sr = (sr And &H7FFFFFFF) \ 2
  If 0 > n Then
   sr = sr Or &H40000000
  End If
  s = s - 1
  sr = sr \ (2 ^ s)
 End If
End Function
Rem sender
Sub cp(ByVal s&, d&, l&)
 If s <= UBound(dt1) Then
  For i& = 1 To min(l, UBound(dt1) + 1 - s)
   td(d) = dt1(s)
   l = l - 1
   s = s + 1
   d = d + 1
  Next i
 End If
 If 0 < l Then
  s = s - UBound(dt1) - 1
  For i& = 1 To min(l, UBound(dt2) + 1 - s)
   td(d) = dt2(s)
   l = l - 1
   s = s + 1
   d = d + 1
  Next i
  While 0 < l
   td(d) = 0
   l = l - 1
   d = d + 1
  Wend
 End If
End Sub
Sub d(x&, y&, i&)
 f 3 * tw + x * cw, 3 * th + y * ch, cw, ch, 0 <> (td(i) And 1)
 td(i) = sr(td(i), 1)
End Sub
Function e&(c&, d&, t&)
 e = c
 Dim s&
 s = (1 + 2 * d) * t - 16 * d * c
 If 0 < s Then
  e = e + s \ (16 * d)
  If (0 <> s Mod (16 * d)) Then e = e + 1
 End If
End Function
Sub em()
 f 0, 0, ww, hh, False
 t 1, 1
 For i& = 1 To dw
  t 1 + 2 * i, 1
 Next i
 t -4, 1
 t -2, 1
 For i& = 1 To dh
  t 1, 1 + 2 * i
 Next i
 t 1, -2
 t -2, -2
End Sub
Sub f(x&, y&, w&, h&, b As Boolean)
 For xx& = x + 1 To x + w
  For yy& = y + 5 To y + h + 4
   Cells(yy, xx).Interior.Color = IIf(b, br, da)
  Next yy
 Next xx
End Sub
Function min&(a&, b&)
 If a <= b Then
  min = a
 Else
  min = b
 End If
End Function
Sub sf()
 Dim fn
 fn = Application.GetOpenFilename
 If False <> fn Then
  Sheets(2).Cells(2, 1).Value = fn
 End If
End Sub
Sub sl(ByVal w&)
 w = GetTickCount() + w
 While w > GetTickCount()
  DoEvents
 Wend
End Sub
Sub st()
 Sheets(2).Activate
 ic
 br = 0
 da = &HFFFFFF
 pw = 10
 ph = 12
 ch = 1
 cw = 1
 de = 300
 dh = 1
 dw = 1
 id = 2000
 th = 2
 tw = 2
 Application.ScreenUpdating = False
 Cells.Font.Name = "Courier New"
 Cells.Font.Size = 8
 Cells.ColumnWidth = 100
 Cells.RowHeight = 100
 With Cells(1, 1)
  Cells.Interior.Color = da
  Cells.ColumnWidth = pw * .ColumnWidth / .Width
  Cells.RowHeight = ph * .RowHeight / .Height
 End With
 ch = e(ch, dh, th)
 cw = e(cw, dw, tw)
 hh = 4 * th + 16 * dh * ch
 ww = 6 * tw + 16 * dw * cw
 ds = 28 * dh * dw - 1
 cx1 = 8 * dw
 cx2 = 16 * dw - 1
 cy1 = 8 * dh
 cy2 = 16 * dh - 1
 ReDim td(32 * dh * dw - 1)
 em
 Application.ScreenUpdating = True
 fr = Cells(1, 1)
 Cells(1, 1).Value = fr
 Dim fn$
 fn = Cells(2, 1)
 Open fn For Binary As #1
  ReDim dt2(LOF(1) - 1)
  Get #1, , dt2
 Close #1
 While 0 < InStr(fn, "\")
  fn = Mid(fn, InStr(fn, "\") + 1)
 Wend
 ReDim dt1(4 + Len(fn))
 dt1(0) = Len(fn)
 For i& = 1 To Len(fn)
  dt1(i) = Asc(Mid(fn, i, 1))
 Next i
 For ii& = 0 To 3
  dt1(Len(fn) + 1 + ii) = sr(UBound(dt2) + 1, 8 * ii) And &HFF
 Next ii
 fr = fr + ds - UBound(dt1) - 1
 c = (dh * &H10000) Or dw
 cl = False
 of = 0
 Dim cc&, dj&, tdc&, tdi&
 Dim mx As Boolean, my As Boolean, sx As Boolean, sy As Boolean
 sl id
 Application.ScreenUpdating = False
 While UBound(dt1) + UBound(dt2) + 2 > of
  td(0) = IIf(cl, &HA5, &H5A)
  For di = 0 To dh * dw - 1
   dj = IIf(0 = di, 1, 0)
   cp of, 32 * di + dj, 28 - dj
   of = of + 28 - dj
   c = cr(c, td, 32 * di, 28)
   cc = c
   tdi = 32 * di + 28
   For tdc = 1 To 4
    td(tdi) = cc And &HFF
    tdi = tdi + 1
    cc = sr(cc, 8)
   Next tdc
  Next di
  cl = Not cl
  If fr <= of Then
   em
   tdc = 8
   tdi = 1
   For yy& = 0 To 16 * dh - 1
    my = cy1 = yy
    sy = (0 = yy) Or (cy2 = yy)
    For xx& = 0 To 16 * dw - 1
     mx = cx1 = xx
     sx = (0 = xx) Or (cx2 = xx)
     If (sx And sy) Or (mx And sy) Or (sx And my) Then
      d xx, yy, 0
     Else
      d xx, yy, tdi
      tdc = tdc - 1
      If 0 >= tdc Then
       tdi = tdi + 1
       tdc = 8
      End If
     End If
    Next xx
   Next yy
   Application.ScreenUpdating = True
   sl de
   Application.ScreenUpdating = False
  End If
 Wend
 Application.ScreenUpdating = True
End Sub
Sub t(x&, y&)
 x = x * tw
 If 0 > x Then x = x + ww
 y = y * th
 If 0 > y Then y = y + hh
 f x, y, tw, th, True
End Sub
