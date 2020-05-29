
package com.bp.app.notification.jdbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;

public class JDBCOperation
{
    private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    private JDBCOperation()
    {
    }

    private static Connection getConn()
    {
        Connection conn = null;
        String username = null;
        String password = null;
        String driver = null;
        String url = null;
        try
        {
            Properties prop = new Properties();
            InputStream in = JDBCOperation.class.getResourceAsStream("/jdbc.properties");
            BufferedReader bf = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            prop.load(bf);
            username = prop.getProperty("jdbc.username");
            password = prop.getProperty("jdbc.password");
            driver = prop.getProperty("jdbc.driver");
            url = prop.getProperty("jdbc.url");

            Class.forName(driver); //classLoader,加载对应驱动
            conn = (Connection)DriverManager.getConnection(url, username, password);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } /// 加载属性列表

        return conn;
    }

    /**
     * @Title: insertNotificationContent
     * @Description: TODO(插入报警消息推送数据至content表)
     * @param @return 设定文件
     * @return int 返回类型
     * @throws
     */
    public static int insertNotificationContent(Map<String, Object> map)
    {
        int id = 0;
        if (map.isEmpty())
        {
            return id;
        }
        Connection conn = getConn();

        String sql = "INSERT INTO notification_data (notification_group,terminal_group,mac,description,recv,delay,delivery_sucess,in_time) VALUES(?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = null;
        try
        {
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (map.containsKey("notiGroup") && null != map.get("notiGroup"))
            {
            		if(map.get("notiGroup").toString().length() > 20000)
            			pstmt.setString(1, JSON.toJSONString(map.get("notiGroup")).substring(0, 20000));
            		else
            			pstmt.setString(1, JSON.toJSONString(map.get("notiGroup")));
            }
            else
            {
                return id;
            }
            if (map.containsKey("terGroup") && null != map.get("terGroup"))
            {
                pstmt.setString(2, (String)map.get("terGroup"));
            }
            else
            {
                return id;
            }
            if (map.containsKey("mac") && null != map.get("mac"))
            {
                pstmt.setString(3, (String)map.get("mac"));
            }
            else
            {
                return id;
            }
            if (map.containsKey("description") && null != map.get("description"))
            {
                pstmt.setString(4, (String)map.get("description"));
            }
            else
            {
                return id;
            }
            if (map.containsKey("recv") && null != map.get("recv"))
            {
                pstmt.setString(5, (String)map.get("recv"));
            }
            else
            {
                return id;
            }
            if (map.containsKey("delay") && null != map.get("delay"))
            {
                pstmt.setLong(6, Long.parseLong(map.get("delay").toString()));
            }
            else
            {
                return id;
            }
            pstmt.setInt(7, 0);
            String in_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            pstmt.setString(8, in_time);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next())
            {
                id = rs.getInt(1);
                logger.info("数据主键：" + id);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (null != pstmt)
                {
                    pstmt.close();
                }
                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return id;
    }

    /**
     * @Title: updateContent
     * @Description: TODO(消息发送成功更新content表)
     * @param @param id
     * @param @param delivery_sucess
     * @param @param delivery_time
     * @param @param up_time 设定文件
     * @return void 返回类型
     * @throws
     */
    public static void updateContent(int id, int delivery_sucess, String delivery_time, String up_time)
    {
        Connection conn = getConn();
        String sql = "UPDATE notification_data SET delivery_sucess=" + delivery_sucess + ",delivery_time='" + delivery_time + "',up_time='" + up_time
                + "' WHERE id=" + id;
        PreparedStatement pstmt = null;
        try
        {
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (null != pstmt)
                {
                    pstmt.close();
                }
                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * @Title: getDelayByGroupName
     * @Description: TODO(根据通报组名查询所对应的延时时间)
     * @param @param groupName
     * @param @return 设定文件
     * @return long 返回类型
     * @throws
     */
    public static long getDelayByGroupName(String groupName)
    {
        long delay = -1;
        Connection conn = getConn();
        String sql = "SELECT no.delay FROM notification_group no WHERE no.group_name = '" + groupName + "'";
        PreparedStatement pstmt = null;
        try
        {
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                delay = rs.getLong("delay");
            }
            return delay;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return delay;
        }
        finally
        {
            try
            {
                if (null != pstmt)
                {
                    pstmt.close();
                }
                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * @Title: getContentSuccessFlag
     * @Description: TODO(定时器定时查询数据库是否有未推送微信的数据)
     * @param @return 设定文件
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public static List<Map<String, Object>> getContentNoSuccess()
    {
        Connection conn = getConn();
        String sql = "SELECT * FROM notification_data no WHERE no.delivery_sucess =0";
        PreparedStatement pstmt = null;
        try
        {
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            while (rs.next())
            {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", rs.getInt("id"));
                map.put("notification_group", rs.getString("notification_group"));
                map.put("description", rs.getString("description"));
                map.put("terminal_group", rs.getString("terminal_group"));
                map.put("mac", rs.getString("mac"));
                map.put("recv", rs.getString("recv"));
                map.put("delay", rs.getLong("delay"));

                list.add(map);
            }
            return list;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            try
            {
                if (null != pstmt)
                {
                    pstmt.close();
                }
                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

}
