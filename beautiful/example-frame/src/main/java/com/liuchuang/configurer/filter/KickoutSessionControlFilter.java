package com.liuchuang.configurer.filter;

import com.alibaba.fastjson.JSON;
import com.liuchuang.model.core.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * 继承 shiro 的AccessControlFilter 实现 账户 顶替和下线功能
 *
 * @author LiuChuang
 */
@Setter
@Getter
public class KickoutSessionControlFilter extends AccessControlFilter {
    /***
     * 踢出后到的地址
     */
    private String kickoutUrl;
    /**
     * 踢出之前登录的/之后登录的用户 默认踢出之前登录的用户
     */
    private boolean kickoutAfter = false;
    /**
     * 同一个帐号最大会话数 默认1
     */

    private int maxSession = 1;
    /**
     * shiro 的 session 管理器 进行踢出操作
     */
    private SessionManager sessionManager;
    /**
     * 缓存管理
     */
    private Cache<String, Deque<Serializable>> cache;


    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        //获取 当前用户
        Subject subject = getSubject(servletRequest, servletResponse);
        //没有登录没有记得的直接放行
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            return true;
        }
        //获取链接的 session 从中获取 session 唯一id 并存入 缓存中
        Session session = subject.getSession();
        User user = (User) subject.getPrincipal();
        String username = user.getUsername();
        //获取 session 唯一id
        Serializable sessionId = session.getId();

        //读取缓存  是否存在 判定
        Deque<Serializable> serializables = cache.get(username);
        //如果队列里没有此sessionId，且用户没有被踢出；放入队列
        if (!serializables.contains(sessionId) && session.getAttribute("kickout") == null) {
            //将sessionId存入队列
            serializables.push(sessionId);
            //将用户的sessionId队列缓存
            cache.put(username, serializables);
        }

        //如果队列里的sessionId数超出最大会话数，开始踢人
        while (serializables.size() > maxSession) {
            //踢出的session编号
            Serializable kickoutSessionId = null;
            if (kickoutAfter) {
                //如果踢出后者
                kickoutSessionId = serializables.removeFirst();
            } else {
                //否则踢出前者
                kickoutSessionId = serializables.removeLast();
            }
            cache.put(username, serializables);

            //获取被踢出的sessionId的session对象
            Session managerSession = sessionManager.getSession(new DefaultSessionKey(kickoutSessionId));
            if (managerSession != null) {
                //设置会话的kickout属性表示踢出了
                managerSession.setAttribute("kickout", true);
            }
            //如果被踢出了，直接退出，重定向到踢出后的地址
            if ((Boolean) session.getAttribute("kickout") != null && (Boolean) session.getAttribute("kickout") == true) {
                //设定退出
                subject.logout();
                //将此请求 保留下来  ，登录后 重新 到之前请求的页面
                saveRequest(servletRequest);
                Map<String, String> resultMap = new HashMap<String, String>();

                //判断是不是Ajax请求
                if ("XMLHttpRequest".equalsIgnoreCase(((HttpServletResponse)servletResponse).getHeader("X-Requested-With"))) {
                    resultMap.put("user_status", "300");
                    resultMap.put("message", "您已经在其他地方登录，请重新登录！");
                    //输出json串
                    out(servletResponse, resultMap);
                }else{
                    //重定向
                    WebUtils.issueRedirect(servletRequest, servletResponse, kickoutUrl);
                }

                WebUtils.issueRedirect(servletRequest, servletResponse, kickoutUrl);
//                //将此请求重定向到登录界面
//                redirectToLogin(servletRequest,servletResponse);
                return false;
            }
        }
        return true;
    }

    private void out(ServletResponse hresponse, Map<String, String> resultMap)
            throws IOException {
        try {
            hresponse.setCharacterEncoding("UTF-8");
            PrintWriter out = hresponse.getWriter();
            out.println(JSON.toJSONString(resultMap));
            out.flush();
            out.close();
        } catch (Exception e) {
            System.err.println("KickoutSessionFilter.class 输出JSON异常，可以忽略。");
        }
    }

    public void setCache(CacheManager cacheManager) {
        this.cache = cacheManager.getCache("shiro_redis_cache");
    }



}
