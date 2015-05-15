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
    comment.setComment(mergeSingle(first.getComment(),second.getComment()));
    comment.setContent(first.getContent());
    return comment;
  }

  @Override
  public boolean doIsEquals(LineComment first, LineComment second) {
    if(!first.getContent().equals(second.getContent())) return false;
    return true;
  }
}
