package com.zt.ad_oper; /**
 * @Description:
 *
 * @Title: LdapByUser.java
 * @Package com.joyce.ad
 * @Copyright: Copyright (c) 2014
 *
 * @author Comsys-LZP
 * @date 2014-8-8 上午10:39:35
 * @version V2.0
 */


import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

/**
 * @Description:
 *
 * @ClassName: LdapByUser
 * @Copyright: Copyright (c) 2014
 *
 * @author Comsys-LZP
 * @date 2014-8-8 上午10:39:35
 * @version V2.0
 */
public class LdapbyUser {
    DirContext dc = null;
    String root = "ou=4#608,dc=cipherchina,dc=com"; // LDAP的根节点的DC

    /**
     * @Description: 程序主入口
     *
     * @param args
     *
     * @Title: LdapByUser.java
     * @Copyright: Copyright (c) 2014
     *
     * @author Comsys-LZP
     * @date 2014-8-11 上午10:27:15
     * @version V2.0
     */
    public static void main(String[] args) {
        LdapbyUser ldap = new LdapbyUser();
//		ldap.delete("CN=董平,OU=IT,"+ldap.root);

		ldap.renameEntry("CN=,OU=test,DC=2003,DC=com", "CN=joyce.luo,OU=研发部,DC=2003,DC=com");

//        SearchResult sr = ldap.searchByUserName(ldap.root, "tao.zhou");
//        System.out.println(sr.getName());
//		ldap.modifyInformation(sr.getName(), "test");
//        ldap.searchInformation(ldap.root);
//        ldap.add("周涛");
//        ldap.close();
    }

    /**
     *
     */
    public LdapbyUser() {
        super();
        init();
    }

    /**
     * @Description: Ldap连接
     *
     *
     * @Title: LdapByUser.java
     * @Copyright: Copyright (c) 2014
     *
     * @author Comsys-LZP
     * @date 2014-8-8 下午02:32:15
     * @version V2.0
     */
    public void init() {
        Properties env = new Properties();
        String adminName = "administrator@cipherchina.com";// username@domain
        String adminPassword = "ns25000@360";// password
        String ldapURL = "LDAP://192.168.1.169:389";// ip:port
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");// "none","simple","strong"
        env.put(Context.SECURITY_PRINCIPAL, adminName);
        env.put(Context.SECURITY_CREDENTIALS, adminPassword);
        env.put(Context.PROVIDER_URL, ldapURL);
        try {
            dc = new InitialLdapContext(env, null);
            System.out.println("认证成功");
        } catch (Exception e) {
            System.out.println("认证失败");
            e.printStackTrace();
        }
    }

    /**
     * @Description:关闭Ldap连接
     *
     *
     * @Title: LdapByUser.java
     * @Copyright: Copyright (c) 2014
     *
     * @author Comsys-LZP
     * @date 2014-8-8 下午02:31:44
     * @version V2.0
     */
    public void close() {
        if (dc != null) {
            try {
                dc.close();
            } catch (NamingException e) {
                System.out.println("NamingException in close():" + e);
            }
        }
    }


    /**
     * @Description: 新增域账号
     *
     * @param newUserName
     *
     * @Title: LdapByUser.java
     * @Copyright: Copyright (c) 2014
     *
     * @author Comsys-LZP
     * @date 2014-8-8 下午02:32:50
     * @version V2.0
     */
    public void add(String newUserName) {
        try {
            BasicAttributes attrs = new BasicAttributes();
            BasicAttribute objclassSet = new BasicAttribute("objectClass");
//            objclassSet.add("sAMAccountName");
//            objclassSet.add("employeeID");
            objclassSet.add("person");
            objclassSet.add("top");
            objclassSet.add("user");

            attrs.put(objclassSet);
            attrs.put("cn", newUserName);
            dc.createSubcontext("cn=" + newUserName + ",ou=java," + root, attrs);
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("Exception in add():" + e);
        }
    }

    /**
     * 删除
     *
     * @param dn
     */
    public void delete(String dn) {
        try {
            dc.destroySubcontext(dn);
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("Exception in delete():" + e);
        }
    }

    /**
     * @Description: 重命名节点
     *
     * @param oldDN
     * @param newDN
     * @return
     *
     * @Title: LdapByUser.java
     * @Copyright: Copyright (c) 2014
     *
     * @author Comsys-LZP
     * @date 2014-8-8 下午02:31:14
     * @version V2.0
     */
    private void renameEntry(String oldDN, String newDN) {
        try {
            dc.rename(oldDN, newDN);
        } catch (NamingException ne) {
            ne.printStackTrace();
        }
    }


    /**
     * @Description:修改
     *
     * @param dn
     * @param employeeID
     * @return
     *
     * @Title: LdapByUser.java
     * @Copyright: Copyright (c) 2014
     *
     * @author Comsys-LZP
     * @date 2014-8-8 下午02:31:30
     * @version V2.0
     */
    public boolean modifyInformation(String dn, String employeeID) {
        try {
            System.out.println("updating...\n");
            ModificationItem[] mods = new ModificationItem[1];
            // 修改属性
            Attribute attr0 = new BasicAttribute("OU",employeeID);
            mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr0);
            /* 修改属性 */
            dc.modifyAttributes(dn+",DC=2003,DC=com", mods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * @Description:搜索节点
     *
     * @param searchBase
     *
     * @Title: LdapByUser.java
     * @Copyright: Copyright (c) 2014
     *
     * @author Comsys-LZP
     * @date 2014-8-8 上午11:26:49
     * @version V2.0
     */
    public void searchInformation(String searchBase) {
        try {
            SearchControls searchCtls = new SearchControls();
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String searchFilter = "(&(objectCategory=person)(objectClass=user)(name=*))";
            String returnedAtts[] = { "memberOf" };
            searchCtls.setReturningAttributes(returnedAtts);
            NamingEnumeration<SearchResult> answer = dc.search(searchBase,
                    searchFilter, searchCtls);
            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult) answer.next();
                System.out.println("<<<::[" + sr.getName() + "]::>>>>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 指定搜索节点搜索制定域用户
     *
     * @param searchBase
     * @param userName
     * @return
     *
     * @Title: LdapByUser.java
     * @Copyright: Copyright (c) 2014
     *
     * @author Comsys-LZP
     * @date 2014-8-8 上午11:55:25
     * @version V2.0
     */
    public SearchResult searchByUserName(String searchBase, String userName) {
        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String searchFilter = "sAMAccountName=" + userName;
        String returnedAtts[] = { "memberOf" }; // 定制返回属性
        searchCtls.setReturningAttributes(returnedAtts); // 设置返回属性集
        try {
            NamingEnumeration<SearchResult> answer = dc.search(searchBase,
                    searchFilter, searchCtls);
            return answer.next();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Throw Exception : " + e);
        }
        return null;
    }
}
