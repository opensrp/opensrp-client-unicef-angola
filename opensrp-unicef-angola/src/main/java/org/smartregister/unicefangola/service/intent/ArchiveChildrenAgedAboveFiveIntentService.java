package org.smartregister.unicefangola.service.intent;

import org.smartregister.child.service.intent.ArchiveClientRecordIntentService;
import org.smartregister.unicefangola.dao.AppChildDao;

import java.util.List;

public class ArchiveChildrenAgedAboveFiveIntentService extends ArchiveClientRecordIntentService {

    @Override
    protected List<String> getClientIdsToArchive() {
        return AppChildDao.getChildrenAboveFiveYears();
    }

    @Override
    protected void onArchiveDone() {
        //Do nothing for now
    }
}
