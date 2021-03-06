package org.system;

import gui.BusyBoxSelectGUI;
import gui.RecoveryBootSelectGUI;
import java.util.HashSet;
import org.adb.AdbUtility;
import org.logger.MyLogger;

public class DeviceEntry {


	PropertiesFile _entry;
	private static String fsep = OS.getFileSeparator();
	private Boolean hasBusybox=null;
	private boolean isRecoveryMode=false;
	
	public void queryAll() {
		setVersion();
		setKernelVersion();
		try {
			isRecoveryMode=!AdbUtility.isMounted("/system");
		}
		catch (Exception e) {
		}
	}

	public boolean hasRoot() {
		if (AdbUtility.hasRootNative()) return AdbUtility.hasRootNative();
		return AdbUtility.hasRootPerms();
	}

	public boolean isRecovery() {
		return isRecoveryMode;
	}

	public boolean hasSU() {
		try {
		return AdbUtility.hasSU();
		}
		catch (Exception e) {
			return false;
		}
	}

	public void rebootSelectedRecovery() throws Exception {
		RecoveryBootSelectGUI rsel = new RecoveryBootSelectGUI();
		rsel.setTitle("Recovery selector");
		String current = rsel.getVersion();
		if (current.length()>0) {
			MyLogger.getLogger().info("Rebooting into recovery mode");
			Shell shell = new Shell("rebootrecoveryt");
			shell.setProperty("RECOV_VERSION", current);
			shell.runRoot();
			MyLogger.getLogger().info("Phone will reboot into recovery mode");
		}
		else {
			MyLogger.getLogger().info("Canceled");
		}
	}
	
	public void setDefaultRecovery() throws Exception {
		RecoveryBootSelectGUI rsel = new RecoveryBootSelectGUI();
		String current = rsel.getVersion();
		if (current.length()>0) {
			if (AdbUtility.Sysremountrw()) {
			MyLogger.getLogger().info("Setting default recovery");
			Shell shell = new Shell("setdefaultrecovery");
			shell.setProperty("RECOV_VERSION", current);
			shell.runRoot();
			MyLogger.getLogger().info("Done");
			}
		}
		else {
			MyLogger.getLogger().info("Canceled");
		}
	}
	
	private void setKernelVersion() {
		_entry.setProperty("kernel.version", AdbUtility.getKernelVersion(isBusyboxInstalled()));
	}
	
	public String getKernelVersion() {
		return _entry.getProperty("kernel.version");
	}
	
	public DeviceEntry(PropertiesFile entry) {
		_entry = entry;
	}
	
	public String getId() {
		return _entry.getProperty("internalname");
	}
	
	public String getName() {
		return _entry.getProperty("realname");
	}
	
	public String getWorkDir() {
		return OS.getWorkDir()+fsep+"devices"+fsep+getId()+fsep+"work";
	}
	
	public String getDeviceDir() {
		return OS.getWorkDir()+fsep+"devices"+fsep+getId();
	}
	
	public String getBuildProp() {
		return _entry.getProperty("buildprop");
	}
	
	public String getLoaderMD5() {
		return _entry.getProperty("loader").toUpperCase();
	}

	public String getLoaderUnlockedMD5() {
		return _entry.getProperty("loader_unlocked").toUpperCase();
	}

	public boolean hasUnlockedLoader() {
		return _entry.getProperties().containsKey("loader_unlocked");
	}

	public String getBusyBoxInstallPath() {
		return _entry.getProperty("busyboxinstallpath");
	}
	
	public String getInstalledBusyboxVersion() {
		if (Devices.getCurrent().isBusyboxInstalled()) {
			return AdbUtility.getBusyboxVersion(getBusyBoxInstallPath());
		}
		else 
			return "N/A";

	}
	
	public HashSet<String> getRecognitionList() {
		String[] result = _entry.getProperty("recognition").split(",");
		HashSet<String> set = new HashSet<String>();
		for (int i=0;i<result.length;i++) {
			set.add(result[i]);
		}
		return set;
	}
	
	public void addRecognitionToList(String recog) {
		String current = _entry.getProperty("recognition");
		current = current + ","+recog;
		_entry.setProperty("recognition", current);
		_entry.write("ISO-8859-1");
	}
	
	public String getLoader() {
		return "./devices/"+_entry.getProperty("internalname")+"/loader.sin";
	}

	public String getLoaderUnlocked() {
		return "./devices/"+_entry.getProperty("internalname")+"/loader_unlocked.sin";
	}
	
	private void setVersion () {
		_entry.setProperty("android.release",DeviceProperties.getProperty("ro.build.version.release"));
	}
	
	public String getVersion() {
		return _entry.getProperty("android.release");
	}
	
	public boolean canFlash() {
		return _entry.getProperty("canflash").equals("true");
	}
	
	public boolean canKernel() {
		return (_entry.getProperty("cankernel").equals("true"));
	}

	public boolean canRecovery() {
		return (_entry.getProperty("canrecovery").equals("true"));
	}

	public boolean canFastboot() {
		return _entry.getProperty("canfastboot").equals("true");
	}

	public String getBusybox(boolean select) {
		String version;
		if (!select) version = _entry.getProperty("busyboxhelper");
		else {
			BusyBoxSelectGUI sel = new BusyBoxSelectGUI(getId());
			version = sel.getVersion();
		}
		if (version.length()==0) return "";
		else return "."+fsep+"devices"+fsep+_entry.getProperty("internalname")+fsep+"busybox"+fsep+version+fsep+"busybox";
	}
	
	public String getOptimize() {
		return "./devices/"+_entry.getProperty("internalname")+"/optimize.tar";
	}
	
	public String getBuildMerge() {
		return "./devices/"+_entry.getProperty("internalname")+"/build.prop";
	}

	public String getCharger() {
		return "./devices/"+_entry.getProperty("internalname")+"/charger";
	}

	public boolean isBusyboxInstalled() {
    	if (hasBusybox==null)
    		hasBusybox = (AdbUtility.getBusyboxVersion(getBusyBoxInstallPath()).length()>0);
    	return hasBusybox.booleanValue();
    }

    public void doBusyboxHelper() throws Exception {
    	if (!isBusyboxInstalled()) {
    		AdbUtility.push(getBusybox(false), GlobalConfig.getProperty("deviceworkdir")+"/busybox");
    		Shell shell = new Shell("busyhelper");
    		shell.run(true);
		}
    }

    public void reboot() throws Exception {
    	Shell s = new Shell("reboot");
    	s.runRoot(false);
    }
}
