package com.free.common.request;

import lombok.Data;

import java.util.List;

@Data
public class JobReq {
    public String jobId;
    public String serachContent;
    public String searchLimt;
    public List<String> pluginList;
}
