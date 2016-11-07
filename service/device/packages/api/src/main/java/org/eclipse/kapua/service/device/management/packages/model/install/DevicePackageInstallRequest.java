package org.eclipse.kapua.service.device.management.packages.model.install;

/**
 * Device package install request definition.
 * 
 * @since 1.0
 *
 */
public interface DevicePackageInstallRequest {

    /**
     * Get the package name
     * 
     * @return
     */
    public String getName();

    /**
     * Set the package name
     * 
     * @param name
     */
    public void setName(String name);

    /**
     * Get the package version
     * 
     * @return
     */
    public String getVersion();

    /**
     * Set the package version
     * 
     * @param version
     */
    public void setVersion(String version);

    /**
     * Get the device reboot flag
     * 
     * @return
     */
    public Boolean isReboot();

    /**
     * Set the device reboot flag
     * 
     * @param reboot
     */
    public void setReboot(Boolean reboot);

    /**
     * Get the reboot delay
     * 
     * @return
     */
    public Integer getRebootDelay();

    /**
     * Set the reboot delay
     * 
     * @param rebootDelay
     */
    public void setRebootDelay(Integer rebootDelay);
}
