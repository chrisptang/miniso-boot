package com.miniso.boot.autoconfiguration.xxljob;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class XxlJobCatIntegration {

    @Around("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
    public Object profileAllMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        //Get intercepted method details
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        String xxlJobName = "";
        XxlJob xxlJobAnnotation = methodSignature.getMethod().getAnnotation(XxlJob.class);
        if (null != xxlJobAnnotation) {
            xxlJobName = xxlJobAnnotation.value();
        }

        Transaction transaction = Cat.newTransaction("XxlJob", String.format("%s.%s() - %s", className, methodName, xxlJobName));
        Object result = null;
        try {
            result = proceedingJoinPoint.proceed();
            if (null != result && result instanceof ReturnT) {
                ReturnT<?> returnedObject = (ReturnT) result;
                if (returnedObject.getCode() == ReturnT.SUCCESS_CODE) {
                    transaction.setSuccessStatus();
                } else {
                    //失败的任务，记录返回的对象string；
                    transaction.setStatus(returnedObject.toString());
                }
            }
        } catch (Exception e) {
            transaction.setStatus(e);
            throw e;
        } finally {
            transaction.complete();
        }


        return result;
    }
}
