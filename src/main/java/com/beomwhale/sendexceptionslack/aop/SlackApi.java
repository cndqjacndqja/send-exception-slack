package com.beomwhale.sendexceptionslack.aop;

import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import java.io.IOException;

public class SlackApi {

    public void sendMessage(final String exceptionMessage, final String slackUrl) throws IOException {
        Slack slack = Slack.getInstance();
        Payload payload = Payload.builder()
                .text(exceptionMessage)
                .build();
        slack.send(slackUrl, payload);
    }
}
