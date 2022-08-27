package com.beomwhale.sendexceptionslack.aop;

import static com.beomwhale.sendexceptionslack.support.ExceptionMessageGenerator.generate;

import com.beomwhale.sendexceptionslack.filter.RequestContext;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;

@Aspect
public class SendExceptionSlackAdvice {

    private final RequestContext requestContext;
    private final SlackApi slackApi;

    public SendExceptionSlackAdvice(final RequestContext requestContext, final SlackApi slackApi) {
        this.requestContext = requestContext;
        this.slackApi = slackApi;
    }

    @Async
    @After("@annotation(com.beomwhale.sendexceptionslack.aop.SendSlackMessage)")
    public void sendMessage(JoinPoint joinPoint) throws IOException {
        String exceptionMessage = createExceptionMessage(joinPoint);
        slackApi.sendMessage(getSlackUrl(joinPoint), exceptionMessage);
    }

    private String createExceptionMessage(final JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String exceptionMessage = getExceptionMessage(args);

        if (checkDetailRequest(joinPoint)) {
            exceptionMessage = generate(requestContext.getRequest(), exceptionMessage);
        }
        return exceptionMessage;
    }

    private String getExceptionMessage(Object[] objects) {
        return Arrays.stream(objects)
                .filter(i -> i instanceof Exception)
                .map(i -> ((Exception) i).getMessage())
                .findAny()
                .orElse("예외 메세지를 찾을 수 없습니다.");
    }

    private boolean checkDetailRequest(final JoinPoint joinPoint) {
        SendSlackMessage annotation = getAnnotation(joinPoint);
        return annotation.detailRequest();
    }

    private String getSlackUrl(final JoinPoint joinPoint) {
        SendSlackMessage annotation = getAnnotation(joinPoint);
        return annotation.slackUrl();
    }

    private SendSlackMessage getAnnotation(final JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(SendSlackMessage.class);
    }
}
