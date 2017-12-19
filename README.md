# Auto ApiDoc Generator #


----------
Generates raw comments for ApiDoc to parse and generate the API documents.


## Usage ##

Please make sure that you have installed **ApiDoc** tool.  If you don't know how to use it, please refer to [here](https://github.com/apidoc/apidoc).

When ApiDoc is ready, please download the Jar file from this directly, and then create a property file called "*autoApiDoc.properties*". Please set necessary properties:
    
    # This path is to parse the code comments from service interfaces/request parameters/DTO. 
	# It supports more than one path, please split it by ';'
    # NOTICE: You could not set this property, and program would take "$modulePath/src/main/java" by default.
    commentPath=D:/workspace/tcc/service;D:/workspace/tcc/dto
    
    # Output directory
    # NOTICE: You could not set this property, and program would take "./apidoc" by default.
    outputPath=./apidoc
    
    # Please add your class path and the JARs path here.
    # It supports more than one path, please split it by ';'
    # NOTICE: You could not set this property if your module is undependent. The program would add "$modulePath/target/classes" by default.  
    classPath=D:/workspace/tcc/target/classes
    
    # All the interface/methods which are implemented "RequestMapping" interface/methods under this folder will generate comments
    # It would work for the sub-folders recursively.
    # NOTICE: You could not set this property, and program would take "$modulePath/src/main/java" by default.
    servicePath=D:/workspace/tcc/service

    # If you don't want to set more properties like servicePath/commentPath, please feel free to use this property. This is the main work directory, and please make sure that "src/main/java" and "target/classes" folders are under this path.
    modulePath=D:/workspace/tcc

When you finish the above settings, please run the jar or run the bat script：

    java -jar AutoGenerateApiDoc.jar

Finally, you will get some text files. Please run ApiDoc to generate the final documents:

    apidoc -i apidoc -o final/ -f ".txt"

Of course, you could double click AutoGenerateApiDoc.bat, you will combine the above two steps into one. But if you have problem, please edit it direclty.   


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

  