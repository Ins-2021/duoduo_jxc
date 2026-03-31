package com.duoduo.jxc.service;

import java.util.concurrent.TimeUnit;

public interface DuplicateCheckService {

    boolean tryAcquireScanLock(String bundleNo, Long workerId, Long processId);

    void releaseScanLock(String bundleNo, Long workerId, Long processId);

    boolean markScanned(Long bundleId, Long workerId, Long processId);

    boolean isScannedToday(Long bundleId, Long workerId, Long processId);

    boolean tryAcquireBundleLock(Long bundleId);

    void releaseBundleLock(Long bundleId);
}
