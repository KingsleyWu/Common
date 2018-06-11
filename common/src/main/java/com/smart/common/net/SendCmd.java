package com.smart.common.net;

import com.smart.common.util.ThreadUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SendCmd {

	private static SendCmd mySendCmd = null;
	
	private SendCmd(){}
	
	public static SendCmd get(){
		if(mySendCmd == null){
			mySendCmd = new SendCmd();
		}
		
		return mySendCmd;
	}
	
	
	public String system_rw(){
		return send("mount -o rw,remount -t ext4 /system");
	}
	
	public String system_ro(){
		return send("mount -o ro,remount -t ext4 /system");
	}
	
	public String oem_rwx() {
        return send("chmod -R 777 /mnt/oem");
    }
	
	public String etc_rwx() {
        return send("chmod -R 777 /data/etc");
    }
	
	public String linux_cp(String oldfile,String newfile) {
	    //linux cp命令
         send("cp "+oldfile +" "+newfile);
         return send("chmod -R 777 "+newfile);
    }
	
	public String linux_dir_cp(String oldfile,String newfile) {
	     oldfile = oldfile.endsWith("/")?oldfile:(oldfile+"/");
         //linux cp命令 复制文件夹下所有文件
         send("cp -r "+oldfile +"* "+newfile);
         return send("chmod -R 777 "+newfile);
    }
	
	/**
	 * 执行需要root权限的命令
	 * @param cmd
	 * @return 如果命令有返回值，则返回返回值，如果命令没有返回值，返回OK
	 */
	public String exec(String cmd) {
	    return send(cmd);
	}
	
	public void chmodFile(int mode, String path) {
	    final String command = "chmod " + mode + " " + path;
	    if (Thread.currentThread().getName().equals("main")){
			ThreadUtil.getSinglePool().submit(new Runnable() {
				@Override
				public void run() {
					send(command);
				}
			});
	    } else {
	        send(command);
	    }
	}
	
	/**
	 * 删除文件，谨慎调用
	 * @param path
	 * @return
	 */
	public String rmFile(String path) {
	    return send("rm "+path);
	}
	
	private synchronized String send(String cmd){
		StringBuilder result = new StringBuilder();
		Socket socket = null;
		OutputStream os = null;
		InputStream is = null;
		
		try {
			socket = new Socket("127.0.0.1",40000);
			os = socket.getOutputStream();
			
			PrintWriter pw= new PrintWriter(os);
			pw.write(cmd);
			pw.flush();
			
			is = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String info = null;
            while((info = br.readLine())!=null){
            	result.append(info);
            }
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
				
		return result.toString();
	}

    public void clearLauncherData(final String packageName) {
		ThreadUtil.getSinglePool().submit(new Runnable() {
			@Override
			public void run() {
				send("pm clear "+packageName);
			}
		});
    }
}
