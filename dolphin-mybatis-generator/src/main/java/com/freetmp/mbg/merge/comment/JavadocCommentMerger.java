package com.freetmp.mbg.merge.comment;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.comments.JavadocComment;

/**
 * Created by pin on 2015/4/12.
 */
public class JavadocCommentMerger extends AbstractMerger<JavadocComment> {

    @Override
    public JavadocComment merge(JavadocComment first, JavadocComment second) {
        if(first.getContent() == null) return second;
        if(second.getContent() == null) return first;

        if(first.getContent().length() > second.getContent().length()){
            return first;
        }else {
            return second;
        }
    }

    @Override
    public boolean isEquals(JavadocComment first, JavadocComment second) {
        return true;
    }
}
