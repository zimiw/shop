var ioc = {
    dataSource : {
        type : "com.alibaba.druid.pool.DruidDataSource",
        events : {
            create : "init",
            depose : 'close'
        },
        fields : {
            //url : "jdbc:mysql://rdsi1w39xy2x5qp1r219.mysql.rds.aliyuncs.com:3306/easyshop",
            //username : "db",
            //password : "easyshop_123456",
            url : "jdbc:mysql://localhost:3306/easyshop",
            username : "root",
            password : "root123",
            testWhileIdle : true,
            validationQuery : "select 1" ,
            maxActive : 100
        }
    },
    dao : {
        type : "org.nutz.dao.impl.NutDao",
        args : [{refer:"dataSource"}]
    },
    // 直接初始化Ehcache,默认找ehcache.xml文件哦
    cacheManager : {
        type : "net.sf.ehcache.CacheManager",
        factory : "net.sf.ehcache.CacheManager#create"
    }
    /* // 与shiro共享一个ehcache示例的方式
    cacheManager : {
        type : "net.sf.ehcache.CacheManager",
        factory : "net.sf.ehcache.CacheManager#getCacheManager",
        args : ["nutzbook"] // 对应shiro.ini中指定的ehcache.xml中定义的name
    }
     */
};