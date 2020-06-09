package team.se24.employfast.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;

public class Mission {
    public static final String missionIdKey = "missionId";
    public static final String missionNameKey = "missionName";
    public static final String creatorKey = "creator";
    public static final String contactKey = "contact";
    public static final String launchDateKey = "launchDate";
    public static final String originKey = "originCountry";
    public static final String durationKey = "missionDuration";
    public static final String typeKey = "missionType";
    public static final String statusKey = "missionStatus";
    public static final String descriptionKey = "missionDescription";
    public static final String employKey = "employmentRequirements";
    public static final String minAgeKey = "minAge";
    public static final String maxAgeKey = "maxAge";
    public static final String workExperienceYearsKey = "workExperienceYears";
    public static final String qualificationsKey = "qualifications";
    public static final String occupationsKey = "occupations";
    public static final String employNumberKey = "employNumbers";
    public static final String computerSkillKey = "computerSkillRequired";
    public static final String languageRequiredKey = "languageRequired";
    public static final String secondLanguagesKey = "secondaryLanguages";
    public static final String cargoForKey = "cargoFor";
    public static final String cargoWeightKey = "cargoWeight";
    public static final String[] recordKeys = {missionIdKey, missionNameKey, creatorKey, contactKey, launchDateKey,
            originKey, durationKey, typeKey, descriptionKey, employKey, minAgeKey, maxAgeKey, workExperienceYearsKey,
            qualificationsKey, occupationsKey, employNumberKey, computerSkillKey, languageRequiredKey,
            secondLanguagesKey, cargoForKey, cargoWeightKey, statusKey};

    public static String[] missionAllStatus = {"Planning Phase", "Departed Earth", "Landed on Mars",
            "Mission in progress", "Returned to Earth", "Mission Completed"};
    public static String[] cargoForList = {"Mission", "Journey", "Other Missions"};

    private long missionId;
    private String missionName;
    private long creator;
    private Date launchDate;
    private String originCountry;
    private int missionDuration;
    private String missionType;
    private String contact;
    private String missionDescription;
    private String[] employmentRequirements;
    private int[] workExperienceYears;
    private int minAge;
    private int maxAge;
    private String[] qualifications;
    private String[] occupations;
    private int[] employNumbers;
    private String computerSkillRequired;
    private String languageRequired;
    private String[] secondaryLanguages;
    private String cargoFor;
    private int cargoWeight;
    private int missionStatus;

    /*Generate a Mission object from Map<String, String> which is usually obtained from CSV File
    * @Parmas Map<String, String> map:  a complete map with no missing field
    * @return a Mission object*/
    public static Mission parseMap(Map<String, String> map) throws ParseException {
        Mission m = new Mission();

        m.missionId = Long.parseLong(map.get(missionIdKey));
        m.missionName = map.get(missionNameKey);
        m.creator = Long.parseLong(map.get(creatorKey));
        m.contact = map.get(contactKey);
        m.launchDate = new SimpleDateFormat("MM/dd/yyyy").parse(map.get(launchDateKey));
        m.originCountry = map.get(originKey);
        m.missionDuration = Integer.parseInt(map.get(durationKey));
        m.missionType = map.get(typeKey);
        m.missionDescription = map.get(descriptionKey);
        m.minAge = Integer.parseInt(map.get(minAgeKey));
        m.maxAge = Integer.parseInt(map.get(maxAgeKey));
        m.missionStatus = Integer.parseInt(map.get(statusKey));
        m.computerSkillRequired = map.get(computerSkillKey);
        m.languageRequired = map.get(languageRequiredKey);
        m.cargoFor = map.get(cargoForKey);
        m.cargoWeight = Integer.parseInt(map.get(cargoWeightKey));

        m.secondaryLanguages = map.get(secondLanguagesKey).split(",");
        for(int i = 0; i < m.secondaryLanguages.length; i++) {
            m.secondaryLanguages[i] = m.secondaryLanguages[i].trim();
        }

        m.qualifications = map.get(qualificationsKey).split(",");
        for(int i = 0; i < m.qualifications.length; i++) {
            m.qualifications[i] = m.qualifications[i].trim();
        }

        int len = 0;
        m.employmentRequirements = map.get(employKey).split(",");
        String[] yearStrs = map.get(workExperienceYearsKey).split(",");
        if (yearStrs.length == m.employmentRequirements.length) {
            len = yearStrs.length;
            m.workExperienceYears = new int[len];

            for (int i = 0; i < len; i++) {
                m.employmentRequirements[i] = m.employmentRequirements[i].trim();
                m.workExperienceYears[i] = Integer.parseInt(yearStrs[i].trim());
            }
        }

        m.occupations = map.get(occupationsKey).split(",");
        String[] numStrs = map.get(employNumberKey).split(",");
        if (numStrs.length == m.occupations.length) {
            len = numStrs.length;
            m.employNumbers = new int[len];

            for (int i = 0; i < len; i++) {
                m.occupations[i] = m.occupations[i].trim();
                m.employNumbers[i] = Integer.parseInt(numStrs[i].trim());
            }
        }

        return m;
    }

