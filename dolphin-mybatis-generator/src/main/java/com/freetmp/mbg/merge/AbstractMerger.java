package com.freetmp.mbg.merge;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BaseParameter;
import com.github.javaparser.ast.body.ModifierSet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LiuPin on 2015/3/27.
 */
public abstract class AbstractMerger<M> {

    protected static ConcurrentHashMap<Class,AbstractMerger> map = new ConcurrentHashMap<>();

    public <T> boolean isAllNull(T one, T two) {
        return one == null ? two == null : false;
    }

    public  <T> boolean isAllNotNull(T one, T two) {
        return one != null && two != null;
    }

    public  <T> T findFirstNotNull(T... types) {
        for (T type : types) {
            if (type != null) return type;
        }
        return null;
    }

    public  <T> int indexOf(int start, List<T> datas, T target) {
        int index = -1;

        for (int i = start; i < datas.size(); i++) {
            if (datas.get(i).equals(target)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public  <T> boolean isEitherContains(List<T> one, List<T> two) {
        if (!isAllNotNull(one, two)) return true;

        List<T> longer = one.size() > two.size() ? one : two;
        List<T> shorter = one.size() > two.size() ? two : one;

        boolean contains = true;

        for (T t : shorter) {
            if (!longer.contains(t)) {
                contains = false;
                break;
            }
        }

        return contains;

    }

    @SuppressWarnings("unchecked")
    public  <T> T mergeSelective(T one, T two) {
        T t = null;

        if (isAllNull(one, two)) {
            return t;
        }

        t = findFirstNotNull(one, two);

        return t;
    }

    public  <T> boolean isListEquals(List<T> one, List<T> two) {
        boolean isEqual = true;

        if (isAllNull(one, two)) {
            isEqual = true;
        } else if (isAllNotNull(one, two)) {
            if (one.size() != two.size()) {
                isEqual = false;
            } else {
                for (int index = 0; index < one.size(); index++) {
                    T t1 = one.get(index);
                    T t2 = one.get(index);
                    if (!t1.equals(t2)) {
                        isEqual = false;
                        break;
                    }
                }
            }
        } else {
            isEqual = false;
        }

        return isEqual;
    }

    public  <T extends BaseParameter> boolean isParametersEquals(List<T> one, List<T> two) {
        boolean isEqual = true;

        if (isAllNull(one, two)) {
            isEqual = true;
        } else if (isAllNotNull(one, two)) {
            if (one.size() != two.size()) {
                isEqual = false;
            } else {
                for (int index = 0; index < one.size(); index++) {
                    T t1 = one.get(index);
                    T t2 = one.get(index);
                    if (!t1.getId().equals(t2.getId())) {
                        isEqual = false;
                        break;
                    }
                }
            }
        } else {
            isEqual = false;
        }

        return isEqual;
    }

    /*
     * 合并修饰符
     */
    public  int mergeModifiers(int one, int two) {
        return ModifierSet.addModifier(one, two);
    }

    /*
     * 合并注解声明
     */
    @SuppressWarnings("unchecked")
    public  <T> List<T> mergeListNoDuplicate(List<T> one, List<T> two) {

        if (isAllNull(one, two)) return null;

        List<T> result = new ArrayList<>();

        if (isAllNotNull(one, two)) {
            result.addAll(one);
            for (T t : two) {
                if (one.indexOf(t) == -1) {
                    result.add(t);
                }
            }
        } else {
            result.addAll(findFirstNotNull(one, two));
        }

        return result;
    }

    /*
     * 合并表达式集合
     */
    @SuppressWarnings("unchecked")
    public  <T> List<T> mergeListInOrder(List<T> one, List<T> two) {
        List<T> results = new ArrayList<>();

        if (isAllNull(one, two)) return null;

        if (isAllNotNull(one, two)) {

            int start = 0;
            for (int i = 0; i < one.size(); i++) {
                T t = one.get(i);
                int index = indexOf(start, two, t);
                if (index == -1 || index == start) {
                    results.add(t);
                    start += 1;
                } else {

                    results.addAll(two.subList(start, ++index));
                    start = index;
                }
            }

            if (start < two.size()) {
                results.addAll(two.subList(start, two.size()));
            }

        } else {
            results.addAll(findFirstNotNull(one, two));
        }

        return results;
    }

    public static <T> AbstractMerger getMerger(Class<T> clazz){
        return map.get(clazz);
    }

    protected static <T> void register(Class<T> clazz,AbstractMerger<T> abstractMerger){
        map.put(clazz,abstractMerger);
    }


    @SuppressWarnings("unchecked")
    protected static <T extends Node> List<T> mergeCollcetions(List<T> first, List<T> second){

        if(first == null) return second;
        if(second == null) return first;

        List<T> nodes = new ArrayList<>();

        List<T> copies = new ArrayList<>();
        copies.addAll(second);

        for(T node : first){

            AbstractMerger<T> merger = getMerger(node.getClass());

            T found = null;

            for(T anotherNode : second){
                if(node.getClass().equals(anotherNode.getClass())){
                    if (merger.isEquals(node,anotherNode)) {
                        found = anotherNode;
                        break;
                    }
                }
            }

            if(found != null){
                nodes.add(merger.merge(node, found));
                copies.remove(found);
            }else {
                nodes.add(node);
            }

        }

        if(!copies.isEmpty()){
            nodes.addAll(copies);
        }

        return nodes;
    }


    @SuppressWarnings("unchecked")
    protected static <T extends Node> T mergeSingle(T first, T second){

        /**
         * ensure the parameter passed to the merge is either not null
         */
        if(first == null) return second;
        if(second == null) return first;

        AbstractMerger<T> merger = getMerger(first.getClass());

        if(merger.isEquals(first,second)){
            return merger.merge(first, second);
        }

        return null;
    }

    public abstract M merge(M first, M second);

    public abstract boolean isEquals(M first, M second);
}
