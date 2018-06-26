package com.zt.ad_oper;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;
import java.util.*;

/**
 * @author zt 获取AD域用户相关信息
 */
public class LdapOperUtil {
    private static LdapOperUtil ldapOperUtil;

    private String root = "OU=4#608,DC=cipherchina,DC=com"; // LDAP的根节点的DC

    //    private LdapContext ldapContext = null;
    private DirContext ldapContext = null;

    private LdapOperUtil() {
        getConnect();
    }


    public static LdapOperUtil getNewInstance() {
        if (ldapOperUtil == null) {
            ldapOperUtil = new LdapOperUtil();
        }
        return ldapOperUtil;
    }


    /**
     * 连接AD域
     *
     * @return
     */
    private void getConnect() {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        String ldapURL = "ldap://" + "192.168.1.169" + ":" + 389;
        env.put(Context.PROVIDER_URL, ldapURL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "administrator@cipherchina.com");
        env.put(Context.SECURITY_CREDENTIALS, "ns25000@360"); //密码
        try {
            this.ldapContext = new InitialDirContext(env);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 遍历AD域
     *
     * @throws NamingException
     */
    private void getUserInfo() throws NamingException {
        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        // 注意OU和DC的先后顺序
        NamingEnumeration results = this.ldapContext.search("OU=4#608,DC=cipherchina,DC=com", "objectClass=User", searchCtls);

        while (results.hasMoreElements()) {
            SearchResult sr = (SearchResult) results.next();
            Attributes attributes = sr.getAttributes();
            NamingEnumeration values = attributes.getAll();
            while (values.hasMore()) {
                Attribute attr = (Attribute) values.next();
                Enumeration vals = attr.getAll();
                while (vals.hasMoreElements()) {
                    Object o = vals.nextElement();
                    System.out.println(attr.getID() + "--------------" + o.toString());
                }
            }
        }
    }

    /**
     * 添加一个用户
     *
     * @param newUserName
     */
    private void add(String newUserName) {
        try {
            BasicAttributes attrs = new BasicAttributes();
            BasicAttribute objclassSet = new BasicAttribute("objectClass");
            objclassSet.add("person");
            objclassSet.add("top");
            objclassSet.add("user");
            attrs.put(objclassSet);
            attrs.put("ou", newUserName);
            this.ldapContext.createSubcontext("CN=" + newUserName + ",ou=java," + root, attrs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除一个用户
     *
     * @param dn
     */
    private void delete(String dn) {
        try {
            this.ldapContext.destroySubcontext(dn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重命名一个用户
     *
     * @param oldDN
     * @param newDN
     * @return
     */
    public boolean renameEntry(String oldDN, String newDN) {
        try {
            this.ldapContext.rename(oldDN, newDN);
            return true;
        } catch (NamingException ne) {
            ne.printStackTrace();
            return false;
        }
    }


    /**
     * @param searchBase 需要登录的AD域
     * @param userName 查询的用户名
     * @return
     * @Description: 指定搜索节点搜索制定域用户
     */
    public SearchResult searchByUserName(String searchBase, String userName) {
        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
//        String searchFilter = "objectclass=organizationalUnit" + userName;
        String searchFilter="(&(objectCategory=person)(objectClass=user)(name="+userName+"))";
        String returnedAtts[] = {"memberOf"}; // 定制返回属性
        searchCtls.setReturningAttributes(returnedAtts); // 设置返回属性集
        try {
            NamingEnumeration<SearchResult> answer = this.ldapContext.search(searchBase, searchFilter, searchCtls);
            return answer.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) throws NamingException {
        LdapOperUtil ldap = LdapOperUtil.getNewInstance();
        try {
//            ldap.getUserInfo();
//            删除一个用户
//            ldap.delete("CN=test,OU=java,"+ldap.root);
//            添加一个用户
//            ldap.add("test");
//            重命名一个用户
//            ldap.renameEntry("cn=test1,"+ldap.root,"cn=周涛,ou=java,"+ldap.root);
        ldap.searchByUserName("dc=cipherchina,dc=com","周涛");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        ldap.getUserInfo();
    }

}