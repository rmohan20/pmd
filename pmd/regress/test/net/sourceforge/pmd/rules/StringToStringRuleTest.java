package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.StringToStringRule;

public class StringToStringRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class StringToString1 {" + CPD.EOL +
    " private String baz() {" + CPD.EOL +
    "  String bar = \"howdy\";" + CPD.EOL +
    "  return bar.toString();" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class StringToString2 {" + CPD.EOL +
    " private String baz(String bar) {" + CPD.EOL +
    "  return bar.toString();" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class StringToString3 {" + CPD.EOL +
    " private String baz;" + CPD.EOL +
    " private int getBaz() {" + CPD.EOL +
    "  return baz.toString();" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class StringToString4 {" + CPD.EOL +
    " private int baz;" + CPD.EOL +
    " private int getBaz() {" + CPD.EOL +
    "  return baz;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class StringToString5 {" + CPD.EOL +
    " private String getBaz(String foo, StringBuffer buffer) {" + CPD.EOL +
    "  return buffer.toString();" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST6 =
    "public class StringToString6 {" + CPD.EOL +
    " private String getBaz() {" + CPD.EOL +
    "  String[] foo = {\"hi\"};" + CPD.EOL +
    "  return foo[0].toString();" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    public void testLocalVar() throws Throwable {
        runTestFromString(TEST1, 1, new StringToStringRule());
    }

    public void testParam() throws Throwable {
        runTestFromString(TEST2, 1, new StringToStringRule());
    }

    public void testInstanceVar() throws Throwable {
        runTestFromString(TEST3, 1, new StringToStringRule());
    }

    public void testPrimitiveType() throws Throwable {
        runTestFromString(TEST4, 0, new StringToStringRule());
    }

    public void testMultipleSimilarParams() throws Throwable {
        runTestFromString(TEST5, 0, new StringToStringRule());
    }

    public void testStringArray() throws Throwable {
        runTestFromString(TEST6, 1, new StringToStringRule());
    }
}
