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
      copyPosition(first,comment);
    }else {
      comment.setContent(second.getContent());
      copyPosition(second,comment);
    }

    return comment;
  }

  @Override
  public boolean doIsEquals(JavadocComment first, JavadocComment second) {
    return similarity(first.getContent(), second.getContent()) > 0.9d;
  }
}
