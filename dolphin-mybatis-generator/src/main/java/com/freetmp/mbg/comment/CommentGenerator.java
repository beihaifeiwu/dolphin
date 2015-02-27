package com.freetmp.mbg.comment;

import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.internal.DefaultCommentGenerator;

/**
 * Created by LiuPin on 2015/2/14.
 */
public class CommentGenerator extends DefaultCommentGenerator {

    public CommentGenerator() {
        super();
    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {
        compilationUnit.addFileCommentLine(" Copyright 2014-2015 the original author or authors.");
        compilationUnit.addFileCommentLine("\n");
        compilationUnit.addFileCommentLine(" Licensed under the Apache License, Version 2.0 (the \"License\");");
        compilationUnit.addFileCommentLine(" you may not use this file except in compliance with the License.");
        compilationUnit.addFileCommentLine(" You may obtain a copy of the License at");
        compilationUnit.addFileCommentLine("\n");
        compilationUnit.addFileCommentLine("   http://www.apache.org/licenses/LICENSE-2.0");
        compilationUnit.addFileCommentLine("\n");
        compilationUnit.addFileCommentLine(" Unless required by applicable law or agreed to in writing, software");
        compilationUnit.addFileCommentLine(" distributed under the License is distributed on an \"AS IS\" BASIS,");
        compilationUnit.addFileCommentLine(" WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
        compilationUnit.addFileCommentLine(" See the License for the specific language governing permissions and");
        compilationUnit.addFileCommentLine(" limitations under the License.");
    }


}
