package com.onlinejudge.util;

import org.jetbrains.annotations.Contract;

public class HandlerCode {
    /*
    UPD: maybe enum is better solution... switch case in java doesn't support long
         Java sucks :(

    dual hashed with this C++ code, since comparing Long is faster that comparing chars
    long long dualhash(const std::string &str){
        // uppercase str
        const long long mod1 = 19260817, mod2 = 998244353;
        long long val1 = 0, val2 = 0;
        for (auto it : str){
            val1 = (val1 * 26 + (it - 'A')) % mod2;
            val2 = (val2 * 26 + (it - 'A')) % mod2;
        }
        return val1 * mod2 + val2;
    }
    Java Implementation below
     */
    private static final long mod1 = 19260817L;
    private static final long mod2 =  998244353L;
    protected static final long LOGIN = 5267812320873258L;
    protected static final long QUERYSTUDENTPRACTICESCORE = 4781425251978486L;
    protected static final long DELETEEXAM = 9231245594631845L;
    protected static final long LISTEXAMSTUDENT = 18037027745840739L;
    protected static final long ADMINMODIFYSEX = 456989366501257L;
    protected static final long ADMINUPDATEUSERNAME = 9528922585096161L;
    protected static final long ADMINRESETPASSWORD = 3177252542266145L;
    protected static final long MODIFYSEX = 564985342119026L;
    protected static final long UPDATEUSERNAME = 18174341654976044L;
    protected static final long UPDATEPASSWORD = 17437012788166473L;
    protected static final long DELETEACCOUNT = 9791304127385178L;
    protected static final long LISTALLUSER = 18294796317990453L;
    protected static final long SEARCHCONTEST = 18217134742722287L;
    protected static final long SEARCHPROBLEM = 5900594328765754L;
    protected static final long CREATEMODIFYEXAM = 12401651360337240L;
    protected static final long DELETEPROBLEMFROMEXAM = 549039435153916L;
    protected static final long LISTEXAM = 17219892980380031L;
    protected static final long LISTEXAMPROBLEM = 8551146938781403L;
    protected static final long QUERYSTUDENTSCORE = 14558195039819831L;
    protected static final long REPLACEPROBLEMFROMEXAM = 10972240276613547L;
    protected static final long LISTPROBLEMFROMDATABASE = 15748947643126009L;
    protected static final long LISTSUBJECT = 18258404993279462L;
    protected static final long LISTTAG = 15461836239667084L;
    protected static final long PROBLEMUPDATE = 14516411261422039L;
    protected static final long SUBUPDATE = 19003703498092297L;
    protected static final long SUBJECTSUBLIST = 12134028564678587L;
    protected static final long CHANGESCORE = 12227906976696568L;
    protected static final long ADDLISTUSER = 6171989054062145L;
    protected static final long EDITPROBLEMFROMEXAM = 7595788552758732L;
    protected static final long DELETEPROBLEMFROMDATABASE = 15197749505620344L;
    protected static final long CREATEPROBLEMLIST = 4718659792655081L;
    protected static final long CREATEPROBLEMTAG = 926165837817287L;

    @Contract(pure = true)
    protected static long dualhash(String str) {
        String upperStr = str.toUpperCase();
        long val1 = 0L, val2 = 0L;
        int len = str.length();
        for (int i = 0; i < len; ++i) {
            int curval = upperStr.charAt(i);
            val1 = (val1 * 26L + (curval - 65)) % mod1;
            val2 = (val2 * 26L + (curval - 65)) % mod2;
        }
        return val1 * mod2 + val2;
    }
}
