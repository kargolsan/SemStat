## Description
This robot get unique domain with website content keyword.

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

## Deploy
- Add chrome browser driver to ```drivers``` folder of application
