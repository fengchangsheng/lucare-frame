package com.lucare.tsession;


import com.lucare.common.utils.CommonUtils;
import com.lucare.common.utils.CryptUtils;
import com.lucare.jedis.RedisProxyFactory;
import com.lucare.jedis.proxy.RedisProxy;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class TSessionManager {
    private static String session_md5factor = "www.zhongbin.org";
    private static final String sessionkey_prefix = "sessionkey_prefix_";
    private static final int unixtime_length = 10;

    public TSessionManager() {
    }

    public static TSession newTSession(int userid, String userAgent) {
        return newTSession(userid, getUnixtimeOfNow(), userAgent);
    }

    public static TSession newTSession(int userid, long loginTime, String userAgent) {
        String sid = encodeSID(userid, loginTime);
        TSession t = new TSession(sid, userid, loginTime, userAgent);
        t.setKey("sessionkey_prefix_" + userid);
        t.setExpireSecond(7200);
        return t;
    }

    public static void save(TSession session) {
        if(session != null && session.getUserid() > 0) {
            RedisProxy proxy = RedisProxyFactory.getProxy();
            proxy.putObjectExpire(session.getKey(), session, session.getExpireSecond());
        } else {
            throw new IllegalArgumentException("TSession=" + session);
        }
    }

    public static TSession get(int userid) {
        String key = getTessionRedisKey(userid);
        return (TSession)RedisProxyFactory.getProxy().getObject(key, TSession.class);
    }

    public static TSession get(String sid) {
        String key = getTessionRedisKey(sid);
        TSession ts = null;
        if(!CommonUtils.isEmpty(key)) {
            RedisProxy proxy = RedisProxyFactory.getProxy();
            ts = (TSession)proxy.getObject(key, TSession.class);
            if(ts != null && ts.getSid() != null && !ts.getSid().equals(sid)) {
                ts = null;
            }
        }

        return ts;
    }

    public static void remove(String sid) {
        String key = getTessionRedisKey(sid);
        if(key != null) {
            RedisProxy proxy = RedisProxyFactory.getProxy();
            proxy.del(key);
        }

    }

    public static int decodeUserid(String sid) {
        if(!CommonUtils.isEmpty(sid) && !sid.contains("-")) {
            int point = sid.length() - 10 - 1;
            int len = sid.charAt(point) - 48;
            int begin = point - len;
            if(len >= 0 && begin >= 0) {
                String line = sid.substring(begin, begin + len);
                return CommonUtils.parseInt(line);
            }
        }

        return 0;
    }

    public static long decodeLoginTime(String sid) {
        if(!CommonUtils.isEmpty(sid)) {
            String time = sid.substring(sid.length() - 10);
            return Long.valueOf(time).longValue();
        } else {
            return 0L;
        }
    }

    private static String getTessionRedisKey(String sid) {
        String key = null;
        int userid = decodeUserid(sid);
        if(userid != 0) {
            key = getTessionRedisKey(userid);
        }

        return key;
    }

    private static String getTessionRedisKey(int userid) {
        return "sessionkey_prefix_" + userid;
    }

    private static String encodeSID(int userid, long loginTime) {
        String input = session_md5factor + userid;
        String md5 = CryptUtils.md5(input).toUpperCase();
        String result = md5 + userid + String.valueOf(userid).length() + loginTime;
        return result;
    }

    private static long getUnixtimeOfNow() {
        return System.currentTimeMillis() / 1000L;
    }
}
