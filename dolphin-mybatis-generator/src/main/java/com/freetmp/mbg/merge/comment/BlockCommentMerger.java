package com.freetmp.mbg.merge.comment;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.comments.BlockComment;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by pin on 2015/4/19.
 */
public class BlockCommentMerger extends AbstractMerger<BlockComment> {

    private BlockCommentMerger(){}

    static {
        if(getMerger(BlockComment.class) == null) {
            register(BlockComment.class, new BlockCommentMerger());
        }
    }

    @Override
    public BlockComment merge(BlockComment first, BlockComment second) {
        if(StringUtils.isBlank(first.getContent())) return second;
        if(StringUtils.isBlank(second.getContent())) return first;

        BlockComment comment = new BlockComment();
        comment.setContent(first.getContent() + "\n" + second.getContent());

        return comment;
    }

    @Override
    public boolean isEquals(BlockComment first, BlockComment second) {
        return true;
    }
}