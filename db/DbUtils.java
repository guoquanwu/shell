package com.xiaomi.infra.hchecker.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库实用类，封装对数据库的原子操作 crud
 *
 * @author Administrator
 *
 */
public class DbUtils {

//    private Connection conn;
//
//    private Statement stat;

//    private ResultSet rs;

    private static DbUtils instance;

    /**
     * 构造 方法私有化
     */
    private DbUtils() {
    }

    /**
     * 返回本类的一个实例
     */
    public static synchronized DbUtils getInstance() {
        if (null == instance) {
            instance = new DbUtils();
        }
        return instance;
    }

    /**
     * 执行sql
     *
     * @param sql
     *            要执行的insert语句
     * @return int 保存影响的行数 如果为0保存失败 否则保存成功
     * @author Administrator
     */
    public int executeSql(String sql) {
        int result = 0;
        Connection conn = null;
        Statement stat = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            stat = conn.createStatement();
            result = stat.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbConnectionPool.getInstance().relase(conn);
        }
        return result;
    }



    /**
     * 查询数据库
     *
     * @param sql
     *            要执行的select语句
     * @return ResultSet查询的结果集
     * @author Administrator
     */
    public ResultSet query(String sql) {
        ResultSet rs = null;
        Connection conn = null;
        Statement stat = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            stat = conn.createStatement();
            rs = stat.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbConnectionPool.getInstance().relase(conn);
            conn = null;
        }
        return rs;
    }

    /**
     * 分页查询
     *
     * @param page
     *            显示第几页
     * @param pageSize
     *            一页显示多少条记录
     * @param sql
     *            要执行的select 语句
     * @return ResultSet 分页查询的结果
     * @author Administrator
     */
    public ResultSet queryByPage(int page, int pageSize, String sql) {
        ResultSet rs = null;
        try {
            int begin = (page - 1) * pageSize;
            sql = sql + " limit " + begin + "," + pageSize;
            rs = query(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * 获取总记录数
     *
     * @param sql
     *            要执行的select 语句
     * @return int 总记录数
     */
    public int getRecordCount(String sql) {
        ResultSet rs = null;
        int count = 0;
        try {
            sql = "select count(*) " + sql.substring(sql.indexOf("from"));
            rs = query(sql);
            rs.next();
            count = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * 获取总页数
     *
     * @param sql
     *            要执行的select语句
     * @param pageSize
     *            一页显示的记录数
     * @return int 总页数
     */
    public int getPageCount(String sql, int pageSize) {
        int count = 0;
        int total = this.getRecordCount(sql);
        count = total % pageSize == 0 ? (total / pageSize)
                : (total / pageSize + 1);
        return count;
    }

    /**
     * 删除
     *
     * @param sql
     *            要执行的delete语句
     * @return int 删除影响的行数 如果为0删除失败 否则删除成功
     * @author Administrator
     */
    public int delete(String sql) {
        Connection conn = null;
        Statement stat = null;
        int result = 0;
        conn = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            stat = conn.createStatement();
            result = stat.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            DbConnectionPool.getInstance().relase(conn);
        }
        return result;
    }


}
