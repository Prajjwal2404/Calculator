package com.prajjwal.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    String show ;
    float cal;
    int count = 0;
    int cln = 0;
    TextView output;
    @SuppressLint("SetTextI18n")
    public void input(View view){
        Button click = (Button) view;
        if (cln != 0 && click.getId() != R.id.result){
            clean();
        }
        if (count < 24) {
            if (click.getId() != R.id.clear && click.getId() != R.id.backspace && click.getId() != R.id.result && click.getId() != R.id.modulus
                    && click.getId() != R.id.division && click.getId() != R.id.multiplication && click.getId() !=
                    R.id.subtraction && click.getId() != R.id.addition && click.getId() != R.id.power) {
                if (click.getId() != R.id.decimal) {
                    CharSequence c = click.getText();
                    String s = (String) c;
                    if (count == 0) show = s;
                    else show = show + s;
                    ++count;
                    output.setText(show);
                }
                else {
                    if (count == 0) {
                        show = "0.";
                        output.setText(show);
                        count = count + 2;
                    }
                    else {
                        int l = show.length();
                        char c;
                        int j = 0;
                        for (int i = l - 1; i >= 0; i--) {
                            c = show.charAt(i);
                            if (c == '.') j = 1;
                            else if (c == '^' || c == '%' || c == '/' || c == '*' || c == '×' || c == '+' || c == '-') break;
                        }
                        if (j == 0) {
                            char chr = show.charAt(show.length() - 1);
                            if (chr != '^' && chr != '%' && chr != '/' && chr != '×' && chr != '+' && chr != '-') {
                                show = show + ".";
                                ++count;
                            }
                            else {
                                show = show + "0.";
                                count = count + 2;
                            }
                            output.setText(show);
                        }
                    }
                }
            }
            else if (click.getId() == R.id.modulus || click.getId() == R.id.division || click.getId() == R.id.multiplication ||
                    click.getId() == R.id.subtraction || click.getId() == R.id.addition || click.getId() == R.id.power) {
                CharSequence c = click.getText();
                String s = (String) c;
                if (count == 0) {
                    show = "0" + s;
                    count = count + 2;
                }
                else {
                    char chr = show.charAt(show.length() - 1);
                    if (chr != '^' && chr != '%' && chr != '/' && chr != '×' && chr != '+' && chr != '-') {
                        show = show + s;
                        ++count;
                    }
                }
                output.setText(show);
            }
        }
        if (click.getId() == R.id.clear){
            clean();
        }
       else if (click.getId() == R.id.result) {
           if (show != null) {
               String out = calculate(show);
               float w = Float.parseFloat(out);
               int x = (int) w;
               if (x == w) {
                   output.setText(Integer.toString(x));
               }
               else {
                   output.setText(out);
               }
               ++cln;
           }
       }
       else if (click.getId() == R.id.backspace){
            if(show != null) {
                --count;
                show = show.substring(0, show.length() - 1);
                if (show.length() == 0) clean();
                else if (show.charAt(0) == '0' && show.length() == 1) clean();
                else output.setText(show);
            }
       }
    }
    public String calculate(String s){
        char tr = s.charAt(s.length() - 1);
        if (tr == '^' || tr == '%' || tr == '/' || tr == '×' || tr == '+' || tr == '-' || tr == '.') {
            s = s.substring(0, s.length() - 1);
        }
        char c;
        char d;
        int l = s.length();
        int idx = l;
        for (int i = l - 1; i >= 0; i--){
            c = s.charAt(i);
            if (c == '^') {
                for (int j = i - 1; j >= 0; j--){
                    d = s.charAt(j);
                    if(d == '^' || d == '%' || d == '/' || d == '×' || d == '+' || d == '-' || j == 0){
                        String fr;
                        if (j != 0){
                            fr = s.substring(j + 1,i);
                        }
                        else{
                            fr = s.substring(0,i);
                        }
                        String dr;
                        dr = s.substring(i + 1,idx);
                        cal =(float) Math.pow(Double.parseDouble(fr),Double.parseDouble(dr));
                        String n;
                        String m;
                        if (j != 0){
                            n = s.substring(0,j + 1);
                            n = n + cal;
                        }
                        else{
                            n = cal + " ";
                            n = n.trim();
                        }
                        if (idx != l)
                        {
                            m = s.substring(idx,l);
                            n = n + m;
                        }
                        s = n;
                        l = s.length();
                        i = l - 1;
                        idx = l;
                        break;
                    }
                }
            }
            else {
                if (c == '%' || c == '/' || c == '×' || c == '+' || c == '-'){
                    idx = i;
                }
            }
        }
        idx = 0;
        for (int i = 0; i < l; i++){
            c = s.charAt(i);
            if (c == '%' || c == '/' || c == '×') {
                for (int j = i + 1; j < l; j++){
                    d = s.charAt(j);
                    if (d == '%' || d == '/' || d == '×' || d == '+' || d == '-' || j == l-1){
                        String dr;
                        if (j != l-1){
                            dr = s.substring(i + 1,j);
                        }
                        else{
                            dr = s.substring(i + 1,l);
                        }
                        String fr;
                        fr = s.substring(idx,i);
                        if(c == '%') cal = Float.parseFloat(fr) % Float.parseFloat(dr);
                        if(c == '/') cal = Float.parseFloat(fr) / Float.parseFloat(dr);
                        if(c == '×') cal = Float.parseFloat(fr) * Float.parseFloat(dr);
                        String n;
                        String m;
                        if (j != l-1){
                            n = s.substring(j,l);
                            n = cal + n;
                        }
                        else{
                            n = cal + " ";
                            n = n.trim();
                        }
                        if(idx != 0){
                            m = s.substring(0,idx);
                            n = m + n;
                        }
                        s = n;
                        l = s.length();
                        i = 0;
                        idx = 0;
                        break;
                    }
                }
            }
            else {
                if (c == '+' || c == '-'){
                    idx = i + 1;
                }
            }
        }
        idx = 0;
        for (int i = 0; i < l; i++){
            c = s.charAt(i);
            if (c == '+' || c == '-'){
                for (int j = i + 1; j < l; j++){
                    d = s.charAt(j);
                    if (d == '+' || d == '-' || j == l-1){
                        String dr;
                        if (j != l - 1){
                            dr = s.substring(i + 1,j);
                        }
                        else{
                            dr = s.substring(i + 1,l);
                        }
                        String fr;
                        fr = s.substring(idx,i);
                        if (c == '+') cal = Float.parseFloat(fr) + Float.parseFloat(dr);
                        if (c == '-') cal = Float.parseFloat(fr) - Float.parseFloat(dr);
                        String n;
                        if (j != l-1){
                            n = s.substring(j,l);
                            n = cal + n;
                        }
                        else{
                            n = cal + " ";
                            n = n.trim();
                        }
                        s = n;
                        l = s.length();
                        i = 0;
                        idx = 0;
                        break;
                    }
                }
            }
        }
        return s;
    }
    public void clean(){
        show = null;
        cal = 0.0f;
        count = 0;
        cln = 0;
        output.setText("0");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        output = findViewById(R.id.display);
    }
}