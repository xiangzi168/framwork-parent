package ch.qos.logback.classic.spi;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoggingEventDecorator {

    private String requestId;

    private String traceStageId;

    private String traceStageParentId;

    private String traceMethodName;

    private ILoggingEvent loggingEvent;

}
