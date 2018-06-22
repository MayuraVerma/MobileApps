package com.kannada.android;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KanKey extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kankey);
        Typeface fontface = Typeface.createFromAsset(getAssets(), "fonts/TUNGA.TTF");        

        TextView appHeader = (TextView) findViewById(R.id.App_header);
        appHeader.setTypeface(fontface,Typeface.BOLD);
        appHeader.setText(UnicodeUtil.unicode2tsc(getResources().getString(R.string.text_Header)));
        
        String urltxt = getResources().getString(R.string.shabdalipi_url);
        String url = new StringBuilder("<a href = \"")
        					.append(urltxt)
        					.append("\">")
        					.append(urltxt)
        					.append("</a>").toString();
        
        
        
        TextView footerText = (TextView) findViewById(R.id.Footer_txt);
        footerText.setTypeface(fontface,Typeface.BOLD);     
        footerText.setTextSize(20);
        footerText.setText(UnicodeUtil.unicode2tsc(getResources().getString(R.string.kankey_txt)));
        footerText.setTextColor(Color.WHITE);
        
        TextView footerUrl = (TextView) findViewById(R.id.Footer_url);
        footerUrl.setMovementMethod(LinkMovementMethod.getInstance());
        footerUrl.setTextColor(Color.WHITE);
        footerUrl.setText(Html.fromHtml(url));
        
        TextView welcomeTxt =(TextView) findViewById(R.id.Welcome_text);
        welcomeTxt.setText(UnicodeUtil.unicode2tsc(getResources().getString(R.string.text_welcome)));
        welcomeTxt.setTypeface(fontface,Typeface.BOLD);
        welcomeTxt.setTextColor(Color.WHITE);
        
        TextView helpHdr =(TextView) findViewById(R.id.Help_header);
        helpHdr.setText(R.string.text_help_header);
        helpHdr.setTypeface(fontface, Typeface.BOLD);
        helpHdr.setTextColor(Color.BLACK);
        
        TextView helpSteps =(TextView) findViewById(R.id.Help_steps);
        helpSteps.setText(R.string.text_help_steps);
        helpSteps.setTextColor(Color.BLACK);
        helpSteps.setTypeface(fontface);
        
        LinearLayout layout = (LinearLayout)findViewById(R.id.LinearLayout01);
        layout.setBackgroundColor(Color.WHITE);
        
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        super.onCreateContextMenu(menu, v, menuInfo);
    }
}
