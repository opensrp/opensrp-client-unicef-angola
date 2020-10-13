package org.smartregister.unicefangola.util;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.unicefangola.application.UnicefAngolaApplication;
import org.smartregister.unicefangola.dao.AppChildDao;

public class VaccineUtils {
    public static void refreshImmunizationSchedules(String caseId) {
        boolean prematureBaby = AppChildDao.isPrematureBaby(caseId);
        String conditionalVaccine = null;

        if (prematureBaby) {
            conditionalVaccine = AppConstants.ConditionalVaccines.PRETERM_VACCINES;
        }

        if (!StringUtils.equalsIgnoreCase(conditionalVaccine, ImmunizationLibrary.getInstance().getCurrentConditionalVaccine())) {
            VaccineSchedule.setVaccineSchedules(null);
            ImmunizationLibrary.getInstance().setCurrentConditionalVaccine(conditionalVaccine);
            UnicefAngolaApplication.getInstance().initOfflineSchedules();
        } else if (conditionalVaccine == null && ImmunizationLibrary.getInstance().getCurrentConditionalVaccine() == null) {
            UnicefAngolaApplication.getInstance().initOfflineSchedules();
        }
    }
}
