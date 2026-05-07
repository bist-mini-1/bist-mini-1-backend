package com.bist.mini.common.resolver;

import com.bist.mini.common.annotation.LoginMember;
import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.common.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class) &&
               parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String authorization = request.getHeader("Authorization");
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