    /*Parse a mission object to Map<String, String>. It is typically used when a Mission object needs to be written to the CSV file
    * @Params Mission m: The given Mission object
    * @return Map<String, String> convert the fields and values to keys and values. The arrays and date will convert to String.*/
    public static Map<String, String> parseObject(Mission m) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put(missionIdKey, Long.toString(m.missionId));
        map.put(missionNameKey, m.missionName);
        map.put(contactKey, m.contact);
        map.put(creatorKey, Long.toString(m.creator));
        map.put(launchDateKey, new SimpleDateFormat("MM/dd/yyyy").format(m.launchDate));
        map.put(originKey, m.originCountry);
        map.put(durationKey, Integer.toString(m.missionDuration));
        map.put(typeKey, m.missionType);
        map.put(descriptionKey, m.missionDescription);
        map.put(minAgeKey, Integer.toString(m.minAge));
        map.put(maxAgeKey, Integer.toString(m.maxAge));
        map.put(statusKey, Integer.toString(m.missionStatus));
        map.put(computerSkillKey, m.computerSkillRequired);
        map.put(languageRequiredKey, m.languageRequired);
        map.put(cargoForKey, m.cargoFor);
        map.put(cargoWeightKey, Integer.toString(m.cargoWeight));

        int len = 0;
        if (m.employmentRequirements.length == m.workExperienceYears.length) {
            StringBuffer erSb = new StringBuffer(), weySb = new StringBuffer();
            len = m.employmentRequirements.length;
            for (int i = 0; i < len; i++) {
                erSb.append(m.employmentRequirements[i]);
                weySb.append(m.workExperienceYears[i]);
                if (i != len - 1) {
                    erSb.append(',');
                    weySb.append(',');
                }
            }

            map.put(employKey, erSb.toString());
            map.put(workExperienceYearsKey, weySb.toString());
        } else {
            throw new Exception(employKey + ", " + workExperienceYearsKey + " not equivalent");
        }

        len = m.qualifications.length;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            sb.append(m.qualifications[i]);
            if (i != len - 1)
                sb.append(',');
        }
        map.put(qualificationsKey, sb.toString());

        sb.setLength(0);
        len = m.secondaryLanguages.length;
        for (int i = 0; i < len; i++) {
            sb.append(m.secondaryLanguages[i]);
            if (i != len - 1)
                sb.append(',');
        }
        map.put(secondLanguagesKey, sb.toString());

        if (m.employNumbers.length == m.occupations.length) {
            StringBuffer enSb = new StringBuffer(), oSb = new StringBuffer();
            len = m.employNumbers.length;
            for (int i = 0; i < len; i++) {
                enSb.append(m.employNumbers[i]);
                oSb.append(m.occupations[i]);
                if (i != len - 1) {
                    enSb.append(',');
                    oSb.append(',');
                }
            }

            map.put(occupationsKey, oSb.toString());
            map.put(employNumberKey, enSb.toString());
        } else {
            throw new Exception(occupationsKey + ", " + employNumberKey + " not equivalent");
        }

        return map;
    }


    public Mission() {
    }

    public long getMissionId() {
        return missionId;
    }

    public void setMissionId(long missionId) {
        this.missionId = missionId;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(Date launchDate) {
        this.launchDate = launchDate;
    }

    public int getMissionDuration() {
        return missionDuration;
    }

    public void setMissionDuration(int missionDuration) {
        this.missionDuration = missionDuration;
    }

    public String getMissionType() {
        return missionType;
    }

    public void setMissionType(String missionType) {
        this.missionType = missionType;
    }

    public String getMissionDescription() {
        return missionDescription;
    }

    public void setMissionDescription(String missionDescription) {
        this.missionDescription = missionDescription;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public String[] getQualifications() {
        return qualifications;
    }

    public void setQualifications(String[] qualifications) {
        this.qualifications = qualifications;
    }

    public String getComputerSkillRequired() {
        return computerSkillRequired;
    }

    public void setComputerSkillRequired(String computerSkillRequired) {
        this.computerSkillRequired = computerSkillRequired;
    }

    public String getLanguageRequired() {
        return languageRequired;
    }

    public void setLanguageRequired(String languageRequired) {
        this.languageRequired = languageRequired;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public String[] getSecondaryLanguages() {
        return secondaryLanguages;
    }

    public void setSecondaryLanguages(String[] secondaryLanguages) {
        this.secondaryLanguages = secondaryLanguages;
    }

    public int[] getWorkExperienceYears() {
        return workExperienceYears;
    }

    public void setWorkExperienceYears(int[] workExperienceYears) {
        this.workExperienceYears = workExperienceYears;
    }

    public int[] getEmployNumbers() {
        return employNumbers;
    }

    public void setEmployNumbers(int[] employNumbers) {
        this.employNumbers = employNumbers;
    }

    public int getCargoWeight() {
        return cargoWeight;
    }

    public void setCargoWeight(int cargoWeight) {
        this.cargoWeight = cargoWeight;
    }

    public String[] getEmploymentRequirements() {
        return employmentRequirements;
    }

    public void setEmploymentRequirements(String[] employmentRequirements) {
        this.employmentRequirements = employmentRequirements;
    }

    public String[] getOccupations() {
        return occupations;
    }

    public void setOccupations(String[] occupations) {
        this.occupations = occupations;
    }

    public String getCargoFor() {
        return cargoFor;
    }

    public void setCargoFor(String cargoFor) {
        this.cargoFor = cargoFor;
    }

    public int getMissionStatus() {
        return missionStatus;
    }

    public void setMissionStatus(int missionStatus) {
        this.missionStatus = missionStatus;
    }

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public long getCreator() {
        return creator;
    }

    public void setCreator(long creator) {
        this.creator = creator;
    }
}
