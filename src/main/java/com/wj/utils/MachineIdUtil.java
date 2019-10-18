package com.wj.utils;

/**
 * @author jun.wang
 * @title: MachineIdUtil
 * @projectName ownerpro
 * @description: TODO
 * @date 2019/10/11 16:16
 */

import com.wj.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 机器id生成工具
 */

@Component
public class MachineIdUtil {

    @Autowired
    private RedisService redisService;


    private final static String WORK_ID_LIST = "workIdList:";

    private static Map<String, Boolean> WORK_ID_MAP = new LinkedHashMap<>();

    static {
        for (int i = 1; i <= 31; i++) {
            WORK_ID_MAP.put(String.valueOf(i), false);
        }
    }

    @PostConstruct
    public void init() {
        if (!redisService.hasKey(WORK_ID_LIST)) {
            redisService.set(WORK_ID_LIST, WORK_ID_MAP);
        }
    }

    public int getWorkId() {
        Map<String, Boolean> resultMap = redisService.get(WORK_ID_LIST);
        Set<Map.Entry<String, Boolean>> set = resultMap.entrySet();
        int workId = 0;
        for (Map.Entry<String, Boolean> entry : set) {
            if (!entry.getValue()) {
                resultMap.put(entry.getKey(), true);
                redisService.set(WORK_ID_LIST, resultMap);
                workId = Integer.valueOf(entry.getKey());
                break;
            }
        }
        return workId;
    }

    public void clear(int workId) {
        Map<Integer, Boolean> resultMap = redisService.get(WORK_ID_LIST);
        resultMap.put(workId, false);
    }
}
