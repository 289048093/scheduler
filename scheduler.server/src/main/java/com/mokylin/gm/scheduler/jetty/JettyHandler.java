package com.mokylin.gm.scheduler.jetty;

import com.mokylin.gm.scheduler.JobManagerImpl;
import org.mortbay.jetty.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/4/8.
 */

public class JettyHandler extends AbstractHandler {
    @Override
    public void handle(String s, HttpServletRequest request, HttpServletResponse response, int i) throws IOException, ServletException {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
//        if (s.equals("/list")) {
//        }else{
//            response.getWriter().write("404 go to /list");
//        }
            response.getWriter().write("<h4>running jobs info</h4>");
            response.getWriter().write(getRunningListStr());
            response.getWriter().close();
    }

    private String getRunningListStr() {
        List<Map<String, Object>> maps = JobManagerImpl.getInstance().listRunning();
        List<String> thead = new ArrayList<>();
        StringBuilder tbody = new StringBuilder("<tbody>");
        for (Map<String, Object> obj : maps) {
            tbody.append("<tr>");
            for (String k : thead) {
                Object o = obj.get(k);
                tbody.append("<td>").append(o).append("</td>");
                obj.remove(k);
            }
            for (Map.Entry<String, Object> e : obj.entrySet()) {
                thead.add(e.getKey());
                tbody.append("<td>").append(e.getValue()).append("</td>");
            }
            tbody.append("</tr>");
        }
        if(maps.isEmpty()){
            tbody.append("<tr><td>no running job ...</td></tr>");
        }
        tbody.append("</tbody>");
        StringBuilder th = new StringBuilder();
        for(String s:thead){
            th.append("<td>").append(s).append("</td>");
        }
        return "<table border='1'><thead><tr>"+th+"</tr></thead>"+tbody+"</table>";
    }


}
