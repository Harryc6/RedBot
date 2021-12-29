package com.nco.enums;


import java.util.HashMap;
import java.util.Map;

import static com.nco.enums.Crits.CritFixType.*;

public enum Crits {

    DISMEMBERED_ARM("Body", 2, "Dismembered Arm", QF_NA, T_SURGERY17),
    DISMEMBERED_HAND("Body", 3, "Dismembered Hand", QF_NA, T_SURGERY17),
    COLLAPSED_LUNG("Body", 4, "Collapsed Lung", QF_PARAMEDIC15, T_SURGERY15),
    BROKEN_RIBS("Body", 5, "Broken Ribs", QF_PARAMEDIC13, T_PARAMEDIC15, T_SURGERY13),
    BROKEN_ARM("Body", 6, "Broken Arm", QF_PARAMEDIC13, T_PARAMEDIC15, T_SURGERY13),
    FOREIGN_OBJECT_BODY("Body", 7, "Foreign Object In Body", QF_FIRST_AID13, QF_PARAMEDIC13, T_QUICK_FIX),
    BROKEN_LEG("Body", 8, "Broken Leg", QF_PARAMEDIC13, T_PARAMEDIC15, T_SURGERY13),
    TORN_MUSCLE("Body", 9, "Torn Muscle", QF_FIRST_AID13, QF_PARAMEDIC13, T_QUICK_FIX),
    SPINAL_INJURY("Body", 10, "Spinal Injury", QF_PARAMEDIC15, T_SURGERY15),
    CRUSHED_FINGERS("Body", 11, "Crushed Fingers", QF_PARAMEDIC13, T_SURGERY15),
    DISMEMBERED_LEG("Body", 12, "Dismembered Leg", QF_NA, T_SURGERY17),

    LOST_EYE("Head", 2, "Lost Eye", QF_NA, T_SURGERY17),
    BRAIN_INJURY("Head", 3, "Brain Injury", QF_NA, T_SURGERY17),
    DAMAGED_EYE("Head", 4, "Damaged Eye", QF_PARAMEDIC15, T_SURGERY13),
    CONCUSSION("Head", 5, "Concussion", QF_FIRST_AID13, QF_PARAMEDIC13, T_QUICK_FIX),
    BROKEN_JAW("Head", 6, "Broken Jaw", QF_PARAMEDIC13, T_PARAMEDIC13, T_SURGERY13),
    FOREIGN_OBJECT_HEAD("Head", 7, "Foreign Object In Head", QF_FIRST_AID13, QF_PARAMEDIC13, T_QUICK_FIX),
    WHIPLASH("Head", 8, "Whiplash", QF_PARAMEDIC13, T_PARAMEDIC13, T_SURGERY13),
    CRACKED_SKULL("Head", 9, "Cracked Skull", QF_PARAMEDIC15, T_PARAMEDIC15, T_SURGERY15),
    DAMAGED_EAR("Head", 10, "Damaged Ear", QF_PARAMEDIC13, T_SURGERY13),
    CRUSHED_WINDPIPE("Head", 11, "Crushed Windpipe", QF_NA, T_SURGERY15),
    LOST_EAR("Head", 12, "Lost Ear", QF_NA, T_SURGERY17);

    private static final Map<Integer, Crits> BY_HEAD_ROLL = new HashMap<>();
    private static final Map<Integer, Crits> BY_BODY_ROLL = new HashMap<>();
    private static final Map<String, Crits> BY_DESC = new HashMap<>();

    static {
        for (Crits crit : values()) {
            if (crit.headOrBody.equalsIgnoreCase("Head")) {
                BY_HEAD_ROLL.put(crit.roll, crit);
            } else {
                BY_BODY_ROLL.put(crit.roll, crit);
            }
            BY_DESC.put(crit.injuryDesc, crit);
        }
    }

    private final String headOrBody;
    private final int roll;
    public final String injuryDesc;
    public final CritFixType[] critFixTypes;

    Crits(String headOrBody, int roll, String injuryDesc, CritFixType... critFixTypes) {
        this.headOrBody = headOrBody;
        this.roll = roll;
        this.injuryDesc = injuryDesc;
        this.critFixTypes = critFixTypes;
    }

    public static Crits valueOfRoll(String table, int roll) {
        if (table.equalsIgnoreCase("Head")) {
            return BY_HEAD_ROLL.get(roll);
        } else {
            return BY_BODY_ROLL.get(roll);
        }
    }

    public static Crits valueOfDesc(String description) {
        return BY_DESC.get(description);
    }

    public enum CritFixType{
        QF_NA("Quick Fix", "N/A"),
        QF_PARAMEDIC13("Quick Fix", "Paramedic DV13"),
        QF_PARAMEDIC15("Quick Fix", "Paramedic DV15"),
        QF_FIRST_AID13("Quick Fix", "First Aid DV13"),
        T_SURGERY13("Treatment", "Surgery DV13"),
        T_SURGERY15("Treatment", "Surgery DV15"),
        T_SURGERY17("Treatment", "Surgery DV17"),
        T_PARAMEDIC15("Treatment", "Paramedic DV15"),
        T_PARAMEDIC13("Treatment", "Paramedic DV13"),
        T_QUICK_FIX("Treatment", "Quick Fix Removes Injury Effect Permanently");

        public final String type;
        public final String fix;

        CritFixType(String type, String fix) {
            this.type = type;
            this.fix = fix;
        }
    }
}


