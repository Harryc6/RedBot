package com.nco.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RPGDice {

    private static final Pattern DICE_PATTERN = Pattern.compile("(?<A>\\d*)d(?<B>\\d+)(?>(?<MULT>[*/])(?<C>\\d+))?(?>(?<ADD>[+-])(?<D>\\d+))?");

    private int rolls = 0;
    private int faces = 0;
    private int multiplier = 0;
    private int additive = 0;

    public RPGDice(int rolls, int faces, int multiplier, int additive) {
        this.rolls = rolls;
        this.faces = faces;
        this.multiplier = multiplier;
        this.additive = additive;
    }

    public int getRolls() {
        return rolls;
    }

    public int getFaces() {
        return faces;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public int getAdditive() {
        return additive;
    }

    public String toString() {
        return String.format("{\"rolls\": %s, \"faces\": %s, \"multiplier\": %s, \"additive\": %s}", rolls, faces, multiplier, additive);
    }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private static Integer getInt(Matcher matcher, String group, int defaultValue) {
        String groupValue = matcher.group(group);
        return isEmpty(groupValue) ? defaultValue : Integer.parseInt(groupValue);
    }

    private static Integer getSign(Matcher matcher, String group, String positiveValue) {
        String groupValue = matcher.group(group);
        return isEmpty(groupValue) || groupValue.equals(positiveValue) ? 1 : -1;
    }

    public static RPGDice parse(String str) {
        str = str.toLowerCase().replace(" ", "");
        Matcher matcher = DICE_PATTERN.matcher(str);
        if(matcher.matches()) {
            int rolls = getInt(matcher, "A", 1);
            int faces = getInt(matcher, "B", -1);
            int multiplier = getInt(matcher, "C", 1);
            int additive = getInt(matcher, "D", 0);
            int multiplierSign = getSign(matcher, "MULT", "*");
            int additiveSign = getSign(matcher, "ADD", "+");
            return new RPGDice(rolls, faces, multiplier * multiplierSign, additive * additiveSign);
        }
        return null;
    }

    public static int roll(String str) {
        RPGDice rpgDice = RPGDice.parse(str);
        if (rpgDice == null) {
            return 0;
        }
        double total = 0.0;
        for (int i = 0; i < rpgDice.getRolls(); i++) {
            total += NumberUtils.getRandomNumber(1, rpgDice.getFaces());
        }
        if (rpgDice.getMultiplier() > 0) {
            total = total * rpgDice.getMultiplier();
        } else if (rpgDice.getMultiplier() < 0) {
            total = Math.ceil(total / (rpgDice.getMultiplier() * -1));
        } else {
            return 0;
        }
        total += rpgDice.getAdditive();
        return ((int) total);
    }

}
