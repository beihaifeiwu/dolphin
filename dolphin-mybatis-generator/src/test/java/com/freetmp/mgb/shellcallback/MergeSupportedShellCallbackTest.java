package com.freetmp.mgb.shellcallback;

import com.freetmp.mbg.shellcallback.MergeSupportedShellCallback;
import org.eclipse.jdt.core.dom.*;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by pin on 2015/2/7.
 */
public class MergeSupportedShellCallbackTest {

    static MergeSupportedShellCallback callback;

    @BeforeClass
    public static void init(){
        callback = new MergeSupportedShellCallback(true);
    }

    public CompilationUnit generateAST(String source){
        ASTParser astParser = ASTParser.newParser(AST.JLS8);
        astParser.setSource(source.toCharArray());
        astParser.setKind(ASTParser.K_COMPILATION_UNIT);
        return (CompilationUnit) astParser.createAST(null);
    }

    @Test
    public void testAstEquals(){
        CompilationUnit c1 = generateAST("import org.eclipse.jdt.core.dom.AST;\n" +
                "import org.eclipse.jdt.core.dom.ASTParser;\n" +
                "import org.eclipse.jdt.core.dom.CompilationUnit;\n" +
                "\n" +
                "public class Question {\n" +
                "    public static void main(String[] args) {\n" +
                "        String source = \"class Bob {}\";\n" +
                "        ASTParser parser = ASTParser.newParser(AST.JLS3); \n" +
                "        parser.setSource(source.toCharArray());\n" +
                "        CompilationUnit result = (CompilationUnit) parser.createAST(null);\n" +
                "\n" +
                "        String source2 = \"class Bob {public void MyMethod(){}}\";\n" +
                "        ASTParser parser2 = ASTParser.newParser(AST.JLS3); \n" +
                "        parser2.setSource(source2.toCharArray());\n" +
                "        CompilationUnit result2 = (CompilationUnit) parser2.createAST(null);\n" +
                "    }\n" +
                "}");
        CompilationUnit c2 = generateAST("import org.eclipse.jdt.core.dom.AST;\n" +
                "import org.eclipse.jdt.core.dom.ASTParser;\n" +
                "import org.eclipse.jdt.core.dom.CompilationUnit;\n" +
                "\n" +
                "public class Question {\n" +
                "    public static void main(String[] args) {\n" +
                "        String source = \"class Bob {}\";\n" +
                "        ASTParser parser = ASTParser.newParser(AST.JLS3); \n" +
                "        parser.setSource(source.toCharArray());\n" +
                "        CompilationUnit result = (CompilationUnit) parser.createAST(null);\n" +
                "\n" +
                "        String source2 = \"class Bob {public void MyMethod(){}}\";\n" +
                "        ASTParser parser2 = ASTParser.newParser(AST.JLS3); \n" +
                "        parser2.setSource(source2.toCharArray());\n" +
                "        CompilationUnit result2 = (CompilationUnit) parser2.createAST(null);\n" +
                "    }\n" +
                "}");
        ASTMatcher matcher = new ASTMatcher();
        System.out.println(matcher.match(c1, c2));
        assertTrue(matcher.match(c1,c2));

        c2 = generateAST("import org.eclipse.jdt.core.dom.AST;\n" +
                "import org.eclipse.jdt.core.dom.ASTParser;\n" +
                "import org.eclipse.jdt.core.dom.CompilationUnit;\n" +
                "\n" +
                "public class Question {\n" +
                "        String source2 = \"class Bob {public void MyMethod(){}}\";\n" +
                "        ASTParser parser2 = ASTParser.newParser(AST.JLS3); \n" +
                "        parser2.setSource(source2.toCharArray());\n" +
                "        CompilationUnit result2 = (CompilationUnit) parser2.createAST(null);\n" +
                "    }\n" +
                "}");
        assertFalse(matcher.match(c1,c2));

        System.out.println(c2);
        System.out.println(matcher.match(c1, c2));
    }
}
