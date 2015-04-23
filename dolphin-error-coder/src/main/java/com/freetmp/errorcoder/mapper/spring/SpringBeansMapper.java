package com.freetmp.errorcoder.mapper.spring;

import com.freetmp.errorcoder.annotation.ErrorCodeMapper;
import com.freetmp.errorcoder.annotation.LoadOnClassExist;
import com.freetmp.errorcoder.base.ErrorCode;
import com.freetmp.errorcoder.base.ErrorType;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.PropertyBatchUpdateException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * Created by LiuPin on 2015/4/22.
 */
@LoadOnClassExist(BeanFactory.class)
public class SpringBeansMapper {

    @ErrorCodeMapper(type = ErrorType.INTERNAL_ERROR)
    public ErrorCode beans(BeansException exception, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper(type = ErrorType.INTERNAL_ERROR)
    public ErrorCode bnorte(BeanNotOfRequiredTypeException exception, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper(type = ErrorType.INTERNAL_ERROR)
    public ErrorCode fbe(FatalBeanException exception, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper(type = ErrorType.INTERNAL_ERROR)
    public ErrorCode nbde(NoSuchBeanDefinitionException exception, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper(type = ErrorType.INTERNAL_ERROR)
    public ErrorCode pae(PropertyAccessException exception, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper(type = ErrorType.INTERNAL_ERROR)
    public ErrorCode pbue(PropertyBatchUpdateException exception,ErrorCode code){
        return code;
    }
}
