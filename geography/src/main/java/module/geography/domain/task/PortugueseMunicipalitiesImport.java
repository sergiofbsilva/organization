/*
 * @(#)PortugueseMunicipalitiesImport.java
 *
 * Copyright 2010 Instituto Superior Tecnico
 * Founding Authors: Pedro Santos
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Geography Module.
 *
 *   The Geography Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Geography Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Geography Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.geography.domain.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;

import module.geography.domain.Country;
import module.geography.domain.CountrySubdivision;
import module.organization.domain.Accountability;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import pt.ist.bennu.core.i18n.BundleUtil;
import pt.ist.bennu.core.util.ConfigurationManager;
import pt.ist.bennu.scheduler.CronTask;
import pt.ist.bennu.scheduler.annotation.Task;
import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

/**
 * 
 * @author Pedro Santos
 * @author João Antunes
 * 
 */

@Task(englishTitle = "Import Portuguese Municipalities")
public class PortugueseMunicipalitiesImport extends CronTask {

    private final MultiLanguageString municipalityLevelName = new MultiLanguageString().with(Language.pt, "Concelho").with(
            Language.en, "Municipality");

    @Override
    public String getLocalizedName() {
        return BundleUtil.getString("resources/GeographyResources", "label.task.ctt.portugal.municipalities.import");
    }

    private static final String CTT_MUNICIPALITIESFILE = "/concelhos.txt";

    Country portugal;

    protected int additions = 0;

    protected int deletions = 0;

    protected int modifications = 0;

    protected int touches = 0;

    @Override
    public void runTask() {
        // let's retrieve Portugal, if it doesn't exist, we must create it
        portugal = Country.getPortugal();
        LineNumberReader reader = null;
        ArrayList<CountrySubdivision> municipalitiesOnFile = new ArrayList<CountrySubdivision>();
        ArrayList<CountrySubdivision> existingMunicipalities = new ArrayList<CountrySubdivision>();

        try {
            File file =
                    new File(ConfigurationManager.getProperty("modules.geography.file.import.location") + CTT_MUNICIPALITIESFILE);
            if (getLastRun() == null || file.lastModified() > getLastRun().getMillis()) {
                DateTime lastReview = new DateTime(file.lastModified());
                FileInputStream fileReader = new FileInputStream(file);
                reader = new LineNumberReader(new InputStreamReader(fileReader, "ISO-8859-1"));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    String districtCode = parts[0];
                    String municipalityCode = parts[1];
                    String municipalityName = parts[2];
                    CountrySubdivision district = portugal.getChildByCode(districtCode);
                    CountrySubdivision municipality = null;
                    if (district == null) {
                        // abort the transaction!! throw some kind of exception
                        // and warn the user of the outcome
                        taskLog("Script aborted because the district with code: " + districtCode + " couldn't be found");
                        throw new RuntimeException("Script aborted because the district with code: " + districtCode
                                + " couldn't be found");
                    } else {
                        // get the municipality
                        municipality = district.getChildByCode(municipalityCode);
                        if (municipality == null) {
                            municipality = createMunicipality(district, municipalityCode, municipalityName, lastReview);
                        } else {
                            modifyMunicipality(municipality, municipalityCode, municipalityName, "", lastReview);

                        }
                    }
                    municipalitiesOnFile.add(municipality);
                }

                // 'removing' all of the existing municipalities

                // getting all of the existing districts
                for (CountrySubdivision countrySubdivision : portugal.getChildren()) {
                    if (countrySubdivision.getLevelName().getContent(Language.pt)
                            .equalsIgnoreCase(municipalityLevelName.getContent(Language.pt))) {
                        existingMunicipalities.add(countrySubdivision);
                    }
                } // let's assert
                  // which ones are in excess and remove them
                existingMunicipalities.removeAll(municipalitiesOnFile);
                for (CountrySubdivision countrySubdivision : existingMunicipalities) {
                    removeMunicipality(countrySubdivision);
                }
                taskLog("File last modification was: " + lastReview);
                taskLog(additions + " municipalities added.");
                taskLog(touches + " municipalities unmodified, but whose date has changed.");
                taskLog(modifications + " municipalities modified");
            } else {
                taskLog("File unmodified, nothing imported.");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
    }

    private DateTime getLastRun() {
        return new DateTime(0);
    }

    private void removeMunicipality(CountrySubdivision municipality) {
        // modify the district by setting the accountability of the last one
        // end before the date of the last review
        Accountability activeAccountability = null;
        for (Accountability accountability : municipality.getUnit().getParentAccountabilities(
                municipality.getOrCreateAccountabilityType())) {
            if (accountability.isActiveNow()) {
                activeAccountability = accountability;
            }
        }

        if (activeAccountability != null) {
            // make the accountability expire a minimal measure of time before
            // of
            // the current date
            LocalDate endDate = new LocalDate();
            // endDate = endDate.minusDays(1);
            activeAccountability.setEndDate(endDate);
        }
        deletions++;

    }

    private void modifyMunicipality(CountrySubdivision municipality, String municipalityCode, String municipalityName,
            String municipalityAcronym, DateTime lastReview) {
        String originalMunicipalityName = municipality.getName().getContent(Language.pt);
        String originalMunicipalityAcronym = municipality.getAcronym();
        // let's check if there are changes on the data:
        if (!originalMunicipalityName.equals(municipalityName) || !originalMunicipalityAcronym.equals(municipalityAcronym)
                || !municipality.getLevelName().equals(municipalityLevelName)) {

            removeMunicipality(municipality);
            deletions--;
            // create the new district!
            // lets correct the counters afterwards!
            createMunicipality(municipality.getParentSubdivision(), municipalityCode, municipalityName, lastReview);
            additions--;
            modifications++;
        }
        // if not let's just 'touch' it
        else {
            touchMunicipality(municipality, lastReview);
        }

    }

    private void touchMunicipality(CountrySubdivision municipality, DateTime lastReview) {
        municipality.setLastReview(lastReview);
        touches++;
    }

    private CountrySubdivision createMunicipality(CountrySubdivision district, String municipalityCode, String municipalityName,
            DateTime lastReview) {
        CountrySubdivision municipality = new CountrySubdivision(district, municipalityName, "", municipalityCode);
        municipality.setLevelName(municipalityLevelName, false);
        municipality.setLastReview(lastReview);
        additions++;
        return municipality;
    }

}
