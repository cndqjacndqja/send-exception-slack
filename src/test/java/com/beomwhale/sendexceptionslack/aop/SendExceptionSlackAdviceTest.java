package com.beomwhale.sendexceptionslack.aop;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beomwhale.sendexceptionslack.filter.RequestContext;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.web.util.ContentCachingRequestWrapper;


class SendExceptionSlackAdviceTest {

    private SlackApi slackApi;
    private RequestContext requestContext;

    static class SendExceptionSlackAdviceExecution {
        @SendSlackMessage(slackUrl = "insert here slackUrl")
        void executeSendExceptionSlackAdvice() {}
    }

    @BeforeEach
    void setUp() {
        slackApi = mock(SlackApi.class);
        requestContext = mock(RequestContext.class);
    }

    @Test
    @DisplayName("@SendSlackMessage을 선언하면 SendExceptionSlackAdvice가 실행된다.")
    void sendExceptionSlackAdvice() throws IOException {
        // given
        SendExceptionSlackAdviceExecution target = new SendExceptionSlackAdviceExecution();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SendExceptionSlackAdvice sendExceptionSlackAdvice = new SendExceptionSlackAdvice(requestContext, slackApi);
        factory.addAspect(sendExceptionSlackAdvice);
        SendExceptionSlackAdviceExecution sendAdviceExecution = factory.getProxy();
        ContentCachingRequestWrapper requestWrapper = mock(ContentCachingRequestWrapper.class);
        when(requestContext.getRequest()).thenReturn(requestWrapper);

        // when
        sendAdviceExecution.executeSendExceptionSlackAdvice();

        // then
        verify(slackApi).sendMessage(any(), any());
        verify(requestContext).getRequest();
    }
}
