package com.aegal.frontend.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: A.Egal
 * Date: 9/6/14
 * Time: 7:26 PM
 */
@Path("/system")
public class SystemResource {

    public static class SystemValues {
        public int availableProcessors;
        public long freeJVMMemoryInBytes;
        public long totalJVMMemoryInBytes;
        public long freeSystemMemory;
        public long totalSystemMemory;

        public List<SystemStorageInformation> systemStorageInformationList = new ArrayList<>();
    }

    public static class SystemStorageInformation {
        public String absolutePath;
        public long totalspace;
        public long freespace;
        public long usablespace;
    }

    @GET
    @Path("info")
    @Produces("application/json")
    public SystemValues getSystemResourceValues() {
        SystemValues systemValues = new SystemValues();

        systemValues.availableProcessors = Runtime.getRuntime().availableProcessors();
        systemValues.freeJVMMemoryInBytes = Runtime.getRuntime().freeMemory();
        systemValues.freeSystemMemory = Runtime.getRuntime().maxMemory();
        systemValues.totalJVMMemoryInBytes = Runtime.getRuntime().totalMemory();

        com.sun.management.OperatingSystemMXBean bean =
                (com.sun.management.OperatingSystemMXBean)
                        java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        systemValues.totalSystemMemory = bean.getTotalPhysicalMemorySize();

        File[] roots = File.listRoots();

        for (File root : roots) {
            SystemStorageInformation systemStorageInformation = new SystemStorageInformation();

            systemStorageInformation.absolutePath = root.getAbsolutePath();
            systemStorageInformation.totalspace = root.getTotalSpace();
            systemStorageInformation.freespace = root.getFreeSpace();
            systemStorageInformation.usablespace = root.getUsableSpace();

            systemValues.systemStorageInformationList.add(systemStorageInformation);
        }

        return systemValues;

    }

}
