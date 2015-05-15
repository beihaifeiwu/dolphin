package com.freetmp.mbg.merge

import spock.lang.Specification

/**
 * Created by LiuPin on 2015/5/13.
 */
class CommentMergeSpec extends Specification {

    def "One line comments merge"() {
        expect:
        CompilationUnitMerger.merge(first, second).trim() == result.trim()
        where:
        first = """
package com.freetmp.mbg.comments;

public class ClassWithLineComments {

    public void aMethod(){
        int a=0; // second comment
        // third comment
        // fourth comment
    }
}
"""

        second = """
package com.freetmp.mbg.comments;

public class ClassWithLineComments {

    public void aMethod(){
        // first comment
        int a=0; // second comment
        // third comment
    }
}
"""
        result = """
package com.freetmp.mbg.comments;

public class ClassWithLineComments {

    public void aMethod() {
        // second comment
        int a = 0;
    }
}
"""
    }

    def "Combine comments merge"() {
        expect:
        CompilationUnitMerger.merge(first, second).trim() == result.trim()
        where:
        first = """
package com.freetmp.mbg.comments;

/**Javadoc associated with the class*/
public class ClassWithOrphanComments {

    //comment associated to the method
    void foo(){
        /*comment floating inside the method*/
    }

    //a second comment floating in the class
}

//Orphan comment inside the CompilationUnit
"""
        second = """
package com.freetmp.mbg.comments;

public class ClassWithOrphanComments {
    //a first comment floating in the class

    //comment associated to the method
    void foo(){
        /*comment floating inside the method*/
    }

    //a second comment floating in the class
}

//Orphan comment inside the CompilationUnit
"""
        result = """
package com.freetmp.mbg.comments;

/**Javadoc associated with the class*/
public class ClassWithOrphanComments {

    //comment associated to the method
    void foo() {
    }
    //a first comment floating in the class
    //a second comment floating in the class
}
//Orphan comment inside the CompilationUnit
"""
    }

    def "Orphan Comment in Class Declaration"() {
        expect:
            CompilationUnitMerger.merge(first, second).trim() == result.trim()
        where:
        first = """
class /*Comment1*/ A {
    //comment2
    // comment3
    int a;
    /**comment4
    *
    * */
    //comment5
}
"""
        second = """
class A {
    //comment2
    // comment3
    int a;

    //comment5
}
"""
        result = """
class A {

    // comment3
    int a;
    /*Comment1*/
    //comment2
    /**comment4
    *
    * */
    //comment5
}
"""
    }
}
