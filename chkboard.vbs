Option Compare Database
Function board()
n = vbLf
Open "d:\downloads\CheckersXML.txt" For Output As 1
head = "<?xml version=""1.0"" encoding=""utf-8""?>" & n _
    & "<LinearLayout xmlns:android=""http://schemas.android.com/apk/res/android""" & n _
    & " android:layout_width=""fill_parent""" & n _
    & " android:layout_height=""fill_parent""" & n _
    & " android:background=""#888888""" & n _
    & " android:orientation=""vertical"">" & n _
    & " <com.example.checkers.SquareLayout" & n _
    & "     android:layout_width=""fill_parent""" & n _
    & "     android:layout_height=""fill_parent""" & n _
    & "     android:background=""#ff0000""" & n _
    & "     android:orientation=""vertical"">"
Print #1, head
For k = 0 To 7
    row (k * 8)
Next k
foot = "  </com.example.checkers.SquareLayout>" & n _
    & "</LinearLayout>"
Print #1, foot
Close 1
Debug.Print "done"
End Function
Function row(k)
n = vbLf: j = k / 2
llh = "<LinearLayout" & n & "    android:orientation = ""horizontal""" & n & "    android:layout_width=""fill_parent"" android:layout_height=""0sp"" android:layout_weight=""1"">"
Print #1, llh
For i = 0 + k To 7 + k
If (i + (k / 8)) Mod 2 = 0 Then RedBlk = "#ff0000": j1 = "" Else RedBlk = "#000000": j = j + 1: j1 = j
'Debug.Print i, RedBlk, k, j, j1
iv = "    <ImageView  android:id=""@+id/board" & i & """" & n _
& "    android:gravity=""center"" android:layout_margin=""1sp"" android:background=""" & RedBlk & """" & n _
& "    android:layout_width=""0sp"" android:layout_weight=""1"" android:layout_height=""fill_parent""/>"
Print #1, iv
Next i
Print #1, "</LinearLayout>"
End Function
