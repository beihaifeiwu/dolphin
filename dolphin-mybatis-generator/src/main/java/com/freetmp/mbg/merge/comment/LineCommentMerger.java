package com.freetmp.mbg.merge.comment;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.comments.LineComment;

/**
 * Created by pin on 2015/4/19.
 */
public class LineCommentMerger extends AbstractMerger<LineComment> {

  @Override
  public LineComment doMerge(LineComment first, LineComment second) {
    LineComment comment = new LineComment();
    comment.setContent(first.getContent());
    copyPosition(first,comment);
    return comment;
  }

  @Override
  public boolean doIsEquals(LineComment first, LineComment second) {
    return similarity(first.getContent(),second.getContent()) > 0.9d;
  }
}
