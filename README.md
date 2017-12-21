# API Helper #


----------
Generates raw comments for ApiDoc to parse and generate the API documents. 

For now, API Helper generate ApiDoc, Postman and Markdown files under different folders after run once.

## Usage ##

1. Please make sure that you have installed **ApiDoc** tool.  If you don't know how to use it, please refer to [here](https://github.com/apidoc/apidoc).

        1.  Install nodejs: http://nodejs.cn/download
        2.  Install apidoc: npm install -g apidoc 

2. When ApiDoc is ready, please  create a property file called "*apiHelper.properties*" and set necessary properties. Please refer to the *Configuration* part.

3. After set the properties, you could double click **runApiHelper.bat** directly, it would generate __autoAPI. Of course, you could run it manually as well:

	    # generate temp files： __autoAPI/apidoc 和 __autoAPI/postman 
		java -jar ApiHelper.jar 
	 
		# generate final Api Documents： final目录
	    apidoc -i __autoAPI/apidoc -o final/ -f ".txt" 

4. Import APIs into Postman
       
		Open Postman tool and click the Import button，please choose __autoAPI/postman/postman.json to upload.


## Configuration ##
 
	# Necessary setting
    # Please set your project/module home directory, and it would be great when "$modulePath/src/main/java" and "$modulePath/target/classes" folders exist.
    modulePath=D:/workspace/tcc
   
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

	# Example
    commentPath=D:/workspace/tcc
	servicePath=D:/workspace/tcc/service
	classPath=D:/repository/xxx.jar;D:/project2/target/classes

	# Example
    modulePath=D:/workspace/tcc
	classPath=D:/repository/xxx.jar;D:/project2/target/classes
	pagableClassName=PageContainer
	requestURL=http://localhost:8070	

	# Example
    modulePath=D:/workspace/tcc
 	commentPath=D:/workspace/abc;D:/workspace/edf
	servicePath=D:/workspace/tcc/service
	classPath=D:/repository/xxx.jar;D:/project2/target/classes


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

  