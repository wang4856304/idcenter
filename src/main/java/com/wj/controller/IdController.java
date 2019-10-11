package com.wj.controller;

import com.wj.annotation.Time;
import com.wj.idcenter.IdCenterGenerateTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jun.wang
 * @title: IdController
 * @projectName ownerpro
 * @description: TODO
 * @date 2019/10/8 10:51
 */

@RestController
@RequestMapping("/api/id")
public class IdController extends BaseController {

    @Autowired
    private IdCenterGenerateTemplate template;

    @GetMapping("/getId")
    @Time
    public ResponseEntity<Long> getId(String ip) {
        return buildSuccessResponse(template.getId(ip));
    }
}
