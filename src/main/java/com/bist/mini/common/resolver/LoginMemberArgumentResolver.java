package com.bist.mini.common.resolver;

import com.bist.mini.common.annotation.LoginMember;
import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.common.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class LoginMemberArgumentResolver implements 
        org.springframework.web.method.support.HandlerMethodArgumentResolver,
        org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class) &&
               parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String authorization = request.getHeader("Authorization");
        return resolve(parameter, authorization);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter, @NonNull Message<?> message) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        String authorization = accessor.getFirstNativeHeader("Authorization");
        return resolve(parameter, authorization);
    }

    private Object resolve(MethodParameter parameter, String authorization) {
        LoginMember annotation = parameter.getParameterAnnotation(LoginMember.class);
        boolean isRequired = annotation != null && annotation.required();

        if (authorization == null || authorization.isEmpty()) {
            if (isRequired) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }
            return null;
        }

        try {
            return jwtProvider.getMemberIdFromToken(authorization);
        } catch (CustomException e) {
            if (isRequired) {
                throw e;
            }
            return null;
        } catch (Exception e) {
            if (isRequired) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }
            return null;
        }
    }
}
