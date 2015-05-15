package com.freetmp.mbg.merge

import spock.lang.Specification

/**
 * Created by LiuPin on 2015/5/15.
 */
class InnerClassMergeSpec extends Specification {

    def "Add new InnerClass for Class"(){
        expect:
        CompilationUnitMerger.merge(first, second).trim() == result.trim()
        where:
        first = """
public class A {
    public void foo(){}

    public class B {
        public B(){}
    }
}
"""
        second = """
public class A {
    public void foo(){}
}
"""
        result = """
public class A {

    public void foo() {
    }

    public class B {

        public B() {
        }
    }
}
"""
    }

    def "Add more InnerClasses for Class"(){
        expect:
        CompilationUnitMerger.merge(first, second).trim() == result.trim()
        where:
        first = """
public class A {
    public void foo(){}

    public class B {
        public B(){}
    }
}
"""
        second = """
public class A {
    public void foo(){}

    public class C {
        public C(){}
    }
}
"""
        result = """
public class A {

    public void foo() {
    }

    public class B {

        public B() {
        }
    }

    public class C {

        public C() {
        }
    }
}
"""
    }
}
