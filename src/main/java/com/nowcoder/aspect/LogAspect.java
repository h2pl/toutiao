package com.nowcoder.aspect;

import com.nowcoder.controller.IndexController;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by 周杰伦 on 2018/5/2.
 */
@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* com.nowcoder.controller.IndexController.*(..))")
    public void before(JoinPoint joinPoint) {
        StringBuffer sb = new StringBuffer();
        for (Object arg : joinPoint.getArgs()) {
            sb.append("arg:" + arg.toString());
        }
        logger.info("before method:" + sb.toString());
    }

    @After("execution(* com.nowcoder.controller.*Controller.*(..))")
    public void after(JoinPoint joinPoint) {
        logger.info("after method:");
    }
}
