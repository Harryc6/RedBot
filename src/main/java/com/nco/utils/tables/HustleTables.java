package com.nco.utils.tables;

import com.nco.pojos.PlayerCharacter;

import java.util.ArrayList;

public class HustleTables {

    public static ArrayList<HustleRow> getRoleTable(PlayerCharacter pc) {
        switch (pc.getRole().toUpperCase()) {
            case "ROCKERBOY":
                return getRockerboyTable();
            case "SOLO":
                return getSoloTable();
            case "NETRUNNER":
                return getNetrunnerTable();
            case "TECH":
                return getTechTable();
            case "MEDTECH":
                return getMedtechTable();
            case "MEDIA":
                return getMediaTable();
            case "LAWMAN":
                return getLawmanTable();
            case "EXEC":
                return getExecTable();
            case "FIXER":
                return getFixerTable();
            case "NOMAD":
                return getNomadTable();
            default:
                return new ArrayList<>();
        }
    }


    public static ArrayList<HustleRow> getRockerboyTable() {
        ArrayList<HustleRow> table = new ArrayList<>();
        table.add(new HustleRow("Played a small local gig.", 200, 300, 600));
        table.add(new HustleRow("No gigs or jobs to be had this week.", 0, 100, 300));
        table.add(new HustleRow("Played a big gig for a rich Corporate or Local Personality.", 300, 500, 800));
        table.add(new HustleRow("Got some royalties in for your most recent Data Pool download.", 300, 500, 800));
        table.add(new HustleRow("Opening act for a Big-Name group.", 300, 500, 800));
        table.add(new HustleRow("Personal appearance netted you a large fee.", 200, 300, 600));
        return table;
    }

    public static ArrayList<HustleRow> getSoloTable() {
        ArrayList<HustleRow> table = new ArrayList<>();
        table.add(new HustleRow("Bodyguard work, low-end client.", 100, 200, 500));
        table.add(new HustleRow("Bodyguard work, high-end client.", 200, 300, 600));
        table.add(new HustleRow("Difficult hit or extraction.", 200, 300, 600));
        table.add(new HustleRow("Hired out as muscle to a Fixer, Corp, or Gang.", 100, 200, 500));
        table.add(new HustleRow("Attracted undue attention, had to lay low.", 0, 100, 300));
        table.add(new HustleRow("Basic enforcer or hitman work for a local Corp.", 100, 200, 500));
        return table;
    }

    public static ArrayList<HustleRow> getNetrunnerTable() {
        ArrayList<HustleRow> table = new ArrayList<>();
        table.add(new HustleRow("Cracked a small system and sold the data.", 100, 200, 500));
        table.add(new HustleRow("Cracked a major Corporate system and sold the data.", 200, 300, 600));
        table.add(new HustleRow("You got sidetracked and didn’t hack anything this week.", 0, 100, 300));
        table.add(new HustleRow("Found a valuable data cache in an abandoned system and sold it.", 200, 300, 600));
        table.add(new HustleRow("Brought down a major system with ransomware and got paid off to uninstall it.", 200, 300, 600));
        table.add(new HustleRow("Sabotaged or otherwise disabled a major system for a faceless client", 200, 300, 600));
        return table;
    }

    public static ArrayList<HustleRow> getTechTable() {
        ArrayList<HustleRow> table = new ArrayList<>();
        table.add(new HustleRow("No jobs this week.", 0, 100, 300));
        table.add(new HustleRow("Rebuilt some tech you scavenged in the Combat Zone.", 100, 200, 500));
        table.add(new HustleRow("Helped a client break into some place or installed security systems for a client.", 200, 300, 600));
        table.add(new HustleRow("Did some modifications or repairs to some cybertech.", 100, 200, 500));
        table.add(new HustleRow("Did some modifications or repairs to some weapons.", 100, 200, 500));
        table.add(new HustleRow("Sabotaged or otherwise disabled something for a client.", 100, 200, 500));
        return table;
    }

