package com.prajjwal.calculator;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private int count = 0, braces = 0;
    private double cal;
    private boolean minus = true, cln = false;
    private String show;
    private Button img;
    private ImageView imc;
    private EditText output;
    @SuppressLint("SetTextI18n")
    public void input(View view) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(10);
        Button click = (Button) view;
        String s = (String) click.getTag();
        showInput(s);
    }
    private void showInput(String s) {
        if (cln) clean();
        if (count < 60) {
            if (!s.equals("(") && !s.equals(")") && !s.equals("/") && !s.equals("×") && !s.equals("-") && !s.equals("+") && !s.equals("^")) {
                if (!s.equals(".")) {
                    if (count == 0) show = s;
                    else show = show + s;
                    ++count;
                    textshow();
                }
                else {
                    if (count == 0) {
                        show = "0.";
                        textshow();
                        count = count + 2;
                    }
                    else {
                        int l = show.length();
                        char c;
                        boolean j = true;
                        for (int i = l - 1; i >= 0; i--) {
                            c = show.charAt(i);
                            if (c == '.') j = false;
                            else if (c == '^' || c == '(' || c == ')' || c == '/' || c == '×' || c == '+' || c == '-') break;
                        }
                        if (j) {
                            char chr = show.charAt(show.length() - 1);
                            if (chr != '^' && chr != '(' && chr != ')' && chr != '/' && chr != '×' && chr != '+' && chr != '-') {
                                show = show + ".";
                                ++count;
                            }
                            else {
                                show = show + "0.";
                                count = count + 2;
                            }
                            textshow();
                        }
                    }
                }
            }
            else if (s.equals("/") || s.equals("×") || s.equals("-") || s.equals("+") || s.equals("^")) {
                if (count == 0 && !s.equals("-")) {
                    show = "0" + s;
                    count = count + 2;
                }
                else if (count == 0) {
                    show = s;
                    ++count;
                }
                else {
                    char chr = show.charAt(show.length() - 1);
                    if (chr != '^' && chr != '(' && chr != '/' && chr != '×' && chr != '+' && chr != '-') {
                        show = show + s;
                        ++count;
                    }
                    else if (count != 1 && chr != '(' && show.charAt(show.length() - 2) != '(') {
                        show = show.substring(0, show.length() - 1);
                        show = show + s;
                    }
                    else if (chr == '(') {
                        if (s.equals("-")) {
                            show = show + s;
                            ++count;
                        }
                    }
                }
                textshow();
            }
            else if (s.equals("(")) {
                if (count == 0) show = "(";
                else show = show + "(";
                ++braces;
                ++count;
                textshow();
            }
            else {
                if (count != 0) {
                    if (braces != 0) {
                        char chr = show.charAt(show.length() - 1);
                        if (chr != '^' && chr != '(' && chr != '/' && chr != '×' && chr != '+' && chr != '-') {
                            show = show + ")";
                            --braces;
                            ++count;
                            textshow();
                        }
                    }
                }
            }
        }
    }
    public void result(View view) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(10);
        if (show != null) {
            cln = true;
            calculate();
            if (minus) {
                double zb = Double.parseDouble(show);
                long zd = (long) zb;
                if (zd == zb) show = String.valueOf(zd);
                textshow();
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private void calculate() {
        if (show.charAt(show.length() - 1) == '(' || show.charAt(show.length() - 1) == '-') {
            char c;
            int l = show.length();
            for (int i = l - 1; i > 0 ; i--) {
                c = show.charAt(i);
                if (c == '(' || c == '-') {
                    show = show.substring(0, show.length() - 1);
                    if (c == '(') --braces;
                }
                else break;
            }
        }
        if (show.equals("-") || show.equals("(")) {
            minus = false;
            output.setText("Error");
            show = null;
            count = 0;
        }
        else {
            minus = true;
            char tr = show.charAt(show.length() - 1);
            if (tr == '^' || tr == '/' || tr == '×' || tr == '+' || tr == '-' || tr == '.') show = show.substring(0, show.length() - 1);
            while (braces > 0) {
                show = show.concat(")");
                --braces;
            }
            show = find(show);
        }
    }
    private String find(String s) {
        if (s.charAt(0) == '-') s = "0" + s;
        boolean br = false, ex = false, md = false, sa = false;
        int l = s.length();
        char c;
        for (int i = 0; i < l; i++) {
            c = s.charAt(i);
            if (c == '(') br = true;
            else if (c == '^') ex = true;
            else if (c == '/' || c == '×') md = true;
            else if (c == '+' || c == '-') sa = true;
        }
        if (br) s = brc(s);
        if (s.charAt(0) == '-') s = "0" + s;
        l = s.length();
        for (int i = 0; i < l; i++) {
            c = s.charAt(i);
            if (c == '^') ex = true;
            else if (c == '/' || c == '×') md = true;
            else if (c == '+' || c == '-') sa = true;
        }
        if (ex) s = exp(s);
        if (s.charAt(0) == '-') s = "0" + s;
        if (md) s = mdm(s);
        if (s.charAt(0) == '-') s = "0" + s;
        if (sa) s = sad(s);
        return s;
    }
    private String brc(String s) {
        char c;
        char d;
        int idxc = 0, idxo = 0;
        boolean brac = false;
        boolean check = true;
        int l = s.length();
        while (check) {
            check = false;
            for (int i = l - 1; i >= 0; i--) {
                c = s.charAt(i);
                if (c == ')') {
                    brac = true;
                    idxc = i;
                    check = true;
                }
                else if (brac && c == '(') {
                    brac = false;
                    idxo = i;
                }
            }
            if (check) {
                String fr;
                String dr;
                String sol = s.substring(idxo + 1,idxc);
                sol = find(sol);
                if (idxo != 0) {
                    d = s.charAt(idxo - 1);
                    fr = s.substring(0, idxo);
                    if (d != '(' && sol.charAt(0) == '-') sol = "#" + sol.substring(1);
                    if (d != '^' && d != '/' && d != '×' && d != '+' && d != '-' && d != '(') sol = "×" + sol;
                    sol = fr + sol;
                }
                if (idxc != s.length() - 1){
                    d = s.charAt(idxc + 1);
                    dr = s.substring(idxc + 1,l);
                    if (d != '^' && d != '/' && d != '×' && d != '+' && d != '-' && d != ')') sol = sol + "×";
                    sol = sol + dr;
                }
                s = sol;
                l = s.length();
            }
        }
        return s;
    }
    private String exp(String s) {
        char c;
        char d;
        int l = s.length();
        int idx = l;
        for (int i = l - 1; i >= 0; i--) {
            c = s.charAt(i);
            if (c == '^') {
                for (int j = i - 1; j >= 0; j--) {
                    d = s.charAt(j);
                    if (d == '^' || d == '/' || d == '×' || d == '+' || d == '-' || j == 0) {
                        if (d == '-' || d == '+' && j != 0) {
                            if (s.charAt(j - 1) == 'E' || s.charAt(j - 1) == 'e') continue;
                        }
                        String fr;
                        if (d == '-') {
                            fr = s.substring(j,i);
                        }
                        else if (j != 0) {
                            fr = s.substring(j + 1, i);
                        }
                        else {
                            fr = s.substring(0, i);
                        }
                        if (fr.charAt(0) == '#') fr = "-" + fr.substring(1);
                        String dr;
                        dr = s.substring(i + 1, idx);
                        if (dr.charAt(0) == '#') dr = "-" + dr.substring(1);
                        cal = Math.pow(Double.parseDouble(fr), Double.parseDouble(dr));
                        String n;
                        String m;
                        if (j != 0) {
                            boolean neg = false;
                            if (cal < 0) {
                                neg = true;
                                cal = cal * -1;
                            }
                            if (!neg && d == '-') n = s.substring(0, j) + "+";
                            else n = s.substring(0, j + 1);
                            if (neg && d != '-') n = n + "#" + cal;
                            else n = n + cal;
                        }
                        else {
                            n = cal + " ";
                            n = n.trim();
                        }
                        if (idx != l) {
                            m = s.substring(idx, l);
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
                if (c == '/' || c == '×' || c == '+' || c == '-') {
                    if (c == '-' || c == '+' && i != 0) {
                        if (s.charAt(i - 1) != 'E' && s.charAt(i - 1) != 'e') idx = i;
                    }
                    else if (c == '/' || c == '×') idx = i;
                }
            }
        }
        return s;
    }
    private String mdm(String s) {
        char c;
        char d;
        int l = s.length();
        int idx = 0;
        for (int i = 0; i < l; i++) {
            c = s.charAt(i);
            if (c == '/' || c == '×') {
                for (int j = i + 1; j < l; j++) {
                    d = s.charAt(j);
                    if (d == '/' || d == '×' || d == '+' || d == '-' || j == l - 1) {
                        if (d == '-' || d == '+') {
                            if (s.charAt(j - 1) == 'E' || s.charAt(j - 1) == 'e') continue;
                        }
                        String dr;
                        if (j != l - 1) {
                            dr = s.substring(i + 1, j);
                        }
                        else {
                            dr = s.substring(i + 1, l);
                        }
                        if (dr.charAt(0) == '#') dr = "-" + dr.substring(1);
                        String fr;
                        fr = s.substring(idx, i);
                        if (fr.charAt(0) == '#') fr = "-" + fr.substring(1);
                        if (c == '/') cal = Double.parseDouble(fr) / Double.parseDouble(dr);
                        if (c == '×') cal = Double.parseDouble(fr) * Double.parseDouble(dr);
                        String n;
                        String m;
                        if (j != l - 1) {
                            n = s.substring(j, l);
                            n = cal + n;
                        }
                        else {
                            n = cal + " ";
                            n = n.trim();
                        }
                        if (idx != 0) {
                            if (n.charAt(0) == '-') n = "#" + n.substring(1);
                            m = s.substring(0, idx);
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
            else if (c == '+' || c == '-') {
                if (s.charAt(i - 1) != 'E' && s.charAt(i - 1) != 'e') idx = i + 1;
            }
        }
        return s;
    }
    private String sad(String s) {
        char c;
        char d;
        int l = s.length();
        int idx = 0;
        for (int i = 0; i < l; i++) {
            c = s.charAt(i);
            if (c == '+' || c == '-') {
                if (s.charAt(i - 1) == 'E' || s.charAt(i - 1) == 'e') continue;
                for (int j = i + 1; j < l; j++) {
                    d = s.charAt(j);
                    if (d == '+' || d == '-' || j == l - 1) {
                        if (d == '-' || d == '+') {
                            if (s.charAt(j - 1) == 'E' || s.charAt(j - 1) == 'e') continue;
                        }
                        String dr;
                        if (j != l - 1) {
                            dr = s.substring(i + 1, j);
                        }
                        else {
                            dr = s.substring(i + 1, l);
                        }
                        if (dr.charAt(0) == '#') dr = "-" + dr.substring(1);
                        String fr;
                        fr = s.substring(idx, i);
                        if (fr.charAt(0) == '#') fr = "-" + fr.substring(1);
                        if (c == '+') cal = Double.parseDouble(fr) + Double.parseDouble(dr);
                        if (c == '-') cal = Double.parseDouble(fr) - Double.parseDouble(dr);
                        String n;
                        if (j != l - 1) {
                            n = s.substring(j, l);
                            n = cal + n;
                        }
                        else {
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
    private void textshow() {
        int l = show.length();
        if (l > 45) {
            output.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);
        }
        else if (l > 27) {
            output.setTextSize(TypedValue.COMPLEX_UNIT_SP,45);
        }
        else if (l > 9) {
            output.setTextSize(TypedValue.COMPLEX_UNIT_SP,50);
        }
        else {
            output.setTextSize(TypedValue.COMPLEX_UNIT_SP,60);
        }
        output.setText(show);
    }
    public void backspace(View view) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(10);
        if (cln) clean();
        if(show != null) {
            if (show.charAt(show.length() - 1) == ')') ++braces;
            else if (show.charAt(show.length() - 1) == '(') --braces;
            --count;
            show = show.substring(0, show.length() - 1);
            if (show.length() == 0) clean();
            else if (show.charAt(0) == '0' && show.length() == 1) clean();
            else textshow();
        }
    }
    private void clear() {
        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(13);
                int cx = output.getWidth();
                int cy = output.getHeight();
                float fr = (float) Math.hypot(cx,cy);
                Animator anim = ViewAnimationUtils.createCircularReveal(imc,100,cy,100f,fr);
                imc.setVisibility(View.VISIBLE);
                anim.start();
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                clean();
                                imc.setVisibility(View.INVISIBLE);
                            }
                        }, 600
                );
                return true;
            }
        });
    }
    private void clean() {
        show = null;
        cal = 0.0f;
        count = 0;
        minus = true;
        braces = 0;
        cln = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                output.setText("0");
                output.setTextSize(TypedValue.COMPLEX_UNIT_SP,60);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        output = findViewById(R.id.display);
        imc = findViewById(R.id.topcover);
        img = findViewById(R.id.clear);
        clear();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_NUMPAD_0:
                if (event.getModifiers() == 0)
                showInput("0");
                return true;
            case KeyEvent.KEYCODE_0:
                if (event.isShiftPressed()) {
                    showInput(")");
                }
                else if (event.getModifiers() == 0) {
                    showInput("0");
                }
                return true;
            case KeyEvent.KEYCODE_NUMPAD_1:
            case KeyEvent.KEYCODE_1:
                if (event.getModifiers() == 0)
                showInput("1");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_2:
            case KeyEvent.KEYCODE_2:
                if (event.getModifiers() == 0)
                showInput("2");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_3:
            case KeyEvent.KEYCODE_3:
                if (event.getModifiers() == 0)
                showInput("3");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_4:
            case KeyEvent.KEYCODE_4:
                if (event.getModifiers() == 0)
                showInput("4");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_5:
            case KeyEvent.KEYCODE_5:
                if (event.getModifiers() == 0)
                showInput("5");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_6:
                if (event.getModifiers() == 0)
                showInput("6");
                return true;
            case KeyEvent.KEYCODE_6:
                if (event.isShiftPressed()) {
                    showInput("^");
                }
                else if (event.getModifiers() == 0) {
                    showInput("6");
                }
                return true;
            case KeyEvent.KEYCODE_NUMPAD_7:
            case KeyEvent.KEYCODE_7:
                if (event.getModifiers() == 0)
                showInput("7");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_8:
                if (event.getModifiers() == 0)
                showInput("8");
                return true;
            case KeyEvent.KEYCODE_8:
                if (event.isShiftPressed()) {
                    showInput("×");
                }
                else if (event.getModifiers() == 0) {
                    showInput("8");
                }
                return true;
            case KeyEvent.KEYCODE_NUMPAD_9:
                if (event.getModifiers() == 0)
                showInput("9");
                return true;
            case KeyEvent.KEYCODE_9:
                if (event.isShiftPressed()) {
                    showInput("(");
                }
                else if (event.getModifiers() == 0) {
                    showInput("9");
                }
                return true;
            case KeyEvent.KEYCODE_NUMPAD_DOT:
            case KeyEvent.KEYCODE_PERIOD:
                if (event.getModifiers() == 0)
                showInput(".");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_ADD:
            case KeyEvent.KEYCODE_PLUS:
                if (event.getModifiers() == 0)
                showInput("+");
                return true;
            case KeyEvent.KEYCODE_EQUALS:
                if (event.isShiftPressed()){
                    showInput("+");
                }
                return true;
            case KeyEvent.KEYCODE_NUMPAD_SUBTRACT:
            case KeyEvent.KEYCODE_MINUS:
                if (event.getModifiers() == 0)
                showInput("-");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_MULTIPLY:
            case KeyEvent.KEYCODE_STAR:
                if (event.getModifiers() == 0)
                showInput("×");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_DIVIDE:
            case KeyEvent.KEYCODE_SLASH:
                if (event.getModifiers() == 0)
                showInput("/");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (event.getModifiers() == 0) {
                    if (show != null) {
                        cln = true;
                        calculate();
                        if (minus) {
                            double zb = Double.parseDouble(show);
                            long zd = (long) zb;
                            if (zd == zb) show = String.valueOf(zd);
                            textshow();
                        }
                    }
                }
                return true;
            case KeyEvent.KEYCODE_DEL:
                if (event.getModifiers() == 0) {
                    if (cln) clean();
                    if(show != null) {
                        if (show.charAt(show.length() - 1) == ')') ++braces;
                        else if (show.charAt(show.length() - 1) == '(') --braces;
                        --count;
                        show = show.substring(0, show.length() - 1);
                        if (show.length() == 0) clean();
                        else if (show.charAt(0) == '0' && show.length() == 1) clean();
                        else textshow();
                    }
                }
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

}