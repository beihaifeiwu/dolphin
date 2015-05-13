package com.freetmp.mbg.merge

import spock.lang.Specification

/**
 * Created by LiuPin on 2015/5/13.
 */
class CommentMergeSpec extends Specification {

    def "One line comments merge"(){
        expect:
            CompilationUnitMerger.merge(first,second) == result
        where:
            first << [
"""
package com.freetmp.mbg.comments;

public class ClassWithLineComments {

    public void aMethod(){
        int a=0; // second comment
        // third comment
        // fourth comment
    }
}
"""
            ]

            second << [
"""
package com.freetmp.mbg.comments;

public class ClassWithLineComments {

    public void aMethod(){
        // first comment
        int a=0; // second comment
        // third comment
    }
}
"""
            ]

            result << [
"""
package com.freetmp.mbg.comments;

public class ClassWithLineComments {

    public void aMethod(){
        // first comment
        int a=0; // second comment
        // third comment
        // fourth comment
    }
}
"""
            ]
    }
}
