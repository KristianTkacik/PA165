package cz.muni.fi.pa165.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.StopWatch;

import javax.inject.Named;
import java.util.Arrays;

@Aspect
@Named
public class LoggingAspect {

    @Around("execution(public * *(..))")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch stopWatch = new StopWatch();

        System.err.println("Calling method " + pjp.getSignature().getName() +
                " with parameters " + Arrays.asList(pjp.getArgs()));

        stopWatch.start();
        Object returnValue = pjp.proceed();
        stopWatch.stop();
        System.err.println("Calling of method " + pjp.getSignature().getName() +
                " finished. Lasted: " + stopWatch.shortSummary());
        return returnValue;
    }
}
