package com.liuchuang.configurer;

import com.liuchuang.model.core.entity.User;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;

import java.util.Base64;

/**
 *  重写自定义密码
 */
public class CredentialsMatcher extends SimpleCredentialsMatcher {
    /***
     *   要重写的方法
     * @param token
     * @param info
     * @return
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        //用户前台请求输入的密码账户
        String tokenCredentials = (String)this.getCredentials(token);
        String accountCredentials = (String)this.getCredentials(info);
        //可以添加 md5和其他的加密方式
        return this.equals(tokenCredentials, accountCredentials);
    }
}
