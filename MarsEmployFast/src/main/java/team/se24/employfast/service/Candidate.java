package team.se24.employfast.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.java2d.pipe.SpanShapeRenderer;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Candidate {
    private static ObjectMapper mapper = null;
    public static final String nameKey = "name";
    public static final String dobKey = "dob";
    public static final String genderKey = "gender";
    public static final String countryKey = "country";
    public static final String addressKey = "address";
    public static final String postalKey = "postal";
    public static final String identificationKey = "identification";
    public static final String allergiesKey = "allergies";
    public static final String foodPrefKey = "foodPreference";
    public static final String qualificationKey = "qualifications";
    public static final String workYearsKey = "years of work experience";
    public static final String occupationKey = "occupations";
    public static final String computerSkillKey = "computer skill";
    public static final String languageKey = "languages known";
    public static final String[] basicKeys = {nameKey, dobKey, genderKey, countryKey, postalKey};
    public static final String[] allKeys = {nameKey, dobKey, genderKey, countryKey, postalKey, addressKey,
                            identificationKey, allergiesKey, foodPrefKey, qualificationKey,
                            workYearsKey, occupationKey, computerSkillKey, languageKey};

    private long userId;
    private String name;
    private String gender;
    private Date dob;
    private String address;
    private String country;
    private String postal;
    private String identification;
    private String allergies;
    private String foodPreference;
    private String[] qualifications;
    private Map<String, Integer> workExperience;
    private String computerSkill;
    private String[] languageKnown;

    public static Map<String, String> parseRequestCandidateInfo(HttpServletRequest req) throws Exception {
        Map<String, String> candidateInfoMap = new HashMap<>();
        for (String key : Candidate.allKeys) {
            String val = req.getParameter(key);
            if (val != null)
                candidateInfoMap.put(key, val);
            else
                throw new Exception(key + " missed");
        }
        return candidateInfoMap;
    }

    public static Candidate parseCandidate(Map<String, String> candidateMap) throws Exception {
        String val = null;
        Candidate cand = new Candidate();
        if ((val = candidateMap.get(User.userIdKey)) != null) {
            cand.setUserId(Long.parseLong(val));
            val = null;
        } else {
            throw new Exception(User.userIdKey + " missed");
        }
        if ((val = candidateMap.get(nameKey)) != null) {
            cand.setName(val);
            val = null;
        } else {
            throw new Exception(nameKey + " missed");
        }
        if ((val = candidateMap.get(genderKey)) != null) {
            cand.setGender(val.toLowerCase());
            val = null;
        } else {
            throw new Exception(genderKey + " missed");
        }
        if ((val = candidateMap.get(dobKey)) != null) {
            cand.setDob(new SimpleDateFormat("MM/dd/yyyy").parse(val));
            val = null;
        } else {
            throw new Exception(dobKey + " missed");
        }
        if ((val = candidateMap.get(countryKey)) != null) {
            cand.setCountry(val);
            val = null;
        } else {
            throw new Exception(countryKey + " missed");
        }
        if ((val = candidateMap.get(addressKey)) != null) {
            cand.setPostal(val);
            val = null;
        } else {
            throw new Exception(addressKey + " missed");
        }
        if ((val = candidateMap.get(postalKey)) != null) {
            cand.setPostal(val);
            val = null;
        } else {
            throw new Exception(postalKey + " missed");
        }
        if ((val = candidateMap.get(identificationKey)) != null) {
            cand.setIdentification(val);
            val = null;
        } else {
            throw new Exception(identificationKey + " missed");
        }
        if ((val = candidateMap.get(allergiesKey)) != null) {
            cand.setAllergies(val);
            val = null;
        } else {
            throw new Exception(allergiesKey + " missed");
        }
        if ((val = candidateMap.get(foodPrefKey)) != null) {
            cand.setFoodPreference(val);
            val = null;
        } else {
            throw new Exception(foodPrefKey + " missed");
        }
        if ((val = candidateMap.get(qualificationKey)) != null) {
            String[] splits = val.split(",");
            for (int i = 0; i < splits.length; i++){
                String s = splits[i].trim();
                if (s != null && s.length() != 0)
                    splits[i] = s;
            }
            if (splits.length == 0)
                throw new Exception(qualificationKey + " no value");
            cand.setQualifications(splits);
            val = null;
        } else {
            throw new Exception(qualificationKey + " missed");
        }
        if ((val = candidateMap.get(computerSkillKey)) != null) {
            cand.setComputerSkill(val);
            val = null;
        } else {
            throw new Exception(computerSkillKey + " missed");
        }
        if ((val = candidateMap.get(languageKey)) != null) {
            String[] splits = val.split(",");
            for (int i = 0; i < splits.length; i++) {
                String s = splits[i].trim();
                if (s != null && s.length() != 0)
                    splits[i] = s;
            }
            if (splits.length == 0)
                throw new Exception(languageKey + " no value");
            cand.setLanguageKnown(splits);
            val = null;
        } else {
            throw new Exception(languageKey + " missed");
        }

        String[] occupations = null;
        int[] workYears = null;
        if ((val = candidateMap.get(occupationKey)) != null) {
            occupations = val.split(",");
            for (int i = 0; i < occupations.length; i++){
                String s = occupations[i].trim();
                if (s != null && s.length() != 0)
                    occupations[i] = s;
            }
            val = null;
        } else {
            throw new Exception(occupationKey + " missed");
        }
        workYears = new int[occupations.length];
        if ((val = candidateMap.get(workYearsKey)) != null) {
            String[] splits = val.split(",");
            if (splits.length == occupations.length) {
                for (int i = 0; i < splits.length; i++) {
                    splits[i] = splits[i].trim();
                    if (splits[i] != null && splits[i].length() != 0) {
                        workYears[i] = Integer.parseInt(splits[i]);
                        if (workYears[i] == 0)
                            throw new Exception("Work Experience Years could not be 0");
                    }
                }
            } else
                throw new Exception("Work Experience Years must be equivalent with Occupations");
            val = null;
        } else {
            throw new Exception(workYearsKey + " missed");
        }

        if (occupations != null && workYears != null && occupations.length == workYears.length) {
            Map<String, Integer> map = new HashMap<>();
            for (int i = 0; i < occupations.length; i++) {
                map.put(occupations[i], workYears[i]);
            }
            cand.setWorkExperience(map);
        }

        return cand;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getFoodPreference() {
        return foodPreference;
    }

    public void setFoodPreference(String foodPreference) {
        this.foodPreference = foodPreference;
    }

    public String[] getQualifications() {
        return qualifications;
    }

    public void setQualifications(String[] qualifications) {
        this.qualifications = qualifications;
    }

    public Map<String, Integer> getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(Map<String, Integer> workExperience) {
        this.workExperience = workExperience;
    }

    public String getComputerSkill() {
        return computerSkill;
    }

    public void setComputerSkill(String computerSkill) {
        this.computerSkill = computerSkill;
    }

    public String[] getLanguageKnown() {
        return languageKnown;
    }

    public void setLanguageKnown(String[] languageKnown) {
        this.languageKnown = languageKnown;
    }

    public Candidate() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
