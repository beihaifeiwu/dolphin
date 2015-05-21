package com.freetmp.mbg.merge

import spock.lang.Specification

/**
 * Created by LiuPin on 2015/5/15.
 */
class AnnotationMergeSpec extends Specification {

    def "Annotations on Field and Class"(){
        expect:
            CompilationUnitMerger.merge(first, second).trim() == result.trim()
        where:
        first = """
package com.freetmp.web.login.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

/**
 * Created by LiuPin on 2015/5/14.
 */
@Data @Builder
public class LoginForm {

  @NotNull String email;

  @NotNull String password;

  @NotNull String captcha;
}
"""
        second = """
package com.freetmp.web.login.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

/**
 * Created by LiuPin on 2015/5/14.
 */
@Data
public class LoginForm {

  @Email String email;

  @NotNull String password;

  @NotNull String captcha;
}
"""
       result = """
package com.freetmp.web.login.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * Created by LiuPin on 2015/5/14.
 */
@Data
@Builder
public class LoginForm {

    @NotNull
    @Email
    String email;

    @NotNull
    String password;

    @NotNull
    String captcha;
}
"""
    }

    def "Annotation on method and parameter"(){
        expect:
            CompilationUnitMerger.merge(first, second).trim() == result.trim()
        where:
        first = """
public class MethodWithAnnotation {

    public String method(@NunNull String arg){
        return "hello " + arg;
    }
}
"""
        second = """
public class MethodWithAnnotation {

    @SuppressWarnings("unchecked")
    public String method(String arg){
        return "hello " + arg;
    }
}
"""
        result = """
public class MethodWithAnnotation {

    @SuppressWarnings("unchecked")
    public String method(@NunNull String arg) {
        return "hello " + arg;
    }
}
"""
    }
}
