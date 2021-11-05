package com.amg.framework.boot.swagger.filter;


import com.amg.framework.boot.system.wrapper.ResponseWrapper;
import com.amg.framework.boot.utils.common.Utils;
import net.sf.json.JSONObject;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ResponseFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {
        ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) arg1);
        arg2.doFilter(arg0, responseWrapper);
        String content = new String(responseWrapper.getBytes(), "UTF-8");
        String path = ((HttpServletRequest) arg0).getParameter("path");
        JSONObject jsonObject = JSONObject.fromObject(content);
        JSONObject paths = jsonObject.getJSONObject("paths");
        Map map = new HashMap();
        map.putAll(paths);
        for (Iterator<Map.Entry<String, String>> it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, String> item = it.next();
            String key = item.getKey().substring(item.getKey().indexOf("/",item.getKey().indexOf("/") + 1));
            if (!key.startsWith("/" + path.split(":")[1].trim()))
                it.remove();
        }
        jsonObject.put("paths", map);
        jsonObject.put("tags", null);
        jsonObject.getJSONObject("info").put("title", path.split(":")[0].trim());
        jsonObject.getJSONObject("info").put("license", "");
        Utils.print(jsonObject);
    }

    @Override
    public void destroy() {

    }

}
