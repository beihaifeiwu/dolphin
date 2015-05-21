package com.freetmp.mbg.comment;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.io.StringReader;

/*
 * Created by LiuPin on 2015/3/4.
 */
public class AstTest {

  @Rule
  public final SystemOutRule systemOutRule = new SystemOutRule().muteForSuccessfulTests();

  @Test
  public void testFileComment() throws ParseException {
    String source = "/**                                                                         \n" +
        " * Copyright 2015-2015 the original author or authors.                         \n" +
        " *                                                                           \n" +
        " *       HaHa,I have the right to do anything!                               \n" +
        " */\n" +
        "package com.freetmp.xmbg.postgresql.mapper;\n" +
        "\n" +
        "import com.freetmp.xmbg.postgresql.entity.Admin;\n" +
        "import com.freetmp.xmbg.postgresql.entity.AdminExample;\n" +
        "import java.util.List;\n" +
        "import org.apache.ibatis.annotations.Param;\n" +
        "\n" +
        "public interface AdminMapper {\n" +
        "    int countByExample(AdminExample example);\n" +
        "\n" +
        "    int deleteByExample(AdminExample example);\n" +
        "\n" +
        "    int deleteByPrimaryKey(Long id);\n" +
        "\n" +
        "    int insert(Admin record);\n" +
        "\n" +
        "    int insertSelective(Admin record);\n" +
        "\n" +
        "    List<Admin> selectByExample(AdminExample example);\n" +
        "\n" +
        "    Admin selectByPrimaryKey(Long id);\n" +
        "\n" +
        "    int updateByExampleSelective(@Param(\"record\") Admin record, @Param(\"example\") AdminExample example);\n" +
        "\n" +
        "    int updateByExample(@Param(\"record\") Admin record, @Param(\"example\") AdminExample example);\n" +
        "\n" +
        "    int updateByPrimaryKeySelective(Admin record);\n" +
        "\n" +
        "    int updateByPrimaryKey(Admin record);\n" +
        "\n" +
        "    int batchInsert(List<Admin> list);\n" +
        "\n" +
        "    int batchUpdate(List<Admin> list);\n" +
        "\n" +
        "    int upsert(@Param(\"record\") Admin record, @Param(\"array\") String[] array);\n" +
        "\n" +
        "    int batchUpsert(@Param(\"records\") List<Admin> list, @Param(\"array\") String[] array);\n" +
        "}";

    String source2 =
        "package com.freetmp.xmbg.postgresql.mapper;\n" +
            "\n" +
            "import com.freetmp.xmbg.postgresql.entity.Admin;\n" +
            "import com.freetmp.xmbg.postgresql.entity.AdminExample;\n" +
            "import java.util.List;\n" +
            "import org.apache.ibatis.annotations.Param;\n" +
            "\n" +
            "public interface AdminMapper {\n" +
            "    int countByExample(AdminExample example);\n" +
            "\n" +
            "    int deleteByExample(AdminExample example);\n" +
            "\n" +
            "    int deleteByPrimaryKey(Long id);\n" +
            "\n" +
            "    int insert(Admin record);\n" +
            "\n" +
            "    int insertSelective(Admin record);\n" +
            "\n" +
            "    List<Admin> selectByExample(AdminExample example);\n" +
            "\n" +
            "    Admin selectByPrimaryKey(Long id);\n" +
            "\n" +
            "    int updateByExampleSelective(@Param(\"record\") Admin record, @Param(\"example\") AdminExample example);\n" +
            "\n" +
            "    int updateByExample(@Param(\"record\") Admin record, @Param(\"example\") AdminExample example);\n" +
            "\n" +
            "    int updateByPrimaryKeySelective(Admin record);\n" +
            "\n" +
            "    int updateByPrimaryKey(Admin record);\n" +
            "\n" +
            "    int batchInsert(List<Admin> list);\n" +
            "\n" +
            "    int batchUpdate(List<Admin> list);\n" +
            "\n" +
            "    int upsert(@Param(\"record\") Admin record, @Param(\"array\") String[] array);\n" +
            "\n" +
            "    int batchUpsert(@Param(\"records\") List<Admin> list, @Param(\"array\") String[] array);\n" +
            "}" +
            "\n" +
            "interface Serializable {\n" +
            "}\n";


    CompilationUnit unit = generateAst(source);
    System.out.println(unit.getComment());
    System.out.println(unit.getComment().getClass());

    CompilationUnit unit1 = generateAst(source);
    for (int i = 0; i < unit.getImports().size(); i++) {
      System.out.println(unit.getImports().get(i).equals(unit1.getImports().get(i)));
    }
    System.out.println();

    CompilationUnit unit2 = generateAst(source2);
    unit.setTypes(unit2.getTypes());
    System.out.println(unit2.toString());

    JavaInterfaceSource javaClass = Roaster.parse(JavaInterfaceSource.class, source);
    System.out.println(javaClass.getJavaDoc().getText());

  }

  protected CompilationUnit generateAst(String source) throws ParseException {
    return JavaParser.parse(new StringReader(source), true);
  }
}
