package com.freetmp.mbg.merge.comment;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.comments.JavadocComment;

/**
 * Created by pin on 2015/4/12.
 */
public class JavadocCommentMerger extends AbstractMerger<JavadocComment> {

  @Override
  public JavadocComment doMerge(JavadocComment first, JavadocComment second) {
    JavadocComment comment = new JavadocComment();

    if(first.getContent().length() > second.getContent().length()){
      comment.setContent(first.getContent());
    }else {
      comment.setContent(second.getContent());
    }

    return comment;
  }

  @Override
  public boolean doIsEquals(JavadocComment first, JavadocComment second) {
    return true;
  }
}
