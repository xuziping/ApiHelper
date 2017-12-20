java -jar  ApiHelper.jar 

ping -n 20 127.1>nul | apidoc -i __autoAPI/apidoc -o final/ -f ".txt"

