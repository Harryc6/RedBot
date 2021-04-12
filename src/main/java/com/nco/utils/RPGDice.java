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

    @Override
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
        str = str.toLowerCase();
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

    public static void main(String[] args) {
        System.out.println(RPGDice.parse("d6"));
        System.out.println(RPGDice.parse("d6*"));
        System.out.println(RPGDice.parse("33d6*10"));
        System.out.println(RPGDice.parse("336*10"));
        System.out.println(RPGDice.parse("d6/"));
        System.out.println(RPGDice.parse("d6/5"));
        System.out.println(RPGDice.parse("d6/5+2"));
        System.out.println(RPGDice.parse("2d6/5-32"));
        System.out.println(RPGDice.parse("2d6/5+-32"));
        System.out.println(RPGDice.parse("2D6/2"));

        System.out.println(RPGDice.roll("d6"));
        System.out.println(RPGDice.roll("d6*"));
        System.out.println(RPGDice.roll("33d6*10"));
        System.out.println(RPGDice.roll("336*10"));
        System.out.println(RPGDice.roll("d6/"));
        System.out.println(RPGDice.roll("d6/5"));
        System.out.println(RPGDice.roll("d6/5+2"));
        System.out.println(RPGDice.roll("2d6/5-32"));
        System.out.println(RPGDice.roll("2d6/5+-32"));
        System.out.println(RPGDice.roll("2D6/2"));
    }

    public static String roll(String str) {
        RPGDice rpgDice = RPGDice.parse(str);
        if (rpgDice == null) {
            return null;
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
            return null;
        }
        total += rpgDice.getAdditive();
        return String.valueOf((int) total);
    }
//
//    public static int rollDice(int dice, int diceSize, boolean half) {
//        double d = diceSize;
//        if (half) {
//            diceSize = (int) Math.ceil(d / 2);
//        }
////        return getRandomNumber(dice , dice * diceSize);
//    }

}
