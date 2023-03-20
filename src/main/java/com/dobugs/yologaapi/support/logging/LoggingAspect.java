package com.dobugs.yologaapi.support.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class LoggingAspect {

    @Pointcut("@annotation(com.dobugs.yologaapi.support.logging.UnhandledException)")
    private void unhandledException() {
    }

    @Around("unhandledException()")
    public Object log(final ProceedingJoinPoint joinPoint) throws Throwable {
        final Object result = joinPoint.proceed();
        printStackTrace(joinPoint);
        return result;
    }

    private void printStackTrace(final ProceedingJoinPoint joinPoint) {
        final Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof final Exception exception) {
            exception.printStackTrace();
        }
    }
}