    public static ArrayList<HustleRow> getMedtechTable() {
        ArrayList<HustleRow> table = new ArrayList<>();
        table.add(new HustleRow("Patched up someone after a firefight.", 100, 200, 500));
        table.add(new HustleRow("Sold cyberware from a \"failed\" medical case.", 200, 300, 600));
        table.add(new HustleRow("Helped Trauma Team on some backup work when they were overloaded.", 100, 200, 500));
        table.add(new HustleRow("Did some minor \"free clinic\" work for locals. You can’t eat goodwill though.", 0, 100, 300));
        table.add(new HustleRow("Did a major medical procedure for a very well-heeled client.", 200, 300, 600));
        table.add(new HustleRow("Designed and delivered medicines or street drugs to a client.", 100, 200, 500));
        return table;
    }

    public static ArrayList<HustleRow> getMediaTable() {
        ArrayList<HustleRow> table = new ArrayList<>();
        table.add(new HustleRow("Wrote an expose that covered a major topic, made a big sale.", 300, 500, 800));
        table.add(new HustleRow("Wrote a popular \"puff piece\" that got you some notice and some cash.", 200, 300, 600));
        table.add(new HustleRow("Did some boring ad writing to pay the bills.", 200, 300, 600));
        table.add(new HustleRow("Exposed a big story that got you a few enemies and some cash.", 200, 300, 600));
        table.add(new HustleRow("No good stories or leads this week.", 0, 100, 300));
        table.add(new HustleRow("Wrote an expose that blew the lid off a major topic.", 300, 500, 800));
        return table;
    }

    public static ArrayList<HustleRow> getLawmanTable() {
        ArrayList<HustleRow> table = new ArrayList<>();
        table.add(new HustleRow("Made a few minor busts, business as usual.", 100, 200, 500));
        table.add(new HustleRow("Got a reward from a grateful citizen. Or was it a bribe?", 200, 300, 600));
        table.add(new HustleRow("Bust went bad, and it came out of your salary.", 0, 100, 300));
        table.add(new HustleRow("Nothing much happened this week. Collected a paycheck and that was it.", 100, 200, 500));
        table.add(new HustleRow("Pulled off a major drug or smuggling bust and gained a bonus from the boss.", 200, 300, 600));
        table.add(new HustleRow("Took down a big gang and got some of a \"civil seizure\" bonus.", 300, 500, 800));
        return table;
    }

    public static ArrayList<HustleRow> getExecTable() {
        ArrayList<HustleRow> table = new ArrayList<>();
        table.add(new HustleRow("Landed a moderate success on a project, earned a reward bonus.", 300, 500, 800));
        table.add(new HustleRow("Nothing much happened, and Corporate was unimpressed. Lost a bonus.", 0, 100, 300));
        table.add(new HustleRow("Collected a paycheck and that was it.", 200, 300, 600));
        table.add(new HustleRow("Got some dirt on a rival and used it to score a bonus.", 300, 500, 800));
        table.add(new HustleRow("Pulled off a major project success and gained a bonus from the Head Office.", 300, 500, 800));
        table.add(new HustleRow("Took out a legitimate target that was threatening a job and took their funding.", 200, 300, 600));
        return table;
    }

    public static ArrayList<HustleRow> getFixerTable() {
        ArrayList<HustleRow> table = new ArrayList<>();
        table.add(new HustleRow("Got a Media some information for a good bribe.", 200, 300, 600));
        table.add(new HustleRow("Got a Rocker a good Gig for your 12% fee.", 200, 300, 600));
        table.add(new HustleRow("Helped a client locate a desirable item they needed and got a cut.", 200, 300, 600));
        table.add(new HustleRow("Deal went south; you’re keeping your head down till it blows over.", 0, 100, 300));
        table.add(new HustleRow("Got a Solo or Netrunner a profitable \"job\" and took your agency fee.", 200, 300, 600));
        table.add(new HustleRow("Brought in a rare, illegal, or very hard to get item for a client.", 300, 500, 800));
        return table;
    }

    public static ArrayList<HustleRow> getNomadTable() {
        ArrayList<HustleRow> table = new ArrayList<>();
        table.add(new HustleRow("Made a legit shipment.", 100, 200, 500));
        table.add(new HustleRow("Protected a shipment.", 100, 200, 500));
        table.add(new HustleRow("Smuggled some small contraband.", 100, 300, 600));
        table.add(new HustleRow("Smuggled a huge shipment.", 200, 300, 600));
        table.add(new HustleRow("Delivered a client safely to destination.", 100, 200, 500));
        table.add(new HustleRow("Couldn’t find work this week, legit or otherwise.", 0, 100, 300));
        return table;
    }

}
