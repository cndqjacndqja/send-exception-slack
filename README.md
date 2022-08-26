# send-exception-slack

## 왜 만들게 되었는가?

현재 공책 프로젝트에서는 예외를 모두 `ControllerAdvice` 에서 처리해 주고 있다. 이와 관련해서 로깅도 하고 있지만, 빠르게 대응해야 할 예외 상황은 Slack 알림으로 받기 위해 maricn 라이브러리를 사용했었다.

당시 빠르게 slack 예외 알림 기능을 구현하려는 상황에서, 이미 구현된 라이브러리가 있고 검색 했을 당시에 많은 개발자가 사용하고 있어서 급하게 도입하게 됐다.

### **하지만 [코드](https://github.com/maricn/logback-slack-appender/blob/master/src/main/java/com/github/maricn/logback/SlackAppender.java)를 봤을 때 위험하다고 느꼈던 부분은 다음과 같다.**

1. 꼭 logging 레벨에 따라서 slack 알림을 보낼 수 있게 구현되어 있다. (만약 로그 레벨은 같지만, 특정 exception에 대해서만 알림을 보내는 상황은 고려되어 있지 않다.)
2. logback과 slack 두 개의 외부 라이브러리에 의존하게 되는데, logback 혹은 slack API가 업데이트 된 후, 이전 버전을 쓸 수 없게 된다면 어떻게 될까? -> maricn이 바로 업데이트 되지 않는 이상 서비스 전체에 영향을 받는다.

----
### 사용 대상자

ControllerAdvice에서 예외를 처리하고 있고, 특정 예외 처리 메서드에 대해 Slack 알림으로 보내고 싶을 때 사용한다.

### 설정

```
repositories {
    ...
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.beomWhale:slack-send-exception:1.0.0'
}
```

----

### 사용법

적용하고자 하는 메서드에 `@SendSlackMessage`을 추가한다.

만약 request에 대한 detail 정보까지 보내고 싶지 않다면, detailRequest를 false로 설정한다. 

(ex. `@SendSlackMessage(detailRequest = false, slackUrl = "slackUrl...")`)

#### 코드 적용 예시
```java
@RestControllerAdvice
public class ControllerAdvice {

    @SendSlackMessage(slackUrl = "slackUrl...")
    @ExceptionHandler(CustomException.class)
    public String handleUnauthorized(final CustomException exception) {
        /*
        * ... */
    }
}
```
#### 결과 예시
<img width="440" alt="image" src="https://user-images.githubusercontent.com/48307960/186996789-0f838ea3-cb2a-4553-9258-f8351b0c7339.png">
