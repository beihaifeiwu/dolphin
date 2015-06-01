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

  def "License Comment above package"(){
    expect:
    CompilationUnitMerger.merge(first, second).trim() == result.trim()
    where:
    first =
"""
/**
 * Copyright 2015-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.freetmp.xmbg.test.entity;
class A {}
"""
    second =
        """
package com.freetmp.xmbg.test.entity;
class A {}
"""
    result =
        """
/**
 * Copyright 2015-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.freetmp.xmbg.test.entity;

class A {
}
"""
  }

}
