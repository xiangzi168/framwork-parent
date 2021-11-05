package net.devh.boot.grpc.client.inject;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import io.grpc.ClientInterceptor;
import net.devh.boot.grpc.client.config.GrpcChannelProperties;
import net.devh.boot.grpc.client.config.GrpcChannelProperties.Security;



@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface GrpcClient {

    /**
     * The name of the grpc client. This name will be used to get the {@link GrpcChannelProperties config options} for
     * this client.
     *
     * <p>
     * <b>Example:</b> <code>@GrpcClient("myClient")</code> &lt;-&gt;
     * {@code grpc.client.myClient.address=static://localhost:9090}
     * </p>
     *
     * <p>
     * <b>Note:</b> This value might also be used to check the common / alternative names in server certificate, you can
     * overwrite this value with the {@link Security security.authorityOverride} property.
     * </p>
     *
     * @return The name of the grpc client.
     */
    String value();

    /**
     * A list of {@link ClientInterceptor} classes that should be used with this client in addition to the globally
     * defined ones. If a bean of the given type exists, it will be used; otherwise a new instance of that class will be
     * created via no-args constructor.
     *
     * <p>
     * <b>Note:</b> Please read the javadocs regarding the ordering of interceptors.
     * </p>
     *
     * @return A list of ClientInterceptor classes that should be used.
     */
    Class<? extends ClientInterceptor>[] interceptors() default {};

    /**
     * A list of {@link ClientInterceptor} beans that should be used with this client in addition to the globally
     * defined ones.
     *
     * <p>
     * <b>Note:</b> Please read the javadocs regarding the ordering of interceptors.
     * </p>
     *
     * @return A list of ClientInterceptor beans that should be used.
     */
    String[] interceptorNames() default {};

    /**
     * Whether the custom interceptors should be mixed with the global interceptors and sorted afterwards. Use this
     * option if you want to add a custom interceptor between global interceptors.
     *
     * @return True, if the custom interceptors should be merged with the global ones and sorted afterwards. False
     *         otherwise.
     */
    boolean sortInterceptors() default false;


    String fallback() default "";

}
