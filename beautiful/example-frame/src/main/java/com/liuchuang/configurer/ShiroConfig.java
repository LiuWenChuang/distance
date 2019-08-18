package com.liuchuang.configurer;

import com.liuchuang.configurer.filter.KickoutSessionControlFilter;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.crazycake.shiro.IRedisManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.io.Serializable;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro  配置类
 */
@Configuration
public class ShiroConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //配置登录请求的url
        shiroFilterFactoryBean.setLoginUrl("/common/login");
        //配置登录成功的跳转页面
        shiroFilterFactoryBean.setSuccessUrl("/auth/index");
        //配置跳转未授权的页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/common/unauth");

        //自定义的 filtes 放入  Filters 中
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("kickout", kickoutSessionControlFilter());
        shiroFilterFactoryBean.setFilters(filterMap);
        //拦截请求
        Map<String, String> stringObjectMap = new LinkedHashMap<>();
        // 公共请求
        stringObjectMap.put("/common/**", "anon");
        // 静态资源
        stringObjectMap.put("/static/**", "anon");
        // 静态资源
        stringObjectMap.put("/thymeleaf/**", "anon");
        // 登录方法 表示可以匿名访问
        stringObjectMap.put("/admin/login*", "anon");

        //此处需要添加一个kickout，上面添加的自定义拦截器才能生效 表示需要认证才可以访问
        stringObjectMap.put("/admin/**", "authc,kickout");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(stringObjectMap);

        return shiroFilterFactoryBean;
    }

    /**
     * 设置安全管理器
     * @return
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置  角色、权限
        securityManager.setRealm(myrealm());
        //设置 缓存管理
        securityManager.setCacheManager(cacheManager());
        //设置 使用redis
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

    /**
     * 设置  校验登录 获取权限的类
     *
     * @return
     */
    @Bean
    public ShiroRealm myrealm() {
        ShiroRealm shiroRealm = new ShiroRealm();
        //设置 自定义密码类
        shiroRealm.setCredentialsMatcher(credentialsMatcher());
        return shiroRealm;
    }

    /**
     * 加载 自定义密码类
     *
     * @return
     */
    @Bean
    public CredentialsMatcher credentialsMatcher() {
        return new CredentialsMatcher();
    }

    /**
     * 创建实现了  shiro  AccessControlFilter 的filter
     *
     * @return
     */
    @Bean
    public KickoutSessionControlFilter kickoutSessionControlFilter() {
        KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter();
        kickoutSessionControlFilter.setCache(cacheManager());
        //用于根据会话ID，获取会话进行踢出操作的；
        kickoutSessionControlFilter.setSessionManager(sessionManager());
        //是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；踢出顺序。
        kickoutSessionControlFilter.setKickoutAfter(false);
        //同一个用户最大的会话数，默认1；比如2的意思是同一个用户允许最多同时两个人登录；
        kickoutSessionControlFilter.setMaxSession(1);
        //被踢出后重定向到的地址；
        kickoutSessionControlFilter.setKickoutUrl("/kickout");
        return kickoutSessionControlFilter;
    }

    /**
     * Session Manager
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    @Bean
    public SessionManager sessionManager() {
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setPath("/");
        simpleCookie.setHttpOnly(false);
        SessionManager sessionManager = new SessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        sessionManager.setSessionIdCookieEnabled(false);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionIdCookie(simpleCookie);

        return null;
    }

    /**
     * RedisSessionDAO shiro sessionDao层的实现 通过redis
     * 使用的是shiro-redis开源插件
     */
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        redisSessionDAO.setKeyPrefix("SPRINGBOOT_SESSION:");
        return redisSessionDAO;
    }

    /**
     * 缓存配置 缓存 redis实现
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    @Bean
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        //设置 redisManager
        redisCacheManager.setRedisManager(redisManager());
        //设置前缀
        redisCacheManager.setKeyPrefix("SPRINGBOOT_CACHE:");
        return redisCacheManager;
    }


    /**
     * 设置 redis
     *
     * @return
     */
    private IRedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        //设置 域名ip
        redisManager.setHost(redisHost);
        //设置 端口
        redisManager.setPort(redisPort);
        //设置 密码
        redisManager.setPassword(redisPassword);
        //设置 过期时间
        redisManager.setTimeout(1800);
        return redisManager;
    }
}
