# API Helper #


----------
Generates raw comments for ApiDoc to parse and generate the API documents. 

For now, API Helper generate ApiDoc, Postman and Markdown files under different folders after run once.

## Usage ##

1. Please make sure that you have installed **ApiDoc** tool.  If you don't know how to use it, please refer to [here](https://github.com/apidoc/apidoc).

        1.  Install nodejs: http://nodejs.cn/download
        2.  Install apidoc: npm install -g apidoc 

2. When ApiDoc is ready, please  create a property file called "*apiHelper.properties*" and set necessary properties. Please refer to the *Configuration* part.

3. Please copy templates from src/main/resources/template under your "templatePath". For now, there are four templates:

		apidoc_template.ftl
		It is the whole template for ApiDoc. We don't suggest you to change it.

		markdown_template.ftl
		It is the whole template for Markdown. 
		You could re-write it with your pleasure. Please refer to the "How to write your own templates" part before you re-design.

		response_json.ftl
		It is the response json template. 
		Because different company/teams have different return json format, please feel free to re-design it. 

		help.ftl
		It is not so useful, the help message just displays in console after run the program.
		When you re-design your templates, you might write your help messages here. 

4. After set the properties, you could double click **runApiHelper.bat** directly, it would generate __autoAPI. Of course, you could run it manually as well:

	    # generate temp files： __autoAPI/apidoc 和 __autoAPI/postman 
		java -jar ApiHelper.jar 
	 
		# generate final Api Documents： final目录
	    apidoc -i __autoAPI/apidoc -o final/ -f ".txt" 

	**runApiHelper.bat**：

		java -jar ApiHelper.jar 

		ping -n 20 127.1>nul | apidoc -i __autoAPI/apidoc -o final/ -f ".txt"


5. Import APIs into Postman
       
		Open Postman tool and click the Import button，please choose __autoAPI/postman/postman.json to upload.


## Configuration ##
 
	# Necessary setting
    # Please set your project/module home here
	# It would be great when "$modulePath/src/main/java" and "$modulePath/target/classes" folders exist.
    modulePath=D:/workspace/tcc
   
	# Ncessary setting
	# Please set your template directory here.
	# The program would locate freemarker templates including "apidoc_template.ftl", "markdown_template.ftl", "response_json.ftl" and "help.ftl" files under the directory.
	templatePath=template

	# Optional setting
    # This path is to parse code comments. 
	# It supports more than one path, please split by ';'
    # NOTICE: Program would add "$modulePath/src/main/java" by default.
    commentPath=D:/workspace/tcc/service;D:/workspace/tcc/dto
    
	# Optional setting
    # Output directory
    # NOTICE: Program would use "__autoAPI" by default.
    outputPath=__autoAPI
    
	# Optional setting
    # Please add your class folder path and the JAR file path here. 
    # It supports more than one path, please split it by ';'
    # NOTICE: The program would add "$modulePath/target/classes" by default.  
    classPath=D:/workspace/tcc/target/classes

	# Optional setting
    # All the interface/methods which are implemented "RequestMapping" interface/methods under this folder will generate comments
    # It works for the sub-folders recursively.
    # NOTICE: Program would add "$modulePath/src/main/java" by default.
    servicePath=D:/workspace/tcc/service

	# Optional setting
    # NOTICE: Program would use "http://127.0.0.1:8080" by default.		
	requestURL=http://www.tcc.com

    # Optional setting
	# It is the class name of the pagable container class.
	# NOTICE: Program would use "Page" by default.		
	pagableClassName=Page



Here are some examples:

	# Example
    modulePath=D:/workspace/tcc
	classPath=D:/repository/xxx.jar;D:/project2/target/classes
	template=template

	# Example
    commentPath=D:/workspace/tcc
	servicePath=D:/workspace/tcc/service
	classPath=D:/repository/xxx.jar;D:/project2/target/classes
	template=template

	# Example
    modulePath=D:/workspace/tcc
	classPath=D:/repository/xxx.jar;D:/project2/target/classes
	pagableClassName=PageContainer
	requestURL=http://localhost:8070	
	template=template

	# Example
    modulePath=D:/workspace/tcc
 	commentPath=D:/workspace/abc;D:/workspace/edf
	servicePath=D:/workspace/tcc/service
	classPath=D:/repository/xxx.jar;D:/project2/target/classes
	template=template

## How to write your own templates ##

Sometimes, it is possible that you want to design your own ApiDoc or Markdown templates. Please refer to the following PARAMs which are valid in **apidoc_template.ftl** and **markdown_template.ftl**


| PARAM NAME      | TYPE | DESC |
| :-------- | :--------| :--: |
| APINAME | String | API short name |
| APIGROUP | String | API group name |
| APIMETHOD | String | API Method, like "POST","PUT" and so on |
| LABELNAME | String | API label name |
| PATH | String | API path, like "query" or "category/new"  |
| DESC | String | API description |
| PARAM_LIST | String | API param list, for now it is NOT allowed more detail user-defined |
| REQUEST_JSON | String | API request json |
| RESPONSE_JSON | String | API response json |	 

