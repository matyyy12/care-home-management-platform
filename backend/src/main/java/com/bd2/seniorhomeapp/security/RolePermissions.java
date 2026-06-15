package com.bd2.seniorhomeapp.security;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import com.bd2.seniorhomeapp.authentication.entity.AppRole;

//to macierz uprawnień jbc 
public final class RolePermissions {

    private static final Map<AppModule, Set<AppRole>> READ = new EnumMap<>(AppModule.class);
    private static final Map<AppModule, Set<AppRole>> WRITE = new EnumMap<>(AppModule.class);

    static {
        READ.put(AppModule.SENIORS, EnumSet.of(
                AppRole.DIRECTOR, AppRole.MANAGER, AppRole.EMPLOYEE, AppRole.MEDICAL));
        READ.put(AppModule.SENIOR_HOMES, EnumSet.of(AppRole.DIRECTOR));
        READ.put(AppModule.ADDRESSES, EnumSet.of(AppRole.DIRECTOR));
        READ.put(AppModule.EMPLOYEES, EnumSet.of(AppRole.DIRECTOR, AppRole.MANAGER));
        READ.put(AppModule.POSITIONS, EnumSet.of(AppRole.DIRECTOR));
        READ.put(AppModule.ROOMS, EnumSet.of(AppRole.DIRECTOR, AppRole.MANAGER, AppRole.EMPLOYEE));
        READ.put(AppModule.ACTIVITIES, EnumSet.of(
                AppRole.DIRECTOR, AppRole.MANAGER, AppRole.EMPLOYEE, AppRole.MEDICAL));
        READ.put(AppModule.EQUIPMENT, EnumSet.of(AppRole.DIRECTOR));
        READ.put(AppModule.ASSIGNMENTS, EnumSet.of(
                AppRole.DIRECTOR, AppRole.MANAGER, AppRole.EMPLOYEE, AppRole.MEDICAL));
        READ.put(AppModule.REPORTS, EnumSet.of(AppRole.DIRECTOR, AppRole.MANAGER, AppRole.MEDICAL));
        READ.put(AppModule.REPORT_ROOM_OCCUPANCY, EnumSet.of(AppRole.DIRECTOR, AppRole.MANAGER));
        READ.put(AppModule.REPORT_SENIOR_ACTIVITY, EnumSet.of(
                AppRole.DIRECTOR, AppRole.MANAGER, AppRole.MEDICAL));
        READ.put(AppModule.REPORT_EMPLOYEE_WORKLOAD, EnumSet.of(AppRole.DIRECTOR, AppRole.MANAGER));
        READ.put(AppModule.REPORT_DATA_QUALITY, EnumSet.of(AppRole.DIRECTOR));
        READ.put(AppModule.REPORT_ROOM_UTILIZATION, EnumSet.of(AppRole.DIRECTOR, AppRole.MANAGER));
        READ.put(AppModule.REPORT_MEDICAL_ACTIVITY, EnumSet.of(
                AppRole.DIRECTOR, AppRole.MANAGER, AppRole.MEDICAL));
        READ.put(AppModule.ADMINISTRATION, EnumSet.of(AppRole.DIRECTOR, AppRole.ADMIN_TECH));

        WRITE.put(AppModule.SENIORS, EnumSet.of(AppRole.DIRECTOR, AppRole.MANAGER));
        WRITE.put(AppModule.SENIOR_HOMES, EnumSet.of(AppRole.DIRECTOR));
        WRITE.put(AppModule.ADDRESSES, EnumSet.of(AppRole.DIRECTOR));
        WRITE.put(AppModule.EMPLOYEES, EnumSet.of(AppRole.DIRECTOR, AppRole.MANAGER));
        WRITE.put(AppModule.POSITIONS, EnumSet.of(AppRole.DIRECTOR));
        WRITE.put(AppModule.ROOMS, EnumSet.of(AppRole.DIRECTOR, AppRole.MANAGER));
        WRITE.put(AppModule.ACTIVITIES, EnumSet.of(AppRole.DIRECTOR, AppRole.MANAGER));
        WRITE.put(AppModule.EQUIPMENT, EnumSet.of(AppRole.DIRECTOR));
        WRITE.put(AppModule.ASSIGNMENTS, EnumSet.of(
                AppRole.DIRECTOR, AppRole.MANAGER, AppRole.EMPLOYEE, AppRole.MEDICAL));
        WRITE.put(AppModule.ADMINISTRATION, EnumSet.of(AppRole.ADMIN_TECH));
    }

    private RolePermissions() {
    }

    public static boolean canRead(AppRole role, AppModule module) {
        return READ.getOrDefault(module, Set.of()).contains(role);
    }

    public static boolean canWrite(AppRole role, AppModule module) {
        return WRITE.getOrDefault(module, Set.of()).contains(role);
    }
}
