package com.freetmp.mbg.merge.comment;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.comments.LineComment;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by pin on 2015/4/19.
 */
public class LineCommentMerger extends AbstractMerger<LineComment> {

    @Override
    public LineComment merge(LineComment first, LineComment second) {
        if(StringUtils.isBlank(first.getContent())) return second;
        if(StringUtils.isBlank(second.getContent())) return first;

        if(first.getContent().length() > second.getContent().length())
            return first;
        else
            return second;
    }

    @Override
    public boolean isEquals(LineComment first, LineComment second) {
        return true;
    }
}
