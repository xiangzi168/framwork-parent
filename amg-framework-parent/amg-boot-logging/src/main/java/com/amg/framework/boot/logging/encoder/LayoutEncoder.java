package com.amg.framework.boot.logging.encoder;

import ch.qos.logback.core.Layout;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import java.nio.charset.Charset;


public class LayoutEncoder<E> extends ContextAwareBase implements LifeCycle {

    private Layout<E> layout;

    private Charset charset;

    private boolean started = false;

    private static final Charset UTF8 = Charset.forName("UTF-8");


    public String doEncode(E event) {
        return this.layout.doLayout(event);
    }


    @Override
    public void start() {
        if (charset == null) {
            addInfo("no set charset, set the default charset is utf-8");
            charset = UTF8;
        }
        started = true;
    }

    @Override
    public void stop() {
        started = false;
    }

    @Override
    public boolean isStarted() {
        return this.started;
    }


    public Layout<E> getLayout() {
        return layout;
    }

    public void setLayout(Layout<E> layout) {
        this.layout = layout;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

}