> Postman file is based on V2.1 Collection format, it is NOT necessary to design its template any more. Thus we not support Postman Template under templatePath for now.

> The default templates are under src/main/resources/template, please feel free to check them.

**apidoc_template.ftl**

	/**
	 * @api {${APIMETHOD}} /${APIGROUP}/${PATH} ${LABELNAME}
	 * @apiVersion 1.0.0
	 * @apiName ${APINAME}
	 * @apiGroup ${APIGROUP}
	 * @apiExample Example
	 *   http://ip:port/${APIGROUP}/${PATH}
	<#if DESC!=''>
	 * @apiDescription <b>使用说明：</b>${DESC}
	</#if>
	<#if PARAM_LIST??>
	${PARAM_LIST}
	</#if>
	<#if REQUEST_JSON??>
	 * @apiParamExample {json} 接口请求入参示例
	${REQUEST_JSON}
	</#if>
	 * @apiSuccess {String} success true:成功, false:失败
	 * @apiSuccess {String} msg 返回信息
	 * @apiSuccessExample {json} 接口数据响应示例
	${RESPONSE_JSON}
	 */


**markdown_template.ftl**

	## ${LABELNAME} ##
	
	##### 请求方式: ${APIMETHOD}
	##### URL:  http://ip:port/${APIGROUP}/${PATH}
	
	<#if DESC??>
	#####  使用说明
	> ${DESC}
	</#if>
	
	<#if PARAM_LIST??>
	#####   请求参数
	| 参数名      | 类型 | 说明|
	| :-------- | :--------| :--: |
	${PARAM_LIST}
	</#if>
	
	<#if REQUEST_JSON??>
	#####  接口请求入参示例:
	${REQUEST_JSON}
	</#if>
	
	<#if RESPONSE_JSON??>
	##### 接口数据响应示例：
	${RESPONSE_JSON}
	</#if>
	
	----------

**response_json.ftl**

	{
	"msg": "",
	"additionalInfo": {},
	<#if RESPONSE_JSON??>
	"value": ${RESPONSE_JSON},
	</#if>
	"success": true
	}


**help.ftl**

	
	##############   使用帮助：  ################
	1 在运行本程序前，建议先给方法和字段名添加必要的注释
	2 在运行本程序前，请先在程序同级目录下的apiHelper.properties中设置相应值并且准备好相应的template，具体设置请参见README.md
	3 请运行程序 java -jar MainGenerator.jar，目前支持生成ApiDoc, Postman, Markdown格式文件
	#############################################


## How to get Jar package ##

The project is based on Maven. Thus please clean and package the project using  maven commands directly:
	
	mvn clean
	mvn package 


## About how to write comments in Java code ##

The standard service share interface looks like this:

    package com.example.share.service;
    
    import com.example.dto.ResultBase;
    import com.example.dto.request.XXRequestDTO;
	import com.example.dto.response.XXResponseDTO;
    import org.springframework.web.bind.annotation.RequestMapping;
    
    
    @RequestMapping("/example")
    public interface TestShareService {
    
      /**
       * Delete some data
       * @param requestDTO
       * @return
       */
      @RequestMapping("/delete")
      ResultBase<Void> delete(XXRequestDTO requestDTO);
    

      /**
       * Save some data
       */
      @RequestMapping("/save")
      ResultBase<XXResponseDTO> save(XXRequestDTO requestDTO);

      // Update some data
      @RequestMapping("/update")
      ResultBase<XXResponseDTO> update(XXRequestDTO requestDTO);

      /** Query some data */
      @RequestMapping("/query")
      ResultBase<XXResponseDTO> query(XXRequestDTO requestDTO);

      /** Ignore */
      ResultBase<XXResponseDTO> ignore(XXRequestDTO requestDTO);
    }


> All the above method comments could be figure out. And the "ignore" method has no "RequestMapping", thus it would be ignored.

> For now, it just supports "RequestMapping", but not support "GetMapping", "PostMapping", "PutMapping" or "DeleteMapping".

The standard parameter code looks like this:

    package com.example.dto.request;
    
    import lombok.Data;
    
    import java.io.Serializable;
    import java.util.List;
    
    
    @Data
    public class XXRequestDTO extends BaseRequestDTO implements Serializable {
    
        private static final long serialVersionUID = 1L;
    
    	/** Primary Key id */
    	private Long id;
    
        /**
         * Reuqest Name
         */
    	private String name;
    
   		// Value List
  	    private List<String> values;    
    }

> All the above field comments could be figure out. Now it could parse the fields from the parent-classes. 
>
> It would ignore the static fields.

  As you see, if you have nice code style, you don't need to do some other special job and this program would not invade your original code.
  Tool is just a tool:)

  