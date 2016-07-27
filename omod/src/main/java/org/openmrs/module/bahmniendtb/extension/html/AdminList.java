package org.openmrs.module.bahmniendtb.extension.html;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class defines the links that will appear on the administration page under the
 * "bacteriology.title" heading.
 */
public class AdminList extends AdministrationSectionExt {

    /**
     * @see AdministrationSectionExt#getMediaType()
     */
    public Extension.MEDIA_TYPE getMediaType() {
        return Extension.MEDIA_TYPE.html;
    }

    /**
     * @see AdministrationSectionExt#getTitle()
     */
    public String getTitle() {
        return "endtb-bahmniextn.title";
    }

    /**
     * @see AdministrationSectionExt#getLinks()
     */
    public Map<String, String> getLinks() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("/module/bacteriology/manage.form", "endtb-bahmniextn.manage");
        return map;
    }

}
