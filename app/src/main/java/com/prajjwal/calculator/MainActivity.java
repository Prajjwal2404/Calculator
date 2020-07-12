package com.prajjwal.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    String show;
    float cal;
    int count = 0;
    TextView output;
    boolean minus = true;
    @SuppressLint("SetTextI18n")
    public void input(View view) {
        Button click = (Button) view;
        if (count < 28) {
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
                        boolean j  = true;
                        for (int i = l - 1; i >= 0; i--) {
                            c = show.charAt(i);
                            if (c == '.') j = false;
                            else if (c == '^' || c == '%' || c == '/' || c == '*' || c == '×' || c == '+' || c == '-') break;
                        }
                        if (j) {
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
                if (count == 0 && click.getId() != R.id.subtraction) {
                    show = "0" + s;
                    count = count + 2;
                }
                else if (count == 0 && click.getId() == R.id.subtraction) {
                    show = s;
                    ++count;
                }
                else if (count != 0) {
                    char chr = show.charAt(show.length() - 1);
                    if (chr != '^' && chr != '%' && chr != '/' && chr != '×' && chr != '+' && chr != '-') {
                        show = show + s;
                        ++count;
                    }
                    else if (count != 1) {
                        show = show.substring(0,show.length() - 1);
                        show = show + s;
                    }
                }
                output.setText(show);
            }
        }
        if (click.getId() == R.id.clear) {
            clean();
        }
       else if (click.getId() == R.id.result) {
           if (show != null) {
               calculate();
               if (minus) {
                   float w = Float.parseFloat(show);
                   int x = (int) w;
                   if (x == w) show = String.valueOf(x);
                   output.setText(show);
                   if (x != 0) count = show.length();
                   else count = 0;
               }
           }
       }
       else if (click.getId() == R.id.backspace) {
            if(show != null) {
                --count;
                show = show.substring(0, show.length() - 1);
                if (show.length() == 0) clean();
                else if (show.charAt(0) == '0' && show.length() == 1) clean();
                else output.setText(show);
            }
       }
    }
    public void calculate() {
        if (show.length() == 1 && show.charAt(0) == '-') minus = false;
        else {
            minus = true;
            char tr = show.charAt(show.length() - 1);
            if (tr == '^' || tr == '%' || tr == '/' || tr == '×' || tr == '+' || tr == '-' || tr == '.') show = show.substring(0, show.length() - 1);
            if (show.charAt(0) == '-') show = "0" + show;
            boolean ex = false, md = false, sa = false;
            int l = show.length();
            char c;
            for (int i = 0; i < l; i++) {
                c = show.charAt(i);
                if (c == '^') ex = true;
                else if (c == '%' || c == '/' || c == '×') md = true;
                else if (c == '+' || c == '-') sa = true;
            }
            if (ex) exp();
            if (md) mdm();
            if (sa) sad();
        }
    }
    public void exp() {
        char c;
        char d;
        int l = show.length();
        int idx = l;
        for (int i = l - 1; i >= 0; i--) {
            c = show.charAt(i);
            if (c == '^') {
                for (int j = i - 1; j >= 0; j--) {
                    d = show.charAt(j);
                    if (d == '^' || d == '%' || d == '/' || d == '×' || d == '+' || d == '-' || j == 0) {
                        String fr;
                        if (j != 0) {
                            fr = show.substring(j + 1, i);
                        } else {
                            fr = show.substring(0, i);
                        }
                        String dr;
                        dr = show.substring(i + 1, idx);
                        cal = (float) Math.pow(Double.parseDouble(fr), Double.parseDouble(dr));
                        String n;
                        String m;
                        if (j != 0) {
                            n = show.substring(0, j + 1);
                            n = n + cal;
                        } else {
                            n = cal + " ";
                            n = n.trim();
                        }
                        if (idx != l) {
                            m = show.substring(idx, l);
                            n = n + m;
                        }
                        show = n;
                        l = show.length();
                        i = l - 1;
                        idx = l;
                        break;
                    }
                }
            } else {
                if (c == '%' || c == '/' || c == '×' || c == '+' || c == '-') {
                    idx = i;
                }
            }
        }
    }
    public void mdm() {
        char c;
        char d;
        int l = show.length();
        int idx = 0;
        for (int i = 0; i < l; i++) {
            c = show.charAt(i);
            if (c == '%' || c == '/' || c == '×') {
                for (int j = i + 1; j < l; j++) {
                    d = show.charAt(j);
                    if (d == '%' || d == '/' || d == '×' || d == '+' || d == '-' || j == l - 1) {
                        String dr;
                        if (j != l - 1) {
                            dr = show.substring(i + 1, j);
                        } else {
                            dr = show.substring(i + 1, l);
                        }
                        String fr;
                        fr = show.substring(idx, i);
                        if (c == '%') cal = Float.parseFloat(fr) % Float.parseFloat(dr);
                        if (c == '/') cal = Float.parseFloat(fr) / Float.parseFloat(dr);
                        if (c == '×') cal = Float.parseFloat(fr) * Float.parseFloat(dr);
                        String n;
                        String m;
                        if (j != l - 1) {
                            n = show.substring(j, l);
                            n = cal + n;
                        } else {
                            n = cal + " ";
                            n = n.trim();
                        }
                        if (idx != 0) {
                            m = show.substring(0, idx);
                            n = m + n;
                        }
                        show = n;
                        l = show.length();
                        i = 0;
                        idx = 0;
                        break;
                    }
                }
            } else {
                if (c == '+' || c == '-') {
                    idx = i + 1;
                }
            }
        }
    }
    public void sad() {
        char c;
        char d;
        int l = show.length();
        int idx = 0;
        for (int i = 0; i < l; i++) {
            c = show.charAt(i);
            if (c == '+' || c == '-') {
                for (int j = i + 1; j < l; j++) {
                    d = show.charAt(j);
                    if (d == '+' || d == '-' || j == l - 1) {
                        String dr;
                        if (j != l - 1) {
                            dr = show.substring(i + 1, j);
                        } else {
                            dr = show.substring(i + 1, l);
                        }
                        String fr;
                        fr = show.substring(idx, i);
                        if (c == '+') cal = Float.parseFloat(fr) + Float.parseFloat(dr);
                        if (c == '-') cal = Float.parseFloat(fr) - Float.parseFloat(dr);
                        String n;
                        if (j != l - 1) {
                            n = show.substring(j, l);
                            n = cal + n;
                        } else {
                            n = cal + " ";
                            n = n.trim();
                        }
                        show = n;
                        l = show.length();
                        i = 0;
                        idx = 0;
                        break;
                    }
                }
            }
        }
    }
    public void clean() {
        show = null;
        cal = 0.0f;
        count = 0;
        minus = true;
        output.setText("0");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        output = findViewById(R.id.display);
    }
}