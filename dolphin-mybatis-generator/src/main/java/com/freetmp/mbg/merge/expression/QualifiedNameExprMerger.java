package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.QualifiedNameExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class QualifiedNameExprMerger extends AbstractMerger<QualifiedNameExpr> {

  @Override public QualifiedNameExpr merge(QualifiedNameExpr first, QualifiedNameExpr second) {
    QualifiedNameExpr qne = new QualifiedNameExpr();

    qne.setComment(mergeSingle(first.getComment(),second.getComment()));
    qne.setName(first.getName());
    qne.setQualifier(mergeSingle(first.getQualifier(),second.getQualifier()));

    return qne;
  }

  @Override public boolean isEquals(QualifiedNameExpr first, QualifiedNameExpr second) {
    if(first == second) return true;

    if(!first.getName().equals(second.getName())) return false;
    if(!isEqualsUseMerger(first.getQualifier(),second.getQualifier())) return false;

    return true;
  }
}
