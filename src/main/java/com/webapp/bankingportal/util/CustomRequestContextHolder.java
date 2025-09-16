package com.webapp.bankingportal.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.micrometer.common.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;

public class CustomRequestContextHolder {

    private static final ThreadLocal<RequestAttributes> transmittableAttributesHolder = new TransmittableThreadLocal<>();

    public static void resetRequestAttributes() {
        transmittableAttributesHolder.remove();
    }

    public static void setRequestAttribute(@Nullable RequestAttributes attributes) {
        if (attributes == null) resetRequestAttributes();
        else{
            transmittableAttributesHolder.set(attributes);
        }
    }

    @Nullable
    public static RequestAttributes getRequestAttributes() {
        return transmittableAttributesHolder.get();
    }

}
