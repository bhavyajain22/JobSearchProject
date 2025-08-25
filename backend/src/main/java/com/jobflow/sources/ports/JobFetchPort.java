package com.jobflow.sources.ports;

import com.jobflow.sources.model.RawJob;

import java.util.List;

public interface JobFetchPort {
    String sourceKey();
    List<RawJob> fetch(String jobTitle, String location, boolean remoteOnly, int max);
}
