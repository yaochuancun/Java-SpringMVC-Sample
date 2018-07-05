package single.practice.dao.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import single.practice.utils.string.EncodeForLogUtil;

import java.text.Normalizer;
import java.util.*;

@Repository
public class RedisApplicationsDao {
    //日志记录对象
    private static final Logger logger = LoggerFactory.getLogger(RedisApplicationsDao.class);

    //redis连接池
    @Autowired
    public JedisPool jedisPool;


    public boolean mapSet(String key,Map<String,String> content){
        boolean response = false;
        String status;
        try {
            //获取redis操作对象
            Jedis jedis = jedisPool.getResource();
            try {
                status = jedis.hmset(key,content);
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("map set error");
            return response;
        }
        if("OK".equals(status)){
            response = true;
        }
        return response;
    }

    public boolean stringSet(String key,String value){
        boolean response = false;
        String status;
        try {
            //获取redis操作对象
            Jedis jedis = jedisPool.getResource();
            try {
                status = jedis.set(key, value);
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("string set error");
            return response;
        }
        if("OK".equals(status)){
            response = true;
        }
        return response;
    }

    public boolean mapSet(String key,String field ,String value){
        boolean response = false;
        long  status;
        try {
            //获取redis操作对象
            Jedis jedis = jedisPool.getResource();
            try {
                status = jedis.hset(key,field,value);
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("map set error");
            return response;
        }
        if(status> 0){
            response = true;
        }
        return response;
    }

    public boolean mapIncr(String key,String field){
        try {
            //获取redis操作对象
            Jedis jedis = jedisPool.getResource();
            try {
                jedis.hincrBy(key,field,1);
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("map inc error");
            return false;
        }
        return true;

    }

    public boolean mapDecr(String key,String field){
        try {
            //获取redis操作对象
            Jedis jedis = jedisPool.getResource();
            try {
                jedis.hincrBy(key,field,-1);
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("map inc error");
            return false;
        }
        return true;

    }

    public boolean setAdd(String key, String value){
        long flag;
        try {
            //获取redis操作对象
            Jedis jedis = jedisPool.getResource();
            try {
                flag = jedis.sadd(key,value);
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("set add error");
            return false;
        }
        if(flag>0){
            return true;
        }
        return false;
    }

    public boolean setdel(String key,String value){
        long flag;
        try {
            //获取redis操作对象
            Jedis jedis = jedisPool.getResource();
            try {
                flag = jedis.srem(key,value);
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("set add error");
            return false;
        }
        if(flag>0){
            return true;
        }
        return false;
    }

    public Set<String> setKeyGet(String key){
        Set<String> response;
        try {
            //获取redis操作对象
            Jedis jedis = jedisPool.getResource();
            try {
                response = jedis.smembers(key);
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("get key error");
            return null;
        }
        return response;
    }




    public boolean mapDel(String key,String field){
        boolean response = false;
        Long status;
        try {
            //获取redis操作对象
            Jedis jedis = jedisPool.getResource();
            try {
                status = jedis.hdel(key,field);
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("map del error");
            return response;
        }
        if(status>0){
            response = true;
        }
        return response;
    }

    public boolean del(String key){
        boolean response = false;
        Long status;
        try {
            //获取redis操作对象
            Jedis jedis = jedisPool.getResource();
            try {
                status = jedis.del(key);
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("map del error");
            return response;
        }
        if(status>0){
            response = true;
        }
        return response;
    }

    public String mapGet(String key,String field){
        List<String> response;
        try {
            //获取redis操作对象
            Jedis jedis = jedisPool.getResource();
            try {
                response = jedis.hmget(key,field);
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("map get error");
            return null;
        }
//        if(response.get(0).equals()){
//            response = true;
//        }
        return response.get(0);
    }

    public Set<String> HashKeys(String taskKey){
        Set<String> response = new HashSet<>();
        try {
            //获取redis操作对象
            Jedis jedis = jedisPool.getResource();
            try {
                response = jedis.hkeys(taskKey);
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("map set error");
            return response;
        }
        return response;
    }

    public boolean pushList(String key,String id){
        logger.info("pushList key:" + key + ",id:" + id);
        boolean response = false;
        try {
            //获取redis操作对象
            Jedis jedis = jedisPool.getResource();
            try {
                logger.info("exec restore to task_queue,return:"+jedis.rpush(key,id));
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("map set error");
            return response;
        }
        response = true;
        return response;
    }

    public  String popList(String key){
        String response = null;
        try {
            //获取redis操作对象
            Jedis jedis = jedisPool.getResource();
            try {
                response = jedis.lpop(key);
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("map set error");
            return response;
        }
        return response;
    }

    public boolean expire(String key ,int second){
        long result;
        boolean response = false;
        try {
            //获取redis操作对象
            Jedis jedis = jedisPool.getResource();
            try {
                result = jedis.expire(key,second);
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("map set error");
            return response;
        }
        if(result>0){
            response = true;
        }
        return response;
    }

    public boolean keyExists(String key){
        boolean response = false;
        try {
            //获取redis操作对象
            Jedis jedis = jedisPool.getResource();
            try {
                response = jedis.exists(key);
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("map set error");
            return response;
        }
        return response;
    }

    public boolean listDel(String key, String value) {
        Long response = 0L;
        try {
            //获取redis操作对象
            Jedis jedis = jedisPool.getResource();
            try {
                response = jedis.lrem(key,1,value);
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("map set error");
            return false;
        }
        if(response == 1L){
            return true;
        }else{
            logger.info("listDel failed,key:" + EncodeForLogUtil.handleLogForging(Normalizer.normalize(key, Normalizer.Form.NFKC)) + ", value:" + EncodeForLogUtil.handleLogForging(Normalizer.normalize(value, Normalizer.Form.NFKC)));
            return false;
        }
    }

    public int incrKillLock(String key, String filed) {
        int response = 0;
        Jedis jedis = null;
        try {
            //获取redis操作对象
            jedis = jedisPool.getResource();
            try {
                response = jedis.hincrBy(key,filed,1).intValue();
            } finally {
                //关闭连接
                jedis.close();
            }
        } catch (Exception e) {
            //记录错误日志
            logger.error("map set error");
            try {
                jedis.del(key);
            }catch (Exception e1){
                return 0;
            }
            return 0;
        }
        return response;
    }
}
