/**
 * @FileName: UserModel.java
 * @PackageName com.bp.business.entity
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年1月23日 下午5:37:30
 * @version
 */

package com.bp.notice.business.model;

/**
 * @ClassName: UserModel
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author MT
 * @date 2018年1月23日 下午5:37:30
 */
public class LoginModel
{

    private Integer id;

    private String uname;

    private String pwd;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getUname()
    {
        return uname;
    }

    public void setUname(String uname)
    {
        this.uname = uname;
    }

    public String getPwd()
    {
        return pwd;
    }

    public void setPwd(String pwd)
    {
        this.pwd = pwd;
    }

}
