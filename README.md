# VirusTotal for IBM Watson Workspace

This bot scans files in IBM Watson Workspace conversations for malicious content.

Requirements
------------
- [JDK 1.8 or higher version]
- [Apache Maven 3.x]
- [IBM Bluemix CLI] that includes [CloudFoundry]
- [Virustotal] API-Key
- [IBM Watson Workspace] App-ID (see [https://developer.watsonwork.ibm.com/apps] )
- opt. Lambok installed for Eclipse [https://projectlombok.org/setup/eclipse]

Getting started
---------------
1. Clone project from GIT repo
 - `git clone https://github.ibm.com/michael-wuerdemann/...`
2. OR [Download] the project as a zip file and extract
 - `https://github.ibm.com/michael-wuerdemann/.../archive/master.zip`
3. Copy `src/main/resources/application-sample.yml`to `src/main/resources/application.yml` and add your own Service Credentials for [IBM Watson Work] (mandatory) and [VirusTotal API Key]
4. Build and install the project using maven and Bluemix CLI
 - `./buildandpush.sh`
 
This Bot will be deployed according to `manifest.yml` (modify the route or name of the service, if needed):
 
``` yml
---
applications:
- name: VT4WW
  memory: 512M
  host: VT4WW
  domain: eu-gb.mybluemix.net
  buildpack: liberty-for-java
 ```
 
 In this case it will become available as [https://VT4WW.eu-gb.mybluemix.net/].
 
 Contact
---------------
### Support or Contact
Having trouble with this project? Do not hesitate to contact me: Michael Würdemann <michael.wuerdemann@de.ibm.com> or via IBM Watson Workspace and I'll help you to sort it out.

### Contribute to this Project
You are welcome to suggest new features and improvements. Please feel free to fork and make pull requests with your additions and improvements. 
