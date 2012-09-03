
package com.manuelnaranjo.btle.installer;

import android.util.Log;

import com.stericson.RootTools.Command;
import com.stericson.RootTools.CommandCapture;
import com.stericson.RootTools.RootTools;

import java.io.File;

public class RemoveProcess extends Thread {
    private static final String WRAPPER_PATH = InstallProcess.WRAPPER_PATH;
    private InstallerListener mListener;
    
    public RemoveProcess(InstallerListener l) {
        mListener = l;
        mListener.clearLog();
    }
    
    public int removeFile(String p){
        try {
            RootTools.remount("/system", "RW");
            Command cmd = RootTools.getShell(true).add(
                    new CommandCapture(0, "rm -rf " + p));
            if (cmd.exitCode()==0){
                return 0;
            } else {
                return 1;
            }
        } catch (Exception e) {
            Log.e(StatusActivity.TAG, "rm error", e);
        }
        return 2;
    }
   
    public void run() {
        RootTools.debugMode = true;
        
        if (new File(WRAPPER_PATH+".orig").exists()){
            if (!RootTools.copyFile(WRAPPER_PATH+".orig", WRAPPER_PATH, true, true)){
                mListener.addToLog("Failed to overwrite wrapper with original");
            } else {
                mListener.addToLog("Recovered original");
            }
        }

        switch (removeFile(WRAPPER_PATH+".orig")){
            case 0:
                mListener.addToLog("Removed wrapper completely");
                break;
            default:
                mListener.addToLog("Failed removing backup");
        }
        
        switch (removeFile(InstallProcess.FRAMEWORK_PATH)){
            case 0:
                mListener.addToLog("Removed jar completely");
                break;
            default:
                mListener.addToLog("Failed removing jar");
        }
        
        switch (removeFile(InstallProcess.PERM_PATH)){
            case 0:
                mListener.addToLog("Removed permisions completely");
                break;
            default:
                mListener.addToLog("Failed removing permissions");
        }
        
        switch (removeFile(InstallProcess.LAUNCH_PATH)){
            case 0:
                mListener.addToLog("Removed launcher completely");
                break;
            default:
                mListener.addToLog("Failed removing launcher");
        }
        
        RootTools.remount("/system", "RO");
        mListener.addToLog("Framework removed");
        mListener.addToLog("It's better if you restart your cellphone");
        mListener.reboot();
    }
}
