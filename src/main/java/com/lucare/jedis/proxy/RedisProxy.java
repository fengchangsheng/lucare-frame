package com.lucare.jedis.proxy;

import redis.clients.jedis.*;

import java.io.Closeable;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public interface RedisProxy extends JedisCommands, MultiKeyCommands, AdvancedJedisCommands, ScriptingCommands, BasicCommands, ClusterCommands, BinaryJedisCommands, MultiKeyBinaryCommands, AdvancedBinaryJedisCommands, BinaryScriptingCommands, Closeable, RedisProxyApi  {
}
