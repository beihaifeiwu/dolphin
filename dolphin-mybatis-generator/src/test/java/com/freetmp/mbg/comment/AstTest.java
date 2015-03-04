package com.freetmp.mbg.comment;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.Test;

import java.io.StringReader;

/**
 * Created by LiuPin on 2015/3/4.
 */
public class AstTest {

    @Test
    public void testFileComment() throws ParseException {
        String source = "/**                                                                          \n" +
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

        CompilationUnit unit = generateAst(source);
        System.out.println(unit.getComment());
    }

    protected CompilationUnit generateAst(String source) throws ParseException {
        return JavaParser.parse(new StringReader(source),true);
    }
}
