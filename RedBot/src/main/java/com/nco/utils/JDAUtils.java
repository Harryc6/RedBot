package com.nco.utils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class JDAUtils {

    public static Role findRole(Member member, String name) {
        List<Role> roles = member.getRoles();
        return roles.stream()
                .filter(role -> role.getName().equals(name)) // filter by role name
                .findFirst() // take first result
                .orElse(null); // else return null
    }

    public static boolean hasRole(Member member, String roleName) {
        List<Role> roles = member.getRoles();
        return roles.stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(roleName));
    }

    public static boolean hasRoleIn(Member member, String[] roles) {
        for (String role: roles) {
            if (hasRole(member, role)) {
                return true;
            }
        }
        return false;
    }
}
