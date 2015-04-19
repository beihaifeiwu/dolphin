package com.freetmp.mbg.merge;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.TypeParameter;
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

    @SuppressWarnings("unchecked")
    public  <T extends BaseParameter> boolean isParametersEquals(List<T> one, List<T> two) {

        if(one == two) return true;
        if(one == null || two == null) return false;

        if(one.size() != two.size()) return false;

        for(int i = 0; i < one.size(); i++){

            T o = one.get(i);
            T t = two.get(i);

            AbstractMerger merger = getMerger(o.getClass());
            if(!merger.isEquals(o,t)) return false;
        }

        return true;
    }

    public boolean isTypeParameterEquals(List<TypeParameter> first, List<TypeParameter> second){

        if(first == second) return true;
        if(first == null || second == null) return false;

        if(first.size() != second.size()) return false;

        for(int i = 0; i < first.size(); i++){
            AbstractMerger<TypeParameter> merger = getMerger(TypeParameter.class);
            if(!merger.isEquals(first.get(i),second.get(i))) return false;
        }

        return true;
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

    /**
     * first check if mapper of the type T exist, if existed return it
     * else check if mapper of the supper type exist, then return it
     * ...
     */
    @SuppressWarnings("unchecked")
    public static <T extends Node> AbstractMerger<T> getMerger(Class<T> clazz){

        AbstractMerger<T> merger = null;

        Class<?> type = clazz;

        while (merger == null && type != null){
            merger = map.get(type);
            type = type.getSuperclass();
        }

        return merger;
    }

    protected static <T> void register(Class<T> clazz,AbstractMerger<T> abstractMerger){
        map.put(clazz,abstractMerger);
    }


    @SuppressWarnings("unchecked")
    protected <T extends Node> List<T> mergeCollections(List<T> first, List<T> second){

        if(first == null) return second;
        if(second == null) return first;

        List<T> nodes = new ArrayList<>();

        List<T> copies = new ArrayList<>();
        copies.addAll(second);

        for(T node : first){

            AbstractMerger merger = getMerger(node.getClass());

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
                nodes.add((T) merger.merge(node, found));
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
    protected <T extends Node> List<T> mergeCollectionsInOrder(List<T> first,List<T> second){
        if(first == null) return second;
        if(second == null) return first;

        List<T> nodes = new ArrayList<>();

        int max = Math.max(first.size(),second.size());
        for(int i = 0; i < max; i++){
            T f = i < first.size() ? first.get(i) : null;
            T s = i < second.size() ? second.get(i) : null;
            if(isAllNotNull(f,s)){

                AbstractMerger merger = getMerger(f.getClass());
                nodes.add((T) merger.merge(f,s));

            }else {
                nodes.add(f != null ? f : s);
            }
        }

        return nodes;
    }


    @SuppressWarnings("unchecked")
    protected <T extends Node> T mergeSingle(T first, T second){

        /**
         * ensure the parameter passed to the merge is either not null
         */
        if(first == null) return second;
        if(second == null) return first;

        AbstractMerger merger = getMerger(first.getClass());

        if(merger.isEquals(first,second)){
            return (T) merger.merge(first, second);
        }

        return null;
    }

    public abstract M merge(M first, M second);

    public abstract boolean isEquals(M first, M second);
}
