package com.xiaomi.infra.hchecker.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.Vector;

/**
 *数据库连接池
 *
 * @author Administrator
 *
 */
public class DbConnectionPool {

    private String username;// 连接数据库的用户名

    private String password;// 连接数据库的密码

    private String driver;// 连接数据库的驱动

    private String url;// 连接数据库的url

    private Vector<Connection> pool;// 连接池，用来存放连接的

    private int poolSize = 100; // 连接池的大小 ，换句话说就是连接池中(pool)存入连接的数量

    private static DbConnectionPool instance = null;

    /**
     * 构造方法私有化，防止用户通过new在外部创建对象
     */
    private DbConnectionPool() {

        init();
    }

    /**
     * 提供返回本类实例的一个接口
     */
    public static synchronized DbConnectionPool getInstance() {
        if (null == instance) {
            instance = new DbConnectionPool();
        }
        return instance;
    }

    /**
     * 初始化连接池
     */
    private void init() {
        pool = new Vector<Connection>();
        readConfig();
        addConnection();
    }

    /**
     * 向连接池添加连接
     */
    private void addConnection() {
        try {
            Class.forName(this.driver);// 加载数据库驱动
            Connection conn = null;
            for (int i = 0; i < poolSize; i++) {
                conn = DriverManager.getConnection(url, username, password);
                pool.add(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从连接池中取出一个连接
     */
  /*  public synchronized Connection getConnection() {
        if (pool.size() > 0) {
            Connection conn = pool.get(0);
            pool.remove(conn);// 将获取的连接从连接池中暂时移除
            return conn;
        } else {
            return null;
        }
    }*/

    /**
     * 从连接池中取出一个连接
     */
    public synchronized Connection getConnection() {
        while(true){
            if (pool.size() > 0) {
                Connection conn = pool.get(0);
                pool.remove(conn);// 将获取的连接从连接池中暂时移除
                return conn;
            } else {
                try {
                    wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 将连接放回连接池
     */
    public synchronized void relase(Connection conn) {
        pool.add(conn);
        notify();
    }

    /**
     * 读取配置文件
     */
    private void readConfig() {
        try {
            Properties prop = new Properties();// 用来读取属性文件

            InputStream is = this.getClass().getClassLoader()
                    .getResourceAsStream("pool.properties");

			/*
			 * InputStream is = ClassLoader
			 * .getSystemResourceAsStream("pool.properties");
			 */
            prop.load(is);

            this.username = prop.getProperty("username");
            this.password = prop.getProperty("password");
            this.driver = prop.getProperty("driver");
            this.url = prop.getProperty("url");
            this.poolSize = Integer.parseInt(prop.getProperty("poolSize"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
