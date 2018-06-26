package tmp;
import java.util.Hashtable;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;

public class TmpTest {

    private final static String adminname = "kkk@test.com";
    private final static String BASEDN = "CN=Users,DC=test,DC=com";
    private final static String password = "888";
    private final static String host = "9999";
    private final static String port = "389";
    private final static String AdminPwd = "888";
    private static DirContext ctx = null;
    public static void LDAP_connect() {

        String url = new String("ldap://" + host + ":" + port);

        Hashtable env = new Hashtable();

        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        env.put(Context.SECURITY_PRINCIPAL, adminname);

        env.put(Context.SECURITY_CREDENTIALS, password);

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

        env.put(Context.REFERRAL, "follow");
        env.put("com.sun.jndi.ldap.read.timeout", "3000");
        env.put(Context.PROVIDER_URL, url);

        try {

            ctx = new InitialDirContext(env);

        } catch (NamingException err) {

            err.printStackTrace();

        }

    }
    public static void closeContext() {

        if (ctx != null) {

            try {

                ctx.close();

            } catch (NamingException e) {

                e.printStackTrace();

            }

        }

    }
    public static String getUserDN(String uid) {

        String userDN = "";

        LDAP_connect();

        try {

            SearchControls constraints = new SearchControls();

            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration en = ctx.search("", "uid=" + uid, constraints);

            if (en == null || !en.hasMoreElements()) {

                System.out.println("not find the user");

            }

            while (en != null && en.hasMoreElements()) {

                Object obj = en.nextElement();

                if (obj instanceof SearchResult) {

                    SearchResult si = (SearchResult) obj;

                    userDN += si.getName();

                    userDN += "," + BASEDN;

                } else {

                    System.out.println(obj);

                }

            }

        } catch (Exception e) {

            System.out.println("query user exception");

            e.printStackTrace();

        }

        return userDN;

    }

    public static boolean authenticate(String UID, String password) {

        boolean valide = false;

        String userDN = getUserDN(UID);

        try {

            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);

            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);

            System.out.println(userDN + " 验证通过");

            valide = true;

        } catch (AuthenticationException e) {

            System.out.println(userDN + " 验证失败");

            System.out.println(e.toString());

            valide = false;

        } catch (NamingException e) {

            System.out.println(userDN + " 验证失败");

            valide = false;

        }

        closeContext();

        return valide;

    }

    public static boolean addUser(String usr, String pwd, String uid, String description) {
        try {
            BasicAttributes attrsbu = new BasicAttributes();
            BasicAttribute objclassSet = new BasicAttribute("objectclass");
            objclassSet.add("inetOrgPerson");
            attrsbu.put(objclassSet);
            attrsbu.put("sn", usr);
            attrsbu.put("cn", usr);
            attrsbu.put("uid", uid);
            attrsbu.put("userPassword", pwd);
            attrsbu.put("description", description);
// attrsbu.put("userAccountControl",544);
            attrsbu.put("userPrincipalName", "aaa@test.com");
            ctx.createSubcontext(uid, attrsbu);
            return true;
        } catch (NamingException ex) {
            ex.printStackTrace();
        }
        closeContext();
        return false;
    }

    /**
     * 删除一个用户
     *
     * @param userDN
     * @return
     */
    public static boolean delUser(String userDN) {
        try {
            LDAP_connect();
            ctx.destroySubcontext(userDN);
            return true;
        } catch (NamingException e) {
            System.err.println("Problem changing password: " + e);
        } catch (Exception e) {
            System.err.println("Problem: " + e);
        } finally {
            closeContext();
        }
        return false;
    }

    /**
     * 修改一个用户
     *
     * @param uid
     * @return
     */
    public static boolean modify(String uid) {
        try {
            BasicAttributes attrsbu = new BasicAttributes();
// ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userName);
// ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, AdminPwd);
            attrsbu.remove("CN");
            attrsbu.put("userAccountControl", "514");
            ctx.modifyAttributes(uid, DirContext.REPLACE_ATTRIBUTE, attrsbu);
            return true;

        } catch (NamingException e) {
            System.err.println("Problem changing password: " + e);
        } catch (Exception e) {
            System.err.println("Problem: " + e);
        } finally {
            closeContext();
        }

        return false;

    }

    public static String GetADInfo(String type) {
        String result = "";
        try {
            String searchBase = "DC=test,DC=com";
            String searchFilter = "objectClass=" + type + "";
            SearchControls searchCtls = new SearchControls();
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String returnedAtts[] = {"memberOf"}; // 定制返回属性
            searchCtls.setReturningAttributes(returnedAtts); // 设置返回属性集 不设置则返回所有属性
// 根据设置的域节点、过滤器类和搜索控制器搜索LDAP得到结果
            @SuppressWarnings("rawtypes")
            NamingEnumeration answer = ctx.search(searchBase, searchFilter, searchCtls);// Search for objects using the filter
            while (answer.hasMore()) {
                SearchResult result1 = (SearchResult) answer.next();
                System.err.println(result1.getName());
                NamingEnumeration attrs = result1.getAttributes().getAll();
                while (attrs.hasMore()) {
                    Attribute attr = (Attribute) attrs.next();
                    System.out.println(attr.getID() + "  =  " + attr.get());
                }
            }

        } catch (NamingException e) {
            e.printStackTrace();
            System.err.println("Throw Exception : " + e);
        }
        return result;
    }


    public static void main(String[] args) {
        LDAP_connect();
        System.out.print("--" + modify("CN=haha,CN=Users,DC=test,DC=com"));
        //System.out.print("haha"+addUser("aaa","Aa123","aaa","aaaaa"));

//System.out.print("---"+addUser("haha","Abcd123456","CN=haha,CN=Users,DC=test,DC=com","一无所有的王健林"));

// System.out.print("hehe"+modify(""));

// ad.GetADInfo("user","cn","李XX");//查找用户

// ad.GetADInfo("organizationalUnit","ou","工程");//查找组织架构

// ad.GetADInfo("group","cn","福建xxx");//查找用户组

// String pwd = "\"" +password() + "\"";

// byte[] newUnicodePassword =  pwd.getBytes("UTF-16LE");

// attr.put("unicodePwd" , newUnicodePassword.toString());

        closeContext();

    }

}

