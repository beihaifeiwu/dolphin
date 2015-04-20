package com.freetmp.mbg.merge;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.BaseParameter;
import com.github.javaparser.ast.body.ModifierSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
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

    @SuppressWarnings("unchecked")
    public  <T> T mergeSelective(T one, T two) {
        T t = null;

        if (isAllNull(one, two)) {
            return t;
        }

        t = findFirstNotNull(one, two);

        return t;
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

    @SuppressWarnings("unchecked")
    public <T extends Node> boolean isSmallerHasEqualsInBigger(List<T> first, List<T> second, boolean useOrigin){

        if(first == second) return true;
        if(first == null || second == null) return true;

        List<T> smaller = null;
        List<T> bigger = null;

        if(first.size() > second.size()){
            smaller = second; bigger = first;
        }else {
            smaller = first; bigger = second;
        }

        for(T st : smaller){
            if(useOrigin){
                if(!bigger.contains(st)) return false;
            }else {
                AbstractMerger merger = getMerger(st.getClass());
                boolean found = false;
                for(T bt : bigger){
                    if(merger.isEquals(st,bt)){
                        found = true; break;
                    }
                }
                if(!found) return false;
            }
        }

        return true;
    }

    public  int mergeModifiers(int one, int two) {
        return ModifierSet.addModifier(one, two);
    }


    @SuppressWarnings("unchecked")
    public  <T extends Node> List<T> mergeListNoDuplicate(List<T> one, List<T> two, boolean useMerger) {

        if(one == two) return one;
        if(one == null) return two;
        if(two == null) return one;

        List<T> results = new ArrayList<>();

        if(useMerger){

            List<T> twoCopy = new ArrayList<>();
            Collections.copy(twoCopy,two);

            for(T ot : one){
                AbstractMerger merger = getMerger(ot.getClass());
                T found = null;
                for(T tt : twoCopy){
                    if(ot.getClass().equals(tt.getClass()) && merger.isEquals(ot,tt)){
                        found = tt; break;
                    }
                }
                if(found != null){
                    twoCopy.remove(found);
                    results.add((T) merger.merge(ot,found));
                }else {
                    results.add(ot);
                }
            }

            results.addAll(twoCopy);

        }else {
            TreeSet<T> treeSet = new TreeSet<>();
            treeSet.addAll(one);
            treeSet.addAll(two);
            results.addAll(treeSet);
        }

        return results;
    }

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

        if(first.getClass().equals(second.getClass())) {

            AbstractMerger merger = getMerger(first.getClass());

            if (merger.isEquals(first, second)) {
                return (T) merger.merge(first, second);
            }
        }else {
            //TODO have no idea what to do
        }

        return null;
    }

    public abstract M merge(M first, M second);

    public abstract boolean isEquals(M first, M second);
}
