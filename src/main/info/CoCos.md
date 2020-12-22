# Ideas for CoCos

- Common File Extension
     - Error
     - The name of the file should end with .sysml (or .kerml)
- Import Statement Valid
    - Warning
    - import statements can be resolved
- PackageName equals Filename
    - Warning   
    - In SysMl the package name is typically equal to the filename, because a 
    package can contain multiple elements 
    this naming convention facilitates navigation. 
- Reference is defined
    - Error    
    - All references should be defined in the current artifact or in an imported artifact.
- Naming Convention
    - Warning 
    - All definitions should begin with a capital letter and all usages should begin with a lowercase letter, e.g.:
      1. All blocks should begin with a capital letter
      2. All parts should begin with a lowercase letter
      3. Port definitions should begin with a capital letter
      4. A port usage should begin with a lowercase letter
    
- PortDirection
    - Error    
    - An "in" port should connect to an "out" port.
- Redefinition and Subsetting
    - Error    
    - ... 

----
There are still a lot to find.    
    
- CoCoName
    - Warning or Error    
    - Description
- CoCoName
    - Warning or Error    
    - Description