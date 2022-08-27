package com.beomwhale.sendexceptionslack;

import com.beomwhale.sendexceptionslack.aop.SendExceptionSlackAdvice;
import com.beomwhale.sendexceptionslack.aop.SlackApi;
import com.beomwhale.sendexceptionslack.filter.RequestContext;
import com.beomwhale.sendexceptionslack.filter.RequestServletWrappingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class SpringConfig {

    @Bean
    public SendExceptionSlackAdvice sendExceptionSlackAdvice() {
        return new SendExceptionSlackAdvice(requestContext(), slackApi());
    }

    @Bean
    public RequestServletWrappingFilter requestServletWrappingFilter() {
        return new RequestServletWrappingFilter(requestContext());
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public RequestContext requestContext() {
        return new RequestContext();
    }

    @Bean
    public SlackApi slackApi() {
        return new SlackApi();
    }
}
