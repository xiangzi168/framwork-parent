package com.amg.framework.boot.swagger.transword.controller;

import com.amg.framework.boot.swagger.transword.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;


@Controller
public class WordController {

    @Autowired
    private WordService tableService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${server.port}")
    private Integer port;

    @Value("${spring.application.name}")
    private String application;


    /**
     * 将swagger文档转换成html文档
     * @return
     */
    @Deprecated
    @RequestMapping("/app/word")
    public String getWord(Model model, @RequestParam(value = "url", required = false) String url,
                          @RequestParam(value = "download", required = false, defaultValue = "1") Integer download) {
        url = "http://localhost:" + port + "/v2/api-docs";
        Map<String, Object> result = tableService.tableList(url);
      //  model.addAttribute("url", application.split("-")[1]);
        model.addAttribute("download", download);
        model.addAllAttributes(result);
        return "word";
    }


    /**
     * 下载word文档
     */
    @RequestMapping("/app/downloadword")
    public void word(@RequestParam(required = false) String url, HttpServletResponse response) {
        ResponseEntity<String> forEntity = restTemplate.getForEntity("http://localhost:" + port + "/app/word?download=0", String.class);
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        try (BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(application + ".doc", "utf-8"));
            byte[] bytes = forEntity.getBody().getBytes();
            bos.write(bytes, 0, bytes.length);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
