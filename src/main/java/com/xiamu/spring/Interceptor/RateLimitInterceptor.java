package com.xiamu.spring.Interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import com.xiamu.spring.annotation.AccessLimit;
import com.xiamu.spring.enumeration.LimitKeyTypeEnum;
import com.xiamu.spring.utils.RateLimitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.xiamu.spring.annotation.AccessLimit.CacheType.EXCLUSIVE_TYPE;

/**
 * @Author xianghui.luo
 * @Date 2025/2/7 14:08
 * @Description 请求拦截器
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitInterceptor.class);

    /**
     * 本地缓存
     * maximumSize 设置缓存个数
     * expireAfterWrite 写入后过期时间
     * 请求过来时根据限制类型key创建一个RateLimiter，1s内有多个同样的请求时，进行访问限制拒绝处理
     * 但是有随机性，不知道具体原因 比如同时有 10个请求过来，但是限制是1s 5个，那么可能只有其中5个请求会成功处理(但是是随机的，不是优先过来的请求就一定会成功处理，也不是一定只有5个请求会处理成功)
     */
    private static final LoadingCache<String, RateLimiter> SECONDS_TYPE_CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build(new CacheLoader<String, RateLimiter>() {
                @Override
                public RateLimiter load(String key) {
                    double perSecondLimit = RateLimitUtil.getCacheKeyPerSecond(key);
                    return RateLimiter.create(perSecondLimit);
                }
            });

    private static final ConcurrentHashMap<String, Object> EXCLUSIVE_TYPE_CACHE = new ConcurrentHashMap<>();

    /**
     * @Author xianghui.luo
     * @Date 2025/2/7 15:33
     * @Description 请求前置处理
     * @Param
     * @Return 
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
        if(accessLimit !=null){
            AccessLimit.CacheType cacheType = accessLimit.cacheType();
            String cacheKey=null;
            RateLimiter limiter =null;
            switch (cacheType){
                case EXCLUSIVE_TYPE: //特殊限制，限制某个时间段不能访问
                    if(StringUtils.isNotEmpty(accessLimit.startTime()) &&
                            StringUtils.isNotEmpty(accessLimit.endTime()) && !(
                            LocalTime.now().isAfter(LocalTime.parse(accessLimit.startTime(), DateTimeFormatter.ofPattern("HH:mm"))) &&
                                    LocalTime.now().isBefore( LocalTime.parse(accessLimit.endTime(), DateTimeFormatter.ofPattern("HH:mm"))))
                    ) {
                        doResult(response, handlerMethod, accessLimit, cacheKey);
                        return false;
                    }
                    cacheKey = RateLimitUtil.generateSimpleCacheKey(handlerMethod, request);
                    if (EXCLUSIVE_TYPE_CACHE.containsKey(cacheKey)) {
                        doResult(response, handlerMethod, accessLimit, cacheKey);
                        return false;
                    }else{
                        EXCLUSIVE_TYPE_CACHE.put(cacheKey,cacheKey);
                    }
                    break;
                case SECONDS_TYPE: //秒级限制
                    cacheKey = RateLimitUtil.generateCacheKey(handlerMethod, request);
                    limiter = SECONDS_TYPE_CACHE.get(cacheKey);//当cacheKey不存在时会触发load放入缓存中
                    if (!limiter.tryAcquire()) {//acquire 阻塞当前线程 tryAcquire不阻塞当前线程
                        //return ResponseEntity.builder().code(404).msg("访问速率过快").build();
                        doResult(response, handlerMethod, accessLimit, cacheKey);
                        return false;
                    }
                    break;
                case MINUTES_TYPE:
                    break;
            }


        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    /**
     * @Author xianghui.luo
     * @Date 2025/2/7 14:46
     * @Description 访问限制返回信息处理
     * @Param
     */
    private void doResult(HttpServletResponse response, HandlerMethod handlerMethod, AccessLimit accessLimit, String cacheKey) throws IOException {
        StringBuilder msg = new StringBuilder();
        if(accessLimit.limitKeyType()== LimitKeyTypeEnum.IPADDR
                || accessLimit.limitKeyType()== LimitKeyTypeEnum.IPADDRACCOUNT
        ){
            msg.append(cacheKey);
        }
        if (StringUtils.isNotEmpty(accessLimit.tipMsg())) {
            msg.append(" resource info: ").append(accessLimit.tipMsg());
        }
        Class<?> returnType = handlerMethod.getMethod().getReturnType();
        Object result= msg.toString();
        //if (returnType.isAssignableFrom(CommResponse.class)) {
        //    result=new CommResponse(false,msg.toString());
        //}else if(returnType.isAssignableFrom(QueryResponse.class)){
        //    result=new QueryResponse();
        //}else{
        //    result=new CommResponse(false,msg.toString());
        //}
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONUtil.toJsonStr(result));
        LOGGER.info(msg.toString());
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //System.out.println("拦截器: 请求处理后执行");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //System.out.println("拦截器: 请求完成后执行");
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
        if(accessLimit !=null) {
            AccessLimit.CacheType cacheType = accessLimit.cacheType();
            if (cacheType==EXCLUSIVE_TYPE){
                String cacheKey = RateLimitUtil.generateSimpleCacheKey(handlerMethod, request);
                EXCLUSIVE_TYPE_CACHE.remove(cacheKey);
            }
        }
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
