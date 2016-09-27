#SemStat for SEO and SEM

![interface_robot](https://cloud.githubusercontent.com/assets/12084504/18873464/e5616752-84bf-11e6-9894-74fe7bcb5d9c.jpg)

## Description
This robot get unique domain with website content keyword. Interface is for English and Polish languages.

## License

![88x31](https://cloud.githubusercontent.com/assets/12084504/18874988/46d29e30-84c4-11e6-97c7-0ffc2229e0eb.png)
Creative Commons. Attribution-NonCommercial-NoDerivatives 3.0 International (CC BY-NC-ND 3.0)

## Before run application
- Install Chrome Web Browser in your system

## Configuration Sources
You can add urls manual to application. If Google Engine will not minimality 15 urls, then application
get urls from ```Sources tab```. Add  minimality 15 links for start job of robot. Each w new line. Example:
```
http://website.com?query={{keyword}}
http://websitetwo.com
```

If you not added, robot will analyzed keyword only search engine (if search engine will configured).
If will not minimality urls in sources and will not configured search engine, application will not analyzed keyword.

Search engine can be not configuration, but sources with urls mu be fill with minimality 15 urls.

The more urls in the ```Sources tab``` better results.

You may use ```{{keyword}}``` template to your manual urls.

## Configuration Google API
Go to your account in Google Service and create API Search Custom and create
your credentials for get API. Create in Google Service your Search Engine Custom for get CX ID.
In your Search Engine Custom, set option ```Websites for search``` to ```all```

In application set option ```Queries for keyword```. Default is ```1```. 
For FREE Queries in Google API, limit day in 100 queries.

Example: ```1 queries for keyword``` get 10 results links of keyword from Google Search Engine API.

## Configuration MySQL
Create table in your mysql server with 5 columns:
- for ```domains``` (String) [255]
- for ```url``` (Text)
- for ```quantity``` (Integer) [4]
- for ```date``` (Timestamp)
- for ```keyword``` (String) [100]

Your table name and column name add to configuration application in setting tab.

## Deploy

####Requirements

- Java 8 or higher. Gradle must run with Java 8
- Wix Toolset or WixEdit must be installed
- Add variable ProgramFiles(x86) to system path when value is ProgramFiles directory of x86 architecture in your system Windows
- Add variable ProgramW6432 to system path when value is ProgramFiles directory of x64 architecture in your system Windows

#### Steps

For create *.msi run tasks of gradle:

- gradle clean
- gradle assemble
- gradle.build
- gradle copyLibs
- gradle copyDrivers
- gradle copyUrls
- gradle launch4j
- gradle msi create msi installer for architecture 32bit
- gradle msi64 create msi installer for architecture 64bit


