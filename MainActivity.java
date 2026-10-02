package com.babareza.calendar;

import android.app.*;
import android.os.*;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.content.*;
import android.net.Uri;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.text.*;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout root; EditText date, plaque, title, weight, notes; ArrayList<String[]> events = new ArrayList<>();
    @Override public void onCreate(Bundle b){ super.onCreate(b); getWindow().setStatusBarColor(Color.rgb(54,82,48)); build(); load(); }
    TextView tv(String s,int size){ TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(Color.DKGRAY); t.setPadding(12,12,12,12); return t; }
    EditText input(String hint){ EditText e=new EditText(this); e.setHint(hint); e.setTextSize(16); e.setPadding(18,8,18,8); return e; }
    void build(){
      ScrollView sv=new ScrollView(this); root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(24,18,24,24); root.setBackgroundColor(Color.rgb(247,242,228)); sv.addView(root);
      TextView head=tv("🐑  تقویم رویدادهای دامداری بابا رضا",24); head.setTextColor(Color.rgb(54,82,48)); head.setGravity(Gravity.CENTER); root.addView(head);
      root.addView(tv("ثبت رویداد و سوابق دام",16));
      date=input("تاریخ شمسی  (مثلاً ۱۴۰۵/۰۷/۱۰)"); plaque=input("شماره پلاک گوسفند"); weight=input("وزن (کیلوگرم)"); title=input("عنوان رویداد"); notes=input("توضیحات"); notes.setMinLines(3);
      root.addView(date);root.addView(plaque);root.addView(weight);root.addView(title);root.addView(notes);
      Button save=btn("💾 ثبت رویداد"); Button pdf=btn("📄 خروجی PDF"); Button csv=btn("📊 خروجی Excel"); Button list=btn("📋 نمایش سوابق");
      root.addView(save);root.addView(pdf);root.addView(csv);root.addView(list); setContentView(sv);
      save.setOnClickListener(v->{ if(plaque.getText().toString().trim().isEmpty()){toast("شماره پلاک را وارد کنید");return;} events.add(new String[]{date.getText().toString(),plaque.getText().toString(),weight.getText().toString(),title.getText().toString(),notes.getText().toString()}); persist(); toast("رویداد ثبت شد"); title.setText(""); notes.setText(""); });
      list.setOnClickListener(v->showList()); pdf.setOnClickListener(v->exportPdf()); csv.setOnClickListener(v->exportCsv());
    }
    Button btn(String s){ Button b=new Button(this); b.setText(s); b.setTextSize(15); b.setAllCaps(false); return b; }
    void showList(){ StringBuilder s=new StringBuilder(); for(String[] e:events) s.append("📅 ").append(e[0]).append(" | پلاک ").append(e[1]).append(" | ").append(e[3]).append("\n").append(e[4]).append("\n\n"); new AlertDialog.Builder(this).setTitle("سوابق دامداری").setMessage(s.length()==0?"هنوز رویدادی ثبت نشده است.":s.toString()).setPositiveButton("بستن",null).show(); }
    void persist(){ getPreferences(0).edit().putString("data",serialize()).apply(); }
    String serialize(){ StringBuilder s=new StringBuilder(); for(String[] e:events){for(String x:e)s.append(x.replace("|"," ")).append("|");s.append("\n");}return s.toString(); }
    void load(){ String d=getPreferences(0).getString("data",""); for(String line:d.split("\\n")){ if(line.trim().isEmpty())continue; String[] a=line.split("\\|",-1); if(a.length>=6)events.add(Arrays.copyOf(a,5)); } }
    void exportCsv(){ try{File f=new File(getExternalFilesDir(null),"BabaReza_Events.csv"); PrintWriter p=new PrintWriter(new OutputStreamWriter(new FileOutputStream(f),"UTF-8")); p.println("تاریخ,پلاک,وزن,عنوان,توضیحات");for(String[]e:events)p.println(String.join(",",e));p.close();share(f,"text/csv");}catch(Exception e){toast("خطا در خروجی Excel");} }
    void exportPdf(){ try{File f=new File(getExternalFilesDir(null),"BabaReza_Events.pdf"); PdfDocument d=new PdfDocument(); Paint p=new Paint();p.setTextSize(13); PdfDocument.Page page=d.startPage(new PdfDocument.PageInfo.Builder(595,842,1).create());Canvas c=page.getCanvas();c.drawText("Baba Reza Livestock - Event Records",30,40,p);int y=70;for(String[]e:events){c.drawText(Arrays.toString(e),30,y,p);y+=24;if(y>810){d.finishPage(page);page=d.startPage(new PdfDocument.PageInfo.Builder(595,842,1).create());c=page.getCanvas();y=40;}}d.finishPage(page);FileOutputStream out=new FileOutputStream(f);d.writeTo(out);out.close();d.close();share(f,"application/pdf");}catch(Exception e){toast("خطا در خروجی PDF");} }
    void share(File f,String type){Intent i=new Intent(Intent.ACTION_SEND);i.setType(type);i.putExtra(Intent.EXTRA_STREAM,Uri.parse("content://com.babareza.calendar.fileprovider/"+f.getName()));toast("فایل ساخته شد: "+f.getName());}
    void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
}
