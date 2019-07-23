package com.themelove.tool.vas.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * 流处理线程
 * @author:lqs
 * date	  :2017年3月26日
 */
public class StreamThread extends Thread{
	private InputStream in;
	private OutputStream out;
	public StreamThread(InputStream in){
		this.in=in;
	}

	public StreamThread(InputStream in,OutputStream out){
		this.in=in;
		this.out=out;
	}
	
	@Override
	public void run() {
		BufferedReader br = null;
		PrintWriter pw = null;

		try {
			if (in!=null) br=new BufferedReader(new InputStreamReader(in));
			if (out!=null) pw=new PrintWriter(new OutputStreamWriter(out));
			String line=null;
			if (br==null) return;
			while((line=br.readLine())!=null){
				if (pw!=null) pw.println(line);
				System.out.println(line);
			}
			if (br!=null) br.close();
			if(pw!=null)  pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
				try {
					if (br!=null)br.close();
					if (pw!=null)pw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
		}
	}
	
}
