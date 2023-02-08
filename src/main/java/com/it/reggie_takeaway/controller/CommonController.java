package com.it.reggie_takeaway.controller;

import com.it.reggie_takeaway.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件下载上传
 */
@RestController
@RequestMapping("/common")
public class CommonController {

	/*
	动态定义地址
	 */
	@Value("${reggie.path}")
	private String basePath;

	/**
	 * 文件上传方法(这个文件请求上来了之后是在一个临时位置 C:/盘
	 * 需要转存到指定位置，否则这次请求结束会被删掉.transferTo方法
	 *
	 * @param file 方法接收参数向来都是要和前端传过来一样
	 * @return
	 */
	@PostMapping("/upload")
	public R<String> upload(MultipartFile file) {
		//原始文件名
		String filename = file.getOriginalFilename();
		//index找出截取的下标，再截取后缀
		String suffix = filename.substring(filename.lastIndexOf("."));

		//使用UUid来防止文件重复+后缀
		String ranFileName = UUID.randomUUID().toString() + suffix;

		//创建一个目录对象，判断是否存在这个目录
		File dir = new File(basePath);
		if (!dir.exists()) {
			//目录不存在则创造一个
			dir.mkdirs();
		}
		try {
			file.transferTo(new File(basePath + ranFileName));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return R.success(ranFileName);
	}

	/**
	 * 下载文件方法
	 * 上传方法之后立马通过name调用下载方法把图片回显
	 */
	@GetMapping("/download")
	public void download(HttpServletResponse response, String name) {
		try {
			//输入流从磁盘读取文件内容(地址加文件名
			FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
			//输出流把文件写回浏览器
			ServletOutputStream outputStream= response.getOutputStream();

			int len=0;
			byte[] bytes=new byte[1024];
			//没把bytes数组读满就一直读
			while((len=fileInputStream.read(bytes)) != -1) {
				outputStream.write(bytes,0,len);//从byte[0]读到byte[len-1]
				outputStream.flush();
			}

			fileInputStream.close();
			outputStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
