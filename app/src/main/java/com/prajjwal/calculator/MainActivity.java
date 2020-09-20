package com.prajjwal.calculator;

import android.animation.Animator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String HISTORY_ITEMS_KEY = "historyItems";
    private static final String RADIAN_KEY = "radDeg";
    private static final String HISTORY_METHODS_KEY = "historyMethods";
    private int count = 0, braces = 0, pos;
    private double cal;
    private static boolean cln = false, hyp = true, inv = true, rd = true, canChange = true, shouldPreResult = false, shouldClean = false;
    private Button hyperbolic, inverse, rad, sin, cos, tan, cot, sec, csc;
    private String show;
    private ImageView imc, historyImg;
    private TextView textHistory, pResult;
    private EditText output;
    private ScrollView scrollTxt;
    private ViewPager viewPager;
    private HistoryRecycler adapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson = new Gson();
    private ArrayList<HistoryItems> historyItems = new ArrayList<>();
    private ArrayList<ArrayList<SciMethod>> histSciMethod = new ArrayList<>();
    private ArrayList<SciMethod> sciMethods = new ArrayList<>();
    private ArrayList<SciMethod> storeMethods = new ArrayList<>();
    public void input(View view) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(10);
        Button click = (Button) view;
        String s = (String) click.getTag();
        if (cln) clean();
        if (shouldClean) shouldClean = false;
        restore_sciMethod();
        pos = output.getSelectionStart();
        if (isOutside_sciMethods(pos)) showInput(s);
    }
    private void showInput(String s) {
        if (count < 250) {
            if (!s.equals("(") && !s.equals(")") && !s.equals("/") && !s.equals("%") && !s.equals("×") && !s.equals("-") && !s.equals("+")
                    && !s.equals("^") && !s.equals("!") && !s.equals("e") && !s.equals("π") && !s.equals("^2")) {
                if (!s.equals(".")) {
                    if (count == 0 && pos == 1) show = s;
                    else if (count == 0) {
                        show = s + "0";
                        ++count;
                    }
                    else {
                        show = show.substring(0, pos) + s + show.substring(pos);
                        update_sciMethods(pos - 1, 1);
                    }
                    ++count;
                    textShow();
                }
                else {
                    if (count == 0 && pos == 1) {
                        show = "0.";
                        textShow();
                        count = count + 2;
                    }
                    else {
                        int l = pos;
                        char c;
                        boolean j = true;
                        if (pos == 0) j = false;
                        for (int i = l - 1; i >= 0; i--) {
                            c = show.charAt(i);
                            if (c == '.') j = false;
                            else if (c == '^' || c == '(' || c == ')' || c == '/' || c == '×' || c == '+' || c == '-') break;
                        }
                        if (pos != 0) {
                            for (int i = l; i < show.length(); i++) {
                                c = show.charAt(i);
                                if (c == '.') j = false;
                                else if (c == '^' || c == '(' || c == ')' || c == '/' || c == '×' || c == '+' || c == '-') break;
                            }
                        }
                        if (j) {
                            char chr = show.charAt(pos - 1);
                            if (chr != '^' && chr != '(' && chr != ')' && chr != '/' && chr != '×' && chr != '+' && chr != '-') {
                                show = show.substring(0, pos) + "." + show.substring(pos);
                                ++count;
                                update_sciMethods(pos - 1, 1);
                            }
                            else {
                                show = show.substring(0, pos) + "0." + show.substring(pos);
                                count = count + 2;
                                update_sciMethods(pos - 1, 2);
                            }
                            textShow();
                        }
                    }
                }
            }
            else if (s.equals("/") || s.equals("%") || s.equals("×") || s.equals("-") || s.equals("+") || s.equals("^")) {
                if (count == 0 && !s.equals("-") && pos == 1) {
                    show = "0" + s;
                    count = count + 2;
                }
                else if (count == 0 && pos == 1) {
                    show = s;
                    ++count;
                }
                else if (count == 0 && s.equals("-")) {
                    show = "-0";
                    count = count + 2;
                }
                else if (count != 0 && pos == 0 && s.equals("-")) {
                    show = s + show;
                    ++count;
                    update_sciMethods(-1, 1);
                }
                else if (count != 0 && pos != 0) {
                    char chr, dhr;
                    chr = show.charAt(pos - 1);
                    if (pos < show.length()) dhr = show.charAt(pos);
                    else dhr = '0';
                    if (chr != '^' && chr != '/' && chr != '%' && chr != '×' && chr != '+' && chr != '-'
                        && dhr != '^' && dhr != '/' && dhr != '%' && dhr != '×' && dhr != '+' && dhr != '-') {
                        show = show.substring(0, pos) + s + show.substring(pos);
                        ++count;
                        update_sciMethods(pos - 1, 1);
                    }
                    else if (pos != 1 && dhr != '^' && dhr != '/' && dhr != '%' && dhr != '×' && dhr != '+' && dhr != '-') {
                        show = show.substring(0, pos - 1) + show.substring(pos);
                        show = show.substring(0, pos - 1) + s + show.substring(pos - 1);
                    }
                }
                if (show != null) textShow();
            }
            else if (s.equals("(")) {
                if (count == 0 && pos == 1) show = "(";
                else if (count == 0) {
                    show = "(0";
                    ++count;
                }
                else {
                    show = show.substring(0, pos) + s + show.substring(pos);
                    update_sciMethods(pos - 1, 1);
                }
                ++braces;
                ++count;
                textShow();
            }
            else if (s.equals(")")) {
                if (count != 0) {
                    show = show.substring(0, pos) + s + show.substring(pos);
                    --braces;
                    ++count;
                    update_sciMethods(pos - 1, 1);
                    textShow();
                }
            }
            else if (s.equals("!")) {
                if (count == 0 && pos == 1) {
                    show = "0!";
                    count = count + 2;
                }
                else if (count != 0 && pos != 0) {
                    show = show.substring(0, pos) + "!" + show.substring(pos);
                    ++count;
                    update_sciMethods(pos - 1, 1);
                }
                textShow();
            }
            else if (s.equals("e")) {
                if (count == 0) {
                    show = "e";
                    ++count;
                }
                else {
                    char chr, dhr;
                    if (pos > 0) chr = show.charAt(pos - 1);
                    else chr = '+';
                    if (pos < show.length()) dhr = show.charAt(pos);
                    else dhr = '+';
                    if (chr == '/' || chr == '%' || chr == '×' || chr == '+' || chr == '-' || chr == '^' || chr == '(' || chr == ')') {
                        if (dhr == '/' || dhr == '%' || dhr == '×' || dhr == '+' || dhr == '-' || dhr == '^' || dhr == '!' || dhr == '(' || dhr == ')') {
                            show = show.substring(0, pos) + "e" + show.substring(pos);
                            ++count;
                            update_sciMethods(pos - 1, 1);
                        }
                    }
                }
                textShow();
            }
            else if (s.equals("π")) {
                if (count == 0) {
                    show = "π";
                    ++count;
                }
                else {
                    char chr, dhr;
                    if (pos > 0) chr = show.charAt(pos - 1);
                    else chr = '+';
                    if (pos < show.length()) dhr = show.charAt(pos);
                    else dhr = '+';
                    if (chr == '/' || chr == '%' || chr == '×' || chr == '+' || chr == '-' || chr == '^' || chr == '(' || chr == ')') {
                        if (dhr == '/' || dhr == '%' || dhr == '×' || dhr == '+' || dhr == '-' || dhr == '^' || dhr == '!' || dhr == '(' || dhr == ')') {
                            show = show.substring(0, pos) + "π" + show.substring(pos);
                            ++count;
                            update_sciMethods(pos - 1, 1);
                        }
                    }
                }
                textShow();
            }
            else {
                if (count == 0 && pos == 1) {
                    show = "0^2";
                    count = count + 3;
                    textShow();
                }
                else if (count != 0 && pos != 0) {
                    char chr, dhr;
                    chr = show.charAt(pos - 1);
                    if (pos < show.length()) dhr = show.charAt(pos);
                    else dhr = '+';
                    if (chr == '1' || chr == '2' || chr == '3' || chr == '4' || chr == '5' || chr == '6'
                            || chr == '7' || chr == '8' || chr == '9' || chr == '0' || chr == '.') {
                        if (dhr == '^' || dhr == '/' || dhr == '%' || dhr == '×' || dhr == '+' || dhr == '-') {
                            show = show.substring(0, pos) + s + show.substring(pos);
                            count = count + 2;
                            update_sciMethods(pos - 1, 2);
                            textShow();
                        }
                    }
                }
            }
            if (show != null) preResult();
        }
    }
    public void scientificInput(View view) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(10);
        Button click = (Button) view;
        String s = (String) click.getTag();
        if (cln) clean();
        if (shouldClean) shouldClean = false;
        restore_sciMethod();
        pos = output.getSelectionStart();
        if (count < 250 && isOutside_sciMethods(pos)) {
            if (count == 0 && pos == 1) {
                show = s;
                count = count + s.length();
                ++braces;
                sciMethods.add(new SciMethod(0, s.length() - 1, s.substring(0, s.length() - 1), rd));
            }
            else if (count == 0 && pos == 0) {
                show = s + "0";
                count = count + s.length() + 1;
                ++braces;
                sciMethods.add(new SciMethod(0, s.length() - 1, s.substring(0, s.length() - 1), rd));
            }
            else if (count != 0) {
                show = show.substring(0, pos) + s + show.substring(pos);
                count = count + s.length();
                ++braces;
                update_sciMethods(pos - 1, s.length());
                int i = insert_sciMethods(pos - 1);
                if (i != sciMethods.size()) {
                    sciMethods.add(i, new SciMethod(pos,  pos + s.length() - 1, s.substring(0, s.length() - 1), rd));
                }
                else sciMethods.add(new SciMethod(pos, pos + s.length() - 1, s.substring(0, s.length() - 1), rd));
            }
            textShow();
            if (show != null) preResult();
        }
    }
    private void update_sciMethods(int index, int length) {
        int stidx, edidx;
        for (int i = 0; i < sciMethods.size(); i++) {
            if (sciMethods.get(i).getStartIdx() > index) {
                stidx = sciMethods.get(i).getStartIdx();
                edidx = sciMethods.get(i).getEndIdx();
                stidx += length;
                edidx += length;
                sciMethods.get(i).setStartIdx(stidx);
                sciMethods.get(i).setEndIdx(edidx);
            }
        }
    }
    private void removeUpdate_sciMethods(int index, int length) {
        int stidx, edidx;
        for (int i = 0; i < sciMethods.size(); i++) {
            if (sciMethods.get(i).getStartIdx() > index) {
                stidx = sciMethods.get(i).getStartIdx();
                edidx = sciMethods.get(i).getEndIdx();
                stidx -= length;
                edidx -= length;
                sciMethods.get(i).setStartIdx(stidx);
                sciMethods.get(i).setEndIdx(edidx);
            }
        }
    }
    private int insert_sciMethods(int index) {
        for (int i = 0; i < sciMethods.size(); i++) {
            if (sciMethods.get(i).getStartIdx() > index) {
                return i;
            }
        }
        return sciMethods.size();
    }
    private boolean isOutside_sciMethods(int index) {
        int stidx, edidx;
        for (int i = 0; i < sciMethods.size(); i++) {
            stidx = sciMethods.get(i).getStartIdx();
            edidx = sciMethods.get(i).getEndIdx();
            if (index > stidx && index <= edidx) return false;
        }
        return true;
    }
    private void restore_sciMethod() {
        sciMethods = null;
        sciMethods = new ArrayList<>();
        int stidx, edidx;
        String fuc;
        boolean red;
        for (int i = 0; i < storeMethods.size(); i++) {
            stidx = storeMethods.get(i).getStartIdx();
            edidx = storeMethods.get(i).getEndIdx();
            fuc = storeMethods.get(i).getFunction();
            red = storeMethods.get(i).isRad();
            sciMethods.add(new SciMethod(stidx, edidx, fuc, red));
        }
    }
    public void result(View view) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(10);
        if (show != null && !cln && !shouldClean) {
            restore_sciMethod();
            shouldClean = true;
            String exp = show;
            storeMethods = null;
            storeMethods = new ArrayList<>();
            int stidx, edidx;
            String fuc;
            boolean red;
            for (int i = 0; i < sciMethods.size(); i++) {
                stidx = sciMethods.get(i).getStartIdx();
                edidx = sciMethods.get(i).getEndIdx();
                fuc = sciMethods.get(i).getFunction();
                red = sciMethods.get(i).isRad();
                storeMethods.add(new SciMethod(stidx, edidx, fuc, red));
            }
            show = calculate(show, braces);
            braces = 0;
            if (!show.equals("Math Error") && !show.equals("Error") && !show.equals("Domain Error") && !show.equals("Not a number")) {
                try {
                    double zb = Double.parseDouble(show);
                    long zd = (long) zb;
                    if (zd == zb) show = String.valueOf(zd);
                }
                catch (NumberFormatException e) {
                    Log.d(TAG, e + "result");
                    show = "Error";
                    cln = true;
                }
            }
            else cln = true;
            if (show.equalsIgnoreCase("Infinity") || show.equalsIgnoreCase("-Infinity")) cln = true;
            String rst;
            if (show.equalsIgnoreCase("Infinity")) rst = "Value too large";
            else rst = show;
            pResult.setText("");
            pResult.setVisibility(View.GONE);
            textShow();
            if (historyItems.size() > 100) {
                historyItems.remove(historyItems.size() - 1);
                histSciMethod.remove(histSciMethod.size() - 1);
            }
            historyItems.add(0, new HistoryItems(exp, rst));
            histSciMethod.add(0, storeMethods);
            if (historyItems.size() == 0) {
                historyImg.setVisibility(View.VISIBLE);
                textHistory.setVisibility(View.VISIBLE);
            } else {
                historyImg.setVisibility(View.GONE);
                textHistory.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
            editor = sharedPreferences.edit();
            editor.remove(HISTORY_ITEMS_KEY);
            editor.putString(HISTORY_ITEMS_KEY, gson.toJson(historyItems));
            editor.remove(HISTORY_METHODS_KEY);
            editor.putString(HISTORY_METHODS_KEY, gson.toJson(histSciMethod));
            editor.apply();
            count = show.length();
            shouldPreResult = false;
            sciMethods = null;
            sciMethods = new ArrayList<>();
            storeMethods = null;
            storeMethods = new ArrayList<>();
        }
    }
    private void preResult() {
        storeMethods = null;
        storeMethods = new ArrayList<>();
        int stidx, edidx;
        String fuc;
        boolean red;
        for (int i = 0; i < sciMethods.size(); i++) {
            stidx = sciMethods.get(i).getStartIdx();
            edidx = sciMethods.get(i).getEndIdx();
            fuc = sciMethods.get(i).getFunction();
            red = sciMethods.get(i).isRad();
            storeMethods.add(new SciMethod(stidx, edidx, fuc, red));
        }
        String pShow = calculate(show, braces);
        if (shouldPreResult) {
            pResult.setVisibility(View.VISIBLE);
            if (!pShow.equals("Math Error") && !pShow.equals("Error") && !pShow.equals("Domain Error") && !pShow.equals("Not a number")) {
                try {
                    double zb = Double.parseDouble(pShow);
                    long zd = (long) zb;
                    if (zd == zb) pShow = String.valueOf(zd);
                    if (pShow.equalsIgnoreCase("Infinity")) pShow = "Value too large";
                    pResult.setText(pShow);
                } catch (NumberFormatException e) {
                    pResult.setText("");
                }
            } else pResult.setText("");
        }
        else {
            pResult.setText("");
            pResult.setVisibility(View.GONE);
        }
    }
    private String calculate(String s, int setBraces) {
        char tr = s.charAt(s.length() - 1);
        if (tr == '^' || tr == '/' || tr == '%' || tr == '×' || tr == '+' || tr == '-' || tr == '.')
            s = s.substring(0, s.length() - 1);
        int l = s.length();
        char c, chr = '+', dhr = '+';
        String fr;
        for (int i = 0; i < l; i++) {
            c = s.charAt(i);
            if (c == 'e' || c == 'π') {
                if (i > 0) chr = s.charAt(i - 1);
                if (i < l - 1) dhr = s.charAt(i + 1);
                if (chr == '/' || chr == '%' || chr == '×' || chr == '+' || chr == '-' || chr == '^' || chr == '(' || chr == ')') {
                    if (dhr == '/' || dhr == '%' || dhr == '×' || dhr == '+' || dhr == '-' || dhr == '^' || dhr == '!' || dhr == '(' || dhr == ')') {
                        if (c == 'e') {
                            fr = s.substring(0, i) + "2.71828183";
                            if (i < l - 1) fr = fr + s.substring(i + 1, l);
                            s = fr;
                            update_sciMethods(i, 9);
                        }
                        if (c == 'π') {
                            fr = s.substring(0, i) + "3.14159265";
                            if (i < l - 1) fr = fr + s.substring(i + 1, l);
                            s = fr;
                            update_sciMethods(i, 9);
                        }
                    }
                    else {
                        return "Error";
                    }
                }
                else if (c == 'e' && chr == 's') {
                    chr = '+';
                    dhr = '+';
                    continue;
                }
                else if (c == 'π') {
                    return "Error";
                }
                else {
                    return "Error";
                }
                chr = '+';
                dhr = '+';
                l = s.length();
                i = -1;
            }
            if (c == '!') {
                if (i < l - 1) dhr = s.charAt(i + 1);
                if (dhr != '!' && dhr != '^' && dhr != '/' && dhr != '%' && dhr != '×' && dhr != '+' && dhr != '-' && dhr != ')' && dhr != '(') {
                    return "Error";
                }
                dhr = '+';
            }
        }
        if (setBraces > 0)
            while (setBraces > 0) {
                s = s.concat(")");
                --setBraces;
            }
        else {
            while (setBraces < 0) {
                s = "(".concat(s);
                ++setBraces;
                update_sciMethods(-1, 1);
            }
        }
        if (s.length() != 0 && s.charAt(0) == '-') {
            s = "0" + s;
            update_sciMethods(0, 1);
        }
        s = find(s);
        return s;
    }
    private String find(String s) {
        shouldPreResult = false;
        if (s.length() == 0) return "Error";
        if (s.charAt(0) == '-') {
            s = "0" + s;
        }
        boolean br = false, fc = false, ex = false, md = false, sa = false;
        int l = s.length();
        char c;
        for (int i = 0; i < l; i++) {
            c = s.charAt(i);
            if (c == '(' || c == ')') br = true;
        }
        if (br) s = brc(s);
        if (s.charAt(0) == '-') {
            s = "0" + s;
        }
        l = s.length();
        for (int i = 0; i < l; i++) {
            c = s.charAt(i);
            if (c == '!') fc = true;
            else if (c == '^') ex = true;
            else if (c == '/' || c == '%' || c == '×') md = true;
            else if (c == '+' || c == '-') sa = true;
        }
        if (fc) s = fct(s);
        if (s.charAt(0) == '-') {
            s = "0" + s;
            sa = true;
        }
        if (s.charAt(0) == '#') {
            s = "0+" + s;
            sa = true;
        }
        if (ex) s = exp(s);
        if (s.charAt(0) == '-') {
            s = "0" + s;
            sa = true;
        }
        if (s.charAt(0) == '#') {
            s = "0+" + s;
            sa = true;
        }
        if (md) s = mdm(s);
        if (s.charAt(0) == '-') {
            s = "0" + s;
            sa = true;
        }
        if (s.charAt(0) == '#') {
            s = "0+" + s;
            sa = true;
        }
        if (sa) s = sad(s);
        if (br || fc || ex || md || sa) shouldPreResult = true;
        return s;
    }
    private SciMethod findSci(int index) {
        for (int i = 0; i < sciMethods.size(); i++) {
            if (sciMethods.get(i).getEndIdx() == index) return sciMethods.get(i);
        }
        return null;
    }
    private String brc(String s) {
        char c;
        char d;
        int idxc = 0, idxo = 0;
        byte cbrac = 2, obrac = 2;
        boolean check = true;
        int l = s.length();
        while (check) {
            check = false;
            for (int i = l - 1; i >= 0; i--) {
                c = s.charAt(i);
                if (c == ')') {
                    cbrac = 1;
                    idxc = i;
                    check = true;
                }
                else if (cbrac == 1 && c == '(') {
                    cbrac = 0;
                    idxo = i;
                    obrac = 1;
                }
                else if (obrac == 2 && c == '(') {
                    obrac = 0;
                    check = true;
                }
            }
            if (cbrac == 1) {
                s = "(".concat(s);
                update_sciMethods(-1, 1);
            }
            else if (obrac == 0) s = s.concat(")");
            else if (check) {
                String fr;
                String dr;
                String sf = "";
                String sol = s.substring(idxo + 1,idxc);
                int len = sol.length() + 2;
                if (sol.length() == 0) return "Error";
                char chr = sol.charAt(sol.length() - 1);
                char dhr = sol.charAt(0);
                if (chr == '^' || chr == '/' || chr == '%' || chr == '×' || chr == '+' || chr == '-' ||
                        dhr == '!' || dhr == '^' || dhr == '/' || dhr == '%' || dhr == '×' || sol.equals(".")) return "Error";
                if (dhr == '+') sol = sol.substring(1);
                sol = find(sol);
                if (sol.equals("Math Error") || sol.equals("Domain Error") || sol.equals("Not a number")) return sol;
                SciMethod sci = findSci(idxo);
                try {
                    if (sci != null) {
                        sf = sci.getFunction();
                        double pr = Double.parseDouble(sol);
                        switch (sf) {
                            case "abs":
                                if (pr < 0) pr = pr * -1;
                                sol = String.valueOf(pr);
                                break;
                            case "log":
                                sol = String.valueOf(Math.log10(pr));
                                break;
                            case "ln":
                                sol = String.valueOf(Math.log(pr));
                                break;
                            case "∛":
                                sol = String.valueOf(Math.cbrt(pr));
                                break;
                            case "√":
                                sol = String.valueOf(Math.sqrt(pr));
                                break;
                            default:
                                sol = trig(sf,pr,sci.isRad());
                                break;
                        }
                        if (sol.equals("Domain Error") || Double.isNaN(Double.parseDouble(sol))) return "Domain Error";
                    }
                }
                catch (NumberFormatException e) {
                    Log.d(TAG, e + "braces");
                    return "Error";
                }
                if (sol.charAt(0) == '-') sol = "#" + sol.substring(1);
                len = len + sf.length();
                if (sci != null) sciMethods.remove(sci);
                removeUpdate_sciMethods(idxo, len);
                update_sciMethods((idxo - sf.length() - 1), sol.length());
                if (idxo - sf.length() != 0) {
                    d = s.charAt((idxo - sf.length()) - 1);
                    fr = s.substring(0, idxo - sf.length());
                    if (d != '^' && d != '/' && d != '%' && d != '×' && d != '+' && d != '-' && d != '(') {
                        sol = "×" + sol;
                        update_sciMethods(idxo - sf.length() - 1, 1);
                    }
                    sol = fr + sol;
                }
                if (idxc != s.length() - 1) {
                    d = s.charAt(idxc + 1);
                    dr = s.substring(idxc + 1,l);
                    if (d != '!' && d != '^' && d != '/' && d != '%' && d != '×' && d != '+' && d != '-' && d != ')') {
                        sol = sol + "×";
                        update_sciMethods(idxo - sf.length() - 1, 1);
                    }
                    sol = sol + dr;
                }
                s = sol;
            }
            l = s.length();
            obrac = 2;
            cbrac = 2;
        }
        return s;
    }
    @SuppressWarnings("DuplicateExpressions")
    private String trig(String function, double number, boolean isRadian) {
        int a = 1000000000;
        switch (function) {
            case "sin":
                if (!isRadian) number = Math.toRadians(number);
                number = (double) Math.round(Math.sin(number)*a)/a;
                break;
            case "cos":
                if (!isRadian) number = Math.toRadians(number);
                number = (double) Math.round(Math.cos(number)*a)/a;
                break;
            case "tan":
                if (!isRadian && number == 90.0) return "Domain Error";
                if (!isRadian) number = Math.toRadians(number);
                number = (double) Math.round(Math.tan(number)*a)/a;
                break;
            case "cot":
                if (!isRadian && number == 90.0) return "1.0";
                if (!isRadian) number = Math.toRadians(number);
                number = (double) Math.round(Math.tan(number)*a)/a;
                if (number == 0.0) return "Domain Error";
                number = 1.0 / number;
                break;
            case "sec":
                if (!isRadian) number = Math.toRadians(number);
                number = (double) Math.round(Math.cos(number)*a)/a;
                if (number == 0.0) return "Domain Error";
                number = 1.0 / number;
                break;
            case "csc":
                if (!isRadian) number = Math.toRadians(number);
                number = (double) Math.round(Math.sin(number)*a)/a;
                if (number == 0.0) return "Domain Error";
                number = 1.0 / number;
                break;
            case "asin":
                number = Math.toDegrees(Math.asin(number));
                if (!Double.isNaN(number)) number = (double) Math.round(number*a)/a;
                else return "Domain Error";
                break;
            case "acos":
                number = Math.toDegrees(Math.acos(number));
                if (!Double.isNaN(number)) number = (double) Math.round(number*a)/a;
                else return "Domain Error";
                break;
            case "atan":
                number = Math.toDegrees(Math.atan(number));
                if (!Double.isNaN(number)) number = (double) Math.round(number*a)/a;
                else return "Domain Error";
                break;
            case "acot":
                number = 1.0 / number;
                number = Math.toDegrees(Math.atan(number));
                if (!Double.isNaN(number)) number = (double) Math.round(number*a)/a;
                else return "Domain Error";
                break;
            case "asec":
                if (number == 0.0) return "Domain Error";
                number = 1.0 / number;
                number = Math.toDegrees(Math.acos(number));
                if (!Double.isNaN(number)) number = (double) Math.round(number*a)/a;
                else return "Domain Error";
                break;
            case "acsc":
                if (number == 0.0) return "Domain Error";
                number = 1.0 / number;
                number = Math.toDegrees(Math.asin(number));
                if (!Double.isNaN(number)) number = (double) Math.round(number*a)/a;
                else return "Domain Error";
                break;
            case "sinh":
                number = (double) Math.round(Math.sinh(number)*a)/a;
                break;
            case "cosh":
                number = (double) Math.round(Math.cosh(number)*a)/a;
                break;
            case "tanh":
                number = (double) Math.round(Math.tanh(number)*a)/a;
                break;
            case "coth":
                number = (double) Math.round(Math.tanh(number)*a)/a;
                if (number == 0.0) return "Domain Error";
                number = 1.0 / number;
                break;
            case "sech":
                number = (double) Math.round(Math.cosh(number)*a)/a;
                number = 1.0 / number;
                break;
            case "csch":
                number = (double) Math.round(Math.sinh(number)*a)/a;
                if (number == 0.0) return "Domain Error";
                number = 1.0 / number;
                break;
            default:
                Log.d(TAG, "trig: err");
                return "Error";
        }
        return String.valueOf(number);
    }
    private String fct(String s) {
        char c;
        int l = s.length();
        int idx = -1;
        double nm;
        double fac = 1.0;
        try {
            for (int i = 0; i < l; i++) {
                c = s.charAt(i);
                if (c == '!') {
                    String fr;
                    fr = s.substring(idx + 1, i);
                    if (fr.charAt(0) == '#') return "Error";
                    nm = Double.parseDouble(fr);
                    if (nm > 170) return "Infinity";
                    int in = (int) nm;
                    if (nm != in) return "Domain Error";
                    for (int f = 1; f <= nm; f++) {
                        fac = fac * f;
                    }
                    String n, m;
                    n = s.substring(0, idx + 1);
                    n = n + fac;
                    if (i != l - 1) {
                        m = s.substring(i + 1, l);
                        n = n + m;
                    }
                    s = n;
                    l = s.length();
                    idx = -1;
                    fac = 1.0;
                    i = -1;
                }
                else if (c == '^' || c == '/' || c == '%' || c == '×' || c == '+' || c == '-') {
                    if (c == '-' || c == '+') {
                        if (i != 0 && s.charAt(i - 1) != 'E' && s.charAt(i - 1) != 'e') idx = i;
                    } else idx = i;
                }
            }
            return s;
        }
        catch (NumberFormatException | StringIndexOutOfBoundsException | NullPointerException e) {
            Log.d(TAG, e + "factorial");
            return "Error";
        }
    }
    private String exp(String s) {
        char c;
        char d;
        int l = s.length();
        int idx = l;
        try {
            for (int i = l - 1; i >= 0; i--) {
                c = s.charAt(i);
                if (c == '^') {
                    for (int j = i - 1; j >= 0; j--) {
                        d = s.charAt(j);
                        if (d == '^' || d == '/' || d == '%' || d == '×' || d == '+' || d == '-' || j == 0) {
                            if (d == '-' || d == '+' && j != 0) {
                                if (s.charAt(j - 1) == 'E' || s.charAt(j - 1) == 'e') continue;
                            }
                            String fr;
                            if (d == '-') {
                                fr = s.substring(j, i);
                            } else if (j != 0) {
                                fr = s.substring(j + 1, i);
                            } else {
                                fr = s.substring(0, i);
                            }
                            if (fr.charAt(0) == '#') fr = "-" + fr.substring(1);
                            String dr;
                            dr = s.substring(i + 1, idx);
                            if (dr.charAt(0) == '#') dr = "-" + dr.substring(1);
                            cal = Math.pow(Double.parseDouble(fr), Double.parseDouble(dr));
                            if (Double.isNaN(cal)) return "Not a number";
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
                            } else {
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
                } else if (c == '/' || c == '%' || c == '×' || c == '+' || c == '-') {
                    if (c == '-' || c == '+') {
                        if (i != 0 && s.charAt(i - 1) != 'E' && s.charAt(i - 1) != 'e') idx = i;
                    } else idx = i;
                }
            }
            return s;
        }
        catch (NumberFormatException | StringIndexOutOfBoundsException | NullPointerException e) {
            Log.d(TAG, e + "exponent");
            return "Error";
        }
    }
    private String mdm(String s) {
        char c;
        char d;
        int l = s.length();
        int idx = 0;
        try {
            for (int i = 0; i < l; i++) {
                c = s.charAt(i);
                if (c == '/' || c == '%' || c == '×') {
                    for (int j = i + 1; j < l; j++) {
                        d = s.charAt(j);
                        if (d == '/' || d == '%' || d == '×' || d == '+' || d == '-' || j == l - 1) {
                            if (d == '-' || d == '+') {
                                if (s.charAt(j - 1) == 'E' || s.charAt(j - 1) == 'e') continue;
                            }
                            String dr;
                            if (j != l - 1) {
                                dr = s.substring(i + 1, j);
                            } else {
                                dr = s.substring(i + 1, l);
                            }
                            if (dr.charAt(0) == '#') dr = "-" + dr.substring(1);
                            String fr;
                            fr = s.substring(idx, i);
                            if (fr.charAt(0) == '#') fr = "-" + fr.substring(1);
                            if (c == '/') {
                                if (Double.parseDouble(dr) == 0.0) {
                                    return "Math Error";
                                }
                                cal = Double.parseDouble(fr) / Double.parseDouble(dr);
                            }
                            if (c == '%') {
                                if (Double.parseDouble(dr) == 0.0) {
                                    return "Math Error";
                                }
                                cal = Double.parseDouble(fr) % Double.parseDouble(dr);
                            }
                            if (c == '×') cal = Double.parseDouble(fr) * Double.parseDouble(dr);
                            if (Double.isNaN(cal)) return "Not a number";
                            String n;
                            String m;
                            if (j != l - 1) {
                                n = s.substring(j, l);
                                n = cal + n;
                            } else {
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
                } else if (c == '+' || c == '-') {
                    if (s.charAt(i - 1) != 'E' && s.charAt(i - 1) != 'e') idx = i + 1;
                }
            }
            return s;
        }
        catch (NumberFormatException | StringIndexOutOfBoundsException | NullPointerException e) {
            Log.d(TAG, e + "multiplication");
            return "Error";
        }
    }
    private String sad(String s) {
        char c;
        char d;
        int l = s.length();
        int idx = 0;
        try {
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
                            } else {
                                dr = s.substring(i + 1, l);
                            }
                            if (dr.charAt(0) == '#') dr = "-" + dr.substring(1);
                            String fr;
                            fr = s.substring(idx, i);
                            if (fr.charAt(0) == '#') fr = "-" + fr.substring(1);
                            if (c == '+') cal = Double.parseDouble(fr) + Double.parseDouble(dr);
                            if (c == '-') cal = Double.parseDouble(fr) - Double.parseDouble(dr);
                            if (Double.isNaN(cal)) return "Not a number";
                            String n;
                            if (j != l - 1) {
                                n = s.substring(j, l);
                                n = cal + n;
                            } else {
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
        catch (NumberFormatException | StringIndexOutOfBoundsException | NullPointerException e) {
            Log.d(TAG, e + "addition");
            return "Error";
        }
    }
    private void textShow() {
        if (show.equalsIgnoreCase("Infinity")) show = "Value too large";
        if (cln) output.setSelection(output.length());
        int l = show.length();
        if (l > 27) {
            output.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);
        }
        else if (l > 9) {
            output.setTextSize(TypedValue.COMPLEX_UNIT_SP,45);
        }
        else {
            output.setTextSize(TypedValue.COMPLEX_UNIT_SP,50);
        }
        if (!output.hasFocus()) output.requestFocus();
        int cursor = output.length() - output.getSelectionStart();
        output.setText(show);
        output.setSelection(output.length() - cursor);
        Layout layout = output.getLayout();
        int offset = output.getSelectionStart();
        int y = 0;
        if (layout != null) {
            int line = layout.getLineForOffset(offset);
            int baseline = layout.getLineBaseline(line);
            int ascent = layout.getLineAscent(line);
            y = baseline + ascent;
        }
        scrollTxt.scrollTo(0, y);
    }
    public void backspace(View view) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(10);
        if (cln || shouldClean) clean();
        restore_sciMethod();
        pos = output.getSelectionStart();
        if (isOutside_sciMethods(pos))
        if(show != null && pos != 0) {
            boolean canBack = true;
            if (show.charAt(pos - 1) == ')') ++braces;
            else if (show.charAt(pos - 1) == '(') {
                SciMethod sci = findSci(pos - 1);
                if (sci != null) {
                    int len = sci.getFunction().length() + 1;
                    show = show.substring(0, pos - len) + show.substring(pos);
                    count = count - len;
                    sciMethods.remove(sci);
                    removeUpdate_sciMethods(pos - 1, len);
                    canBack = false;
                }
                --braces;
            }
            if (canBack) {
                --count;
                show = show.substring(0, pos - 1) + show.substring(pos);
                removeUpdate_sciMethods(pos - 1, 1);
            }
            if (show.length() == 0 || show.equals("0")) clean();
            else {
                textShow();
                preResult();
            }
        }
    }
    public void clear() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(13);
        int cx = imc.getWidth();
        int cy = imc.getHeight();
        float fr = (float) Math.hypot(cx,cy);
        Animator anim = ViewAnimationUtils.createCircularReveal(imc,100,cy,100f,fr);
        imc.setVisibility(View.VISIBLE);
        anim.start();
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        clean();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imc.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }, 600
        );
    }
    private void clean() {
        show = null;
        cal = 0.0f;
        count = 0;
        braces = 0;
        cln = false;
        shouldClean = false;
        shouldPreResult = false;
        sciMethods = null;
        sciMethods = new ArrayList<>();
        storeMethods = null;
        storeMethods = new ArrayList<>();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pResult.setText("");
                pResult.setVisibility(View.GONE);
                output.setText("0");
                output.setSelection(1);
            }
        });
        new java.util.Timer().schedule(new java.util.TimerTask(){
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        output.setTextSize(TypedValue.COMPLEX_UNIT_SP,50);
                    }
                });
            }
        },10);
    }
    public void change(View view) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(10);
        Button check = (Button) view;
        if (check.getId() == R.id.hyperbolic) {
            if (hyp) {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hyperbolic.setPressed(false);
                                        hyperbolic.setBackgroundResource(R.drawable.ripplehyperbolicsel);
                                    }
                                });
                            }
                        }, 200
                );
                inverse.setBackgroundResource(R.drawable.rippleinverse);
                inv = true;
                hyp = false;
                sin.setBackgroundResource(R.drawable.ripplesinh);
                cos.setBackgroundResource(R.drawable.ripplecosh);
                tan.setBackgroundResource(R.drawable.rippletanh);
                cot.setBackgroundResource(R.drawable.ripplecoth);
                sec.setBackgroundResource(R.drawable.ripplesech);
                csc.setBackgroundResource(R.drawable.ripplecsch);
                sin.setTag("sinh(");
                cos.setTag("cosh(");
                tan.setTag("tanh(");
                cot.setTag("coth(");
                sec.setTag("sech(");
                csc.setTag("csch(");
                rad.setBackgroundResource(R.drawable.rippleradian);
                rd = true;
                canChange = false;
                editor = sharedPreferences.edit();
                editor.remove(RADIAN_KEY);
                editor.putBoolean(RADIAN_KEY, rd);
                editor.apply();
            }
            else {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hyperbolic.setPressed(false);
                                        hyperbolic.setBackgroundResource(R.drawable.ripplehyperbolic);
                                    }
                                });
                            }
                        }, 200
                );
                hyp = true;
                sin.setBackgroundResource(R.drawable.ripplesin);
                cos.setBackgroundResource(R.drawable.ripplecos);
                tan.setBackgroundResource(R.drawable.rippletan);
                cot.setBackgroundResource(R.drawable.ripplecot);
                sec.setBackgroundResource(R.drawable.ripplesec);
                csc.setBackgroundResource(R.drawable.ripplecsc);
                sin.setTag("sin(");
                cos.setTag("cos(");
                tan.setTag("tan(");
                cot.setTag("cot(");
                sec.setTag("sec(");
                csc.setTag("csc(");
                canChange = true;
            }
        }
        else if (check.getId() == R.id.inverse) {
            if (inv) {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        inverse.setPressed(false);
                                        inverse.setBackgroundResource(R.drawable.rippleinversesel);
                                    }
                                });
                            }
                        }, 200
                );
                hyperbolic.setBackgroundResource(R.drawable.ripplehyperbolic);
                hyp = true;
                inv = false;
                sin.setBackgroundResource(R.drawable.rippleasin);
                cos.setBackgroundResource(R.drawable.rippleacos);
                tan.setBackgroundResource(R.drawable.rippleatan);
                cot.setBackgroundResource(R.drawable.rippleacot);
                sec.setBackgroundResource(R.drawable.rippleasec);
                csc.setBackgroundResource(R.drawable.rippleacsc);
                sin.setTag("asin(");
                cos.setTag("acos(");
                tan.setTag("atan(");
                cot.setTag("acot(");
                sec.setTag("asec(");
                csc.setTag("acsc(");
                rad.setBackgroundResource(R.drawable.rippledegree);
                rd = false;
                canChange = false;
                editor = sharedPreferences.edit();
                editor.remove(RADIAN_KEY);
                editor.putBoolean(RADIAN_KEY, rd);
                editor.apply();
            }
            else {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        inverse.setPressed(false);
                                        inverse.setBackgroundResource(R.drawable.rippleinverse);
                                    }
                                });
                            }
                        }, 200
                );
                inv = true;
                sin.setBackgroundResource(R.drawable.ripplesin);
                cos.setBackgroundResource(R.drawable.ripplecos);
                tan.setBackgroundResource(R.drawable.rippletan);
                cot.setBackgroundResource(R.drawable.ripplecot);
                sec.setBackgroundResource(R.drawable.ripplesec);
                csc.setBackgroundResource(R.drawable.ripplecsc);
                sin.setTag("sin(");
                cos.setTag("cos(");
                tan.setTag("tan(");
                cot.setTag("cot(");
                sec.setTag("sec(");
                csc.setTag("csc(");
                canChange = true;
            }
        }
        else if (check.getId() == R.id.radian && canChange) {
            if (rd) {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rad.setPressed(false);
                                        rad.setBackgroundResource(R.drawable.rippledegree);
                                    }
                                });
                            }
                        }, 250
                );
                rd = false;
            }
            else {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rad.setPressed(false);
                                        rad.setBackgroundResource(R.drawable.rippleradian);
                                    }
                                });
                            }
                        }, 250
                );
                rd = true;
            }
            editor = sharedPreferences.edit();
            editor.remove(RADIAN_KEY);
            editor.putBoolean(RADIAN_KEY, rd);
            editor.apply();
        }
    }
    public void initData(View view) {
        hyperbolic = view.findViewById(R.id.hyperbolic);
        inverse = view.findViewById(R.id.inverse);
        rad = view.findViewById(R.id.radian);
        sin = view.findViewById(R.id.sin);
        cos = view.findViewById(R.id.cos);
        tan = view.findViewById(R.id.tan);
        cot = view.findViewById(R.id.cot);
        sec = view.findViewById(R.id.sec);
        csc = view.findViewById(R.id.csc);
        if (!rd) rad.setBackgroundResource(R.drawable.rippledegree);
    }
    public void histData(View view) {
        historyImg = view.findViewById(R.id.historyImg);
        textHistory = view.findViewById(R.id.textHistory);
        RecyclerView recyclerView = view.findViewById(R.id.history_recyclerView);
        if (historyItems.size() == 0) {
            historyImg.setVisibility(View.VISIBLE);
            textHistory.setVisibility(View.VISIBLE);
        }
        else {
            historyImg.setVisibility(View.GONE);
            textHistory.setVisibility(View.GONE);
        }
        adapter = new HistoryRecycler(historyItems, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    public void histMenu(View view) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(10);
        Button btn = (Button) view;
        if (btn.getId() == R.id.back_btn) {
            viewPager.setCurrentItem(1);
        }
        else {
            if (historyItems.size() != 0) {
                historyItems.clear();
                histSciMethod.clear();
                adapter.notifyDataSetChanged();
                if (historyItems.size() == 0) {
                    historyImg.setVisibility(View.VISIBLE);
                    textHistory.setVisibility(View.VISIBLE);
                }
                else {
                    historyImg.setVisibility(View.GONE);
                    textHistory.setVisibility(View.GONE);
                }
                Toast.makeText(this, "History Cleared!", Toast.LENGTH_SHORT).show();
                editor = sharedPreferences.edit();
                editor.remove(HISTORY_ITEMS_KEY);
                editor.putString(HISTORY_ITEMS_KEY, gson.toJson(historyItems));
                editor.remove(HISTORY_METHODS_KEY);
                editor.putString(HISTORY_METHODS_KEY, gson.toJson(histSciMethod));
                editor.apply();
            }
        }
    }
    public void setHistMethod(int position, String value) {
        if (cln) clean();
        restore_sciMethod();
        ArrayList<SciMethod> setMethod = histSciMethod.get(position);
        pos = output.getSelectionStart();
        if (isOutside_sciMethods(pos)) {
            String s;
            if (show == null) s = "0";
            else s = show;
            s = s.substring(0, pos);
            int len = s.length();
            int ins = -1;
            for (int i = 0; i < sciMethods.size(); i++) {
                if (sciMethods.get(i).getStartIdx() > (pos - 1)) {
                    ins = i;
                    break;
                }
            }
            update_sciMethods(pos - 1, value.length());
            int stidx, edidx;
            String fuc;
            boolean red;
            if (ins == -1) {
                for (int i = 0; i < setMethod.size(); i++) {
                    stidx = setMethod.get(i).getStartIdx();
                    edidx = setMethod.get(i).getEndIdx();
                    fuc = setMethod.get(i).getFunction();
                    red = setMethod.get(i).isRad();
                    stidx += len;
                    edidx += len;
                    sciMethods.add(new SciMethod(stidx, edidx, fuc, red));
                }
            } else {
                for (int i = (setMethod.size() - 1); i >= 0; i--) {
                    stidx = setMethod.get(i).getStartIdx();
                    edidx = setMethod.get(i).getEndIdx();
                    fuc = setMethod.get(i).getFunction();
                    red = setMethod.get(i).isRad();
                    stidx += len;
                    edidx += len;
                    sciMethods.add(ins, new SciMethod(stidx, edidx, fuc, red));
                }
            }
        }
    }
    public void setHist(String value, boolean isExp) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(10);
        if (!isExp) {
            if (cln) clean();
            restore_sciMethod();
            pos = output.getSelectionStart();
            if (isOutside_sciMethods(pos)) update_sciMethods(pos - 1, value.length());
        }
        if (isOutside_sciMethods(pos)) {
            if (shouldClean) shouldClean = false;
            String s;
            if (show == null) s = "0";
            else s = show;
            s = s.substring(0,pos) + value + s.substring(pos);
            show = s;
            count = show.length();
            textShow();
            preResult();
        }
    }
    private void removeHist(int position , int element) {
        if (element == 122) {
            historyItems.get(position).setExpression(null);
            histSciMethod.remove(position);
            histSciMethod.add(position, null);
        }
        if (element == 124) {
            historyItems.get(position).setResult(null);
        }
        if (historyItems.get(position).getExpression() == null && historyItems.get(position).getResult() == null) {
            historyItems.remove(position);
            histSciMethod.remove(position);
        }
        if (historyItems.size() == 0) {
            historyImg.setVisibility(View.VISIBLE);
            textHistory.setVisibility(View.VISIBLE);
        }
        else {
            historyImg.setVisibility(View.GONE);
            textHistory.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
        editor = sharedPreferences.edit();
        editor.remove(HISTORY_ITEMS_KEY);
        editor.putString(HISTORY_ITEMS_KEY, gson.toJson(historyItems));
        editor.remove(HISTORY_METHODS_KEY);
        editor.putString(HISTORY_METHODS_KEY, gson.toJson(histSciMethod));
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = this.getSharedPreferences("history", MODE_PRIVATE);
        viewPager = findViewById(R.id.viewPager);
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        output = findViewById(R.id.display);
        imc = findViewById(R.id.topcover);
        pResult = findViewById(R.id.preResult);
        output.setShowSoftInputOnFocus(false);
        output.requestFocus();
        scrollTxt = findViewById(R.id.scrolltxt);
        pos = output.getSelectionStart();
        registerForContextMenu(output);
        registerForContextMenu(pResult);
        rd = sharedPreferences.getBoolean(RADIAN_KEY, true);
        Type type = new TypeToken<ArrayList<HistoryItems>>(){}.getType();
        historyItems = gson.fromJson(sharedPreferences.getString(HISTORY_ITEMS_KEY, null), type);
        if (historyItems == null) {
            historyItems = new ArrayList<>();
            editor = sharedPreferences.edit();
            editor.putString(HISTORY_ITEMS_KEY, gson.toJson(historyItems));
            editor.apply();
        }
        Type typ = new TypeToken<ArrayList<ArrayList<SciMethod>>>(){}.getType();
        histSciMethod = gson.fromJson(sharedPreferences.getString(HISTORY_METHODS_KEY, null), typ);
        if (histSciMethod == null) {
            histSciMethod = new ArrayList<>();
            editor = sharedPreferences.edit();
            editor.putString(HISTORY_METHODS_KEY, gson.toJson(histSciMethod));
            editor.apply();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == output.getId())
            getMenuInflater().inflate(R.menu.display_context, menu);
        else
            getMenuInflater().inflate(R.menu.preresult_context, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.copyTxt) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Calculation", output.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (item.getItemId() == R.id.copyPreResult) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Calculation", pResult.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (item.getItemId() == 121 || item.getItemId() == 123) {
            String s;
            if (item.getItemId() == 121) s = historyItems.get(item.getGroupId()).getExpression();
            else s = historyItems.get(item.getGroupId()).getResult();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Calculation", s);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (item.getItemId() == 122 || item.getItemId() == 124) {
            removeHist(item.getGroupId(), item.getItemId());
            Toast.makeText(this, "Removed!", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            return super.onContextItemSelected(item);
        }
    }
}